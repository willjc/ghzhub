<template>
	<view class="page">
		<!-- 入住前置检查警告 -->
		<view v-if="!canCheckin" style="background:#fff7e6;border:1rpx solid #ffd591;border-radius:12rpx;padding:20rpx 24rpx;margin:20rpx 24rpx;display:flex;align-items:center;justify-content:space-between;">
			<text style="font-size:24rpx;color:#d46b08;flex:1;line-height:36rpx;">{{ checkinBlockMsg }}</text>
			<view v-if="!depositPaid" @click="goToBill" style="background:#fa8c16;border-radius:8rpx;padding:10rpx 20rpx;margin-left:16rpx;flex-shrink:0;">
				<text style="font-size:24rpx;color:#fff;">去缴押金</text>
			</view>
			<view v-else-if="!firstRentPaid" @click="goToBill" style="background:#fa8c16;border-radius:8rpx;padding:10rpx 20rpx;margin-left:16rpx;flex-shrink:0;">
				<text style="font-size:24rpx;color:#fff;">去缴房租</text>
			</view>
		</view>

		<scroll-view class="scroll-content" scroll-y>
			<!-- 入住申请卡片列表 -->
			<view class="card" v-for="(item, index) in checkinList" :key="index">
				<!-- 右上角已取消标签 -->
				<text class="cancel-tag" v-if="item.status === 'cancelled'">已取消</text>
				
				<!-- 申请状态 -->
				<view class="info-row">
					<text class="info-label">申请状态</text>
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
				
				<!-- 倒计时条 - 待办理 (status=0) 且开启自动解约 -->
				<view
					class="countdown-bar"
					v-if="item.statusCode === '0' && item.contractId && countdownMap[item.contractId] && countdownMap[item.contractId].showCountdown"
					:class="getCountdownLevelClass(countdownMap[item.contractId].remainingSeconds, countdownMap[item.contractId].totalSeconds)"
				>
					<view class="countdown-header">
						<text class="countdown-title">⏰ 入住办理倒计时</text>
						<text class="countdown-time">{{ formatRemain(countdownMap[item.contractId].remainingSeconds) }}</text>
					</view>
					<view class="countdown-progress">
						<view class="countdown-progress-inner" :style="{ width: getProgressWidth(countdownMap[item.contractId].remainingSeconds, countdownMap[item.contractId].totalSeconds) }"></view>
					</view>
					<text class="countdown-tip">超时未办理将自动解约并原路退还押金及首期租金</text>
				</view>

				<!-- 按钮区域 - 待办理 (status=0) -->
				<view class="button-group" v-if="item.statusCode === '0'">
					<view
						class="btn"
						:class="canCheckin ? 'btn-checkin' : 'btn-disabled'"
						@click="canCheckin ? handleCheckin(index) : showBlockReason()"
					>
						<text class="btn-text-white">{{ canCheckin ? '办理入住' : '条件未满足' }}</text>
					</view>
				</view>

				<!-- 按钮区域 - 审批中 (status=1) -->
				<view class="button-group" v-if="item.statusCode === '1'">
					<view class="btn btn-cancel" @click="handleCancel(index)">
						<text class="btn-text-blue">取消申请</text>
					</view>
					<view class="btn btn-edit" @click="handleEdit(index)">
						<text class="btn-text-white">修改信息</text>
					</view>
				</view>

				<!-- 按钮区域 - 审核通过 (status=2) -->
				<view class="button-group" v-if="item.statusCode === '2'">
					<view class="btn btn-confirm" @click="handleConfirm(index)">
						<text class="btn-text-white">入住确认</text>
					</view>
				</view>

				<!-- 按钮区域 - 已拒绝/已取消 (status=3) -->
				<view class="button-group" v-if="item.statusCode === '3'">
					<view class="btn btn-detail" @click="handleDetail(index)">
						<text class="btn-text-blue">查看详情</text>
					</view>
				</view>

				<!-- 按钮区域 - 已入住确认 (status=4) -->
				<view class="button-group" v-if="item.statusCode === '4'">
					<view class="btn btn-handle" @click="handleCheckinDetail(index)">
						<text class="btn-text-white">入驻详情</text>
					</view>
				</view>
			</view>
			
			<!-- 空状态 -->
			<view class="empty-state" v-if="checkinList.length === 0">
				<text class="empty-text">暂无入住申请</text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import { getCheckInList, cancelCheckIn, getCheckinCountdown } from '@/api/checkin.js'
	import { checkinCheck } from '@/api/order'
	import authCheck from '@/mixins/authCheck'

	export default {
		data() {
			return {
				housingType: '',
				tenantId: null, // 从登录信息获取租户ID
				loading: false,
				checkinList: [],
				canCheckin: true,
				depositPaid: false,
				materialApproved: false,
				firstRentPaid: false,
				checkinBlockMsg: '',
				checkinRemainSeconds: 0,
				_checkinTimer: null,
				countdownMap: {}, // { contractId: { showCountdown, remainingSeconds, totalSeconds, deadline, timeoutHours } }
				_countdownTimer: null,

				statusClassMap: {
					'pending': 'status-pending',
					'approved': 'status-approved',
					'rejected': 'status-rejected',
					'cancelled': 'status-cancelled',
					'confirm': 'status-confirm',
					'confirmed': 'status-confirmed'
				}
			}
		},
		onLoad(options) {
			// 使用统一的登录检查
			authCheck.checkLogin.call(this, options, (options) => {
				if (options.type) {
					this.housingType = options.type
				}
				this.tenantId = this.userId
				this.loadCheckinList()
				this.checkCheckinCondition()
			})
		},
		onUnload() {
			if (this._checkinTimer) clearInterval(this._checkinTimer)
			if (this._countdownTimer) clearInterval(this._countdownTimer)
		},
		onShow() {
			// 每次页面显示时重新加载列表数据，确保显示最新状态
			if (this.tenantId) {
				this.loadCheckinList()
			}
		},
		methods: {
			// 加载入住申请列表
			async loadCheckinList() {
				try {
					this.loading = true
					console.log('加载入住申请，租户ID:', this.tenantId)

					const response = await getCheckInList(this.tenantId)

					if (response.code === 200 && response.data) {
						// 转换后端数据格式为前端需要的格式
						this.checkinList = response.data.map(item => {
							return this.convertCheckInData(item)
						})
						// 加载倒计时数据（仅 status=0 待办理项）
						this.loadCountdowns()
					} else {
						this.checkinList = []
					}
				} catch (error) {
					console.error('获取入住列表失败:', error)
					this.checkinList = []
				} finally {
					this.loading = false
				}
			},

			// 转换后端数据为前端显示格式
			convertCheckInData(item) {
				// 解析备注中的房源信息
				const remark = item.remark || ''
				const community = this.extractInfo(remark, '项目：')
				const room = this.extractInfo(remark, '房间：')
				const rentPeriod = this.extractInfo(remark, '租期：')
				const rent = this.extractInfo(remark, '月租金：')
				const deposit = this.extractInfo(remark, '押金：')

				// 状态映射
				const statusMap = {
					'0': 'pending',      // 待办理
					'1': 'approved',     // 审批中（用户已提交，待管理员审核）
					'2': 'confirm',      // 审核通过（待用户确认）
					'3': 'rejected',     // 已拒绝
					'4': 'confirmed'     // 已入住确认
				}

				const statusTextMap = {
					'0': '待办理',
					'1': '审批中',
					'2': '审核通过',
					'3': '已拒绝',
					'4': '已入住确认'
				}

				return {
					recordId: item.recordId,
					contractId: item.contractId,
					checkinNo: item.checkinNo,
					status: statusMap[item.status] || 'pending',
					statusCode: item.status,
					statusText: statusTextMap[item.status] || '未知',
					community: community || '未知小区',
					room: room || '未知房间',
					rentPeriod: rentPeriod || '-',
					rent: rent || '-',
					deposit: deposit || '-',
					checkinDate: item.checkinDate,
					actualCheckinDate: item.actualCheckinDate
				}
			},

			// 从备注中提取信息
			extractInfo(remark, key) {
				const index = remark.indexOf(key)
				if (index !== -1) {
					const start = index + key.length
					let end = remark.indexOf('|', start)
					if (end === -1) end = remark.length
					return remark.substring(start, end).trim()
				}
				return ''
			},

			// 取消申请
			async handleCancel(index) {
				const item = this.checkinList[index]

				uni.showModal({
					title: '提示',
					content: '确定要取消此申请吗？取消后您可以重新填写入住信息。',
					success: async (res) => {
						if (res.confirm) {
							try {
								uni.showLoading({ title: '取消中...' })

								const response = await cancelCheckIn(item.recordId)

								uni.hideLoading()

								if (response.code === 200) {
									uni.showToast({
										title: '已取消申请',
										icon: 'success'
									})

									// 重新加载列表
									setTimeout(() => {
										this.loadCheckinList()
									}, 1500)
								} else {
									uni.showToast({
										title: response.msg || '取消失败',
										icon: 'none'
									})
								}
							} catch (error) {
								uni.hideLoading()
								console.error('取消申请失败:', error)
								uni.showToast({
									title: '取消失败，请重试',
									icon: 'none'
								})
							}
						}
					}
				})
			},

			// 修改信息 - 直接跳转到办理页面进行编辑
			handleEdit(index) {
				const item = this.checkinList[index]
				uni.redirectTo({
					url: `/subpkg/affairs/checkin-process?type=${this.housingType}&id=${item.recordId}&mode=edit`
				})
			},

			// 办理入住 (status=0 时显示)
			handleCheckin(index) {
				const item = this.checkinList[index]
				uni.redirectTo({
					url: `/subpkg/affairs/checkin-process?type=${this.housingType}&id=${item.recordId}`
				})
			},

			// 查看详情
			handleDetail(index) {
				const item = this.checkinList[index]
				uni.redirectTo({
					url: `/subpkg/affairs/checkin-detail?type=${this.housingType}&id=${item.recordId}`
				})
			},

			// 入住确认 (status=1 时显示，用于管理员审核后用户确认)
			handleConfirm(index) {
				const item = this.checkinList[index]
				uni.redirectTo({
					url: `/subpkg/affairs/checkin-confirm?type=${this.housingType}&id=${item.recordId}`
				})
			},

			// 入驻详情 (status=4 时显示，跳转到入驻详情页面)
			handleCheckinDetail(index) {
				const item = this.checkinList[index]
				uni.redirectTo({
					url: `/subpkg/affairs/checkin-detail?type=${this.housingType}&id=${item.recordId}`
				})
			},

			// 入住前置检查（三重校验：押金+资料审核+首期房租）
			async checkCheckinCondition() {
				try {
					const res = await checkinCheck(this.userId)
					if (res.code === 200 && res.data) {
						const d = res.data
						this.depositPaid      = d.depositPaid      || false
						this.materialApproved = d.materialApproved || false
						this.firstRentPaid    = d.firstRentPaid    || false
						this.canCheckin       = d.canCheckin       || false
						this.checkinBlockMsg  = d.blockMsg         || ''
					}
				} catch (e) {
					console.error('入住前置检查失败', e)
					uni.showToast({ title: '状态检查失败，请刷新重试', icon: 'none' })
				}
			},
			goToBill() {
				uni.navigateTo({ url: '/subpkg/affairs/bill' })
			},
			startCheckinCountdown() {
				if (this._checkinTimer) clearInterval(this._checkinTimer)
				this._checkinTimer = setInterval(() => {
					if (this.checkinRemainSeconds > 0) {
						this.checkinRemainSeconds--
						this.checkinBlockMsg = `您有资料尚未上传，需在 ${this.formatCountdownCheckin(this.checkinRemainSeconds)} 内完成上传方可办理入住`
					}
				}, 1000)
			},
			formatCountdownCheckin(s) {
				if (!s || s <= 0) return '已到期'
				const h = Math.floor(s / 3600), m = Math.floor((s % 3600) / 60), sec = s % 60
				return `${h}时${m}分${sec}秒`
			},
			goUpload() {
				uni.navigateTo({ url: '/pages/upload/index' })
			},
			showBlockReason() {
				uni.showToast({ title: this.checkinBlockMsg || '请先满足入住条件', icon: 'none', duration: 2500 })
			},

			// 加载倒计时数据（针对所有 status=0 的合同）
			async loadCountdowns() {
				try {
					const targets = this.checkinList.filter(it => it.statusCode === '0' && it.contractId)
					if (targets.length === 0) {
						this.countdownMap = {}
						this.stopCountdownTick()
						return
					}
					const map = {}
					for (const it of targets) {
						try {
							const res = await getCheckinCountdown(it.contractId)
							if (res && res.code === 200 && res.data) {
								map[it.contractId] = {
									showCountdown: !!res.data.showCountdown,
									remainingSeconds: Number(res.data.remainingSeconds) || 0,
									totalSeconds: Number(res.data.totalSeconds) || 0,
									deadline: res.data.deadline,
									timeoutHours: res.data.timeoutHours
								}
							}
						} catch (e) {
							// 单条失败不影响其他
							console.warn('countdown 加载失败 contractId=' + it.contractId, e)
						}
					}
					this.countdownMap = map
					this.startCountdownTick()
				} catch (e) {
					console.error('loadCountdowns 失败', e)
				}
			},

			// 启动每秒递减定时器
			startCountdownTick() {
				if (this._countdownTimer) clearInterval(this._countdownTimer)
				this._countdownTimer = setInterval(() => {
					let hasActive = false
					const map = { ...this.countdownMap }
					Object.keys(map).forEach(k => {
						const c = map[k]
						if (c && c.showCountdown && c.remainingSeconds > 0) {
							c.remainingSeconds = c.remainingSeconds - 1
							hasActive = true
						}
					})
					this.countdownMap = map
					if (!hasActive) this.stopCountdownTick()
				}, 1000)
			},

			stopCountdownTick() {
				if (this._countdownTimer) {
					clearInterval(this._countdownTimer)
					this._countdownTimer = null
				}
			},

			// 格式化剩余时间：>=1h 显示 X时X分；<1h 显示 X分X秒
			formatRemain(s) {
				if (!s || s <= 0) return '已超时'
				const h = Math.floor(s / 3600)
				const m = Math.floor((s % 3600) / 60)
				const sec = s % 60
				if (h > 0) return `${h}小时${m}分钟`
				if (m > 0) return `${m}分${sec}秒`
				return `${sec}秒`
			},

			// 进度条宽度（剩余/总）
			getProgressWidth(remaining, total) {
				if (!total || total <= 0) return '0%'
				let p = (remaining / total) * 100
				if (p < 0) p = 0
				if (p > 100) p = 100
				return p.toFixed(2) + '%'
			},

			// 倒计时颜色级别：>50% 蓝、24h~50% 黄、<24h 红
			getCountdownLevelClass(remaining, total) {
				if (!remaining || remaining <= 0) return 'countdown-danger'
				const ratio = total > 0 ? remaining / total : 0
				if (remaining < 24 * 3600) return 'countdown-danger'
				if (ratio < 0.5) return 'countdown-warning'
				return 'countdown-normal'
			},
		}
	}
