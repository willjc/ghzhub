import request from '@/utils/request'

// 查询用户列表
export function listUser(query) {
  return request({
    url: '/gangzhu/user/list',
    method: 'get',
    params: query
  })
}

// 查询用户详细
export function getUser(userId) {
  return request({
    url: '/gangzhu/user/' + userId,
    method: 'get'
  })
}

// 修改用户信息
export function updateUser(data) {
  return request({
    url: '/gangzhu/user',
    method: 'put',
    data: data
  })
}

// 修改用户状态
export function changeUserStatus(userId, status) {
  return request({
    url: '/gangzhu/user/changeStatus',
    method: 'put',
    data: {
      userId,
      status
    }
  })
}

// 删除用户
export function delUser(userId) {
  return request({
    url: '/gangzhu/user/' + userId,
    method: 'delete'
  })
}

// 导出用户
export function exportUser(query) {
  return request({
    url: '/gangzhu/user/export',
    method: 'post',
    params: query
  })
}
