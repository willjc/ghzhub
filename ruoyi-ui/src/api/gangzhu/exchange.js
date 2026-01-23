import request from '@/utils/request'

// 查询换房列表
export function listExchange(query) {
  return request({
    url: '/gangzhu/exchange/list',
    method: 'get',
    params: query
  })
}

// 查询换房详细
export function getExchange(exchangeId) {
  return request({
    url: '/gangzhu/exchange/' + exchangeId,
    method: 'get'
  })
}

// 新增换房
export function addExchange(data) {
  return request({
    url: '/gangzhu/exchange',
    method: 'post',
    data: data
  })
}

// 修改换房
export function updateExchange(data) {
  return request({
    url: '/gangzhu/exchange',
    method: 'put',
    data: data
  })
}

// 删除换房
export function delExchange(exchangeId) {
  return request({
    url: '/gangzhu/exchange/' + exchangeId,
    method: 'delete'
  })
}

// 审核换房
export function auditExchange(data) {
  return request({
    url: '/gangzhu/exchange/audit',
    method: 'post',
    data: data
  })
}

// ==================== 换房处理相关接口 ====================

// 获取项目列表
export function getProjectList() {
  return request({
    url: '/system/project/list',
    method: 'get',
    params: { pageNum: 1, pageSize: 1000 }
  })
}

// 获取项目下的楼栋列表
export function getBuildings(projectId) {
  return request({
    url: '/gangzhu/exchange/buildings/' + projectId,
    method: 'get'
  })
}

// 获取楼��下的单元列表
export function getUnits(buildingId) {
  return request({
    url: '/gangzhu/exchange/units/' + buildingId,
    method: 'get'
  })
}

// 获取单元下的可用房间列表
export function getAvailableRooms(params) {
  return request({
    url: '/gangzhu/exchange/availableRooms',
    method: 'get',
    params: params
  })
}

// 分配目标房源
export function assignTargetHouse(data) {
  return request({
    url: '/gangzhu/exchange/assignTarget',
    method: 'put',
    data: data
  })
}

// 确认换房
export function confirmExchange(data) {
  return request({
    url: '/gangzhu/exchange/confirmExchange',
    method: 'post',
    data: data
  })
}