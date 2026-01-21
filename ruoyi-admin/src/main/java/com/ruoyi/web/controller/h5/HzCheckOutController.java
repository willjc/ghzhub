package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzCheckoutApply;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.domain.HzTenant;
import com.ruoyi.system.service.IHzCheckoutService;
import com.ruoyi.system.service.IHzContractService;
import com.ruoyi.system.service.IHzTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * H5端退租申请Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/checkout")
public class HzCheckOutController extends BaseController {

    @Autowired
    private IHzCheckoutService checkoutService;

    @Autowired
    private IHzTenantService tenantService;

    @Autowired
    private IHzContractService contractService;

    /**
     * 查询当前用户的退租申请列表
     */
    @GetMapping("/list")
    public AjaxResult list() {
        // TODO: 从登录态获取userId
        Long userId = 1L;
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null) {
            return error("请先完善租户信息");
        }

        List<HzCheckoutApply> list = checkoutService.selectCheckoutApplyListByTenantId(tenant.getTenantId());
        return success(list);
    }

    /**
     * 获取退租申请详情
     */
    @GetMapping("/{applyId}")
    public AjaxResult getInfo(@PathVariable("applyId") Long applyId) {
        HzCheckoutApply checkoutApply = checkoutService.selectCheckoutApplyByApplyId(applyId);
        return success(checkoutApply);
    }

    /**
     * 提交退租申请
     */
    @PostMapping("/apply")
    public AjaxResult apply(@RequestBody HzCheckoutApply checkoutApply) {
        // TODO: 从登录态获取userId
        Long userId = 1L;
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null) {
            return error("请先完善租户信息");
        }

        // 校验合同是否存在
        HzContract contract = contractService.selectContractById(checkoutApply.getContractId());
        if (contract == null) {
            return error("合同不存在");
        }

        // 校验合同是否属于当前租户
        if (!contract.getTenantId().equals(tenant.getTenantId())) {
            return error("无权操作此合同");
        }

        // 校验合同状态(履行中)
        if (!"3".equals(contract.getContractStatus())) {
            return error("合同状态不正确,只有履行中的合同可以申请退租");
        }

        // 设置租户和房源信息
        checkoutApply.setTenantId(tenant.getTenantId());
        checkoutApply.setHouseId(contract.getHouseId());

        // 检查是否已提交过退租申请 (通过查询该租户的所有申请，并检查合同ID)
        List<HzCheckoutApply> existingApplies = checkoutService.selectCheckoutApplyListByTenantId(tenant.getTenantId());
        for (HzCheckoutApply existing : existingApplies) {
            if (checkoutApply.getContractId().equals(existing.getContractId())
                && !"2".equals(existing.getApplyStatus())
                && !"3".equals(existing.getApplyStatus())) {
                return error("该合同已提交过退租申请");
            }
        }

        int result = checkoutService.insertCheckoutApply(checkoutApply);
        return result > 0 ? success() : error("提交失败");
    }

    /**
     * 修改退租申请
     */
    @PutMapping("/update")
    public AjaxResult update(@RequestBody HzCheckoutApply checkoutApply) {
        // TODO: 从登录态获取userId
        Long userId = 1L;
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null) {
            return error("请先完善租户信息");
        }

        // 校验退租申请是否存在
        HzCheckoutApply existCheckoutApply = checkoutService.selectCheckoutApplyByApplyId(checkoutApply.getApplyId());
        if (existCheckoutApply == null) {
            return error("退租申请不存在");
        }

        // 校验是否属于当前租户
        if (!existCheckoutApply.getTenantId().equals(tenant.getTenantId())) {
            return error("无权操作此退租申请");
        }

        // 只有审批中状态可以修改
        if (!"0".equals(existCheckoutApply.getApplyStatus())) {
            return error("当前状态不允许修改");
        }

        int result = checkoutService.updateCheckoutApply(checkoutApply);
        return result > 0 ? success() : error("修改失败");
    }

    /**
     * 取消退租申请
     */
    @DeleteMapping("/{applyId}")
    public AjaxResult cancel(@PathVariable("applyId") Long applyId) {
        // TODO: 从登录态获取userId
        Long userId = 1L;
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null) {
            return error("请先完善租户信息");
        }

        // 校验退租申请是否存在
        HzCheckoutApply existCheckoutApply = checkoutService.selectCheckoutApplyByApplyId(applyId);
        if (existCheckoutApply == null) {
            return error("退租申请不存在");
        }

        // 校验是否属于当前租户
        if (!existCheckoutApply.getTenantId().equals(tenant.getTenantId())) {
            return error("无权操作此退租申请");
        }

        // 只有审批中状态可以取消
        if (!"0".equals(existCheckoutApply.getApplyStatus())) {
            return error("当前状态不允许取消");
        }

        int result = checkoutService.cancelCheckoutApply(applyId);
        return result > 0 ? success() : error("取消失败");
    }
}
