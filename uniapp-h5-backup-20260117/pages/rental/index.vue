<template>
	<view class="page">
		<!-- 搜索栏 -->
		<view class="search-section">
			<view class="search-bar">
				<image class="search-icon" src="/static/画板 2@2x.png"></image>
				<input
					class="search-input"
					v-model="searchKeyword"
					placeholder="请输入项目名称"
					placeholder-style="color: #999999"
					confirm-type="search"
					@confirm="handleSearch"
				/>
				<view class="search-btn" @click="handleSearch">
					<text>搜索</text>
				</view>
			</view>
		</view>

		<!-- 房源列表 -->
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
						<view class="listing-header">
							<text class="listing-title">{{ item.title }}</text>

						</view>
						<view class="listing-status">
							<text class="status-text" :class="{ available: item.hasUnits }">有房源</text>
							<text class="status-divider">|</text>
							<text class="status-count">共{{ item.totalUnits }}套</text>
							<text class="listing-distance">{{ item.distance }}</text>
						</view>
						<text class="listing-address">{{ item.address }}</text>
						<view class="listing-tags">
							<text class="tag" v-for="(tag, tagIndex) in item.tags" :key="tagIndex">{{ tag }}</text>
						</view>
						<view class="listing-price">
							<text class="price-number">{{ item.price }}</text>
							<text class="price-unit">元</text>
							<text class="price-suffix">/月起</text>
						</view>
					</view>
				</view>
			</view>

			<!-- 底部提示 -->
			<view class="footer-line">
				<text class="footer-text">—— 我是有底线的 ——</text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import { getProjectListByType } from '@/api/project'

	export default {
		data() {
			return {
				loading: false,
				searchKeyword: '', // 搜索关键词
				listingData: [] // 从API加载保租房项目列表
			}
		},
		onLoad() {
			// 页面加载时获取保租房项目列表
			this.loadProjectList()
		},
		methods: {
			/**
			 * 处理搜索
			 */
			handleSearch() {
				console.log('搜索关键词:', this.searchKeyword)
				this.loadProjectList()
			},

			/**
			 * 加载保租房项目列表
			 */
			async loadProjectList() {
				this.loading = true

				try {
					// 构建查询参数
					const params = {}
					if (this.searchKeyword.trim()) {
						params.projectName = this.searchKeyword.trim()
					}

					// 项目类型：2=保租房
					const response = await getProjectListByType('2', params)

					// 若依框架响应格式：{ code: 200, msg: 'success', data: [...] }
					const projectList = response.data || []

					// 转换数据格式
					this.listingData = projectList.map(project => this.transformProjectData(project))

					console.log('加载保租房项目列表成功:', this.listingData)

					// 搜索无结果提示
					if (this.searchKeyword.trim() && this.listingData.length === 0) {
						uni.showToast({
							title: '未找到匹配的项目',
							icon: 'none'
						})
					}
				} catch (error) {
					console.error('加载保租房项目列表失败:', error)
					uni.showToast({
						title: '加载项目列表失败',
						icon: 'none'
					})
					this.listingData = []
				} finally {
					this.loading = false
				}
			},

			/**
			 * 转换后端项目数据为前端展示格式
			 * @param {Object} project 后端项目对象
			 * @returns {Object} 前端展示对象
			 */
			transformProjectData(project) {
				return {
					projectId: project.projectId, // 项目ID（用于跳转详情）
					title: project.projectName || '未命名项目', // 项目名称
					hasUnits: (project.availableHouses || 0) > 0, // 是否有房源
					totalUnits: project.totalHouses || 0, // 总套数
					distance: '附近', // 距离（暂时固定值）
					address: project.address || '地址未填写', // 地址
					tags: this.parseFacilities(project.facilities), // 设施标签
					price: parseFloat(project.price) || 0, // 起租价格（转换为数字）
					image: this.getImageUrl(project.coverImage) // 封面图
				}
			},

			/**
			 * 解析设施字符串为标签数组
			 * @param {String} facilities 设施字符串（逗号分隔）
			 * @returns {Array} 标签数组
			 */
			parseFacilities(facilities) {
				if (!facilities) {
					return []
				}
				return facilities.split(',').filter(item => item.trim() !== '').slice(0, 3) // 最多显示3个标签
			},

			/**
			 * 获取图片完整URL
			 * @param {String} imagePath 图片相对路径
			 * @returns {String} 完整URL
			 */
			getImageUrl(imagePath) {
				if (!imagePath) {
					// 无图片时返回默认图
					return '/static/矩形 21@2x.png'
				}

				// 外部链接直接返回
				if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
					return imagePath
				}

				// 直接返回相对路径，由manifest.json中的代理转发到后端
				return imagePath
			},

			goToDetail(item) {
				// 跳转到项目详情页面
				uni.navigateTo({
					url: `/pages/project/detail?id=${item.projectId}`
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

	.search-input {
		flex: 1;
		color: #3c3c43;
		font-size: 28rpx;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		height: 100%;
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
		margin-bottom: 4rpx;
	}

	.listing-title {
		width: 320rpx;
		height: 40rpx;
		opacity: 1;
		color: #1a1a1a;
		font-size: 32rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
		flex: 1;
	}

	.listing-distance {
		width: 69rpx;
		height: 40rpx;
		opacity: 1;
		color: #999999;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
		margin-left: 170rpx;
	}

	.listing-status {
		display: flex;
		align-items: center;
		margin-bottom: 6rpx;
	}

	.status-text {
		font-size: 24rpx;
		color: #999;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.status-text.available {
		width: 72rpx;
		height: 40rpx;
		opacity: 1;
		color: #207fff;
		font-size: 24rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
	}

	.status-divider {
		width: 24rpx;
		height: 40rpx;
		opacity: 1;
		color: #cfcfcf;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
		margin: 0 10rpx;
	}

	.status-count {

		height: 40rpx;
		opacity: 1;
		color: #999999;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
	}

	.listing-address {
		width: 360rpx;
		height: 40rpx;
		opacity: 1;
		color: #333333;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
		margin-bottom: 6rpx;
	}

	.listing-tags {
		display: flex;
		flex-wrap: wrap;
		gap: 10rpx;
		margin-bottom: 12rpx;
	}

	.tag {
		width: 60rpx;
		height: 32rpx;
		opacity: 1;
		color: #4c617d;
		font-size: 20rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 32rpx;
		background-color: #f5f5f5;
		padding: 4rpx 12rpx;
		border-radius: 8rpx;
	}

	.listing-price {
		display: flex;
		align-items: baseline;
		margin-top: auto;
	}

	.price-number {
		height: 40rpx;
		opacity: 1;
		color: #e5252b;
		font-weight: 700;
		text-align: left;
		line-height: 40rpx;
		font-size: 30rpx;
		font-family: "HarmonyOSSansSC", sans-serif;
	}

	.price-unit {
		height: 40rpx;
		opacity: 1;
		color: #e5252b;
		font-weight: normal;
		text-align: left;
		line-height: 40rpx;
		font-size: 28rpx;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.price-suffix {
		height: 40rpx;
		opacity: 1;
		color: #000000;
		font-weight: normal;
		text-align: left;
		line-height: 40rpx;
		font-size: 28rpx;
		font-family: "PingFang SC", "苹方-简", sans-serif;
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

