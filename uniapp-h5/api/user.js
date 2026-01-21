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
 * 上传用户头像
 * @param {String} filePath 本地文件路径
 * @param {Number} userId 用户ID（临时参数）
 */
export function uploadAvatar(filePath, userId) {
  return new Promise((resolve, reject) => {
    const fullUrl = config.uploadUrl + '/h5/user/uploadAvatar' + (userId ? `?userId=${userId}` : '')

    console.log('上传头像URL:', fullUrl)
    console.log('uploadUrl配置:', config.uploadUrl)

    uni.uploadFile({
      url: fullUrl,
      filePath: filePath,
      name: 'file',
      header: {
        'Authorization': uni.getStorageSync('token') || ''
      },
      success: (res) => {
        console.log('上传响应:', res)
        if (res.statusCode === 200) {
          try {
            const data = typeof res.data === 'string' ? JSON.parse(res.data) : res.data
            if (data.code === 200) {
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
        console.error('上传失败:', err)
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
  const { userId, ...data } = userData
  return request({
    url: '/h5/user/update' + (userId ? `?userId=${userId}` : ''),
    method: 'PUT',
    data: data
  })
}

/**
 * 学历字典映射
 */
export const educationMap = {
  '1': '小学',
  '2': '初中',
  '3': '高中',
  '4': '大专',
  '5': '本科',
  '6': '硕士',
  '7': '博士'
}

/**
 * 性别字典映射（与后�� sys_user_sex 字典保持一致）
 */
export const genderMap = {
  '0': '男',
  '1': '女',
  '2': '未知'
}

/**
 * 获取学历标签
 */
export function getEducationLabel(value) {
  return educationMap[value] || '未知'
}

/**
 * 获取性别标签
 */
export function getGenderLabel(value) {
  return genderMap[value] || '未知'
}

/**
 * 手机号脱敏显示
 */
export function maskPhone(phone) {
  if (!phone || phone.length !== 11) return phone
  return phone.substring(0, 3) + '****' + phone.substring(7)
}

/**
 * 身份证号脱敏显示
 */
export function maskIdCard(idCard) {
  if (!idCard || idCard.length < 10) return idCard
  return idCard.substring(0, 6) + '********' + idCard.substring(idCard.length - 4)
}

export default {
  getUserInfo,
  uploadAvatar,
  uploadWorkProof,
  updateUser,
  educationMap,
  genderMap,
  getEducationLabel,
  getGenderLabel,
  maskPhone,
  maskIdCard
}
