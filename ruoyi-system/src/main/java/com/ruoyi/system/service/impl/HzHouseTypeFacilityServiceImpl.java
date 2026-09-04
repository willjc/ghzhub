package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.HzHouseTypeFacility;
import com.ruoyi.system.domain.HzFacilityItem;
import com.ruoyi.system.domain.HzHouseType;
import com.ruoyi.system.domain.HzProject;
import com.ruoyi.system.mapper.HzHouseTypeMapper;
import com.ruoyi.system.mapper.HzHouseTypeFacilityMapper;
import com.ruoyi.system.mapper.HzProjectMapper;
import com.ruoyi.system.service.HzFacilityTemplateMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import com.ruoyi.system.service.IHzHouseTypeFacilityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 户型设施配置Service业务层处理
 *
 * @author ruoyi
 * @date 2026-04-17
 */
@Service
public class HzHouseTypeFacilityServiceImpl extends ServiceImpl<HzHouseTypeFacilityMapper, HzHouseTypeFacility> implements IHzHouseTypeFacilityService
{
    @Autowired
    private HzHouseTypeMapper houseTypeMapper;

    @Autowired
    private HzProjectMapper projectMapper;

    @Autowired
    private HzFacilityTemplateMappingService facilityTemplateMappingService;

    /**
     * 查询户型设施列表
     */
    @Override
    public List<HzHouseTypeFacility> selectByHouseTypeId(Long houseTypeId)
    {
        LambdaQueryWrapper<HzHouseTypeFacility> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzHouseTypeFacility::getHouseTypeId, houseTypeId)
               .eq(HzHouseTypeFacility::getDelFlag, "0")
               .orderByAsc(HzHouseTypeFacility::getFacilityCategory);
        return this.list(wrapper);
    }

    /**
     * 批量保存户型设施（先物理删除旧数据，再批量插入新数据）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSave(Long houseTypeId, List<HzHouseTypeFacility> list)
    {
        HzHouseType houseType = houseTypeMapper.selectById(houseTypeId);
        HzProject project = houseType == null ? null : projectMapper.selectById(houseType.getProjectId());
        if (project == null)
        {
            throw new IllegalArgumentException("户型所属项目不存在");
        }
        Map<Long, HzFacilityItem> canonicalItems = facilityTemplateMappingService.validate(project.getProjectType(),
                list == null ? null : list.stream().map(item -> item == null ? null : item.getFacilityItemId())
                        .collect(java.util.stream.Collectors.toList()));

        if (list != null)
        {
            for (HzHouseTypeFacility item : list)
            {
                if (item.getQuantity() == null || item.getQuantity() < 1 || item.getQuantity() > 99)
                {
                    throw new IllegalArgumentException("设施数量必须在1至99之间");
                }
                HzFacilityItem canonical = canonicalItems.get(item.getFacilityItemId());
                item.setFacilityName(canonical.getFacilityName());
                item.setFacilityCategory(canonical.getFacilityCategory());
            }
        }

        // 物理删除旧数据（绕过@TableLogic，避免唯一键冲突）
        this.baseMapper.physicalDeleteByHouseTypeId(houseTypeId);

        // 批量插入新数据
        if (list != null && !list.isEmpty())
        {
            for (HzHouseTypeFacility item : list)
            {
                item.setId(null);
                item.setHouseTypeId(houseTypeId);
                item.setDelFlag("0");
            }
            this.saveBatch(list);
        }
    }
}
