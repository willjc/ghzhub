package com.ruoyi.web.controller.h5;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzHouse;
import com.ruoyi.system.service.IHzHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 房源Controller (H5端)
 *
 * @author ruoyi
 * @date 2025-11-17
 */
@RestController
@RequestMapping("/h5/house")
public class HzHouseController extends BaseController
{
    @Autowired
    private IHzHouseService houseService;

    /**
     * 分页查询房源列表
     */
    @GetMapping("/page")
    public AjaxResult page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            HzHouse house)
    {
        // 设置状态为正常,房源状态为空置或已预订(H5端只展示可租房源)
        house.setStatus("0");
        IPage<HzHouse> page = houseService.selectHousePage(house, pageNum, pageSize);
        return AjaxResult.success(page);
    }

    /**
     * 获取房源详细信息
     */
    @GetMapping(value = "/{houseId}")
    public AjaxResult getInfo(@PathVariable("houseId") Long houseId)
    {
        // 增加浏览次数
        houseService.increaseViewCount(houseId);

        HzHouse house = houseService.selectHouseById(houseId);
        return success(house);
    }

    /**
     * 根据项目ID查询房源列表
     */
    @GetMapping("/listByProject/{projectId}")
    public AjaxResult listByProject(
            @PathVariable("projectId") Long projectId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize)
    {
        HzHouse house = new HzHouse();
        house.setProjectId(projectId);
        house.setStatus("0"); // 只查询正常状态
        IPage<HzHouse> page = houseService.selectHousePage(house, pageNum, pageSize);
        return success(page);
    }

    /**
     * 查询精选房源
     */
    @GetMapping("/featured")
    public AjaxResult getFeaturedHouses(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize)
    {
        HzHouse house = new HzHouse();
        house.setIsFeatured("1"); // 精选房源
        house.setStatus("0"); // 正常状态
        house.setHouseStatus("0"); // 空置状态
        IPage<HzHouse> page = houseService.selectHousePage(house, pageNum, pageSize);
        return success(page);
    }
}
