import request from '@/utils/request'

// 查询服务公司列表（分页）
export function listServiceCompany(query) {
  return request({
    url: '/gangzhu/serviceCompany/list',
    method: 'get',
    params: query
  })
}

// 查询启用中的服务公司（订单分配下拉用）
export function activeServiceCompanies(orderType) {
  return request({
    url: '/gangzhu/serviceCompany/active',
    method: 'get',
    params: { orderType }
  })
}

// 查询服务公司详情
export function getServiceCompany(companyId) {
  return request({
    url: '/gangzhu/serviceCompany/' + companyId,
    method: 'get'
  })
}

// 新增服务公司
export function addServiceCompany(data) {
  return request({
    url: '/gangzhu/serviceCompany',
    method: 'post',
    data: data
  })
}

// 修改服务公司
export function updateServiceCompany(data) {
  return request({
    url: '/gangzhu/serviceCompany',
    method: 'put',
    data: data
  })
}

// 删除服务公司
export function delServiceCompany(companyIds) {
  return request({
    url: '/gangzhu/serviceCompany/' + companyIds,
    method: 'delete'
  })
}
