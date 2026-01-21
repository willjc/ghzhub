import request from '@/utils/request'

// 查询预约看房列表
export function listAppointment(query) {
  return request({
    url: '/system/appointment/list',
    method: 'get',
    params: query
  })
}

// 查询预约看房详细
export function getAppointment(appointmentId) {
  return request({
    url: '/system/appointment/' + appointmentId,
    method: 'get'
  })
}

// 新增预约看房
export function addAppointment(data) {
  return request({
    url: '/system/appointment',
    method: 'post',
    data: data
  })
}

// 修改预约看房
export function updateAppointment(data) {
  return request({
    url: '/system/appointment',
    method: 'put',
    data: data
  })
}

// 删除预约看房
export function delAppointment(appointmentId) {
  return request({
    url: '/system/appointment/' + appointmentId,
    method: 'delete'
  })
}

// 确认预约
export function confirmAppointment(appointmentId) {
  return request({
    url: '/system/appointment/confirm/' + appointmentId,
    method: 'put'
  })
}

// 取消预约
export function cancelAppointment(appointmentId, cancelReason) {
  return request({
    url: '/system/appointment/cancel/' + appointmentId,
    method: 'put',
    data: cancelReason
  })
}

// 审核完成看房(管理员)
export function completeAppointment(appointmentId) {
  return request({
    url: '/system/appointment/complete/' + appointmentId,
    method: 'put'
  })
}
