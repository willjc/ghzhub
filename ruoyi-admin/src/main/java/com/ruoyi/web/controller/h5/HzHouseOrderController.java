package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.IHzHouseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * H5端选房预订单Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/order")
public class HzHouseOrderController extends BaseController {

    @Autowired
    private IHzHouseOrderService orderService;

    /**
     * 选房确认，创建预订单
     * 请求体：{ "tenantId": 1, "houseId": 101 }
     */
    @PostMapping("/create")
    public AjaxResult createOrder(@RequestBody Map<String, Long> body) {
        Long tenantId = body.get("tenantId");
        Long houseId = body.get("houseId");
        if (tenantId == null || houseId == null) {
            return error("参数不完整");
        }
        return orderService.createOrder(tenantId, houseId);
    }

    /**
     * 查询订单状态及剩余时间
     *
     * @param orderNo 预订单号
     */
    @GetMapping("/status/{orderNo}")
    public AjaxResult getOrderStatus(@PathVariable String orderNo) {
        Map<String, Object> status = orderService.getOrderStatus(orderNo);
        if (status == null) {
            return error("订单不存在");
        }
        return success(status);
    }

    /**
     * 用户主动取消预订单
     * 请求体：{ "orderNo": "HO...", "tenantId": 1 }
     */
    @PostMapping("/cancel")
    public AjaxResult cancelOrder(@RequestBody Map<String, Object> body) {
        String orderNo = (String) body.get("orderNo");
        Long tenantId = ((Number) body.get("tenantId")).longValue();
        orderService.cancelOrder(orderNo, tenantId);
        return success();
    }

    /**
     * 获取用户待上传资料的订单列表（含倒计时）
     *
     * @param tenantId 租户ID
     */
    @GetMapping("/pending-upload/{tenantId}")
    public AjaxResult getPendingUploadOrders(@PathVariable Long tenantId) {
        return success(orderService.getPendingUploadOrders(tenantId));
    }

    /**
     * 入住前置检查：是否存在未完成的待上传资料订单
     *
     * @param tenantId 租户ID
     */
    @GetMapping("/checkin-check/{tenantId}")
    public AjaxResult checkinCheck(@PathVariable Long tenantId) {
        return success(orderService.checkinCheck(tenantId));
    }
}
