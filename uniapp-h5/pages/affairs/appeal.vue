<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 资格核验卡片列表 -->
			<view class="card" v-for="(item, index) in qualificationList" :key="index">
				<!-- 卡片头部 -->
				<view class="card-header">
					<text class="card-title">资格核验</text>
					<text class="record-link" @click="goToRecordDetail(item.appealId)">资格申诉记录>></text>
				</view>

				<!-- 分隔线 -->


				<!-- 信息列表 -->
				<view class="info-list">
					<view class="info-row">
						<text class="info-label">姓名</text>
						<text class="info-value">{{ item.name }}</text>
					</view>
					<view class="info-row">
						<text class="info-label">身份证号</text>
						<text class="info-value">{{ item.idCard }}</text>
					</view>
					<view class="info-row">
						<text class="info-label">手机号</text>
						<text class="info-value">{{ item.phone }}</text>
					</view>
					<view class="info-row">
						<text class="info-label">提交时间</text>
						<text class="info-value info-time">{{ item.createTime }}</text>
					</view>

					<!-- 不符合状态显示 -->
					<template v-if="item.status === 'failed'">
						<view class="info-row">
							<text class="info-label">资格状态</text>
							<text class="info-value status-failed">不符合</text>
						</view>
						<view class="info-row">
							<text class="info-label">资格不符合项</text>
							<text class="info-value">{{ item.failReason }}</text>
						</view>
					</template>

					<!-- 已申诉状态显示 -->
					<template v-if="item.status === 'appealed'">
						<view class="info-row">
							<text class="info-label">申诉类型</text>
							<text class="info-value">{{ item.appealType }}</text>
						</view>
					</template>
				</view>

				<!-- 按钮区域 - 不符合状态 -->
				<view class="button-group" v-if="item.status === 'failed'">
					<view class="btn btn-refresh" @click="handleRefresh(index)">
						<text class="btn-text-blue">刷新</text>
					</view>
					<view class="btn btn-appeal" @click="handleAppeal(index)">
						<text class="btn-text-white">申诉</text>
					</view>
				</view>
			</view>
		</scroll-view>

		<!-- 底部发起申诉按钮 -->
		<view class="bottom-btn-container">
			<view class="bottom-btn" @click="handleStartAppeal">
				<text class="bottom-btn-text">发起申诉</text>
			</view>
		</view>
	</view>
</template>

<script>
	import { getAppealList } from '@/api/appeal'

	export default {
		data() {
			return {
				qualificationList: [],
				housingType: '', // talent, guaranteed, market
				userId: null // 当前登录用户ID
			}
		},
		onLoad(options) {
			// 获取用户信息
			const userInfo = uni.getStorageSync('userInfo')
			if (!userInfo || !userInfo.userId) {
				uni.showToast({
					title: '请先登录',
					icon: 'none'
				})
				setTimeout(() => {
					uni.navigateTo({
						url: '/pages/login/index'
					})
				}, 1500)
				return
			}

			// 保存用户ID
			this.userId = userInfo.userId

			if (options.type) {
				this.housingType = options.type
			}
			this.loadQualificationData()
		},
		onShow() {
			// 每次页面显示时重新加载列表数据，确保显示最新数据
			if (this.userId) {
				this.loadQualificationData()
			}
		},
		methods: {
			// 加载资格数据
			async loadQualificationData() {
				if (!this.userId) return

				try {
					uni.showLoading({ title: '加载中...' })
					const res = await getAppealList(this.userId)

					if (res.code === 200 && res.data) {
						// 将后端数据映射为前端需要的格式
						this.qualificationList = res.data.map(appeal => ({
							appealId: appeal.appealId,
							name: appeal.realName || appeal.nickname || '未知',
							idCard: appeal.idCard || '***',
							phone: appeal.contactPhone || appeal.phone || '***',
							createTime: appeal.appealTime || appeal.createTime || '',
							status: this.getAppealStatus(appeal.handleResult),
							appealType: '学历',
							handleResult: appeal.handleResult
						})).sort((a, b) => {
							// 按提交时间倒序排列（最新的在前）
							return new Date(b.createTime) - new Date(a.createTime)
						})
					}
					uni.hideLoading()
				} catch (err) {
					console.error('加载申诉列表失败:', err)
					uni.hideLoading()
				}
			},

			// 获取申诉状态
			getAppealStatus(handleResult) {
				// 0: 待处理, 1: 已通过, 2: 已拒绝
				if (handleResult === '0') return 'pending'
				if (handleResult === '1') return 'approved'
				if (handleResult === '2') return 'rejected'
				return 'pending'
			},

			// 跳转到申诉详情
			goToRecordDetail(appealId) {
				if (appealId) {
					uni.navigateTo({
						url: '/pages/affairs/appeal-record?id=' + appealId
					})
				} else {
					uni.showToast({
						title: '暂无申诉记录',
						icon: 'none'
					})
				}
			},

			// 刷新资格
			handleRefresh(index) {
				uni.showLoading({
					title: '刷新中...'
				})
				// TODO: 调用刷新API
				setTimeout(() => {
					uni.hideLoading()
					uni.showToast({
						title: '刷新成功',
						icon: 'success'
					})
				}, 1000)
			},

			// 申诉
			handleAppeal(index) {
				const item = this.qualificationList[index]
				uni.navigateTo({
					url: `/pages/affairs/appeal-submit?type=${this.housingType}&id=${index}`
				})
			},

			// 发起申诉
			handleStartAppeal() {
				uni.navigateTo({
					url: '/pages/affairs/appeal-submit?type=' + this.housingType
				})
			}
		}
	}
</script>

<style scoped>
	.page {
		width: 100%;
		min-height: 95vh;
		background-color: #f5f6fc;
		display: flex;
		flex-direction: column;
	}

	.scroll-content {
		flex: 1;
		padding: 24rpx;
		padding-bottom: 140rpx;
		box-sizing: border-box;
	}

	.card {
		width: 702rpx;
		border-radius: 20rpx;
		background: #ffffff;
		padding: 28rpx;
		margin: 0 auto 24rpx;
		box-sizing: border-box;
	}

	.card-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 27rpx;
		border-bottom: 1rpx solid #e1eaf2;
		padding-bottom: 20rpx;
	}

	.card-title {
		height: 40rpx;
		color: #333333;
		font-size: 28rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 40rpx;
	}

	.record-link {
		height: 40rpx;
		color: #1281ff;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 40rpx;
	}

	.divider {
		width: 646rpx;
		height: 0;

		margin: 0 auto 24rpx;
	}

	.info-list {
		padding: 0 10rpx;
	}

	.info-row {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 28rpx;
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
	}

	.info-time {
		color: #666666;
	}

	.status-failed {
		color: #1a1a1a;
	}

	.button-group {
		display: flex;
		justify-content: flex-end;
		gap: 16rpx;
		margin-top: 30rpx;
	}

	.btn {
		width: 148rpx;
		height: 68rpx;
		border-radius: 12rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.btn-refresh {
		border: 1rpx solid #0063ff;
		background: #ffffff;
	}

	.btn-appeal {
		background: #1281ff;
	}

	.btn-text-blue {
		color: #0063ff;
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
