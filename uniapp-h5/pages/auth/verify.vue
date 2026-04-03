<template>
	<view class="page">
		<!-- 已认证状态 -->
		<view class="verify-card" v-if="authenticated">
			<view class="success-icon">
				<text class="icon-text">&#10003;</text>
			</view>
			<text class="success-title">实名认证已完成</text>
			<text class="success-desc">您已完成实名认证，可以正常使用平台功能</text>
			<view class="action-btn btn-primary" @click="goBack">
				<text class="btn-text-white">返回</text>
			</view>
		</view>

		<!-- 认证表单 -->
		<view class="verify-card" v-else>
			<text class="card-title">实名认证</text>
			<text class="card-desc">为保障您的权益，使用平台服务前需完成实名认证</text>

			<view class="form-group">
				<text class="form-label">真实姓名</text>
				<input class="form-input" v-model="form.realName" placeholder="请输入真实姓名" />
			</view>

			<view class="form-group">
				<text class="form-label">身份证号</text>
				<input class="form-input" v-model="form.idCard" placeholder="请输入18位身份证号" maxlength="18" />
			</view>

			<view class="tips-card">
				<text class="tips-text">&#x26A0; 您的信息仅用于实名认证，我们将严格保密</text>
			</view>

			<view class="action-btn btn-primary" :class="{ 'btn-disabled': submitting }" @click="handleSubmit">
				<text class="btn-text-white">{{ submitting ? '提交中...' : '开始认证' }}</text>
			</view>

			<view class="skip-link" @click="goBack">
				<text class="skip-text">稍后认证</text>
			</view>
		</view>

		<!-- 回调处理中 -->
		<view class="verify-card" v-if="authDone && !authenticated">
			<view class="loading-icon">
				<text class="loading-text">...</text>
			</view>
			<text class="success-title">正在查询认证结果</text>
			<text class="success-desc">请稍候</text>
		</view>
	</view>
</template>

<script>
	import { getAuthUrl, getAuthStatus } from '@/api/esign.js'

	export default {
		data() {
			return {
				userId: null,
				authenticated: false,
				submitting: false,
				authDone: false,
				form: {
					realName: '',
					idCard: ''
				}
			}
		},
		onLoad(options) {
			const userInfo = uni.getStorageSync('userInfo')
			if (!userInfo || !userInfo.userId) {
				uni.redirectTo({ url: '/pages/login/index' })
				return
			}
			this.userId = userInfo.userId

			// 预填充已有的姓名和身份证
			if (userInfo.realName) this.form.realName = userInfo.realName
			if (userInfo.idCard) this.form.idCard = userInfo.idCard

			// 如果从e签宝认证页面跳回来（auth_done=1），查询认证结果
			if (options.auth_done === '1') {
				this.authDone = true
				this.checkAuthResult()
				return
			}

			// 检查是否已认证
			this.checkAuthStatus()
		},
		methods: {
			async checkAuthStatus() {
				try {
					const res = await getAuthStatus(this.userId)
					if (res.code === 200 && res.data && res.data.authenticated) {
						this.authenticated = true
						this.updateLocalUserInfo(res.data)
					}
				} catch (e) {
					console.error('查询认证状态失败', e)
				}
			},

			async checkAuthResult() {
				try {
					const res = await getAuthStatus(this.userId)
					if (res.code === 200 && res.data && res.data.authenticated) {
						this.authenticated = true
						this.updateLocalUserInfo(res.data)
						uni.showToast({ title: '认证成功', icon: 'success' })
					} else {
						this.authDone = false
						uni.showToast({ title: '认证未完成，请重新认证', icon: 'none' })
					}
				} catch (e) {
					this.authDone = false
					uni.showToast({ title: '查询认证结果失败', icon: 'none' })
				}
			},

			async handleSubmit() {
				if (this.submitting) return

				if (!this.form.realName.trim()) {
					uni.showToast({ title: '请输入真实姓名', icon: 'none' })
					return
				}
				if (!this.form.idCard.trim() || this.form.idCard.trim().length < 15) {
					uni.showToast({ title: '请输入正确的身份证号', icon: 'none' })
					return
				}

				this.submitting = true
				try {
					const res = await getAuthUrl({
						userId: this.userId,
						realName: this.form.realName.trim(),
						idCard: this.form.idCard.trim()
					})

					if (res.code === 200 && res.data) {
						if (!res.data.needAuth) {
							this.authenticated = true
							this.updateLocalUserInfo(res.data)
							uni.showToast({ title: '已完成认证', icon: 'success' })
						} else if (res.data.authUrl) {
							window.location.href = res.data.authUrl
						}
					} else {
						uni.showToast({ title: res.msg || '获取认证链接失败', icon: 'none' })
					}
				} catch (e) {
					uni.showToast({ title: e.message || '操作失败', icon: 'none' })
				} finally {
					this.submitting = false
				}
			},

			updateLocalUserInfo(data) {
				try {
					const userInfo = uni.getStorageSync('userInfo') || {}
					if (data.psnId) userInfo.esignPsnId = data.psnId
					if (data.realName) userInfo.realName = data.realName
					userInfo.isVerified = true
					userInfo.authStatus = '2'
					uni.setStorageSync('userInfo', userInfo)
				} catch (e) {
					console.error('更新本地用户信息失败', e)
				}
			},

			goBack() {
				const pages = getCurrentPages()
				if (pages.length > 1) {
					uni.navigateBack()
				} else {
					uni.reLaunch({ url: '/pages/index/index' })
				}
			}
		}
	}
