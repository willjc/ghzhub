<template>
	<view class="page">
		<view class="scroll-content">
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
						<image v-if="uploadedFile" class="uploaded-image" :src="uploadedFile" mode="aspectFill"></image>
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
import { getUserInfo, uploadWorkProof } from '@/api/user'
import config from '@/config/index'

export default {
	data() {
		return {
			userId: null, // 当前用户ID（临时存储，后期从token获取）
			formData: {
				identity: '',      // 人员身份
				name: '',          // 姓名
				idCard: '',        // 身份证号
				phone: '',         // 联系电话
				workUnit: '',      // 工作单位
				unitPhone: '',     // 单位联系电话
				spouse: ''         // 配偶
			},
			uploadedFile: '',
			loading: false
		}
	},
	onLoad(options) {
		// 从本地存储获取登录用户信息
		const userInfo = uni.getStorageSync('userInfo')
		if (!userInfo || !userInfo.userId) {
			uni.showToast({
				title: '请先登录',
				icon: 'none'
			})
			setTimeout(() => {
				uni.navigateTo({
					url: '/pages/login/index'
				})
			}, 1500)
			return
		}

		// 使用当前登录用户的userId
		this.userId = userInfo.userId
		this.loadUserInfo()
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
						this.uploadedFile = this.getImageUrl(user.workProofAttachment)
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

		handleUpload() {
			uni.chooseImage({
				count: 1,
				sizeType: ['compressed'],
				sourceType: ['album', 'camera'],
				success: async (res) => {
					try {
						this.uploadedFile = res.tempFilePaths[0]

						// 立即上传到服务器
						uni.showLoading({ title: '上传中...' })
						const uploadRes = await uploadWorkProof(res.tempFilePaths[0], this.userId)

						uni.hideLoading()

						if (uploadRes.code === 200 || uploadRes.fileName) {
							uni.showToast({
								title: '上传成功',
								icon: 'success'
							})
							// 更新显示的图片为服务器路径
							if (uploadRes.fileName) {
								this.uploadedFile = this.getImageUrl(uploadRes.fileName)
							}
						}
					} catch (err) {
						uni.hideLoading()
						console.error('上传失败:', err)
					}
				},
				fail: (err) => {
					console.error('选择图片失败:', err)
					uni.showToast({
						title: '选择图片失败',
						icon: 'none'
					})
				}
			})
		},
		downloadTemplate() {
			// 下载模版逻辑
			console.log('下载工作证明模版')
			uni.showToast({
				title: '模版下载功能待实现',
				icon: 'none'
			})
		},
		handleSubmit() {
			if (!this.uploadedFile) {
				uni.showToast({
					title: '请上传工作证明',
					icon: 'none'
				})
				return
			}
			// 提交逻辑
			console.log('提交材料')
			uni.showToast({
				title: '提交成功',
				icon: 'success'
			})
		}
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

