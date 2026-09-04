package com.ruoyi.system.gov.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.utils.IdCardUtils;
import com.ruoyi.system.domain.HzBatchTenant;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.domain.HzProject;
import com.ruoyi.system.domain.HzQualification;
import com.ruoyi.system.domain.HzUser;
import com.ruoyi.system.gov.client.GovDataClient;
import com.ruoyi.system.gov.dto.QualificationCheckResult;
import com.ruoyi.system.gov.dto.QualificationCheckResult.CheckItem;
import com.ruoyi.system.mapper.HzBatchTenantMapper;
import com.ruoyi.system.mapper.HzContractMapper;
import com.ruoyi.system.mapper.HzProjectMapper;
import com.ruoyi.system.service.IHzQualificationService;
import com.ruoyi.system.service.IHzUserService;

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

    @Value("${ghz.preview-phones:}")
    private String previewPhones;

    /** 申请人在港区单位连续缴纳社保所需月份数 */
    private static final int REQUIRED_SOCIAL_MONTHS = 3;

    /**
     * 社保基准月偏移：以 T-SOCIAL_MONTH_OFFSET 为最新需校验月。
     * 政务社保接口每月底 3~4 天内分批入库 T-1 月数据，T-1 全月大部分时间不可靠，
     * 因此从 T-2 起步往前取 3 连月（T-2、T-3、T-4），规避更新延迟。
     */
    private static final int SOCIAL_MONTH_OFFSET = 2;

    /**
     * 【暂时关闭】人才公寓社保校验开关：false = 人才公寓关闭社保校验（不调政务社保接口，社保项视为 skipped 通过）。
     * 仅影响人才公寓（applyType=1），保租房不受影响。恢复时改回 true 即可。
     */
    private static final boolean SOCIAL_CHECK_ENABLED = false;

    /** 社保校验是否关闭（仅人才公寓受开关控制） */
    private boolean isSocialCheckDisabled(String applyType) {
        return !SOCIAL_CHECK_ENABLED && "1".equals(applyType);
    }

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

    @Autowired
    private com.ruoyi.system.service.IHzQualificationAppealService appealService;

    @Autowired
    private HzContractMapper contractMapper;

    @Autowired
    private HzProjectMapper projectMapper;

    @Autowired
    private HzBatchTenantMapper batchTenantMapper;

    // ==================== 查询最新资格态 ====================

    public QualificationCheckResult getStatus(Long userId) {
        return getStatus(userId, "1");
    }

    public QualificationCheckResult getStatus(Long userId, String applyType) {
        if (isRentalType(applyType)) {
            HzUser user = userService.selectHzUserById(userId);
            validateRentalUser(user);
            HzQualification q = qualificationService.selectQualificationByTenantIdAndType(userId, applyType);
            if (q == null || !isCheckedToday(q.getLastCheckTime())) {
                QualificationCheckResult ret = new QualificationCheckResult();
                ret.setChecked(false);
                return ret;
            }
            QualificationCheckResult ret = buildRentalResult(user, applyType, !isQualificationExempt(userId));
            ret.setQualificationId(q.getQualificationId());
            ret.setLastCheckTime(q.getLastCheckTime());
            return ret;
        }

        QualificationCheckResult ret = new QualificationCheckResult();
        HzQualification q = qualificationService.selectQualificationByTenantIdAndType(userId, applyType);
        if (q == null) {
            ret.setChecked(false);
            return ret;
        }
        // 资格校验时效：一次管一个自然日，次日首次访问需重新校验（通过/失败同口径过期）
        if (!isCheckedToday(q.getLastCheckTime())) {
            ret.setChecked(false);
            return ret;
        }
        // 申诉豁免（学历 / 社保两项独立判定）
        boolean appealEduPassed = appealService.existsPassedEducationAppeal(userId);
        boolean appealSocPassed = appealService.existsPassedSocialAppeal(userId);

        ret.setChecked(true);
        ret.setQualificationId(q.getQualificationId());
        ret.setLastCheckTime(q.getLastCheckTime());
        // 简略回放 items，使失败页可以直接用
        ret.getItems().addAll(buildItemsFromEntity(q, appealEduPassed, appealSocPassed, applyType));

        // 总判定：缓存的 final_result 不一定反映最新申诉态（如校验后才被审核通过），
        // 这里基于回放 items 重新计算一次。
        boolean passed = true;
        for (CheckItem it : ret.getItems()) {
            String code = it.getCode();
            String st = it.getStatus();
            if ("marriage".equals(code)) continue;
            if (!"passed".equals(st) && !"skipped".equals(st)) {
                passed = false;
                break;
            }
        }
        ret.setPassed(passed);
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
        return check(userId, "1");
    }

    public QualificationCheckResult check(Long userId, String applyType) {
        HzUser user = userService.selectHzUserById(userId);
        if (isRentalType(applyType)) {
            validateRentalUser(user);
            QualificationCheckResult result = buildRentalResult(user, applyType, !isQualificationExempt(userId));
            persistRentalResult(user, applyType, result);
            return result;
        }

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

        // 一人一户/在住拦截：若用户当前有在住的同类型合同，直接拦截
        if (hasActiveContractOfType(userId, applyType)) {
            QualificationCheckResult r = new QualificationCheckResult();
            r.setChecked(true);
            r.setPassed(false);
            if ("2".equals(applyType)) {
                r.getItems().add(new CheckItem("activeContract", "保租房在住校验", "failed",
                        "您当前正在租住保租房，不可重复申请"));
                r.getFailReasons().add("您当前正在租住保租房，不可重复申请");
            } else {
                r.getItems().add(new CheckItem("talentApartment", "人才公寓在住校验", "failed",
                        "您当前正在住人才公寓，不可重复申请"));
                r.getFailReasons().add("您当前正在住人才公寓，不可重复申请");
            }
            return r;
        }

        // 首轮并发：婚姻 / 社保 / 本人不动产 / 本人公租房
        CompletableFuture<Map<String, Object>> fMarriage = supply(() -> govDataClient.queryMarriage(idCard, name));
        // 【暂时关闭】人才公寓社保校验关闭时不调政务社保接口；恢复时删除本条件分支，改回直接 supply 调用
        CompletableFuture<Map<String, Object>> fSocial = isSocialCheckDisabled(applyType)
                ? CompletableFuture.completedFuture(null)
                : supply(() -> govDataClient.querySocialInsurance(idCard, name));
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

        // 是否存在已通过的申诉（学历 / 社保独立豁免）
        boolean appealEduPassed = appealService.existsPassedEducationAppeal(userId);
        boolean appealSocPassed = appealService.existsPassedSocialAppeal(userId);

        // ====== 逐项判定 ======
        QualificationCheckResult result = new QualificationCheckResult();
        result.setChecked(true);

        // 婚姻项（展示用，不判定）
        boolean married = isSuccessAndHasRecord(marriage);
        result.getItems().add(new CheckItem(
                "marriage", "婚姻信息",
                marriage != null && Boolean.TRUE.equals(marriage.get("success")) ? "passed" : "error",
                married ? "已婚，将同步核验配偶信息" : "未婚，无需核验配偶信息"));

        // 社保（叠加申诉豁免：仅社保侧）
        CheckItem socialItem;
        if (isSocialCheckDisabled(applyType)) {
            // 【暂时关闭】人才公寓社保校验关闭，直接视为跳过（不判定、不展示失败）
            socialItem = new CheckItem("social", "社保缴纳", "skipped", "社保校验暂时关闭");
        } else {
            socialItem = checkSocial(social, user.getWorkUnit());
            if (appealSocPassed && !"passed".equals(socialItem.getStatus())) {
                socialItem = new CheckItem("social", "社保缴纳", "passed", "已通过人工审核");
            }
        }
        result.getItems().add(socialItem);

        // 学历（人才公寓需核验，无政务接口默认 failed，学历申诉通过则 passed；保租房无需学历核验）
        CheckItem educationItem;
        if ("2".equals(applyType)) {
            educationItem = new CheckItem("education", "学历核验", "skipped", "保租房无需学历核验");
        } else if (appealEduPassed) {
            educationItem = new CheckItem("education", "学历核验", "passed", "已通过人工审核");
        } else {
            educationItem = new CheckItem("education", "学历核验", "failed", "学历待人工审核，请提交申诉");
        }
        result.getItems().add(educationItem);

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

        // 通过条件：5 项判定项都是 passed 或 skipped + 学历 passed/skipped（社保关闭期间 skipped 视同通过）
        boolean passed =
                  ("passed".equals(socialItem.getStatus()) || "skipped".equals(socialItem.getStatus()))
                && ("passed".equals(educationItem.getStatus()) || "skipped".equals(educationItem.getStatus()))
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
                passed, joinReasons(result.getFailReasons()), now, applyType);
        upsert(entity, applyType);
        result.setQualificationId(entity.getQualificationId());
        return result;
    }

    /**
     * 创建预订单/合同前的服务端资格守卫。
     */
    public void requireEligible(Long userId, String projectType) {
        QualificationCheckResult result;
        if (isRentalType(projectType)) {
            HzUser user = userService.selectHzUserById(userId);
            validateRentalUser(user);
            result = buildRentalResult(user, projectType, !isQualificationExempt(userId));
        } else if ("1".equals(projectType)) {
            if (isQualificationExempt(userId)) {
                return;
            }
            result = getStatus(userId, projectType);
        } else {
            throw new IllegalStateException("房源项目类型无效");
        }

        if (!result.isChecked()) {
            throw new IllegalStateException("请先完成资格校验");
        }
        if (!result.isPassed()) {
            String reason = result.getFailReasons().isEmpty()
                    ? "当前不符合申请条件"
                    : result.getFailReasons().get(0);
            throw new IllegalStateException(reason);
        }
    }

    private boolean isRentalType(String applyType) {
        return "2".equals(applyType) || "3".equals(applyType);
    }

    private void validateRentalUser(HzUser user) {
        if (user == null) {
            throw new IllegalStateException("用户不存在");
        }
        if (IdCardUtils.calculateAge(user.getIdCard()) == null) {
            throw new IllegalStateException("身份证信息无效，请先完善有效身份信息");
        }
    }

    private QualificationCheckResult buildRentalResult(HzUser user, String applyType, boolean checkActiveContract) {
        Integer ageValue = IdCardUtils.calculateAge(user.getIdCard());
        if (ageValue == null) {
            throw new IllegalStateException("身份证信息无效，请先完善有效身份信息");
        }
        int age = ageValue;
        boolean agePassed = age >= 18 && (!"2".equals(applyType) || age <= 60);
        String ageRequirement = "2".equals(applyType) ? "18至60周岁（含）" : "年满18周岁";

        QualificationCheckResult result = new QualificationCheckResult();
        result.setChecked(true);
        result.getItems().add(new CheckItem("age", "年龄校验",
                agePassed ? "passed" : "failed",
                agePassed ? "当前" + age + "周岁，符合年龄要求" : "申请人年龄须为" + ageRequirement));
        if (!agePassed) {
            result.getFailReasons().add("申请人年龄须为" + ageRequirement);
        }
        if (checkActiveContract && "2".equals(applyType) && hasActiveContractOfType(user.getUserId(), applyType)) {
            result.getFailReasons().add("您当前正在租住保租房，不可重复申请");
        }
        result.setPassed(agePassed && result.getFailReasons().isEmpty());
        return result;
    }

    private void persistRentalResult(HzUser user, String applyType, QualificationCheckResult result) {
        String now = LocalDateTime.now().format(FMT);
        result.setLastCheckTime(now);

        HzQualification entity = new HzQualification();
        entity.setTenantId(user.getUserId());
        entity.setApplyType(applyType);
        entity.setIdCard(user.getIdCard());
        entity.setName(user.getRealName());
        entity.setPhone(user.getPhone());
        entity.setSocialValid("0");
        entity.setHasLocalHouse("0");
        entity.setSelfHasHousing("0");
        entity.setSpouseHasEstate("0");
        entity.setSpouseHasHousing("0");
        entity.setAutoCheckResult(result.isPassed() ? "1" : "0");
        entity.setFinalResult(result.isPassed() ? "1" : "0");
        entity.setAutoCheckReason(joinReasons(result.getFailReasons()));
        entity.setLastCheckTime(now);
        entity.setApplyTime(now);
        upsert(entity, applyType);
        result.setQualificationId(entity.getQualificationId());
    }

    public boolean isQualificationExempt(Long userId) {
        HzUser user = userService.selectHzUserById(userId);
        if (user == null) {
            return false;
        }
        if (!isBlank(user.getPhone()) && !isBlank(previewPhones)) {
            for (String phone : previewPhones.split(",")) {
                if (user.getPhone().trim().equals(phone.trim())) {
                    return true;
                }
            }
        }
        if (isBlank(user.getIdCard())) {
            return false;
        }
        Long count = batchTenantMapper.selectCount(new LambdaQueryWrapper<HzBatchTenant>()
                .eq(HzBatchTenant::getIdCard, user.getIdCard())
                .eq(HzBatchTenant::getDelFlag, "0"));
        return count != null && count > 0;
    }

    // ==================== 逐项判定 ====================

    private CheckItem checkSocial(Map<String, Object> resp, String userWorkUnit) {
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

        // 单位一致性校验：取 AAE041 最大月份对应的所有 AAB004，与申请人填写的工作单位比对
        // 同一个月可能有多条社保记录（不同险种/不同单位编号），只要任意一条匹配即通过
        if (userWorkUnit == null || userWorkUnit.trim().isEmpty()) {
            return new CheckItem("social", "社保缴纳", "failed", "请先在「完善信息」中填写工作单位");
        }
        String latestYm = null;
        for (Object rec : records) {
            if (!(rec instanceof Map)) continue;
            Map<?, ?> r = (Map<?, ?>) rec;
            Object ym = r.get("AAE041");
            if (ym == null) continue;
            String ymStr = String.valueOf(ym).trim();
            if (ymStr.length() > 6) ymStr = ymStr.substring(0, 6);
            if (latestYm == null || ymStr.compareTo(latestYm) > 0) {
                latestYm = ymStr;
            }
        }
        String inputCompany = userWorkUnit.trim();
        // 收集最大月份对应的所有单位名称
        java.util.Set<String> latestCompanies = new java.util.HashSet<>();
        if (latestYm != null) {
            for (Object rec : records) {
                if (!(rec instanceof Map)) continue;
                Map<?, ?> r = (Map<?, ?>) rec;
                Object ym = r.get("AAE041");
                Object company = r.get("AAB004");
                if (ym == null) continue;
                String ymStr = String.valueOf(ym).trim();
                if (ymStr.length() > 6) ymStr = ymStr.substring(0, 6);
                if (ymStr.equals(latestYm) && company != null) {
                    latestCompanies.add(String.valueOf(company).trim());
                }
            }
        }
        log.info("[checkSocial] 单位一致性 latestYm={}, latestCompanies={}, inputCompany={}", latestYm, latestCompanies, inputCompany);
        boolean companyMatched = latestCompanies.stream().anyMatch(c -> c.equals(inputCompany));
        if (latestCompanies.isEmpty() || !companyMatched) {
            return new CheckItem("social", "社保缴纳", "failed", "社保缴费单位与您填写的单位不一致");
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
                                        boolean passed, String reason, String now, String applyType) {
        HzQualification e = new HzQualification();
        e.setTenantId(userId);
        e.setApplyType(applyType);
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

    private void upsert(HzQualification entity, String applyType) {
        HzQualification exist = qualificationService.selectQualificationByTenantIdAndType(entity.getTenantId(), applyType);
        if (exist == null) {
            qualificationService.insertQualification(entity);
        } else {
            entity.setQualificationId(exist.getQualificationId());
            qualificationService.updateQualification(entity);
        }
    }

    /** 从已存实体反向构造 items（用于 status 接口回放） */
    private List<CheckItem> buildItemsFromEntity(HzQualification q, boolean appealEduPassed, boolean appealSocPassed, String applyType) {
        List<CheckItem> list = new ArrayList<>();
        // 婚姻
        boolean hasSpouse = q.getSpouseIdCard() != null && !q.getSpouseIdCard().isEmpty();
        list.add(new CheckItem("marriage", "婚姻信息", "passed",
                hasSpouse ? "已婚" : "未婚"));
        // 社保（叠加社保申诉豁免）
        if (!SOCIAL_CHECK_ENABLED && "1".equals(applyType)) {
            // 【暂时关闭】人才公寓社保校验关闭，回放时直接展示为跳过
            list.add(new CheckItem("social", "社保缴纳", "skipped", "社保校验暂时关闭"));
        } else {
            boolean socialOk = "1".equals(q.getSocialValid()) || appealSocPassed;
            list.add(new CheckItem("social", "社保缴纳",
                    socialOk ? "passed" : "failed",
                    socialOk
                            ? (appealSocPassed && !"1".equals(q.getSocialValid()) ? "已通过人工审核" : "近 3 个月港区单位连续缴纳")
                            : "近 3 个月社保缴纳不满足"));
        }
        // 学历（无政务接口，依赖学历申诉豁免；保租房无需学历核验）
        if ("2".equals(applyType)) {
            list.add(new CheckItem("education", "学历核验", "skipped", "保租房无需学历核验"));
        } else if (appealEduPassed) {
            list.add(new CheckItem("education", "学历核验", "passed", "已通过人工审核"));
        } else {
            list.add(new CheckItem("education", "学历核验", "failed", "学历待人工审核，请提交申诉"));
        }
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

    /**
     * 判断用户是否当前有在住的同类型合同（contract_status IN 2,3 且 project_type=applyType）
     */
    private boolean hasActiveContractOfType(Long userId, String applyType) {
        // 查询用户所有活跃合同（已签署或履行中）
        List<HzContract> activeContracts = contractMapper.selectList(
                new LambdaQueryWrapper<HzContract>()
                        .eq(HzContract::getTenantId, userId)
                        .in(HzContract::getContractStatus, "2", "3")
                        .eq(HzContract::getDelFlag, "0"));
        if (activeContracts == null || activeContracts.isEmpty()) {
            return false;
        }
        // 逐个检查合同关联的项目是否为目标类型
        for (HzContract c : activeContracts) {
            if (c.getProjectId() == null) continue;
            HzProject project = projectMapper.selectById(c.getProjectId());
            if (project != null && applyType != null && applyType.equals(project.getProjectType())) {
                log.info("[资格校验] 在住拦截命中：applyType={}, userId={}, contractId={}, projectId={}",
                        applyType, userId, c.getContractId(), c.getProjectId());
                return true;
            }
        }
        return false;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
