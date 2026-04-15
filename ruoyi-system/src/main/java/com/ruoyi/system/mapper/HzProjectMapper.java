package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.HzProject;
import java.util.List;

/**
 * 项目Mapper接口
 *
 * @author ruoyi
 * @date 2025-11-17
 */
@Mapper
public interface HzProjectMapper extends BaseMapper<HzProject>
{
    /**
     * 查询项目列表(带总房源数和可用房源数统计)
     *
     * @param project 项目
     * @return 项目集合
     */
    List<HzProject> selectProjectAllocationList(HzProject project);

    /**
     * 分页查询项目列表(带总房源数和可用房源数统计)
     *
     * @param page 分页对象
     * @param project 项目查询条件
     * @return 项目分页列表
     */
    IPage<HzProject> selectProjectAllocationPage(Page<HzProject> page, @Param("project") HzProject project);

    /**
     * 查询单个项目详情(带实时统计)
     *
     * @param projectId 项目ID
     * @return 项目
     */
    HzProject selectProjectByIdWithStats(Long projectId);
}
