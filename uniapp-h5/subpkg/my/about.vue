<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<view class="card" v-if="content">
				<rich-text class="content" :nodes="content"></rich-text>
			</view>
			<view v-else class="loading">
				<text>加载中...</text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import config from '@/config/index'

	export default {
		data() {
			return {
				content: ''
			}
		},
		onLoad() {
			this.loadAboutContent();
		},
		methods: {
			/** 加载关于我们内容 */
			loadAboutContent() {
				uni.request({
					url: config.baseUrl + '/h5/notice/about',
					method: 'GET',
					success: (res) => {
						if (res.data.code === 200 && res.data.data) {
							// 处理HTML内容
							let htmlContent = res.data.data.noticeContent || res.data.data.notice_content || '';
							this.content = this.processHtmlContent(htmlContent);
						} else {
							this.content = this.getDefaultContent();
						}
					},
					fail: (err) => {
						console.error('获取关于我们内容失败:', err);
						this.content = this.getDefaultContent();
					}
				});
			},

			/** 处理HTML内容 */
			processHtmlContent(html) {
				if (!html) return '';
				// 如果已经是字符串，直接返回
				if (typeof html === 'string') {
					return html;
				}
				// 如果是Buffer，转为字符串
				return html.toString();
			},

			/** 获取默认内容（降级方案） */
			getDefaultContent() {
				return '<p>港区人才公寓、保租房、市场租赁房源信息平台</p>';
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
		box-sizing: border-box;
	}

	.card {
		border-radius: 20rpx;
		padding: 32rpx 40rpx;
		box-sizing: border-box;
		margin: 24rpx;
		background-color: #ffffff;
	}

	.content {
		display: block;
		color: #333333;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 48rpx;
	}

	/* rich-text 内部元素样式 */
	.content >>> p {
		margin: 10rpx 0;
		line-height: 48rpx;
		text-indent: 2em;
	}

	.loading {
		display: flex;
		justify-content: center;
		align-items: center;
		padding: 100rpx 0;
		color: #999999;
		font-size: 28rpx;
	}
</style>
