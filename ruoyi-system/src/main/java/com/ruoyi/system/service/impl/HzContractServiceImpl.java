package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzCheckIn;
import com.ruoyi.system.domain.HzCheckoutApply;
import com.ruoyi.system.domain.HzCheckoutRecord;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.domain.HzHouse;
import com.ruoyi.system.domain.HzProject;
import com.ruoyi.system.mapper.HzCheckInMapper;
import com.ruoyi.system.mapper.HzCheckoutApplyMapper;
import com.ruoyi.system.mapper.HzCheckoutRecordMapper;
import com.ruoyi.system.mapper.HzContractMapper;
import com.ruoyi.system.mapper.HzHouseMapper;
import com.ruoyi.system.mapper.HzProjectMapper;
import com.ruoyi.system.service.IHzCheckoutService;
import com.ruoyi.system.service.IHzContractService;
import com.ruoyi.system.service.IHzRoleProjectService;
import com.ruoyi.system.service.IHzUserMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 合同Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzContractServiceImpl extends ServiceImpl<HzContractMapper, HzContract> implements IHzContractService {

    @Autowired
    private HzCheckoutApplyMapper checkoutApplyMapper;

    @Autowired
    private HzHouseMapper houseMapper;

    @Autowired
    private HzProjectMapper projectMapper;

    @Autowired
    private HzCheckInMapper checkInMapper;

    @Autowired
    private HzCheckoutRecordMapper checkoutRecordMapper;

    @Autowired
    private IHzUserMessageService messageService;

    @Autowired
    private IHzRoleProjectService roleProjectService;

    @Override
    public HzContract selectContractById(Long contractId) {
        // 先查询基本信息
        HzContract contract = this.getById(contractId);
        if (contract == null) {
            return null;
        }
        // 联表查询房源详细信息
        java.util.Map<String, Object> detailMap = baseMapper.selectContractDetailById(contractId);
        if (detailMap != null && !detailMap.isEmpty()) {
            // 填充房源详细信息
            contract.setProjectName((String) detailMap.get("project_name"));
            contract.setBuildingId((Long) detailMap.get("building_id"));
            contract.setBuildingName((String) detailMap.get("building_name"));
            contract.setUnitId((Long) detailMap.get("unit_id"));
            contract.setUnitName((String) detailMap.get("unit_name"));
            contract.setHouseNo((String) detailMap.get("house_no"));
            contract.setFloor((Integer) detailMap.get("floor"));
            contract.setHouseTypeName((String) detailMap.get("house_type_name"));
            contract.setArea((java.math.BigDecimal) detailMap.get("area"));
            contract.setOrientation((String) detailMap.get("orientation"));
            contract.setDecoration((String) detailMap.get("decoration"));
            contract.setFacilities((String) detailMap.get("facilities"));
        }
        return contract;
    }

    @Override
    public HzContract selectContractByContractNo(String contractNo) {
        LambdaQueryWrapper<HzContract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzContract::getContractNo, contractNo)
               .eq(HzContract::getDelFlag, "0")
               .last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    public List<HzContract> selectContractListByTenantId(Long tenantId) {
        LambdaQueryWrapper<HzContract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzContract::getTenantId, tenantId)
               .eq(HzContract::getDelFlag, "0")
               .orderByDesc(HzContract::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public HzContract selectValidContractByHouseId(Long houseId) {
        LambdaQueryWrapper<HzContract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzContract::getHouseId, houseId)
               .in(HzContract::getContractStatus, "2", "3") // 已签署或履行中
               .eq(HzContract::getDelFlag, "0")
               .orderByDesc(HzContract::getCreateTime)
               .last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    public List<HzContract> selectContractList(HzContract contract) {
        // 【调试日志】记录查询参数
        System.out.println("===== selectContractList 调试信息 =====");
        Long tenantId = (contract != null) ? contract.getTenantId() : null;
        Long houseId = (contract != null) ? contract.getHouseId() : null;
        Long projectId = (contract != null) ? contract.getProjectId() : null;
        String contractNo = (contract != null) ? contract.getContractNo() : null;
        String contractType = (contract != null) ? contract.getContractType() : null;
        String contractStatus = (contract != null) ? contract.getContractStatus() : null;
        System.out.println("contract.getTenantId() = " + tenantId);
        System.out.println("contract.getHouseId() = " + houseId);
        System.out.println("contract.getProjectId() = " + projectId);
        System.out.println("contract.getContractNo() = " + contractNo);
        System.out.println("contract.getContractType() = " + contractType);
        System.out.println("contract.getContractStatus() = " + contractStatus);
        System.out.println("contract.getParams() = " + (contract != null && contract.getParams() != null ? contract.getParams().keySet() : "params is null"));

        LambdaQueryWrapper<HzContract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(tenantId != null, HzContract::getTenantId, tenantId)
               .eq(houseId != null, HzContract::getHouseId, houseId)
               .eq(projectId != null, HzContract::getProjectId, projectId)
               .like(StringUtils.isNotEmpty(contractNo), HzContract::getContractNo, contractNo)
               .eq(StringUtils.isNotEmpty(contractType), HzContract::getContractType, contractType)
               .eq(StringUtils.isNotEmpty(contractStatus), HzContract::getContractStatus, contractStatus)
               .eq(HzContract::getDelFlag, "0")
               .orderByDesc(HzContract::getCreateTime);

        // 项目权限过滤
        List<Long> allowedProjectIds = roleProjectService.getCurrentUserProjectIds();
        if (allowedProjectIds != null && !allowedProjectIds.isEmpty()) {
            wrapper.in(HzContract::getProjectId, allowedProjectIds);
        } else if (allowedProjectIds != null && allowedProjectIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        // 签约时间范围（前端 params.beginSignTime / endSignTime）
        if (contract != null && contract.getParams() != null) {
            Object begin = contract.getParams().get("beginSignTime");
            Object end = contract.getParams().get("endSignTime");
            if (begin != null && StringUtils.isNotEmpty(begin.toString())) {
                wrapper.ge(HzContract::getSignTime, begin.toString() + " 00:00:00");
            }
            if (end != null && StringUtils.isNotEmpty(end.toString())) {
                wrapper.le(HzContract::getSignTime, end.toString() + " 23:59:59");
            }
        }

        // 检查是否有 dataScope 参数
        if (contract != null && contract.getParams() != null && contract.getParams().containsKey("dataScope")) {
            String dataScope = (String) contract.getParams().get("dataScope");
            System.out.println("发现 dataScope 参数: " + dataScope);
            if (StringUtils.isNotEmpty(dataScope)) {
                wrapper.apply(dataScope);
            }
        }

        List<HzContract> result = this.list(wrapper);
        System.out.println("查询结果数量: " + result.size());
        for (HzContract c : result) {
            System.out.println("  - contractId=" + c.getContractId() + ", contractNo=" + c.getContractNo() + ", tenantId=" + c.getTenantId());
        }
        System.out.println("===== selectContractList 调试结束 =====");

        // 回填导出/列表所需的虚拟字段：projectName + allocationType
        if (!result.isEmpty()) {
            // 批量查询项目名称
            Set<Long> projectIds = result.stream()
                .map(HzContract::getProjectId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
            Map<Long, String> projectNameMap = new HashMap<>();
            if (!projectIds.isEmpty()) {
                List<HzProject> projects = projectMapper.selectList(
                    new LambdaQueryWrapper<HzProject>()
                        .in(HzProject::getProjectId, projectIds)
                        .eq(HzProject::getDelFlag, "0")
                );
                for (HzProject p : projects) {
                    projectNameMap.put(p.getProjectId(), p.getProjectName());
                }
            }
            for (HzContract c : result) {
                c.setProjectName(projectNameMap.getOrDefault(c.getProjectId(), ""));
                c.setAllocationType(computeAllocationType(c.getBatchId(), c.getRemark()));
            }
        }
        return result;
    }

    @Override
    public IPage<HzContract> selectContractPage(HzContract contract, int pageNum, int pageSize) {
        Page<HzContract> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HzContract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(contract.getTenantId() != null, HzContract::getTenantId, contract.getTenantId())
               .eq(contract.getHouseId() != null, HzContract::getHouseId, contract.getHouseId())
               .eq(contract.getProjectId() != null, HzContract::getProjectId, contract.getProjectId())
               .like(StringUtils.isNotEmpty(contract.getContractNo()), HzContract::getContractNo, contract.getContractNo())
               .like(StringUtils.isNotEmpty(contract.getContractType()), HzContract::getContractType, contract.getContractType())
               .eq(StringUtils.isNotEmpty(contract.getContractStatus()), HzContract::getContractStatus, contract.getContractStatus())
               .like(StringUtils.isNotEmpty(contract.getTenantName()), HzContract::getTenantName, contract.getTenantName())
               .eq(HzContract::getDelFlag, "0")
               .orderByDesc(HzContract::getContractId);

        // 签约时间范围（前端 params.beginSignTime / endSignTime）
        if (contract.getParams() != null) {
            Object begin = contract.getParams().get("beginSignTime");
            Object end = contract.getParams().get("endSignTime");
            if (begin != null && StringUtils.isNotEmpty(begin.toString())) {
                wrapper.ge(HzContract::getSignTime, begin.toString() + " 00:00:00");
            }
            if (end != null && StringUtils.isNotEmpty(end.toString())) {
                wrapper.le(HzContract::getSignTime, end.toString() + " 23:59:59");
            }
        }

        // 配租方式筛选：batch_id 非空 或 remark 首段是"集中分配" -> 集中分配；否则常规分配
        String allocType = contract.getAllocationType();
        if (StringUtils.isNotEmpty(allocType)) {
            if ("集中分配".equals(allocType)) {
                wrapper.and(w -> w.isNotNull(HzContract::getBatchId)
                        .or().likeRight(HzContract::getRemark, "集中分配"));
            } else if ("常规分配".equals(allocType)) {
                wrapper.and(w -> w.isNull(HzContract::getBatchId))
                       .and(w -> w.isNull(HzContract::getRemark)
                               .or().notLikeRight(HzContract::getRemark, "集中分配"));
            }
        }

        IPage<HzContract> result = this.page(page, wrapper);
        // 回填 allocationType 虚拟字段
        if (result.getRecords() != null) {
            for (HzContract c : result.getRecords()) {
                c.setAllocationType(computeAllocationType(c.getBatchId(), c.getRemark()));
            }
        }
        return result;
    }

    /** 根据 batch_id 和 remark 首段推断配租方式 */
    private String computeAllocationType(Long batchId, String remark) {
        if (batchId != null) {
            return "集中分配";
        }
        if (remark != null && remark.startsWith("集中分配")) {
            return "集中分配";
        }
        return "常规分配";
    }

    @Override
    public int insertContract(HzContract contract) {
        contract.setDelFlag("0");
        contract.setContractStatus("0"); // 默认草稿状态
        if (StringUtils.isEmpty(contract.getContractNo())) {
            contract.setContractNo(generateContractNo());
        }
        return this.save(contract) ? 1 : 0;
    }

    @Override
    public int updateContract(HzContract contract) {
        return this.updateById(contract) ? 1 : 0;
    }

    @Override
    public int deleteContractById(Long contractId) {
        LambdaUpdateWrapper<HzContract> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(HzContract::getDelFlag, "2")
               .eq(HzContract::getContractId, contractId)
               .eq(HzContract::getDelFlag, "0");
        return this.update(wrapper) ? 1 : 0;
    }

    @Override
    public String generateContractNo() {
        return "HT" + DateUtils.dateTimeNow() + String.format("%04d", (int)(Math.random() * 10000));
    }

    @Override
    public List<HzContract> selectCheckoutableContractList(Long tenantId) {
        // 获取租户的所有履行中合同（状态为'3'='履行中'）
        LambdaQueryWrapper<HzContract> contractWrapper = new LambdaQueryWrapper<>();
        contractWrapper.eq(HzContract::getTenantId, tenantId)
                       .eq(HzContract::getContractStatus, "3") // 3 = 履行中
                       .eq(HzContract::getDelFlag, "0")
                       .orderByDesc(HzContract::getCreateTime);

        List<HzContract> contracts = this.list(contractWrapper);

        // 获取该租户的所有退租申请
        LambdaQueryWrapper<HzCheckoutApply> applyWrapper = new LambdaQueryWrapper<>();
        applyWrapper.eq(HzCheckoutApply::getTenantId, tenantId)
                 .eq(HzCheckoutApply::getDelFlag, "0")
                 .in(HzCheckoutApply::getApplyStatus, "0", "1"); // 0=审批中, 1=审批通过
        List<HzCheckoutApply> applyingApplies = checkoutApplyMapper.selectList(applyWrapper);

        // 提取有进行中退租申请的合同ID集合
        Set<Long> applyingContractIds = applyingApplies.stream()
                .map(HzCheckoutApply::getContractId)
                .collect(Collectors.toSet());

        // 过滤掉有进行中退租申请的合同
        return contracts.stream()
                .filter(contract -> !applyingContractIds.contains(contract.getContractId()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public int createContractWithLockHouse(HzContract contract) {
        // 1. 原子锁定房源（house_status: 0→1）
        int locked = houseMapper.update(null, new LambdaUpdateWrapper<HzHouse>()
                .eq(HzHouse::getHouseId, contract.getHouseId())
                .eq(HzHouse::getHouseStatus, "0")
                .set(HzHouse::getHouseStatus, "1"));
        if (locked == 0) {
            throw new RuntimeException("该房源已被他人选中，请重新选择其他房源");
        }
        // 2. 插入合同
        return this.insertContract(contract);
    }

    @Override
    @Transactional
    public void expireContractAndReleaseHouse(Long contractId, Long houseId) {
        // 1. 查询合同信息（判断是否为批次配租合同）
        HzContract contract = baseMapper.selectById(contractId);
        // 2. 合同状态更新为超时失效
        baseMapper.update(null, new LambdaUpdateWrapper<HzContract>()
                .eq(HzContract::getContractId, contractId)
                .set(HzContract::getContractStatus, "6"));
        // 3. 释放关联房源
        if (houseId != null) {
            // 批次配租合同：房源回退到'3'(修缮中)，保留在配租池中
            // 普通合同：房源释放为'0'(空置)
            String targetStatus = (contract != null && contract.getBatchId() != null) ? "3" : "0";
            houseMapper.update(null, new LambdaUpdateWrapper<HzHouse>()
                    .eq(HzHouse::getHouseId, houseId)
                    .eq(HzHouse::getHouseStatus, "1")
                    .set(HzHouse::getHouseStatus, targetStatus));
        }
    }

    /**
     * 入住超时自动解约（DB 部分，事务）。
     * 不调微信退款 API，仅完成数据库 6 件事并落地一条 hz_checkout_apply（applyStatus='5' 已完成）+
     * hz_checkout_record（refundStatus='0' 待退还）。
     * 调用方在事务外发起微信退款后，再调 markCheckoutRecordRefunded 把 refundStatus 改为 1。
     */
    @Override
    @Transactional
    public Long createAutoCancelCheckoutApplyTx(Long contractId, BigDecimal totalRefund, BigDecimal depositAmt) {
        if (contractId == null) {
            return null;
        }
        HzContract contract = baseMapper.selectById(contractId);
        if (contract == null || !"3".equals(contract.getContractStatus()) || "2".equals(contract.getDelFlag())) {
            // 已被其他流程处理（合同已不再是履行中）
            return null;
        }

        Date now = new Date();
        BigDecimal refundAmount = totalRefund != null ? totalRefund : BigDecimal.ZERO;
        BigDecimal depositRefund = depositAmt != null ? depositAmt : BigDecimal.ZERO;

        // 1. 写 hz_checkout_apply（applyStatus='5' 已完成，让退款管理页面能立即看到）
        HzCheckoutApply apply = new HzCheckoutApply();
        apply.setContractId(contractId);
        apply.setTenantId(contract.getTenantId());
        apply.setHouseId(contract.getHouseId());
        apply.setApplyTime(now);
        apply.setPlanCheckoutDate(now);
        apply.setCheckoutReason("入住超时自动解约");
        apply.setIsEarlyTermination("1");
        apply.setApplyStatus("5");
        apply.setApproveTime(now);
        apply.setApproveBy("系统");
        apply.setApproveOpinion("用户付款后超过 72 小时未提交入住申请，系统自动解约并原路退款");
        apply.setDepositRefund(depositRefund);
        apply.setRefundAmount(refundAmount);
        apply.setPenaltyAmount(BigDecimal.ZERO);
        apply.setUnpaidBills(BigDecimal.ZERO);
        apply.setDamageDeduction(BigDecimal.ZERO);
        apply.setWaterFee(BigDecimal.ZERO);
        apply.setElectricFee(BigDecimal.ZERO);
        apply.setGasFee(BigDecimal.ZERO);
        apply.setHeatingFee(BigDecimal.ZERO);
        apply.setPropertyFee(BigDecimal.ZERO);
        apply.setDelFlag("0");
        apply.setCreateBy("system-auto-cancel");
        apply.setCreateTime(now);
        checkoutApplyMapper.insert(apply);
        Long applyId = apply.getApplyId();

        // 2. 写 hz_checkout_record（refundStatus='0' 待退还，由事务外微信退款成功后改为 1）
        HzCheckoutRecord record = new HzCheckoutRecord();
        record.setApplyId(applyId);
        record.setContractId(contractId);
        record.setTenantId(contract.getTenantId());
        record.setHouseId(contract.getHouseId());
        record.setCheckoutDate(now);
        record.setCheckoutTime(now);
        record.setDepositRefund(depositRefund);
        record.setUnpaidRent(BigDecimal.ZERO);
        record.setPenaltyAmount(BigDecimal.ZERO);
        record.setDamageDeduction(BigDecimal.ZERO);
        record.setUtilityBill(BigDecimal.ZERO);
        record.setRefundStatus("0");
        record.setPaymentMethod("3"); // 3=微信
        record.setPaymentRemark("入住超时系统自动退款，等待微信回调");
        record.setManagerName("系统");
        record.setDelFlag("0");
        record.setCreateBy("system-auto-cancel");
        record.setCreateTime(now);
        checkoutRecordMapper.insert(record);

        // 3. 合同状态改为已解约（5）
        baseMapper.update(null, new LambdaUpdateWrapper<HzContract>()
                .eq(HzContract::getContractId, contractId)
                .set(HzContract::getContractStatus, "5"));

        // 4. 释放房源
        if (contract.getHouseId() != null) {
            houseMapper.update(null, new LambdaUpdateWrapper<HzHouse>()
                    .eq(HzHouse::getHouseId, contract.getHouseId())
                    .eq(HzHouse::getHouseStatus, "1")
                    .set(HzHouse::getHouseStatus, "0"));
        }

        // 5. 软删该合同下的入住单（理论上 status>=1 已豁免，所以这里通常 0 行）
        String cancelTimeStr = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);
        checkInMapper.update(null, new LambdaUpdateWrapper<HzCheckIn>()
                .eq(HzCheckIn::getContractId, contractId)
                .eq(HzCheckIn::getDelFlag, "0")
                .set(HzCheckIn::getDelFlag, "2")
                .set(HzCheckIn::getStatus, "3")
                .set(HzCheckIn::getCancelReason, "入住超时系统自动取消")
                .set(HzCheckIn::getCancelTime, cancelTimeStr));

        // 6. 站内消息
        try {
            String contractNo = contract.getContractNo() != null ? contract.getContractNo() : ("ID:" + contractId);
            String title = "入住超时-合同已自动解约并退款";
            String content = "您的合同" + contractNo + "因付款后超过 72 小时未提交入住申请，"
                    + "系统已自动解约并发起原路退款，预计退款 "
                    + refundAmount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString()
                    + " 元（押金+首期租金），微信退款将在 2 分钟内到账，如有疑问请联系管理员。";
            messageService.sendMessage(contract.getTenantId(), "contract", title, content);
        } catch (Exception ignore) {
            // 消息发送失败不影响主流程
        }

        return applyId;
    }

    @Override
    @Transactional
    public void markCheckoutRecordRefunded(Long applyId, String paymentRemark) {
        if (applyId == null) {
            return;
        }
        Date now = new Date();
        checkoutRecordMapper.update(null, new LambdaUpdateWrapper<HzCheckoutRecord>()
                .eq(HzCheckoutRecord::getApplyId, applyId)
                .set(HzCheckoutRecord::getRefundStatus, "1")
                .set(HzCheckoutRecord::getRefundTime, now)
                .set(HzCheckoutRecord::getPaymentMethod, "3")
                .set(HzCheckoutRecord::getPaymentRemark, paymentRemark)
                .set(HzCheckoutRecord::getUpdateBy, "system-auto-cancel")
                .set(HzCheckoutRecord::getUpdateTime, now));
    }

    @Override
    @Transactional
    public void markCheckoutRecordRefundFailed(Long applyId, String paymentRemark) {
        if (applyId == null) {
            return;
        }
        Date now = new Date();
        checkoutRecordMapper.update(null, new LambdaUpdateWrapper<HzCheckoutRecord>()
                .eq(HzCheckoutRecord::getApplyId, applyId)
                .set(HzCheckoutRecord::getPaymentRemark, paymentRemark)
                .set(HzCheckoutRecord::getUpdateBy, "system-auto-cancel")
                .set(HzCheckoutRecord::getUpdateTime, now));
    }
}
