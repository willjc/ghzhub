<template>
	<view class="page">
		<!-- 顶部搜索栏 + 状态筛选 -->
		<view class="top-bar">
			<view class="search-row">
				<view class="search-input-wrap">
					<image class="search-icon" src="/static/搜索@2x.png" mode="aspectFit"></image>
					<input
						class="search-input"
						type="text"
						v-model="keyword"
						placeholder="请输入订单号/地址搜索"
						placeholder-class="placeholder"
						@confirm="loadList"
					/>
				</view>
				<view class="search-btn" @click="loadList"><text>搜索</text></view>
			</view>
			<scroll-view class="status-tabs" scroll-x>
				<view
					v-for="tab in statusTabs"
					:key="tab.value"
					class="status-tab"
					:class="{ active: status === tab.value }"
					@click="onStatusTabClick(tab.value)"
				>
					<text>{{ tab.label }}</text>
				</view>
			</scroll-view>
		</view>

		<scroll-view class="scroll-content" scroll-y>
			<!-- 订单卡片 -->
			<view class="card" v-for="item in orderList" :key="item.orderId" @click="handleViewDetail(item)">
				<view class="card-header">
					<text class="order-no">订单号：{{ item.orderNo }}</text>
					<text class="status-tag" :class="statusClassMap[item.status] || ''">{{ getStatusText(item.status) }}</text>
				</view>

				<!-- 保洁卡片 -->
				<view v-if="orderType === '1'" class="card-body">
					<view class="info-row">
						<text class="info-label">保洁类型：</text>
						<text class="info-value">{{ getCleanTypeText(item.cleanType) }}</text>
					</view>
					<view class="info-row">
						<text class="info-label">服务地址：</text>
						<text class="info-value ellipsis2">{{ item.serviceAddress }}</text>
					</view>
					<view class="info-row">
						<text class="info-label">期望时间：</text>
						<text class="info-value">{{ formatTime(item.expectTime) }}</text>
					</view>
				</view>

				<!-- 搬家卡片 -->
				<view v-else class="card-body">
					<view class="info-row">
						<text class="info-label">起运地址：</text>
						<text class="info-value ellipsis2">{{ item.fromAddress }}</text>
					</view>
					<view class="info-row">
						<text class="info-label">目的地址：</text>
						<text class="info-value ellipsis2">{{ item.toAddress }}</text>
					</view>
					<view class="info-row">
						<text class="info-label">期望时间：</text>
						<text class="info-value">{{ formatTime(item.expectTime) }}</text>
					</view>
				</view>

				<view class="card-footer">
					<text class="create-time">提交时间：{{ formatTime(item.createTime) }}</text>
					<view class="footer-arrow">
						<text class="view-detail">查看详情</text>
						<image class="arrow-icon" src="/static/向右1@2x.png" mode="aspectFit"></image>
					</view>
				</view>
			</view>

			<view class="empty-state" v-if="orderList.length === 0 && !loading">
				<text class="empty-text">暂无订单</text>
			</view>
			<view class="loading-state" v-if="loading">
				<text class="loading-text">加载中...</text>
			</view>
		</scroll-view>

		<!-- 底部申请按钮 -->
		<view class="bottom-btn-container">
			<view class="bottom-btn" @click="handleApply">
				<text class="bottom-btn-text">{{ orderType === '1' ? '保洁申请' : '搬家申请' }}</text>
			</view>
		</view>
	</view>
</template>

<script>
import { getMyServiceOrders } from '@/api/serviceOrder.js'
import authCheck from '@/mixins/authCheck'

