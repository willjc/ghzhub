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
					<text class="form-label">审核状态</text>
					<view class="form-value-wrap">
						<text class="form-value status-confirm">待入住确认</text>
					</view>
				</view>
			</view>

			<!-- 配套设备卡片 -->
			<view class="card">
				<view class="card-header">
					<view class="card-indicator"></view>
					<text class="card-title">配套设备</text>
				</view>

				<!-- 设备列表 -->
				<view class="equipment-row" v-for="(item, index) in equipmentList" :key="index">
					<view class="equipment-check" @click="toggleEquipment(index)">
						<image class="check-icon" :src="item.checked ? '/static/多选-选中@2x.png' : '/static/多选-未选择@2x.png'"></image>
					</view>
					<text class="equipment-name">{{ item.name }}</text>
					<view class="equipment-status">
						<view
							class="status-btn"
							:class="{ 'active': item.status === 'good' }"
							@click="setEquipmentStatus(index, 'good')"
						>
							<text class="status-text" :class="{ 'active': item.status === 'good' }">完好</text>
						</view>
						<view
							class="status-btn"
							:class="{ 'active': item.status === 'damaged' }"
							@click="setEquipmentStatus(index, 'damaged')"
						>
							<text class="status-text" :class="{ 'active': item.status === 'damaged' }">破损</text>
						</view>
					</view>
				</view>

				<!-- 空状态 -->
				<view class="empty-state" v-if="equipmentList.length === 0">
					<text class="empty-text">暂无配套设备信息</text>
				</view>
			</view>
		</scroll-view>

		<!-- 底部按钮 -->
		<view class="bottom-btn-container">
			<view class="bottom-btn" @click="handleConfirm">
				<text class="bottom-btn-text">入住确认</text>
			</view>
		</view>
	</view>
</template>

<script>
	import { getCheckInConfirmInfo } from '@/api/checkin.js'

	export default {
		data() {
			return {
				housingType: '',
				recordId: '',
				loading: false,

				formData: {
					community: '',
					room: '',
					checkinDate: '',
					applicant: '',
					applyTime: ''
				},

				equipmentList: []
			}
		},
		onLoad(options) {
			if (options.type) {
				this.housingType = options.type
			}
			if (options.id) {
				this.recordId = options.id
			}
			this.loadCheckInDetail()
		},
		methods: {
			// 加载申请详情
			async loadCheckInDetail() {
				try {
					this.loading = true

					const response = await getCheckInConfirmInfo(this.recordId)

					if (response.code === 200 && response.data) {
						const data = response.data

						// 填充基本信息
						this.formData.community = data.projectName || '未知小区'
						this.formData.room = data.roomAddress || '未知房间'
						this.formData.checkinDate = data.checkinDate || ''
						this.formData.applicant = data.userName || '租户'
						this.formData.applyTime = data.createTime ? data.createTime.substring(0, 19) : ''

						// 解析配套设施
						this.parseFacilities(data.facilities)
					} else {
						uni.showToast({
							title: response.msg || '获取信息失败',
							icon: 'none'
						})
						setTimeout(() => {
							uni.navigateBack()
						}, 1500)
					}
				} catch (error) {
					console.error('获取入住确认信息失败:', error)
					uni.showToast({
						title: '获取信息失败',
						icon: 'none'
					})
					setTimeout(() => {
						uni.navigateBack()
					}, 1500)
				} finally {
					this.loading = false
				}
			},

			// 解析配套设施
			parseFacilities(facilitiesStr) {
				this.equipmentList = []

				if (!facilitiesStr) {
					return
				}

				try {
					// 尝试解析JSON格式
					if (facilitiesStr.startsWith('{') || facilitiesStr.startsWith('[')) {
						const facilities = JSON.parse(facilitiesStr)

						if (Array.isArray(facilities)) {
							// 数组格式: [{"冰箱": 1}, {"洗衣机": 1}]
							facilities.forEach(item => {
								if (typeof item === 'object') {
									Object.keys(item).forEach(key => {
										this.equipmentList.push({
											name: key,
											checked: false,
											status: 'good'
										})
									})
								}
							})
						} else {
							// 对象格式: {"冰箱": 1, "洗衣机": 1}
							Object.keys(facilities).forEach(key => {
								this.equipmentList.push({
									name: key,
									checked: false,
									status: 'good'
								})
							})
						}
					} else {
						// 逗号分隔格式: "冰箱,洗衣机,电视机"
						const items = facilitiesStr.split(/[,，、]/)
						items.forEach(item => {
							const name = item.trim()
							if (name) {
								this.equipmentList.push({
									name: name,
									checked: false,
									status: 'good'
								})
							}
						})
					}
				} catch (e) {
					console.error('解析设施信息失败:', e)
					// 如果解析失败，尝试按逗号分隔
					const items = facilitiesStr.split(/[,，、]/)
					items.forEach(item => {
						const name = item.trim()
						if (name) {
							this.equipmentList.push({
								name: name,
								checked: false,
								status: 'good'
							})
						}
					})
				}
			},

			// 切换设备勾选
			toggleEquipment(index) {
				this.equipmentList[index].checked = !this.equipmentList[index].checked
			},

			// 设置设备状态
			setEquipmentStatus(index, status) {
				this.equipmentList[index].status = status
			},

			// 入住确认 - 跳转到签字页面
			handleConfirm() {
				// 检查是否有勾选设备
				const checkedItems = this.equipmentList.filter(item => item.checked)
				if (checkedItems.length === 0) {
					uni.showModal({
						title: '提示',
						content: '请至少勾选一项配套设备',
						showCancel: false
					})
					return
				}

				// 准备传递给签字页面的数据
				const confirmData = {
					recordId: this.recordId,
					equipmentList: this.equipmentList.filter(item => item.checked)
				}

				// 跳转到签字页面，传递数据
				uni.navigateTo({
					url: `/pages/signature/index?type=${this.housingType}&recordId=${this.recordId}&data=${encodeURIComponent(JSON.stringify(confirmData))}`
				})
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

	.status-confirm {
		color: #1281ff;
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
		width: 120rpx;
		color: #333333;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 40rpx;
	}

	.equipment-status {
		flex: 1;
		display: flex;
		justify-content: flex-end;
		gap: 16rpx;
	}

	.status-btn {
		width: 150rpx;
		height: 58rpx;
		border-radius: 12rpx;
		background: #f8f8f8;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.status-btn.active {
		border: 1.5rpx solid #1976f8;
		background: #f8f8f8;
	}

	.status-text {
		color: #333333;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.status-text.active {
		color: #1976f8;
	}

	/* 空状态 */
	.empty-state {
		display: flex;
		justify-content: center;
		align-items: center;
		padding: 60rpx 0;
	}

	.empty-text {
		color: #999999;
		font-size: 28rpx;
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
