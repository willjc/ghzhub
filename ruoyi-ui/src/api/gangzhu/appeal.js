import request from '@/utils/request'

// 查询申诉列表
export function listAppeal(query) {
  return request({
    url: '/system/qualificationAppeal/list',
    method: 'get',
    params: query
  })
}

// 查询申诉详情
export function getAppeal(appealId) {
  return request({
    url: '/system/qualificationAppeal/' + appealId,
    method: 'get'
  })
}

// 审核申诉
export function handleAppeal(data) {
  return request({
    url: '/system/qualificationAppeal/handle',
    method: 'put',
    data: data
  })
}

// 删除申诉
export function delAppeal(appealIds) {
  return request({
    url: '/system/qualificationAppeal/' + appealIds,
    method: 'delete'
  })
}
