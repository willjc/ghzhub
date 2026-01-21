package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.HzProject;

import java.util.List;

/**
 * 项目Service接口
 *
 * @author ruoyi
 * @date 2025-11-17
 */
public interface IHzProjectService extends IService<HzProject>
{
    /**
     * 查询项目列表
     *
     * @param project 项目查询条件
     * @return 项目列表
     */
    List<HzProject> selectProjectList(HzProject project);

    /**
     * 分页查询项目列表
     *
     * @param project 项目查询条件
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @return 项目分页列表
     */
    IPage<HzProject> selectProjectPage(HzProject project, int pageNum, int pageSize);

    /**
     * 查询项目详情
     *
     * @param projectId 项目ID
     * @return 项目
     */
    HzProject selectProjectById(Long projectId);

    /**
     * 新增项目
     *
     * @param project 项目
     * @return 结果
     */
    int insertProject(HzProject project);

    /**
     * 修改项目
     *
     * @param project 项目
     * @return 结果
     */
    int updateProject(HzProject project);

    /**
     * 删除项目
     *
     * @param projectId 项目ID
     * @return 结果
     */
    int deleteProjectById(Long projectId);
}
