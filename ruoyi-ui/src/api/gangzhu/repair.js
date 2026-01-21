import request from '@/utils/request'

// 查询物业报修列表
export function listRepair(query) {
  return request({
    url: '/gangzhu/repair/list',
    method: 'get',
    params: query
  })
}

// 查询物业报修详细
export function getRepair(repairId) {
  return request({
    url: '/gangzhu/repair/' + repairId,
    method: 'get'
  })
}

// 处理物业报修
export function handleRepair(data) {
  return request({
    url: '/gangzhu/repair/handle',
    method: 'put',
    data: data
  })
}

// 删除物业报修
export function delRepair(repairIds) {
  return request({
    url: '/gangzhu/repair/' + repairIds,
    method: 'delete'
  })
}

// 导出物业报修
export function exportRepair(query) {
  return request({
    url: '/gangzhu/repair/export',
    method: 'post',
    params: query
  })
}
