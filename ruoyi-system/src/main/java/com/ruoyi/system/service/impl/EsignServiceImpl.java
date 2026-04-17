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
import com.ruoyi.system.service.IHzUserMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private final HzHouseFacilityMapper houseFacilityMapper;
    private final IHzCheckInService checkInService;
    private final IHzUserMessageService messageService;

    public EsignServiceImpl(HzUserMapper userMapper, HzContractMapper contractMapper,
                            HzHouseOrderMapper orderMapper, HzBillMapper billMapper,
                            HzHouseMapper houseMapper, HzProjectMapper projectMapper,
                            HzBuildingMapper buildingMapper, HzUnitMapper unitMapper,
                            HzHouseFacilityMapper houseFacilityMapper,
                            IHzCheckInService checkInService, IHzUserMessageService messageService) {
        this.userMapper = userMapper;
        this.contractMapper = contractMapper;
        this.orderMapper = orderMapper;
        this.billMapper = billMapper;
        this.houseMapper = houseMapper;
        this.projectMapper = projectMapper;
        this.buildingMapper = buildingMapper;
        this.unitMapper = unitMapper;
        this.houseFacilityMapper = houseFacilityMapper;
        this.checkInService = checkInService;
        this.messageService = messageService;
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
                // 甲方（平台企业，自动盖章）— 签在第14页企业章位置，显示签署日期
                + "\"signers\":[{\"signerType\":1,\"orgSignerInfo\":{\"orgId\":\"" + orgId + "\"},"
                + "\"signConfig\":{\"signOrder\":1},\"signFields\":[{\"fileId\":\"" + fileId + "\","
                + "\"normalSignFieldConfig\":{\"autoSign\":true,\"freeMode\":false,"
                + "\"signFieldPosition\":{\"positionPage\":\"14\",\"positionX\":240,\"positionY\":646},"
                + "\"signFieldStyle\":1},\"signFieldType\":0,"
                + "\"signDateConfig\":{\"showSignDate\":1,\"dateFormat\":\"yyyy年MM月dd日\",\"fontSize\":12,\"signDatePositionX\":380,\"signDatePositionY\":639}}]},"
                + "{\"signerType\":0,\"psnSignerInfo\":{\"psnId\":\"" + psnId + "\"},"
                + "\"signConfig\":{\"signOrder\":2},\"signFields\":["
                // 乙方签名1: 页1 个人章/签名2
                + "{\"fileId\":\"" + fileId + "\","
                + "\"normalSignFieldConfig\":{\"autoSign\":false,\"freeMode\":false,"
                + "\"signFieldPosition\":{\"positionPage\":\"1\",\"positionX\":255,\"positionY\":290},"
                + "\"signFieldStyle\":1},\"signFieldType\":0},"
                // 乙方签名2: 页2 个人章/签名1
                + "{\"fileId\":\"" + fileId + "\","
                + "\"normalSignFieldConfig\":{\"autoSign\":false,\"freeMode\":false,"
                + "\"signFieldPosition\":{\"positionPage\":\"2\",\"positionX\":282,\"positionY\":595},"
                + "\"signFieldStyle\":1},\"signFieldType\":0},"
                // 乙方签名3: 页14 个人章/签名4，显示签署日期
                + "{\"fileId\":\"" + fileId + "\","
                + "\"normalSignFieldConfig\":{\"autoSign\":false,\"freeMode\":false,"
                + "\"signFieldPosition\":{\"positionPage\":\"14\",\"positionX\":243,\"positionY\":536},"
                + "\"signFieldStyle\":1},\"signFieldType\":0,"
                + "\"signDateConfig\":{\"showSignDate\":1,\"dateFormat\":\"yyyy年MM月dd日\",\"fontSize\":12,\"signDatePositionX\":376,\"signDatePositionY\":531}}"
                + "]}]}";

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
     * 控件 ID 已通过 GET /v3/doc-templates/{templateId} 确认（模板版本：2026-4-16 更新）
     * 查询方法: curl -s 'https://api.caigon.cn/h5/esign/template-detail'
     *
     * ── 填充控件（18个，由代码传值）──────────────────────────────────────
     *   单行文本3  (ebfedbda...) → 地址+房间号
     *   单行文本4  (37fdb412...) → 租金大写（中文大写金额）
     *   单行文本9  (23c33b47...) → 合同编号
     *   单行文本10 (fe46bc6a...) → 合同编号
     *   数字1      (d2a93cfe...) → 房租单价（元/平方米/月）
     *   数字2      (ef7ed4f3...) → 每月租金
     *   数字3      (7af62cd8...) → 房间平米数（面积）
     *   身份证号1  (ba2d50d3...) → 身份证号
     *   手机号1    (520eaa1e...) → 手机号
     *   数字4~6    → 合同起始日期 年/月/日
     *   数字7~9    → 合同终止日期 年/月/日
     *   数字10~12  → 签约日期 年/月/日（当前日期）
     *   数字13~57  → 设施数量字段（电视、空调、洗衣机...晾衣架）
     *   单行文本11 → 项目地址
     *
     * ── 默认值控件（2个，不传参）────────────────────────────────────────
     *   单行文本5 (43f58df6..., default:栗毅) → 甲方代表人
     *   单行文本7 (a09daefd..., default:\)    → 保留
     *
     * ── 签章控件（4个，由签署流自动处理）─────────────────────────────────
     * ── 签署日期控件（2个，签署流showSignDate自动填充）────────────────────
     */
    private String buildTemplateComponents(HzContract contract, HzUser user) {
        // ── 租户信息（优先从合同表取，回退到用户表）────────────────────
        String idCard     = contract.getTenantIdCard()  != null ? contract.getTenantIdCard()
                          : (user.getIdCard()    != null ? user.getIdCard()  : "");
        String phone      = contract.getTenantPhone()   != null ? contract.getTenantPhone()
                          : (user.getPhone()     != null ? user.getPhone()   : "");

        // ── 合同编号 ──────────────────────────────────────────────────────
        String contractNo   = contract.getContractNo()   != null ? contract.getContractNo()  : "";

        // ── 房源信息（查询房源表获取面积、房间号）────────────────────────
        HzHouse house = null;
        if (contract.getHouseId() != null) {
            house = houseMapper.selectById(contract.getHouseId());
        }

        // 地址+房间号：合同地址已包含完整地址（项目地址+楼栋+单元+房间号），无需再拼接
        String houseAddress = contract.getHouseAddress() != null ? contract.getHouseAddress() : "";
        String houseNo = (house != null && house.getHouseNo() != null) ? house.getHouseNo() : "";
        // 避免重复拼接：如果houseAddress已经以houseNo结尾，则不再追加
        String addressWithRoom = (!houseNo.isEmpty() && houseAddress.endsWith(houseNo))
                ? houseAddress : houseAddress + houseNo;

        // 房间面积（平方米）
        String houseArea = (house != null && house.getArea() != null) ? house.getArea().toString() : "0";

        // ── 金额 ────────────────────────────────────────────────────────
        BigDecimal rentPriceNum = contract.getRentPrice() != null ? contract.getRentPrice() : BigDecimal.ZERO;
        String monthlyRent = rentPriceNum.toString();  // 每月租金

        // 房租单价 = 月租金 / 面积（元/平方米/月）
        String unitPrice = "0";
        if (house != null && house.getArea() != null && house.getArea().compareTo(BigDecimal.ZERO) > 0) {
            unitPrice = rentPriceNum.divide(house.getArea(), 2, BigDecimal.ROUND_HALF_UP).toString();
        }

        // 租金大写（中文大写金额）
        String rentPriceChinese = convertToChineseAmount(rentPriceNum);

        // ── 合同日期拆分 ────────────────────────────────────────────────
        String startDate  = contract.getStartDate() != null ? contract.getStartDate() : "";
        String endDate    = contract.getEndDate()   != null ? contract.getEndDate()   : "";

        String startYear = "", startMonth = "", startDay = "";
        String endYear = "", endMonth = "", endDay = "";
        try {
            java.time.format.DateTimeFormatter isoFmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
            if (!startDate.isEmpty()) {
                java.time.LocalDate sd = java.time.LocalDate.parse(startDate, isoFmt);
                startYear  = String.valueOf(sd.getYear());
                startMonth = String.valueOf(sd.getMonthValue());
                startDay   = String.valueOf(sd.getDayOfMonth());
            }
            if (!endDate.isEmpty()) {
                java.time.LocalDate ed = java.time.LocalDate.parse(endDate, isoFmt);
                endYear  = String.valueOf(ed.getYear());
                endMonth = String.valueOf(ed.getMonthValue());
                endDay   = String.valueOf(ed.getDayOfMonth());
            }
        } catch (Exception e) {
            log.warn("合同日期解析失败，startDate={}, endDate={}", startDate, endDate);
        }

        // 签约日期：使用当前日期
        java.time.LocalDate signDate = java.time.LocalDate.now();
        String signYear  = String.valueOf(signDate.getYear());
        String signMonth = String.valueOf(signDate.getMonthValue());
        String signDay   = String.valueOf(signDate.getDayOfMonth());

        // ── 项目地址 ──────────────────────────────────────────────────
        String projectAddress = "";
        if (contract.getProjectId() != null) {
            HzProject project = projectMapper.selectById(contract.getProjectId());
            if (project != null && project.getAddress() != null) {
                projectAddress = project.getAddress();
            }
        }

        // ── 设施数据（按设施名称汇总数量）──────────────────────────────
        Map<String, Integer> facilityQtyMap = new HashMap<>();
        if (contract.getHouseId() != null) {
            List<HzHouseFacility> facilities = houseFacilityMapper.selectList(
                    new LambdaQueryWrapper<HzHouseFacility>()
                            .eq(HzHouseFacility::getHouseId, contract.getHouseId())
                            .eq(HzHouseFacility::getDelFlag, "0"));
            for (HzHouseFacility f : facilities) {
                String name = f.getFacilityName();
                int qty = f.getQuantity() != null ? f.getQuantity() : 0;
                facilityQtyMap.merge(name, qty, Integer::sum);
            }
        }

        // ── 组装控件JSON ────────────────────────────────────────────────
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        // 单行文本3: 地址+房间号
        sb.append("  {\"componentId\": \"ebfedbda264e446390801f1ba6ee96eb\", \"componentValue\": \"").append(escapeJson(addressWithRoom)).append("\"},\n");
        // 单行文本4: 租金大写（中文大写金额）
        sb.append("  {\"componentId\": \"37fdb4123afb45139912f5dc938d5e3c\", \"componentValue\": \"").append(escapeJson(rentPriceChinese)).append("\"},\n");
        // 数字1: 房租单价（元/平方米/月）
        sb.append("  {\"componentId\": \"d2a93cfe598449c49fac10a5c8d58f08\", \"componentValue\": \"").append(unitPrice).append("\"},\n");
        // 数字2: 每月租金
        sb.append("  {\"componentId\": \"ef7ed4f368094d19a52d45369994e7e7\", \"componentValue\": \"").append(monthlyRent).append("\"},\n");
        // 数字3: 房间平米数（面积）
        sb.append("  {\"componentId\": \"7af62cd84df944dba1d828dba66ae394\", \"componentValue\": \"").append(houseArea).append("\"},\n");
        // 单行文本9: 合同编号
        sb.append("  {\"componentId\": \"23c33b4791934632b6cc8322d8b15fe3\", \"componentValue\": \"").append(escapeJson(contractNo)).append("\"},\n");
        // 单行文本10: 合同编号
        sb.append("  {\"componentId\": \"fe46bc6ad7c84533949d2c32e75c3182\", \"componentValue\": \"").append(escapeJson(contractNo)).append("\"},\n");
        // 身份证号1
        sb.append("  {\"componentId\": \"ba2d50d300394daba46764c3f7ca5aec\", \"componentValue\": \"").append(escapeJson(idCard)).append("\"},\n");
        // 手机号1
        sb.append("  {\"componentId\": \"520eaa1e2b634c0592937bd216a74cf5\", \"componentValue\": \"").append(escapeJson(phone)).append("\"},\n");
        // ── 合同起始日期拆分 ──
        sb.append("  {\"componentId\": \"0a17d6c252294058bef7a5959d0444e3\", \"componentValue\": \"").append(startYear).append("\"},\n");
        sb.append("  {\"componentId\": \"873b2dca5ffd407c896bc42266b590e2\", \"componentValue\": \"").append(startMonth).append("\"},\n");
        sb.append("  {\"componentId\": \"31e87f7e9f1e4ec6a6d9db1301d65f6b\", \"componentValue\": \"").append(startDay).append("\"},\n");
        // ── 合同终止日期拆分 ──
        sb.append("  {\"componentId\": \"2ba6a017f99b42f7ad3a7b90b5f7e021\", \"componentValue\": \"").append(endYear).append("\"},\n");
        sb.append("  {\"componentId\": \"75361beac8664e859fa090b9253b7ccf\", \"componentValue\": \"").append(endMonth).append("\"},\n");
        sb.append("  {\"componentId\": \"6f93df14d68e4b9e80f58973b6b948e9\", \"componentValue\": \"").append(endDay).append("\"},\n");
        // ── 签约日期拆分（当前日期）──
        sb.append("  {\"componentId\": \"682336d1a20744f888d18c38026cf1ba\", \"componentValue\": \"").append(signYear).append("\"},\n");
        sb.append("  {\"componentId\": \"a96cbcb2bb6c469dbce41f2896919423\", \"componentValue\": \"").append(signMonth).append("\"},\n");
        sb.append("  {\"componentId\": \"994019cb2cfb41a2ae832f8e106d5282\", \"componentValue\": \"").append(signDay).append("\"},\n");

        // ── 设施数量字段（数字13~57）──────────────────────────────────
        // 设施名称(DB) → componentId 映射（一对一直接映射）
        String[][] facilityMapping = {
            // DB设施名 → componentId (模板控件名)
            {"空调",         "1346c3069a2a434f920c7b0ed652ca9b"},   // 数字14
            {"洗衣机",       "e0fe08cd3faa49beb984fea7d67ef55f"},   // 数字15
            {"冰箱",         "55bea9110f2e4e35aa890351a47f9e8d"},   // 数字16
            {"热水器",       "af27c759b2774ec0add8e3721c9b58eb"},   // 数字17
            {"燃气灶",       "085c6aa3fe8b4d7cb57f22f8264ece1c"},   // 数字18
            {"抽烟机",       "d990367bea0f4d9b9d5bbab7016f5d3b"},   // 数字19 (模板:抽油烟机)
            {"客厅灯具",     "a24748a0897e458e9c4c60f7c196414e"},   // 数字20
            {"卧室灯具",     "74196081487f4adfa78bb01c29eb0dd3"},   // 数字21
            {"厨房灯具",     "f37dcc62d1cf47baaf52904c8aa4f344"},   // 数字22
            {"卫生间灯",     "d6554b0e934b45aaab3150ff676cb9e5"},   // 数字23
            {"阳台灯具",     "a9ea4805788344a9b9d6b3be2e74a92d"},   // 数字24
            {"卫生间淋浴",   "0b85d6ee68194b03b55d60d98b98514c"},   // 数字25
            {"镜面",         "17e4eca5f3b64f5fb3a3837c0ee778e7"},   // 数字26
            {"洗漱台",       "8efd42ac7d064572b404419e081bd4a1"},   // 数字27
            {"水池",         "7799774972024d93bbdca7659c53b59c"},   // 数字28
            {"软管",         "7089eec143744baa86d21eacb4f63a46"},   // 数字29
            {"马桶",         "3dd55ec8849c43a0b54be1d3931efd53"},   // 数字30
            {"厨房水龙头",   "96fadc84185e4a1d93fe8c9806a08368"},   // 数字31
            {"厨房洗菜池",   "ef259c5b62844c4da2956d20537844e4"},   // 数字32 (模板:洗菜池)
            {"电闸盒",       "0b53c3e723d9469c93ec342d4f139545"},   // 数字36
            {"瓷砖",         "7e5e2672996b4c988e146e1bb4e9a6f1"},   // 数字37
            {"房间防盗门",   "8fbd8eae96124314920024f917efba0d"},   // 数字38 (模板:入户门)
            {"套房内门",     "6e12233b06114fdcaecea860555b8e6c"},   // 数字40
            {"客厅窗帘",     "7dce049c63a34c49a032bca9d423f2f9"},   // 数字43
            {"卧室窗帘",     "28480bbc9c494c40bfdd91484977a752"},   // 数字44
            {"茶几",         "e461395d06d948cf95dc0d457ecc2479"},   // 数字45 (模板:客厅茶几)
            {"餐桌",         "cf8bfbf7579444959f8c02e026a26ffd"},   // 数字46
            {"椅子",         "eb5d547ee12d4c78adc8e1cf0abdaa54"},   // 数字47
            {"书桌",         "7242352de39c4bcbb79249f7e214af73"},   // 数字48 (模板:桌子)
            {"电视柜",       "3739cea4aa2d46fa9a2a040e042dbe79"},   // 数字49
            {"橱柜",         "d8313e95517c450587c5f0f654aa953a"},   // 数字50
            {"鞋柜",         "ed76e70778a64a7b90e3ff5edf3913b5"},   // 数字51
            {"衣柜",         "55295008996341bea97d72652f6b69da"},   // 数字52
            {"床头柜",       "b5168a2851cf49b69a2397dbea906773"},   // 数字53
            {"客厅沙发",     "ebb3a484b0f24858aa22a78addf7572d"},   // 数字54 (模板:沙发)
            {"床",           "c47c22ae46dd41bdbdad92d50008bd7f"},   // 数字55
            {"床垫",         "e27d7ac150c1477aa6c9e3ac03650652"},   // 数字56
            {"晾衣架",       "0a638a08ce044b128809553f91b3e3bb"},   // 数字57
        };

        // 一对一设施填充（无数据时留空）
        for (String[] mapping : facilityMapping) {
            int qty = facilityQtyMap.getOrDefault(mapping[0], 0);
            String qtyStr = qty > 0 ? String.valueOf(qty) : "";
            sb.append("  {\"componentId\": \"").append(mapping[1]).append("\", \"componentValue\": \"").append(qtyStr).append("\"},\n");
        }

        // 数字13: 电视（DB无此设施，留空）
        sb.append("  {\"componentId\": \"df9310d42f124edf986744f92edc78aa\", \"componentValue\": \"\"},\n");
        // 数字33: 墙面（汇总: 客厅墙面+卧室墙面+厨房墙面+卫生间墙面）
        int wallQty = facilityQtyMap.getOrDefault("客厅墙面", 0)
                    + facilityQtyMap.getOrDefault("卧室墙面", 0)
                    + facilityQtyMap.getOrDefault("厨房墙面", 0)
                    + facilityQtyMap.getOrDefault("卫生间墙面", 0);
        sb.append("  {\"componentId\": \"ab92c18d58614881aaf3bdec381920cd\", \"componentValue\": \"").append(wallQty > 0 ? wallQty : "").append("\"},\n");
        // 数字34: 地板（DB: 房间地板）
        int floorQty = facilityQtyMap.getOrDefault("房间地板", 0);
        sb.append("  {\"componentId\": \"3aaf69df97be45c6aca3ed0632b4dfba\", \"componentValue\": \"").append(floorQty > 0 ? floorQty : "").append("\"},\n");
        // 数字35: 地毯（DB无此设施，留空）
        sb.append("  {\"componentId\": \"fe9eaa91e1c64f30b8e503fc61fee38b\", \"componentValue\": \"\"},\n");
        // 数字39: 密码锁（DB无此设施，留空）
        sb.append("  {\"componentId\": \"bac3286c5f8046ed924291c2e4888ea1\", \"componentValue\": \"\"},\n");
        // 数字41: 厨房推拉门（DB无此设施，留空）
        sb.append("  {\"componentId\": \"cf3857e2240544819dad9c3501b7f139\", \"componentValue\": \"\"},\n");
        // 数字42: 窗户（汇总: 客厅窗户+卧室窗户）
        int windowQty = facilityQtyMap.getOrDefault("客厅窗户", 0)
                      + facilityQtyMap.getOrDefault("卧室窗户", 0);
        sb.append("  {\"componentId\": \"1c395eb1e2534fa0866976ce3d236a56\", \"componentValue\": \"").append(windowQty > 0 ? windowQty : "").append("\"},\n");

        // ── 单行文本11: 项目地址 ──
        sb.append("  {\"componentId\": \"72bf22dc56d24723a715a1f2546346c2\", \"componentValue\": \"").append(escapeJson(projectAddress)).append("\"}\n");

        sb.append("]");
        return sb.toString();
        // 单行文本5 (default:栗毅, 甲方代表人)、单行文本7 (default:\) — 保留模板默认值，不传
    }

    /**
     * 将数字金额转换为中文大写金额
     * 例如：1200 → "壹仟贰佰元整"，3500.50 → "叁仟伍佰元伍角"
     */
    private String convertToChineseAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            return "零元整";
        }

        final String[] cnNums = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
        final String[] cnIntRadice = {"", "拾", "佰", "仟"};
        final String[] cnIntUnits = {"", "万", "亿", "兆"};
        final String cnIntLast = "元";
        final String cnInteger = "整";
        final String[] cnDecUnits = {"角", "分"};

        // 分离整数和小数部分
        long integerPart = amount.abs().longValue();
        int decimalPart = amount.abs().subtract(new BigDecimal(integerPart))
                .multiply(new BigDecimal(100)).intValue();

        StringBuilder sb = new StringBuilder();

        // 整数部分
        if (integerPart > 0) {
            int zeroCount = 0;
            String intStr = String.valueOf(integerPart);
            int intLen = intStr.length();
            for (int i = 0; i < intLen; i++) {
                int digit = intStr.charAt(i) - '0';
                int pos = intLen - i - 1;     // 当前位的权重位置
                int quotient = pos / 4;        // 所在的万级单元
                int remainder = pos % 4;       // 万级内的位置

                if (digit == 0) {
                    zeroCount++;
                } else {
                    if (zeroCount > 0) {
                        sb.append(cnNums[0]);
                    }
                    zeroCount = 0;
                    sb.append(cnNums[digit]).append(cnIntRadice[remainder]);
                }
                // 每个万级单元的末尾加单位（万/亿）
                if (remainder == 0 && zeroCount < 4) {
                    sb.append(cnIntUnits[quotient]);
                }
            }
            sb.append(cnIntLast);
        }

        // 小数部分（角/分）
        if (decimalPart > 0) {
            int jiao = decimalPart / 10;
            int fen = decimalPart % 10;
            if (jiao > 0) {
                sb.append(cnNums[jiao]).append(cnDecUnits[0]);
            }
            if (fen > 0) {
                sb.append(cnNums[fen]).append(cnDecUnits[1]);
            }
        } else {
            sb.append(cnInteger);
        }

        return sb.toString();
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
                .set(HzContract::getContractStatus, "2")
                .set(HzContract::getSignTime, new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())));

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

        // 发送合同签署成功消息
        try {
            messageService.sendMessage(contract.getTenantId(), "contract", "合同签署成功",
                    "您的合同 " + contract.getContractNo() + " 已签署成功，请及时缴纳押金");
        } catch (Exception e) {
            log.warn("发送合同签署成功消息失败，不影响主流程: {}", e.getMessage());
        }

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

        // 1. 更新合同状态为已签署，同时记录签署时间
        contractMapper.update(null, new LambdaUpdateWrapper<HzContract>()
                .eq(HzContract::getContractId, contract.getContractId())
                .set(HzContract::getContractStatus, "2")
                .set(HzContract::getSignTime, new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()))); // 2=已签署

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

        // 6. 发送合同签署成功消息
        try {
            messageService.sendMessage(contract.getTenantId(), "contract", "合同签署成功",
                    "您的合同 " + contract.getContractNo() + " 已签署成功，请及时缴纳押金");
        } catch (Exception e) {
            log.warn("发送合同签署成功消息失败，不影响主流程: {}", e.getMessage());
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
