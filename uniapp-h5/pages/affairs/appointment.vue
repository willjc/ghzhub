<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 预约卡片列表 -->
			<view class="card" v-for="(item, index) in appointmentList" :key="index">
				<!-- 顶部背景 -->
				<image class="card-bg" src="/static/预约卡片-背景@2x.png" mode="aspectFill"></image>
				
				<!-- 卡片头部 -->
				<view class="card-header">
					<text class="project-name">{{ item.projectName }}</text>
					<text class="status-text" :class="statusClassMap[item.status] || ''">{{ item.statusText }}</text>
				</view>
				
				<!-- 信息列表 -->
				<view class="info-list">
					<view class="info-row">
						<text class="info-label">房间编号</text>
						<text class="info-value">{{ item.room }}</text>
					</view>
					
					<view class="info-row">
						<text class="info-label">预约时间</text>
						<text class="info-value">{{ item.appointmentTime }}</text>
					</view>
					
					<view class="info-row" :class="{ 'last-row': item.status === 'pending' }">
						<text class="info-label">联系电话</text>
						<text class="info-value">{{ item.phone }}</text>
					</view>
				</view>

				<!-- 按钮区域 - 待确认(状态0) -->
				<view class="button-group" v-if="item.status === 'pending' && item.appointmentStatus === '0'">
					<view class="btn btn-cancel" @click="handleCancel(index)">
						<text class="btn-text-blue">取消预约</text>
					</view>
					<view class="btn btn-disabled">
						<text class="btn-text-gray">等待确认</text>
					</view>
				</view>

				<!-- 按钮区域 - 已确认(状态1) -->
				<view class="button-group" v-if="item.status === 'pending' && item.appointmentStatus === '1'">
					<view class="btn btn-cancel" @click="handleCancel(index)">
						<text class="btn-text-blue">取消预约</text>
					</view>
					<view class="btn btn-confirm" @click="handleConfirm(index)">
						<text class="btn-text-white">确认看房</text>
					</view>
				</view>
			</view>
			
			<!-- 空状态 -->
			<view class="empty-state" v-if="appointmentList.length === 0">
				<text class="empty-text">暂无预约记录</text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
import { getMyAppointments, confirmViewing, cancelAppointment } from '@/api/appointment'
import authCheck from '@/mixins/authCheck'

