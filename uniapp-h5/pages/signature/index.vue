<template>
	<view class="signature-page">
		<!-- 顶部导航栏 -->
		<view class="navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
			<view class="nav-left" @click="handleBack">
				<text class="back-icon">←</text>
				<text class="back-text">返回</text>
			</view>
			<text class="nav-title">请签名</text>
			<view class="nav-right" @click="handleClear">
				<text class="clear-text">清除</text>
			</view>
		</view>

		<!-- 签名提示 -->
		<view class="signature-tip">
			<text class="tip-text">请在下方区域内签名</text>
		</view>

		<!-- 签名画布区域 -->
		<view class="canvas-wrapper" ref="canvasWrapper">
			<!-- H5端直接使用原生canvas -->
			<!-- #ifdef MP-WEIXIN -->
			<canvas
				canvas-id="signatureCanvas"
				id="signatureCanvas"
				class="signature-canvas"
				@touchstart="mpTouchStart"
				@touchmove="mpTouchMove"
				@touchend="mpTouchEnd"
				disable-scroll="true"
			></canvas>
			<!-- #endif -->
		</view>

		<!-- 底部操作按钮 -->
		<view class="footer-actions">
			<view class="action-btn cancel-btn" @click="handleBack">
				<text class="btn-text">取消</text>
			</view>
			<view class="action-btn confirm-btn" @click="handleConfirm">
				<text class="btn-text">确认签名</text>
			</view>
		</view>
	</view>
</template>

<script>
// #ifdef H5
import SmoothSignature from 'smooth-signature'
import { submitCheckInConfirm } from '@/api/checkin.js'
import { submitCheckoutConfirm, submitCheckoutApply } from '@/api/checkout.js'
// #endif

