<template>
	<view class="page">
		<!-- Tab 切换 -->
		<view class="tab-bar">
			<view class="tab-item" :class="{ active: currentTab === 'pay' }" @click="switchTab('pay')">
				<text class="tab-text" :class="{ active: currentTab === 'pay' }">在线缴费</text>
				<view class="tab-line" v-if="currentTab === 'pay'"></view>
			</view>
			<view class="tab-item" :class="{ active: currentTab === 'checkin' }" @click="switchTab('checkin')">
				<text class="tab-text" :class="{ active: currentTab === 'checkin' }">入住办理</text>
				<view class="tab-line" v-if="currentTab === 'checkin'"></view>
			</view>
			<view class="tab-item" :class="{ active: currentTab === 'checkout' }" @click="switchTab('checkout')">
				<text class="tab-text" :class="{ active: currentTab === 'checkout' }">退租办理</text>
				<view class="tab-line" v-if="currentTab === 'checkout'"></view>
			</view>
		</view>
		
		<!-- 在线缴费内容 - 账单列表 -->
		<scroll-view class="scroll-content" scroll-y v-if="currentTab === 'pay'">
			<!-- 空状态 -->
			<view class="empty-state" v-if="billList.length === 0 && !loading">
				<image class="empty-icon" src="/static/empty@2x.png" mode="aspectFit"></image>
				<text class="empty-text">暂无账单</text>
			</view>

			<!-- 账单列表 -->
			<view class="bill-card" v-for="(bill, index) in billList" :key="index">
				<view class="bill-header">
					<text class="bill-title">{{ bill.batchName || bill.enterpriseName }}</text>
					<view class="bill-status" :class="'status-' + bill.billStatus">
						<text v-if="bill.billStatus === '1'">待支付</text>
						<text v-else-if="bill.billStatus === '2'">已支付</text>
						<text v-else>待审核</text>
					</view>
				</view>
				<view class="bill-row">
					<text class="bill-label">企业名称</text>
					<text class="bill-value">{{ bill.enterpriseName }}</text>
				</view>
				<view class="bill-row">
					<text class="bill-label">联系人</text>
					<text class="bill-value">{{ bill.contactPerson }}</text>
				</view>
				<view class="bill-row">
					<text class="bill-label">入驻日期</text>
					<text class="bill-value">{{ bill.checkInDate }}</text>
				</view>
				<view class="bill-row">
					<text class="bill-label">截止日期</text>
					<text class="bill-value">{{ bill.checkOutDate }}</text>
				</view>
				<view class="bill-row">
					<text class="bill-label">房源数量</text>
					<text class="bill-value">{{ bill.houseCount }}套</text>
				</view>
				<view class="bill-row">
					<text class="bill-label">计算月数</text>
					<text class="bill-value">{{ bill.months }}个月</text>
				</view>
				<view class="bill-row price-row">
					<text class="bill-label">优惠后总价</text>
					<text class="bill-value price">¥{{ bill.finalAmount }}</text>
				</view>
				<view class="bill-btn-wrap" v-if="bill.billStatus === '1'">
					<view class="bill-btn" @click="handlePay(bill)">
						<text class="bill-btn-text">立即缴费</text>
					</view>
				</view>
				<view class="bill-paid" v-else-if="bill.billStatus === '2'">
					<text class="paid-text">已支付</text>
				</view>
			</view>
		</scroll-view>
		
		<!-- 入住办理内容 - 列表 -->
		<scroll-view class="scroll-content" scroll-y v-if="currentTab === 'checkin' && !showCheckinDetail">
			<view class="house-card" v-for="(item, index) in checkinList" :key="index">
				<view class="house-row">
					<text class="house-label">小区</text>
					<text class="house-value">{{ item.community }}</text>
				</view>
				<view class="house-row">
					<text class="house-label">租期</text>
					<text class="house-value">{{ item.rentPeriod }}</text>
				</view>
				<view class="house-row">
					<text class="house-label">租金</text>
					<text class="house-value">{{ item.rent }}</text>
				</view>
				<view class="house-row with-border">
					<text class="house-label">押金</text>
					<text class="house-value">{{ item.deposit }}</text>
				</view>
				<view class="house-btn-wrap">
					<view class="house-btn" @click="handleCheckin(item)">
						<text class="house-btn-text">{{ item.status === 'done' ? '查看详情' : '入住办理' }}</text>
					</view>
				</view>
			</view>
		</scroll-view>
		
		<!-- 入住办理内容 - 详情 -->
		<scroll-view class="scroll-content" scroll-y v-if="currentTab === 'checkin' && showCheckinDetail">
			<!-- 入住信息卡片 -->
			<view class="card">
				<view class="card-header">
					<view class="card-indicator"></view>
					<text class="card-title">入住信息</text>
				</view>
				
				<view class="detail-row">
					<text class="detail-label">小区</text>
					<text class="detail-value">{{ currentCheckinItem.community }}</text>
				</view>
				<view class="detail-row">
					<text class="detail-label">租期</text>
					<text class="detail-value">{{ currentCheckinItem.rentPeriod }}</text>
				</view>
				<view class="detail-row">
					<text class="detail-label">租金</text>
					<text class="detail-value">{{ currentCheckinItem.rent }}</text>
				</view>
				<view class="detail-row">
					<text class="detail-label">押金</text>
					<text class="detail-value">{{ currentCheckinItem.deposit }}</text>
				</view>
			</view>
			
			<!-- 人员名单卡片 -->
			<view class="card">
				<view class="card-header">
					<view class="card-indicator"></view>
					<text class="card-title">人员名单</text>
					<view class="template-download" @click="handleDownloadTemplate">
						<image class="template-icon" src="/static/模版@2x.png" mode="aspectFit"></image>
						<text class="template-text">模版下载</text>
					</view>
				</view>
				
				<!-- 上传区域 -->
				<view class="upload-area" @click="handleChooseFile">
					<image class="upload-icon" src="/static/上传文件@2x.png" mode="aspectFit"></image>
					<text class="upload-text">点击上传文件</text>
				</view>
				
				<!-- 已上传文件列表 -->
				<view class="file-list" v-if="uploadedFiles.length > 0">
					<view class="file-item" v-for="(file, index) in uploadedFiles" :key="index">
						<text class="file-name">{{ file.name }}</text>
						<text class="file-size">{{ file.size }}</text>
						<text class="file-delete" @click="handleDeleteFile(index)">删除</text>
					</view>
				</view>
				
				<!-- 支持格式提示 -->
				<text class="support-tip">支持扩展名：rar.zip.doc.docx.pdf.jpg...</text>
			</view>
		</scroll-view>
		
		<!-- 入住办理底部按钮 -->
		<view class="bottom-btns-container" v-if="currentTab === 'checkin' && showCheckinDetail">
			<view class="btn-back" @click="handleBack">
				<text class="btn-back-text">返回</text>
			</view>
			<view class="btn-save" @click="handleSaveCheckin">
				<text class="btn-save-text">保存</text>
			</view>
		</view>
		
		<!-- 退租办理内容 - 列表 -->
		<scroll-view class="scroll-content" scroll-y v-if="currentTab === 'checkout'">
			<view class="house-card" v-for="(item, index) in checkoutList" :key="index">
				<view class="house-row">
					<text class="house-label">小区</text>
					<text class="house-value">{{ item.community }}</text>
				</view>
				<view class="house-row">
					<text class="house-label">租期</text>
					<text class="house-value">{{ item.rentPeriod }}</text>
				</view>
				<view class="house-row">
					<text class="house-label">租金</text>
					<text class="house-value">{{ item.rent }}</text>
				</view>
				<view class="house-row with-border">
					<text class="house-label">押金</text>
					<text class="house-value">{{ item.deposit }}</text>
				</view>
				<view class="house-btn-wrap">
					<view class="house-btn" @click="handleCheckout(item)">
						<text class="house-btn-text">{{ item.status === 'done' ? '查看详情' : '退租办理' }}</text>
					</view>
				</view>
			</view>
		</scroll-view>
		
		<!-- 底部按钮 -->
		<view class="bottom-btn-container" v-if="currentTab === 'pay'">
			<view class="bottom-btn" @click="handlePay">
				<text class="bottom-btn-text">在线缴费</text>
			</view>
		</view>
	</view>
