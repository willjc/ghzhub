package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzEnterprise;
import com.ruoyi.system.service.IHzEnterpriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 企业客户Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/enterprise")
public class HzEnterpriseController extends BaseController {

    @Autowired
    private IHzEnterpriseService enterpriseService;

    /**
     * 查询企业客户列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:enterprise:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzEnterprise enterprise) {
        Page<HzEnterprise> page = PageUtils.getPage();
        IPage<HzEnterprise> pageResult = enterpriseService.selectEnterprisePage(page, enterprise);
        TableDataInfo data = new TableDataInfo();
        data.setCode(HttpStatus.SUCCESS);
        data.setRows(pageResult.getRecords());
        data.setTotal(pageResult.getTotal());
        data.setMsg("查询成功");
        return data;
    }

    /**
     * 导出企业客户列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:enterprise:export')")
    @Log(title = "企业客户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzEnterprise enterprise) {
        List<HzEnterprise> list = enterpriseService.selectEnterpriseList(enterprise);
        ExcelUtil<HzEnterprise> util = new ExcelUtil<HzEnterprise>(HzEnterprise.class);
        util.exportExcel(response, list, "企业客户数据");
    }

    /**
     * 获取企业客户详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:enterprise:query')")
    @GetMapping(value = "/{enterpriseId}")
    public AjaxResult getInfo(@PathVariable("enterpriseId") Long enterpriseId) {
        return success(enterpriseService.selectEnterpriseById(enterpriseId));
    }

    /**
     * 新增企业客户
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:enterprise:add')")
    @Log(title = "企业客户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HzEnterprise enterprise) {
        return toAjax(enterpriseService.insertEnterprise(enterprise));
    }

    /**
     * 修改企业客户
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:enterprise:edit')")
    @Log(title = "企业客户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzEnterprise enterprise) {
        return toAjax(enterpriseService.updateEnterprise(enterprise));
    }

    /**
     * 删除企业客户
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:enterprise:remove')")
    @Log(title = "企业客户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{enterpriseIds}")
    public AjaxResult remove(@PathVariable Long[] enterpriseIds) {
        return toAjax(enterpriseService.deleteEnterpriseByIds(enterpriseIds));
    }
}
