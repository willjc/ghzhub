package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.HzProject;
import com.ruoyi.system.mapper.HzProjectMapper;
import com.ruoyi.system.service.IHzProjectService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 项目Service业务层处理
 *
 * @author ruoyi
 * @date 2025-11-17
 */
@Service
public class HzProjectServiceImpl extends ServiceImpl<HzProjectMapper, HzProject> implements IHzProjectService
{
    /**
     * 查询项目列表
     *
     * @param project 项目查询条件
     * @return 项目列表
     */
    @Override
    public List<HzProject> selectProjectList(HzProject project)
    {
        // 使用自定义SQL查询,包含统计总房源数和可用房源数
        return this.baseMapper.selectProjectAllocationList(project);
    }

    /**
     * 分页查询项目列表（带统计）
     *
     * @param project 项目查询条件
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @return 项目分页列表
     */
    @Override
    public IPage<HzProject> selectProjectPage(HzProject project, int pageNum, int pageSize)
    {
        Page<HzProject> page = new Page<>(pageNum, pageSize);
        return this.baseMapper.selectProjectAllocationPage(page, project);
    }

    /**
     * 查询项目详情（带实时统计）
     *
     * @param projectId 项目ID
     * @return 项目
     */
    @Override
    public HzProject selectProjectById(Long projectId)
    {
        // 使用自定义SQL查询，包含实时统计的总房源数和可用房源数
        return this.baseMapper.selectProjectByIdWithStats(projectId);
    }

    /**
     * 新增项目
     *
     * @param project 项目
     * @return 结果
     */
    @Override
    public int insertProject(HzProject project)
    {
        return this.save(project) ? 1 : 0;
    }

    /**
     * 修改项目
     *
     * @param project 项目
     * @return 结果
     */
    @Override
    public int updateProject(HzProject project)
    {
        return this.updateById(project) ? 1 : 0;
    }

    /**
     * 删除项目
     *
     * @param projectId 项目ID
     * @return 结果
     */
    @Override
    public int deleteProjectById(Long projectId)
    {
        return this.removeById(projectId) ? 1 : 0;
    }
}
