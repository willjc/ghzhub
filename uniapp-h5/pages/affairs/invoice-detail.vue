<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 状态区域 - 在卡片外 -->
			<view class="status-header">
				<image class="status-icon" src="/static/开票中@2x(1).png" mode="aspectFit"></image>
				<text class="status-text">{{ statusText }}</text>
			</view>
			
			<!-- 金额 -->
			<view class="amount-row">
				<text class="amount-label">发票金额：</text>
				<text class="amount-value">¥{{ invoiceData.amount }}</text>
			</view>
			
			<!-- 发票卡片 -->
			<view class="invoice-card">
				<!-- 详情信息 -->
				<view class="info-list">
					<view class="info-row">
						<text class="info-label">发票类型</text>
						<text class="info-value">{{ invoiceData.invoiceType }}</text>
					</view>
					<view class="info-row">
						<text class="info-label">发票内容</text>
						<text class="info-value">{{ invoiceData.invoiceContent }}</text>
					</view>
					<view class="info-row">
						<text class="info-label">抬头类型</text>
						<text class="info-value">{{ invoiceData.headType }}</text>
					</view>
					<view class="info-row">
						<text class="info-label">姓名</text>
						<text class="info-value">{{ invoiceData.name }}</text>
					</view>
					<view class="info-row">
						<text class="info-label">手机号码</text>
						<text class="info-value">{{ invoiceData.phone }}</text>
					</view>
					<view class="info-row">
						<text class="info-label">邮箱地址</text>
						<text class="info-value">{{ invoiceData.email }}</text>
					</view>
					<view class="info-row">
						<text class="info-label">开票金额</text>
						<text class="info-value">¥{{ invoiceData.amount }}</text>
					</view>
					<view class="info-row">
						<text class="info-label">申请时间</text>
						<text class="info-value">{{ invoiceData.applyTime }}</text>
					</view>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import { getInvoiceDetail } from '@/api/invoice'

	export default {
		data() {
			return {
				userId: null,
				applyId: null,
				status: 'pending', // pending: 开票中, completed: 已开票
				invoiceData: {
					amount: '0.00',
					invoiceType: '电子普通发票',
					invoiceContent: '商品明细',
					headType: '个人',
					name: '',
					phone: '',
					email: '',
					applyTime: ''
				}
			}
		},
		computed: {
			statusText() {
				return '开票中'
			}
		},
		onLoad(options) {
			// 获取用户信息
			const userInfo = uni.getStorageSync('userInfo')
			if (userInfo && userInfo.userId) {
				this.userId = userInfo.userId
			}

			if (options.applyId) {
				this.applyId = options.applyId
				this.loadData()
			}
		},
		methods: {
			async loadData() {
				if (!this.applyId) return

				try {
					const res = await getInvoiceDetail(this.applyId, this.userId)
					if (res.code === 200 && res.data) {
						const data = res.data
						this.invoiceData = {
							amount: data.invoiceAmount || '0.00',
							invoiceType: data.invoiceType === '1' ? '电子普通发票' : '增值税专用发票',
							invoiceContent: data.invoiceContent || '商品明细',
							headType: '个人', // 暂时只有个人
							name: data.receiverName || data.invoiceTitle || '',
							phone: data.receiverPhone || '',
							email: data.receiverEmail || '',
							applyTime: this.formatDateTime(data.createTime)
						}
					}
				} catch (e) {
					console.error('加载发票详情失败:', e)
				}
			},

			formatDateTime(dateStr) {
				if (!dateStr) return ''
				const date = new Date(dateStr)
				const year = date.getFullYear()
				const month = String(date.getMonth() + 1).padStart(2, '0')
				const day = String(date.getDate()).padStart(2, '0')
				const hours = String(date.getHours()).padStart(2, '0')
				const minutes = String(date.getMinutes()).padStart(2, '0')
				const seconds = String(date.getSeconds()).padStart(2, '0')
				return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
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
		padding: 0rpx 10rpx;
		box-sizing: border-box;
	}

	/* 状态区域 - 在卡片外 */
	.status-header {
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 24rpx;
		margin-top: 54rpx;
	}

	.status-icon {
		width: 30rpx;
		height: 30rpx;
		margin-right: 12rpx;
	}

	.status-text {
		color: #ff8d1a;
		font-size: 34rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 30rpx;
	}

	/* 金额 */
	.amount-row {
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 24rpx;
	}

	.amount-label {
		color: #323232;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 30rpx;
	}

	.amount-value {
		color: #323232;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 30rpx;
	}

	/* 发票卡片 */
	.invoice-card {
		border-radius: 20rpx;
		background: #ffffff url('/static/发票bg@2x.png') no-repeat top center;
		background-size: 100% auto;
		overflow: hidden;
		padding: 47rpx 36rpx 36rpx;
		box-sizing: border-box;
	}

	/* 信息列表 */
	.info-list {
		padding: 0;
	}

	.info-row {
		display: flex;
		align-items: center;
		margin-bottom: 28rpx;
	}

	.info-row:last-child {
		margin-bottom: 0;
	}

	.info-label {
		width: 156rpx;
		color: #666666;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 40rpx;
	}

	.info-value {
		color: #333333;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 37rpx;
	}
</style>
