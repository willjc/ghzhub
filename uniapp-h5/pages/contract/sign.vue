<template>
	<view class="page">
		<!-- 租期选择卡片 -->
		<view class="lease-card">
			<view class="card-title">租期选择</view>

			<!-- 合同生效日期选�� -->
			<view class="date-row">
				<text class="label">合同生效日期</text>
				<picker mode="date" :value="startDate" :start="minDate" @change="onStartDateChange">
					<view class="date-picker">
						<text class="date-text" :class="{ placeholder: !startDate }">{{ startDate || '请选择生效日期' }}</text>
						<text class="arrow">›</text>
					</view>
				</picker>
			</view>

			<!-- 租期下拉选择 -->
			<view class="date-row">
				<text class="label">租期选择</text>
				<view class="select-wrapper">
					<select class="month-select" v-model="selectedMonths" @change="onMonthsChange">
						<option value="">请选择租期</option>
						<option value="1">1个月</option>
						<option value="2">2个月</option>
						<option value="3">3个月</option>
						<option value="4">4个月</option>
						<option value="5">5个月</option>
						<option value="6">6个月</option>
						<option value="7">7个月</option>
						<option value="8">8个月</option>
						<option value="9">9个月</option>
						<option value="10">10个月</option>
						<option value="11">11个月</option>
						<option value="12">12个月</option>
					</select>
				</view>
			</view>

			<!-- 租赁信息汇总 -->
			<view class="summary-box" v-if="contractData.rentPrice">
				<view class="summary-row">
					<text class="summary-label">合同结束</text>
					<text class="summary-value">{{ contractData.endDate }}</text>
				</view>
				<view class="summary-row">
					<text class="summary-label">月租金</text>
					<text class="summary-value price">¥{{ contractData.rentPrice }}</text>
				</view>
				<view class="summary-row">
					<text class="summary-label">押金</text>
					<text class="summary-value price">¥{{ contractData.deposit }}</text>
				</view>
				<view class="summary-row highlight">
					<text class="summary-label">合计</text>
					<text class="summary-value total">¥{{ contractData.totalAmount }}</text>
				</view>
			</view>
		</view>

		<!-- 合同内容卡片 -->
		<view class="contract-card" v-if="contractData.contractContent">
			<view class="card-title">合同内容</view>
			<view class="contract-content">
				<rich-text :nodes="contractData.contractContent"></rich-text>
			</view>
		</view>

		<!-- 底部操作区 -->
		<view class="bottom-area">
			<view class="agree-row">
				<checkbox-group @change="onAgreeChange">
					<label class="checkbox-label">
						<checkbox value="agree" :checked="agreed" color="#0f73ff"/>
						<text class="agree-text">我已阅读并同意以上合同内容</text>
					</label>
				</checkbox-group>
			</view>

			<view class="sign-btn" :class="{ disabled: !canSign }" @click="handleSign">
				<text class="btn-text">立即签字</text>
			</view>
		</view>

		<!-- 签字弹窗 -->
		<view class="signature-modal" v-if="showSignature" @click="closeSignature" @touchmove.prevent @mousemove.stop>
			<view class="modal-content" @click.stop @touchmove.stop @mousemove.stop>
				<view class="modal-header">
					<text class="modal-title">请签名</text>
					<text class="close-btn" @click="closeSignature">×</text>
				</view>
				<view class="signature-box" @touchmove.stop.prevent @mousemove.stop>
					<canvas
						ref="signatureCanvas"
						canvas-id="signatureCanvas"
						id="signatureCanvas"
						class="signature-canvas"
						:style="{ width: canvasWidth + 'px', height: canvasHeight + 'px' }"
						:width="canvasWidth"
						:height="canvasHeight"
						@touchmove.stop.prevent
					></canvas>
				</view>
				<view class="modal-footer">
					<view class="footer-btn clear-btn" @click="clearSignature">
						<text class="btn-text">清除</text>
					</view>
					<view class="footer-btn confirm-btn" @click="confirmSignature">
						<text class="btn-text">确认</text>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
// #ifdef H5
import SmoothSignature from 'smooth-signature'
// #endif
import { generateContract, signContract, renewContract } from '@/api/contract'

