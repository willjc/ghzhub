package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.HzHouseFacility;

import java.util.List;

/**
 * 房源设施Service接口
 *
 * @author ruoyi
 * @date 2026-04-17
 */
public interface IHzHouseFacilityService extends IService<HzHouseFacility>
{
    /**
     * 查询房源设施列表
     *
     * @param houseId 房源ID
     * @return 房源设施列表
     */
    List<HzHouseFacility> selectByHouseId(Long houseId);

    /**
     * 批量保存房源设施（先删后插）
     *
     * @param houseId 房源ID
     * @param list 设施列表
     */
    void batchSave(Long houseId, List<HzHouseFacility> list);

    /**
     * 从户型拉取设施到房源
     *
     * @param houseId 房源ID
     * @param houseTypeId 户型ID
     */
    void pullFromHouseType(Long houseId, Long houseTypeId);
}
