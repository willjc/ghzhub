import request from '@/utils/request'

// 查询合同模版列表
export function listTemplate(query) {
  return request({
    url: '/system/contract/template/list',
    method: 'get',
    params: query
  })
}

// 查询合同模版详细
export function getTemplate(templateId) {
  return request({
    url: '/system/contract/template/' + templateId,
    method: 'get'
  })
}

// 根据合同类型查询模版
export function getTemplateByType(contractType) {
  return request({
    url: '/system/contract/template/type/' + contractType,
    method: 'get'
  })
}

// 新增合同模版
export function addTemplate(data) {
  return request({
    url: '/system/contract/template',
    method: 'post',
    data: data
  })
}

// 修改合同模版
export function updateTemplate(data) {
  return request({
    url: '/system/contract/template',
    method: 'put',
    data: data
  })
}

// 删除合同模版
export function delTemplate(templateId) {
  return request({
    url: '/system/contract/template/' + templateId,
    method: 'delete'
  })
}

// 导出合同模版
export function exportTemplate(query) {
  return request({
    url: '/system/contract/template/export',
    method: 'post',
    params: query
  })
}
