package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 合同备案对象 hz_contract_filing
 * 租户提交购房合同备案，管理端审批
 *
 * @author ruoyi
 */
@TableName("hz_contract_filing")
public class HzContractFiling extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 备案ID */
    @TableId(type = IdType.AUTO)
    @Excel(name = "备案ID")
    private Long filingId;

    /** 备案编号 */
    @Excel(name = "备案编号")
    @TableField("filing_no")
    private String filingNo;

    /** 租户ID */
    @TableField("tenant_id")
    private Long tenantId;

    /** 签约人姓名 */
    @Excel(name = "签约人")
    @TableField("sign_name")
    private String signName;

    /** 签约单位 */
    @Excel(name = "签约单位")
    @TableField("sign_unit")
    private String signUnit;

    /** 合同签订日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "签订日期", width = 15, dateFormat = "yyyy-MM-dd")
    @TableField("sign_date")
    private Date signDate;

    /** 合同附件URL（多文件逗号分隔） */
    @TableField("contract_files")
    private String contractFiles;

    /** 审批状态：0=待审批 1=已通过 2=已驳回 */
    @Excel(name = "审批状态", readConverterExp = "0=待审批,1=已通过,2=已驳回")
    @TableField("approve_status")
    private String approveStatus;

    /** 审批人 */
    @TableField("approve_by")
    private String approveBy;

    /** 审批时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("approve_time")
    private Date approveTime;

    /** 审批备注（驳回原因） */
    @TableField("approve_remark")
    private String approveRemark;

    /** 删除标志 */
    @TableField("del_flag")
    @TableLogic
    private String delFlag;

    public Long getFilingId() { return filingId; }
    public void setFilingId(Long filingId) { this.filingId = filingId; }

    public String getFilingNo() { return filingNo; }
    public void setFilingNo(String filingNo) { this.filingNo = filingNo; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public String getSignName() { return signName; }
    public void setSignName(String signName) { this.signName = signName; }

    public String getSignUnit() { return signUnit; }
    public void setSignUnit(String signUnit) { this.signUnit = signUnit; }

    public Date getSignDate() { return signDate; }
    public void setSignDate(Date signDate) { this.signDate = signDate; }

    public String getContractFiles() { return contractFiles; }
    public void setContractFiles(String contractFiles) { this.contractFiles = contractFiles; }

    public String getApproveStatus() { return approveStatus; }
    public void setApproveStatus(String approveStatus) { this.approveStatus = approveStatus; }

    public String getApproveBy() { return approveBy; }
    public void setApproveBy(String approveBy) { this.approveBy = approveBy; }

    public Date getApproveTime() { return approveTime; }
    public void setApproveTime(Date approveTime) { this.approveTime = approveTime; }

    public String getApproveRemark() { return approveRemark; }
    public void setApproveRemark(String approveRemark) { this.approveRemark = approveRemark; }

    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
