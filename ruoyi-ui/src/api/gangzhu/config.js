import request from '@/utils/request'

// 查询运营配置列表
export function listConfig(query) {
  return request({
    url: '/gangzhu/config/list',
    method: 'get',
    params: query
  })
}

// 查询运营配置详细
export function getConfig(configId) {
  return request({
    url: '/gangzhu/config/' + configId,
    method: 'get'
  })
}

// 新增运营配置
export function addConfig(data) {
  return request({
    url: '/gangzhu/config',
    method: 'post',
    data: data
  })
}

// 修改运营配置
export function updateConfig(data) {
  return request({
    url: '/gangzhu/config',
    method: 'put',
    data: data
  })
}

// 删除运营配置
export function delConfig(configId) {
  return request({
    url: '/gangzhu/config/' + configId,
    method: 'delete'
  })
}

// 导出运营配置
export function exportConfig(query) {
  return request({
    url: '/gangzhu/config/export',
    method: 'post',
    params: query
  })
}
