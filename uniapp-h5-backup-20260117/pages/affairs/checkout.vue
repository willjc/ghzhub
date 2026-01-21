<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 退租申请卡片列表 -->
			<view class="card" v-for="(item, index) in checkoutList" :key="index">
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
				
				<!-- 按钮区域 - 审批中 -->
				<view class="button-group" v-if="item.status === 'pending'">
					<view class="btn btn-cancel" @click="handleCancel(index)">
						<text class="btn-text-blue">取消申请</text>
					</view>
					<view class="btn btn-edit" @click="handleEdit(index)">
						<text class="btn-text-white">修改信息</text>
					</view>
				</view>
				
				<!-- 按钮区域 - 审批通过 -->
				<view class="button-group" v-if="item.status === 'approved'">
					<view class="btn btn-checkout" @click="handleCheckout(index)">
						<text class="btn-text-white">退租办理</text>
					</view>
				</view>
				
				<!-- 按钮区域 - 审批驳回/已取消 -->
				<view class="button-group" v-if="item.status === 'rejected' || item.status === 'cancelled'">
					<view class="btn btn-detail" @click="handleDetail(index)">
						<text class="btn-text-blue">查看详情</text>
					</view>
				</view>
				
				<!-- 按钮区域 - 退租确认 -->
				<view class="button-group" v-if="item.status === 'confirm'">
					<view class="btn btn-confirm" @click="handleConfirm(index)">
						<text class="btn-text-white">退租确认</text>
					</view>
				</view>
			</view>
			
			<!-- 空状态 -->
			<view class="empty-state" v-if="checkoutList.length === 0">
				<text class="empty-text">暂无退租申请</text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				housingType: '',
				checkoutList: [
					{
						status: 'pending',
						statusText: '审批中',
						community: '美好人家',
						room: '6号楼6单元606',
						rentPeriod: '2024年2月2日 至 2025年2月1日',
						rent: '2000元/月',
						deposit: '2000元'
					},
					{
						status: 'approved',
						statusText: '审批通过',
						community: '美好人家',
						room: '6号楼6单元606',
						rentPeriod: '2024年2月2日 至 2025年2月1日',
						rent: '2000元/月',
						deposit: '2000元'
					},
					{
						status: 'rejected',
						statusText: '审批驳回',
						community: '美好人家',
						room: '6号楼6单元606',
						rentPeriod: '2024年2月2日 至 2025年2月1日',
						rent: '2000元/月',
						deposit: '2000元'
					},
					{
						status: 'confirm',
						statusText: '退租确认',
						community: '美好人家',
						room: '6号楼6单元606',
						rentPeriod: '2024年2月2日 至 2025年2月1日',
						rent: '2000元/月',
						deposit: '2000元'
					}
				]
			}
		},
		onLoad(options) {
			if (options.type) {
				this.housingType = options.type
			}
			this.loadCheckoutList()
		},
		methods: {
			// 加载退租申请列表
			loadCheckoutList() {
				// TODO: 调用API获取退租申请数据
				console.log('加载退租申请，类型:', this.housingType)
			},
			
			// 获取状态样式类
			getStatusClass(status) {
				const classMap = {
					'pending': 'status-pending',
					'approved': 'status-approved',
					'rejected': 'status-rejected',
					'cancelled': 'status-cancelled',
					'confirm': 'status-confirm'
				}
				return classMap[status] || ''
			},
			
			// 取消申请
			handleCancel(index) {
				uni.showModal({
					title: '提示',
					content: '确定要取消此申请吗？',
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
			
			// 修改信息
			handleEdit(index) {
				const item = this.checkoutList[index]
				uni.navigateTo({
					url: `/pages/affairs/checkout-edit?type=${this.housingType}&id=${index}`
				})
			},
			
			// 退租办理
			handleCheckout(index) {
				const item = this.checkoutList[index]
				uni.navigateTo({
					url: `/pages/affairs/checkout-process?type=${this.housingType}&id=${index}`
				})
			},
			
			// 查看详情
			handleDetail(index) {
				const item = this.checkoutList[index]
				uni.navigateTo({
					url: `/pages/affairs/checkout-detail?type=${this.housingType}&id=${index}`
				})
			},
			
			// 退租确认
			handleConfirm(index) {
				const item = this.checkoutList[index]
				uni.navigateTo({
					url: `/pages/affairs/checkout-confirm?type=${this.housingType}&id=${index}`
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

	.btn-checkout {
		background: #1281ff;
	}

	.btn-detail {
		border: 1rpx solid #1281ff;
		background: #ffffff;
	}

	.btn-confirm {
		background: #1281ff;
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

