import { post, get } from '@/utils/request'

/**
 * 微信预支付
 * @param {Object} params { billNo, payType, openid?, clientIp? }
 */
export function wechatPrepay(params) {
  return post('/h5/pay/wechat/prepay', params)
}

/**
 * 企业账单微信预支付（仅 JSAPI）
 * @param {Object} params { billId, openid }
 */
export function wechatPrepayEnterprise(params) {
  return post('/h5/pay/wechat/prepayEnterprise', params)
}

/**
 * 主动同步支付结果（兜底）
 * @param {String} billNo 账单号
 */
export function syncPayResult(billNo) {
  return post(`/h5/pay/wechat/sync/${billNo}`)
}

/**
 * 查询支付结果
 * @param {String} billNo 账单号
 */
export function queryPayResult(billNo) {
  return get(`/h5/pay/wechat/query/${billNo}`)
}
