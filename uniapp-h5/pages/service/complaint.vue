<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 投诉卡片列表 -->
			<view class="card" v-for="(item, index) in complaintList" :key="index">
				<!-- 卡片头部 -->
				<view class="card-header">
					<text class="card-title">{{ item.title }}</text>
					<view class="status-wrapper">
						<text
							class="urge-tag"
							v-if="item.isUrged === '1'"
						>已催办</text>
						<text
							class="card-status"
							:class="{ 'status-pending': item.status === '0', 'status-handled': item.status === '1' }"
						>
							{{ item.status === '0' ? '待处理' : '已处理' }}
						</text>
					</view>
				</view>

				<!-- 投诉内容 -->
				<view class="complaint-content">
					<view class="content-row">
						<text class="content-value">{{ item.content }}</text>
					</view>
					<view class="time-row">
						<text class="time-text">{{ formatTime(item.createTime) }}</text>
					</view>
				</view>

				<!-- 按钮区域 -->
				<view class="button-group">
					<view class="btn btn-urge" v-if="item.status === '0'" @click="handleUrge(item)">
						<text class="btn-text-blue">催办</text>
					</view>
					<view class="btn btn-detail" @click="handleViewDetail(item)">
						<text class="btn-text-white">查看详情</text>
					</view>
				</view>
			</view>

			<!-- 空状态 -->
			<view class="empty-state" v-if="complaintList.length === 0 && !loading">
				<text class="empty-text">暂无投诉记录</text>
			</view>

			<!-- 加载中 -->
			<view class="loading-state" v-if="loading">
				<text class="loading-text">加载中...</text>
			</view>
		</scroll-view>

		<!-- 底部发起投诉按钮 -->
		<view class="bottom-btn-container">
			<view class="bottom-btn" @click="handleSubmitComplaint">
				<text class="bottom-btn-text">发起投诉</text>
			</view>
		</view>
	</view>
</template>

<script>
import { getMyComplaintList, urgeComplaint } from '@/api/complaint.js'
import authCheck from '@/mixins/authCheck'

export default {
	data() {
		return {
			complaintList: [],
			loading: false,
			userId: null  // 当前登录用户ID
		}
	},
	onLoad() {
		// 使用统一的登录检查
		authCheck.checkLogin.call(this, {}, () => {
			this.loadComplaintList()
		})
	},
	onShow() {
		// 页面显示时刷新列表
		if (this.userId) {
			this.loadComplaintList()
		}
	},
	methods: {
		// 加载投诉列表
		async loadComplaintList() {
			if (!this.userId) {
				console.error('用户ID不存在')
				return
			}

			try {
				this.loading = true
				const res = await getMyComplaintList(this.userId)
				if (res.code === 200 && res.data) {
					this.complaintList = res.data
				}
			} catch (err) {
				console.error('加载投诉列表失败:', err)
			} finally {
				this.loading = false
			}
		},

		// 催办
		handleUrge(item) {
			uni.showModal({
				title: '确认催办',
				content: '确定要催办此投诉吗？',
				success: (res) => {
					if (res.confirm) {
						this.doUrge(item.complaintId)
					}
				}
			})
		},

		// 执行催办
		async doUrge(id) {
			try {
				uni.showLoading({ title: '处理中...' })
				const res = await urgeComplaint(id, this.userId)
				uni.hideLoading()
				if (res.code === 200) {
					uni.showToast({
						title: '催办成功',
						icon: 'success'
					})
					// 刷新列表
					this.loadComplaintList()
				} else {
					uni.showToast({
						title: res.msg || '催办失败',
						icon: 'none'
					})
				}
			} catch (err) {
				uni.hideLoading()
				uni.showToast({
					title: '催办失败',
					icon: 'none'
				})
			}
		},

		// 查看详情
		handleViewDetail(item) {
			uni.navigateTo({
				url: '/pages/service/complaint-detail?id=' + item.complaintId
			})
		},

		// 发起投诉
		handleSubmitComplaint() {
			uni.navigateTo({
				url: '/pages/service/complaint-submit'
			})
		},

		// 格式化时间
		formatTime(timeStr) {
			if (!timeStr) return ''
			const date = new Date(timeStr)
			const year = date.getFullYear()
			const month = String(date.getMonth() + 1).padStart(2, '0')
			const day = String(date.getDate()).padStart(2, '0')
			const hour = String(date.getHours()).padStart(2, '0')
			const minute = String(date.getMinutes()).padStart(2, '0')
			return `${year}-${month}-${day} ${hour}:${minute}`
		}
	}
}
</script>

<style scoped>
	.page {
		width: 100%;
		min-height: 95vh;
		background-color: #f5f6fc;
		display: flex;
		flex-direction: column;
	}

	.scroll-content {
		flex: 1;
		padding: 24rpx;
		padding-bottom: 140rpx;
		box-sizing: border-box;
	}

	.card {
		width: 702rpx;
		border-radius: 20rpx;
		background: #ffffff;
		padding: 28rpx;
		margin: 0 auto 24rpx;
		box-sizing: border-box;
	}

	.card-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 16rpx;
	}

	.card-title {
		flex: 1;
		color: #333333;
		font-size: 28rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 40rpx;
	}

	.status-wrapper {
		display: flex;
		align-items: center;
		gap: 12rpx;
	}

	.urge-tag {
		height: 36rpx;
		padding: 0 12rpx;
		background: #ffe6e6;
		color: #ff4d4f;
		font-size: 22rpx;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 36rpx;
		border-radius: 6rpx;
	}

	.card-status {
		height: 40rpx;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 40rpx;
	}

	.status-pending {
		color: #ff8d1a;
	}

	.status-handled {
		color: #1281ff;
	}

	.complaint-content {
		border-bottom: 1rpx solid #f0f0f0;
		padding-bottom: 24rpx;
	}

	.content-row {
		margin-bottom: 12rpx;
	}

	.content-value {
		color: #888888;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 40rpx;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 2;
		overflow: hidden;
	}

	.time-row {
		margin-top: 8rpx;
	}

	.time-text {
		color: #999999;
		font-size: 24rpx;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.button-group {
		display: flex;
		justify-content: flex-end;
		gap: 16rpx;
		margin-top: 24rpx;
	}

	.btn {
		width: 148rpx;
		height: 68rpx;
		border-radius: 12rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.btn-urge {
		border: 1rpx solid #0063ff;
		background: #ffffff;
	}

	.btn-detail {
		border: 1rpx solid #1281ff;
		background: #1281ff;
	}

	.btn-text-blue {
		color: #0063ff;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.btn-text-white {
		color: #ffffff;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.empty-state {
		display: flex;
		justify-content: center;
		align-items: center;
		padding: 100rpx 0;
	}

	.empty-text {
		color: #999999;
		font-size: 28rpx;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.loading-state {
		display: flex;
		justify-content: center;
		align-items: center;
		padding: 50rpx 0;
	}

	.loading-text {
		color: #999999;
		font-size: 28rpx;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.bottom-btn-container {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		padding: 24rpx;
		background: #f5f6fc;
	}

	.bottom-btn {
		width: 702rpx;
		height: 92rpx;
		border-radius: 20rpx;
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
		display: flex;
		align-items: center;
		justify-content: center;
		margin: 0 auto;
	}

	.bottom-btn-text {
		color: #ffffff;
		font-size: 36rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}
</style>
