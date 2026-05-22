<template>
	<view class="page">
		<!-- 顶部 tab：可领取 / 我的优惠券 -->
		<view class="tab-bar">
			<view class="tab-item active">
				<text class="tab-text">可领取</text>
			</view>
			<view class="tab-item" @click="goMy">
				<text class="tab-text">我的优惠券</text>
			</view>
		</view>

		<!-- 空状态 -->
		<view class="empty-coupon" v-if="!loading && couponList.length === 0">
			<image class="empty-icon" src="/static/fangyaun/优惠券@2x.png" mode="aspectFit"></image>
			<text class="empty-text">暂无可领取优惠券</text>
		</view>

		<!-- 优惠券列表 -->
		<scroll-view class="scroll-content" scroll-y v-if="couponList.length > 0">
			<view class="coupon-list">
				<view
					class="coupon-item"
					v-for="(item, index) in couponList"
					:key="item.id"
				>
					<view
						class="coupon-card"
						:class="{ expired: isExpired(item) }"
					>
						<view class="coupon-card-content">
							<!-- 左侧金额区域 -->
							<view
								class="coupon-left"
								:class="{ 'coupon-bg-expired': isExpired(item), 'coupon-bg-normal': !isExpired(item) }"
							>
								<view class="coupon-amount-wrapper">
									<template v-if="item.couponType === 2">
										<text class="coupon-amount">{{ item.discountRate }}</text>
										<text class="coupon-symbol">%</text>
									</template>
									<template v-else>
										<text class="coupon-symbol">¥</text>
										<text class="coupon-amount">{{ item.discountAmount }}</text>
									</template>
								</view>
								<text class="coupon-condition">{{ buildCondition(item) }}</text>
							</view>

							<!-- 右侧信息区域 -->
							<view class="coupon-right">
								<view class="coupon-info-wrapper">
									<view class="coupon-info">
										<text class="coupon-scope">{{ item.couponName }}</text>
										<text class="coupon-validity">有效期至{{ formatDate(item.validEndDate) }}</text>
									</view>

									<!-- 操作按钮 -->
									<view class="coupon-action">
										<view
											class="action-btn"
											:class="{ expired: isExpired(item) || item.hasReceived }"
											@click="handleClaim(item, index)"
										>
											<text class="action-text">{{ buildBtnText(item) }}</text>
										</view>
									</view>
								</view>
							</view>
						</view>
					</view>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import { getAvailableCoupons, receiveCoupon } from '@/api/coupon.js'

	export default {
		data() {
			return {
				loading: false,
				tenantId: null,
				couponList: []
			}
		},
		onLoad() {
			try {
				const u = uni.getStorageSync('userInfo')
				if (u && u.userId) this.tenantId = u.userId
			} catch (e) {}
			this.loadList()
		},
		onShow() {
			this.loadList()
		},
		methods: {
			async loadList() {
				this.loading = true
				try {
					const res = await getAvailableCoupons(this.tenantId)
					this.couponList = res.data || []
				} catch (e) {
					console.error(e)
				} finally {
					this.loading = false
				}
			},
			isExpired(item) {
				if (!item.validEndDate) return false
				return new Date(item.validEndDate.replace(/-/g, '/')).getTime() < Date.now()
			},
			buildCondition(item) {
				if (item.couponType === 2) {
					return item.minAmount > 0 ? `满${item.minAmount}元可用` : '全场通用'
				}
				return item.minAmount > 0 ? `满${item.minAmount}元可用` : '无门槛'
			},
			buildBtnText(item) {
				if (this.isExpired(item)) return '已过期'
				if (item.hasReceived) return '已领取'
				if (item.totalCount > 0 && item.receivedCount >= item.totalCount) return '已领完'
				return '立即领取'
			},
			formatDate(s) {
				if (!s) return ''
				return s.substring(0, 10).replace(/-/g, '.')
			},
			async handleClaim(item) {
				if (this.isExpired(item) || item.hasReceived) return
				if (!this.tenantId) {
					uni.showToast({ title: '请先登录', icon: 'none' })
					return
				}
				try {
					await receiveCoupon(item.id, this.tenantId)
					uni.showToast({ title: '领取成功', icon: 'success' })
					this.loadList()
				} catch (e) {
					uni.showToast({ title: e.msg || e.message || '领取失败', icon: 'none' })
				}
			},
			goMy() {
				uni.navigateTo({ url: '/pages/coupon/my' })
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

	.tab-bar {
		display: flex;
		background: #ffffff;
		border-bottom: 1rpx solid #eee;
	}

	.tab-item {
		flex: 1;
		text-align: center;
		padding: 24rpx 0;
	}

	.tab-text {
		font-size: 28rpx;
		color: #666;
	}

	.tab-item.active .tab-text {
		color: #3388ff;
		font-weight: 500;
	}

	.tab-item.active {
		border-bottom: 4rpx solid #3388ff;
	}

	/* 空状态 */
	.empty-coupon {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		flex: 1;
		gap: 16rpx;
	}

	.empty-icon {
		width: 120rpx;
		height: 120rpx;
		opacity: 0.4;
	}

	.empty-text {
		color: #999999;
		font-size: 28rpx;
		font-weight: 500;
	}

	.empty-desc {
		color: #cccccc;
		font-size: 24rpx;
	}

	/* 优惠券列表 */
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

	/* 小程序 WXSS 不支持引本地图，改用 base64 内联 */
	.coupon-bg-normal {
		background-image: url('data:image/png;base64,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');
	}

	.coupon-bg-expired {
		background-image: url('data:image/png;base64,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');
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

