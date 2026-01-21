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

    /** 账单ID */
    @TableField("bill_id")
    private Long billId;

    /** 发票类型(1:增值税普通发票 2:增值税专用发票) */
    @TableField("invoice_type")
    private String invoiceType;

    /** 发票抬头 */
    @TableField("invoice_title")
    private String invoiceTitle;

    /** 纳税人识别号 */
    @TableField("tax_no")
    private String taxNo;

    /** 开户银行 */
    @TableField("bank_name")
    private String bankName;

    /** 银行账号 */
    @TableField("bank_account")
    private String bankAccount;

    /** 注册地址 */
    @TableField("register_address")
    private String registerAddress;

    /** 注册电话 */
    @TableField("register_phone")
    private String registerPhone;

    /** 开票金额 */
    @TableField("invoice_amount")
    private BigDecimal invoiceAmount;

    /** 开票内容 */
    @TableField("invoice_content")
    private String invoiceContent;

    /** 收件人 */
    @TableField("receiver_name")
    private String receiverName;

    /** 收件电话 */
    @TableField("receiver_phone")
    private String receiverPhone;

    /** 收件地址 */
    @TableField("receiver_address")
    private String receiverAddress;

    /** 申请状态(0:待审核 1:已通过 2:已拒绝) */
    @TableField("apply_status")
    private String applyStatus;

    /** 审核人 */
    @TableField("auditor")
    private String auditor;

    /** 审核时间 */
    @TableField("audit_time")
    private String auditTime;

    /** 审核意见 */
    @TableField("audit_opinion")
    private String auditOpinion;

    /** 发票ID */
    @TableField("invoice_id")
    private Long invoiceId;

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

    public Long getBillId()
    {
        return billId;
    }

    public void setBillId(Long billId)
    {
        this.billId = billId;
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

    public String getRegisterAddress()
    {
        return registerAddress;
    }

    public void setRegisterAddress(String registerAddress)
    {
        this.registerAddress = registerAddress;
    }

    public String getRegisterPhone()
    {
        return registerPhone;
    }

    public void setRegisterPhone(String registerPhone)
    {
        this.registerPhone = registerPhone;
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

    public String getApplyStatus()
    {
        return applyStatus;
    }

    public void setApplyStatus(String applyStatus)
    {
        this.applyStatus = applyStatus;
    }

    public String getAuditor()
    {
        return auditor;
    }

    public void setAuditor(String auditor)
    {
        this.auditor = auditor;
    }

    public String getAuditTime()
    {
        return auditTime;
    }

    public void setAuditTime(String auditTime)
    {
        this.auditTime = auditTime;
    }

    public String getAuditOpinion()
    {
        return auditOpinion;
    }

    public void setAuditOpinion(String auditOpinion)
    {
        this.auditOpinion = auditOpinion;
    }

    public Long getInvoiceId()
    {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId)
    {
        this.invoiceId = invoiceId;
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
                ", billId=" + billId +
                ", invoiceType='" + invoiceType + '\'' +
                ", invoiceTitle='" + invoiceTitle + '\'' +
                ", taxNo='" + taxNo + '\'' +
                ", bankName='" + bankName + '\'' +
                ", bankAccount='" + bankAccount + '\'' +
                ", registerAddress='" + registerAddress + '\'' +
                ", registerPhone='" + registerPhone + '\'' +
                ", invoiceAmount=" + invoiceAmount +
                ", invoiceContent='" + invoiceContent + '\'' +
                ", receiverName='" + receiverName + '\'' +
                ", receiverPhone='" + receiverPhone + '\'' +
                ", receiverAddress='" + receiverAddress + '\'' +
                ", applyStatus='" + applyStatus + '\'' +
                ", auditor='" + auditor + '\'' +
                ", auditTime='" + auditTime + '\'' +
                ", auditOpinion='" + auditOpinion + '\'' +
                ", invoiceId=" + invoiceId +
                ", delFlag='" + delFlag + '\'' +
                '}';
    }
}
