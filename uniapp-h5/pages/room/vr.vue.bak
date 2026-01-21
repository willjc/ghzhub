<template>
	<view class="vr-container">
		<!-- VR查看器容器 -->
		<view id="viewer" class="viewer"></view>

		<!-- 加载提示 -->
		<view v-if="loading" class="loading-mask">
			<text class="loading-text">加载中...</text>
		</view>

		<!-- 错误提示 -->
		<view v-if="error" class="error-mask">
			<text class="error-text">{{ error }}</text>
			<button class="retry-btn" @click="loadVR">重试</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				roomId: '',
				vrUrl: '',
				loading: true,
				error: ''
			}
		},
		onLoad(options) {
			if (options.roomId) {
				this.roomId = options.roomId
			}
			if (options.vrUrl) {
				this.vrUrl = decodeURIComponent(options.vrUrl)
			}

			// 延迟加载，等待DOM渲染完成
			setTimeout(() => {
				this.loadVR()
			}, 500)
		},
		methods: {
			async loadVR() {
				if (!this.vrUrl) {
					this.error = '缺少VR图片地址'
					this.loading = false
					return
				}

				this.loading = true
				this.error = ''

				try {
					// 拼接完整URL
					const fullUrl = this.getImageUrl(this.vrUrl)

					// 使用web-view加载Photo Sphere Viewer
					// 由于uni-app H5不能直接使用Photo Sphere Viewer库
					// 我们创建一个简单的全景查看器
					this.initSimpleViewer(fullUrl)
				} catch (err) {
					console.error('VR加载失败:', err)
					this.error = 'VR加载失败: ' + err.message
				} finally {
					this.loading = false
				}
			},

			initSimpleViewer(imageUrl) {
				// 简单实现：直接显示全景图，可以缩放和拖动
				const viewer = document.getElementById('viewer')
				if (!viewer) {
					this.error = '查看器容器未找到'
					return
				}

				// 创建图片元素
				const img = document.createElement('img')
				img.src = imageUrl
				img.style.width = '100%'
				img.style.height = '100%'
				img.style.objectFit = 'cover'
				img.style.touchAction = 'pan-x pan-y pinch-zoom'

				// 加载成功
				img.onload = () => {
					this.loading = false
					console.log('VR图片加载成功')
				}

				// 加载失败
				img.onerror = () => {
					this.error = 'VR图片加载失败'
					this.loading = false
				}

				viewer.appendChild(img)

				// 添加触摸缩放支持
				this.addPinchZoom(img)
			},

			addPinchZoom(element) {
				let initialDistance = 0
				let currentScale = 1

				element.addEventListener('touchstart', (e) => {
					if (e.touches.length === 2) {
						initialDistance = this.getDistance(e.touches[0], e.touches[1])
					}
				})

				element.addEventListener('touchmove', (e) => {
					if (e.touches.length === 2) {
						e.preventDefault()
						const distance = this.getDistance(e.touches[0], e.touches[1])
						const scale = distance / initialDistance
						currentScale = Math.min(Math.max(0.5, currentScale * scale), 3)
						element.style.transform = `scale(${currentScale})`
						initialDistance = distance
					}
				})
			},

			getDistance(touch1, touch2) {
				const dx = touch1.clientX - touch2.clientX
				const dy = touch1.clientY - touch2.clientY
				return Math.sqrt(dx * dx + dy * dy)
			},

			/** 获取图片完整URL */
			getImageUrl(url) {
				if (!url) return ''
				if (url.startsWith('http://') || url.startsWith('https://')) {
					return url
				}
				const baseUrl = 'http://localhost:8090'
				return baseUrl + (url.startsWith('/') ? url : '/' + url)
			}
		},
		onUnload() {
			// 清理资源
			const viewer = document.getElementById('viewer')
			if (viewer) {
				viewer.innerHTML = ''
			}
		}
	}
</script>

<style scoped>
	.vr-container {
		width: 100vw;
		height: 100vh;
		position: relative;
		background: #000000;
		overflow: hidden;
	}

	.viewer {
		width: 100%;
		height: 100%;
	}

	.loading-mask {
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.8);
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.loading-text {
		color: #ffffff;
		font-size: 32rpx;
	}

	.error-mask {
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.9);
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 40rpx;
	}

	.error-text {
		color: #ffffff;
		font-size: 28rpx;
		margin-bottom: 40rpx;
		text-align: center;
	}

	.retry-btn {
		background: #4A90E2;
		color: #ffffff;
		border: none;
		padding: 20rpx 60rpx;
		border-radius: 8rpx;
		font-size: 28rpx;
	}
</style>
