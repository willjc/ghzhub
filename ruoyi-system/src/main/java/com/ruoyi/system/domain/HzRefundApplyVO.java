package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 退款申请VO对象
 * 用于退款管理页面展示
 *
 * @author ruoyi
 */
public class HzRefundApplyVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 退款申请ID（实际是退租申请ID） */
    private Long refundId;

    /** 退款编号（实际是退租申请ID） */
    private String refundNo;

    /** 合同编号 */
    private String contractNo;

    /** 合同ID */
    private Long contractId;

    /** 租户ID */
    private Long tenantId;

    /** 租户姓名 */
    private String tenantName;

    /** 退款金额 */
    private BigDecimal refundAmount;

    /** 退款原因 */
    private String refundReason;

    /** 退款状态（0=待退还 1=已退还） */
    private String refundStatus;

    /** 退款状态文本 */
    private String refundStatusText;

    /** 申请时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date applyTime;

    /** 审批人 */
    private String approveBy;

    /** 审批时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date approveTime;

    /** 审批意见 */
    private String approveOpinion;

    /** 开户银行名称 */
    private String bankName;

    /** 银行账号 */
    private String bankAccount;

    /** 账户名称 */
    private String accountName;

    // ===== 费用明细 =====
    /** 水费 */
    private BigDecimal waterFee;

    /** 电费 */
    private BigDecimal electricFee;

    /** 燃气费 */
    private BigDecimal gasFee;

    /** 暖气费 */
    private BigDecimal heatingFee;

    /** 物业费 */
    private BigDecimal propertyFee;

    /** 损坏扣款 */
    private BigDecimal damageDeduction;

    /** 违约金 */
    private BigDecimal penaltyAmount;

    /** 押金 */
    private BigDecimal deposit;

    // ===== 付款信息 =====
    /** 付款方式（1=现金 2=支付宝 3=微信 4=银行转账） */
    private String paymentMethod;

    /** 付款方式文本 */
    private String paymentMethodText;

    /** 付款凭证 */
    private String paymentVoucher;

    /** 付款备注 */
    private String paymentRemark;

    /** 付款时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date paymentTime;

    public Long getRefundId() {
        return refundId;
    }

    public void setRefundId(Long refundId) {
        this.refundId = refundId;
    }

    public String getRefundNo() {
        return refundNo;
    }

    public void setRefundNo(String refundNo) {
        this.refundNo = refundNo;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public String getRefundStatusText() {
        return refundStatusText;
    }

    public void setRefundStatusText(String refundStatusText) {
        this.refundStatusText = refundStatusText;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public String getApproveBy() {
        return approveBy;
    }

    public void setApproveBy(String approveBy) {
        this.approveBy = approveBy;
    }

    public Date getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(Date approveTime) {
        this.approveTime = approveTime;
    }

    public String getApproveOpinion() {
        return approveOpinion;
    }

    public void setApproveOpinion(String approveOpinion) {
        this.approveOpinion = approveOpinion;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public BigDecimal getWaterFee() {
        return waterFee;
    }

    public void setWaterFee(BigDecimal waterFee) {
        this.waterFee = waterFee;
    }

    public BigDecimal getElectricFee() {
        return electricFee;
    }

    public void setElectricFee(BigDecimal electricFee) {
        this.electricFee = electricFee;
    }

    public BigDecimal getGasFee() {
        return gasFee;
    }

    public void setGasFee(BigDecimal gasFee) {
        this.gasFee = gasFee;
    }

    public BigDecimal getHeatingFee() {
        return heatingFee;
    }

    public void setHeatingFee(BigDecimal heatingFee) {
        this.heatingFee = heatingFee;
    }

    public BigDecimal getPropertyFee() {
        return propertyFee;
    }

    public void setPropertyFee(BigDecimal propertyFee) {
        this.propertyFee = propertyFee;
    }

    public BigDecimal getDamageDeduction() {
        return damageDeduction;
    }

    public void setDamageDeduction(BigDecimal damageDeduction) {
        this.damageDeduction = damageDeduction;
    }

    public BigDecimal getPenaltyAmount() {
        return penaltyAmount;
    }

    public void setPenaltyAmount(BigDecimal penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethodText() {
        return paymentMethodText;
    }

    public void setPaymentMethodText(String paymentMethodText) {
        this.paymentMethodText = paymentMethodText;
    }

    public String getPaymentVoucher() {
        return paymentVoucher;
    }

    public void setPaymentVoucher(String paymentVoucher) {
        this.paymentVoucher = paymentVoucher;
    }

    public String getPaymentRemark() {
        return paymentRemark;
    }

    public void setPaymentRemark(String paymentRemark) {
        this.paymentRemark = paymentRemark;
    }

    public Date getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
    }
}
