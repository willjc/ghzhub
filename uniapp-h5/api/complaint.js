/**
 * 投诉建议相关API
 */
import { get, post, put } from '@/utils/request'

/**
 * 提交投诉建议
 * @param {Object} data 投诉数据
 * @param {string} data.title 投诉标题
 * @param {string} data.content 投诉内容
 * @param {string} data.contactPhone 联系方式
 * @param {string} data.images 图片（多个用逗号分隔）
 * @param {number} data.userId 用户ID
 */
export function submitComplaint(data) {
  return post('/h5/app/complaint/submit', data)
}

/**
 * 我的投诉列表
 * @param {number} userId 用户ID
 */
export function getMyComplaintList(userId) {
  return get(`/h5/app/complaint/myList?userId=${userId}`)
}

/**
 * 投诉详情
 * @param {number} id 投诉ID
 * @param {number} userId 用户ID
 */
export function getComplaintDetail(id, userId) {
  return get(`/h5/app/complaint/${id}?userId=${userId}`)
}

/**
 * 催办
 * @param {number} id 投诉ID
 * @param {number} userId 用户ID
 */
export function urgeComplaint(id, userId) {
  return put(`/h5/app/complaint/urge/${id}?userId=${userId}`)
}

/**
 * 取消投诉
 * @param {number} id 投诉ID
 * @param {number} userId 用户ID
 */
export function cancelComplaint(id, userId) {
  return put(`/h5/app/complaint/cancel/${id}?userId=${userId}`)
}
