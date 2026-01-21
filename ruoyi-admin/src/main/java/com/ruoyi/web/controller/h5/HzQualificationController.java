package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzCommitment;
import com.ruoyi.system.domain.HzQualification;
import com.ruoyi.system.domain.HzQualificationAppeal;
import com.ruoyi.system.domain.HzQualificationAppealVO;
import com.ruoyi.system.domain.HzTenant;
import com.ruoyi.system.service.IHzCommitmentService;
import com.ruoyi.system.service.IHzQualificationAppealService;
import com.ruoyi.system.service.IHzQualificationService;
import com.ruoyi.system.service.IHzTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * H5端资格审核Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/qualification")
public class HzQualificationController extends BaseController {

    @Autowired
    private IHzQualificationService qualificationService;

    @Autowired
    private IHzTenantService tenantService;

    @Autowired
    private IHzCommitmentService commitmentService;

    @Autowired
    private IHzQualificationAppealService appealService;

    /**
     * 查询当前用户的资格审核列表
     */
    @GetMapping("/list")
    public AjaxResult list() {
        // TODO: 从登录态获取userId
        Long userId = 1L; // 暂时模拟
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null) {
            return error("请先完善租户信息");
        }

        List<HzQualification> list = qualificationService.selectQualificationListByTenantId(tenant.getTenantId());
        return success(list);
    }

    /**
     * 获取资格审核详细信息
     */
    @GetMapping("/{qualificationId}")
    public AjaxResult getInfo(@PathVariable("qualificationId") Long qualificationId) {
        HzQualification qualification = qualificationService.selectQualificationById(qualificationId);
        return success(qualification);
    }

    /**
     * 提交资格审核申请
     */
    @PostMapping("/apply")
    public AjaxResult apply(@RequestBody HzQualification qualification) {
        // TODO: 从登录态获取userId
        Long userId = 1L; // 暂时模拟
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null) {
            return error("请先完善租户信息");
        }

        // 检查是否已经提交过该类型的申请
        HzQualification existQualification = qualificationService.selectQualificationByTenantIdAndType(
            tenant.getTenantId(), qualification.getApplyType());
        if (existQualification != null && "0".equals(existQualification.getStatus())) {
            return error("您已提交过该类型的申请,请等待审核");
        }

        qualification.setTenantId(tenant.getTenantId());
        qualification.setStatus("0"); // 待审核
        return toAjax(qualificationService.insertQualification(qualification));
    }

    /**
     * 提交资格申诉（学历申诉）
     */
    @PostMapping("/appeal")
    public AjaxResult appeal(@RequestBody HzQualificationAppeal appeal) {
        // 从前端传递的参数中获取userId
        Long userId = appeal.getUserId();
        if (userId == null) {
            return error("用户未登录");
        }

        // 学历申诉不需要租户信息，直接使用用户ID
        appeal.setTenantId(userId);  // 这里tenant_id实际存储的是user_id
        appeal.setQualificationId(0L);  // 学历申诉不关联资格审核记录

        return toAjax(appealService.insertAppeal(appeal));
    }

    /**
     * 查询当前用户的申诉列表
     */
    @GetMapping("/appeal/list")
    public AjaxResult appealList(@RequestParam(required = false) Long userId) {
        // 从前端传递的参数中获取userId
        if (userId == null) {
            return error("用户未登录");
        }

        // 学历申诉不需要租户信息，tenant_id字段实际存储的是user_id
        // 使用VO方法返回包含用户信息的列表
        List<HzQualificationAppealVO> list = appealService.selectAppealVOListByUserId(userId);
        return success(list);
    }

    /**
     * 查询申诉详情
     */
    @GetMapping("/appeal/{appealId}")
    public AjaxResult appealDetail(@PathVariable("appealId") Long appealId) {
        HzQualificationAppeal appeal = appealService.selectAppealById(appealId);
        return success(appeal);
    }

    /**
     * 签署承诺书
     */
    @PostMapping("/commitment")
    public AjaxResult commitment(@RequestBody HzCommitment commitment) {
        // TODO: 从登录态获取userId
        Long userId = 1L; // 暂时模拟
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null) {
            return error("请先完善租户信息");
        }

        commitment.setTenantId(tenant.getTenantId());
        // TODO: 获取真实IP和设备信息
        commitment.setIpAddress("127.0.0.1");
        commitment.setDeviceInfo("H5");

        return toAjax(commitmentService.insertCommitment(commitment));
    }

    /**
     * 查询承诺书列表
     */
    @GetMapping("/commitment/list")
    public AjaxResult commitmentList() {
        // TODO: 从登录态获取userId
        Long userId = 1L; // 暂时模拟
        HzTenant tenant = tenantService.selectTenantByUserId(userId);
        if (tenant == null) {
            return error("请先完善租户信息");
        }

        List<HzCommitment> list = commitmentService.selectCommitmentListByTenantId(tenant.getTenantId());
        return success(list);
    }
}
