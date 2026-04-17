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

				<view v-if="equipmentList.length === 0" style="text-align: center; color: #999; padding: 30rpx;">
					暂无设施信息
				</view>

				<!-- 按类别分组展示 -->
				<view v-for="category in facilityCategories" :key="category" v-if="getEquipmentByCategory(category).length > 0" style="padding: 0 40rpx;">
					<view style="font-size: 28rpx; color: #666; font-weight: bold; padding: 16rpx 0 8rpx;">{{ category }}</view>
					<view class="equipment-row" v-for="(item, index) in getEquipmentByCategory(category)" :key="index">
						<text class="equipment-name">{{ item.facilityName || item.name }}</text>
						<text style="color: #999; font-size: 24rpx; margin-left: 16rpx;">x{{ item.quantity || 1 }}</text>
						<view class="equipment-status">
							<view class="status-tag" :class="item.itemStatus === '完好' ? 'good' : 'damaged'">
								<text class="status-text">{{ item.itemStatus || '完好' }}</text>
							</view>
						</view>
						<text v-if="item.remark" style="color: #999; font-size: 22rpx; margin-left: 8rpx;">{{ item.remark }}</text>
					</view>
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
	import { get } from '@/utils/request'

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

				equipmentList: [],
				facilityCategories: ['电器类', '门窗类', '灯类', '卫浴区', '家具类', '洗菜池', '其他']
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

						// 解析配套设施 — 改为从新API获取
						if (data.houseId) {
							this.loadFacilities(data.houseId)
						}
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

			// 加载设施数据（从新API获取）
			async loadFacilities(houseId) {
				try {
					const res = await get('/h5/app/house/facilities/' + houseId)
					const facilities = res.data || res || []
					this.equipmentList = Array.isArray(facilities) ? facilities : []
				} catch (e) {
					console.log('加载设施失败', e)
					this.equipmentList = []
				}
			},

			// 按类别过滤设施
			getEquipmentByCategory(category) {
				return this.equipmentList.filter(item =>
					(item.facilityCategory || '其他') === category
				)
			},

			// 入住确认 - 跳转到签字页面
			handleConfirm() {
				// 准备传递给签字页面的数据
				const confirmData = {
					recordId: this.recordId
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
		margin-bottom: 20rpx;
	}

	.equipment-name {
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

	.status-tag {
		display: inline-flex;
		align-items: center;
		padding: 4rpx 16rpx;
		border-radius: 20rpx;
		font-size: 22rpx;
	}

	.status-tag.good {
		background: #f0f9eb;
		color: #67c23a;
	}

	.status-tag.damaged {
		background: #fef0f0;
		color: #f56c6c;
	}

	.status-text {
		font-size: 22rpx;
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
