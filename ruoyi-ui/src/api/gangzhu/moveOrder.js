import request from '@/utils/request'

// 查询搬家订单列表
export function listMoveOrder(query) {
  return request({
    url: '/gangzhu/moveOrder/list',
    method: 'get',
    params: query
  })
}

// 查询搬家订单详情
export function getMoveOrder(orderId) {
  return request({
    url: '/gangzhu/moveOrder/' + orderId,
    method: 'get'
  })
}

// 分配服务公司
export function assignMoveOrder(data) {
  return request({
    url: '/gangzhu/moveOrder/assign',
    method: 'post',
    data: data
  })
}

// 标记完成
export function finishMoveOrder(orderId) {
  return request({
    url: '/gangzhu/moveOrder/finish/' + orderId,
    method: 'put'
  })
}

// 删除搬家订单
export function delMoveOrder(orderIds) {
  return request({
    url: '/gangzhu/moveOrder/' + orderIds,
    method: 'delete'
  })
}
