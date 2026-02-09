package com.ruoyi.web.controller.h5;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzHouse;
import com.ruoyi.system.domain.HzProject;
import com.ruoyi.system.domain.HzBuilding;
import com.ruoyi.system.domain.HzUnit;
import com.ruoyi.system.domain.HzAppointment;
import com.ruoyi.system.mapper.HzHouseMapper;
import com.ruoyi.system.mapper.HzProjectMapper;
import com.ruoyi.system.mapper.HzBuildingMapper;
import com.ruoyi.system.mapper.HzUnitMapper;
import com.ruoyi.system.service.IHzAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * H5用户端 - 房源API
 */
@RestController
@RequestMapping("/h5/app/house")
public class HzHouseAppController extends BaseController {

    @Autowired
    private HzHouseMapper houseMapper;

    @Autowired
    private HzProjectMapper projectMapper;

    @Autowired
    private HzBuildingMapper buildingMapper;

    @Autowired
    private HzUnitMapper unitMapper;

    @Autowired
    private com.ruoyi.system.mapper.HzHouseImageMapper houseImageMapper;

    @Autowired
    private com.ruoyi.system.mapper.HzHouseVrMapper houseVrMapper;

    @Autowired
    private IHzAppointmentService appointmentService;

    /**
     * 按项目类型获取房源列表（用于首页房源展示）
     * @param projectType 项目类型（1:人才公寓 2:保租房 3:市场租赁）
     * @param limit 限制返回数量，默认10条
     */
    @GetMapping("/listByProjectType/{projectType}")
    public AjaxResult listByProjectType(
            @PathVariable String projectType,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {

        // 查询该类型下的所有项目ID
        QueryWrapper<HzProject> projectWrapper = new QueryWrapper<>();
        projectWrapper.eq("project_type", projectType)
                .eq("status", "0")
                .eq("del_flag", "0");
        List<HzProject> projects = projectMapper.selectList(projectWrapper);

        if (projects.isEmpty()) {
            return AjaxResult.success(new ArrayList<>());
        }

        // 获取项目ID列表
        List<Long> projectIds = projects.stream()
                .map(HzProject::getProjectId)
                .collect(Collectors.toList());

        // 查询这些项目下的空置精选房源
        QueryWrapper<HzHouse> houseWrapper = new QueryWrapper<>();
        houseWrapper.in("project_id", projectIds)
                .eq("house_status", "0")  // 0=空置可租
                .eq("status", "0")
                .eq("del_flag", "0")
                .eq("is_featured", "1")  // 只查询精选房源
                .orderByDesc("create_time")
                .last("LIMIT " + limit);

        List<HzHouse> houses = houseMapper.selectList(houseWrapper);

        // 转换为前端需要的格式（关联查询项目、楼栋、单元、图片）
        List<Map<String, Object>> result = houses.stream().map(house -> {
            Map<String, Object> houseData = new HashMap<>();

            // 获取关联信息
            HzProject project = projectMapper.selectById(house.getProjectId());
            HzBuilding building = buildingMapper.selectById(house.getBuildingId());
            HzUnit unit = unitMapper.selectById(house.getUnitId());

            // 构建标题：装修+楼栋+单元+楼层+房号
            String decoration = house.getDecoration() != null ? house.getDecoration() : "";
            String buildingName = building != null ? building.getBuildingName() : "";
            String unitName = unit != null ? unit.getUnitName() : "";
            String floor = house.getFloor() != null ? house.getFloor() + "层" : "";
            String houseNo = house.getHouseNo() != null ? house.getHouseNo() : "";
            String title = decoration + buildingName + unitName + floor + houseNo;

            // 构建副标题：朝向+面积
            String orientation = house.getOrientation() != null ? house.getOrientation() + "朝向" : "";
            String area = house.getArea() != null && house.getArea().compareTo(java.math.BigDecimal.ZERO) > 0
                    ? house.getArea() + "平米" : "";
            String subtitle = orientation + (orientation.isEmpty() ? "" : " ") + area;

            // 获取主图
            QueryWrapper<com.ruoyi.system.domain.HzHouseImage> imageWrapper = new QueryWrapper<>();
            imageWrapper.eq("house_id", house.getHouseId())
                    .eq("image_type", "1")  // 1=主图
                    .eq("del_flag", "0")
                    .orderByAsc("sort_order");
            List<com.ruoyi.system.domain.HzHouseImage> mainImages = houseImageMapper.selectList(imageWrapper);

            // 取第一张主图
            String mainImageUrl = "";
            if (mainImages != null && !mainImages.isEmpty()) {
                mainImageUrl = mainImages.get(0).getImageUrl();
            }

            // 设施标签
            List<String> facilities = new ArrayList<>();
            if (house.getFacilities() != null && !house.getFacilities().isEmpty()) {
                facilities = Arrays.asList(house.getFacilities().split(","));
            }

            houseData.put("houseId", house.getHouseId());
            houseData.put("projectId", house.getProjectId());  // 项目ID，用于跳转详情
            houseData.put("title", title);
            houseData.put("subtitle", subtitle);
            houseData.put("projectName", project != null ? project.getProjectName() : "");
            houseData.put("facilities", facilities);
            houseData.put("rentPrice", house.getRentPrice());
            houseData.put("mainImage", mainImageUrl);

            return houseData;
        }).collect(Collectors.toList());

        return AjaxResult.success(result);
    }

    /**
     * 获取房源列表（按楼层分组）
     * 支持筛选: 楼栋、单元、户型、楼层范围、朝向
     */
    @GetMapping("/list")
    public AjaxResult list(
            @RequestParam Long projectId,
            @RequestParam Long buildingId,
            @RequestParam Long unitId,
            @RequestParam(required = false) String houseType,  // 户型: 1/2/3/4/5/6
            @RequestParam(required = false) Integer floorMin,
            @RequestParam(required = false) Integer floorMax,
            @RequestParam(required = false) String orientation  // 朝向
    ) {
        QueryWrapper<HzHouse> wrapper = new QueryWrapper<>();
        wrapper.eq("project_id", projectId)
                .eq("building_id", buildingId)
                .eq("unit_id", unitId)
                .eq("status", "0")  // 正常状态
                .eq("del_flag", "0");  // 未删除

        // 筛选条件
        if (houseType != null && !houseType.isEmpty()) {
            // 户型筛选: houseType="2" 匹配 "2室" 或 "两室"
            wrapper.and(w -> w.like("house_type_name", houseType + "室")
                    .or().like("house_type_name", convertNumberToChinese(houseType) + "室"));
        }

        if (floorMin != null && floorMin > 0) {
            wrapper.ge("floor", floorMin);
        }

        if (floorMax != null && floorMax > 0) {
            wrapper.le("floor", floorMax);
        }

        if (orientation != null && !orientation.isEmpty()) {
            // 朝向筛选
            wrapper.like("orientation", convertOrientationToChinese(orientation));
        }

        wrapper.orderByAsc("floor", "house_no");

        List<HzHouse> houses = houseMapper.selectList(wrapper);

        // 按楼层分组
        Map<Integer, List<HzHouse>> floorGroupMap = houses.stream()
                .collect(Collectors.groupingBy(HzHouse::getFloor));

        // 转换为前端需要的格式
        List<Map<String, Object>> result = floorGroupMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())  // 按楼层排序
                .map(entry -> {
                    Map<String, Object> floorData = new HashMap<>();
                    floorData.put("floor", entry.getKey());

                    List<Map<String, Object>> rooms = entry.getValue().stream().map(house -> {
                        Map<String, Object> room = new HashMap<>();
                        room.put("id", house.getHouseId());
                        room.put("name", house.getHouseNo());
                        room.put("available", "0".equals(house.getHouseStatus()));  // 0=空置(可选), 其他不可选
                        return room;
                    }).collect(Collectors.toList());

                    floorData.put("rooms", rooms);
                    return floorData;
                })
                .collect(Collectors.toList());

        return AjaxResult.success(result);
    }

