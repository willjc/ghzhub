<template>
	<view class="page">
		<!-- 图片分类列表 -->
		<view class="category-section" v-for="category in imageCategories" :key="category.key">
			<text class="category-title">{{ category.name }}（{{ category.images.length }}）</text>
			<view class="image-grid">
				<view class="image-item" v-for="(img, index) in category.images" :key="index" @click="previewImage(category.images, index)">
					<image class="image-thumbnail" :src="img" mode="aspectFill"></image>
					<view class="magnify-icon-wrapper">
						<image class="magnify-icon" src="/static/fangyaun/放大icon@2x.png" mode="aspectFit"></image>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import { getHouseImages } from '@/api/house'
import config from '@/config/index'

	export default {
		data() {
			return {
				roomId: '',
				imageCategories: [],
				loading: false
			}
		},
		onLoad(options) {
			if (options.roomId) {
				this.roomId = options.roomId
				this.loadImages()
			}
		},
		methods: {
			previewImage(images, index) {
				// 转换为完整URL
				const fullUrls = images.map(url => this.getImageUrl(url))
				uni.previewImage({
					urls: fullUrls,
					current: fullUrls[index]
				})
			},
			async loadImages() {
				if (!this.roomId) {
					uni.showToast({
						title: '房源ID缺失',
						icon: 'none'
					})
					return
				}

				this.loading = true
				try {
					const response = await getHouseImages(this.roomId)

					if (response.code === 200) {
						this.imageCategories = response.data || []

						if (this.imageCategories.length === 0) {
							uni.showToast({
								title: '暂无图片',
								icon: 'none'
							})
						}
					}
				} catch (error) {
					console.error('获取房源图片失败:', error)
				} finally {
					this.loading = false
				}
			},
			/** 获取图片完整URL - 遵循RuoYi标准 */
			getImageUrl(url) {
				if (!url) return ''

				// 外部链接(http/https开头),直接返回
				if (url.startsWith('http://') || url.startsWith('https://')) {
					return url
				}

				// RuoYi标准:数据库存储相对路径,前端拼接baseUrl
				const baseUrl = config.staticUrl

				// 如果已包含baseUrl,直接返回
				if (url.indexOf(baseUrl) !== -1) {
					return url
				}

				// 拼接baseUrl + 相对路径
				return baseUrl + (url.startsWith('/') ? url : '/' + url)
			}
		}
	}
</script>

<style scoped>
	.page {
		min-height: 95vh;
		background: #f5f6fc;
		padding: 30rpx 36rpx;
		box-sizing: border-box;
	}
	
	.category-section {
		margin-bottom: 28rpx;
	}
	
	.category-title {
		height: 45rpx;
		color: #1a1a1a;
		font-size: 32rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 45rpx;
		display: block;
		margin-bottom: 12rpx;
	}
	
	.image-grid {
		display: flex;
		flex-wrap: wrap;
		gap: 20rpx;
	}
	
	.image-item {
		width: 240rpx;
		height: 150rpx;
		border-radius: 12rpx;
		overflow: hidden;
		position: relative;
	}
	
	.image-thumbnail {
		width: 100%;
		height: 100%;
	}
	
	.magnify-icon-wrapper {
		position: absolute;
		right: 0rpx;
		bottom: 0rpx;
		width: 40rpx;
		height: 40rpx;
		border-radius: 12rpx 0 12rpx 0;
opacity: 1;
background: #000000a8;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.magnify-icon {
		width: 40rpx;
		height: 40rpx;
	}
</style>

