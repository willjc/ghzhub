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
					<text class="form-label">退租日期</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ formData.checkoutDate }}</text>
					</view>
				</view>

				<view class="form-row">
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
					<view class="empty-equipment" v-if="equipmentList.length === 0">
						<text class="empty-text">暂无设备信息</text>
					</view>
				</view>
			</view>

			<!-- 费用明细卡片 -->
			<view class="card">
				<view class="card-header">
					<view class="card-indicator"></view>
					<text class="card-title">费用明细</text>
				</view>

				<view class="fee-list">
					<!-- 退款总额单独一行 -->
					<view class="fee-row-single">
						<text class="fee-label-large">退款总额：</text>
						<text class="fee-value-large highlight">{{ feeData.depositRefund }}元</text>
					</view>
					<view class="fee-row">
						<view class="fee-item">
							<text class="fee-label">水费：</text>
							<text class="fee-value">{{ feeData.waterFee }}元</text>
						</view>
						<view class="fee-item">
							<text class="fee-label">电费：</text>
							<text class="fee-value">{{ feeData.electricFee }}元</text>
						</view>
						<view class="fee-item">
							<text class="fee-label">燃气费：</text>
							<text class="fee-value">{{ feeData.gasFee }}元</text>
						</view>
					</view>
					<view class="fee-row">
						<view class="fee-item">
							<text class="fee-label">暖气费：</text>
							<text class="fee-value">{{ feeData.heatingFee }}元</text>
						</view>
						<view class="fee-item">
							<text class="fee-label">物业费：</text>
							<text class="fee-value">{{ feeData.propertyFee }}元</text>
						</view>
						<view class="fee-item">
							<text class="fee-label">损坏扣款：</text>
							<text class="fee-value deduct">{{ feeData.damageDeduction }}元</text>
						</view>
					</view>
					<view class="fee-row-desc" v-if="feeData.damageDescription">
						<text class="desc-label">损坏说明：</text>
						<text class="desc-text">{{ feeData.damageDescription }}</text>
					</view>
				</view>
			</view>
		</scroll-view>

		<!-- 底���按钮 -->
		<view class="bottom-btn-container">
			<view class="bottom-btn-group">
				<view class="bottom-btn btn-confirm" @click="handleConfirm">
					<text class="bottom-btn-text">确认</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import { getCheckoutConfirm, submitCheckoutConfirm } from '@/api/checkout.js'

	export default {
		data() {
			return {
				housingType: '',
				applyId: '',

				formData: {
					community: '',
					room: '',
					checkoutDate: '',
					reason: ''
				},

				equipmentList: [],

				feeData: {
					depositRefund: '0',
					waterFee: '0',
					electricFee: '0',
					gasFee: '0',
					heatingFee: '0',
					propertyFee: '0',
					damageDeduction: '0',
					damageDescription: ''
				},

				meterData: {
					water: '',
					electric: '',
					gas: '',
					keyReturned: false
				}
			}
		},
		onLoad(options) {
			if (options.type) {
				this.housingType = options.type
			}
			if (options.applyId) {
				this.applyId = options.applyId
			}
			this.loadCheckoutConfirm()
		},
		methods: {
			// 加载退租确认信息
			async loadCheckoutConfirm() {
				try {
					uni.showLoading({
						title: '加载中...'
					})

					const response = await getCheckoutConfirm(this.applyId)
					uni.hideLoading()

					if (response.code === 200 && response.data) {
						const data = response.data

						// 退租信息
						this.formData.checkoutDate = this.formatDate(data.planCheckoutDate) || ''
						this.formData.reason = data.checkoutReason || ''
						this.formData.community = data.community || '未知小区'
						this.formData.room = data.room || '未知房间'

						// 费用信息（将 BigDecimal 转换为字符串）
						this.feeData = {
							depositRefund: String(data.depositRefund || '0'),
							waterFee: String(data.waterFee || '0'),
							electricFee: String(data.electricFee || '0'),
							gasFee: String(data.gasFee || '0'),
							heatingFee: String(data.heatingFee || '0'),
							propertyFee: String(data.propertyFee || '0'),
							damageDeduction: String(data.damageDeduction || '0'),
							damageDescription: data.damageDescription || ''
						}

						// 表读数
						this.meterData = {
							water: data.meterReadingWater || '',
							electric: data.meterReadingElectric || '',
							gas: data.meterReadingGas || '',
							keyReturned: data.keyReturned || false
						}

						// 配套设备列表（从后端返回的 facilitiesStatusList 转换格式）
						this.equipmentList = (data.equipmentList || []).map(item => ({
							name: item.name,
							status: item.status === '完好' ? '完好' : '损坏'
						}))
					} else {
						uni.showToast({
							title: response.msg || '加载失败',
							icon: 'none'
						})
					}
				} catch (error) {
					uni.hideLoading()
					console.error('加载退租确认信息失败:', error)
					uni.showToast({
						title: '加载失败，请重试',
						icon: 'none'
					})
				}
			},

			// 格式化日期
			formatDate(dateStr) {
				if (!dateStr) return ''
				// 处理ISO格式日期字符串
				if (typeof dateStr === 'string' && dateStr.includes('T')) {
					return dateStr.split('T')[0]
				}
				// 处理Date对象
				if (dateStr instanceof Date) {
					const year = dateStr.getFullYear()
					const month = String(dateStr.getMonth() + 1).padStart(2, '0')
					const day = String(dateStr.getDate()).padStart(2, '0')
					return `${year}-${month}-${day}`
				}
				// 已经是格式化的字符串
				return dateStr
			},

			// 确认退租 - 需要签字
			handleConfirm() {
				// 跳转到签字页面
				uni.navigateTo({
					url: `/pages/signature/index?applyId=${this.applyId}&type=${this.housingType}`
				})
			},

			// 拒绝退租
			handleReject() {
				uni.showModal({
					title: '提示',
					content: '确定要拒绝退租申请吗？',
					success: (res) => {
						if (res.confirm) {
							// TODO: 调用拒绝退租API
							uni.showToast({
								title: '拒绝成功',
								icon: 'success'
							})
							setTimeout(() => {
								uni.navigateBack({
									delta: 2
								})
							}, 1500)
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

	.form-label {
		width: 180rpx;
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

	.empty-equipment {
		width: 100%;
		padding: 40rpx 0;
		text-align: center;
	}

	.empty-text {
		color: #999999;
		font-size: 26rpx;
	}

	/* 费用信息 */
	.fee-list {
		padding: 0 40rpx;
	}

	/* 押金退款单独一行 */
	.fee-row-single {
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 20rpx 0;
		margin-bottom: 20rpx;
		background: #fff5f5;
		border-radius: 12rpx;
	}

	.fee-row {
		display: flex;
		margin-bottom: 20rpx;
	}

	.fee-row-desc {
		margin: 0 40rpx;
		margin-bottom: 20rpx;
		padding: 16rpx;
		background: #f5f6fc;
		border-radius: 12rpx;
	}

	.fee-item {
		display: flex;
		align-items: center;
		margin-right: 20rpx;
		flex: 1;
	}

	/* 押金退款大字体样式 */
	.fee-label-large {
		color: #333333;
		font-size: 32rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.fee-value-large {
		color: #e5252b;
		font-size: 36rpx;
		font-weight: bold;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		margin-left: 10rpx;
	}

	.fee-label {
		color: #333333;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		flex-shrink: 0;
	}

	.fee-value {
		color: #333333;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.fee-value.highlight {
		color: #e5252b;
		font-weight: 600;
		font-size: 28rpx;
	}

	.fee-value.deduct {
		color: #fa5740;
		font-weight: 500;
	}

	.desc-label {
		color: #666666;
		font-size: 26rpx;
		margin-right: 10rpx;
	}

	.desc-text {
		color: #1a1a1a;
		font-size: 26rpx;
		line-height: 40rpx;
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

	.bottom-btn-group {
		display: flex;
		gap: 20rpx;
		justify-content: center;
	}

	.bottom-btn {
		width: 340rpx;
		height: 92rpx;
		border-radius: 20rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.btn-reject {
		background: #ffffff;
		border: 2rpx solid #e5252b;
	}

	.btn-reject .bottom-btn-text {
		color: #e5252b;
	}

	.btn-confirm {
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
	}

	.btn-confirm .bottom-btn-text {
		color: #ffffff;
	}

	.bottom-btn-text {
		font-size: 36rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}
</style>
