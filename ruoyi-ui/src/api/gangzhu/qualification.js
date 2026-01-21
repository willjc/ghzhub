import request from '@/utils/request'

// 查询资格审核列表
export function listQualification(query) {
  return request({
    url: '/system/qualification/list',
    method: 'get',
    params: query
  })
}

// 查询资格审核详细
export function getQualification(qualificationId) {
  return request({
    url: '/system/qualification/' + qualificationId,
    method: 'get'
  })
}

// 新增资格审核
export function addQualification(data) {
  return request({
    url: '/system/qualification',
    method: 'post',
    data: data
  })
}

// 修改资格审核
export function updateQualification(data) {
  return request({
    url: '/system/qualification',
    method: 'put',
    data: data
  })
}

// 删除资格审核
export function delQualification(qualificationId) {
  return request({
    url: '/system/qualification/' + qualificationId,
    method: 'delete'
  })
}
