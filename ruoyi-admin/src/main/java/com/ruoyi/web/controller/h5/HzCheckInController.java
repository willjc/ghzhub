package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzCheckIn;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.domain.HzTenant;
import com.ruoyi.system.service.IHzCheckInService;
import com.ruoyi.system.service.IHzContractService;
import com.ruoyi.system.service.IHzTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * H5端入住申请Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/checkin")
public class HzCheckInController extends BaseController {

    @Autowired
    private IHzCheckInService checkInService;

    @Autowired
    private IHzTenantService tenantService;

    @Autowired
    private IHzContractService contractService;

    /**
     * 查询当前用户的入住申请列表
     */
    @GetMapping("/list")
    public AjaxResult list() {
        // TODO: 从登录态获取userId
        Long userId = 1L;
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null) {
            return error("请先完善租户信息");
        }

        List<HzCheckIn> list = checkInService.selectCheckInListByTenantId(tenant.getTenantId());
        return success(list);
    }

    /**
     * 获取入住申请详情
     */
    @GetMapping("/{checkInId}")
    public AjaxResult getInfo(@PathVariable("checkInId") Long checkInId) {
        HzCheckIn checkIn = checkInService.selectCheckInById(checkInId);
        return success(checkIn);
    }

    /**
     * 提交入住申请
     */
    @PostMapping("/apply")
    public AjaxResult apply(@RequestBody HzCheckIn checkIn) {
        // TODO: 从登录态获取userId
        Long userId = 1L;
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null) {
            return error("请先完善租户信息");
        }

        // 校验合同是否存在
        HzContract contract = contractService.selectContractById(checkIn.getContractId());
        if (contract == null) {
            return error("合同不存在");
        }

        // 校验合同是否属于当前租户
        if (!contract.getTenantId().equals(tenant.getTenantId())) {
            return error("无权操作此合同");
        }

        // 校验合同状态(已签署或履行中)
        if (!"2".equals(contract.getContractStatus()) && !"3".equals(contract.getContractStatus())) {
            return error("合同状态不正确");
        }

        // 检查是否已提交过入住申请
        HzCheckIn existCheckIn = checkInService.selectCheckInByContractId(checkIn.getContractId());
        if (existCheckIn != null) {
            return error("该合同已提交过入住申请");
        }

        // 设置租户和房源信息
        checkIn.setTenantId(tenant.getTenantId());
        checkIn.setHouseId(contract.getHouseId());

        int result = checkInService.insertCheckIn(checkIn);
        return result > 0 ? success() : error("提交失败");
    }

    /**
     * 修改入住申请
     */
    @PutMapping("/update")
    public AjaxResult update(@RequestBody HzCheckIn checkIn) {
        // TODO: 从登录态获取userId
        Long userId = 1L;
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null) {
            return error("请先完善租户信息");
        }

        // 校验入住申请是否存在
        HzCheckIn existCheckIn = checkInService.selectCheckInById(checkIn.getRecordId());
        if (existCheckIn == null) {
            return error("入住申请不存在");
        }

        // 校验是否属于当前租户
        if (!existCheckIn.getTenantId().equals(tenant.getTenantId())) {
            return error("无权操作此入住申请");
        }

        // 只有待审核状态可以修改
        if (!"0".equals(existCheckIn.getStatus())) {
            return error("当前状态不允许修改");
        }

        int result = checkInService.updateCheckIn(checkIn);
        return result > 0 ? success() : error("修改失败");
    }

    /**
     * 取消入住申请
     */
    @DeleteMapping("/{checkInId}")
    public AjaxResult cancel(@PathVariable("checkInId") Long checkInId) {
        // TODO: 从登录态获取userId
        Long userId = 1L;
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null) {
            return error("请先完善租户信息");
        }

        // 校验入住申请是否存在
        HzCheckIn existCheckIn = checkInService.selectCheckInById(checkInId);
        if (existCheckIn == null) {
            return error("入住申请不存在");
        }

        // 校验是否属于当前租户
        if (!existCheckIn.getTenantId().equals(tenant.getTenantId())) {
            return error("无权操作此入住申请");
        }

        // 只有待审核状态可以取消
        if (!"0".equals(existCheckIn.getStatus())) {
            return error("当前状态不允许取消");
        }

        int result = checkInService.deleteCheckInById(checkInId);
        return result > 0 ? success() : error("取消失败");
    }
}
