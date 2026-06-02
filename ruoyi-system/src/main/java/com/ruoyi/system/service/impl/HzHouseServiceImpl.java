package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzHouse;
import com.ruoyi.system.domain.HzHouseImage;
import com.ruoyi.system.domain.HzProject;
import com.ruoyi.system.domain.HzBuilding;
import com.ruoyi.system.domain.HzUnit;
import com.ruoyi.system.domain.HzHouseType;
import com.ruoyi.system.domain.HzHouseVr;
import com.ruoyi.system.mapper.HzHouseMapper;
import com.ruoyi.system.mapper.HzHouseImageMapper;
import com.ruoyi.system.mapper.HzProjectMapper;
import com.ruoyi.system.mapper.HzBuildingMapper;
import com.ruoyi.system.mapper.HzUnitMapper;
import com.ruoyi.system.mapper.HzHouseTypeMapper;
import com.ruoyi.system.mapper.HzHouseVrMapper;
import com.ruoyi.system.service.IHzHouseService;
import com.ruoyi.system.service.IHzHouseStatusAuditService;
import com.ruoyi.system.service.IHzRoleProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * 房源Service业务层处理
 *
 * @author ruoyi
 * @date 2025-11-17
 */
@Service
public class HzHouseServiceImpl extends ServiceImpl<HzHouseMapper, HzHouse> implements IHzHouseService
{
    @Autowired
    private HzHouseImageMapper houseImageMapper;

    @Autowired
    private HzProjectMapper projectMapper;

    @Autowired
    private HzBuildingMapper buildingMapper;

    @Autowired
    private HzUnitMapper unitMapper;

    @Autowired
    private HzHouseTypeMapper houseTypeMapper;

    @Autowired
    private HzHouseVrMapper houseVrMapper;

    @Autowired
    private IHzRoleProjectService roleProjectService;

    @Autowired
    private IHzHouseStatusAuditService houseStatusAuditService;

    /**
     * 查询房源列表（支持分页，带项目名称）
     *
     * @param house 房源查询条件
     * @return 房源分页列表
     */
    @Override
    public IPage<HzHouse> selectHouseList(HzHouse house)
    {
        // 注入项目权限过滤
        List<Long> projectIds = roleProjectService.getCurrentUserProjectIds();
        if (projectIds != null) {
            if (house.getParams() == null) {
                house.setParams(new HashMap<>());
            }
            house.getParams().put("projectIds", projectIds);
        }
        // 获取分页参数
        Page<HzHouse> page = com.ruoyi.common.utils.PageUtils.getPage();
        // 使用 XML 中定义的关联查询，获取项目名称
        return baseMapper.selectHouseListWithImages(page, house);
    }

    /**
     * 分页查询房源列表
     *
     * @param house 房源查询条件
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @return 房源分页列表
     */
    @Override
    public IPage<HzHouse> selectHousePage(HzHouse house, int pageNum, int pageSize)
    {
        Page<HzHouse> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HzHouse> wrapper = buildQueryWrapper(house);
        return this.page(page, wrapper);
    }

    /**
     * 构建查询条件
     *
     * @param house 查询条件
     * @return 查询包装器
     */
    private LambdaQueryWrapper<HzHouse> buildQueryWrapper(HzHouse house)
    {
        LambdaQueryWrapper<HzHouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(house.getProjectId() != null, HzHouse::getProjectId, house.getProjectId())
               .eq(house.getBuildingId() != null, HzHouse::getBuildingId, house.getBuildingId())
               .eq(house.getUnitId() != null, HzHouse::getUnitId, house.getUnitId())
               .like(StringUtils.isNotEmpty(house.getHouseCode()), HzHouse::getHouseCode, house.getHouseCode())
               .like(StringUtils.isNotEmpty(house.getHouseNo()), HzHouse::getHouseNo, house.getHouseNo())
               .eq(StringUtils.isNotEmpty(house.getHouseTypeName()), HzHouse::getHouseTypeName, house.getHouseTypeName())
               .eq(StringUtils.isNotEmpty(house.getHouseStatus()), HzHouse::getHouseStatus, house.getHouseStatus())
               .eq(StringUtils.isNotEmpty(house.getIsFeatured()), HzHouse::getIsFeatured, house.getIsFeatured())
               .eq(StringUtils.isNotEmpty(house.getStatus()), HzHouse::getStatus, house.getStatus())
               .orderByDesc(HzHouse::getCreateTime);
        return wrapper;
    }

