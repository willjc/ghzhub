package com.ruoyi.web.controller.h5;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzBatchTenant;
import com.ruoyi.system.domain.HzCommitment;
import com.ruoyi.system.domain.HzQualification;
import com.ruoyi.system.domain.HzQualificationAppeal;
import com.ruoyi.system.domain.HzQualificationAppealVO;
import com.ruoyi.system.domain.HzTenant;
import com.ruoyi.system.domain.HzUser;
import com.ruoyi.system.gov.dto.QualificationCheckResult;
import com.ruoyi.system.gov.service.QualificationCheckService;
import com.ruoyi.system.mapper.HzBatchTenantMapper;
import com.ruoyi.system.mapper.HzUserMapper;
import com.ruoyi.system.service.IHzCommitmentService;
import com.ruoyi.system.service.IHzQualificationAppealService;
import com.ruoyi.system.service.IHzQualificationService;
import com.ruoyi.system.service.IHzTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private QualificationCheckService qualificationCheckService;

    @Autowired
    private HzUserMapper userMapper;

    @Autowired
    private HzBatchTenantMapper batchTenantMapper;

    /**
     * H5 预览账号白名单（application.yml: ghz.preview-phones）
     * 命中的手机号直接视为"批量配租用户"，豁免资格校验；同时房源列表接口放行全部房源。
     */
    @Value("${ghz.preview-phones:}")
    private String previewPhones;

    /**
     * 判断当前用户是否为"批量配租"用户
     * 依据：hz_user.id_card 命中 hz_batch_tenant（未删除）任一记录
     * 返回：{ isBatchTenant: true/false }
     * 用途：前端资格守卫豁免（批量配租用户免资格校验）
     */
    @GetMapping("/is-batch-tenant")
    public AjaxResult isBatchTenant(@RequestParam Long userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("isBatchTenant", false);
        if (userId == null) {
            return success(data);
        }
        HzUser user = userMapper.selectById(userId);
        if (user == null) {
            return success(data);
        }
        // 预览账号白名单：命中则直接豁免（无需 id_card 也可通过）
        if (isPreviewPhone(user.getPhone())) {
            data.put("isBatchTenant", true);
            return success(data);
        }
        if (StringUtils.isEmpty(user.getIdCard())) {
            return success(data);
        }
        QueryWrapper<HzBatchTenant> wrapper = new QueryWrapper<>();
        wrapper.eq("id_card", user.getIdCard())
                .eq("del_flag", "0")
                .last("LIMIT 1");
        Long count = batchTenantMapper.selectCount(wrapper);
        data.put("isBatchTenant", count != null && count > 0);
        return success(data);
    }

    /**
     * 判断手机号是否在预览白名单（逗号分隔）
     */
    private boolean isPreviewPhone(String phone) {
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(previewPhones)) {
            return false;
        }
        for (String p : previewPhones.split(",")) {
            if (phone.trim().equals(p.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查询资格校验状态（是否已校验 / 是否通过 / 各项结果）
     * - 未校验：checked=false
     * - 已校验：返回最近一次快照（passed / items / failReasons / lastCheckTime）
     */
    @GetMapping("/status")
    public AjaxResult status(@RequestParam Long userId) {
        if (userId == null) {
            return error("用户未登录");
        }
        QualificationCheckResult result = qualificationCheckService.getStatus(userId);
        return success(result);
    }

    /**
     * 触发一次资格校验（同步），失败时返回模糊的失败原因
     */
    @PostMapping("/check")
    public AjaxResult check(@RequestParam Long userId) {
        if (userId == null) {
            return error("用户未登录");
        }
        try {
            QualificationCheckResult result = qualificationCheckService.check(userId);
            return success(result);
        } catch (IllegalStateException e) {
            return error(e.getMessage());
        } catch (Exception e) {
            return error("资格校验失败，请稍后重试");
        }
    }

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
    @GetMapping("/{qualificationId:\\d+}")
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
    @GetMapping("/appeal/{appealId:\\d+}")
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