</template>

<script>
import { get, post } from '@/utils/request'

export default {
	data() {
		return {
			currentTab: 'pay',
			// 账单列表
			billList: [],
			loading: false,
			userInfo: null,
			// 入住办理数据（保留原有功能）
			checkinList: [
				{
					id: 1,
					community: '美好人家',
					rentPeriod: '2024年2月2日 至2025年2月1日',
					rent: '2000元/月',
					deposit: '2000元',
					status: 'pending'
				},
				{
					id: 2,
					community: '美好人家',
					rentPeriod: '2024年2月2日 至2025年2月1日',
					rent: '2000元/月',
					deposit: '2000元',
					status: 'done'
				}
			],
			showCheckinDetail: false,
			currentCheckinItem: {},
			uploadedFiles: [
				{ name: 'AAAGCFF.PDF', size: '12.5Mb' }
			],
			// 退租办理相关
			checkoutList: [
				{
					id: 1,
					community: '美好人家',
					rentPeriod: '2024年2月2日 至2025年2月1日',
					rent: '2000元/月',
					deposit: '2000元',
					status: 'pending'
				}
			]
		}
	},
	onLoad() {
		// 获取用户信息
		this.userInfo = uni.getStorageSync('userInfo')
		// 加载账单列表
		this.loadBills()
	},
	methods: {
		// 加载企业账单列表
		async loadBills() {
			if (!this.userInfo || !this.userInfo.phone) {
				uni.showToast({
					title: '请先登录',
					icon: 'none'
				})
				setTimeout(() => {
					uni.navigateTo({
						url: '/pages/login/index'
					})
				}, 1500)
				return
			}

			this.loading = true
			try {
				const res = await get('/h5/app/enterpriseBill/myBills', {
					phone: this.userInfo.phone
				})
				this.billList = this.formatBills(res.data || [])
			} catch (err) {
				console.error('加载账单失败:', err)
				this.billList = []
			} finally {
				this.loading = false
			}
		},

		// 格式化账单数据
		formatBills(bills) {
			return bills.map(bill => {
				// 格式化日期
				if (bill.checkInDate) {
					const date = new Date(bill.checkInDate)
					bill.checkInDate = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
				}
				if (bill.checkOutDate) {
					const date = new Date(bill.checkOutDate)
					bill.checkOutDate = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
				}
				return bill
			})
		},

		// 支付账单
		async handlePay(bill) {
			uni.showModal({
				title: '确认支付',
				content: `确认支付账单 ¥${bill.finalAmount}？`,
				success: async (res) => {
					if (res.confirm) {
						uni.showLoading({
							title: '支付中...'
						})
						try {
							await post('/h5/app/enterpriseBill/pay', {
								billId: bill.billId,
								payMethod: '在线支付'
							})
							uni.hideLoading()
							uni.showToast({
								title: '支付成功',
								icon: 'success'
							})
							// 重新加载账单列表
							this.loadBills()
						} catch (err) {
							uni.hideLoading()
							uni.showToast({
								title: err.msg || '支付失败',
								icon: 'none'
							})
						}
					}
				}
			})
		},

		// 切换Tab
		switchTab(tab) {
			this.currentTab = tab
			this.showCheckinDetail = false
			this.currentCheckinItem = {}
			// 切换到在线缴费时重新加载
			if (tab === 'pay') {
				this.loadBills()
			}
		},

		// 入住办理
		handleCheckin(item) {
			this.currentCheckinItem = item
			this.showCheckinDetail = true
		},

		// 返回列表
		handleBack() {
			this.showCheckinDetail = false
			this.currentCheckinItem = {}
		},

		// 下载模版
		handleDownloadTemplate() {
			uni.showToast({
				title: '模版下载中...',
				icon: 'none'
			})
		},

		// 选择文件
		handleChooseFile() {
			// #ifdef MP-WEIXIN
			uni.chooseMessageFile({
				count: 1,
				type: 'file',
				success: (res) => {
					const file = res.tempFiles[0]
					const sizeInMb = (file.size / 1024 / 1024).toFixed(1) + 'Mb'
					this.uploadedFiles.push({
						name: file.name,
						size: sizeInMb
					})
				}
			})
			// #endif
			// #ifdef H5
			const input = document.createElement('input')
			input.type = 'file'
			input.accept = '.rar,.zip,.doc,.docx,.pdf,.jpg,.jpeg,.png'
			input.onchange = (e) => {
				const file = e.target.files[0]
				if (file) {
					const sizeInMb = (file.size / 1024 / 1024).toFixed(1) + 'Mb'
					this.uploadedFiles.push({
						name: file.name,
						size: sizeInMb
					})
				}
			}
			input.click()
			// #endif
			// #ifdef APP-PLUS
			uni.chooseImage({
				count: 1,
				success: (res) => {
					const path = res.tempFilePaths[0]
					const name = path.substring(path.lastIndexOf('/') + 1)
					this.uploadedFiles.push({
						name: name,
						size: '未知大小'
					})
				}
			})
			// #endif
		},

		// 删除文件
		handleDeleteFile(index) {
			this.uploadedFiles.splice(index, 1)
		},

		// 保存入住办理
		handleSaveCheckin() {
			if (this.uploadedFiles.length === 0) {
				uni.showToast({
					title: '请上传人员名单',
					icon: 'none'
				})
				return
			}
			uni.showToast({
				title: '保存成功',
				icon: 'success'
			})
			setTimeout(() => {
				this.handleBack()
			}, 1500)
		},

		// 退租办理
		handleCheckout(item) {
			if (item.status === 'done') {
				uni.showToast({
					title: '查看详情功能开发中',
					icon: 'none'
				})
			} else {
				uni.showToast({
					title: '退租办理功能开发中',
					icon: 'none'
				})
			}
		}
	}
}
</script>

