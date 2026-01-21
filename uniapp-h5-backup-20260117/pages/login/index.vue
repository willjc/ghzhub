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

			<!-- 微信登录按钮 -->
			<view class="login-btn wechat-btn" @click="handleWechatLogin">
				<image class="btn-icon" src="/static/wechat-icon.png" mode="aspectFit"></image>
				<text class="btn-text">微信登录</text>
			</view>

			<!-- 郑好办登录按钮 -->
			<view class="login-btn zhenghaoban-btn" @click="handleZhenghabanLogin">
				<image class="btn-icon" src="/static/zhenghaoban-icon.png" mode="aspectFit"></image>
				<text class="btn-text">郑好办登录</text>
			</view>

			<text class="tips">登录即表示同意《用户协议》和《隐私政策》</text>
		</view>
	</view>
</template>

<script>
	import { login } from '@/api/auth'

	export default {
		data() {
			return {
				loading: false
			}
		},
		methods: {
			/**
			 * 微信登录
			 */
			async handleWechatLogin() {
				if (this.loading) return
				this.loading = true

				try {
					// 开发环境：使用测试账号1
					const testData = {
						loginType: 'wechat',
						phone: '13800138001',
						openId: 'wx_test_001',
						nickname: '微信测试用户'
					}

					// 调用登录接口
					const response = await login(testData)

					// 保存Token和用户信息
					uni.setStorageSync('token', response.data.token)
					uni.setStorageSync('userInfo', response.data.userInfo)

					uni.showToast({
						title: '登录成功',
						icon: 'success'
					})

					// 延迟跳转，让用户看到成功提示
					setTimeout(() => {
						uni.reLaunch({
							url: '/pages/index/index'
						})
					}, 1000)

				} catch (error) {
					console.error('微信登录失败:', error)
					uni.showToast({
						title: error.message || '登录失败',
						icon: 'none'
					})
				} finally {
					this.loading = false
				}
			},

			/**
			 * 郑好办登录
			 */
			async handleZhenghabanLogin() {
				if (this.loading) return
				this.loading = true

				try {
					// 开发环境：使用测试账号2
					const testData = {
						loginType: 'zhenghaoban',
						phone: '13800138002',
						openId: 'zhb_test_001',
						nickname: '郑好办测试用户'
					}

					// 调用登录接口
					const response = await login(testData)

					// 保存Token和用户信息
					uni.setStorageSync('token', response.data.token)
					uni.setStorageSync('userInfo', response.data.userInfo)

					uni.showToast({
						title: '登录成功',
						icon: 'success'
					})

					// 延迟跳转
					setTimeout(() => {
						uni.reLaunch({
							url: '/pages/index/index'
						})
					}, 1000)

				} catch (error) {
					console.error('郑好办登录失败:', error)
					uni.showToast({
						title: error.message || '登录失败',
						icon: 'none'
					})
				} finally {
					this.loading = false
				}
			}
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
	}

	.login-btn:active {
		transform: scale(0.98);
		opacity: 0.8;
	}

	.wechat-btn {
		background: linear-gradient(135deg, #09bb07 0%, #06a906 100%);
		box-shadow: 0 8rpx 20rpx rgba(9,187,7,0.3);
	}

	.zhenghaoban-btn {
		background: linear-gradient(135deg, #4A90E2 0%, #357ABD 100%);
		box-shadow: 0 8rpx 20rpx rgba(74,144,226,0.3);
	}

	.btn-icon {
		width: 40rpx;
		height: 40rpx;
		margin-right: 16rpx;
	}

	.btn-text {
		font-size: 32rpx;
		font-weight: 500;
		color: #ffffff;
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
