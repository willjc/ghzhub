/**
 * 活动相关 API
 */
import request from '@/utils/request'

/**
 * 获取活动列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getActivityList(params) {
  return request.get('/h5/activity/list', params)
}

/**
 * 获取活动详情
 * @param {Number} activityId 活动ID
 * @returns {Promise}
 */
export function getActivityDetail(activityId) {
  return request.get(`/h5/activity/${activityId}`)
}

/**
 * 活动报名
 * @param {Object} data 报名数据
 * @param {Number} data.activityId 活动ID
 * @param {Number} data.userId 用户ID
 * @param {String} data.realName 真实姓名（可选）
 * @param {String} data.phone 手机号（可选）
 * @returns {Promise}
 */
export function registerActivity(data) {
  return request.post('/h5/activity/register', data)
}

/**
 * 检查用户是否已报名
 * @param {Number} activityId 活动ID
 * @param {Number} userId 用户ID
 * @returns {Promise}
 */
export function checkRegistered(activityId, userId) {
  return request.get(`/h5/activity/check-registered/${activityId}`, { userId })
}

/**
 * 获取用户的所有报名记录
 * @param {Number} userId 用户ID
 * @returns {Promise}
 */
export function getMyRegistrations(userId) {
  return request.get('/h5/activity/my-registrations', { userId })
}
