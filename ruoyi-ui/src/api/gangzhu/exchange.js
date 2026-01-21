import request from '@/utils/request'

// 查询换房列表
export function listExchange(query) {
  return request({
    url: '/gangzhu/exchange/list',
    method: 'get',
    params: query
  })
}

// 查询换房详细
export function getExchange(exchangeId) {
  return request({
    url: '/gangzhu/exchange/' + exchangeId,
    method: 'get'
  })
}

// 新增换房
export function addExchange(data) {
  return request({
    url: '/gangzhu/exchange',
    method: 'post',
    data: data
  })
}

// 修改换房
export function updateExchange(data) {
  return request({
    url: '/gangzhu/exchange',
    method: 'put',
    data: data
  })
}

// 删除换房
export function delExchange(exchangeId) {
  return request({
    url: '/gangzhu/exchange/' + exchangeId,
    method: 'delete'
  })
}

// 审核换房
export function auditExchange(data) {
  return request({
    url: '/gangzhu/exchange/audit',
    method: 'post',
    data: data
  })
}