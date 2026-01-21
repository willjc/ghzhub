<template>
	<view class="page">
		
		
		<!-- 内容区域 -->
		<scroll-view class="scroll-content" scroll-y>
			<view class="listing-cards">
				<view 
					class="listing-card" 
					v-for="(item, index) in policyList" 
					:key="index"
					@click="viewDetail(item)"
				>
					<image class="listing-image" :src="item.image" mode="aspectFill"></image>
					<view class="listing-info">
						<text class="listing-title">{{ item.title }}</text>
						<view class="listing-footer">
							<text class="listing-office">{{ item.office }}</text>
							<text class="listing-date">{{ item.date }}</text>
						</view>
					</view>
				</view>
			</view>
			
			<!-- 底部提示 -->
			<view class="footer-line">
				<text class="footer-text">—— 我是有底线的 ——</text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import config from '@/config/index'

	export default {
		data() {
			return {
				policyList: []
			}
		},
		onLoad() {
			this.loadPolicyList();
		},
		methods: {
			/** 加载政策列表 */
			loadPolicyList() {
				uni.request({
					url: config.baseUrl + '/h5/policy/list',
					method: 'GET',
					data: {
						pageNum: 1,
						pageSize: 100
					},
					success: (res) => {
						if (res.data.code === 200) {
							const policies = res.data.rows || [];
							this.policyList = policies.map(item => this.formatPolicyItem(item));
						} else {
							console.error('加载政策列表失败:', res.data.msg);
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

			/** 格式化政策列表项 */
			formatPolicyItem(item) {
				// 格式化发布日期
				let publishDate = '';
				if (item.publishDate) {
					publishDate = this.formatDate(item.publishDate);
				}

				return {
					policyId: item.policyId,
					title: item.policyTitle,
					office: item.publishDept || '航空港区管委会',
					date: publishDate,
					image: this.getImageUrl(item.coverImage)
				};
			},

			/** 跳转到政策详情 */
			viewDetail(item) {
				uni.navigateTo({
					url: `/pages/policy/detail?id=${item.policyId}`
				});
			},

			/** 获取图片完整URL */
			getImageUrl(url) {
				if (!url) return '/static/矩形 21@2x.png'; // 默认图片

				// 外部链接(http/https开头)，直接返回
				if (url.startsWith('http://') || url.startsWith('https://')) {
					return url;
				}

				// 拼接后端baseUrl
				const baseUrl = config.staticUrl;
				return baseUrl + (url.startsWith('/') ? url : '/' + url);
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

	.header-image {
		width: 100%;
		display: block;
	}

	.scroll-content {
		flex: 1;
		overflow-y: auto;
		padding: 24rpx;
		box-sizing: border-box;
	}

	/* 房源卡片 */
	.listing-cards {
		display: flex;
		flex-direction: column;
		gap: 24rpx;
	}

	.listing-card {
		width: 702rpx;
		height: 202rpx;
		border-radius: 14.96rpx;
		opacity: 1;
		background: #ffffff;
		display: flex;
		padding: 16rpx;
		box-sizing: border-box;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.listing-image {
		width: 160rpx;
		height: 160rpx;
		border-radius: 12rpx;
		opacity: 1;
		margin-right: 24rpx;
		flex-shrink: 0;
	}

	.listing-info {
		flex: 1;
		display: flex;
		flex-direction: column;
		justify-content: space-between;
	}

	.listing-title {
		margin-top: 8rpx;
		width: 300rpx;
		height: 40rpx;
		opacity: 1;
		color: #1a1a1a;
		font-size: 30rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
		margin-bottom: 20rpx;
	}

	.listing-footer {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 8rpx;
	}

	.listing-office {
		width: 240rpx;
		height: 34rpx;
		opacity: 1;
		color: #666666;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 34rpx;
	}

	.listing-date {
		height: 34rpx;
		opacity: 1;
		color: #999999;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 34rpx;
	}

	.footer-line {
		text-align: center;
		padding: 36rpx 0;
	}

	.footer-text {
		height: 37rpx;
		opacity: 1;
		color: #c7c7c7;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 37rpx;
		display: inline-block;
	}
</style>

