package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzQualification;
import com.ruoyi.system.service.IHzQualificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 资格审核Controller
 *
 * @author ruoyi
 */
@RestController("adminQualificationController")
@RequestMapping("/system/qualification")
public class HzQualificationController extends BaseController
{
    @Autowired
    private IHzQualificationService qualificationService;

    /**
     * 查询资格审核列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:qualification:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzQualification qualification)
    {
        startPage();
        List<HzQualification> list = qualificationService.selectQualificationList(qualification);
        return getDataTable(list);
    }

    /**
     * 导出资格审核列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:qualification:export')")
    @Log(title = "资格审核", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzQualification qualification)
    {
        List<HzQualification> list = qualificationService.selectQualificationList(qualification);
        ExcelUtil<HzQualification> util = new ExcelUtil<HzQualification>(HzQualification.class);
        util.exportExcel(response, list, "资格审核数据");
    }

    /**
     * 获取资格审核详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:qualification:query')")
    @GetMapping(value = "/{qualificationId}")
    public AjaxResult getInfo(@PathVariable("qualificationId") Long qualificationId)
    {
        return success(qualificationService.selectQualificationById(qualificationId));
    }

    /**
     * 新增资格审核
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:qualification:add')")
    @Log(title = "资格审核", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HzQualification qualification)
    {
        return toAjax(qualificationService.insertQualification(qualification));
    }

    /**
     * 修改资格审核
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:qualification:edit')")
    @Log(title = "资格审核", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzQualification qualification)
    {
        return toAjax(qualificationService.updateQualification(qualification));
    }

    /**
     * 删除资格审核
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:qualification:remove')")
    @Log(title = "资格审核", businessType = BusinessType.DELETE)
    @DeleteMapping("/{qualificationIds}")
    public AjaxResult remove(@PathVariable Long[] qualificationIds)
    {
        for (Long qualificationId : qualificationIds) {
            qualificationService.deleteQualificationById(qualificationId);
        }
        return success();
    }
}
