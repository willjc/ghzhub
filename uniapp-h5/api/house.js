/**
 * 房源管理API
 */
import { get } from '@/utils/request'

/**
 * 按项目类型获取房源列表（用于首页房源展示）
 * @param {String} projectType 项目类型（1:人才公寓 2:保租房 3:市场租赁）
 * @param {Number} limit 限制返回数量（可选，默认10）
 * @returns {Promise}
 */
export function getHouseListByProjectType(projectType, limit = 10) {
  return get(`/h5/app/house/listByProjectType/${projectType}`, { limit })
}

/**
 * 获取房源列表（按楼层分组）
 * @param {Object} params 查询参数
 * @param {Number} params.projectId 项目ID
 * @param {Number} params.buildingId 楼栋ID
 * @param {Number} params.unitId 单元ID
 * @param {String} params.houseType 户型（可选）
 * @param {Number} params.floorMin 最小楼层（可���）
 * @param {Number} params.floorMax 最大楼层（可选）
 * @param {String} params.orientation 朝向（可选）
 * @returns {Promise}
 */
export function getHouseList(params) {
  return get('/h5/app/house/list', params)
}

/**
 * 获取房源详情
 * @param {Number} houseId 房源ID
 * @returns {Promise}
 */
export function getHouseDetail(houseId) {
  return get(`/h5/app/house/${houseId}`)
}

/**
 * 获取房源VR列表
 * @param {Number} roomId 房源ID
 * @returns {Promise}
 */
export function getHouseVR(roomId) {
  return get(`/h5/app/house/${roomId}/vr`)
}

/**
 * 获取房源图片（按分类返回）
 * @param {Number} roomId 房源ID
 * @returns {Promise}
 */
export function getHouseImages(roomId) {
  return get(`/h5/app/house/${roomId}/images`)
}
