<template>
	<view class="page">
		<!-- 顶部图片 -->
		<image class="header-image" src="/static/项目图片@2x.png" mode="aspectFill"></image>
		
		<!-- 项目信息卡片 -->
		<view class="info-card">
			<!-- 标题和导航 -->
			<view class="title-row">
				<!-- 项目名称 -->
				 <view class="project-name-wrapper">
				<text class="project-title">{{ projectInfo.projectName }}</text>
				<!-- 地址 -->
				<text class="project-address">{{ projectInfo.address }}</text>
				</view>
				<view class="nav-wrapper" @click="openNavigation">
					<image class="nav-icon" src="/static/导航@2x.png" mode="aspectFit"></image>
					<text class="nav-distance">{{ projectInfo.distance }}</text>
				</view>
			</view>
			
			
			
			
			
			<!-- 分隔线 -->
			<view class="divider"></view>
			
			<!-- 项目详情列表 -->
			<view class="detail-list">

			<view class="detail-item">
					<text class="detail-label">·房源：</text>
					<text class="detail-value">{{ projectInfo.totalHouses }}套</text>
				</view>
				<view class="detail-item">
					<text class="detail-label">·分布：</text>
					<text class="detail-value">{{ projectInfo.distribution || '58号楼1-11层/63号楼1-11层' }}</text>
				</view>
				<view class="detail-item">
					<text class="detail-label">·户型：</text>
					<text class="detail-value">{{ projectInfo.houseType || '一室一厅/二室二厅/三室三厅' }}</text>
				</view>
				<view class="detail-item">
					<text class="detail-label">·面积：</text>
					<text class="detail-value">{{ projectInfo.area || '60平/90平/120平' }}</text>
				</view>
				<view class="detail-item">
					<text class="detail-label">·租金：</text>
					<text class="detail-value rent-value">{{ projectInfo.rent || '480-490元/间/月（60平）\n652-665元/间/月（90平）\n823-840元/间/月（120平）' }}</text>
				</view>
				<view class="detail-item">
					<text class="detail-label">·物业公司：</text>
					<text class="detail-value">{{ projectInfo.propertyCompany || '郑州航空港区合达物业服务有限公司' }}</text>
				</view>
				<view class="detail-item">
					<text class="detail-label">·物业费：</text>
					<text class="detail-value">{{ projectInfo.propertyFee || '2.5元/平方米/月' }}</text>
				</view>
				<view class="detail-item">
					<text class="detail-label">·电费：</text>
					<text class="detail-value">{{ projectInfo.electricFee || '0.6元/度（阶梯电价）' }}</text>
				</view>
				<view class="detail-item">
					<text class="detail-label">·水费：</text>
					<text class="detail-value">{{ projectInfo.waterFee || '4.5元/吨' }}</text>
				</view>
				<view class="detail-item">
					<text class="detail-label">·燃气：</text>
					<text class="detail-value">{{ projectInfo.gasFee || '2.58元/立方' }}</text>
				</view>
			</view>
			
			
			
		</view>
		<view class="info-card1" >
		<!-- 项目简介 -->
		<view class="intro-section">
				<text class="intro-title">项目简介</text>
				<view class="intro-content">{{ projectInfo.description || '项目配备独立卫浴、床、衣柜、空调、冰箱、洗衣机、热水器、电视、书桌、椅子、晾衣架等，拎包入住。周边有生活超市、郑州一中和公交车站等完善的生活配套。' }}</view>
			</view>
		</view>
		<!-- 底部按钮 -->
		<view class="footer-btn" @click="selectRoom">
			<text class="btn-text">选择房源</text>
		</view>
	</view>
</template>

<script>
import { getProjectDetail } from '@/api/project'

