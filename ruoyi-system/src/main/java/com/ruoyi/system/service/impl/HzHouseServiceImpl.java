package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

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

    /**
     * 查询房源列表（支持分页，带项目名称）
     *
     * @param house 房源查询条件
     * @return 房源分页列表
     */
    @Override
    public IPage<HzHouse> selectHouseList(HzHouse house)
    {
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
     * @param updateSupport 是否更新已存在数据
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
                // 检查房源编码是否已存在
                LambdaQueryWrapper<HzHouse> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(HzHouse::getHouseCode, house.getHouseCode());
                HzHouse existHouse = this.getOne(wrapper);

                if (existHouse == null)
                {
                    // 新增房源
                    this.save(house);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、房源编码 ").append(house.getHouseCode()).append(" 导入成功");
                }
                else if (updateSupport)
                {
                    // 更新房源
                    house.setHouseId(existHouse.getHouseId());
                    this.updateById(house);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、房源编码 ").append(house.getHouseCode()).append(" 更新成功");
                }
                else
                {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、房源编码 ").append(house.getHouseCode()).append(" 已存在");
                }
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
}
