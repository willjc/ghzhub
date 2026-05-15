<template>
	<view class="page">
		<view class="scroll-content">
			<!-- 待上传资料订单卡片 -->
			<view class="order-cards" v-if="pendingOrders.length > 0">
				<view style="display:flex;align-items:center;margin:20rpx 0 10rpx 24rpx;">
					<view style="width:6rpx;height:32rpx;background:#4A90E2;border-radius:3rpx;margin-right:14rpx;"></view>
					<text style="font-size:28rpx;font-weight:500;color:#333;">待上传资料的预订单</text>
				</view>
				<view v-for="order in pendingOrders" :key="order.orderNo"
					style="background:#fff;border-radius:16rpx;padding:24rpx;margin:0 24rpx 16rpx;box-shadow:0 2rpx 12rpx rgba(0,0,0,0.06);">
					<view style="display:flex;margin-bottom:10rpx;">
						<text style="color:#999;font-size:26rpx;">预订单号：</text>
						<text style="color:#333;font-size:26rpx;">{{ order.orderNo }}</text>
					</view>
					<view style="display:flex;align-items:center;margin-top:10rpx;">
						<text style="font-size:26rpx;" :style="{ color: order.docRemainSeconds < 86400 ? '#e5252b' : '#fa8c16' }">
							剩余时间：{{ formatCountdown(order.docRemainSeconds) }}
						</text>
					</view>
				</view>
			</view>

			<!-- 顶部 Banner：被驳回提醒（双层提醒-第一层） -->
			<view v-if="rejectedCount > 0" class="rejected-banner">
				<text class="rejected-banner-icon">⚠</text>
				<text class="rejected-banner-text">您有 {{ rejectedCount }} 份资料被驳回，请尽快查看原因并重新上传</text>
			</view>

			<!-- 0 单提示：暂无待上传材料的合同 -->
			<view v-if="pendingOrders.length === 0" class="empty-tip">
				<text class="empty-tip-text">您当前没有待上传材料的合同，请先在小程序内完成选房与签约后再上传。</text>
			</view>

			<!-- 基本信息卡片 -->
			<view class="info-card">
				<!-- 基本信息标题 -->
				<view class="section-title">
					<view class="title-indicator"></view>
					<text class="title-text">基本信息</text>
				</view>

				<!-- 基本信息内容 -->
				<view class="info-content">
					<view class="info-row">
						<text class="info-label">人员身份：</text>
						<text class="info-value">{{ formData.identity }}</text>
					</view>
					<view class="info-row">
						<text class="info-label">姓名：</text>
						<text class="info-value">{{ formData.name }}</text>
					</view>
					<view class="info-row">
						<text class="info-label">身份证号：</text>
						<text class="info-value">{{ formData.idCard }}</text>
					</view>
					<view class="info-row">
						<text class="info-label">联系电话：</text>
						<text class="info-value">{{ formData.phone }}</text>
					</view>
					<view class="info-row">
						<text class="info-label">工作单位：</text>
						<text class="info-value">{{ formData.workUnit }}</text>
					</view>
					<view class="info-row">
						<text class="info-label">单位联系电话：</text>
						<text class="info-value">{{ formData.unitPhone }}</text>
					</view>
					<view class="info-row">
						<text class="info-label">配偶：</text>
						<text class="info-value">{{ formData.spouse }}</text>
					</view>
				</view>
			</view>

			<!-- 附件信息区域 -->
			<view class="attachment-section">
				<view class="section-title">
					<view class="title-indicator"></view>
					<text class="title-text">附件信息</text>
				</view>

				<!-- 工作证明上传 -->
				<view class="upload-section">
					<view class="upload-label-row">
						<text class="required-mark">*</text>
						<text class="upload-label">工作证明</text>
						<text class="upload-tip">（工作证明内容需与基本信息内容一致）</text>
					</view>

					<!-- 状态卡（双层提醒-第二层） -->
					<view v-if="workStatusInfo" class="doc-status-card" :class="'status-' + workStatusInfo.status">
						<view class="doc-status-header">
							<text class="doc-status-tag" :class="'tag-' + workStatusInfo.status">{{ workStatusInfo.label }}</text>
							<text class="doc-status-title">{{ workStatusInfo.title }}</text>
						</view>
						<view v-if="workStatusInfo.status === 'rejected'" class="doc-status-reason">
							<text class="doc-status-reason-label">拒绝原因：</text>
							<text class="doc-status-reason-text">{{ workStatusInfo.reason || '未填写原因' }}</text>
						</view>
					</view>

					<!-- 上传区域 -->
					<view class="upload-area" :class="{ 'upload-area-locked': workLocked }" @click="handleUpload">
						<image v-if="workFilePreview" class="uploaded-image" :src="workFilePreview" mode="aspectFill"></image>
						<view v-else class="upload-placeholder">
							<image class="upload-icon" src="/static/上传@2x.png"></image>
							<text class="upload-text">{{ workLocked ? '审核中，不可修改' : (workIsRejected ? '点击重新上传' : '点击上传') }}</text>
						</view>
					</view>

					<!-- 文件格式提示 -->
					<view class="format-tip">
						<text class="format-tip-text">仅支持图片格式（JPG / PNG），单张不超过 10MB</text>
					</view>
				</view>

			</view>
		</view>
		
		<!-- 提交按钮 -->
		<view class="submit-section">
			<button class="submit-btn" :class="{ 'submit-btn-disabled': pendingOrders.length === 0 }" :disabled="pendingOrders.length === 0" @click="handleSubmit">
				<text class="submit-btn-text">{{ pendingOrders.length === 0 ? '暂无合同' : '提交材料' }}</text>
			</button>
		</view>
	</view>