    /**
     * 获取房源详情
     */
    @GetMapping("/{houseId}")
    public AjaxResult getDetail(@PathVariable Long houseId) {
        HzHouse house = houseMapper.selectById(houseId);
        if (house == null) {
            return AjaxResult.error("房源不存在");
        }

        // 获取关联信息
        HzProject project = projectMapper.selectById(house.getProjectId());
        HzBuilding building = buildingMapper.selectById(house.getBuildingId());
        HzUnit unit = unitMapper.selectById(house.getUnitId());

        // 组装返回数据
        Map<String, Object> result = new HashMap<>();

        // 房源基本信息
        result.put("houseId", house.getHouseId());
        result.put("buildingNo", building != null ? building.getBuildingName() : "");
        result.put("unitNo", unit != null ? unit.getUnitName() : "");
        result.put("roomNo", house.getHouseNo());
        result.put("floor", house.getFloor());
        result.put("price", house.getRentPrice());
        result.put("deposit", house.getDeposit());
        result.put("area", house.getArea());
        result.put("layout", house.getHouseTypeName());
        result.put("orientation", house.getOrientation());
        result.put("decoration", house.getDecoration());
        result.put("remark", house.getRemark());  // 房源简介

        // 配套设施（逗号分隔字符串转数组）
        if (house.getFacilities() != null && !house.getFacilities().isEmpty()) {
            result.put("facilities", Arrays.asList(house.getFacilities().split(",")));
        } else {
            result.put("facilities", new ArrayList<>());
        }

        // 项目信息
        if (project != null) {
            result.put("projectId", project.getProjectId());
            result.put("projectName", project.getProjectName());
            result.put("address", project.getAddress());
            result.put("latitude", project.getLatitude());
            result.put("longitude", project.getLongitude());
            result.put("contactPhone", house.getManagerPhone());  // 使用房源级别的管家电话
        }

        // 房源状态
        result.put("houseStatus", house.getHouseStatus());
        result.put("houseStatusText", getHouseStatusText(house.getHouseStatus()));

        return AjaxResult.success(result);
    }

