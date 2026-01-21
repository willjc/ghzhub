package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 租户信息对象 hz_tenant
 *
 * @author ruoyi
 */
@TableName("hz_tenant")
public class HzTenant extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 租户ID */
    @TableId(type = IdType.AUTO)
    private Long tenantId;

    /** 用户ID */
    @TableField("user_id")
    private Long userId;

    /** 姓名 */
    @TableField("tenant_name")
    private String tenantName;

    /** 身份证号 */
    @TableField("id_card")
    private String idCard;

    /** 手机号 */
    @TableField("phone")
    private String phone;

    /** 性别(1:男 2:女) */
    @TableField("gender")
    private String gender;

    /** 出生日期 */
    @TableField("birth_date")
    private String birthDate;

    /** 学历 */
    @TableField("education")
    private String education;

    /** 婚姻状况 */
    @TableField("marriage_status")
    private String marriageStatus;

    /** 户籍地址 */
    @TableField("household_register")
    private String householdRegister;

    /** 现住址 */
    @TableField("current_address")
    private String currentAddress;

    /** 工作单位 */
    @TableField("work_unit")
    private String workUnit;

    /** 紧急联系人 */
    @TableField("emergency_contact")
    private String emergencyContact;

    /** 紧急联系电话 */
    @TableField("emergency_phone")
    private String emergencyPhone;

    /** 邮箱 */
    @TableField("email")
    private String email;

    /** 微信号 */
    @TableField("wechat")
    private String wechat;

    /** 头像 */
    @TableField("avatar")
    private String avatar;

    /** 信用分 */
    @TableField("credit_score")
    private Integer creditScore;

    /** 是否黑名单(0:否 1:是) */
    @TableField("is_blacklist")
    private String isBlacklist;

    /** 状态(0:正常 1:停用) */
    @TableField("status")
    private String status;

    /** 删除标志(0:存在 2:删除) */
    @TableField("del_flag")
    private String delFlag;

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getEducation() {
        return education;
    }

    public void setMarriageStatus(String marriageStatus) {
        this.marriageStatus = marriageStatus;
    }

    public String getMarriageStatus() {
        return marriageStatus;
    }

    public void setHouseholdRegister(String householdRegister) {
        this.householdRegister = householdRegister;
    }

    public String getHouseholdRegister() {
        return householdRegister;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setWorkUnit(String workUnit) {
        this.workUnit = workUnit;
    }

    public String getWorkUnit() {
        return workUnit;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getWechat() {
        return wechat;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setCreditScore(Integer creditScore) {
        this.creditScore = creditScore;
    }

    public Integer getCreditScore() {
        return creditScore;
    }

    public void setIsBlacklist(String isBlacklist) {
        this.isBlacklist = isBlacklist;
    }

    public String getIsBlacklist() {
        return isBlacklist;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
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
            .append("tenantId", getTenantId())
            .append("userId", getUserId())
            .append("tenantName", getTenantName())
            .append("idCard", getIdCard())
            .append("phone", getPhone())
            .append("gender", getGender())
            .append("birthDate", getBirthDate())
            .append("education", getEducation())
            .append("marriageStatus", getMarriageStatus())
            .append("householdRegister", getHouseholdRegister())
            .append("currentAddress", getCurrentAddress())
            .append("workUnit", getWorkUnit())
            .append("emergencyContact", getEmergencyContact())
            .append("emergencyPhone", getEmergencyPhone())
            .append("email", getEmail())
            .append("wechat", getWechat())
            .append("avatar", getAvatar())
            .append("creditScore", getCreditScore())
            .append("isBlacklist", getIsBlacklist())
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
