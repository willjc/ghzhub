<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 保洁信息卡片 -->
			<view class="card">
				<view class="card-header">
					<view class="card-indicator"></view>
					<text class="card-title">保洁信息</text>
				</view>

				<!-- 保洁类型 -->
				<view class="form-row" @click="showCleanTypePicker = true">
					<text class="form-label"><text class="required">*</text>保洁类型</text>
					<view class="form-value-wrap">
						<text class="form-value placeholder" v-if="!formData.cleanTypeName">请选择保洁类型</text>
						<text class="form-value" v-else>{{ formData.cleanTypeName }}</text>
						<image class="arrow-right" src="/static/向右1@2x.png" mode="aspectFit"></image>
					</view>
				</view>

				<!-- 联系人 -->
				<view class="form-row">
					<text class="form-label"><text class="required">*</text>联系人</text>
					<view class="form-value-wrap">
						<input class="form-input" type="text" v-model="formData.applicantName" placeholder="请输入联系人姓名" placeholder-class="placeholder" />
					</view>
				</view>

				<!-- 联系电话 -->
				<view class="form-row">
					<text class="form-label"><text class="required">*</text>联系电话</text>
					<view class="form-value-wrap">
						<input class="form-input" type="number" v-model="formData.applicantPhone" placeholder="请输入联系电话" placeholder-class="placeholder" />
					</view>
				</view>

				<!-- 服务地址 -->
				<view class="form-row form-row-column">
					<text class="form-label form-label-full"><text class="required">*</text>服务地址</text>
					<view class="textarea-wrap">
						<textarea
							class="form-textarea"
							v-model="formData.serviceAddress"
							placeholder="请输入详细服务地址（小区/楼栋/门牌号）"
							placeholder-class="placeholder"
							maxlength="200"
						></textarea>
					</view>
				</view>

				<!-- 房间数 -->
				<view class="form-row">
					<text class="form-label">房间数</text>
					<view class="form-value-wrap">
						<input class="form-input" type="number" v-model="formData.roomCount" placeholder="请输入房间数（如 3）" placeholder-class="placeholder" />
					</view>
				</view>

				<!-- 期望服务时间 -->
				<view class="form-row" @click="showDatePicker = true">
					<text class="form-label"><text class="required">*</text>期望时间</text>
					<view class="form-value-wrap">
						<text class="form-value placeholder" v-if="!formData.expectTime">请选择期望服务时间</text>
						<text class="form-value" v-else>{{ formData.expectTime }}</text>
						<image class="arrow-right" src="/static/向右1@2x.png" mode="aspectFit"></image>
					</view>
				</view>

				<!-- 备注 -->
				<view class="form-row form-row-column">
					<text class="form-label form-label-full">备注</text>
					<view class="textarea-wrap">
						<textarea
							class="form-textarea"
							v-model="formData.remark"
							placeholder="特殊要求或补充说明（选填）"
							placeholder-class="placeholder"
							maxlength="300"
						></textarea>
					</view>
				</view>
			</view>
		</scroll-view>

		<!-- 底部提交按钮 -->
		<view class="bottom-btn-container">
			<view class="bottom-btn" @click="handleSubmit">
				<text class="bottom-btn-text">{{ submitting ? '提交中...' : '提交申请' }}</text>
			</view>
		</view>

		<!-- 保洁类型选择器 -->
		<view class="picker-mask" v-if="showCleanTypePicker" @click="showCleanTypePicker = false"></view>
		<view class="picker-popup" :class="{ 'show': showCleanTypePicker }">
			<view class="picker-header">
				<text class="picker-cancel" @click="showCleanTypePicker = false">取消</text>
				<text class="picker-title">选择保洁类型</text>
				<text class="picker-confirm" @click="confirmCleanType">确定</text>
			</view>
			<view class="picker-body">
				<picker-view class="picker-view" :value="cleanTypePickerValue" @change="onCleanTypeChange">
					<picker-view-column>
						<view class="picker-item" v-for="item in cleanTypeOptions" :key="item.value">
							<text class="picker-item-text">{{ item.label }}</text>
						</view>
					</picker-view-column>
				</picker-view>
			</view>
		</view>

		<!-- 日期选择器 -->
		<view class="picker-mask" v-if="showDatePicker" @click="showDatePicker = false"></view>
		<view class="picker-popup" :class="{ 'show': showDatePicker }">
			<view class="picker-header">
				<text class="picker-cancel" @click="showDatePicker = false">取消</text>
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
					<picker-view-column>
						<view class="picker-item" v-for="hour in hours" :key="hour">
							<text class="picker-item-text">{{ hour }}时</text>
						</view>
					</picker-view-column>
				</picker-view>
			</view>
		</view>
	</view>
