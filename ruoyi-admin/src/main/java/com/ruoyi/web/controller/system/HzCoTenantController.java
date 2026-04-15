package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzCoTenant;
import com.ruoyi.system.service.IHzCoTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 合租户管理Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/gangzhu/cohabitant")
public class HzCoTenantController extends BaseController {
    @Autowired
    private IHzCoTenantService coTenantService;

    /**
     * 查询合租户申请列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:cohabitant:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzCoTenant coTenant) {
        Page<HzCoTenant> page = PageUtils.getPage();
        IPage<HzCoTenant> pageResult = coTenantService.selectCoTenantPage(coTenant, (int)page.getCurrent(), (int)page.getSize());
        TableDataInfo data = new TableDataInfo();
        data.setCode(HttpStatus.SUCCESS);
        data.setRows(pageResult.getRecords());
        data.setTotal(pageResult.getTotal());
        data.setMsg("查询成功");
        return data;
    }

    /**
     * 导出合租户申请列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:cohabitant:export')")
    @Log(title = "合租户管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzCoTenant coTenant) {
        List<HzCoTenant> list = coTenantService.selectCoTenantList(coTenant);
        ExcelUtil<HzCoTenant> util = new ExcelUtil<HzCoTenant>(HzCoTenant.class);
        util.exportExcel(response, list, "合租户申请数据");
    }

    /**
     * 获取合租户申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:cohabitant:query')")
    @GetMapping(value = "/{coTenantId}")
    public AjaxResult getInfo(@PathVariable("coTenantId") Long coTenantId) {
        return success(coTenantService.selectCoTenantById(coTenantId));
    }

    /**
     * 新增合租户申请
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:cohabitant:add')")
    @Log(title = "合租户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HzCoTenant coTenant) {
        return toAjax(coTenantService.insertCoTenant(coTenant));
    }

    /**
     * 修改合租户申请
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:cohabitant:edit')")
    @Log(title = "合租户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzCoTenant coTenant) {
        return toAjax(coTenantService.updateCoTenant(coTenant));
    }

    /**
     * 删除合租户申请
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:cohabitant:remove')")
    @Log(title = "合租户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{coTenantIds}")
    public AjaxResult remove(@PathVariable Long[] coTenantIds) {
        for (Long coTenantId : coTenantIds) {
            coTenantService.deleteCoTenantById(coTenantId);
        }
        return success();
    }

    /**
     * 审核合租户申请
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:cohabitant:audit')")
    @Log(title = "合租户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/audit")
    public AjaxResult audit(@RequestBody HzCoTenant coTenant) {
        return toAjax(coTenantService.auditCoTenant(
            coTenant.getCoTenantId(),
            coTenant.getStatus(),
            coTenant.getRemark()
        ));
    }
}
