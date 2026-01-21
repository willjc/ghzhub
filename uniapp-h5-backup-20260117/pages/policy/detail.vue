<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 内容区域 -->
			<view class="content-wrapper">
				<!-- 标题 -->
				<text class="detail-title">{{ detailData.title }}</text>

				<!-- 政策信息 -->
				<view class="info-section" v-if="detailData.publishDept || detailData.publishDate">
					<view class="info-item" v-if="detailData.publishDept">
						<text class="info-label">发布部门：</text>
						<text class="info-value">{{ detailData.publishDept }}</text>
					</view>
					<view class="info-item" v-if="detailData.publishDate">
						<text class="info-label">发布日期：</text>
						<text class="info-value">{{ detailData.publishDate }}</text>
					</view>
				</view>

				<!-- 正文内容 -->
				<view class="content-divider"></view>
				<rich-text class="detail-content" :nodes="detailData.content"></rich-text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				policyId: null,
				detailData: {
					title: '',
					coverImage: '',
					publishDept: '',
					publishDate: '',
					content: ''
				}
			}
		},
		onLoad(options) {
			// 获取政策ID
			if (options.id) {
				this.policyId = options.id;
				this.loadDetail(options.id);
			}
		},
		methods: {
			/** 加载政策详情 */
			loadDetail(id) {
				uni.request({
					url: 'http://localhost:8090/h5/policy/' + id,
					method: 'GET',
					success: (res) => {
						if (res.data.code === 200) {
							const data = res.data.data;

							// 格式化发布日期
							let publishDate = '';
							if (data.publishDate) {
								publishDate = this.formatDate(data.publishDate);
							}

							this.detailData = {
								title: data.policyTitle,
								coverImage: data.coverImage,
								publishDept: data.publishDept,
								publishDate: publishDate,
								content: this.processHtmlContent(data.policyContent)
							};
						} else {
							uni.showToast({
								title: res.data.msg || '加载失败',
								icon: 'none'
							});
						}
					},
					fail: (err) => {
						console.error('请求失败:', err);
						uni.showToast({
							title: '网络请求失败',
							icon: 'none'
						});
					}
				});
			},

			/** 获取图片完整URL */
			getImageUrl(url) {
				if (!url) return '';

				// 外部链接(http/https开头)，直接返回
				if (url.startsWith('http://') || url.startsWith('https://')) {
					return url;
				}

				// 拼接后端baseUrl
				const baseUrl = 'http://localhost:8090';
				return baseUrl + (url.startsWith('/') ? url : '/' + url);
			},

			/** 处理HTML内容，替换图片路径 */
			processHtmlContent(html) {
				if (!html) return '';

				// 替换 /dev-api 前缀为完整URL
				const baseUrl = 'http://localhost:8090';
				let processedHtml = html.replace(/src="\/dev-api/g, `src="${baseUrl}`);

				// 同时处理可能存在的相对路径图片
				processedHtml = processedHtml.replace(/src="\/profile/g, `src="${baseUrl}/profile`);

				return processedHtml;
			},

			/** 格式化日期 YYYY-MM-DD */
			formatDate(dateStr) {
				if (!dateStr) return '';
				const date = new Date(dateStr);
				const year = date.getFullYear();
				const month = String(date.getMonth() + 1).padStart(2, '0');
				const day = String(date.getDate()).padStart(2, '0');
				return `${year}-${month}-${day}`;
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

	.scroll-content {
		flex: 1;
		overflow-y: auto;
	}

	.header-image {
		width: 100%;
		display: block;
	}

	.content-wrapper {
		padding: 24rpx;
		box-sizing: border-box;
		background-color: #ffffff;
		
	}

	.detail-title {

		min-height: 150rpx;
		opacity: 1;
		color: #1a1a1a;
		font-size: 40rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 50rpx;
		display: block;
		margin-bottom: 20rpx;
		word-wrap: break-word;
	}

	.info-section {
		margin-bottom: 20rpx;
	}

	.info-item {
		display: flex;
		align-items: flex-start;
		margin-bottom: 16rpx;
	}

	.info-label {
		color: #999999;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 36rpx;
		flex-shrink: 0;
	}

	.info-value {
		color: #666666;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 36rpx;
		flex: 1;
	}

	.content-divider {
		width: 100%;
		height: 1rpx;
		background-color: #e5e5e5;
		margin: 20rpx 0 30rpx;
	}

	.detail-content {
		opacity: 1;
		color: #333333;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
		display: block;
		word-wrap: break-word;
	}

	/* rich-text 内部元素样式 */
	.detail-content >>> img {
		max-width: 100%;
		height: auto;
		display: block;
		margin: 10rpx 0;
	}

	.detail-content >>> p {
		margin: 10rpx 0;
		line-height: 40rpx;
	}
</style>

