package com.ruoyi.web.controller.h5;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.PageUtils;
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
        // 只查询状态正常的政策
        policy.setStatus("0");
        Page<HzPolicy> page = PageUtils.getPage();
        IPage<HzPolicy> pageResult = policyService.selectPolicyPage(page, policy);
        TableDataInfo data = new TableDataInfo();
        data.setCode(HttpStatus.SUCCESS);
        data.setRows(pageResult.getRecords());
        data.setTotal(pageResult.getTotal());
        data.setMsg("查询成功");
        return data;
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
