/**
 * 申诉相关API
 */
import { get } from '@/utils/request'

/**
 * 获取当前用户的申诉列表
 */
export function getAppealList() {
  return get('/h5/qualification/appeal/list')
}

/**
 * 获取申诉详情
 * @param {Number} appealId 申诉ID
 */
export function getAppealDetail(appealId) {
  return get(`/h5/qualification/appeal/${appealId}`)
}
