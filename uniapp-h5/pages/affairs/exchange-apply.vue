<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 选择入驻房源 -->
			<view class="card">
				<view class="card-header">
					<view class="card-indicator"></view>
					<text class="card-title">选择入驻房源</text>
				</view>
				<view class="contract-list" v-if="contractList.length > 0">
					<view
						class="contract-item"
						v-for="(item, index) in contractList"
						:key="index"
						:class="{ 'selected': formData.contractId === item.contractId }"
						@click="selectContract(item)"
					>
						<view class="contract-info">
							<text class="contract-community">{{ item.community || '未知小区' }}</text>
							<text class="contract-room">{{ item.room || '未知房间' }}</text>
						</view>
						<view class="contract-right">
							<view class="radio-icon" :class="{ 'checked': formData.contractId === item.contractId }">
								<text v-if="formData.contractId === item.contractId" class="radio-check">✓</text>
							</view>
						</view>
					</view>
				</view>
				<view class="empty-hint" v-else>
					<text>暂无可调换的房源</text>
				</view>
			</view>

			<!-- 申请调换时间 -->
			<view class="card">
				<view class="card-row" @click="showDatePickerPopup = true">
					<view class="card-left">
						<view class="card-indicator"></view>
						<text class="card-title">申请调换时间</text>
					</view>
					<view class="date-select">
						<image class="date-icon" src="/static/选择日期@2x.png" mode="aspectFit"></image>
						<text class="date-text" :class="{ 'placeholder': !formData.applyDate }">{{ formData.applyDate || '选择日期' }}</text>
					</view>
				</view>
			</view>

			<!-- 调换原因 -->
			<view class="card">
				<view class="card-header">
					<view class="card-indicator"></view>
					<text class="card-title">调换原因</text>
				</view>
				<view class="reason-wrap">
					<textarea
						class="reason-textarea"
						v-model="formData.reason"
						placeholder="请输入你的调换原因"
						placeholder-class="placeholder"
						maxlength="200"
					></textarea>
				</view>
			</view>

			<!-- 底部提交按钮 -->
		<view class="bottom-btn-container">
			<view class="bottom-btn" @click="handleSubmit">
				<text class="bottom-btn-text">提交</text>
			</view>
		</view>
		</scroll-view>



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
	import { getConfirmedContractList, submitExchange } from '@/api/exchange.js'

	export default {
		data() {
			return {
				housingType: '',
				tenantId: 1, // TODO: 从登录信息获取租户ID
				loading: false,
				contractList: [],

				formData: {
					contractId: null,
					applyDate: '',
					reason: ''
				},

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
			this.initDatePicker()
			this.loadContractList()
		},
		methods: {
			// 加载已确认的合同列表
			async loadContractList() {
				try {
					this.loading = true
					const response = await getConfirmedContractList(this.tenantId)

					if (response.code === 200) {
						this.contractList = response.data || []
					} else {
						this.contractList = []
						uni.showToast({
							title: response.msg || '加载失败',
							icon: 'none'
						})
					}
				} catch (error) {
					console.error('获取合同列表失败:', error)
					this.contractList = []
					uni.showToast({
						title: '加载失败，请重试',
						icon: 'none'
					})
				} finally {
					this.loading = false
				}
			},

			// 选择合同
			selectContract(item) {
				this.formData.contractId = item.contractId
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

				// 默认选择今天
				const today = new Date()
				this.datePickerValue = [
					this.years.indexOf(today.getFullYear()),
					today.getMonth(),
					today.getDate() - 1
				]
			},

			// 日期变化
			onDateChange(e) {
				this.datePickerValue = e.detail.value
			},

			// 确认日期
			confirmDate() {
				const year = this.years[this.datePickerValue[0]]
				const month = String(this.months[this.datePickerValue[1]]).padStart(2, '0')
				const day = String(this.days[this.datePickerValue[2]]).padStart(2, '0')
				this.formData.applyDate = `${year}-${month}-${day}`
				this.showDatePickerPopup = false
			},

			// 提交申请
			async handleSubmit() {
				if (!this.formData.contractId) {
					uni.showToast({
						title: '请选择入驻房源',
						icon: 'none'
					})
					return
				}

				if (!this.formData.applyDate) {
					uni.showToast({
						title: '请选择申请调换时间',
						icon: 'none'
					})
					return
				}

				if (!this.formData.reason) {
					uni.showToast({
						title: '请输入调换原因',
						icon: 'none'
					})
					return
				}

				try {
					uni.showLoading({
						title: '提交中...'
					})

					const response = await submitExchange({
						contractId: this.formData.contractId,
						applyDate: this.formData.applyDate,
						reason: this.formData.reason
					})

					uni.hideLoading()

					if (response.code === 200) {
						uni.showToast({
							title: '提交成功',
							icon: 'success'
						})
						setTimeout(() => {
							uni.navigateBack()
						}, 1500)
					} else {
						uni.showToast({
							title: response.msg || '提交失败',
							icon: 'none'
						})
					}
				} catch (error) {
					uni.hideLoading()
					console.error('提交失败:', error)
					uni.showToast({
						title: '提交失败，请重试',
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
		padding: 24rpx;
		padding-bottom: 160rpx;
		box-sizing: border-box;
	}

	.card {
		width: 702rpx;
		border-radius: 20rpx;
		background: #ffffff;
		padding: 26rpx 0rpx;
		margin: 0 auto 24rpx;
		box-sizing: border-box;
	}

	.card-row {
		display: flex;
		align-items: center;
	}

	.card-left {
		display: flex;
		align-items: center;
	}

	.card-header {
		display: flex;
		align-items: center;
		margin-bottom: 24rpx;
		padding: 0 28rpx;
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

	/* 合同列表 */
	.contract-list {
		padding: 0 28rpx;
	}

	.contract-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 24rpx;
		margin-bottom: 16rpx;
		border-radius: 16rpx;
		background: #f8f9fc;
		border: 2rpx solid transparent;
		transition: all 0.3s;
	}

	.contract-item.selected {
		background: #e8f2ff;
		border-color: #0f73ff;
	}

	.contract-info {
		display: flex;
		flex-direction: column;
		gap: 8rpx;
	}

	.contract-community {
		color: #1a1a1a;
		font-size: 28rpx;
		font-weight: 500;
	}

	.contract-room {
		color: #666666;
		font-size: 24rpx;
	}

	.contract-right {
		display: flex;
		align-items: center;
		gap: 16rpx;
		flex-shrink: 0;
	}

	.contract-rent {
		color: #e5252b;
		font-size: 26rpx;
		font-weight: 500;
		flex-shrink: 0;
		min-width: 120rpx;
		text-align: right;
	}

	.radio-icon {
		width: 40rpx;
		height: 40rpx;
		border-radius: 50%;
		border: 2rpx solid #d0d0d0;
		display: flex;
		align-items: center;
		justify-content: center;
		transition: all 0.3s;
	}

	.radio-icon.checked {
		background: #0f73ff;
		border-color: #0f73ff;
	}

	.radio-check {
		color: #ffffff;
		font-size: 24rpx;
		font-weight: bold;
	}

	.empty-hint {
		padding: 40rpx 28rpx;
		text-align: center;
		color: #999999;
		font-size: 26rpx;
	}

	/* 日期选择 */
	.date-select {
		display: flex;
		align-items: center;
	}

	.date-icon {
		width: 28rpx;
		height: 28rpx;
		margin-right: 16rpx;
		margin-top: -2rpx;
		display: block;
	}

	.date-text {
		color: #999999;
		font-size: 26rpx;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 28rpx;
		height: 28rpx;
	}

	.placeholder {
		color: #999999;
	}

	/* 原因输入 */
	.reason-wrap {
		width: 100%;
		min-height: 150rpx;
		padding: 0 38rpx;
		box-sizing: border-box;
	}

	.reason-textarea {
		width: 100%;
		min-height: 150rpx;
		font-size: 26rpx;
		color: #1a1a1a;
		line-height: 40rpx;
	}

	/* 底部按钮 */
	.bottom-btn-container {
		padding: 48rpx 24rpx;
		background: #f5f6fc;
	}

	.bottom-btn {
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
