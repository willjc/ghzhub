<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 退租信息卡片 -->
			<view class="card">
				<view class="card-header">
					<view class="card-indicator"></view>
					<text class="card-title">退租信息</text>
				</view>
				
				<view class="form-row">
					<text class="form-label">申请人</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ formData.applicant }}</text>
					</view>
				</view>
				
				<view class="form-row">
					<text class="form-label">申请时间</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ formData.applyTime }}</text>
					</view>
				</view>
				
				<view class="form-row">
					<text class="form-label">小区名称</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ formData.community }}</text>
					</view>
				</view>
				
				<view class="form-row">
					<text class="form-label">房间地址</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ formData.room }}</text>
					</view>
				</view>
				
				<view class="form-row">
					<text class="form-label">退租账期</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ formData.rentPeriod }}</text>
					</view>
				</view>
				
				<view class="form-row">
					<text class="form-label">退租日期</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ formData.checkoutDate }}</text>
					</view>
				</view>
				
				<view class="form-row">
					<text class="form-label">审核状态</text>
					<view class="form-value-wrap">
						<text class="form-value" :class="getStatusClass(formData.status)">{{ formData.statusText }}</text>
					</view>
				</view>
				
				<view class="form-row reason-row">
					<text class="form-label">退租原因</text>
					<view class="form-value-wrap">
						<text class="form-value reason-text">{{ formData.reason }}</text>
					</view>
				</view>
			</view>
			
			<!-- 配套设备卡片 -->
			<view class="card">
				<view class="card-header">
					<view class="card-indicator"></view>
					<text class="card-title">配套设备</text>
				</view>
				
				<view class="equipment-list">
					<view class="equipment-row" v-for="(item, index) in equipmentList" :key="index">
						<text class="equipment-name">{{ item.name }}：</text>
						<text class="equipment-status">{{ item.status }}</text>
					</view>
				</view>
			</view>
			
			<!-- 配套设备费用卡片 -->
			<view class="card">
				<view class="card-header">
					<view class="card-indicator"></view>
					<text class="card-title">配套设备</text>
				</view>
				
				<view class="fee-list">
					<view class="fee-row">
						<view class="fee-item">
							<text class="fee-label">押金：</text>
							<text class="fee-value">{{ feeData.deposit }}元</text>
						</view>
						<view class="fee-item">
							<text class="fee-label">租金：</text>
							<text class="fee-value">{{ feeData.rent }}元</text>
						</view>
						<view class="fee-item">
							<text class="fee-label">水费：</text>
							<text class="fee-value">{{ feeData.water }}元</text>
						</view>
					</view>
					<view class="fee-row">
						<view class="fee-item">
							<text class="fee-label">电费：</text>
							<text class="fee-value">{{ feeData.electric }}元</text>
						</view>
						<view class="fee-item">
							<text class="fee-label">物业费：</text>
							<text class="fee-value">{{ feeData.property }}元</text>
						</view>
						<view class="fee-item">
							<text class="fee-label">燃气：</text>
							<text class="fee-value">{{ feeData.gas }}元</text>
						</view>
					</view>
					<view class="fee-row">
						<view class="fee-item">
							<text class="fee-label">暖气：</text>
							<text class="fee-value">{{ feeData.heating }}元</text>
						</view>
					</view>
				</view>
			</view>
		</scroll-view>
		
		<!-- 底部按钮 -->
		<view class="bottom-btn-container">
			<view class="bottom-btn" @click="handleConfirm">
				<text class="bottom-btn-text">确认退租</text>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				housingType: '',
				checkoutId: '',
				
				formData: {
					applicant: '张三',
					applyTime: '2025年2月2日 12:12:12',
					community: '美好人间',
					room: '6号楼6单元606',
					rentPeriod: '202512-16至2026-12-13',
					checkoutDate: '2025年2月2日',
					status: 'pending',
					statusText: '审批中',
					reason: '这里是退租原因，有原因和理由的文本展示，可换行大概30字左右'
				},
				
				equipmentList: [
					{ name: '冰箱', status: '完好' },
					{ name: '洗衣机', status: '完好' },
					{ name: '电视机', status: '完好' },
					{ name: '热水器', status: '完好' },
					{ name: '床', status: '完好' }
				],
				
				feeData: {
					deposit: '2000',
					rent: '2000',
					water: '100',
					electric: '62',
					property: '567',
					gas: '48',
					heating: '1356'
				}
			}
		},
		onLoad(options) {
			if (options.type) {
				this.housingType = options.type
			}
			if (options.id) {
				this.checkoutId = options.id
			}
			this.loadCheckoutDetail()
		},
		methods: {
			// 加载申请详情
			loadCheckoutDetail() {
				// TODO: 调用API获取申请详情
				console.log('加载退租确认详情，类型:', this.housingType, 'ID:', this.checkoutId)
			},
			
			// 获取状态样式类
			getStatusClass(status) {
				const classMap = {
					'pending': 'status-pending',
					'approved': 'status-approved',
					'rejected': 'status-rejected',
					'cancelled': 'status-cancelled'
				}
				return classMap[status] || ''
			},
			
			// 确认退租
			handleConfirm() {
				uni.showModal({
					title: '提示',
					content: '确定要确认退租吗？',
					success: (res) => {
						if (res.confirm) {
							uni.showLoading({
								title: '处理中...'
							})
							// TODO: 调用确认退租API
							setTimeout(() => {
								uni.hideLoading()
								uni.showToast({
									title: '退租成功',
									icon: 'success'
								})
								setTimeout(() => {
									uni.navigateBack({
										delta: 2
									})
								}, 1500)
							}, 1000)
						}
					}
				})
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
		padding: 24rpx;
		padding-bottom: 160rpx;
		box-sizing: border-box;
	}

	.card {
		width: 702rpx;
		border-radius: 20rpx;
		background: #ffffff;
		padding: 26rpx 0 10rpx 0;
		margin: 0 auto 24rpx;
		box-sizing: border-box;
	}

	.card-header {
		display: flex;
		align-items: center;
		margin-bottom: 37rpx;
	}

	.card-indicator {
		width: 12rpx;
		height: 34rpx;
		background: #0f73ff;
		margin-right: 26rpx;
	}

	.card-title {
		color: #171a1f;
		font-size: 32rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.form-row {
		display: flex;
		align-items: center;
		margin: 0 40rpx;
		margin-bottom: 28rpx;
	}

	.reason-row {
		align-items: flex-start;
	}

	.form-label {
		width: 166rpx;
		color: #333333;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.form-value-wrap {
		flex: 1;
	}

	.form-value {
		color: #1a1a1a;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.reason-text {
		line-height: 40rpx;
	}

	/* 状态颜色 */
	.status-pending {
		color: #ff8d1a;
	}

	.status-approved {
		color: #12a566;
	}

	.status-rejected {
		color: #fa5740;
	}

	.status-cancelled {
		color: #768394;
	}

	/* 配套设备 */
	.equipment-list {
		padding: 0 40rpx;
		display: flex;
		flex-wrap: wrap;
	}

	.equipment-row {
		display: flex;
		align-items: center;
		margin-right: 40rpx;
		margin-bottom: 20rpx;
	}

	.equipment-name {
		color: #333333;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.equipment-status {
		color: #333333;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	/* 费用信息 */
	.fee-list {
		padding: 0 40rpx;
	}

	.fee-row {
		display: flex;
		margin-bottom: 20rpx;
	}

	.fee-item {
		display: flex;
		align-items: center;
		margin-right: 20rpx;
		width: 220rpx;
	}

	.fee-label {
		color: #333333;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.fee-value {
		color: #333333;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
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

