<template>
	<view class="page">
		<view class="filter-bar">
			<view class="filter-item" :class="{active: filter === ''}" @click="setFilter('')">全部</view>
			<view class="filter-item" :class="{active: filter === '0'}" @click="setFilter('0')">待审批</view>
			<view class="filter-item" :class="{active: filter === '1'}" @click="setFilter('1')">已通过</view>
			<view class="filter-item" :class="{active: filter === '2'}" @click="setFilter('2')">已驳回</view>
		</view>

		<scroll-view class="scroll-content" scroll-y>
			<view class="card" v-for="item in dataList" :key="item.applyId" @click="goDetail(item)">
				<view class="card-header">
					<text class="apply-no">{{ item.applyNo }}</text>
					<text class="status-tag" :class="statusClass(item.approveStatus)">{{ statusText(item.approveStatus) }}</text>
				</view>
				<view class="card-row"><text class="lbl">申请人：</text><text class="val">{{ item.applyName }}</text></view>
				<view class="card-row"><text class="lbl">补贴金额：</text><text class="val amount">¥{{ item.subsidyAmount }}</text></view>
				<view class="card-row"><text class="lbl">提交时间：</text><text class="val">{{ formatTime(item.createTime) }}</text></view>
				<view class="card-row" v-if="item.approveTime"><text class="lbl">审批时间：</text><text class="val">{{ formatTime(item.approveTime) }}</text></view>
				<view class="card-row reject" v-if="item.approveStatus === '2' && item.approveRemark">
					<text class="lbl">驳回原因：</text><text class="val">{{ item.approveRemark }}</text>
				</view>
			</view>
			<view class="empty" v-if="dataList.length === 0 && !loading">暂无补贴申请</view>
		</scroll-view>

		<view class="bottom-btn-container">
			<view class="bottom-btn" @click="goSubmit">
				<text class="bottom-btn-text">申请代购补贴</text>
			</view>
		</view>
	</view>
</template>

<script>
import { getMySubsidyList } from '@/api/subsidyApply'

export default {
	data() {
		return { filter: '', dataList: [], loading: false, tenantId: null }
	},
	onLoad() {
		const u = uni.getStorageSync('userInfo')
		if (!u || !u.userId) {
			uni.showToast({ title: '请先登录', icon: 'none' })
			setTimeout(() => uni.navigateTo({ url: '/pages/login/index' }), 1200)
			return
		}
		this.tenantId = u.userId
	},
	onShow() { if (this.tenantId) this.loadList() },
	methods: {
		setFilter(v) { this.filter = v; this.loadList() },
		async loadList() {
			this.loading = true
			try {
				const res = await getMySubsidyList(this.tenantId, this.filter)
				if (res.code === 200) this.dataList = res.data || []
			} catch (e) { console.error(e) }
			finally { this.loading = false }
		},
		statusText(s) { return { '0': '待审批', '1': '已通过', '2': '已驳回' }[s] || '—' },
		statusClass(s) { return { '0': 'status-pending', '1': 'status-success', '2': 'status-reject' }[s] || '' },
		formatTime(t) {
			if (!t) return '—'
			const dt = new Date(t)
			return `${dt.getFullYear()}-${String(dt.getMonth()+1).padStart(2,'0')}-${String(dt.getDate()).padStart(2,'0')} ${String(dt.getHours()).padStart(2,'0')}:${String(dt.getMinutes()).padStart(2,'0')}`
		},
		goDetail(item) { uni.navigateTo({ url: `/subpkg/purchase/subsidy-detail?id=${item.applyId}` }) },
		goSubmit() { uni.navigateTo({ url: '/subpkg/purchase/subsidy-submit' }) }
	}
}
</script>

<style scoped>
.page { min-height: 100vh; background: #f5f6fc; padding-bottom: 140rpx; }
.filter-bar { display: flex; background: #fff; padding: 20rpx 24rpx; gap: 16rpx; }
.filter-item { padding: 12rpx 28rpx; font-size: 26rpx; color: #666; background: #f0f0f4; border-radius: 30rpx; }
.filter-item.active { background: #0f73ff; color: #fff; }
.scroll-content { padding: 24rpx; }
.card { background: #fff; border-radius: 20rpx; padding: 24rpx; margin-bottom: 20rpx; }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16rpx; }
.apply-no { font-size: 28rpx; font-weight: 500; color: #1a1a1a; }
.status-tag { font-size: 24rpx; padding: 4rpx 16rpx; border-radius: 20rpx; }
.status-pending { background: #fff7e6; color: #fa8c16; }
.status-success { background: #e6ffed; color: #52c41a; }
.status-reject { background: #fff1f0; color: #f5222d; }
.card-row { display: flex; font-size: 26rpx; color: #555; line-height: 1.8; }
.card-row .lbl { color: #999; flex-shrink: 0; }
.card-row .val.amount { color: #f5222d; font-weight: 500; }
.card-row.reject .val { color: #f5222d; }
.empty { text-align: center; color: #999; padding: 100rpx 0; font-size: 28rpx; }
.bottom-btn-container { position: fixed; bottom: 0; left: 0; right: 0; padding: 20rpx 24rpx; background: #fff; box-shadow: 0 -4rpx 16rpx rgba(0,0,0,0.05); }
.bottom-btn { height: 88rpx; border-radius: 16rpx; background: linear-gradient(270deg,#4fc7ff 0%,#0f73ff 100%); display: flex; align-items: center; justify-content: center; }
.bottom-btn-text { color: #fff; font-size: 30rpx; font-weight: 500; }
</style>
