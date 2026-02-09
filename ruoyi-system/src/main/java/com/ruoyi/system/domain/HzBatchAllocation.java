package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 配租批次对象 hz_batch_allocation
 *
 * @author ruoyi
 */
@TableName("hz_batch_allocation")
public class HzBatchAllocation extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 批次ID */
    @TableId(type = IdType.AUTO)
    @TableField("batch_id")
    private Long batchId;

    /** 批次编号 */
    @TableField("batch_no")
    private String batchNo;

    /** 批次名称 */
    @TableField("batch_name")
    private String batchName;

    /** 企业ID */
    @TableField("enterprise_id")
    private Long enterpriseId;

    /** 企业名称 */
    @TableField("enterprise_name")
    private String enterpriseName;

    /** 联系人 */
    @TableField("contact_person")
    private String contactPerson;

    /** 联系电话 */
    @TableField("contact_phone")
    private String contactPhone;

    /** 项目ID */
    @TableField("project_id")
    private Long projectId;

    /** 项目ID列表(逗号分隔,支持多项目) */
    @TableField("project_ids")
    private String projectIds;

    /** 批次人才类型(0普通人才 1定向人才) */
    @TableField("talent_type")
    private String talentType;

    /** 入驻开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("entry_start_date")
    private Date entryStartDate;

    /** 入驻结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("entry_end_date")
    private Date entryEndDate;

    /** 项目名称(非数据库字段) */
    @TableField(exist = false)
    private String projectName;

    /** 房源数量 */
    @TableField("house_count")
    private Integer houseCount;

    /** 已分配数量 */
    @TableField("allocated_count")
    private Integer allocatedCount;

    /** 申请时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("apply_time")
    private Date applyTime;

    /** 审批状态(0待审批 1已通过 2已拒绝) */
    @TableField("approve_status")
    private String approveStatus;

    /** 审批人 */
    @TableField("approve_by")
    private String approveBy;

    /** 审批时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("approve_time")
    private Date approveTime;

    /** 批次状态(0进行中 1已完成 2已作废) */
    @TableField("batch_status")
    private String batchStatus;

    /** 删除标志(0正常 2删除) */
    @TableField("del_flag")
    private String delFlag;

    /** 优惠类型(0:无优惠 1:免租期数) */
    @TableField("preferential_type")
    private String preferentialType;

    /** 免租期数 */
    @TableField("free_rent_periods")
    private Integer freeRentPeriods;

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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectIds() {
        return projectIds;
    }

    public void setProjectIds(String projectIds) {
        this.projectIds = projectIds;
    }

    public String getTalentType() {
        return talentType;
    }

    public void setTalentType(String talentType) {
        this.talentType = talentType;
    }

    public Date getEntryStartDate() {
        return entryStartDate;
    }

    public void setEntryStartDate(Date entryStartDate) {
        this.entryStartDate = entryStartDate;
    }

    public Date getEntryEndDate() {
        return entryEndDate;
    }

    public void setEntryEndDate(Date entryEndDate) {
        this.entryEndDate = entryEndDate;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getHouseCount() {
        return houseCount;
    }

    public void setHouseCount(Integer houseCount) {
        this.houseCount = houseCount;
    }

    public Integer getAllocatedCount() {
        return allocatedCount;
    }

    public void setAllocatedCount(Integer allocatedCount) {
        this.allocatedCount = allocatedCount;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
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

    public String getBatchStatus() {
        return batchStatus;
    }

    public void setBatchStatus(String batchStatus) {
        this.batchStatus = batchStatus;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getPreferentialType() {
        return preferentialType;
    }

    public void setPreferentialType(String preferentialType) {
        this.preferentialType = preferentialType;
    }

    public Integer getFreeRentPeriods() {
        return freeRentPeriods;
    }

    public void setFreeRentPeriods(Integer freeRentPeriods) {
        this.freeRentPeriods = freeRentPeriods;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("batchId", getBatchId())
                .append("batchNo", getBatchNo())
                .append("batchName", getBatchName())
                .append("enterpriseId", getEnterpriseId())
                .append("enterpriseName", getEnterpriseName())
                .append("contactPerson", getContactPerson())
                .append("contactPhone", getContactPhone())
                .append("projectId", getProjectId())
                .append("entryStartDate", getEntryStartDate())
                .append("entryEndDate", getEntryEndDate())
                .append("houseCount", getHouseCount())
                .append("allocatedCount", getAllocatedCount())
                .append("applyTime", getApplyTime())
                .append("approveStatus", getApproveStatus())
                .append("approveBy", getApproveBy())
                .append("approveTime", getApproveTime())
                .append("batchStatus", getBatchStatus())
                .append("delFlag", getDelFlag())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
