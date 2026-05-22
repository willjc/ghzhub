/**
 * 服务订单（保洁/搬家）相关 API
 * 后端基础路径：/h5/app/serviceOrder
 */
import { get, post } from '@/utils/request'

/**
 * 我的订单列表（搜索 + 列表合并）
 * @param {Object} params
 * @param {string} params.phone 手机号（必填）
 * @param {string} [params.orderType] 1=保洁 2=搬家
 * @param {string} [params.status] 0=待处理 1=已分配 2=服务中 3=已完成 4=已取消
 * @param {string} [params.keyword] 关键字（订单号/地址）
 */
export function getMyServiceOrders(params) {
  const usp = new URLSearchParams()
  Object.keys(params || {}).forEach(k => {
    if (params[k] !== undefined && params[k] !== null && params[k] !== '') {
      usp.append(k, params[k])
    }
  })
  return get(`/h5/app/serviceOrder/myOrders?${usp.toString()}`)
}

/**
 * 订单详情
 * @param {number|string} orderId 订单ID
 */
export function getServiceOrderDetail(orderId) {
  return get(`/h5/app/serviceOrder/detail/${orderId}`)
}

/**
 * 提交保洁订单
 * @param {Object} data
 */
export function submitCleanOrder(data) {
  return post('/h5/app/serviceOrder/submitClean', data)
}

/**
 * 提交搬家订单
 * @param {Object} data
 */
export function submitMoveOrder(data) {
  return post('/h5/app/serviceOrder/submitMove', data)
}

/**
 * 取消订单（仅"待处理"状态可取消）
 * @param {Object} data { orderId, phone, cancelReason }
 */
export function cancelServiceOrder(data) {
  return post('/h5/app/serviceOrder/cancel', data)
}

/**
 * 评价（仅"已完成"可评价）
 * @param {Object} data { orderId, phone, rateScore, rateContent }
 */
export function rateServiceOrder(data) {
  return post('/h5/app/serviceOrder/rate', data)
}

/**
 * 获取已启用的服务公司列表
 * @param {string} [orderType] 1=保洁 2=搬家（综合公司始终包含）
 */
export function getActiveServiceCompanies(orderType) {
  const q = orderType ? `?orderType=${orderType}` : ''
  return get(`/h5/app/serviceOrder/companies${q}`)
}