export default {
	data() {
		return {
			roomId: '',
			projectId: '',
			isRenew: false,  // 是否续租
			oldContractId: '',  // 原合同ID(续租时使用)
			selectedMonths: '',  // 选中的租期（字符串，select的value）
			startDate: '',  // 合同生效日期（用户选择）
			minDate: '',    // 可选择的最早日期（今天）
			agreed: false,
			showSignature: false,
			signatureData: '',
			contractData: {
				contractContent: '',
				templateId: null,
				rentPrice: 0,
				deposit: 0,
				totalRent: 0,
				totalAmount: 0,
				endDate: '',
				projectName: '',
				houseAddress: ''
			},
			// smooth-signature实例
			signaturePad: null,
			// canvas尺寸（实际像素）
			canvasWidth: 0,
			canvasHeight: 300
		}
	},
	computed: {
		canSign() {
			return this.agreed && this.contractData.contractContent && this.startDate && this.selectedMonths
		}
	},
	onLoad(options) {
		if (options.roomId) {
			this.roomId = options.roomId
		}
		if (options.projectId) {
			this.projectId = options.projectId
		}
		// 检查是否为续租
		if (options.isRenew === 'true') {
			this.isRenew = true
			this.oldContractId = options.oldContractId || ''
		}

		// 设置可选择的最小日期为今天，但不自动选择
		const today = new Date()
		this.minDate = this.formatDate(today)
		// startDate 由用户选择，保持空值
	},
	onReady() {
		// 初始化签名板将在打开弹窗时进行
	},
	methods: {
		onMonthsChange() {
			// select使用v-model，selectedMonths会自动更新
			// 选择租期后自动加载合同（如果不为空）
			if (this.selectedMonths) {
				this.loadContract()
			}
		},

		/**
		 * 合同生效日期变更处理
		 */
		onStartDateChange(e) {
			this.startDate = e.detail.value
			// 如果已选择租期，重新加载合同
			if (this.selectedMonths) {
				this.loadContract()
			}
		},

		async loadContract() {
			if (!this.startDate || !this.selectedMonths) return

			try {
				uni.showLoading({ title: '生成合同中...' })

				const response = await generateContract({
					houseId: this.roomId,
					rentMonths: this.selectedMonths,
					startDate: this.startDate
				})

				uni.hideLoading()

				if (response.code === 200) {
					this.contractData = response.data
				} else {
					throw new Error(response.msg || '生成合同失败')
				}
			} catch (error) {
				uni.hideLoading()
				console.error('加载合同失败:', error)
				uni.showToast({
					title: error.message || '加载失败',
					icon: 'none'
				})
			}
		},

		onAgreeChange(e) {
			this.agreed = e.detail.value.includes('agree')
		},

		handleSign() {
			if (!this.canSign) {
				if (!this.agreed) {
					uni.showToast({
						title: '请先同意合同内容',
						icon: 'none'
					})
				}
				return
			}

		// 跳转到签名页面
		uni.navigateTo({
			url: '/pages/signature/index',
			events: {
				// 监听签名页面返回的数据
				acceptSignature: (data) => {
					this.signatureData = data.signature
					console.log('收到签名数据', this.signatureData)
					// 自动提交合同
					this.submitContract()
				}
			}
		})
		},

	initSignaturePad() {
		// #ifdef H5
		let canvas = document.querySelector('#signatureCanvas')
		if (!canvas) {
			console.error('找不到 canvas 元素')
			uni.showToast({
				title: '初始化签名板失败',
				icon: 'none'
			})
			return
		}

		// uni-app H5 编译后可能包装了元素，需要获取真正的 canvas
		if (canvas.tagName !== 'CANVAS') {
			const innerCanvas = canvas.querySelector('canvas')
			if (innerCanvas) {
				canvas = innerCanvas
			}
		}

		// 验证是否为真正的 canvas
		if (!canvas || canvas.tagName !== 'CANVAS' || typeof canvas.getContext !== 'function') {
			console.error('获取到的不是有效的 canvas 元素', canvas)
			uni.showToast({
				title: '签名板初始化失败',
				icon: 'none'
			})
			return
		}
		// 获取容器宽度（减去padding）
		const container = canvas.parentElement
		const containerWidth = container.offsetWidth
		const computedWidth = containerWidth > 60 ? containerWidth - 60 : 300

		// 先更新响应式数据，让Vue更新canvas的DOM属性
		this.canvasWidth = computedWidth
		this.canvasHeight = 300

		// 等待Vue更新DOM后再初始化签名板
		this.$nextTick(() => {
			// 再次获取canvas（确保使用Vue更新后的）
			let updatedCanvas = document.querySelector('#signatureCanvas')
			if (updatedCanvas.tagName !== 'CANVAS') {
				updatedCanvas = updatedCanvas.querySelector('canvas')
			}

			console.log('Canvas尺寸:', updatedCanvas.width, 'x', updatedCanvas.height)
			console.log('Canvas元素:', updatedCanvas, 'getContext:', typeof updatedCanvas.getContext)

			// 初始化签名板
			try {
				this.signaturePad = new SmoothSignature(updatedCanvas, {
					width: updatedCanvas.width,
					height: updatedCanvas.height,
					minWidth: 2,
					maxWidth: 4,
					color: '#000000',
					bgColor: '#fafafa'
				})
				console.log('签名板初始化成功', this.signaturePad)
			} catch (error) {
				console.error('初始化签名板失败:', error)
				uni.showToast({
					title: '签名板初始化失败',
					icon: 'none'
				})
			}
		})
		// #endif
	},

		closeSignature() {
			this.showSignature = false
		},

		clearSignature() {
			// #ifdef H5
			if (this.signaturePad) {
				this.signaturePad.clear()
			}
			// #endif
		},

		confirmSignature() {
			// #ifdef H5
			if (!this.signaturePad || this.signaturePad.isEmpty()) {
				uni.showToast({
					title: '请先签名',
					icon: 'none'
				})
				return
			}

			// 获取签名的base64数据
			const canvas = document.querySelector('#signatureCanvas')
			if (canvas) {
				this.signatureData = canvas.toDataURL('image/png')
				this.closeSignature()
				this.submitContract()
			}
			// #endif
		},

		async submitContract() {
			try {
				uni.showLoading({ title: '提交中...' })

				const requestData = {
					houseId: this.roomId,
					projectId: this.projectId,
					templateId: this.contractData.templateId,
					contractContent: this.contractData.contractContent,
					tenantSignature: this.signatureData,
					startDate: this.startDate,
					endDate: this.contractData.endDate,
					rentMonths: this.selectedMonths
				}

				// 续租时需要传递原合同ID
				if (this.isRenew) {
					requestData.oldContractId = this.oldContractId
				}

				// 根据是否续租调用不同接口
				const response = this.isRenew
					? await renewContract(requestData)
					: await signContract(requestData)

				uni.hideLoading()

				if (response.code === 200) {
					uni.showToast({
						title: this.isRenew ? '续租成功' : '签署成功',
						icon: 'success'
					})

					// 跳转逻辑: 续租直接返回我的合同,新租跳转到押金缴纳
					setTimeout(() => {
						if (this.isRenew) {
							// 续租成功,返回我的合同页面
							uni.navigateBack()
						} else {
							// 新租,跳转到押金缴纳页面
							uni.redirectTo({
								url: `/pages/room/deposit?roomId=${this.roomId}&contractId=${response.data.contractId}`
							})
						}
					}, 1500)
				} else {
					throw new Error(response.msg || '提交失败')
				}
			} catch (error) {
				uni.hideLoading()
				console.error('提交合同失败:', error)
				uni.showToast({
					title: error.message || '提交失败',
					icon: 'none'
				})
			}
		},

		formatDate(date) {
			const year = date.getFullYear()
			const month = String(date.getMonth() + 1).padStart(2, '0')
			const day = String(date.getDate()).padStart(2, '0')
			return `${year}-${month}-${day}`
		}
	}
}
</script>

