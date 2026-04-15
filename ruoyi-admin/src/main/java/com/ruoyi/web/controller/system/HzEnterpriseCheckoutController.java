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
import com.ruoyi.system.domain.HzEnterpriseCheckout;
import com.ruoyi.system.service.IHzEnterpriseCheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 企业退租申请Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/enterpriseCheckout")
public class HzEnterpriseCheckoutController extends BaseController {

    @Autowired
    private IHzEnterpriseCheckoutService checkoutService;

    /**
     * 查询企业退租申请列表
     */
    @PreAuthorize("@ss.hasPermi('system:enterpriseCheckout:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzEnterpriseCheckout checkout) {
        Page<HzEnterpriseCheckout> page = PageUtils.getPage();
        IPage<HzEnterpriseCheckout> pageResult = checkoutService.selectCheckoutPage(page, checkout);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(pageResult.getRecords());
        rspData.setTotal(pageResult.getTotal());
        rspData.setMsg("查询成功");
        return rspData;
    }

    /**
     * 导出企业退租申请列表
     */
    @PreAuthorize("@ss.hasPermi('system:enterpriseCheckout:export')")
    @Log(title = "企业退租申请", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzEnterpriseCheckout checkout) {
        List<HzEnterpriseCheckout> list = checkoutService.selectCheckoutList(checkout);
        ExcelUtil<HzEnterpriseCheckout> util = new ExcelUtil<>(HzEnterpriseCheckout.class);
        util.exportExcel(response, list, "企业退租申请数据");
    }

    /**
     * 获取企业退租申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:enterpriseCheckout:query')")
    @GetMapping(value = "/{checkoutId}")
    public AjaxResult getInfo(@PathVariable("checkoutId") Long checkoutId) {
        return success(checkoutService.selectCheckoutById(checkoutId));
    }

    /**
     * 删除企业退租申请
     */
    @PreAuthorize("@ss.hasPermi('system:enterpriseCheckout:remove')")
    @Log(title = "企业退租申请", businessType = BusinessType.DELETE)
    @DeleteMapping("/{checkoutIds}")
    public AjaxResult remove(@PathVariable Long[] checkoutIds) {
        return toAjax(checkoutService.deleteCheckoutByIds(checkoutIds));
    }
}
