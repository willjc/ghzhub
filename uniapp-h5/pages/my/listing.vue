<template>
	<view class="page">
		<!-- 房源列表 -->
		<scroll-view class="scroll-content" scroll-y>
			<view class="listing-cards">
				<view
					class="listing-card"
					v-for="(item, index) in listingData"
					:key="index"
					@click="goToDetail(item)"
				>
					<image class="listing-image" :src="getImageUrl(item.image)" mode="aspectFill"></image>
					<view class="listing-info">
						<view class="listing-header">
							<text class="listing-title">{{ item.title }}</text>
						</view>
						<view class="listing-tags">
							<text class="tag" v-for="(tag, tagIndex) in item.tags" :key="tagIndex">{{ tag }}</text>
						</view>
						<text class="listing-detail">{{ item.detail }}</text>
						<text class="listing-status" :class="getStatusClass(item.status)">{{ item.statusText }}</text>
					</view>
				</view>
			</view>

			<!-- 空状态 -->
			<view class="empty-state" v-if="!loading && listingData.length === 0">
				<text class="empty-text">暂无房源记录</text>
			</view>

			<!-- 底部提示 -->
			<view class="footer-line" v-if="listingData.length > 0">
				<text class="footer-text">—— 我是有底线的 ——</text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
import { getMyListings } from '@/api/checkin'
import { BASE_URL } from '@/utils/request'
import authCheck from '@/mixins/authCheck'

export default {
	data() {
		return {
			listingData: [],
			loading: false
		}
	},
	onLoad() {
		// 使用统一的登录检查
		authCheck.checkLogin.call(this, {}, () => {
			this.loadListingData()
		})
	},
	methods: {
		// 加载房源数据
		async loadListingData() {
			this.loading = true
			try {
				// 使用登录检查时保存的 userId
				const res = await getMyListings(this.userId)

				if (res.code === 200 && res.data) {
					this.listingData = res.data
				} else {
					console.error('获取房源列表失败:', res.msg)
					this.listingData = []
				}
			} catch (error) {
				console.error('加载房源数据失败:', error)
				this.listingData = []
			} finally {
				this.loading = false
			}
		},

		// 获取图片完整URL
		getImageUrl(url) {
			if (!url) return '/static/矩形 21@2x.png'

			// 外部链接直接返回
			if (url.startsWith('http://') || url.startsWith('https://')) {
				return url
			}

			// 本地静态资源
			if (url.startsWith('/static/')) {
				return url
			}

			// 后端返回的相对路径，拼接BASE_URL（从config读取）
			return BASE_URL + url
		},

		// 获取状态样式类
		getStatusClass(status) {
			const classMap = {
				'renting': 'status-renting',
				'checked_out': 'status-checked-out',
				'pending': 'status-pending',
				'reviewing': 'status-reviewing',
				'confirming': 'status-confirming',
				'rejected': 'status-rejected',
				'unknown': 'status-unknown'
			}
			return classMap[status] || ''
		},

		// 跳转详情
		goToDetail(item) {
			// 跳转到房源详情页面
			uni.navigateTo({
				url: `/pages/room/detail?roomId=${item.houseId}&projectId=${item.projectId}`
			})
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
	padding: 24rpx;
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
	background-color: #fff;
	padding: 16rpx;
	border-radius: 16rpx;
	overflow: hidden;
	box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.listing-image {
	width: 192rpx;
	height: 238rpx;
	border-radius: 16rpx;
	margin-right: 24rpx;
	flex-shrink: 0;
	background-color: #f0f0f0;
}

.listing-info {
	flex: 1;
	padding: 8rpx;
	display: flex;
	flex-direction: column;
}

.listing-header {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
	margin-bottom: 12rpx;
}

.listing-title {
	color: #1a1a1a;
	font-size: 32rpx;
	font-weight: 500;
	font-family: "PingFang SC", "苹方-简", sans-serif;
	text-align: left;
	line-height: 44rpx;
	display: -webkit-box;
	-webkit-box-orient: vertical;
	-webkit-line-clamp: 2;
	overflow: hidden;
	text-overflow: ellipsis;
}

.listing-tags {
	display: flex;
	flex-wrap: wrap;
	gap: 10rpx;
	margin-bottom: 12rpx;
}

.tag {
	height: 32rpx;
	color: #4c617d;
	font-size: 20rpx;
	font-weight: normal;
	font-family: "PingFang SC", "苹方-简", sans-serif;
	text-align: left;
	line-height: 32rpx;
	background: #f1f5fa;
	padding: 4rpx 12rpx;
	border-radius: 8rpx;
}

.listing-detail {
	height: 40rpx;
	color: #999999;
	font-size: 24rpx;
	font-weight: normal;
	font-family: "PingFang SC", "苹方-简", sans-serif;
	text-align: left;
	line-height: 40rpx;
	margin-bottom: 12rpx;
}

.listing-status {
	height: 40rpx;
	font-size: 30rpx;
	font-weight: 600;
	font-family: "PingFang SC", "苹方-简", sans-serif;
	text-align: left;
	line-height: 40rpx;
	margin-top: auto;
}

/* 在租中 */
.status-renting {
	color: #12a566;
}

/* 已退租 */
.status-checked-out {
	color: #e5252b;
}

/* 待办理 */
.status-pending {
	color: #f5a623;
}

/* 待审核 */
.status-reviewing {
	color: #4a90e2;
}

/* 待确认 */
.status-confirming {
	color: #9b59b6;
}

/* 已拒绝 */
.status-rejected {
	color: #e74c3c;
}

/* 未知 */
.status-unknown {
	color: #95a5a6;
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
}

.footer-line {
	text-align: center;
	padding: 36rpx 0;
}

.footer-text {
	height: 37rpx;
	color: #c7c7c7;
	font-size: 26rpx;
	font-weight: normal;
	font-family: "PingFang SC", "苹方-简", sans-serif;
	text-align: left;
	line-height: 37rpx;
	display: inline-block;
}
</style>