export default {
	data() {
		return {
			signaturePad: null,
			canvasWidth: 0,
			canvasHeight: 0,
			// 入住确认模式参数
			recordId: null,
			equipmentList: [],
			housingType: '',
			// 退租确认模式参数
			applyId: null,
			// 退租办理申请模式参数
			checkoutApplyData: null,
			// 判断是哪种模式：
			// 有 recordId 为入住确认模式
			// 有 applyId 为退租确认模式
			// 有 mode=checkout_apply 为退租办理申请模式
			// 都没有为承诺书签名模式
			isCheckInMode: false,
			isCheckoutMode: false,
			isCheckoutApplyMode: false,
			submitting: false,
			statusBarHeight: 0,
			// 小程序端签名相关
			mpCtx: null,
			mpLastX: 0,
			mpLastY: 0,
			mpIsDrawing: false,
			mpHasDrawn: false,
			mpCanvasWidth: 0,
			mpCanvasHeight: 0
		}
	},
	onLoad(options) {
		// 获取状态栏高度，用于自定义导航栏偏移
		const systemInfo = uni.getSystemInfoSync()
		this.statusBarHeight = systemInfo.statusBarHeight || 0

		// 判断签名模式：
		// 有 recordId 参数为入住确认模式
		// 有 applyId 参数为退租确认模式
		// 有 mode=checkout_apply 参数为退租办理申请模式
		// 都没有为承诺书签名模式
		this.isCheckInMode = !!options.recordId
		this.isCheckoutMode = !!options.applyId
		this.isCheckoutApplyMode = options.mode === 'checkout_apply'

		// 入住确认模式：解析相关参数
		if (this.isCheckInMode) {
			if (options.recordId) {
				this.recordId = options.recordId
			}
			if (options.type) {
				this.housingType = options.type
			}
			if (options.data) {
				try {
					const data = JSON.parse(decodeURIComponent(options.data))
					this.equipmentList = data.equipmentList || []
					console.log('入住确认模式，接收到的设备列表:', this.equipmentList)
				} catch (e) {
					console.error('解析设备数据失败:', e)
				}
			}
			console.log('入住确认模式')
		} else if (this.isCheckoutMode) {
			// 退租确认模式：解析相关参数
			if (options.applyId) {
				this.applyId = options.applyId
			}
			if (options.type) {
				this.housingType = options.type
			}
			console.log('退租确认模式，申请ID:', this.applyId)
		} else if (this.isCheckoutApplyMode) {
			// 退租办理申请模式：解析相关参数
			if (options.type) {
				this.housingType = options.type
			}
			if (options.data) {
				try {
					this.checkoutApplyData = JSON.parse(decodeURIComponent(options.data))
					console.log('退租办理申请模式，申请数据:', this.checkoutApplyData)
				} catch (e) {
					console.error('解析退租申请数据失败:', e)
				}
			}
			console.log('退租办理申请模式')
		} else {
			console.log('承诺书签名模式')
		}
	},
	onReady() {
		// #ifdef H5
		this.$nextTick(() => {
			setTimeout(() => {
				this.initSignature()
			}, 300)
		})
		// #endif
		// #ifdef MP-WEIXIN
		this.$nextTick(() => {
			setTimeout(() => {
				this.initMpSignature()
			}, 300)
		})
		// #endif
	},
	onUnload() {
		if (this.signaturePad) {
			this.signaturePad = null
		}
	},
	methods: {
		initSignature() {
			// #ifdef H5
			// 获取canvas容器
			const wrapper = this.$refs.canvasWrapper?.$el || this.$refs.canvasWrapper
			if (!wrapper) {
				console.error('找不到canvas-wrapper元素')
				return
			}

			// 获取容器实际尺寸
			const rect = wrapper.getBoundingClientRect()
			this.canvasWidth = Math.floor(rect.width)
			this.canvasHeight = Math.floor(rect.height)

			console.log('Canvas容器尺寸:', this.canvasWidth, this.canvasHeight)

			// 动态创建原生canvas元素，避免uni-app组件包装导致的事件问题
			let canvas = wrapper.querySelector('canvas')
			if (!canvas) {
				canvas = document.createElement('canvas')
				canvas.className = 'signature-canvas'
				canvas.style.cssText = 'width: 100%; height: 100%; display: block; background: #ffffff;'
				wrapper.appendChild(canvas)
			}

			console.log('创建的canvas:', canvas, 'tagName:', canvas.tagName)

			// 初始化签名板，让库自己处理尺寸
			this.signaturePad = new SmoothSignature(canvas, {
				width: this.canvasWidth,
				height: this.canvasHeight,
				minWidth: 2,
				maxWidth: 6,
				color: '#000000',
				bgColor: '#ffffff'
			})

			console.log('✅ 签名板初始化完成')
			// #endif
		},

		// ===== 小程序端签名实现 =====
		// #ifdef MP-WEIXIN
		initMpSignature() {
			const query = uni.createSelectorQuery().in(this)
			query.select('#signatureCanvas').boundingClientRect(rect => {
				if (!rect) {
					console.error('找不到 signatureCanvas')
					return
				}
				this.mpCanvasWidth = rect.width
				this.mpCanvasHeight = rect.height
				this.mpCtx = uni.createCanvasContext('signatureCanvas', this)
				this.mpCtx.setStrokeStyle('#000000')
				this.mpCtx.setLineWidth(3)
				this.mpCtx.setLineCap('round')
				this.mpCtx.setLineJoin('round')
				// 白色背景
				this.mpCtx.setFillStyle('#ffffff')
				this.mpCtx.fillRect(0, 0, this.mpCanvasWidth, this.mpCanvasHeight)
				this.mpCtx.draw()
				console.log('小程序签名板初始化完成, 尺寸:', this.mpCanvasWidth, this.mpCanvasHeight)
			}).exec()
		},
		mpTouchStart(e) {
			const touch = e.touches[0]
			this.mpLastX = touch.x
			this.mpLastY = touch.y
			this.mpIsDrawing = true
		},
		mpTouchMove(e) {
			if (!this.mpIsDrawing || !this.mpCtx) return
			const touch = e.touches[0]
			const x = touch.x
			const y = touch.y
			this.mpCtx.beginPath()
			this.mpCtx.moveTo(this.mpLastX, this.mpLastY)
			this.mpCtx.lineTo(x, y)
			this.mpCtx.stroke()
			this.mpCtx.draw(true)
			this.mpLastX = x
			this.mpLastY = y
			this.mpHasDrawn = true
		},
		mpTouchEnd() {
			this.mpIsDrawing = false
		},
		mpClear() {
			if (!this.mpCtx) return
			this.mpCtx.setFillStyle('#ffffff')
			this.mpCtx.fillRect(0, 0, this.mpCanvasWidth, this.mpCanvasHeight)
			this.mpCtx.draw()
			this.mpHasDrawn = false
		},
		mpGetPNG() {
			return new Promise((resolve, reject) => {
				uni.canvasToTempFilePath({
					canvasId: 'signatureCanvas',
					fileType: 'png',
					success: res => resolve(res.tempFilePath),
					fail: err => reject(err)
				}, this)
			})
		},
		// #endif

		handleClear() {
			// #ifdef H5
			if (this.signaturePad) {
				this.signaturePad.clear()
			}
			// #endif
			// #ifdef MP-WEIXIN
			this.mpClear()
			// #endif
		},

		handleBack() {
			if (this.submitting) {
				return
			}
			uni.navigateBack()
		},

		async handleConfirm() {
			if (this.submitting) return

			// #ifdef H5
			if (!this.signaturePad || this.signaturePad.isEmpty()) {
				uni.showToast({ title: '请先签名', icon: 'none' })
				return
			}
			const signatureData = this.signaturePad.getPNG()
			await this._dispatchConfirm(signatureData)
			// #endif

			// #ifdef MP-WEIXIN
			if (!this.mpHasDrawn) {
				uni.showToast({ title: '请先签名', icon: 'none' })
				return
			}
			try {
				const tempFilePath = await this.mpGetPNG()
				await this._dispatchConfirm(tempFilePath)
			} catch (e) {
				uni.showToast({ title: '获取签名失败，请重试', icon: 'none' })
			}
			// #endif
		},

		async _dispatchConfirm(signatureData) {
			if (this.isCheckInMode) {
				await this.handleCheckInConfirm(signatureData)
			} else if (this.isCheckoutMode) {
				await this.handleCheckoutConfirm(signatureData)
			} else if (this.isCheckoutApplyMode) {
				await this.handleCheckoutApply(signatureData)
			} else {
				this.handleCommitmentSignature(signatureData)
			}
		},

		// 入住确认模式处理
		async handleCheckInConfirm(signatureData) {
			if (!this.recordId) {
				uni.showToast({
					title: '参数错误',
					icon: 'none'
				})
				return
			}

			try {
				this.submitting = true
				uni.showLoading({
					title: '提交中...'
				})

				// 准备提交的数据
				const submitData = {
					recordId: this.recordId,
					signature: signatureData,
					selectedFacilities: JSON.stringify(this.equipmentList)
				}

				console.log('提交入住确认数据:', submitData)

				// 调用API
				const response = await submitCheckInConfirm(submitData)

				uni.hideLoading()

				if (response.code === 200) {
					uni.showToast({
						title: '入住确认成功',
						icon: 'success',
						duration: 2000
					})

					// 延迟跳转到入驻详情页面
					setTimeout(() => {
						uni.redirectTo({
							url: `/subpkg/affairs/checkin-detail?type=${this.housingType}&id=${this.recordId}`
						})
					}, 1500)
				} else {
					uni.showToast({
						title: response.msg || '提交失败',
						icon: 'none',
						duration: 2000
					})
				}
			} catch (error) {
				uni.hideLoading()
				console.error('提交入住确认失败:', error)
				uni.showToast({
					title: '提交失败，请重试',
					icon: 'none'
				})
			} finally {
				this.submitting = false
			}
		},

		// 退租确认模式处理
		async handleCheckoutConfirm(signatureData) {
			if (!this.applyId) {
				uni.showToast({
					title: '参数错误',
					icon: 'none'
				})
				return
			}

			try {
				this.submitting = true
				uni.showLoading({
					title: '提交中...'
				})

				// 准备提交的数据
				const submitData = {
					tenantSignature: signatureData
				}

				console.log('提交退租确认数据，申请ID:', this.applyId)

				// 调用API
				const response = await submitCheckoutConfirm(this.applyId, submitData)

				uni.hideLoading()

				if (response.code === 200) {
					uni.showToast({
						title: '退租确认成功',
						icon: 'success',
						duration: 2000
					})

					// 延迟返回退租列表页面
					setTimeout(() => {
						uni.navigateBack({
							delta: 2  // 返回到退租列表页面（退租确认 -> 退租详情 -> 退租列表）
						})
					}, 1500)
				} else {
					uni.showToast({
						title: response.msg || '提交失败',
						icon: 'none',
						duration: 2000
					})
				}
			} catch (error) {
				uni.hideLoading()
				console.error('提交退租确认失败:', error)
				uni.showToast({
					title: '提交失败，请重试',
					icon: 'none'
				})
			} finally {
				this.submitting = false
			}
		},

		// 退租办理申请模式处理
		async handleCheckoutApply(signatureData) {
			if (!this.checkoutApplyData) {
				uni.showToast({
					title: '参数错误',
					icon: 'none'
				})
				return
			}

			try {
				this.submitting = true
				uni.showLoading({
					title: '提交中...'
				})

				// 准备提交的数据（包含签字）
				const submitData = {
					contractId: this.checkoutApplyData.contractId,
					tenantId: this.checkoutApplyData.tenantId,
					houseId: this.checkoutApplyData.houseId,
					planCheckoutDate: this.checkoutApplyData.planCheckoutDate,
					checkoutReason: this.checkoutApplyData.checkoutReason,
					signature: signatureData  // 签字数据
				}

				console.log('提交退租申请数据:', submitData)

				// 调用API提交退租申请
				const response = await submitCheckoutApply(submitData)

				uni.hideLoading()

				if (response.code === 200) {
					uni.showToast({
						title: '提交成功',
						icon: 'success',
						duration: 2000
					})

					// 延迟返回退租列表页面
					setTimeout(() => {
						uni.navigateBack({
							delta: 2  // 返回到退租列表页面（签字 -> 退租办理 -> 退租列表）
						})
					}, 1500)
				} else {
					uni.showToast({
						title: response.msg || '提交失败',
						icon: 'none',
						duration: 2000
					})
				}
			} catch (error) {
				uni.hideLoading()
				console.error('提交退租申请失败:', error)
				uni.showToast({
					title: '提交失败，请重试',
					icon: 'none'
				})
			} finally {
				this.submitting = false
			}
		},

		// 承诺书签名模式处理
		handleCommitmentSignature(signatureData) {
			// 通过 eventChannel 返回签名数据给上一个页面
			const eventChannel = this.getOpenerEventChannel()
			if (eventChannel) {
				eventChannel.emit('acceptSignature', { signature: signatureData })
				console.log('签名数据已通过 eventChannel 返回')
			}

			// 返回上一��
			uni.navigateBack()
		}
	}
}
</script>

