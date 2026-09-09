/**
 * 合同备案 H5 API（后端基础路径：/h5/app/contractFiling）
 */
import { get, post } from '@/utils/request'

/** 我的合同备案列表 */
export function getMyFilingList(tenantId, approveStatus) {
  return get('/h5/app/contractFiling/myList', { tenantId, approveStatus })
}

/** 合同备案详情 */
export function getFilingDetail(filingId) {
  return get(`/h5/app/contractFiling/detail/${filingId}`)
}

/** 提交合同备案 */
export function submitFiling(data) {
  return post('/h5/app/contractFiling/submit', data)
}
