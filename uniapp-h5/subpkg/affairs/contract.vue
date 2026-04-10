<template>
	<view class="page">
		<!-- Tab 切换 -->
		<view class="tab-bar">
			<view class="tab-item" :class="{ active: currentTab === 'current' }" @click="switchTab('current')">
				<text class="tab-text" :class="{ active: currentTab === 'current' }">当前合同</text>
				<view class="tab-line" v-if="currentTab === 'current'"></view>
			</view>
			<view class="tab-item" :class="{ active: currentTab === 'history' }" @click="switchTab('history')">
				<text class="tab-text" :class="{ active: currentTab === 'history' }">历史合同</text>
				<view class="tab-line" v-if="currentTab === 'history'"></view>
			</view>
		</view>
		
		<!-- 合同列表 -->
		<scroll-view class="scroll-content" scroll-y>
			<!-- 提示卡片 -->
			<view class="tip-card" v-if="currentTab === 'current' && hasUnsignedContract">
				<text class="tip-text">提示：未签约合同，半小时内未签署，择合同自动取消！</text>
			</view>
			
			<!-- 合同卡片列表 -->
			<view
				class="card"
				:class="{ 'card-highlight': item.contractId === highlightContractId }"
				v-for="(item, index) in currentContractList"
				:key="index"
			>
				<!-- 合同状态 -->
				<view class="info-row">
					<text class="info-label">合同状态</text>
					<text class="info-value" :class="statusClassMap[item.status] || ''">{{ item.statusText }}</text>
				</view>
				
				<!-- 小区 -->
				<view class="info-row">
					<text class="info-label">小区</text>
					<text class="info-value">{{ item.community }}</text>
				</view>
				
				<!-- 房间 -->
				<view class="info-row">
					<text class="info-label">房间</text>
					<text class="info-value">{{ item.room }}</text>
				</view>
				
				<!-- 租期 -->
				<view class="info-row">
					<text class="info-label">租期</text>
					<text class="info-value">{{ item.rentPeriod }}</text>
				</view>
				
				<!-- 租金 -->
				<view class="info-row">
					<text class="info-label">租金</text>
					<text class="info-value">{{ item.rent }}</text>
				</view>
				
				<!-- 押金 -->
				<view class="info-row last-row">
					<text class="info-label">押金</text>
					<text class="info-value">{{ item.deposit }}</text>
				</view>

					<!-- 三步状态指示器 - 仅合约中显示 -->
					<view class="steps-bar" v-if="item.status === 'signed'">
						<view class="step-item done">
							<view class="step-dot">✓</view>
							<text class="step-label">合同已签</text>
						</view>
						<view class="step-line" :class="{ active: item.depositPaid }"></view>
						<view class="step-item" :class="{ done: item.depositPaid }">
							<view class="step-dot">{{ item.depositPaid ? '✓' : '2' }}</view>
							<text class="step-label">押金已付</text>
						</view>
						<view class="step-line" :class="{ active: item.materialSubmitted }"></view>
						<view class="step-item" :class="{ done: item.materialSubmitted }">
							<view class="step-dot">{{ item.materialSubmitted ? '✓' : '3' }}</view>
							<text class="step-label">资料已提交</text>
						</view>
					</view>

					<!-- 操作按钮区域 -->
					<!-- 待签署 -->
					<view class="button-group" v-if="item.status === 'pending'">
						<view class="btn btn-sign" @click="goSign(item)">
							<text class="btn-text-white">去签署</text>
						</view>
					</view>
					<!-- 已签署但未付押金 -->
					<view class="button-group" v-else-if="item.status === 'signed' && !item.depositPaid">
						<view class="btn btn-pay" @click="handlePayDeposit(item)">
							<text class="btn-text-white">支付押金 ¥{{ item.depositAmount }}</text>
						</view>
					</view>
					<!-- 已付押金但未提交资料 -->
					<view class="button-group" v-else-if="item.status === 'signed' && item.depositPaid && !item.materialSubmitted">
						<view class="btn btn-pay" @click="handleSubmitMaterial(item)">
							<text class="btn-text-white">提交资料</text>
						</view>
					</view>
					<!-- 全部完成 -->
					<view class="button-group" v-else-if="item.status === 'signed' && item.materialSubmitted">
						<view class="btn btn-renew" @click="handleRenew(item)">
							<text class="btn-text-blue">续租</text>
						</view>
						<view class="btn btn-pay" @click="handlePay(item)">
							<text class="btn-text-white">租金缴纳</text>
						</view>
					</view>
			</view>
			
			<!-- 空状态 -->
			<view class="empty-state" v-if="currentContractList.length === 0">
				<text class="empty-text">暂无合同</text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import { getMyContracts, getDepositBill, payDeposit } from '@/api/contract.js'
	import authCheck from '@/mixins/authCheck'

	export default {
		data() {
			return {
				housingType: '',
				currentTab: 'current',
				allContractList: [],  // 从API获取的所有合同
				userId: null,  // 当前登录用户ID
				highlightContractId: null,  // 需要高亮显示的合同ID (从续租页面跳转时传入)

				statusClassMap: {
					'unsigned': 'status-unsigned',
					'signed': 'status-signed',
					'expired': 'status-expired'
				}
			}
		},
		computed: {
			currentContractList() {
				const today = new Date()
				today.setHours(0, 0, 0, 0)

				// 根据日期筛选
				return this.allContractList.filter(contract => {
					const endDate = new Date(contract.end_date)

					if (this.currentTab === 'current') {
						// 当前合同：结束日期 >= 今天
						return endDate >= today
					} else {
						// 历史合同：结束日期 < 今天
						return endDate < today
					}
				})
			},
			hasUnsignedContract() {
				return this.allContractList.some(item => item.status === 'pending')
			}
		},
		onLoad(options) {
			// 使用统一的登录检查
			authCheck.checkLogin.call(this, options, (options) => {
				if (options.type) {
					this.housingType = options.type
				}

				// 如果传入了contractId参数,保存用于高亮显示
				if (options.contractId) {
					this.highlightContractId = parseInt(options.contractId)
					console.log('需要高亮的合同ID:', this.highlightContractId)
				}

				console.log('当前用户ID:', this.userId)
				this.loadContractList()
			})
		},
		methods: {
			switchTab(tab) {
				this.currentTab = tab
			},

			// 去签署 (e签宝流程)
			goSign(item) {
				uni.navigateTo({
					url: `/pages/contract/sign?contractId=${item.contractId || item.id}`
				})
			},

			// 签约 (旧流程，保留兼容)
			handleSign(index) {
				uni.navigateTo({
					url: `/pages/contract/sign?type=${this.housingType}&id=${index}`
				})
			},

			// 续租
			handleRenew(item) {
				// 跳转到合同签订页面,传递续租标识和合同信息
				uni.navigateTo({
					url: `/pages/contract/sign?isRenew=true&oldContractId=${item.contractId}&roomId=${item.house_id || ''}&projectId=${item.project_id || ''}`
				})
			},

			// 租金缴纳
			handlePay(item) {
				uni.navigateTo({
					url: `/subpkg/affairs/bill?type=${this.housingType}`
				})
			},

			// 支付押金
			async handlePayDeposit(item) {
				try {
					uni.showLoading({ title: '加载中...' })
					const billRes = await getDepositBill(item.contractId)
					uni.hideLoading()
					if (billRes.code === 200 && billRes.data) {
						const bill = billRes.data
						if (bill.billStatus === '1') {
							uni.showToast({ title: '押金已支付', icon: 'success' })
							this.loadContractList()
							return
						}
						uni.showLoading({ title: '发起支付...' })
						const payRes = await payDeposit({
							billId: bill.billId,
							payAmount: bill.unpaidAmount || bill.billAmount
						})
						uni.hideLoading()
						if (payRes.code === 200) {
							if (payRes.data && (payRes.data.mwebUrl || payRes.data.h5Url)) {
								// #ifdef H5
								window.location.href = payRes.data.mwebUrl || payRes.data.h5Url
								// #endif
							} else {
								uni.showToast({ title: '支付发起成功', icon: 'success' })
								setTimeout(() => this.loadContractList(), 2000)
							}
						} else {
							uni.showToast({ title: payRes.msg || '支付失败', icon: 'none' })
						}
					} else {
						uni.showToast({ title: billRes.msg || '未找到押金账单', icon: 'none' })
					}
				} catch (e) {
					uni.hideLoading()
					uni.showToast({ title: e.message || '操作失败', icon: 'none' })
				}
			},

			// 提交资料
			handleSubmitMaterial(item) {
				uni.navigateTo({
					url: `/pages/upload/index?contractId=${item.contractId}`
				})
			},
			
			// 加载合同列表
			async loadContractList() {
				if (!this.userId) {
					console.error('用户ID不存在')
					return
				}

				try {
					uni.showLoading({ title: '加载中...' })

					console.log('开始加载合同，用户ID:', this.userId)

					// 传入 userId 参数
					const res = await getMyContracts(this.userId)

					uni.hideLoading()

					if (res.code === 200) {
						// 映射数据格式
						this.allContractList = res.data.map(item => this.mapContractData(item))
						console.log('获取到合同数据:', this.allContractList.length, '条')
					} else {
						uni.showToast({ title: res.msg || '加载失败', icon: 'none' })
					}
				} catch (e) {
					uni.hideLoading()
					console.error('加载合同失败', e)
					uni.showToast({ title: '加载失败，请重试', icon: 'none' })
				}
			},

			// 数据映射：数据库字段 → 前端显示字段
			mapContractData(item) {
				const status = this.getContractStatus(item)

				return {
					contractId: item.contract_id,
					house_id: item.house_id,        // 保留原始house_id,用于续租
					project_id: item.project_id,    // 保留原始project_id,用于续租
					status: status.code,
					statusText: status.text,
					community: item.project_name || '未知小区',
					room: `${item.building_name || ''}${item.unit_name || ''}${item.house_no || ''}`,
					rentPeriod: this.formatRentPeriod(item.start_date, item.end_date),
					rent: `${item.rent_price}元/月`,
					deposit: `${item.deposit}元`,
					countdown: '',  // 待签约状态的倒计时暂时留空
					end_date: item.end_date,  // 保留原始日期，用于筛选当前/历史合同
						signed: ['2', '3', '4'].includes(item.contract_status),
						depositPaid: item.deposit_paid === '1',
						depositAmount: item.deposit || '0',
						materialSubmitted: item.material_status === '1',
				}
			},

			// 根据contract_status映射状态
			getContractStatus(item) {
				switch(item.contract_status) {
					case '0': return { code: 'pending', text: '待审核' }      // 草稿(待审核)
					case '1': return { code: 'pending', text: '待审核' }      // 待签署
					case '2': return { code: 'signed', text: '合约中' }       // 已签署
					case '3': return { code: 'signed', text: '合约中' }       // 履行中
					case '4': return { code: 'expired', text: '已到期' }      // 已到期
					case '5': return { code: 'expired', text: '已解约' }      // 已解约
					default: return { code: 'expired', text: '未知' }
				}
			},

			// 格式化租期显示
			formatRentPeriod(start, end) {
				if (!start || !end) return ''

				try {
					const startDate = new Date(start)
					const endDate = new Date(end)

					const formatDate = (date) => {
						const year = date.getFullYear()
						const month = String(date.getMonth() + 1).padStart(2, '0')
						const day = String(date.getDate()).padStart(2, '0')
						return `${year}年${month}月${day}日`
					}

					return `${formatDate(startDate)} 至${formatDate(endDate)}`
				} catch (e) {
					console.error('日期格式化失败', e)
					return ''
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
		box-sizing: border-box;
	}
	
	/* 提示卡片 */
	.tip-card {
		width: 702rpx;
		height: 80rpx;
		border-radius: 20rpx;
		background: rgba(255, 68, 0, 0.1);
		display: flex;
		align-items: center;
		justify-content: center;
		margin: 0 auto 24rpx;
	}
	
	.tip-text {
		color: #ff4400;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.card {
		width: 702rpx;
		border-radius: 20rpx;
		background: #ffffff;
		padding: 32rpx 28rpx 22rpx 28rpx;
		margin: 0 auto 24rpx;
		box-sizing: border-box;
		position: relative;
		transition: all 0.3s ease;
	}

	/* 高亮卡片样式 - 从续租页面跳转时高亮显示 */
	.card-highlight {
		border: 2rpx solid #1281ff;
		box-shadow: 0 4rpx 12rpx rgba(18, 129, 255, 0.15);
	}

	.info-row {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 28rpx;
	}

	.last-row {
		margin-bottom: 0;
		padding-bottom: 28rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.info-label {
		height: 37rpx;
		color: #888888;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 37rpx;
	}

	.info-value {
		height: 37rpx;
		color: #1a1a1a;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 37rpx;
		text-align: right;
	}

	/* 状态颜色 */
	.status-unsigned {
		color: #1a1a1a;
	}

	.status-signed {
		color: #12a566;
	}

	.status-expired {
		color: #768394;
	}

	/* 待审核状态行 */
	.pending-row {
		display: flex;
		align-items: center;
		justify-content: center;
		margin-top: 20rpx;
		padding: 20rpx;
		background: rgba(25, 118, 248, 0.05);
		border-radius: 8rpx;
	}

	.pending-text {
		color: #1976f8;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	/* 剩余时间和签约按钮行 */
	.countdown-button-row {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin-top: 20rpx;
	}
	
	.countdown-info {
		display: flex;
		align-items: center;
	}
	
	.countdown-label {
		height: 37rpx;
		color: #fa5740;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 37rpx;
	}
	
	.countdown-time {
		height: 37rpx;
		color: #fa5740;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 37rpx;
	}

	/* 按钮区域 */
	.button-group {
		display: flex;
		justify-content: flex-end;
		gap: 16rpx;
		margin-top: 15rpx;
	}

	.btn {
		width: 180rpx;
		height: 68rpx;
		border-radius: 12rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.btn-sign {
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
	}

	.btn-renew {
		border: 1rpx solid #1281ff;
		background: #ffffff;
	}

	.btn-pay {
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
	}

	.btn-text-blue {
		color: #1281ff;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.btn-text-white {
		color: #ffffff;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
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

	/* 三步状态指示器 */
	.steps-bar {
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 24rpx 0 16rpx;
		margin-top: 20rpx;
		border-top: 1rpx solid #f0f0f0;
	}

	.step-item {
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 8rpx;
	}

	.step-dot {
		width: 48rpx;
		height: 48rpx;
		border-radius: 50%;
		background: #e0e0e0;
		color: #999;
		font-size: 24rpx;
		font-weight: 600;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.step-item.done .step-dot {
		background: #12a566;
		color: #fff;
	}

	.step-label {
		font-size: 22rpx;
		color: #999;
		font-family: "PingFang SC", sans-serif;
	}

	.step-item.done .step-label {
		color: #12a566;
	}

	.step-line {
		flex: 1;
		height: 4rpx;
		background: #e0e0e0;
		margin: 0 16rpx;
		margin-bottom: 28rpx;
	}

	.step-line.active {
		background: #12a566;
	}
</style>

