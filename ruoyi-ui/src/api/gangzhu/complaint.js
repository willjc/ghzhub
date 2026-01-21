import request from '@/utils/request'

// 查询投诉建议列表
export function listComplaint(query) {
  return request({
    url: '/gangzhu/complaint/list',
    method: 'get',
    params: query
  })
}

// 查询投诉建议详细
export function getComplaint(complaintId) {
  return request({
    url: '/gangzhu/complaint/' + complaintId,
    method: 'get'
  })
}

// 处理投诉建议
export function handleComplaint(data) {
  return request({
    url: '/gangzhu/complaint/handle',
    method: 'put',
    data: data
  })
}

// 删除投诉建议
export function delComplaint(complaintIds) {
  return request({
    url: '/gangzhu/complaint/' + complaintIds,
    method: 'delete'
  })
}

// 导出投诉建议
export function exportComplaint(query) {
  return request({
    url: '/gangzhu/complaint/export',
    method: 'post',
    params: query
  })
}
