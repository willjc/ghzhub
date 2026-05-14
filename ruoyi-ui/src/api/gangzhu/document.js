import request from '@/utils/request'

// 分页查询资料文档列表（管理端）
export function listDocument(query) {
  return request({
    url: '/system/document/list',
    method: 'get',
    params: query
  })
}

// 审核资料（复用 h5 通道）
// 入参：{ documentId, auditStatus: '1'|'2', auditOpinion }
export function auditDocument(data) {
  return request({
    url: '/h5/document/audit',
    method: 'put',
    data: data
  })
}

// 标记资料违规（抽查专用，仅留痕，不动订单/合同）
// 入参：{ documentId, violationReason }
export function markViolation(data) {
  return request({
    url: '/h5/document/violation',
    method: 'put',
    data: data
  })
}
