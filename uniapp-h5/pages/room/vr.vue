<template>
	<view class="vr-container">
		<web-view v-if="webViewUrl" :src="webViewUrl"></web-view>
		<view v-else class="loading-view">
			<text class="loading-text">加载中...</text>
		</view>
	</view>
</template>

<script>
	import config from '@/config/index'

	export default {
		data() {
			return {
				roomId: '',
				vrUrl: '',
				webViewUrl: ''
			}
		},
		onLoad(options) {
			if (options.roomId) {
				this.roomId = options.roomId
			}
			if (options.vrUrl) {
				this.vrUrl = decodeURIComponent(options.vrUrl)
				this.loadVR()
			} else {
				uni.showToast({
					title: 'VR资源地址缺失',
					icon: 'none'
				})
			}
		},
		methods: {
			loadVR() {
				// 转换为完整URL
				const fullUrl = this.getImageUrl(this.vrUrl)

				// 获取当前页面的base路径
				// #ifdef H5
				const baseUrl = window.location.origin
				// #endif

				// #ifndef H5
				const baseUrl = ''
				// #endif

				// 构建web-view的URL
				this.webViewUrl = `${baseUrl}/static/vr-viewer.html?imageUrl=${encodeURIComponent(fullUrl)}`

				console.log('VR Viewer URL:', this.webViewUrl)
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
	.vr-container {
		width: 100%;
		height: 100vh;
		background: #000;
	}

	.loading-view {
		width: 100%;
		height: 100vh;
		display: flex;
		align-items: center;
		justify-content: center;
		background: #000;
	}

	.loading-text {
		color: #fff;
		font-size: 32rpx;
	}
</style>
