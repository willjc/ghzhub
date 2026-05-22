package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzServiceCompany;
import com.ruoyi.system.service.IHzServiceCompanyService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 服务公司 Controller（管理端）
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/gangzhu/serviceCompany")
public class HzServiceCompanyController extends BaseController
{
    @Autowired
    private IHzServiceCompanyService companyService;

    /**
     * 分页查询服务公司列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:serviceCompany:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzServiceCompany company)
    {
        Page<HzServiceCompany> page = PageUtils.getPage();
        IPage<HzServiceCompany> result = companyService.selectCompanyPage(
                company, (int) page.getCurrent(), (int) page.getSize());
        TableDataInfo data = new TableDataInfo();
        data.setCode(HttpStatus.SUCCESS);
        data.setRows(result.getRecords());
        data.setTotal(result.getTotal());
        data.setMsg("查询成功");
        return data;
    }

    /**
     * 查询启用中的服务公司（订单分配下拉用）
     */
    @PreAuthorize("@ss.hasAnyPermi('gangzhu:cleanOrder:assign,gangzhu:moveOrder:assign,gangzhu:serviceCompany:query')")
    @GetMapping("/active")
    public AjaxResult activeList(@RequestParam(required = false) String orderType)
    {
        List<HzServiceCompany> list = companyService.selectActiveCompaniesByOrderType(orderType);
        return success(list);
    }

    /**
     * 导出
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:serviceCompany:export')")
    @Log(title = "服务公司", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzServiceCompany company)
    {
        List<HzServiceCompany> list = companyService.selectCompanyList(company);
        ExcelUtil<HzServiceCompany> util = new ExcelUtil<>(HzServiceCompany.class);
        util.exportExcel(response, list, "服务公司数据");
    }

    /**
     * 详情
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:serviceCompany:query')")
    @GetMapping("/{companyId}")
    public AjaxResult getInfo(@PathVariable Long companyId)
    {
        return success(companyService.selectCompanyById(companyId));
    }

    /**
     * 新增
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:serviceCompany:add')")
    @Log(title = "服务公司", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HzServiceCompany company)
    {
        return toAjax(companyService.insertCompany(company));
    }

    /**
     * 修改
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:serviceCompany:edit')")
    @Log(title = "服务公司", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzServiceCompany company)
    {
        return toAjax(companyService.updateCompany(company));
    }

    /**
     * 删除
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:serviceCompany:remove')")
    @Log(title = "服务公司", businessType = BusinessType.DELETE)
    @DeleteMapping("/{companyIds}")
    public AjaxResult remove(@PathVariable Long[] companyIds)
    {
        return toAjax(companyService.deleteCompanyByIds(companyIds));
    }
}
