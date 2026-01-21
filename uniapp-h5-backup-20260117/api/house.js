/**
 * 房源管理API
 */
import { get } from '@/utils/request'

/**
 * 获取房源列表（按楼层分组）
 * @param {Object} params 查询参数
 * @param {Number} params.projectId 项目ID
 * @param {Number} params.buildingId 楼栋ID
 * @param {Number} params.unitId 单元ID
 * @param {String} params.houseType 户型（可选）
 * @param {Number} params.floorMin 最小楼层（可选）
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
