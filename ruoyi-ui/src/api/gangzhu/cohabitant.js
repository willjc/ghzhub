import request from '@/utils/request'

// 查询合住人列表
export function listCohabitant(query) {
  return request({
    url: '/gangzhu/cohabitant/list',
    method: 'get',
    params: query
  })
}

// 查询合住人详细
export function getCohabitant(cohabitantId) {
  return request({
    url: '/gangzhu/cohabitant/' + cohabitantId,
    method: 'get'
  })
}

// 新增合住人
export function addCohabitant(data) {
  return request({
    url: '/gangzhu/cohabitant',
    method: 'post',
    data: data
  })
}

// 修改合住人
export function updateCohabitant(data) {
  return request({
    url: '/gangzhu/cohabitant',
    method: 'put',
    data: data
  })
}

// 删除合住人
export function delCohabitant(cohabitantId) {
  return request({
    url: '/gangzhu/cohabitant/' + cohabitantId,
    method: 'delete'
  })
}

// 审核合住人
export function auditCohabitant(data) {
  return request({
    url: '/gangzhu/cohabitant/audit',
    method: 'post',
    data: data
  })
}