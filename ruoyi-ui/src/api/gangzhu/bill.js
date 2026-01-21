import request from '@/utils/request'

// 查询账单列表
export function listBill(query) {
  return request({
    url: '/system/bill/list',
    method: 'get',
    params: query
  })
}

// 查询账单详细
export function getBill(billId) {
  return request({
    url: '/system/bill/' + billId,
    method: 'get'
  })
}

// 新增账单
export function addBill(data) {
  return request({
    url: '/system/bill',
    method: 'post',
    data: data
  })
}

// 修改账单
export function updateBill(data) {
  return request({
    url: '/system/bill',
    method: 'put',
    data: data
  })
}

// 删除账单
export function delBill(billId) {
  return request({
    url: '/system/bill/' + billId,
    method: 'delete'
  })
}
