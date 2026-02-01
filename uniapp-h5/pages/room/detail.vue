<template>
	<view class="page">
		<!-- 顶部图片区域 -->
		<view class="header-section" @click="handleHeaderClick">
			<image class="header-bg" :src="headerImage" mode="aspectFill"></image>
			<view class="header-overlay"></view>

			<!-- 图片类型切换 -->
			<view class="image-tabs">
				<view
					class="image-tab-item"
					:class="{ active: activeImageTab === item.key }"
					v-for="item in imageTabs"
					:key="item.key"
					@click.stop="switchImageTab(item.key)"
				>
					<text class="image-tab-text" :class="{ active: activeImageTab === item.key }">{{ item.name }}</text>
				</view>
			</view>

			<!-- 查看全部按钮 -->
			<view class="view-all-btn" @click.stop="viewAllImages">
				<text class="view-all-text">查看全部</text>
				<image class="view-all-arrow" src="/static/fangyaun/向右1@2x.png" mode="aspectFit"></image>
			</view>
		</view>
		
		<!-- 房源信息卡片 -->
		<view class="room-info-card">
			<view class="room-info-header">
				<text class="room-title">{{ roomInfo.buildingNo }}-{{ roomInfo.unitNo }}-{{ roomInfo.roomNo }}</text>
				<view class="room-price">
					<text class="price-number">{{ roomInfo.price }}</text>
					<text class="price-unit">元/月</text>
				</view>
			</view>
			
			<!-- 房源详细信息 -->
			<view class="room-detail-card">
				<view class="room-detail-item">
					<view class="detail-value">
						<text class="detail-number">{{ roomInfo.area }}</text>
						<text class="detail-unit">㎡</text>
					</view>
					<text class="detail-label">建筑面积</text>
				</view>
				<view class="room-detail-item">
					<text class="detail-value-text">{{ roomInfo.layout }}</text>
					<text class="detail-label">户型</text>
				</view>
				<view class="room-detail-item">
					<text class="detail-value-text">{{ roomInfo.orientation }}</text>
					<text class="detail-label">朝向</text>
				</view>
				<view class="room-detail-item">
					<text class="detail-value-text">{{ roomInfo.decoration }}</text>
					<text class="detail-label">装修</text>
				</view>
			</view>
		</view>
		
		<!-- 项目信息 -->
		<view class="project-card" @click="goToProject">
			<view class="project-content">
				<view class="project-left">
					<view class="project-header">
						<image class="project-icon" src="/static/fangyaun/项目@2x.png" mode="aspectFit"></image>
						<text class="project-name">{{ roomInfo.projectName }}</text>
					</view>
					<view class="project-location">
						<image class="location-icon" src="/static/fangyaun/定位@2x.png" mode="aspectFit"></image>
						<text class="location-text">{{ roomInfo.address }}</text>
					</view>
				</view>
				<view class="nav-wrapper">
					<image class="nav-icon" src="/static/导航@2x.png" mode="aspectFit"></image>
					<text class="location-distance">{{ roomInfo.distance }}</text>
				</view>
			</view>
		</view>
		
		<!-- 房源简介 -->
		<view class="amenities-card">
			<text class="card-title">房源简介</text>
			<view class="intro-content" v-if="roomInfo.introduction">
				<text class="intro-text">{{ roomInfo.introduction }}</text>
			</view>
			<view class="intro-empty" v-else>
				<text class="empty-text">暂无房源简介</text>
			</view>
		</view>
		
		<!-- 管家电话 -->
		<view class="contact-card">
			<view class="contact-content">
				<view class="contact-left">
					<text class="contact-title">管家电话</text>
					<text class="contact-text">联系电话：{{ roomInfo.contactPhone }}</text>
				</view>
				<view class="call-btn" @click="callManager">
					<image class="call-icon" src="/static/fangyaun/电话@2x.png" mode="aspectFit"></image>
					<text class="call-text">拨打</text>
				</view>
			</view>
		</view>
		
		<!-- 底部按钮 -->
		<view class="footer-btns">
			<view class="reserve-btn" @click="reserveRoom">
				<text class="reserve-text">预约看房</text>
			</view>
			<view class="select-btn" @click="selectRoom">
				<text class="select-text">房源选定</text>
			</view>
		</view>
	</view>
