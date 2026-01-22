<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 入住信息 -->
			<view class="section">
				<view class="section-header">
					<view class="section-indicator"></view>
					<text class="section-title">入住信息</text>
				</view>
				
				<view class="card">
					<view class="info-row">
						<text class="info-label">小区</text>
						<text class="info-value">{{ checkinInfo.community }}</text>
					</view>
					
					<view class="info-row">
						<text class="info-label">房间</text>
						<text class="info-value">{{ checkinInfo.room }}</text>
					</view>
					
					<view class="info-row">
						<text class="info-label">押金</text>
						<text class="info-value">{{ checkinInfo.deposit }}</text>
					</view>
				</view>
			</view>
			
			<!-- 租期信息 -->
			<view class="section">
				<view class="section-header">
					<view class="section-indicator"></view>
					<text class="section-title">租期信息</text>
				</view>
				
				<!-- 账单列表 -->
				<view class="bill-list">
					<view class="bill-item" v-for="(item, index) in billList" :key="index" @click="selectBill(index)">
						<image 
							class="bill-checkbox" 
							:src="selectedIndex === index ? '/static/fangyaun/选中@2x.png' : '/static/fangyaun/未选中@2x.png'" 
							mode="aspectFit"
						></image>
						<view class="bill-card">
							<view class="bill-header">
								<text class="bill-title">账单期数：{{ item.period }}</text>
								<view class="bill-status">
									<text class="status-text">{{ item.statusText }}</text>
								</view>
							</view>
							<view class="bill-info">
								<text class="bill-label">账单金额：</text>
								<text class="bill-value">{{ item.amount }}</text>
							</view>
							<view class="bill-info">
								<text class="bill-label">账单周期：</text>
								<text class="bill-value">{{ item.dateRange }}</text>
							</view>
						</view>
					</view>
				</view>
			</view>
		</scroll-view>
		
		<!-- 底部确定按钮 -->
		<view class="bottom-btn-container">
			<view class="bottom-btn" @click="handleConfirm">
				<text class="bottom-btn-text">确定</text>
			</view>
		</view>
	</view>
</template>

<script>
	import { getCheckinInfo, getAvailableBills } from '@/api/invoice'

	export default {
		data() {
			return {
				housingType: '',
				userId: null,
				selectedIndex: -1,

				checkinInfo: {
					community: '',
					room: '',
					deposit: ''
				},

				billList: []
			}
		},
		onLoad(options) {
			// 获取用户信息
			const userInfo = uni.getStorageSync('userInfo')
			if (!userInfo || !userInfo.userId) {
				uni.showToast({
					title: '请先登录',
					icon: 'none'
				})
				setTimeout(() => {
					uni.navigateTo({
						url: '/pages/login/index'
					})
				}, 1500)
				return
			}
			this.userId = userInfo.userId

			if (options.type) {
				this.housingType = options.type
			}
			this.loadData()
		},
		methods: {
			// 选择账单
			selectBill(index) {
				this.selectedIndex = index
			},

			// 确定
			handleConfirm() {
				if (this.selectedIndex === -1 || this.billList.length === 0) {
					uni.showToast({
						title: '请选择要开票的账单',
						icon: 'none'
					})
					return
				}

				const selectedBill = this.billList[this.selectedIndex]
				uni.navigateTo({
					url: `/pages/affairs/invoice-form?type=${this.housingType}&billId=${selectedBill.id}`
				})
			},

			// 加载数据
			async loadData() {
				try {
					// 并行获取入住信息和可开票账单
					const [checkinRes, billsRes] = await Promise.all([
						getCheckinInfo(this.userId),
						getAvailableBills(this.userId)
					])

					// 处理入住信息
					if (checkinRes.code === 200 && checkinRes.data) {
						this.checkinInfo = {
							community: checkinRes.data.community || '',
							room: checkinRes.data.room || '',
							deposit: checkinRes.data.deposit || '0元'
						}
					}

					// 处理账单列表
					if (billsRes.code === 200 && billsRes.data) {
						this.billList = billsRes.data.map(item => ({
							id: item.billId,
							period: item.billPeriod || '未知期数',
							status: 'paid',
							statusText: '已支付',
							amount: `${item.billAmount || 0}元`,
							dateRange: item.billPeriod || ''
						}))
					}

					// 如果没有可开票账单，显示提示
					if (this.billList.length === 0) {
						uni.showToast({
							title: '暂无可开票的账单',
							icon: 'none'
						})
					}
				} catch (e) {
					console.error('加载数据失败:', e)
					uni.showToast({
						title: '加载失败，请重试',
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
		min-height: 95vh;
		background-color: #f5f6fc;
		display: flex;
		flex-direction: column;
	}

	.scroll-content {
		flex: 1;
		padding: 28rpx 24rpx;
		padding-bottom: 140rpx;
		box-sizing: border-box;
	}

	/* 区块 */
	.section {
		margin-bottom: 24rpx;
	}

	.section-header {
		display: flex;
		align-items: center;
		margin-bottom: 20rpx;
	}

	.section-indicator {
		width: 12rpx;
		height: 34rpx;
		background: #0f73ff;
		margin-right: 20rpx;
	}

	.section-title {
		color: #171a1f;
		font-size: 32rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	/* 卡片 */
	.card {
		width: 702rpx;
		border-radius: 20rpx;
		background: #ffffff;
		padding: 32rpx 40rpx;
		box-sizing: border-box;
	}

	/* 信息行 */
	.info-row {
		display: flex;
		align-items: center;
		margin-bottom: 20rpx;
	}

	.info-row:last-child {
		margin-bottom: 0;
	}

	.info-label {
		width: 110rpx;
		color: #333333;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.info-value {
		flex: 1;
		color: #1a1a1a;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	/* 账单列表 */
	.bill-list {
		display: flex;
		flex-direction: column;
		gap: 24rpx;
	}

	.bill-item {
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.bill-checkbox {
		width: 36rpx;
		height: 36rpx;
		flex-shrink: 0;
	}

	.bill-card {
		width: 642rpx;
		border-radius: 20rpx;
		background: #ffffff;
		padding: 28rpx;
		box-sizing: border-box;
	}

	.bill-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding-bottom: 14rpx;
		border-bottom: 1rpx solid #f0f0f0;
		margin-bottom: 18rpx;
	}

	.bill-title {
		height: 45rpx;
		color: #1a1a1a;
		font-size: 32rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 45rpx;
	}

	.bill-status {
		width: 105rpx;
		height: 48rpx;
		border-radius: 4rpx;
		background: rgba(25, 118, 248, 0.1);
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.status-text {
		color: #1976f8;
		font-size: 24rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
	}

	.bill-info {
		display: flex;
		align-items: center;
		margin-bottom: 12rpx;
	}

	.bill-label {
		height: 34rpx;
		color: #888888;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 34rpx;
	}

	.bill-value {
		height: 34rpx;
		color: #333333;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 34rpx;
	}

	/* 底部按钮 */
	.bottom-btn-container {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		padding: 24rpx;
		background: #f5f6fc;
	}

	.bottom-btn {
		width: 702rpx;
		height: 92rpx;
		border-radius: 20rpx;
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
		display: flex;
		align-items: center;
		justify-content: center;
		margin: 0 auto;
	}

	.bottom-btn-text {
		color: #ffffff;
		font-size: 36rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}
</style>

