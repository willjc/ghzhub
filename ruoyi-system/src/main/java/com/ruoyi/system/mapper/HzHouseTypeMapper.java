package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
     * 查询户型列表(不分页,用于导出/下拉等场景)
     *
     * @param houseType 户型
     * @return 户型集合
     */
    List<HzHouseType> selectHouseTypeList(HzHouseType houseType);

    /**
     * 分页查询户型列表(带项目名称)
     *
     * @param page 分页对象
     * @param ht 户型查询条件
     * @return 户型分页列表
     */
    IPage<HzHouseType> selectHouseTypeListPage(Page<HzHouseType> page, @Param("ht") HzHouseType ht);
}
