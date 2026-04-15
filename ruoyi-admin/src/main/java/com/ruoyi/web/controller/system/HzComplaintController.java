package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzComplaint;
import com.ruoyi.system.service.IHzComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 投诉建议Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/gangzhu/complaint")
public class HzComplaintController extends BaseController
{
    @Autowired
    private IHzComplaintService complaintService;

    /**
     * 查询投诉建议列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:complaint:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzComplaint complaint)
    {
        Page<HzComplaint> page = PageUtils.getPage();
        IPage<HzComplaint> pageResult = complaintService.selectComplaintPage(complaint, (int)page.getCurrent(), (int)page.getSize());
        TableDataInfo data = new TableDataInfo();
        data.setCode(HttpStatus.SUCCESS);
        data.setRows(pageResult.getRecords());
        data.setTotal(pageResult.getTotal());
        data.setMsg("查询成功");
        return data;
    }

    /**
     * 导出投诉建议列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:complaint:export')")
    @Log(title = "投诉建议", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzComplaint complaint)
    {
        IPage<HzComplaint> page = complaintService.selectComplaintPage(complaint, 1, 10000);
        ExcelUtil<HzComplaint> util = new ExcelUtil<>(HzComplaint.class);
        util.exportExcel(response, page.getRecords(), "投诉建议数据");
    }

    /**
     * 获取投诉建议详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:complaint:query')")
    @GetMapping(value = "/{complaintId}")
    public AjaxResult getInfo(@PathVariable("complaintId") Long complaintId)
    {
        return success(complaintService.selectComplaintById(complaintId));
    }

    /**
     * 处理投诉建议
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:complaint:handle')")
    @Log(title = "处理投诉建议", businessType = BusinessType.UPDATE)
    @PutMapping("/handle")
    public AjaxResult handle(@RequestBody HzComplaint complaint)
    {
        HzComplaint existComplaint = complaintService.selectComplaintById(complaint.getComplaintId());
        if (existComplaint == null)
        {
            return error("投诉建议不存在");
        }

        // 设置处理人
        complaint.setHandleBy(SecurityUtils.getUsername());
        return toAjax(complaintService.handleComplaint(complaint));
    }

    /**
     * 删除投诉建议
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:complaint:remove')")
    @Log(title = "投诉建议", businessType = BusinessType.DELETE)
    @DeleteMapping("/{complaintIds}")
    public AjaxResult remove(@PathVariable Long[] complaintIds)
    {
        int result = 0;
        for (Long complaintId : complaintIds)
        {
            result += complaintService.deleteComplaintById(complaintId);
        }
        return toAjax(result > 0);
    }
}
