<template>
	<view class="page">
		<view class="card" v-if="detail.filingId">
			<view class="card-header">
				<text class="filing-no">{{ detail.filingNo }}</text>
				<text class="status-tag" :class="statusClass(detail.approveStatus)">{{ statusText(detail.approveStatus) }}</text>
			</view>

			<view class="info-row"><text class="lbl">签约人</text><text class="val">{{ detail.signName }}</text></view>
			<view class="info-row"><text class="lbl">签约单位</text><text class="val">{{ detail.signUnit }}</text></view>
			<view class="info-row"><text class="lbl">签订日期</text><text class="val">{{ formatDate(detail.signDate) }}</text></view>
			<view class="info-row"><text class="lbl">提交时间</text><text class="val">{{ formatTime(detail.createTime) }}</text></view>
			<view class="info-row" v-if="detail.approveTime"><text class="lbl">审批时间</text><text class="val">{{ formatTime(detail.approveTime) }}</text></view>
			<view class="info-row" v-if="detail.approveBy"><text class="lbl">审批人</text><text class="val">{{ detail.approveBy }}</text></view>

			<view class="info-block" v-if="detail.approveRemark">
				<text class="lbl">审批备注</text>
				<text class="val-block" :class="{ reject: detail.approveStatus === '2' }">{{ detail.approveRemark }}</text>
			</view>

			<view class="info-block">
				<text class="lbl">合同附件</text>
				<view class="file-list">
					<image class="file-img" v-for="(f, i) in fileList" :key="i" :src="f" mode="aspectFill"
						@click="previewFile(i)" />
					<text class="empty-file" v-if="fileList.length === 0">无</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import { getFilingDetail } from '@/api/contractFiling'

export default {
	data() {
		return { detail: {}, fileList: [] }
	},
	onLoad(options) {
		if (!options.id) return uni.showToast({ title: '缺少备案ID', icon: 'none' })
		this.loadDetail(options.id)
	},
	methods: {
		async loadDetail(id) {
			try {
				const res = await getFilingDetail(id)
				if (res.code === 200) {
					this.detail = res.data || {}
					this.fileList = (this.detail.contractFiles || '').split(',').filter(x => x)
				}
			} catch (e) { console.error(e) }
		},
		statusText(s) { return { '0': '待审批', '1': '已通过', '2': '已驳回' }[s] || '—' },
		statusClass(s) { return { '0': 'status-pending', '1': 'status-success', '2': 'status-reject' }[s] || '' },
		formatDate(d) {
			if (!d) return '—'
			const dt = new Date(d)
			return `${dt.getFullYear()}-${String(dt.getMonth()+1).padStart(2,'0')}-${String(dt.getDate()).padStart(2,'0')}`
		},
		formatTime(t) {
			if (!t) return '—'
			const dt = new Date(t)
			return `${this.formatDate(t)} ${String(dt.getHours()).padStart(2,'0')}:${String(dt.getMinutes()).padStart(2,'0')}`
		},
		previewFile(i) { uni.previewImage({ current: i, urls: this.fileList }) }
	}
}
</script>

<style scoped>
.page { min-height: 100vh; background: #f5f6fc; }
.card { background: #fff; margin: 24rpx; border-radius: 20rpx; padding: 24rpx; }
.card-header { display: flex; justify-content: space-between; align-items: center; padding-bottom: 20rpx; border-bottom: 1rpx solid #f0f0f4; margin-bottom: 16rpx; }
.filing-no { font-size: 30rpx; font-weight: 500; color: #1a1a1a; }
.status-tag { font-size: 24rpx; padding: 4rpx 16rpx; border-radius: 20rpx; }
.status-pending { background: #fff7e6; color: #fa8c16; }
.status-success { background: #e6ffed; color: #52c41a; }
.status-reject { background: #fff1f0; color: #f5222d; }
.info-row { display: flex; padding: 16rpx 0; font-size: 28rpx; }
.info-row .lbl { width: 160rpx; color: #999; flex-shrink: 0; }
.info-row .val { flex: 1; color: #1a1a1a; }
.info-block { padding: 16rpx 0; }
.info-block .lbl { display: block; font-size: 28rpx; color: #999; margin-bottom: 12rpx; }
.val-block { font-size: 28rpx; color: #1a1a1a; line-height: 1.6; }
.val-block.reject { color: #f5222d; }
.file-list { display: flex; flex-wrap: wrap; gap: 16rpx; }
.file-img { width: 200rpx; height: 200rpx; border-radius: 12rpx; }
.empty-file { color: #999; font-size: 26rpx; }
</style>
