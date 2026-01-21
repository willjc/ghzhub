<template>
	<view class="page">
		<!-- 个人信息弹窗 -->
		<view class="modal-overlay" v-if="showModal" @click="closeModal">
			<view class="modal-content" @click.stop>
				<!-- 顶部背景 -->
				<view class="modal-header">
					<image class="modal-header-bg" src="/static/弹窗bg@2x.png" mode="aspectFill"></image>
					<text class="modal-title">个人信息</text>
				</view>
				
				<!-- 表单内容 -->
				<view class="modal-body">
					<!-- 选择身份 -->
					<view class="form-item">
						<view class="form-label">
							<text class="required-mark">*</text>
							<text class="label-text">选择身份</text>
						</view>
						<view class="form-input-wrapper">
							<input class="form-input" placeholder="请选择您的身份" v-model="formData.identity" />
							<view class="input-icon-wrapper">
								<image class="input-icon" src="/static/向下@2x.png"></image>
							</view>
						</view>
					</view>
					
					<!-- 姓名 -->
					<view class="form-item">
						<view class="form-label">
							<text class="required-mark">*</text>
							<text class="label-text">姓名</text>
						</view>
						<input class="form-input" placeholder="请输入姓名" v-model="formData.name" />
					</view>
					
					<!-- 身份证号 -->
					<view class="form-item">
						<view class="form-label">
							<text class="required-mark">*</text>
							<text class="label-text">身份证号</text>
						</view>
						<input class="form-input" placeholder="请输入身份证号" v-model="formData.idCard" />
					</view>
					
					<!-- 联系电话 -->
					<view class="form-item">
						<view class="form-label">
							<text class="required-mark">*</text>
							<text class="label-text">联系电话</text>
						</view>
						<input class="form-input" placeholder="请输入联系电话" v-model="formData.phone" />
					</view>
					
					<!-- 工作单位 -->
					<view class="form-item">
						<view class="form-label">
							<text class="required-mark">*</text>
							<text class="label-text">工作单位</text>
						</view>
						<input class="form-input" placeholder="请输入工作单位" v-model="formData.workUnit" />
					</view>
					
					<!-- 单位联系电话 -->
					<view class="form-item">
						<view class="form-label">
							<text class="required-mark">*</text>
							<text class="label-text">单位联系电话</text>
						</view>
						<input class="form-input" placeholder="请输入单位联系电话" v-model="formData.workPhone" />
					</view>
					
					<!-- 配偶 -->
					<view class="form-item">
						<view class="form-label">
							<text class="label-text">配偶</text>
						</view>
						<input class="form-input" placeholder="请输入有/无" v-model="formData.spouse" />
					</view>
					
					<!-- 确定按钮 -->
					<view class="modal-footer">
						<view class="confirm-btn" @click="handleConfirm">
							<text class="confirm-btn-text">确定</text>
						</view>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 滚动内容区域 -->
		<scroll-view class="scroll-content" scroll-y>
			<!-- Hero Banner -->
			<view class="banner-section">
				<swiper class="banner-swiper" :indicator-dots="false" :autoplay="true" :interval="3000" :duration="500" @change="onBannerChange">
					<swiper-item v-for="(item, index) in bannerList" :key="index">
						<image class="banner-image" :src="item.image" mode="widthFix"></image>
					</swiper-item>
				</swiper>
				<!-- 自定义指示点 -->
				<view class="banner-indicators">
					<view 
						class="indicator-dot" 
						v-for="(item, index) in bannerList" 
						:key="index"
						:class="{ active: currentBannerIndex === index }"
					></view>
				</view>
				<!-- 搜索栏 -->
				<view class="search-section">
					<view class="search-bar" @click="handleSearch">
						<image class="search-icon" src="/static/画板 2@2x.png"></image>
						<text class="search-placeholder">搜索你想要的房源</text>
					</view>
				</view>
			</view>

			<!-- 功能图标网格 -->
			<view class="icon-grid">
				<view class="icon-item" v-for="(item, index) in iconList" :key="index" @click="handleIconClick(item)">
					<image class="icon-image" :src="item.icon"></image>
					<text class="icon-text">{{ item.name }}</text>
				</view>
			</view>

			<!-- 通知栏 -->
			<view class="notice-wrapper">
				<view class="notice-section"></view>
				<view class="notice-content">
					<image class="notice-icon" src="/static/通知@2x.png"></image>
					<scroll-view class="notice-text-scroll" scroll-x>
						<text class="notice-label">最新通知：</text>
						<text class="notice-text">这是一条最新消息通知，滚动展示...</text>
					</scroll-view>
				</view>
			</view>

			<!-- 房源列表区域 -->
			<view class="listings-section">
				<!-- 分类标签 -->
				<view class="category-tabs">
					<view 
						class="category-tab" 
						:class="{ active: activeCategory === item.key }"
						v-for="(item, index) in categoryTabs" 
						:key="index"
						@click="switchCategory(item.key)"
					>
						<text class="category-tab-text">{{ item.name }}</text>
						<view class="category-tab-indicator" v-if="activeCategory === item.key"></view>
					</view>
				</view>

				<!-- 子标签 -->
				<view class="sub-tabs">
					<view 
						class="sub-tab" 
						:class="{ active: activeSubTab === item.key }"
						v-for="(item, index) in subTabs" 
						:key="index"
						@click="switchSubTab(item.key)"
					>
						<text>{{ item.name }}</text>
					</view>
					<view class="more-link" @click="goToMore">
						<text class="more-text">更多</text>
						<image class="more-arrow" src="/static/更多@2x.png"></image>
					</view>
				</view>

				<!-- 房源卡片列表 -->
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
			</view>
		</scroll-view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				showModal: true,
				formData: {
					identity: '',
					name: '',
					idCard: '',
					phone: '',
					workUnit: '',
					workPhone: '',
					spouse: ''
				},
				currentBannerIndex: 0,
				bannerList: [
					{ image: '/static/banner@2x.png' },
					{ image: '/static/banner@2x.png' },
					{ image: '/static/banner@2x.png' }
				],
				activeCategory: 'talent',
				activeSubTab: 'project',
				categoryTabs: [
					{ key: 'talent', name: '人才公寓' },
					{ key: 'guaranteed', name: '保租房' },
					{ key: 'market', name: '市场租赁' }
				],
				subTabs: [
					{ key: 'project', name: '项目' },
					{ key: 'listing', name: '房源' }
				],
				iconList: [
					{ name: '人才公寓', icon: '/static/人才公寓@2x.png', key: 'talent' },
					{ name: '保租房', icon: '/static/保租房@2x.png', key: 'guaranteed' },
					{ name: '市场租赁', icon: '/static/市场租赁@2x.png', key: 'market' },
					{ name: '地图找房', icon: '/static/地图找房@2x.png', key: 'map' },
					{ name: '人才家园', icon: '/static/人才家园@2x.png', key: 'home' },
					{ name: '政策文件', icon: '/static/政策文件@2x.png', key: 'policy' },
					{ name: '资料上传', icon: '/static/资料上传@2x.png', key: 'upload' },
					{ name: '优惠券', icon: '/static/优惠券@2x.png', key: 'coupon' },
					{ name: '我的消息', icon: '/static/我的消息@2x.png', key: 'message' }
				],
				listingData: [
					{
						title: '航南新城专家公寓项目',
						hasUnits: true,
						totalUnits: 100,
						distance: '8.2km',
						address: '航空港区新港大道与遵大路交叉口',
						tags: ['商业街', '停车场', '运动场'],
						price: 2000,
						image: '/static/矩形 21@2x.png'
					},
					{
						title: '航南新城专家公寓项目',
						hasUnits: true,
						totalUnits: 100,
						distance: '8.2km',
						address: '航空港区新港大道与遵大路交叉口',
						tags: ['商业街', '停车场', '运动场'],
						price: 2000,
						image: '/static/矩形 21@2x.png'
					}
				]
			}
		},
		onLoad() {
			// 页面加载时显示弹窗
			this.showModal = true
		},
		methods: {
			closeModal() {
				this.showModal = false
			},
			handleConfirm() {
				// 验证表单
				if (!this.formData.identity || !this.formData.name || !this.formData.idCard || 
					!this.formData.phone || !this.formData.workUnit || !this.formData.workPhone) {
					uni.showToast({
						title: '请填写必填项',
						icon: 'none'
					})
					return
				}
				// 提交表单
				console.log('提交表单:', this.formData)
				this.showModal = false
			},
			onBannerChange(e) {
				this.currentBannerIndex = e.detail.current
			},
			handleSearch() {
				// 跳转到搜索页面
				console.log('搜索')
			},
			handleIconClick(item) {
				// 人才公寓跳转
				if (item.key === 'talent') {
					uni.navigateTo({
						url: '/pages/talent/index'
					})
				} else if (item.key === 'home') {
					// 人才家园跳转
					uni.navigateTo({
						url: '/pages/home/index'
					})
				} else if (item.key === 'coupon') {
					// 优惠券跳转
					uni.navigateTo({
						url: '/pages/coupon/index'
					})
				} else if (item.key === 'upload') {
					// 资料上传跳转
					uni.navigateTo({
						url: '/pages/upload/index'
					})
				} else if (item.key === 'message') {
					// 我的消息跳转
					uni.navigateTo({
						url: '/pages/message/index'
					})
				} else if (item.key === 'policy') {
					// 政策文件跳转
					uni.navigateTo({
						url: '/pages/policy/index'
					})
				} else {
					console.log('点击图标:', item.name)
				}
			},
			switchCategory(key) {
				this.activeCategory = key
			},
			switchSubTab(key) {
				this.activeSubTab = key
			},
			goToLatest() {
				console.log('查看最新房源')
			},
			goToMore() {
				console.log('查看更多')
			},
			goToDetail(item) {
				// 跳转到人才公寓页面
				uni.navigateTo({
					url: '/pages/talent/index'
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
	}

	/* 滚动内容 */
	.scroll-content {
		flex: 1;
		overflow-y: auto;
	}

	/* Banner区域 */
	.banner-section {
		position: relative;
		width: 100%;
		margin-bottom: 20rpx;
	}

	.banner-swiper {
		width: 100%;
		height: 250rpx;
	}

	.banner-image {
		width: 100%;
		display: block;
	}

	/* 自定义指示点 */
	.banner-indicators {
		position: absolute;
		bottom: 48rpx;
		right: 24rpx;
		display: flex;
		align-items: center;
		gap: 12rpx;
		z-index: 100;
	}

	.indicator-dot {
		width: 10rpx;
		height: 10rpx;
		opacity: 1;
		background: #00000080;
	
		border-radius: 50%;
	}

	.indicator-dot.active {
		background: #ffffff;
	}

	.banner-content {
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		padding: 0 40rpx;
	}

	.banner-title {
		font-size: 56rpx;
		color: #fff;
		font-weight: bold;
		margin-bottom: 40rpx;
		text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.2);
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.banner-btn {
		background: linear-gradient(135deg, #4A90E2 0%, #357ABD 100%);
		border-radius: 50rpx;
		padding: 20rpx 40rpx;
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.banner-btn-text {
		font-size: 28rpx;
		color: #fff;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.banner-btn-arrow {
		font-size: 24rpx;
		color: #fff;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	/* 搜索栏 */
	.search-section {
		position: absolute;
		bottom: -40rpx;
		left: 0;
		right: 0;
		padding: 0 24rpx;
		z-index: 10;
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
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
		margin: 0 auto;
		box-sizing: border-box;
	}

	.search-icon {
		width: 32rpx;
		height: 32rpx;
		margin-right: 20rpx;
	}

	.search-placeholder {
		font-size: 28rpx;
		color: #999;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	/* 图标网格 */
	.icon-grid {
		display: flex;
		flex-wrap: wrap;
		padding: 32rpx 0 0 0;
		height: 300rpx;
		border-radius: 20rpx;
		opacity: 1;
		background: #ffffff;
		margin-top: 64rpx;
		margin-bottom: 20rpx;
		margin-left: 24rpx;
		margin-right: 24rpx;
	}

	.icon-item {
		width: 20%;
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.icon-image {
		width: 60rpx;
		height: 60rpx;
		margin-bottom: 10rpx;
	}

	.icon-text {
		width: 96rpx;
		height: 40rpx;
		opacity: 1;
		color: #333333;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 40rpx;
	}

	/* 通知栏最底层白色容器 */
	.notice-wrapper {
		width: 702rpx;
		height: 90rpx;
		border-radius: 20rpx;
		opacity: 1;
		background: #ffffff;
		margin: 24rpx;
		position: relative;
		overflow: hidden;
	}

	/* 通知栏中间层背景图 */
	.notice-section {
		width: 432rpx;
		height: 84rpx;
		margin-left: 4rpx;
		border-radius: 18rpx;
		opacity: 1;
		background: linear-gradient(90deg, #ebf6ff 0%, #ebf6ff00 100%);
		background-image: url('/static/通知bg@2x.png');
		background-size: 432rpx 84rpx;
		background-position: center;
		background-repeat: no-repeat;
		position: absolute;
		left: 0;
		top: 50%;
		transform: translateY(-50%);
		z-index: 1;
	}

	/* 通知栏文字内容层 */
	.notice-content {
		width: 100%;
		height: 100%;
		display: flex;
		align-items: center;
		padding: 0 36rpx;
		position: relative;
		z-index: 2;
	}

	.notice-icon {
		width: 44rpx;
		height: 44rpx;
		margin-right: 16rpx;
	}

	.notice-text-scroll {
		flex: 1;
		white-space: nowrap;
		display: flex;
		align-items: center;
	}

	.notice-label {
		height: 40rpx;
		opacity: 1;
		color: #1281ff;
		font-size: 26rpx;
		font-weight: 500;
		text-align: left;
		line-height: 40rpx;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		display: inline-block;
	}

	.notice-text {
		height: 40rpx;
		opacity: 1;
		color: #333333;
		font-size: 26rpx;
		font-weight: normal;
		text-align: left;
		line-height: 40rpx;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		display: inline-block;
	}

	/* 房源列表区域 */
	.listings-section {
		padding: 16rpx 30rpx 30rpx;
	}

	.category-tabs {
		display: flex;
		margin-bottom: 20rpx;
	}

	.category-tab {
		position: relative;
		display: flex;
		flex-direction: column;
		align-items: flex-start;
		margin-right: 40rpx;
	}

	.category-tab-text {
		height: 40rpx;
		opacity: 1;
		color: #7e8394;
		font-size: 32rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
	}

	.category-tab.active .category-tab-text {
		
		height: 40rpx;
		opacity: 1;
		color: #1a1a1a;
		font-size: 40rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
	}

	.category-tab-indicator {
		width: 76rpx;
		height: 12rpx;
		border-radius: 2rpx;
		opacity: 1;
		background: linear-gradient(90deg, #0f73ff 0%, #4fc7ff00 100%);
		background-image: url('/static/选中@2x.png');
		background-size: 76rpx 12rpx;
		background-position: center;
		background-repeat: no-repeat;
		margin-top: -12rpx;
	}

	.sub-tabs {
		display: flex;
		align-items: center;
		margin-bottom: 30rpx;
	}

	.sub-tab {
		width: 120rpx;
		height: 56rpx;
		border-radius: 16rpx;
		opacity: 1;
		background: #ffffff;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 20rpx;
	}

	.sub-tab text {
		font-size: 26rpx;
		color: #666;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.sub-tab.active {
		background: linear-gradient(180deg, #0f73ff 0%, #1ca4ff 72%, #4fc7ff 99%);
		backdrop-filter: blur(6rpx);
	}

	.sub-tab.active text {
		color: #fff;
	}

	.more-link {
		margin-left: auto;
		display: flex;
		align-items: center;
		gap: 8rpx;
	}

	.more-text {
		width: 52rpx;
		height: 30rpx;
		opacity: 1;
		color: #73757d;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 30rpx;
	}

	.more-arrow {
		width: 24rpx;
		height: 24rpx;
		opacity: 1;
	}

	/* 房源卡片 */
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

		margin-left: 150rpx;
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
		width: 100rpx;
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

	/* 弹窗样式 */
	.modal-overlay {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background-color: rgba(0, 0, 0, 0.5);
		display: flex;
		align-items: center;
		justify-content: center;
		z-index: 1000;
	}

	.modal-content {
		width: 640rpx;
		height: 842rpx;
		border-radius: 32rpx;
		opacity: 1;
		background: #ffffff;
		display: flex;
		flex-direction: column;
		overflow: hidden;
		position: relative;
	}

	.modal-header {
		width: 640rpx;
		height: 186rpx;
		border-radius: 32rpx 32rpx 0 0;
		opacity: 1;
		position: relative;
		overflow: hidden;
		display: flex;
		
		justify-content: flex-start;
		padding-left: 30rpx;
	}

	.modal-header-bg {
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
	}

	.modal-title {
		width: 144rpx;
		height: 51rpx;
		opacity: 1;
		color: #000000;
		font-size: 36rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		padding-top: 42rpx;
		padding-left: 10rpx;
		line-height: 51rpx;
		position: relative;
		z-index: 1;
	}

	.modal-body {
		flex: 1;
		padding: 0rpx 36rpx 36rpx ;
		overflow-y: auto;
		box-sizing: border-box;
		margin-top: -60rpx;
		position: relative;
		z-index: 1;
	}

	.form-item {
		display: flex;
		margin-bottom: 30rpx;
	}

	.form-label {
		width: 217rpx;
		display: flex;
		align-items: center;
		margin-bottom: 12rpx;
	}

	.required-mark {
		width: 127rpx;
		height: 40rpx;
		opacity: 1;
		color: #ff0000;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
		width: auto;
		margin-right: 4rpx;
	}

	.label-text {
		width: 217rpx;
		height: 40rpx;
		opacity: 1;
		color: #333333;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
	}

	.form-input-wrapper {
		position: relative;
		display: flex;
		align-items: center;
	}

	.form-input {
		width: 340rpx;
		height: 54rpx;
		border-radius: 4rpx;
		opacity: 1;
		border: 1.4rpx solid #e1eaf2;
		padding: 0 50rpx 0 20rpx;
		box-sizing: border-box;
		font-size: 28rpx;
		color: #333333;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.input-icon-wrapper {
		position: absolute;
		right: 12rpx;
		width: 24rpx;
		height: 24rpx;
		opacity: 1;
		background: #f1f5fa;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.input-icon {
		width: 24rpx;
		height: 24rpx;
	}

	.modal-footer {
		margin-top: 40rpx;
		display: flex;
		justify-content: center;
	}

	.confirm-btn {
		width: 550rpx;
		height: 90rpx;
		border-radius: 20rpx;
		opacity: 1;
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
		backdrop-filter: blur(6rpx);
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.confirm-btn-text {
		color: #ffffff;
		font-size: 32rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}
</style>