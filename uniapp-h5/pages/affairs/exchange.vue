<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 调换房申请卡片列表 -->
			<view class="card" v-for="(item, index) in exchangeList" :key="index">
				<!-- 申请状态 -->
				<view class="info-row">
					<text class="info-label">申请状态</text>
					<text class="info-value" :class="getStatusClass(item.status)">{{ item.statusText }}</text>
				</view>

				<!-- 合同编号 -->
				<view class="info-row">
					<text class="info-label">合同编号</text>
					<text class="info-value">{{ item.contractNo }}</text>
				</view>

				<!-- 申请人 -->
				<view class="info-row">
					<text class="info-label">申请人</text>
					<text class="info-value">{{ item.applicant }}</text>
				</view>

				<!-- 身份证号 -->
				<view class="info-row">
					<text class="info-label">身份证号</text>
					<text class="info-value">{{ item.idCard }}</text>
				</view>

				<!-- 联系电话 -->
				<view class="info-row">
					<text class="info-label">联系电话</text>
					<text class="info-value">{{ item.phone }}</text>
				</view>

				<!-- 房间地址 -->
				<view class="info-row">
					<text class="info-label">房间地址</text>
					<text class="info-value">{{ item.roomAddress }}</text>
				</view>

				<!-- 申请调换时间 -->
				<view class="info-row">
					<text class="info-label">申请调换时间</text>
					<text class="info-value">{{ item.applyTime }}</text>
				</view>

				<!-- 申请原因 -->
				<view class="info-row last-row">
					<text class="info-label">申请原因</text>
					<text class="info-value">{{ item.reason }}</text>
				</view>
			</view>

			<!-- 空状态 -->
			<view class="empty-state" v-if="exchangeList.length === 0 && !loading">
				<text class="empty-text">暂无调换房申请</text>
			</view>
		</scroll-view>

		<!-- 底部申请调换按钮 -->
		<view class="bottom-btn-container">
			<view class="bottom-btn" @click="handleApply">
				<text class="bottom-btn-text">申请调换</text>
			</view>
		</view>
	</view>
</template>

<script>
	import { getExchangeList } from '@/api/exchange.js'

	export default {
		data() {
			return {
				housingType: '',
				tenantId: 1, // TODO: 从登录信息获取租户ID
				loading: false,
				exchangeList: []
			}
		},
		onLoad(options) {
			if (options.type) {
				this.housingType = options.type
			}
			this.loadExchangeList()
		},
		methods: {
			// 加载调换房申请列表
			async loadExchangeList() {
				try {
					this.loading = true
					const response = await getExchangeList(this.tenantId)

					if (response.code === 200) {
						this.exchangeList = response.data || []
					} else {
						this.exchangeList = []
						uni.showToast({
							title: response.msg || '加载失败',
							icon: 'none'
						})
					}
				} catch (error) {
					console.error('获取调换房列表失败:', error)
					this.exchangeList = []
					uni.showToast({
						title: '加载失败，请重试',
						icon: 'none'
					})
				} finally {
					this.loading = false
				}
			},

			// 获取状态样式类
			getStatusClass(status) {
				const classMap = {
					'0': 'status-pending',    // 审批中
					'1': 'status-approved',   // 审批通过
					'2': 'status-rejected'    // 审批拒绝
				}
				return classMap[status] || ''
			},

			// 申请调换
			handleApply() {
				uni.navigateTo({
					url: `/pages/affairs/exchange-apply?type=${this.housingType}`
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
		max-width: 450rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
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

	/* 底部按钮 */
	.bottom-btn-container {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		padding: 24rpx;
		background: #f5f6fc;
		box-shadow: 0 -4rpx 12rpx rgba(0, 0, 0, 0.05);
	}

	.bottom-btn {
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
