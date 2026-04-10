<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 入住信息 -->
			<view class="section">
				<view class="section-header">
					<view class="section-indicator"></view>
					<text class="section-title">入住信息</text>
				</view>

				<!-- 房源列表 -->
				<view class="house-list">
					<view
						class="house-card"
						v-for="(house, index) in houseList"
						:key="index"
						:class="{ 'house-card-selected': selectedHouseIndex === index }"
						@click="selectHouse(index)"
					>
						<view class="house-header">
							<text class="house-community">{{ house.community }}</text>
							<image
								class="house-radio"
								:src="selectedHouseIndex === index ? '/static/fangyaun/选中@2x.png' : '/static/fangyaun/未选中@2x.png'"
								mode="aspectFit"
							></image>
						</view>
						<view class="house-info">
							<view class="info-item">
								<text class="info-label">房间</text>
								<text class="info-value">{{ house.room }}</text>
							</view>
							<view class="info-item">
								<text class="info-label">押金</text>
								<text class="info-value">{{ house.deposit }}元</text>
							</view>
						</view>
					</view>
				</view>

				<!-- 空状态 -->
				<view class="empty-house" v-if="houseList.length === 0">
					<text class="empty-text">暂无入住信息</text>
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
				selectedHouseIndex: -1, // 选中的房源索引
				selectedIndex: -1, // 选中的账单索引

				houseList: [], // 房源列表
				billList: [] // 账单列表
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
			// 选择房源
			selectHouse(index) {
				if (this.selectedHouseIndex === index) return // 已选中则不重复加载
				this.selectedHouseIndex = index
				this.selectedIndex = -1 // 清空账单选择
				this.loadBills() // 加载该房源的账单
			},

			// 选择账单
			selectBill(index) {
				this.selectedIndex = index
			},

			// 确定
			handleConfirm() {
				if (this.selectedHouseIndex === -1) {
					uni.showToast({
						title: '请选择房源',
						icon: 'none'
					})
					return
				}
				if (this.selectedIndex === -1 || this.billList.length === 0) {
					uni.showToast({
						title: '请选择要开票的账单',
						icon: 'none'
					})
					return
				}

				const selectedBill = this.billList[this.selectedIndex]
				uni.navigateTo({
					url: `/subpkg/affairs/invoice-form?type=${this.housingType}&billId=${selectedBill.id}`
				})
			},

			// 加载房源列表
			async loadData() {
				try {
					const checkinRes = await getCheckinInfo(this.userId)

					// 处理房源列表
					if (checkinRes.code === 200 && checkinRes.data) {
						this.houseList = checkinRes.data.map(item => ({
							community: item.community || '',
							room: item.room || '',
							deposit: item.deposit || '0',
							houseId: item.houseId,
							projectId: item.projectId,
							contractId: item.contractId
						}))

						// 如果只有一个房源，自动选中
						if (this.houseList.length === 1) {
							this.selectHouse(0)
						}
					}

					// 如果没有房源，显示提示
					if (this.houseList.length === 0) {
						uni.showToast({
							title: '暂无可开票的房源',
							icon: 'none'
						})
					}
				} catch (e) {
					console.error('加载房源列表失败:', e)
					uni.showToast({
						title: '加载失败，请重试',
						icon: 'none'
					})
				}
			},

			// 加载指定房源的账单
			async loadBills() {
				if (this.selectedHouseIndex === -1) return

				const selectedHouse = this.houseList[this.selectedHouseIndex]
				const contractId = selectedHouse.contractId

				try {
					const billsRes = await getAvailableBills(this.userId, contractId)

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
					} else {
						this.billList = []
					}

					// 如果该房源没有可开票账单，显示提示
					if (this.billList.length === 0) {
						uni.showToast({
							title: '该房源暂无可开票账单',
							icon: 'none'
						})
					}
				} catch (e) {
					console.error('加载账单失败:', e)
					uni.showToast({
						title: '加载账单失败，请重试',
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

	/* 房源列表 */
	.house-list {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
		margin-bottom: 24rpx;
	}

	.house-card {
		width: 702rpx;
		border-radius: 20rpx;
		background: #ffffff;
		padding: 28rpx 32rpx;
		box-sizing: border-box;
		border: 2rpx solid transparent;
		transition: all 0.3s;
	}

	.house-card-selected {
		border-color: #0f73ff;
		background: rgba(15, 115, 255, 0.03);
	}

	.house-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin-bottom: 16rpx;
	}

	.house-community {
		color: #1a1a1a;
		font-size: 30rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.house-radio {
		width: 36rpx;
		height: 36rpx;
		flex-shrink: 0;
	}

	.house-info {
		display: flex;
		flex-direction: column;
		gap: 8rpx;
	}

	.info-item {
		display: flex;
		align-items: center;
	}

	.info-label {
		width: 110rpx;
		color: #666666;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.info-value {
		color: #333333;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.empty-house {
		padding: 60rpx 0;
		text-align: center;
	}

	.empty-text {
		color: #999999;
		font-size: 28rpx;
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

