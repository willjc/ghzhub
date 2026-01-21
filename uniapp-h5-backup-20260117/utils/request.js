/**
 * 统一HTTP请求工具
 * 基于uni.request封装
 */
import config from '@/config/index'

// 后端API基础地址
// 从配置文件读取
const BASE_URL = config.baseUrl

/**
 * 发起HTTP请求
 * @param {Object} options 请求配置
 * @param {String} options.url 请求路径（相对路径）
 * @param {String} options.method 请求方法（GET/POST/PUT/DELETE）
 * @param {Object} options.data 请求参数
 * @param {Object} options.header 请求头
 * @returns {Promise}
 */
export function request(options) {
  return new Promise((resolve, reject) => {
    // 从本地存储获取token（如果已登录）
    const token = uni.getStorageSync('token') || ''

    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
        ...options.header
      },
      success: (res) => {
        // 请求成功
        if (res.statusCode === 200) {
          // 若依框架标准响应格式：{ code: 200, msg: 'success', data: ... }
          if (res.data.code === 200 || res.data.code === undefined) {
            resolve(res.data)
          } else {
            // 业务错误
            uni.showToast({
              title: res.data.msg || '请求失败',
              icon: 'none',
              duration: 2000
            })
            reject(res.data)
          }
        } else {
          // HTTP错误
          uni.showToast({
            title: `请求错误: ${res.statusCode}`,
            icon: 'none',
            duration: 2000
          })
          reject(res)
        }
      },
      fail: (err) => {
        // 网络错误
        console.error('网络请求失败:', err)
        uni.showToast({
          title: '网络连接失败',
          icon: 'none',
          duration: 2000
        })
        reject(err)
      }
    })
  })
}

/**
 * GET请求
 */
export function get(url, params = {}) {
  // GET请求参数需要拼接到URL上
  const queryString = Object.keys(params)
    .filter(key => params[key] !== undefined && params[key] !== null && params[key] !== '')
    .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
    .join('&')

  const fullUrl = queryString ? `${url}?${queryString}` : url

  return request({
    url: fullUrl,
    method: 'GET'
  })
}

/**
 * POST请求
 */
export function post(url, data = {}) {
  return request({
    url,
    method: 'POST',
    data
  })
}

/**
 * PUT请求
 */
export function put(url, data = {}) {
  return request({
    url,
    method: 'PUT',
    data
  })
}

/**
 * DELETE请求
 */
export function del(url, data = {}) {
  return request({
    url,
    method: 'DELETE',
    data
  })
}

export default {
  request,
  get,
  post,
  put,
  del,
  BASE_URL
}
