package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzCheckoutApply;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.mapper.HzCheckoutApplyMapper;
import com.ruoyi.system.mapper.HzContractMapper;
import com.ruoyi.system.service.IHzCheckoutService;
import com.ruoyi.system.service.IHzContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
        return this.page(page, wrapper);
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
}
