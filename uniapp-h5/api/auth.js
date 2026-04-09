import request from '@/utils/request'

/**
 * 用户登录
 * @param {Object} data - 登录数据
 * @param {String} data.loginType - 登录类型：wechat/zhenghaoban
 * @param {String} data.phone - 手机号
 * @param {String} data.openId - 微信OpenID 或 郑好办ID
 * @param {String} data.nickname - 昵称（可选）
 */
export function login(data) {
	return request.post('/app/auth/login', data)
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
	return request.get('/app/auth/userInfo')
}

/**
 * 更新用户信息
 * @param {Object} data - 用户信息
 */
export function updateUserInfo(data) {
	return request.put('/app/auth/updateInfo', data)
}

/**
 * 退出登录
 */
export function logout() {
	return request.post('/app/auth/logout')
}

/**
 * 郑好办授权登录
 * @param {Object} data - 登录数据
 * @param {String} data.authCode - 郑好办授权码
 */
export function zhbLogin(data) {
	return request.post('/app/auth/zhbLogin', data)
}

/**
 * 微信小程序登录
 * @param {Object} data - 登录数据
 * @param {String} data.code - wx.login()返回的code
 * @param {String} data.phoneCode - getPhoneNumber返回的code
 */
export function wxLogin(data) {
	return request.post('/app/auth/wxLogin', data)
}
