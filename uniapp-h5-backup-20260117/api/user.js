/**
 * 用户相关API
 */
import { get, post, request } from '@/utils/request'
import config from '@/config/index'

/**
 * 获取当前用户信息
 * @param {Number} userId 用户ID（临时参数，后期会从token获取）
 */
export function getUserInfo(userId) {
  return get('/h5/user/info', { userId })
}

/**
 * 上传工作证明附件
 * @param {File} file 文件对象
 * @param {Number} userId 用户ID（临时参数）
 */
export function uploadWorkProof(file, userId) {
  return new Promise((resolve, reject) => {
    // uni.uploadFile 需要完整的URL地址
    const fullUrl = config.uploadUrl + '/h5/user/uploadWorkProof' + (userId ? `?userId=${userId}` : '')

    uni.uploadFile({
      url: fullUrl,
      filePath: file,
      name: 'file',
      header: {
        'Authorization': uni.getStorageSync('token') || ''
      },
      success: (res) => {
        if (res.statusCode === 200) {
          try {
            const data = typeof res.data === 'string' ? JSON.parse(res.data) : res.data
            if (data.code === 200 || data.code === undefined) {
              resolve(data)
            } else {
              uni.showToast({
                title: data.msg || '上传失败',
                icon: 'none'
              })
              reject(data)
            }
          } catch (e) {
            reject(e)
          }
        } else {
          uni.showToast({
            title: '上传失败',
            icon: 'none'
          })
          reject(res)
        }
      },
      fail: (err) => {
        uni.showToast({
          title: '网络错误',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

/**
 * 更新用户信息
 * @param {Object} userData 用户数据
 */
export function updateUser(userData) {
  return request({
    url: '/h5/user/update',
    method: 'PUT',
    data: userData
  })
}

export default {
  getUserInfo,
  uploadWorkProof,
  updateUser
}
