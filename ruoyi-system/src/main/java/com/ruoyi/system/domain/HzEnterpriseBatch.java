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
 * 企业批次对象 hz_enterprise_batch
 *
 * @author ruoyi
 */
@TableName("hz_enterprise_batch")
public class HzEnterpriseBatch extends BaseEntity {
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

    /** 项目ID列表(逗号分隔) */
    @TableField("project_ids")
    private String projectIds;

    /** 房源数量 */
    @TableField("house_count")
    private Integer houseCount;

    /** 选房开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("selection_start_date")
    private Date selectionStartDate;

    /** 选房结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("selection_end_date")
    private Date selectionEndDate;

    /** 入驻日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("check_in_date")
    private Date checkInDate;

    /** 截止日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("check_out_date")
    private Date checkOutDate;

    /** 合同文件路径 */
    @TableField("contract_file")
    private String contractFile;

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

    /** 项目名称(非数据库字段) */
    @TableField(exist = false)
    private String projectName;

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

    public String getProjectIds() {
        return projectIds;
    }

    public void setProjectIds(String projectIds) {
        this.projectIds = projectIds;
    }

    public Integer getHouseCount() {
        return houseCount;
    }

    public void setHouseCount(Integer houseCount) {
        this.houseCount = houseCount;
    }

    public Date getSelectionStartDate() {
        return selectionStartDate;
    }

    public void setSelectionStartDate(Date selectionStartDate) {
        this.selectionStartDate = selectionStartDate;
    }

    public Date getSelectionEndDate() {
        return selectionEndDate;
    }

    public void setSelectionEndDate(Date selectionEndDate) {
        this.selectionEndDate = selectionEndDate;
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

    public String getContractFile() {
        return contractFile;
    }

    public void setContractFile(String contractFile) {
        this.contractFile = contractFile;
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
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("batchId", getBatchId())
                .append("batchNo", getBatchNo())
                .append("batchName", getBatchName())
                .append("enterpriseId", getEnterpriseId())
                .append("enterpriseName", getEnterpriseName())
                .append("contactPerson", getContactPerson())
                .append("contactPhone", getContactPhone())
                .append("projectIds", getProjectIds())
                .append("houseCount", getHouseCount())
                .append("selectionStartDate", getSelectionStartDate())
                .append("selectionEndDate", getSelectionEndDate())
                .append("checkInDate", getCheckInDate())
                .append("checkOutDate", getCheckOutDate())
                .append("contractFile", getContractFile())
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
