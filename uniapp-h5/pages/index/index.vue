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
					<select class="form-input form-select" v-model="formData.identity">
						<option value="" disabled>请选择您的身份</option>
						<option value="1">在职人员</option>
						<option value="2">应届毕业生</option>
					</select>
				</view>					</view>

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
					<input class="form-input" placeholder="请输入联系电话" v-model="formData.phone" type="number" maxlength="11" />
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
					<text class="notice-label">最新通知：</text>
					<view class="notice-scroll-container">
						<view class="notice-scroll-content">
							<text class="notice-text">{{ noticeText || '暂无通知' }}</text>
						</view>
					</view>
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
	import { getProjectListByType } from '@/api/project'
	import { getBannerList, getLatestNotice } from '@/api/config'
	import { updateUserInfo } from '@/api/auth'
	import { BASE_URL } from '@/utils/request'

	export default {
		data() {
			return {
				noticeText: '', // 最新通知内容
				showModal: false, // 弹窗显示状态
				formData: { // 弹窗表单数据
					identity: '',
					name: '',
					idCard: '',
					phone: '', // 联系电话
					workUnit: '',
					workPhone: '',
					spouse: ''
				},
				identityTypes: [ // 身份类型选项
					{ label: '在职人员', value: '1' },
					{ label: '应届毕业生', value: '2' }
				],
				selectedIdentityIndex: -1, // 选中的身份索引
				currentBannerIndex: 0, // 当前轮播图索引
				bannerList: [], // 轮播图列表（从API加载）
				activeCategory: 'talent',
				activeSubTab: 'project',
				loading: false, // 加载状态
				categoryTabs: [
					{ key: 'talent', name: '人才公寓', type: '1' },
					{ key: 'guaranteed', name: '保租房', type: '2' },
					{ key: 'market', name: '市场租赁', type: '3' }
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
				listingData: [] // 项目列表数据（从API加载）
			}
		},
		onLoad() {
			// 检查登录状态
			const token = uni.getStorageSync('token')
			if (!token) {
				// 未登录，跳转登录页
				uni.reLaunch({
					url: '/pages/login/index'
				})
				return
			}

			// 检查用户是否已填写个人信息
			const userInfo = uni.getStorageSync('userInfo')
			if (userInfo && userInfo.isInfoCompleted !== '1') {
				// 首次登录且未填写信息，显示弹窗
				this.showModal = true
				// 联系电话默认为登录手机号
				this.formData.phone = userInfo.phone
			} else {
				this.showModal = false
			}

			// 获取人才公寓项目列表
			this.loadProjectList()

			// 加载轮播图
			this.loadBanners()

			// 加载最新通知
			this.loadLatestNotice()

		},
		methods: {
			/**
			 * 关闭弹窗
			 */
			closeModal() {
				this.showModal = false
			},

			/**
			 * 选择身份类型
			 */
			onIdentityChange(e) {
				const index = e.detail.value
				this.selectedIdentityIndex = index
				this.formData.identity = this.identityTypes[index].value
			},

			/**
			 * 获取身份类型标签
			 */
			getIdentityLabel() {
				if (!this.formData.identity) return ''
				const type = this.identityTypes.find(item => item.value === this.formData.identity)
				return type ? type.label : ''
			},

			/**
			 * 确认提交表单
			 */
			async handleConfirm() {
				// 验证表单
				if (!this.formData.identity || !this.formData.name || !this.formData.idCard ||
					!this.formData.phone || !this.formData.workUnit || !this.formData.workPhone) {
					uni.showToast({
						title: '请填写必填项',
						icon: 'none'
					})
					return
				}

				try {
					uni.showLoading({ title: '提交中...' })

					// 获取用户信息
					const userInfo = uni.getStorageSync('userInfo') || {}

					// 调用后端API提交表单
					await updateUserInfo({
						userId: userInfo.userId, // 传递userId而不是phone
						contactPhone: this.formData.phone, // 联系电话
						identityType: this.formData.identity,
						realName: this.formData.name,
						idCard: this.formData.idCard,
						workUnit: this.formData.workUnit,
						unitContact: this.formData.workPhone,
						spouseName: this.formData.spouse
					})











					uni.hideLoading()

					// 更新本地用户信息
					// userInfo已在上方声明
					userInfo.isInfoCompleted = '1'
					userInfo.identityType = this.formData.identity
					userInfo.realName = this.formData.name
					userInfo.idCard = this.formData.idCard
				userInfo.contactPhone = this.formData.phone // 更新联系电话
					userInfo.workUnit = this.formData.workUnit
					userInfo.unitContact = this.formData.workPhone
					userInfo.spouseName = this.formData.spouse
					uni.setStorageSync('userInfo', userInfo)

					uni.showToast({
						title: '提交成功',
						icon: 'success'
					})

					setTimeout(() => {
						this.showModal = false
					}, 1000)
				} catch (error) {
					uni.hideLoading()
					console.error('提交表单失败:', error)
					uni.showToast({
						title: error.msg || '提交失败',
						icon: 'none'
					})
				}
			},

			/**
			 * Banner轮播图切换事件
			 */
			onBannerChange(e) {
				this.currentBannerIndex = e.detail.current
			},

			/**
			 * 加载项目列表
			 */
			
			async loadProjectList() {
				// 只在项目标签下加载数据
				if (this.activeSubTab !== 'project') {
					return
				}

				// 获取当前选中分类对应的项目类型
				const currentCategory = this.categoryTabs.find(item => item.key === this.activeCategory)
				if (!currentCategory) {
					return
				}

				this.loading = true

				try {
					const response = await getProjectListByType(currentCategory.type)

					// 若依框架响应格式：{ code: 200, msg: 'success', data: [...] }
					const projectList = response.data || []

					// 转换数据格式
					this.listingData = projectList.map(project => this.transformProjectData(project))

					console.log(`加载${currentCategory.name}项目列表成功:`, this.listingData)
				} catch (error) {
					console.error('加载项目列表失败:', error)
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
					distance: '附近', // 距离（暂时固定值，后续可根据经纬度计算）
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

			handleSearch() {
				// 跳转到所有房源页面
				uni.navigateTo({
					url: '/pages/house/all'
				})
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
				}else if (item.key === 'guaranteed') {
					// 保租房跳转
					uni.navigateTo({
						url: '/pages/rental/index'
					})
				} else if (item.key === 'market') {
					// 市场租赁跳转
					uni.navigateTo({
						url: '/pages/market/index'
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
				} else if (item.key === 'map') {
					// 地图找房跳转
					uni.navigateTo({
						url: '/pages/map/index'
					})
				} else {
					console.log('点击图标:', item.name)
				}
			},
			switchCategory(key) {
				this.activeCategory = key
				// 切换分类时重新加载项目列表
				this.loadProjectList()
			},
			switchSubTab(key) {
				this.activeSubTab = key
				// 切换到项目标签时加载数据
				if (key === 'project') {
					this.loadProjectList()
				}
			},
			goToLatest() {
				console.log('查看最新房源')
			},
			goToMore() {
				console.log('查看更多')
			},
			goToDetail(item) {
				// 跳转到项目详情页面
				uni.navigateTo({
					url: `/pages/project/detail?id=${item.projectId}`
				})
			},

			/**
			 * 加载轮播图列表
			 */
			async loadBanners() {
				try {
					const response = await getBannerList()
					if (response.code === 200 && response.data) {
						this.bannerList = response.data.map(item => ({
							configId: item.configId,
							title: item.title,
							image: this.getImageUrl(item.imageUrl),
							linkUrl: item.linkUrl,
							linkType: item.linkType
						}))
						console.log('轮播图加载成功:', this.bannerList)
					}
				} catch (error) {
					console.error('加载轮播图失败:', error)
					// 失败时使用默认图片
					this.bannerList = [
						{ image: '/static/banner@2x.png' }
					]
				}
			},

			/**
			 * 加载最新通知
			 */
			async loadLatestNotice() {
				try {
					const response = await getLatestNotice()
					if (response.code === 200 && response.msg) {
						// 去除 HTML 标签，只保留纯文本
						this.noticeText = this.stripHtmlTags(response.msg)
					}
				} catch (error) {
					console.error('加载最新通知失败:', error)
					this.noticeText = '暂无通知'
				}
			},

			/**
			 * 去除 HTML 标签
			 * @param {String} html 包含 HTML 标签的字符串
			 * @returns {String} 纯文本
			 */
			stripHtmlTags(html) {
				if (!html) return ''
				// 创建临时 DOM 元素提取纯文本
				const temp = document.createElement('div')
				temp.innerHTML = html
				return temp.textContent || temp.innerText || ''
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
		flex-shrink: 0;
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
		flex-shrink: 0;
	}

	/* 滚动容器 */
	.notice-scroll-container {
		flex: 1;
		overflow: hidden;
		white-space: nowrap;
	}

	/* 滚动内容 */
	.notice-scroll-content {
		display: inline-block;
		white-space: nowrap;
		padding-left: 100%;
		animation: scroll-left 20s linear infinite;
	}

	/* 从右到左滚动动画 */
	@keyframes scroll-left {
		0% {
		transform: translateX(0);
	}
		100% {
		transform: translateX(-100%);
	}
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
		z-index: 100;
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
		width: 340rpx;
		max-width: 340rpx;
	}

	/* Picker组件样式控制 */
	.form-input-wrapper picker,
	.form-input-wrapper .uni-picker {
		width: 100%;
		max-width: 340rpx;
	}

	/* H5环境下控制picker生成的select元素 */
	.form-input-wrapper ::v-deep select {
		max-width: 340rpx !important;
		width: 340rpx !important;
		box-sizing: border-box !important;
	}

	/* 控制下拉选项列表 */
	.form-input-wrapper ::v-deep .uni-picker-dropdown,
	.form-input-wrapper ::v-deep .uni-picker-list,
	.form-input-wrapper ::v-deep .uni-picker-options {
		max-width: 340rpx !important;
		width: 340rpx !important;
		box-sizing: border-box !important;
	}

	/* 控制每个下拉选项 */
	.form-input-wrapper ::v-deep option {
		max-width: 340rpx !important;
		width: 100% !important;
		overflow: hidden !important;
		text-overflow: ellipsis !important;
		white-space: nowrap !important;
	}

	.picker-input {
		display: flex;
		align-items: center;
		justify-content: space-between;
		width: 100%;
		max-width: 100%;
		box-sizing: border-box;
		overflow: hidden;
	}

	.picker-input .placeholder {
		color: #b3b3b3;
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

	/* select下拉选择框样式 */
	.form-select {
		width: 340rpx !important;
		max-width: 340rpx !important;
		padding: 0 20rpx !important;
		appearance: none;
		-webkit-appearance: none;
		-moz-appearance: none;
		background-color: #ffffff;
		cursor: pointer;
	}

	.form-select option {
		color: #333333;
		background-color: #ffffff;
		max-width: 340rpx !important;
	}

	.picker-input.form-input {
		width: 100%;
		max-width: 340rpx;
		padding-right: 50rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
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

<!-- 全局样式：控制picker下拉菜单宽度 -->
<style>
/* 强制限制picker下拉菜单宽度，防止超出弹窗 */
.uni-picker-container,
.uni-picker-dropdown,
uni-picker-view {
	max-width: 340rpx !important;
	width: 340rpx !important;
	box-sizing: border-box !important;
}

/* H5环境下的select下拉菜单 */
select {
	max-width: 340rpx !important;
	box-sizing: border-box !important;
}

/* 下拉选项 */
select option {
	max-width: 340rpx !important;
	width: 100% !important;
	overflow: hidden !important;
	text-overflow: ellipsis !important;
	white-space: nowrap !important;
	box-sizing: border-box !important;
	padding: 10rpx !important;
}
</style>