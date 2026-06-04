package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.HzHouseTypeFacility;

/**
 * 户型设施配置Mapper接口
 *
 * @author ruoyi
 * @date 2026-04-17
 */
@Mapper
public interface HzHouseTypeFacilityMapper extends BaseMapper<HzHouseTypeFacility>
{
    /**
     * 物理删除指定户型的所有设施记录（绕过@TableLogic逻辑删除）
     */
    @Delete("DELETE FROM hz_house_type_facility WHERE house_type_id = #{houseTypeId}")
    int physicalDeleteByHouseTypeId(@Param("houseTypeId") Long houseTypeId);
}
