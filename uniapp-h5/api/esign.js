import { get } from '@/utils/request'

/**
 * 获取实名认证URL
 * @param {number} tenantId
 */
export function getAuthUrl(tenantId) {
  return get(`/h5/esign/auth-url/${tenantId}`)
}

/**
 * 查询实名认证状态
 * @param {number} tenantId
 */
export function getAuthStatus(tenantId) {
  return get(`/h5/esign/auth-status/${tenantId}`)
}

/**
 * 获取签署URL
 * @param {number} contractId
 * @param {number} tenantId
 */
export function getSignUrl(contractId, tenantId) {
  return get(`/h5/esign/sign-url/${contractId}?tenantId=${tenantId}`)
}
