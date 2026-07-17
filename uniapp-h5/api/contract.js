/**
 * 合同相关 API
 */
import request from '@/utils/request'

/**
 * 根据用户ID获取我的合同列表
 * @param {Number} userId 用户ID
 * @param {String} [projectType] 项目类型（1:人才公寓 2:保租房 3:市场租赁），为空则不过滤
 * @returns {Promise}
 */
export function getMyContracts(userId, projectType) {
  return request.get(`/h5/app/contract/user/${userId}`, projectType ? { projectType } : {})
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

/**
 * 人才公寓 7 折分档：实际月租预览（用于房源详情页提示）
 * @param {Object} params
 * @param {Number} params.houseId 房源ID
 * @param {Number} [params.userId] 用户ID（不传则后端从 token 解析）
 * @returns {Promise<{applicable, area, areaLimit, originalRent, standardPrice, overflowArea, actualMonthlyRent, remark}>}
 */
export function getTalentRentPreview(params) {
  return request.get('/h5/app/contract/talent-rent-preview', params)
}
