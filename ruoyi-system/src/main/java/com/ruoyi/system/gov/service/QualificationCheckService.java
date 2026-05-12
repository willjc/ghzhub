package com.ruoyi.system.gov.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.system.domain.HzQualification;
import com.ruoyi.system.domain.HzUser;
import com.ruoyi.system.gov.client.GovDataClient;
import com.ruoyi.system.gov.dto.QualificationCheckResult;
import com.ruoyi.system.gov.dto.QualificationCheckResult.CheckItem;
import com.ruoyi.system.service.IHzQualificationService;
import com.ruoyi.system.service.IHzUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

/**
 * 政务资格校验编排服务
 * <p>
 * 5 项判定（全过才通过）：
 * <ol>
 *   <li>社保：最近 3 个连续完整月有缴费记录 + 近期缴费单位在港区</li>
 *   <li>本人不动产：名下无房</li>
 *   <li>本人公租房：无享受记录</li>
 *   <li>配偶不动产：配偶名下无房（未婚直接通过）</li>
 *   <li>配偶公租房：配偶无享受记录（未婚直接通过）</li>
 * </ol>
 * 婚姻信息是前置步骤（不判定）。
 * <p>
 * 并发策略：首轮 4 个接口（婚姻、社保、本人不动产、本人公租房）并发；
 * 婚姻返回且已婚后再并发启动配偶 2 个接口。
 */
@Service
public class QualificationCheckService {

    private static final Logger log = LoggerFactory.getLogger(QualificationCheckService.class);

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter YM = DateTimeFormatter.ofPattern("yyyyMM");

    /**
     * 港区行政区划代码白名单（可通过配置覆盖）。
     * 默认 410173,410126：410173 是政务接口当前实际返回值（航空港经济综合实验区），
     * 410126 为老区划代码兼容保留。支持前缀匹配。
     */
    @Value("${gov.proxy.hangzone-codes:410173,410126}")
    private String hangzoneCodesRaw;

    /** 申请人在港区单位连续缴纳社保所需月份数 */
    private static final int REQUIRED_SOCIAL_MONTHS = 3;

    /**
     * 社保基准月偏移：以 T-SOCIAL_MONTH_OFFSET 为最新需校验月。
     * 政务社保接口每月底 3~4 天内分批入库 T-1 月数据，T-1 全月大部分时间不可靠，
     * 因此从 T-2 起步往前取 3 连月（T-2、T-3、T-4），规避更新延迟。
     */
    private static final int SOCIAL_MONTH_OFFSET = 2;

    /** 政务查询专用线程池（守护线程） */
    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(
            4, 8, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(64),
            r -> {
                Thread t = new Thread(r, "gov-check-" + System.nanoTime());
                t.setDaemon(true);
                return t;
            });

    @Autowired
    private GovDataClient govDataClient;

    @Autowired
    private IHzUserService userService;

    @Autowired
    private IHzQualificationService qualificationService;

    // ==================== 查询最新资格态 ====================

    public QualificationCheckResult getStatus(Long userId) {
        QualificationCheckResult ret = new QualificationCheckResult();
        HzQualification q = qualificationService.selectQualificationByTenantIdAndType(userId, "1");
        if (q == null) {
            ret.setChecked(false);
            return ret;
        }
        // 资格校验时效：一次管一个自然日，次日首次访问需重新校验（通过/失败同口径过期）
        if (!isCheckedToday(q.getLastCheckTime())) {
            ret.setChecked(false);
            return ret;
        }
        ret.setChecked(true);
        ret.setPassed("1".equals(q.getFinalResult()));
        ret.setQualificationId(q.getQualificationId());
        ret.setLastCheckTime(q.getLastCheckTime());
        // 简略回放 items，使失败页可以直接用
        ret.getItems().addAll(buildItemsFromEntity(q));
        ret.getFailReasons().addAll(collectFailReasons(ret.getItems()));
        return ret;
    }

