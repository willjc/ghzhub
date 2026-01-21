package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.HzUnit;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 单元Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface HzUnitMapper extends BaseMapper<HzUnit> {

    /**
     * 查询单元列表(带关联楼栋名称、项目名称和总房源数统计)
     *
     * @param unit 单元
     * @return 单元集合
     */
    List<HzUnit> selectUnitAllocationList(HzUnit unit);

    /**
     * 分页查询单元列表(带关联楼栋名称、项目名称和总房源数统计)
     *
     * @param page 分页对象
     * @param unit 单元查询条件
     * @return 单元分页集合
     */
    IPage<HzUnit> selectUnitAllocationPage(Page<HzUnit> page, @Param("unit") HzUnit unit);

}
