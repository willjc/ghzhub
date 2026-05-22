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
import com.ruoyi.system.domain.HzContractFiling;
import com.ruoyi.system.service.IHzContractFilingService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 合同备案 Controller（管理端）
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/gangzhu/contractFiling")
public class HzContractFilingController extends BaseController
{
    @Autowired
    private IHzContractFilingService filingService;

    @PreAuthorize("@ss.hasPermi('gangzhu:contractFiling:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzContractFiling filing)
    {
        Page<HzContractFiling> page = PageUtils.getPage();
        IPage<HzContractFiling> result = filingService.selectFilingPage(
                filing, (int) page.getCurrent(), (int) page.getSize());
        TableDataInfo data = new TableDataInfo();
        data.setCode(HttpStatus.SUCCESS);
        data.setRows(result.getRecords());
        data.setTotal(result.getTotal());
        data.setMsg("查询成功");
        return data;
    }

    @PreAuthorize("@ss.hasPermi('gangzhu:contractFiling:export')")
    @Log(title = "合同备案", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzContractFiling filing)
    {
        List<HzContractFiling> list = filingService.selectFilingList(filing);
        ExcelUtil<HzContractFiling> util = new ExcelUtil<>(HzContractFiling.class);
        util.exportExcel(response, list, "合同备案数据");
    }

    @PreAuthorize("@ss.hasPermi('gangzhu:contractFiling:query')")
    @GetMapping("/{filingId}")
    public AjaxResult getInfo(@PathVariable Long filingId)
    {
        return success(filingService.selectFilingById(filingId));
    }

    /**
     * 审批：通过/驳回
     * Body: { filingId, approveStatus(1/2), approveRemark }
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:contractFiling:approve')")
    @Log(title = "合同备案审批", businessType = BusinessType.UPDATE)
    @PostMapping("/approve")
    public AjaxResult approve(@RequestBody Map<String, Object> body)
    {
        Long filingId = body.get("filingId") != null ? Long.valueOf(body.get("filingId").toString()) : null;
        String approveStatus = body.get("approveStatus") != null ? body.get("approveStatus").toString() : null;
        String approveRemark = body.get("approveRemark") != null ? body.get("approveRemark").toString() : null;
        if (filingId == null) return error("备案ID不能为空");
        return toAjax(filingService.approveFiling(filingId, approveStatus, approveRemark));
    }

    @PreAuthorize("@ss.hasPermi('gangzhu:contractFiling:remove')")
    @Log(title = "合同备案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{filingIds}")
    public AjaxResult remove(@PathVariable Long[] filingIds)
    {
        return toAjax(filingService.deleteFilingByIds(filingIds));
    }
}
