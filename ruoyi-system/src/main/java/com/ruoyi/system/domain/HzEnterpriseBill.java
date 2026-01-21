package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 企业账单对象 hz_qiyebill
 *
 * @author ruoyi
 */
@TableName("hz_qiyebill")
public class HzEnterpriseBill extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 账单ID */
    @TableId(type = IdType.AUTO)
    @TableField("bill_id")
    private Long billId;

    /** 账单编号 */
    @TableField("bill_no")
    private String billNo;

    /** 批次ID */
    @TableField("batch_id")
    private Long batchId;

    /** 批次编号 */
    @TableField("batch_no")
    private String batchNo;

    /** 批次名称 */
    @TableField("batch_name")
    private String batchName;

    /** 企业名称 */
    @TableField("enterprise_name")
    private String enterpriseName;

    /** 联系人 */
    @TableField("contact_person")
    private String contactPerson;

    /** 联系方式 */
    @TableField("contact_phone")
    private String contactPhone;

    /** 房源数量 */
    @TableField("house_count")
    private Integer houseCount;

    /** 计算总价 */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /** 优惠金额 */
    @TableField("discount_amount")
    private BigDecimal discountAmount;

    /** 优惠后总价 */
    @TableField("final_amount")
    private BigDecimal finalAmount;

    /** 入驻日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("check_in_date")
    private Date checkInDate;

    /** 截止日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("check_out_date")
    private Date checkOutDate;

    /** 计算月数 */
    @TableField("months")
    private Integer months;

    /** 合同文件路径 */
    @TableField("contract_file")
    private String contractFile;

    /** 账单状态(0待审核 1已审核 2已支付 3已关闭) */
    @TableField("bill_status")
    private String billStatus;

    /** 审批状态(0待审核 1已通过 2已拒绝) */
    @TableField("approve_status")
    private String approveStatus;

    /** 审核人 */
    @TableField("approve_by")
    private String approveBy;

    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("approve_time")
    private Date approveTime;

    /** 支付时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("pay_time")
    private Date payTime;

    /** 支付方式 */
    @TableField("pay_method")
    private String payMethod;

    /** 交易流水号 */
    @TableField("transaction_no")
    private String transactionNo;

    /** 删除标志(0正常 2删除) */
    @TableField("del_flag")
    private String delFlag;

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Integer getHouseCount() {
        return houseCount;
    }

    public void setHouseCount(Integer houseCount) {
        this.houseCount = houseCount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Date getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public String getContractFile() {
        return contractFile;
    }

    public void setContractFile(String contractFile) {
        this.contractFile = contractFile;
    }

    public String getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(String billStatus) {
        this.billStatus = billStatus;
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

    public Date getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(Date approveTime) {
        this.approveTime = approveTime;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("billId", getBillId())
                .append("billNo", getBillNo())
                .append("batchId", getBatchId())
                .append("batchNo", getBatchNo())
                .append("batchName", getBatchName())
                .append("enterpriseName", getEnterpriseName())
                .append("contactPerson", getContactPerson())
                .append("contactPhone", getContactPhone())
                .append("houseCount", getHouseCount())
                .append("totalAmount", getTotalAmount())
                .append("discountAmount", getDiscountAmount())
                .append("finalAmount", getFinalAmount())
                .append("checkInDate", getCheckInDate())
                .append("checkOutDate", getCheckOutDate())
                .append("months", getMonths())
                .append("contractFile", getContractFile())
                .append("billStatus", getBillStatus())
                .append("approveStatus", getApproveStatus())
                .append("approveBy", getApproveBy())
                .append("approveTime", getApproveTime())
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
