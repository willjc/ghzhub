package com.ruoyi.web.controller.h5;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.HzContractFiling;
import com.ruoyi.system.service.IHzContractFilingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * H5端 合同备案 Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/app/contractFiling")
public class HzContractFilingAppController extends BaseController
{
    @Autowired
    private IHzContractFilingService filingService;

    /**
     * 我的备案列表
     */
    @GetMapping("/myList")
    public AjaxResult myList(@RequestParam("tenantId") Long tenantId,
                             @RequestParam(required = false) String approveStatus)
    {
        if (tenantId == null) return error("租户ID不能为空");
        List<HzContractFiling> list = filingService.selectMyFilings(tenantId, approveStatus);
        return success(list);
    }

    /**
     * 备案详情
     */
    @GetMapping("/detail/{filingId}")
    public AjaxResult detail(@PathVariable Long filingId)
    {
        HzContractFiling filing = filingService.selectFilingById(filingId);
        if (filing == null) return error("备案不存在");
        return success(filing);
    }

    /**
     * 提交备案
     */
    @Log(title = "提交合同备案", businessType = BusinessType.INSERT)
    @PostMapping("/submit")
    public AjaxResult submit(@RequestBody HzContractFiling filing)
    {
        return toAjax(filingService.submitFiling(filing));
    }
}
