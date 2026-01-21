package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 退款申请对象 hz_refund_apply
 *
 * @author ruoyi
 * @date 2025-01-18
 */
@TableName("hz_refund_apply")
public class HzRefundApply extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 退款申请ID */
    @TableId(type = IdType.AUTO)
    private Long applyId;

    /** 退款编号 */
    private String applyNo;

    /** 支付ID */
    private Long paymentId;

    /** 租户ID */
    private Long tenantId;

    /** 租户姓名 */
    private String tenantName;

    /** 退款金额 */
    private java.math.BigDecimal refundAmount;

    /** 退款原因 */
    private String refundReason;

    /** 开户银行名称 */
    private String bankName;

    /** 银行账号 */
    private String bankAccount;

    /** 账户名称 */
    private String accountName;

    /** 审批状态（0=待审核 1=审核通过 2=审核驳回） */
    private String approveStatus;

    /** 审批人 */
    private String approveBy;

    /** 审批时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date approveTime;

    /** 审批意见 */
    private String approveOpinion;

    /** 申请时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date applyTime;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public Long getApplyId() {
        return applyId;
    }

    public void setApplyId(Long applyId) {
        this.applyId = applyId;
    }

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
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

    public java.math.BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(java.math.BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
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

    public String getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(String approveStatus) {
        this.approveStatus = approveStatus;
    }

    public String getApproveBy() {
        return approveBy;
    }

    public void setApproveBy(String approveBy) {
        this.approveBy = approveBy;
    }

    public java.util.Date getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(java.util.Date approveTime) {
        this.approveTime = approveTime;
    }

    public String getApproveOpinion() {
        return approveOpinion;
    }

    public void setApproveOpinion(String approveOpinion) {
        this.approveOpinion = approveOpinion;
    }

    public java.util.Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(java.util.Date applyTime) {
        this.applyTime = applyTime;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("applyId", getApplyId())
                .append("applyNo", getApplyNo())
                .append("paymentId", getPaymentId())
                .append("tenantId", getTenantId())
                .append("tenantName", getTenantName())
                .append("refundAmount", getRefundAmount())
                .append("refundReason", getRefundReason())
                .append("bankName", getBankName())
                .append("bankAccount", getBankAccount())
                .append("accountName", getAccountName())
                .append("approveStatus", getApproveStatus())
                .append("approveBy", getApproveBy())
                .append("approveTime", getApproveTime())
                .append("approveOpinion", getApproveOpinion())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
