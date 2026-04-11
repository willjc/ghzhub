package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.vo.BatchPreferenceVo;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.EsignService;
import com.ruoyi.system.service.IHzCheckInService;
import com.ruoyi.system.service.IHzContractService;
import com.ruoyi.system.service.IHzDocumentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * H5用户端 - 合同API
 */
@RestController
@RequestMapping("/h5/app/contract")
public class HzContractAppController extends BaseController {

    @Autowired
    private HzHouseMapper houseMapper;

    @Autowired
    private HzProjectMapper projectMapper;

    @Autowired
    private HzBuildingMapper buildingMapper;

    @Autowired
    private HzUnitMapper unitMapper;

    @Autowired
    private HzTenantMapper tenantMapper;

    @Autowired
    private HzContractTemplateMapper templateMapper;

    @Autowired
    private HzBillMapper billMapper;

    @Autowired
    private HzContractMapper contractMapper;

    @Autowired
    private IHzCheckInService checkInService;

    @Autowired
    private IHzContractService contractService;

    @Autowired
    private HzUserMapper hzUserMapper;

    @Autowired
    private HzBatchHouseMapper batchHouseMapper;

    @Autowired
    private IHzDocumentService documentService;

    @Autowired
    private EsignService esignService;

    /**
     * 根据用户ID获取合同列表
     * @param userId 用户ID
     */
    @GetMapping("/user/{userId}")
    public AjaxResult getContractsByUserId(@PathVariable Long userId) {
        // 查询该用户的所有合同（关联项目、楼栋、单元信息）
        List<Map<String, Object>> contracts = contractMapper.selectContractVOByUserId(userId);

        // 补充押金支付状态和资料提交状态
        for (Map<String, Object> contract : contracts) {
            Object contractIdObj = contract.get("contract_id");
            if (contractIdObj != null) {
                Long contractId = Long.parseLong(contractIdObj.toString());
                // 查询押金账单状态
                HzBill depositBill = billMapper.selectOne(new LambdaQueryWrapper<HzBill>()
                        .eq(HzBill::getContractId, contractId)
                        .eq(HzBill::getBillType, "1")
                        .last("LIMIT 1"));
                contract.put("deposit_paid", depositBill != null && "1".equals(depositBill.getBillStatus()) ? "1" : "0");

                // 查询第一期租金是否已缴
                HzBill firstRent = billMapper.selectOne(new LambdaQueryWrapper<HzBill>()
                        .eq(HzBill::getContractId, contractId)
                        .eq(HzBill::getBillType, "2")
                        .eq(HzBill::getDelFlag, "0")
                        .orderByAsc(HzBill::getBillDate)
                        .last("LIMIT 1"));
                contract.put("first_rent_paid", firstRent != null && "1".equals(firstRent.getBillStatus()) ? "1" : "0");

                // 签约时间用 update_time 近似（合同状态变更为已签署时更新）
                contract.put("signed_date", contract.get("update_time"));
            }
            // 动态查询资料审核状态：0=未提交, 1=审核中, 2=已通过
            Object tenantIdObj = contract.get("tenant_id");
            String materialStatus = "0";
            if (tenantIdObj != null) {
                Long tId = Long.parseLong(tenantIdObj.toString());
                List<com.ruoyi.system.domain.HzDocument> docs = documentService.selectDocumentListByTenantId(tId);
                if (docs != null && !docs.isEmpty()) {
                    boolean allApproved = docs.stream().allMatch(d -> "1".equals(d.getAuditStatus()));
                    boolean anyPending  = docs.stream().anyMatch(d -> "0".equals(d.getAuditStatus()));
                    materialStatus = allApproved ? "2" : (anyPending ? "1" : "0");
                }
            }
            contract.put("material_status", materialStatus);
        }
        return success(contracts);
    }

