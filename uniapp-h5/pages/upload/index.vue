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
					

					<!-- 上传区域 -->
					<view class="upload-area" @click="handleUpload">
						<image v-if="workFile" class="uploaded-image" :src="workFile" mode="aspectFill"></image>
						<view v-else class="upload-placeholder">
							<image class="upload-icon" src="/static/上传@2x.png"></image>
							<text class="upload-text">点击上传</text>
						</view>
					</view>

					<!-- 模版下载 -->
					<view class="template-download" @click="downloadTemplate">
						<text class="template-label">模版下载：</text>
						<text class="template-link">工作证明模版</text>
					</view>
				</view>

				<!-- 学历证明上传 -->
				<view class="upload-section" style="margin-top:30rpx;">
					<view class="upload-label-row">
						<text class="required-mark">*</text>
						<text class="upload-label">学历证明</text>
						<text class="upload-tip">（毕业证或在读证明）</text>
					</view>
					<view class="upload-area" @click="handleEducationUpload">
						<image v-if="educationFile" :src="getImageUrl(educationFile)" mode="aspectFill" class="uploaded-image"></image>
						<view v-else class="upload-placeholder">
							<image class="upload-icon" src="/static/上传@2x.png"></image>
							<text class="upload-text">点击上传</text>
						</view>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 提交按钮 -->
		<view class="submit-section">
			<button class="submit-btn" @click="handleSubmit">
				<text class="submit-btn-text">提交材料</text>
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
			// 工作证明：只存本地临时路径，不立即上传
			workFile: null,        // 本地临时路径（用于预览和提交时上传）
			// 学历证明：只存本地临时路径，不立即上传
			educationFile: null,   // 本地临时路径（用于预览和提交时上传）
			loading: false,
			pendingOrders: [],
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
	methods: {
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
						this.workFile = this.getImageUrl(user.workProofAttachment)
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

		// 工作证明：只选图，不上传，仅本地预览
		handleUpload() {
			uni.chooseImage({
				count: 1,
				sizeType: ['compressed'],
				sourceType: ['album', 'camera'],
				success: (res) => {
					this.workFile = res.tempFilePaths[0]
				},
				fail: (err) => {
					console.error('选择图片失败:', err)
					uni.showToast({ title: '选择图片失败', icon: 'none' })
				}
			})
		},
		downloadTemplate() {
			uni.showToast({ title: '模版下载功能待实现', icon: 'none' })
		},
		// 学历证明：只选图，不上传，仅本地预览
		handleEducationUpload() {
			uni.chooseImage({
				count: 1,
				sizeType: ['compressed'],
				sourceType: ['album', 'camera'],
				success: (res) => {
					this.educationFile = res.tempFilePaths[0]
				},
				fail: (err) => {
					console.error('选择图片失败:', err)
					uni.showToast({ title: '选择图片失败', icon: 'none' })
				}
			})
		},
		// 提交材料：两个文件都选好后，点击才上传入库
		async handleSubmit() {
			if (!this.workFile) {
				uni.showToast({ title: '请上传工作证明', icon: 'none' })
				return
			}
			if (!this.educationFile) {
				uni.showToast({ title: '请上传学历证明', icon: 'none' })
				return
			}
			if (!this.contractId) {
				uni.showToast({ title: '缺少合同信息，请返回合同页重新进入', icon: 'none' })
				return
			}

			try {
				uni.showLoading({ title: '提交中...' })

				// 依次上传工作证明（type=3）和学历证明（type=2）
				const workRes = await this.uploadFile(this.workFile, '3')
				if (!workRes || workRes.code !== 200) {
					uni.hideLoading()
					uni.showToast({ title: workRes?.msg || '工作证明上传失败', icon: 'none' })
					return
				}

				const eduRes = await this.uploadFile(this.educationFile, '2')
				if (!eduRes || eduRes.code !== 200) {
					uni.hideLoading()
					uni.showToast({ title: eduRes?.msg || '学历证明上传失败', icon: 'none' })
					return
				}

				uni.hideLoading()
				uni.showToast({ title: '提交成功', icon: 'success' })
				setTimeout(() => {
					uni.navigateBack()
				}, 1500)
			} catch (e) {
				uni.hideLoading()
				console.error('提交失败:', e)
				uni.showToast({ title: '提交失败，请重试', icon: 'none' })
			}
		},
		async loadPendingOrders() {
			try {
				const res = await getPendingUploadOrders(this.userId)
				if (res.code === 200) {
					this.pendingOrders = res.data || []
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
		async uploadFile(filePath, documentType) {
			return new Promise((resolve) => {
				const token = uni.getStorageSync('token') || ''
				uni.uploadFile({
					url: config.baseUrl + '/h5/document/upload',
					filePath,
					name: 'file',
					formData: { documentType, tenantId: this.userId, contractId: this.contractId },
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
						resolve({ code: 500, msg: '网络请求失败' })
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

	/* 模版下载 */
	.template-download {
		display: flex;
		align-items: center;
		margin-left: 41rpx;
	}

	.template-label {
		height: 34rpx;
		opacity: 1;
		color: #333333;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 34rpx;
	}

	.template-link {
		width: 264rpx;
		height: 34rpx;
		opacity: 1;
		color: #1976f8;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 34rpx;

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

