/**
 * 郑好办 AlipayJSBridge 工具类
 * 用于在郑好办APP的WebView中与原生交互
 */

/**
 * 等待AlipayJSBridge注入完成
 * @param {Function} callback 回调函数
 */
export function ready(callback) {
  if (window.AlipayJSBridge) {
    callback && callback()
  } else {
    document.addEventListener('AlipayJSBridgeReady', callback, false)
  }
}

/**
 * 检查是否在郑好办WebView中
 * @returns {Boolean}
 */
export function isInZhbApp() {
  return !!window.AlipayJSBridge
}

/**
 * 调用郑好办用户授权
 * @param {Object} options 配置项
 * @param {String} options.moduleId 应用ID
 * @param {String} options.scope 授权范围 userInfo(基本信息) 或 userDetail(详细信息含身份证)
 * @returns {Promise<String>} 返回authCode
 */
export function userAuth(options = {}) {
  return new Promise((resolve, reject) => {
    ready(() => {
      AlipayJSBridge.call('userAuth', {
        moduleId: options.moduleId || '',
        scope: options.scope || 'userDetail'
      }, (result) => {
        if (result.code === 0) {
          resolve(result.data.authCode)
        } else {
          reject(new Error(result.message || '授权失败'))
        }
      })
    })
  })
}

/**
 * 关闭当前WebView容器
 */
export function popWindow() {
  ready(() => {
    AlipayJSBridge.call('popWindow')
  })
}

/**
 * 拉起实名认证
 * @returns {Promise<Object>}
 */
export function getOnVerified() {
  return new Promise((resolve, reject) => {
    ready(() => {
      AlipayJSBridge.call('getonverified', {}, (result) => {
        if (result.result === 'true') {
          resolve({
            result: true,
            message: result.message,
            authLevel: result.authLevel
          })
        } else {
          reject(new Error(result.message || '认证失败'))
        }
      })
    })
  })
}

export default {
  ready,
  isInZhbApp,
  userAuth,
  popWindow,
  getOnVerified
}
