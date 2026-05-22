import request from '@/utils/request'

// 查询保洁订单列表
export function listCleanOrder(query) {
  return request({
    url: '/gangzhu/cleanOrder/list',
    method: 'get',
    params: query
  })
}

// 查询保洁订单详情
export function getCleanOrder(orderId) {
  return request({
    url: '/gangzhu/cleanOrder/' + orderId,
    method: 'get'
  })
}

// 分配服务公司
export function assignCleanOrder(data) {
  return request({
    url: '/gangzhu/cleanOrder/assign',
    method: 'post',
    data: data
  })
}

// 标记完成
export function finishCleanOrder(orderId) {
  return request({
    url: '/gangzhu/cleanOrder/finish/' + orderId,
    method: 'put'
  })
}

// 删除保洁订单
export function delCleanOrder(orderIds) {
  return request({
    url: '/gangzhu/cleanOrder/' + orderIds,
    method: 'delete'
  })
}
