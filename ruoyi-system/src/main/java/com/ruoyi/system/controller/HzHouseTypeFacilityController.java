package com.ruoyi.system.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.HzHouseTypeFacility;
import com.ruoyi.system.service.IHzHouseTypeFacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 户型设施配置Controller
 *
 * @author ruoyi
 * @date 2026-04-17
 */
@RestController
@RequestMapping("/gangzhu/houseTypeFacility")
public class HzHouseTypeFacilityController extends BaseController
{
    @Autowired
    private IHzHouseTypeFacilityService hzHouseTypeFacilityService;

    /**
     * 查询户型设施配置列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:houseType:list')")
    @GetMapping("/list/{houseTypeId}")
    public AjaxResult list(@PathVariable("houseTypeId") Long houseTypeId)
    {
        List<HzHouseTypeFacility> list = hzHouseTypeFacilityService.selectByHouseTypeId(houseTypeId);
        return success(list);
    }

    /**
     * 批量保存户型设施配置
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:houseType:edit')")
    @Log(title = "户型设施配置", businessType = BusinessType.UPDATE)
    @PostMapping("/batchSave")
    public AjaxResult batchSave(@RequestBody HouseTypeFacilitySaveRequest request)
    {
        hzHouseTypeFacilityService.batchSave(request.getHouseTypeId(), request.getList());
        return success();
    }

    /**
     * 批量保存请求体
     */
    public static class HouseTypeFacilitySaveRequest
    {
        private Long houseTypeId;
        private List<HzHouseTypeFacility> list;

        public Long getHouseTypeId()
        {
            return houseTypeId;
        }

        public void setHouseTypeId(Long houseTypeId)
        {
            this.houseTypeId = houseTypeId;
        }

        public List<HzHouseTypeFacility> getList()
        {
            return list;
        }

        public void setList(List<HzHouseTypeFacility> list)
        {
            this.list = list;
        }
    }
}
