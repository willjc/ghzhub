package com.ruoyi.system.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.HzHouseFacility;
import com.ruoyi.system.service.IHzHouseFacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房源设施Controller
 *
 * @author ruoyi
 * @date 2026-04-17
 */
@RestController
@RequestMapping("/gangzhu/houseFacility")
public class HzHouseFacilityController extends BaseController
{
    @Autowired
    private IHzHouseFacilityService hzHouseFacilityService;

    /**
     * 查询房源设施列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:house:list')")
    @GetMapping("/list/{houseId}")
    public AjaxResult list(@PathVariable("houseId") Long houseId)
    {
        List<HzHouseFacility> list = hzHouseFacilityService.selectByHouseId(houseId);
        return success(list);
    }

    /**
     * 批量保存房源设施
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:house:edit')")
    @Log(title = "房源设施", businessType = BusinessType.UPDATE)
    @PostMapping("/batchSave")
    public AjaxResult batchSave(@RequestBody HouseFacilitySaveRequest request)
    {
        hzHouseFacilityService.batchSave(request.getHouseId(), request.getList());
        return success();
    }

    /**
     * 从户型拉取设施到房源
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:house:edit')")
    @Log(title = "房源设施", businessType = BusinessType.UPDATE)
    @PostMapping("/pullFromType")
    public AjaxResult pullFromType(@RequestBody PullFromTypeRequest request)
    {
        hzHouseFacilityService.pullFromHouseType(request.getHouseId(), request.getHouseTypeId());
        return success();
    }

    /**
     * 批量保存请求体
     */
    public static class HouseFacilitySaveRequest
    {
        private Long houseId;
        private List<HzHouseFacility> list;

        public Long getHouseId()
        {
            return houseId;
        }

        public void setHouseId(Long houseId)
        {
            this.houseId = houseId;
        }

        public List<HzHouseFacility> getList()
        {
            return list;
        }

        public void setList(List<HzHouseFacility> list)
        {
            this.list = list;
        }
    }

    /**
     * 从户型拉取设施请求体
     */
    public static class PullFromTypeRequest
    {
        private Long houseId;
        private Long houseTypeId;

        public Long getHouseId()
        {
            return houseId;
        }

        public void setHouseId(Long houseId)
        {
            this.houseId = houseId;
        }

        public Long getHouseTypeId()
        {
            return houseTypeId;
        }

        public void setHouseTypeId(Long houseTypeId)
        {
            this.houseTypeId = houseTypeId;
        }
    }
}
