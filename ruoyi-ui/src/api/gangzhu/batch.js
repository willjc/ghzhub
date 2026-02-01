import request from '@/utils/request'

// 查询配租批次列表
export function listBatch(query) {
  return request({
    url: '/system/batch/list',
    method: 'get',
    params: query
  })
}

// 查询配租批次详细
export function getBatch(batchId) {
  return request({
    url: '/system/batch/' + batchId,
    method: 'get'
  })
}

// 新增配租批次
export function addBatch(data) {
  return request({
    url: '/system/batch',
    method: 'post',
    data: data
  })
}

// 修改配租批次
export function updateBatch(data) {
  return request({
    url: '/system/batch',
    method: 'put',
    data: data
  })
}

// 删除配租批次
export function delBatch(batchId) {
  return request({
    url: '/system/batch/' + batchId,
    method: 'delete'
  })
}

// 导出配租批次
export function exportBatch(query) {
  return request({
    url: '/system/batch/export',
    method: 'post',
    params: query
  })
}

// 审批配租批次
export function approveBatch(data) {
  return request({
    url: '/system/batch/approve',
    method: 'put',
    data: data
  })
}

// 作废配租批次
export function cancelBatch(batchId) {
  return request({
    url: '/system/batch/cancel/' + batchId,
    method: 'put'
  })
}

// 下载人员导入模板
export function downloadTenantTemplate() {
  return request({
    url: '/system/batch/importTemplate',
    method: 'get',
    responseType: 'blob'
  })
}

// 根据项目ID获取可用房源列表
export function getAvailableHouses(projectIds) {
  return request({
    url: '/system/batch/availableHouses',
    method: 'get',
    params: { projectIds }
  })
}

// 导入人员数据
export function importTenants(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/system/batch/importTenants',
    method: 'post',
    data: formData,
    transformRequest: [function(data, headers) {
      delete headers['Content-Type']
      return data
    }]
  })
}

// 保存批次分配（包含房源和人员匹配）
export function saveBatchAllocation(data) {
  return request({
    url: '/system/batch/saveAllocation',
    method: 'post',
    data: data
  })
}

// 获取批次房源列表
export function getBatchHouses(batchId) {
  return request({
    url: '/system/batch/' + batchId + '/houses',
    method: 'get'
  })
}

// 获取批次人员列表
export function getBatchTenants(batchId) {
  return request({
    url: '/system/batch/' + batchId + '/tenants',
    method: 'get'
  })
}
