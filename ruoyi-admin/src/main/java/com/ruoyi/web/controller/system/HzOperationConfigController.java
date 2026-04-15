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
import com.ruoyi.system.domain.HzOperationConfig;
import com.ruoyi.system.service.IHzOperationConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 运营配置Controller
 *
 * @author ruoyi
 * @date 2026-01-12
 */
@RestController
@RequestMapping("/gangzhu/config")
public class HzOperationConfigController extends BaseController {
    @Autowired
    private IHzOperationConfigService configService;

    /**
     * 查询运营配置列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:config:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzOperationConfig config) {
        Page<HzOperationConfig> page = PageUtils.getPage();
        IPage<HzOperationConfig> pageResult = configService.selectOperationConfigPage(page, config);
        TableDataInfo data = new TableDataInfo();
        data.setCode(HttpStatus.SUCCESS);
        data.setRows(pageResult.getRecords());
        data.setTotal(pageResult.getTotal());
        data.setMsg("查询成功");
        return data;
    }

    /**
     * 导出运营配置列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:config:export')")
    @Log(title = "运营配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzOperationConfig config) {
        List<HzOperationConfig> list = configService.selectConfigList(config);
        ExcelUtil<HzOperationConfig> util = new ExcelUtil<>(HzOperationConfig.class);
        util.exportExcel(response, list, "运营配置数据");
    }

    /**
     * 获取运营配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:config:query')")
    @GetMapping(value = "/{configId}")
    public AjaxResult getInfo(@PathVariable("configId") Long configId) {
        return success(configService.getById(configId));
    }

    /**
     * 新增运营配置
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:config:add')")
    @Log(title = "运营配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HzOperationConfig config) {
        return toAjax(configService.insertConfig(config));
    }

    /**
     * 修改运营配置
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:config:edit')")
    @Log(title = "运营配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzOperationConfig config) {
        return toAjax(configService.updateConfig(config));
    }

    /**
     * 删除运营配置
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:config:remove')")
    @Log(title = "运营配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public AjaxResult remove(@PathVariable Long[] configIds) {
        return toAjax(configService.deleteConfigByIds(configIds));
    }
}
