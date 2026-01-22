/**
 * 地图找房相关API
 */
import { get } from '@/utils/request'

/**
 * 获取地图找房项目列表（带经纬度）
 * @param {String} projectType 项目类型（1:人才公寓 2:保租房 3:市场租赁）
 * @returns {Promise}
 */
export function getMapProjects(projectType) {
  return get(`/h5/project/listByType/${projectType}`)
}

/**
 * 获取项目详细信息
 * @param {Number} projectId 项目ID
 * @returns {Promise}
 */
export function getProjectDetail(projectId) {
  return get(`/h5/project/${projectId}`)
}
