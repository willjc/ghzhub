<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 报修信息卡片 -->
			<view class="card">
				<view class="card-header">
					<view class="card-indicator"></view>
					<text class="card-title">报修信息</text>
					<view class="status-info">
						<text
							class="status-tag"
							:class="{
								'status-pending': detailData.status === '0',
								'status-completed': detailData.status === '1',
								'status-cancelled': detailData.status === '2'
							}"
						>
							{{ statusText }}
						</text>
					</view>
				</view>

				<view class="form-row">
					<text class="form-label">报修编号</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ detailData.repairNo }}</text>
					</view>
				</view>

				<view class="form-row">
					<text class="form-label">所在位置</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ detailData.location }}</text>
					</view>
				</view>

				<view class="form-row">
					<text class="form-label">房间号</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ detailData.roomNo }}</text>
					</view>
				</view>

				<view class="form-row">
					<text class="form-label">联系电话</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ detailData.phone }}</text>
					</view>
				</view>

				<view class="form-row">
					<text class="form-label">问题描述</text>
					<view class="form-value-wrap-full">
						<text class="form-value-desc">{{ detailData.description }}</text>
					</view>
				</view>

				<!-- 照片 -->
				<view class="form-row" style="margin-bottom: 59rpx;" v-if="imageList.length > 0">
					<text class="form-label">照片</text>
					<view class="image-list">
						<image
							class="detail-image"
							v-for="(img, index) in imageList"
							:key="index"
							:src="getImageUrl(img)"
							mode="aspectFill"
							@click="previewImage(index)"
						></image>
					</view>
				</view>

				<view class="form-row">
					<text class="form-label">报修时间</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ detailData.repairDate }}</text>
					</view>
				</view>

				<view class="form-row">
					<text class="form-label">提交时间</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ formatTime(detailData.createTime) }}</text>
					</view>
				</view>

				<!-- 处理结果 -->
				<view class="form-row" style="margin-bottom: 20rpx;" v-if="detailData.status === '1' && detailData.handleResult">
					<text class="form-label">处理结果</text>
					<view class="form-value-wrap-full">
						<text class="form-value-desc">{{ detailData.handleResult }}</text>
					</view>
				</view>

				<view class="form-row" v-if="detailData.status === '1' && detailData.handleTime">
					<text class="form-label">处理时间</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ formatTime(detailData.handleTime) }}</text>
					</view>
				</view>
			</view>
		</scroll-view>

		<!-- 底部取消报修按钮 -->
		<view class="bottom-btn-container" v-if="detailData.status === '0' && !loading">
			<view class="bottom-btn cancel-btn" @click="handleCancelRepair">
				<text class="bottom-btn-text">取消报修</text>
			</view>
		</view>

		<!-- 加载中 -->
		<view class="loading-wrapper" v-if="loading">
			<text class="loading-text">加载中...</text>
		</view>
	</view>
</template>

<script>
import { getRepairDetail, cancelRepair } from '@/api/repair.js'
import { BASE_URL } from '@/utils/request'

