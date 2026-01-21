import request from '@/utils/request'

// 查询企业客户列表
export function listEnterprise(query) {
  return request({
    url: '/system/enterprise/list',
    method: 'get',
    params: query
  })
}

// 查询企业客户详细
export function getEnterprise(enterpriseId) {
  return request({
    url: '/system/enterprise/' + enterpriseId,
    method: 'get'
  })
}

// 新增企业客户
export function addEnterprise(data) {
  return request({
    url: '/system/enterprise',
    method: 'post',
    data: data
  })
}

// 修改企业客户
export function updateEnterprise(data) {
  return request({
    url: '/system/enterprise',
    method: 'put',
    data: data
  })
}

// 删除企业客户
export function delEnterprise(enterpriseId) {
  return request({
    url: '/system/enterprise/' + enterpriseId,
    method: 'delete'
  })
}

// 导出企业客户
export function exportEnterprise(query) {
  return request({
    url: '/system/enterprise/export',
    method: 'post',
    params: query
  })
}

// ========== 企业批次相关接口 ==========

// 查询企业批次列表
export function listEnterpriseBatch(query) {
  return request({
    url: '/system/enterpriseBatch/list',
    method: 'get',
    params: query
  })
}

// 查询企业批次详细
export function getEnterpriseBatch(batchId) {
  return request({
    url: '/system/enterpriseBatch/' + batchId,
    method: 'get'
  })
}

// 新增企业批次
export function addEnterpriseBatch(data) {
  return request({
    url: '/system/enterpriseBatch',
    method: 'post',
    data: data
  })
}

// 修改企业批次
export function updateEnterpriseBatch(data) {
  return request({
    url: '/system/enterpriseBatch',
    method: 'put',
    data: data
  })
}

// 删除企业批次
export function delEnterpriseBatch(batchId) {
  return request({
    url: '/system/enterpriseBatch/' + batchId,
    method: 'delete'
  })
}

// 保存企业批次及房源关联
export function saveEnterpriseBatchWithHouses(data) {
  return request({
    url: '/system/enterpriseBatch/saveWithHouses',
    method: 'post',
    data: data
  })
}

// 获取企业批次关联的房源列表
export function getEnterpriseBatchHouses(batchId) {
  return request({
    url: '/system/enterpriseBatch/' + batchId + '/houses',
    method: 'get'
  })
}

// ========== 企业账单相关接口 ==========

// 查询企业账单列表
export function listEnterpriseBill(query) {
  return request({
    url: '/system/enterpriseBill/list',
    method: 'get',
    params: query
  })
}

// 查询企业账单详细
export function getEnterpriseBill(billId) {
  return request({
    url: '/system/enterpriseBill/' + billId,
    method: 'get'
  })
}

// 根据批次ID获取账单
export function getEnterpriseBillByBatchId(batchId) {
  return request({
    url: '/system/enterpriseBill/batch/' + batchId,
    method: 'get'
  })
}

// 新增企业账单
export function addEnterpriseBill(data) {
  return request({
    url: '/system/enterpriseBill',
    method: 'post',
    data: data
  })
}

// 修改企业账单
export function updateEnterpriseBill(data) {
  return request({
    url: '/system/enterpriseBill',
    method: 'put',
    data: data
  })
}

// 删除企业账单
export function delEnterpriseBill(billId) {
  return request({
    url: '/system/enterpriseBill/' + billId,
    method: 'delete'
  })
}

// 审核企业账单
export function approveEnterpriseBill(billId, data) {
  return request({
    url: '/system/enterpriseBill/approve/' + billId,
    method: 'put',
    data: data
  })
}
