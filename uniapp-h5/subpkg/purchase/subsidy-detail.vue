<template>
	<view class="page">
		<view class="card" v-if="detail.applyId">
			<view class="card-header">
				<text class="apply-no">{{ detail.applyNo }}</text>
				<text class="status-tag" :class="{ 'status-pending': detail.approveStatus === '0', 'status-success': detail.approveStatus === '1', 'status-reject': detail.approveStatus === '2' }">{{ statusText(detail.approveStatus) }}</text>
			</view>

			<view class="info-row"><text class="lbl">申请人</text><text class="val">{{ detail.applyName }}</text></view>
			<view class="info-row"><text class="lbl">身份证号</text><text class="val">{{ detail.idCard }}</text></view>
			<view class="info-row"><text class="lbl">联系电话</text><text class="val">{{ detail.phone }}</text></view>
			<view class="info-row"><text class="lbl">补贴金额</text><text class="val amount">¥{{ detail.subsidyAmount }}</text></view>
			<view class="info-row"><text class="lbl">承诺书</text><text class="val">{{ detail.commitmentId ? '已签署 #' + detail.commitmentId : '未签署' }}</text></view>
			<view class="info-row"><text class="lbl">提交时间</text><text class="val">{{ formatTime(detail.createTime) }}</text></view>
			<view class="info-row" v-if="detail.approveTime"><text class="lbl">审批时间</text><text class="val">{{ formatTime(detail.approveTime) }}</text></view>
			<view class="info-row" v-if="detail.approveBy"><text class="lbl">审批人</text><text class="val">{{ detail.approveBy }}</text></view>

			<view class="info-block" v-if="detail.approveRemark">
				<text class="lbl">审批备注</text>
				<text class="val-block" :class="{ reject: detail.approveStatus === '2' }">{{ detail.approveRemark }}</text>
			</view>

			<view class="info-block">
				<text class="lbl">购房合同附件</text>
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
import { getSubsidyDetail } from '@/api/subsidyApply'

export default {
	data() {
		return { detail: {}, fileList: [] }
	},
	onLoad(options) {
		if (!options.id) return uni.showToast({ title: '缺少申请ID', icon: 'none' })
		this.loadDetail(options.id)
	},
	methods: {
		async loadDetail(id) {
			try {
				const res = await getSubsidyDetail(id)
				if (res.code === 200) {
					this.detail = res.data || {}
					this.fileList = (this.detail.purchaseContractFiles || '').split(',').filter(x => x)
				}
			} catch (e) { console.error(e) }
		},
		statusText(s) { return { '0': '待审批', '1': '已通过', '2': '已驳回' }[s] || '—' },
		statusClass(s) { return { '0': 'status-pending', '1': 'status-success', '2': 'status-reject' }[s] || '' },
		formatTime(t) {
			if (!t) return '—'
			const dt = new Date(t)
			return `${dt.getFullYear()}-${String(dt.getMonth()+1).padStart(2,'0')}-${String(dt.getDate()).padStart(2,'0')} ${String(dt.getHours()).padStart(2,'0')}:${String(dt.getMinutes()).padStart(2,'0')}`
		},
		previewFile(i) { uni.previewImage({ current: i, urls: this.fileList }) }
	}
}
</script>

<style scoped>
.page { min-height: 100vh; background: #f5f6fc; }
.card { background: #fff; margin: 24rpx; border-radius: 20rpx; padding: 24rpx; }
.card-header { display: flex; justify-content: space-between; align-items: center; padding-bottom: 20rpx; border-bottom: 1rpx solid #f0f0f4; margin-bottom: 16rpx; }
.apply-no { font-size: 30rpx; font-weight: 500; color: #1a1a1a; }
.status-tag { font-size: 24rpx; padding: 4rpx 16rpx; border-radius: 20rpx; }
.status-pending { background: #fff7e6; color: #fa8c16; }
.status-success { background: #e6ffed; color: #52c41a; }
.status-reject { background: #fff1f0; color: #f5222d; }
.info-row { display: flex; padding: 16rpx 0; font-size: 28rpx; }
.info-row .lbl { width: 160rpx; color: #999; flex-shrink: 0; }
.info-row .val { flex: 1; color: #1a1a1a; }
.info-row .val.amount { color: #f5222d; font-weight: 500; }
.info-block { padding: 16rpx 0; }
.info-block .lbl { display: block; font-size: 28rpx; color: #999; margin-bottom: 12rpx; }
.val-block { font-size: 28rpx; color: #1a1a1a; line-height: 1.6; }
.val-block.reject { color: #f5222d; }
.file-list { display: flex; flex-wrap: wrap; gap: 16rpx; }
.file-img { width: 200rpx; height: 200rpx; border-radius: 12rpx; }
.empty-file { color: #999; font-size: 26rpx; }
</style>
