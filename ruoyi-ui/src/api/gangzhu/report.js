import request from '@/utils/request'

// 收款台账汇总
export function getReceiptSummary(params) {
  return request({
    url: '/system/report/receipt/summary',
    method: 'get',
    params
  })
}

// 收款明细列表
export function getReceiptDetail(params) {
  return request({
    url: '/system/report/receipt/detail',
    method: 'get',
    params
  })
}

// 自定义报表生成
export function generateCustomReport(data) {
  return request({
    url: '/system/report/custom/generate',
    method: 'post',
    data
  })
}

// 项目列表（筛选用）
export function getProjectList() {
  return request({
    url: '/system/project/list',
    method: 'get',
    params: { pageNum: 1, pageSize: 1000, status: '0' }
  })
}
