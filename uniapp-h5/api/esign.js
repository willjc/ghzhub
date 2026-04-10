import { get, post } from '@/utils/request'

/**
 * 获取实名认证URL
 * @param {number} userId 用户ID
 * @param {object} opts
 * @param {string} opts.realName   真实姓名（表单填写，优先使用）
 * @param {string} opts.idCard     身份证号（表单填写，优先使用）
 * @param {string} opts.redirectUrl 认证完成跳转地址（小程序传 wechat://back）
 */
export function getAuthUrl(userId, { realName = '', idCard = '', redirectUrl = '' } = {}) {
  const parts = []
  if (realName) parts.push('realName=' + encodeURIComponent(realName))
  if (idCard) parts.push('idCard=' + encodeURIComponent(idCard))
  if (redirectUrl) parts.push('redirectUrl=' + encodeURIComponent(redirectUrl))
  const qs = parts.join('&')
  return get(`/h5/esign/auth-url/${userId}${qs ? '?' + qs : ''}`)
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
 * @param {number} userId     用户ID
 * @param {string} platform   "mp" = 小程序（使用 wechat://back），其他 = H5
 */
export function initSign(contractId, userId, platform = 'h5') {
  return post('/h5/esign/init-sign', { contractId, userId, platform })
}

/**
 * 获取e签宝签署URL（兼容旧入口）
 */
export function getSignUrl(contractId, userId) {
  return get(`/h5/esign/sign-url/${contractId}?userId=${userId}`)
}
