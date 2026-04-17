package com.ruoyi.system.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.HzFacilityItem;
import com.ruoyi.system.service.IHzFacilityItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设施物品Controller
 *
 * @author ruoyi
 * @date 2026-04-17
 */
@RestController
@RequestMapping("/gangzhu/facilityItem")
public class HzFacilityItemController extends BaseController
{
    @Autowired
    private IHzFacilityItemService hzFacilityItemService;

    /**
     * 查询设施物品列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:facilityItem:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzFacilityItem query)
    {
        startPage();
        List<HzFacilityItem> list = hzFacilityItemService.selectFacilityItemList(query);
        return getDataTable(list);
    }
}
