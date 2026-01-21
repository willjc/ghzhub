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
 * 企业退租申请对象 hz_qiyetuizu
 *
 * @author ruoyi
 */
@TableName("hz_qiyetuizu")
public class HzEnterpriseCheckout extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 退租申请ID */
    @TableId(type = IdType.AUTO)
    @TableField("checkout_id")
    private Long checkoutId;

    /** 退租申请编号 */
    @TableField("checkout_no")
    private String checkoutNo;

    /** 账单ID */
    @TableField("bill_id")
    private Long billId;

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

    /** 联系电话 */
    @TableField("contact_phone")
    private String contactPhone;

    /** 项目名称 */
    @TableField("project_name")
    private String projectName;

    /** 房源信息（JSON格式） */
    @TableField("house_info")
    private String houseInfo;

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

    /** 退租状态(0已申请 1已处理) */
    @TableField("checkout_status")
    private String checkoutStatus;

    /** 申请时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("apply_time")
    private Date applyTime;

    /** 删除标志(0正常 2删除) */
    @TableField("del_flag")
    private String delFlag;

    public Long getCheckoutId() {
        return checkoutId;
    }

    public void setCheckoutId(Long checkoutId) {
        this.checkoutId = checkoutId;
    }

    public String getCheckoutNo() {
        return checkoutNo;
    }

    public void setCheckoutNo(String checkoutNo) {
        this.checkoutNo = checkoutNo;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
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

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getHouseInfo() {
        return houseInfo;
    }

    public void setHouseInfo(String houseInfo) {
        this.houseInfo = houseInfo;
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

    public String getCheckoutStatus() {
        return checkoutStatus;
    }

    public void setCheckoutStatus(String checkoutStatus) {
        this.checkoutStatus = checkoutStatus;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
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
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("checkoutId", getCheckoutId())
                .append("checkoutNo", getCheckoutNo())
                .append("billId", getBillId())
                .append("batchId", getBatchId())
                .append("batchNo", getBatchNo())
                .append("batchName", getBatchName())
                .append("enterpriseName", getEnterpriseName())
                .append("contactPerson", getContactPerson())
                .append("contactPhone", getContactPhone())
                .append("projectName", getProjectName())
                .append("houseInfo", getHouseInfo())
                .append("houseCount", getHouseCount())
                .append("totalAmount", getTotalAmount())
                .append("discountAmount", getDiscountAmount())
                .append("finalAmount", getFinalAmount())
                .append("checkInDate", getCheckInDate())
                .append("checkOutDate", getCheckOutDate())
                .append("months", getMonths())
                .append("checkoutStatus", getCheckoutStatus())
                .append("applyTime", getApplyTime())
                .append("delFlag", getDelFlag())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
