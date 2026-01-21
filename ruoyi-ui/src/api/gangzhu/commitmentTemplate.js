import request from '@/utils/request'

// 查询承诺书模板列表
export function listCommitmentTemplate(query) {
  return request({
    url: '/system/commitment/template/list',
    method: 'get',
    params: query
  })
}

// 查询承诺书模板详细
export function getCommitmentTemplate(templateId) {
  return request({
    url: '/system/commitment/template/' + templateId,
    method: 'get'
  })
}

// 根据类型查询默认模板
export function getDefaultTemplate(commitmentType) {
  return request({
    url: '/system/commitment/template/default/' + commitmentType,
    method: 'get'
  })
}

// 新增承诺书模板
export function addCommitmentTemplate(data) {
  return request({
    url: '/system/commitment/template',
    method: 'post',
    data: data
  })
}

// 修改承诺书模板
export function updateCommitmentTemplate(data) {
  return request({
    url: '/system/commitment/template',
    method: 'put',
    data: data
  })
}

// 删除承诺书模板
export function delCommitmentTemplate(templateId) {
  return request({
    url: '/system/commitment/template/' + templateId,
    method: 'delete'
  })
}