<style scoped>
.signature-page {
	width: 100%;
	height: 100vh;
	background: #f5f5f5;
	display: flex;
	flex-direction: column;
}

/* 顶部导航栏 */
.navbar {
	min-height: 88rpx;
	background: #ffffff;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 0 24rpx;
	border-bottom: 1rpx solid #e5e5e5;
}

.nav-left {
	display: flex;
	align-items: center;
	gap: 8rpx;
}

.back-icon {
	font-size: 40rpx;
	color: #333333;
	line-height: 1;
}

.back-text {
	font-size: 28rpx;
	color: #333333;
}

.nav-title {
	position: absolute;
	left: 50%;
	transform: translateX(-50%);
	font-size: 32rpx;
	font-weight: 500;
	color: #1a1a1a;
}

.nav-right {
	padding: 8rpx 16rpx;
}

.clear-text {
	font-size: 28rpx;
	color: #0f73ff;
}

/* 签名提示 */
.signature-tip {
	height: 80rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	background: #fff9e6;
	border-bottom: 1rpx solid #ffe6a0;
}

.tip-text {
	font-size: 26rpx;
	color: #ed6a0c;
}

/* 签名画布区域 */
.canvas-wrapper {
	flex: 1;
	background: #ffffff;
	margin: 20rpx;
	border-radius: 16rpx;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
	overflow: hidden;
}

.signature-canvas {
	width: 100%;
	height: 100%;
	display: block;
	background: #ffffff;
	min-height: 400rpx;
}

/* 底部操作按钮 */
.footer-actions {
	display: flex;
	gap: 20rpx;
	padding: 24rpx;
	background: #ffffff;
	border-top: 1rpx solid #e5e5e5;
}

.action-btn {
	flex: 1;
	height: 88rpx;
	border-radius: 16rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.cancel-btn {
	background: #f5f5f5;
}

.cancel-btn .btn-text {
	color: #666666;
}

.confirm-btn {
	background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
}

.confirm-btn .btn-text {
	color: #ffffff;
}

.btn-text {
	font-size: 32rpx;
	font-weight: 500;
}
</style>
