// uniapp-h5/api/order.js
import { get, post } from '@/utils/request'

/** 创建选房预订单 */
export function createOrder(tenantId, houseId) {
  return post('/h5/order/create', { tenantId, houseId })
}

/** 查询订单状态及剩余时间 */
export function getOrderStatus(orderNo) {
  return get(`/h5/order/status/${orderNo}`)
}

/** 取消预订单 */
export function cancelOrder(orderNo, tenantId) {
  return post('/h5/order/cancel', { orderNo, tenantId })
}

/** 获取待上传资料的订单列表 */
export function getPendingUploadOrders(tenantId) {
  return get(`/h5/order/pending-upload/${tenantId}`)
}

/** 入住前置检查 */
export function checkinCheck(tenantId) {
  return get(`/h5/order/checkin-check/${tenantId}`)
}
