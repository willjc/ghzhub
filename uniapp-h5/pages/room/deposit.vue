<template>
	<view class="page">
		<!-- 账单列表 -->
		<view class="bill-list">
			<view class="bill-item" v-for="(bill, index) in billList" :key="bill.id" @click="toggleBill(index)">
				<image 
					class="bill-checkbox" 
					:src="bill.selected ? '/static/fangyaun/选中@2x.png' : '/static/fangyaun/未选中@2x.png'" 
					mode="aspectFit"
				></image>
				<view class="bill-card">
					<view class="bill-header">
						<text class="bill-title">账单期数：{{ bill.period }}</text>
						<view class="bill-status">
							<text class="status-text">{{ bill.status }}</text>
						</view>
					</view>
					<view class="bill-info">
						<text class="bill-label">账单金额：</text>
						<text class="bill-value">{{ bill.amount }}元</text>
					</view>
					<view class="bill-info">
						<text class="bill-label">账单周期：</text>
						<text class="bill-value">{{ bill.dateRange }}</text>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 底部结算栏 -->
		<view class="footer-bar">
			<view class="footer-left">
				<view class="select-all" @click="toggleSelectAll">
					<image 
						class="select-all-checkbox" 
						:src="isAllSelected ? '/static/fangyaun/选中@2x.png' : '/static/fangyaun/未选中@2x.png'" 
						mode="aspectFit"
					></image>
					<text class="select-all-text">全选</text>
				</view>
			</view>
			<view class="footer-center">
				<view class="total-info">
					<text class="total-label">已选{{ selectedCount }}项，合计：</text>
					<text class="total-symbol">¥</text>
					<text class="total-amount">{{ totalAmount }}</text>
				</view>
				<view class="discount-info" @click="toggleDetail">
					<text class="discount-text">共减 ¥ {{ discountAmount }}</text>
					<text class="detail-divider">|</text>
					<text class="detail-text">查看明细</text>
					<image class="detail-arrow" :class="{ expanded: !showDetail }" src="/static/fangyaun/明细@2x.png" mode="aspectFit"></image>
				</view>
			</view>
			<view class="checkout-btn" @click="checkout">
				<text class="checkout-text">去结算</text>
			</view>
		</view>
		
		<!-- 金额明细弹窗 -->
		<view class="detail-popup-mask" v-if="showDetail" @click="closeDetail">
			<view class="detail-popup" @click.stop>
				<image class="detail-close-icon" src="/static/关闭@2x.png" mode="aspectFit" @click="closeDetail"></image>
				<text class="detail-popup-title">金额明细</text>
				<text class="detail-popup-tip">实际优惠金额请以下单页为准</text>
				
				<view class="detail-row">
					<text class="detail-row-label">商品总价</text>
					<view class="detail-row-value">
						<text class="price-symbol">¥</text>
						<text class="price-number">{{ totalAmount }}</text>
					</view>
				</view>
				
				<view class="detail-row">
					<text class="detail-row-label">共减</text>
					<view class="detail-row-value discount">
						<text class="discount-label">减</text>
						<text class="price-symbol discount">¥</text>
						<text class="price-number discount">{{ discountAmount }}</text>
					</view>
				</view>
				
				<view class="detail-row total-row">
					<text class="detail-row-label">合计</text>
					<view class="detail-row-value">
						<text class="price-symbol">¥</text>
						<text class="price-number">{{ finalAmount }}</text>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import { get, post } from '@/utils/request'
	import { wechatPrepay, queryPayResult } from '@/api/pay'

	export default {
		data() {
			return {
				roomId: '',
				contractId: '',
				showDetail: false,
				billList: [],
				discountAmount: '0.00',
				loading: false,
				billInfo: null
			}
		},
		computed: {
			isAllSelected() {
				return this.billList.length > 0 && this.billList.every(bill => bill.selected)
			},
			selectedCount() {
				return this.billList.filter(bill => bill.selected).length
			},
			totalAmount() {
				return this.billList
					.filter(bill => bill.selected)
					.reduce((sum, bill) => sum + bill.amount, 0)
			},
			finalAmount() {
				return (this.totalAmount - parseFloat(this.discountAmount)).toFixed(0)
			}
		},
		onLoad(options) {
			if (options.roomId) {
				this.roomId = options.roomId
			}
			if (options.contractId) {
				this.contractId = options.contractId
			}

			// 如果没有contractId，提示错误
			if (!this.contractId) {
				uni.showToast({
					title: '缺少合同信息',
					icon: 'none'
				})
				setTimeout(() => {
					uni.navigateBack()
				}, 1500)
				return
			}

			this.loadBillList()
		},
		methods: {
			toggleBill(index) {
				// 只允许选择一个账单（押金账单应该只有1条）
				this.billList.forEach((bill, i) => {
					bill.selected = i === index
				})
			},
			toggleSelectAll() {
				const newValue = !this.isAllSelected
				this.billList.forEach(bill => {
					bill.selected = newValue
				})
			},
			toggleDetail() {
				this.showDetail = !this.showDetail
			},
			closeDetail() {
				this.showDetail = false
			},
			async checkout() {
				if (this.selectedCount === 0) {
					uni.showToast({
						title: '请选择要支付的账单',
						icon: 'none'
					})
					return
				}
				const selectedBill = this.billList.find(bill => bill.selected)
				if (!selectedBill) return
				// 将 billNo 赋值后交给 handlePay 统一处理
				this.billInfo = selectedBill
				await this.handlePay()
			},

			/**
			 * 判断当前运行环境
			 * 返回: 'miniprogram' | 'wechat_h5' | 'browser'
			 */
			detectPayEnv() {
				// #ifdef MP-WEIXIN
				return 'miniprogram'
				// #endif

				// #ifdef H5
				const ua = navigator.userAgent.toLowerCase()
				const isWechat = /micromessenger/.test(ua)
				return isWechat ? 'wechat_h5' : 'browser'
				// #endif
			},

			/**
			 * 获取微信 openid
			 */
			getOpenid() {
				return uni.getStorageSync('openid') || ''
			},

			async handlePay() {
				if (this.loading || !this.billInfo) return
				this.loading = true

				try {
					const payEnv = this.detectPayEnv()
					const billNo = this.billInfo.billNo

					let prepayParams = { billNo }

					if (payEnv === 'miniprogram' || payEnv === 'wechat_h5') {
						const openid = this.getOpenid()
						if (!openid) {
							uni.showToast({ title: '获取用户信息失败，请重新登录', icon: 'none' })
							this.loading = false
							return
						}
						prepayParams.payType = 'jsapi'
						prepayParams.openid = openid
					} else {
						prepayParams.payType = 'h5'
					}

					const res = await wechatPrepay(prepayParams)
					if (res.code !== 200) {
						uni.showToast({ title: res.msg || '预支付失败', icon: 'none' })
						return
					}

					if (payEnv === 'miniprogram') {
						await this.invokeWxPayMiniprogram(res.data)
					} else if (payEnv === 'wechat_h5') {
						await this.invokeWxPayJsapi(res.data)
					} else {
						// H5 WAP: redirect to WeChat payment page
						const redirectUrl = encodeURIComponent(
							window.location.origin + '/#/pages/room/payResult?billNo=' + billNo
						)
						window.location.href = res.data.mwebUrl + '&redirect_url=' + redirectUrl
						return
					}

					await this.pollPayResult(billNo)

				} catch (e) {
					console.error('支付失败', e)
					uni.showToast({ title: '支付失败，请重试', icon: 'none' })
				} finally {
					this.loading = false
				}
			},

			invokeWxPayMiniprogram(params) {
				return new Promise((resolve, reject) => {
					// #ifdef MP-WEIXIN
					wx.requestPayment({
						timeStamp: params.timeStamp,
						nonceStr:  params.nonceStr,
						package:   params.package,
						signType:  params.signType,
						paySign:   params.paySign,
						success:   resolve,
						fail:      reject
					})
					// #endif
				})
			},

			invokeWxPayJsapi(params) {
				return new Promise((resolve, reject) => {
					// #ifdef H5
					if (typeof WeixinJSBridge === 'undefined') {
						reject(new Error('WeixinJSBridge 未就绪'))
						return
					}
					WeixinJSBridge.invoke('getBrandWCPayRequest', {
						appId:     params.appId,
						timeStamp: params.timeStamp,
						nonceStr:  params.nonceStr,
						package:   params.package,
						signType:  params.signType,
						paySign:   params.paySign
					}, (res) => {
						if (res.err_msg === 'get_brand_wcpay_request:ok') {
							resolve(res)
						} else {
							reject(new Error(res.err_msg))
						}
					})
					// #endif
				})
			},

			async pollPayResult(billNo) {
				for (let i = 0; i < 10; i++) {
					await new Promise(r => setTimeout(r, 2000))
					try {
						const res = await queryPayResult(billNo)
						if (res.code === 200 && res.data.paid) {
							uni.showToast({ title: '支付成功', icon: 'success' })
							setTimeout(() => {
								uni.navigateTo({ url: '/pages/upload/index' })
							}, 1500)
							return
						}
					} catch (e) {
						console.error('查询支付结果失败', e)
					}
				}
				uni.showToast({ title: '支付结果未确认，请稍后在账单页查看', icon: 'none' })
			},

			async loadBillList() {
				try {
					// 调用API获取押金账单
					const res = await this.getDepositBill(this.contractId)

					if (res.code === 200) {
						// 将后端返回的账单数据转换为前端格式
						const bill = res.data
						this.billList = [{
							id: bill.billId,
							period: bill.period,
							status: bill.status,
							amount: parseFloat(bill.amount),
							dateRange: bill.dateRange,
							selected: true  // 默认选中
						}]
					} else {
						uni.showToast({
							title: res.msg || '获取账单失败',
							icon: 'none'
						})
						// 如果获取失败，返回上一页
						setTimeout(() => {
							uni.navigateBack()
						}, 1500)
					}
				} catch (error) {
					console.error('获取账单列表失败:', error)
					uni.showToast({
						title: '获取账单失败',
						icon: 'none'
					})
				}
			},
			// 获取押金账单API
			async getDepositBill(contractId) {
				return await get(`/h5/app/bill/deposit/${contractId}`)
			},
			// 支付账单API
			async payBill(billId, payAmount) {
				return await post('/h5/app/bill/pay', {
					billId: billId,
					payAmount: payAmount
				})
			}
		}
	}
