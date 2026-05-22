package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 代购补贴申请对象 hz_subsidy_apply
 * 租户购房后申请代购补贴，含承诺书签署
 *
 * @author ruoyi
 */
@TableName("hz_subsidy_apply")
public class HzSubsidyApply extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 申请ID */
    @TableId(type = IdType.AUTO)
    @Excel(name = "申请ID")
    private Long applyId;

    /** 申请编号 */
    @Excel(name = "申请编号")
    @TableField("apply_no")
    private String applyNo;

    /** 租户ID */
    @TableField("tenant_id")
    private Long tenantId;

    /** 申请人姓名 */
    @Excel(name = "申请人")
    @TableField("apply_name")
    private String applyName;

    /** 身份证号 */
    @Excel(name = "身份证号")
    @TableField("id_card")
    private String idCard;

    /** 联系电话 */
    @Excel(name = "联系电话")
    @TableField("phone")
    private String phone;

    /** 购房合同附件URL（多文件逗号分隔） */
    @TableField("purchase_contract_files")
    private String purchaseContractFiles;

    /** 申请补贴金额 */
    @Excel(name = "补贴金额")
    @TableField("subsidy_amount")
    private BigDecimal subsidyAmount;

    /** 承诺书签署记录ID */
    @TableField("commitment_id")
    private Long commitmentId;

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

    public Long getApplyId() { return applyId; }
    public void setApplyId(Long applyId) { this.applyId = applyId; }

    public String getApplyNo() { return applyNo; }
    public void setApplyNo(String applyNo) { this.applyNo = applyNo; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public String getApplyName() { return applyName; }
    public void setApplyName(String applyName) { this.applyName = applyName; }

    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPurchaseContractFiles() { return purchaseContractFiles; }
    public void setPurchaseContractFiles(String purchaseContractFiles) { this.purchaseContractFiles = purchaseContractFiles; }

    public BigDecimal getSubsidyAmount() { return subsidyAmount; }
    public void setSubsidyAmount(BigDecimal subsidyAmount) { this.subsidyAmount = subsidyAmount; }

    public Long getCommitmentId() { return commitmentId; }
    public void setCommitmentId(Long commitmentId) { this.commitmentId = commitmentId; }

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
