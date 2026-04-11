<template>
	<view class="page">
		<!-- Tab 切换 -->
		<view class="tab-bar">
			<view class="tab-item" :class="{ active: currentTab === 'current' }" @click="switchTab('current')">
				<text class="tab-text" :class="{ active: currentTab === 'current' }">当前账单</text>
				<view class="tab-line" v-if="currentTab === 'current'"></view>
			</view>
			<view class="tab-item" :class="{ active: currentTab === 'history' }" @click="switchTab('history')">
				<text class="tab-text" :class="{ active: currentTab === 'history' }">历史账单</text>
				<view class="tab-line" v-if="currentTab === 'history'"></view>
			</view>
		</view>
		
		<!-- 账单列表 -->
		<scroll-view class="scroll-content" scroll-y>
			<view class="bill-list">
				<view class="bill-item" v-for="(bill, index) in currentBillList" :key="bill.id"
					@click="!bill.locked && toggleBill(index)">
					<image
						class="bill-checkbox"
						:src="bill.locked ? '/static/fangyaun/未选中@2x.png' : (bill.selected ? '/static/fangyaun/选中@2x.png' : '/static/fangyaun/未选中@2x.png')"
						mode="aspectFit"
						:style="bill.locked ? 'opacity:0.3' : ''"
					></image>
					<view class="bill-card" :style="bill.locked ? 'opacity:0.45' : ''"  >
						<view class="bill-header">
							<text class="bill-title">账单期数：{{ bill.period }}</text>
							<view class="bill-status" :class="bill.locked ? 'status-locked' : (statusClassMap[bill.status] || '')">
								<text class="status-text" :class="bill.locked ? 'status-locked' : (statusClassMap[bill.status] || '')">{{ bill.locked ? '需先付押金' : bill.statusText }}</text>
							</view>
						</view>
						<view class="bill-info">
							<text class="bill-label">小区：</text>
							<text class="bill-value">{{ bill.community }}</text>
						</view>
						<view class="bill-info">
							<text class="bill-label">房间：</text>
							<text class="bill-value">{{ bill.room }}</text>
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
			
			<!-- 空状态 -->
			<view class="empty-state" v-if="currentBillList.length === 0">
				<text class="empty-text">暂无账单</text>
			</view>
		</scroll-view>
		
		<!-- 底部结算栏 -->
		<view class="footer-bar" v-if="currentTab === 'current'">
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
	import { getBillListByUserId } from '@/api/bill.js'
	import authCheck from '@/mixins/authCheck'

	export default {
		data() {
			return {
				housingType: '',
				currentTab: 'current',
				showDetail: false,
				discountAmount: '0.00',
				loading: false,

				// 当前账单（未支付）
				billList: [],

				// 历史账单（已支付）
				historyBillList: [],

				// 当前登录用户信息
				userId: null,

				// 从合同页传入的参数
				contractId: null,   // 限定查某合同的账单
				filterBillType: null, // '1'=只看押金
				depositPaid: true,   // 押金是否已付（false时租金不可选）

				statusClassMap: {
					'paid': 'status-paid',
					'unpaid': 'status-unpaid',
					'locked': 'status-locked'
				}
			}
		},
		computed: {
			currentBillList() {
				return this.currentTab === 'current' ? this.billList : this.historyBillList
			},
			isAllSelected() {
				const list = this.currentBillList
				return list.length > 0 && list.every(bill => bill.selected)
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
			// 使用统一的登录检查
			authCheck.checkLogin.call(this, options, (options) => {
				if (options.type) this.housingType = options.type
				// 从合同页跳来时携带的参数
				if (options.contractId) this.contractId = parseInt(options.contractId)
				if (options.billType) this.filterBillType = options.billType
				// depositPaid=0 表示押金未付，租金账单灰色不可选
				this.depositPaid = options.depositPaid !== '0'
				this.loadBillList()
			})
		},
		methods: {
			/**
			 * 加载账���列表
			 */
			async loadBillList() {
				if (!this.userId) {
					console.error('用户ID不存在')
					return
				}

				try {
					this.loading = true
					uni.showLoading({
						title: '加载中...'
					})

					console.log('开始加载账单，用户ID:', this.userId)

					// 调用API获取所有账单
					const response = await getBillListByUserId(this.userId)

					uni.hideLoading()

					if (response.code === 200 && response.data) {
						const bills = response.data
						console.log('获取到账单数据:', bills.length, '条')

						// 映射并过滤
						let mappedBills = bills.map(bill => this.mapBillToFrontEnd(bill))

						// 从合同页跳来时，只显示指定类型账单
						if (this.filterBillType) {
							mappedBills = mappedBills.filter(b => b.billType === this.filterBillType)
						}

						this.billList = mappedBills.filter(bill => bill.status === 'unpaid')
						this.historyBillList = mappedBills.filter(bill => bill.status === 'paid')

						console.log('当前账单:', this.billList.length, '条')
						console.log('历史账单:', this.historyBillList.length, '条')
					} else {
						uni.showToast({
							title: response.msg || '加载失败',
							icon: 'none'
						})
					}
				} catch (error) {
					uni.hideLoading()
					console.error('加载账单失败:', error)
					uni.showToast({
						title: '加载失败，请重试',
						icon: 'none'
					})
				} finally {
					this.loading = false
				}
			},

			/**
			 * 将数据库账单映射为前端格式
			 */
			mapBillToFrontEnd(bill) {
				// 映射账单状态
				let status = 'unpaid'
				let statusText = '未支付'

				if (bill.billStatus === '1') {
					status = 'paid'
					statusText = '已支付'
				} else if (bill.billStatus === '2') {
					status = 'unpaid'
					statusText = '部分支付'
				} else if (bill.billStatus === '3') {
					status = 'unpaid'
					statusText = '已逾期'
				}

				// 映射账单期数
				let period = ''
				if (bill.billType === '1') {
					period = '押金'
				} else if (bill.billType === '2') {
					// 租金账单，从bill_period提取期数
					// bill_period格式：2026-01
					period = bill.billPeriod ? `${bill.billPeriod}房租` : '房租'
				} else if (bill.billType === '3') {
					period = '水费'
				} else if (bill.billType === '4') {
					period = '电费'
				} else if (bill.billType === '5') {
					period = '燃气费'
				} else if (bill.billType === '6') {
					period = '物业费'
				} else {
					period = '其他费用'
				}

				// 格式化账单周期
				let dateRange = ''
				if (bill.billDate) {
					const billDate = new Date(bill.billDate)
					const year = billDate.getFullYear()
					const month = String(billDate.getMonth() + 1).padStart(2, '0')
					dateRange = `${year}年${month}月`
				}

				// 拼接完整房间号：楼栋 + 单元 + 房源号
				// 例如：5号楼 + 2单元 + 101 = 5号楼2单元101
				let fullRoomNo = ''
				if (bill.buildingName) {
					fullRoomNo += bill.buildingName
				}
				if (bill.unitName) {
					fullRoomNo += bill.unitName
				}
				if (bill.houseNo) {
					fullRoomNo += bill.houseNo
				}
				// 如果没有拼接出任何内容，使用 houseCode
				if (!fullRoomNo && bill.houseCode) {
					fullRoomNo = bill.houseCode
				}

				return {
					id: bill.billId,
					billNo: bill.billNo,        // 微信支付按 billNo 查账单
					billType: bill.billType,
					period: period,
					status: status,
					statusText: statusText,
					// 押金未付时，租金账单锁定（灰色不可选）
					locked: !this.depositPaid && bill.billType === '2',
					community: bill.projectName || '港好住',
					room: fullRoomNo || '-',
					amount: parseFloat(bill.billAmount),
					dateRange: dateRange,
					selected: false
				}
			},

			switchTab(tab) {
				this.currentTab = tab
			},

			toggleBill(index) {
				if (this.currentTab === 'current') {
					this.billList[index].selected = !this.billList[index].selected
				}
			},
			
			toggleSelectAll() {
				if (this.currentTab === 'current') {
					const newValue = !this.isAllSelected
					this.billList.forEach(bill => {
						bill.selected = newValue
					})
				}
			},
			
			toggleDetail() {
				this.showDetail = !this.showDetail
			},
			
			closeDetail() {
				this.showDetail = false
			},
			
			/**
			 * 去结算 - 调用微信支付
			 */
			async checkout() {
				const selectedBills = this.billList.filter(b => b.selected && !b.locked)
				if (selectedBills.length === 0) {
					uni.showToast({ title: '请选择要支付的账单', icon: 'none' })
					return
				}

				// JSAPI 支付需要 openid
				const userInfo = uni.getStorageSync('userInfo') || {}
				const openid = userInfo.wechatOpenid
				if (!openid) {
					uni.showToast({ title: '无法获取 openid，请重新登录', icon: 'none' })
					return
				}

				// 当前只支持逐笔支付，先付第一张（多选批量后续优化）
				const bill = selectedBills[0]

				try {
					uni.showLoading({ title: '发起支付...' })

					// 1. 向后端请求预支付参数
					const { post } = require('@/utils/request')
					const res = await post('/h5/pay/wechat/prepay', {
						billNo: bill.billNo || String(bill.id),
						payType: 'jsapi',
						openid
					})
					uni.hideLoading()

					if (res.code !== 200) {
						uni.showToast({ title: res.msg || '发起支付失败', icon: 'none' })
						return
					}

					const p = res.data

					// 2. 调起微信支付收银台
					// #ifdef MP-WEIXIN
					uni.requestPayment({
						provider: 'wxpay',
						timeStamp: p.timeStamp,
						nonceStr:  p.nonceStr,
						package:   p.package,
						signType:  p.signType || 'RSA',
						paySign:   p.paySign,
						success: () => {
							uni.showToast({ title: '支付成功', icon: 'success' })
							setTimeout(() => {
								this.loadBillList()  // 刷新账单列表
							}, 1500)
						},
						fail: (err) => {
							const msg = err.errMsg || ''
							uni.showToast({
								title: msg.includes('cancel') ? '已取消支付' : '支付失败，请重试',
								icon: 'none'
							})
						}
					})
					// #endif

				} catch (e) {
					uni.hideLoading()
					console.error('支付失败:', e)
					uni.showToast({ title: '支付失败，请重试', icon: 'none' })
				}
			}
		}
	}
</script>

<style scoped>
	.page {
		min-height: 95vh;
		background: #f5f6fc;
		display: flex;
		flex-direction: column;
	}
	
	/* Tab 切换 */
	.tab-bar {
		display: flex;
		background: #ffffff;
		padding: 0 24rpx;
	}
	
	.tab-item {
		flex: 1;
		display: flex;
		flex-direction: column;
		align-items: center;
		padding: 24rpx 0 0 0;
		position: relative;
	}
	
	.tab-text {
		height: 40rpx;
		color: #171a1f;
		font-size: 28rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 40rpx;
	}
	
	.tab-text.active {
		color: #1976f8;
	}
	
	.tab-line {
		width: 125rpx;
		height: 7rpx;
		background: #1976f8;
		
		margin-top: 12rpx;
	}
	
	/* 滚动内容 */
	.scroll-content {
		flex: 1;
		padding: 24rpx;
		padding-bottom: 160rpx;
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
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.bill-status.status-unpaid {
		background: rgba(250, 87, 64, 0.1);
	}
	
	.bill-status.status-paid {
		background: rgba(18, 165, 102, 0.1);
	}

	.bill-status.status-locked {
		background: rgba(150, 150, 150, 0.1);
		width: 160rpx;
	}
	.status-text.status-locked {
		color: #aaaaaa;
	}
	
	.status-text {
		font-size: 24rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
	}
	
	.status-text.status-unpaid {
		color: #fa5740;
	}
	
	.status-text.status-paid {
		color: #12a566;
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
	
	/* 空状态 */
	.empty-state {
		display: flex;
		justify-content: center;
		align-items: center;
		padding: 100rpx 0;
	}
	
	.empty-text {
		color: #999999;
		font-size: 28rpx;
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

