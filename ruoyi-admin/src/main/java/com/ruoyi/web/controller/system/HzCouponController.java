package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.HzCoupon;
import com.ruoyi.system.service.IHzCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 优惠券 Controller（管理端）
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/gangzhu/coupon")
public class HzCouponController extends BaseController
{
    @Autowired
    private IHzCouponService couponService;

    /** 列表 */
    @PreAuthorize("@ss.hasPermi('gangzhu:coupon:list')")
    @GetMapping("/list")
    public TableDataInfo list(HzCoupon coupon)
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        IPage<HzCoupon> page = couponService.selectCouponPage(coupon,
                pageNum == null ? 1 : pageNum, pageSize == null ? 10 : pageSize);
        TableDataInfo info = new TableDataInfo();
        info.setCode(200);
        info.setMsg("查询成功");
        info.setRows(page.getRecords());
        info.setTotal(page.getTotal());
        return info;
    }

    /** 导出 */
    @PreAuthorize("@ss.hasPermi('gangzhu:coupon:export')")
    @Log(title = "优惠券", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HzCoupon coupon)
    {
        List<HzCoupon> list = couponService.selectCouponList(coupon);
        ExcelUtil<HzCoupon> util = new ExcelUtil<>(HzCoupon.class);
        util.exportExcel(response, list, "优惠券数据");
    }

    /** 详情 */
    @PreAuthorize("@ss.hasPermi('gangzhu:coupon:query')")
    @GetMapping(value = "/{couponId}")
    public AjaxResult getInfo(@PathVariable("couponId") Long couponId)
    {
        return AjaxResult.success(couponService.selectCouponById(couponId));
    }

    /** 新增 */
    @PreAuthorize("@ss.hasPermi('gangzhu:coupon:add')")
    @Log(title = "优惠券", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HzCoupon coupon)
    {
        return toAjax(couponService.insertCoupon(coupon));
    }

    /** 修改 */
    @PreAuthorize("@ss.hasPermi('gangzhu:coupon:edit')")
    @Log(title = "优惠券", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HzCoupon coupon)
    {
        return toAjax(couponService.updateCoupon(coupon));
    }

    /** 删除 */
    @PreAuthorize("@ss.hasPermi('gangzhu:coupon:remove')")
    @Log(title = "优惠券", businessType = BusinessType.DELETE)
    @DeleteMapping("/{couponIds}")
    public AjaxResult remove(@PathVariable Long[] couponIds)
    {
        return toAjax(couponService.deleteCouponByIds(couponIds));
    }

    /** 领取记录列表 */
    @PreAuthorize("@ss.hasPermi('gangzhu:coupon:list')")
    @GetMapping("/receiveList")
    public TableDataInfo receiveList(@RequestParam(required = false) Long couponId,
                                     @RequestParam(required = false) Long tenantId,
                                     @RequestParam(required = false) String receiveStatus)
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        IPage<Map<String, Object>> page = couponService.selectReceiveRecordPage(couponId, tenantId, receiveStatus,
                pageNum == null ? 1 : pageNum, pageSize == null ? 10 : pageSize);
        TableDataInfo info = new TableDataInfo();
        info.setCode(200);
        info.setMsg("查询成功");
        info.setRows(page.getRecords());
        info.setTotal(page.getTotal());
        return info;
    }
}
