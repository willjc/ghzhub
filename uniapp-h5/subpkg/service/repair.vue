<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 报修卡片列表 -->
			<view class="card" v-for="(item, index) in repairList" :key="index">
				<!-- 卡片顶部渐变背景 -->
				<view class="card-top-bg">
					<image class="card-bg-image" src="/static/物业卡片-背景@2x.png" mode="aspectFill"></image>
				</view>

				<!-- 卡片内容 -->
				<view class="card-content">
					<!-- 报修编号和日期 -->
					<view class="card-header">
						<text class="repair-number">报修编号：{{ item.repairNo }}</text>
						<text class="repair-date">{{ item.createTime }}</text>
					</view>

					<!-- 状态标签 -->
					<view class="status-wrapper">
						<text
							class="status-tag"
							:class="{
								'status-pending': item.status === '0',
								'status-completed': item.status === '1',
								'status-cancelled': item.status === '2'
							}"
						>
							{{ item.statusText }}
						</text>
					</view>

					<!-- 报修标题 -->
					<text class="repair-title">{{ item.title }}</text>

					<!-- 报修描述 -->
					<text class="repair-desc">{{ item.content }}</text>

					<!-- 图片列表 -->
					<view class="image-list" v-if="item.images && item.images.length > 0">
						<image
							class="repair-image"
							v-for="(img, imgIndex) in item.images"
							:key="imgIndex"
							:src="img"
							mode="aspectFill"
							@click="previewImage(item.images, imgIndex)"
						></image>
					</view>

					<!-- 查看详情 -->
					<view class="card-footer" @click="handleViewDetail(item)">
						<text class="view-detail">查看详情</text>
						<image class="arrow-icon" src="/static/向右1@2x.png" mode="aspectFit"></image>
					</view>
				</view>
			</view>

			<!-- 空状态 -->
			<view class="empty-state" v-if="repairList.length === 0 && !loading">
				<text class="empty-text">暂无报修记录</text>
			</view>

			<!-- 加载中 -->
			<view class="loading-state" v-if="loading">
				<text class="loading-text">加载中...</text>
			</view>
		</scroll-view>

		<!-- 底部报修申请按钮 -->
		<view class="bottom-btn-container">
			<view class="bottom-btn" @click="handleRepairApply">
				<text class="bottom-btn-text">报修申请</text>
			</view>
		</view>
	</view>
</template>

<script>
import { getMyRepairList } from '@/api/repair.js'
import { BASE_URL } from '@/utils/request'
import authCheck from '@/mixins/authCheck'

export default {
	data() {
		return {
			repairList: [],
			loading: false,
			userId: null  // 当前登录用户ID
		}
	},
	onLoad() {
		// 使用统一的登录检查
		authCheck.checkLogin.call(this, {}, () => {
			this.loadRepairList()
		})
	},
	onShow() {
		// 页面显示时刷新列表
		if (this.userId) {
			this.loadRepairList()
		}
	},
	methods: {
		// 加载报修列表
		async loadRepairList() {
			if (!this.userId) {
				console.error('用户ID不存在')
				return
			}

			try {
				this.loading = true
				const res = await getMyRepairList(this.userId)
				if (res.code === 200 && res.data) {
					// 处理数据，添加图片数组、格式化时间
					this.repairList = res.data.map(item => {
						return {
							...item,
							id: item.repairId,
							title: this.getRepairTitle(item.location, item.description),
							content: item.description,
							createTime: this.formatTime(item.createTime),
							statusText: this.getStatusText(item.status),
							images: item.images ? this.parseImages(item.images) : []
						}
					})
				}
			} catch (err) {
				console.error('加载报修列表失败:', err)
			} finally {
				this.loading = false
			}
		},

		// 获取状态文本
		getStatusText(status) {
			const statusMap = {
				'0': '待处理',
				'1': '已完成',
				'2': '已取消'
			}
			return statusMap[status] || '未知'
		},

		// 获取报修标题（位置+描述摘要）
		getRepairTitle(location, description) {
			const loc = location || '未知位置'
			const desc = description ? description.substring(0, 15) + (description.length > 15 ? '...' : '') : '无描述'
			return `${loc} - ${desc}`
		},

		// 解析图片字符串为数组
		parseImages(imagesStr) {
			if (!imagesStr) return []
			return imagesStr.split(',').filter(img => img).map(img => {
				// 如果不是完整URL，拼接BASE_URL
				if (img.startsWith('http://') || img.startsWith('https://') || img.startsWith('/static/')) {
					return img
				}
				return BASE_URL + img
			})
		},

		// 格式化时间
		formatTime(timeStr) {
			if (!timeStr) return ''
			const date = new Date(timeStr)
			const year = date.getFullYear()
			const month = String(date.getMonth() + 1).padStart(2, '0')
			const day = String(date.getDate()).padStart(2, '0')
			return `${year}-${month}-${day}`
		},

		// 预览图片
		previewImage(images, index) {
			uni.previewImage({
				current: index,
				urls: images
			})
		},

		// 查看详情
		handleViewDetail(item) {
			uni.navigateTo({
				url: '/subpkg/service/repair-detail?id=' + item.repairId
			})
		},

		// 报修申请
		handleRepairApply() {
			uni.navigateTo({
				url: '/subpkg/service/repair-submit'
			})
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
		margin: 0 auto 24rpx;
		box-sizing: border-box;
		overflow: hidden;
	}

	.card-top-bg {
		width: 100%;
		height: 80rpx;
		border-radius: 18rpx 18rpx 0 0;

		position: relative;
	}

	.card-bg-image {
		width: 100%;
		height: 100%;
		position: absolute;
		top: 0;
		left: 0;
	}

	.card-content {
		padding: 0 28rpx 28rpx;
		margin-top: -60rpx;
		position: relative;
	}

	.card-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 12rpx;
	}

	.repair-number {
		color: #666666 !important;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.repair-date {
		color: #666666 !important;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.status-wrapper {
		margin-bottom: 12rpx;
	}

	.status-tag {
		display: inline-block;
		padding: 4rpx 16rpx;
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

	.repair-title {
		display: block;
		color: #333333;
		font-size: 28rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		margin-bottom: 8rpx;
	}

	.repair-desc {
		display: block;
		color: #666666;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 37rpx;
		margin-bottom: 12rpx;
	}

	.image-list {
		display: flex;
		flex-wrap: wrap;
		gap: 16rpx;
		margin-bottom: 19rpx;
		border-bottom: 1rpx solid #f0f0f0;
		padding-bottom: 24rpx;
	}

	.repair-image {
		width: 140rpx;
		height: 140rpx;
		border-radius: 8rpx;
	}

	.card-footer {
		display: flex;
		justify-content: flex-end;
		align-items: center;
	}

	.view-detail {
		color: #999999;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.arrow-icon {
		width: 24rpx;
		height: 24rpx;
		margin-left: 12rpx;
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
