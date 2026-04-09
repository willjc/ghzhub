<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 调换房申请卡片列表 -->
			<view class="card" v-for="(item, index) in exchangeList" :key="index">
				<!-- 申请状态 -->
				<view class="info-row">
					<text class="info-label">申请状态</text>
					<text class="info-value" :class="statusClassMap[item.status] || ''">{{ getStatusText(item.status) }}</text>
				</view>

				<!-- 原房源信息 -->
				<view class="section-title">原房源</view>
				<view class="info-section">
					<view class="info-row">
						<text class="info-label">房源地址</text>
						<text class="info-value">{{ item.oldFullAddress || item.roomAddress || '-' }}</text>
					</view>
					<view class="info-row">
						<text class="info-label">合同期限</text>
						<text class="info-value">{{ item.contractStartDate || '-' }} ~ {{ item.contractEndDate || '-' }}</text>
					</view>
				</view>

				<!-- 换房箭头指示（仅已完成时显示） -->
				<view class="exchange-arrow" v-if="item.status === '1' && item.newFullAddress">
					<text class="arrow-text">↓ 换房成功 ↓</text>
				</view>

				<!-- 目标房源信息（仅已完成时显示） -->
				<view v-if="item.status === '1' && item.newFullAddress">
					<view class="section-title">目标房源</view>
					<view class="info-section">
						<view class="info-row">
							<text class="info-label">房源地址</text>
							<text class="info-value highlight">{{ item.newFullAddress }}</text>
						</view>
						<view class="info-row">
							<text class="info-label">换房时间</text>
							<text class="info-value">{{ item.exchangeTime || '-' }}</text>
						</view>
					</view>
				</view>

				<!-- 申请信息 -->
				<view class="section-title">申请信息</view>
				<view class="info-section">
					<view class="info-row">
						<text class="info-label">申请时间</text>
						<text class="info-value">{{ item.applyTime || '-' }}</text>
					</view>
					<view class="info-row last-row">
						<text class="info-label">申请原因</text>
						<text class="info-value">{{ item.exchangeReason || item.reason || '-' }}</text>
					</view>
				</view>

				<!-- 审核信息（有审核意见时显示） -->
				<view class="info-section" v-if="item.approveOpinion || item.status === '2'">
					<view class="info-row last-row">
						<text class="info-label">审核意见</text>
						<text class="info-value" :class="statusClassMap[item.status] || ''">{{ item.approveOpinion || (item.status === '2' ? '申请已拒绝' : '-') }}</text>
					</view>
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
	import authCheck from '@/mixins/authCheck'

	export default {
		data() {
			return {
				housingType: '',
				tenantId: null,
				loading: false,
				exchangeList: [],

				statusClassMap: {
					'0': 'status-pending',
					'1': 'status-approved',
					'2': 'status-rejected'
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
				this.loadExchangeList()
			})
		},
		onShow() {
			// 每次页面显示时重新加载列表数据，确保显示最新数据
			if (this.tenantId) {
				this.loadExchangeList()
			}
		},
		methods: {
			// 加载调换房申请列表
			async loadExchangeList() {
				try {
					this.loading = true
					const response = await getExchangeList(this.tenantId)

					if (response.code === 200) {
						// 处理数据，添加状态文本
						this.exchangeList = (response.data || []).map(item => {
							return {
								...item,
								statusText: this.getStatusText(item.status)
							}
						})
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

			// 获取状态文本
			getStatusText(status) {
				const textMap = {
					'0': '待审核',
					'1': '已完成',
					'2': '已拒绝'
				}
				return textMap[status] || '未知'
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

	/* 分区标题 */
	.section-title {
		color: #0f73ff;
		font-size: 28rpx;
		font-weight: 600;
		margin-bottom: 16rpx;
		margin-top: 8rpx;
	}

	.info-section {
		background: #f8f9fc;
		border-radius: 12rpx;
		padding: 16rpx 20rpx;
		margin-bottom: 16rpx;
	}

	/* 换房箭头 */
	.exchange-arrow {
		text-align: center;
		padding: 16rpx 0;
	}

	.arrow-text {
		color: #0f73ff;
		font-size: 26rpx;
		font-weight: 500;
	}

	.info-row {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;
	}

	.info-row:last-child {
		margin-bottom: 0;
	}

	.info-label {
		color: #888888;
		font-size: 26rpx;
		min-width: 140rpx;
	}

	.info-value {
		color: #1a1a1a;
		font-size: 26rpx;
		text-align: right;
		flex: 1;
		word-break: break-all;
	}

	.info-value.highlight {
		color: #0f73ff;
		font-weight: 500;
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
