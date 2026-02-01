<template>
	<view class="page">
		<!-- 顶部警告提示 -->
		<view class="warning-banner">
			<text class="warning-text">预约看房爽约2次后，一个月内不可再次看房！</text>
		</view>
		
		<!-- 房源信息卡片 -->
		<view class="room-card">
			<image class="room-image" src="/static/fangyaun/房源图片@2x.png" mode="aspectFill"></image>
			<view class="room-info">
				<text class="room-title">{{ roomInfo.projectName }}</text>
				<view class="room-tags">
					<view class="room-tag" v-for="tag in roomInfo.tags" :key="tag">
						<text class="tag-text">{{ tag }}</text>
					</view>
				</view>
				<text class="room-detail">{{ roomInfo.orientation }}-{{ roomInfo.area }}㎡-{{ roomInfo.building }}</text>
				<view class="room-price">
					<text class="price-number">{{ roomInfo.price }}</text>
					<text class="price-unit">元</text>
					<text class="price-suffix">/月起</text>
				</view>
			</view>
		</view>
		
		<!-- 预约信息表单 -->
		<view class="form-card">
			<view class="form-header">
				<view class="form-indicator"></view>
				<text class="form-title">预约信息</text>
			</view>
			
			<view class="form-item-wrapper">
				<view class="form-item">
					<view class="form-label">
						<text class="required">*</text>
						<text class="label-text">联系人</text>
					</view>
					<input class="form-input" placeholder="请输入联系人" v-model="formData.contactName" />
				</view>
				<view class="form-divider"></view>
			</view>
			
			<view class="form-item-wrapper">
				<view class="form-item">
					<view class="form-label">
						<text class="required">*</text>
						<text class="label-text">联系方式</text>
					</view>
					<input class="form-input" placeholder="请输入联系方式" v-model="formData.contactPhone" type="number" maxlength="11" />
				</view>
				<view class="form-divider"></view>
			</view>
			
			<view class="form-item-wrapper">
				<view class="form-item">
					<view class="form-label">
						<text class="required">*</text>
						<text class="label-text">看房时间</text>
					</view>
					<picker mode="date" :value="formData.visitDate" :start="minDate" :end="maxDate" @change="onDateChange">
						<view class="picker-wrapper">
							<text class="picker-text" :class="{ placeholder: !formData.visitDate }">{{ formData.visitDate || '请选择看房时间' }}</text>
						</view>
					</picker>
				</view>
			</view>
		</view>
		
		<!-- 提交按钮 -->
		<view class="submit-btn" @click="submitReserve">
			<text class="submit-text">提交</text>
		</view>
	</view>
</template>

<script>
	import { get, post } from '@/utils/request'

	export default {
		data() {
			return {
				roomId: '',
				projectId: '',
				roomInfo: {
					projectName: '',
					buildingNo: '',
					unitNo: '',
					roomNo: '',
					tags: [],
					orientation: '',
					area: '',
					building: '',
					price: 0
				},
				formData: {
					contactName: '',
					contactPhone: '',
					visitDate: ''
				},
				minDate: '',  // 最小可选日期（明天）
				maxDate: ''   // 最大可选日期（90天后）
			}
		},
		onLoad(options) {
			// 初始化日期范围：明天到90天后
			const tomorrow = new Date()
			tomorrow.setDate(tomorrow.getDate() + 1)
			this.minDate = tomorrow.toISOString().split('T')[0]

			const maxDay = new Date()
			maxDay.setDate(maxDay.getDate() + 90)
			this.maxDate = maxDay.toISOString().split('T')[0]

			if (options.roomId) {
				this.roomId = options.roomId
			}
			if (options.projectId) {
				this.projectId = options.projectId
			}
			this.loadRoomInfo()
		},
		methods: {
			onDateChange(e) {
				this.formData.visitDate = e.detail.value
			},

			async submitReserve() {
				// 表单验证
				if (!this.formData.contactName) {
					uni.showToast({ title: '请输入联系人', icon: 'none' })
					return
				}
				if (!this.formData.contactPhone) {
					uni.showToast({ title: '请输入联系方式', icon: 'none' })
					return
				}
				// 手机号格式验证
				if (!/^1[3-9]\d{9}$/.test(this.formData.contactPhone)) {
					uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
					return
				}
				if (!this.formData.visitDate) {
					uni.showToast({ title: '请选择看房时间', icon: 'none' })
					return
				}

				try {
					uni.showLoading({ title: '提交中...' })

					// 调用API提交预约
					const response = await post(`/h5/app/house/${this.roomId}/appointment`, {
						visitorName: this.formData.contactName,
						visitorPhone: this.formData.contactPhone,
						appointmentDate: this.formData.visitDate,
						appointmentTime: '10:00'  // 默认上午10点
					})
					uni.hideLoading()

					uni.showToast({
						title: response.msg || '预约成功',
						icon: 'success'
					})
					setTimeout(() => {
						uni.redirectTo({
							url: '/pages/affairs/appointment'
						})
					}, 1500)

				} catch (error) {
					uni.hideLoading()
					console.error('提交预约失败:', error)
					uni.showToast({
						title: error.message || '提交失败，请稍后重试',
						icon: 'none',
						duration: 3000
					})
				}
			},

			async loadRoomInfo() {
				if (!this.roomId) {
					return
				}

				try {
					const response = await get(`/h5/app/house/${this.roomId}`)

					if (response.code === 200) {
						const data = response.data

						// 映射房源信息
						this.roomInfo = {
							projectName: data.projectName || '',
							buildingNo: data.buildingNo || '',
							unitNo: data.unitNo || '',
							roomNo: data.roomNo || '',
							tags: [], // 暂无标签数据
							orientation: data.orientation || '',
							area: data.area || '',
							building: `${data.buildingNo || ''}-${data.unitNo || ''}-${data.roomNo || ''}`,
							price: data.price || 0
						}
					}
				} catch (error) {
					console.error('获取房源信息失败:', error)
				}
			}
		}
	}
