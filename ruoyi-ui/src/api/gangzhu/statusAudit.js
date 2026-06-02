import request from '@/utils/request'

// 查询房源状态审批列表
export function listStatusAudit(query) {
  return request({
    url: '/system/house/statusAudit/list',
    method: 'get',
    params: query
  })
}

// 查询审批详情
export function getStatusAudit(auditId) {
  return request({
    url: '/system/house/statusAudit/' + auditId,
    method: 'get'
  })
}

// 审批操作（通过/驳回）
export function approveStatusAudit(data) {
  return request({
    url: '/system/house/statusAudit/approve',
    method: 'post',
    params: data
  })
}