<style scoped>
.page {
	min-height: 100vh;
	background: #f5f6fc;
	padding: 24rpx;
	padding-bottom: 200rpx;
}

/* 租期选择卡片 */
.lease-card {
	background: #ffffff;
	border-radius: 20rpx;
	padding: 30rpx;
	margin-bottom: 24rpx;
}

.card-title {
	font-size: 32rpx;
	font-weight: 500;
	color: #1a1a1a;
	margin-bottom: 24rpx;
}

.date-row {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 24rpx 0;
	border-bottom: 1rpx solid #e1eaf2;
}

.label {
	font-size: 28rpx;
	color: #333333;
}

.select-wrapper {
	display: flex;
	align-items: center;
}

.month-select {
	min-width: 200rpx;
	height: 60rpx;
	padding: 0 20rpx;
	border: 2rpx solid #e1eaf2;
	border-radius: 12rpx;
	font-size: 28rpx;
	color: #333333;
	background-color: #ffffff;
	outline: none;
}

.month-select:focus {
	border-color: #0f73ff;
}

.date-picker {
	display: flex;
	align-items: center;
	gap: 8rpx;
	cursor: pointer;
}

.date-text {
	font-size: 28rpx;
	color: #333333;
}

.date-text.placeholder {
	color: #999999;
}

