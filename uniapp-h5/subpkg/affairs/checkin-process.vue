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
				
				<view class="form-row" @click="showDatePicker('checkinDate')">
					<text class="form-label"><text class="required">*</text>入住日期</text>
					<view class="form-value-wrap">
						<text class="form-value placeholder" v-if="!formData.checkinDate">请选择入住日期</text>
						<text class="form-value" v-else>{{ formData.checkinDate }}</text>
					</view>
				</view>
			</view>

			<!-- 合住人信息卡片 -->
			<view class="card">
				<view class="card-header">
					<view class="card-indicator"></view>
					<text class="card-title">合住人信息</text>
				</view>
				
				<view class="form-row">
					<text class="form-label">姓名</text>
					<view class="form-value-wrap">
						<input class="form-input" type="text" v-model="formData.cohabitantName" placeholder="请输入合住人姓名" placeholder-class="placeholder" />
					</view>
				</view>
				
				<view class="form-row" @click="showRelationPicker('cohabitant')">
					<text class="form-label">关系</text>
					<view class="form-value-wrap">
						<text class="form-value placeholder" v-if="!formData.cohabitantRelation">请选择关系</text>
						<text class="form-value" v-else>{{ formData.cohabitantRelation }}</text>
					</view>
				</view>
				
				<view class="form-row" @click="showDatePicker('cohabitantDate')">
					<text class="form-label">入住日期</text>
					<view class="form-value-wrap">
						<text class="form-value placeholder" v-if="!formData.cohabitantDate">请选择入住日期</text>
						<text class="form-value" v-else>{{ formData.cohabitantDate }}</text>
					</view>
				</view>
			</view>
			
			<!-- 紧急联系人卡片 -->
			<view class="card">
				<view class="card-header">
					<view class="card-indicator"></view>
					<text class="card-title">紧急联系人</text>
				</view>
				
				<view class="form-row">
					<text class="form-label"><text class="required">*</text>姓名</text>
					<view class="form-value-wrap">
						<input class="form-input" type="text" v-model="formData.emergencyName" placeholder="请输入合住人姓名" placeholder-class="placeholder" />
					</view>
				</view>
				
				<view class="form-row" @click="showRelationPicker('emergency')">
					<text class="form-label"><text class="required">*</text>关系</text>
					<view class="form-value-wrap">
						<text class="form-value placeholder" v-if="!formData.emergencyRelation">请选择关系</text>
						<text class="form-value" v-else>{{ formData.emergencyRelation }}</text>
					</view>
				</view>
				
				<view class="form-row">
					<text class="form-label"><text class="required">*</text>联系方式</text>
					<view class="form-value-wrap">
						<input class="form-input" type="text" v-model="formData.emergencyPhone" placeholder="请输入联系方式" placeholder-class="placeholder" />
					</view>
				</view>
			</view>
			
			<!-- 协议勾选 -->
			<view class="agreement-row" @click="toggleAgreement">
				<image class="agreement-icon" :src="agreed ? '/static/fangyaun/选中@2x.png' : '/static/fangyaun/未选中@2x.png'"></image>
				<text class="agreement-text">我已阅读并同意</text>
				<text class="agreement-link" @click.stop="viewAgreement">《入住公约》</text>
			</view>
		</scroll-view>
		
		<!-- 底部申请按钮 -->
		<view class="bottom-btn-container">
			<view class="bottom-btn" @click="handleSubmit">
				<text class="bottom-btn-text">申请入住</text>
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
	import { getCheckInDetail, submitCheckIn } from '@/api/checkin.js'

	export default {
		data() {
			return {
				housingType: '',
				recordId: '',
				loading: false,
				agreed: false,

				// 入住单详情
				checkinDetail: null,

				formData: {
					community: '',
					room: '',
					checkinDate: '',
					cohabitantName: '',
					cohabitantRelation: '',
					cohabitantDate: '',
					emergencyName: '',
					emergencyRelation: '',
					emergencyPhone: ''
				},

				// 日期选择器
				showDatePickerPopup: false,
				currentDateField: '',
				datePickerValue: [0, 0, 0],
				years: [],
				months: [],
				days: [],

				// 关系选择器
				showRelationPickerPopup: false,
				currentRelationField: '',
				relationPickerValue: [0],
				relationOptions: ['父母', '配偶', '子女', '兄弟姐妹', '朋友', '其他']
			}
		},
		onLoad(options) {
			if (options.type) {
				this.housingType = options.type
			}
			if (options.id) {
				this.recordId = options.id
			}
			this.initDatePicker()
			this.loadCheckInDetail()
		},
		methods: {
			// 加载入住单详情
			async loadCheckInDetail() {
				try {
					this.loading = true

					const response = await getCheckInDetail(this.recordId)

					if (response.code === 200 && response.data) {
						this.checkinDetail = response.data

						// 解析备注中的房源信息
						const remark = response.data.remark || ''
						this.formData.community = this.extractInfo(remark, '项目：')
						this.formData.room = this.extractInfo(remark, '房间：')

						// 如果已有实际入住日期，回显
						if (response.data.actualCheckinDate) {
							this.formData.checkinDate = response.data.actualCheckinDate
						}

						// 回显合住人信息
						if (response.data.roommateInfo) {
							try {
								const roommates = JSON.parse(response.data.roommateInfo)
								if (roommates && roommates.length > 0) {
									const roommate = roommates[0]
									this.formData.cohabitantName = roommate.name || ''
									this.formData.cohabitantRelation = roommate.relation || ''
									this.formData.cohabitantDate = roommate.checkinDate || ''
								}
							} catch (e) {
								console.error('解析合住人信息失败:', e)
							}
						}

						// 回显紧急联系人信息
						this.formData.emergencyName = response.data.emergencyContactName || ''
						this.formData.emergencyRelation = response.data.emergencyContactRelation || ''
						this.formData.emergencyPhone = response.data.emergencyContactPhone || ''
					} else {
						uni.showToast({
							title: '获取入住单信息失败',
							icon: 'none'
						})
						setTimeout(() => {
							uni.navigateBack()
						}, 1500)
					}
				} catch (error) {
					console.error('获取入住单详情失败:', error)
					uni.showToast({
						title: '获取入住单信息失败',
						icon: 'none'
					})
					setTimeout(() => {
						// 判断页面栈深度，如果只有当前页则跳转回列表页
						const pages = getCurrentPages()
						if (pages.length > 1) {
							uni.navigateBack()
						} else {
							uni.redirectTo({
								url: `/subpkg/affairs/checkin?type=${this.housingType}`
							})
						}
					}, 1500)
				} finally {
					this.loading = false
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
			showDatePicker(field) {
				this.currentDateField = field
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
				const dateStr = `${year}年${month}月${day}日`

				if (this.currentDateField === 'checkinDate') {
					this.formData.checkinDate = dateStr
				} else if (this.currentDateField === 'cohabitantDate') {
					this.formData.cohabitantDate = dateStr
				}

				this.showDatePickerPopup = false
			},

			// 显示关系选择器
			showRelationPicker(field) {
				this.currentRelationField = field
				this.relationPickerValue = [0]
				this.showRelationPickerPopup = true
			},

			// 关系变化
			onRelationChange(e) {
				this.relationPickerValue = e.detail.value
			},

			// 确认关系
			confirmRelation() {
				const relation = this.relationOptions[this.relationPickerValue[0]]

				if (this.currentRelationField === 'cohabitant') {
					this.formData.cohabitantRelation = relation
				} else if (this.currentRelationField === 'emergency') {
					this.formData.emergencyRelation = relation
				}

				this.showRelationPickerPopup = false
			},

			// 切换协议勾选
			toggleAgreement() {
				this.agreed = !this.agreed
			},

			// 查看协议
			viewAgreement() {
				uni.navigateTo({
					url: '/subpkg/affairs/checkin-agreement'
				})
			},

			// 提交申请
			async handleSubmit() {
				// 表单验证
				if (!this.formData.checkinDate) {
					uni.showToast({
						title: '请选择入住日期',
						icon: 'none'
					})
					return
				}

				if (!this.formData.emergencyName) {
					uni.showToast({
						title: '请输入紧急联系人姓名',
						icon: 'none'
					})
					return
				}

				if (!this.formData.emergencyRelation) {
					uni.showToast({
						title: '请选择紧急联系人关系',
						icon: 'none'
					})
					return
				}

				if (!this.formData.emergencyPhone) {
					uni.showToast({
						title: '请输入紧急联系人联系方式',
						icon: 'none'
					})
					return
				}

				if (!this.agreed) {
					uni.showToast({
						title: '请阅读并同意入住公约',
						icon: 'none'
					})
					return
				}

				// 手机号验证
				const phoneReg = /^1[3-9]\d{9}$/
				if (!phoneReg.test(this.formData.emergencyPhone)) {
					uni.showToast({
						title: '请输入正确的手机号',
						icon: 'none'
					})
					return
				}

				try {
					uni.showLoading({
						title: '提交中...'
					})

					// 构建合住人信息数组
					let roommateInfo = null
					if (this.formData.cohabitantName) {
						roommateInfo = JSON.stringify([{
							name: this.formData.cohabitantName,
							relation: this.formData.cohabitantRelation,
							checkinDate: this.formData.cohabitantDate
						}])
					}

					// 提交数据
					const submitData = {
						recordId: this.recordId,
						actualCheckinDate: this.formData.checkinDate,
						roommateInfo: roommateInfo,
						emergencyContactName: this.formData.emergencyName,
						emergencyContactRelation: this.formData.emergencyRelation,
						emergencyContactPhone: this.formData.emergencyPhone
					}

					const response = await submitCheckIn(submitData)

					uni.hideLoading()

					if (response.code === 200) {
						uni.showToast({
							title: response.msg || '提交成功',
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
					console.error('提交入住申请失败:', error)
					uni.showToast({
						title: error.msg || '提交失败，请重试',
						icon: 'none'
					})
					setTimeout(() => {
						// 判断页面栈深度，如果只有当前页则跳转回列表页
						const pages = getCurrentPages()
						if (pages.length > 1) {
							uni.navigateBack()
						} else {
							uni.redirectTo({
								url: `/subpkg/affairs/checkin?type=${this.housingType}`
							})
						}
					}, 1500)
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
		padding:  26rpx 0 10rpx 0;
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

	.form-row:last-child {
		border-bottom: none;
	}

	.form-label-wrap {
		display: flex;
		align-items: center;
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

	/* 协议勾选 */
	.agreement-row {
		display: flex;
		align-items: center;
		padding: 24rpx 0;
		justify-content: center;
	}

	.agreement-icon {
		width: 30rpx;
		height: 30rpx;
		margin-right: 12rpx;
	}

	.agreement-text {
		color: #171a1f;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 31rpx;
	}

	.agreement-link {
		color: #0f73ff;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 31rpx;
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

