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
			
			<!-- 费用信息卡片 -->
			<view class="card">
				<view class="card-header">
					<view class="card-indicator"></view>
					<text class="card-title">费用信息</text>
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
	</view>
</template>

<script>
	import { getCheckoutConfirm, getCheckoutDetail } from '@/api/checkout.js'

	export default {
		data() {
			return {
				housingType: '',
				applyId: '',

				formData: {
					applicant: '',
					applyTime: '',
					community: '',
					room: '',
					rentPeriod: '',
					checkoutDate: '',
					status: '',
					statusText: '',
					reason: ''
				},

				equipmentList: [],

				feeData: {
					deposit: '0',
					rent: '0',
					water: '0',
					electric: '0',
					property: '0',
					gas: '0',
					heating: '0'
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
			this.loadCheckoutDetail()
		},
		methods: {
			// 加载申请详情
			async loadCheckoutDetail() {
				try {
					uni.showLoading({
						title: '加载中...'
					})

					// 调用API获取退租申请详情（包含申请时间、审批状态等）
					const detailResponse = await getCheckoutDetail(this.applyId)
					uni.hideLoading()

					if (detailResponse.code === 200 && detailResponse.data) {
						const detailData = detailResponse.data

						// 映射申请状态
						const statusMap = {
							'0': { status: 'pending', text: '审批中' },
							'1': { status: 'approved', text: '审批通过' },
							'2': { status: 'rejected', text: '审批驳回' },
							'3': { status: 'cancelled', text: '已取消' },
							'4': { status: 'pending', text: '待确认' },
							'5': { status: 'approved', text: '已确认' }
						}
						const statusInfo = statusMap[detailData.applyStatus] || { status: '', text: '未知状态' }

						// 设置基本信息
						this.formData.applyTime = this.formatDateTime(detailData.applyTime) || ''
						this.formData.checkoutDate = this.formatDate(detailData.planCheckoutDate) || ''
						this.formData.status = statusInfo.status
						this.formData.statusText = statusInfo.text
						this.formData.reason = detailData.checkoutReason || ''

						// 设置费用信息（将 BigDecimal 转换为字符串）
						this.feeData = {
							deposit: '0', // 押金需要从合同获取
							rent: '0', // 租金需要从合同获取
							water: String(detailData.waterFee || '0'),
							electric: String(detailData.electricFee || '0'),
							property: String(detailData.propertyFee || '0'),
							gas: String(detailData.gasFee || '0'),
							heating: String(detailData.heatingFee || '0')
						}
					}

					// 再调用 getCheckoutConfirm 获取房源信息和设备列表
					uni.showLoading({ title: '加载中...' })
					const confirmResponse = await getCheckoutConfirm(this.applyId)
					uni.hideLoading()

					if (confirmResponse.code === 200 && confirmResponse.data) {
						const confirmData = confirmResponse.data

						// 退租信息
						this.formData.applicant = confirmData.applicant || '申请人'
						this.formData.community = confirmData.community || '未知小区'
						this.formData.room = confirmData.room || '未知房间'
						this.formData.checkoutDate = this.formatDate(confirmData.planCheckoutDate) || this.formData.checkoutDate
						this.formData.reason = confirmData.checkoutReason || this.formData.reason

						// 合同期限（退租账期）
						if (confirmData.startDate && confirmData.endDate) {
							this.formData.rentPeriod = `${confirmData.startDate}至${confirmData.endDate}`
						}

						// 更新费用信息（押金和租金从合同获取）
						this.feeData.deposit = String(confirmData.deposit || '0')
						this.feeData.rent = String(confirmData.rentPrice || '0')

						// 配套设备列表（从后端返回的 equipmentList 转换格式）
						this.equipmentList = (confirmData.equipmentList || []).map(item => ({
							name: item.name,
							status: item.status === '完好' ? '完好' : '损坏'
						}))
					}
				} catch (error) {
					uni.hideLoading()
					console.error('加载退租详情失败:', error)
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

			// 格式化日期时间
			formatDateTime(dateStr) {
				if (!dateStr) return ''
				// 处理ISO格式日期字符串
				if (typeof dateStr === 'string' && dateStr.includes('T')) {
					const date = new Date(dateStr)
					const year = date.getFullYear()
					const month = String(date.getMonth() + 1).padStart(2, '0')
					const day = String(date.getDate()).padStart(2, '0')
					const hour = String(date.getHours()).padStart(2, '0')
					const minute = String(date.getMinutes()).padStart(2, '0')
					const second = String(date.getSeconds()).padStart(2, '0')
					return `${year}年${month}月${day}日 ${hour}:${minute}:${second}`
				}
				// 处理Date对象
				if (dateStr instanceof Date) {
					const year = dateStr.getFullYear()
					const month = String(dateStr.getMonth() + 1).padStart(2, '0')
					const day = String(dateStr.getDate()).padStart(2, '0')
					const hour = String(dateStr.getHours()).padStart(2, '0')
					const minute = String(dateStr.getMinutes()).padStart(2, '0')
					const second = String(dateStr.getSeconds()).padStart(2, '0')
					return `${year}年${month}月${day}日 ${hour}:${minute}:${second}`
				}
				// 已经是格式化的字符串
				return dateStr
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
</style>

