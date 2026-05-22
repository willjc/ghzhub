<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<view class="card">
				<view class="card-header">
					<view class="card-indicator"></view>
					<text class="card-title">订单信息</text>
					<view class="status-info">
						<text class="status-tag" :class="statusClassMap[detail.status] || ''">{{ getStatusText(detail.status) }}</text>
					</view>
				</view>

				<view class="form-row">
					<text class="form-label">订单号</text>
					<view class="form-value-wrap"><text class="form-value">{{ detail.orderNo }}</text></view>
				</view>
				<view class="form-row">
					<text class="form-label">订单类型</text>
					<view class="form-value-wrap"><text class="form-value">{{ detail.orderType === '1' ? '保洁服务' : '搬家服务' }}</text></view>
				</view>
				<view class="form-row">
					<text class="form-label">联系人</text>
					<view class="form-value-wrap"><text class="form-value">{{ detail.applicantName }}</text></view>
				</view>
				<view class="form-row">
					<text class="form-label">联系电话</text>
					<view class="form-value-wrap"><text class="form-value">{{ detail.applicantPhone }}</text></view>
				</view>

				<!-- 保洁专属字段 -->
				<template v-if="detail.orderType === '1'">
					<view class="form-row">
						<text class="form-label">保洁类型</text>
						<view class="form-value-wrap"><text class="form-value">{{ getCleanTypeText(detail.cleanType) }}</text></view>
					</view>
					<view class="form-row">
						<text class="form-label">服务地址</text>
						<view class="form-value-wrap-full"><text class="form-value-desc">{{ detail.serviceAddress }}</text></view>
					</view>
					<view class="form-row" v-if="detail.roomCount">
						<text class="form-label">房间数</text>
						<view class="form-value-wrap"><text class="form-value">{{ detail.roomCount }} 间</text></view>
					</view>
				</template>

				<!-- 搬家专属字段 -->
				<template v-else>
					<view class="form-row">
						<text class="form-label">起运地址</text>
						<view class="form-value-wrap-full"><text class="form-value-desc">{{ detail.fromAddress }}</text></view>
					</view>
					<view class="form-row">
						<text class="form-label">目的地址</text>
						<view class="form-value-wrap-full"><text class="form-value-desc">{{ detail.toAddress }}</text></view>
					</view>
					<view class="form-row" v-if="detail.moveItemDesc">
						<text class="form-label">物品描述</text>
						<view class="form-value-wrap-full"><text class="form-value-desc">{{ detail.moveItemDesc }}</text></view>
					</view>
					<view class="form-row">
						<text class="form-label">需要拆装</text>
						<view class="form-value-wrap"><text class="form-value">{{ detail.needDisassembly === 'Y' ? '是' : '否' }}</text></view>
					</view>
				</template>

				<view class="form-row">
					<text class="form-label">期望时间</text>
					<view class="form-value-wrap"><text class="form-value">{{ formatTime(detail.expectTime) }}</text></view>
				</view>
				<view class="form-row" v-if="detail.remark">
					<text class="form-label">备注</text>
					<view class="form-value-wrap-full"><text class="form-value-desc">{{ detail.remark }}</text></view>
				</view>
				<view class="form-row">
					<text class="form-label">提交时间</text>
					<view class="form-value-wrap"><text class="form-value">{{ formatTime(detail.createTime) }}</text></view>
				</view>
			</view>

			<!-- 服务公司分配信息 -->
			<view class="card" v-if="['1','2','3'].indexOf(detail.status) >= 0">
				<view class="card-header">
					<view class="card-indicator"></view>
					<text class="card-title">服务公司</text>
				</view>
				<view class="form-row">
					<text class="form-label">公司名称</text>
					<view class="form-value-wrap"><text class="form-value">{{ detail.companyName || '-' }}</text></view>
				</view>
				<view class="form-row" v-if="detail.assignedTime">
					<text class="form-label">分配时间</text>
					<view class="form-value-wrap"><text class="form-value">{{ formatTime(detail.assignedTime) }}</text></view>
				</view>
				<view class="form-row" v-if="detail.finishTime">
					<text class="form-label">完成时间</text>
					<view class="form-value-wrap"><text class="form-value">{{ formatTime(detail.finishTime) }}</text></view>
				</view>
			</view>

			<!-- 评价信息（已评价） -->
			<view class="card" v-if="detail.rateScore">
				<view class="card-header">
					<view class="card-indicator"></view>
					<text class="card-title">我的评价</text>
				</view>
				<view class="form-row">
					<text class="form-label">评分</text>
					<view class="form-value-wrap">
						<view class="star-row">
							<text v-for="n in 5" :key="n" class="star" :class="{ active: n <= detail.rateScore }">★</text>
						</view>
					</view>
				</view>
				<view class="form-row" v-if="detail.rateContent">
					<text class="form-label">评价内容</text>
					<view class="form-value-wrap-full"><text class="form-value-desc">{{ detail.rateContent }}</text></view>
				</view>
				<view class="form-row" v-if="detail.rateTime">
					<text class="form-label">评价时间</text>
					<view class="form-value-wrap"><text class="form-value">{{ formatTime(detail.rateTime) }}</text></view>
				</view>
			</view>

			<!-- 取消原因 -->
			<view class="card" v-if="detail.status === '4' && detail.cancelReason">
				<view class="card-header">
					<view class="card-indicator"></view>
					<text class="card-title">取消原因</text>
				</view>
				<view class="form-row">
					<view class="form-value-wrap-full"><text class="form-value-desc">{{ detail.cancelReason }}</text></view>
				</view>
			</view>
		</scroll-view>

		<!-- 底部操作栏 -->
		<view class="bottom-btn-container" v-if="!loading && (detail.status === '0' || (detail.status === '3' && !detail.rateScore))">
			<view class="bottom-btn cancel-btn" v-if="detail.status === '0'" @click="handleCancel">
				<text class="bottom-btn-text cancel-text">取消订单</text>
			</view>
			<view class="bottom-btn" v-if="detail.status === '3' && !detail.rateScore" @click="showRateDialog = true">
				<text class="bottom-btn-text">立即评价</text>
			</view>
		</view>

		<!-- 评价弹层 -->
		<view class="picker-mask" v-if="showRateDialog" @click="showRateDialog = false"></view>
		<view class="rate-popup" :class="{ show: showRateDialog }">
			<view class="rate-header">
				<text class="rate-title">服务评价</text>
				<text class="rate-close" @click="showRateDialog = false">×</text>
			</view>
			<view class="rate-body">
				<view class="rate-row">
					<text class="rate-label">评分</text>
					<view class="star-row star-input">
						<text
							v-for="n in 5"
							:key="n"
							class="star big"
							:class="{ active: n <= rateForm.rateScore }"
							@click="rateForm.rateScore = n"
						>★</text>
					</view>
				</view>
				<view class="rate-row rate-row-column">
					<text class="rate-label">评价内容</text>
					<textarea
						class="rate-textarea"
						v-model="rateForm.rateContent"
						placeholder="说说本次服务的体验吧（选填）"
						placeholder-class="placeholder"
						maxlength="300"
					></textarea>
				</view>
			</view>
			<view class="rate-footer">
				<view class="rate-submit-btn" @click="handleSubmitRate">
					<text class="rate-submit-text">{{ rateSubmitting ? '提交中...' : '提交评价' }}</text>
				</view>
			</view>
		</view>

		<view class="loading-wrapper" v-if="loading">
			<text class="loading-text">加载中...</text>
		</view>
	</view>