    /**
     * 判断 lastCheckTime 是否在今天（服务器当前自然日）。
     * 兼容 "yyyy-MM-dd HH:mm:ss" 与 "yyyy-MM-dd" 两种格式，解析失败一律视为已过期。
     */
    private boolean isCheckedToday(String lastCheckTime) {
        if (lastCheckTime == null || lastCheckTime.length() < 10) {
            return false;
        }
        String today = java.time.LocalDate.now().toString(); // yyyy-MM-dd
        return lastCheckTime.startsWith(today);
    }

    // ==================== 执行一次完整校验 ====================

    public QualificationCheckResult check(Long userId) {
        HzUser user = userService.selectHzUserById(userId);
        if (user == null || isBlank(user.getIdCard()) || isBlank(user.getRealName())) {
            QualificationCheckResult r = new QualificationCheckResult();
            r.setChecked(true);
            r.setPassed(false);
            r.getItems().add(new CheckItem("prerequisite", "实名认证", "failed", "请先完成实名认证"));
            r.getFailReasons().add("请先完成实名认证");
            return r;
        }
        String idCard = user.getIdCard().trim();
        String name = user.getRealName().trim();

        // 首轮并发：婚姻 / 社保 / 本人不动产 / 本人公租房
        CompletableFuture<Map<String, Object>> fMarriage = supply(() -> govDataClient.queryMarriage(idCard, name));
        CompletableFuture<Map<String, Object>> fSocial = supply(() -> govDataClient.querySocialInsurance(idCard, name));
        CompletableFuture<Map<String, Object>> fSelfEstate = supply(() -> govDataClient.queryRealEstate(idCard, name));
        CompletableFuture<Map<String, Object>> fSelfHousing = supply(() -> govDataClient.queryPublicHousing(idCard, name));

        // 等婚姻完成后，根据已婚/未婚决定是否并发配偶 2 个接口
        Map<String, Object> marriage = safeGet(fMarriage);
        String[] spouse = parseSpouse(marriage, idCard);
        String spouseIdCard = spouse[0];
        String spouseName = spouse[1];

        CompletableFuture<Map<String, Object>> fSpouseEstate;
        CompletableFuture<Map<String, Object>> fSpouseHousing;
        if (spouseIdCard != null && spouseName != null) {
            fSpouseEstate = supply(() -> govDataClient.queryRealEstate(spouseIdCard, spouseName));
            fSpouseHousing = supply(() -> govDataClient.queryPublicHousing(spouseIdCard, spouseName));
        } else {
            fSpouseEstate = CompletableFuture.completedFuture(null);
            fSpouseHousing = CompletableFuture.completedFuture(null);
        }

        Map<String, Object> social = safeGet(fSocial);
        Map<String, Object> selfEstate = safeGet(fSelfEstate);
        Map<String, Object> selfHousing = safeGet(fSelfHousing);
        Map<String, Object> spouseEstate = safeGet(fSpouseEstate);
        Map<String, Object> spouseHousing = safeGet(fSpouseHousing);

        // ====== 逐项判定 ======
        QualificationCheckResult result = new QualificationCheckResult();
        result.setChecked(true);

        // 婚姻项（展示用，不判定）
        boolean married = isSuccessAndHasRecord(marriage);
        result.getItems().add(new CheckItem(
                "marriage", "婚姻信息",
                marriage != null && Boolean.TRUE.equals(marriage.get("success")) ? "passed" : "error",
                married ? "已婚，将同步核验配偶信息" : "未婚，无需核验配偶信息"));

        // 社保
        CheckItem socialItem = checkSocial(social);
        result.getItems().add(socialItem);

        // 本人不动产
        CheckItem selfEstateItem = checkEstate(selfEstate, "selfEstate", "名下不动产", false);
        result.getItems().add(selfEstateItem);

        // 本人公租房
        CheckItem selfHousingItem = checkHousing(selfHousing, "selfHousing", "公租房记录", false);
        result.getItems().add(selfHousingItem);

        // 配偶不动产
        CheckItem spouseEstateItem;
        CheckItem spouseHousingItem;
        if (married) {
            spouseEstateItem = checkEstate(spouseEstate, "spouseEstate", "配偶名下不动产", true);
            spouseHousingItem = checkHousing(spouseHousing, "spouseHousing", "配偶公租房记录", true);
        } else {
            spouseEstateItem = new CheckItem("spouseEstate", "配偶名下不动产", "skipped", "未婚，无需核验");
            spouseHousingItem = new CheckItem("spouseHousing", "配偶公租房记录", "skipped", "未婚，无需核验");
        }
        result.getItems().add(spouseEstateItem);
        result.getItems().add(spouseHousingItem);

        // 通过条件：5 项判定项都是 passed 或 skipped
        boolean passed =
                  "passed".equals(socialItem.getStatus())
                && ("passed".equals(selfEstateItem.getStatus()))
                && ("passed".equals(selfHousingItem.getStatus()))
                && ("passed".equals(spouseEstateItem.getStatus()) || "skipped".equals(spouseEstateItem.getStatus()))
                && ("passed".equals(spouseHousingItem.getStatus()) || "skipped".equals(spouseHousingItem.getStatus()));
        result.setPassed(passed);
        result.getFailReasons().addAll(collectFailReasons(result.getItems()));

        // ====== 落库 upsert ======
        String now = LocalDateTime.now().format(FMT);
        result.setLastCheckTime(now);
        HzQualification entity = buildEntity(userId, user, spouseIdCard, spouseName,
                socialItem, selfEstateItem, selfHousingItem,
                spouseEstateItem, spouseHousingItem,
                passed, joinReasons(result.getFailReasons()), now);
        upsert(entity);
        result.setQualificationId(entity.getQualificationId());
        return result;
    }