    /**
     * 获取合同详情
     */
    @GetMapping("/detail/{contractId}")
    public AjaxResult getContractDetail(@PathVariable Long contractId) {
        HzContract contract = contractMapper.selectById(contractId);
        if (contract == null) return error("合同不存在");

        Map<String, Object> data = new HashMap<>();
        data.put("contractId", contract.getContractId());
        data.put("contractNo", contract.getContractNo());
        data.put("contractStatus", contract.getContractStatus());
        data.put("projectId", contract.getProjectId());
        data.put("houseId", contract.getHouseId());
        data.put("houseCode", contract.getHouseCode());
        data.put("houseAddress", contract.getHouseAddress());
        data.put("rentPrice", contract.getRentPrice());
        data.put("deposit", contract.getDeposit());
        data.put("startDate", contract.getStartDate());
        data.put("endDate", contract.getEndDate());
        data.put("rentMonths", contract.getRentMonths());
        data.put("signedFileUrl", contract.getContractContent());
        data.put("esignFlowId", contract.getEsignFlowId());

        // 押金账单
        HzBill depositBill = billMapper.selectOne(new LambdaQueryWrapper<HzBill>()
                .eq(HzBill::getContractId, contractId)
                .eq(HzBill::getBillType, "1")
                .last("LIMIT 1"));
        data.put("depositPaid", depositBill != null && "1".equals(depositBill.getBillStatus()));
        if (depositBill != null) {
            data.put("depositBillId", depositBill.getBillId());
            data.put("depositAmount", depositBill.getUnpaidAmount() != null ? depositBill.getUnpaidAmount() : depositBill.getBillAmount());
        }
        // 动态查询资料审核状态：0=未提交, 1=审核中, 2=已通过
        List<com.ruoyi.system.domain.HzDocument> docs2 = documentService.selectDocumentListByTenantId(contract.getTenantId());
        String matStatus2 = "0";
        if (docs2 != null && !docs2.isEmpty()) {
            boolean allApproved = docs2.stream().allMatch(d -> "1".equals(d.getAuditStatus()));
            boolean anyPending  = docs2.stream().anyMatch(d -> "0".equals(d.getAuditStatus()));
            matStatus2 = allApproved ? "2" : (anyPending ? "1" : "0");
        }
        data.put("materialStatus", matStatus2);

        return success(data);
    }

    /**
     * 从请求头中的Token解析出hz_user的ID
     * Token格式：hz_token_{userId}_{timestamp}
     */
    private Long getHzUserIdFromToken() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("Authorization");

            // 调试日志：打印原始token
            logger.info("【调试】从请求头获取到的Authorization: {}", token);

            if (token == null || token.isEmpty()) {
                logger.warn("【调试】Authorization为空");
                return null;
            }

