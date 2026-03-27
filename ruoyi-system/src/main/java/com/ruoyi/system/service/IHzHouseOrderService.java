package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzHouseOrder;

import java.util.List;
import java.util.Map;

/**
 * 选房预订单Service接口
 *
 * @author ruoyi
 */
public interface IHzHouseOrderService extends IService<HzHouseOrder> {

    /**
     * 创建选房预订单，原子锁定房源
     *
     * @param tenantId 租户ID
     * @param houseId  房源ID
     * @return 创建结果（包含 orderNo、lockExpireTime、depositAmount）
     */
    AjaxResult createOrder(Long tenantId, Long houseId);

    /**
     * 查询订单状态及剩余时间
     *
     * @param orderNo 预订单号
     * @return 订单状态Map
     */
    Map<String, Object> getOrderStatus(String orderNo);

    /**
     * 用户取消订单
     *
     * @param orderNo  预订单号
     * @param tenantId 租户ID（用于权限校验）
     */
    void cancelOrder(String orderNo, Long tenantId);

    /**
     * 查询用户待上传资料的订单列表（含倒计时）
     *
     * @param tenantId 租户ID
     * @return 预订单列表
     */
    List<HzHouseOrder> getPendingUploadOrders(Long tenantId);

    /**
     * 入住前置检查：是否有未完成的待上传资料订单
     *
     * @param tenantId 租户ID
     * @return 检查结果Map（canCheckin、remainSeconds、orderNo）
     */
    Map<String, Object> checkinCheck(Long tenantId);

    /**
     * 押金支付成功后更新订单状态（由支付回调调用）
     *
     * @param orderNo 预订单号
     */
    void onDepositPaid(String orderNo);

    /**
     * 资料审核通过后检查是否可完成订单（由文档审核接口调用）
     *
     * @param tenantId 租户ID
     */
    void onDocumentsApproved(Long tenantId);

    /**
     * 定时任务：处理已过期的预订单并释放房源
     */
    void processExpiredOrders();
}
