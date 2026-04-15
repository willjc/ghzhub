package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.esign.*;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.EsignService;
import com.ruoyi.system.domain.HzCheckIn;
import com.ruoyi.system.service.IHzCheckInService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class EsignServiceImpl implements EsignService {

    private static final Logger log = LoggerFactory.getLogger(EsignServiceImpl.class);
    private static final Gson gson = new Gson();

    @Value("${esign.app-id}") private String appId;
    @Value("${esign.app-secret}") private String appSecret;
    @Value("${esign.host}") private String host;
    @Value("${esign.notify-url}") private String notifyUrl;
    @Value("${esign.org-id}") private String orgId;
    @Value("${esign.redirect-url}") private String redirectUrl;
    @Value("${esign.template-id}") private String templateId;

    private final HzUserMapper userMapper;
    private final HzContractMapper contractMapper;
    private final HzHouseOrderMapper orderMapper;
    private final HzBillMapper billMapper;
    private final HzHouseMapper houseMapper;
    private final HzProjectMapper projectMapper;
    private final HzBuildingMapper buildingMapper;
    private final HzUnitMapper unitMapper;
    private final IHzCheckInService checkInService;

    public EsignServiceImpl(HzUserMapper userMapper, HzContractMapper contractMapper,
                            HzHouseOrderMapper orderMapper, HzBillMapper billMapper,
                            HzHouseMapper houseMapper, HzProjectMapper projectMapper,
                            HzBuildingMapper buildingMapper, HzUnitMapper unitMapper,
                            IHzCheckInService checkInService) {
        this.userMapper = userMapper;
        this.contractMapper = contractMapper;
        this.orderMapper = orderMapper;
        this.billMapper = billMapper;
        this.houseMapper = houseMapper;
        this.projectMapper = projectMapper;
        this.buildingMapper = buildingMapper;
        this.unitMapper = unitMapper;
        this.checkInService = checkInService;
    }

    // ==================== 实名认证 ====================

    @Override
    public String getPsnAuthUrl(Long userId, String mobile, String realName, String idCard, String redirectUrl) {
        HzUser user = userMapper.selectById(userId);
        if (user != null && user.getEsignPsnId() != null && !user.getEsignPsnId().isEmpty()) return null;

        // 优先使用传入参数；为空则从 DB 读取
        String finalRealName = (realName != null && !realName.isBlank()) ? realName
                : (user != null ? user.getRealName() : null);
        String finalIdCard = (idCard != null && !idCard.isBlank()) ? idCard
                : (user != null ? user.getIdCard() : null);

        // 构建实名认证请求参数，预填充姓名和身份证号
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\"psnAuthConfig\":{\"psnAccount\":\"").append(mobile).append("\"");
        // 预填充姓名和身份证
        if (finalRealName != null && !finalRealName.isEmpty()) {
            jsonBuilder.append(",\"psnInfo\":{\"psnName\":\"").append(escapeJson(finalRealName)).append("\"");
            if (finalIdCard != null && !finalIdCard.isEmpty()) {
                jsonBuilder.append(",\"psnIDCardType\":\"CRED_PSN_CH_IDCARD\",\"psnIDCardNum\":\"").append(escapeJson(finalIdCard)).append("\"");
            }
            jsonBuilder.append("}");
        }
        jsonBuilder.append(",\"psnAuthPageConfig\":{\"psnEditableFields\":[\"mobile\"]}}"); // 只允许修改手机号
        jsonBuilder.append(",\"clientType\":\"ALL\",\"redirectConfig\":{\"redirectDelayTime\":\"3\",\"redirectUrl\":\"").append(redirectUrl).append("\"}");
        jsonBuilder.append(",\"repeatableAuth\":true}");
        String jsonParm = jsonBuilder.toString();
        try {
            EsignHttpResponse resp = callApi("POST", "/v3/psn-auth-url", jsonParm);
            JsonObject root = gson.fromJson(resp.getBody(), JsonObject.class);
            int code = root.has("code") ? root.get("code").getAsInt() : -1;
            if (code == 1450005) {
                // 个人用户已实名，查询并保存psnId
                log.info("e签宝提示已实名，查询psnId并保存, mobile={}", mobile);
                String psnId = queryAndSavePsnId(userId, mobile);
                if (psnId != null) return null; // 已保存psnId，无需认证
                throw new RuntimeException("已实名但查询psnId失败");
            }
            if (code != 0) {
                String msg = root.has("message") ? root.get("message").getAsString() : "未知错误";
                log.error("e签宝获取认证链接失败: code={}, message={}, body={}", code, msg, resp.getBody());
                throw new RuntimeException("e签宝返回错误(" + code + "): " + msg);
            }
            JsonObject data = root.getAsJsonObject("data");
            if (data == null || data.isJsonNull()) {
                log.error("e签宝获取认证链接data为空: body={}", resp.getBody());
                throw new RuntimeException("e签宝返回数据为空");
            }
            return data.get("authUrl").getAsString();
        } catch (Exception e) {
            log.error("获取e签宝认证链接失败, mobile={}, error={}", mobile, e.getMessage(), e);
            throw new RuntimeException("获取 e签宝 认证链接失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String queryAndSavePsnId(Long userId, String mobile) {
        try {
            EsignHttpResponse resp = callApi("GET", "/v3/persons/identity-info?psnAccount=" + mobile, null);
            JsonObject root = gson.fromJson(resp.getBody(), JsonObject.class);
            // 账号不存在（code=1435203）或data为空 → 用户未在e签宝注册，返回null
            if (root.has("code") && root.get("code").getAsInt() != 0) {
                log.info("e签宝查询psnId：用户未注册或未实名, mobile={}, code={}, msg={}",
                        mobile, root.get("code"), root.get("message"));
                return null;
            }
            JsonObject data = root.has("data") && !root.get("data").isJsonNull() ? root.getAsJsonObject("data") : null;
            if (data == null) {
                log.info("e签宝查询psnId：data为空, mobile={}", mobile);
                return null;
            }
            // realnameStatus: 0=未实名, 1=已实名(可能信息不全), 2=已实名
            String psnId = (data.has("psnId") && !data.get("psnId").isJsonNull()) ? data.get("psnId").getAsString() : null;
            if (psnId == null || psnId.isEmpty()) {
                log.warn("e签宝查询psnId为空, mobile={}, realnameStatus={}", mobile, data.get("realnameStatus"));
                return null;
            }
            userMapper.update(null, new LambdaUpdateWrapper<HzUser>().eq(HzUser::getUserId, userId)
                    .set(HzUser::getEsignPsnId, psnId)
                    .set(HzUser::getAuthStatus, "2")
                    .set(HzUser::getAuthTime, new java.util.Date()));
            log.info("e签宝个人认证完成，userId={}, psnId={}", userId, psnId);
            return psnId;
        } catch (Exception e) {
            log.error("查询e签宝认证状态失败, userId={}, error={}", userId, e.getMessage(), e);
            throw new RuntimeException("查询 e签宝 认证状态失败: " + e.getMessage(), e);
        }
    }

    // ==================== 模板模式 ====================

    @Override
    public String queryTemplateDetail() throws Exception {
        EsignHttpResponse resp = callApi("GET", "/v3/doc-templates/" + templateId, null);
        JsonObject root = gson.fromJson(resp.getBody(), JsonObject.class);
        if (root.get("code").getAsInt() != 0) {
            throw new RuntimeException("查询模板详情失败: " + root.get("message").getAsString());
        }
        log.info("模板详情查询成功，templateId={}", templateId);
        return resp.getBody();
    }

    @Override
    public String createFileByTemplate(Long contractId) throws Exception {
        HzContract contract = contractMapper.selectById(contractId);
        if (contract == null) throw new RuntimeException("合同不存在: " + contractId);

        HzUser user = userMapper.selectById(contract.getTenantId());
        if (user == null) throw new RuntimeException("用户不存在");

        String componentsJson = buildTemplateComponents(contract, user);
        String fileName = "租赁合同_" + contractId + ".pdf";

        String jsonParm = "{\"docTemplateId\":\"" + templateId + "\","
                + "\"fileName\":\"" + fileName + "\","
                + "\"components\":" + componentsJson + "}";

        EsignHttpResponse resp = callApi("POST", "/v3/files/create-by-doc-template", jsonParm);
        JsonObject root = gson.fromJson(resp.getBody(), JsonObject.class);
        if (root.get("code").getAsInt() != 0) {
            throw new RuntimeException("模板填充生成文件失败: " + root.get("message").getAsString());
        }

        String fileId = root.getAsJsonObject("data").get("fileId").getAsString();
        log.info("e签宝模板填充成功，contractId={}, fileId={}", contractId, fileId);

        // 轮询等待文件就绪（状态 2 或 5）
        for (int i = 0; i < 10; i++) {
            TimeUnit.SECONDS.sleep(2);
            EsignHttpResponse statusResp = callApi("GET", "/v3/files/" + fileId, null);
            JsonObject statusRoot = gson.fromJson(statusResp.getBody(), JsonObject.class);
            if (statusRoot.get("code").getAsInt() == 0) {
                String fileStatus = statusRoot.getAsJsonObject("data").get("fileStatus").getAsString();
                log.info("e签宝文件状态查询第{}次，fileStatus={}", i + 1, fileStatus);
                if ("2".equals(fileStatus) || "5".equals(fileStatus)) {
                    log.info("e签宝文件已就绪，fileId={}", fileId);
                    return fileId;
                }
            }
        }
        throw new RuntimeException("e签宝文件长时间未就绪，fileId=" + fileId);
    }

    @Override
    public String initSign(Long contractId, String psnId, String redirectUrl) throws Exception {
        // 1. 模板填充生成文件
        String fileId = createFileByTemplate(contractId);

        // 2. 创建签署流
        HzContract contract = contractMapper.selectById(contractId);
        if (contract == null) throw new RuntimeException("合同不存在: " + contractId);

        String jsonParm = "{\"docs\":[{\"fileId\":\"" + fileId + "\",\"fileName\":\"港好住租赁合同.pdf\"}],"
                + "\"signFlowConfig\":{\"signFlowTitle\":\"港好住租赁合同-" + contractId + "\",\"autoFinish\":true,"
                + "\"notifyUrl\":\"" + notifyUrl + "\",\"chargeConfig\":{\"chargeMode\":0}},"
                // 甲方（平台企业，自动盖章）— 签在第14页企业章位置
                + "\"signers\":[{\"signerType\":1,\"orgSignerInfo\":{\"orgId\":\"" + orgId + "\"},"
                + "\"signConfig\":{\"signOrder\":1},\"signFields\":[{\"fileId\":\"" + fileId + "\","
                + "\"normalSignFieldConfig\":{\"autoSign\":true,\"freeMode\":false,"
                + "\"signFieldPosition\":{\"positionPage\":\"14\",\"positionX\":240,\"positionY\":646},"
                + "\"signFieldStyle\":1},\"signFieldType\":0}]},"
                // 乙方（租户个人，手动签）— 签在第1页个人签名位置
                + "{\"signerType\":0,\"psnSignerInfo\":{\"psnId\":\"" + psnId + "\"},"
                + "\"signConfig\":{\"signOrder\":2},\"signFields\":[{\"fileId\":\"" + fileId + "\","
                + "\"normalSignFieldConfig\":{\"autoSign\":false,\"freeMode\":false,"
                + "\"signFieldPosition\":{\"positionPage\":\"1\",\"positionX\":255,\"positionY\":290},"
                + "\"signFieldStyle\":1},\"signFieldType\":0}]}]}";

        EsignHttpResponse resp = callApi("POST", "/v3/sign-flow/create-by-file", jsonParm);
        JsonObject root = gson.fromJson(resp.getBody(), JsonObject.class);
        if (root.get("code").getAsInt() != 0) {
            throw new RuntimeException("创建签署流失败: " + root.get("message").getAsString());
        }
        String signFlowId = root.getAsJsonObject("data").get("signFlowId").getAsString();

        // 3. 保存 esignFlowId 到合同
        contractMapper.update(null, new LambdaUpdateWrapper<HzContract>()
                .eq(HzContract::getContractId, contractId)
                .set(HzContract::getEsignFlowId, signFlowId)
                .set(HzContract::getContractStatus, "1")); // 1=待签署
        log.info("签署流创建成功，contractId={}, signFlowId={}", contractId, signFlowId);

        // 4. 启动签署流（将状态从"草稿"变为"签署中"，签署人才能签署）
        EsignHttpResponse startResp = callApi("POST", "/v3/sign-flow/" + signFlowId + "/start", "");
        JsonObject startRoot = gson.fromJson(startResp.getBody(), JsonObject.class);
        if (startRoot.get("code").getAsInt() != 0) {
            throw new RuntimeException("启动签署流失败: " + startRoot.get("message").getAsString());
        }
        log.info("签署流已启动，signFlowId={}", signFlowId);

        // 5. 获取签署链接
        HzUser signUser = userMapper.selectById(contract.getTenantId());
        String mobile = signUser != null ? signUser.getPhone() : "";
        return getSignUrl(signFlowId, mobile, redirectUrl);
    }

    /**
     * 构建模板填充数据 — 将合同+租户数据映射到 e签宝 模板控件
     * 控件 ID 已通过 GET /system/contract/esign-template 确认（模板版本：2026-3-30 招商反馈）
     *
     * 控件布局（按页码/Y坐标排序）：
     *   页1  Y=220  日期3 (yyyy-MM-dd)       → 合同签约日期
     *   页2  Y=80   日期1/2 (yyyy年MM月dd日) → 合同起止日期
     *   页2  Y=301  数字3                    → 租期(月)
     *   页2  Y=356  单行文本3               → 乙方姓名
     *   页2  Y=499  单行文本7 (default:\)   → 保留默认值，不传
     *   页2  Y=530  手机号1                 → 手机号
     *   页2  Y=558  身份证号1               → 身份证号
     *   页2  Y=685  单行文本5 (default:栗毅) → 甲方代表人，保留默认值，不传
     *   页3  Y=401  单行文本4               → 房源地址
     *   页3  Y=425  数字1                   → 月租金
     *   页3  Y=423  数字2                   → 押金
     *   页14 Y=736  单行文本9               → 乙方姓名（签署页）
     *   页1  Y=752  单行文本10              → 保留默认值，不传
     */
    private String buildTemplateComponents(HzContract contract, HzUser user) {
        // ── 租户信息（优先从合同表取，回退到用户表）────────────────────
        String tenantName = contract.getTenantName() != null ? contract.getTenantName()
                          : (user.getRealName()  != null ? user.getRealName()
                          : (user.getNickname()  != null ? user.getNickname() : ""));
        String idCard     = contract.getTenantIdCard()  != null ? contract.getTenantIdCard()
                          : (user.getIdCard()    != null ? user.getIdCard()  : "");
        String phone      = contract.getTenantPhone()   != null ? contract.getTenantPhone()
                          : (user.getPhone()     != null ? user.getPhone()   : "");

        // ── 合同日期 ────────────────────────────────────────────────────
        String startDate  = contract.getStartDate() != null ? contract.getStartDate() : "";
        String endDate    = contract.getEndDate()   != null ? contract.getEndDate()   : "";
        // 日期1/日期2控件要求 yyyy年MM月dd日 格式
        String startDateCn = "";
        String endDateCn   = "";
        try {
            java.time.format.DateTimeFormatter isoFmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
            java.time.format.DateTimeFormatter cnFmt  = java.time.format.DateTimeFormatter.ofPattern("yyyy年MM月dd日");
            if (!startDate.isEmpty()) startDateCn = java.time.LocalDate.parse(startDate, isoFmt).format(cnFmt);
            if (!endDate.isEmpty())   endDateCn   = java.time.LocalDate.parse(endDate, isoFmt).format(cnFmt);
        } catch (Exception e) {
            startDateCn = startDate;
            endDateCn   = endDate;
        }

        // ── 金额 ────────────────────────────────────────────────────────
        String rentPrice  = contract.getRentPrice() != null ? contract.getRentPrice().toString() : "0";
        String deposit    = contract.getDeposit()   != null ? contract.getDeposit().toString()   : "0";

        // ── 房源与租期 ──────────────────────────────────────────────────
        String houseAddress = contract.getHouseAddress() != null ? contract.getHouseAddress() : "";
        String rentMonths   = contract.getRentMonths()   != null ? contract.getRentMonths().toString() : "";

        return "[\n"
            // 页1: 合同签约日期
            + "  {\"componentId\": \"5a2b66c26133442d9bbfe8ea93fa45ae\", \"componentValue\": \"" + escapeJson(startDate)    + "\"},\n"  // 日期3 (yyyy-MM-dd)
            // 页2: 合同期限
            + "  {\"componentId\": \"768fda9eaf6d4492bc56e2b90a08c97d\", \"componentValue\": \"" + escapeJson(startDateCn) + "\"},\n"  // 日期1: 起始日 (yyyy年MM月dd日)
            + "  {\"componentId\": \"8608e65d7a8943d9bd6bf25b6de50045\", \"componentValue\": \"" + escapeJson(endDateCn)   + "\"},\n"  // 日期2: 终止日 (yyyy年MM月dd日)
            + "  {\"componentId\": \"7af62cd84df944dba1d828dba66ae394\", \"componentValue\": \"" + escapeJson(rentMonths)   + "\"},\n"  // 数字3: 租期(月)
            // 页2: 乙方个人信息
            + "  {\"componentId\": \"ebfedbda264e446390801f1ba6ee96eb\", \"componentValue\": \"" + escapeJson(tenantName)   + "\"},\n"  // 单行文本3: 乙方姓名
            + "  {\"componentId\": \"520eaa1e2b634c0592937bd216a74cf5\", \"componentValue\": \"" + escapeJson(phone)        + "\"},\n"  // 手机号1
            + "  {\"componentId\": \"ba2d50d300394daba46764c3f7ca5aec\", \"componentValue\": \"" + escapeJson(idCard)       + "\"},\n"  // 身份证号1
            // 页3: 租金押金
            + "  {\"componentId\": \"37fdb4123afb45139912f5dc938d5e3c\", \"componentValue\": \"" + escapeJson(houseAddress) + "\"},\n"  // 单行文本4: 房源地址
            + "  {\"componentId\": \"d2a93cfe598449c49fac10a5c8d58f08\", \"componentValue\": \"" + rentPrice               + "\"},\n"  // 数字1: 月租金
            + "  {\"componentId\": \"ef7ed4f368094d19a52d45369994e7e7\", \"componentValue\": \"" + deposit                 + "\"},\n"  // 数字2: 押金
            // 页14: 签署页乙方姓名
            + "  {\"componentId\": \"23c33b4791934632b6cc8322d8b15fe3\", \"componentValue\": \"" + escapeJson(tenantName)   + "\"}\n"   // 单行文本9: 乙方姓名(签署页)
            + "]";
        // 单行文本5 (default:栗毅, 甲方代表人)、单行文本7 (default:\)、单行文本10 — 保留模板默认值，不传
    }

    /**
     * 按 e签宝文档规则处理 Query 参数：
     * 对 key 按 ASCII 升序排序，然后拼接对应的 value（不含 key=）
     * 例如 "b=2&a=1" → 排序后 a,b → 拼接 value "12"
     */
    private String sortQueryValues(String queryString) {
        if (queryString == null || queryString.isEmpty()) return "";
        java.util.TreeMap<String, String> map = new java.util.TreeMap<>();
        for (String pair : queryString.split("&")) {
            int idx = pair.indexOf('=');
            if (idx > 0) {
                map.put(pair.substring(0, idx), pair.substring(idx + 1));
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String v : map.values()) sb.append(v);
        return sb.toString();
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    // ==================== 旧方法（保留兼容）====================

    @SuppressWarnings("unused")
    private String uploadContractPdf(byte[] pdfBytes, String fileName) throws Exception {
        EsignFileBean fileBean = new EsignFileBean(pdfBytes, fileName);
        String getUrlParm = "{\"contentMd5\":\"" + fileBean.getFileContentMD5() + "\",\"fileName\":\"" + fileName
                + "\",\"fileSize\":" + fileBean.getFileSize() + ",\"convertToPDF\":false,\"contentType\":\"application/octet-stream\"}";
        EsignHttpResponse getUrlResp = callApi("POST", "/v3/files/file-upload-url", getUrlParm);
        JsonObject getUrlData = gson.fromJson(getUrlResp.getBody(), JsonObject.class).getAsJsonObject("data");
        String fileId = getUrlData.get("fileId").getAsString();
        String uploadUrl = getUrlData.get("fileUploadUrl").getAsString();
        EsignHttpHelper.doUploadHttp(uploadUrl, EsignRequestType.PUT, fileBean.getFileBytes(), fileBean.getFileContentMD5(), "application/octet-stream", false);
        for (int i = 0; i < 5; i++) {
            TimeUnit.SECONDS.sleep(2);
            EsignHttpResponse sr = callApi("GET", "/v3/files/" + fileId, null);
            String fs = gson.fromJson(sr.getBody(), JsonObject.class).getAsJsonObject("data").get("fileStatus").getAsString();
            if ("2".equalsIgnoreCase(fs) || "5".equalsIgnoreCase(fs)) { log.info("e签宝文件已就绪 fileId={}", fileId); return fileId; }
        }
        throw new RuntimeException("e签宝文件长时间未就绪，fileId=" + fileId);
    }

    @SuppressWarnings("unused")
    @Transactional
    private String createSignFlow(Long contractId, String psnId, String fileId) throws Exception {
        HzContract contract = contractMapper.selectById(contractId);
        if (contract == null) throw new RuntimeException("合同不存在: " + contractId);
        String jsonParm = "{\"docs\":[{\"fileId\":\"" + fileId + "\",\"fileName\":\"港好住租赁合同.pdf\"}],"
                + "\"signFlowConfig\":{\"signFlowTitle\":\"港好住租赁合同-" + contractId + "\",\"autoFinish\":true,"
                + "\"notifyUrl\":\"" + notifyUrl + "\",\"chargeConfig\":{\"chargeMode\":0}},"
                + "\"signers\":[{\"signerType\":1,\"orgSignerInfo\":{\"orgId\":\"" + orgId + "\"},"
                + "\"signConfig\":{\"signOrder\":1},\"signFields\":[{\"fileId\":\"" + fileId + "\","
                + "\"normalSignFieldConfig\":{\"autoSign\":true,\"freeMode\":false,"
                + "\"signFieldPosition\":{\"positionPage\":\"1\",\"positionX\":120,\"positionY\":460},"
                + "\"signFieldStyle\":1},\"signFieldType\":0}]},"
                + "{\"signerType\":0,\"psnSignerInfo\":{\"psnId\":\"" + psnId + "\"},"
                + "\"signConfig\":{\"signOrder\":2},\"signFields\":[{\"fileId\":\"" + fileId + "\","
                + "\"normalSignFieldConfig\":{\"autoSign\":false,\"freeMode\":false,"
                + "\"signFieldPosition\":{\"positionPage\":\"1\",\"positionX\":320,\"positionY\":460},"
                + "\"signFieldStyle\":1},\"signFieldType\":0}]}]}";
        EsignHttpResponse resp = callApi("POST", "/v3/sign-flow/create-by-file", jsonParm);
        String signFlowId = gson.fromJson(resp.getBody(), JsonObject.class).getAsJsonObject("data").get("signFlowId").getAsString();
        contractMapper.update(null, new LambdaUpdateWrapper<HzContract>().eq(HzContract::getContractId, contractId)
                .set(HzContract::getEsignFlowId, signFlowId).set(HzContract::getContractStatus, "1"));
        log.info("e签宝签署流程创建成功 contractId={} signFlowId={}", contractId, signFlowId);
        return signFlowId;
    }

    @Override
    public String getSignUrl(String signFlowId, String mobile, String redirectUrl) throws Exception {
        String jsonParm = "{\"clientType\":\"ALL\",\"needLogin\":false,\"operator\":{\"psnAccount\":\"" + mobile + "\"},"
                + "\"redirectConfig\":{\"redirectUrl\":\"" + redirectUrl + "\"},\"urlType\":2}";
        log.info("获取签署链接: signFlowId={}, mobile={}, request={}", signFlowId, mobile, jsonParm);
        EsignHttpResponse resp = callApi("POST", "/v3/sign-flow/" + signFlowId + "/sign-url", jsonParm);
        log.info("获取签署链接响应: {}", resp.getBody());
        return gson.fromJson(resp.getBody(), JsonObject.class).getAsJsonObject("data").get("url").getAsString();
    }

    // ==================== 主动查询签署状态（不依赖回调）====================

    @Override
    @Transactional
    public boolean checkAndFinalizeSignFlow(Long contractId) throws Exception {
        HzContract contract = contractMapper.selectById(contractId);
        if (contract == null) throw new RuntimeException("合同不存在: " + contractId);

        // 已签署，直接返回
        if ("2".equals(contract.getContractStatus())) {
            log.info("合同已签署，无需再查 contractId={}", contractId);
            return true;
        }

        String signFlowId = contract.getEsignFlowId();
        if (signFlowId == null || signFlowId.isEmpty()) {
            log.warn("合同尚未创建签署流 contractId={}", contractId);
            return false;
        }

        // 直接查 e签宝 签署流状态
        EsignHttpResponse resp = callApi("GET", "/v3/sign-flow/" + signFlowId, null);
        JsonObject root = gson.fromJson(resp.getBody(), JsonObject.class);
        if (root.get("code").getAsInt() != 0) {
            log.warn("查询签署流状态失败 signFlowId={}, resp={}", signFlowId, resp.getBody());
            return false;
        }

        JsonObject data = root.getAsJsonObject("data");
        String flowStatus = data != null && data.has("signFlowStatus")
                ? data.get("signFlowStatus").getAsString() : "";
        log.info("e签宝签署流状态 signFlowId={}, status={}", signFlowId, flowStatus);

        if (!"FINISH".equals(flowStatus)) {
            return false;
        }

        // 签署完成，执行与回调相同的后续逻辑
        contractMapper.update(null, new LambdaUpdateWrapper<HzContract>()
                .eq(HzContract::getContractId, contractId)
                .set(HzContract::getContractStatus, "2"));

        // 获取 PDF 下载链接
        try {
            EsignHttpResponse fileResp = callApi("GET",
                    "/v3/sign-flow/" + signFlowId + "/file-download-url", "");
            JsonObject fileRoot = gson.fromJson(fileResp.getBody(), JsonObject.class);
            if (fileRoot.get("code").getAsInt() == 0) {
                String fileUrl = fileRoot.getAsJsonObject("data")
                        .getAsJsonArray("files").get(0).getAsJsonObject()
                        .get("downloadUrl").getAsString();
                contractMapper.update(null, new LambdaUpdateWrapper<HzContract>()
                        .eq(HzContract::getContractId, contractId)
                        .set(HzContract::getContractContent, fileUrl));
            }
        } catch (Exception e) {
            log.warn("获取已签PDF链接失败（不影响主流程）: {}", e.getMessage());
        }

        generateBills(contract);
        generateCheckInRecord(contract);

        HzHouseOrder order = orderMapper.selectOne(new LambdaQueryWrapper<HzHouseOrder>()
                .eq(HzHouseOrder::getContractId, contractId).last("LIMIT 1"));
        if (order != null && "0".equals(order.getOrderStatus())) {
            orderMapper.update(null, new LambdaUpdateWrapper<HzHouseOrder>()
                    .eq(HzHouseOrder::getOrderNo, order.getOrderNo())
                    .set(HzHouseOrder::getOrderStatus, "1"));
        }

        log.info("主动查询签署完成，已更新合同/账单/入住记录 contractId={}", contractId);
        return true;
    }

    // ==================== 回调处理 ====================

    @Override
    public String getSignedPdfUrl(String flowId) throws Exception {
        log.info("获取已签合同PDF链接，flowId={}", flowId);
        EsignHttpResponse fileResp = callApi("GET",
                "/v3/sign-flow/" + flowId + "/file-download-url", "");
        JsonObject fileRoot = gson.fromJson(fileResp.getBody(), JsonObject.class);
        if (fileRoot.get("code").getAsInt() == 0) {
            String fileUrl = fileRoot.getAsJsonObject("data").getAsJsonArray("files").get(0).getAsJsonObject().get("downloadUrl").getAsString();
            log.info("获取已签合同PDF链接成功，flowId={}", flowId);
            return fileUrl;
        }
        throw new RuntimeException("获取签署文件失败：" + fileRoot.get("message").getAsString());
    }

    @Override
    @Transactional
    public void handleSignCallback(String timestamp, String requestQuery, String body, String signature) throws Exception {
        String ts = timestamp != null ? timestamp : "";

        // e签宝文档：Query 参数按 key ASCII 升序排序后，拼接 value（不含 key=）
        // 若无 query 参数（通常如此），sortedQueryValues 为空串
        String sortedQueryValues = sortQueryValues(requestQuery);

        boolean verified = EsignEncryption.callBackCheck(ts, sortedQueryValues, body, appSecret, signature);
        if (!verified) {
            log.error("e签宝回调验签失败: timestamp={}, signature={}, sortedQuery={}, bodyLen={}",
                    ts, signature, sortedQueryValues, body.length());
            throw new RuntimeException("e签宝回调验签失败");
        }
        log.info("e签宝回调验签通过");
        JsonObject nb = gson.fromJson(body, JsonObject.class);
        String action = nb.get("action").getAsString();
        // e签宝V3回调使用signFlowId字段
        String flowId = nb.has("signFlowId") ? nb.get("signFlowId").getAsString() : nb.get("flowId").getAsString();
        int signResult = nb.has("signResult") ? nb.get("signResult").getAsInt() : -1;
        int signOrder = nb.has("signOrder") ? nb.get("signOrder").getAsInt() : 0;
        log.info("e签宝回调 action={} flowId={} signResult={} signOrder={}", action, flowId, signResult, signOrder);
        // 只处理签署完成回调，且必须是个人签署完成（signOrder=2）或整个流程完成
        // signOrder=1 是企业自动盖章，不应触发账单生成
        if (!"SIGN_MISSON_COMPLETE".equals(action) && !"SIGN_FLOW_UPDATE".equals(action)) return;
        if (signResult != 2) return;
        if (signOrder == 1) {
            log.info("企业自动盖章完成（signOrder=1），等待个人签署，flowId={}", flowId);
            return;
        }

        HzContract contract = contractMapper.selectOne(new LambdaQueryWrapper<HzContract>().eq(HzContract::getEsignFlowId, flowId).last("LIMIT 1"));
        if (contract == null) { log.warn("e签宝回调未找到对应合同 flowId={}", flowId); return; }
        if ("2".equals(contract.getContractStatus())) { log.info("合同已处理，忽略重复回调 flowId={}", flowId); return; }

        // 1. 更新合同状态为已签署
        contractMapper.update(null, new LambdaUpdateWrapper<HzContract>()
                .eq(HzContract::getContractId, contract.getContractId())
                .set(HzContract::getContractStatus, "2")); // 2=已签署

        // 2. 获取已签合同PDF下载链接
        try {
            EsignHttpResponse fileResp = callApi("GET",
                    "/v3/sign-flow/" + flowId + "/file-download-url", "");
            JsonObject fileRoot = gson.fromJson(fileResp.getBody(), JsonObject.class);
            if (fileRoot.get("code").getAsInt() == 0) {
                String fileUrl = fileRoot.getAsJsonObject("data").getAsJsonArray("files").get(0).getAsJsonObject().get("downloadUrl").getAsString();
                contractMapper.update(null, new LambdaUpdateWrapper<HzContract>()
                        .eq(HzContract::getContractId, contract.getContractId())
                        .set(HzContract::getContractContent, fileUrl));
                log.info("已签合同PDF下载链接已保存，contractId={}", contract.getContractId());
            }
        } catch (Exception e) {
            log.warn("获取已签合同PDF链接失败，不影响主流程：{}", e.getMessage());
        }

        // 3. 生成账单（押金 + 租金）— 失败则整个事务回滚
        generateBills(contract);
        log.info("签署回调：账单生成成功，contractId={}", contract.getContractId());

        // 4. 生成入住记录
        generateCheckInRecord(contract);
        log.info("签署回调：入住记录生成成功，contractId={}", contract.getContractId());

        // 5. 推进预订单状态
        HzHouseOrder order = orderMapper.selectOne(new LambdaQueryWrapper<HzHouseOrder>()
                .eq(HzHouseOrder::getContractId, contract.getContractId()).last("LIMIT 1"));
        if (order != null && "0".equals(order.getOrderStatus())) {
            orderMapper.update(null, new LambdaUpdateWrapper<HzHouseOrder>()
                    .eq(HzHouseOrder::getOrderNo, order.getOrderNo())
                    .set(HzHouseOrder::getOrderStatus, "1")); // 1=待付押金
            log.info("预订单推进至待付押金 orderNo={}", order.getOrderNo());
        }
    }

    // ==================== 账单生成（从 HzContractAppController 搬迁）====================

    private void generateBills(HzContract contract) {
        // 0. 检查是否已生成过账单（防止e签宝重复回调）
        Long existCount = billMapper.selectCount(new LambdaQueryWrapper<HzBill>()
                .eq(HzBill::getContractId, contract.getContractId()));
        if (existCount > 0) {
            log.info("合同{}的账单已存在（{}条），跳过重复生成", contract.getContractId(), existCount);
            return;
        }

        // 1. 生成押金账单
        // 查询关联预订单，将 orderNo 写入押金账单，微信支付回调时依赖此字段更新订单状态
        HzHouseOrder relatedOrder = orderMapper.selectOne(new LambdaQueryWrapper<HzHouseOrder>()
                .eq(HzHouseOrder::getContractId, contract.getContractId())
                .eq(HzHouseOrder::getDelFlag, "0")
                .last("LIMIT 1"));

        HzBill depositBill = new HzBill();
        depositBill.setBillNo("YJ" + DateUtils.dateTimeNow(DateUtils.YYYYMMDDHHMMSS));
        depositBill.setContractId(contract.getContractId());
        depositBill.setTenantId(contract.getTenantId());
        depositBill.setTenantName(contract.getTenantName());
        depositBill.setHouseId(contract.getHouseId());
        depositBill.setHouseCode(contract.getHouseCode());
        depositBill.setBillType("1");  // 1=押金
        depositBill.setBillPeriod(contract.getStartDate());
        depositBill.setBillAmount(contract.getDeposit());
        depositBill.setPaidAmount(BigDecimal.ZERO);
        depositBill.setUnpaidAmount(contract.getDeposit());
        depositBill.setBillDate(contract.getStartDate());
        depositBill.setDueDate(contract.getStartDate());
        depositBill.setLateFee(BigDecimal.ZERO);
        depositBill.setBillStatus("0");  // 0=待支付
        depositBill.setDelFlag("0");
        // 关联预订单号，微信回调时通过此字段触发 onDepositPaid
        if (relatedOrder != null) {
            depositBill.setOrderNo(relatedOrder.getOrderNo());
        }
        billMapper.insert(depositBill);

        // 2. 生成租金账单
        int paymentCycle = Integer.parseInt(contract.getPaymentCycle());
        int rentMonths = contract.getRentMonths();
        BigDecimal monthlyRent = contract.getRentPrice();
        int freeRentPeriods = contract.getFreeRentPeriods() != null ? contract.getFreeRentPeriods() : 0;
        int billCount = rentMonths / paymentCycle;
        if (rentMonths % paymentCycle != 0) billCount++;

        LocalDate startDate = LocalDate.parse(contract.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        for (int i = 0; i < billCount; i++) {
            HzBill rentBill = new HzBill();
            rentBill.setBillNo("ZJ" + DateUtils.dateTimeNow(DateUtils.YYYYMMDDHHMMSS) + String.format("%02d", i + 1));
            rentBill.setContractId(contract.getContractId());
            rentBill.setTenantId(contract.getTenantId());
            rentBill.setTenantName(contract.getTenantName());
            rentBill.setHouseId(contract.getHouseId());
            rentBill.setHouseCode(contract.getHouseCode());
            rentBill.setBillType("2");  // 2=租金

            LocalDate billDueDate = startDate.plusMonths((long) i * paymentCycle);
            rentBill.setBillPeriod(billDueDate.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            rentBill.setDueDate(billDueDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            rentBill.setBillDate(billDueDate.format(DateTimeFormatter.ISO_LOCAL_DATE));

            int monthsForThisBill = Math.min(paymentCycle, rentMonths - i * paymentCycle);
            BigDecimal billAmount = monthlyRent.multiply(new BigDecimal(monthsForThisBill));
            if (i < freeRentPeriods) billAmount = BigDecimal.ZERO;

            rentBill.setBillAmount(billAmount);
            rentBill.setLateFee(BigDecimal.ZERO);
            rentBill.setDelFlag("0");
            if (billAmount.compareTo(BigDecimal.ZERO) == 0) {
                rentBill.setPaidAmount(BigDecimal.ZERO);
                rentBill.setUnpaidAmount(BigDecimal.ZERO);
                rentBill.setBillStatus("1");  // 已支付
            } else {
                rentBill.setPaidAmount(BigDecimal.ZERO);
                rentBill.setUnpaidAmount(billAmount);
                rentBill.setBillStatus("0");  // 待支付
            }
            billMapper.insert(rentBill);
        }
        log.info("合同 {} 生成账单成功：押金1条，租金{}条，免租{}期", contract.getContractNo(), billCount, freeRentPeriods);
    }

    // ==================== 入住记录生成（从 HzContractAppController 搬迁）====================

    private void generateCheckInRecord(HzContract contract) {
        // 检查是否已存在入住记录（防止重复回调）
        HzCheckIn existing = checkInService.selectCheckInByContractId(contract.getContractId());
        if (existing != null) {
            log.info("合同{}的入住记录已存在(recordId={})，跳过重复生成", contract.getContractId(), existing.getRecordId());
            return;
        }

        HzHouse house = houseMapper.selectById(contract.getHouseId());
        if (house == null) { log.warn("房源不存在，跳过入住记录生成，houseId={}", contract.getHouseId()); return; }
        HzProject project = projectMapper.selectById(house.getProjectId());
        if (project == null) project = new HzProject();

        HzBuilding building = buildingMapper.selectById(house.getBuildingId());
        HzUnit unit = unitMapper.selectById(house.getUnitId());

        String fullAddress = project.getAddress() != null ? project.getAddress() : "";
        if (building != null) fullAddress += building.getBuildingName() != null ? building.getBuildingName() : "";
        if (unit != null) fullAddress += unit.getUnitName() != null ? unit.getUnitName() : "";
        fullAddress += house.getHouseNo() != null ? house.getHouseNo() : "";

        HzCheckIn checkIn = new HzCheckIn();
        checkIn.setApplyId(0L);
        checkIn.setContractId(contract.getContractId());
        checkIn.setTenantId(contract.getTenantId());
        checkIn.setHouseId(contract.getHouseId());
        checkIn.setCheckinDate(contract.getStartDate());
        checkIn.setMeterReadingElectric("0");
        checkIn.setMeterReadingWater("0");
        checkIn.setMeterReadingGas("0");
        checkIn.setKeyCount(1);
        checkIn.setStatus("0");  // 0=待办理
        checkIn.setDelFlag("0");
        checkIn.setRemark(String.format("项目：%s | 房间：%s | 租期：%s至%s（%d个月） | 月租金：%s元 | 押金：%s元",
                project.getProjectName() != null ? project.getProjectName() : "",
                fullAddress,
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getRentMonths(),
                contract.getRentPrice(),
                contract.getDeposit()));
        checkInService.insertCheckIn(checkIn);
        log.info("合同 {} 生成入驻记录成功：recordId={}", contract.getContractNo(), checkIn.getRecordId());
    }

    // ==================== 内部工具 ====================

    private EsignHttpResponse callApi(String method, String apiAddr, String jsonParm) throws EsignDemoException {
        log.info("e签宝API调用: {} {} host={}", method, apiAddr, host);
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(appId, appSecret, jsonParm, method, apiAddr, false);
        EsignHttpResponse resp = EsignHttpHelper.doCommHttp(host, apiAddr, EsignRequestType.valueOf(method.toUpperCase()), jsonParm, header, false);
        log.info("e签宝API响应: {} {} status={} body={}", method, apiAddr, resp.getStatus(), resp.getBody());
        return resp;
    }
}
