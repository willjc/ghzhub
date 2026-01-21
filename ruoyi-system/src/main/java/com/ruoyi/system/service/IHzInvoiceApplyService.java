package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.HzInvoiceApply;

import java.util.List;

/**
 * 发票申请Service接口
 *
 * @author ruoyi
 */
public interface IHzInvoiceApplyService
{
    /**
     * 根据ID查询发票申请
     *
     * @param applyId 发票申请ID
     * @return 发票申请
     */
    public HzInvoiceApply selectInvoiceApplyById(Long applyId);

    /**
     * 根据租户ID查询发票申请列表
     *
     * @param tenantId 租户ID
     * @return 发票申请列表
     */
    public List<HzInvoiceApply> selectInvoiceApplyListByTenantId(Long tenantId);

    /**
     * 根据账单ID查询发票申请
     *
     * @param billId 账单ID
     * @return 发票申请
     */
    public HzInvoiceApply selectInvoiceApplyByBillId(Long billId);

    /**
     * 分页查询发票申请列表
     *
     * @param invoiceApply 发票申请
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 发票申请列表
     */
    public IPage<HzInvoiceApply> selectInvoiceApplyPage(HzInvoiceApply invoiceApply, int pageNum, int pageSize);

    /**
     * 新增发票申请
     *
     * @param invoiceApply 发票申请
     * @return 结果
     */
    public int insertInvoiceApply(HzInvoiceApply invoiceApply);

    /**
     * 修改发票申请
     *
     * @param invoiceApply 发票申请
     * @return 结果
     */
    public int updateInvoiceApply(HzInvoiceApply invoiceApply);

    /**
     * 删除发票申请
     *
     * @param applyId 发票申请ID
     * @return 结果
     */
    public int deleteInvoiceApplyById(Long applyId);

    /**
     * 生成申请编号
     *
     * @return 申请编号
     */
    public String generateApplyNo();
}
