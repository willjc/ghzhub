/**
 * 开票相关 API
 */
import request from '@/utils/request'

/**
 * 获取我的发票申请列表
 * @param {Number} userId 用户ID
 * @returns {Promise}
 */
export function getMyInvoiceList(userId) {
  return request.get('/h5/app/invoice/myList', { userId })
}

/**
 * 获取入住信息
 * @param {Number} userId 用��ID
 * @returns {Promise}
 */
export function getCheckinInfo(userId) {
  return request.get('/h5/app/invoice/checkinInfo', { userId })
}

/**
 * 获取可开票账单列表（已支付且未开票）
 * @param {Number} userId 用户ID
 * @param {Number} contractId 合同ID（可选，用于过滤指定房源的账单）
 * @returns {Promise}
 */
export function getAvailableBills(userId, contractId) {
  const params = { userId }
  if (contractId) {
    params.contractId = contractId
  }
  return request.get('/h5/app/invoice/availableBills', params)
}

/**
 * 提交开票申请
 * @param {Object} data 开票信息
 * @param {Number} data.userId 用户ID
 * @param {Number} data.billId 账单ID
 * @param {String} data.headType 抬头类型 personal/company
 * @param {String} data.name 个人名称或企业名称
 * @param {String} data.phone 手机号码
 * @param {String} data.email 邮箱地址
 * @param {String} data.taxNo 税号（企业可选）
 * @returns {Promise}
 */
export function submitInvoiceApply(data) {
  return request.post('/h5/app/invoice/apply', data)
}

/**
 * 获取发票详情
 * @param {Number} applyId 申请ID
 * @param {Number} userId 用户ID
 * @returns {Promise}
 */
export function getInvoiceDetail(applyId, userId) {
  return request.get(`/h5/app/invoice/${applyId}`, { userId })
}
