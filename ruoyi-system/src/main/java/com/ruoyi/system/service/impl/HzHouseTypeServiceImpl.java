package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzHouseType;
import com.ruoyi.system.mapper.HzHouseTypeMapper;
import com.ruoyi.system.service.IHzHouseTypeImageService;
import com.ruoyi.system.service.IHzHouseTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 户型Service业务层处理
 *
 * @author ruoyi
 * @date 2025-11-18
 */
@Service
public class HzHouseTypeServiceImpl extends ServiceImpl<HzHouseTypeMapper, HzHouseType> implements IHzHouseTypeService
{
    @Autowired
    private IHzHouseTypeImageService hzHouseTypeImageService;

    /**
     * 校验户型编码在同一项目下是否唯一
     *
     * @param houseType 户型信息
     */
    private void checkHouseTypeCodeUnique(HzHouseType houseType)
    {
        if (StringUtils.isEmpty(houseType.getHouseTypeCode()))
        {
            return;
        }

        LambdaQueryWrapper<HzHouseType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzHouseType::getProjectId, houseType.getProjectId())
               .eq(HzHouseType::getHouseTypeCode, houseType.getHouseTypeCode())
               .eq(HzHouseType::getDelFlag, "0");

        // 如果是修改操作，排除当前户型ID
        if (houseType.getHouseTypeId() != null)
        {
            wrapper.ne(HzHouseType::getHouseTypeId, houseType.getHouseTypeId());
        }

        long count = this.count(wrapper);
        if (count > 0)
        {
            throw new ServiceException("同一项目下户型编码已存在，请使用其他编码");
        }
    }

    /**
     * 查询户型列表
     *
     * @param houseType 户型查询条件
     * @return 户型列表
     */
    @Override
    public List<HzHouseType> selectHouseTypeList(HzHouseType houseType)
    {
        // 使用自定义SQL查询
        return this.baseMapper.selectHouseTypeList(houseType);
    }

    /**
     * 分页查询户型列表
     *
     * @param houseType 户型查询条件
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @return 户型分页列表
     */
    @Override
    public IPage<HzHouseType> selectHouseTypePage(HzHouseType houseType, int pageNum, int pageSize)
    {
        Page<HzHouseType> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HzHouseType> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(houseType.getHouseTypeName()), HzHouseType::getHouseTypeName, houseType.getHouseTypeName())
               .eq(StringUtils.isNotEmpty(houseType.getHouseTypeCode()), HzHouseType::getHouseTypeCode, houseType.getHouseTypeCode())
               .eq(houseType.getBedroomCount() != null, HzHouseType::getBedroomCount, houseType.getBedroomCount())
               .eq(houseType.getLivingroomCount() != null, HzHouseType::getLivingroomCount, houseType.getLivingroomCount())
               .eq(houseType.getBathroomCount() != null, HzHouseType::getBathroomCount, houseType.getBathroomCount())
               .eq(StringUtils.isNotEmpty(houseType.getStatus()), HzHouseType::getStatus, houseType.getStatus())
               .eq(HzHouseType::getDelFlag, "0")
               .orderByAsc(HzHouseType::getSortOrder)
               .orderByDesc(HzHouseType::getCreateTime);
        return this.page(page, wrapper);
    }

    /**
     * 查询户型详情
     *
     * @param houseTypeId 户型ID
     * @return 户型
     */
    @Override
    public HzHouseType selectHouseTypeById(Long houseTypeId)
    {
        return this.getById(houseTypeId);
    }

    /**
     * 新增户型
     *
     * @param houseType 户型
     * @return 结果
     */
    @Override
    public int insertHouseType(HzHouseType houseType)
    {
        // 校验户型编码唯一性
        checkHouseTypeCodeUnique(houseType);
        houseType.setDelFlag("0");
        return this.save(houseType) ? 1 : 0;
    }

    /**
     * 修改户型
     *
     * @param houseType 户型
     * @return 结果
     */
    @Override
    public int updateHouseType(HzHouseType houseType)
    {
        // 校验户型编码唯一性
        checkHouseTypeCodeUnique(houseType);
        return this.updateById(houseType) ? 1 : 0;
    }

    /**
     * 删除户型
     *
     * @param houseTypeId 户型ID
     * @return 结果
     */
    @Override
    public int deleteHouseTypeById(Long houseTypeId)
    {
        // 级联删除户型图片
        hzHouseTypeImageService.deleteImagesByHouseTypeId(houseTypeId);

        // 逻辑删除户型
        HzHouseType houseType = new HzHouseType();
        houseType.setHouseTypeId(houseTypeId);
        houseType.setDelFlag("2");
        return this.updateById(houseType) ? 1 : 0;
    }

    /**
     * 批量删除户型
     *
     * @param houseTypeIds 户型ID数组
     * @return 结果
     */
    @Override
    public int deleteHouseTypeByIds(Long[] houseTypeIds)
    {
        return Arrays.stream(houseTypeIds)
                .mapToInt(this::deleteHouseTypeById)
                .sum();
    }
}
