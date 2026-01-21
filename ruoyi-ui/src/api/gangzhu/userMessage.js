import request from '@/utils/request'

// 查询用户消息列表
export function listUserMessage(query) {
  return request({
    url: '/gangzhu/userMessage/list',
    method: 'get',
    params: query
  })
}

// 查询用户消息详细
export function getUserMessage(messageId) {
  return request({
    url: '/gangzhu/userMessage/' + messageId,
    method: 'get'
  })
}

// 新增用户消息
export function addUserMessage(data) {
  return request({
    url: '/gangzhu/userMessage',
    method: 'post',
    data: data
  })
}

// 修改用户消息
export function updateUserMessage(data) {
  return request({
    url: '/gangzhu/userMessage',
    method: 'put',
    data: data
  })
}

// 删除用户消息
export function delUserMessage(messageIds) {
  return request({
    url: '/gangzhu/userMessage/' + messageIds,
    method: 'delete'
  })
}

// 导出用户消息
export function exportUserMessage(query) {
  return request({
    url: '/gangzhu/userMessage/export',
    method: 'post',
    params: query
  })
}
