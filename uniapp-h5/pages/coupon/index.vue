<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 优惠券列表 -->
			<view class="coupon-list">
				<view 
					class="coupon-item"
					v-for="(item, index) in couponList" 
					:key="index"
				>
					<view 
						class="coupon-card" 
						:class="{ expired: item.isExpired }"
					>
						<view class="coupon-card-content">
							<!-- 左侧金额区域 -->
							<view 
								class="coupon-left"
								:class="{ 'coupon-bg-expired': item.isExpired, 'coupon-bg-normal': !item.isExpired }"
							>
								<view class="coupon-amount-wrapper">
									<text class="coupon-symbol">¥</text>
									<text class="coupon-amount">{{ item.amount }}</text>
								</view>
								<text class="coupon-condition">{{ item.condition }}</text>
							</view>
							
							<!-- 右侧信息区域 -->
							<view class="coupon-right">
								<view class="coupon-info-wrapper">
									<view class="coupon-info">
										<text class="coupon-scope">{{ item.scope }}</text>
										<text class="coupon-validity">有效期至{{ item.validity }}</text>
									</view>
									
									<!-- 操作按钮 -->
									<view class="coupon-action">
										<view 
											class="action-btn" 
											:class="{ expired: item.isExpired }"
											@click="handleClaim(item, index)"
										>
											<text class="action-text">{{ item.isExpired ? '已过期' : '立即领取' }}</text>
										</view>
									</view>
								</view>
							</view>
						</view>
						
						<!-- 详细信息（如果有） -->
						<view class="coupon-details" v-if="item.communities || item.types">
							<text class="detail-item" v-if="item.communities">可用小区: {{ item.communities }}</text>
							<text class="detail-item" v-if="item.types">可用类型: {{ item.types }}</text>
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
				couponList: [
					{
						amount: 200,
						condition: '满10000元可用',
						scope: '全场通用',
						validity: '2026.08.31',
						isExpired: false
					},
					{
						amount: 200,
						condition: '满10000元可用',
						scope: '仅限保租房可用',
						validity: '2026.08.31',
						isExpired: false
					},
					{
						amount: 200,
						condition: '满10000元可用',
						scope: '仅限保租房可用',
						validity: '2026.08.31',
						communities: '美好人间一期、保利心语一期',
						types: '保租房、市场化租赁',
						isExpired: false
					},
					{
						amount: 200,
						condition: '满10000元可用',
						scope: '仅限保租房可用',
						validity: '2026.08.31',
						isExpired: true
					}
				]
			}
		},
		onLoad() {
			
		},
		methods: {
			handleClaim(item, index) {
				if (item.isExpired) {
					return
				}
				console.log('领取优惠券:', item)
				uni.showToast({
					title: '领取成功',
					icon: 'success'
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

	.scroll-content {
		flex: 1;
		overflow-y: auto;
		padding: 24rpx;
		box-sizing: border-box;
	}

	.coupon-list {
		display: flex;
		flex-direction: column;
		gap: 24rpx;
	}

	.coupon-item {
		display: flex;
		flex-direction: column;
		width: 702rpx;
		margin: 0 auto;
	}

	.coupon-card {
		width: 702rpx;
		border-radius: 20rpx;
		opacity: 1;
		background: #ffffff;
		display: flex;
		flex-direction: column;
		overflow: hidden;
		margin: 0 auto;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.coupon-card-content {
		display: flex;
	}

	.coupon-card.expired {
		opacity: 0.6;
	}

	/* 左侧金额区域 */
	.coupon-left {
		width: 188rpx;
		height: 146rpx;
		display: flex;
		flex-direction: column;
		align-items: center;

		background-size: 188rpx 146rpx;
		background-position: center;
		background-repeat: no-repeat;
		margin: 22rpx 26rpx;
	}

	.coupon-bg-normal {
		background-image: url('/static/矩形 2@2x.png');
	}

	.coupon-bg-expired {
		background-image: url('/static/矩形 2@2x(1).png');
	}

	.coupon-amount-wrapper {
		position: relative;
		z-index: 2;
		display: flex;
		align-items: baseline;
		margin-bottom: 23rpx;
		margin-top: 35rpx;
	}

	.coupon-symbol {
		width: 17rpx;
		height: 28rpx;
		opacity: 1;
		color: #ffffff;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 28rpx;
	}

	.coupon-amount {
		width: 94rpx;
		height: 46rpx;
		opacity: 1;
		color: #ffffff;
		font-size: 52rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
	}

	.coupon-condition {
		position: relative;
		z-index: 2;
		width: 150rpx;
		height: 24rpx;
		opacity: 1;
		color: #ffffff;
		font-size: 22rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 24rpx;
	}

	/* 右侧信息区域 */
	.coupon-right {
		flex: 1;
		padding: 24rpx;
		display: flex;
		flex-direction: column;
		justify-content: space-between;
		box-sizing: border-box;
	}

	.coupon-info-wrapper {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin-top: 26rpx;
	}

	.coupon-info {
		display: flex;
		flex-direction: column;
		gap: 12rpx;
		flex: 1;
	}

	.coupon-scope {
		height: 42rpx;
		opacity: 1;
		color: #000000;
		font-size: 30rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 42rpx;
	}

	.coupon-validity {
		width: 220rpx;
		height: 34rpx;
		opacity: 1;
		color: #999999;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 34rpx;
	}

	.coupon-details {
		display: flex;
		flex-direction: column;
		gap: 4rpx;
		margin: 0 24rpx;
		padding: 16rpx 0;
		box-sizing: border-box;
		background-color: #ffffff;
		border-top: 0.5rpx solid #E5E5E5;
	}

	.detail-item {
		height: 32rpx;
		opacity: 1;
		color: #999999;
		font-size: 22rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 32rpx;
	}

	.coupon-action {
		display: flex;
		align-items: center;
		margin-left: auto;
	}

	.action-btn {
		width: 133rpx;
		height: 60rpx;
		border-radius: 12rpx;
		opacity: 1;
		line-height: 60rpx;
		border: 2rpx solid #3388ff;
		background: transparent;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.action-btn.expired {
		border: 2rpx solid #999999;
	}

	.action-text {
		width: 96rpx;
		height: 34rpx;
		opacity: 1;
		color: #3388ff;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 34rpx;
	}

	.action-btn.expired .action-text {
		width: 72rpx;
		height: 34rpx;
		opacity: 1;
		color: #999999;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 34rpx;
	}
</style>

