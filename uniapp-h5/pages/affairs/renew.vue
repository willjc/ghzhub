<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 续租申请卡片列表 -->
			<view class="card" v-for="(item, index) in renewList" :key="index">
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

				<!-- 按钮区域 - 已入住确认显示续约按钮 -->
				<view class="button-group">
					<view class="btn btn-primary" @click="handleRenew(index)">
						<text class="btn-text-white">续约</text>
					</view>
				</view>
			</view>

			<!-- 空状态 -->
			<view class="empty-state" v-if="renewList.length === 0">
				<text class="empty-text">暂无续租申请</text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import { getConfirmedCheckInList } from '@/api/checkin.js'

	export default {
		data() {
			return {
				housingType: '',
				tenantId: 1, // TODO: 从登录信息获取租户ID
				loading: false,
				renewList: []
			}
		},
		onLoad(options) {
			if (options.type) {
				this.housingType = options.type
			}
			this.loadRenewList()
		},
		methods: {
			// 加载续租申请列表 (已入住确认的入住单)
			async loadRenewList() {
				try {
					this.loading = true
					console.log('加载续租列表，租户ID:', this.tenantId)

					const response = await getConfirmedCheckInList(this.tenantId)

					if (response.code === 200 && response.data) {
						// 转换后端数据格式为前端需要的格式
						this.renewList = response.data.map(item => {
							return this.convertCheckInData(item)
						})
						console.log('获取到续租列表:', this.renewList.length, '条')
					} else {
						this.renewList = []
					}
				} catch (error) {
					console.error('获取续租列表失败:', error)
					this.renewList = []
					uni.showToast({
						title: '加载失败，请重试',
						icon: 'none'
					})
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

				return {
					recordId: item.recordId,
					contractId: item.contractId,  // 保存合同ID，用于跳转
					checkinNo: item.checkinNo,
					status: 'confirmed',  // 已入住确认
					statusText: '已入住确认',
					community: community || '未知小区',
					room: room || '未知房间',
					rentPeriod: rentPeriod || '-',
					rent: rent ? rent : '-',
					deposit: deposit ? deposit : '-',
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
					'confirmed': 'status-confirmed'
				}
				return classMap[status] || ''
			},

			// 取消 (已入住确认状态不显示此按钮)
			handleCancel(index) {
				uni.showModal({
					title: '提示',
					content: '确定要取消此续租申请吗？',
					success: (res) => {
						if (res.confirm) {
							// TODO: 调用取消API
							uni.showToast({
								title: '已取消',
								icon: 'success'
							})
						}
					}
				})
			},

			// 续租 - 跳转到我的合同页面
			handleRenew(index) {
				const item = this.renewList[index]

				// 跳转到我的合同页面，传递合同ID参数
				uni.navigateTo({
					url: `/pages/affairs/contract?type=${this.housingType}&contractId=${item.contractId}`
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

	.btn-primary {
		background: #1281ff;
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

