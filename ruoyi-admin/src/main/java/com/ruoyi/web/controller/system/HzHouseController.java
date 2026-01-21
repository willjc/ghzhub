package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzHouse;
import com.ruoyi.system.service.IHzHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 房源管理Controller
 *
 * @author ruoyi
 */
@RestController("adminHouseController")
@RequestMapping("/system/house")
public class HzHouseController extends BaseController
{
    @Autowired
    private IHzHouseService houseService;

    /**
     * 查询房源列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:house:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzHouse house)
    {
        // 不需要调用 startPage()，分页参数在 Service 层获取
        IPage<HzHouse> page = houseService.selectHouseList(house);

        // 构造返回数据
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setMsg("查询成功");
        rspData.setRows(page.getRecords());
        rspData.setTotal(page.getTotal());
        return rspData;
    }

    /**
     * 导出房源列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:house:export')")
    @Log(title = "房源管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzHouse house)
    {
        IPage<HzHouse> page = houseService.selectHouseList(house);
        List<HzHouse> list = page.getRecords();
        ExcelUtil<HzHouse> util = new ExcelUtil<HzHouse>(HzHouse.class);
        util.exportExcel(response, list, "房源数据");
    }

    /**
     * 获取房源详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:house:query')")
    @GetMapping(value = "/{houseId}")
    public AjaxResult getInfo(@PathVariable("houseId") Long houseId)
    {
        return success(houseService.selectHouseById(houseId));
    }

    /**
     * 获取房源详细信息（包含关联数据）
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:house:query')")
    @GetMapping(value = "/detail/{houseId}")
    public AjaxResult getHouseDetail(@PathVariable("houseId") Long houseId)
    {
        return success(houseService.selectHouseDetailById(houseId));
    }

    /**
     * 新增房源
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:house:add')")
    @Log(title = "房源管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HzHouse house)
    {
        int result = houseService.insertHouse(house);
        if (result > 0) {
            // 返回新插入的房源ID
            return success(house.getHouseId());
        }
        return error();
    }

    /**
     * 修改房源
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:house:edit')")
    @Log(title = "房源管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzHouse house)
    {
        return toAjax(houseService.updateHouse(house));
    }

    /**
     * 删除房源
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:house:remove')")
    @Log(title = "房源管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{houseIds}")
    public AjaxResult remove(@PathVariable Long[] houseIds)
    {
        int count = 0;
        for (Long houseId : houseIds)
        {
            count += houseService.deleteHouseById(houseId);
        }
        return toAjax(count);
    }

    /**
     * 下载房源导入模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<HzHouse> util = new ExcelUtil<HzHouse>(HzHouse.class);
        util.importTemplateExcel(response, "房源数据");
    }

    /**
     * 导入房源数据
     */
    @Log(title = "房源管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('gangzhu:house:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<HzHouse> util = new ExcelUtil<HzHouse>(HzHouse.class);
        List<HzHouse> houseList = util.importExcel(file.getInputStream());
        String message = houseService.importHouse(houseList, updateSupport);
        return success(message);
    }

    /**
     * 获取房源图片列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:house:query')")
    @GetMapping("/{houseId}/images")
    public AjaxResult getHouseImages(@PathVariable("houseId") Long houseId)
    {
        return success(houseService.getHouseImages(houseId));
    }

    /**
     * 保存房源图片
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:house:edit')")
    @Log(title = "房源管理", businessType = BusinessType.UPDATE)
    @PostMapping("/images")
    public AjaxResult saveHouseImages(@RequestBody Map<String, Object> data)
    {
        Long houseId = Long.valueOf(data.get("houseId").toString());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> images = (List<Map<String, Object>>) data.get("images");
        houseService.saveHouseImages(houseId, images);
        return success();
    }

    /**
     * 获取房源VR列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:house:query')")
    @GetMapping("/{houseId}/vrs")
    public AjaxResult getHouseVrs(@PathVariable("houseId") Long houseId)
    {
        return success(houseService.getHouseVrs(houseId));
    }

    /**
     * 保存房源VR
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:house:edit')")
    @Log(title = "房源管理", businessType = BusinessType.UPDATE)
    @PostMapping("/vrs")
    public AjaxResult saveHouseVrs(@RequestBody Map<String, Object> data)
    {
        Long houseId = Long.valueOf(data.get("houseId").toString());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> vrs = (List<Map<String, Object>>) data.get("vrs");
        houseService.saveHouseVrs(houseId, vrs);
        return success();
    }
}
