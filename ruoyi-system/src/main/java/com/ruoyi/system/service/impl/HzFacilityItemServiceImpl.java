package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzFacilityItem;
import com.ruoyi.system.mapper.HzFacilityItemMapper;
import com.ruoyi.system.service.IHzFacilityItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 设施物品Service业务层处理
 *
 * @author ruoyi
 * @date 2026-04-17
 */
@Service
public class HzFacilityItemServiceImpl extends ServiceImpl<HzFacilityItemMapper, HzFacilityItem> implements IHzFacilityItemService
{
    /**
     * 查询设施物品列表（支持按分类筛选）
     */
    @Override
    public List<HzFacilityItem> selectFacilityItemList(HzFacilityItem query)
    {
        LambdaQueryWrapper<HzFacilityItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(query.getFacilityName()), HzFacilityItem::getFacilityName, query.getFacilityName())
               .eq(StringUtils.isNotEmpty(query.getFacilityCategory()), HzFacilityItem::getFacilityCategory, query.getFacilityCategory())
               .eq(StringUtils.isNotEmpty(query.getStatus()), HzFacilityItem::getStatus, query.getStatus())
               .eq(HzFacilityItem::getDelFlag, "0")
               .orderByAsc(HzFacilityItem::getSortOrder);
        return this.list(wrapper);
    }
}
