package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzBill;
import com.ruoyi.system.domain.HzTenant;
import com.ruoyi.system.service.IHzBillService;
import com.ruoyi.system.service.IHzTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * H5端账单管理Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/bill")
public class HzBillController extends BaseController {

    @Autowired
    private IHzBillService billService;

    @Autowired
    private IHzTenantService tenantService;

    /**
     * 查询当前用户的账单列表
     */
    @GetMapping("/list")
    public AjaxResult list() {
        // TODO: 从登录态获取userId
        Long userId = 1L;
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null) {
            return error("请先完善租户信息");
        }

        List<HzBill> list = billService.selectBillListByTenantId(tenant.getTenantId());
        return success(list);
    }

    /**
     * 获取账单详情
     */
    @GetMapping("/{billId}")
    public AjaxResult getInfo(@PathVariable("billId") Long billId) {
        HzBill bill = billService.selectBillById(billId);
        return success(bill);
    }

    /**
     * 查询待支付账单列表
     */
    @GetMapping("/unpaid")
    public AjaxResult unpaidList() {
        // TODO: 从登录态获取userId
        Long userId = 1L;
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null) {
            return error("请先完善租户信息");
        }

        List<HzBill> list = billService.selectUnpaidBillListByTenantId(tenant.getTenantId());
        return success(list);
    }

    /**
     * 查询逾期账单列表
     */
    @GetMapping("/overdue")
    public AjaxResult overdueList() {
        // TODO: 从登录态获取userId
        Long userId = 1L;
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null) {
            return error("请先完善租户信息");
        }

        List<HzBill> list = billService.selectOverdueBillListByTenantId(tenant.getTenantId());
        return success(list);
    }

    /**
     * 根据合同ID查询账单列表
     */
    @GetMapping("/contract/{contractId}")
    public AjaxResult listByContract(@PathVariable("contractId") Long contractId) {
        List<HzBill> list = billService.selectBillListByContractId(contractId);
        return success(list);
    }
}
