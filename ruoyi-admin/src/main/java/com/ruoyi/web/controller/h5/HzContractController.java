package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.domain.HzTenant;
import com.ruoyi.system.service.IHzContractService;
import com.ruoyi.system.service.IHzTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * H5端合同管理Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/contract")
public class HzContractController extends BaseController {

    @Autowired
    private IHzContractService contractService;

    @Autowired
    private IHzTenantService tenantService;

    /**
     * 查询当前用户的合同列表
     */
    @GetMapping("/list")
    public AjaxResult list() {
        // TODO: 从登录态获取userId
        Long userId = 1L;
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null) {
            return error("请先完善租户信息");
        }

        List<HzContract> list = contractService.selectContractListByTenantId(tenant.getTenantId());
        return success(list);
    }

    /**
     * 获取合同详情
     */
    @GetMapping("/{contractId}")
    public AjaxResult getInfo(@PathVariable("contractId") Long contractId) {
        HzContract contract = contractService.selectContractById(contractId);
        return success(contract);
    }

    /**
     * 获取合同PDF
     */
    @GetMapping("/pdf/{contractId}")
    public AjaxResult getPdf(@PathVariable("contractId") Long contractId) {
        HzContract contract = contractService.selectContractById(contractId);
        if (contract == null) {
            return error("合同不存在");
        }
        return success(contract.getContractFile());
    }
}
