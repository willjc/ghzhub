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
import com.ruoyi.system.domain.HzCommitmentTemplate;
import com.ruoyi.system.service.IHzCommitmentTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 承诺书模板Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/commitment/template")
public class HzCommitmentTemplateController extends BaseController {

    @Autowired
    private IHzCommitmentTemplateService commitmentTemplateService;

    /**
     * 查询承诺书模板列表
     */
    @PreAuthorize("@ss.hasPermi('system:commitment:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzCommitmentTemplate template) {
        Page<HzCommitmentTemplate> page = PageUtils.getPage();
        IPage<HzCommitmentTemplate> pageResult = commitmentTemplateService.selectCommitmentTemplatePage(page, template);
        TableDataInfo data = new TableDataInfo();
        data.setCode(HttpStatus.SUCCESS);
        data.setRows(pageResult.getRecords());
        data.setTotal(pageResult.getTotal());
        data.setMsg("查询成功");
        return data;
    }

    /**
     * 导出承诺书模板列表
     */
    @PreAuthorize("@ss.hasPermi('system:commitment:export')")
    @Log(title = "承诺书模板", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzCommitmentTemplate template) {
        List<HzCommitmentTemplate> list = commitmentTemplateService.selectTemplateList(template);
        ExcelUtil<HzCommitmentTemplate> util = new ExcelUtil<>(HzCommitmentTemplate.class);
        util.exportExcel(response, list, "承诺书模板数据");
    }

    /**
     * 获取承诺书模板详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:commitment:query')")
    @GetMapping(value = "/{templateId}")
    public AjaxResult getInfo(@PathVariable("templateId") Long templateId) {
        return success(commitmentTemplateService.selectTemplateById(templateId));
    }

    /**
     * 根据承诺书类型获取默认模板
     */
    @GetMapping("/default/{commitmentType}")
    public AjaxResult getDefaultTemplate(@PathVariable("commitmentType") String commitmentType) {
        return success(commitmentTemplateService.selectDefaultTemplateByType(commitmentType));
    }

    /**
     * 新增承诺书模板
     */
    @PreAuthorize("@ss.hasPermi('system:commitment:add')")
    @Log(title = "承诺书模板", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HzCommitmentTemplate template) {
        return toAjax(commitmentTemplateService.insertTemplate(template));
    }

    /**
     * 修改承诺书模板
     */
    @PreAuthorize("@ss.hasPermi('system:commitment:edit')")
    @Log(title = "承诺书模板", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzCommitmentTemplate template) {
        return toAjax(commitmentTemplateService.updateTemplate(template));
    }

    /**
     * 删除承诺书模板
     */
    @PreAuthorize("@ss.hasPermi('system:commitment:remove')")
    @Log(title = "承诺书模板", businessType = BusinessType.DELETE)
    @DeleteMapping("/{templateIds}")
    public AjaxResult remove(@PathVariable Long[] templateIds) {
        return toAjax(commitmentTemplateService.deleteTemplateByIds(templateIds));
    }
}