            // 移除 "Bearer " 前缀（如果有）
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
                logger.info("【调试】移除Bearer前缀后的token: {}", token);
            }

            // 解析token: hz_token_{userId}_{timestamp}
            if (token.startsWith("hz_token_")) {
                String[] parts = token.split("_");
                logger.info("【调试】token分割结果: {}, parts.length={}", (Object) parts, parts.length);

                if (parts.length >= 3) {
                    Long userId = Long.parseLong(parts[2]);
                    logger.info("【调试】解析成功，userId={}", userId);
                    return userId; // parts[0]=hz, parts[1]=token, parts[2]=userId
                }
            }

            logger.warn("【调试】token格式不匹配，不是hz_token_开头或分割后长度不足3");
            return null;
        } catch (Exception e) {
            logger.error("【调试】解析token失败", e);
            return null;
        }
    }

    /**
     * 生成合同内容
     */
    @PostMapping("/generate")
    public AjaxResult generateContract(@RequestBody Map<String, Object> params) {
        Long houseId = Long.parseLong(params.get("houseId").toString());
        Integer rentMonths = Integer.parseInt(params.get("rentMonths").toString());

        // 合同生效日期自动计算为当前日期 + 3天
        LocalDate startDate = LocalDate.now().plusDays(3);
        String startDateStr = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        // 1. 获取房源信息
        HzHouse house = houseMapper.selectById(houseId);
        if (house == null) {
            return error("房源不存在");
        }

        // 2. 获取项目信息
        HzProject project = projectMapper.selectById(house.getProjectId());
        if (project == null) {
            return error("项目不存在");
        }

        // 3. 获取楼栋和单元信息，拼接完整地址
        HzBuilding building = buildingMapper.selectById(house.getBuildingId());
        HzUnit unit = unitMapper.selectById(house.getUnitId());

        String houseAddress = project.getAddress();
        if (building != null) {
            houseAddress += building.getBuildingName();
        }
        if (unit != null) {
            houseAddress += unit.getUnitName();
        }
        houseAddress += house.getHouseNo();

        // 4. 判断合同类型（根据项目类型）
        String contractType = project.getProjectType(); // 1=人才公寓, 2=保租房, 3=市场租赁

        // 5. 获取合同模版
        HzContractTemplate template = templateMapper.selectTemplateByType(contractType);
        if (template == null) {
            return error("未找到对应的合同模版");
        }

        // 6. 获取租户信息 (TODO: 从登录态获取，这里暂时写死)
        HzTenant tenant = new HzTenant();
        tenant.setTenantName("张三");
        tenant.setIdCard("410123199001011234");
        tenant.setPhone("13800138000");

        // 7. 计算结束日期
        LocalDate end = startDate.plusMonths(rentMonths);
        String endDate = end.format(DateTimeFormatter.ISO_LOCAL_DATE);

        // 8. 替换合同模版变量
        // 押金优先使用房源自身的押金，若房源未配置则回退到合同模版的押金
        BigDecimal depositAmount = (house.getDeposit() != null && house.getDeposit().compareTo(BigDecimal.ZERO) > 0)
                ? house.getDeposit()
                : (template.getDepositAmount() != null ? template.getDepositAmount() : BigDecimal.ZERO);

        String contractContent = template.getTemplateContent();
        contractContent = contractContent.replace("${tenantName}", tenant.getTenantName())
                .replace("${tenantIdCard}", tenant.getIdCard())
                .replace("${tenantPhone}", tenant.getPhone())
                .replace("${houseAddress}", houseAddress)
                .replace("${rentPrice}", house.getRentPrice().toString())
                .replace("${rentPriceUpper}", convertToUpperCase(house.getRentPrice()))
                .replace("${deposit}", depositAmount.toString())
                .replace("${depositUpper}", convertToUpperCase(depositAmount))
                .replace("${startDate}", startDateStr)
                .replace("${endDate}", endDate);

        // 9. 组装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("contractContent", contractContent);
        result.put("templateId", template.getTemplateId());
        result.put("rentPrice", house.getRentPrice());
        result.put("deposit", depositAmount);
        result.put("totalRent", house.getRentPrice().multiply(new BigDecimal(rentMonths)));
        result.put("totalAmount", house.getRentPrice().multiply(new BigDecimal(rentMonths)).add(depositAmount));
        result.put("endDate", endDate);
        result.put("projectName", project.getProjectName());
        result.put("houseAddress", houseAddress);

        return success(result);
    }

    /**
     * 保存签署合同
     */
    @PostMapping("/sign")
    public AjaxResult signContract(@RequestBody Map<String, Object> params) {
        try {
            // 参数安全解析（兼容e签宝流程，部分参数可选）
            Long houseId = Long.parseLong(params.getOrDefault("houseId", "0").toString());
            Long projectId = Long.parseLong(params.getOrDefault("projectId", "0").toString());
            Long templateId = params.get("templateId") != null
                ? Long.parseLong(params.get("templateId").toString()) : 0L;
            String contractContent = params.getOrDefault("contractContent", "").toString();
            String endDate = params.getOrDefault("endDate", "").toString();
            Integer rentMonths = params.get("rentMonths") != null
                ? Integer.parseInt(params.get("rentMonths").toString()) : 12;

            // 优先从请求参数获取 userId（e签宝流程前端直接传参），否则从 token 解析
            Long userId = null;
            Object userIdParam = params.get("userId");
            if (userIdParam != null) {
                try { userId = Long.parseLong(userIdParam.toString()); } catch (Exception ignored) {}
            }
            if (userId == null) {
                userId = getHzUserIdFromToken();
            }
            if (userId == null) {
                return error("获取用户信息失败，请重新登录");
            }

            // 合同生效日期自动计算为当前日期 + 3天
            LocalDate startDateLocal = LocalDate.now().plusDays(3);
            String startDate = startDateLocal.format(DateTimeFormatter.ISO_LOCAL_DATE);

            // 1. 处理签名图片（e签宝模式下不再需要手写签名，签名由e签宝完成）
            String signatureUrl = "";
            String tenantSignatureBase64 = params.get("tenantSignature") != null
                ? params.get("tenantSignature").toString() : "";
            if (!tenantSignatureBase64.isEmpty() && tenantSignatureBase64.startsWith("data:image")) {
                try {
                    String base64Data = tenantSignatureBase64.substring(tenantSignatureBase64.indexOf(",") + 1);
                    byte[] imageBytes = Base64.getDecoder().decode(base64Data);
                    String fileName = DateUtils.datePath() + "/" + com.ruoyi.common.utils.uuid.IdUtils.fastSimpleUUID() + ".png";
                    java.io.File file = FileUploadUtils.getAbsoluteFile(RuoYiConfig.getUploadPath(), fileName);
                    java.nio.file.Files.write(file.toPath(), imageBytes);
                    signatureUrl = FileUploadUtils.getPathFileName(RuoYiConfig.getUploadPath(), fileName);
                } catch (Exception e) {
                    logger.error("上传签名图片失败", e);
                    return error("上传签名失败");
                }
            }

            // 2. 将签名图片插入到合同内容中（仅在旧模式下有手写签名时执行）
            String finalContract = contractContent;
            if (!signatureUrl.isEmpty()) {
                finalContract = contractContent.replace("乙方（签字）：______________",
                    "乙方（签字）：<img src='" + signatureUrl + "' style='height:60px;vertical-align:middle;'/>");
            }

            // 3. 获取房源和项目信息
            HzHouse house = houseMapper.selectById(houseId);
            HzProject project = projectMapper.selectById(projectId);

            // 拼接完整地址
            HzBuilding building = buildingMapper.selectById(house.getBuildingId());
            HzUnit unit = unitMapper.selectById(house.getUnitId());

            String houseAddress = project.getAddress();
            if (building != null) {
                houseAddress += building.getBuildingName();
            }
            if (unit != null) {
                houseAddress += unit.getUnitName();
            }
            houseAddress += house.getHouseNo();

            // userId 已在方法开头从请求参数或token中获取

            HzUser hzUser = hzUserMapper.selectById(userId);
            if (hzUser == null) {
                return error("用户不存在");
            }

            // 构造租户信息（优先使用真实姓名，否则使用昵称）
            String tenantName = hzUser.getRealName() != null && !hzUser.getRealName().isEmpty()
                ? hzUser.getRealName() : hzUser.getNickname();
            String tenantIdCard = hzUser.getIdCard() != null ? hzUser.getIdCard() : "";
            String tenantPhone = hzUser.getPhone() != null ? hzUser.getPhone() : "";

            // 用于生成入驻记录的临时租户对象
            HzTenant tenant = new HzTenant();
            tenant.setTenantId(userId);
            tenant.setTenantName(tenantName);
            tenant.setIdCard(tenantIdCard);
            tenant.setPhone(tenantPhone);

            // 4. 生成合同编号
            String contractNo = "HT" + DateUtils.dateTimeNow(DateUtils.YYYYMMDDHHMMSS);

            // 5. 创建合同对象
            HzContract contract = new HzContract();
            contract.setContractNo(contractNo);
            contract.setContractType("1"); // 首次签约
            contract.setTemplateId(templateId);
            contract.setTenantId(tenant.getTenantId());
            contract.setTenantName(tenant.getTenantName());
            contract.setTenantIdCard(tenant.getIdCard());
            contract.setTenantPhone(tenant.getPhone());
            contract.setProjectId(projectId);
            contract.setHouseId(houseId);
            contract.setHouseCode(house.getHouseNo());
            contract.setHouseAddress(houseAddress);
            contract.setRentPrice(house.getRentPrice());

            // 5.1 获取批次优惠信息（如果该房源属于配租批次）
            try {
                BatchPreferenceVo batchPreference = batchHouseMapper.selectBatchPreferenceByHouseId(houseId);
                if (batchPreference != null && "1".equals(batchPreference.getPreferentialType())) {
                    // 有免租优惠
                    contract.setBatchId(batchPreference.getBatchId());
                    contract.setFreeRentPeriods(batchPreference.getFreeRentPeriods());
                    logger.info("房源 {} 属于配租批次 {}，享受免租 {} 期优惠", houseId, batchPreference.getBatchId(), batchPreference.getFreeRentPeriods());
                } else {
                    // 无优惠
                    contract.setFreeRentPeriods(0);
                }
            } catch (Exception ex) {
                logger.error("获取批次优惠信息失败，默认无优惠: {}", ex.getMessage());
                contract.setFreeRentPeriods(0);
            }

            // 获取合同模板，押金优先使用房源自身配置，若未配置则回退到模板默认值
            HzContractTemplate template = templateMapper.selectById(templateId);
            if (template == null) {
                return error("合同模板不存在");
            }
            BigDecimal houseDeposit = (house.getDeposit() != null && house.getDeposit().compareTo(BigDecimal.ZERO) > 0)
                    ? house.getDeposit()
                    : (template.getDepositAmount() != null ? template.getDepositAmount() : BigDecimal.ZERO);
            contract.setDeposit(houseDeposit);

            contract.setStartDate(startDate);  // 使用自动计算的生效日期（签字日期+3天）
            contract.setEndDate(endDate);
            contract.setRentMonths(rentMonths);

            // 转换支付周期：模板中是英文全称，合同表中是单字符代码
            String paymentCycleCode = convertPaymentCycleToCode(template.getPaymentCycle());
            contract.setPaymentCycle(paymentCycleCode);

            contract.setContractContent(finalContract);
            contract.setTenantSignature(signatureUrl.isEmpty() ? null : signatureUrl);
            contract.setContractStatus("0"); // 草稿(待e签宝签署)
            contract.setDelFlag("0");

            // 6. 保存合同
            int result = contractService.insertContract(contract);

            if (result > 0) {
                // 注意：账单和入驻记录已迁移到 e签宝签署回调中生成（EsignServiceImpl.handleSignCallback）

                Map<String, Object> data = new HashMap<>();
                data.put("contractId", contract.getContractId());
                data.put("contractNo", contractNo);
                return success(data);
            } else {
                return error("保存合同失败");
            }

        } catch (Exception e) {
            logger.error("签署合同失败", e);
            return error("签署合同失败：" + e.getMessage());
        }
    }

    /**
     * 续租合同
     */
    @PostMapping("/renew")
    public AjaxResult renewContract(@RequestBody Map<String, Object> params) {
        try {
            Long oldContractId = Long.parseLong(params.get("oldContractId").toString());
            Long houseId = Long.parseLong(params.get("houseId").toString());
            Long projectId = Long.parseLong(params.get("projectId").toString());
            Long templateId = Long.parseLong(params.get("templateId").toString());
            String contractContent = params.getOrDefault("contractContent", "").toString();
            String endDate = params.getOrDefault("endDate", "").toString();
            Integer rentMonths = params.get("rentMonths") != null
                ? Integer.parseInt(params.get("rentMonths").toString()) : 12;

            // 优先从请求参数获取 userId，兼容前端直接传参
            Long userId = null;
            Object userIdParam = params.get("userId");
            if (userIdParam != null) {
                try { userId = Long.parseLong(userIdParam.toString()); } catch (Exception ignored) {}
            }
            if (userId == null) {
                userId = getHzUserIdFromToken();
            }
            if (userId == null) {
                return error("获取用户信息失败，请重新登录");
            }

            // 合同生效日期自动计算为当前日期 + 3天
            LocalDate startDateLocal = LocalDate.now().plusDays(3);
            String startDate = startDateLocal.format(DateTimeFormatter.ISO_LOCAL_DATE);

            // 1. 处理签名图片（e签宝模式下签名由e签宝完成）
            String signatureUrl = "";
            String tenantSignatureBase64 = params.get("tenantSignature") != null
                ? params.get("tenantSignature").toString() : "";
            if (!tenantSignatureBase64.isEmpty() && tenantSignatureBase64.startsWith("data:image")) {
                try {
                    String base64Data = tenantSignatureBase64.substring(tenantSignatureBase64.indexOf(",") + 1);
                    byte[] imageBytes = Base64.getDecoder().decode(base64Data);
                    String fileName = DateUtils.datePath() + "/" + com.ruoyi.common.utils.uuid.IdUtils.fastSimpleUUID() + ".png";
                    java.io.File file = FileUploadUtils.getAbsoluteFile(RuoYiConfig.getUploadPath(), fileName);
                    java.nio.file.Files.write(file.toPath(), imageBytes);
                    signatureUrl = FileUploadUtils.getPathFileName(RuoYiConfig.getUploadPath(), fileName);
                } catch (Exception e) {
                    logger.error("上传签名图片失败", e);
                    return error("上传签名失败");
                }
            }

            // 2. 将签名图片插入到合同内容中
            String finalContract = contractContent.replace("乙方（签字）：______________",
                "乙方（签字）：<img src='" + signatureUrl + "' style='height:60px;vertical-align:middle;'/>");

            // 3. 获取房源和项目信息
            HzHouse house = houseMapper.selectById(houseId);
            HzProject project = projectMapper.selectById(projectId);

            // 拼接完整地址
            HzBuilding building = buildingMapper.selectById(house.getBuildingId());
            HzUnit unit = unitMapper.selectById(house.getUnitId());

            String houseAddress = project.getAddress();
            if (building != null) {
                houseAddress += building.getBuildingName();
            }
            if (unit != null) {
                houseAddress += unit.getUnitName();
            }
            houseAddress += house.getHouseNo();

            // userId 已在方法开头从请求参数或token中获取

            HzUser hzUser = hzUserMapper.selectById(userId);
            if (hzUser == null) {
                return error("用户不存在");
            }

            // 构造租户信息（优先使用真实姓名，否则使用昵称）
            String tenantName = hzUser.getRealName() != null && !hzUser.getRealName().isEmpty()
                ? hzUser.getRealName() : hzUser.getNickname();
            String tenantIdCard = hzUser.getIdCard() != null ? hzUser.getIdCard() : "";
            String tenantPhone = hzUser.getPhone() != null ? hzUser.getPhone() : "";

            // 用于生成入驻记录的临时租户对象
            HzTenant tenant = new HzTenant();
            tenant.setTenantId(userId);
            tenant.setTenantName(tenantName);
            tenant.setIdCard(tenantIdCard);
            tenant.setPhone(tenantPhone);

            // 4. 生成合同编号
            String contractNo = "HT" + DateUtils.dateTimeNow(DateUtils.YYYYMMDDHHMMSS);

            // 5. 创建续租合同对象
            HzContract contract = new HzContract();
            contract.setContractNo(contractNo);
            contract.setContractType("2"); // 续租
            contract.setTemplateId(templateId);
            contract.setTenantId(tenant.getTenantId());
            contract.setTenantName(tenant.getTenantName());
            contract.setTenantIdCard(tenant.getIdCard());
            contract.setTenantPhone(tenant.getPhone());
            contract.setProjectId(projectId);
            contract.setHouseId(houseId);
            contract.setHouseCode(house.getHouseNo());
            contract.setHouseAddress(houseAddress);
            contract.setRentPrice(house.getRentPrice());

            // 获取合同模板，押金优先使用房源自身配置，若未配置则回退到模板默认值
            HzContractTemplate template = templateMapper.selectById(templateId);
            if (template == null) {
                return error("合同模板不存在");
            }
            BigDecimal renewDeposit = (house.getDeposit() != null && house.getDeposit().compareTo(BigDecimal.ZERO) > 0)
                    ? house.getDeposit()
                    : (template.getDepositAmount() != null ? template.getDepositAmount() : BigDecimal.ZERO);
            contract.setDeposit(renewDeposit);

            contract.setStartDate(startDate);  // 使用自动计算的生效日期（签字日期+3天）
            contract.setEndDate(endDate);
            contract.setRentMonths(rentMonths);

            // 转换支付周期
            String paymentCycleCode = convertPaymentCycleToCode(template.getPaymentCycle());
            contract.setPaymentCycle(paymentCycleCode);

            contract.setContractContent(finalContract);
            contract.setTenantSignature(signatureUrl);
            contract.setSignTime(DateUtils.getTime());
            contract.setContractStatus("0"); // 草稿(待审核)
            contract.setDelFlag("0");

            // 续租不享受免租优惠
            contract.setFreeRentPeriods(0);

            // 6. 保存合同
            int result = contractService.insertContract(contract);

            if (result > 0) {
                // 7. 标记原合同已续租
                HzContract oldContract = contractService.selectContractById(oldContractId);
                if (oldContract != null) {
                    oldContract.setIsRenewed("1");
                    oldContract.setRenewedContractId(contract.getContractId());
                    contractService.updateContract(oldContract);
                }

                // 8. 只生成租金账单,不生成押金账单
                try {
                    generateRentBillsOnly(contract);
                } catch (Exception billEx) {
                    logger.error("生成租金账单失败", billEx);
                }

                // 9. 生成入驻记录
                try {
                    generateCheckInRecord(contract, house, project, tenant);
                } catch (Exception checkInEx) {
                    logger.error("生成入驻记录失败", checkInEx);
                }

                Map<String, Object> data = new HashMap<>();
                data.put("contractId", contract.getContractId());
                data.put("contractNo", contractNo);
                return success(data);
            } else {
                return error("保存续租合同失败");
            }

        } catch (Exception e) {
            logger.error("续租合同失败", e);
            return error("续租合同失败：" + e.getMessage());
        }
    }

    /**
     * 转换支付周期：英文全称转为单字符代码
     * monthly -> 1 (月付)
     * quarterly -> 3 (季付)
     * half_yearly -> 6 (半年付)
     * yearly -> 12 (年付)
     */
    private String convertPaymentCycleToCode(String paymentCycle) {
        if (paymentCycle == null) {
            return "1"; // 默认月付
        }

        switch (paymentCycle.toLowerCase()) {
            case "monthly":
                return "1";
            case "quarterly":
                return "3";
            case "half_yearly":
                return "6";
            case "yearly":
                return "12";
            default:
                return "1"; // 默认月付
        }
    }

    /**
     * 将数字金额转换为大写中文
     */
    private String convertToUpperCase(BigDecimal amount) {
        if (amount == null) {
            return "零元整";
        }

        String[] units = {"", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿"};
        String[] nums = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};

        // 将金额转换为分（整数）
        long fen = amount.multiply(new BigDecimal(100)).longValue();
        long yuan = fen / 100;
        long jiao = (fen % 100) / 10;
        long fenUnit = fen % 10;

        if (yuan == 0 && jiao == 0 && fenUnit == 0) {
            return "零元整";
        }

        StringBuilder result = new StringBuilder();

        // 处理元
        if (yuan > 0) {
            String yuanStr = String.valueOf(yuan);
            int length = yuanStr.length();
            boolean zeroFlag = false;

            for (int i = 0; i < length; i++) {
                int num = yuanStr.charAt(i) - '0';
                int pos = length - i - 1;

                if (num == 0) {
                    // 避免连续的零
                    if (!zeroFlag && pos > 0 && pos != 4 && pos != 8) {
                        zeroFlag = true;
                    }
                    // 万位和亿位的零要保留单位
                    if (pos == 4 || pos == 8) {
                        result.append(units[pos]);
                        zeroFlag = false;
                    }
                } else {
                    if (zeroFlag) {
                        result.append(nums[0]);
                        zeroFlag = false;
                    }
                    result.append(nums[num]).append(units[pos]);
                }
            }
            result.append("元");
        } else {
            result.append("零元");
        }

        // 处理角分
        if (jiao == 0 && fenUnit == 0) {
            result.append("整");
        } else {
            if (jiao > 0) {
                result.append(nums[(int) jiao]).append("角");
            }
            if (fenUnit > 0) {
                if (jiao == 0) {
                    result.append("零");
                }
                result.append(nums[(int) fenUnit]).append("分");
            }
        }

        return result.toString();
    }

    /**
     * 生成账单（押金 + 租金）
     */
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
        depositBill.setBillPeriod(contract.getStartDate());  // 账期为合同开始日期
        depositBill.setBillAmount(contract.getDeposit());
        depositBill.setPaidAmount(BigDecimal.ZERO);
        depositBill.setUnpaidAmount(contract.getDeposit());
        depositBill.setBillDate(contract.getStartDate());
        depositBill.setDueDate(contract.getStartDate());  // 押金到期日=开始日期（立即缴纳）
        depositBill.setLateFee(BigDecimal.ZERO);
        depositBill.setBillStatus("0");  // 0=待支付
        depositBill.setDelFlag("0");

        billMapper.insert(depositBill);

        // 2. 生成租金账单
        int paymentCycle = Integer.parseInt(contract.getPaymentCycle());  // 支付周期（月数）
        int rentMonths = contract.getRentMonths();  // 总租期（月）
        BigDecimal monthlyRent = contract.getRentPrice();  // 月租金

        // 获取免租期数
        int freeRentPeriods = contract.getFreeRentPeriods() != null ? contract.getFreeRentPeriods() : 0;

        // 计算需要生成几条账单
        int billCount = rentMonths / paymentCycle;
        if (rentMonths % paymentCycle != 0) {
            billCount++;  // 如果有余数，需要多一条账单
        }

        // 解析开始日期
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

            // 计算当前账单的账期和到期日期
            LocalDate billDueDate = startDate.plusMonths(i * paymentCycle);
            String dueDateStr = billDueDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
            String billPeriodStr = billDueDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));

            rentBill.setBillPeriod(billPeriodStr);
            rentBill.setDueDate(dueDateStr);
            rentBill.setBillDate(dueDateStr);

            // 计算当前账单的月数和金额
            int monthsForThisBill = Math.min(paymentCycle, rentMonths - i * paymentCycle);
            BigDecimal billAmount = monthlyRent.multiply(new BigDecimal(monthsForThisBill));

            // 【免租优惠】如果当前期数在免租范围内，金额设为0
            if (i < freeRentPeriods) {
                billAmount = BigDecimal.ZERO;
            }

            rentBill.setBillAmount(billAmount);
            rentBill.setLateFee(BigDecimal.ZERO);
            rentBill.setDelFlag("0");

            // 【免租优惠】0元账单直接设为已支付状态
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

        logger.info("合同 {} 生成账单成功：押金1条，租金{}条，免租{}期", contract.getContractNo(), billCount, freeRentPeriods);
    }

    /**
     * 续租时只生成租金账单(不生成押金账单)
     * 注意：续租不享受免租优惠
     */
    private void generateRentBillsOnly(HzContract contract) {
        // 生成租金账单
        int paymentCycle = Integer.parseInt(contract.getPaymentCycle());  // 支付周期（月数）
        int rentMonths = contract.getRentMonths();  // 总租期（月）
        BigDecimal monthlyRent = contract.getRentPrice();  // 月租金

        // 续租不享受免租优惠
        int freeRentPeriods = 0;

        // 计算需要生成几条账单
        int billCount = rentMonths / paymentCycle;
        if (rentMonths % paymentCycle != 0) {
            billCount++;  // 如果有余数，需要多一条账单
        }

        // 解析开始日期
        LocalDate startDate = LocalDate.parse(contract.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE);

        for (int i = 0; i < billCount; i++) {
            HzBill rentBill = new HzBill();
            rentBill.setBillNo("XZ" + DateUtils.dateTimeNow(DateUtils.YYYYMMDDHHMMSS) + String.format("%02d", i + 1));
            rentBill.setContractId(contract.getContractId());
            rentBill.setTenantId(contract.getTenantId());
            rentBill.setTenantName(contract.getTenantName());
            rentBill.setHouseId(contract.getHouseId());
            rentBill.setHouseCode(contract.getHouseCode());
            rentBill.setBillType("2");  // 2=租金

            // 计算当前账单的账期和到期日期
            LocalDate billDueDate = startDate.plusMonths(i * paymentCycle);
            String dueDateStr = billDueDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
            String billPeriodStr = billDueDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));

            rentBill.setBillPeriod(billPeriodStr);
            rentBill.setDueDate(dueDateStr);
            rentBill.setBillDate(dueDateStr);

            // 计算当前账单的月数和金额
            int monthsForThisBill = Math.min(paymentCycle, rentMonths - i * paymentCycle);
            BigDecimal billAmount = monthlyRent.multiply(new BigDecimal(monthsForThisBill));

            rentBill.setBillAmount(billAmount);
            rentBill.setPaidAmount(BigDecimal.ZERO);
            rentBill.setUnpaidAmount(billAmount);
            rentBill.setLateFee(BigDecimal.ZERO);
            rentBill.setBillStatus("0");  // 0=待支付
            rentBill.setDelFlag("0");

            billMapper.insert(rentBill);
        }

        logger.info("续租合同 {} 生成租金账单成功：{}条（续租不享受免租优惠）", contract.getContractNo(), billCount);
    }

    /**
     * 生成入驻记录
     */
    private void generateCheckInRecord(HzContract contract, HzHouse house, HzProject project, HzTenant tenant) {
        // 拼接完整地址
        HzBuilding building = buildingMapper.selectById(house.getBuildingId());
        HzUnit unit = unitMapper.selectById(house.getUnitId());

        String fullAddress = project.getAddress();
        if (building != null) {
            fullAddress += building.getBuildingName();
        }
        if (unit != null) {
            fullAddress += unit.getUnitName();
        }
        fullAddress += house.getHouseNo();

        // 创建入驻记录
        HzCheckIn checkIn = new HzCheckIn();
        checkIn.setApplyId(0L);  // 没有申请ID，直接签合同入驻
        checkIn.setContractId(contract.getContractId());
        checkIn.setTenantId(contract.getTenantId());
        checkIn.setHouseId(contract.getHouseId());
        checkIn.setCheckinDate(contract.getStartDate());
        // 注意：checkinTime 是用户确认时间，不是申请时间，在这里不设置
        // 申请时间由 createTime 字段记录，在 Service 层自动设置

        // 初始化抄表读数为0
        checkIn.setMeterReadingElectric("0");
        checkIn.setMeterReadingWater("0");
        checkIn.setMeterReadingGas("0");

        checkIn.setKeyCount(1);  // 默认1把钥匙
        checkIn.setStatus("0");  // 0=待办理
        checkIn.setDelFlag("0");

        // 在remark中记录项目名、房间号、租期、租金、押金等信息
        String remark = String.format("项目：%s | 房间：%s | 租期：%s至%s（%d个月） | 月租金：%s元 | 押金：%s元",
                project.getProjectName(),
                fullAddress,
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getRentMonths(),
                contract.getRentPrice(),
                contract.getDeposit());
        checkIn.setRemark(remark);

        // 使用 Service 层插入，自动生成入住单号
        checkInService.insertCheckIn(checkIn);

        logger.info("合同 {} 生成入驻记录成功：recordId={}", contract.getContractNo(), checkIn.getRecordId());
    }

    /**
     * 获取合同 PDF 下载链接（实时向 e签宝 刷新，避免存库链接过期 403）
     */
    @GetMapping("/{contractId}/pdf-url")
    public AjaxResult getContractPdfUrl(@PathVariable Long contractId) {
        HzContract contract = contractMapper.selectById(contractId);
        if (contract == null) return error("合同不存在");

        String flowId = contract.getEsignFlowId();
        if (flowId == null || flowId.isEmpty()) {
            // 无 flowId：合同内容本身就是 URL（旧模式），直接返回
            String content = contract.getContractContent();
            if (content != null && content.startsWith("http")) {
                return AjaxResult.success("操作成功", (Object) content);
            }
            return error("合同尚未完成电子签署");
        }

        try {
            String freshUrl = esignService.getSignedPdfUrl(flowId);
            return AjaxResult.success("操作成功", (Object) freshUrl);
        } catch (Exception e) {
            logger.error("获取合同 PDF 链接失败，contractId={}, flowId={}", contractId, flowId, e);
            return error("获取 PDF 链接失败：" + e.getMessage());
        }
    }

    /**
     * 代理下载合同 PDF（小程序专用）
     * 避免小程序直接访问 e签宝 CDN 域名不在白名单的问题
     */
    @GetMapping("/{contractId}/download-pdf")
    public void downloadContractPdf(@PathVariable Long contractId, HttpServletResponse response) {
        HzContract contract = contractMapper.selectById(contractId);
        if (contract == null) {
            try { response.sendError(HttpServletResponse.SC_NOT_FOUND, "合同不存在"); } catch (Exception ignored) {}
            return;
        }

        // 获取实时 PDF URL
        String pdfUrl = null;
        String flowId = contract.getEsignFlowId();
        try {
            if (flowId != null && !flowId.isEmpty()) {
                pdfUrl = esignService.getSignedPdfUrl(flowId);
            }
        } catch (Exception e) {
            logger.error("获取e签宝PDF链接失败 contractId={}", contractId, e);
        }
        if (pdfUrl == null) {
            String content = contract.getContractContent();
            if (content != null && content.startsWith("http")) {
                pdfUrl = content;
            }
        }

        if (pdfUrl == null) {
            try { response.sendError(HttpServletResponse.SC_NOT_FOUND, "合同尚未完成电子签署"); } catch (Exception ignored) {}
            return;
        }

        // SSRF 防护：只允许 e签宝 CDN 域名
        HttpURLConnection conn = null;
        try {
            URL url = URI.create(pdfUrl).toURL();
            String host = url.getHost().toLowerCase();
            if (!host.endsWith("esign.cn") && !host.endsWith("qian.com") && !host.endsWith("esigncloud.com")) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "非法的文件来源");
                return;
            }

            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(30000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.connect();

            int status = conn.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                response.sendError(status, "获取合同文件失败");
                return;
            }

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=contract-" + contractId + ".pdf");

            String contentLength = conn.getHeaderField("Content-Length");
            if (contentLength != null) {
                response.setHeader("Content-Length", contentLength);
            }

            try (InputStream in = conn.getInputStream();
                 OutputStream out = response.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.flush();
            }
        } catch (Exception e) {
            logger.error("代理下载合同PDF失败 contractId={}", contractId, e);
            if (!response.isCommitted()) {
                try { response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "下载合同失败"); } catch (Exception ignored) {}
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