.arrow {
	font-size: 36rpx;
	color: #999999;
	font-weight: 300;
}

.summary-box {
	margin-top: 30rpx;
	padding: 24rpx;
	background: #f8f9fb;
	border-radius: 12rpx;
}

.summary-row {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 12rpx 0;
}

.summary-row.highlight {
	border-top: 1rpx solid #e1eaf2;
	padding-top: 20rpx;
	margin-top: 12rpx;
}

.summary-label {
	font-size: 26rpx;
	color: #666666;
}

.summary-value {
	font-size: 26rpx;
	color: #333333;
}

.summary-value.price {
	color: #0f73ff;
	font-weight: 500;
}

.summary-value.total {
	font-size: 32rpx;
	color: #e5252b;
	font-weight: 700;
}

/* 合同内容卡片 */
.contract-card {
	background: #ffffff;
	border-radius: 20rpx;
	padding: 30rpx;
	margin-bottom: 24rpx;
}

.contract-content {
	max-height: 800rpx;
	overflow-y: auto;
	padding: 20rpx;
	background: #fafafa;
	border-radius: 12rpx;
	line-height: 1.8;
}

/* 底部操作区 */
.bottom-area {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	background: #ffffff;
	padding: 24rpx;
	box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.agree-row {
	margin-bottom: 20rpx;
}

.checkbox-label {
	display: flex;
	align-items: center;
}

.agree-text {
	font-size: 26rpx;
	color: #666666;
	margin-left: 12rpx;
}

.sign-btn {
	width: 100%;
	height: 92rpx;
	border-radius: 20rpx;
	background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
	display: flex;
	align-items: center;
	justify-content: center;
}

.sign-btn.disabled {
	background: #cccccc;
}

.btn-text {
	font-size: 32rpx;
	font-weight: 500;
	color: #ffffff;
}

/* 签字弹窗 */
.signature-modal {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.5);
	display: flex;
	align-items: center;
	justify-content: center;
	z-index: 999;
}

.modal-content {
	width: 90%;
	background: #ffffff;
	border-radius: 20rpx;
	overflow: hidden;
}

.modal-header {
	padding: 30rpx;
	display: flex;
	justify-content: space-between;
	align-items: center;
	border-bottom: 1rpx solid #e1eaf2;
}

.modal-title {
	font-size: 32rpx;
	font-weight: 500;
	color: #1a1a1a;
}

.close-btn {
	font-size: 60rpx;
	color: #999999;
	line-height: 1;
}

.signature-box {
	padding: 30rpx;
}

.signature-canvas {
	display: block;
	background: #fafafa;
	border: 2rpx dashed #cccccc;
	border-radius: 12rpx;
}

.modal-footer {
	display: flex;
	gap: 20rpx;
	padding: 30rpx;
}

.footer-btn {
	flex: 1;
	height: 80rpx;
	border-radius: 12rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.clear-btn {
	background: #f5f5f5;
	color: #666666;
}

.confirm-btn {
	background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
}

.clear-btn .btn-text {
	color: #666666;
}

.confirm-btn .btn-text {
	color: #ffffff;
}
</style>
