/**
 * 合租户申请相关API
 */
import { get, post } from '@/utils/request'

/**
 * 获取用户的合租户申请列表
 * @param {Number} tenantId 租户ID
 */
export function getCohabitantList(tenantId) {
  return get(`/h5/app/cohabitant/list/${tenantId}`)
}

/**
 * 获取合租户申请详情
 * @param {Number} coTenantId 合租户ID
 */
export function getCohabitantDetail(coTenantId) {
  return get(`/h5/app/cohabitant/${coTenantId}`)
}

/**
 * 获取用户已确认的合同列表（可添加合租户的房源）
 * @param {Number} tenantId 租户ID
 */
export function getConfirmedContractList(tenantId) {
  return get(`/h5/app/cohabitant/confirmed/${tenantId}`)
}

/**
 * 提交合租户申请
 * @param {Object} data 合租户申请数据
 * @param {Number} data.contractId 合同ID（房源）
 * @param {String} data.tenantName 合租人姓名
 * @param {String} data.idCard 身份证号
 * @param {String} data.phone 联系电话
 * @param {String} data.relationship 与主承租人关系
 * @param {String} data.checkinDate 入住日期
 */
export function submitCohabitant(data) {
  return post('/h5/app/cohabitant', data)
}

export default {
  getCohabitantList,
  getCohabitantDetail,
  getConfirmedContractList,
  submitCohabitant
}
