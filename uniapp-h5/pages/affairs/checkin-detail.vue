<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 入住信息卡片 -->
			<view class="card">
				<view class="card-header">
					<view class="card-indicator"></view>
					<text class="card-title">入住信息</text>
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
					<text class="form-label">入住日期</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ formData.checkinDate }}</text>
					</view>
				</view>

				<view class="form-row">
					<text class="form-label">实际入住日期</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ formData.actualCheckinDate || '未填写' }}</text>
					</view>
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
						<text class="form-value">{{ formData.applyTime || '-' }}</text>
					</view>
				</view>

				<view class="form-row">
					<text class="form-label">用户确认时间</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ formData.checkinTime || '未确认' }}</text>
					</view>
				</view>

				<view class="form-row">
					<text class="form-label">审核状态</text>
					<view class="form-value-wrap">
						<text class="form-value" :class="getStatusClass(formData.status)">{{ formData.statusText }}</text>
					</view>
				</view>
			</view>

			<!-- 配套设备卡片 -->
			<view class="card" v-if="equipmentList.length > 0">
				<view class="card-header">
					<view class="card-indicator"></view>
					<text class="card-title">配套设备确认</text>
				</view>

				<!-- 设备列表 -->
				<view class="equipment-row" v-for="(item, index) in equipmentList" :key="index">
					<view class="equipment-check">
						<image class="check-icon" src="/static/多选-选中@2x.png"></image>
					</view>
					<text class="equipment-name">{{ item.name }}</text>
					<view class="equipment-status-badge" :class="item.status === 'good' ? 'good' : 'damaged'">
						<text class="status-text">{{ item.status === 'good' ? '完好' : '破损' }}</text>
					</view>
				</view>
			</view>

			<!-- 租户签字卡片 -->
			<view class="card" v-if="signatureUrl">
				<view class="card-header">
					<view class="card-indicator"></view>
					<text class="card-title">租户签字</text>
				</view>
				<view class="signature-container">
					<image class="signature-image" :src="signatureUrl" mode="aspectFit"></image>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import { getCheckInDetail } from '@/api/checkin.js'

	export default {
		data() {
			return {
				housingType: '',
				checkinId: '',
				loading: false,

				formData: {
					community: '',
					room: '',
					checkinDate: '',
					actualCheckinDate: '',
					applicant: '',
					applyTime: '',
					checkinTime: '',
					status: '',
					statusText: ''
				},

				equipmentList: [],
				signatureUrl: ''
			}
		},
		onLoad(options) {
			if (options.type) {
				this.housingType = options.type
			}
			if (options.id) {
				this.checkinId = options.id
			}
			this.loadCheckinDetail()
		},
		methods: {
			// 加载申请详情
			async loadCheckinDetail() {
				try {
					this.loading = true

					const response = await getCheckInDetail(this.checkinId)

					if (response.code === 200 && response.data) {
						const data = response.data

						// 解析备注中的基本信息
						const remark = data.remark || ''
						this.formData.community = this.extractInfo(remark, '项目：') || '未知小区'
						this.formData.room = this.extractInfo(remark, '房间：') || '未知房间'
						this.formData.checkinDate = data.checkinDate || ''
						this.formData.actualCheckinDate = data.actualCheckinDate || ''
						this.formData.applicant = data.userName || '租户'
						this.formData.applyTime = data.createTime ? data.createTime.substring(0, 19) : ''
						this.formData.checkinTime = data.checkinTime ? data.checkinTime.substring(0, 19) : ''

						// 状态映射
						const statusMap = {
							'0': 'pending',
							'1': 'approved',
							'2': 'confirm',
							'3': 'rejected',
							'4': 'confirmed'
						}
						const statusTextMap = {
							'0': '待办理',
							'1': '审批中',
							'2': '待入住确认',
							'3': '已拒绝',
							'4': '已入住确认'
						}
						this.formData.status = statusMap[data.status] || 'pending'
						this.formData.statusText = statusTextMap[data.status] || '未知'

						// 解析设施确认信息
						this.parseFacilities(remark)

						// 租户签字
						if (data.tenantSignature) {
							this.signatureUrl = this.getImageUrl(data.tenantSignature)
						}
					} else {
						uni.showToast({
							title: response.msg || '获取详情失败',
							icon: 'none'
						})
						setTimeout(() => {
							uni.navigateBack()
						}, 1500)
					}
				} catch (error) {
					console.error('获取入住详情失败:', error)
					uni.showToast({
						title: '获取详情失败',
						icon: 'none'
					})
					setTimeout(() => {
						uni.navigateBack()
					}, 1500)
				} finally {
					this.loading = false
				}
			},

			// 从备注中提取信息
			extractInfo(remark, key) {
				if (!remark || !key) return ''
				const index = remark.indexOf(key)
				if (index !== -1) {
					const start = index + key.length
					let end = remark.indexOf('|', start)
					if (end === -1) end = remark.length
					return remark.substring(start, end).trim()
				}
				return ''
			},

			// 解析设施确认信息
			parseFacilities(remark) {
				this.equipmentList = []
				if (!remark || !remark.includes('设施确认')) {
					return
				}
				try {
					const facilitiesStr = remark.substring(remark.indexOf('设施确认：') + 5)
					const facilities = JSON.parse(facilitiesStr)
					this.equipmentList = facilities.map(f => ({
						name: f.name,
						status: f.status
					}))
				} catch (e) {
					console.error('解析设施信息失败:', e)
				}
			},

			// 获取图片完整URL
			getImageUrl(url) {
				if (!url) return ''
				// 如果已经是完整URL，直接返回
				if (url.startsWith('http://') || url.startsWith('https://')) {
					return url
				}
				// 拼接baseUrl
				const baseUrl = process.env.VUE_APP_BASE_API || '/dev-api'
				return baseUrl + (url.startsWith('/') ? url : '/' + url)
			},

			// 获取状态样式类
			getStatusClass(status) {
				const classMap = {
					'pending': 'status-pending',
					'approved': 'status-approved',
					'rejected': 'status-rejected',
					'cancelled': 'status-cancelled',
					'confirm': 'status-confirm',
					'confirmed': 'status-confirmed'
				}
				return classMap[status] || ''
			}
		}
	}
