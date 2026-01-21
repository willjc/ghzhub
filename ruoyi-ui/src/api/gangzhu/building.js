import request from '@/utils/request'

// 查询楼栋列表
export function listBuilding(query) {
  return request({
    url: '/system/building/list',
    method: 'get',
    params: query
  })
}

// 查询楼栋详细
export function getBuilding(buildingId) {
  return request({
    url: '/system/building/' + buildingId,
    method: 'get'
  })
}

// 新增楼栋
export function addBuilding(data) {
  return request({
    url: '/system/building',
    method: 'post',
    data: data
  })
}

// 修改楼栋
export function updateBuilding(data) {
  return request({
    url: '/system/building',
    method: 'put',
    data: data
  })
}

// 删除楼栋
export function delBuilding(buildingId) {
  return request({
    url: '/system/building/' + buildingId,
    method: 'delete'
  })
}
