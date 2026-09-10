import request from '@/utils/request'

// 查询黑名单列表
export function listBlacklist(query) {
  return request({
    url: '/gangzhu/blacklist/list',
    method: 'get',
    params: query
  })
}

// 新增黑名单
export function addBlacklist(data) {
  return request({
    url: '/gangzhu/blacklist',
    method: 'post',
    data: data
  })
}

// 解除黑名单
export function removeBlacklist(blacklistId, removeReason) {
  return request({
    url: '/gangzhu/blacklist/remove/' + blacklistId,
    method: 'put',
    data: { removeReason: removeReason }
  })
}

// 查询用户黑名单状态
export function checkBlacklist(userId) {
  return request({
    url: '/gangzhu/blacklist/check/' + userId,
    method: 'get'
  })
}
