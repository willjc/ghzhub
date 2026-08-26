import request from '@/utils/request'

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
 * 微信小程序登录
 * @param {Object} data - 登录数据
 * @param {String} data.code - wx.login()返回的code
 * @param {String} data.phoneCode - getPhoneNumber返回的code
 */
export function wxLogin(data) {
	return request.post('/app/auth/wxLogin', data)
}
