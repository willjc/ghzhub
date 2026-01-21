import request from '@/utils/request'

// 查询退租申请列表
export function listCheckout(query) {
  return request({
    url: '/system/checkout/list',
    method: 'get',
    params: query
  })
}

// 查询退租申请详细
export function getCheckout(applyId) {
  return request({
    url: '/system/checkout/' + applyId,
    method: 'get'
  })
}

// 新增退租申请
export function addCheckout(data) {
  return request({
    url: '/system/checkout',
    method: 'post',
    data: data
  })
}

// 修改退租申请
export function updateCheckout(data) {
  return request({
    url: '/system/checkout',
    method: 'put',
    data: data
  })
}

// 删除退租申请
export function delCheckout(applyIds) {
  return request({
    url: '/system/checkout/' + applyIds,
    method: 'delete'
  })
}

// 审批退租申请
export function approveCheckout(data) {
  return request({
    url: '/system/checkout/approve',
    method: 'post',
    data: data
  })
}

// 计算退租费用
export function calculateCheckout(data) {
  return request({
    url: '/system/checkout/calculate',
    method: 'post',
    data: data
  })
}

// 获取退租记录详情
export function getCheckoutRecord(recordId) {
  return request({
    url: '/system/checkout/record/' + recordId,
    method: 'get'
  })
}

// 根据申请ID获取退租记录
export function getCheckoutRecordByApplyId(applyId) {
  return request({
    url: '/system/checkout/record/byApply/' + applyId,
    method: 'get'
  })
}

// 获取合同的账单列表（用于退租审批时查看缴费记录）
export function getContractBills(contractId) {
  return request({
    url: '/system/checkout/bills/' + contractId,
    method: 'get'
  })
}
