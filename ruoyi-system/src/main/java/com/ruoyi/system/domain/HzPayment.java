package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 支付记录对象 hz_payment
 *
 * @author ruoyi
 */
@TableName("hz_payment")
public class HzPayment extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 支付ID */
    @TableId(type = IdType.AUTO)
    private Long paymentId;

    /** 账单ID */
    @TableField("bill_id")
    private Long billId;

    /** 支付流水号 */
    @TableField("payment_no")
    private String paymentNo;

    /** 支付金额 */
    @TableField("payment_amount")
    private BigDecimal paymentAmount;

    /** 支付方式(1:支付宝 2:微信 3:银行转账 4:现金 5:其他) */
    @TableField("payment_method")
    private String paymentMethod;

    /** 支付状态(0:待支付 1:支付中 2:支付成功 3:支付失败 4:已退款) */
    @TableField("payment_status")
    private String paymentStatus;

    /** 第三方交易号 */
    @TableField("transaction_no")
    private String transactionNo;

    /** 支付时间 */
    @TableField("pay_time")
    private String payTime;

    /** 退款金额 */
    @TableField("refund_amount")
    private BigDecimal refundAmount;

    /** 退款时间 */
    @TableField("refund_time")
    private String refundTime;

    /** 退款原因 */
    @TableField("refund_reason")
    private String refundReason;

    /** 支付凭证 */
    @TableField("payment_voucher")
    private String paymentVoucher;

    /** 删除标志(0:存在 2:删除) */
    @TableField("del_flag")
    private String delFlag;

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public Long getBillId() {
        return billId;
    }

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }

    public String getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundTime(String refundTime) {
        this.refundTime = refundTime;
    }

    public String getRefundTime() {
        return refundTime;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setPaymentVoucher(String paymentVoucher) {
        this.paymentVoucher = paymentVoucher;
    }

    public String getPaymentVoucher() {
        return paymentVoucher;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getDelFlag() {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("paymentId", getPaymentId())
            .append("billId", getBillId())
            .append("paymentNo", getPaymentNo())
            .append("paymentAmount", getPaymentAmount())
            .append("paymentMethod", getPaymentMethod())
            .append("paymentStatus", getPaymentStatus())
            .append("transactionNo", getTransactionNo())
            .append("payTime", getPayTime())
            .append("refundAmount", getRefundAmount())
            .append("refundTime", getRefundTime())
            .append("refundReason", getRefundReason())
            .append("paymentVoucher", getPaymentVoucher())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
