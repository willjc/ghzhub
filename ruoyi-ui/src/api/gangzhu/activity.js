import request from '@/utils/request'

// 查询活动列表
export function listActivity(query) {
  return request({
    url: '/gangzhu/activity/list',
    method: 'get',
    params: query
  })
}

// 查询活动详细
export function getActivity(activityId) {
  return request({
    url: '/gangzhu/activity/' + activityId,
    method: 'get'
  })
}

// 新增活动
export function addActivity(data) {
  return request({
    url: '/gangzhu/activity',
    method: 'post',
    data: data
  })
}

// 修改活动
export function updateActivity(data) {
  return request({
    url: '/gangzhu/activity',
    method: 'put',
    data: data
  })
}

// 删除活动
export function delActivity(activityId) {
  return request({
    url: '/gangzhu/activity/' + activityId,
    method: 'delete'
  })
}

// 增加浏览次数
export function increaseViewCount(activityId) {
  return request({
    url: '/gangzhu/activity/view/' + activityId,
    method: 'post'
  })
}