</template>

<script>
import { getUserInfo } from '@/api/user'
import { getPendingUploadOrders } from '@/api/order'
import config from '@/config/index'
import authCheck from '@/mixins/authCheck'

export default {
	data() {
		return {
			userId: null,
			contractId: null, // 从合同页跳转时传入，上传资料时关联合同
			formData: {
				identity: '',
				name: '',
				idCard: '',
				phone: '',
				workUnit: '',
				unitPhone: '',
				spouse: ''
			},
			// 工作证明
			workFile: null,           // 服务器返回的文件路径（已上传）
			workFilePreview: null,    // 用于界面预览的图片路径
			workUploading: false,     // 是否正在上传中
			loading: false,
			pendingOrders: [],
			// 后端 pending-upload 返回的资料状态对象 { documentId, auditStatus, auditOpinion, filePath }
			workDoc: null,
			_countdownTimer: null,
		}
	},
	onLoad(options) {
		// 使用统一的登录检查
		authCheck.checkLogin.call(this, options, () => {
			if (options.contractId) {
				this.contractId = options.contractId
			}
			this.loadUserInfo()
			this.loadPendingOrders()
		})
	},
	onUnload() {
		if (this._countdownTimer) clearInterval(this._countdownTimer)
	},
	computed: {
		// 工作证明状态描述（用于状态卡显示）
		workStatusInfo() {
			return this.buildStatusInfo(this.workDoc)
		},
		// 是否锁定（待审核 / 已通过 时锁定上传区）
		workLocked() {
			return this.workDoc && (this.workDoc.auditStatus === '0' || this.workDoc.auditStatus === '1')
		},
		// 是否被驳回
		workIsRejected() {
			return this.workDoc && this.workDoc.auditStatus === '2'
		},
		// 被驳回总数（用于顶部 banner）
		rejectedCount() {
			let n = 0
			if (this.workIsRejected) n++
			return n
		},
	},
	methods: {
		/**
		 * 根据资料对象生成状态卡所需信息
		 */
		buildStatusInfo(doc) {
			if (!doc) return null
			const map = {
				'0': { status: 'pending',  label: '审核中',     title: '资料已提交，等待管理员审核' },
				'1': { status: 'approved', label: '✓ 已通过',  title: '资料已审核通过' },
				'2': { status: 'rejected', label: '× 已驳回',  title: '资料未通过审核，请按原因重新上传' },
			}
			const info = map[doc.auditStatus]
			if (!info) return null
			return { ...info, reason: doc.auditOpinion }
		},
		/**
		 * 加载用户信息
		 */
		async loadUserInfo() {
			try {
				this.loading = true
				uni.showLoading({ title: '加载中...' })

				const res = await getUserInfo(this.userId)

				if (res.code === 200 && res.data) {
					const user = res.data

					// 映射后端字段到前端formData
					this.formData = {
						identity: this.getIdentityLabel(user.identityType),
						name: user.realName || '',
						idCard: user.idCard || '',
						phone: user.contactPhone || user.phone || '',
						workUnit: user.workUnit || '',
						unitPhone: user.unitContact || '',
						spouse: user.spouseName || '无'
					}

					// 如果已有工作证明附件，显示
					if (user.workProofAttachment) {
						this.workFile = user.workProofAttachment
						this.workFilePreview = this.getImageUrl(user.workProofAttachment)
					}
				} else {
					uni.showToast({
						title: res.msg || '获取用户信息失败',
						icon: 'none'
					})
				}
			} catch (err) {
				console.error('加载用户信息失败:', err)
				uni.showToast({
					title: '加载失败，请重试',
					icon: 'none'
				})
			} finally {
				this.loading = false
				uni.hideLoading()
			}
		},

		/**
		 * 转换身份类型代码为文字
		 */
		getIdentityLabel(identityType) {
			const map = {
				'1': '在职人员',
				'2': '应届毕业生'
			}
			return map[identityType] || '未知'
		},

		/**
		 * 获取图片完整URL
		 */
		getImageUrl(url) {
			if (!url) return ''
			if (url.startsWith('http://') || url.startsWith('https://')) {
				return url
			}
			// 从配置文件读取 baseUrl
			const baseUrl = config.staticUrl
			return baseUrl + (url.startsWith('/') ? url : '/' + url)
		},

		// 工作证明：选择图片后立即上传到服务器
		handleUpload() {
			if (this.workLocked) {
				uni.showToast({ title: '资料正在审核中或已通过，不可修改', icon: 'none' })
				return
			}
			if (this.workUploading) {
				uni.showToast({ title: '正在上传中，请稍候', icon: 'none' })
				return
			}
			uni.chooseImage({
				count: 1,
				sizeType: ['compressed'],
				sourceType: ['album', 'camera'],
				success: (res) => {
					const tempPath = res.tempFilePaths[0]
					// 立即显示本地预览
					this.workFilePreview = tempPath
					this.workFile = null // 清除旧的服务器路径
					this.workUploading = true
					uni.showLoading({ title: '上传中...' })
					// 被驳回时走 reupload 覆盖原记录；否则走普通 upload
					const reuploadId = this.workIsRejected ? (this.workDoc && this.workDoc.documentId) : null
					this.uploadFile(tempPath, '3', reuploadId).then(uploadRes => {
						uni.hideLoading()
						this.workUploading = false
						if (uploadRes && uploadRes.code === 200) {
							const resData = uploadRes.data || uploadRes
							this.workFile = resData.filePath || resData.fileName || resData.url
							uni.showToast({ title: '工作证明上传成功', icon: 'success' })
							// 重传成功后刷新状态
							this.loadPendingOrders()
						} else {
							this.workFilePreview = null
							uni.showToast({ title: uploadRes?.msg || '工作证明上传失败', icon: 'none' })
						}
					})
				},
				fail: (err) => {
					console.error('选择图片失败:', err)
					uni.showToast({ title: '选择图片失败', icon: 'none' })
				}
			})
		},
		// 学历证明：选择图片后立即上传到服务器
		// 提交材料：文件已在选择时上传，此处仅做校验和确认
		async handleSubmit() {
			if (!this.workFile) {
				uni.showToast({ title: this.workUploading ? '工作证明正在上传中' : '请上传工作证明', icon: 'none' })
				return
			}
			if (!this.contractId) {
				uni.showToast({ title: '缺少合同信息，请返回合同页重新进入', icon: 'none' })
				return
			}

			// 长文案告知弹窗：单按钮"我已知晓"，留在原页
			uni.showModal({
				title: '资料上传完成',
				content: '您已完成资料上传，请于合同签订后 72 小时内办理入住手续。我方将在 1 个月内完成资料审核，请务必保证所提交材料真实、完整、有效。后续在审核及不定期抽查中，如发现资料缺失、虚假填报、材料过期等违规情形，将记入个人诚信档案，同时责令限期退房并追缴相应违约金。',
				showCancel: false,
				confirmText: '我已知晓',
				confirmColor: '#0f73ff',
				success: () => {
					// 关闭弹窗后留在 upload 页，刷新一下状态展示
					this.loadPendingOrders()
				}
			})
		},
		async loadPendingOrders() {
			try {
				const res = await getPendingUploadOrders(this.userId)
				if (res.code === 200) {
					this.pendingOrders = res.data || []
					// 从首条订单解析两类资料的最新状态（hz_document.tenant_id 等于用户 id，全用户共用一份）
					if (this.pendingOrders.length > 0) {
						const first = this.pendingOrders[0]
						this.workDoc = first.workProof || null
						// 已有图片回填预览
						if (this.workDoc && this.workDoc.filePath) {
							this.workFilePreview = this.getImageUrl(this.workDoc.filePath)
							this.workFile = this.workDoc.filePath
						}
					} else {
						this.workDoc = null
					}
					this.startCountdownTimer()
				}
			} catch (e) {
				console.error('加载待上传订单失败', e)
			}
		},
		startCountdownTimer() {
			if (this._countdownTimer) clearInterval(this._countdownTimer)
			this._countdownTimer = setInterval(() => {
				this.pendingOrders.forEach(o => {
					if (o.docRemainSeconds > 0) o.docRemainSeconds--
				})
			}, 1000)
		},
		formatCountdown(seconds) {
			if (!seconds || seconds <= 0) return '已到期'
			const h = Math.floor(seconds / 3600)
			const m = Math.floor((seconds % 3600) / 60)
			const s = seconds % 60
			return `${h}时${m}分${s}秒`
		},
		async uploadFile(filePath, documentType, reuploadDocumentId) {
			// 校验文件路径有效性
			if (!filePath) {
				return { code: 500, msg: '文件路径无效，请重新选择' }
			}
			return new Promise((resolve) => {
				const token = uni.getStorageSync('token') || ''
				// 区分新上传 vs 重传（覆盖被驳回记录）
				const isReupload = !!reuploadDocumentId
				const url = config.baseUrl + (isReupload ? '/h5/document/reupload' : '/h5/document/upload')
				const formData = isReupload
					? { documentId: reuploadDocumentId }
					: { documentType, tenantId: this.userId, contractId: this.contractId }
				uni.uploadFile({
					url,
					filePath,
					name: 'file',
					formData,
					header: {
						'Authorization': token ? ('Bearer ' + token) : ''
					},
					success: (res) => {
						try {
							resolve(JSON.parse(res.data))
						} catch (e) {
							resolve({ code: 500, msg: '解析响应失败' })
						}
					},
					fail: (err) => {
						console.error('uploadFile fail:', err)
						const errMsg = (err && err.errMsg) || ''
						if (errMsg.indexOf('file not found') !== -1 || errMsg.indexOf('createUploadTask:fail') !== -1) {
							resolve({ code: 500, msg: '文件已过期，请重新选择' })
						} else {
							resolve({ code: 500, msg: '网络请求失败' })
						}
					}
				})
			})
		},
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
		overflow: hidden;
	}

	.scroll-content {
		flex: 1;
		overflow: hidden;
		padding-bottom: 180rpx;
	}

	/* 基本信息卡片 */
	.info-card {
		width: 702rpx;
		border-radius: 20rpx;
		opacity: 1;
		background: #ffffff;
		margin: 24rpx 24rpx 32rpx 24rpx;
		padding: 26rpx 0 28rpx 0;
		box-sizing: border-box;
	}

	/* 标题区域 */
	.section-title {
		display: flex;
		align-items: center;
		
	}

	.title-indicator {
		width: 12rpx;
		height: 36rpx;
		opacity: 1;
		background: #0f73ff;
		margin-right: 26rpx;
		border-radius: 2rpx;
	}

	.title-text {
		width: 128rpx;
		height: 45rpx;
		opacity: 1;
		color: #171a1f;
		font-size: 32rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 45rpx;
	}

	/* 基本信息内容 */
	.info-content {
		display: flex;
		flex-direction: column;
		gap: 16rpx;
		margin: 24rpx 0 0 41rpx;
	}

	.info-row {
		display: flex;
		align-items: center;
	}

	.info-label {
		height: 40rpx;
		opacity: 1;
		color: #888888;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
	}

	.info-value {
		width: 252rpx;
		height: 40rpx;
		opacity: 1;
		color: #333333;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
		flex: 1;
	}

	/* 附件信息区域 */
	.attachment-section {
		width: 702rpx;
		border-radius: 20rpx;
		opacity: 1;
		background: #ffffff;
		margin: 0 24rpx 32rpx 24rpx;
		padding: 26rpx 0 28rpx 0;
		box-sizing: border-box;
	}

	.upload-section {
		display: flex;
		flex-direction: column;
	}

	.upload-label-row {
		display: flex;
		align-items: flex-end;
		flex-wrap: nowrap;
		margin-bottom: 10rpx;
		margin-top: 24rpx;
		margin-left: 41rpx;
	}

	.required-mark {
		height: 40rpx;
		opacity: 1;
		color: #ff0000;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
		margin-right: 0;
		flex-shrink: 0;
	}

	.upload-label {
		height: 40rpx;
		opacity: 1;
		color: #333333;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
		margin-right: 10rpx;
		flex-shrink: 0;
	}

	.upload-tip {
		height: 34rpx;
		opacity: 1;
		color: #ff0000;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 34rpx;
		white-space: nowrap;
		flex-shrink: 0;
	}

	/* 上传区域 */
	.upload-area {
		width: 166rpx;
		height: 166rpx;
		border-radius: 12rpx;
		opacity: 1;
		border: 2rpx solid #cdced5;
		background: #fafbff;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 20rpx;
		position: relative;
		overflow: hidden;
		margin: 24rpx 0 26rpx 41rpx;
	}

	.upload-placeholder {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}

	.upload-icon {
		width: 38rpx;
		height: 38rpx;
		opacity: 1;
		margin-bottom: 10rpx;
	}

	.upload-text {
		font-size: 24rpx;
		color: #999999;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.uploaded-image {
		width: 100%;
		height: 100%;
		border-radius: 10rpx;
	}

	/* 文件格式提示 */
	.format-tip {
		margin-left: 41rpx;
		margin-bottom: 20rpx;
	}

	.format-tip-text {
		color: #999999;
		font-size: 22rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 32rpx;
	}

	/* 0 单提示 */
	.empty-tip {
		margin: 24rpx;
		padding: 20rpx 24rpx;
		background: #fff8e1;
		border: 1rpx solid #ffe082;
		border-radius: 12rpx;
	}

	.empty-tip-text {
		color: #b76b00;
		font-size: 26rpx;
		line-height: 40rpx;
	}

	/* 顶部 Banner：被驳回提醒 */
	.rejected-banner {
		display: flex;
		align-items: center;
		margin: 16rpx 24rpx 8rpx;
		padding: 20rpx 24rpx;
		background: #fff1f0;
		border: 1rpx solid #ffccc7;
		border-radius: 12rpx;
	}

	.rejected-banner-icon {
		font-size: 32rpx;
		color: #e5252b;
		margin-right: 12rpx;
	}

	.rejected-banner-text {
		color: #cf1322;
		font-size: 26rpx;
		font-weight: 500;
		line-height: 36rpx;
	}

	/* 资料状态卡 */
	.doc-status-card {
		margin: 0 41rpx 16rpx 41rpx;
		padding: 16rpx 20rpx;
		border-radius: 12rpx;
		border: 1rpx solid transparent;
	}

	.doc-status-card.status-pending {
		background: #f5f5f5;
		border-color: #e0e0e0;
	}

	.doc-status-card.status-approved {
		background: #f6ffed;
		border-color: #b7eb8f;
	}

	.doc-status-card.status-rejected {
		background: #fff1f0;
		border-color: #ffa39e;
	}

	.doc-status-header {
		display: flex;
		align-items: center;
		flex-wrap: wrap;
	}

	.doc-status-tag {
		display: inline-block;
		padding: 4rpx 14rpx;
		border-radius: 8rpx;
		font-size: 22rpx;
		margin-right: 12rpx;
		flex-shrink: 0;
	}

	.doc-status-tag.tag-pending {
		background: #d9d9d9;
		color: #595959;
	}

	.doc-status-tag.tag-approved {
		background: #52c41a;
		color: #fff;
	}

	.doc-status-tag.tag-rejected {
		background: #e5252b;
		color: #fff;
	}

	.doc-status-title {
		font-size: 24rpx;
		color: #333;
		line-height: 36rpx;
		flex: 1;
	}

	.doc-status-reason {
		margin-top: 10rpx;
		display: flex;
		align-items: flex-start;
	}

	.doc-status-reason-label {
		font-size: 24rpx;
		color: #cf1322;
		font-weight: 500;
		flex-shrink: 0;
	}

	.doc-status-reason-text {
		font-size: 24rpx;
		color: #cf1322;
		line-height: 34rpx;
		flex: 1;
	}

	/* 锁定态：上传区灰显且不可点击 */
	.upload-area-locked {
		opacity: 0.55;
		background: #f0f0f0 !important;
	}

	/* 提交按钮区域 */
	.submit-section {
		position: fixed;
		bottom: 68rpx;
		left: 24rpx;
		width: 702rpx;
		box-sizing: border-box;
		z-index: 100;
	}

	.submit-btn {
		width: 100%;
		height: 92rpx;
		border-radius: 20rpx;
		opacity: 1;
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
		backdrop-filter: blur(6rpx);
		border: none;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.submit-btn::after {
		border: none;
	}

	.submit-btn-disabled {
		background: #c8c9cc !important;
		opacity: 0.8;
	}

	.submit-btn-text {
		width: 144rpx;
		height: 51rpx;
		opacity: 1;
		color: #ffffff;
		font-size: 36rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 51rpx;
	}
</style>

