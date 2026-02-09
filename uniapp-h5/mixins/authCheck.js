/**
 * 全局登录检查混入
 * 用于需要登录的页面，统一处理登录验证
 *
 * 使用方法：
 * 1. 在页面中导入：import authCheck from '@/mixins/authCheck'
 * 2. 在 mixins 选项中添加：mixins: [authCheck]
 * 3. 在 onLoad 中调用：authCheck.onLoad.call(this, options)
 */
export default {
	/**
	 * 检查用户是否已登录
	 * @param {Object} options 页面 onLoad 的 options 参数
	 * @param {Function} callback 登录成功后的回调函数
	 */
	checkLogin(options, callback) {
		// 获取用户信息
		const userInfo = uni.getStorageSync('userInfo')
		const token = uni.getStorageSync('token')

		// 检查是否已登录
		if (!userInfo || !userInfo.userId || !token) {
			uni.showToast({
				title: '请先登录',
				icon: 'none',
				duration: 1500
			})
			setTimeout(() => {
				// 使用 redirectTo 而不是 navigateTo，避免返回循环
				uni.redirectTo({
					url: '/pages/login/index'
				})
			}, 1500)
			return false
		}

		// 登录成功，保存用户ID到组件实例
		this.userId = userInfo.userId
		this.userInfo = userInfo

		// 执行回调
		if (callback && typeof callback === 'function') {
			callback.call(this, options)
		}

		return true
	}
}
