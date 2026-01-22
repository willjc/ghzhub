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
 * 账单对象 hz_bill
 *
 * @author ruoyi
 */
@TableName("hz_bill")
public class HzBill extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 账单ID */
    @TableId(type = IdType.AUTO)
    private Long billId;

    /** 账单编号 */
    @TableField("bill_no")
    private String billNo;

    /** 合同ID */
    @TableField("contract_id")
    private Long contractId;

    /** 租户ID */
    @TableField("tenant_id")
    private Long tenantId;

    /** 租户姓名 */
    @TableField("tenant_name")
    private String tenantName;

    /** 房源ID */
    @TableField("house_id")
    private Long houseId;

    /** 房源编号 */
    @TableField("house_code")
    private String houseCode;

    /** 账单类型(1:押金 2:租金 3:水费 4:电费 5:燃气费 6:物业费 7:其他) */
    @TableField("bill_type")
    private String billType;

    /** 账单周期 */
    @TableField("bill_period")
    private String billPeriod;

    /** 账单金额 */
    @TableField("bill_amount")
    private BigDecimal billAmount;

    /** 已支付金额 */
    @TableField("paid_amount")
    private BigDecimal paidAmount;

    /** 未支付金额 */
    @TableField("unpaid_amount")
    private BigDecimal unpaidAmount;

    /** 账单日期 */
    @TableField("bill_date")
    private String billDate;

    /** 应付日期 */
    @TableField("due_date")
    private String dueDate;

    /** 滞纳金 */
    @TableField("late_fee")
    private BigDecimal lateFee;

    /** 账单状态(0:待支付 1:已支付 2:部分支付 3:已逾期 4:已关闭) */
    @TableField("bill_status")
    private String billStatus;

    /** 开票状态(0:未开票 1:开票中 2:已开票) */
    @TableField("invoice_status")
    private String invoiceStatus;

    /** 支付时间 */
    @TableField("pay_time")
    private String payTime;

    /** 支付方式 */
    @TableField("pay_method")
    private String payMethod;

    /** 交易流水号 */
    @TableField("transaction_no")
    private String transactionNo;

    /** 删除标志(0:存在 2:删除) */
    @TableField("del_flag")
    private String delFlag;
    /** 是否逾期(0:否 1:是) */
    @TableField("is_overdue")
    private String isOverdue;
    /** 逾期天数 */
    @TableField("overdue_days")
    private Integer overdueDays;

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setHouseId(Long houseId) {
        this.houseId = houseId;
    }

    public Long getHouseId() {
        return houseId;
    }

    public void setHouseCode(String houseCode) {
        this.houseCode = houseCode;
    }

    public String getHouseCode() {
        return houseCode;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillPeriod(String billPeriod) {
        this.billPeriod = billPeriod;
    }

    public String getBillPeriod() {
        return billPeriod;
    }

    public void setBillAmount(BigDecimal billAmount) {
        this.billAmount = billAmount;
    }

    public BigDecimal getBillAmount() {
        return billAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setUnpaidAmount(BigDecimal unpaidAmount) {
        this.unpaidAmount = unpaidAmount;
    }

    public BigDecimal getUnpaidAmount() {
        return unpaidAmount;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setLateFee(BigDecimal lateFee) {
        this.lateFee = lateFee;
    }

    public BigDecimal getLateFee() {
        return lateFee;
    }

    public void setBillStatus(String billStatus) {
        this.billStatus = billStatus;
    }

    public String getBillStatus() {
        return billStatus;
    }

    public String getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(String invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public String getIsOverdue() {
        return isOverdue;
    }

    public void setIsOverdue(String isOverdue) {
        this.isOverdue = isOverdue;
    }

    public Integer getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(Integer overdueDays) {
        this.overdueDays = overdueDays;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("billId", getBillId())
            .append("billNo", getBillNo())
            .append("contractId", getContractId())
            .append("tenantId", getTenantId())
            .append("tenantName", getTenantName())
            .append("houseId", getHouseId())
            .append("houseCode", getHouseCode())
            .append("billType", getBillType())
            .append("billPeriod", getBillPeriod())
            .append("billAmount", getBillAmount())
            .append("paidAmount", getPaidAmount())
            .append("unpaidAmount", getUnpaidAmount())
            .append("billDate", getBillDate())
            .append("dueDate", getDueDate())
            .append("lateFee", getLateFee())
            .append("billStatus", getBillStatus())
            .append("payTime", getPayTime())
            .append("payMethod", getPayMethod())
            .append("transactionNo", getTransactionNo())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
