package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.HzHouseType;
import java.util.List;

/**
 * 户型Mapper接口
 *
 * @author ruoyi
 * @date 2025-11-18
 */
@Mapper
public interface HzHouseTypeMapper extends BaseMapper<HzHouseType>
{
    /**
     * 查询户型列表
     *
     * @param houseType 户型
     * @return 户型集合
     */
    List<HzHouseType> selectHouseTypeList(HzHouseType houseType);
}
