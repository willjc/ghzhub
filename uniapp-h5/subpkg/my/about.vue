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
				return '<p>中国工艺美术协会成立于1988年，是国家一级社团组织。协会的工作宗旨：遵守国家法律、法规和国家政策，遵守社会道德风尚；按照社会主义市场经济体制和现代化建设的要求，发挥政府和企业之间的桥梁和纽带作用，热忱地为行业、企业和会员服务；保护传统工艺、传承民族文化，创造现代工艺、发展工艺产业，实现工艺美术事业和产业的共同繁荣。</p>';
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
