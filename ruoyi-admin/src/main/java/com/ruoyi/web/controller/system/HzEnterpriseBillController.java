package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzEnterpriseBill;
import com.ruoyi.system.service.IHzEnterpriseBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 企业账单Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/enterpriseBill")
public class HzEnterpriseBillController extends BaseController {

    @Autowired
    private IHzEnterpriseBillService enterpriseBillService;

    /**
     * 查询企业账单列表
     */
    @PreAuthorize("@ss.hasPermi('system:enterpriseBill:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzEnterpriseBill enterpriseBill) {
        startPage();
        List<HzEnterpriseBill> list = enterpriseBillService.selectEnterpriseBillList(enterpriseBill);
        return getDataTable(list);
    }

    /**
     * 导出企业账单列表
     */
    @PreAuthorize("@ss.hasPermi('system:enterpriseBill:export')")
    @Log(title = "企业账单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzEnterpriseBill enterpriseBill) {
        List<HzEnterpriseBill> list = enterpriseBillService.selectEnterpriseBillList(enterpriseBill);
        ExcelUtil<HzEnterpriseBill> util = new ExcelUtil<>(HzEnterpriseBill.class);
        util.exportExcel(response, list, "企业账单数据");
    }

    /**
     * 获取企业账单详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:enterpriseBill:query')")
    @GetMapping(value = "/{billId}")
    public AjaxResult getInfo(@PathVariable("billId") Long billId) {
        return success(enterpriseBillService.selectEnterpriseBillById(billId));
    }

    /**
     * 根据批次ID获取账单
     */
    @GetMapping(value = "/batch/{batchId}")
    public AjaxResult getBillByBatchId(@PathVariable("batchId") Long batchId) {
        return success(enterpriseBillService.selectBillByBatchId(batchId));
    }

    /**
     * 新增企业账单
     */
    @PreAuthorize("@ss.hasPermi('system:enterpriseBill:add')")
    @Log(title = "企业账单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HzEnterpriseBill enterpriseBill) {
        return toAjax(enterpriseBillService.insertEnterpriseBill(enterpriseBill));
    }

    /**
     * 修改企业账单
     */
    @PreAuthorize("@ss.hasPermi('system:enterpriseBill:edit')")
    @Log(title = "企业账单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzEnterpriseBill enterpriseBill) {
        return toAjax(enterpriseBillService.updateEnterpriseBill(enterpriseBill));
    }

    /**
     * 删除企业账单
     */
    @PreAuthorize("@ss.hasPermi('system:enterpriseBill:remove')")
    @Log(title = "企业账单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{billIds}")
    public AjaxResult remove(@PathVariable Long[] billIds) {
        return toAjax(enterpriseBillService.deleteEnterpriseBillByIds(billIds));
    }

    /**
     * 审核企业账单
     */
    @PreAuthorize("@ss.hasPermi('system:enterpriseBill:approve')")
    @Log(title = "审核企业账单", businessType = BusinessType.UPDATE)
    @PutMapping("/approve/{billId}")
    public AjaxResult approve(@PathVariable Long billId, @RequestBody(required = false) com.alibaba.fastjson2.JSONObject params) {
        boolean approved = true;
        if (params != null && params.containsKey("approved")) {
            approved = params.getBoolean("approved");
        }
        String approveBy = getUsername();
        int result = enterpriseBillService.approveBill(billId, approved, approveBy);
        return toAjax(result);
    }

    /**
     * 管理端线下支付企业账单
     */
    @PreAuthorize("@ss.hasPermi('system:enterpriseBill:pay')")
    @Log(title = "企业账单支付", businessType = BusinessType.UPDATE)
    @PostMapping("/pay/{billId}")
    public AjaxResult adminPay(@PathVariable Long billId, @RequestBody(required = false) com.alibaba.fastjson2.JSONObject params) {
        String payMethod = null;
        String payVoucher = null;
        if (params != null) {
            payMethod = params.getString("payMethod");
            payVoucher = params.getString("payVoucher");
        }
        int result = enterpriseBillService.adminPayBill(billId, payMethod, payVoucher);
        return toAjax(result);
    }
}
