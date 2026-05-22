<template>
	<view class="page">
		<view class="form-card">
			<view class="form-item">
				<text class="form-label">申请人姓名 <text class="req">*</text></text>
				<input class="form-input" v-model="form.applyName" placeholder="请输入姓名" maxlength="20" />
			</view>
			<view class="form-item">
				<text class="form-label">身份证号 <text class="req">*</text></text>
				<input class="form-input" v-model="form.idCard" placeholder="请输入身份证号" maxlength="18" />
			</view>
			<view class="form-item">
				<text class="form-label">联系电话 <text class="req">*</text></text>
				<input class="form-input" v-model="form.phone" placeholder="请输入手机号" type="number" maxlength="11" />
			</view>
			<view class="form-item">
				<text class="form-label">申请补贴金额（元）<text class="req">*</text></text>
				<input class="form-input" v-model="form.subsidyAmount" placeholder="请输入金额" type="digit" />
			</view>
			<view class="form-item">
				<text class="form-label">购房合同附件 <text class="req">*</text></text>
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

		<!-- 承诺书 -->
		<view class="commitment-card" v-if="commitmentData.commitmentContent">
			<view class="commit-title">{{ commitmentData.templateName || '代购补贴承诺书' }}</view>
			<scroll-view class="commit-content" scroll-y>
				<rich-text :nodes="commitmentData.commitmentContent"></rich-text>
			</scroll-view>
			<view class="commit-status" v-if="commitmentId">
				<text class="signed-tag">✓ 已签署</text>
			</view>
			<view class="commit-actions" v-else>
				<checkbox-group @change="onAgreeChange">
					<label class="checkbox-label">
						<checkbox :checked="agreed" color="#0f73ff" value="agree"/>
						<text>我已阅读并同意以上承诺内容</text>
					</label>
				</checkbox-group>
				<view class="sign-btn" :class="{ disabled: !agreed }" @click="goSign">立即签字</view>
			</view>
		</view>

		<view class="bottom-btn-container">
			<view class="bottom-btn" :class="{ disabled: submitting }" @click="handleSubmit">
				<text class="bottom-btn-text">{{ submitting ? '提交中...' : '提交申请' }}</text>
			</view>
		</view>
	</view>
</template>

<script>
import { submitSubsidy } from '@/api/subsidyApply'
import { getTemplateByCode, signCommitment } from '@/api/commitment'
import { BASE_URL } from '@/utils/request'
import config from '@/config/index'

