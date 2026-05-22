<template>
	<view class="page">
		<view class="form-card">
			<view class="form-item">
				<text class="form-label">签约人姓名 <text class="req">*</text></text>
				<input class="form-input" v-model="form.signName" placeholder="请输入签约人姓名" maxlength="20" />
			</view>
			<view class="form-item">
				<text class="form-label">签约单位 <text class="req">*</text></text>
				<input class="form-input" v-model="form.signUnit" placeholder="请输入签约单位" maxlength="100" />
			</view>
			<view class="form-item">
				<text class="form-label">合同签订日期 <text class="req">*</text></text>
				<picker mode="date" :value="form.signDate" @change="onDateChange">
					<view class="picker-input">{{ form.signDate || '请选择日期' }}</view>
				</picker>
			</view>
			<view class="form-item">
				<text class="form-label">合同附件 <text class="req">*</text></text>
				<view class="upload-area">
					<view class="upload-item" v-for="(f, i) in fileList" :key="i">
						<image class="upload-img" :src="f" mode="aspectFill" @click="previewFile(i)" />
						<view class="upload-del" @click="removeFile(i)">×</view>
					</view>
					<view class="upload-add" v-if="fileList.length < 5" @click="chooseFile">+</view>
				</view>
				<text class="upload-tip">最多上传 5 个文件</text>
			</view>
		</view>

		<view class="bottom-btn-container">
			<view class="bottom-btn" :class="{ disabled: submitting }" @click="handleSubmit">
				<text class="bottom-btn-text">{{ submitting ? '提交中...' : '提交备案' }}</text>
			</view>
		</view>
	</view>
</template>

<script>
import { submitFiling } from '@/api/contractFiling'
import { BASE_URL } from '@/utils/request'
import config from '@/config/index'

export default {
	data() {
		return {
			tenantId: null,
			submitting: false,
			fileList: [],
			form: {
				signName: '',
				signUnit: '',
				signDate: ''
			}
		}
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
	methods: {
		onDateChange(e) { this.form.signDate = e.detail.value },
		chooseFile() {
			uni.chooseImage({
				count: 5 - this.fileList.length,
				success: (res) => { res.tempFilePaths.forEach(p => this.uploadOne(p)) }
			})
		},
		uploadOne(filePath) {
			const token = uni.getStorageSync('token') || ''
			uni.showLoading({ title: '上传中...' })
			uni.uploadFile({
				url: (config.uploadUrl || BASE_URL) + '/common/upload',
				filePath, name: 'file',
				header: { 'Authorization': token ? `Bearer ${token}` : '' },
				success: (r) => {
					try {
						const data = JSON.parse(r.data)
						if (data.code === 200) {
							const url = data.url || data.fileName || data.data
							if (url) this.fileList.push(url.startsWith('http') ? url : (BASE_URL + url))
						} else { uni.showToast({ title: data.msg || '上传失败', icon: 'none' }) }
					} catch (e) { uni.showToast({ title: '上传响应解析失败', icon: 'none' }) }
				},
				complete: () => uni.hideLoading()
			})
		},
		previewFile(i) { uni.previewImage({ current: i, urls: this.fileList }) },
		removeFile(i) { this.fileList.splice(i, 1) },
		async handleSubmit() {
			if (this.submitting) return
			if (!this.form.signName) return uni.showToast({ title: '请填写签约人', icon: 'none' })
			if (!this.form.signUnit) return uni.showToast({ title: '请填写签约单位', icon: 'none' })
			if (!this.form.signDate) return uni.showToast({ title: '请选择签订日期', icon: 'none' })
			if (this.fileList.length === 0) return uni.showToast({ title: '请上传合同附件', icon: 'none' })
			this.submitting = true
			try {
				const res = await submitFiling({
					tenantId: this.tenantId,
					signName: this.form.signName,
					signUnit: this.form.signUnit,
					signDate: this.form.signDate,
					contractFiles: this.fileList.join(',')
				})
				if (res.code === 200) {
					uni.showToast({ title: '提交成功，等待审批', icon: 'success' })
					setTimeout(() => uni.navigateBack(), 1500)
				}
			} catch (e) { console.error(e) }
			finally { this.submitting = false }
		}
	}
}
</script>

<style scoped>
.page { min-height: 100vh; background: #f5f6fc; padding-bottom: 140rpx; }
.form-card { background: #fff; margin: 24rpx; border-radius: 20rpx; padding: 24rpx; }
.form-item { margin-bottom: 32rpx; }
.form-label { display: block; font-size: 28rpx; color: #1a1a1a; margin-bottom: 16rpx; }
.req { color: #f5222d; margin-left: 6rpx; }
.form-input, .picker-input { display: block; height: 80rpx; line-height: 80rpx; padding: 0 20rpx; background: #f5f6fc; border-radius: 12rpx; font-size: 28rpx; color: #1a1a1a; }
.picker-input { color: #1a1a1a; }
.upload-area { display: flex; flex-wrap: wrap; gap: 16rpx; }
.upload-item { position: relative; width: 160rpx; height: 160rpx; }
.upload-img { width: 100%; height: 100%; border-radius: 12rpx; }
.upload-del { position: absolute; top: -16rpx; right: -16rpx; width: 36rpx; height: 36rpx; line-height: 32rpx; text-align: center; background: rgba(0,0,0,0.6); color: #fff; border-radius: 50%; font-size: 28rpx; }
.upload-add { width: 160rpx; height: 160rpx; line-height: 160rpx; text-align: center; background: #f5f6fc; color: #999; border-radius: 12rpx; font-size: 60rpx; }
.upload-tip { display: block; margin-top: 12rpx; font-size: 24rpx; color: #999; }
.bottom-btn-container { position: fixed; bottom: 0; left: 0; right: 0; padding: 20rpx 24rpx; background: #fff; box-shadow: 0 -4rpx 16rpx rgba(0,0,0,0.05); }
.bottom-btn { height: 88rpx; border-radius: 16rpx; background: linear-gradient(270deg,#4fc7ff 0%,#0f73ff 100%); display: flex; align-items: center; justify-content: center; }
.bottom-btn.disabled { opacity: 0.6; }
.bottom-btn-text { color: #fff; font-size: 30rpx; font-weight: 500; }
</style>
