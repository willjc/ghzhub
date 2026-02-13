<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 入住申请卡片列表 -->
			<view class="card" v-for="(item, index) in checkinList" :key="index">
				<!-- 右上角已取消标签 -->
				<text class="cancel-tag" v-if="item.status === 'cancelled'">已取消</text>
				
				<!-- 申请状态 -->
				<view class="info-row">
					<text class="info-label">申请状态</text>
					<text class="info-value" :class="getStatusClass(item.status)">{{ item.statusText }}</text>
				</view>
				
				<!-- 小区 -->
				<view class="info-row">
					<text class="info-label">小区</text>
					<text class="info-value">{{ item.community }}</text>
				</view>
				
				<!-- 房间 -->
				<view class="info-row">
					<text class="info-label">房间</text>
					<text class="info-value">{{ item.room }}</text>
				</view>
				
				<!-- 租期 -->
				<view class="info-row">
					<text class="info-label">租期</text>
					<text class="info-value">{{ item.rentPeriod }}</text>
				</view>
				
				<!-- 租金 -->
				<view class="info-row">
					<text class="info-label">租金</text>
					<text class="info-value">{{ item.rent }}</text>
				</view>
				
				<!-- 押金 -->
				<view class="info-row last-row">
					<text class="info-label">押金</text>
					<text class="info-value">{{ item.deposit }}</text>
				</view>
				
				<!-- 按钮区域 - 待办理 (status=0) -->
				<view class="button-group" v-if="item.statusCode === '0'">
					<view class="btn btn-checkin" @click="handleCheckin(index)">
						<text class="btn-text-white">办理入住</text>
					</view>
				</view>

				<!-- 按钮区域 - 审批中 (status=1) -->
				<view class="button-group" v-if="item.statusCode === '1'">
					<view class="btn btn-cancel" @click="handleCancel(index)">
						<text class="btn-text-blue">取消申请</text>
					</view>
					<view class="btn btn-edit" @click="handleEdit(index)">
						<text class="btn-text-white">修改信息</text>
					</view>
				</view>

				<!-- 按钮区域 - 审核通过 (status=2) -->
				<view class="button-group" v-if="item.statusCode === '2'">
					<view class="btn btn-confirm" @click="handleConfirm(index)">
						<text class="btn-text-white">入住确认</text>
					</view>
				</view>

				<!-- 按钮区域 - 已拒绝/已取消 (status=3) -->
				<view class="button-group" v-if="item.statusCode === '3'">
					<view class="btn btn-detail" @click="handleDetail(index)">
						<text class="btn-text-blue">查看详情</text>
					</view>
				</view>

				<!-- 按钮区域 - 已入住确认 (status=4) -->
				<view class="button-group" v-if="item.statusCode === '4'">
					<view class="btn btn-handle" @click="handleCheckinDetail(index)">
						<text class="btn-text-white">入驻详情</text>
					</view>
				</view>
			</view>
			
			<!-- 空状态 -->
			<view class="empty-state" v-if="checkinList.length === 0">
				<text class="empty-text">暂无入住申请</text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import { getCheckInList, cancelCheckIn } from '@/api/checkin.js'
	import authCheck from '@/mixins/authCheck'

	export default {
		data() {
			return {
				housingType: '',
				tenantId: null, // 从登录信息获取租户ID
				loading: false,
				checkinList: []
			}
		},
		onLoad(options) {
			// 使用统一的登录检查
			authCheck.checkLogin.call(this, options, (options) => {
				if (options.type) {
					this.housingType = options.type
				}
				this.tenantId = this.userId
				this.loadCheckinList()
			})
		},
		onShow() {
			// 每次页面显示时重新加载列表数据，确保显示最新状态
			if (this.tenantId) {
				this.loadCheckinList()
			}
		},
		methods: {
			// 加载入住申请列表
			async loadCheckinList() {
				try {
					this.loading = true
					console.log('加载入住申请，租户ID:', this.tenantId)

					const response = await getCheckInList(this.tenantId)

					if (response.code === 200 && response.data) {
						// 转换后端数据格式为前端需要的格式
						this.checkinList = response.data.map(item => {
							return this.convertCheckInData(item)
						})
					} else {
						this.checkinList = []
					}
				} catch (error) {
					console.error('获取入住列表失败:', error)
					this.checkinList = []
				} finally {
					this.loading = false
				}
			},

			// 转换后端数据为前端显示格式
			convertCheckInData(item) {
				// 解析备注中的房源信息
				const remark = item.remark || ''
				const community = this.extractInfo(remark, '项目：')
				const room = this.extractInfo(remark, '房间：')
				const rentPeriod = this.extractInfo(remark, '租期：')
				const rent = this.extractInfo(remark, '月租金：')
				const deposit = this.extractInfo(remark, '押金：')

				// 状态映射
				const statusMap = {
					'0': 'pending',      // 待办理
					'1': 'approved',     // 审批中（用户已提交，待管理员审核）
					'2': 'confirm',      // 审核通过（待用户确认）
					'3': 'rejected',     // 已拒绝
					'4': 'confirmed'     // 已入住确认
				}

				const statusTextMap = {
					'0': '待办理',
					'1': '审批中',
					'2': '审核通过',
					'3': '已拒绝',
					'4': '已入住确认'
				}

				return {
					recordId: item.recordId,
					checkinNo: item.checkinNo,
					status: statusMap[item.status] || 'pending',
					statusCode: item.status,
					statusText: statusTextMap[item.status] || '未知',
					community: community || '未知小区',
					room: room || '未知房间',
					rentPeriod: rentPeriod || '-',
					rent: rent || '-',
					deposit: deposit || '-',
					checkinDate: item.checkinDate,
					actualCheckinDate: item.actualCheckinDate
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
			},

			// 取消申请
			async handleCancel(index) {
				const item = this.checkinList[index]

				uni.showModal({
					title: '提示',
					content: '确定要取消此申请吗？取消后您可以重新填写入住信息。',
					success: async (res) => {
						if (res.confirm) {
							try {
								uni.showLoading({ title: '取消中...' })

								const response = await cancelCheckIn(item.recordId)

								uni.hideLoading()

								if (response.code === 200) {
									uni.showToast({
										title: '已取消申请',
										icon: 'success'
									})

									// 重新加载列表
									setTimeout(() => {
										this.loadCheckinList()
									}, 1500)
								} else {
									uni.showToast({
										title: response.msg || '取消失败',
										icon: 'none'
									})
								}
							} catch (error) {
								uni.hideLoading()
								console.error('取消申请失败:', error)
								uni.showToast({
									title: '取消失败，请重试',
									icon: 'none'
								})
							}
						}
					}
				})
			},

			// 修改信息 - 直接跳转到办理页面进行编辑
			handleEdit(index) {
				const item = this.checkinList[index]
				uni.redirectTo({
					url: `/pages/affairs/checkin-process?type=${this.housingType}&id=${item.recordId}&mode=edit`
				})
			},

			// 办理入住 (status=0 时显示)
			handleCheckin(index) {
				const item = this.checkinList[index]
				uni.redirectTo({
					url: `/pages/affairs/checkin-process?type=${this.housingType}&id=${item.recordId}`
				})
			},

			// 查看详情
			handleDetail(index) {
				const item = this.checkinList[index]
				uni.redirectTo({
					url: `/pages/affairs/checkin-detail?type=${this.housingType}&id=${item.recordId}`
				})
			},

			// 入住确认 (status=1 时显示，用于管理员审核后用户确认)
			handleConfirm(index) {
				const item = this.checkinList[index]
				uni.redirectTo({
					url: `/pages/affairs/checkin-confirm?type=${this.housingType}&id=${item.recordId}`
				})
			},

			// 入驻详情 (status=4 时显示，跳转到入驻详情页面)
			handleCheckinDetail(index) {
				const item = this.checkinList[index]
				uni.redirectTo({
					url: `/pages/affairs/checkin-detail?type=${this.housingType}&id=${item.recordId}`
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
		min-height: 526rpx;
		border-radius: 20rpx;
		background: #ffffff;
		padding: 32rpx 28rpx 22rpx 28rpx;
		margin: 0 auto 24rpx;
		box-sizing: border-box;
		position: relative;
	}

	.cancel-tag {
		position: absolute;
		top: 0;
		right: 0;
		padding: 8rpx 20rpx;
		background: #768394;
		color: #ffffff;
		font-size: 22rpx;
		border-radius: 0 20rpx 0 12rpx;
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
		width: 520rpx;
		color: #1a1a1a;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 37rpx;
		text-align: right;
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

	/* 按钮区域 */
	.button-group {
		display: flex;
		justify-content: flex-end;
		gap: 16rpx;
		margin-top: 15rpx;
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

	.btn-edit {
		background: #1281ff;
	}

	.btn-checkin {
		background: #1281ff;
	}

	.btn-detail {
		border: 1rpx solid #1281ff;
		background: #ffffff;
	}

	.btn-confirm {
		background: #1281ff;
	}

	.btn-handle {
		background: #52c41a;
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

