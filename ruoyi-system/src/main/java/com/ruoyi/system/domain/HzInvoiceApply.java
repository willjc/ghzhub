package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * 发票申请对象 hz_invoice_apply
 *
 * @author ruoyi
 */
@TableName("hz_invoice_apply")
public class HzInvoiceApply extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 发票申请ID */
    @TableId(type = IdType.AUTO)
    private Long applyId;

    /** 申请编号 */
    @TableField("apply_no")
    private String applyNo;

    /** 租户ID */
    @TableField("tenant_id")
    private Long tenantId;

    /** 租户名称 */
    @TableField("tenant_name")
    private String tenantName;

    /** 账单ID列表（逗号分隔） */
    @TableField("bill_ids")
    private String billIds;

    /** 发票类型(1:增值税普通发票 2:增值税专用发票) */
    @TableField("invoice_type")
    private String invoiceType;

    /** 发票抬头 */
    @TableField("invoice_title")
    private String invoiceTitle;

    /** 纳税人识别号 */
    @TableField("tax_no")
    private String taxNo;

    /** 企业地址 */
    @TableField("company_address")
    private String companyAddress;

    /** 企业电话 */
    @TableField("company_phone")
    private String companyPhone;

    /** 开户银行 */
    @TableField("bank_name")
    private String bankName;

    /** 银行账号 */
    @TableField("bank_account")
    private String bankAccount;

    /** 开票金额 */
    @TableField("invoice_amount")
    private BigDecimal invoiceAmount;

    /** 开票内容 */
    @TableField("invoice_content")
    private String invoiceContent;

    /** 邮箱 */
    @TableField("email")
    private String email;

    /** 收件人 */
    @TableField("receiver_name")
    private String receiverName;

    /** 收件电话 */
    @TableField("receiver_phone")
    private String receiverPhone;

    /** 收件地址 */
    @TableField("receiver_address")
    private String receiverAddress;

    /** 申请时间 */
    @TableField("apply_time")
    private String applyTime;

    /** 申请状态(0:待审核 1:开票中 2:已开票) */
    @TableField("apply_status")
    private String applyStatus;

    /** 审批人 */
    @TableField("approve_by")
    private String approveBy;

    /** 审批时间 */
    @TableField("approve_time")
    private String approveTime;

    /** 拒绝原因 */
    @TableField("reject_reason")
    private String rejectReason;

    /** 删除标志(0:正常 2:删除) */
    @TableField("del_flag")
    private String delFlag;

    public Long getApplyId()
    {
        return applyId;
    }

    public void setApplyId(Long applyId)
    {
        this.applyId = applyId;
    }

    public String getApplyNo()
    {
        return applyNo;
    }

    public void setApplyNo(String applyNo)
    {
        this.applyNo = applyNo;
    }

    public Long getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(Long tenantId)
    {
        this.tenantId = tenantId;
    }

    public String getTenantName()
    {
        return tenantName;
    }

    public void setTenantName(String tenantName)
    {
        this.tenantName = tenantName;
    }

    public String getBillIds()
    {
        return billIds;
    }

    public void setBillIds(String billIds)
    {
        this.billIds = billIds;
    }

    public String getInvoiceType()
    {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType)
    {
        this.invoiceType = invoiceType;
    }

    public String getInvoiceTitle()
    {
        return invoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle)
    {
        this.invoiceTitle = invoiceTitle;
    }

    public String getTaxNo()
    {
        return taxNo;
    }

    public void setTaxNo(String taxNo)
    {
        this.taxNo = taxNo;
    }

    public String getCompanyAddress()
    {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress)
    {
        this.companyAddress = companyAddress;
    }

    public String getCompanyPhone()
    {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone)
    {
        this.companyPhone = companyPhone;
    }

    public String getBankName()
    {
        return bankName;
    }

    public void setBankName(String bankName)
    {
        this.bankName = bankName;
    }

    public String getBankAccount()
    {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount)
    {
        this.bankAccount = bankAccount;
    }

    public BigDecimal getInvoiceAmount()
    {
        return invoiceAmount;
    }

    public void setInvoiceAmount(BigDecimal invoiceAmount)
    {
        this.invoiceAmount = invoiceAmount;
    }

    public String getInvoiceContent()
    {
        return invoiceContent;
    }

    public void setInvoiceContent(String invoiceContent)
    {
        this.invoiceContent = invoiceContent;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getReceiverName()
    {
        return receiverName;
    }

    public void setReceiverName(String receiverName)
    {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone()
    {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone)
    {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverAddress()
    {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress)
    {
        this.receiverAddress = receiverAddress;
    }

    public String getApplyTime()
    {
        return applyTime;
    }

    public void setApplyTime(String applyTime)
    {
        this.applyTime = applyTime;
    }

    public String getApplyStatus()
    {
        return applyStatus;
    }

    public void setApplyStatus(String applyStatus)
    {
        this.applyStatus = applyStatus;
    }

    public String getApproveBy()
    {
        return approveBy;
    }

    public void setApproveBy(String approveBy)
    {
        this.approveBy = approveBy;
    }

    public String getApproveTime()
    {
        return approveTime;
    }

    public void setApproveTime(String approveTime)
    {
        this.approveTime = approveTime;
    }

    public String getRejectReason()
    {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason)
    {
        this.rejectReason = rejectReason;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    @Override
    public String toString()
    {
        return "HzInvoiceApply{" +
                "applyId=" + applyId +
                ", applyNo='" + applyNo + '\'' +
                ", tenantId=" + tenantId +
                ", tenantName='" + tenantName + '\'' +
                ", billIds='" + billIds + '\'' +
                ", invoiceType='" + invoiceType + '\'' +
                ", invoiceTitle='" + invoiceTitle + '\'' +
                ", taxNo='" + taxNo + '\'' +
                ", invoiceAmount=" + invoiceAmount +
                ", applyStatus='" + applyStatus + '\'' +
                '}';
    }
}
