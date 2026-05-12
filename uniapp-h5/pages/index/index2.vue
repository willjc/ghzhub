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
								<text class="status-text" :class="{ available: item.hasUnits }">{{ item.hasUnits ? '有房源' : '无房源' }}</text>
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
		background-image: url('data:image/png;base64,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');
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
		background-image: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJgAAAAYCAYAAAAGcjT5AAAAAXNSR0IArs4c6QAAAiRJREFUaEPtW81qFEEQ/qpn1uDBFV2ibyB4EkHBi+DFJxBfJJtrYJ8j4AOEPEDOeRHvmoOHRXGTna+k+md2A7mom6DwHYama6pquodv6qvuqTYsvH/8A68G4sgML8wwMwMBOACawRF9Ay36IW/3Q1blWb/ImdLGHmEfsupj1DN49tfsq17cDzmKj7DL9uEny+sz8ri25VW/jW/U3diV+cS1Nb7mJ8tiLH593m2M7fls4wu/7Z2UueU+HO43yMMvrbyHrBf6BJmKXZPTq98mq3ZhH3pMVdfhYTNEW2Vxz1j6ozwVm7DvBnDdlbbrwKEDE+Hbbd+D/Rq8JHzSg5MrcD0B+wF+da+0lyvw/lNwtQIna/jP7+DeDL5cgs8eYPj2GTz9GHM0t+ncX6eETwCeV0CVFyCA5Q9GAPs9gO0/AS++gu/OwcXCaI/mfuKGDy3S5K9SABsjtwD2ZwDbv4BHFLOHh/7FgJkAJooMqvxbimwRbAOwuY85gShSOZgApiS/LAj+0SRfEUyryFtdRQpgApgApn2wspJvVBj7Z//LPpgimCKYIpgimCJY2enXr6LrO/36VURRpChSFCmKFEWKIlVNcWM1hShSFCmKFEWKIkWRokhRZK3EbZWwqmi9g4pW5WDKwe44B1PBYYlsqsnPtfm7rwc79DMD3quiVRWttwKw6YG/SR2OdehDANslwMZDH3FsbbrES+twYI63OramY2u7PLb2CxPHLSJVy5bjAAAAAElFTkSuQmCC');
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