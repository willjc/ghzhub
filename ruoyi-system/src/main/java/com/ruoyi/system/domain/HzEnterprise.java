package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 企业客户对象 hz_enterprise
 *
 * @author ruoyi
 */
@TableName("hz_enterprise")
public class HzEnterprise extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 企业ID */
    @TableId(type = IdType.AUTO)
    @TableField("enterprise_id")
    private Long enterpriseId;

    /** 企业名称 */
    @TableField("enterprise_name")
    private String enterpriseName;

    /** 统一社会信用代码 */
    @TableField("social_credit_code")
    private String socialCreditCode;

    /** 法人代表 */
    @TableField("legal_person")
    private String legalPerson;

    /** 联系人 */
    @TableField("contact_person")
    private String contactPerson;

    /** 联系电话 */
    @TableField("contact_phone")
    private String contactPhone;

    /** 联系邮箱 */
    @TableField("contact_email")
    private String contactEmail;

    /** 企业地址 */
    @TableField("enterprise_address")
    private String enterpriseAddress;

    /** 营业执照(文件路径) */
    @TableField("business_license")
    private String businessLicense;

    /** 员工人数 */
    @TableField("employee_count")
    private Integer employeeCount;

    /** 已租数量 */
    @TableField("rented_count")
    private Integer rentedCount;

    /** 状态(0正常 1停用) */
    @TableField("status")
    private String status;

    /** 删除标志(0正常 2删除) */
    @TableField("del_flag")
    private String delFlag;

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getSocialCreditCode() {
        return socialCreditCode;
    }

    public void setSocialCreditCode(String socialCreditCode) {
        this.socialCreditCode = socialCreditCode;
    }

    public String getLegalPerson() {
        return legalPerson;
    }

    public void setLegalPerson(String legalPerson) {
        this.legalPerson = legalPerson;
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

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getEnterpriseAddress() {
        return enterpriseAddress;
    }

    public void setEnterpriseAddress(String enterpriseAddress) {
        this.enterpriseAddress = enterpriseAddress;
    }

    public String getBusinessLicense() {
        return businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }

    public Integer getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(Integer employeeCount) {
        this.employeeCount = employeeCount;
    }

    public Integer getRentedCount() {
        return rentedCount;
    }

    public void setRentedCount(Integer rentedCount) {
        this.rentedCount = rentedCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
                .append("enterpriseId", getEnterpriseId())
                .append("enterpriseName", getEnterpriseName())
                .append("socialCreditCode", getSocialCreditCode())
                .append("legalPerson", getLegalPerson())
                .append("contactPerson", getContactPerson())
                .append("contactPhone", getContactPhone())
                .append("contactEmail", getContactEmail())
                .append("enterpriseAddress", getEnterpriseAddress())
                .append("businessLicense", getBusinessLicense())
                .append("employeeCount", getEmployeeCount())
                .append("rentedCount", getRentedCount())
                .append("status", getStatus())
                .append("delFlag", getDelFlag())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