</script>

<style scoped>
	.page {
		width: 100%;
		min-height: 100vh;
		background-color: #f5f6fc;
		display: flex;
		flex-direction: column;
	}

	.scroll-content {
		flex: 1;
		padding: 24rpx;
		padding-bottom: 40rpx;
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

	.status-confirm {
		color: #1281ff;
	}

	.status-confirmed {
		color: #52c41a;
	}

	/* 配套设备 */
	.equipment-row {
		display: flex;
		align-items: center;
		margin: 0 40rpx;
		margin-bottom: 20rpx;
	}

	.equipment-check {
		display: flex;
		align-items: center;
		margin-right: 16rpx;
	}

	.check-icon {
		width: 30rpx;
		height: 30rpx;
		display: block;
	}

	.equipment-name {
		flex: 1;
		color: #333333;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 40rpx;
	}

	.equipment-status-badge {
		padding: 8rpx 20rpx;
		border-radius: 8rpx;
		font-size: 24rpx;
	}

	.equipment-status-badge.good {
		background: #e6f7e6;
	}

	.equipment-status-badge.good .status-text {
		color: #52c41a;
	}

	.equipment-status-badge.damaged {
		background: #ffe6e6;
	}

	.equipment-status-badge.damaged .status-text {
		color: #fa5740;
	}

	/* 签字图片 */
	.signature-container {
		padding: 20rpx 40rpx;
		display: flex;
		justify-content: center;
	}

	.signature-image {
		width: 400rpx;
		height: 200rpx;
		border: 1rpx solid #e5e5e5;
		border-radius: 8rpx;
	}
</style>