</template>

<script>
import { submitCleanOrder } from '@/api/serviceOrder.js'

export default {
	data() {
		return {
			submitting: false,
			formData: {
				applicantName: '',
				applicantPhone: '',
				serviceAddress: '',
				cleanType: '',
				cleanTypeName: '',
				roomCount: '',
				expectTime: '',
				remark: ''
			},
			// 保洁类型字典（与后端 hz_clean_service_type 字典对齐）
			cleanTypeOptions: [
				{ value: 'daily', label: '日常保洁' },
				{ value: 'deep', label: '深度保洁' },
				{ value: 'rough', label: '开荒保洁' },
				{ value: 'checkout', label: '退租保洁' }
			],
			showCleanTypePicker: false,
			cleanTypePickerValue: [0],

			showDatePicker: false,
			datePickerValue: [0, 0, 0, 0],
			years: [],
			months: [],
			days: [],
			hours: []
		}
	},
	onLoad() {
		const userInfo = uni.getStorageSync('userInfo')
		if (!userInfo || !userInfo.userId) {
			uni.showToast({ title: '请先登录', icon: 'none' })
			setTimeout(() => {
				uni.navigateTo({ url: '/pages/login/index' })
			}, 1500)
			return
		}
		// 默认填充
		if (userInfo.nickName) this.formData.applicantName = userInfo.nickName
		if (userInfo.phone) this.formData.applicantPhone = userInfo.phone
		this.initDatePicker()
	},
	methods: {
		initDatePicker() {
			const now = new Date()
			const currentYear = now.getFullYear()
			this.years = []
			for (let i = currentYear; i <= currentYear + 1; i++) this.years.push(i)
			this.months = Array.from({ length: 12 }, (_, i) => i + 1)
			this.days = Array.from({ length: 31 }, (_, i) => i + 1)
			this.hours = Array.from({ length: 24 }, (_, i) => i)
		},

		onCleanTypeChange(e) {
			this.cleanTypePickerValue = e.detail.value
		},
		confirmCleanType() {
			const idx = this.cleanTypePickerValue[0]
			const item = this.cleanTypeOptions[idx]
			if (item) {
				this.formData.cleanType = item.value
				this.formData.cleanTypeName = item.label
			}
			this.showCleanTypePicker = false
		},

		onDateChange(e) {
			this.datePickerValue = e.detail.value
		},
		confirmDate() {
			const v = this.datePickerValue
			const y = this.years[v[0]]
			const m = String(this.months[v[1]]).padStart(2, '0')
			const d = String(this.days[v[2]]).padStart(2, '0')
			const h = String(this.hours[v[3]]).padStart(2, '0')
			this.formData.expectTime = `${y}-${m}-${d} ${h}:00:00`
			this.showDatePicker = false
		},

		async handleSubmit() {
			if (this.submitting) return

			if (!this.formData.cleanType) {
				uni.showToast({ title: '请选择保洁类型', icon: 'none' })
				return
			}
			if (!this.formData.applicantName) {
				uni.showToast({ title: '请输入联系人', icon: 'none' })
				return
			}
			if (!this.formData.applicantPhone) {
				uni.showToast({ title: '请输入联系电话', icon: 'none' })
				return
			}
			const phoneReg = /^1[3-9]\d{9}$/
			if (!phoneReg.test(this.formData.applicantPhone)) {
				uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
				return
			}
			if (!this.formData.serviceAddress) {
				uni.showToast({ title: '请输入服务地址', icon: 'none' })
				return
			}
			if (!this.formData.expectTime) {
				uni.showToast({ title: '请选择期望服务时间', icon: 'none' })
				return
			}

			try {
				this.submitting = true
				uni.showLoading({ title: '提交中...' })

				const submitData = {
					applicantName: this.formData.applicantName,
					applicantPhone: this.formData.applicantPhone,
					serviceAddress: this.formData.serviceAddress,
					cleanType: this.formData.cleanType,
					roomCount: this.formData.roomCount ? parseInt(this.formData.roomCount) : null,
					expectTime: this.formData.expectTime,
					remark: this.formData.remark
				}

				const res = await submitCleanOrder(submitData)
				uni.hideLoading()
				if (res.code === 200) {
					uni.showToast({ title: '提交成功', icon: 'success' })
					setTimeout(() => {
						uni.redirectTo({ url: '/subpkg/service/service-order?type=clean' })
					}, 1500)
				} else {
					uni.showToast({ title: res.msg || '提交失败', icon: 'none' })
				}
			} catch (err) {
				uni.hideLoading()
				console.error('提交保洁订单失败:', err)
				uni.showToast({ title: '提交失败', icon: 'none' })
			} finally {
				this.submitting = false
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
	.scroll-content { flex: 1; padding: 24rpx; padding-bottom: 160rpx; box-sizing: border-box; }

	.card {
		width: 702rpx;
		border-radius: 20rpx;
		background: #ffffff;
		padding: 26rpx 0 10rpx 0;
		margin: 0 auto 24rpx;
		box-sizing: border-box;
	}
	.card-header { display: flex; align-items: center; margin-bottom: 37rpx; }
	.card-indicator { width: 12rpx; height: 34rpx; background: #0f73ff; margin-right: 26rpx; }
	.card-title { color: #171a1f; font-size: 32rpx; font-weight: 600; font-family: "PingFang SC", "苹方-简", sans-serif; }

	.form-row { display: flex; align-items: center; margin: 0 40rpx 28rpx 40rpx; }
	.form-row-column { flex-direction: column; align-items: flex-start; }
	.required { color: #ff0000; font-size: 28rpx; }
	.form-label {
		width: 166rpx; color: #333333; font-size: 28rpx;
		padding-bottom: 28rpx; flex-shrink: 0;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}
	.form-label-full { width: 100%; margin-bottom: 16rpx; padding-bottom: 0; }
	.form-value-wrap { flex: 1; padding-bottom: 28rpx; border-bottom: 1rpx solid #f0f0f0; display: flex; align-items: center; }
	.form-value { color: #1a1a1a; font-size: 26rpx; font-family: "PingFang SC", "苹方-简", sans-serif; }
	.form-input { width: 100%; color: #1a1a1a; font-size: 26rpx; text-align: left; }
	.arrow-right { width: 24rpx; height: 24rpx; margin-left: 8rpx; flex-shrink: 0; }
	.placeholder { color: #999999; }

	.textarea-wrap {
		width: 100%; border: 1rpx solid #e5e5e5;
		border-radius: 12rpx; padding: 16rpx; box-sizing: border-box;
	}
	.form-textarea { width: 100%; height: 121rpx; color: #333333; font-size: 26rpx; line-height: 40rpx; }

	.bottom-btn-container { position: fixed; bottom: 0; left: 0; right: 0; padding: 24rpx; background: #f5f6fc; }
	.bottom-btn {
		width: 702rpx; height: 92rpx; border-radius: 20rpx;
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
		display: flex; align-items: center; justify-content: center; margin: 0 auto;
	}
	.bottom-btn-text { color: #ffffff; font-size: 36rpx; font-weight: 600; font-family: "PingFang SC", "苹方-简", sans-serif; }

	.picker-mask { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); z-index: 998; }
	.picker-popup {
		position: fixed; left: 0; right: 0; bottom: 0; background: #ffffff;
		border-radius: 24rpx 24rpx 0 0; z-index: 999;
		transform: translateY(100%); transition: transform 0.3s ease;
	}
	.picker-popup.show { transform: translateY(0); }
	.picker-header { width: 750rpx; height: 95rpx; display: flex; justify-content: space-between; align-items: center; padding: 0 32rpx; box-sizing: border-box; border-bottom: 1rpx solid #f0f0f0; }
	.picker-cancel { color: rgba(0,0,0,0.6); font-size: 32rpx; line-height: 48rpx; }
	.picker-title { color: rgba(0,0,0,0.9); font-size: 32rpx; font-weight: 600; line-height: 48rpx; }
	.picker-confirm { color: #1281ff; font-size: 32rpx; line-height: 48rpx; }
	.picker-body { width: 750rpx; height: 400rpx; background: #ffffff; }
	.picker-view { width: 100%; height: 100%; }
	.picker-item { height: 80rpx; display: flex; align-items: center; justify-content: center; }
	.picker-item-text { color: #000000; font-size: 28rpx; line-height: 80rpx; }
</style>
