package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * 发票对象 hz_invoice
 *
 * @author ruoyi
 */
@TableName("hz_invoice")
public class HzInvoice extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 发票ID */
    @TableId(type = IdType.AUTO)
    private Long invoiceId;

    /** 发票代码 */
    @TableField("invoice_code")
    private String invoiceCode;

    /** 发票号码 */
    @TableField("invoice_no")
    private String invoiceNo;

    /** 申请ID */
    @TableField("apply_id")
    private Long applyId;

    /** 租户ID */
    @TableField("tenant_id")
    private Long tenantId;

    /** 发票类型(1:增值税普通发票 2:增值税专用发票) */
    @TableField("invoice_type")
    private String invoiceType;

    /** 发票抬头 */
    @TableField("invoice_title")
    private String invoiceTitle;

    /** 纳税人识别号 */
    @TableField("tax_no")
    private String taxNo;

    /** 开票金额 */
    @TableField("invoice_amount")
    private BigDecimal invoiceAmount;

    /** 开票日期 */
    @TableField("invoice_date")
    private String invoiceDate;

    /** 发票文件路径 */
    @TableField("invoice_pdf")
    private String invoiceFile;

    /** 发票状态(0:已开具 1:已作废 2:已红冲) */
    @TableField("invoice_status")
    private String invoiceStatus;

    /** 删除标志(0:正常 2:删除) */
    @TableField("del_flag")
    private String delFlag;

    public Long getInvoiceId()
    {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId)
    {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceCode()
    {
        return invoiceCode;
    }

    public void setInvoiceCode(String invoiceCode)
    {
        this.invoiceCode = invoiceCode;
    }

    public String getInvoiceNo()
    {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo)
    {
        this.invoiceNo = invoiceNo;
    }

    public Long getApplyId()
    {
        return applyId;
    }

    public void setApplyId(Long applyId)
    {
        this.applyId = applyId;
    }

    public Long getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(Long tenantId)
    {
        this.tenantId = tenantId;
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

    public BigDecimal getInvoiceAmount()
    {
        return invoiceAmount;
    }

    public void setInvoiceAmount(BigDecimal invoiceAmount)
    {
        this.invoiceAmount = invoiceAmount;
    }

    public String getInvoiceDate()
    {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate)
    {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceFile()
    {
        return invoiceFile;
    }

    public void setInvoiceFile(String invoiceFile)
    {
        this.invoiceFile = invoiceFile;
    }

    public String getInvoiceStatus()
    {
        return invoiceStatus;
    }

    public void setInvoiceStatus(String invoiceStatus)
    {
        this.invoiceStatus = invoiceStatus;
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
        return "HzInvoice{" +
                "invoiceId=" + invoiceId +
                ", invoiceCode='" + invoiceCode + '\'' +
                ", invoiceNo='" + invoiceNo + '\'' +
                ", applyId=" + applyId +
                ", tenantId=" + tenantId +
                ", invoiceType='" + invoiceType + '\'' +
                ", invoiceTitle='" + invoiceTitle + '\'' +
                ", taxNo='" + taxNo + '\'' +
                ", invoiceAmount=" + invoiceAmount +
                ", invoiceDate='" + invoiceDate + '\'' +
                ", invoiceFile='" + invoiceFile + '\'' +
                ", invoiceStatus='" + invoiceStatus + '\'' +
                ", delFlag='" + delFlag + '\'' +
                '}';
    }
}
