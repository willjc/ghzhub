package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.HzHouse;
import org.apache.ibatis.annotations.Param;

/**
 * 房源Mapper接口
 *
 * @author ruoyi
 * @date 2025-11-17
 */
@Mapper
public interface HzHouseMapper extends BaseMapper<HzHouse>
{
    /**
     * 分页查询房源列表(带图片)
     *
     * @param page 分页对象
     * @param house 房源查询条件
     * @return 房源列表
     */
    IPage<HzHouse> selectHouseListWithImages(Page<HzHouse> page, @Param("house") HzHouse house);
}
