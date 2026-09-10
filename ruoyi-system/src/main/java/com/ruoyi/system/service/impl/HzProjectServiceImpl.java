package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.HzProject;
import com.ruoyi.system.mapper.HzProjectMapper;
import com.ruoyi.system.service.IHzProjectService;
import com.ruoyi.system.service.IHzRoleProjectService;
import com.ruoyi.common.core.domain.model.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
    @Autowired
    private IHzRoleProjectService roleProjectService;

    /**
     * 查询项目列表
     *
     * @param project 项目查询条件
     * @return 项目列表
     */
    @Override
    public List<HzProject> selectProjectList(HzProject project)
    {
        // 注入项目权限过滤
        injectProjectFilter(project);
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
        // 注入项目权限过滤
        injectProjectFilter(project);
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
     * 导入项目数据（按项目编码判重；支持 updateSupport 覆盖更新）
     *
     * @param projectList 导入的项目列表
     * @param updateSupport 是否支持更新已存在数据
     * @return 导入结果消息
     */
    @Override
    public String importProject(List<HzProject> projectList, boolean updateSupport)
    {
        if (projectList == null || projectList.isEmpty())
        {
            throw new RuntimeException("导入项目数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (HzProject project : projectList)
        {
            try
            {
                if (project.getProjectName() == null || project.getProjectName().trim().isEmpty())
                {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、项目名称不能为空");
                    continue;
                }
                // 跳过模板自带的示例行（不参与统计）
                if (project.getProjectName().contains("导入前请删除本行"))
                {
                    continue;
                }
                // 按项目名称判重
                HzProject exist = this.getOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<HzProject>()
                        .eq(HzProject::getProjectName, project.getProjectName().trim())
                        .eq(HzProject::getDelFlag, "0"));
                if (exist != null)
                {
                    if (updateSupport)
                    {
                        project.setProjectId(exist.getProjectId());
                        this.updateById(project);
                        successNum++;
                        successMsg.append("<br/>").append(successNum).append("、项目 ").append(project.getProjectName()).append(" 更新成功");
                    }
                    else
                    {
                        failureNum++;
                        failureMsg.append("<br/>").append(failureNum).append("、项目 ").append(project.getProjectName()).append(" 已存在");
                    }
                    continue;
                }
                project.setDelFlag("0");
                this.save(project);
                successNum++;
                successMsg.append("<br/>").append(successNum).append("、项目 ").append(project.getProjectName()).append(" 导入成功");
            }
            catch (Exception e)
            {
                failureNum++;
                failureMsg.append("<br/>").append(failureNum).append("、项目 ").append(project.getProjectName() == null ? "未知" : project.getProjectName()).append(" 导入失败：").append(e.getMessage());
            }
        }
        StringBuilder result = new StringBuilder();
        result.append("导入完成：成功 ").append(successNum).append(" 条，失败 ").append(failureNum).append(" 条。");
        if (failureMsg.length() > 0)
        {
            result.append(failureMsg);
        }
        return result.toString();
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

    /**
     * 注入项目权限过滤：
     * - 匿名用户（H5公开接口）：跳过过滤，查询全部项目
     * - 管理方/超管：不限制（projectIds=null）
     * - 物业角色：只查其绑定的项目
     */
    private void injectProjectFilter(HzProject project)
    {
        // 匿名用户（H5公开接口）跳过权限过滤，避免 getLoginUser() 抛异常
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof LoginUser)) {
            return;
        }
        List<Long> projectIds = roleProjectService.getCurrentUserProjectIds();
        if (projectIds != null) {
            if (project.getParams() == null) {
                project.setParams(new HashMap<>());
            }
            project.getParams().put("projectIds", projectIds);
        }
    }
}
