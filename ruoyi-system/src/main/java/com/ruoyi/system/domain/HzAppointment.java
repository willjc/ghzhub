package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 预约看房对象 hz_appointment
 *
 * @author ruoyi
 */
@TableName("hz_appointment")
public class HzAppointment extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 预约ID */
    @TableId(type = IdType.AUTO)
    private Long appointmentId;

    /** 预约编号 */
    @TableField("appointment_no")
    private String appointmentNo;

    /** 租户ID */
    @TableField("tenant_id")
    private Long tenantId;

    /** 房源ID */
    @TableField("house_id")
    private Long houseId;

    /** 项目ID */
    @TableField("project_id")
    private Long projectId;

    /** 预约日期 */
    @TableField("appointment_date")
    private String appointmentDate;

    /** 预约时段 */
    @TableField("time_slot")
    private String timeSlot;

    /** 访客姓名 */
    @TableField("visitor_name")
    private String visitorName;

    /** 访客电话 */
    @TableField("visitor_phone")
    private String visitorPhone;

    /** 身份证号 */
    @TableField("id_card")
    private String idCard;

    /** 预约时间 */
    @TableField("appointment_time")
    private String appointmentTime;

    /** 预约来源 */
    @TableField("appointment_source")
    private String appointmentSource;

    /** 预约人数 */
    @TableField("visitor_count")
    private Integer visitorCount;

    /** 预约状态(0:待确认 1:已确认 2:已取消 3:已完成 4:已过期) */
    @TableField("appointment_status")
    private String appointmentStatus;

    /** 取消原因 */
    @TableField("cancel_reason")
    private String cancelReason;

    /** 确认人 */
    @TableField("confirm_by")
    private String confirmBy;

    /** 确认时间 */
    @TableField("confirm_time")
    private String confirmTime;

    /** 取消时间 */
    @TableField("cancel_time")
    private String cancelTime;

    /** 删除标志(0:存在 2:删除) */
    @TableField("del_flag")
    private String delFlag;

    /** 项目名称（非表字段，用于关联查询） */
    @TableField(exist = false)
    private String projectName;

    /** 房间号（非表字段，用于关联查询） */
    @TableField(exist = false)
    private String houseNo;

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentNo(String appointmentNo) {
        this.appointmentNo = appointmentNo;
    }

    public String getAppointmentNo() {
        return appointmentNo;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setHouseId(Long houseId) {
        this.houseId = houseId;
    }

    public Long getHouseId() {
        return houseId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorPhone(String visitorPhone) {
        this.visitorPhone = visitorPhone;
    }

    public String getVisitorPhone() {
        return visitorPhone;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentSource(String appointmentSource) {
        this.appointmentSource = appointmentSource;
    }

    public String getAppointmentSource() {
        return appointmentSource;
    }

    public void setVisitorCount(Integer visitorCount) {
        this.visitorCount = visitorCount;
    }

    public Integer getVisitorCount() {
        return visitorCount;
    }

    public void setAppointmentStatus(String appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    public String getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setConfirmBy(String confirmBy) {
        this.confirmBy = confirmBy;
    }

    public String getConfirmBy() {
        return confirmBy;
    }

    public void setConfirmTime(String confirmTime) {
        this.confirmTime = confirmTime;
    }

    public String getConfirmTime() {
        return confirmTime;
    }

    public void setCancelTime(String cancelTime) {
        this.cancelTime = cancelTime;
    }

    public String getCancelTime() {
        return cancelTime;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public String getHouseNo() {
        return houseNo;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("appointmentId", getAppointmentId())
            .append("appointmentNo", getAppointmentNo())
            .append("tenantId", getTenantId())
            .append("houseId", getHouseId())
            .append("projectId", getProjectId())
            .append("appointmentDate", getAppointmentDate())
            .append("timeSlot", getTimeSlot())
            .append("visitorName", getVisitorName())
            .append("visitorPhone", getVisitorPhone())
            .append("idCard", getIdCard())
            .append("appointmentTime", getAppointmentTime())
            .append("appointmentSource", getAppointmentSource())
            .append("visitorCount", getVisitorCount())
            .append("appointmentStatus", getAppointmentStatus())
            .append("cancelReason", getCancelReason())
            .append("confirmBy", getConfirmBy())
            .append("confirmTime", getConfirmTime())
            .append("cancelTime", getCancelTime())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
