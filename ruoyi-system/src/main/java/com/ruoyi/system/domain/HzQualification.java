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
 * 资格审核对象 hz_qualification
 *
 * @author ruoyi
 */
@TableName("hz_qualification")
public class HzQualification extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 资格ID */
    @TableId(type = IdType.AUTO)
    private Long qualificationId;

    /** 租户ID */
    @TableField("tenant_id")
    private Long tenantId;

    /** 申请类型(1:人才公寓 2:保租房) */
    @TableField("apply_type")
    private String applyType;

    /** 申请时间 */
    @TableField("apply_time")
    private String applyTime;

    /** 身份证号 */
    @TableField("id_card")
    private String idCard;

    /** 姓名 */
    @TableField("name")
    private String name;

    /** 手机号 */
    @TableField("phone")
    private String phone;

    /** 社保缴纳月数 */
    @TableField("social_security_months")
    private Integer socialSecurityMonths;

    /** 是否有本地住房(0:否 1:是) */
    @TableField("has_local_house")
    private String hasLocalHouse;

    /** 学历 */
    @TableField("education_level")
    private String educationLevel;

    /** 婚姻状况 */
    @TableField("marriage_status")
    private String marriageStatus;

    /** 家庭人数 */
    @TableField("household_count")
    private Integer householdCount;

    /** 工作单位 */
    @TableField("work_unit")
    private String workUnit;

    /** 月收入 */
    @TableField("monthly_income")
    private BigDecimal monthlyIncome;

    /** 自动审核结果(0:未通过 1:通过) */
    @TableField("auto_check_result")
    private String autoCheckResult;

    /** 自动审核原因 */
    @TableField("auto_check_reason")
    private String autoCheckReason;

    /** 人工审核结果(0:未通过 1:通过) */
    @TableField("manual_check_result")
    private String manualCheckResult;

    /** 人工审核原因 */
    @TableField("manual_check_reason")
    private String manualCheckReason;

    /** 最终结果(0:未通过 1:通过) */
    @TableField("final_result")
    private String finalResult;

    /** 审核人 */
    @TableField("check_by")
    private String checkBy;

    /** 审核时间 */
    @TableField("check_time")
    private String checkTime;

    /** 状态(0:正常 1:停用) */
    @TableField("status")
    private String status;

    /** 删除标志(0:存在 2:删除) */
    @TableField("del_flag")
    private String delFlag;

    public void setQualificationId(Long qualificationId) {
        this.qualificationId = qualificationId;
    }

    public Long getQualificationId() {
        return qualificationId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }

    public String getApplyType() {
        return applyType;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setSocialSecurityMonths(Integer socialSecurityMonths) {
        this.socialSecurityMonths = socialSecurityMonths;
    }

    public Integer getSocialSecurityMonths() {
        return socialSecurityMonths;
    }

    public void setHasLocalHouse(String hasLocalHouse) {
        this.hasLocalHouse = hasLocalHouse;
    }

    public String getHasLocalHouse() {
        return hasLocalHouse;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setMarriageStatus(String marriageStatus) {
        this.marriageStatus = marriageStatus;
    }

    public String getMarriageStatus() {
        return marriageStatus;
    }

    public void setHouseholdCount(Integer householdCount) {
        this.householdCount = householdCount;
    }

    public Integer getHouseholdCount() {
        return householdCount;
    }

    public void setWorkUnit(String workUnit) {
        this.workUnit = workUnit;
    }

    public String getWorkUnit() {
        return workUnit;
    }

    public void setMonthlyIncome(BigDecimal monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setAutoCheckResult(String autoCheckResult) {
        this.autoCheckResult = autoCheckResult;
    }

    public String getAutoCheckResult() {
        return autoCheckResult;
    }

    public void setAutoCheckReason(String autoCheckReason) {
        this.autoCheckReason = autoCheckReason;
    }

    public String getAutoCheckReason() {
        return autoCheckReason;
    }

    public void setManualCheckResult(String manualCheckResult) {
        this.manualCheckResult = manualCheckResult;
    }

    public String getManualCheckResult() {
        return manualCheckResult;
    }

    public void setManualCheckReason(String manualCheckReason) {
        this.manualCheckReason = manualCheckReason;
    }

    public String getManualCheckReason() {
        return manualCheckReason;
    }

    public void setFinalResult(String finalResult) {
        this.finalResult = finalResult;
    }

    public String getFinalResult() {
        return finalResult;
    }

    public void setCheckBy(String checkBy) {
        this.checkBy = checkBy;
    }

    public String getCheckBy() {
        return checkBy;
    }

    public void setCheckTime(String checkTime) {
        this.checkTime = checkTime;
    }

    public String getCheckTime() {
        return checkTime;
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
            .append("qualificationId", getQualificationId())
            .append("tenantId", getTenantId())
            .append("applyType", getApplyType())
            .append("applyTime", getApplyTime())
            .append("idCard", getIdCard())
            .append("name", getName())
            .append("phone", getPhone())
            .append("socialSecurityMonths", getSocialSecurityMonths())
            .append("hasLocalHouse", getHasLocalHouse())
            .append("educationLevel", getEducationLevel())
            .append("marriageStatus", getMarriageStatus())
            .append("householdCount", getHouseholdCount())
            .append("workUnit", getWorkUnit())
            .append("monthlyIncome", getMonthlyIncome())
            .append("autoCheckResult", getAutoCheckResult())
            .append("autoCheckReason", getAutoCheckReason())
            .append("manualCheckResult", getManualCheckResult())
            .append("manualCheckReason", getManualCheckReason())
            .append("finalResult", getFinalResult())
            .append("checkBy", getCheckBy())
            .append("checkTime", getCheckTime())
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
