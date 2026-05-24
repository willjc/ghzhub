<template>
	<view class="page">
		<!-- 状态筛选 -->
		<view class="filter-bar">
			<view
				class="filter-item"
				v-for="(it, idx) in statusOptions"
				:key="idx"
				:class="{ active: status === it.value }"
				@click="onStatus(it.value)"
			>
				<text>{{ it.label }}</text>
			</view>
		</view>

		<!-- 空状态 -->
		<view class="empty-coupon" v-if="!loading && list.length === 0">
			<image class="empty-icon" src="/static/fangyaun/优惠券@2x.png" mode="aspectFit"></image>
			<text class="empty-text">暂无优惠券</text>
		</view>

		<!-- 优惠券列表 -->
		<scroll-view class="scroll-content" scroll-y v-if="list.length > 0">
			<view class="coupon-list">
				<view class="coupon-item" v-for="item in list" :key="item.id">
					<view
						class="coupon-card"
						:class="{ expired: item.receiveStatus !== 1 }"
					>
						<view class="coupon-card-content">
							<view
								class="coupon-left"
								:class="{
									'coupon-bg-expired': item.receiveStatus !== 1,
									'coupon-bg-normal': item.receiveStatus === 1
								}"
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

							<view class="coupon-right">
								<view class="coupon-info-wrapper">
									<view class="coupon-info">
										<text class="coupon-scope">{{ item.couponName }}</text>
										<text class="coupon-validity">有效期至{{ formatDate(item.validEndDate) }}</text>
									</view>
									<view class="coupon-action">
										<view class="status-tag" :class="{ unused: item.receiveStatus === 1, used: item.receiveStatus === 2, expired: item.receiveStatus !== 1 && item.receiveStatus !== 2 }">
											<text>{{ statusText(item) }}</text>
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
	import { getMyCoupons } from '@/api/coupon.js'

	export default {
		data() {
			return {
				loading: false,
				tenantId: null,
				status: '',
				statusOptions: [
					{ label: '全部', value: '' },
					{ label: '未使用', value: 1 },
					{ label: '已使用', value: 2 },
					{ label: '已过期', value: 3 }
				],
				list: []
			}
		},
		onLoad() {
			try {
				const u = uni.getStorageSync('userInfo')
				if (u && u.userId) this.tenantId = u.userId
			} catch (e) {}
			this.loadList()
		},
		methods: {
			onStatus(v) {
				this.status = v
				this.loadList()
			},
			async loadList() {
				if (!this.tenantId) {
					uni.showToast({ title: '请先登录', icon: 'none' })
					return
				}
				this.loading = true
				try {
					const res = await getMyCoupons(this.tenantId, this.status)
					this.list = res.data || []
				} catch (e) {
					console.error(e)
				} finally {
					this.loading = false
				}
			},
			buildCondition(item) {
				if (item.couponType === 2) {
					return item.minAmount > 0 ? `满${item.minAmount}元可用` : '全场通用'
				}
				return item.minAmount > 0 ? `满${item.minAmount}元可用` : '无门槛'
			},
			formatDate(s) {
				if (!s) return ''
				return s.substring(0, 10).replace(/-/g, '.')
			},
			statusText(item) {
				const map = { 1: '未使用', 2: '已使用', 3: '已过期' }
				return map[item.receiveStatus] || '-'
			},
			statusClass(item) {
				if (item.receiveStatus === 1) return 'unused'
				if (item.receiveStatus === 2) return 'used'
				return 'expired'
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

	.filter-bar {
		display: flex;
		background: #ffffff;
		padding: 0 24rpx;
		border-bottom: 1rpx solid #eee;
	}

	.filter-item {
		flex: 1;
		text-align: center;
		padding: 24rpx 0;
		font-size: 28rpx;
		color: #666;
	}

	.filter-item.active {
		color: #3388ff;
		font-weight: 500;
		border-bottom: 4rpx solid #3388ff;
	}

	.empty-coupon {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 200rpx 0;
		gap: 16rpx;
	}

	.empty-icon {
		width: 120rpx;
		height: 120rpx;
		opacity: 0.4;
	}

	.empty-text {
		color: #999;
		font-size: 28rpx;
	}

	.scroll-content {
		flex: 1;
		padding: 24rpx;
	}

	.coupon-list {
		display: flex;
		flex-direction: column;
		gap: 24rpx;
	}

	.coupon-card {
		width: 702rpx;
		border-radius: 20rpx;
		background: #ffffff;
		overflow: hidden;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.coupon-card.expired {
		opacity: 0.7;
	}

	.coupon-card-content {
		display: flex;
	}

	.coupon-left {
		width: 200rpx;
		padding: 30rpx 0;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}

	.coupon-bg-normal {
		background: linear-gradient(135deg, #4a9eff 0%, #3388ff 100%);
	}

	.coupon-bg-expired {
		background: linear-gradient(135deg, #b5b5b5 0%, #999 100%);
	}

	.coupon-amount-wrapper {
		display: flex;
		align-items: baseline;
	}

	.coupon-symbol {
		color: #ffffff;
		font-size: 28rpx;
	}

	.coupon-amount {
		color: #ffffff;
		font-size: 52rpx;
		font-weight: 500;
	}

	.coupon-condition {
		color: #ffffff;
		font-size: 22rpx;
		margin-top: 12rpx;
	}

	.coupon-right {
		flex: 1;
		padding: 24rpx;
	}

	.coupon-info-wrapper {
		display: flex;
		align-items: center;
		justify-content: space-between;
		height: 100%;
	}

	.coupon-info {
		display: flex;
		flex-direction: column;
		gap: 12rpx;
		flex: 1;
	}

	.coupon-scope {
		color: #000;
		font-size: 30rpx;
		font-weight: 500;
	}

	.coupon-validity {
		color: #999;
		font-size: 24rpx;
	}

	.status-tag {
		padding: 8rpx 20rpx;
		border-radius: 8rpx;
		font-size: 24rpx;
	}

	.status-tag.unused {
		color: #3388ff;
		border: 2rpx solid #3388ff;
	}

	.status-tag.used {
		color: #999;
		border: 2rpx solid #999;
		background: #f5f5f5;
	}

	.status-tag.expired {
		color: #999;
		border: 2rpx solid #ccc;
	}
</style>
