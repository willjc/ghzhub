<template>
	<view class="page">
		<!-- 搜索栏 -->
		<view class="search-section">
			<view class="search-bar">
				<image class="search-icon" src="/static/画板 2@2x.png"></image>
				<view class="search-divider"></view>
				<text class="search-text">航南新城</text>
				<view class="search-btn">
					<text>搜索</text>
				</view>
			</view>
		</view>

		<!-- 活动列表 -->
		<scroll-view class="scroll-content" scroll-y>
			<view class="listing-cards">
				<view 
					class="listing-card" 
					v-for="(item, index) in listingData" 
					:key="index"
					@click="goToDetail(item)"
				>
					<image class="listing-image" :src="item.image" mode="aspectFill"></image>
					<view class="listing-info">
						<text class="listing-title">{{ item.title }}</text>
						<view class="activity-detail">
							<text class="detail-label">活动地点：</text>
							<text class="detail-value">{{ item.location }}</text>
						</view>
						<view class="activity-detail">
							<text class="detail-label">报名时间：</text>
							<text class="detail-value">{{ item.registrationTime }}</text>
						</view>
						<view class="activity-detail">
							<text class="detail-label">活动时间：</text>
							<text class="detail-value">{{ item.activityTime }}</text>
						</view>
					</view>
				</view>
			</view>
			
			
		</scroll-view>
	</view>
</template>

<script>
	import config from '@/config/index'

	export default {
		data() {
			return {
				listingData: []
			}
		},
		onLoad() {
			this.loadActivityList();
		},
		methods: {
			/** 加载活动列表 */
			loadActivityList() {
				uni.request({
					url: config.baseUrl + '/h5/activity/list',
					method: 'GET',
					data: {
						pageNum: 1,
						pageSize: 100
					},
					success: (res) => {
						if (res.data.code === 200) {
							const activities = res.data.rows || [];
							this.listingData = activities.map(item => this.formatActivityItem(item));
						} else {
							console.error('加载活动列表失败:', res.data.msg);
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

			/** 格式化活动列表项 */
			formatActivityItem(item) {
				// 格式化报名时间
				let registrationTime = '';
				if (item.registrationStartTime && item.registrationEndTime) {
					const startDate = this.formatDate(item.registrationStartTime);
					const endDate = this.formatDate(item.registrationEndTime);
					registrationTime = `${startDate} 至 ${endDate}`;
				}

				// 格式化活动时间
				let activityTime = '';
				if (item.activityStartTime && item.activityEndTime) {
					const startDateTime = this.formatDateTime(item.activityStartTime);
					const endTime = this.formatTime(item.activityEndTime);
					activityTime = `${startDateTime} - ${endTime}`;
				}

				return {
					activityId: item.activityId,
					title: item.activityTitle,
					location: item.activityLocation,
					registrationTime: registrationTime,
					activityTime: activityTime,
					image: this.getImageUrl(item.coverImage)
				};
			},

			/** 跳转到活动详情 */
			goToDetail(item) {
				uni.navigateTo({
					url: `/pages/home/detail?id=${item.activityId}`
				});
			},

			/** 获取图片完整URL */
			getImageUrl(url) {
				if (!url) return '/static/矩形 21@2x.png'; // 默认图片

				// 外部链接(http/https开头)，直接返回
				if (url.startsWith('http://') || url.startsWith('https://')) {
					return url;
				}

				// 拼接后端baseUrl
				const baseUrl = config.staticUrl;
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

	/* 搜索栏 */
	.search-section {
		padding: 24rpx;
		background-color: #f5f6fc;
		box-sizing: border-box;
	}

	.search-bar {
		width: 100%;
		max-width: 702rpx;
		height: 80rpx;
		border-radius: 20rpx;
		opacity: 1;
		background: #ffffff;
		display: flex;
		align-items: center;
		padding: 0 30rpx;
		margin: 0 auto;
		box-sizing: border-box;
	}

	.search-icon {
		width: 28rpx;
		height: 28rpx;
		margin-right: 16rpx;
	}

	.search-divider {
		width: 1rpx;
		height: 28rpx;
		background-color: #3c3c43;
		margin-right: 16rpx;
	}

	.search-text {
		flex: 1;
		color: #3c3c43;
		font-size: 28rpx;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.search-btn {
		width: 100rpx;
		height: 56rpx;
		border-radius: 12rpx;
		opacity: 1;
		background: linear-gradient(201.4deg, #d0edf7 0%, #f2fafc 100%);
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 0 20rpx;
		box-sizing: border-box;
		flex-shrink: 0;
	}

	.search-btn text {
		width: 56rpx;
		height: 30rpx;
		opacity: 1;
		color: #1281ff;
		font-size: 28rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 30rpx;
	}

	.scroll-content {
		flex: 1;
		overflow-y: auto;
		padding: 0 24rpx;
		box-sizing: border-box;
	}

	.listing-cards {
		display: flex;
		flex-direction: column;
		gap: 30rpx;
		
		border-radius: 20rpx;
		overflow: hidden;
		
	}

	.listing-card {
		display: flex;
		flex-direction: column;
		background-color: #fff;
		border-radius: 16rpx;
		overflow: hidden;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.listing-image {
		width: 100%;
		height: 360rpx;
		display: block;
	}

	.listing-info {
		flex: 1;
		display: flex;
		flex-direction: column;
		justify-content: space-between;
		padding: 16rpx 24rpx 24rpx 24rpx;
	}

	.listing-title {
		width: 100%;
		opacity: 1;
		color: #1a1a1a;
		font-size: 30rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
		margin-bottom: 16rpx;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 2;
		line-clamp: 2;
		overflow: hidden;
		text-overflow: ellipsis;
		margin-top: 8rpx;
	}

	.activity-detail {
		display: flex;
		align-items: center;
		margin-bottom: 4rpx;
	}

	.detail-label {
		width: 120rpx;
		height: 34rpx;
		opacity: 1;
		color: #999999;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 34rpx;
		flex-shrink: 0;
	}

	.detail-value {
		
		height: 34rpx;
		opacity: 1;
		color: #666666;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 34rpx;
	}

	.footer-line {
		text-align: center;
		padding: 36rpx 0;
	}

	.footer-text {

		height: 37rpx;
		opacity: 1;
		color: #c7c7c7;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 37rpx;
		display: inline-block;
	}
</style>

