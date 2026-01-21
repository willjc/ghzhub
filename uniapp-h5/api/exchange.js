/**
 * 调换房申请相关API
 */
import { get, post } from '@/utils/request'

/**
 * 获取用户的调换房申请列表
 * @param {Number} tenantId 租户ID
 */
export function getExchangeList(tenantId) {
  return get(`/h5/app/exchange/list/${tenantId}`)
}

/**
 * 获取调换房申请详情
 * @param {Number} exchangeId 调换房ID
 */
export function getExchangeDetail(exchangeId) {
  return get(`/h5/app/exchange/${exchangeId}`)
}

/**
 * 获取用户已确认的合同列表（可调换的房源）
 * @param {Number} tenantId 租户ID
 */
export function getConfirmedContractList(tenantId) {
  return get(`/h5/app/exchange/confirmed/${tenantId}`)
}

/**
 * 提交调换房申请
 * @param {Object} data 调换房申请数据
 * @param {Number} data.contractId 合同ID（原房源）
 * @param {String} data.applyDate 申请调换时间
 * @param {String} data.reason 调换原因
 */
export function submitExchange(data) {
  return post('/h5/app/exchange', data)
}

export default {
  getExchangeList,
  getExchangeDetail,
  getConfirmedContractList,
  submitExchange
}
