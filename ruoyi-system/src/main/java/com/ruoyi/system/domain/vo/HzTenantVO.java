package com.ruoyi.system.domain.vo;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 管理端租户视图，以 hz_user 为主数据源。
 */
public class HzTenantVO extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long userId;

    @Excel(name = "租户姓名")
    private String realName;

    @Excel(name = "身份证号")
    private String idCard;

    @Excel(name = "手机号")
    private String phone;

    private String contactPhone;

    @Excel(name = "性别", readConverterExp = "0=未知,1=男,2=女")
    private String gender;

    @Excel(name = "学历", dictType = "hz_education_type")
    private String education;

    @Excel(name = "职业（身份类型）", dictType = "hz_identity_type")
    private String identityType;

    @Excel(name = "工作单位")
    private String workUnit;

    private String unitNature;
    private String marriageStatus;

    @Excel(name = "是否配租", readConverterExp = "0=未配租,1=已配租")
    private String allocated;

    @Excel(name = "申请时间")
    private String applyTime;

    private String applyType;
    private String qualificationResult;

    @Excel(name = "用户状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getIdentityType() {
        return identityType;
    }

    public void setIdentityType(String identityType) {
        this.identityType = identityType;
    }

    public String getWorkUnit() {
        return workUnit;
    }

    public void setWorkUnit(String workUnit) {
        this.workUnit = workUnit;
    }

    public String getUnitNature() {
        return unitNature;
    }

    public void setUnitNature(String unitNature) {
        this.unitNature = unitNature;
    }

    public String getMarriageStatus() {
        return marriageStatus;
    }

    public void setMarriageStatus(String marriageStatus) {
        this.marriageStatus = marriageStatus;
    }

    public String getAllocated() {
        return allocated;
    }

    public void setAllocated(String allocated) {
        this.allocated = allocated;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public String getApplyType() {
        return applyType;
    }

    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }

    public String getQualificationResult() {
        return qualificationResult;
    }

    public void setQualificationResult(String qualificationResult) {
        this.qualificationResult = qualificationResult;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
