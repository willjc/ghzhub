package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzBatchAllocation;
import com.ruoyi.system.service.IHzBatchAllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 配租批次Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/batch")
public class HzBatchAllocationController extends BaseController {

    @Autowired
    private IHzBatchAllocationService batchAllocationService;

    /**
     * 查询配租批次列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:batch:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzBatchAllocation batch) {
        startPage();
        List<HzBatchAllocation> list = batchAllocationService.selectBatchAllocationList(batch);
        return getDataTable(list);
    }

    /**
     * 导出配租批次列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:batch:export')")
    @Log(title = "配租批次", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzBatchAllocation batch) {
        List<HzBatchAllocation> list = batchAllocationService.selectBatchAllocationList(batch);
        ExcelUtil<HzBatchAllocation> util = new ExcelUtil<HzBatchAllocation>(HzBatchAllocation.class);
        util.exportExcel(response, list, "配租批次数据");
    }

    /**
     * 获取配租批次详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:batch:query')")
    @GetMapping(value = "/{batchId}")
    public AjaxResult getInfo(@PathVariable("batchId") Long batchId) {
        return success(batchAllocationService.selectBatchAllocationById(batchId));
    }

    /**
     * 新增配租批次
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:batch:add')")
    @Log(title = "配租批次", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HzBatchAllocation batch) {
        return toAjax(batchAllocationService.insertBatchAllocation(batch));
    }

    /**
     * 修改配租批次
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:batch:edit')")
    @Log(title = "配租批次", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzBatchAllocation batch) {
        return toAjax(batchAllocationService.updateBatchAllocation(batch));
    }

    /**
     * 删除配租批次
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:batch:remove')")
    @Log(title = "配租批次", businessType = BusinessType.DELETE)
    @DeleteMapping("/{batchIds}")
    public AjaxResult remove(@PathVariable Long[] batchIds) {
        return toAjax(batchAllocationService.deleteBatchAllocationByIds(batchIds));
    }

    /**
     * 审批配租批次
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:batch:approve')")
    @Log(title = "配租批次审批", businessType = BusinessType.UPDATE)
    @PutMapping("/approve")
    public AjaxResult approve(@RequestBody HzBatchAllocation batch) {
        return toAjax(batchAllocationService.approveBatchAllocation(
                batch.getBatchId(),
                batch.getApproveStatus(),
                batch.getRemark()
        ));
    }

    /**
     * 作废配租批次
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:batch:cancel')")
    @Log(title = "配租批次作废", businessType = BusinessType.UPDATE)
    @PutMapping("/cancel/{batchId}")
    public AjaxResult cancel(@PathVariable Long batchId) {
        return toAjax(batchAllocationService.cancelBatchAllocation(batchId));
    }

    /**
     * 下载人员导入模板
     */
    @GetMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        batchAllocationService.downloadTenantTemplate(response);
    }

    /**
     * 根据项目ID获取可用房源列表
     */
    @GetMapping("/availableHouses")
    public AjaxResult getAvailableHouses(@RequestParam String projectIds) {
        return success(batchAllocationService.getAvailableHousesByProjects(projectIds));
    }

    /**
     * 导入人员数据
     */
    @PostMapping("/importTenants")
    public AjaxResult importTenants(MultipartFile file) throws Exception {
        List<Map<String, Object>> tenantList = batchAllocationService.importTenants(file);
        return success(tenantList);
    }

    /**
     * 保存批次分配（包含房源和人员匹配）
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:batch:add')")
    @Log(title = "保存批次分配", businessType = BusinessType.INSERT)
    @PostMapping("/saveAllocation")
    public AjaxResult saveAllocation(@RequestBody Map<String, Object> params) {
        return toAjax(batchAllocationService.saveBatchAllocation(params));
    }

    /**
     * 获取批次房源列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:batch:query')")
    @GetMapping("/{batchId}/houses")
    public AjaxResult getBatchHouses(@PathVariable Long batchId) {
        return success(batchAllocationService.getBatchHouses(batchId));
    }

    /**
     * 获取批次人员列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:batch:query')")
    @GetMapping("/{batchId}/tenants")
    public AjaxResult getBatchTenants(@PathVariable Long batchId) {
        return success(batchAllocationService.getBatchTenants(batchId));
    }
}
