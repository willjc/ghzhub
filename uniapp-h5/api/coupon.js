/**
 * 优惠券 H5 API（后端基础路径：/h5/app/coupon）
 */
import { get, post } from '@/utils/request'

/** 可领取列表（公开，登录可标记 hasReceived） */
export function getAvailableCoupons(tenantId) {
  return get('/h5/app/coupon/available', { tenantId })
}

/** 我的已领取列表 */
export function getMyCoupons(tenantId, receiveStatus) {
  return get('/h5/app/coupon/myList', { tenantId, receiveStatus })
}

/** 领取优惠券 */
export function receiveCoupon(couponId, tenantId) {
  return post('/h5/app/coupon/receive', { couponId, tenantId })
}
