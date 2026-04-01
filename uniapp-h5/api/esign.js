import { get, post } from '@/utils/request'

/**
 * 获取实名认证URL
 * @param {number} userId 用户ID
 */
export function getAuthUrl(userId) {
  return get(`/h5/esign/auth-url/${userId}`)
}

/**
 * 查询实名认证状态
 * @param {number} userId 用户ID
 */
export function getAuthStatus(userId) {
  return get(`/h5/esign/auth-status/${userId}`)
}

/**
 * 一体化签署接口：模板填充→创建签署流→返回签署URL或认证链接
 * @param {number} contractId 合同ID
 * @param {number} userId 用户ID
 */
export function initSign(contractId, userId) {
  return post('/h5/esign/init-sign', { contractId, userId })
}

/**
 * 获取e签宝签署URL（兼容旧入口）
 * @param {number} contractId 合同ID
 * @param {number} userId 用户ID
 */
export function getSignUrl(contractId, userId) {
  return get(`/h5/esign/sign-url/${contractId}?userId=${userId}`)
}
