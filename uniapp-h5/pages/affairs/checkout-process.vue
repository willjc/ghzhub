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
					<text class="form-label">租期</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ formData.rentPeriod }}</text>
					</view>
				</view>

				<view class="form-row">
					<text class="form-label">租金</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ formData.rent }}</text>
					</view>
				</view>

				<view class="form-row">
					<text class="form-label">押金</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ formData.deposit }}</text>
					</view>
				</view>

				<view class="form-row" @click="showDatePicker">
					<text class="form-label"><text class="required">*</text>计划退租日期</text>
					<view class="form-value-wrap">
						<text class="form-value placeholder" v-if="!formData.checkoutDate">请选择退租日期</text>
						<text class="form-value" v-else>{{ formData.checkoutDate }}</text>
					</view>
				</view>

				<view class=" reason-row" style="margin-left:40rpx;">
					<text class="form-label">退租原因</text>
				</view>

				<!-- 退租原因文本框 -->
				<view class="reason-wrap" >
					<textarea
						class="reason-textarea"
						v-model="formData.reason"
						placeholder="请输入，限制30个字"
						placeholder-class="placeholder"
						maxlength="30"
					></textarea>
				</view>
			</view>
		</scroll-view>

		<!-- 底部申请按钮 -->
		<view class="bottom-btn-container">
			<view class="bottom-btn" @click="handleSubmit">
				<text class="bottom-btn-text">确认退租</text>
			</view>
		</view>

		<!-- 日期选择器 -->
		<view class="picker-mask" v-if="showDatePickerPopup" @click="showDatePickerPopup = false"></view>
		<view class="picker-popup" :class="{ 'show': showDatePickerPopup }">
			<view class="picker-header">
				<text class="picker-cancel" @click="showDatePickerPopup = false">取消</text>
				<text class="picker-title">选择日期</text>
				<text class="picker-confirm" @click="confirmDate">确定</text>
			</view>
			<view class="picker-body">
				<picker-view class="picker-view" :value="datePickerValue" @change="onDateChange">
					<picker-view-column>
						<view class="picker-item" v-for="year in years" :key="year">
							<text class="picker-item-text">{{ year }}年</text>
						</view>
					</picker-view-column>
					<picker-view-column>
						<view class="picker-item" v-for="month in months" :key="month">
							<text class="picker-item-text">{{ month }}月</text>
						</view>
					</picker-view-column>
					<picker-view-column>
						<view class="picker-item" v-for="day in days" :key="day">
							<text class="picker-item-text">{{ day }}日</text>
						</view>
					</picker-view-column>
				</picker-view>
			</view>
		</view>
	</view>
</template>

