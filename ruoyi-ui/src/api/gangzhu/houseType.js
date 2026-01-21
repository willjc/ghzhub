import request from '@/utils/request'

// 查询户型列表
export function listHouseType(query) {
  return request({
    url: '/gangzhu/houseType/list',
    method: 'get',
    params: query
  })
}

// 查询户型详细
export function getHouseType(houseTypeId) {
  return request({
    url: '/gangzhu/houseType/' + houseTypeId,
    method: 'get'
  })
}

// 新增户型
export function addHouseType(data) {
  return request({
    url: '/gangzhu/houseType',
    method: 'post',
    data: data
  })
}

// 修改户型
export function updateHouseType(data) {
  return request({
    url: '/gangzhu/houseType',
    method: 'put',
    data: data
  })
}

// 删除户型
export function delHouseType(houseTypeId) {
  return request({
    url: '/gangzhu/houseType/' + houseTypeId,
    method: 'delete'
  })
}