</script>

<style scoped>
	.page {
		min-height: 95vh;
		background: #f5f6fc;
		padding: 24rpx;
		box-sizing: border-box;
	}
	
	/* 警告提示 */
	.warning-banner {
		width: 702rpx;
		height: 88rpx;
		border-radius: 20rpx;
		background: rgba(255, 68, 0, 0.1);
		display: flex;
		align-items: center;
		padding: 0 24rpx;
		box-sizing: border-box;
		margin-bottom: 24rpx;
	}
	
	.warning-text {
		height: 40rpx;
		color: #ff4400;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
	}
	
	/* 房源信息卡片 */
	.room-card {
		width: 702rpx;
		height: 270rpx;
		border-radius: 20rpx;
		background: #ffffff;
		display: flex;
		padding: 16rpx;
		box-sizing: border-box;
		margin-bottom: 24rpx;
	}
	
	.room-image {
		width: 192rpx;
		height: 238rpx;
		border-radius: 12rpx;
		flex-shrink: 0;
		margin-right: 24rpx;
	}
	
	.room-info {
		flex: 1;
		display: flex;
		flex-direction: column;
	}
	
	.room-title {
		
		height: 80rpx;
		color: #1a1a1a;
		font-size: 32rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 2;
		line-clamp: 2;
		overflow: hidden;
		margin-bottom: 8rpx;
		margin-top: 8rpx;
	}
	
	.room-tags {
		display: flex;
		gap: 12rpx;
		margin-bottom: 8rpx;
	}
	
	.room-tag {
		height: 32rpx;
		border-radius: 4rpx;
		background: #f1f5fa;
		padding: 0 9rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.tag-text {
		color: #4c617d;
		font-size: 20rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 32rpx;
	}
	
	.room-detail {
		height: 40rpx;
		color: #999999;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
		margin-bottom: 12rpx;
	}
	
	.room-price {
		display: flex;
		align-items: baseline;
	}
	
	.price-number {
		height: 40rpx;
		color: #e5252b;
		font-size: 30rpx;
		font-weight: 700;
		font-family: "HarmonyOS Sans SC", sans-serif;
		text-align: left;
		line-height: 40rpx;
	}
	
	.price-unit {
		height: 40rpx;
		color: #e5252b;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
	}
	
	.price-suffix {
		height: 40rpx;
		color: #000000;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
	}
	
	/* 预约信息表单 */
	.form-card {
		width: 702rpx;
		border-radius: 20rpx;
		background: #ffffff;
		padding: 26rpx 0rpx;
		box-sizing: border-box;
		margin-bottom: 40rpx;
	}
	
	.form-header {
		display: flex;
		align-items: center;
		margin-bottom: 36rpx;
	}
	
	.form-indicator {
		width: 12rpx;
		height: 34rpx;
		background: #0f73ff;

		margin-right: 26rpx;
	}
	
	.form-title {
		height: 45rpx;
		color: #171a1f;
		font-size: 32rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 45rpx;
	}
	
	.form-item-wrapper {
		padding: 0 40rpx;
	}
	
	.form-item {
		display: flex;
		align-items: center;
		padding: 20rpx 0;
	}
	
	.form-label {
		display: flex;
		align-items: center;
		width: 166rpx;
		flex-shrink: 0;
	}
	
	.required {
		color: #ff0000;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		margin-right: 4rpx;
	}
	
	.label-text {
		height: 40rpx;
		color: #333333;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
	}
	
	.form-input {
		flex: 1;
		height: 37rpx;
		color: #333333;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}
	
	.form-input::placeholder {
		color: #999999;
	}
	
	.picker-wrapper {
		flex: 1;
	}
	
	.picker-text {
		height: 37rpx;
		color: #333333;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 37rpx;
	}
	
	.picker-text.placeholder {
		color: #999999;
	}
	
	.form-divider {
		width: 456rpx;
		height: 1rpx;
		background: #e1eaf2;
		margin: 10rpx 0;
		margin-left: 166rpx;
		
	}
	
	/* 提交按钮 */
	.submit-btn {
		position: fixed;
		bottom: 40rpx;
		left: 50%;
		transform: translateX(-50%);
		width: 702rpx;
		height: 92rpx;
		border-radius: 20rpx;
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
		backdrop-filter: blur(6rpx);
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.submit-text {
		width: 72rpx;
		height: 51rpx;
		color: #ffffff;
		font-size: 36rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 51rpx;
	}
</style>

