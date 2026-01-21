package com.ruoyi.gangzhu.activity.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.gangzhu.activity.domain.HzActivity;
import com.ruoyi.gangzhu.activity.service.IHzActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 人才家园活动Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/gangzhu/activity")
public class HzActivityController extends BaseController {

    @Autowired
    private IHzActivityService activityService;

    /**
     * 查询人才家园活动列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:activity:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzActivity activity) {
        startPage();
        List<HzActivity> list = activityService.selectActivityList(activity);
        return getDataTable(list);
    }

    /**
     * 导出人才家园活动列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:activity:export')")
    @Log(title = "人才家园活动", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzActivity activity) {
        List<HzActivity> list = activityService.selectActivityList(activity);
        ExcelUtil<HzActivity> util = new ExcelUtil<HzActivity>(HzActivity.class);
        util.exportExcel(response, list, "人才家园活动数据");
    }

    /**
     * 获取人才家园活动详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:activity:query')")
    @GetMapping(value = "/{activityId}")
    public AjaxResult getInfo(@PathVariable("activityId") Long activityId) {
        return success(activityService.getById(activityId));
    }

    /**
     * 新增人才家园活动
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:activity:add')")
    @Log(title = "人才家园活动", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HzActivity activity) {
        return toAjax(activityService.insertActivity(activity));
    }

    /**
     * 修改人才家园活动
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:activity:edit')")
    @Log(title = "人才家园活动", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzActivity activity) {
        return toAjax(activityService.updateActivity(activity));
    }

    /**
     * 删除人才家园活动
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:activity:remove')")
    @Log(title = "人才家园活动", businessType = BusinessType.DELETE)
    @DeleteMapping("/{activityIds}")
    public AjaxResult remove(@PathVariable Long[] activityIds) {
        return toAjax(activityService.deleteActivityByIds(activityIds));
    }

    /**
     * 增加浏览次数
     */
    @PostMapping("/view/{activityId}")
    public AjaxResult increaseViewCount(@PathVariable Long activityId) {
        return toAjax(activityService.increaseViewCount(activityId));
    }
}