    /**
     * 获取房源图片列表（按分类）
     */
    @GetMapping("/{houseId}/images")
    public AjaxResult getImages(@PathVariable Long houseId) {
        // 查询房源图片
        QueryWrapper<com.ruoyi.system.domain.HzHouseImage> wrapper = new QueryWrapper<>();
        wrapper.eq("house_id", houseId)
                .eq("del_flag", "0")
                .orderByAsc("image_type", "sort_order");

        List<com.ruoyi.system.domain.HzHouseImage> images = houseImageMapper.selectList(wrapper);

        // 按图片类型分类
        Map<String, List<String>> categoryMap = new LinkedHashMap<>();
        categoryMap.put("main", new ArrayList<>());      // 1-主图
        categoryMap.put("layout", new ArrayList<>());    // 2-户型图
        categoryMap.put("bedroom", new ArrayList<>());   // 3-卧室
        categoryMap.put("bathroom", new ArrayList<>()); // 4-卫生间
        categoryMap.put("indoor", new ArrayList<>());    // 5-室内
        categoryMap.put("outdoor", new ArrayList<>());   // 6-室外

        for (com.ruoyi.system.domain.HzHouseImage image : images) {
            String url = image.getImageUrl();
            if (url == null || url.isEmpty()) continue;

            String type = image.getImageType();
            switch (type) {
                case "1": categoryMap.get("main").add(url); break;
                case "2": categoryMap.get("layout").add(url); break;
                case "3": categoryMap.get("bedroom").add(url); break;
                case "4": categoryMap.get("bathroom").add(url); break;
                case "5": categoryMap.get("indoor").add(url); break;
                case "6": categoryMap.get("outdoor").add(url); break;
            }
        }

        // 构建返回数据（只包含有图片的分类）
        List<Map<String, Object>> result = new ArrayList<>();
        addCategoryIfNotEmpty(result, "main", "主图", categoryMap.get("main"));
        addCategoryIfNotEmpty(result, "layout", "户型图", categoryMap.get("layout"));
        addCategoryIfNotEmpty(result, "bedroom", "卧室", categoryMap.get("bedroom"));
        addCategoryIfNotEmpty(result, "bathroom", "卫生间", categoryMap.get("bathroom"));
        addCategoryIfNotEmpty(result, "indoor", "室内", categoryMap.get("indoor"));
        addCategoryIfNotEmpty(result, "outdoor", "室外", categoryMap.get("outdoor"));

        return AjaxResult.success(result);
    }

