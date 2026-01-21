/**
 * 退租管理相关API
 */
import { get, post, put, del } from '@/utils/request'

/**
 * 获取用户���退租申请列表
 * @param {Number} tenantId 租户ID
 */
export function getCheckoutList(tenantId) {
  return get(`/h5/app/checkout/list/${tenantId}`)
}

/**
 * 获取退租申请详情
 * @param {Number} applyId 退租申请ID
 */
export function getCheckoutDetail(applyId) {
  return get(`/h5/app/checkout/${applyId}`)
}

/**
 * 提交退租申请
 * @param {Object} data 退租申请数据
 * @param {Number} data.contractId 合同ID
 * @param {Number} data.tenantId 租户ID
 * @param {Number} data.houseId 房源ID
 * @param {String} data.planCheckoutDate 计划退租日期(yyyy-MM-dd)
 * @param {String} data.checkoutReason 退租原因
 */
export function submitCheckoutApply(data) {
  return post('/h5/app/checkout/apply', data)
}

/**
 * 修改退租申请
 * @param {Number} applyId 申请ID
 * @param {Object} data 请求数据
 */
export function updateCheckoutApply(applyId, data) {
  return put(`/h5/app/checkout/${applyId}`, data)
}

/**
 * 取消退租申请
 * @param {Number} applyId 申请ID
 */
export function cancelCheckoutApply(applyId) {
  return del(`/h5/app/checkout/${applyId}`)
}

/**
 * 获取退租确认信息（费用详情）
 * @param {Number} applyId 申请ID
 */
export function getCheckoutConfirm(applyId) {
  return get(`/h5/app/checkout/confirm/${applyId}`)
}

/**
 * 提交退租确认（用户签字）
 * @param {Number} applyId 申请ID
 * @param {Object} data 请求数据
 * @param {String} data.tenantSignature 租户签字(base64图片)
 */
export function submitCheckoutConfirm(applyId, data) {
  return post(`/h5/app/checkout/confirm/${applyId}`, data)
}

export default {
  getCheckoutList,
  getCheckoutDetail,
  submitCheckoutApply,
  updateCheckoutApply,
  cancelCheckoutApply,
  getCheckoutConfirm,
  submitCheckoutConfirm
}
