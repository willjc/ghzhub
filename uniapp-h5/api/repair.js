/**
 * 物业报修相关API
 */
import { get, post, put } from '@/utils/request'

/**
 * 提交物业报修
 * @param {Object} data 报修数据
 * @param {number} data.userId 用户ID
 * @param {number} data.projectId 项目ID
 * @param {number} data.buildingId 楼栋ID
 * @param {number} data.unitId 单元ID
 * @param {string} data.location 所在位置
 * @param {string} data.roomNo 房间号
 * @param {string} data.phone 联系电话
 * @param {string} data.description 问题描述
 * @param {string} data.images 图片（多个用逗号分隔）
 * @param {string} data.repairDate 期望维修时间
 */
export function submitRepair(data) {
  return post('/h5/app/repair/submit', data)
}

/**
 * 我的报修列表
 * @param {number} userId 用户ID
 */
export function getMyRepairList(userId) {
  return get(`/h5/app/repair/myList?userId=${userId}`)
}

/**
 * 报修详情
 * @param {number} id 报修ID
 * @param {number} userId 用户ID
 */
export function getRepairDetail(id, userId) {
  return get(`/h5/app/repair/${id}?userId=${userId}`)
}

/**
 * 取消报修
 * @param {number} id 报修ID
 * @param {number} userId 用户ID
 */
export function cancelRepair(id, userId) {
  return put(`/h5/app/repair/cancel/${id}?userId=${userId}`)
}
