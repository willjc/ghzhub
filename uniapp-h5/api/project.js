/**
 * 项目管理API
 */
import { get } from '@/utils/request'

/**
 * 根据项目类型查询项目列表
 * @param {String} projectType 项目类型（1:人才公寓 2:保租房 3:市场租赁）
 * @param {Object} params 查询参数（可选）
 * @param {String} params.projectName 项目名称（模糊查询）
 * @returns {Promise}
 */
export function getProjectListByType(projectType, params = {}) {
  return get(`/h5/project/listByType/${projectType}`, params)
}

/**
 * 分页查询项目列表
 * @param {Object} params 查询参数
 * @param {Number} params.pageNum 页码
 * @param {Number} params.pageSize 每页数量
 * @param {String} params.projectType 项目类型
 * @returns {Promise}
 */
export function getProjectPage(params) {
  return get('/h5/project/page', params)
}

/**
 * 获取项目详细信息
 * @param {Number} projectId 项目ID
 * @returns {Promise}
 */
export function getProjectDetail(projectId) {
  return get(`/h5/project/${projectId}`)
}
