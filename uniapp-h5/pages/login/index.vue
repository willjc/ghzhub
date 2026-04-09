<template>
	<view class="login-page">
		<!-- 背景装饰 -->
		<view class="bg-decoration"></view>

		<!-- Logo区域 -->
		<view class="logo-section">
			<image class="logo" src="/static/logo.png" mode="aspectFit"></image>
			<text class="app-name">港好住</text>
			<text class="app-desc">港区保租房·人才公寓服务平台</text>
		</view>

		<!-- 登录卡片 -->
		<view class="login-card">
			<text class="card-title">欢迎登录</text>

			<!-- #ifdef MP-WEIXIN -->
			<!-- 微信小程序：授权手机号登录 -->
			<button class="login-btn wechat-btn" open-type="getPhoneNumber" @getphonenumber="onGetPhoneNumber" :disabled="loading">
				<text class="btn-text">{{ loading ? '登录中...' : '微信授权登录' }}</text>
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
			<view class="login-btn phone-btn" :class="{ disabled: loading }" @click="handlePhoneLogin">
				<text class="btn-text">{{ loading ? '登录中...' : '登录' }}</text>
			</view>
			<!-- #endif -->

			<text class="tips">登录即表示同意《用户协议》和《隐私政策》</text>
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
				// #ifdef H5
				phone: ''
				// #endif
			}
		},
		methods: {
			// #ifdef MP-WEIXIN
			/**
			 * 微信小程序：获取手机号回调
			 */
			async onGetPhoneNumber(e) {
				if (this.loading) return

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
					uni.setStorageSync('userInfo', response.data.userInfo)

					uni.showToast({
						title: '登录成功',
						icon: 'success'
					})

					// 4. 根据isInfoCompleted决定跳转
					setTimeout(() => {
						if (response.data.userInfo.isInfoCompleted === '1') {
							uni.reLaunch({ url: '/pages/index/index' })
						} else {
							uni.redirectTo({ url: '/pages/my/complete-info' })
						}
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
						if (response.data.userInfo.isInfoCompleted === '1') {
							uni.reLaunch({ url: '/pages/index/index' })
						} else {
							uni.redirectTo({ url: '/pages/my/complete-info' })
						}
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

	.tips {
		font-size: 24rpx;
		color: #999999;
		text-align: center;
		display: block;
		margin-top: 40rpx;
		line-height: 36rpx;
	}
</style>
