package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.HzServiceOrder;

import java.util.List;

/**
 * 服务订单 Service 接口
 *
 * @author ruoyi
 */
public interface IHzServiceOrderService extends IService<HzServiceOrder>
{
    /**
     * 分页查询订单（管理端）
     */
    IPage<HzServiceOrder> selectOrderPage(HzServiceOrder order, int pageNum, int pageSize);

    /**
     * 列表查询（不分页，导出用）
     */
    List<HzServiceOrder> selectOrderList(HzServiceOrder order);

    /**
     * 按手机号查询订单（H5 我的订单）
     */
    List<HzServiceOrder> selectMyOrders(String phone, String orderType, String status, String keyword);

    /**
     * 按 ID 查询
     */
    HzServiceOrder selectOrderById(Long orderId);

    /**
     * 提交订单（H5 端，根据 order_type 决定校验保洁/搬家专属字段）
     */
    int submitOrder(HzServiceOrder order);

    /**
     * 取消订单（H5 端，仅本人 + 仅"待处理"可取消）
     */
    int cancelOrder(Long orderId, String phone, String cancelReason);

    /**
     * 分配服务公司（管理端）
     */
    int assignOrder(Long orderId, Long companyId, String assignRemark);

    /**
     * 标记完成（管理端）
     */
    int finishOrder(Long orderId);

    /**
     * 评价（H5 端，仅本人 + 仅"已完成"可评价 + 一次性）
     */
    int rateOrder(Long orderId, String phone, Integer rateScore, String rateContent);

    /**
     * 删除（管理端）
     */
    int deleteOrderByIds(Long[] orderIds);

    /**
     * 生成订单号
     */
    String generateOrderNo(String orderType);
}