    // ==================== 逐项判定 ====================

    private CheckItem checkSocial(Map<String, Object> resp) {
        if (resp == null || !Boolean.TRUE.equals(resp.get("success"))) {
            return new CheckItem("social", "社保缴纳", "error", "政务社保接口暂不可用，请稍后重试");
        }
        Object rawRecords = resp.get("records");
        List<?> records = rawRecords instanceof List ? (List<?>) rawRecords : null;
        if (records == null) {
            Object raw = resp.get("raw");
            if (raw instanceof JSONObject) {
                Object data = ((JSONObject) raw).get("data");
                if (data instanceof List) records = (List<?>) data;
            }
        }
        if (records == null || records.isEmpty()) {
            return new CheckItem("social", "社保缴纳", "failed", "近 3 个月社保缴纳不满足");
        }

        // 需要最近 3 个完整月连续：以 T-SOCIAL_MONTH_OFFSET（默认 T-2）为基准往前取 3 连月。
        // 例如今日 2026-05-xx → needMonths = {202603, 202602, 202601}
        // 规避政务社保 T-1 月底才入库的延迟期。
        LocalDate now = LocalDate.now();
        Set<String> needMonths = new HashSet<>();
        for (int i = SOCIAL_MONTH_OFFSET; i < SOCIAL_MONTH_OFFSET + REQUIRED_SOCIAL_MONTHS; i++) {
            needMonths.add(now.minusMonths(i).format(YM));
        }

        Set<String> paidMonths = new HashSet<>();
        Set<String> hangzoneMonths = new HashSet<>();
        Set<String> hangzoneCodes = parseHangzoneCodes();

        for (Object rec : records) {
            if (!(rec instanceof Map)) continue;
            Map<?, ?> r = (Map<?, ?>) rec;
            Object ym = r.get("AAE041");
            Object region = r.get("AAB301");
            if (ym == null) continue;
            String ymStr = String.valueOf(ym).trim();
            if (ymStr.length() > 6) ymStr = ymStr.substring(0, 6); // 兼容 202401 或 20240115 等
            paidMonths.add(ymStr);
            if (region != null && hangzoneCodes.stream().anyMatch(code -> String.valueOf(region).startsWith(code))) {
                hangzoneMonths.add(ymStr);
            }
        }

        boolean continuous = paidMonths.containsAll(needMonths);
        boolean inHangzone = needMonths.stream().anyMatch(hangzoneMonths::contains);

        log.info("[checkSocial] needMonths={}, paidMonths={}, hangzoneMonths={}, hangzoneCodes={}, continuous={}, inHangzone={}",
                needMonths, paidMonths, hangzoneMonths, hangzoneCodes, continuous, inHangzone);

        if (!continuous) {
            return new CheckItem("social", "社保缴纳", "failed", "近 3 个月社保缴纳不连续");
        }
        if (!inHangzone) {
            return new CheckItem("social", "社保缴纳", "failed", "近 3 个月缴费单位不在港区");
        }
        return new CheckItem("social", "社保缴纳", "passed", "近 3 个月港区单位连续缴纳");
    }

