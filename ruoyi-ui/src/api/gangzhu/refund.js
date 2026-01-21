import request from '@/utils/request'

// 查询退款列表
export function listRefund(query) {
  return request({
    url: '/gangzhu/refund/list',
    method: 'get',
    params: query
  })
}

// 查询退款详细
export function getRefund(refundId) {
  return request({
    url: '/gangzhu/refund/' + refundId,
    method: 'get'
  })
}

// 新增退款
export function addRefund(data) {
  return request({
    url: '/gangzhu/refund',
    method: 'post',
    data: data
  })
}

// 修改退款
export function updateRefund(data) {
  return request({
    url: '/gangzhu/refund',
    method: 'put',
    data: data
  })
}

// 删除退款
export function delRefund(refundId) {
  return request({
    url: '/gangzhu/refund/' + refundId,
    method: 'delete'
  })
}

// 审核退款
export function auditRefund(data) {
  return request({
    url: '/gangzhu/refund/audit',
    method: 'post',
    data: data
  })
}

// 提交付款信息
export function submitPayment(data) {
  return request({
    url: '/gangzhu/refund/payment',
    method: 'post',
    data: data
  })
}