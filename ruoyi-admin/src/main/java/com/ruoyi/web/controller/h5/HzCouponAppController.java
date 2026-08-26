package com.ruoyi.web.controller.h5;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.HzCouponReceive;
import com.ruoyi.system.service.IHzCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 优惠券 H5 接口（公开）
 * <p>
 * 接口路径前缀：/h5/app/coupon
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/app/coupon")
public class HzCouponAppController
{
    @Autowired
    private IHzCouponService couponService;

    /** 可领取列表 */
    @Anonymous
    @GetMapping("/available")
    public AjaxResult available(@RequestParam(required = false) Long tenantId)
    {
        tenantId = SecurityUtils.getHzUserIdOrNull();
        List<Map<String, Object>> list = couponService.selectAvailableCoupons(tenantId);
        return AjaxResult.success(list);
    }

    /** 我的已领取列表 */
    @GetMapping("/myList")
    public AjaxResult myList(@RequestParam Long tenantId,
                             @RequestParam(required = false) String receiveStatus)
    {
        tenantId = SecurityUtils.getHzUserId();
        return AjaxResult.success(couponService.selectMyReceivedCoupons(tenantId, receiveStatus));
    }

    /** 领取 */
    @PostMapping("/receive")
    public AjaxResult receive(@RequestBody Map<String, Object> body)
    {
        Object cIdObj = body.get("couponId");
        if (cIdObj == null)
        {
            return AjaxResult.error("缺少参数");
        }
        Long couponId = Long.valueOf(cIdObj.toString());
        Long tenantId = SecurityUtils.getHzUserId();
        HzCouponReceive r = couponService.receiveCoupon(couponId, tenantId);
        Map<String, Object> data = new HashMap<>();
        data.put("receiveId", r.getReceiveId());
        data.put("couponId", r.getCouponId());
        data.put("receiveTime", r.getReceiveTime());
        return AjaxResult.success("领取成功", data);
    }
}
