<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 封面图 -->
			<image v-if="detailData.coverImage" class="header-image" :src="getImageUrl(detailData.coverImage)" mode="aspectFill"></image>

			<!-- 内容区域 -->
			<view class="content-wrapper">
				<!-- 标题 -->
				<text class="detail-title">{{ detailData.activityTitle }}</text>

				<!-- 活动信息 -->
				<view class="info-section">
					<view class="info-item" v-if="detailData.activityLocation">
						<text class="info-label">活动地点：</text>
						<text class="info-value">{{ detailData.activityLocation }}</text>
					</view>
					<view class="info-item" v-if="detailData.registrationTime">
						<text class="info-label">报名时间：</text>
						<text class="info-value">{{ detailData.registrationTime }}</text>
					</view>
					<view class="info-item" v-if="detailData.activityTime">
						<text class="info-label">活动时间：</text>
						<text class="info-value">{{ detailData.activityTime }}</text>
					</view>
					<view class="info-item" v-if="detailData.organizer">
						<text class="info-label">主办单位：</text>
						<text class="info-value">{{ detailData.organizer }}</text>
					</view>
					<view class="info-item" v-if="detailData.maxParticipants">
						<text class="info-label">报名人数限制：</text>
						<text class="info-value">{{ detailData.maxParticipants }}人</text>
					</view>
				</view>

				<!-- 正文内容 -->
				<view class="content-divider"></view>
				<rich-text class="detail-content" :nodes="detailData.activityContent"></rich-text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				activityId: null,
				detailData: {
					activityTitle: '',
					coverImage: '',
					activityLocation: '',
					registrationTime: '',
					activityTime: '',
					organizer: '',
					maxParticipants: null,
					currentParticipants: 0,
					activityContent: ''
				}
			}
		},
		onLoad(options) {
			// 获取活动ID
			if (options.id) {
				this.activityId = options.id;
				this.loadDetail(options.id);
			}
		},
		methods: {
			/** 加载活动详情 */
			loadDetail(id) {
				uni.request({
					url: 'http://localhost:8090/h5/activity/' + id,
					method: 'GET',
					success: (res) => {
						if (res.data.code === 200) {
							const data = res.data.data;

							// 格式化报名时间
							let registrationTime = '';
							if (data.registrationStartTime && data.registrationEndTime) {
								const startDate = this.formatDate(data.registrationStartTime);
								const endDate = this.formatDate(data.registrationEndTime);
								registrationTime = `${startDate} 至 ${endDate}`;
							}

							// 格式化活动时间
							let activityTime = '';
							if (data.activityStartTime && data.activityEndTime) {
								const startDateTime = this.formatDateTime(data.activityStartTime);
								const endTime = this.formatTime(data.activityEndTime);
								activityTime = `${startDateTime} - ${endTime}`;
							}

							this.detailData = {
								activityTitle: data.activityTitle,
								coverImage: data.coverImage,
								activityLocation: data.activityLocation,
								registrationTime: registrationTime,
								activityTime: activityTime,
								organizer: data.organizer,
								maxParticipants: data.maxParticipants,
								currentParticipants: data.currentParticipants,
								activityContent: this.processHtmlContent(data.activityContent)
							};
						} else {
							uni.showToast({
								title: res.data.msg || '加载失败',
								icon: 'none'
							});
						}
					},
					fail: (err) => {
						console.error('请求失败:', err);
						uni.showToast({
							title: '网络请求失败',
							icon: 'none'
						});
					}
				});
			},

			/** 获取图片完整URL */
			getImageUrl(url) {
				if (!url) return '';

				// 外部链接(http/https开头)，直接返回
				if (url.startsWith('http://') || url.startsWith('https://')) {
					return url;
				}

				// 拼接后端baseUrl
				const baseUrl = 'http://localhost:8090';
				return baseUrl + (url.startsWith('/') ? url : '/' + url);
			},

			/** 格式化日期 YYYY-MM-DD */
			formatDate(dateStr) {
				if (!dateStr) return '';
				const date = new Date(dateStr);
				const year = date.getFullYear();
				const month = String(date.getMonth() + 1).padStart(2, '0');
				const day = String(date.getDate()).padStart(2, '0');
				return `${year}-${month}-${day}`;
			},

			/** 格式化日期时间 YYYY-MM-DD HH:mm */
			formatDateTime(dateStr) {
				if (!dateStr) return '';
				const date = new Date(dateStr);
				const datepart = this.formatDate(dateStr);
				const hour = String(date.getHours()).padStart(2, '0');
				const minute = String(date.getMinutes()).padStart(2, '0');
				return `${datepart} ${hour}:${minute}`;
			},

			/** 格式化时间 HH:mm */
			formatTime(dateStr) {
				if (!dateStr) return '';
				const date = new Date(dateStr);
				const hour = String(date.getHours()).padStart(2, '0');
				const minute = String(date.getMinutes()).padStart(2, '0');
				return `${hour}:${minute}`;
			},

			/** 处理HTML内容，替换图片路径 */
			processHtmlContent(html) {
				if (!html) return '';

				// 替换 /dev-api 前缀为完整URL
				const baseUrl = 'http://localhost:8090';
				let processedHtml = html.replace(/src="\/dev-api/g, `src="${baseUrl}`);

				// 同时处理可能存在的相对路径图片
				processedHtml = processedHtml.replace(/src="\/profile/g, `src="${baseUrl}/profile`);

				return processedHtml;
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

	.header-image {
		width: 100%;
		height: 400rpx;
		display: block;
	}

	.content-wrapper {
		padding: 24rpx;
		box-sizing: border-box;
		background-color: #ffffff;
	}

	.detail-title {
		min-height: 100rpx;
		opacity: 1;
		color: #1a1a1a;
		font-size: 40rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 50rpx;
		display: block;
		margin-bottom: 30rpx;
		word-wrap: break-word;
	}

	.info-section {
		margin-bottom: 20rpx;
	}

	.info-item {
		display: flex;
		align-items: flex-start;
		margin-bottom: 16rpx;
	}

	.info-label {
		color: #999999;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 36rpx;
		flex-shrink: 0;
	}

	.info-value {
		color: #666666;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 36rpx;
		flex: 1;
	}

	.content-divider {
		width: 100%;
		height: 1rpx;
		background-color: #e5e5e5;
		margin: 20rpx 0 30rpx;
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
	.detail-content >>> img {
		max-width: 100%;
		height: auto;
		display: block;
		margin: 10rpx 0;
	}

	.detail-content >>> p {
		margin: 10rpx 0;
		line-height: 40rpx;
	}
</style>
