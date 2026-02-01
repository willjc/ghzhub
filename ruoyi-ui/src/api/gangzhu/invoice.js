import request from '@/utils/request'

// 查询开票申请列表
export function listInvoice(query) {
  return request({
    url: '/gangzhu/invoice/list',
    method: 'get',
    params: query
  })
}

// 查询开票申请���细
export function getInvoice(applyId) {
  return request({
    url: '/gangzhu/invoice/' + applyId,
    method: 'get'
  })
}

// 完成开票（上传发票）
export function completeInvoice(data) {
  return request({
    url: '/gangzhu/invoice/complete',
    method: 'post',
    data: data
  })
}

// 删除开票申请
export function delInvoice(applyId) {
  return request({
    url: '/gangzhu/invoice/' + applyId,
    method: 'delete'
  })
}

// 导出开票申请
export function exportInvoice(query) {
  return request({
    url: '/gangzhu/invoice/export',
    method: 'post',
    params: query
  })
}
