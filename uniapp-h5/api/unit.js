/**
 * 单元管理API
 */
import { get } from '@/utils/request'

/**
 * 根据楼栋ID获取单元列表
 * @param {Number} buildingId 楼栋ID
 * @returns {Promise}
 */
export function getUnitListByBuilding(buildingId) {
  return get(`/h5/app/unit/list/${buildingId}`)
}