</script>

<style scoped>
	.page {
		width: 100%;
		min-height: 100vh;
		background: #f5f6fc;
		padding: 24rpx;
		box-sizing: border-box;
	}

	.verify-card {
		background: #fff;
		border-radius: 20rpx;
		padding: 48rpx 32rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.card-title {
		font-size: 40rpx;
		font-weight: 700;
		color: #1a1a1a;
		margin-bottom: 16rpx;
	}

	.card-desc {
		font-size: 26rpx;
		color: #999;
		margin-bottom: 40rpx;
		text-align: center;
		line-height: 1.6;
	}

	.success-icon {
		width: 120rpx;
		height: 120rpx;
		border-radius: 50%;
		background: #12a566;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 24rpx;
	}

	.icon-text {
		color: #fff;
		font-size: 56rpx;
	}

	.success-title {
		font-size: 36rpx;
		font-weight: 600;
		color: #1a1a1a;
		margin-bottom: 12rpx;
	}

	.success-desc {
		font-size: 26rpx;
		color: #999;
		margin-bottom: 48rpx;
		text-align: center;
	}

	.loading-icon {
		width: 120rpx;
		height: 120rpx;
		border-radius: 50%;
		background: #4A90E2;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 24rpx;
	}

	.loading-text {
		color: #fff;
		font-size: 56rpx;
		animation: blink 1s infinite;
	}

	@keyframes blink {
		0%, 100% { opacity: 1; }
		50% { opacity: 0.3; }
	}

	.form-group {
		width: 100%;
		margin-bottom: 28rpx;
	}

	.form-label {
		font-size: 28rpx;
		color: #333;
		font-weight: 500;
		margin-bottom: 12rpx;
		display: block;
	}

	.form-input {
		width: 100%;
		height: 88rpx;
		border: 1rpx solid #e0e0e0;
		border-radius: 12rpx;
		padding: 0 24rpx;
		font-size: 28rpx;
		color: #333;
		box-sizing: border-box;
	}

	.form-input:focus {
		border-color: #4A90E2;
	}

	.tips-card {
		width: 100%;
		background: #f8f9ff;
		border-radius: 12rpx;
		padding: 20rpx 24rpx;
		margin-bottom: 40rpx;
	}

	.tips-text {
		font-size: 24rpx;
		color: #999;
		line-height: 1.6;
	}

	.action-btn {
		width: 100%;
		height: 88rpx;
		border-radius: 44rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.btn-primary {
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
	}

	.btn-disabled {
		opacity: 0.6;
	}

	.btn-text-white {
		color: #fff;
		font-size: 30rpx;
		font-weight: 500;
	}

	.skip-link {
		width: 100%;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-top: 24rpx;
	}

	.skip-text {
		font-size: 26rpx;
		color: #999999;
	}
</style>