</template>

<script>
import { getServiceOrderDetail, cancelServiceOrder, rateServiceOrder } from '@/api/serviceOrder.js'

export default {
	data() {
		return {
			orderId: '',
			detail: {
				orderId: '',
				orderNo: '',
				orderType: '',
				status: '',
				applicantName: '',
				applicantPhone: '',
				serviceAddress: '',
				cleanType: '',
				roomCount: 0,
				fromAddress: '',
				toAddress: '',
				moveItemDesc: '',
				needDisassembly: '',
				expectTime: '',
				remark: '',
				companyName: '',
				assignedTime: '',
				finishTime: '',
				rateScore: 0,
				rateContent: '',
				rateTime: '',
				cancelReason: '',
				createTime: ''
			},
			loading: false,
			phone: '',
			cleanTypeMap: {
				'daily': '日常保洁',
				'deep': '深度保洁',
				'rough': '开荒保洁',
				'checkout': '退租保洁'
			},
			statusClassMap: {
				'0': 'status-pending',
				'1': 'status-assigned',
				'2': 'status-servicing',
				'3': 'status-completed',
				'4': 'status-cancelled'
			},
			showRateDialog: false,
			rateForm: { rateScore: 5, rateContent: '' },
			rateSubmitting: false
		}
	},
	onLoad(options) {
		const userInfo = uni.getStorageSync('userInfo')
		if (!userInfo || !userInfo.userId) {
			uni.showToast({ title: '请先登录', icon: 'none' })
			setTimeout(() => uni.navigateTo({ url: '/pages/login/index' }), 1500)
			return
		}
		this.phone = userInfo.phone || ''
		if (options.id) {
			this.orderId = options.id
			this.loadDetail()
		}
	},
	methods: {
		async loadDetail() {
			try {
				this.loading = true
				const res = await getServiceOrderDetail(this.orderId)
				if (res.code === 200 && res.data) {
					this.detail = res.data
				} else {
					uni.showToast({ title: res.msg || '加载失败', icon: 'none' })
				}
			} catch (err) {
				console.error('加载订单详情失败:', err)
				uni.showToast({ title: '加载失败', icon: 'none' })
			} finally {
				this.loading = false
			}
		},

		getStatusText(status) {
			const map = { '0': '待处理', '1': '已分配', '2': '服务中', '3': '已完成', '4': '已取消' }
			return map[status] || '-'
		},
		getCleanTypeText(t) { return this.cleanTypeMap[t] || '-' },

		formatTime(timeStr) {
			if (!timeStr) return ''
			const date = new Date(timeStr.replace(/-/g, '/'))
			if (isNaN(date.getTime())) return timeStr
			const y = date.getFullYear()
			const m = String(date.getMonth() + 1).padStart(2, '0')
			const d = String(date.getDate()).padStart(2, '0')
			const h = String(date.getHours()).padStart(2, '0')
			const mi = String(date.getMinutes()).padStart(2, '0')
			return `${y}-${m}-${d} ${h}:${mi}`
		},

		handleCancel() {
			uni.showModal({
				title: '确认取消',
				content: '确定要取消此订单吗？取消后无法恢复',
				confirmColor: '#ff4d4f',
				success: (res) => {
					if (res.confirm) this.doCancel()
				}
			})
		},
		async doCancel() {
			try {
				uni.showLoading({ title: '处理中...' })
				const res = await cancelServiceOrder({
					orderId: this.detail.orderId,
					phone: this.phone,
					cancelReason: '用户主动取消'
				})
				uni.hideLoading()
				if (res.code === 200) {
					uni.showToast({ title: '取消成功', icon: 'success' })
					setTimeout(() => this.loadDetail(), 800)
				} else {
					uni.showToast({ title: res.msg || '取消失败', icon: 'none' })
				}
			} catch (err) {
				uni.hideLoading()
				uni.showToast({ title: '取消失败', icon: 'none' })
			}
		},

		async handleSubmitRate() {
			if (this.rateSubmitting) return
			if (!this.rateForm.rateScore || this.rateForm.rateScore < 1) {
				uni.showToast({ title: '请选择评分', icon: 'none' })
				return
			}
			try {
				this.rateSubmitting = true
				uni.showLoading({ title: '提交中...' })
				const res = await rateServiceOrder({
					orderId: this.detail.orderId,
					phone: this.phone,
					rateScore: this.rateForm.rateScore,
					rateContent: this.rateForm.rateContent
				})
				uni.hideLoading()
				if (res.code === 200) {
					uni.showToast({ title: '评价成功', icon: 'success' })
					this.showRateDialog = false
					setTimeout(() => this.loadDetail(), 800)
				} else {
					uni.showToast({ title: res.msg || '提交失败', icon: 'none' })
				}
			} catch (err) {
				uni.hideLoading()
				uni.showToast({ title: '提交失败', icon: 'none' })
			} finally {
				this.rateSubmitting = false
			}
		}
	}
}
</script>

