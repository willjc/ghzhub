package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzProject;
import com.ruoyi.system.domain.HzHouseType;
import com.ruoyi.system.service.IHzProjectService;
import com.ruoyi.system.service.IHzHouseTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;

/**
 * 项目管理Controller
 *
 * @author ruoyi
 */
@RestController("adminProjectController")
@RequestMapping("/system/project")
public class HzProjectController extends BaseController
{
    @Autowired
    private IHzProjectService projectService;

    @Autowired
    private IHzHouseTypeService houseTypeService;

    /**
     * 查询项目列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:project:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzProject project)
    {
        Page<HzProject> page = PageUtils.getPage();
        IPage<HzProject> pageResult = projectService.selectProjectPage(project, (int)page.getCurrent(), (int)page.getSize());

        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setMsg("查询成功");
        rspData.setRows(pageResult.getRecords());
        rspData.setTotal(pageResult.getTotal());
        return rspData;
    }

    /**
     * 导出项目列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:project:export')")
    @Log(title = "项目管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzProject project)
    {
        List<HzProject> list = projectService.selectProjectList(project);
        ExcelUtil<HzProject> util = new ExcelUtil<HzProject>(HzProject.class);
        util.exportExcel(response, list, "项目数据");
    }

    /**
     * 获取项目详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:project:query')")
    @GetMapping(value = "/{projectId}")
    public AjaxResult getInfo(@PathVariable("projectId") Long projectId)
    {
        return success(projectService.selectProjectById(projectId));
    }

    /**
     * 新增项目
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:project:add')")
    @Log(title = "项目管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HzProject project)
    {
        return toAjax(projectService.insertProject(project));
    }

    /**
     * 修改项目
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:project:edit')")
    @Log(title = "项目管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzProject project)
    {
        return toAjax(projectService.updateProject(project));
    }

    /**
     * 删除项目
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:project:remove')")
    @Log(title = "项目管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{projectIds}")
    public AjaxResult remove(@PathVariable Long[] projectIds)
    {
        int count = 0;
        for (Long projectId : projectIds)
        {
            count += projectService.deleteProjectById(projectId);
        }
        return toAjax(count);
    }

    /**
     * 批量生成标准房型（一室到六室）
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:project:edit')")
    @Log(title = "项目管理", businessType = BusinessType.INSERT)
    @PostMapping("/{projectId}/generateHouseTypes")
    public AjaxResult generateHouseTypes(@PathVariable Long projectId)
    {
        HzProject project = projectService.selectProjectById(projectId);
        if (project == null)
        {
            return error("项目不存在");
        }

        int successCount = 0;

        // 生成一室到六室的标准房型
        String[] roomNames = {"一室", "两室", "三室", "四室", "五室", "六室"};

        for (int i = 1; i <= 6; i++)
        {
            HzHouseType houseType = new HzHouseType();
            houseType.setProjectId(projectId);
            houseType.setHouseTypeName(roomNames[i - 1]);
            houseType.setHouseTypeCode(project.getProjectCode() + "-" + i + "S");
            houseType.setBedroomCount(i);
            houseType.setLivingroomCount(i);
            houseType.setBathroomCount(i >= 3 ? 2 : 1); // 三室及以上配2个卫生间
            houseType.setKitchenCount(1);
            houseType.setBalconyCount(i >= 2 ? 1 : 0); // 二室及以上配阳台

            // 设置典型面积（估算：一室约60平，每增加一室增加30平）
            BigDecimal typicalArea = BigDecimal.valueOf(60 + (i - 1) * 30);
            houseType.setTypicalArea(typicalArea);

            houseType.setStatus("0");
            houseType.setSortOrder(i);

            try
            {
                houseTypeService.insertHouseType(houseType);
                successCount++;
            }
            catch (Exception e)
            {
                // 如果房型已存在，跳过
                logger.error("生成房型失败: " + houseType.getHouseTypeName(), e);
            }
        }

        return success("成功生成 " + successCount + " 种房型");
    }
}
