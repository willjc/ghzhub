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
import com.ruoyi.system.domain.HzTenant;
import com.ruoyi.system.service.IHzTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 租户管理Controller
 *
 * @author ruoyi
 */
@RestController("adminTenantController")
@RequestMapping("/system/tenant")
public class HzTenantController extends BaseController
{
    @Autowired
    private IHzTenantService tenantService;

    /**
     * 查询租户列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:tenant:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzTenant tenant)
    {
        Page<HzTenant> page = PageUtils.getPage();
        IPage<HzTenant> pageResult = tenantService.selectTenantPage(tenant, (int)page.getCurrent(), (int)page.getSize());

        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setMsg("查询成功");
        rspData.setRows(pageResult.getRecords());
        rspData.setTotal(pageResult.getTotal());
        return rspData;
    }

    /**
     * 导出租户列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:tenant:export')")
    @Log(title = "租户管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzTenant tenant)
    {
        List<HzTenant> list = tenantService.selectTenantList(tenant);
        ExcelUtil<HzTenant> util = new ExcelUtil<HzTenant>(HzTenant.class);
        util.exportExcel(response, list, "租户数据");
    }

    /**
     * 获取租户详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:tenant:query')")
    @GetMapping(value = "/{tenantId}")
    public AjaxResult getInfo(@PathVariable("tenantId") Long tenantId)
    {
        return success(tenantService.selectTenantById(tenantId));
    }

    /**
     * 修改租户
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:tenant:edit')")
    @Log(title = "租户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzTenant tenant)
    {
        return toAjax(tenantService.updateTenant(tenant));
    }
}
