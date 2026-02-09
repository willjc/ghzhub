<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<view class="message-list" v-if="messageList.length > 0">
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
			<view class="empty-tips" v-else>
				<text>暂无消息</text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import config from '@/config/index'
	import authCheck from '@/mixins/authCheck'

	export default {
		data() {
			return {
				messageList: [],
				userId: null // 临时方案：从存储或参数获取userId
			}
		},
		onLoad() {
			// 使用统一的登录检查
			authCheck.checkLogin.call(this, {}, () => {
				this.userId = this.userId
				this.loadMessageList()
			})
		},
		methods: {

			/** 加载消息列表 */
			loadMessageList() {
				if (!this.userId) {
					console.warn('用户ID为空，无法加载消息');
					return;
				}

				uni.request({
					url: config.baseUrl + '/h5/userMessage/list',
					method: 'GET',
					data: {
						userId: this.userId
					},
					success: (res) => {
						if (res.data.code === 200) {
							const list = res.data.data || [];
							this.messageList = list.map(item => ({
								title: item.messageTitle,
								content: item.messageContent,
								time: this.formatDateTime(item.createTime),
								id: item.messageId
							}));
						} else {
							console.error('获取消息列表失败:', res.data.msg);
							uni.showToast({
								title: res.data.msg || '获取消息失败',
								icon: 'none'
							});
						}
					},
					fail: (err) => {
						console.error('请求失败:', err);
						// 使用静态数据作为降级方案
						this.messageList = [];
					}
				});
			},

			/** 查看详情 */
			viewDetail(item) {
				uni.navigateTo({
					url: '/pages/message/detail?id=' + item.id
				});
			},

			/** 格式化日期时间 */
			formatDateTime(dateStr) {
				if (!dateStr) return '';
				const date = new Date(dateStr);
				const year = date.getFullYear();
				const month = String(date.getMonth() + 1).padStart(2, '0');
				const day = String(date.getDate()).padStart(2, '0');
				const hour = String(date.getHours()).padStart(2, '0');
				const minute = String(date.getMinutes()).padStart(2, '0');
				return `${year}-${month}-${day} ${hour}:${minute}`;
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

	.message-text {
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

	.message-divider {
		width: 100%;
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
