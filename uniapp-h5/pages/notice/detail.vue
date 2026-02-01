<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<view class="content-wrapper">
				<!-- 标题 -->
				<text class="detail-title">{{ detailData.noticeTitle }}</text>

				<!-- 日期 -->
				<view class="info-section">
					<text class="info-time">{{ detailData.createTime }}</text>
				</view>

				<!-- 分隔线 -->
				<view class="content-divider"></view>

				<!-- 正文内容 -->
				<rich-text class="detail-content" :nodes="detailData.noticeContent"></rich-text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import config from '@/config/index'

	export default {
		data() {
			return {
				noticeId: null,
				detailData: {
					noticeTitle: '',
					noticeContent: '',
					createTime: ''
				}
			}
		},
		onLoad(options) {
			if (options.noticeId) {
				this.noticeId = options.noticeId
				this.loadDetail(options.noticeId)
			}
		},
		methods: {
			/** 加载通知公告详情 */
			loadDetail(noticeId) {
				uni.request({
					url: config.baseUrl + '/h5/notice/' + noticeId,
					method: 'GET',
					success: (res) => {
						if (res.data.code === 200) {
							const data = res.data.data
							this.detailData = {
								noticeTitle: data.noticeTitle || '',
								noticeContent: data.noticeContent || '',
								createTime: this.formatDateTime(data.createTime)
							}
						} else {
							uni.showToast({
								title: res.data.msg || '加载失败',
								icon: 'none'
							})
						}
					},
					fail: (err) => {
						console.error('请求失败:', err)
						uni.showToast({
							title: '网络请求失败',
							icon: 'none'
						})
					}
				})
			},

			/** 格式化日期时间 */
			formatDateTime(dateStr) {
				if (!dateStr) return ''
				const date = new Date(dateStr)
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
		background-color: #f5f6fc;
		display: flex;
		flex-direction: column;
		min-height: 100vh;
	}

	.scroll-content {
		flex: 1;
		overflow-y: auto;
	}

	.content-wrapper {
		padding: 24rpx;
		box-sizing: border-box;
		background-color: #ffffff;
		min-height: 100vh;
	}

	.detail-title {
		opacity: 1;
		color: #1a1a1a;
		font-size: 40rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 50rpx;
		display: block;
		margin-bottom: 16rpx;
		word-wrap: break-word;
	}

	.info-section {
		margin-bottom: 16rpx;
	}

	.info-time {
		color: #999999;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 36rpx;
	}

	.content-divider {
		width: 100%;
		height: 1rpx;
		background-color: #e5e5e5;
		margin: 16rpx 0 30rpx;
	}

	.detail-content {
		opacity: 1;
		color: #333333;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
		display: block;
		word-wrap: break-word;
	}

	/* rich-text 内部元素样式 */
	.detail-content >>> p {
		margin: 10rpx 0;
		line-height: 40rpx;
	}

	.detail-content >>> img {
		max-width: 100%;
		height: auto;
		display: block;
		margin: 20rpx 0;
	}
</style>
