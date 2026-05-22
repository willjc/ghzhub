package com.ruoyi.web.controller.h5;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.HzSubsidyApply;
import com.ruoyi.system.service.IHzSubsidyApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * H5端 代购补贴申请 Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/app/subsidyApply")
public class HzSubsidyApplyAppController extends BaseController
{
    @Autowired
    private IHzSubsidyApplyService applyService;

    @GetMapping("/myList")
    public AjaxResult myList(@RequestParam("tenantId") Long tenantId,
                             @RequestParam(required = false) String approveStatus)
    {
        if (tenantId == null) return error("租户ID不能为空");
        List<HzSubsidyApply> list = applyService.selectMyApplies(tenantId, approveStatus);
        return success(list);
    }

    @GetMapping("/detail/{applyId}")
    public AjaxResult detail(@PathVariable Long applyId)
    {
        HzSubsidyApply apply = applyService.selectApplyById(applyId);
        if (apply == null) return error("申请不存在");
        return success(apply);
    }

    /**
     * 提交申请（前端先调 /h5/app/commitment/sign 拿到 commitmentId 再调本接口）
     */
    @Log(title = "提交代购补贴申请", businessType = BusinessType.INSERT)
    @PostMapping("/submit")
    public AjaxResult submit(@RequestBody HzSubsidyApply apply)
    {
        return toAjax(applyService.submitApply(apply));
    }
}
