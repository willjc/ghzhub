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
 * 交易流水对象 hz_transaction
 *
 * @author ruoyi
 */
@TableName("hz_transaction")
public class HzTransaction extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 交易ID */
    @TableId(type = IdType.AUTO)
    private Long transactionId;

    /** 交易流水号 */
    @TableField("transaction_no")
    private String transactionNo;

    /** 账单ID */
    @TableField("bill_id")
    private Long billId;

    /** 支付ID */
    @TableField("payment_id")
    private Long paymentId;

    /** 租户ID */
    @TableField("tenant_id")
    private Long tenantId;

    /** 交易类型(1:支付 2:退款 3:转账) */
    @TableField("transaction_type")
    private String transactionType;

    /** 交易金额 */
    @TableField("transaction_amount")
    private BigDecimal transactionAmount;

    /** 交易状态(0:处理中 1:成功 2:失败) */
    @TableField("transaction_status")
    private String transactionStatus;

    /** 第三方交易号 */
    @TableField("third_party_no")
    private String thirdPartyNo;

    /** 支付渠道(1:支付宝 2:微信 3:银行) */
    @TableField("pay_channel")
    private String payChannel;

    /** 交易时间 */
    @TableField("transaction_time")
    private String transactionTime;

    /** 失败原因 */
    @TableField("fail_reason")
    private String failReason;

    /** 删除标志(0:存在 2:删除) */
    @TableField("del_flag")
    private String delFlag;

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public Long getBillId() {
        return billId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setThirdPartyNo(String thirdPartyNo) {
        this.thirdPartyNo = thirdPartyNo;
    }

    public String getThirdPartyNo() {
        return thirdPartyNo;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }

    public String getPayChannel() {
        return payChannel;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public String getFailReason() {
        return failReason;
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
            .append("transactionId", getTransactionId())
            .append("transactionNo", getTransactionNo())
            .append("billId", getBillId())
            .append("paymentId", getPaymentId())
            .append("tenantId", getTenantId())
            .append("transactionType", getTransactionType())
            .append("transactionAmount", getTransactionAmount())
            .append("transactionStatus", getTransactionStatus())
            .append("thirdPartyNo", getThirdPartyNo())
            .append("payChannel", getPayChannel())
            .append("transactionTime", getTransactionTime())
            .append("failReason", getFailReason())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
