package com.ruoyi.system.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzHouseType;
import com.ruoyi.system.domain.HzHouseTypeImage;
import com.ruoyi.system.service.IHzHouseTypeService;
import com.ruoyi.system.service.IHzHouseTypeImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 户型Controller
 *
 * @author ruoyi
 * @date 2025-11-18
 */
@RestController
@RequestMapping("/gangzhu/houseType")
public class HzHouseTypeController extends BaseController
{
    @Autowired
    private IHzHouseTypeService hzHouseTypeService;

    @Autowired
    private IHzHouseTypeImageService hzHouseTypeImageService;

    /**
     * 查询户型列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:houseType:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzHouseType hzHouseType)
    {
        startPage();
        List<HzHouseType> list = hzHouseTypeService.selectHouseTypeList(hzHouseType);
        return getDataTable(list);
    }

    /**
     * 导出户型列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:houseType:export')")
    @Log(title = "户型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzHouseType hzHouseType)
    {
        List<HzHouseType> list = hzHouseTypeService.selectHouseTypeList(hzHouseType);
        ExcelUtil<HzHouseType> util = new ExcelUtil<HzHouseType>(HzHouseType.class);
        util.exportExcel(response, list, "户型数据");
    }

    /**
     * 获取户型详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:houseType:query')")
    @GetMapping(value = "/{houseTypeId}")
    public AjaxResult getInfo(@PathVariable("houseTypeId") Long houseTypeId)
    {
        return success(hzHouseTypeService.selectHouseTypeById(houseTypeId));
    }

    /**
     * 新增户型
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:houseType:add')")
    @Log(title = "户型", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HzHouseType hzHouseType)
    {
        int result = hzHouseTypeService.insertHouseType(hzHouseType);
        if (result > 0) {
            // 返回新增的户型ID,供前端保存图片使用
            return AjaxResult.success("新增成功", hzHouseType.getHouseTypeId());
        }
        return AjaxResult.error("新增失败");
    }

    /**
     * 修改户型
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:houseType:edit')")
    @Log(title = "户型", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzHouseType hzHouseType)
    {
        return toAjax(hzHouseTypeService.updateHouseType(hzHouseType));
    }

    /**
     * 删除户型
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:houseType:remove')")
    @Log(title = "户型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{houseTypeIds}")
    public AjaxResult remove(@PathVariable Long[] houseTypeIds)
    {
        return toAjax(hzHouseTypeService.deleteHouseTypeByIds(houseTypeIds));
    }

    /**
     * 查询所有可用户型列表(用于下拉选择)
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:houseType:list')")
    @GetMapping("/listAll")
    public AjaxResult listAll()
    {
        HzHouseType query = new HzHouseType();
        query.setStatus("0"); // 只查询正常状态的户型
        List<HzHouseType> list = hzHouseTypeService.selectHouseTypeList(query);

        // 为每个户型构建详细描述
        for (HzHouseType houseType : list)
        {
            StringBuilder detail = new StringBuilder(houseType.getHouseTypeName());
            detail.append("(");

            if (houseType.getBedroomCount() != null && houseType.getBedroomCount() > 0)
            {
                detail.append(houseType.getBedroomCount()).append("室");
            }
            if (houseType.getLivingroomCount() != null && houseType.getLivingroomCount() > 0)
            {
                detail.append(houseType.getLivingroomCount()).append("厅");
            }
            if (houseType.getBathroomCount() != null && houseType.getBathroomCount() > 0)
            {
                detail.append(houseType.getBathroomCount()).append("卫");
            }
            if (houseType.getKitchenCount() != null && houseType.getKitchenCount() > 0)
            {
                detail.append(houseType.getKitchenCount()).append("厨");
            }
            if (houseType.getBalconyCount() != null && houseType.getBalconyCount() > 0)
            {
                detail.append(houseType.getBalconyCount()).append("阳台");
            }

            detail.append(")");
            houseType.setRemark(detail.toString()); // 使用remark字段临时存储详细描述
        }

        return success(list);
    }

    /**
     * 查询户型图片列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:houseType:query')")
    @GetMapping("/{houseTypeId}/images")
    public AjaxResult getImages(@PathVariable("houseTypeId") Long houseTypeId)
    {
        List<HzHouseTypeImage> list = hzHouseTypeImageService.selectImageListByHouseTypeId(houseTypeId);
        return success(list);
    }

    /**
     * 批量保存户型图片
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:houseType:edit')")
    @Log(title = "户型图片", businessType = BusinessType.UPDATE)
    @PostMapping("/{houseTypeId}/images")
    public AjaxResult saveImages(@PathVariable("houseTypeId") Long houseTypeId,
                                  @RequestBody List<HzHouseTypeImage> imageList)
    {
        return toAjax(hzHouseTypeImageService.batchSaveImages(houseTypeId, imageList));
    }

    /**
     * 删除户型图片
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:houseType:edit')")
    @Log(title = "户型图片", businessType = BusinessType.DELETE)
    @DeleteMapping("/images/{imageId}")
    public AjaxResult removeImage(@PathVariable("imageId") Long imageId)
    {
        return toAjax(hzHouseTypeImageService.deleteImageById(imageId));
    }
}