    private CheckItem checkEstate(Map<String, Object> resp, String code, String label, boolean spouse) {
        if (resp == null || !Boolean.TRUE.equals(resp.get("success"))) {
            return new CheckItem(code, label, "error", "政务不动产接口暂不可用，请稍后重试");
        }
        // 业务规则：只校验航空港区内的房产，其他区域的房产不影响通过
        // 字段参考 ghz-gov-proxy/deploy/港好住后端对接指南.md §4.4
        //   SUBSYSTEMID = "航空港区"，FWZL = 房屋坐落
        List<?> records = extractEstateRecords(resp);
        boolean hasInHangzone = false;
        if (records != null) {
            for (Object rec : records) {
                if (!(rec instanceof Map)) continue;
                Map<?, ?> r = (Map<?, ?>) rec;
                if (isHangzoneEstate(r)) {
                    hasInHangzone = true;
                    break;
                }
            }
        }
        if (hasInHangzone) {
            return new CheckItem(code, label, "failed",
                    spouse ? "配偶名下在郑州航空港区已有房产" : "名下在郑州航空港区已有房产");
        }
        return new CheckItem(code, label, "passed",
                spouse ? "配偶在郑州航空港区无房产" : "在郑州航空港区无房产");
    }

    /** 从代理返回里抽取不动产 records 列表（兼容 records 顶层 / raw.data 两种） */
    private List<?> extractEstateRecords(Map<String, Object> resp) {
        Object recs = resp.get("records");
        if (recs instanceof List) return (List<?>) recs;
        Object raw = resp.get("raw");
        if (raw instanceof JSONObject) {
            Object data = ((JSONObject) raw).get("data");
            if (data instanceof List) return (List<?>) data;
        }
        return null;
    }

    /** 判断一条不动产记录是否属于航空港区：优先看 SUBSYSTEMID，兜底看 FWZL 前缀 */
    private boolean isHangzoneEstate(Map<?, ?> rec) {
        Object sub = rec.get("SUBSYSTEMID");
        if (sub != null) {
            String s = String.valueOf(sub).trim();
            if (s.contains("航空港")) return true;
        }
        Object fwzl = rec.get("FWZL");
        if (fwzl != null) {
            String addr = String.valueOf(fwzl).trim();
            // FWZL 通常以"航空港区..."开头
            if (addr.startsWith("航空港") || addr.contains("郑州航空港")) return true;
        }
        return false;
    }

    private CheckItem checkHousing(Map<String, Object> resp, String code, String label, boolean spouse) {
        if (resp == null || !Boolean.TRUE.equals(resp.get("success"))) {
            return new CheckItem(code, label, "error", "政务公租房接口暂不可用，请稍后重试");
        }
        boolean has = Boolean.TRUE.equals(resp.get("hasRecord"));
        if (has) {
            return new CheckItem(code, label, "failed", spouse ? "配偶已享受公租房保障" : "已享受公租房保障");
        }
        return new CheckItem(code, label, "passed", spouse ? "配偶无公租房记录" : "无公租房记录");
    }

    // ==================== 辅助 ====================

