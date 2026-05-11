package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzHouse;
import com.ruoyi.system.domain.HzHouseImage;
import com.ruoyi.system.domain.HzHouseType;
import com.ruoyi.system.domain.HzHouseTypeImage;
import com.ruoyi.system.domain.HzHouseVr;
import com.ruoyi.system.mapper.HzHouseImageMapper;
import com.ruoyi.system.mapper.HzHouseMapper;
import com.ruoyi.system.mapper.HzHouseTypeMapper;
import com.ruoyi.system.mapper.HzHouseVrMapper;
import com.ruoyi.system.service.IHzHouseTypeImageService;
import com.ruoyi.system.service.IHzHouseTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private HzHouseMapper hzHouseMapper;

    @Autowired
    private HzHouseImageMapper hzHouseImageMapper;

    @Autowired
    private HzHouseVrMapper hzHouseVrMapper;

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
     * 分页查询户型列表(使用XML自定义SQL,带项目名称)
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
        return this.baseMapper.selectHouseTypeListPage(page, houseType);
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

        // 逻辑删除户型（removeById会自动处理@TableLogic: SET del_flag='2' WHERE del_flag='0'）
        return this.removeById(houseTypeId) ? 1 : 0;
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

    /**
     * 将户型的 6 类图片与 VR 下发到该户型所有房源（仅填空，不覆盖房源已有数据）。
     */
    @Override
    @Transactional
    public Map<String, Integer> pushImagesAndVrToHouses(Long houseTypeId)
    {
        HzHouseType houseType = this.getById(houseTypeId);
        if (houseType == null)
        {
            throw new ServiceException("户型不存在");
        }

        // 1) 户型图片按 imageType 分组（只保留有效图片）
        List<HzHouseTypeImage> typeImages = hzHouseTypeImageService.selectImageListByHouseTypeId(houseTypeId);
        Map<String, List<HzHouseTypeImage>> typeImageByType = typeImages == null
                ? Collections.emptyMap()
                : typeImages.stream()
                    .filter(i -> StringUtils.isNotEmpty(i.getImageType()))
                    .collect(Collectors.groupingBy(HzHouseTypeImage::getImageType));

        String typeVrUrl = houseType.getVrUrl();

        // 2) 查询该户型下所有有效房源（MyBatis-Plus @TableLogic 全局配置自动追加 del_flag 过滤）
        List<HzHouse> houses = hzHouseMapper.selectList(
                new LambdaQueryWrapper<HzHouse>()
                        .eq(HzHouse::getHouseTypeId, houseTypeId));

        int imageFilled = 0;
        int vrFilled = 0;
        final String[] imageTypes = {"1", "2", "3", "4", "5", "6"};

        for (HzHouse h : houses)
        {
            Long houseId = h.getHouseId();
            boolean thisHouseGotImage = false;

            // 2a) 6 类图片：房源该类别无图才补
            for (String t : imageTypes)
            {
                List<HzHouseTypeImage> src = typeImageByType.getOrDefault(t, Collections.emptyList());
                if (src.isEmpty())
                {
                    continue;
                }
                Long exists = hzHouseImageMapper.selectCount(
                        new LambdaQueryWrapper<HzHouseImage>()
                                .eq(HzHouseImage::getHouseId, houseId)
                                .eq(HzHouseImage::getImageType, t)
                                .eq(HzHouseImage::getDelFlag, "0"));
                if (exists != null && exists > 0)
                {
                    continue;
                }
                int idx = 0;
                for (HzHouseTypeImage s : src)
                {
                    HzHouseImage ni = new HzHouseImage();
                    ni.setHouseId(houseId);
                    ni.setImageUrl(s.getImageUrl());
                    ni.setImageType(t);
                    // 主图(type=1)首张设为封面
                    ni.setIsCover("1".equals(t) && idx == 0 ? "1" : "0");
                    ni.setSortOrder(idx + 1);
                    ni.setDelFlag("0");
                    hzHouseImageMapper.insert(ni);
                    idx++;
                }
                thisHouseGotImage = true;
            }
            if (thisHouseGotImage)
            {
                imageFilled++;
            }

            // 2b) VR：房源无 VR 且户型有 VR 才补
            if (StringUtils.isNotBlank(typeVrUrl))
            {
                Long vrExists = hzHouseVrMapper.selectCount(
                        new LambdaQueryWrapper<HzHouseVr>()
                                .eq(HzHouseVr::getHouseId, houseId)
                                .eq(HzHouseVr::getDelFlag, "0"));
                if (vrExists == null || vrExists == 0)
                {
                    HzHouseVr nv = new HzHouseVr();
                    nv.setHouseId(houseId);
                    nv.setVrUrl(typeVrUrl);
                    nv.setVrName("VR");
                    nv.setSortOrder(1);
                    nv.setDelFlag("0");
                    hzHouseVrMapper.insert(nv);
                    vrFilled++;
                }
            }
        }

        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("total", houses.size());
        result.put("imageFilled", imageFilled);
        result.put("vrFilled", vrFilled);
        return result;
    }
}
