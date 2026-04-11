import request from '@/utils/request'

// 查询合同列表
export function listContract(query) {
  return request({
    url: '/system/contract/list',
    method: 'get',
    params: query
  })
}

// 查询合同详细
export function getContract(contractId) {
  return request({
    url: '/system/contract/' + contractId,
    method: 'get'
  })
}

// 新增合同
export function addContract(data) {
  return request({
    url: '/system/contract',
    method: 'post',
    data: data
  })
}

// 修改合同
export function updateContract(data) {
  return request({
    url: '/system/contract',
    method: 'put',
    data: data
  })
}

// 删除合同
export function delContract(contractId) {
  return request({
    url: '/system/contract/' + contractId,
    method: 'delete'
  })
}

// 审核合同
export function approveContract(data) {
  return request({
    url: '/system/contract/approve',
    method: 'post',
    data: data
  })
}

// 查询合同缴费记录
export function getContractBills(contractId) {
  return request({
    url: '/system/contract/' + contractId + '/bills',
    method: 'get'
  })
}

// 查询合同用户资料
export function getContractDocuments(contractId) {
  return request({
    url: '/system/contract/' + contractId + '/documents',
    method: 'get'
  })
}

// 审核用户资料（管理端代理调用 h5 接口）
export function auditDocument(data) {
  return request({
    url: '/h5/document/audit',
    method: 'put',
    data: data
  })
}

// 获取合同 PDF 查看链接（管理端，实时刷新 e签宝 链接）
export function getContractPdfUrl(contractId) {
  return request({
    url: '/system/contract/' + contractId + '/pdf-url',
    method: 'get'
  })
}
