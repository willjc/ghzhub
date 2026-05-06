/**
 * 资格校验相关 API + 前置守卫工具
 *
 * 后端接口：
 * - GET  /h5/qualification/status   查询最近一次校验结果（是否已校验 / 是否通过 / 各项明细）
 * - POST /h5/qualification/check    同步触发一次完整校验并返回结果
 */
import { get, post } from '@/utils/request'

/**
 * 查询资格校验状态
 * @param {Number} userId 当前登录用户 ID
 * @returns {Promise<{code, data: { checked, passed, items, failReasons, lastCheckTime, qualificationId }}>}
 */
export function getQualificationStatus(userId) {
  return get('/h5/qualification/status', { userId })
}

/**
 * 触发一次资格校验（同步，服务端会并发调政务接口）
 * @param {Number} userId 当前登录用户 ID
 */
export function runQualificationCheck(userId) {
  return post(`/h5/qualification/check?userId=${encodeURIComponent(userId)}`, {})
}

/**
 * 从本地存储读取当前登录用户 ID
 * - 复用现有登录态 key: 'userInfo'
 */
export function getCurrentUserId() {
  const userInfo = uni.getStorageSync('userInfo') || {}
  return userInfo.userId || userInfo.id || null
}

/**
 * 资格前置守卫：
 * 1. 未登录 → 跳登录
 * 2. 已校验通过 → onPass 回调（放行）
 * 3. 已校验未通过 → 跳 fail 页
 * 4. 未校验过 → 跳 check 页（进度页跑完再决定去哪）
 *
 * @param {Function} onPass 通过时的放行回调
 * @param {Object} [options]
 * @param {String} [options.redirectAfterPass] 校验通过后 check 页要跳的目标（check 页用 URLSearchParams 透传）
 * @param {Object} [options.redirectParams]    目标页需要的参数（会拼到 URL）
 */
export function ensureQualified(onPass, options = {}) {
  const userId = getCurrentUserId()
  if (!userId) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    setTimeout(() => {
      uni.navigateTo({ url: '/pages/login/index' })
    }, 800)
    return
  }

  uni.showLoading({ title: '校验中...', mask: true })
  getQualificationStatus(userId)
    .then((res) => {
      uni.hideLoading()
      const data = (res && res.data) || {}
      if (data.checked && data.passed) {
        if (typeof onPass === 'function') onPass()
        return
      }
      if (data.checked && !data.passed) {
        goFailPage(data)
        return
      }
      // 未校验过 → 跳进度页
      goCheckPage(options)
    })
    .catch(() => {
      uni.hideLoading()
      // 读不到状态时直接跳进度页让用户显式触发一次
      goCheckPage(options)
    })
}

function goCheckPage(options = {}) {
  const params = []
  if (options.redirectAfterPass) {
    params.push(`redirect=${encodeURIComponent(options.redirectAfterPass)}`)
  }
  if (options.redirectParams && typeof options.redirectParams === 'object') {
    const payload = encodeURIComponent(JSON.stringify(options.redirectParams))
    params.push(`redirectParams=${payload}`)
  }
  const qs = params.length ? `?${params.join('&')}` : ''
  uni.navigateTo({ url: `/subpkg/qualification/check${qs}` })
}

function goFailPage(data) {
  const reasons = encodeURIComponent(JSON.stringify(data.failReasons || []))
  const items = encodeURIComponent(JSON.stringify(data.items || []))
  uni.navigateTo({
    url: `/subpkg/qualification/fail?reasons=${reasons}&items=${items}`
  })
}

/**
 * 轻量兜底守卫（用于下游页面的 onLoad）
 * - 未登录 / 读不到状态：不做任何拦截（让原页面自己处理登录态）
 * - 已校验且未通过：redirect 到 fail 页
 * - 其余情况：放行
 */
export function guardOrRedirect() {
  const userId = getCurrentUserId()
  if (!userId) return
  getQualificationStatus(userId)
    .then((res) => {
      const data = (res && res.data) || {}
      if (data.checked && !data.passed) {
        const reasons = encodeURIComponent(JSON.stringify(data.failReasons || []))
        const items = encodeURIComponent(JSON.stringify(data.items || []))
        uni.redirectTo({
          url: `/subpkg/qualification/fail?reasons=${reasons}&items=${items}`
        })
      }
    })
    .catch(() => {})
}

export default {
  getQualificationStatus,
  runQualificationCheck,
  getCurrentUserId,
  ensureQualified,
  guardOrRedirect
}