    /** 解析婚姻查询结果，返回 [spouseIdCard, spouseName]；未婚或解析失败返回 null 数组 */
    private String[] parseSpouse(Map<String, Object> marriage, String selfIdCard) {
        String[] none = new String[]{null, null};
        if (marriage == null || !Boolean.TRUE.equals(marriage.get("success"))) return none;
        if (!Boolean.TRUE.equals(marriage.get("hasRecord"))) return none;

        Object raw = marriage.get("raw");
        if (!(raw instanceof JSONObject)) return none;
        Object dataObj = ((JSONObject) raw).get("data");
        if (!(dataObj instanceof List) || ((List<?>) dataObj).isEmpty()) return none;

        // 取最新一条（通常第一条）
        Object first = ((List<?>) dataObj).get(0);
        if (!(first instanceof Map)) return none;
        Map<?, ?> rec = (Map<?, ?>) first;

        // 身份证第 17 位奇男偶女
        boolean selfIsMale = isMale(selfIdCard);
        // 政务婚姻接口字段名参考 ghz-gov-proxy/deploy/港好住后端对接指南.md §4.1
        String spouseIdKey = selfIsMale ? "女方身份证件号码" : "男方身份证件号码";
        String spouseNameKey = selfIsMale ? "女方姓名" : "男方姓名";
        Object sid = rec.get(spouseIdKey);
        Object sname = rec.get(spouseNameKey);
        // 兼容政务方字段命名的潜在差异：身份证号码 / 身份证 / 身份证件号
        if (sid == null) {
            String[] fallback = selfIsMale
                    ? new String[]{"女方身份证号码", "女方身份证号", "女方身份证"}
                    : new String[]{"男方身份证号码", "男方身份证号", "男方身份证"};
            for (String k : fallback) {
                Object v = rec.get(k);
                if (v != null && !String.valueOf(v).trim().isEmpty()) {
                    sid = v;
                    break;
                }
            }
        }
        if (sid == null || sname == null) {
            log.warn("婚姻接口返回缺少配偶字段，keys={}", rec.keySet());
            return none;
        }
        String sidStr = String.valueOf(sid).trim();
        String snameStr = String.valueOf(sname).trim();
        if (sidStr.isEmpty() || snameStr.isEmpty()) return none;
        return new String[]{sidStr, snameStr};
    }

    private boolean isMale(String idCard) {
        if (idCard == null || idCard.length() < 17) return true;
        char c = idCard.charAt(16);
        return Character.isDigit(c) && ((c - '0') % 2 == 1);
    }

    private Set<String> parseHangzoneCodes() {
        Set<String> set = new HashSet<>();
        if (hangzoneCodesRaw == null) return set;
        for (String s : hangzoneCodesRaw.split(",")) {
            String t = s.trim();
            if (!t.isEmpty()) set.add(t);
        }
        return set;
    }

    private boolean isSuccessAndHasRecord(Map<String, Object> resp) {
        return resp != null && Boolean.TRUE.equals(resp.get("success")) && Boolean.TRUE.equals(resp.get("hasRecord"));
    }

