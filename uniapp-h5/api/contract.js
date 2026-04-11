/**
 * 合同相关 API
 */
import request from '@/utils/request'

/**
 * 根据用户ID获取我的合同列表
 * @param {Number} userId 用户ID
 * @returns {Promise}
 */
export function getMyContracts(userId) {
  return request.get(`/h5/app/contract/user/${userId}`)
}

/**
 * 生成合同预览
 * @param {Object} data 合同数据
 * @returns {Promise}
 */
export function generateContract(data) {
  return request.post('/h5/app/contract/generate', data)
}

/**
 * 签署合同
 * @param {Object} data 签署数据
 * @returns {Promise}
 */
export function signContract(data) {
  return request.post('/h5/app/contract/sign', data)
}

/**
 * 续租合同
 * @param {Object} data 续租数据
 * @returns {Promise}
 */
export function renewContract(data) {
  return request.post('/h5/app/contract/renew', data)
}

/**
 * 获取合同的押金账单
 * @param {Number} contractId 合同ID
 */
export function getDepositBill(contractId) {
  return request.get(`/h5/app/bill/deposit/${contractId}`)
}

/**
 * 支付押金
 * @param {Object} data 支付数据
 * @param {Number} data.billId 账单ID
 * @param {Number} data.payAmount 支付金额
 */
export function payDeposit(data) {
  return request.post('/h5/app/bill/pay', data)
}

/**
 * 获取合同详情
 * @param {Number} contractId 合同ID
 */
export function getContractDetail(contractId) {
  return request.get(`/h5/app/contract/detail/${contractId}`)
}

/**
 * 获取合同 PDF 最新下载链接（实时向 e签宝 刷新，避免存库链接过期 403）
 * @param {Number} contractId 合同ID
 */
export function getContractPdfUrl(contractId) {
  return request.get(`/h5/app/contract/${contractId}/pdf-url`)
}