    /**
     * 获取房源VR列表
     */
    @GetMapping("/{houseId}/vr")
    public AjaxResult getVrList(@PathVariable Long houseId) {
        QueryWrapper<com.ruoyi.system.domain.HzHouseVr> wrapper = new QueryWrapper<>();
        wrapper.eq("house_id", houseId)
                .eq("del_flag", "0")
                .orderByAsc("sort_order");

        List<com.ruoyi.system.domain.HzHouseVr> vrList = houseVrMapper.selectList(wrapper);

        // 转换为前端需要的格式
        List<Map<String, Object>> result = vrList.stream().map(vr -> {
            Map<String, Object> vrData = new HashMap<>();
            vrData.put("vrId", vr.getVrId());
            vrData.put("vrName", vr.getVrName());
            vrData.put("vrUrl", vr.getVrUrl());
            return vrData;
        }).collect(Collectors.toList());

        return AjaxResult.success(result);
    }

    /**
     * 添加非空分类
     */
    private void addCategoryIfNotEmpty(List<Map<String, Object>> result, String key, String name, List<String> images) {
        if (images != null && !images.isEmpty()) {
            Map<String, Object> category = new HashMap<>();
            category.put("key", key);
            category.put("name", name);
            category.put("images", images);
            result.add(category);
        }
    }

    /**
     * 数字转中文
     */
    private String convertNumberToChinese(String number) {
        Map<String, String> map = new HashMap<>();
        map.put("1", "一");
        map.put("2", "两");
        map.put("3", "三");
        map.put("4", "四");
        map.put("5", "五");
        map.put("6", "六");
        return map.getOrDefault(number, number);
    }

    /**
     * 朝向英文转中文
     */
    private String convertOrientationToChinese(String orientation) {
        Map<String, String> map = new HashMap<>();
        map.put("south", "南");
        map.put("east", "东");
        map.put("north", "北");
        map.put("west", "西");
        map.put("southeast", "东南");
        map.put("southwest", "西南");
        map.put("northeast", "东北");
        map.put("northwest", "西北");
        return map.getOrDefault(orientation, orientation);
    }

    /**
     * 房源状态文本
     */
    private String getHouseStatusText(String status) {
        if (status == null) return "未知";
        switch (status) {
            case "0": return "空置";
            case "1": return "已租";
            case "2": return "预订";
            case "3": return "维修";
            default: return "未知";
        }
    }

    /**
     * 提交预约看房申请（简化版，用于预约页面）
     */
    @PostMapping("/{houseId}/appointment")
    public AjaxResult submitAppointment(
            @PathVariable Long houseId,
            @RequestBody HzAppointment appointment) {

        // 校验房源是否存在
        HzHouse house = houseMapper.selectById(houseId);
        if (house == null) {
            return error("房源不存在");
        }

        // 设置房源ID和项目ID
        appointment.setHouseId(houseId);
        appointment.setProjectId(house.getProjectId());

        // 从token中解析hz_user的ID
        Long hzUserId = getHzUserIdFromToken();
        if (hzUserId == null) {
            return error("请先登录");
        }
        appointment.setTenantId(hzUserId);

        // 设置预约来源
        appointment.setAppointmentSource("H5");

        // 设置预约状态为待确认
        appointment.setAppointmentStatus("0");

        // 提交预约
        int result = appointmentService.insertAppointment(appointment);

        if (result > 0) {
            return success("预约成功，请等待确认");
        } else {
            return error("预约失败，请稍后重试");
        }
    }

    /**
     * 从请求头中的Token解析出hz_user的ID
     * Token格式：hz_token_{userId}_{timestamp}
     */
    private Long getHzUserIdFromToken() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("Authorization");

            if (token == null || token.isEmpty()) {
                return null;
            }

            // 移除 "Bearer " 前缀（如果有）
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // 解析token: hz_token_{userId}_{timestamp}
            if (token.startsWith("hz_token_")) {
                String[] parts = token.split("_");
                if (parts.length >= 3) {
                    return Long.parseLong(parts[2]); // parts[0]=hz, parts[1]=token, parts[2]=userId
                }
            }

            return null;
        } catch (Exception e) {
            logger.error("解析token失败", e);
            return null;
        }
    }
}
