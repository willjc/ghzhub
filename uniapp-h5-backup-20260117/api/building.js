/**
 * 楼栋管理API
 */
import { get } from '@/utils/request'

/**
 * 根据项目ID获取楼栋列表
 * @param {Number} projectId 项目ID
 * @returns {Promise}
 */
export function getBuildingListByProject(projectId) {
  return get(`/h5/app/building/list/${projectId}`)
}
