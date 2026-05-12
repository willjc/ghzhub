import request from '@/utils/request'

// 查询项目列表
export function listProject(query) {
  return request({
    url: '/system/project/list',
    method: 'get',
    params: query
  })
}

// 查询项目详细
export function getProject(projectId) {
  return request({
    url: '/system/project/' + projectId,
    method: 'get'
  })
}

// 新增项目
export function addProject(data) {
  return request({
    url: '/system/project',
    method: 'post',
    data: data
  })
}

// 修改项目
export function updateProject(data) {
  return request({
    url: '/system/project',
    method: 'put',
    data: data
  })
}

// 删除项目
export function delProject(projectId) {
  return request({
    url: '/system/project/' + projectId,
    method: 'delete'
  })
}

// 批量生成标准房型
export function generateHouseTypes(projectId) {
  return request({
    url: '/system/project/' + projectId + '/generateHouseTypes',
    method: 'post'
  })
}

// 按项目批量修改房源状态（受控过渡：0/3/4 可互转，跳过 1/2）
export function batchUpdateHouseStatusByProject(projectId, targetStatus) {
  return request({
    url: '/system/house/project/' + projectId + '/batchUpdateStatus',
    method: 'post',
    data: { targetStatus }
  })
}
