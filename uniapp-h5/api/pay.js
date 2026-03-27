import { post, get } from '@/utils/request'

/**
 * 微信预支付
 * @param {Object} params { billNo, payType, openid?, clientIp? }
 */
export function wechatPrepay(params) {
  return post('/h5/pay/wechat/prepay', params)
}

/**
 * 查询支付结果
 * @param {String} billNo 账单号
 */
export function queryPayResult(billNo) {
  return get(`/h5/pay/wechat/query/${billNo}`)
}
