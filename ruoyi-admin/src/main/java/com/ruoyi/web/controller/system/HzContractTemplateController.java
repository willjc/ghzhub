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
import com.ruoyi.system.domain.HzContractTemplate;
import com.ruoyi.system.service.IHzContractTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 合同模版Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/contract/template")
public class HzContractTemplateController extends BaseController {

    @Autowired
    private IHzContractTemplateService templateService;

    /**
     * 查询合同模版列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:template:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzContractTemplate template) {
        Page<HzContractTemplate> page = PageUtils.getPage();
        IPage<HzContractTemplate> pageResult = templateService.selectTemplatePage(page, template);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(pageResult.getRecords());
        rspData.setTotal(pageResult.getTotal());
        rspData.setMsg("查询成功");
        return rspData;
    }

    /**
     * 导出合同模版列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:template:export')")
    @Log(title = "合同模版", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzContractTemplate template) {
        List<HzContractTemplate> list = templateService.selectTemplateList(template);
        ExcelUtil<HzContractTemplate> util = new ExcelUtil<>(HzContractTemplate.class);
        util.exportExcel(response, list, "合同模版数据");
    }

    /**
     * 获取合同模版详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:template:query')")
    @GetMapping(value = "/{templateId}")
    public AjaxResult getInfo(@PathVariable("templateId") Long templateId) {
        return success(templateService.selectTemplateById(templateId));
    }

    /**
     * 根据合同类型获取模版
     */
    @GetMapping("/type/{contractType}")
    public AjaxResult getByType(@PathVariable("contractType") String contractType) {
        return success(templateService.selectTemplateByType(contractType));
    }

    /**
     * 新增合同模版
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:template:add')")
    @Log(title = "合同模版", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HzContractTemplate template) {
        return toAjax(templateService.insertTemplate(template));
    }

    /**
     * 修改合同模版
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:template:edit')")
    @Log(title = "合同模版", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzContractTemplate template) {
        return toAjax(templateService.updateTemplate(template));
    }

    /**
     * 删除合同模版
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:template:remove')")
    @Log(title = "合同模版", businessType = BusinessType.DELETE)
    @DeleteMapping("/{templateIds}")
    public AjaxResult remove(@PathVariable Long[] templateIds) {
        return toAjax(templateService.deleteTemplateByIds(templateIds));
    }
}
