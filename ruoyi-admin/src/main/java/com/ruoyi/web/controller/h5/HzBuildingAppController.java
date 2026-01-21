package com.ruoyi.web.controller.h5;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzBuilding;
import com.ruoyi.system.mapper.HzBuildingMapper;
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
 * H5用户端 - 楼栋API
 */
@RestController
@RequestMapping("/h5/app/building")
public class HzBuildingAppController extends BaseController {

    @Autowired
    private HzBuildingMapper buildingMapper;

    /**
     * 根据项目ID获取楼栋列表
     * @param projectId 项目ID
     * @return 楼栋列表 [{ id, name }]
     */
    @GetMapping("/list/{projectId}")
    public AjaxResult listByProject(@PathVariable Long projectId) {
        QueryWrapper<HzBuilding> wrapper = new QueryWrapper<>();
        wrapper.eq("project_id", projectId)
                .eq("status", "0")  // 只查询正常状态的楼栋
                .eq("del_flag", "0")  // 未删除
                .orderByAsc("sort_order", "building_name");

        List<HzBuilding> buildings = buildingMapper.selectList(wrapper);

        // 转换为简化的前端格式
        List<Map<String, Object>> result = buildings.stream().map(building -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", building.getBuildingId());
            map.put("name", building.getBuildingName());
            return map;
        }).collect(Collectors.toList());

        return AjaxResult.success(result);
    }
}
