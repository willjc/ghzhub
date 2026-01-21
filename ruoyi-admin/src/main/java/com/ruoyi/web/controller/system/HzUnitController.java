package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzUnit;
import com.ruoyi.system.service.IHzUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 单元管理Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/unit")
public class HzUnitController extends BaseController {

    @Autowired
    private IHzUnitService unitService;

    /**
     * 查询单元列表
     */
    @GetMapping("/list")
    public TableDataInfo list(HzUnit unit) {
        // 使用MyBatis-Plus分页
        com.ruoyi.common.core.page.PageDomain pageDomain = com.ruoyi.common.core.page.TableSupport.buildPageRequest();
        int pageNum = pageDomain.getPageNum();
        int pageSize = pageDomain.getPageSize();

        com.baomidou.mybatisplus.core.metadata.IPage<HzUnit> page = unitService.selectUnitPage(unit, pageNum, pageSize);

        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(com.ruoyi.common.constant.HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(page.getRecords());
        rspData.setTotal(page.getTotal());
        return rspData;
    }

    /**
     * 导出单元列表
     */
    @Log(title = "单元管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzUnit unit) {
        List<HzUnit> list = unitService.selectUnitList(unit);
        ExcelUtil<HzUnit> util = new ExcelUtil<HzUnit>(HzUnit.class);
        util.exportExcel(response, list, "单元数据");
    }

    /**
     * 获取单元详细信息
     */
    @GetMapping(value = "/{unitId}")
    public AjaxResult getInfo(@PathVariable("unitId") Long unitId) {
        return success(unitService.selectUnitById(unitId));
    }

    /**
     * 新增单元
     */
    @Log(title = "单元管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HzUnit unit) {
        return toAjax(unitService.insertUnit(unit));
    }

    /**
     * 修改单元
     */
    @Log(title = "单元管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzUnit unit) {
        return toAjax(unitService.updateUnit(unit));
    }

    /**
     * 删除单元
     */
    @Log(title = "单元管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{unitIds}")
    public AjaxResult remove(@PathVariable Long[] unitIds) {
        int count = 0;
        for (Long unitId : unitIds) {
            count += unitService.deleteUnitById(unitId);
        }
        return toAjax(count);
    }
}
