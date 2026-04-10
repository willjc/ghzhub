<template>
	<view class="page">
		<view class="scroll-content" v-if="!loading">
			<!-- 投诉及建议 -->
			<view class="form-card">
				<view class="section-title">
					<view class="title-indicator"></view>
					<text class="title-text">投诉及建议</text>
					<text class="title-value">{{ detailData.title }}</text>
				</view>
			</view>

			<!-- 投诉及建议内容 -->
			<view class="form-card">
				<view class="section-title">
					<view class="title-indicator"></view>
					<text class="title-text">投诉及建议内容</text>
				</view>
				<view class="content-wrapper">
					<text class="content-text">{{ detailData.content }}</text>
				</view>
			</view>

			<!-- 联系方式 -->
			<view class="form-card">
				<view class="section-title">
					<view class="title-indicator"></view>
					<text class="title-text">联系方式</text>
					<text class="title-value">{{ detailData.contactPhone }}</text>
				</view>
			</view>

			<!-- 上传照片 -->
			<view class="form-card" v-if="imageList.length > 0">
				<view class="section-title">
					<view class="title-indicator"></view>
					<text class="title-text">上传照片</text>
				</view>
				<view class="upload-section">
					<view class="image-list">
						<view class="image-item" v-for="(img, index) in imageList" :key="index" @click="previewImage(index)">
							<image class="detail-image" :src="getImageUrl(img)" mode="aspectFill"></image>
						</view>
					</view>
				</view>
			</view>

			<!-- 处理结果 -->
			<view class="form-card" v-if="detailData.status === '1' && detailData.handleResult">
				<view class="section-title">
					<view class="title-indicator"></view>
					<text class="title-text">处理结果</text>
				</view>
				<view class="content-wrapper">
					<text class="content-text">{{ detailData.handleResult }}</text>
				</view>
				<view class="time-info">
					<text class="time-text">处理时间：{{ formatTime(detailData.handleTime) }}</text>
				</view>
			</view>

			<!-- 状态信息 -->
			<view class="form-card">
				<view class="section-title">
					<view class="title-indicator"></view>
					<text class="title-text">状态</text>
					<view class="status-info">
						<text
							class="urge-tag"
							v-if="detailData.isUrged === '1'"
						>已催办 ({{ detailData.urgeCount || 0 }}次)</text>
						<text
							class="status-text"
							:class="{ 'status-pending': detailData.status === '0', 'status-handled': detailData.status === '1' }"
						>
							{{ detailData.status === '0' ? '待处理' : '已处理' }}
						</text>
					</view>
				</view>
				<view class="time-info" v-if="detailData.createTime">
					<text class="time-text">提交时间：{{ formatTime(detailData.createTime) }}</text>
				</view>
			</view>
		</view>

		<!-- 加载中 -->
		<view class="loading-wrapper" v-if="loading">
			<text class="loading-text">加载中...</text>
		</view>

		<!-- 取消投诉按钮 -->
		<view class="submit-section" v-if="detailData.status === '0' && !loading">
			<button class="submit-btn cancel-btn" @click="handleCancelComplaint">
				<text class="submit-btn-text">取消投诉</text>
			</button>
		</view>
	</view>
</template>

<script>
import { getComplaintDetail, cancelComplaint } from '@/api/complaint.js'
import { BASE_URL } from '@/utils/request'

export default {
	data() {
		return {
			complaintId: '',
			detailData: {
				complaintId: '',
				title: '',
				content: '',
				contactPhone: '',
				images: '',
				status: '0',
				isUrged: '0',
				urgeCount: 0,
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
			this.complaintId = options.id
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
				const res = await getComplaintDetail(this.complaintId, this.userId)
				if (res.code === 200 && res.data) {
					this.detailData = res.data
				} else {
					uni.showToast({
						title: res.msg || '加载失败',
						icon: 'none'
					})
				}
			} catch (err) {
				console.error('加载投诉详情失败:', err)
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

		// 取消投诉
		handleCancelComplaint() {
			uni.showModal({
				title: '确认取消',
				content: '确定要取消此投诉吗？取消后无法恢复',
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
				const res = await cancelComplaint(this.complaintId, this.userId)
				uni.hideLoading()
				if (res.code === 200) {
					uni.showToast({
						title: '取消成功',
						icon: 'success'
					})
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
		overflow: hidden;
	}

	.scroll-content {
		flex: 1;
		overflow: hidden;
		padding-bottom: 180rpx;
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

	/* 表单卡片 */
	.form-card {
		width: 702rpx;
		border-radius: 20rpx;
		opacity: 1;
		background: #ffffff;
		margin: 24rpx 24rpx 0 24rpx;
		padding: 26rpx 0 28rpx 0;
		box-sizing: border-box;
	}

	/* 标题区域 */
	.section-title {
		display: flex;
		align-items: center;
		margin-bottom: 16rpx;
	}

	.section-title:last-child {
		margin-bottom: 0;
	}

	.title-indicator {
		width: 12rpx;
		height: 36rpx;
		opacity: 1;
		background: #0f73ff;
		margin-right: 26rpx;
		border-radius: 2rpx;
		flex-shrink: 0;
	}

	.title-text {
		height: 45rpx;
		opacity: 1;
		color: #171a1f;
		font-size: 32rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 45rpx;
		flex-shrink: 0;
	}

	.title-value {
		flex: 1;
		height: 45rpx;
		color: #333333;
		font-size: 28rpx;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		margin-left: 54rpx;
		line-height: 45rpx;
	}

	.status-info {
		flex: 1;
		display: flex;
		align-items: center;
		justify-content: flex-end;
		gap: 12rpx;
		margin-left: 54rpx;
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

	.status-text {
		font-size: 28rpx;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.status-pending {
		color: #ff8d1a;
	}

	.status-handled {
		color: #1281ff;
	}

	/* 内容区域 */
	.content-wrapper {
		margin: 0 28rpx;
	}

	.content-text {
		color: #333333;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 40rpx;
	}

	.time-info {
		margin: 16rpx 28rpx 0;
	}

	.time-text {
		color: #999999;
		font-size: 24rpx;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	/* 图片区域 */
	.upload-section {
		margin: 0 28rpx;
	}

	.image-list {
		display: flex;
		flex-wrap: wrap;
		gap: 20rpx;
	}

	.image-item {
		width: 166rpx;
		height: 166rpx;
	}

	.detail-image {
		width: 100%;
		height: 100%;
		border-radius: 12rpx;
	}

	/* 提交按钮区域 */
	.submit-section {
		position: fixed;
		bottom: 68rpx;
		left: 24rpx;
		width: 702rpx;
		box-sizing: border-box;
		z-index: 100;
	}

	.submit-btn {
		width: 100%;
		height: 92rpx;
		border-radius: 20rpx;
		opacity: 1;
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
		backdrop-filter: blur(6rpx);
		border: none;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.cancel-btn {
		background: #ffffff;
		border: 1rpx solid #ff4d4f;
	}

	.submit-btn::after {
		border: none;
	}

	.submit-btn-text {
		opacity: 1;
		color: #ffffff;
		font-size: 36rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
	}

	.cancel-btn .submit-btn-text {
		color: #ff4d4f;
	}
</style>
