import request from '@/utils/request'

// 查询户型已配置的设施
export function listHouseTypeFacility(houseTypeId) {
  return request({
    url: '/gangzhu/houseTypeFacility/list/' + houseTypeId,
    method: 'get'
  })
}

// 批量保存户型设施
export function batchSaveHouseTypeFacility(data) {
  return request({
    url: '/gangzhu/houseTypeFacility/batchSave',
    method: 'post',
    data: data
  })
}
