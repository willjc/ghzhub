import request from '@/utils/request'

// 查询优惠券列表
export function listCoupon(query) {
  return request({
    url: '/gangzhu/coupon/list',
    method: 'get',
    params: query
  })
}

// 查询优惠券详情
export function getCoupon(couponId) {
  return request({
    url: '/gangzhu/coupon/' + couponId,
    method: 'get'
  })
}

// 新增优惠券
export function addCoupon(data) {
  return request({
    url: '/gangzhu/coupon',
    method: 'post',
    data: data
  })
}

// 修改优惠券
export function updateCoupon(data) {
  return request({
    url: '/gangzhu/coupon',
    method: 'put',
    data: data
  })
}

// 删除优惠券
export function delCoupon(couponIds) {
  return request({
    url: '/gangzhu/coupon/' + couponIds,
    method: 'delete'
  })
}

// 领取记录
export function listCouponReceive(query) {
  return request({
    url: '/gangzhu/coupon/receiveList',
    method: 'get',
    params: query
  })
}
