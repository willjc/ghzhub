package com.ruoyi.system.domain;

/**
 * 完成开票请求DTO
 *
 * @author ruoyi
 */
public class CompleteInvoiceDTO
{
    /** 申请ID */
    private Long applyId;

    /** 发票号码 */
    private String invoiceNo;

    /** 发票文件路径 */
    private String invoiceFile;

    public Long getApplyId()
    {
        return applyId;
    }

    public void setApplyId(Long applyId)
    {
        this.applyId = applyId;
    }

    public String getInvoiceNo()
    {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo)
    {
        this.invoiceNo = invoiceNo;
    }

    public String getInvoiceFile()
    {
        return invoiceFile;
    }

    public void setInvoiceFile(String invoiceFile)
    {
        this.invoiceFile = invoiceFile;
    }
}
