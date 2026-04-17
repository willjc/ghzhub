package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.HzHouseTypeFacility;

import java.util.List;

/**
 * 户型设施配置Service接口
 *
 * @author ruoyi
 * @date 2026-04-17
 */
public interface IHzHouseTypeFacilityService extends IService<HzHouseTypeFacility>
{
    /**
     * 查询户型设施列表
     *
     * @param houseTypeId 户型ID
     * @return 户型设施列表
     */
    List<HzHouseTypeFacility> selectByHouseTypeId(Long houseTypeId);

    /**
     * 批量保存户型设施（先删后插）
     *
     * @param houseTypeId 户型ID
     * @param list 设施列表
     */
    void batchSave(Long houseTypeId, List<HzHouseTypeFacility> list);
}