</script>

<style scoped>
	.page {
		width: 100%;
		min-height: 100vh;
		background-color: #f5f6fc;
		display: flex;
		flex-direction: column;
	}

	.scroll-content {
		flex: 1;
		padding: 24rpx;
		box-sizing: border-box;
	}

	.card {
		width: 702rpx;
		min-height: 526rpx;
		border-radius: 20rpx;
		background: #ffffff;
		padding: 32rpx 28rpx 22rpx 28rpx;
		margin: 0 auto 24rpx;
		box-sizing: border-box;
		position: relative;
	}

	.cancel-tag {
		position: absolute;
		top: 0;
		right: 0;
		padding: 8rpx 20rpx;
		background: #768394;
		color: #ffffff;
		font-size: 22rpx;
		border-radius: 0 20rpx 0 12rpx;
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
		width: 520rpx;
		color: #1a1a1a;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 37rpx;
		text-align: right;
	}

	/* 状态颜色 */
	.status-pending {
		color: #ff8d1a;
	}

	.status-approved {
		color: #12a566;
	}

	.status-rejected {
		color: #fa5740;
	}

	.status-cancelled {
		color: #768394;
	}

	.status-confirm {
		color: #1281ff;
	}

	.status-confirmed {
		color: #52c41a;
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

	.btn-cancel {
		border: 1rpx solid #1281ff;
		background: #ffffff;
	}

	.btn-edit {
		background: #1281ff;
	}

	.btn-checkin {
		background: #1281ff;
	}

	.btn-disabled {
		background: #cccccc;
	}

	.btn-detail {
		border: 1rpx solid #1281ff;
		background: #ffffff;
	}

	.btn-confirm {
		background: #1281ff;
	}

	.btn-handle {
		background: #52c41a;
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

	/* 倒计时条 */
	.countdown-bar {
		margin-top: 20rpx;
		padding: 18rpx 20rpx;
		border-radius: 12rpx;
		border: 1rpx solid #e6f4ff;
		background: #f0f8ff;
	}

	.countdown-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 12rpx;
	}

	.countdown-title {
		font-size: 24rpx;
		color: #333;
	}

	.countdown-time {
		font-size: 26rpx;
		font-weight: 600;
		color: #1281ff;
	}

	.countdown-progress {
		width: 100%;
		height: 8rpx;
		background: #e6e6e6;
		border-radius: 4rpx;
		overflow: hidden;
	}

	.countdown-progress-inner {
		height: 100%;
		background: #1281ff;
		transition: width 0.6s linear;
	}

	.countdown-tip {
		display: block;
		margin-top: 10rpx;
		font-size: 22rpx;
		color: #888;
	}

	/* 不同等级配色 */
	.countdown-normal {
		background: #f0f8ff;
		border-color: #d6e8ff;
	}
	.countdown-normal .countdown-time { color: #1281ff; }
	.countdown-normal .countdown-progress-inner { background: #1281ff; }

	.countdown-warning {
		background: #fff7e6;
		border-color: #ffd591;
	}
	.countdown-warning .countdown-time { color: #fa8c16; }
	.countdown-warning .countdown-progress-inner { background: #fa8c16; }

	.countdown-danger {
		background: #fff1f0;
		border-color: #ffa39e;
	}
	.countdown-danger .countdown-time { color: #fa5740; }
	.countdown-danger .countdown-progress-inner { background: #fa5740; }
	.countdown-danger .countdown-tip { color: #d4380d; }
</style>

