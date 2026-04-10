<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 选择入驻房源 -->
			<view class="card">
				<view class="card-header">
					<view class="card-indicator"></view>
					<text class="card-title">选择房源</text>
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
					<text>暂无可添加合租户的房源</text>
				</view>
			</view>

			<!-- 合住人信息卡片 -->
			<view class="card">
				<view class="card-header">
					<view class="card-indicator"></view>
					<text class="card-title">合住人信息</text>
				</view>

				<view class="form-row">
					<text class="form-label"><text class="required">*</text>姓名</text>
					<view class="form-value-wrap">
						<input class="form-input" type="text" v-model="formData.name" placeholder="请输入合住人姓名" placeholder-class="placeholder" />
					</view>
				</view>

				<view class="form-row">
					<text class="form-label"><text class="required">*</text>身份证号</text>
					<view class="form-value-wrap">
						<input class="form-input" type="idcard" v-model="formData.idCard" placeholder="请输入身份证号" placeholder-class="placeholder" />
					</view>
				</view>

				<view class="form-row">
					<text class="form-label"><text class="required">*</text>联系电话</text>
					<view class="form-value-wrap">
						<input class="form-input" type="number" v-model="formData.phone" placeholder="请输入联系电话" placeholder-class="placeholder" />
					</view>
				</view>

				<view class="form-row" @click="showRelationPicker">
					<text class="form-label"><text class="required">*</text>关系</text>
					<view class="form-value-wrap">
						<text class="form-value placeholder" v-if="!formData.relation">请选择关系</text>
						<text class="form-value" v-else>{{ formData.relation }}</text>
					</view>
				</view>

				<view class="form-row" @click="showDatePickerPopup = true">
					<text class="form-label"><text class="required">*</text>入住日期</text>
					<view class="form-value-wrap">
						<text class="form-value placeholder" v-if="!formData.checkinDate">请选择入住日期</text>
						<text class="form-value" v-else>{{ formData.checkinDate }}</text>
					</view>
				</view>
			</view>
		</scroll-view>

		<!-- 底部提交按钮 -->
		<view class="bottom-btn-container">
			<view class="bottom-btn" @click="handleSubmit">
				<text class="bottom-btn-text">提交申请</text>
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

		<!-- 关系选择器 -->
		<view class="picker-mask" v-if="showRelationPickerPopup" @click="showRelationPickerPopup = false"></view>
		<view class="picker-popup" :class="{ 'show': showRelationPickerPopup }">
			<view class="picker-header">
				<text class="picker-cancel" @click="showRelationPickerPopup = false">取消</text>
				<text class="picker-title">选择关系</text>
				<text class="picker-confirm" @click="confirmRelation">确定</text>
			</view>
			<view class="picker-body">
				<picker-view class="picker-view" :value="relationPickerValue" @change="onRelationChange">
					<picker-view-column>
						<view class="picker-item" v-for="(item, index) in relationOptions" :key="index">
							<text class="picker-item-text">{{ item }}</text>
						</view>
					</picker-view-column>
				</picker-view>
			</view>
		</view>
	</view>
</template>

<script>
	import { getConfirmedContractList, submitCohabitant } from '@/api/cohabitant.js'

	export default {
		data() {
			return {
				housingType: '',
				tenantId: null, // 从登录信息获取租户ID
				loading: false,
				contractList: [],

				formData: {
					contractId: null,
					name: '',
					idCard: '',
					phone: '',
					relation: '',
					checkinDate: ''
				},

				// 日期选择器
				showDatePickerPopup: false,
				datePickerValue: [0, 0, 0],
				years: [],
				months: [],
				days: [],

				// 关系选择器
				showRelationPickerPopup: false,
				relationPickerValue: [0],
				relationOptions: ['父母', '配偶', '子女', '兄弟姐妹', '朋友', '其他']
			}
		},
		onLoad(options) {
			// 从本地存储获取登录用户信息
			const userInfo = uni.getStorageSync('userInfo');
			if (!userInfo || !userInfo.userId) {
				uni.showToast({
					title: '请先登录',
					icon: 'none'
				});
				setTimeout(() => {
					uni.navigateTo({
						url: '/pages/login/index'
					});
				}, 1500);
				return;
			}

			this.tenantId = userInfo.userId;

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

			// 显示关系选择器
			showRelationPicker() {
				this.relationPickerValue = [0]
				this.showRelationPickerPopup = true
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
				this.formData.checkinDate = `${year}-${month}-${day}`
				this.showDatePickerPopup = false
			},

			// 关系变化
			onRelationChange(e) {
				this.relationPickerValue = e.detail.value
			},

			// 确认关系
			confirmRelation() {
				this.formData.relation = this.relationOptions[this.relationPickerValue[0]]
				this.showRelationPickerPopup = false
			},

			// 提交申请
			async handleSubmit() {
				if (!this.formData.contractId) {
					uni.showToast({
						title: '请选择房源',
						icon: 'none'
					})
					return
				}

				if (!this.formData.name) {
					uni.showToast({
						title: '请输入合住人姓名',
						icon: 'none'
					})
					return
				}

				if (!this.formData.idCard) {
					uni.showToast({
						title: '请输入身份证号',
						icon: 'none'
					})
					return
				}

				if (!this.formData.phone) {
					uni.showToast({
						title: '请输入联系电话',
						icon: 'none'
					})
					return
				}

				if (!this.formData.relation) {
					uni.showToast({
						title: '请选择关系',
						icon: 'none'
					})
					return
				}

				if (!this.formData.checkinDate) {
					uni.showToast({
						title: '请选择入住日期',
						icon: 'none'
					})
					return
				}

				try {
					uni.showLoading({
						title: '提交中...'
					})

					const response = await submitCohabitant({
						contractId: this.formData.contractId,
						tenantName: this.formData.name,
						idCard: this.formData.idCard,
						phone: this.formData.phone,
						relationship: this.formData.relation,
						checkinDate: this.formData.checkinDate
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

	/* 表单样式 */
	.form-row {
		display: flex;
		align-items: center;
		margin: 0 40rpx;
		margin-bottom: 28rpx;
	}

	.form-row:last-child {
		border-bottom: none;
	}

	.required {
		color: #ff0000;
		font-size: 28rpx;
	}

	.form-label {
		width: 166rpx;
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

	.form-input {
		width: 100%;
		color: #1a1a1a;
		font-size: 26rpx;
	}

	.placeholder {
		color: #999999;
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
