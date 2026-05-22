package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzSubsidyApply;
import com.ruoyi.system.service.IHzSubsidyApplyService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 代购补贴申请 Controller（管理端）
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/gangzhu/subsidyApply")
public class HzSubsidyApplyController extends BaseController
{
    @Autowired
    private IHzSubsidyApplyService applyService;

    @PreAuthorize("@ss.hasPermi('gangzhu:subsidyApply:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzSubsidyApply apply)
    {
        Page<HzSubsidyApply> page = PageUtils.getPage();
        IPage<HzSubsidyApply> result = applyService.selectApplyPage(
                apply, (int) page.getCurrent(), (int) page.getSize());
        TableDataInfo data = new TableDataInfo();
        data.setCode(HttpStatus.SUCCESS);
        data.setRows(result.getRecords());
        data.setTotal(result.getTotal());
        data.setMsg("查询成功");
        return data;
    }

    @PreAuthorize("@ss.hasPermi('gangzhu:subsidyApply:export')")
    @Log(title = "代购补贴申请", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzSubsidyApply apply)
    {
        List<HzSubsidyApply> list = applyService.selectApplyList(apply);
        ExcelUtil<HzSubsidyApply> util = new ExcelUtil<>(HzSubsidyApply.class);
        util.exportExcel(response, list, "代购补贴申请数据");
    }

    @PreAuthorize("@ss.hasPermi('gangzhu:subsidyApply:query')")
    @GetMapping("/{applyId}")
    public AjaxResult getInfo(@PathVariable Long applyId)
    {
        return success(applyService.selectApplyById(applyId));
    }

    /**
     * 审批：通过/驳回
     * Body: { applyId, approveStatus(1/2), approveRemark }
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:subsidyApply:approve')")
    @Log(title = "代购补贴审批", businessType = BusinessType.UPDATE)
    @PostMapping("/approve")
    public AjaxResult approve(@RequestBody Map<String, Object> body)
    {
        Long applyId = body.get("applyId") != null ? Long.valueOf(body.get("applyId").toString()) : null;
        String approveStatus = body.get("approveStatus") != null ? body.get("approveStatus").toString() : null;
        String approveRemark = body.get("approveRemark") != null ? body.get("approveRemark").toString() : null;
        if (applyId == null) return error("申请ID不能为空");
        return toAjax(applyService.approveApply(applyId, approveStatus, approveRemark));
    }

    @PreAuthorize("@ss.hasPermi('gangzhu:subsidyApply:remove')")
    @Log(title = "代购补贴申请", businessType = BusinessType.DELETE)
    @DeleteMapping("/{applyIds}")
    public AjaxResult remove(@PathVariable Long[] applyIds)
    {
        return toAjax(applyService.deleteApplyByIds(applyIds));
    }
}
