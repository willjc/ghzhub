package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.gangzhu.policy.domain.HzPolicy;
import com.ruoyi.gangzhu.policy.service.IHzPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端 - 政策文件Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/policy")
public class HzPolicyAppController extends BaseController {

    @Autowired
    private IHzPolicyService policyService;

    /**
     * 查询政策文件列表（用户端）
     * 只返回状态为正常的政策
     */
    @GetMapping("/list")
    public TableDataInfo list(HzPolicy policy) {
        startPage();
        // 只查询状态正常的政策
        policy.setStatus("0");
        List<HzPolicy> list = policyService.selectPolicyList(policy);
        return getDataTable(list);
    }

    /**
     * 获取政策文件详细信息（用户端）
     */
    @GetMapping(value = "/{policyId}")
    public AjaxResult getInfo(@PathVariable("policyId") Long policyId) {
        HzPolicy policy = policyService.getById(policyId);
        if (policy == null) {
            return error("政策不存在");
        }
        // 检查政策状态
        if (!"0".equals(policy.getStatus())) {
            return error("政策已停用");
        }
        // 增加浏览次数
        policyService.increaseViewCount(policyId);
        return success(policy);
    }
}
