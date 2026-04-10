<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 退租办理卡片列表 -->
			<view class="card" v-for="(item, index) in checkoutList" :key="index">
				<!-- 申请状态 -->
				<view class="info-row">
					<text class="info-label">申请状态</text>
					<text class="info-value" :class="statusClassMap[item.status] || ''">{{ item.statusText }}</text>
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

				<!-- 按钮区域 - 已入住确认（显示退租办理按钮） -->
				<view class="button-group" v-if="item.status === 'confirmed'">
					<view class="btn btn-primary" @click="handleCheckout(index)">
						<text class="btn-text-white">退租办理</text>
					</view>
				</view>

				<!-- 按钮区域 - 审批中 -->
				<view class="button-group" v-if="item.status === 'pending'">
					<view class="btn btn-cancel" @click="handleCancel(index)">
						<text class="btn-text-blue">取消申请</text>
					</view>
				</view>

				<!-- 按钮区域 - 待确认 -->
				<view class="button-group" v-if="item.status === 'wait_confirm'">
					<view class="btn btn-primary" @click="handleConfirm(index)">
						<text class="btn-text-white">确认</text>
					</view>
				</view>

				<!-- 按钮区域 - 审批驳回/已确认 -->
				<view class="button-group" v-if="item.status === 'rejected' || item.status === 'confirmed_done'">
					<view class="btn btn-detail" @click="handleDetail(index)">
						<text class="btn-text-blue">查看详情</text>
					</view>
				</view>

				<!-- 按钮区域 - 已取消 -->
				<view class="button-group" v-if="item.status === 'cancelled'">
					<view class="btn btn-detail" @click="handleDetail(index)">
						<text class="btn-text-blue">查看详情</text>
					</view>
					<view class="btn btn-primary" @click="handleCheckout(index)">
						<text class="btn-text-white">再次退租</text>
					</view>
				</view>
			</view>

			<!-- 空状态 -->
			<view class="empty-state" v-if="checkoutList.length === 0 && !loading">
				<text class="empty-text">暂无可退租房源</text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import { getConfirmedCheckInList } from '@/api/checkin.js'
	import { getCheckoutList, cancelCheckoutApply } from '@/api/checkout.js'
	import authCheck from '@/mixins/authCheck'

	export default {
		data() {
			return {
				housingType: '',
				tenantId: null,
				loading: false,
				checkoutList: [],

				statusClassMap: {
					'pending': 'status-pending',
					'approved': 'status-approved',
					'rejected': 'status-rejected',
					'cancelled': 'status-cancelled',
					'confirmed': 'status-confirmed',
					'wait_confirm': 'status-wait-confirm',
					'confirmed_done': 'status-confirmed-done'
				}
			}
		},
		onLoad(options) {
			// 使用统一的登录检查
			authCheck.checkLogin.call(this, options, (options) => {
				// 使用真实登录用户的 userId
				this.tenantId = this.userId

				if (options.type) {
					this.housingType = options.type
				}
				this.loadCheckoutList()
			})
		},
		onShow() {
			// 每次页面显示时重新加载列表数据，确保显示最新状态
			// 包括：退租申请签字后、退租确认签字后返回
			if (this.tenantId) {
				this.loadCheckoutList()
			}
		},
		methods: {
			// 加载退租列表 (已入住确认的入住单 + 退租申请)
			async loadCheckoutList() {
				try {
					this.loading = true

					// 1. 获取已入住确认的入住单
					const checkinResponse = await getConfirmedCheckInList(this.tenantId)
					let checkinList = []
					if (checkinResponse.code === 200 && checkinResponse.data) {
						checkinList = checkinResponse.data.map(item => this.convertCheckInData(item))
					}

					// 2. 获取退租申请列表
					const checkoutResponse = await getCheckoutList(this.tenantId)
					let checkoutList = []
					if (checkoutResponse.code === 200 && checkoutResponse.data) {
						checkoutList = checkoutResponse.data.map(item => this.convertCheckoutData(item))
					}

					// 3. 合并列表：如果有退租申请，使用申请数据；否则显示入住单
					this.checkoutList = this.mergeCheckoutList(checkinList, checkoutList)

					console.log('获取到退租列表:', this.checkoutList.length, '条')
				} catch (error) {
					console.error('获取退租列表失败:', error)
					this.checkoutList = []
					uni.showToast({
						title: '加载失败，请重试',
						icon: 'none'
					})
				} finally {
					this.loading = false
				}
			},

			// 合并退租列表：退租申请优先于入住单
			mergeCheckoutList(checkinList, checkoutList) {
				const result = []

				// 遍历入住单，检查是否有对应的退租申请
				checkinList.forEach(checkinItem => {
					const checkoutItem = checkoutList.find(c => c.contractId === checkinItem.contractId)

					if (checkoutItem) {
						// 有退租申请，使用申请数据
						result.push(checkoutItem)
					} else {
						// 没有退租申请，显示入住单（可退租）
						result.push(checkinItem)
					}
				})

				// 添加没有对应入住单的退租申请（历史数据）
				checkoutList.forEach(checkoutItem => {
					const exists = result.find(r => r.contractId === checkoutItem.contractId)
					if (!exists) {
						result.push(checkoutItem)
					}
				})

				return result
			},

			// 转换入住单数据为前端显示格式
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
					contractId: item.contractId,
					status: 'confirmed',  // 已入住确认（可退租）
					statusText: '已入住',
					community: community || '未知小区',
					room: room || '未知房间',
					rentPeriod: rentPeriod || '-',
					rent: rent ? rent : '-',
					deposit: deposit ? deposit : '-'
				}
			},

			// 转换退租申请数据为前端显示格式
			convertCheckoutData(item) {
				// 后端现在返回的是包含完整信息的Map，直接使用即可
				// 状态映射
				const statusMap = {
					'0': 'pending',        // 审批中
					'1': 'approved',       // 审批通过（待用户确认）
					'2': 'rejected',       // 审批驳回
					'3': 'cancelled',      // 已取消
					'4': 'wait_confirm',   // 待确认
					'5': 'confirmed_done'  // 已确认
				}

				return {
					recordId: item.recordId, // 入住记录ID，用于再次退租
					applyId: item.applyId,
					contractId: item.contractId,
					status: statusMap[item.applyStatus] || 'pending',
					statusText: item.statusText || '审批中',
					community: item.community || '未知小区',
					room: item.room || '未知房间',
					rentPeriod: item.rentPeriod || '-',
					rent: item.rent ? item.rent : '-',
					deposit: item.deposit ? item.deposit : '-'
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

			// 退租办理 - 跳转到退租办理页面
			handleCheckout(index) {
				const item = this.checkoutList[index]

				if (item.recordId) {
					uni.navigateTo({
						url: `/subpkg/affairs/checkout-process?type=${this.housingType}&recordId=${item.recordId}`
					})
				}
			},

			// 取消申请
			async handleCancel(index) {
				const item = this.checkoutList[index]

				uni.showModal({
					title: '提示',
					content: '确定要取消此申请吗？',
					success: async (res) => {
						if (res.confirm) {
							try {
								const response = await cancelCheckoutApply(item.applyId)
								if (response.code === 200) {
									uni.showToast({
										title: '已取消',
										icon: 'success'
									})
									// 重新加载列表
									this.loadCheckoutList()
								} else {
									uni.showToast({
										title: response.msg || '取消失败',
										icon: 'none'
									})
								}
							} catch (error) {
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

			// 修改信息
			handleEdit(index) {
				const item = this.checkoutList[index]
				uni.navigateTo({
					url: `/subpkg/affairs/checkout-edit?type=${this.housingType}&applyId=${item.applyId}`
				})
			},

			// 确认 - 跳转到退租确认页面
			handleConfirm(index) {
				const item = this.checkoutList[index]
				uni.navigateTo({
					url: `/subpkg/affairs/checkout-confirm?type=${this.housingType}&applyId=${item.applyId}`
				})
			},

			// 查看详情
			handleDetail(index) {
				const item = this.checkoutList[index]
				uni.navigateTo({
					url: `/subpkg/affairs/checkout-detail?type=${this.housingType}&applyId=${item.applyId}`
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

	.status-wait-confirm {
		color: #1281ff;
	}

	.status-confirmed-done {
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

	.btn-cancel {
		border: 1rpx solid #1281ff;
		background: #ffffff;
	}

	.btn-edit {
		background: #1281ff;
	}

	.btn-detail {
		border: 1rpx solid #1281ff;
		background: #ffffff;
	}

	.btn-text-white {
		color: #ffffff;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.btn-text-blue {
		color: #1281ff;
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