export default {
	data() {
		return {
			projectId: '',
			projectInfo: {
				projectName: '',
				address: '',
				distance: '',
				totalHouses: 0,
				distribution: '',
				houseType: '',
				area: '',
				rent: '',
				propertyCompany: '',
				propertyFee: '',
				electricFee: '',
				waterFee: '',
				gasFee: '',
				description: ''
			}
		}
	},
	onLoad(options) {
		if (options.id) {
			this.projectId = options.id
			this.loadProjectDetail()
		}
	},
	methods: {
		async loadProjectDetail() {
			try {
				uni.showLoading({ title: '加载中...' })
				const response = await getProjectDetail(this.projectId)

				if (response.code === 200 && response.data) {
					const project = response.data

					// 映射后端数据到前端字段
					this.projectInfo = {
						projectName: project.projectName || '未命名项目',
						address: project.address || '地址未填写',
						distance: this.calculateDistance(project.latitude, project.longitude),
						totalHouses: project.totalHouses || 0,
						distribution: project.distribution || '',
						houseType: project.houseType || '',
						area: project.area || '',
						rent: project.rentDetail || '',
						propertyCompany: project.propertyCompany || '',
						propertyFee: project.propertyFee || '',
						electricFee: project.electricFee || '',
						waterFee: project.waterFee || '',
						gasFee: project.gasFee || '',
						description: project.projectIntro || ''
					}

					console.log('项目详情加载成功:', this.projectInfo)
				} else {
					uni.showToast({
						title: response.msg || '获取项目详情失败',
						icon: 'none'
					})
				}
			} catch (error) {
				console.error('获取项目详情失败:', error)
				uni.showToast({
					title: '获取项目详情失败',
					icon: 'none'
				})
			} finally {
				uni.hideLoading()
			}
		},

		/**
		 * 计算距离（暂时返回固定值，后续可根据经纬度计算）
		 */
		calculateDistance(latitude, longitude) {
			if (latitude && longitude) {
				// TODO: 根据用户位置和项目经纬度计算实际距离
				return '附近'
			}
			return '附近'
		},

		openNavigation() {
			// 打开地图导航
			uni.openLocation({
				latitude: 34.5,
				longitude: 113.8,
				name: this.projectInfo.projectName,
				address: this.projectInfo.address,
				fail: (err) => {
					console.error('打开导航失败:', err)
					uni.showToast({
						title: '打开导航失败',
						icon: 'none'
					})
				}
			})
		},
		selectRoom() {
			// 先跳转到承诺书签署页面
			uni.navigateTo({
				url: `/pages/commitment/sign?projectId=${this.projectId}`
			})
		}
	}
}
</script>

<style scoped>
	.page {
		min-height: 100vh;
		background: #f5f6fc;
		padding-bottom: 140rpx;
	}
	
	.header-image {
		width: 100%;
		height: 280rpx;
		display: block;
	}
	
	.info-card {
		width: 702rpx;
		margin: -58rpx auto 0;
		border-radius: 20rpx;
		background: #ffffff;
		padding: 28rpx 33rpx 26rpx 28rpx;
		box-sizing: border-box;
		position: relative;
		z-index: 1;
	}
	.info-card1 {
		width: 702rpx;
		margin: 26rpx auto 0;
		border-radius: 20rpx;
		background: #ffffff;
		padding: 28rpx;
		box-sizing: border-box;
		position: relative;
		z-index: 1;
	}
	
	
	.title-row {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}
	
	.project-title {
		width: 320rpx;
		height: 45rpx;
		color: #1a1a1a;
		font-size: 32rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 45rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
		margin-bottom: 6rpx;
	}
	
	.nav-wrapper {
		display: flex;
		flex-direction: column;
		align-items: center;
	}
	.project-name-wrapper{
		display: flex;
		flex-direction: column;
		
	}
	
	.nav-icon {
		width: 48rpx;
		height: 48rpx;
		border-radius: 16rpx;
	}
	
	.nav-distance {
		width: 57rpx;
		height: 34rpx;
		color: #333333;
		font-size: 20rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 34rpx;
		margin-top: 4rpx;
	}
	
	.project-address {
		width: 100%;
		height: 34rpx;
		color: #666666;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 34rpx;
		margin-bottom: 16rpx;
	}
	
	.room-info {
		display: flex;
		align-items: center;
		margin-bottom: 30rpx;
	}
	
	.room-label {
		height: 37rpx;
		color: #999999;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 37rpx;
	}
	
	.room-count {
		height: 37rpx;
		color: #333333;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 37rpx;
	}
	
	.divider {
		width: 100%;
		height: 1rpx;
		background: #f0f0f0;
		margin-bottom: 8rpx;
	}
	
	.detail-list {
		padding: 10rpx 0;
	}
	
	.detail-item {
		display: flex;
		margin-bottom: 16rpx;
	}
	
	.detail-label {
		width: 146rpx;
		color: #999999;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 37rpx;
		flex-shrink: 0;
	}
	
	.detail-value {
		flex: 1;
		color: #333333;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 37rpx;
		white-space: pre-wrap;
	}
	
	.rent-value {
		color: #333333;
	}
	
	.intro-section {
		padding-top: 10rpx;
	}
	
	.intro-title {
		width: 128rpx;
		height: 45rpx;
		color: #1a1a1a;
		font-size: 32rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 45rpx;
		display: block;
		margin-bottom: 18rpx;
	}
	
	.intro-content {
		width: 622rpx;
		color: #333333;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
		margin-left: 26rpx;
	}
	
	.footer-btn {
		position: fixed;
		bottom: 40rpx;
		left: 50%;
		transform: translateX(-50%);
		width: 702rpx;
		height: 92rpx;
		border-radius: 20rpx;
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
		backdrop-filter: blur(6rpx);
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.btn-text {
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

