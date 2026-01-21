<template>
	<view class="page">
		<view class="content">
			<!-- 成功图标 -->
			<image class="success-icon" src="/static/成功@2x.png" mode="aspectFit"></image>
			
			<!-- 成功提示 -->
			<text class="success-title">订单支付成功</text>
			<text class="order-amount">订单金额：¥{{ amount }}</text>
		</view>
		
		<!-- 返回首页按钮 -->
		<view class="back-btn" @click="goHome">
			<text class="back-btn-text">返回首页</text>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				amount: '0.00'
			}
		},
		onLoad(options) {
			if (options.amount) {
				this.amount = options.amount
			}
		},
		methods: {
			goHome() {
				uni.switchTab({
					url: '/pages/index/index'
				})
			}
		}
	}
</script>

<style scoped>
	.page {
		min-height: 95vh;
		background: #f5f6fc;
		display: flex;
		flex-direction: column;
		align-items: center;
		
		box-sizing: border-box;
	}
	
	.content {
		display: flex;
		flex-direction: column;
		align-items: center;
		margin-top: 65rpx;
	}
	
	.success-icon {
		width: 330rpx;
		height: 298rpx;
		margin-bottom: 40rpx;
	}
	
	.success-title {
		height: 56rpx;
		color: #000000;
		font-size: 40rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 56rpx;
		margin-bottom: 4rpx;
	}
	
	.order-amount {
		height: 34rpx;
		color: #000000;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 34rpx;
	}
	
	.back-btn {
		width: 640rpx;
		height: 92rpx;
		border-radius: 16rpx;
		border: 2rpx solid #1976f8;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-top: 94rpx;
		margin-bottom: 120rpx;
	}
	
	.back-btn-text {
		height: 50rpx;
		color: #1976f8;
		font-size: 36rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 50rpx;
	}
</style>
