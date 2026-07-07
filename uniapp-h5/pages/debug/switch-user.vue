<template>
	<view class="page">
		<view class="header">
			<text class="title">调试：切换用户身份</text>
			<text class="desc">输入目标用户手机号，切换后将以该用户身份操作小程序</text>
		</view>

		<!-- 切回测试账号快捷按钮 -->
		<view class="back-btn" @click="switchBack" v-if="currentPhone !== '18539279011'">
			<text class="back-btn-text">切换回测试账号 (18539279011)</text>
		</view>

		<view class="form">
			<view class="input-wrapper">
				<input
					class="phone-input"
					v-model="targetPhone"
					type="number"
					maxlength="11"
					placeholder="请输入目标用户手机号"
					placeholder-class="placeholder"
				/>
			</view>

			<view class="btn" :class="{ disabled: loading }" @click="handleSwitch">
				<text class="btn-text">{{ loading ? '切换中...' : '切换身份' }}</text>
			</view>
		</view>

		<!-- 当前身份信息 -->
		<view class="current-info">
			<text class="info-label">当前身份：</text>
			<text class="info-value">{{ currentPhone || '未知' }}（userId: {{ currentUserId || '无' }}）</text>
		</view>

		<!-- 切换历史 -->
		<view class="history" v-if="historyList.length > 0">
			<text class="history-title">最近切换记录（点击快速切换）</text>
			<view
				class="history-item"
				v-for="(item, index) in historyList"
				:key="index"
				@click="quickSwitch(item)"
			>
				<text class="history-phone">{{ item.phone }}</text>
				<text class="history-name" v-if="item.name">{{ item.name }}</text>
			</view>
		</view>
	</view>
</template>

<script>
	import { post } from '@/utils/request'

	export default {
		data() {
			return {
				targetPhone: '',
				loading: false,
				currentPhone: '',
				currentUserId: '',
				historyList: []
			}
		},
		onLoad() {
			// 读取当前登录信息
			const userInfo = uni.getStorageSync('userInfo')
			if (userInfo) {
				this.currentPhone = userInfo.phone || ''
				this.currentUserId = userInfo.userId || ''
			}
			// 读取切换历史
			this.historyList = uni.getStorageSync('debugSwitchHistory') || []
		},
		methods: {
			async handleSwitch() {
				const phone = this.targetPhone.trim()
				if (!phone || phone.length !== 11) {
					uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
					return
				}

				this.loading = true
				try {
					const res = await post('/app/auth/debugSwitch', { phone })

					if (res.code === 200 && res.data) {
						// 覆盖写入 storage
						uni.setStorageSync('token', res.data.token)
						uni.setStorageSync('userId', res.data.userInfo.userId)
						uni.setStorageSync('userInfo', res.data.userInfo)

						// 保存切换历史
						this.addToHistory(phone, res.data.userInfo.nickname || res.data.userInfo.realName || '')

						uni.showToast({ title: '已切换为 ' + phone, icon: 'success' })

						setTimeout(() => {
							uni.reLaunch({ url: '/pages/index/index' })
						}, 1000)
					}
				} catch (err) {
					console.error('切换失败:', err)
					uni.showToast({
						title: err.msg || err.message || '切换失败',
						icon: 'none'
					})
				} finally {
					this.loading = false
				}
			},

			quickSwitch(item) {
				this.targetPhone = item.phone
				this.handleSwitch()
			},

			// 快捷切回测试账号
			switchBack() {
				this.targetPhone = '18539279011'
				this.handleSwitch()
			},

			addToHistory(phone, name) {
				// 去重，最新的放最前
				this.historyList = this.historyList.filter(h => h.phone !== phone)
				this.historyList.unshift({ phone, name })
				// 只保留最近 10 条
				if (this.historyList.length > 10) {
					this.historyList = this.historyList.slice(0, 10)
				}
				uni.setStorageSync('debugSwitchHistory', this.historyList)
			}
		}
	}
</script>

<style scoped>
	.page {
		min-height: 100vh;
		background: #f5f6fc;
		padding: 40rpx;
	}

	.header {
		margin-bottom: 60rpx;
	}

	.title {
		font-size: 40rpx;
		font-weight: bold;
		color: #333333;
		display: block;
		margin-bottom: 16rpx;
	}

	.desc {
		font-size: 26rpx;
		color: #999999;
	}

	.back-btn {
		background: linear-gradient(135deg, #ff9500 0%, #ff7800 100%);
		border-radius: 48rpx;
		padding: 28rpx 0;
		text-align: center;
		margin-bottom: 40rpx;
		box-shadow: 0 8rpx 20rpx rgba(255,149,0,0.3);
	}

	.back-btn-text {
		font-size: 32rpx;
		font-weight: 600;
		color: #ffffff;
	}

	.form {
		background: #ffffff;
		border-radius: 20rpx;
		padding: 40rpx;
		margin-bottom: 40rpx;
	}

	.input-wrapper {
		margin-bottom: 30rpx;
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

	.placeholder {
		color: #bbbbbb;
	}

	.btn {
		width: 100%;
		height: 96rpx;
		border-radius: 48rpx;
		background: linear-gradient(135deg, #4A90E2 0%, #357ABD 100%);
		display: flex;
		align-items: center;
		justify-content: center;
		box-shadow: 0 8rpx 20rpx rgba(74,144,226,0.3);
	}

	.btn.disabled {
		opacity: 0.6;
	}

	.btn-text {
		font-size: 32rpx;
		font-weight: 500;
		color: #ffffff;
	}

	.current-info {
		background: #ffffff;
		border-radius: 20rpx;
		padding: 30rpx 40rpx;
		margin-bottom: 40rpx;
		display: flex;
		align-items: center;
		flex-wrap: wrap;
	}

	.info-label {
		font-size: 28rpx;
		color: #999999;
	}

	.info-value {
		font-size: 28rpx;
		color: #333333;
		font-weight: 500;
	}

	.history {
		background: #ffffff;
		border-radius: 20rpx;
		padding: 30rpx 40rpx;
	}

	.history-title {
		font-size: 28rpx;
		color: #999999;
		display: block;
		margin-bottom: 20rpx;
	}

	.history-item {
		display: flex;
		align-items: center;
		padding: 24rpx 0;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.history-item:last-child {
		border-bottom: none;
	}

	.history-phone {
		font-size: 30rpx;
		color: #333333;
		font-weight: 500;
		margin-right: 20rpx;
	}

	.history-name {
		font-size: 26rpx;
		color: #999999;
	}
</style>
