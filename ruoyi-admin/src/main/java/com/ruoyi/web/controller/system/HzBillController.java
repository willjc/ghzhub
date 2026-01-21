package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzBill;
import com.ruoyi.system.domain.HzBillVO;
import com.ruoyi.system.service.IHzBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 账单管理Controller
 *
 * @author ruoyi
 */
@RestController("adminBillController")
@RequestMapping("/system/bill")
public class HzBillController extends BaseController
{
    @Autowired
    private IHzBillService billService;

    /**
     * 查询账单列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:bill:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzBill bill)
    {
        IPage<HzBillVO> page = billService.selectBillVOPage(bill);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setRows(page.getRecords());
        rspData.setTotal(page.getTotal());
        return rspData;
    }

    /**
     * 导出账单列表
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:bill:export')")
    @Log(title = "账单管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzBill bill)
    {
        List<HzBill> list = billService.selectBillList(bill);
        ExcelUtil<HzBill> util = new ExcelUtil<HzBill>(HzBill.class);
        util.exportExcel(response, list, "账单数据");
    }

    /**
     * 获取账单详细信息
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:bill:query')")
    @GetMapping(value = "/{billId}")
    public AjaxResult getInfo(@PathVariable("billId") Long billId)
    {
        return success(billService.selectBillVOById(billId));
    }

    /**
     * 新增账单
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:bill:add')")
    @Log(title = "账单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HzBill bill)
    {
        return toAjax(billService.insertBill(bill));
    }

    /**
     * 修改账单
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:bill:edit')")
    @Log(title = "账单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzBill bill)
    {
        return toAjax(billService.updateBill(bill));
    }

    /**
     * 删除账单
     */
    @PreAuthorize("@ss.hasPermi('gangzhu:bill:remove')")
    @Log(title = "账单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{billIds}")
    public AjaxResult remove(@PathVariable Long[] billIds)
    {
        int count = 0;
        for (Long billId : billIds)
        {
            count += billService.deleteBillById(billId);
        }
        return toAjax(count);
    }
}
