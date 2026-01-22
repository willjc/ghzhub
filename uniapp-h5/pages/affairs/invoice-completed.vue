<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 状态区域 - 在卡片外 -->
			<view class="status-header">
				<image class="status-icon" src="/static/已开票@2x(1).png" mode="aspectFit"></image>
				<text class="status-text">已开票</text>
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
			
			<!-- 按钮区域 -->
			<view class="button-group">
				
				<view class="btn btn-download" @click="handleDownload">
					<text class="btn-text-white">下载</text>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import { getInvoiceDetail } from '@/api/invoice'
	import config from '@/config/index'

	export default {
		data() {
			return {
				userId: null,
				applyId: null,
				invoiceFile: '', // 发票文件路径
				invoiceData: {
					amount: '0.00',
					invoiceType: '电子普通发票',
					invoiceContent: '商品明细',
					headType: '个���',
					name: '',
					phone: '',
					email: '',
					applyTime: ''
				}
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
							headType: '个人',
							name: data.receiverName || data.invoiceTitle || '',
							phone: data.receiverPhone || '',
							email: data.receiverEmail || '',
							applyTime: this.formatDateTime(data.createTime)
						}

						// 获取发票文件
						if (res.data.invoice && res.data.invoice.invoiceFile) {
							this.invoiceFile = res.data.invoice.invoiceFile
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
			},

			// 预览发票
			handlePreview() {
				if (!this.invoiceFile) {
					uni.showToast({
						title: '暂无发票文件',
						icon: 'none'
					})
					return
				}

				// 拼接完整的文件URL
				const fileUrl = config.baseUrl + this.invoiceFile
				uni.downloadFile({
					url: fileUrl,
					success: (res) => {
						if (res.statusCode === 200) {
							const filePath = res.tempFilePath
							uni.openDocument({
								filePath: filePath,
								showMenu: true
							})
						}
					},
					fail: () => {
						uni.showToast({
							title: '预览失败',
							icon: 'none'
						})
					}
				})
			},

			// 下载发票
			handleDownload() {
				if (!this.invoiceFile) {
					uni.showToast({
						title: '暂无发票文件',
						icon: 'none'
					})
					return
				}

				// 拼接完整的文件URL
				const fileUrl = config.baseUrl + this.invoiceFile

				// #ifdef H5
				// H5端：使用浏览器原生下载
				const link = document.createElement('a')
				link.href = fileUrl
				link.target = '_blank'
				// 提取文件名
				const fileName = this.getFileName(this.invoiceFile)
				link.download = fileName
				document.body.appendChild(link)
				link.click()
				document.body.removeChild(link)
				uni.showToast({
					title: '下载中...',
					icon: 'success'
				})
				// #endif

				// #ifndef H5
				// 非H5端：使用uni.downloadFile + uni.saveFile
				uni.downloadFile({
					url: fileUrl,
					success: (res) => {
						if (res.statusCode === 200) {
							uni.showToast({
								title: '下载成功',
								icon: 'success'
							})
							// 保存到本地
							uni.saveFile({
								tempFilePath: res.tempFilePath,
								success: () => {
									uni.showToast({
										title: '已保存到本地',
										icon: 'success'
									})
								}
							})
						}
					},
					fail: () => {
						uni.showToast({
							title: '下载失败',
							icon: 'none'
						})
					}
				})
				// #endif
			},
			// 获取文件名
			getFileName(path) {
				if (!path) return ''
				const parts = path.split('/')
				return parts[parts.length - 1]
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
		padding: 0rpx 24rpx;
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
		color: #1976f8;
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

	/* 按钮区域 */
	.button-group {
		display: flex;
		justify-content: center;
		gap: 24rpx;
		margin-top: 48rpx;
		padding-bottom: 48rpx;
	}

	.btn {
		width: 200rpx;
		height: 80rpx;
		border-radius: 20rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.btn-preview {
		border: 2rpx solid #1976f8;
		background: #ffffff;
	}

	.btn-download {
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
	}

	.btn-text-blue {
		color: #1976f8;
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
</style>

