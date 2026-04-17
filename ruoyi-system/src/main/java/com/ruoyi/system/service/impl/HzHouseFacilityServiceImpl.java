package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.HzHouseFacility;
import com.ruoyi.system.domain.HzHouseTypeFacility;
import com.ruoyi.system.mapper.HzHouseFacilityMapper;
import com.ruoyi.system.service.IHzHouseFacilityService;
import com.ruoyi.system.service.IHzHouseTypeFacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 房源设施Service业务层处理
 *
 * @author ruoyi
 * @date 2026-04-17
 */
@Service
public class HzHouseFacilityServiceImpl extends ServiceImpl<HzHouseFacilityMapper, HzHouseFacility> implements IHzHouseFacilityService
{
    @Autowired
    private IHzHouseTypeFacilityService hzHouseTypeFacilityService;

    /**
     * 查询房源设施列表
     */
    @Override
    public List<HzHouseFacility> selectByHouseId(Long houseId)
    {
        LambdaQueryWrapper<HzHouseFacility> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzHouseFacility::getHouseId, houseId)
               .eq(HzHouseFacility::getDelFlag, "0")
               .orderByAsc(HzHouseFacility::getFacilityCategory);
        return this.list(wrapper);
    }

    /**
     * 批量保存房源设施（先逻辑删除旧数据，再批量插入新数据）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSave(Long houseId, List<HzHouseFacility> list)
    {
        // 逻辑删除旧数据
        LambdaUpdateWrapper<HzHouseFacility> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(HzHouseFacility::getHouseId, houseId)
                     .eq(HzHouseFacility::getDelFlag, "0")
                     .set(HzHouseFacility::getDelFlag, "1");
        this.update(updateWrapper);

        // 批量插入新数据
        if (list != null && !list.isEmpty())
        {
            for (HzHouseFacility item : list)
            {
                item.setId(null);
                item.setHouseId(houseId);
                item.setDelFlag("0");
            }
            this.saveBatch(list);
        }
    }

    /**
     * 从户型拉取设施到房源
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pullFromHouseType(Long houseId, Long houseTypeId)
    {
        // 查询户型设施配置
        List<HzHouseTypeFacility> typeList = hzHouseTypeFacilityService.selectByHouseTypeId(houseTypeId);

        // 转换为房源设施
        List<HzHouseFacility> facilityList = new ArrayList<>();
        for (HzHouseTypeFacility typeFacility : typeList)
        {
            HzHouseFacility facility = new HzHouseFacility();
            facility.setHouseId(houseId);
            facility.setFacilityItemId(typeFacility.getFacilityItemId());
            facility.setFacilityName(typeFacility.getFacilityName());
            facility.setFacilityCategory(typeFacility.getFacilityCategory());
            facility.setQuantity(typeFacility.getQuantity());
            facility.setItemStatus(typeFacility.getItemStatus());
            facility.setRemark(typeFacility.getRemark());
            facilityList.add(facility);
        }

        // 批量保存（先删后插）
        batchSave(houseId, facilityList);
    }
}
