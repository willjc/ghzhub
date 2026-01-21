import request from '@/utils/request'

// 查询承诺书记录列表
export function listCommitment(query) {
  return request({
    url: '/system/commitment/list',
    method: 'get',
    params: query
  })
}

// 查询承诺书记录详细
export function getCommitment(commitmentId) {
  return request({
    url: '/system/commitment/' + commitmentId,
    method: 'get'
  })
}

// 修改承诺书记录状态
export function updateCommitment(data) {
  return request({
    url: '/system/commitment',
    method: 'put',
    data: data
  })
}

// 删除承诺书记录
export function delCommitment(commitmentId) {
  return request({
    url: '/system/commitment/' + commitmentId,
    method: 'delete'
  })
}

// 导出承诺书记录
export function exportCommitment(query) {
  return request({
    url: '/system/commitment/export',
    method: 'post',
    params: query
  })
}
