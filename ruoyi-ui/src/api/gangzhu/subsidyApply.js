import request from '@/utils/request'

// 查询代购补贴列表
export function listSubsidyApply(query) {
  return request({
    url: '/gangzhu/subsidyApply/list',
    method: 'get',
    params: query
  })
}

// 查询代购补贴详情
export function getSubsidyApply(applyId) {
  return request({
    url: '/gangzhu/subsidyApply/' + applyId,
    method: 'get'
  })
}

// 审批（通过/驳回）
export function approveSubsidyApply(data) {
  return request({
    url: '/gangzhu/subsidyApply/approve',
    method: 'post',
    data: data
  })
}

// 删除代购补贴
export function delSubsidyApply(applyIds) {
  return request({
    url: '/gangzhu/subsidyApply/' + applyIds,
    method: 'delete'
  })
}
