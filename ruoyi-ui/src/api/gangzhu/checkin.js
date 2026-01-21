import request from '@/utils/request'

// 查询入住申请列表
export function listCheckIn(query) {
  return request({
    url: '/system/checkin/list',
    method: 'get',
    params: query
  })
}

// 查询入住申请详细
export function getCheckIn(recordId) {
  return request({
    url: '/system/checkin/' + recordId,
    method: 'get'
  })
}

// 新增入住申请
export function addCheckIn(data) {
  return request({
    url: '/system/checkin',
    method: 'post',
    data: data
  })
}

// 修改入住申请
export function updateCheckIn(data) {
  return request({
    url: '/system/checkin',
    method: 'put',
    data: data
  })
}

// 删除入住申请
export function delCheckIn(recordId) {
  return request({
    url: '/system/checkin/' + recordId,
    method: 'delete'
  })
}

// 审核入住申请
export function auditCheckIn(data) {
  return request({
    url: '/system/checkin/audit',
    method: 'put',
    data: data
  })
}