</template>

<script>
import { getHouseDetail, getHouseVR, getHouseImages } from '@/api/house'
import config from '@/config/index'

	export default {
		data() {
			return {
				roomId: '',
				projectId: '',
				activeImageTab: 'vr',
				imageTabs: [
					{ key: 'vr', name: 'VR' },
					{ key: 'main', name: '主图' },
					{ key: 'outdoor', name: '户型图' },
					{ key: 'indoor', name: '室内图' },
					{ key: 'bathroom', name: '卫生间' }
				],
				roomInfo: {
					buildingNo: '',
					unitNo: '',
					roomNo: '',
					price: 0,
					area: '',
					layout: '',
					orientation: '',
					decoration: '',
					projectName: '',
					address: '',
					distance: '附近',
					contactPhone: '',
					introduction: ''  // 房源简介
				},
				amenitiesList: [],
				hasVR: false,  // 是否有VR
				vrList: [],    // VR列表
				headerImage: '/static/fangyaun/房源图片@2x.png',  // 顶部显示的图片,默认为静态图片
				// 各类型图片数据
				imageData: {
					vr: [],       // VR图片（来自vrList）
					main: [],     // 主图
					outdoor: [],  // 户外图
					indoor: [],   // 室内图
					bathroom: []  // 卫生间
				}
			}
		},
		onLoad(options) {
			if (options.roomId) {
				this.roomId = options.roomId
			}
			if (options.projectId) {
				this.projectId = options.projectId
			}
			this.loadRoomDetail()
			this.loadVRList()    // 加载VR列表
			this.loadImages()    // 加载房源图片
		},
		methods: {
			switchImageTab(key) {
				this.activeImageTab = key

				// 根据选择的标签切换顶部图片
				const images = this.imageData[key] || []
				if (images.length > 0) {
					// 有对应类型图片，显示第一张
					this.headerImage = this.getImageUrl(images[0])
				} else {
					// 没有对应类型图片，显示默认图片
					this.headerImage = '/static/fangyaun/房源图片@2x.png'
				}

				console.log(`切换到${key}标签，图片:`, this.headerImage)
			},
			handleHeaderClick() {
				// 只有当前是VR标签且有VR资源时，才能进入VR页面
				if (this.activeImageTab === 'vr' && this.hasVR && this.vrList.length > 0) {
					const vrUrl = encodeURIComponent(this.vrList[0].vrUrl)
					uni.navigateTo({
						url: `/pages/room/vr?roomId=${this.roomId}&vrUrl=${vrUrl}`
					})
				} else if (this.activeImageTab === 'vr') {
					// VR标签但没有VR资源
					uni.showToast({
						title: '暂无VR资源',
						icon: 'none'
					})
				}
				// 其他标签（main/outdoor/indoor/bathroom）点击无响应
			},
			viewAllImages() {
				uni.navigateTo({
					url: `/pages/room/images?roomId=${this.roomId}`
				})
			},
			async loadVRList() {
				try {
					const response = await getHouseVR(this.roomId)

					if (response.code === 200) {
						this.vrList = response.data || []
						this.hasVR = this.vrList.length > 0

						// 将VR图片URL存储到imageData.vr
						this.imageData.vr = this.vrList.map(vr => vr.vrUrl)

						// 如果有VR图片,将顶部图片设置为第一张VR图片
						if (this.hasVR) {
							this.headerImage = this.getImageUrl(this.vrList[0].vrUrl)
							console.log('VR图片已设置为顶部图片:', this.headerImage)
						}
					}
				} catch (error) {
					console.error('获取VR列表失败:', error)
				}
			},
			async loadImages() {
				try {
					const response = await getHouseImages(this.roomId)

					if (response.code === 200) {
						const categories = response.data || []

						// 将API返回的分类数据存储到imageData
						categories.forEach(category => {
							const key = category.key  // main, layout, bedroom, bathroom, indoor, outdoor
							const images = category.images || []

							// 映射API返回的key到我们的imageData
							if (key === 'main') {
								this.imageData.main = images
							} else if (key === 'outdoor') {
								this.imageData.outdoor = images
							} else if (key === 'indoor') {
								this.imageData.indoor = images
							} else if (key === 'bathroom') {
								this.imageData.bathroom = images
							}
						})

						console.log('房源图片加载完成:', this.imageData)
					}
				} catch (error) {
					console.error('获取房源图片失败:', error)
				}
			},
			goToProject() {
				if (this.projectId) {
					uni.navigateTo({
						url: `/pages/project/detail?id=${this.projectId}`
					})
				}
			},
			callManager() {
				uni.makePhoneCall({
					phoneNumber: this.roomInfo.contactPhone,
					fail: () => {
						uni.showToast({
							title: '拨打电话失败',
							icon: 'none'
						})
					}
				})
			},
			reserveRoom() {
				uni.navigateTo({
					url: `/pages/room/reserve?roomId=${this.roomId}&projectId=${this.projectId}`
				})
			},
			selectRoom() {
				uni.navigateTo({
					url: `/pages/contract/sign?roomId=${this.roomId}&projectId=${this.projectId}`
				})
			},
			async loadRoomDetail() {
				try {
					uni.showLoading({ title: '加载中...' })
					const response = await getHouseDetail(this.roomId)

					if (response.code === 200 && response.data) {
						const data = response.data

						// 映射后端数据到前端
						this.roomInfo = {
							buildingNo: data.buildingNo || '',
							unitNo: data.unitNo || '',
							roomNo: data.roomNo || '',
							price: data.price || 0,
							area: data.area || '',
							layout: data.layout || '',
							orientation: data.orientation || '',
							decoration: data.decoration || '',
							projectName: data.projectName || '',
							address: data.address || '',
							distance: '附近',  // TODO: 根据经纬度计算距离
							contactPhone: data.contactPhone || '',
							introduction: data.remark || ''  // 房源简介使用remark字段
						}

						// 保存项目ID
						if (data.projectId) {
							this.projectId = data.projectId
						}

						// 配套设施映射
						if (data.facilities && data.facilities.length > 0) {
							this.amenitiesList = this.mapFacilities(data.facilities)
						}

						console.log('房源详情加载成功:', this.roomInfo)
					} else {
						uni.showToast({
							title: response.msg || '获取房源详情失败',
							icon: 'none'
						})
					}
				} catch (error) {
					console.error('获取房源详情失败:', error)
					uni.showToast({
						title: '获取房源详情失败',
						icon: 'none'
					})
				} finally {
					uni.hideLoading()
				}
			},

			/**
			 * 设施映射 - 将后端设施名称映射为图标
			 */
			mapFacilities(facilities) {
				const facilityMap = {
					'洗衣机': { key: 'washer', name: '洗衣机', icon: '/static/fangyaun/洗衣机@2x.png' },
					'天然气': { key: 'gas', name: '天然气', icon: '/static/fangyaun/天燃气@2x.png' },
					'电视': { key: 'tv', name: '电视', icon: '/static/fangyaun/电视@2x.png' },
					'冰箱': { key: 'fridge', name: '冰箱', icon: '/static/fangyaun/冰箱@2x.png' },
					'暖气': { key: 'heating', name: '暖气', icon: '/static/fangyaun/暖气@2x.png' },
					'空调': { key: 'ac', name: '空调', icon: '/static/fangyaun/暖气@2x.png' },
					'热水器': { key: 'heater', name: '热水器', icon: '/static/fangyaun/暖气@2x.png' },
					'床': { key: 'bed', name: '床', icon: '/static/fangyaun/暖气@2x.png' },
					'衣柜': { key: 'wardrobe', name: '衣柜', icon: '/static/fangyaun/暖气@2x.png' }
				}

				return facilities.map(name => {
					return facilityMap[name] || { key: name, name: name, icon: '/static/fangyaun/暖气@2x.png' }
				})
			},

			/** 获取图片完整URL - 遵循RuoYi标准 */
			getImageUrl(url) {
				if (!url) return ''

				// 外部链接(http/https开头),直接返回
				if (url.startsWith('http://') || url.startsWith('https://')) {
					return url
				}

				// RuoYi标准:数据库存储相对路径,前端拼接baseUrl
				const baseUrl = config.staticUrl

				// 如果已包含baseUrl,直接返回
				if (url.indexOf(baseUrl) !== -1) {
					return url
				}

				// 拼接baseUrl + 相对路径
				return baseUrl + (url.startsWith('/') ? url : '/' + url)
			}
		}
	}
