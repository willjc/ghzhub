<template>
	<view class="login-page">
		<!-- 背景装饰 -->
		<view class="bg-decoration"></view>

		<!-- 返回按钮 -->
		<view class="back-btn" @click="goBack">
			<text class="back-icon">←</text>
			<text class="back-text">返回</text>
		</view>

		<!-- Logo区域 -->
		<view class="logo-section">
			<image class="logo" src="/static/logo.png" mode="aspectFit"></image>
			<text class="app-name">港好住</text>
			<text class="app-desc">郑州航空港区人才公寓服务平台</text>
		</view>

		<!-- 登录卡片 -->
		<view class="login-card">
			<text class="card-title">欢迎登录</text>

			<!-- #ifdef MP-WEIXIN -->
			<!-- 微信小程序：授权手机号登录 -->
			<button class="login-btn wechat-btn" open-type="getPhoneNumber" @getphonenumber="onGetPhoneNumber" :disabled="loading || !agreed">
				<text class="btn-text">{{ loading ? '登录中...' : '手机号快捷登录' }}</text>
			</button>
			<!-- #endif -->

			<!-- #ifdef H5 -->
			<!-- H5环境：手机号登录 -->
			<view class="phone-input-wrapper">
				<input
					class="phone-input"
					v-model="phone"
					type="number"
					maxlength="11"
					placeholder="请输入手机号"
					placeholder-class="phone-placeholder"
				/>
			</view>
			<view class="login-btn phone-btn" :class="{ disabled: loading || !agreed }" @click="handlePhoneLogin">
				<text class="btn-text">{{ loading ? '登录中...' : '登录' }}</text>
			</view>
			<!-- #endif -->

			<!-- 隐私协议勾选 -->
			<view class="agreement-row">
				<view class="checkbox" :class="{ checked: agreed }" @click="toggleAgree">
					<text v-if="agreed" class="check-icon">✓</text>
				</view>
				<view class="agreement-text">
					<text>我已阅读并同意</text>
					<text class="link" @click="openAgreement('user')">《用户服务协议》</text>
					<text>和</text>
					<text class="link" @click="openAgreement('privacy')">《隐私政策》</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	// #ifdef MP-WEIXIN
	import { wxLogin } from '@/api/auth'
	// #endif

	// #ifdef H5
	import { login } from '@/api/auth'
	// #endif

	export default {
		data() {
			return {
					loading: false,
				agreed: false,
				// #ifdef H5
				phone: ''
				// #endif
			}
		},
		methods: {
			// 返回上一页
			goBack() {
				uni.navigateBack({ delta: 1 })
			},
			// 勾选/取消勾选协议
			toggleAgree() {
				this.agreed = !this.agreed
			},
			// 打开协议页面
			openAgreement(type) {
				const url = type === 'user'
					? '/pages/agreement/user'
					: '/pages/agreement/privacy'
				uni.navigateTo({ url })
			},
			// 登录前检查协议
			checkAgreement() {
				if (!this.agreed) {
					uni.showToast({
						title: '请先阅读并同意协议',
						icon: 'none'
					})
					return false
				}
				return true
			},
			// #ifdef MP-WEIXIN
			/**
			 * 微信小程序：获取手机号回调
			 */
			async onGetPhoneNumber(e) {
				if (this.loading) return

				// 检查是否同意协议
				if (!this.checkAgreement()) return

				// 用户拒绝授权
				if (e.detail.errMsg !== 'getPhoneNumber:ok') {
					uni.showToast({
						title: '需要授权手机号才能登录',
						icon: 'none'
					})
					return
				}

				this.loading = true

				try {
					// 1. 调用wx.login获取code
					const loginRes = await new Promise((resolve, reject) => {
						wx.login({
							success: resolve,
							fail: reject
						})
					})

					// 2. 发送code和phoneCode到后端
					const response = await wxLogin({
						code: loginRes.code,
						phoneCode: e.detail.code
					})

					// 3. 存储登录信息
					uni.setStorageSync('token', response.data.token)
					uni.setStorageSync('userId', response.data.userInfo.userId)
					const userInfoToStore = response.data.userInfo
					// 若 DB 返回 authStatus 为 '0' 但本地有已认证记录，以 DB 为准（DB 才是权威）
					// 若 DB 返回 '2'，直接清除本地独立标记
					if (userInfoToStore.authStatus === '2') {
						uni.removeStorageSync('esign_auth_status')
					}
					uni.setStorageSync('userInfo', userInfoToStore)

					uni.showToast({
						title: '登录成功',
						icon: 'success'
					})

					// 4. 跳转首页（首页根据isInfoCompleted决定是否弹窗）
					setTimeout(() => {
						uni.reLaunch({ url: '/pages/index/index' })
					}, 1000)

				} catch (error) {
					console.error('微信登录失败:', error)
					uni.showToast({
						title: error.message || error.msg || '登录失败',
						icon: 'none'
					})
				} finally {
					this.loading = false
				}
			},
			// #endif

			// #ifdef H5
			/**
			 * H5环境：手机号登录
			 */
			async handlePhoneLogin() {
				if (this.loading) return

				// 检查是否同意协议
				if (!this.checkAgreement()) return

				const phone = this.phone.trim()
				if (!phone || phone.length !== 11) {
					uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
					return
				}

				this.loading = true

				try {
					const response = await login({
						loginType: 'wechat',
						phone: phone,
						openId: 'h5_' + phone,
						nickname: 'H5用户'
					})

					uni.setStorageSync('token', response.data.token)
					uni.setStorageSync('userId', response.data.userInfo.userId)
					uni.setStorageSync('userInfo', response.data.userInfo)

					uni.showToast({ title: '登录成功', icon: 'success' })

					setTimeout(() => {
						uni.reLaunch({ url: '/pages/index/index' })
					}, 1000)

				} catch (error) {
					console.error('H5登录失败:', error)
					uni.showToast({
						title: error.message || error.msg || '登录失败',
						icon: 'none'
					})
				} finally {
					this.loading = false
				}
			}
			// #endif
		}
	}