<script>
	import { getCheckInDetail } from '@/api/checkin.js'

	export default {
		data() {
			return {
				housingType: '',
				recordId: '',
				tenantId: 1, // TODO: 从登录信息获取租户ID

				formData: {
					community: '',
					room: '',
					rentPeriod: '',
					rent: '',
					deposit: '',
					checkoutDate: '',
					reason: ''
				},

				// 用于提交的数据
				contractId: null,
				houseId: null,

				// 日期选择器
				showDatePickerPopup: false,
				datePickerValue: [0, 0, 0],
				years: [],
				months: [],
				days: []
			}
		},
		onLoad(options) {
			if (options.type) {
				this.housingType = options.type
			}
			if (options.recordId) {
				this.recordId = options.recordId
			}
			this.initDatePicker()
			this.loadCheckInDetail()
		},
		methods: {
			// 加载入住记录详情
			async loadCheckInDetail() {
				try {
					uni.showLoading({
						title: '加载中...'
					})

					const response = await getCheckInDetail(this.recordId)
					uni.hideLoading()

					if (response.code === 200 && response.data) {
						const data = response.data

						// 保存ID用于提交
						this.contractId = data.contractId
						this.houseId = data.houseId

						// 解析备注中的房源信息
						const remark = data.remark || ''
						this.formData.community = this.extractInfo(remark, '项目：') || '未知小区'
						this.formData.room = this.extractInfo(remark, '房间：') || '未知房间'
						this.formData.rentPeriod = this.extractInfo(remark, '租期：') || '-'
						this.formData.rent = this.extractInfo(remark, '月租金：') || '-'
						this.formData.deposit = this.extractInfo(remark, '押金：') || '-'
					} else {
						uni.showToast({
							title: '加载失败',
							icon: 'none'
						})
					}
				} catch (error) {
					uni.hideLoading()
					console.error('加载入住记录详情失败:', error)
					uni.showToast({
						title: '加载失败，请重试',
						icon: 'none'
					})
				}
			},

			// 从备注中提取信息
			extractInfo(remark, key) {
				const index = remark.indexOf(key)
				if (index !== -1) {
					const start = index + key.length
					let end = remark.indexOf('|', start)
					if (end === -1) end = remark.length
					return remark.substring(start, end).trim()
				}
				return ''
			},

			// 初始化日期选择器
			initDatePicker() {
				const currentYear = new Date().getFullYear()
				this.years = []
				for (let i = currentYear; i <= currentYear + 5; i++) {
					this.years.push(i)
				}
				this.months = Array.from({ length: 12 }, (_, i) => i + 1)
				this.days = Array.from({ length: 31 }, (_, i) => i + 1)
			},

			// 显示日期选择器
			showDatePicker() {
				this.datePickerValue = [0, 0, 0]
				this.showDatePickerPopup = true
			},

			// 日期变化
			onDateChange(e) {
				this.datePickerValue = e.detail.value
			},

			// 确认日期
			confirmDate() {
				const year = this.years[this.datePickerValue[0]]
				const month = this.months[this.datePickerValue[1]]
				const day = this.days[this.datePickerValue[2]]

				// 格式化为 yyyy-MM-dd 格式（后端要求的格式）
				const monthStr = month < 10 ? '0' + month : month
				const dayStr = day < 10 ? '0' + day : day
				this.formData.checkoutDate = `${year}-${monthStr}-${dayStr}`
				this.showDatePickerPopup = false
			},

			// 提交退租 - 跳转到签字页面
			handleSubmit() {
				// 表单验证
				if (!this.formData.checkoutDate) {
					uni.showToast({
						title: '请选择退租日期',
						icon: 'none'
					})
					return
				}

				// 准备传递给签字页面的数据
				const checkoutData = {
					recordId: this.recordId,
					contractId: this.contractId,
					houseId: this.houseId,
					tenantId: this.tenantId,
					planCheckoutDate: this.formData.checkoutDate,
					checkoutReason: this.formData.reason
				}

				// 跳转到签字页面（传递退租办理数据）
				uni.navigateTo({
					url: `/pages/signature/index?mode=checkout_apply&type=${this.housingType}&data=${encodeURIComponent(JSON.stringify(checkoutData))}`
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
		padding: 26rpx 0 28rpx 0;
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
		margin-bottom: 16rpx;
	}

	.required {
		color: #ff0000;
		font-size: 28rpx;
	}

	.form-label {
		width: 200rpx;
		color: #333333;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		padding-bottom: 28rpx;
	}

	.form-value-wrap {
		flex: 1;
		padding-bottom: 28rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.form-value {
		color: #1a1a1a;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.placeholder {
		color: #999999;
	}

	/* 退租原因文本框 */
	.reason-wrap {
		margin: 0 40rpx;
	}

	.reason-textarea {
		width: 622rpx;
		height: 121rpx;
		border-radius: 13rpx;
		border: 1rpx solid #e1eaf2;
		padding: 16rpx;
		box-sizing: border-box;
		color: #1a1a1a;
		font-size: 26rpx;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	/* 底部按钮 */
	.bottom-btn-container {
		position: fixed;
		bottom: 24rpx;
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

	/* Picker弹窗样式 */
	.picker-mask {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.5);
		z-index: 998;
	}

	.picker-popup {
		position: fixed;
		left: 0;
		right: 0;
		bottom: 0;
		background: #ffffff;
		border-radius: 24rpx 24rpx 0 0;
		z-index: 999;
		transform: translateY(100%);
		transition: transform 0.3s ease;
	}

	.picker-popup.show {
		transform: translateY(0);
	}

	.picker-header {
		width: 750rpx;
		height: 95rpx;
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 0 32rpx;
		box-sizing: border-box;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.picker-cancel {
		color: rgba(0, 0, 0, 0.6);
		font-size: 32rpx;
		font-weight: 400;
		font-family: "Roboto", "PingFang SC", sans-serif;
		line-height: 48rpx;
	}

	.picker-title {
		color: rgba(0, 0, 0, 0.9);
		font-size: 32rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 48rpx;
	}

	.picker-confirm {
		color: #1281ff;
		font-size: 32rpx;
		font-weight: 400;
		font-family: "Roboto", "PingFang SC", sans-serif;
		line-height: 48rpx;
	}

	.picker-body {
		width: 750rpx;
		height: 400rpx;
		background: #ffffff;
	}

	.picker-view {
		width: 100%;
		height: 100%;
	}

	.picker-item {
		height: 80rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.picker-item-text {
		color: #000000;
		font-size: 32rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 80rpx;
	}
</style>