    private CompletableFuture<Map<String, Object>> supply(java.util.function.Supplier<Map<String, Object>> sup) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return sup.get();
            } catch (Exception e) {
                log.warn("政务接口调用异常", e);
                Map<String, Object> err = new HashMap<>();
                err.put("success", false);
                err.put("error", e.getMessage());
                return err;
            }
        }, EXECUTOR);
    }

    private Map<String, Object> safeGet(CompletableFuture<Map<String, Object>> f) {
        try {
            return f.get(40, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("政务接口等待超时", e);
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("error", "timeout");
            return err;
        }
    }

    private List<String> collectFailReasons(List<CheckItem> items) {
        List<String> reasons = new ArrayList<>();
        for (CheckItem it : items) {
            if ("failed".equals(it.getStatus()) || "error".equals(it.getStatus())) {
                reasons.add(it.getMessage());
            }
        }
        return reasons;
    }

    private String joinReasons(List<String> reasons) {
        if (reasons == null || reasons.isEmpty()) return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < reasons.size(); i++) {
            if (i > 0) sb.append("；");
            sb.append(reasons.get(i));
        }
        return sb.length() > 500 ? sb.substring(0, 500) : sb.toString();
    }

    private HzQualification buildEntity(Long userId, HzUser user,
                                        String spouseIdCard, String spouseName,
                                        CheckItem social,
                                        CheckItem selfEstate, CheckItem selfHousing,
                                        CheckItem spouseEstate, CheckItem spouseHousing,
                                        boolean passed, String reason, String now) {
        HzQualification e = new HzQualification();
        e.setTenantId(userId);
        e.setApplyType("1");
        e.setIdCard(user.getIdCard());
        e.setName(user.getRealName());
        e.setPhone(user.getPhone());
        e.setSpouseIdCard(spouseIdCard);
        e.setSpouseName(spouseName);
        e.setSocialValid("passed".equals(social.getStatus()) ? "1" : "0");
        e.setHasLocalHouse("failed".equals(selfEstate.getStatus()) ? "1" : "0");
        e.setSelfHasHousing("failed".equals(selfHousing.getStatus()) ? "1" : "0");
        e.setSpouseHasEstate("failed".equals(spouseEstate.getStatus()) ? "1" : "0");
        e.setSpouseHasHousing("failed".equals(spouseHousing.getStatus()) ? "1" : "0");
        e.setAutoCheckResult(passed ? "1" : "0");
        e.setAutoCheckReason(reason);
        e.setFinalResult(passed ? "1" : "0");
        e.setLastCheckTime(now);
        e.setApplyTime(now);
        return e;
    }

    private void upsert(HzQualification entity) {
        HzQualification exist = qualificationService.selectQualificationByTenantIdAndType(entity.getTenantId(), "1");
        if (exist == null) {
            qualificationService.insertQualification(entity);
        } else {
            entity.setQualificationId(exist.getQualificationId());
            qualificationService.updateQualification(entity);
        }
    }

    /** 从已存实体反向构造 items（用于 status 接口回放） */
    private List<CheckItem> buildItemsFromEntity(HzQualification q) {
        List<CheckItem> list = new ArrayList<>();
        // 婚姻
        boolean hasSpouse = q.getSpouseIdCard() != null && !q.getSpouseIdCard().isEmpty();
        list.add(new CheckItem("marriage", "婚姻信息", "passed",
                hasSpouse ? "已婚" : "未婚"));
        // 社保
        list.add(new CheckItem("social", "社保缴纳",
                "1".equals(q.getSocialValid()) ? "passed" : "failed",
                "1".equals(q.getSocialValid()) ? "近 3 个月港区单位连续缴纳" : "近 3 个月社保缴纳不满足"));
        // 本人不动产
        list.add(new CheckItem("selfEstate", "名下不动产",
                "1".equals(q.getHasLocalHouse()) ? "failed" : "passed",
                "1".equals(q.getHasLocalHouse()) ? "名下在郑州航空港区已有房产" : "在郑州航空港区无房产"));
        // 本人公租房
        list.add(new CheckItem("selfHousing", "公租房记录",
                "1".equals(q.getSelfHasHousing()) ? "failed" : "passed",
                "1".equals(q.getSelfHasHousing()) ? "已享受公租房保障" : "无公租房记录"));
        // 配偶
        if (hasSpouse) {
            list.add(new CheckItem("spouseEstate", "配偶名下不动产",
                    "1".equals(q.getSpouseHasEstate()) ? "failed" : "passed",
                    "1".equals(q.getSpouseHasEstate()) ? "配偶名下在郑州航空港区已有房产" : "配偶在郑州航空港区无房产"));
            list.add(new CheckItem("spouseHousing", "配偶公租房记录",
                    "1".equals(q.getSpouseHasHousing()) ? "failed" : "passed",
                    "1".equals(q.getSpouseHasHousing()) ? "配偶已享受公租房保障" : "配偶无公租房记录"));
        } else {
            list.add(new CheckItem("spouseEstate", "配偶名下不动产", "skipped", "未婚，无需核验"));
            list.add(new CheckItem("spouseHousing", "配偶公租房记录", "skipped", "未婚，无需核验"));
        }
        return list;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
