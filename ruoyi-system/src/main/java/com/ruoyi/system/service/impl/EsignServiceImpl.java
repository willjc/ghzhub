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

    private final HzTenantMapper tenantMapper;
    private final HzContractMapper contractMapper;
    private final HzHouseOrderMapper orderMapper;
    private final HzBillMapper billMapper;
    private final HzHouseMapper houseMapper;
    private final HzProjectMapper projectMapper;
    private final HzBuildingMapper buildingMapper;
    private final HzUnitMapper unitMapper;
    private final IHzCheckInService checkInService;

    public EsignServiceImpl(HzTenantMapper tenantMapper, HzContractMapper contractMapper,
                            HzHouseOrderMapper orderMapper, HzBillMapper billMapper,
                            HzHouseMapper houseMapper, HzProjectMapper projectMapper,
                            HzBuildingMapper buildingMapper, HzUnitMapper unitMapper,
                            IHzCheckInService checkInService) {
        this.tenantMapper = tenantMapper;
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
    public String getPsnAuthUrl(Long tenantId, String mobile, String redirectUrl) {
        HzTenant tenant = tenantMapper.selectById(tenantId);
        if (tenant != null && tenant.getEsignPsnId() != null && !tenant.getEsignPsnId().isEmpty()) return null;
        String jsonParm = "{\"psnAuthConfig\":{\"psnAccount\":\"" + mobile + "\",\"psnAuthPageConfig\":{\"psnEditableFields\":[\"name\",\"IDCardNum\",\"mobile\"]}},"
                + "\"clientType\":\"ALL\",\"redirectConfig\":{\"redirectDelayTime\":\"3\",\"redirectUrl\":\"" + redirectUrl + "\"},"
                + "\"authorizeConfig\":{\"authorizedScopes\":[\"get_psn_identity_info\",\"psn_initiate_sign\",\"manage_psn_resource\"]},"
                + "\"repeatableAuth\":true}";
        try {
            EsignHttpResponse resp = callApi("POST", "/v3/psn-auth-url", jsonParm);
            return gson.fromJson(resp.getBody(), JsonObject.class).getAsJsonObject("data").get("authUrl").getAsString();
        } catch (Exception e) { throw new RuntimeException("获取 e签宝 认证链接失败", e); }
    }

    @Override
    public String queryAndSavePsnId(Long tenantId, String mobile) {
        try {
            EsignHttpResponse resp = callApi("GET", "/v3/persons/identity-info?psnAccount=" + mobile, null);
            JsonObject data = gson.fromJson(resp.getBody(), JsonObject.class).getAsJsonObject("data");
            if (!"2".equals(data.get("realnameStatus").getAsString())) return null;
            String psnId = data.get("psnId").getAsString();
            tenantMapper.update(null, new LambdaUpdateWrapper<HzTenant>().eq(HzTenant::getTenantId, tenantId).set(HzTenant::getEsignPsnId, psnId));
            log.info("e签宝个人认证完成，tenantId={}, psnId={}", tenantId, psnId);
            return psnId;
        } catch (Exception e) { throw new RuntimeException("查询 e签宝 认证状态失败", e); }
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

        HzTenant tenant = tenantMapper.selectById(contract.getTenantId());
        if (tenant == null) throw new RuntimeException("租户不存在");

        String componentsJson = buildTemplateComponents(contract, tenant);
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
    public String initSign(Long contractId, String psnId) throws Exception {
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

        // 4. 获取签署链接
        HzTenant tenant = tenantMapper.selectById(contract.getTenantId());
        String mobile = tenant != null ? tenant.getPhone() : "";
        return getSignUrl(signFlowId, mobile, redirectUrl);
    }

    /**
     * 构建模板填充数据 — 将合同+租户数据映射到 e签宝 模板控件
     * 控件 ID 来自模板详情（已通过 API 验证）
     */
    private String buildTemplateComponents(HzContract contract, HzTenant tenant) {
        String tenantName = tenant.getTenantName() != null ? tenant.getTenantName() : "";
        String idCard = tenant.getIdCard() != null ? tenant.getIdCard() : "";
        String phone = tenant.getPhone() != null ? tenant.getPhone() : "";
        String rentPrice = contract.getRentPrice() != null ? contract.getRentPrice().toString() : "0";
        String deposit = contract.getDeposit() != null ? contract.getDeposit().toString() : "0";

        return "[\n"
            + "  {\"componentId\": \"564553c91f36457b95542f661c7de7d2\", \"componentValue\": \"" + escapeJson(tenantName) + "\"},\n"
            + "  {\"componentId\": \"ba2d50d300394daba46764c3f7ca5aec\", \"componentValue\": \"" + escapeJson(idCard) + "\"},\n"
            + "  {\"componentId\": \"520eaa1e2b634c0592937bd216a74cf5\", \"componentValue\": \"" + escapeJson(phone) + "\"},\n"
            + "  {\"componentId\": \"d2a93cfe598449c49fac10a5c8d58f08\", \"componentValue\": \"" + rentPrice + "\"},\n"
            + "  {\"componentId\": \"ef7ed4f368094d19a52d45369994e7e7\", \"componentValue\": \"" + deposit + "\"}\n"
            + "]";
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
        String jsonParm = "{\"clientType\":\"ALL\",\"needLogin\":true,\"operator\":{\"psnAccount\":\"" + mobile + "\"},"
                + "\"redirectUrl\":\"" + redirectUrl + "\",\"urlType\":1}";
        EsignHttpResponse resp = callApi("POST", "/v3/sign-flow/" + signFlowId + "/sign-url", jsonParm);
        return gson.fromJson(resp.getBody(), JsonObject.class).getAsJsonObject("data").get("url").getAsString();
    }

    // ==================== 回调处理 ====================

    @Override
    @Transactional
    public void handleSignCallback(String timestamp, String requestQuery, String body, String signature) throws Exception {
        if (!EsignEncryption.callBackCheck(timestamp, requestQuery, body, appSecret, signature))
            throw new RuntimeException("e签宝回调验签失败");
        JsonObject nb = gson.fromJson(body, JsonObject.class);
        String action = nb.get("action").getAsString();
        String flowId = nb.get("flowId").getAsString();
        int signResult = nb.has("signResult") ? nb.get("signResult").getAsInt() : -1;
        log.info("e签宝回调 action={} flowId={} signResult={}", action, flowId, signResult);
        if (!"SIGN_FLOW_UPDATE".equals(action) || signResult != 2) return;

        HzContract contract = contractMapper.selectOne(new LambdaQueryWrapper<HzContract>().eq(HzContract::getEsignFlowId, flowId).last("LIMIT 1"));
        if (contract == null) { log.warn("e签宝回调未找到对应合同 flowId={}", flowId); return; }
        if ("2".equals(contract.getContractStatus())) { log.info("合同已处理，忽略重复回调 flowId={}", flowId); return; }

        // 1. 更新合同状态为已签署
        contractMapper.update(null, new LambdaUpdateWrapper<HzContract>()
                .eq(HzContract::getContractId, contract.getContractId())
                .set(HzContract::getContractStatus, "2")); // 2=已签署

        // 2. 生成账单（押金 + 租金）
        try {
            generateBills(contract);
            log.info("签署回调：账单生成成功，contractId={}", contract.getContractId());
        } catch (Exception e) {
            log.error("签署回调：生成账单失败，contractId={}", contract.getContractId(), e);
        }

        // 3. 生成入住记录
        try {
            generateCheckInRecord(contract);
            log.info("签署回调：入住记录生成成功，contractId={}", contract.getContractId());
        } catch (Exception e) {
            log.error("签署回调：生成入住记录失败，contractId={}", contract.getContractId(), e);
        }

        // 4. 推进预订单状态
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
        // 1. 生成押金账单
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
        Map<String, String> header = EsignHttpHelper.signAndBuildSignAndJsonHeader(appId, appSecret, jsonParm, method, apiAddr, false);
        return EsignHttpHelper.doCommHttp(host, apiAddr, EsignRequestType.valueOf(method.toUpperCase()), jsonParm, header, false);
    }
}
