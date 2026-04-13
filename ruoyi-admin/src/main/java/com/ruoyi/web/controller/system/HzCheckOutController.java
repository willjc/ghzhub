package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzCheckoutApply;
import com.ruoyi.system.domain.HzCheckoutApplyVO;
import com.ruoyi.system.domain.HzCheckoutRecord;
import com.ruoyi.system.service.IHzCheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 退租管理Controller
 *
 * @author ruoyi
 */
@RestController("adminCheckOutController")
@RequestMapping("/system/checkout")
public class HzCheckOutController extends BaseController {

    @Autowired
    private IHzCheckoutService checkoutService;

    /**
     * 查询退租申请列表（包含合同和房源完整信息）
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:checkout:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzCheckoutApply checkoutApply) {
        Page<HzCheckoutApply> page = PageUtils.getPage();
        return checkoutService.selectCheckoutApplyListWithDetails(page, checkoutApply);
    }

    /**
     * 导出退租申请列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:checkout:export')")
    @Log(title = "退租管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzCheckoutApply checkoutApply) {
        List<HzCheckoutApply> list = checkoutService.selectCheckoutApplyList(checkoutApply);
        ExcelUtil<HzCheckoutApply> util = new ExcelUtil<HzCheckoutApply>(HzCheckoutApply.class);
        util.exportExcel(response, list, "退租申请数据");
    }

    /**
     * 获取退租申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:checkout:query')")
    @GetMapping(value = "/{applyId}")
    public AjaxResult getInfo(@PathVariable("applyId") Long applyId) {
        HzCheckoutApply apply = checkoutService.selectCheckoutApplyByApplyId(applyId);
        return success(apply);
    }

    /**
     * 新增退租申请
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:checkout:add')")
    @Log(title = "退租管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HzCheckoutApply checkoutApply) {
        return toAjax(checkoutService.insertCheckoutApply(checkoutApply));
    }

    /**
     * 修改退租申请
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:checkout:edit')")
    @Log(title = "退租管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzCheckoutApply checkoutApply) {
        return toAjax(checkoutService.updateCheckoutApply(checkoutApply));
    }

    /**
     * 删除退租申请
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:checkout:remove')")
    @Log(title = "退租管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{applyIds}")
    public AjaxResult remove(@PathVariable Long[] applyIds) {
        return toAjax(checkoutService.deleteCheckoutApplyByApplyIds(applyIds));
    }

    /**
     * 审批退租申请
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:checkout:approve')")
    @Log(title = "审批退租申请", businessType = BusinessType.UPDATE)
    @PostMapping("/approve")
    public AjaxResult approve(@RequestBody Map<String, Object> requestData) {
        Long applyId = Long.valueOf(requestData.get("applyId").toString());
        String applyStatus = requestData.get("applyStatus").toString();
        String approveOpinion = requestData.get("approveOpinion") != null ?
                                 requestData.get("approveOpinion").toString() : "";
        String approveBy = SecurityUtils.getUsername();

        int result = checkoutService.approveCheckoutApply(applyId, applyStatus, approveOpinion, approveBy);
        return toAjax(result);
    }

    /**
     * 保存退租费用计算
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:checkout:calculate')")
    @Log(title = "计算退租费用", businessType = BusinessType.UPDATE)
    @PostMapping("/calculate")
    public AjaxResult calculate(@RequestBody HzCheckoutApply checkoutApply) {
        int result = checkoutService.saveCheckoutCalculate(checkoutApply);
        return toAjax(result);
    }

    /**
     * 获取退租记录详情
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:checkout:query')")
    @GetMapping("/record/{recordId}")
    public AjaxResult getRecordInfo(@PathVariable("recordId") Long recordId) {
        HzCheckoutRecord record = checkoutService.selectCheckoutRecordByRecordId(recordId);
        return success(record);
    }

    /**
     * 根据申请ID获取退租记录
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:checkout:query')")
    @GetMapping("/record/byApply/{applyId}")
    public AjaxResult getRecordByApplyId(@PathVariable("applyId") Long applyId) {
        HzCheckoutRecord record = checkoutService.selectCheckoutRecordByApplyId(applyId);
        return success(record);
    }

    /**
     * 获取合同的账单列表（用于退租审批时查看缴费记录）
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:checkout:query')")
    @GetMapping("/bills/{contractId}")
    public AjaxResult getContractBills(@PathVariable("contractId") Long contractId) {
        List<Map<String, Object>> bills = checkoutService.selectContractBillList(contractId);
        return success(bills);
    }
}