<style scoped>
	.page { width: 100%; min-height: 95vh; background-color: #f5f6fc; display: flex; flex-direction: column; }
	.scroll-content { flex: 1; padding: 24rpx; padding-bottom: 160rpx; box-sizing: border-box; }

	.loading-wrapper { display: flex; justify-content: center; align-items: center; height: 400rpx; }
	.loading-text { color: #999; font-size: 28rpx; }

	.card { width: 702rpx; border-radius: 20rpx; background: #ffffff; padding: 26rpx 0 10rpx 0; margin: 0 auto 24rpx; box-sizing: border-box; }
	.card-header { display: flex; align-items: center; margin-bottom: 32rpx; padding: 0 40rpx; }
	.card-indicator { width: 12rpx; height: 34rpx; background: #0f73ff; margin-right: 26rpx; flex-shrink: 0; }
	.card-title { color: #171a1f; font-size: 32rpx; font-weight: 600; flex: 1; }

	.status-info { display: flex; align-items: center; }
	.status-tag { padding: 6rpx 16rpx; border-radius: 8rpx; font-size: 22rpx; }
	.status-pending { background: #fff7e6; color: #ff8d1a; }
	.status-assigned { background: #e6f7ff; color: #1281ff; }
	.status-servicing { background: #e6fffb; color: #13c2c2; }
	.status-completed { background: #f6ffed; color: #52c41a; }
	.status-cancelled { background: #f5f5f5; color: #999; }

	.form-row { display: flex; align-items: flex-start; margin: 0 40rpx 28rpx 40rpx; }
	.form-label { width: 166rpx; color: #333; font-size: 28rpx; flex-shrink: 0; line-height: 40rpx; }
	.form-value-wrap { flex: 1; display: flex; align-items: flex-start; line-height: 40rpx; }
	.form-value-wrap-full { width: 100%; }
	.form-value { color: #1a1a1a; font-size: 26rpx; line-height: 40rpx; }
	.form-value-desc { color: #666; font-size: 26rpx; line-height: 40rpx; }

	.star-row { display: flex; align-items: center; gap: 4rpx; }
	.star { color: #d9d9d9; font-size: 32rpx; }
	.star.active { color: #faad14; }
	.star-input .star { font-size: 56rpx; padding: 0 8rpx; }
	.star.big.active { color: #faad14; }

	.bottom-btn-container { position: fixed; bottom: 0; left: 0; right: 0; padding: 24rpx; background: #f5f6fc; display: flex; gap: 16rpx; justify-content: center; }
	.bottom-btn {
		flex: 1; max-width: 702rpx; height: 92rpx; border-radius: 20rpx;
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
		display: flex; align-items: center; justify-content: center;
	}
	.cancel-btn { background: #ffffff; border: 1rpx solid #ff4d4f; }
	.bottom-btn-text { color: #ffffff; font-size: 36rpx; font-weight: 600; }
	.cancel-text { color: #ff4d4f; }

	.picker-mask { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); z-index: 998; }
	.rate-popup {
		position: fixed; left: 0; right: 0; bottom: 0; background: #ffffff;
		border-radius: 24rpx 24rpx 0 0; z-index: 999; padding-bottom: 40rpx;
		transform: translateY(100%); transition: transform 0.3s ease;
	}
	.rate-popup.show { transform: translateY(0); }
	.rate-header { display: flex; justify-content: space-between; align-items: center; padding: 32rpx 40rpx; border-bottom: 1rpx solid #f0f0f0; }
	.rate-title { font-size: 32rpx; font-weight: 600; color: #171a1f; }
	.rate-close { font-size: 48rpx; color: #999; line-height: 1; }
	.rate-body { padding: 32rpx 40rpx; }
	.rate-row { display: flex; align-items: center; margin-bottom: 32rpx; }
	.rate-row-column { flex-direction: column; align-items: flex-start; }
	.rate-label { width: 166rpx; color: #333; font-size: 28rpx; flex-shrink: 0; }
	.rate-row-column .rate-label { width: 100%; margin-bottom: 16rpx; }
	.rate-textarea {
		width: 100%; height: 200rpx; border: 1rpx solid #e5e5e5;
		border-radius: 12rpx; padding: 16rpx; box-sizing: border-box;
		font-size: 26rpx; color: #333;
	}
	.placeholder { color: #999; }
	.rate-footer { padding: 0 40rpx; }
	.rate-submit-btn {
		width: 100%; height: 92rpx; border-radius: 20rpx;
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
		display: flex; align-items: center; justify-content: center;
	}
	.rate-submit-text { color: #fff; font-size: 32rpx; font-weight: 600; }
</style>
