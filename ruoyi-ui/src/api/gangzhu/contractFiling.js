import request from '@/utils/request'

// 查询合同备案列表
export function listContractFiling(query) {
  return request({
    url: '/gangzhu/contractFiling/list',
    method: 'get',
    params: query
  })
}

// 查询合同备案详情
export function getContractFiling(filingId) {
  return request({
    url: '/gangzhu/contractFiling/' + filingId,
    method: 'get'
  })
}

// 审批（通过/驳回）
export function approveContractFiling(data) {
  return request({
    url: '/gangzhu/contractFiling/approve',
    method: 'post',
    data: data
  })
}

// 删除合同备案
export function delContractFiling(filingIds) {
  return request({
    url: '/gangzhu/contractFiling/' + filingIds,
    method: 'delete'
  })
}