export default {
	data() {
		return {
			housingType: '',
			appointmentList: [],
			loading: false,
			userId: null,  // 当前登录用户ID

			statusClassMap: {
				'pending': 'status-pending',
				'viewed': 'status-viewed',
				'expired': 'status-expired',
				'cancelled': 'status-cancelled'
			}
		}
	},
	onLoad(options) {
		// 使用统一的登录检查
		authCheck.checkLogin.call(this, options, (options) => {
			if (options.type) {
				this.housingType = options.type
			}
			this.loadAppointmentList()
		})
	},
	methods: {
		// 加载预约列表
		async loadAppointmentList() {
			if (this.loading) return
			if (!this.userId) {
				console.error('用户ID不存在')
				return
			}

			this.loading = true
			try {
				const response = await getMyAppointments(this.userId)
				if (response.code === 200) {
					// 转换后端数据为前端展示格式
					this.appointmentList = response.data.map(item => ({
						id: item.appointmentId,
						projectName: item.projectName || '未知项目',
						status: this.mapBackendStatus(item.appointmentStatus),
						statusText: this.getStatusText(item.appointmentStatus),
						room: item.houseNo || '未知房间',
						appointmentTime: this.formatAppointmentTime(item.appointmentDate, item.timeSlot),
						phone: item.visitorPhone || '未知',
						appointmentStatus: item.appointmentStatus  // 保留原始状态值
					}))
				} else {
					uni.showToast({
						title: response.msg || '加载失败',
						icon: 'none'
					})
				}
			} catch (error) {
				console.error('加载预约列表失败:', error)
				uni.showToast({
					title: '加载失败',
					icon: 'none'
				})
			} finally {
				this.loading = false
			}
		},

		// 映射后端状态值到前端状态标识
		// 后端: 0-待确认, 1-已确认, 2-用户已确认看房, 3-已完成, 4-已取消, 5-已过期
		// 前端: pending(待看房), viewed(已看房), expired(已过期), cancelled(已取消)
		mapBackendStatus(backendStatus) {
			const statusMap = {
				'0': 'pending',   // 待确认 → 待看房
				'1': 'pending',   // 已确认 → 待看房
				'2': 'viewed',    // 用户已确认看房 → 已看房
				'3': 'viewed',    // 已完成 → 已看房
				'4': 'cancelled', // 已取消
				'5': 'expired'    // 已过期
			}
			return statusMap[backendStatus] || 'pending'
		},

		// 获取状态文本
		getStatusText(backendStatus) {
			const textMap = {
				'0': '待看房',
				'1': '待看房',
				'2': '已看房',
				'3': '已看房',
				'4': '已取消',
				'5': '已过期'
			}
			return textMap[backendStatus] || '未知'
		},

		// 格式化预约时间
		formatAppointmentTime(date, timeSlot) {
			if (!date) return '未知时间'

			// 解析日期: yyyy-MM-dd → M月d日
			const dateObj = new Date(date)
			const month = dateObj.getMonth() + 1
			const day = dateObj.getDate()

			// timeSlot格式: 09:00-10:00
			return `${month}月${day}日 ${timeSlot || ''}`
		},

		// 取消预约
		handleCancel(index) {
			const item = this.appointmentList[index]

			uni.showModal({
				title: '提示',
				content: '确定要取消此预约吗？',
				success: async (res) => {
					if (res.confirm) {
						try {
							const response = await cancelAppointment(item.id, '用户主动取消')
							if (response.code === 200) {
								uni.showToast({
									title: '已取消',
									icon: 'success'
								})
								// 刷新列表
								this.loadAppointmentList()
							} else {
								uni.showToast({
									title: response.msg || '取消失败',
									icon: 'none'
								})
							}
						} catch (error) {
							console.error('取消预约失败:', error)
							uni.showToast({
								title: '操作失败',
								icon: 'none'
							})
						}
					}
				}
			})
		},

		// 确认看房
		handleConfirm(index) {
			const item = this.appointmentList[index]

			uni.showModal({
				title: '提示',
				content: '确定已完成看房吗？',
				success: async (res) => {
					if (res.confirm) {
						try {
							const response = await confirmViewing(item.id)
							if (response.code === 200) {
								uni.showToast({
									title: '已确认，等待管理员审核',
									icon: 'success'
								})
								// 刷新列表
								this.loadAppointmentList()
							} else {
								uni.showToast({
									title: response.msg || '确认失败',
									icon: 'none'
								})
							}
						} catch (error) {
							console.error('确认看房失败:', error)
							uni.showToast({
								title: '操作失败',
								icon: 'none'
							})
						}
					}
				}
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
		box-sizing: border-box;
	}

	.card {
		width: 702rpx;
		border-radius: 20rpx;
		background: #ffffff;
		padding: 0 28rpx 22rpx 28rpx;
		margin: 0 auto 24rpx;
		box-sizing: border-box;
		position: relative;
		overflow: hidden;
	}

	.card-bg {
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 80rpx;
		border-radius: 16rpx 16rpx 0 0;
	}

	.card-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 32rpx 0 28rpx 0;
		position: relative;
		z-index: 1;
	}

	.project-name {
		flex: 1;
		height: 40rpx;
		color: #333333;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 40rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.status-text {
		height: 37rpx;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 37rpx;
		margin-left: 20rpx;
		flex-shrink: 0;
	}

	/* 状态颜色 */
	.status-pending {
		color: #ff8d1a;
	}

	.status-viewed {
		color: #1281ff;
	}

	.status-expired {
		color: #fa5740;
	}

	.status-cancelled {
		color: #999999;
	}

	.info-list {
		position: relative;
		z-index: 1;
	}

	.info-row {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 28rpx;
	}

	.last-row {
		margin-bottom: 0;
		padding-bottom: 28rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.info-label {
		height: 37rpx;
		color: #888888;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 37rpx;
	}

	.info-value {
		height: 37rpx;
		color: #1a1a1a;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 37rpx;
		text-align: right;
	}

	/* 按钮区域 */
	.button-group {
		display: flex;
		justify-content: flex-end;
		gap: 16rpx;
		margin-top: 15rpx;
		position: relative;
		z-index: 1;
	}

	.btn {
		width: 180rpx;
		height: 68rpx;
		border-radius: 12rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.btn-cancel {
		border: 1rpx solid #1281ff;
		background: #ffffff;
	}

	.btn-confirm {
		background: #1281ff;
	}

	.btn-disabled {
		border: 1rpx solid #cccccc;
		background: #f5f5f5;
	}

	.btn-text-blue {
		color: #1281ff;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.btn-text-white {
		color: #ffffff;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.btn-text-gray {
		color: #999999;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	/* 空状态 */
	.empty-state {
		display: flex;
		justify-content: center;
		align-items: center;
		padding: 100rpx 0;
	}

	.empty-text {
		color: #999999;
		font-size: 28rpx;
	}
</style>

