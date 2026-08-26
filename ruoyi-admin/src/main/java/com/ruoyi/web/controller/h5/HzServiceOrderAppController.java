package com.ruoyi.web.controller.h5;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.HzServiceCompany;
import com.ruoyi.system.domain.HzServiceOrder;
import com.ruoyi.system.service.IHzServiceCompanyService;
import com.ruoyi.system.service.IHzServiceOrderService;
import com.ruoyi.system.service.IHzUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * H5端 服务订单 Controller（保洁/搬家共用）
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/app/serviceOrder")
public class HzServiceOrderAppController extends BaseController
{
    @Autowired
    private IHzServiceOrderService orderService;

    @Autowired
    private IHzServiceCompanyService companyService;

    @Autowired
    private IHzUserService userService;

    /**
     * 我的订单列表（搜索 + 列表合并）
     * 86/90 + 87/91：按手机号查询，可按订单类型/状态/关键字筛选
     */
    @GetMapping("/myOrders")
    public AjaxResult myOrders(@RequestParam String phone,
                               @RequestParam(required = false) String orderType,
                               @RequestParam(required = false) String status,
                               @RequestParam(required = false) String keyword)
    {
        phone = currentPhone();
        List<HzServiceOrder> list = orderService.selectMyOrders(phone, orderType, status, keyword);
        return success(list);
    }

    /**
     * 订单详情
     */
    @GetMapping("/detail/{orderId}")
    public AjaxResult detail(@PathVariable Long orderId)
    {
        HzServiceOrder order = orderService.selectOrderById(orderId);
        if (order == null) return error("订单不存在");
        requireOwnedOrder(order);
        return success(order);
    }

    /**
     * 提交保洁订单（88）
     */
    @Log(title = "提交保洁订单", businessType = BusinessType.INSERT)
    @PostMapping("/submitClean")
    public AjaxResult submitClean(@RequestBody HzServiceOrder order)
    {
        order.setApplicantPhone(currentPhone());
        order.setOrderType("1");
        return toAjax(orderService.submitOrder(order));
    }

    /**
     * 提交搬家订单（92）
     */
    @Log(title = "提交搬家订单", businessType = BusinessType.INSERT)
    @PostMapping("/submitMove")
    public AjaxResult submitMove(@RequestBody HzServiceOrder order)
    {
        order.setApplicantPhone(currentPhone());
        order.setOrderType("2");
        return toAjax(orderService.submitOrder(order));
    }

    /**
     * 取消订单（仅"待处理"状态可取消）
     * Body: { orderId, phone, cancelReason }
     */
    @Log(title = "取消服务订单", businessType = BusinessType.UPDATE)
    @PostMapping("/cancel")
    public AjaxResult cancel(@RequestBody Map<String, Object> body)
    {
        Long orderId = body.get("orderId") != null ? Long.valueOf(body.get("orderId").toString()) : null;
        String phone = currentPhone();
        String reason = body.get("cancelReason") != null ? body.get("cancelReason").toString() : null;
        if (orderId == null || StringUtils.isEmpty(phone)) return error("参数缺失");
        return toAjax(orderService.cancelOrder(orderId, phone, reason));
    }

    /**
     * 评价（89/93，5星 + 文本，仅"已完成"可评价）
     * Body: { orderId, phone, rateScore, rateContent }
     */
    @Log(title = "服务订单评价", businessType = BusinessType.UPDATE)
    @PostMapping("/rate")
    public AjaxResult rate(@RequestBody Map<String, Object> body)
    {
        Long orderId = body.get("orderId") != null ? Long.valueOf(body.get("orderId").toString()) : null;
        String phone = currentPhone();
        Integer rateScore = body.get("rateScore") != null
                ? Integer.valueOf(body.get("rateScore").toString()) : null;
        String rateContent = body.get("rateContent") != null
                ? body.get("rateContent").toString() : null;
        if (orderId == null || StringUtils.isEmpty(phone)) return error("参数缺失");
        return toAjax(orderService.rateOrder(orderId, phone, rateScore, rateContent));
    }

    /**
     * 获取已启用的服务公司列表（用于小程序展示，可选）
     */
    @GetMapping("/companies")
    public AjaxResult companies(@RequestParam(required = false) String orderType)
    {
        List<HzServiceCompany> list = companyService.selectActiveCompaniesByOrderType(orderType);
        return success(list);
    }

    private String currentPhone() {
        com.ruoyi.system.domain.HzUser user = userService.selectHzUserById(SecurityUtils.getHzUserId());
        if (user == null || StringUtils.isEmpty(user.getPhone())) {
            throw new com.ruoyi.common.exception.ServiceException("当前用户手机号不存在");
        }
        return user.getPhone();
    }

    private void requireOwnedOrder(HzServiceOrder order) {
        if (!currentPhone().equals(order.getApplicantPhone())) {
            throw new com.ruoyi.common.exception.ServiceException("无权查看此订单", 403);
        }
    }
}
