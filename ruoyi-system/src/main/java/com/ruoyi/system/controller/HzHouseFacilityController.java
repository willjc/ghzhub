package com.ruoyi.system.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.HzHouse;
import com.ruoyi.system.domain.HzHouseFacility;
import com.ruoyi.system.domain.HzHouseTypeFacility;
import com.ruoyi.system.mapper.HzHouseMapper;
import com.ruoyi.system.service.IHzHouseFacilityService;
import com.ruoyi.system.service.IHzHouseTypeFacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
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

    @Autowired
    private HzHouseMapper hzHouseMapper;

    @Autowired
    private IHzHouseTypeFacilityService hzHouseTypeFacilityService;

    /**
     * 查询房源设施列表（三级fallback：房源设施表 → 户型设施表 → 旧字段）
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:house:list')")
    @GetMapping("/list/{houseId}")
    public AjaxResult list(@PathVariable("houseId") Long houseId)
    {
        // 1. 优先查询房源设施新表
        List<HzHouseFacility> list = hzHouseFacilityService.selectByHouseId(houseId);
        if (list != null && !list.isEmpty()) {
            return success(list);
        }

        // 2. 回退：通过房源的户型ID查询户型设施表
        HzHouse house = hzHouseMapper.selectById(houseId);
        if (house != null && house.getHouseTypeId() != null) {
            List<HzHouseTypeFacility> typeList = hzHouseTypeFacilityService.selectByHouseTypeId(house.getHouseTypeId());
            if (typeList != null && !typeList.isEmpty()) {
                return success(typeList);
            }
        }

        // 3. 再回退：查询旧 hz_house.facilities 字段
        if (house != null && house.getFacilities() != null && !house.getFacilities().isEmpty()) {
            String oldFacilities = house.getFacilities();
            List<Map<String, Object>> result = new ArrayList<>();
            String[] items = oldFacilities.split("[,，、]");
            for (String item : items) {
                String name = item.trim();
                if (!name.isEmpty()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("facilityName", name);
                    map.put("facilityCategory", "其他");
                    map.put("quantity", 1);
                    map.put("itemStatus", "完好");
                    map.put("remark", "");
                    result.add(map);
                }
            }
            return success(result);
        }

        return success(new ArrayList<>());
    }

    /**
     * 批量保存房源设施
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:house:edit')")
    @Log(title = "房源设施", businessType = BusinessType.UPDATE)
    @PostMapping("/batchSave")
    public AjaxResult batchSave(@RequestBody HouseFacilitySaveRequest request)
    {
        if (request.getFacilities() == null) {
            return error("设施列表不能为空");
        }
        hzHouseFacilityService.batchSave(request.getHouseId(), request.getFacilities());
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
        private List<HzHouseFacility> facilities;

        public Long getHouseId()
        {
            return houseId;
        }

        public void setHouseId(Long houseId)
        {
            this.houseId = houseId;
        }

        public List<HzHouseFacility> getFacilities()
        {
            return facilities;
        }

        public void setFacilities(List<HzHouseFacility> facilities)
        {
            this.facilities = facilities;
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
