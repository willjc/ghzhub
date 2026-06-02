package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 房源状态变更审批对象 hz_house_status_audit
 *
 * @author ruoyi
 */
@TableName("hz_house_status_audit")
public class HzHouseStatusAudit extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 审批ID */
    @TableId(type = IdType.AUTO)
    private Long auditId;

    /** 房源ID */
    @TableField("house_id")
    private Long houseId;

    /** 房源编号 */
    @TableField("house_code")
    private String houseCode;

    /** 项目ID */
    @TableField("project_id")
    private Long projectId;

    /** 当前状态 */
    @TableField("current_status")
    private String currentStatus;

    /** 目标状态 */
    @TableField("target_status")
    private String targetStatus;

    /** 申请人(物业) */
    @TableField("apply_by")
    private String applyBy;

    /** 申请时间 */
    @TableField("apply_time")
    private String applyTime;

    /** 审批状态(0=待审批,1=通过,2=驳回) */
    @TableField("approve_status")
    private String approveStatus;

    /** 审批人(管理方) */
    @TableField("approve_by")
    private String approveBy;

    /** 审批时间 */
    @TableField("approve_time")
    private String approveTime;

    /** 审批意见 */
    @TableField("approve_opinion")
    private String approveOpinion;

    /** 删除标志 */
    @TableField("del_flag")
    private String delFlag;

    /** 非数据库字段：项目名称（前端展示用） */
    @TableField(exist = false)
    private String projectName;

    /** 非数据库字段：房间号（前端展示用） */
    @TableField(exist = false)
    private String houseNo;

    public Long getAuditId() { return auditId; }
    public void setAuditId(Long auditId) { this.auditId = auditId; }
    public Long getHouseId() { return houseId; }
    public void setHouseId(Long houseId) { this.houseId = houseId; }
    public String getHouseCode() { return houseCode; }
    public void setHouseCode(String houseCode) { this.houseCode = houseCode; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }
    public String getTargetStatus() { return targetStatus; }
    public void setTargetStatus(String targetStatus) { this.targetStatus = targetStatus; }
    public String getApplyBy() { return applyBy; }
    public void setApplyBy(String applyBy) { this.applyBy = applyBy; }
    public String getApplyTime() { return applyTime; }
    public void setApplyTime(String applyTime) { this.applyTime = applyTime; }
    public String getApproveStatus() { return approveStatus; }
    public void setApproveStatus(String approveStatus) { this.approveStatus = approveStatus; }
    public String getApproveBy() { return approveBy; }
    public void setApproveBy(String approveBy) { this.approveBy = approveBy; }
    public String getApproveTime() { return approveTime; }
    public void setApproveTime(String approveTime) { this.approveTime = approveTime; }
    public String getApproveOpinion() { return approveOpinion; }
    public void setApproveOpinion(String approveOpinion) { this.approveOpinion = approveOpinion; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public String getHouseNo() { return houseNo; }
    public void setHouseNo(String houseNo) { this.houseNo = houseNo; }
}
