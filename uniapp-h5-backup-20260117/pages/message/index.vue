<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<view class="message-list">
				<view 
					class="message-card" 
					v-for="(item, index) in messageList" 
					:key="index"
					@click="viewDetail(item)"
				>
					<view class="message-content">
						<view class="message-header">
							<image class="message-icon" src="/static/消息@2x.png"></image>
							<text class="message-title">{{ item.title }}</text>
						</view>
						<text class="message-text">{{ item.content }}</text>
						<view class="message-divider"></view>
						<view class="message-footer">
							<text class="message-time">{{ item.time }}</text>
							<view class="message-link">
								<text>查看详情</text>
								<image class="link-arrow" src="/static/my/youjiantou@2x.png"></image>
							</view>
						</view>
					</view>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				messageList: [
					{
						title: '保租房合同到期提醒',
						content: '这里平台公告内容详情这里平台公告内容详情这...',
						time: '2025-05-20 12:38:45'
					},
					{
						title: '保租房合同到期提醒',
						content: '这里平台公告内容详情这里平台公告内容详情这...',
						time: '2025-05-20 12:38:45'
					},
					{
						title: '保租房合同到期提醒',
						content: '这里平台公告内容详情这里平台公告内容详情这...',
						time: '2025-05-20 12:38:45'
					}
				]
			}
		},
		onLoad() {
			
		},
		methods: {
			viewDetail(item) {
				console.log('查看详情:', item)
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
		min-height: 95vh;
	}

	.scroll-content {
		flex: 1;
		overflow-y: auto;
		padding: 24rpx;
		box-sizing: border-box;
	}

	.message-list {
		display: flex;
		flex-direction: column;
		gap: 24rpx;
	}

	.message-card {
		width: 702rpx;
		border-radius: 20rpx;
		opacity: 1;
		background: #ffffff;
		padding: 29rpx 28rpx;
		box-sizing: border-box;
		margin: 0 auto;
	}

	.message-content {
		width: 100%;
		height: 100%;
		display: flex;
		flex-direction: column;
	}

	.message-header {
		display: flex;
		align-items: center;
		margin-bottom: 16rpx;
	}

	.message-icon {
		width: 50rpx;
		height: 50rpx;
		margin-right: 14rpx;
		flex-shrink: 0;
	}

	.message-title {
		width: 288rpx;
		height: 45rpx;
		opacity: 1;
		color: #1a1a1a;
		font-size: 32rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 45rpx;
	}

	.message-text {
		width: 646rpx;
		height: 40rpx;
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
		white-space: nowrap;
	}

	.message-divider {
		width: 646rpx;
		height: 0rpx;
		border-top: 1rpx solid #E5E5E5;
		margin-bottom: 24rpx;
	}

	.message-footer {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.message-time {
		height: 24rpx;
		opacity: 1;
		color: #999999;
		font-size: 24rpx;
		font-weight: 400;
		font-family: "PingFang", sans-serif;
		text-align: left;
		line-height: 24rpx;
	}

	.message-link {
		display: flex;
		align-items: center;
		height: 34rpx;
	}

	.message-link text {
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
</style>