export default {
	data() {
		return {
			tenantId: null,
			submitting: false,
			fileList: [],
			form: { applyName: '', idCard: '', phone: '', subsidyAmount: '' },
			commitmentData: { templateId: null, commitmentType: '3', commitmentContent: '', templateName: '' },
			agreed: false,
			signatureData: '',
			commitmentId: null
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
		this.loadCommitment()
	},
	methods: {
		async loadCommitment() {
			try {
				const res = await getTemplateByCode('SUBSIDY_COMMITMENT_V1.0')
				if (res.code === 200) this.commitmentData = res.data || this.commitmentData
			} catch (e) { console.error(e) }
		},
		onAgreeChange(e) { this.agreed = (e.detail.value || []).includes('agree') },
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
		goSign() {
			if (!this.agreed) return uni.showToast({ title: '请先勾选同意', icon: 'none' })
			uni.navigateTo({
				url: '/pages/signature/index',
				events: {
					acceptSignature: (data) => {
						this.signatureData = data.signature
						this.signCommitmentNow()
					}
				}
			})
		},
		async signCommitmentNow() {
			try {
				uni.showLoading({ title: '签署中...' })
				const res = await signCommitment({
					projectId: null,
					tenantId: this.tenantId,
					commitmentType: this.commitmentData.commitmentType || '3',
					commitmentContent: this.commitmentData.commitmentContent,
					signatureData: this.signatureData
				})
				uni.hideLoading()
				if (res.code === 200 && res.data) {
					this.commitmentId = res.data.commitmentId
					uni.showToast({ title: '承诺书签署成功', icon: 'success' })
				}
			} catch (e) { uni.hideLoading(); console.error(e) }
		},
		async handleSubmit() {
			if (this.submitting) return
			if (!this.form.applyName) return uni.showToast({ title: '请填写姓名', icon: 'none' })
			if (!this.form.idCard) return uni.showToast({ title: '请填写身份证号', icon: 'none' })
			if (!this.form.phone) return uni.showToast({ title: '请填写电话', icon: 'none' })
			if (!this.form.subsidyAmount) return uni.showToast({ title: '请填写补贴金额', icon: 'none' })
			if (this.fileList.length === 0) return uni.showToast({ title: '请上传购房合同附件', icon: 'none' })
			if (!this.commitmentId) return uni.showToast({ title: '请先签署承诺书', icon: 'none' })
			this.submitting = true
			try {
				const res = await submitSubsidy({
					tenantId: this.tenantId,
					applyName: this.form.applyName,
					idCard: this.form.idCard,
					phone: this.form.phone,
					subsidyAmount: this.form.subsidyAmount,
					purchaseContractFiles: this.fileList.join(','),
					commitmentId: this.commitmentId
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
.form-card, .commitment-card { background: #fff; margin: 24rpx; border-radius: 20rpx; padding: 24rpx; }
.form-item { margin-bottom: 32rpx; }
.form-label { display: block; font-size: 28rpx; color: #1a1a1a; margin-bottom: 16rpx; }
.req { color: #f5222d; margin-left: 6rpx; }
.form-input { display: block; height: 80rpx; line-height: 80rpx; padding: 0 20rpx; background: #f5f6fc; border-radius: 12rpx; font-size: 28rpx; color: #1a1a1a; }
.upload-area { display: flex; flex-wrap: wrap; gap: 16rpx; }
.upload-item { position: relative; width: 160rpx; height: 160rpx; }
.upload-img { width: 100%; height: 100%; border-radius: 12rpx; }
.upload-del { position: absolute; top: -16rpx; right: -16rpx; width: 36rpx; height: 36rpx; line-height: 32rpx; text-align: center; background: rgba(0,0,0,0.6); color: #fff; border-radius: 50%; font-size: 28rpx; }
.upload-add { width: 160rpx; height: 160rpx; line-height: 160rpx; text-align: center; background: #f5f6fc; color: #999; border-radius: 12rpx; font-size: 60rpx; }
.upload-tip { display: block; margin-top: 12rpx; font-size: 24rpx; color: #999; }
.commit-title { font-size: 30rpx; font-weight: 500; color: #1a1a1a; text-align: center; padding-bottom: 16rpx; border-bottom: 1rpx solid #f0f0f4; margin-bottom: 16rpx; }
.commit-content { max-height: 600rpx; font-size: 26rpx; line-height: 1.8; color: #333; padding: 16rpx 0; }
.commit-status { text-align: center; padding: 16rpx 0; }
.signed-tag { color: #52c41a; font-size: 28rpx; }
.commit-actions { padding-top: 16rpx; }
.checkbox-label { display: flex; align-items: center; font-size: 26rpx; color: #666; margin-bottom: 20rpx; }
.sign-btn { height: 76rpx; line-height: 76rpx; text-align: center; background: linear-gradient(270deg,#4fc7ff 0%,#0f73ff 100%); color: #fff; border-radius: 12rpx; font-size: 28rpx; }
.sign-btn.disabled { background: #ccc; }
.bottom-btn-container { position: fixed; bottom: 0; left: 0; right: 0; padding: 20rpx 24rpx; background: #fff; box-shadow: 0 -4rpx 16rpx rgba(0,0,0,0.05); }
.bottom-btn { height: 88rpx; border-radius: 16rpx; background: linear-gradient(270deg,#4fc7ff 0%,#0f73ff 100%); display: flex; align-items: center; justify-content: center; }
.bottom-btn.disabled { opacity: 0.6; }
.bottom-btn-text { color: #fff; font-size: 30rpx; font-weight: 500; }
</style>
