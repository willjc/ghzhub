import request from '@/utils/request'

// 查询房源列表
export function listHouse(query) {
  return request({
    url: '/system/house/list',
    method: 'get',
    params: query
  })
}

// 查询房源详细
export function getHouse(houseId) {
  return request({
    url: '/system/house/' + houseId,
    method: 'get'
  })
}

// 新增房源
export function addHouse(data) {
  return request({
    url: '/system/house',
    method: 'post',
    data: data
  })
}

// 修改房源
export function updateHouse(data) {
  return request({
    url: '/system/house',
    method: 'put',
    data: data
  })
}

// 删除房源
export function delHouse(houseId) {
  return request({
    url: '/system/house/' + houseId,
    method: 'delete'
  })
}

// 查询所有可用房型列表(用于下拉选择)
export function listAllHouseType() {
  return request({
    url: '/gangzhu/houseType/listAll',
    method: 'get'
  })
}

// 查询房源详细信息（包含关联数据）
export function getHouseDetail(houseId) {
  return request({
    url: '/system/house/detail/' + houseId,
    method: 'get'
  })
}

// 导出房源数据
export function exportHouse(query) {
  return request({
    url: '/system/house/export',
    method: 'post',
    params: query
  })
}

// 获取房源图片列表
export function getHouseImages(houseId) {
  return request({
    url: '/system/house/' + houseId + '/images',
    method: 'get'
  })
}

// 保存房源图片
export function saveHouseImages(data) {
  return request({
    url: '/system/house/images',
    method: 'post',
    data: data
  })
}

// 获取房源VR列表
export function getHouseVrs(houseId) {
  return request({
    url: '/system/house/' + houseId + '/vrs',
    method: 'get'
  })
}

// 保存房源VR
export function saveHouseVrs(data) {
  return request({
    url: '/system/house/vrs',
    method: 'post',
    data: data
  })
}
