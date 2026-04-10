<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 按日期分组的发票列表 -->
			<view class="date-group" v-for="(group, groupIndex) in invoiceGroups" :key="groupIndex">
				<!-- 日期标题 -->
				<text class="date-title">{{ group.date }}</text>
				
				<!-- 发票卡片列表 -->
				<view class="invoice-card" v-for="(item, index) in group.list" :key="index">
					<!-- 第一行：公司名称 | 金额 -->
					<view class="card-row">
						<text class="company-name">{{ item.companyName }}</text>
						
					</view>
					<!-- 第二行：发票类型 | 发票详情 -->
					<view class="card-row deV">
						<text class="invoice-type">{{ item.invoiceType }}</text>
						<text class="invoice-amount">¥{{ item.amount }}</text>
						
					</view>
					<!-- 第三行：状态 -->
					<view class="card-row">
						<text class="invoice-status" :class="statusClassMap[item.status] || ''">{{ item.statusText }}</text>
						<view class="detail-btn" @click="handleDetail(item)">
							<text class="detail-text">发票详情</text>
						</view>
					</view>
				</view>
			</view>
			
			<!-- 空状态 -->
			<view class="empty-state" v-if="invoiceGroups.length === 0">
				<text class="empty-text">暂无开票记录</text>
			</view>
		</scroll-view>
		
		<!-- 底部申请开票按钮 -->
		<view class="bottom-btn-container">
			<view class="bottom-btn" @click="handleApply">
				<text class="bottom-btn-text">申请开票</text>
			</view>
		</view>
	</view>
</template>

							
<script>
	import { getMyInvoiceList } from '@/api/invoice'
	import authCheck from '@/mixins/authCheck'

	export default {
		data() {
			return {
				housingType: '',
				userId: null,
				invoiceGroups: [],

				statusClassMap: {
					'1': 'status-pending',
					'2': 'status-done'
				}
			}
		},
		onLoad(options) {
			// 使用统一的登录检查
			authCheck.checkLogin.call(this, options, (options) => {
				if (options.type) {
					this.housingType = options.type
				}
				this.loadInvoiceList()
			})
		},
		onShow() {
			// 每次页面显示时重新加载列表数据，确保显示最新数据
			if (this.userId) {
				this.loadInvoiceList()
			}
		},
		methods: {
			// 获取发票类型文本
			getInvoiceTypeText(type) {
				return type === '1' ? '增值税电子普通发票' : '增值税专用发票'
			},

			// 获取状态文本
			getStatusText(status) {
				return status === '1' ? '开票中' : '已开票'
			},

			// 格式化日期
			formatDate(dateStr) {
				if (!dateStr) return ''
				const date = new Date(dateStr)
				const year = date.getFullYear()
				const month = String(date.getMonth() + 1).padStart(2, '0')
				const day = String(date.getDate()).padStart(2, '0')
				return `${year}/${month}/${day}`
			},

			// 查看发票详情
			handleDetail(item) {
				const pageUrl = item.applyStatus === '2'
					? `/subpkg/affairs/invoice-completed?applyId=${item.applyId}`
					: `/subpkg/affairs/invoice-detail?applyId=${item.applyId}`
				uni.navigateTo({
					url: pageUrl
				})
			},

			// 申请开票
			handleApply() {
				uni.navigateTo({
					url: `/subpkg/affairs/invoice-apply?type=${this.housingType}`
				})
			},

			// 加载发票列表
			async loadInvoiceList() {
				try {
					const res = await getMyInvoiceList(this.userId)
					if (res.code === 200 && res.data) {
						// 按日期分组
						const groups = {}
						res.data.forEach(item => {
							const date = this.formatDate(item.createTime)
							if (!groups[date]) {
								groups[date] = []
							}
							groups[date].push({
								id: item.applyId,
								applyId: item.applyId,
								companyName: item.invoiceTitle || '个人',
								invoiceType: this.getInvoiceTypeText(item.invoiceType),
								amount: item.invoiceAmount || '0.00',
								status: item.applyStatus,
								statusText: this.getStatusText(item.applyStatus),
								applyStatus: item.applyStatus
							})
						})

						// 转换为数组格式
						this.invoiceGroups = Object.keys(groups).map(date => ({
							date,
							list: groups[date]
						}))
					}
				} catch (e) {
					console.error('加载发票列表失败:', e)
					uni.showToast({
						title: '加载失败，请重试',
						icon: 'none'
					})
				}
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
		padding: 30rpx 24rpx;
		padding-bottom: 140rpx;
		box-sizing: border-box;
	}
	
	/* 日期分组 */
	.date-group {
		margin-bottom: 24rpx;
	}
	.deV{
		border-bottom: 1rpx solid #e9e9e9;
		padding-bottom: 28rpx;
		margin-bottom: 21rpx !important;
	}
	.date-title {
		height: 44rpx;
		color: #999999;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 44rpx;
		margin-bottom: 20rpx;
		display: block;
		text-align: center;
	}
	
	/* 发票卡片 */
	.invoice-card {
		width: 702rpx;
		height: 248rpx;
		border-radius: 20rpx;
		border: 1rpx solid #e9e9e9;
		background: #ffffff;
		margin: 0 auto 24rpx;
		box-sizing: border-box;
		padding: 32rpx 28rpx;
	}
	
	.card-row {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 8rpx;
	}
	
	.card-row:last-child {
		margin-bottom: 0;
	}
	
	.company-name {
		height: 44rpx;
		color: #323232;
		font-size: 32rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 44rpx;
	}
	
	.invoice-type {
		height: 36rpx;
		color: #737373;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 36rpx;
	}
	
	.invoice-status {
		height: 36rpx;
		font-size: 28rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 36rpx;
	}
	
	.invoice-status.status-pending {
		color: #ff8d1a;
	}
	
	.invoice-status.status-done {
		color: #333333;
	}
	
	.invoice-amount {
		height: 44rpx;
		color: #ff5733;
		font-size: 32rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 44rpx;
		text-align: right;
	}
	
	.detail-btn {
		width: 135rpx;
		height: 48rpx;
		border-radius: 8rpx;
		border: 1rpx solid #e1eaf2;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.detail-text {
		height: 36rpx;
		color: #333333;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 36rpx;
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

	/* 底部按钮 */
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

