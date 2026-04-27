<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 顶部欢迎区域 -->
			<view class="header-section">
				<image class="header-bg" src="/static/fuwu/顶部背景@2x.png" mode="widthFix"></image>
				
			</view>

			<!-- 服务网格 -->
			<view class="service-grid">
				<view 
					class="service-item" 
					v-for="(item, index) in serviceList" 
					:key="index"
					@click="handleServiceClick(item.key)"
				>
					<view class="service-card">
						<image class="service-bg" src="/static/fuwu/卡片bg@2x.png" mode="aspectFill"></image>
						<view class="service-content">
							
							<text class="service-text">{{ item.name }}</text>
							<image class="service-icon" :src="item.icon"></image>
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
				serviceList: [
					{ key: 'complaint', name: '投诉与建议', icon: '/static/fuwu/投诉及建议@2x.png' },
					// { key: 'cleaning', name: '保洁服务', icon: '/static/fuwu/保洁@2x.png' },
					// { key: 'moving', name: '搬家服务', icon: '/static/fuwu/搬家@2x.png' },
					{ key: 'repair', name: '物业报修', icon: '/static/fuwu/物业报修@2x.png' },
					{ key: 'enterprise', name: '企业客户', icon: '/static/fuwu/企业客户@2x.png' }
					// { key: 'face', name: '人脸门禁', icon: '/static/fuwu/人脸门禁@2x.png' },
					// { key: 'contract', name: '合同备案', icon: '/static/fuwu/合同@2x.png' },
					// { key: 'purchase', name: '代购补贴', icon: '/static/fuwu/代购@2x.png' }
				]
			}
		},
		onLoad() {
			
		},
		methods: {
			handleServiceClick(key) {
				console.log('点击服务:', key)
				switch (key) {
					case 'complaint':
						uni.navigateTo({
							url: '/subpkg/service/complaint'
						})
						break
					case 'repair':
						uni.navigateTo({
							url: '/subpkg/service/repair'
						})
						break
					case 'enterprise':
						uni.navigateTo({
							url: '/subpkg/service/enterprise'
						})
						break
					default:
						uni.showToast({
							title: '功能开发中',
							icon: 'none'
						})
				}
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

	.scroll-content {
		flex: 1;
		overflow-y: auto;
	}

	/* 顶部欢迎区域 */
	.header-section {
		position: relative;
		width: 100%;
	}

	.header-bg {
		width: 100%;
		display: block;
	}

	.header-content {
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		display: flex;
		flex-direction: column;
		justify-content: center;
		padding: 60rpx 30rpx 40rpx;
	}

	.welcome-text {
		font-size: 48rpx;
		color: #1a1a1a;
		font-weight: bold;
		margin-bottom: 40rpx;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.features {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.feature-item {
		display: flex;
		align-items: center;
	}

	.feature-check {
		font-size: 32rpx;
		color: #4CAF50;
		margin-right: 16rpx;
		font-weight: bold;
	}

	.feature-text {
		font-size: 28rpx;
		color: #333333;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	/* 服务网格 */
	.service-grid {
		display: flex;
		flex-wrap: wrap;
		padding: 0rpx 24rpx;
		gap: 24rpx;
	}

	.service-item {
		width: 333rpx;
		height: 160rpx;
	}

	.service-card {
		position: relative;
		width: 340rpx;
		height: 160rpx;
		border-radius: 20rpx;
		opacity: 1;
		background: #ffffff;
		overflow: hidden;
	}

	.service-bg {
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
	}

	.service-content {
		position: relative;
		width: 100%;
		height: 100%;
		display: flex;
		flex-direction: row;
		align-items: center;
		z-index: 1;
	}

	.service-icon {
		width: 52rpx;
		height: 52rpx;
		flex-shrink: 0;
		margin-right: 30rpx;
	}

	.service-text {
		width: 140rpx;
 		height: 40rpx;
 opacity: 1;
 color: #333333;
 font-size: 28rpx;
 font-weight: 500;
 font-family: "PingFang SC", "苹方-简", sans-serif;
 text-align: left;
 margin: 0 40rpx 0 60rpx;
	}
</style>
