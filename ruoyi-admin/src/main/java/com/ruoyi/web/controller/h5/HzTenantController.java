package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzBlacklist;
import com.ruoyi.system.domain.HzTenant;
import com.ruoyi.system.service.IHzBlacklistService;
import com.ruoyi.system.service.IHzTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * H5端租户管理Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/tenant")
public class HzTenantController extends BaseController {

    @Autowired
    private IHzTenantService tenantService;

    @Autowired
    private IHzBlacklistService blacklistService;

    /**
     * 获取当前用户的租户信息
     */
    @GetMapping("/info")
    public AjaxResult getInfo() {
        // TODO: 从登录态获取userId
        Long userId = 1L; // 暂时模拟
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        return success(tenant);
    }

    /**
     * 新增或更新租户信息
     */
    @PostMapping("/save")
    public AjaxResult save(@RequestBody HzTenant tenant) {
        // 检查是否在黑名单中
        if (tenant.getIdCard() != null) {
            HzBlacklist blacklist = blacklistService.selectBlacklistByIdCard(tenant.getIdCard());
            if (blacklist != null) {
                return error("该用户已被列入黑名单,原因:" + blacklist.getReason());
            }
        }

        // 检查身份证号是否已存在
        HzTenant existTenant = tenantService.selectTenantByIdCard(tenant.getIdCard());
        if (existTenant != null && !existTenant.getTenantId().equals(tenant.getTenantId())) {
            return error("该身份证号已注册");
        }

        // TODO: 从登录态获取userId
        Long userId = 1L; // 暂时模拟
        tenant.setUserId(userId);
        tenant.setStatus("0");

        if (tenant.getTenantId() == null) {
            // 新增
            return toAjax(tenantService.insertTenant(tenant));
        } else {
            // 修改
            return toAjax(tenantService.updateTenant(tenant));
        }
    }

    /**
     * 根据身份证号查询租户信息(用于校验)
     */
    @GetMapping("/checkIdCard/{idCard}")
    public AjaxResult checkIdCard(@PathVariable("idCard") String idCard) {
        HzTenant tenant = tenantService.selectTenantByIdCard(idCard);
        if (tenant != null) {
            return error("该身份证号已注册");
        }

        // 检查黑名单
        HzBlacklist blacklist = blacklistService.selectBlacklistByIdCard(idCard);
        if (blacklist != null) {
            return error("该用户已被列入黑名单");
        }

        return success();
    }
}