<style scoped>
	.page {
		width: 100%;
		min-height: 95vh;
		background-color: #f5f6fc;
		display: flex;
		flex-direction: column;
	}

	/* 空状态 */
	.empty-state {
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		padding: 120rpx 0;
	}

	.empty-icon {
		width: 200rpx;
		height: 200rpx;
		margin-bottom: 24rpx;
	}

	.empty-text {
		color: #999999;
		font-size: 28rpx;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	/* 账单卡片 */
	.bill-card {
		width: 702rpx;
		border-radius: 20rpx;
		background: #ffffff;
		padding: 28rpx 28rpx 10rpx 28rpx;
		margin: 0 auto 24rpx;
		box-sizing: border-box;
	}

	.bill-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 24rpx;
		padding-bottom: 16rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.bill-title {
		color: #1a1a1a;
		font-size: 32rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		flex: 1;
	}

	.bill-status {
		padding: 8rpx 20rpx;
		border-radius: 20rpx;
		font-size: 24rpx;
	}

	.bill-status.status-0 {
		background: #e6f7ff;
		color: #1281ff;
	}

	.bill-status.status-1 {
		background: #fff7e6;
		color: #fa8c16;
	}

	.bill-status.status-2 {
		background: #e6f7ff;
		color: #52c41a;
	}

	.bill-row {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding-bottom: 20rpx;
	}

	.bill-row.price-row {
		padding-top: 10rpx;
		margin-bottom: 16rpx;
	}

	.bill-label {
		color: #888888;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.bill-value {
		color: #1a1a1a;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.bill-value.price {
		color: #f56c6c;
		font-size: 36rpx;
		font-weight: bold;
	}

	.bill-btn-wrap {
		display: flex;
		justify-content: flex-end;
		margin-top: 10rpx;
	}

	.bill-btn {
		width: 200rpx;
		height: 68rpx;
		border-radius: 34rpx;
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.bill-btn-text {
		color: #ffffff;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.bill-paid {
		text-align: right;
		padding: 10rpx 0;
	}

	.paid-text {
		color: #52c41a;
		font-size: 26rpx;
		font-family: "PingFang SC", "苹方-简", sans-serif;
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

	.scroll-content {
		flex: 1;
		padding: 24rpx;
		padding-bottom: 160rpx;
		box-sizing: border-box;
	}

	.card {
		width: 702rpx;
		border-radius: 20rpx;
		background: #ffffff;
		padding: 26rpx 0 10rpx 0;
		margin: 0 auto 24rpx;
		box-sizing: border-box;
	}

	.card-header {
		display: flex;
		align-items: center;
		margin-bottom: 37rpx;
	}

	.card-indicator {
		width: 12rpx;
		height: 34rpx;
		background: #0f73ff;
		margin-right: 26rpx;
	}

	.card-title {
		color: #171a1f;
		font-size: 32rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.form-row {
		display: flex;
		align-items: center;
		margin: 0 40rpx;
		margin-bottom: 28rpx;
	}

	.form-label {
		width: 166rpx;
		color: #333333;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		padding-bottom: 28rpx;
		flex-shrink: 0;
	}

	.form-value-wrap {
		flex: 1;
		padding-bottom: 28rpx;
		border-bottom: 1rpx solid #f0f0f0;
		display: flex;
		align-items: center;
	}

	.form-value-wrap.no-border {
		border-bottom: none;
	}

	.form-value {
		color: #1a1a1a;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.form-input {
		width: 100%;
		color: #1a1a1a;
		font-size: 26rpx;
		text-align: left;
	}

	.placeholder {
		color: #999999;
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
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	/* 底部按钮 */
	.bottom-btn-container {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		padding: 24rpx;
		background: #f5f6fc;
	}

	.bottom-btn {
		width: 702rpx;
		height: 92rpx;
		border-radius: 20rpx;
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
		display: flex;
		align-items: center;
		justify-content: center;
		margin: 0 auto;
	}

	.bottom-btn-text {
		color: #ffffff;
		font-size: 36rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	/* 房源卡片 */
	.house-card {
		width: 702rpx;
		border-radius: 20rpx;
		background: #ffffff;
		padding: 32rpx 28rpx 20rpx 28rpx;
		margin: 0 auto 24rpx;
		box-sizing: border-box;
	}

	.house-row {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding-bottom: 28rpx;
	}

	.house-row.with-border {
		border-bottom: 1rpx solid #f0f0f0;
		margin-bottom: 15rpx;
	}

	.house-label {
		color: #888888;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.house-value {
		color: #1a1a1a;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.house-btn-wrap {
		display: flex;
		justify-content: flex-end;
		margin-top: 16rpx;
	}

	.house-btn {
		width: 180rpx;
		height: 68rpx;
		border-radius: 12rpx;
		background: #1281ff;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.house-btn-text {
		color: #ffffff;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	/* 详情页样式 */
	.detail-row {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin: 0 40rpx;
		padding-bottom: 24rpx;
	}

	.detail-label {
		color: #333333;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.detail-value {
		color: #1a1a1a;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	/* 模版下载 */
	.template-download {
		display: flex;
		align-items: center;
		margin-left: 20rpx;
	}

	.template-icon {
		width: 30rpx;
		height: 30rpx;
		margin-right: 8rpx;
	}

	.template-text {
		color: #1281ff;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	/* 上传区域 */
	.upload-area {
		width: 622rpx;
		height: 96rpx;
		border-radius: 12rpx;
		border: 2rpx dashed #cdced5;
		background: #fafbff;
		display: flex;
		align-items: center;
		justify-content: center;
		margin: 0 40rpx 24rpx;
	}

	.upload-icon {
		width: 24rpx;
		height: 24rpx;
		margin-right: 12rpx;
	}

	.upload-text {
		color: #999999;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	/* 文件列表 */
	.file-list {
		margin: 0 40rpx;
	}

	.file-item {
		width: 622rpx;
		height: 58rpx;
		border-radius: 8rpx;
		background: rgba(83, 162, 252, 0.1);
		display: flex;
		align-items: center;
		padding: 0 20rpx;
		margin-bottom: 16rpx;
		box-sizing: border-box;
	}

	.file-name {
		color: #333333;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		flex: 1;
	}

	.file-size {
		color: #999999;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		margin-right: 20rpx;
	}

	.file-delete {
		color: #fa5740;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	/* 支持格式提示 */
	.support-tip {
		color: #999999;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		margin: 0 40rpx 20rpx;
	}

	/* 底部双按钮 */
	.bottom-btns-container {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		padding: 24rpx;
		background: #f5f6fc;
		display: flex;
		justify-content: space-between;
	}

	.btn-back {
		width: 336rpx;
		height: 92rpx;
		border-radius: 20rpx;
		border: 2rpx solid #0f73ff;
		background: #ffffff;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.btn-back-text {
		color: #0f73ff;
		font-size: 36rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.btn-save {
		width: 336rpx;
		height: 92rpx;
		border-radius: 20rpx;
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.btn-save-text {
		color: #ffffff;
		font-size: 36rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}
</style>

