import request from '@/utils/request'

export function listHouseFacility(houseId) {
  return request({ url: '/gangzhu/houseFacility/list/' + houseId, method: 'get' })
}

export function batchSaveHouseFacility(data) {
  return request({ url: '/gangzhu/houseFacility/batchSave', method: 'post', data: data })
}

export function pullFromType(data) {
  return request({ url: '/gangzhu/houseFacility/pullFromType', method: 'post', data: data })
}