</script>

<style scoped>
	.page {
		min-height: 95vh;
		background: #f5f6fc;
		padding: 24rpx;
	
		box-sizing: border-box;
	}
	
	/* 账单列表 */
	.bill-list {
		display: flex;
		flex-direction: column;
		gap: 24rpx;
	}
	
	.bill-item {
		display: flex;
		align-items: center;
		gap: 20rpx;
	}
	
	.bill-checkbox {
		width: 36rpx;
		height: 36rpx;
		flex-shrink: 0;
	}
	
	.bill-card {
		width: 642rpx;
		height: 214rpx;
		border-radius: 20rpx;
		background: #ffffff;
		padding: 28rpx;
		box-sizing: border-box;
	}
	
	.bill-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding-bottom: 14rpx;
		border-bottom: 1rpx solid #f0f0f0;
		margin-bottom: 18rpx;
	}
	
	.bill-title {
		height: 45rpx;
		color: #1a1a1a;
		font-size: 32rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 45rpx;
	}
	
	.bill-status {
		width: 105rpx;
		height: 48rpx;
		border-radius: 4rpx;
		background: rgba(250, 87, 64, 0.1);
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.status-text {
		color: #fa5740;
		font-size: 24rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
	}
	
	.bill-info {
		display: flex;
		align-items: center;
		margin-bottom: 12rpx;
	}
	
	.bill-label {
		height: 34rpx;
		color: #888888;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 34rpx;
	}
	
	.bill-value {
		height: 34rpx;
		color: #333333;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 34rpx;
	}
	
	/* 底部结算栏 */
	.footer-bar {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		height: 100rpx;
		background: #ffffff;
		display: flex;
		align-items: center;
		padding: 0 24rpx;
		box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
	}
	
	.footer-left {
		margin-right: 28rpx;
	}
	
	.select-all {
		display: flex;
		align-items: center;
		gap: 8rpx;
	}
	
	.select-all-checkbox {
		width: 36rpx;
		height: 36rpx;
	}
	
	.select-all-text {
		height: 34rpx;
		color: #888888;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 34rpx;
	}
	
	.footer-center {
		flex: 1;
		display: flex;
		flex-direction: column;
		justify-content: center;
	}
	
	.total-info {
		display: flex;
		align-items: baseline;
		justify-content: flex-end;
		margin-right: 16rpx;
	}
	
	.total-label {
		height: 34rpx;
		color: #333333;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 34rpx;
	}
	
	.total-symbol {
		height: 38rpx;
		color: #e5252b;
		font-size: 28rpx;
		font-weight: 500;
		font-family: "MiSans", sans-serif;
		text-align: left;
		line-height: 38rpx;
	}
	
	.total-amount {
		height: 46rpx;
		color: #e5252b;
		font-size: 34rpx;
		font-weight: 600;
		font-family: "MiSans", sans-serif;
		text-align: left;
		line-height: 46rpx;
	}
	
	.discount-info {
		display: flex;
		align-items: center;
		margin-top: 8rpx;
		justify-content: flex-end;
		margin-right: 30rpx;
	}
	
	.discount-text {
		height: 34rpx;
		color: #e5252b;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 34rpx;
	}
	
	.detail-divider {
		color: #e5252b;
		font-size: 24rpx;
		margin: 0 8rpx;
	}
	
	.detail-text {
		height: 34rpx;
		color: #e5252b;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 34rpx;
	}
	
	.detail-arrow {
		width: 20rpx;
		height: 16rpx;
		margin-left: 4rpx;
		transition: transform 0.3s;
	}
	
	.detail-arrow.expanded {
		transform: rotate(180deg);
	}
	
	.checkout-btn {
		width: 225rpx;
		height: 80rpx;
		border-radius: 12rpx;
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
		backdrop-filter: blur(6rpx);
		display: flex;
		align-items: center;
		justify-content: center;
		
	}
	
	.checkout-text {
		height: 42rpx;
		color: #ffffff;
		font-size: 30rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 42rpx;
	}
	
	/* 金额明细弹窗 */
	.detail-popup-mask {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 100rpx;
		background: rgba(0, 0, 0, 0.5);
		z-index: 999;
		display: flex;
		align-items: flex-end;
		justify-content: center;
	}
	
	.detail-popup {
		width: 750rpx;
		height: 424rpx;
		border-radius: 32rpx 32rpx 0 0;
		background: #ffffff;
		padding: 32rpx 32rpx 48rpx 32rpx;
		box-sizing: border-box;
		position: relative;
	}
	
	.detail-close-icon {
		width: 22.86rpx;
		height: 22.86rpx;
		position: absolute;
		top: 32rpx;
		right: 32rpx;
	}
	
	.detail-popup-title {
		height: 48rpx;
		color: #131313;
		font-size: 34rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 48rpx;
		display: block;
	
	}
	
	.detail-popup-tip {
		height: 37rpx;
		color: #131313;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 37rpx;
		margin-bottom: 45rpx;
		display: block;
 
	}
	
	.detail-row {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin-bottom: 36rpx;
	}
	
	.detail-row.total-row {
		margin-bottom: 0;


	}
	
	.detail-row-label {
		height: 45rpx;
		color: #333333;
		font-size: 32rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 45rpx;
	}
	
	.detail-row-value {
		display: flex;
		align-items: baseline;
	}
	
	.price-symbol {
		height: 40rpx;
		color: #333333;
		font-size: 30rpx;
		font-weight: 500;
		font-family: "MiSans", sans-serif;
		text-align: left;
		line-height: 40rpx;
	}
	
	.price-number {
		height: 51rpx;
		color: #333333;
		font-size: 38rpx;
		font-weight: 600;
		font-family: "MiSans", sans-serif;
		text-align: left;
		line-height: 51rpx;
	}
	
	.detail-row-value.discount {
		align-items: center;
	}
	
	.discount-label {
		height: 45rpx;
		color: #e5252b;
		font-size: 32rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 45rpx;
		margin-right: 4rpx;
	}
	
	.price-symbol.discount {
		color: #e5252b;
	}
	
	.price-number.discount {
		height: 46rpx;
		color: #e5252b;
		font-size: 34rpx;
		line-height: 46rpx;
	}
</style>