export default {
	data() {
		return {
			repairId: '',
			detailData: {
				repairId: '',
				repairNo: '',
				location: '',
				roomNo: '',
				phone: '',
				description: '',
				images: '',
				repairDate: '',
				status: '0',
				handleResult: '',
				handleTime: '',
				createTime: ''
			},
			loading: false,
			userId: null  // 当前登录用户ID
		}
	},
	computed: {
		// 图片列表
		imageList() {
			if (!this.detailData.images) return []
			return this.detailData.images.split(',').filter(img => img)
		},
		// 状态文本
		statusText() {
			const statusMap = {
				'0': '待处理',
				'1': '已完成',
				'2': '已取消'
			}
			return statusMap[this.detailData.status] || '未知'
		}
	},
	onLoad(options) {
		// 获取用户信息
		const userInfo = uni.getStorageSync('userInfo')
		if (!userInfo || !userInfo.userId) {
			uni.showToast({
				title: '请先登录',
				icon: 'none'
			})
			setTimeout(() => {
				uni.navigateTo({
					url: '/pages/login/index'
				})
			}, 1500)
			return
		}

		this.userId = userInfo.userId

		if (options.id) {
			this.repairId = options.id
			this.loadDetailData()
		}
	},
	methods: {
		// 获取图片完整URL
		getImageUrl(url) {
			if (!url) return ''
			if (url.startsWith('http://') || url.startsWith('https://')) {
				return url
			}
			if (url.startsWith('/static/')) {
				return url
			}
			return BASE_URL + url
		},

		// 加载详情数据
		async loadDetailData() {
			if (!this.userId) {
				console.error('用户ID不存在')
				return
			}

			try {
				this.loading = true
				const res = await getRepairDetail(this.repairId, this.userId)
				if (res.code === 200 && res.data) {
					this.detailData = res.data
				} else {
					uni.showToast({
						title: res.msg || '加载失败',
						icon: 'none'
					})
				}
			} catch (err) {
				console.error('加载报修详情失败:', err)
				uni.showToast({
					title: '加载失败',
					icon: 'none'
				})
			} finally {
				this.loading = false
			}
		},

		// 预览图片
		previewImage(index) {
			const urls = this.imageList.map(img => this.getImageUrl(img))
			uni.previewImage({
				current: index,
				urls: urls
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
		},

		// 取消报修
		handleCancelRepair() {
			uni.showModal({
				title: '确认取消',
				content: '确定要取消此报修吗？取消后无法恢复',
				confirmColor: '#ff4d4f',
				success: (res) => {
					if (res.confirm) {
						this.doCancel()
					}
				}
			})
		},

		// 执行取消
		async doCancel() {
			try {
				uni.showLoading({ title: '处理中...' })
				const res = await cancelRepair(this.repairId, this.userId)
				uni.hideLoading()
				if (res.code === 200) {
					uni.showToast({
						title: '取消成功',
						icon: 'success'
					})
					// 更新本地状态
					this.detailData.status = '2'
					setTimeout(() => {
						uni.navigateBack()
					}, 1500)
				} else {
					uni.showToast({
						title: res.msg || '取消失败',
						icon: 'none'
					})
				}
			} catch (err) {
				uni.hideLoading()
				uni.showToast({
					title: '取消失败',
					icon: 'none'
				})
			}
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
		padding-bottom: 160rpx;
		box-sizing: border-box;
	}

	.loading-wrapper {
		display: flex;
		justify-content: center;
		align-items: center;
		height: 400rpx;
	}

	.loading-text {
		color: #999999;
		font-size: 28rpx;
	}

	.card {
		width: 702rpx;
		border-radius: 20rpx;
		background: #ffffff;
		padding: 26rpx 0 10rpx 0;
		margin: 0 auto 24rpx;
		box-sizing: border-box;
	}

	.card-header {
		display: flex;
		align-items: center;
		margin-bottom: 32rpx;
		padding: 0 40rpx;
	}

	.card-indicator {
		width: 12rpx;
		height: 34rpx;
		background: #0f73ff;
		margin-right: 26rpx;
		flex-shrink: 0;
	}

	.card-title {
		color: #171a1f;
		font-size: 32rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		flex: 1;
	}

	.status-info {
		display: flex;
		align-items: center;
	}

	.status-tag {
		padding: 6rpx 16rpx;
		border-radius: 8rpx;
		font-size: 22rpx;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.status-pending {
		background: #fff7e6;
		color: #ff8d1a;
	}

	.status-completed {
		background: #e6f7ff;
		color: #1281ff;
	}

	.status-cancelled {
		background: #f5f5f5;
		color: #999999;
	}

	.form-row {
		display: flex;
		align-items: flex-start;
		margin: 0 40rpx;
		margin-bottom: 28rpx;
	}

	.form-label {
		width: 166rpx;
		color: #333333;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		flex-shrink: 0;
		line-height: 40rpx;
	}

	.form-value-wrap {
		flex: 1;
		display: flex;
		align-items: flex-start;
		line-height: 40rpx;
	}

	.form-value-wrap-full {
		width: 100%;
	}

	.form-value {
		color: #1a1a1a;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 40rpx;
	}

	.form-value-desc {
		color: #666666;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 40rpx;
	}

	/* 图片列表 */
	.image-list {
		display: flex;
		flex-wrap: wrap;
		gap: 16rpx;
	}

	.detail-image {
		width: 100rpx;
		height: 100rpx;
		border-radius: 4rpx;
	}

	/* 底部按钮 */
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

	.cancel-btn {
		background: #ffffff;
		border: 1rpx solid #ff4d4f;
	}

	.bottom-btn-text {
		color: #ffffff;
		font-size: 36rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.cancel-btn .bottom-btn-text {
		color: #ff4d4f;
	}
</style>
