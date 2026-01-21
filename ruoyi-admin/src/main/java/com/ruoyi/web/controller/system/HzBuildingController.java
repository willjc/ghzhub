package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzBuilding;
import com.ruoyi.system.service.IHzBuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 楼栋管理Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/building")
public class HzBuildingController extends BaseController {

    @Autowired
    private IHzBuildingService buildingService;

    /**
     * 查询楼栋列表
     */
    @GetMapping("/list")
    public TableDataInfo list(HzBuilding building) {
        startPage();
        List<HzBuilding> list = buildingService.selectBuildingList(building);
        return getDataTable(list);
    }

    /**
     * 导出楼栋列表
     */
    @Log(title = "楼栋管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzBuilding building) {
        List<HzBuilding> list = buildingService.selectBuildingList(building);
        ExcelUtil<HzBuilding> util = new ExcelUtil<HzBuilding>(HzBuilding.class);
        util.exportExcel(response, list, "楼栋数据");
    }

    /**
     * 获取楼栋详细信息
     */
    @GetMapping(value = "/{buildingId}")
    public AjaxResult getInfo(@PathVariable("buildingId") Long buildingId) {
        return success(buildingService.selectBuildingById(buildingId));
    }

    /**
     * 新增楼栋
     */
    @Log(title = "楼栋管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HzBuilding building) {
        return toAjax(buildingService.insertBuilding(building));
    }

    /**
     * 修改楼栋
     */
    @Log(title = "楼栋管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzBuilding building) {
        return toAjax(buildingService.updateBuilding(building));
    }

    /**
     * 删除楼栋
     */
    @Log(title = "楼栋管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{buildingIds}")
    public AjaxResult remove(@PathVariable Long[] buildingIds) {
        int count = 0;
        for (Long buildingId : buildingIds) {
            count += buildingService.deleteBuildingById(buildingId);
        }
        return toAjax(count);
    }
}
