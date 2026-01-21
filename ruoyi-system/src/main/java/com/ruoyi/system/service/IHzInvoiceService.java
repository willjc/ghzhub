package com.ruoyi.system.service;

import com.ruoyi.system.domain.HzInvoice;

import java.util.List;

/**
 * 发票Service接口
 *
 * @author ruoyi
 */
public interface IHzInvoiceService
{
    /**
     * 根据ID查询发票
     *
     * @param invoiceId 发票ID
     * @return 发票
     */
    public HzInvoice selectInvoiceById(Long invoiceId);

    /**
     * 根据申请ID查询发票
     *
     * @param applyId 申请ID
     * @return 发票
     */
    public HzInvoice selectInvoiceByApplyId(Long applyId);

    /**
     * 根据租户ID查询发票列表
     *
     * @param tenantId 租户ID
     * @return 发票列表
     */
    public List<HzInvoice> selectInvoiceListByTenantId(Long tenantId);

    /**
     * 新增发票
     *
     * @param invoice 发票
     * @return 结果
     */
    public int insertInvoice(HzInvoice invoice);

    /**
     * 修改发票
     *
     * @param invoice 发票
     * @return 结果
     */
    public int updateInvoice(HzInvoice invoice);

    /**
     * 删除发票
     *
     * @param invoiceId 发票ID
     * @return 结果
     */
    public int deleteInvoiceById(Long invoiceId);
}
