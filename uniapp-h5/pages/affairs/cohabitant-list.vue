<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 合租户申请卡片列表 -->
			<view class="card" v-for="(item, index) in cohabitantList" :key="index">
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
					<text class="info-label">合租人姓名</text>
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

				<!-- 申请时间 -->
				<view class="info-row">
					<text class="info-label">申请时间</text>
					<text class="info-value">{{ formatDateTime(item.applyTime) }}</text>
				</view>

				<!-- 与主承租人关系 -->
				<view class="info-row last-row">
					<text class="info-label">与主承租人关系</text>
					<text class="info-value">{{ item.relationship }}</text>
				</view>
			</view>

			<!-- 空状态 -->
			<view class="empty-state" v-if="cohabitantList.length === 0 && !loading">
				<text class="empty-text">暂无合租户申请</text>
			</view>
		</scroll-view>

		<!-- 底部申请按钮 -->
		<view class="bottom-btn-container">
			<view class="bottom-btn" @click="handleApply">
				<text class="bottom-btn-text">申请</text>
			</view>
		</view>
	</view>
</template>

<script>
	import { getCohabitantList } from '@/api/cohabitant.js'

	export default {
		data() {
			return {
				housingType: '',
				tenantId: null, // 从登录信息获取租户ID
				loading: false,
				cohabitantList: []
			}
		},
		onLoad(options) {
			// 从本地存储获取登录用户信息
			const userInfo = uni.getStorageSync('userInfo');
			if (!userInfo || !userInfo.userId) {
				uni.showToast({
					title: '请先登录',
					icon: 'none'
				});
				setTimeout(() => {
					uni.navigateTo({
						url: '/pages/login/index'
					});
				}, 1500);
				return;
			}

			this.tenantId = userInfo.userId;

			if (options.type) {
				this.housingType = options.type
			}
			this.loadCohabitantList()
		},
		methods: {
			// 加载合租户申请列表
			async loadCohabitantList() {
				try {
					this.loading = true
					const response = await getCohabitantList(this.tenantId)

					if (response.code === 200) {
						this.cohabitantList = response.data || []
					} else {
						this.cohabitantList = []
						uni.showToast({
							title: response.msg || '加载失败',
							icon: 'none'
						})
					}
				} catch (error) {
					console.error('获取合租户列表失败:', error)
					this.cohabitantList = []
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

			// 格式化日期时间
			formatDateTime(dateTime) {
				if (!dateTime) return ''
				// 2026-01-17T21:37:51.000+08:00 -> 2026-01-17 21:37
				return dateTime.replace('T', ' ').substring(0, 16)
			},

			// 申请合租户
			handleApply() {
				uni.navigateTo({
					url: `/pages/affairs/cohabitant?type=${this.housingType}`
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
