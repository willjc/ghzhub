package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.HzFacilityItem;

import java.util.List;

/**
 * 设施物品Service接口
 *
 * @author ruoyi
 * @date 2026-04-17
 */
public interface IHzFacilityItemService extends IService<HzFacilityItem>
{
    /**
     * 查询设施物品列表（支持按分类筛选）
     *
     * @param query 查询条件
     * @return 设施物品列表
     */
    List<HzFacilityItem> selectFacilityItemList(HzFacilityItem query);
}
