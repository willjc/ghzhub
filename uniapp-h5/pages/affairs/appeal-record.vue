<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 申诉记录卡片列表 -->
			<view class="card" v-for="(item, index) in recordList" :key="index">
				<!-- 申诉项 -->
				<view class="info-row">
					<text class="info-label">申诉项</text>
					<text class="info-value">{{ item.appealType }}</text>
				</view>
				
				<!-- 学历 -->
				<view class="info-row">
					<text class="info-label">学历</text>
					<text class="info-value">{{ item.education }}</text>
				</view>
				
				<!-- 附件 -->
				<view class="info-row attachment-row">
					<text class="info-label">附件</text>
					<view class="image-list">
						<image 
							class="attachment-image" 
							v-for="(img, imgIndex) in item.images" 
							:key="imgIndex"
							:src="img"
							mode="aspectFill"
							@click="previewImage(item.images, imgIndex)"
						></image>
					</view>
				</view>
				
				<!-- 申诉结果 -->
				<view class="info-row">
					<text class="info-label">申诉结果</text>
					<text class="info-value" :class="getStatusClass(item.status)">{{ item.statusText }}</text>
				</view>
				
				<!-- 驳回原因 -->
				<view class="info-row reject-reason" v-if="item.status === 'rejected'">
					<text class="info-label">驳回原因</text>
					<text class="info-value">{{ item.rejectReason }}</text>
				</view>
				
				<!-- 申诉时间 -->
				<view class="info-row">
					<text class="info-label">申诉时间</text>
					<text class="info-value">{{ item.appealTime }}</text>
				</view>
				
				<!-- 申诉审核 -->
				<view class="info-row1" >
					<text class="info-label">申诉审核</text>
					<text class="info-value">{{ item.auditTime }}</text>
				</view>
			</view>
			
			<!-- 空状态 -->
			<view class="empty-state" v-if="recordList.length === 0">
				<text class="empty-text">暂无申诉记录</text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import { getAppealDetail } from '@/api/appeal'
	import config from '@/config/index'

	export default {
		data() {
			return {
				appealId: null,
				recordList: []
			}
		},
		onLoad(options) {
			if (options.id) {
				this.appealId = options.id
				this.loadRecordDetail()
			} else {
				uni.showToast({
					title: '缺少申诉ID',
					icon: 'none'
				})
			}
		},
		methods: {
			// 加载申诉详情
			async loadRecordDetail() {
				try {
					uni.showLoading({ title: '加载中...' })
					const res = await getAppealDetail(this.appealId)

					if (res.code === 200 && res.data) {
						const appeal = res.data
						// 转换为页面需要的格式
						this.recordList = [{
							appealType: '学历',
							education: this.getEducationLabel(appeal.appealReason),
							images: appeal.appealAttachments ? appeal.appealAttachments.split(',').map(img => this.getImageUrl(img)) : [],
							status: this.getStatusValue(appeal.handleResult),
							statusText: this.getStatusText(appeal.handleResult),
							rejectReason: appeal.handleOpinion || '',
							appealTime: appeal.appealTime || appeal.createTime || '',
							auditTime: appeal.handleTime || ''
						}]
					}
					uni.hideLoading()
				} catch (err) {
					console.error('加载申诉详情失败:', err)
					uni.hideLoading()
					uni.showToast({
						title: '加载失败',
						icon: 'none'
					})
				}
			},

			// 获取学历标签
			getEducationLabel(value) {
				const educationMap = {
					'4': '大专',
					'5': '本科',
					'6': '硕士',
					'7': '博士'
				}
				return educationMap[value] || '未知'
			},

			// 获取状态值
			getStatusValue(handleResult) {
				if (handleResult === '0') return 'pending'
				if (handleResult === '1') return 'approved'
				if (handleResult === '2') return 'rejected'
				return 'pending'
			},

			// 获取状态文本
			getStatusText(handleResult) {
				const statusMap = {
					'0': '待处理',
					'1': '通过',
					'2': '驳回'
				}
				return statusMap[handleResult] || '待处理'
			},

			// 拼接图片完整URL
			getImageUrl(relativePath) {
				if (!relativePath) return ''
				if (relativePath.startsWith('http://') || relativePath.startsWith('https://')) {
					return relativePath
				}
				return config.baseUrl + (relativePath.startsWith('/') ? relativePath : '/' + relativePath)
			},

			// 获取状态样式类
			getStatusClass(status) {
				const classMap = {
					'pending': 'status-pending',
					'approved': 'status-approved',
					'rejected': 'status-rejected'
				}
				return classMap[status] || ''
			},

			// 预览图片
			previewImage(images, current) {
				uni.previewImage({
					urls: images,
					current: current
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
		box-sizing: border-box;
	}
	.reject-reason{
		border-bottom: 1rpx solid #f0f0f0;
		padding-bottom: 28rpx;
	}
	.card {
		width: 702rpx;
		min-height: 578rpx;
		border-radius: 20rpx;
		background: #ffffff;
		padding:32rpx 28rpx 28rpx 28rpx;
	
		box-sizing: border-box;
	}

	.info-row {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 28rpx;
	}
	.info-row1{
		display: flex;
		justify-content: space-between;
		align-items: center;
		
	}
	.attachment-row {
		display: flex;
		
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
		text-align: right;
	}

	.status-pending {
		color: #ff9500;
	}

	.status-approved {
		color: #34c759;
	}

	.status-rejected {
		color: #1a1a1a;
	}

	/* 图片列表 */
	.image-list {
		display: flex;
		flex-wrap: wrap;
		gap: 16rpx;
		justify-content: flex-end;
		flex: 1;
		margin-left: 20rpx;
	}

	.attachment-image {
		width: 100rpx;
		height: 100rpx;
		border-radius: 4rpx;
		background: rgba(0, 99, 255, 0.1);
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

