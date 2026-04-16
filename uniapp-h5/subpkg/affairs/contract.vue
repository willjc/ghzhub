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
				<text class="tip-text">提示：合同签署后30分钟内未支付押金，则合同自动失效！</text>
			</view>
			
			<!-- 合同卡片列表 -->
			<view
				class="card"
				:class="{ 'card-highlight': item.contractId === highlightContractId, 'card-voided': item.status === 'voided' }"
				v-for="(item, index) in currentContractList"
				:key="item.type === 'order' ? ('order_' + item.orderId) : ('contract_' + item.contractId)"
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
				<view class="info-row" v-if="item.rentPeriod">
					<text class="info-label">租期</text>
					<text class="info-value">{{ item.rentPeriod }}</text>
				</view>
				
				<!-- 租金 -->
				<view class="info-row">
					<text class="info-label">租金</text>
					<text class="info-value">{{ item.rent }}</text>
				</view>
				
				<!-- 押金 -->
				<view class="info-row">
					<text class="info-label">押金</text>
					<text class="info-value">{{ item.deposit }}</text>
				</view>

				<!-- 合同详情按钮 -->
				<view class="info-row last-row" v-if="item.hasPdf">
					<text class="info-label">电子合同</text>
					<view class="btn-contract-detail" @click="openContractPdf(item)">
						<text class="btn-contract-detail-text">查看合同 ›</text>
					</view>
				</view>
				<view class="info-row last-row" v-else>
					<text class="info-label">电子合同</text>
					<text class="info-value" style="color:#bbb;">待生成</text>
				</view>

					<!-- 4步状态指示器 - 待签署和已签署状态显示 -->
					<view class="steps-bar" v-if="item.status === 'pending' || item.status === 'signed'">
						<!-- Step1: 去签署 -->
						<view class="step-item" :class="{ done: item.signed, 'step-current': item.status === 'pending' }">
							<view class="step-dot" :class="{ 'step-dot-current': item.status === 'pending' }">
								{{ item.signed ? '✓' : '1' }}
							</view>
							<text class="step-label">去签署</text>
							<text class="step-countdown" v-if="item.status === 'pending' && getStepCountdown(item, 'booking')"
								:class="{ 'step-countdown-urgent': isStepUrgent(item, 'booking') }">
								{{ getStepCountdown(item, 'booking') }}
							</text>
						</view>
						<view class="step-line" :class="{ active: item.signed }"></view>
						<!-- Step2: 合同已签 -->
						<view class="step-item" :class="{ done: item.signed }">
							<view class="step-dot">{{ item.signed ? '✓' : '2' }}</view>
							<text class="step-label">合同已签</text>
							<text class="step-date" v-if="item.signedDate && item.signed">{{ item.signedDate.slice(0,10) }}</text>
						</view>
						<view class="step-line" :class="{ active: item.depositPaid }"></view>
						<!-- Step3: 押金已缴 -->
						<view class="step-item" :class="{ done: item.depositPaid, 'step-current': item.signed && !item.depositPaid }">
							<view class="step-dot" :class="{ 'step-dot-current': item.signed && !item.depositPaid }">
								{{ item.depositPaid ? '✓' : '3' }}
							</view>
							<text class="step-label">押金已缴</text>
							<text class="step-countdown" v-if="item.signed && !item.depositPaid && getStepCountdown(item, 'deposit')"
								:class="{ 'step-countdown-urgent': isStepUrgent(item, 'deposit') }">
								{{ getStepCountdown(item, 'deposit') }}
							</text>
						</view>
						<view class="step-line" :class="{ active: item.materialSubmitted }"></view>
						<!-- Step4: 提交资料 -->
						<view class="step-item" :class="{ done: item.materialSubmitted, 'step-pending': item.materialSubmitted && !item.materialApproved }">
							<view class="step-dot" :class="{ 'step-dot-pending': item.materialSubmitted && !item.materialApproved }">
								{{ item.materialApproved ? '✓' : (item.materialSubmitted ? '⏳' : '4') }}
							</view>
							<text class="step-label">{{ item.materialApproved ? '资料已审' : (item.materialSubmitted ? '审核中' : '提交资料') }}</text>
							<text class="step-countdown" v-if="item.depositPaid && !item.materialSubmitted">3日内</text>
						</view>
					</view>

					<!-- 3日资料上传提示 -->
					<view class="material-alert" v-if="item.status !== 'voided' && item.depositPaid && !item.materialSubmitted">
						<text class="material-alert-text">⚠ 请在3日内上传工作证明和学历证明，逾期合同将失效，房源将被释放</text>
					</view>

					<!-- 操作按钮区域 -->
					<!-- 已失效 -->
					<view class="button-group" v-if="item.status === 'voided'">
						<view class="btn btn-disabled-state">
							<text class="btn-text-gray">已失效</text>
						</view>
					</view>
					<!-- 待签署 -->
					<view class="button-group" v-else-if="item.status === 'pending'">
						<view class="btn btn-sign" @click="goSign(item)">
							<text class="btn-text-white">去签署</text>
						</view>
					</view>
					<!-- 履行中 -->
					<view class="button-group" v-else-if="item.status === 'active'">
						<view class="btn btn-renew" @click="handleRenew(item)">
							<text class="btn-text-blue">续租</text>
						</view>
						<view class="btn btn-pay" @click="handlePay(item)">
							<text class="btn-text-white">租金缴纳</text>
						</view>
					</view>
					<!-- 已签署但未付押金 -->
					<view class="button-group" v-else-if="item.status === 'signed' && !item.depositPaid">
						<view class="btn btn-pay" @click="handlePayDeposit(item)">
							<text class="btn-text-white">支付押金</text>
						</view>
					</view>
					<!-- 押金已缴但未提交资料 -->
					<view class="button-group" v-else-if="item.status === 'signed' && item.depositPaid && !item.materialSubmitted">
						<view class="btn btn-pay" @click="handleSubmitMaterial(item)">
							<text class="btn-text-white">提交资料</text>
						</view>
					</view>
					<!-- 资料审核中 -->
					<view class="button-group" v-else-if="item.status === 'signed' && item.materialSubmitted && !item.materialApproved">
						<view class="btn btn-disabled-state">
							<text class="btn-text-gray">资料审核中</text>
						</view>
					</view>
					<!-- 审核通过但未缴首期房租 -->
					<view class="button-group" v-else-if="item.status === 'signed' && item.materialApproved && !item.firstRentPaid">
						<view class="btn btn-pay" @click="handlePay(item)">
							<text class="btn-text-white">缴纳房租</text>
						</view>
					</view>
					<!-- 全部完成：合约中 -->
					<view class="button-group" v-else-if="item.status === 'signed' && item.firstRentPaid">
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
	import { getMyContracts, getContractPdfUrl } from '@/api/contract.js'
	import config from '@/config/index'
	import authCheck from '@/mixins/authCheck'

	export default {
		data() {
			return {
				housingType: '',
				currentTab: 'current',
				allContractList: [],  // 从API获取的所有合同
				userId: null,  // 当前登录用户ID
				highlightContractId: null,  // 需要高亮显示的合同ID (从续租页面跳转时传入)
				countdownTimers: {},   // key=contractId, value=剩余秒数
				timerIntervals: {},    // key=contractId, value=setInterval ID

				statusClassMap: {
					'unsigned': 'status-unsigned',
					'pending': 'status-unsigned',
					'signed': 'status-signed',
					'active': 'status-active',
					'expired': 'status-expired',
					'voided': 'status-voided'
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
				return this.allContractList.some(item => item.status === 'pending' || (item.status === 'signed' && !item.depositPaid))
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
		// 从账单页/上传页返回时，重新拉合同数据以刷新押金状态
		onShow() {
			if (this.userId) {
				this.loadContractList()
			}
		},
		beforeDestroy() {
			this.clearAllTimers()
		},
		methods: {
			switchTab(tab) {
				this.currentTab = tab
			},

			// 查看已签合同PDF（实时刷新 e签宝 临时链接）
			async openContractPdf(item) {
				if (!item.contractId) {
					uni.showToast({ title: '合同尚未生成，请稍后再试', icon: 'none' })
					return
				}
				uni.showLoading({ title: '获取链接...' })
				let pdfUrl = ''
				try {
					const res = await getContractPdfUrl(item.contractId)
					uni.hideLoading()
					if (res.code === 200 && res.data) {
						pdfUrl = res.data
					} else {
						uni.showToast({ title: res.msg || '获取链接失败', icon: 'none' })
						return
					}
				} catch (e) {
					uni.hideLoading()
					uni.showToast({ title: '获取链接失败，请重试', icon: 'none' })
					return
				}

				// #ifdef H5
				// H5：直接跳转到 PDF 链接，微信浏览器会触发预览或下载
				window.location.href = pdfUrl
				// #endif

				// #ifdef MP-WEIXIN
				// 小程序：通过后端代理接口下载 PDF，避免 e签宝 CDN 域名不在白名单问题
				const token = uni.getStorageSync('token') || ''
				const proxyUrl = config.baseUrl + `/h5/app/contract/${item.contractId}/download-pdf`
				uni.showLoading({ title: '加载合同...' })
				uni.downloadFile({
					url: proxyUrl,
					header: {
						'Authorization': token ? `Bearer ${token}` : ''
					},
					success: (dlRes) => {
						uni.hideLoading()
						if (dlRes.statusCode === 200) {
							uni.openDocument({
								filePath: dlRes.tempFilePath,
								fileType: 'pdf',
								showMenu: true,
								fail: (err) => {
									console.error('openDocument 失败', err)
									uni.showToast({ title: '无法打开PDF，请稍后重试', icon: 'none' })
								}
							})
						} else {
							uni.showToast({ title: '下载失败(状态码:' + dlRes.statusCode + ')，请重试', icon: 'none' })
						}
					},
					fail: (err) => {
						uni.hideLoading()
						console.error('downloadFile 失败', err)
						uni.showToast({ title: '网络错误，请检查网络后重试', icon: 'none' })
					}
				})
				// #endif
			},

			// 去签署 (e签宝流程)
			goSign(item) {
				if (item.type === 'order') {
					// 预订单 → 跳转签约页（需要 roomId, projectId, houseCode, orderNo, lockExpireTime）
					const params = `roomId=${item.houseId}&projectId=${item.projectId}&houseCode=${encodeURIComponent(item.houseCode || '')}&orderNo=${item.orderNo}&lockExpireTime=${encodeURIComponent(item.bookingExpireTime || '')}`
					uni.navigateTo({
						url: `/pages/contract/sign?${params}`
					})
				} else {
					// 已有合同 → 跳转签约页（只需 contractId）
					uni.navigateTo({
						url: `/pages/contract/sign?contractId=${item.contractId || item.id}`
					})
				}
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

			// 支付押金 → 跳转账单页，只展示押金账单
			handlePayDeposit(item) {
				uni.navigateTo({
					url: `/subpkg/affairs/bill?contractId=${item.contractId}&billType=1&depositPaid=0`
				})
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
						// 启动倒计时定时器
						this.startCountdownTimers()
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
				// === 预订单类型（尚未生成合同）===
				if (item.type === 'order') {
					return {
						type:             'order',
						orderId:          item.order_id,
						orderNo:          item.order_no,
						contractId:       null,
						house_id:         item.house_id,
						houseId:          item.house_id,
						project_id:       item.project_id,
						projectId:        item.project_id,
						houseCode:        item.house_code || item.house_no || '',
						status:           'pending',
						statusText:       '待签署',
						community:        item.project_name || '未知小区',
						room:             `${item.building_name || ''}${item.unit_name || ''}${item.house_no || ''}`,
						rentPeriod:       '',
						rent:             item.rent_price ? `${item.rent_price}元/月` : '',
						deposit:          item.deposit ? `${item.deposit}元` : '',
						depositAmount:    item.deposit || '0',
						end_date:         new Date(Date.now() + 365 * 24 * 3600 * 1000).toISOString().slice(0, 10), // 保证在"当前合同"tab
						signedDate:       '',
						signed:           false,
						depositPaid:      false,
						materialStatus:   '0',
						materialSubmitted: false,
						materialApproved: false,
						firstRentPaid:    false,
						contractContent:  '',
						hasPdf:           false,
						lockExpireTime:   '',
						bookingExpireTime: item.booking_expire_time || '',
					}
				}

				// === 正常合同类型 ===
				const status = this.getContractStatus(item)

				const signed           = ['2','3','4'].includes(item.contract_status)
				const depositPaid      = item.deposit_paid === '1'
				const materialStatus   = item.material_status || '0' // 0未提交 1审核中 2已通过
				const materialSubmitted = materialStatus !== '0'
				const materialApproved  = materialStatus === '2'
				const firstRentPaid    = item.first_rent_paid === '1'

				return {
					type:             'contract',
					contractId:       item.contract_id,
					house_id:         item.house_id,
					project_id:       item.project_id,
					status:           status.code,
					statusText:       status.text,
					community:        item.project_name || '未知小区',
					room:             `${item.building_name || ''}${item.unit_name || ''}${item.house_no || ''}`,
					rentPeriod:       this.formatRentPeriod(item.start_date, item.end_date),
					rent:             `${item.rent_price}元/月`,
					deposit:          `${item.deposit}元`,
					depositAmount:    item.deposit || '0',
					end_date:         item.end_date,
					signedDate:       item.sign_time || item.signed_date || '',
					signed,
					depositPaid,
					materialStatus,
					materialSubmitted,
					materialApproved,
					firstRentPaid,
					contractContent:  item.contract_content || '',
					hasPdf: item.contract_content && item.contract_content.startsWith('http'),
					lockExpireTime:   item.lock_expire_time || '',  // 押金支付锁定过期时间
					bookingExpireTime: item.booking_expire_time || '',  // 预订锁定过期时间
				}
			},

			// 根据contract_status映射状态
			getContractStatus(item) {
				switch(item.contract_status) {
					case '0': return { code: 'pending', text: '待签署' }      // 草稿(待签署)
					case '1': return { code: 'pending', text: '待签署' }      // 待签署
					case '2': return { code: 'signed', text: '已签署' }       // 已签署(办理中)
					case '3': return { code: 'active', text: '履行中' }       // 履行中
					case '4': return { code: 'expired', text: '已到期' }      // 已到期
					case '5': return { code: 'expired', text: '已解约' }      // 已解约
					case '6': return { code: 'voided', text: '已失效' }       // 超时失效
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
			},

			// ========== 倒计时相关方法 ==========

			// 启动所有需要倒计时的合同定时器
			startCountdownTimers() {
				this.clearAllTimers()
				this.allContractList.forEach(item => {
					const itemKey = item.contractId || `order_${item.orderId}`
					// 1. 预订倒计时：待签署(pending)且有bookingExpireTime
					if (item.status === 'pending' && item.bookingExpireTime) {
						this._startTimer(item, item.bookingExpireTime, 'booking', itemKey)
					}
					// 2. 押金支付倒计时：已签署未付押金且有lockExpireTime
					else if (item.signed && !item.depositPaid && item.lockExpireTime) {
						this._startTimer(item, item.lockExpireTime, 'deposit', itemKey)
					}
				})
			},

			// 通用定时器启动（type: 'booking' | 'deposit'）
			_startTimer(item, expireTime, type, itemKey) {
				const timerKey = `${type}_${itemKey || item.contractId}`
				const remaining = Math.max(0, Math.floor((new Date(expireTime) - new Date()) / 1000))
				this.$set(this.countdownTimers, timerKey, remaining)
				if (remaining > 0) {
					const intervalId = setInterval(() => {
						const cur = this.countdownTimers[timerKey]
						if (cur <= 1) {
							this.$set(this.countdownTimers, timerKey, 0)
							clearInterval(this.timerIntervals[timerKey])
							delete this.timerIntervals[timerKey]
							// 倒计时结束：立即在前端乐观更新为"已失效"状态
							this.markItemVoidedLocally(item)
							// 立即刷新一次，获取后端最新状态
							this.loadContractList()
							// 延迟3秒后再刷新一次，确保后端定时任务已执行
							setTimeout(() => {
								if (this.userId) {
									this.loadContractList()
								}
							}, 3000)
						} else {
							this.$set(this.countdownTimers, timerKey, cur - 1)
						}
					}, 1000)
					this.$set(this.timerIntervals, timerKey, intervalId)
				} else {
					// 页面加载时倒计时已经归零，立即标记为失效
					this.markItemVoidedLocally(item)
				}
			},

			// 前端乐观更新：将指定合同/预订单本地标记为"已失效"
			markItemVoidedLocally(item) {
				const idx = this.allContractList.findIndex(c => {
					if (item.type === 'order') {
						return c.type === 'order' && c.orderId === item.orderId
					}
					return c.contractId === item.contractId
				})
				if (idx !== -1) {
					const updated = { ...this.allContractList[idx], status: 'voided', statusText: '已失效' }
					this.$set(this.allContractList, idx, updated)
				}
			},

			// 清除所有定时器
			clearAllTimers() {
				Object.values(this.timerIntervals).forEach(id => clearInterval(id))
				this.timerIntervals = {}
			},

			// 获取步骤内倒计时文本（mm:ss格式）
			getStepCountdown(item, type) {
				const itemKey = item.contractId || `order_${item.orderId}`
				const timerKey = `${type}_${itemKey}`
				const seconds = this.countdownTimers[timerKey]
				if (seconds === undefined || seconds === null || seconds <= 0) return ''
				const min = String(Math.floor(seconds / 60)).padStart(2, '0')
				const sec = String(seconds % 60).padStart(2, '0')
				return `${min}:${sec}`
			},

			// 步骤内倒计时是否紧急（预订<3分钟，押金<5分钟）
			isStepUrgent(item, type) {
				const itemKey = item.contractId || `order_${item.orderId}`
				const timerKey = `${type}_${itemKey}`
				const seconds = this.countdownTimers[timerKey]
				if (seconds === undefined || seconds <= 0) return false
				const threshold = type === 'booking' ? 180 : 300
				return seconds < threshold
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

	.status-active {
		color: #1976f8;
	}

	.status-expired {
		color: #768394;
	}

	.status-voided {
		color: #ff4400;
	}

	/* 已失效合同卡片 - 灰色遮罩 */
	.card-voided {
		opacity: 0.6;
		background: #f9f9f9;
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

	/* 合同详情按钮 */
	.btn-contract-detail {
		padding: 6rpx 20rpx;
		border: 1rpx solid #1281ff;
		border-radius: 30rpx;
	}
	.btn-contract-detail-text {
		color: #1281ff;
		font-size: 26rpx;
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

	/* 签约日期 */
	.step-date {
		font-size: 18rpx;
		color: #aaaaaa;
		margin-top: 4rpx;
		display: block;
		text-align: center;
	}

	/* 当前活跃步骤 - 蓝色高亮 */
	.step-current .step-dot,
	.step-dot-current {
		background: #1976f8 !important;
		color: #fff !important;
		animation: stepPulse 1.5s ease-in-out infinite;
	}
	.step-current .step-label {
		color: #1976f8;
		font-weight: 600;
	}

	/* 步骤内倒计时文本 */
	.step-countdown {
		font-size: 20rpx;
		color: #ff6600;
		font-family: "PingFang SC", sans-serif;
		margin-top: 4rpx;
		font-weight: 600;
		text-align: center;
	}
	.step-countdown-urgent {
		color: #ff0000 !important;
		animation: countdownBlink 1s ease-in-out infinite;
	}

	@keyframes stepPulse {
		0%, 100% { transform: scale(1); }
		50% { transform: scale(1.1); }
	}
	@keyframes countdownBlink {
		0%, 100% { opacity: 1; }
		50% { opacity: 0.5; }
	}

	/* 审核中状态 - 黄色 */
	.step-dot-pending {
		background: #faad14 !important;
		border-color: #faad14 !important;
		color: #fff !important;
		font-size: 20rpx !important;
	}
	.step-pending .step-label {
		color: #faad14;
	}

	/* 审核中按钮 */
	.btn-disabled-state {
		background: #f5f5f5;
		border: 1rpx solid #d9d9d9;
	}
	.btn-text-gray {
		color: #999999;
		font-size: 28rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	/* 资料上传提示 */
	.material-alert {
		margin-top: 16rpx;
		padding: 14rpx 20rpx;
		background: rgba(255, 102, 0, 0.08);
		border-radius: 10rpx;
	}
	.material-alert-text {
		color: #ff6600;
		font-size: 24rpx;
		font-family: "PingFang SC", sans-serif;
		line-height: 36rpx;
	}
</style>

