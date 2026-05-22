/**
 * 代购补贴 H5 API（后端基础路径：/h5/app/subsidyApply）
 */
import { get, post } from '@/utils/request'

/** 我的代购补贴列表 */
export function getMySubsidyList(tenantId, approveStatus) {
  const usp = new URLSearchParams()
  usp.append('tenantId', tenantId)
  if (approveStatus !== undefined && approveStatus !== null && approveStatus !== '') {
    usp.append('approveStatus', approveStatus)
  }
  return get(`/h5/app/subsidyApply/myList?${usp.toString()}`)
}

/** 代购补贴详情 */
export function getSubsidyDetail(applyId) {
  return get(`/h5/app/subsidyApply/detail/${applyId}`)
}

/** 提交代购补贴申请（需先签署承诺书并取得 commitmentId） */
export function submitSubsidy(data) {
  return post('/h5/app/subsidyApply/submit', data)
}
