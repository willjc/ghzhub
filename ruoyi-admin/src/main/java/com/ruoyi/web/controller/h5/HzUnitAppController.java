package com.ruoyi.web.controller.h5;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzUnit;
import com.ruoyi.system.mapper.HzUnitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * H5用户端 - 单元API
 */
@RestController
@RequestMapping("/h5/app/unit")
public class HzUnitAppController extends BaseController {

    @Autowired
    private HzUnitMapper unitMapper;

    /**
     * 根据楼栋ID获取单元列表
     * @param buildingId 楼栋ID
     * @return 单元列表 [{ id, name }]
     */
    @GetMapping("/list/{buildingId}")
    public AjaxResult listByBuilding(@PathVariable Long buildingId) {
        QueryWrapper<HzUnit> wrapper = new QueryWrapper<>();
        wrapper.eq("building_id", buildingId)
                .eq("status", "0")  // 只查询正常状态的单元
                .eq("del_flag", "0")  // 未删除
                .orderByAsc("sort_order", "unit_name");

        List<HzUnit> units = unitMapper.selectList(wrapper);

        // 转换为简化的前端格式
        List<Map<String, Object>> result = units.stream().map(unit -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", unit.getUnitId());
            map.put("name", unit.getUnitName());
            return map;
        }).collect(Collectors.toList());

        return AjaxResult.success(result);
    }
}