</script>

<style scoped>
	.login-page {
		min-height: 100vh;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 40rpx;
		position: relative;
		overflow: hidden;
	}

	.back-btn {
		position: absolute;
		top: 80rpx;
		left: 30rpx;
		z-index: 10;
		display: flex;
		align-items: center;
		padding: 16rpx 24rpx;
		background: rgba(255, 255, 255, 0.2);
		border-radius: 40rpx;
		backdrop-filter: blur(10rpx);
	}

	.back-icon {
		font-size: 32rpx;
		color: #ffffff;
		margin-right: 8rpx;
	}

	.back-text {
		font-size: 28rpx;
		color: #ffffff;
	}

	.bg-decoration {
		position: absolute;
		width: 200%;
		height: 200%;
		background: radial-gradient(circle, rgba(255,255,255,0.1) 1px, transparent 1px);
		background-size: 50rpx 50rpx;
		animation: float 20s linear infinite;
	}

	@keyframes float {
		from { transform: translate(0, 0); }
		to { transform: translate(-50rpx, -50rpx); }
	}

	.logo-section {
		display: flex;
		flex-direction: column;
		align-items: center;
		margin-bottom: 80rpx;
		position: relative;
		z-index: 1;
	}

	.logo {
		width: 160rpx;
		height: 160rpx;
		margin-bottom: 30rpx;
		background: #ffffff;
		border-radius: 80rpx;
		padding: 20rpx;
	}

	.app-name {
		font-size: 48rpx;
		font-weight: bold;
		color: #ffffff;
		margin-bottom: 16rpx;
		text-shadow: 0 2rpx 10rpx rgba(0,0,0,0.2);
	}

	.app-desc {
		font-size: 26rpx;
		color: rgba(255,255,255,0.9);
	}

	.login-card {
		width: 100%;
		max-width: 600rpx;
		background: #ffffff;
		border-radius: 32rpx;
		padding: 60rpx 40rpx;
		box-shadow: 0 20rpx 60rpx rgba(0,0,0,0.2);
		position: relative;
		z-index: 1;
	}

	.card-title {
		font-size: 36rpx;
		font-weight: 500;
		color: #333333;
		text-align: center;
		display: block;
		margin-bottom: 60rpx;
	}

	.login-btn {
		width: 100%;
		height: 96rpx;
		border-radius: 48rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 30rpx;
		transition: all 0.3s;
		border: none;
		padding: 0;
		line-height: 96rpx;
	}

	.login-btn::after {
		border: none;
	}

	.login-btn:active {
		transform: scale(0.98);
		opacity: 0.8;
	}

	.wechat-btn {
		background: linear-gradient(135deg, #09bb07 0%, #06a906 100%);
		box-shadow: 0 8rpx 20rpx rgba(9,187,7,0.3);
	}

	.btn-text {
		font-size: 32rpx;
		font-weight: 500;
		color: #ffffff;
	}

	.phone-input-wrapper {
		width: 100%;
		margin-bottom: 24rpx;
	}

	.phone-input {
		width: 100%;
		height: 96rpx;
		background: #f5f6fc;
		border-radius: 48rpx;
		padding: 0 40rpx;
		font-size: 30rpx;
		color: #333333;
		box-sizing: border-box;
	}

	.phone-placeholder {
		color: #bbbbbb;
	}

	.phone-btn {
		background: linear-gradient(135deg, #4A90E2 0%, #357ABD 100%);
		box-shadow: 0 8rpx 20rpx rgba(74,144,226,0.3);
	}

	.phone-btn.disabled {
		opacity: 0.6;
	}

	.agreement-row {
		display: flex;
		align-items: flex-start;
		margin-top: 30rpx;
		padding: 0 10rpx;
	}

	.checkbox {
		width: 32rpx;
		height: 32rpx;
		border: 2rpx solid #cccccc;
		border-radius: 6rpx;
		margin-right: 12rpx;
		margin-top: 4rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		flex-shrink: 0;
		background: #ffffff;
	}

	.checkbox.checked {
		background: #4A90E2;
		border-color: #4A90E2;
	}

	.check-icon {
		font-size: 22rpx;
		color: #ffffff;
		font-weight: bold;
	}

	.agreement-text {
		font-size: 24rpx;
		color: #666666;
		line-height: 40rpx;
		flex: 1;
	}

	.link {
		color: #4A90E2;
	}
</style>
