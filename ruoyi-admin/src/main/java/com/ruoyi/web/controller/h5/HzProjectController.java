package com.ruoyi.web.controller.h5;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.HzProject;
import com.ruoyi.system.service.IHzProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目Controller (H5端)
 *
 * @author ruoyi
 * @date 2025-11-17
 */
@RestController
@RequestMapping("/h5/project")
public class HzProjectController extends BaseController
{
    @Autowired
    private IHzProjectService projectService;

    /**
     * 查询项目列表
     */
    @GetMapping("/list")
    public TableDataInfo list(HzProject project)
    {
        startPage();
        List<HzProject> list = projectService.selectProjectList(project);
        return getDataTable(list);
    }

    /**
     * 分页查询项目列表
     */
    @GetMapping("/page")
    public AjaxResult page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            HzProject project)
    {
        // 设置状态为正常(H5端只展示正常状态的项目)
        project.setStatus("0");
        IPage<HzProject> page = projectService.selectProjectPage(project, pageNum, pageSize);
        return AjaxResult.success(page);
    }

    /**
     * 获取项目详细信息
     */
    @GetMapping(value = "/{projectId}")
    public AjaxResult getInfo(@PathVariable("projectId") Long projectId)
    {
        return success(projectService.selectProjectById(projectId));
    }

    /**
     * 根据项目类型查询项目列表
     */
    @GetMapping("/listByType/{projectType}")
    public AjaxResult listByType(
            @PathVariable("projectType") String projectType,
            @RequestParam(value = "projectName", required = false) String projectName)
    {
        HzProject project = new HzProject();

        // 如果projectType不为null且不是"null"字符串，也不是"0"（全部类型），才设置项目类型
        if (projectType != null && !"null".equals(projectType) && !"0".equals(projectType)) {
            project.setProjectType(projectType);
        }

        project.setStatus("0"); // 只查询正常状态

        // 如果有项目名称参数，设置到查询条件中
        if (projectName != null && !projectName.trim().isEmpty()) {
            project.setProjectName(projectName.trim());
        }

        List<HzProject> list = projectService.selectProjectList(project);
        return success(list);
    }
}
