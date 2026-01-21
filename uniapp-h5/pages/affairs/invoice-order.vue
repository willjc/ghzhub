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
					<view class="bill-item" v-for="(item, index) in billList" :key="index">
						<image 
							class="bill-checkbox" 
							src="/static/选中1@2x.png" 
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
			
			<!-- 申请信息 -->
			<view class="section">
				<view class="section-header">
					<view class="section-indicator"></view>
					<text class="section-title">申请信息</text>
				</view>
				
				<view class="card">
					<view class="info-row">
						<text class="info-label1">开票状态</text>
						<text class="info-value">{{ applyInfo.status }}</text>
					</view>
					
					<view class="info-row">
						<text class="info-label1">拒绝原因</text>
						<text class="info-value">{{ applyInfo.rejectReason }}</text>
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
				invoiceId: '',
				
				checkinInfo: {
					community: '美好人间',
					room: '6号楼6单元606',
					deposit: '2000元'
				},
				
				billList: [
					{
						id: 1,
						period: '第1期房租',
						status: 'paid',
						statusText: '已支付',
						amount: '2000元',
						dateRange: '2025年10月20日至2025年11月30日'
					},
					{
						id: 2,
						period: '第1期房租',
						status: 'paid',
						statusText: '已支付',
						amount: '2000元',
						dateRange: '2025年10月20日至2025年11月30日'
					}
				],
				
				applyInfo: {
					status: '美好人间',
					rejectReason: '填写信息不正确'
				}
			}
		},
		onLoad(options) {
			if (options.id) {
				this.invoiceId = options.id
			}
			this.loadData()
		},
		methods: {
			// 加载数据
			loadData() {
				// TODO: 调用API获取订单详情数据
				console.log('加载订单详情，ID:', this.invoiceId)
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
	.info-label1 {
		width: 166rpx;
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
</style>