export default {
	data() {
		return {
			orderType: '1', // 1=保洁 2=搬家
			keyword: '',
			status: '',
			statusTabs: [
				{ value: '', label: '全部' },
				{ value: '0', label: '待处理' },
				{ value: '1', label: '已分配' },
				{ value: '2', label: '服务中' },
				{ value: '3', label: '已完成' },
				{ value: '4', label: '已取消' }
			],
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
			orderList: [],
			loading: false,
			userId: null,
			phone: ''
		}
	},
	onLoad(options) {
		const type = options.type === 'move' ? '2' : '1'
		this.orderType = type
		uni.setNavigationBarTitle({
			title: type === '1' ? '保洁服务' : '搬家服务'
		})
		authCheck.checkLogin.call(this, {}, () => {
			const userInfo = uni.getStorageSync('userInfo') || {}
			this.phone = userInfo.phone || ''
			if (!this.phone) {
				uni.showToast({ title: '请先完善手机号', icon: 'none' })
				return
			}
			this.loadList()
		})
	},
	onShow() {
		if (this.phone) {
			this.loadList()
		}
	},
	methods: {
		async loadList() {
			if (!this.phone) return
			try {
				this.loading = true
				const res = await getMyServiceOrders({
					phone: this.phone,
					orderType: this.orderType,
					status: this.status,
					keyword: this.keyword
				})
				if (res.code === 200) {
					this.orderList = res.data || []
				}
			} catch (err) {
				console.error('加载订单列表失败:', err)
			} finally {
				this.loading = false
			}
		},

		onStatusTabClick(value) {
			this.status = value
			this.loadList()
		},

		getStatusText(status) {
			const map = { '0': '待处理', '1': '已分配', '2': '服务中', '3': '已完成', '4': '已取消' }
			return map[status] || '-'
		},
		getCleanTypeText(t) {
			return this.cleanTypeMap[t] || '-'
		},

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

		handleViewDetail(item) {
			uni.navigateTo({
				url: `/subpkg/service/service-order-detail?id=${item.orderId}`
			})
		},
		handleApply() {
			const url = this.orderType === '1'
				? '/subpkg/service/cleaning-submit'
				: '/subpkg/service/moving-submit'
			uni.navigateTo({ url })
		}
	}
}
</script>

<style scoped>
	.page { width: 100%; min-height: 95vh; background-color: #f5f6fc; display: flex; flex-direction: column; }

	.top-bar { background: #ffffff; padding: 20rpx 24rpx 0 24rpx; }
	.search-row { display: flex; align-items: center; gap: 20rpx; }
	.search-input-wrap {
		flex: 1; height: 70rpx; border-radius: 35rpx;
		background: #f5f6fc; display: flex; align-items: center;
		padding: 0 24rpx;
	}
	.search-icon { width: 32rpx; height: 32rpx; margin-right: 12rpx; }
	.search-input { flex: 1; font-size: 26rpx; color: #333; }
	.search-btn {
		padding: 0 24rpx; height: 70rpx;
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
		border-radius: 35rpx; color: #fff; font-size: 26rpx;
		display: flex; align-items: center; justify-content: center;
	}
	.status-tabs { white-space: nowrap; padding: 20rpx 0; }
	.status-tab {
		display: inline-block; padding: 8rpx 24rpx; margin-right: 16rpx;
		border-radius: 24rpx; background: #f5f6fc; color: #666;
		font-size: 24rpx;
	}
	.status-tab.active { background: #e6f0ff; color: #0f73ff; }

	.scroll-content { flex: 1; padding: 24rpx; padding-bottom: 140rpx; box-sizing: border-box; }

	.card { width: 702rpx; border-radius: 20rpx; background: #ffffff; padding: 28rpx; margin: 0 auto 24rpx; box-sizing: border-box; }
	.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16rpx; padding-bottom: 16rpx; border-bottom: 1rpx solid #f0f0f0; }
	.order-no { color: #666; font-size: 26rpx; }
	.status-tag { padding: 4rpx 16rpx; border-radius: 8rpx; font-size: 22rpx; }
	.status-pending { background: #fff7e6; color: #ff8d1a; }
	.status-assigned { background: #e6f7ff; color: #1281ff; }
	.status-servicing { background: #e6fffb; color: #13c2c2; }
	.status-completed { background: #f6ffed; color: #52c41a; }
	.status-cancelled { background: #f5f5f5; color: #999; }

	.card-body { display: flex; flex-direction: column; gap: 12rpx; margin-bottom: 16rpx; }
	.info-row { display: flex; align-items: flex-start; }
	.info-label { color: #666; font-size: 26rpx; flex-shrink: 0; }
	.info-value { color: #333; font-size: 26rpx; flex: 1; }
	.ellipsis2 { display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }

	.card-footer { display: flex; justify-content: space-between; align-items: center; padding-top: 16rpx; border-top: 1rpx solid #f0f0f0; }
	.create-time { color: #999; font-size: 24rpx; }
	.footer-arrow { display: flex; align-items: center; }
	.view-detail { color: #999; font-size: 24rpx; }
	.arrow-icon { width: 24rpx; height: 24rpx; margin-left: 8rpx; }

	.empty-state, .loading-state { display: flex; justify-content: center; align-items: center; padding: 100rpx 0; }
	.empty-text, .loading-text { color: #999; font-size: 28rpx; }

	.bottom-btn-container { position: fixed; bottom: 0; left: 0; right: 0; padding: 24rpx; background: #f5f6fc; }
	.bottom-btn { width: 702rpx; height: 92rpx; border-radius: 20rpx; background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%); display: flex; align-items: center; justify-content: center; margin: 0 auto; }
	.bottom-btn-text { color: #ffffff; font-size: 36rpx; font-weight: 600; }
</style>
