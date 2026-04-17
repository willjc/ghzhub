import request from '@/utils/request'

// 查询设施总表
export function listFacilityItem(query) {
  return request({
    url: '/gangzhu/facilityItem/list',
    method: 'get',
    params: query
  })
}
