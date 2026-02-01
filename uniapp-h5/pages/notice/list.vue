<template>
	<view class="page">
		<view class="page-header">
			<text class="page-title">通知公告</text>
		</view>
		<scroll-view class="scroll-content" scroll-y>
			<view class="notice-list" v-if="noticeList.length > 0">
				<view
					class="notice-card"
					v-for="(item, index) in noticeList"
					:key="index"
					@click="viewDetail(item)"
				>
					<view class="notice-content">
						<view class="notice-header">
							<image class="notice-icon" src="/static/通知@2x.png"></image>
							<text class="notice-title">{{ item.noticeTitle }}</text>
						</view>
						<text class="notice-text">{{ item.previewContent }}</text>
						<view class="notice-divider"></view>
						<view class="notice-footer">
							<text class="notice-time">{{ item.createTime }}</text>
							<view class="notice-link">
								<text>查看详情</text>
								<image class="link-arrow" src="/static/my/youjiantou@2x.png"></image>
							</view>
						</view>
					</view>
				</view>
			</view>
			<view class="empty-tips" v-else>
				<text>暂无通知公告</text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import { getNoticeList } from '@/api/config'

	export default {
		data() {
			return {
				noticeList: []
			}
		},
		onLoad() {
			this.loadNoticeList()
		},
		methods: {
			/** 加载通知公告列表 */
			async loadNoticeList() {
				try {
					const response = await getNoticeList()
					if (response.code === 200) {
						const list = response.data || []
						this.noticeList = list.map(item => ({
							noticeId: item.noticeId,
							noticeTitle: item.noticeTitle,
							previewContent: this.stripHtmlTags(item.noticeContent),
							createTime: this.formatDate(item.createTime)
						}))
					} else {
						uni.showToast({
							title: response.msg || '获取通知失败',
							icon: 'none'
						})
					}
				} catch (error) {
					console.error('加载通知列表失败:', error)
					this.noticeList = []
				}
			},

			/** 查看详情 */
			viewDetail(item) {
				uni.navigateTo({
					url: '/pages/notice/detail?noticeId=' + item.noticeId
				})
			},

			/** 去除HTML标签 */
			stripHtmlTags(html) {
				if (!html) return ''
				const div = document.createElement('div')
				div.innerHTML = html
				const text = div.textContent || div.innerText || ''
				return text.substring(0, 100) // 只取前100字作为预览
			},

			/** 格式化日期 */
			formatDate(dateStr) {
				if (!dateStr) return ''
				const date = new Date(dateStr)
				const year = date.getFullYear()
				const month = String(date.getMonth() + 1).padStart(2, '0')
				const day = String(date.getDate()).padStart(2, '0')
				return `${year}-${month}-${day}`
			}
		}
	}
</script>

<style scoped>
	.page {
		width: 100%;
		background-color: #f5f6fc;
		display: flex;
		flex-direction: column;
		min-height: 100vh;
	}

	.page-header {
		height: 88rpx;
		background: linear-gradient(135deg, #4A90E2 0%, #357ABD 100%);
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.page-title {
		color: #ffffff;
		font-size: 36rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.scroll-content {
		flex: 1;
		overflow-y: auto;
		padding: 24rpx;
		box-sizing: border-box;
	}

	.notice-list {
		display: flex;
		flex-direction: column;
		gap: 24rpx;
	}

	.notice-card {
		width: 702rpx;
		border-radius: 20rpx;
		opacity: 1;
		background: #ffffff;
		padding: 29rpx 28rpx;
		box-sizing: border-box;
		margin: 0 auto;
	}

	.notice-content {
		width: 100%;
		height: 100%;
		display: flex;
		flex-direction: column;
	}

	.notice-header {
		display: flex;
		align-items: center;
		margin-bottom: 16rpx;
	}

	.notice-icon {
		width: 50rpx;
		height: 50rpx;
		margin-right: 14rpx;
		flex-shrink: 0;
	}

	.notice-title {
		flex: 1;
		opacity: 1;
		color: #1a1a1a;
		font-size: 32rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 45rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.notice-text {
		width: 100%;
		opacity: 1;
		color: #666666;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
		margin-bottom: 22rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
	}

	.notice-divider {
		width: 100%;
		height: 0rpx;
		border-top: 1rpx solid #E5E5E5;
		margin-bottom: 24rpx;
	}

	.notice-footer {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.notice-time {
		height: 24rpx;
		opacity: 1;
		color: #999999;
		font-size: 24rpx;
		font-weight: 400;
		font-family: "PingFang", sans-serif;
		text-align: left;
		line-height: 24rpx;
	}

	.notice-link {
		display: flex;
		align-items: center;
		height: 34rpx;
	}

	.notice-link text {
		height: 34rpx;
		opacity: 1;
		color: #999999;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 34rpx;
	}

	.link-arrow {
		width: 24rpx;
		height: 24rpx;
		margin-left: 8rpx;
	}

	.empty-tips {
		display: flex;
		justify-content: center;
		align-items: center;
		padding-top: 200rpx;
	}

	.empty-tips text {
		color: #999999;
		font-size: 28rpx;
	}
</style>
