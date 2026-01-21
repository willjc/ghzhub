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
