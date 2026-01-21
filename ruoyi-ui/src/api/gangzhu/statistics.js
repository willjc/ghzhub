import request from '@/utils/request'

// 获取首页统计数据
export function getDashboardStats(month) {
  return request({
    url: '/gangzhu/statistics/dashboard',
    method: 'get',
    params: { month }
  })
}

// 获取房源统计数据
export function getHouseStats() {
  return request({
    url: '/gangzhu/statistics/house',
    method: 'get'
  })
}

// 获取租户画像数据
export function getTenantProfile() {
  return request({
    url: '/gangzhu/statistics/tenant-profile',
    method: 'get'
  })
}

// 获取学历职业数据
export function getEducationJob() {
  return request({
    url: '/gangzhu/statistics/education-job',
    method: 'get'
  })
}

// 获取项目台账数据
export function getProjectLedger(month) {
  return request({
    url: '/gangzhu/statistics/project-ledger',
    method: 'get',
    params: { month }
  })
}