package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.HzCheckoutApply;
import com.ruoyi.system.domain.HzCheckoutRecord;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.domain.HzRefundApply;
import com.ruoyi.system.domain.HzRefundApplyVO;
import com.ruoyi.system.mapper.HzCheckoutApplyMapper;
import com.ruoyi.system.mapper.HzCheckoutRecordMapper;
import com.ruoyi.system.mapper.HzContractMapper;
import com.ruoyi.system.mapper.HzRefundApplyMapper;
import com.ruoyi.system.service.IHzRefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 退款申请Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzRefundServiceImpl extends ServiceImpl<HzRefundApplyMapper, HzRefundApply> implements IHzRefundService {

    @Autowired
    private HzContractMapper contractMapper;

    @Autowired
    private HzCheckoutApplyMapper checkoutApplyMapper;

    @Autowired
    private HzCheckoutRecordMapper checkoutRecordMapper;

    @Override
    public TableDataInfo selectRefundList(Page<HzCheckoutApply> page, String refundNo, String contractNo, String refundStatus) {
        // 构建退租申请查询条件（已确认且有退款金额）
        LambdaQueryWrapper<HzCheckoutApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzCheckoutApply::getApplyStatus, "5")
               .isNotNull(HzCheckoutApply::getRefundAmount)
               .gt(HzCheckoutApply::getRefundAmount, BigDecimal.ZERO)
               .orderByDesc(HzCheckoutApply::getApproveTime);

        // 退款编号（申请ID）过滤
        if (refundNo != null && !refundNo.isEmpty()) {
            wrapper.like(HzCheckoutApply::getApplyId, refundNo);
        }

        // 合同编号过滤：先查出匹配的 contractId 集合
        if (contractNo != null && !contractNo.isEmpty()) {
            LambdaQueryWrapper<HzContract> contractWrapper = new LambdaQueryWrapper<>();
            contractWrapper.like(HzContract::getContractNo, contractNo);
            List<Long> contractIds = contractMapper.selectList(contractWrapper)
                    .stream().map(HzContract::getContractId).collect(Collectors.toList());
            if (contractIds.isEmpty()) {
                return emptyResult();
            }
            wrapper.in(HzCheckoutApply::getContractId, contractIds);
        }

        // 退款状态过滤：先查出匹配的 applyId 集合
        if (refundStatus != null && !refundStatus.isEmpty()) {
            LambdaQueryWrapper<HzCheckoutRecord> recordWrapper = new LambdaQueryWrapper<>();
            recordWrapper.eq(HzCheckoutRecord::getRefundStatus, refundStatus);
            List<Long> applyIds = checkoutRecordMapper.selectList(recordWrapper)
                    .stream().map(HzCheckoutRecord::getApplyId).collect(Collectors.toList());
            if (applyIds.isEmpty()) {
                return emptyResult();
            }
            wrapper.in(HzCheckoutApply::getApplyId, applyIds);
        }

        // 分页查询
        IPage<HzCheckoutApply> pageResult = checkoutApplyMapper.selectPage(page, wrapper);

        // 对当前页结果做关联填充
        List<HzRefundApplyVO> voList = new ArrayList<>();
        for (HzCheckoutApply checkout : pageResult.getRecords()) {
            voList.add(convertFromCheckout(checkout));
        }

        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setMsg("查询成功");
        rspData.setRows(voList);
        rspData.setTotal(pageResult.getTotal());
        return rspData;
    }

    @Override
    public HzRefundApplyVO selectRefundById(Long refundId) {
        HzCheckoutApply checkout = checkoutApplyMapper.selectById(refundId);
        if (checkout == null) {
            return null;
        }
        return convertFromCheckout(checkout);
    }

    @Override
    public int auditRefund(Long refundId, String approveStatus, String approveOpinion) {
        return 1;
    }

    @Override
    public int deleteRefundById(Long refundId) {
        return 1;
    }

    private TableDataInfo emptyResult() {
        TableDataInfo empty = new TableDataInfo();
        empty.setCode(200);
        empty.setMsg("查询成功");
        empty.setRows(new ArrayList<>());
        empty.setTotal(0);
        return empty;
    }

    /**
     * 从退租申请转换为退款VO
     */
    private HzRefundApplyVO convertFromCheckout(HzCheckoutApply checkout) {
        HzRefundApplyVO vo = new HzRefundApplyVO();

        vo.setRefundId(checkout.getApplyId());
        vo.setRefundNo(String.valueOf(checkout.getApplyId()));
        vo.setContractId(checkout.getContractId());
        vo.setTenantId(checkout.getTenantId());
        vo.setRefundAmount(checkout.getRefundAmount());
        vo.setRefundReason(checkout.getCheckoutReason());
        vo.setApplyTime(checkout.getApproveTime());
        vo.setApproveBy(checkout.getApproveBy());
        vo.setApproveTime(checkout.getApproveTime());
        vo.setApproveOpinion(checkout.getApproveOpinion());

        vo.setWaterFee(checkout.getWaterFee() != null ? checkout.getWaterFee() : BigDecimal.ZERO);
        vo.setElectricFee(checkout.getElectricFee() != null ? checkout.getElectricFee() : BigDecimal.ZERO);
        vo.setGasFee(checkout.getGasFee() != null ? checkout.getGasFee() : BigDecimal.ZERO);
        vo.setHeatingFee(checkout.getHeatingFee() != null ? checkout.getHeatingFee() : BigDecimal.ZERO);
        vo.setPropertyFee(checkout.getPropertyFee() != null ? checkout.getPropertyFee() : BigDecimal.ZERO);
        vo.setDamageDeduction(checkout.getDamageDeduction() != null ? checkout.getDamageDeduction() : BigDecimal.ZERO);
        vo.setPenaltyAmount(checkout.getPenaltyAmount() != null ? checkout.getPenaltyAmount() : BigDecimal.ZERO);
        vo.setDeposit(getDepositFromContract(checkout.getContractId()));

        if (checkout.getContractId() != null) {
            HzContract contract = contractMapper.selectById(checkout.getContractId());
            if (contract != null) {
                vo.setContractNo(contract.getContractNo());
            }
        }

        LambdaQueryWrapper<HzCheckoutRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.eq(HzCheckoutRecord::getApplyId, checkout.getApplyId());
        HzCheckoutRecord record = checkoutRecordMapper.selectOne(recordWrapper);

        String refundStatusVal = "0";
        if (record != null) {
            if (record.getRefundStatus() != null) {
                refundStatusVal = record.getRefundStatus();
            }
            vo.setPaymentMethod(record.getPaymentMethod());
            vo.setPaymentMethodText(getPaymentMethodText(record.getPaymentMethod()));
            vo.setPaymentVoucher(record.getPaymentVoucher());
            vo.setPaymentRemark(record.getPaymentRemark());
            vo.setPaymentTime(record.getRefundTime());
        }
        vo.setRefundStatus(refundStatusVal);
        vo.setRefundStatusText(getRefundStatusText(refundStatusVal));

        return vo;
    }

    private BigDecimal getDepositFromContract(Long contractId) {
        if (contractId == null) return BigDecimal.ZERO;
        HzContract contract = contractMapper.selectById(contractId);
        return (contract != null && contract.getDeposit() != null) ? contract.getDeposit() : BigDecimal.ZERO;
    }

    private String getRefundStatusText(String status) {
        if (status == null) return "待退还";
        switch (status) {
            case "0": return "待退还";
            case "1": return "已退还";
            default: return "待退还";
        }
    }

    private String getPaymentMethodText(String method) {
        if (method == null) return "";
        switch (method) {
            case "1": return "现金";
            case "2": return "支付宝";
            case "3": return "微信";
            case "4": return "银行转账";
            default: return "";
        }
    }
}