    /**
     * 查询房源详情
     *
     * @param houseId 房源ID
     * @return 房源
     */
    @Override
    public HzHouse selectHouseById(Long houseId)
    {
        return this.getById(houseId);
    }

    /**
     * 查询房源详细信息（包含关联数据）
     *
     * @param houseId 房源ID
     * @return 房源详细信息
     */
    @Override
    public Map<String, Object> selectHouseDetailById(Long houseId)
    {
        Map<String, Object> result = new HashMap<>();

        // 查询房源基本信息
        HzHouse house = this.getById(houseId);
        result.put("house", house);

        if (house != null) {
            // 查询项目信息
            if (house.getProjectId() != null) {
                HzProject project = projectMapper.selectById(house.getProjectId());
                result.put("project", project);
            }

            // 查询楼栋信息
            if (house.getBuildingId() != null) {
                HzBuilding building = buildingMapper.selectById(house.getBuildingId());
                result.put("building", building);
            }

            // 查询单元信息
            if (house.getUnitId() != null) {
                HzUnit unit = unitMapper.selectById(house.getUnitId());
                result.put("unit", unit);
            }

            // 查询户型信息
            if (house.getHouseTypeId() != null) {
                HzHouseType houseType = houseTypeMapper.selectById(house.getHouseTypeId());
                result.put("houseType", houseType);
            }

            // 查询房源图片
            LambdaQueryWrapper<HzHouseImage> imageWrapper = new LambdaQueryWrapper<>();
            imageWrapper.eq(HzHouseImage::getHouseId, houseId)
                       .eq(HzHouseImage::getDelFlag, "0")
                       .orderByAsc(HzHouseImage::getSortOrder);
            List<HzHouseImage> images = houseImageMapper.selectList(imageWrapper);
            result.put("images", images);

            // 查询房源VR
            LambdaQueryWrapper<HzHouseVr> vrWrapper = new LambdaQueryWrapper<>();
            vrWrapper.eq(HzHouseVr::getHouseId, houseId)
                    .eq(HzHouseVr::getDelFlag, "0")
                    .orderByAsc(HzHouseVr::getSortOrder);
            List<HzHouseVr> vrs = houseVrMapper.selectList(vrWrapper);
            result.put("vrs", vrs);
        }

        return result;
    }

    /**
     * 新增房源
     *
     * @param house 房源
     * @return 结果
     */
    @Override
    public int insertHouse(HzHouse house)
    {
        return this.save(house) ? 1 : 0;
    }

    /**
     * 修改房源
     *
     * @param house 房源
     * @return 结果
     */
    @Override
    public int updateHouse(HzHouse house)
    {
        return this.updateById(house) ? 1 : 0;
    }

    /**
     * 删除房源
     *
     * @param houseId 房源ID
     * @return 结果
     */
    @Override
    public int deleteHouseById(Long houseId)
    {
        return this.removeById(houseId) ? 1 : 0;
    }

    /**
     * 增加浏览次数
     *
     * @param houseId 房源ID
     * @return 结果
     */
    @Override
    public int increaseViewCount(Long houseId)
    {
        HzHouse house = this.getById(houseId);
        if (house != null)
        {
            house.setViewCount(house.getViewCount() == null ? 1 : house.getViewCount() + 1);
            return this.updateById(house) ? 1 : 0;
        }
        return 0;
    }

