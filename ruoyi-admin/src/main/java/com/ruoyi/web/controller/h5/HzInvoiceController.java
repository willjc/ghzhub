package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzInvoice;
import com.ruoyi.system.domain.HzInvoiceApply;
import com.ruoyi.system.domain.HzTenant;
import com.ruoyi.system.service.IHzInvoiceApplyService;
import com.ruoyi.system.service.IHzInvoiceService;
import com.ruoyi.system.service.IHzTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * H5端开票管理Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/invoice")
public class HzInvoiceController extends BaseController
{
    @Autowired
    private IHzInvoiceApplyService invoiceApplyService;

    @Autowired
    private IHzInvoiceService invoiceService;

    @Autowired
    private IHzTenantService tenantService;

    /**
     * 查询当前用户的发票申请列表
     */
    @GetMapping("/apply/list")
    public AjaxResult applyList()
    {
        // TODO: 从登录态获取userId
        Long userId = 1L;
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null)
        {
            return error("请先完善租户信息");
        }

        List<HzInvoiceApply> list = invoiceApplyService.selectInvoiceApplyListByTenantId(tenant.getTenantId());
        return success(list);
    }

    /**
     * 获取发票申请详情
     */
    @GetMapping("/apply/{applyId}")
    public AjaxResult getApplyInfo(@PathVariable("applyId") Long applyId)
    {
        HzInvoiceApply invoiceApply = invoiceApplyService.selectInvoiceApplyById(applyId);

        // TODO: 校验申请是否属于当前用户

        return success(invoiceApply);
    }

    /**
     * 根据账单ID查询发票申请
     */
    @GetMapping("/apply/bill/{billId}")
    public AjaxResult getApplyByBillId(@PathVariable("billId") Long billId)
    {
        HzInvoiceApply invoiceApply = invoiceApplyService.selectInvoiceApplyByBillId(billId);
        return success(invoiceApply);
    }

    /**
     * 提交发票申请
     */
    @PostMapping("/apply")
    public AjaxResult submitApply(@RequestBody HzInvoiceApply invoiceApply)
    {
        // TODO: 从登录态获取userId
        Long userId = 1L;
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null)
        {
            return error("请先完善租户信息");
        }

        // 检查该账单是否已申请开票
        if (invoiceApply.getBillId() != null)
        {
            HzInvoiceApply existApply = invoiceApplyService.selectInvoiceApplyByBillId(invoiceApply.getBillId());
            if (existApply != null)
            {
                return error("该账单已申请开票");
            }
        }

        // 设置租户ID
        invoiceApply.setTenantId(tenant.getTenantId());

        int result = invoiceApplyService.insertInvoiceApply(invoiceApply);
        return result > 0 ? success() : error("提交失败");
    }

    /**
     * 修改发票申请
     */
    @PutMapping("/apply")
    public AjaxResult updateApply(@RequestBody HzInvoiceApply invoiceApply)
    {
        // TODO: 校验申请是否属于当前用户

        // 只有待审核状态可以修改
        HzInvoiceApply existApply = invoiceApplyService.selectInvoiceApplyById(invoiceApply.getApplyId());
        if (existApply == null)
        {
            return error("申请不存在");
        }

        if (!"0".equals(existApply.getApplyStatus()))
        {
            return error("当前状态不允许修改");
        }

        int result = invoiceApplyService.updateInvoiceApply(invoiceApply);
        return result > 0 ? success() : error("修改失败");
    }

    /**
     * 取消发票申请
     */
    @DeleteMapping("/apply/{applyId}")
    public AjaxResult cancelApply(@PathVariable("applyId") Long applyId)
    {
        // TODO: 校验申请是否属于当前用户

        // 只有待审核状态可以取消
        HzInvoiceApply existApply = invoiceApplyService.selectInvoiceApplyById(applyId);
        if (existApply == null)
        {
            return error("申请不存在");
        }

        if (!"0".equals(existApply.getApplyStatus()))
        {
            return error("当前状态不允许取消");
        }

        int result = invoiceApplyService.deleteInvoiceApplyById(applyId);
        return result > 0 ? success() : error("取消失败");
    }

    /**
     * 查询当前用户的发票列表
     */
    @GetMapping("/list")
    public AjaxResult list()
    {
        // TODO: 从登录态获取userId
        Long userId = 1L;
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null)
        {
            return error("请先完善租户信息");
        }

        List<HzInvoice> list = invoiceService.selectInvoiceListByTenantId(tenant.getTenantId());
        return success(list);
    }

    /**
     * 获取发票详情
     */
    @GetMapping("/{invoiceId}")
    public AjaxResult getInfo(@PathVariable("invoiceId") Long invoiceId)
    {
        HzInvoice invoice = invoiceService.selectInvoiceById(invoiceId);

        // TODO: 校验发票是否属于当前用户

        return success(invoice);
    }

    /**
     * 根据申请ID获取发票
     */
    @GetMapping("/byApply/{applyId}")
    public AjaxResult getInvoiceByApplyId(@PathVariable("applyId") Long applyId)
    {
        HzInvoice invoice = invoiceService.selectInvoiceByApplyId(applyId);
        return success(invoice);
    }
}