</script>

<style scoped>
	.page {
		min-height: 95vh;
		background: #f5f6fc;

	}
	
	/* 顶部图片区域 */
	.header-section {
		width: 750rpx;
		height: 295rpx;
		position: relative;
		overflow: hidden;
	}
	
	.header-bg {
		width: 100%;
		height: 100%;
		filter: blur(2rpx);
	}
	
	.header-overlay {
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
	}
	
	.image-tabs {
		position: absolute;
		bottom: 80rpx;
		left: 24rpx;
		display: flex;
		justify-content: center;
		align-items: center;
padding: 5rpx 5rpx;
border-radius: 8rpx;
opacity: 1;
background: #00000066;
backdrop-filter: blur(4rpx);
	}
	
	.image-tab-item {
		padding: 0rpx 16rpx;
		border-radius: 5rpx;
		height: 34rpx;
		line-height: 34rpx;
	}
	
	.image-tab-item.active {
		background: linear-gradient(0deg, #38a9ff 0%, #0f73ff 100%);
		backdrop-filter: blur(6rpx);
	}
	
	.image-tab-text {
		height: 34rpx;
		color: rgba(255, 255, 255, 0.8);
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 34rpx;
	}
	
	.image-tab-text.active {
		color: #ffffff;
	}
	
	.view-all-btn {
		position: absolute;
		bottom: 80rpx;
		right: 24rpx;
		width: 126rpx;
		height: 42rpx;
		border-radius: 8rpx;
		background: rgba(0, 0, 0, 0.4);
		backdrop-filter: blur(4rpx);
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 4rpx;
	}
	
	.view-all-text {
		color: rgba(255, 255, 255, 0.8);
		font-size: 22rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
	}
	
	.view-all-arrow {
		width: 16rpx;
		height: 16rpx;
	}
	
	/* 房源信息卡片 */
	.room-info-card {
		width: 702rpx;
		border-radius: 20rpx;
		background: #ffffff;
		margin: -60rpx auto 24rpx;
		padding: 28rpx;
		box-sizing: border-box;
		position: relative;
		z-index: 1;
	}
	
	.room-info-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin-bottom: 24rpx;
	}
	
	.room-title {
		height: 45rpx;
		color: #1a1a1a;
		font-size: 32rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 45rpx;
	}
	
	.room-price {
		display: flex;
		align-items: baseline;
	}
	
	.price-number {
		height: 48rpx;
		color: #e5252b;
		font-size: 44rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 48rpx;
	}
	
	.price-unit {
		height: 40rpx;
		color: #e5252b;
		font-size: 28rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
	}
	
	/* 房源详细信息卡片 */
	.room-detail-card {
		width: 646rpx;
		height: 132rpx;
		border-radius: 10rpx;
		background: #f7f9fa;
		display: flex;
		align-items: center;
		justify-content: space-around;
	}
	
	.room-detail-item {
		display: flex;
		flex-direction: column;
		align-items: center;
	}
	
	.detail-value {
		display: flex;
		align-items: baseline;
		justify-content: center;
	}
	
	.detail-number {
		height: 42rpx;
		color: #333333;
		font-size: 30rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 42rpx;
	}
	
	.detail-unit {
		height: 42rpx;
		color: #333333;
		font-size: 26rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 42rpx;
	}
	
	.detail-value-text {
		height: 42rpx;
		color: #333333;
		font-size: 30rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 42rpx;
	}
	
	.detail-label {
		height: 31rpx;
		color: #888888;
		font-size: 22rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 31rpx;
		margin-top: 8rpx;
	}
	
	/* 项目信息卡片 */
	.project-card {
		width: 702rpx;
		border-radius: 20rpx;
		background: #ffffff;
		margin: 0 auto 24rpx;
		padding: 28rpx;
		box-sizing: border-box;
	}
	
	.project-content {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}
	
	.project-left {
		flex: 1;
	}
	
	.project-header {
		display: flex;
		align-items: center;
		margin-bottom: 6rpx;
	}
	
	.project-icon {
		width: 27rpx;
		height: 27rpx;
		margin-right: 12rpx;
	}
	
	.project-name {
		height: 45rpx;
		color: #1a1a1a;
		font-size: 32rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 45rpx;
	}
	
	.project-location {
		display: flex;
		align-items: center;
	}
	
	.location-icon {
		width: 27rpx;
		height: 27rpx;
		margin-right: 12rpx;
	}
	
	.location-text {
		flex: 1;
		height: 34rpx;
		color: #666666;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 34rpx;
	}
	
	.location-distance {
		height: 34rpx;
		color: #333333;
		font-size: 20rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 34rpx;
	}
	
	.nav-wrapper {
		display: flex;
		flex-direction: column;
		align-items: center;
	}
	
	.nav-icon {
		width: 48rpx;
		height: 48rpx;
	}
	
	/* 房源简介卡片 */
	.amenities-card {
		width: 702rpx;
		border-radius: 20rpx;
		background: #ffffff;
		margin: 0 auto 24rpx;
		padding: 28rpx;
		box-sizing: border-box;
	}
	
	.card-title {
		width: 128rpx;
		height: 45rpx;
		color: #1a1a1a;
		font-size: 32rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 45rpx;
		display: block;
		margin-bottom: 38rpx;
	}
	
	.amenities-list {
		display: flex;
		flex-wrap: wrap;
		gap: 40rpx;
		justify-content: space-around;
	}
	
	.amenity-item {
		display: flex;
		flex-direction: column;
		align-items: center;
	}
	
	.amenity-icon {
		width: 47rpx;
		height: 54rpx;
		margin-bottom: 18rpx;
	}
	
	.amenity-text {
		height: 34rpx;
		color: #333333;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 34rpx;
	}

	/* 房源简介内容 */
	.intro-content {
		padding: 10rpx 0;
	}

	.intro-text {
		color: #333333;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
		white-space: pre-wrap;
		word-break: break-all;
	}

	.intro-empty {
		padding: 40rpx 0;
		display: flex;
		justify-content: center;
		align-items: center;
	}

	.empty-text {
		color: #999999;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	/* 管家电话卡片 */
	.contact-card {
		width: 702rpx;
		border-radius: 20rpx;
		background: #ffffff;
		margin: 0 auto 24rpx;
		padding: 28rpx;
		box-sizing: border-box;
	}
	
	.contact-content {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}
	
	.contact-left {
		display: flex;
		flex-direction: column;
	}
	
	.contact-title {
		height: 45rpx;
		color: #1a1a1a;
		font-size: 32rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 45rpx;
		margin-bottom: 18rpx;
	}
	
	.contact-text {
		height: 37rpx;
		color: #333333;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 37rpx;
	}
	
	.call-btn {
		width: 133rpx;
		height: 60rpx;
		border-radius: 12rpx;
		border: 2rpx solid #0f73ff;
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 8rpx;
	}
	
	.call-icon {
		width: 21rpx;
		height: 24rpx;
	}
	
	.call-text {
		height: 34rpx;
		color: #0f73ff;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 34rpx;
	}
	
	/* 底部按钮 */
	.footer-btns {
		
		display: flex;
		padding: 189rpx 24rpx 24rpx 24rpx;
		gap: 22rpx;
		
		
	}
	
	.reserve-btn {
		width: 240rpx;
		height: 92rpx;
		border-radius: 20rpx;
		border: 2rpx solid #0f73ff;
		background: #ffffff;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.reserve-text {
		width: 144rpx;
		height: 51rpx;
		color: #0f73ff;
		font-size: 36rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 51rpx;
	}
	
	.select-btn {
		width: 440rpx;
		height: 92rpx;
		border-radius: 20rpx;
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
		backdrop-filter: blur(6rpx);
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.select-text {
		width: 144rpx;
		height: 51rpx;
		color: #ffffff;
		font-size: 36rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 51rpx;
	}
</style>