    /**
     * 导入房源数据
     *
     * @param houseList 房源列表
     * @param updateSupport 是否更新已存在数据(此参数已废弃,保留用��接口兼容)
     * @return 结果消息
     */
    @Override
    public String importHouse(List<HzHouse> houseList, boolean updateSupport)
    {
        if (houseList == null || houseList.isEmpty())
        {
            throw new RuntimeException("导入房源数据不能为空！");
        }

        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();

        for (HzHouse house : houseList)
        {
            try
            {
                // 1. 检查房源编码是否已存在
                LambdaQueryWrapper<HzHouse> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(HzHouse::getHouseCode, house.getHouseCode());
                HzHouse existHouse = this.getOne(wrapper);

                if (existHouse != null)
                {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、房源编码 ").append(house.getHouseCode()).append(" 已存在");
                    continue;
                }

                // 2. 根据项目名称查找项目ID
                if (house.getProjectName() == null || house.getProjectName().trim().isEmpty())
                {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、房源编码 ").append(house.getHouseCode()).append(" 导入失败：项目名称不能为空");
                    continue;
                }

                LambdaQueryWrapper<com.ruoyi.system.domain.HzProject> projectWrapper = new LambdaQueryWrapper<>();
                projectWrapper.eq(com.ruoyi.system.domain.HzProject::getProjectName, house.getProjectName().trim());
                com.ruoyi.system.domain.HzProject project = projectMapper.selectOne(projectWrapper);

                if (project == null)
                {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、房源编码 ").append(house.getHouseCode()).append(" 导入失败：项目名称 '").append(house.getProjectName()).append("' 不存在，请检查项目信息是否正确");
                    continue;
                }
                house.setProjectId(project.getProjectId());

                // 3. 根据项目名称+楼栋名称查找楼栋ID
                if (house.getBuildingName() == null || house.getBuildingName().trim().isEmpty())
                {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、房源编码 ").append(house.getHouseCode()).append(" 导入失败：楼栋名称不能为空");
                    continue;
                }

                LambdaQueryWrapper<com.ruoyi.system.domain.HzBuilding> buildingWrapper = new LambdaQueryWrapper<>();
                buildingWrapper.eq(com.ruoyi.system.domain.HzBuilding::getProjectId, project.getProjectId())
                              .eq(com.ruoyi.system.domain.HzBuilding::getBuildingName, house.getBuildingName().trim());
                com.ruoyi.system.domain.HzBuilding building = buildingMapper.selectOne(buildingWrapper);

                if (building == null)
                {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、房源编码 ").append(house.getHouseCode()).append(" 导入失败：楼栋名称 '").append(house.getBuildingName()).append("' 在项目 '").append(house.getProjectName()).append("' 下不存在，请检查楼栋信息是否正确");
                    continue;
                }
                house.setBuildingId(building.getBuildingId());

                // 4. 根据楼栋ID+单元名称查找单元ID
                if (house.getUnitName() == null || house.getUnitName().trim().isEmpty())
                {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、房源编码 ").append(house.getHouseCode()).append(" 导入失败：单元名称不能为空");
                    continue;
                }

                LambdaQueryWrapper<com.ruoyi.system.domain.HzUnit> unitWrapper = new LambdaQueryWrapper<>();
                unitWrapper.eq(com.ruoyi.system.domain.HzUnit::getBuildingId, building.getBuildingId())
                         .eq(com.ruoyi.system.domain.HzUnit::getUnitName, house.getUnitName().trim());
                com.ruoyi.system.domain.HzUnit unit = unitMapper.selectOne(unitWrapper);

                if (unit == null)
                {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、房源编码 ").append(house.getHouseCode()).append(" 导入失败：单元名称 '").append(house.getUnitName()).append("' 在楼栋 '").append(house.getBuildingName()).append("' 下不存在，请检查单元信息是否正确");
                    continue;
                }
                house.setUnitId(unit.getUnitId());

                // 5. 根据项目名称+户型名称查找户型ID
                if (house.getHouseTypeName() == null || house.getHouseTypeName().trim().isEmpty())
                {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、房源编码 ").append(house.getHouseCode()).append(" 导入失败：户型名称不能为空");
                    continue;
                }

                LambdaQueryWrapper<com.ruoyi.system.domain.HzHouseType> houseTypeWrapper = new LambdaQueryWrapper<>();
                houseTypeWrapper.eq(com.ruoyi.system.domain.HzHouseType::getProjectId, project.getProjectId())
                               .eq(com.ruoyi.system.domain.HzHouseType::getHouseTypeName, house.getHouseTypeName().trim());
                com.ruoyi.system.domain.HzHouseType houseType = houseTypeMapper.selectOne(houseTypeWrapper);

                if (houseType == null)
                {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、房源编码 ").append(house.getHouseCode()).append(" 导入失败：户型名称 '").append(house.getHouseTypeName()).append("' 在项目 '").append(house.getProjectName()).append("' 下不存在，请检查户型信息是否正确");
                    continue;
                }
                house.setHouseTypeId(houseType.getHouseTypeId());

                // 6. 新增房源
                this.save(house);
                successNum++;
                successMsg.append("<br/>").append(successNum).append("、房源编码 ").append(house.getHouseCode()).append(" 导入成功");
            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、房源编码 " + house.getHouseCode() + " 导入失败：";
                failureMsg.append(msg).append(e.getMessage());
            }
        }

        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new RuntimeException(failureMsg.toString());
        }
        else
        {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }

