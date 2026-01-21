package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

/**
 * 退款申请Service业务层处���
 *
 * @author ruoyi
 */
@Service
public class HzRefundServiceImpl extends ServiceImpl<HzRefundApplyMapper, HzRefundApply> implements IHzRefundService {

    @Autowired
    private HzRefundApplyMapper refundApplyMapper;

    @Autowired
    private HzContractMapper contractMapper;

    @Autowired
    private HzCheckoutApplyMapper checkoutApplyMapper;

    @Autowired
    private HzCheckoutRecordMapper checkoutRecordMapper;

    @Override
    public List<HzRefundApplyVO> selectRefundList(String refundNo, String contractNo, String refundStatus) {
        // 从退租申请表中查询已确认且有退款金额的记录
        LambdaQueryWrapper<HzCheckoutApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzCheckoutApply::getApplyStatus, "5")  // 已确认
               .isNotNull(HzCheckoutApply::getRefundAmount)
               .gt(HzCheckoutApply::getRefundAmount, BigDecimal.ZERO)
               .orderByDesc(HzCheckoutApply::getApproveTime);

        // 如果有退款编号条件（使用申请ID搜索）
        if (refundNo != null && !refundNo.isEmpty()) {
            try {
                wrapper.like(HzCheckoutApply::getApplyId, refundNo);
            } catch (NumberFormatException e) {
                // 如果不是数字，忽略该条件
            }
        }

        // 查询退租申请列表
        List<HzCheckoutApply> checkoutList = checkoutApplyMapper.selectList(wrapper);

        // 转换为VO并填充关联信息
        List<HzRefundApplyVO> voList = new ArrayList<>();
        for (HzCheckoutApply checkout : checkoutList) {
            HzRefundApplyVO vo = convertFromCheckout(checkout);

            // 合同编号过滤
            if (contractNo != null && !contractNo.isEmpty()) {
                if (vo.getContractNo() == null || !vo.getContractNo().contains(contractNo)) {
                    continue;
                }
            }

            // 退款状态过滤
            if (refundStatus != null && !refundStatus.isEmpty()) {
                if (!refundStatus.equals(vo.getRefundStatus())) {
                    continue;
                }
            }

            voList.add(vo);
        }

        return voList;
    }

    @Override
    public HzRefundApplyVO selectRefundById(Long refundId) {
        // refundId 实际上是退租申请ID
        HzCheckoutApply checkout = checkoutApplyMapper.selectById(refundId);
        if (checkout == null) {
            return null;
        }
        return convertFromCheckout(checkout);
    }

    @Override
    public int auditRefund(Long refundId, String approveStatus, String approveOpinion) {
        // 审核功能暂不使用，退款状态通过更新退租记录实现
        return 1;
    }

    @Override
    public int deleteRefundById(Long refundId) {
        // 退款管理页面不需要删除功能
        return 1;
    }

    /**
     * 从退租申请转换为退款VO
     */
    private HzRefundApplyVO convertFromCheckout(HzCheckoutApply checkout) {
        HzRefundApplyVO vo = new HzRefundApplyVO();

        // 基本信息
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

        // 费用明细（从退租申请获取）
        vo.setWaterFee(checkout.getWaterFee() != null ? checkout.getWaterFee() : BigDecimal.ZERO);
        vo.setElectricFee(checkout.getElectricFee() != null ? checkout.getElectricFee() : BigDecimal.ZERO);
        vo.setGasFee(checkout.getGasFee() != null ? checkout.getGasFee() : BigDecimal.ZERO);
        vo.setHeatingFee(checkout.getHeatingFee() != null ? checkout.getHeatingFee() : BigDecimal.ZERO);
        vo.setPropertyFee(checkout.getPropertyFee() != null ? checkout.getPropertyFee() : BigDecimal.ZERO);
        vo.setDamageDeduction(checkout.getDamageDeduction() != null ? checkout.getDamageDeduction() : BigDecimal.ZERO);
        vo.setPenaltyAmount(checkout.getPenaltyAmount() != null ? checkout.getPenaltyAmount() : BigDecimal.ZERO);
        // 押金从合同中获取
        vo.setDeposit(getDepositFromContract(checkout.getContractId()));

        // 查询合同编号
        if (checkout.getContractId() != null) {
            HzContract contract = contractMapper.selectById(checkout.getContractId());
            if (contract != null) {
                vo.setContractNo(contract.getContractNo());
            }
        }

        // 查询退租记录获取退款状态和付款信息
        LambdaQueryWrapper<HzCheckoutRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.eq(HzCheckoutRecord::getApplyId, checkout.getApplyId());
        HzCheckoutRecord record = checkoutRecordMapper.selectOne(recordWrapper);

        String refundStatus = "0"; // 默认待退还
        if (record != null) {
            if (record.getRefundStatus() != null) {
                refundStatus = record.getRefundStatus();
            }
            // 付款信息
            vo.setPaymentMethod(record.getPaymentMethod());
            vo.setPaymentMethodText(getPaymentMethodText(record.getPaymentMethod()));
            vo.setPaymentVoucher(record.getPaymentVoucher());
            vo.setPaymentRemark(record.getPaymentRemark());
            vo.setPaymentTime(record.getRefundTime());
        }
        vo.setRefundStatus(refundStatus);
        vo.setRefundStatusText(getRefundStatusText(refundStatus));

        return vo;
    }

    /**
     * 从合同获取押金
     */
    private BigDecimal getDepositFromContract(Long contractId) {
        if (contractId == null) {
            return BigDecimal.ZERO;
        }
        HzContract contract = contractMapper.selectById(contractId);
        if (contract != null && contract.getDeposit() != null) {
            return contract.getDeposit();
        }
        return BigDecimal.ZERO;
    }

    /**
     * 获取退款状态文本
     */
    private String getRefundStatusText(String status) {
        if (status == null) {
            return "待退还";
        }
        switch (status) {
            case "0":
                return "待退还";
            case "1":
                return "已退还";
            default:
                return "待退还";
        }
    }

    /**
     * 获取付款方式文本
     */
    private String getPaymentMethodText(String method) {
        if (method == null) {
            return "";
        }
        switch (method) {
            case "1":
                return "现金";
            case "2":
                return "支付宝";
            case "3":
                return "微信";
            case "4":
                return "银行转账";
            default:
                return "";
        }
    }
}
