package com.ruoyi.gangzhu.activity.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

import java.io.Serializable;
import java.util.Date;

/**
 * 活动报名记录对象 hz_activity_registration
 *
 * @author ruoyi
 */
@TableName("hz_activity_registration")
public class HzActivityRegistration implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 报名ID */
    @TableId(value = "registration_id", type = IdType.AUTO)
    private Long registrationId;

    /** 活动ID */
    private Long activityId;

    /** 用户ID */
    private Long userId;

    /** 姓名 */
    @Excel(name = "姓名")
    private String realName;

    /** 联系电话 */
    @Excel(name = "联系方式")
    private String phone;

    /** 备注 */
    private String remark;

    /** 报名状态（0-已报名 1-已取消） */
    @Excel(name = "状态", readConverterExp = "0=已报名,1=已取消")
    private String registrationStatus;

    /** 报名时间 */
    @Excel(name = "报名时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 删除标志（0代表存在 2代表删除） */
    @TableLogic
    private String delFlag;

    /** 所属项目名称（非数据库字段，由有效合同动态推导） */
    @TableField(exist = false)
    @Excel(name = "所属项目")
    private String projectName;

    public Long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public String toString() {
        return "HzActivityRegistration{" +
                "registrationId=" + registrationId +
                ", activityId=" + activityId +
                ", userId=" + userId +
                ", realName='" + realName + '\'' +
                ", phone='" + phone + '\'' +
                ", remark='" + remark + '\'' +
                ", registrationStatus='" + registrationStatus + '\'' +
                ", createTime=" + createTime +
                ", delFlag='" + delFlag + '\'' +
                '}';
    }
}