    /**
     * 获取房源图片列表
     *
     * @param houseId 房源ID
     * @return 图片列表
     */
    @Override
    public List<HzHouseImage> getHouseImages(Long houseId)
    {
        LambdaQueryWrapper<HzHouseImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzHouseImage::getHouseId, houseId)
               .eq(HzHouseImage::getDelFlag, "0")
               .orderByAsc(HzHouseImage::getSortOrder);
        return houseImageMapper.selectList(wrapper);
    }

    /**
     * 保存房源图片
     *
     * @param houseId 房源ID
     * @param images 图片列表
     */
    @Override
    public void saveHouseImages(Long houseId, List<Map<String, Object>> images)
    {
        // 先删除该房源的所有旧图片
        LambdaQueryWrapper<HzHouseImage> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(HzHouseImage::getHouseId, houseId);
        houseImageMapper.delete(deleteWrapper);

        // 保存新图片
        if (images != null && !images.isEmpty())
        {
            for (Map<String, Object> imageData : images)
            {
                HzHouseImage houseImage = new HzHouseImage();
                houseImage.setHouseId(houseId);
                houseImage.setImageUrl(imageData.get("imageUrl").toString());
                houseImage.setImageType(imageData.get("imageType").toString());
                houseImage.setIsCover(imageData.get("isCover").toString());
                houseImage.setSortOrder(Integer.parseInt(imageData.get("sortOrder").toString()));
                houseImageMapper.insert(houseImage);
            }
        }
    }

    /**
     * 获取房源VR列表
     *
     * @param houseId 房源ID
     * @return VR列表
     */
    @Override
    public List<HzHouseVr> getHouseVrs(Long houseId)
    {
        LambdaQueryWrapper<HzHouseVr> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzHouseVr::getHouseId, houseId)
               .eq(HzHouseVr::getDelFlag, "0")
               .orderByAsc(HzHouseVr::getSortOrder);
        return houseVrMapper.selectList(wrapper);
    }

    /**
     * 保存房源VR
     *
     * @param houseId 房源ID
     * @param vrs VR列表
     */
    @Override
    public void saveHouseVrs(Long houseId, List<Map<String, Object>> vrs)
    {
        // 先删除该房源的所有旧VR
        LambdaQueryWrapper<HzHouseVr> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(HzHouseVr::getHouseId, houseId);
        houseVrMapper.delete(deleteWrapper);

        // 保存新VR
        if (vrs != null && !vrs.isEmpty())
        {
            for (Map<String, Object> vrData : vrs)
            {
                HzHouseVr houseVr = new HzHouseVr();
                houseVr.setHouseId(houseId);
                houseVr.setVrName(vrData.get("vrName") != null ? vrData.get("vrName").toString() : "");
                houseVr.setVrUrl(vrData.get("vrUrl").toString());
                houseVr.setSortOrder(Integer.parseInt(vrData.get("sortOrder").toString()));
                houseVrMapper.insert(houseVr);
            }
        }
    }

    /**
     * 按项目批量修改房源状态（受控过渡白名单）。
     * 管理方：仅 0/3/4 之间可互转，直接修改；
     * 物业：目标3/4直接修改，目标0/1/2走审批。
     */
    @Override
    @Transactional
    public Map<String, Integer> batchUpdateHouseStatusByProject(Long projectId, String targetStatus)
    {
        if (projectId == null)
        {
            throw new ServiceException("项目ID不能为空");
        }

        boolean isProperty = SecurityUtils.isPropertyRole();
        // 物业可申请的目标状态包含0/1/2/3/4，但0/1/2需审批
        Set<String> allowed = isProperty
                ? new HashSet<>(Arrays.asList("0", "1", "2", "3", "4"))
                : new HashSet<>(Arrays.asList("0", "3", "4"));
        if (targetStatus == null || !allowed.contains(targetStatus))
        {
            throw new ServiceException("目标状态非法");
        }

        // 物业角色 + 目标状态0/1/2 → 走审批流程
        Set<String> auditRequired = new HashSet<>(Arrays.asList("0", "1", "2"));
        if (isProperty && auditRequired.contains(targetStatus))
        {
            // 查询该项目下可变更的房源（状态为0/3/4且非目标状态的）
            List<HzHouse> houses = this.list(new LambdaQueryWrapper<HzHouse>()
                    .eq(HzHouse::getProjectId, projectId)
                    .in(HzHouse::getHouseStatus, "0", "3", "4")
                    .ne(HzHouse::getHouseStatus, targetStatus)
                    .select(HzHouse::getHouseId));
            List<Long> houseIds = houses.stream().map(HzHouse::getHouseId).collect(java.util.stream.Collectors.toList());
            int submitted = 0;
            if (!houseIds.isEmpty()) {
                submitted = houseStatusAuditService.batchSubmitStatusChange(houseIds, targetStatus);
            }
            Map<String, Integer> result = new LinkedHashMap<>();
            result.put("total", houseIds.size());
            result.put("submitted", submitted);
            return result;
        }

        // 管理方 或 物业目标3/4：直接修改
        long total = this.count(new LambdaQueryWrapper<HzHouse>()
                .eq(HzHouse::getProjectId, projectId));
        long skippedBooked = this.count(new LambdaQueryWrapper<HzHouse>()
                .eq(HzHouse::getProjectId, projectId)
                .eq(HzHouse::getHouseStatus, "1"));
        long skippedRented = this.count(new LambdaQueryWrapper<HzHouse>()
                .eq(HzHouse::getProjectId, projectId)
                .eq(HzHouse::getHouseStatus, "2"));

        LambdaUpdateWrapper<HzHouse> uw = new LambdaUpdateWrapper<HzHouse>()
                .eq(HzHouse::getProjectId, projectId)
                .in(HzHouse::getHouseStatus, "0", "3", "4")
                .ne(HzHouse::getHouseStatus, targetStatus)
                .set(HzHouse::getHouseStatus, targetStatus);
        int affected = this.baseMapper.update(null, uw);

        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("total", (int) total);
        result.put("affected", affected);
        result.put("skippedBooked", (int) skippedBooked);
        result.put("skippedRented", (int) skippedRented);
        return result;
    }

    /**
     * 按房源ID列表批量修改房源状态（受控过渡白名单）。
     * 管理方：仅 0/3/4 之间可互转，直接修改；
     * 物业：目标3/4直接修改，目标0/1/2走审批。
     */
    @Override
    @Transactional
    public Map<String, Integer> batchUpdateHouseStatusByIds(List<Long> houseIds, String targetStatus)
    {
        if (houseIds == null || houseIds.isEmpty())
        {
            throw new ServiceException("房源ID不能为空");
        }

        boolean isProperty = SecurityUtils.isPropertyRole();
        Set<String> allowed = isProperty
                ? new HashSet<>(Arrays.asList("0", "1", "2", "3", "4"))
                : new HashSet<>(Arrays.asList("0", "3", "4"));
        if (targetStatus == null || !allowed.contains(targetStatus))
        {
            throw new ServiceException("目标状态非法");
        }

        // 物业角色 + 目标状态0/1/2 → 走审批流程
        Set<String> auditRequired = new HashSet<>(Arrays.asList("0", "1", "2"));
        if (isProperty && auditRequired.contains(targetStatus))
        {
            int submitted = houseStatusAuditService.batchSubmitStatusChange(houseIds, targetStatus);
            Map<String, Integer> result = new LinkedHashMap<>();
            result.put("total", houseIds.size());
            result.put("submitted", submitted);
            return result;
        }

        // 管理方 或 物业目标3/4：直接修改
        long total = this.count(new LambdaQueryWrapper<HzHouse>()
                .in(HzHouse::getHouseId, houseIds));
        long skippedBooked = this.count(new LambdaQueryWrapper<HzHouse>()
                .in(HzHouse::getHouseId, houseIds)
                .eq(HzHouse::getHouseStatus, "1"));
        long skippedRented = this.count(new LambdaQueryWrapper<HzHouse>()
                .in(HzHouse::getHouseId, houseIds)
                .eq(HzHouse::getHouseStatus, "2"));

        LambdaUpdateWrapper<HzHouse> uw = new LambdaUpdateWrapper<HzHouse>()
                .in(HzHouse::getHouseId, houseIds)
                .in(HzHouse::getHouseStatus, "0", "3", "4")
                .ne(HzHouse::getHouseStatus, targetStatus)
                .set(HzHouse::getHouseStatus, targetStatus);
        int affected = this.baseMapper.update(null, uw);

        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("total", (int) total);
        result.put("affected", affected);
        result.put("skippedBooked", (int) skippedBooked);
        result.put("skippedRented", (int) skippedRented);
        return result;
    }

    /**
     * 按查询条件统计各状态房源数量（列表页看板用）。
     * 条件与 selectHouseList 对齐：projectId / buildingId / unitId / houseCode / houseNo / status。
     */
    @Override
    public Map<String, Integer> selectHouseStatusStats(HzHouse house)
    {
        LambdaQueryWrapper<HzHouse> base = buildStatsQueryWrapper(house);

        long total = this.count(base);
        long vacant = this.count(buildStatsQueryWrapper(house).eq(HzHouse::getHouseStatus, "0"));
        long booked = this.count(buildStatsQueryWrapper(house).eq(HzHouse::getHouseStatus, "1"));
        long rented = this.count(buildStatsQueryWrapper(house).eq(HzHouse::getHouseStatus, "2"));
        long maintain = this.count(buildStatsQueryWrapper(house).eq(HzHouse::getHouseStatus, "3"));
        long offline = this.count(buildStatsQueryWrapper(house).eq(HzHouse::getHouseStatus, "4"));

        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("total", (int) total);
        result.put("vacant", (int) vacant);
        result.put("booked", (int) booked);
        result.put("rented", (int) rented);
        result.put("maintain", (int) maintain);
        result.put("offline", (int) offline);
        return result;
    }

    /** 构造看板统计的基础查询条件（与列表页对齐） */
    private LambdaQueryWrapper<HzHouse> buildStatsQueryWrapper(HzHouse house)
    {
        LambdaQueryWrapper<HzHouse> qw = new LambdaQueryWrapper<>();
        if (house == null)
        {
            return qw;
        }
        if (house.getProjectId() != null)
        {
            qw.eq(HzHouse::getProjectId, house.getProjectId());
        }
        if (house.getBuildingId() != null)
        {
            qw.eq(HzHouse::getBuildingId, house.getBuildingId());
        }
        if (house.getUnitId() != null)
        {
            qw.eq(HzHouse::getUnitId, house.getUnitId());
        }
        if (StringUtils.isNotBlank(house.getHouseCode()))
        {
            qw.like(HzHouse::getHouseCode, house.getHouseCode());
        }
        if (StringUtils.isNotBlank(house.getHouseNo()))
        {
            qw.like(HzHouse::getHouseNo, house.getHouseNo());
        }
        if (StringUtils.isNotBlank(house.getStatus()))
        {
            qw.eq(HzHouse::getStatus, house.getStatus());
        }
        return qw;
    }
}
