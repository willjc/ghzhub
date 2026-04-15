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
import com.ruoyi.system.domain.HzRepair;
import com.ruoyi.system.service.IHzRepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 物业报修Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/gangzhu/repair")
public class HzRepairController extends BaseController
{
    @Autowired
    private IHzRepairService repairService;

    /**
     * 查询物业报修列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:repair:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzRepair repair)
    {
        Page<HzRepair> page = PageUtils.getPage();
        IPage<HzRepair> pageResult = repairService.selectRepairPage(repair, (int)page.getCurrent(), (int)page.getSize());
        TableDataInfo data = new TableDataInfo();
        data.setCode(HttpStatus.SUCCESS);
        data.setRows(pageResult.getRecords());
        data.setTotal(pageResult.getTotal());
        data.setMsg("查询成功");
        return data;
    }

    /**
     * 导出物业报修列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:repair:export')")
    @Log(title = "物业报修", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzRepair repair)
    {
        IPage<HzRepair> page = repairService.selectRepairPage(repair, 1, 10000);
        ExcelUtil<HzRepair> util = new ExcelUtil<>(HzRepair.class);
        util.exportExcel(response, page.getRecords(), "物业报修数据");
    }

    /**
     * 获取物业报修详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:repair:query')")
    @GetMapping(value = "/{repairId}")
    public AjaxResult getInfo(@PathVariable("repairId") Long repairId)
    {
        return success(repairService.selectRepairById(repairId));
    }

    /**
     * 处理物业报修
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:repair:handle')")
    @Log(title = "处理物业报修", businessType = BusinessType.UPDATE)
    @PutMapping("/handle")
    public AjaxResult handle(@RequestBody HzRepair repair)
    {
        HzRepair existRepair = repairService.selectRepairById(repair.getRepairId());
        if (existRepair == null)
        {
            return error("报修记录不存在");
        }

        // 设置处理人
        repair.setHandleBy(SecurityUtils.getUsername());
        return toAjax(repairService.handleRepair(repair));
    }

    /**
     * 删除物业报修
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:repair:remove')")
    @Log(title = "物业报修", businessType = BusinessType.DELETE)
    @DeleteMapping("/{repairIds}")
    public AjaxResult remove(@PathVariable Long[] repairIds)
    {
        int result = 0;
        for (Long repairId : repairIds)
        {
            result += repairService.deleteRepairById(repairId);
        }
        return toAjax(result > 0);
    }
}
