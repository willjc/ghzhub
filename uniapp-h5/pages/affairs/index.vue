<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 人才公寓 -->
			<view class="section">
				<view class="section-header">
					<image class="section-indicator" src="/static/banshi/人才公寓@2x.png"></image>
					<text class="section-title">人才公寓</text>
				</view>
				<view class="icon-grid">
					<view 
						class="icon-item" 
						v-for="(item, index) in functionList" 
						:key="index"
						@click="handleFunctionClick('talent', item.key)"
					>
						<view class="icon-wrapper">
							<image class="icon-image" :src="getIconPath('talent', item.key)"></image>
						</view>
						<text class="icon-text">{{ item.name }}</text>
					</view>
				</view>
			</view>

			<!-- 保租房 -->
			<view class="section">
				<view class="section-header">
					<image class="section-indicator" src="/static/banshi/保租房@2x.png"></image>
					<text class="section-title">保租房</text>
				</view>
				<view class="icon-grid">
					<view 
						class="icon-item" 
						v-for="(item, index) in functionList" 
						:key="index"
						@click="handleFunctionClick('guaranteed', item.key)"
					>
						<view class="icon-wrapper">
							<image class="icon-image" :src="getIconPath('guaranteed', item.key)"></image>
						</view>
						<text class="icon-text">{{ item.name }}</text>
					</view>
				</view>
			</view>

			<!-- 市场租赁 -->
			<view class="section">
				<view class="section-header">
					<image class="section-indicator" src="/static/banshi/市场租赁@2x.png"></image>
					<text class="section-title">市场租赁</text>
				</view>
				<view class="icon-grid">
					<view 
						class="icon-item" 
						v-for="(item, index) in functionList" 
						:key="index"
						@click="handleFunctionClick('market', item.key)"
					>
						<view class="icon-wrapper">
							<image class="icon-image" :src="getIconPath('market', item.key)"></image>
						</view>
						<text class="icon-text">{{ item.name }}</text>
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
				functionList: [
					{ key: 'appeal', name: '资格申诉' },
					{ key: 'select', name: '选房' },
					{ key: 'checkin', name: '入住办理' },
					{ key: 'checkout', name: '退租办理' },
					{ key: 'renew', name: '续租' },
					{ key: 'bill', name: '账单缴费' },
					{ key: 'contract', name: '我的合同' },
					{ key: 'invoice', name: '开票' },
					{ key: 'appointment', name: '我的预约' },
					{ key: 'cohabitant', name: '合住人申请' },
					{ key: 'exchange', name: '调换房申请' }
				]
			}
		},
		onLoad() {
			
		},
		methods: {
			getIconPath(type, key) {
				const iconMap = {
					appeal: '资格申诉',
					select: '选房',
					checkin: '入住办理',
					checkout: '退租',
					renew: '续租',
					bill: '账单缴费',
					contract: '合同',
					invoice: '开票',
					appointment: '预约',
					cohabitant: '合住人',
					exchange: '调换房'
				}
				
				const iconName = iconMap[key]
				let colorSuffix = ''
				
				if (type === 'talent') {
					colorSuffix = '-蓝'
					// 退租办理在人才公寓中使用特殊文件名
					if (key === 'checkout') {
						return `/static/banshi/退租办理-蓝@2x.png`
					}
				} else if (type === 'guaranteed') {
					colorSuffix = '-灰'
				} else {
					colorSuffix = '-紫蓝'
				}
				
				return `/static/banshi/${iconName}${colorSuffix}@2x.png`
			},
			handleFunctionClick(type, key) {
				console.log('点击功能:', type, key)

				// 根据功能key跳转到对应页面
				if (key === 'select') {
					// 选房 - 根据类型跳转到不同页面
					if (type === 'talent') {
						uni.navigateTo({
							url: '/pages/talent/index'
						})
					} else if (type === 'guaranteed') {
						uni.navigateTo({
							url: '/pages/rental/index'
						})
					} else if (type === 'market') {
						uni.navigateTo({
							url: '/pages/market/index'
						})
					}
				} else if (key === 'appeal') {
					// 资格申诉
					uni.navigateTo({
						url: `/subpkg/affairs/appeal?type=${type}`
					})
				} else if (key === 'checkin') {
					// 入住办理
					uni.navigateTo({
						url: `/subpkg/affairs/checkin?type=${type}`
					})
				} else if (key === 'checkout') {
					// 退租办理
					uni.navigateTo({
						url: `/subpkg/affairs/checkout?type=${type}`
					})
				} else if (key === 'renew') {
					// 续租
					uni.navigateTo({
						url: `/subpkg/affairs/renew?type=${type}`
					})
				} else if (key === 'bill') {
					// 账单缴费
					uni.navigateTo({
						url: `/subpkg/affairs/bill?type=${type}`
					})
				} else if (key === 'contract') {
					// 我的合同
					uni.navigateTo({
						url: `/subpkg/affairs/contract?type=${type}`
					})
				} else if (key === 'invoice') {
					// 开票
					uni.navigateTo({
						url: `/subpkg/affairs/invoice?type=${type}`
					})
				} else if (key === 'appointment') {
					// 我的预约
					uni.navigateTo({
						url: `/subpkg/affairs/appointment?type=${type}`
					})
				} else if (key === 'cohabitant') {
					// 合住人申请 - 先跳转到列表页
					uni.navigateTo({
						url: `/subpkg/affairs/cohabitant-list?type=${type}`
					})
				} else if (key === 'exchange') {
					// 调换房申请
					uni.navigateTo({
						url: `/subpkg/affairs/exchange?type=${type}`
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
		min-height: 100vh;
	}

	.scroll-content {
		flex: 1;
		overflow-y: auto;
		padding: 24rpx;
		box-sizing: border-box;
	}

	.section {
		width: 702rpx;
		height: 518rpx;
		border-radius: 20rpx;
		opacity: 1;
		background: #ffffff;
		padding: 24rpx;
		margin: 0 auto 24rpx;
		box-sizing: border-box;
	}

	.section-header {
		display: flex;
		align-items: center;
		margin-bottom: 36rpx;
	}

	.section-indicator {
		width: 10rpx;
		height: 32rpx;
		margin-right: 16rpx;
	}

	.section-title {
		font-size: 32rpx;
		color: #1a1a1a;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.icon-grid {
		display: flex;
		flex-wrap: wrap;
		gap: 32rpx 0;
	}

	.icon-item {
		width: 25%;
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.icon-wrapper {
		width: 45.94rpx;
		height: 50.63rpx;
		opacity: 1;
		
		border-radius: 8rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 13rpx;
	}

	.icon-image {
		width: 45.94rpx;
		height: 50.63rpx;
	}

	.icon-text {

		height: 40rpx;
		opacity: 1;
		color: #333333;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 40rpx;
	}
</style>
