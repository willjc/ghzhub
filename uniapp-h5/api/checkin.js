/**
 * 入住管理相关API
 */
import { get, post, put } from '@/utils/request'

/**
 * 获取用户的入住单列表
 * @param {Number} tenantId 租户ID
 */
export function getCheckInList(tenantId) {
  return get(`/h5/app/checkin/list/${tenantId}`)
}

/**
 * 获取用户的已入住确认的入住单列表 (用于续租页面)
 * @param {Number} tenantId 租户ID
 */
export function getConfirmedCheckInList(tenantId) {
  return get(`/h5/app/checkin/confirmed/${tenantId}`)
}

/**
 * 获取入住单详情
 * @param {Number} recordId 入住记录ID
 */
export function getCheckInDetail(recordId) {
  return get(`/h5/app/checkin/${recordId}`)
}

/**
 * 获取入住确认信息（包含房源设施）
 * @param {Number} recordId 入住记录ID
 */
export function getCheckInConfirmInfo(recordId) {
  return get(`/h5/app/checkin/confirm/${recordId}`)
}

/**
 * 提交入住办理信息
 * @param {Object} data 入住办理数据
 * @param {Number} data.recordId 入住记录ID
 * @param {String} data.actualCheckinDate 实际入住日期
 * @param {String} data.roommateInfo 合住人信息(JSON字符串)
 * @param {String} data.emergencyContactName 紧急联系人姓名
 * @param {String} data.emergencyContactRelation 紧急联系人关系
 * @param {String} data.emergencyContactPhone 紧急联系人电话
 */
export function submitCheckIn(data) {
  return post('/h5/app/checkin/submit', data)
}

/**
 * 提交入住确认（保存签字和设施）
 * @param {Object} data 入住确认数据
 * @param {Number} data.recordId 入住记录ID
 * @param {String} data.signature 签字(base64图片)
 * @param {String} data.selectedFacilities 用户选择的配套设施
 */
export function submitCheckInConfirm(data) {
  return post('/h5/app/checkin/confirm', data)
}

/**
 * 根据合同ID获取入住单
 * @param {Number} contractId 合同ID
 */
export function getCheckInByContractId(contractId) {
  return get(`/h5/app/checkin/contract/${contractId}`)
}

/**
 * 取消入住申请
 * @param {Number} recordId 入住记录ID
 */
export function cancelCheckIn(recordId) {
  return post(`/h5/app/checkin/cancel/${recordId}`)
}

/**
 * 获取用户的房源列表（用于我的房源页面）
 * @param {Number} tenantId 租户ID
 */
export function getMyListings(tenantId) {
  return get(`/h5/app/checkin/mylistings/${tenantId}`)
}

export default {
  getCheckInList,
  getConfirmedCheckInList,
  getCheckInDetail,
  getCheckInConfirmInfo,
  submitCheckIn,
  submitCheckInConfirm,
  getCheckInByContractId,
  cancelCheckIn,
  getMyListings
}
