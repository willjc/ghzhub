/**
 * 预约看房API
 */
import { get, post } from '@/utils/request'

/**
 * 获取我的预约列表
 * @param {Number} userId 用户ID
 */
export function getMyAppointments(userId) {
  return get(`/h5/appointment/user/${userId}`)
}

/**
 * 获取预约详情
 * @param {Number} appointmentId 预约ID
 */
export function getAppointmentDetail(appointmentId) {
  return get(`/h5/appointment/${appointmentId}`)
}

/**
 * 提交预约申请
 * @param {Object} data 预约数据
 */
export function applyAppointment(data) {
  return post('/h5/appointment/apply', data)
}

/**
 * 用户确认看房
 * @param {Number} appointmentId 预约ID
 */
export function confirmViewing(appointmentId) {
  return post(`/h5/appointment/confirmViewing?appointmentId=${appointmentId}`)
}

/**
 * 取消预约
 * @param {Number} appointmentId 预约ID
 * @param {String} cancelReason 取消原因
 */
export function cancelAppointment(appointmentId, cancelReason) {
  return post('/h5/appointment/cancel', {
    appointmentId,
    cancelReason
  })
}
