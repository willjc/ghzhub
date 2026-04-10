<template>
	<view class="page">
		<view class="scroll-content">
			<!-- 投诉及建议 -->
			<view class="form-card">
				<view class="section-title">
					<view class="title-indicator"></view>
					<text class="title-text">投诉及建议</text>
					<input class="title-input" v-model="formData.title" placeholder="请输入" placeholder-class="placeholder" />
				</view>
			</view>

			<!-- 投诉及建议内容 -->
			<view class="form-card">
				<view class="section-title">
					<view class="title-indicator"></view>
					<text class="title-text">投诉及建议内容</text>
				</view>
				<view class="textarea-wrapper">
					<textarea
						class="content-textarea"
						v-model="formData.content"
						placeholder="请输入"
						placeholder-class="placeholder"
						maxlength="500"
					></textarea>
				</view>
			</view>

			<!-- 联系方式 -->
			<view class="form-card">
				<view class="section-title">
					<view class="title-indicator"></view>
					<text class="required-mark">*</text>
					<text class="title-text">联系方式</text>
					<input class="title-input" v-model="formData.phone" placeholder="请输入" placeholder-class="placeholder" type="number" />
				</view>
			</view>

			<!-- 上传照片 -->
			<view class="form-card">
				<view class="section-title">
					<view class="title-indicator"></view>
					<text class="title-text">上传照片</text>
				</view>
				<view class="upload-section">
					<!-- 已上传的图片列表 -->
					<view class="upload-list">
						<view class="upload-item" v-for="(img, index) in uploadedImages" :key="index">
							<image class="uploaded-image" :src="getImageUrl(img)" mode="aspectFill"></image>
							<view class="delete-btn" @click="handleDeleteImage(index)">
								<text class="delete-icon">×</text>
							</view>
						</view>
						<!-- 上传按钮 -->
						<view class="upload-area" @click="handleUpload" v-if="uploadedImages.length < 9">
							<view class="upload-placeholder">
								<image class="upload-icon" src="/static/上传@2x.png"></image>
								<text class="upload-text">点击上传</text>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 提交按钮 -->
		<view class="submit-section">
			<button class="submit-btn" @click="handleSubmit">
				<text class="submit-btn-text">提交</text>
			</button>
		</view>
	</view>
</template>

<script>
import { submitComplaint } from '@/api/complaint.js'
import { BASE_URL } from '@/utils/request'

export default {
	data() {
		return {
			formData: {
				title: '',      // 投诉及建议标题
				content: '',    // 投诉及建议内容
				phone: ''       // 联系方式
			},
			uploadedImages: [], // 已上传的图片URL列表（相对路径）
			uploading: false,
			userId: null  // 当前登录用户ID
		}
	},
	onLoad() {
		// 获取用户信息
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

		this.userId = userInfo.userId
	},
	methods: {
		// 获取图片完整URL
		getImageUrl(url) {
			if (!url) return ''
			// 如果已经是完整URL，直接返回
			if (url.startsWith('http://') || url.startsWith('https://')) {
				return url
			}
			// 本地路径或相对路径
			if (url.startsWith('/static/')) {
				return url
			}
			// 后端返回的相对路径，拼接BASE_URL
			return BASE_URL + url
		},

		// 上传图片
		handleUpload() {
			const remainCount = 9 - this.uploadedImages.length
			uni.chooseImage({
				count: remainCount,
				sizeType: ['compressed'],
				sourceType: ['album', 'camera'],
				success: (res) => {
					// 上传选中的图片
					this.uploadImages(res.tempFilePaths)
				},
				fail: (err) => {
					console.error('选择图片失败:', err)
				}
			})
		},

		// 上传图片到服务器
		async uploadImages(tempFilePaths) {
			for (let filePath of tempFilePaths) {
				try {
					uni.showLoading({ title: '上传中...' })
					const result = await this.uploadSingleImage(filePath)
					uni.hideLoading()

					if (result) {
						// 保存相对路径
						this.uploadedImages.push(result)
					}
				} catch (err) {
					uni.hideLoading()
					uni.showToast({
						title: '上传失败',
						icon: 'none'
					})
					console.error('上传图片失败:', err)
				}
			}
		},

		// 上传单张图片
		uploadSingleImage(filePath) {
			return new Promise((resolve, reject) => {
				uni.uploadFile({
					url: BASE_URL + '/common/upload',
					filePath: filePath,
					name: 'file',
					header: {
						'Authorization': uni.getStorageSync('token') ? `Bearer ${uni.getStorageSync('token')}` : ''
					},
					success: (res) => {
						if (res.statusCode === 200) {
							const data = JSON.parse(res.data)
							if (data.code === 200 && data.fileName) {
								resolve(data.fileName) // 返回相对路径
							} else {
								reject(new Error(data.msg || '上传失败'))
							}
						} else {
							reject(new Error('上传失败'))
						}
					},
					fail: (err) => {
						reject(err)
					}
				})
			})
		},

		// 删除图片
		handleDeleteImage(index) {
			this.uploadedImages.splice(index, 1)
		},

		// 提交
		async handleSubmit() {
			// 验证必填项
			if (!this.formData.phone) {
				uni.showToast({
					title: '请输入联系方式',
					icon: 'none'
				})
				return
			}

			// 验证手机号格式
			const phoneReg = /^1[3-9]\d{9}$/
			if (!phoneReg.test(this.formData.phone)) {
				uni.showToast({
					title: '请输入正确的手机号',
					icon: 'none'
				})
				return
			}

			// 验证标题
			if (!this.formData.title || !this.formData.title.trim()) {
				uni.showToast({
					title: '请输入投诉标题',
					icon: 'none'
				})
				return
			}

			// 验证内容
			if (!this.formData.content || !this.formData.content.trim()) {
				uni.showToast({
					title: '请输入投诉内容',
					icon: 'none'
				})
				return
			}

			// 防止重复提交
			if (this.uploading) {
				return
			}
			this.uploading = true

			try {
				uni.showLoading({ title: '提交中...' })

				// 准备提交数据
				const submitData = {
					title: this.formData.title.trim(),
					content: this.formData.content.trim(),
					contactPhone: this.formData.phone,
					images: this.uploadedImages.join(','), // 多个图片用逗号分隔
					userId: this.userId  // 添加用户ID
				}

				const res = await submitComplaint(submitData)
				uni.hideLoading()

				if (res.code === 200) {
					uni.showToast({
						title: '提交成功',
						icon: 'success'
					})
					setTimeout(() => {
						uni.navigateBack()
					}, 1500)
				} else {
					uni.showToast({
						title: res.msg || '提交失败',
						icon: 'none'
					})
				}
			} catch (err) {
				uni.hideLoading()
				uni.showToast({
					title: '提交失败',
					icon: 'none'
				})
				console.error('提交投诉失败:', err)
			} finally {
				this.uploading = false
			}
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

	/* 表单卡片 */
	.form-card {
		width: 702rpx;
		border-radius: 20rpx;
		opacity: 1;
		background: #ffffff;
		margin: 24rpx 24rpx 0 24rpx;
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

	.required-mark {
		color: #ff0000;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		margin-right: 4rpx;
	}

	.title-text {
		height: 45rpx;
		opacity: 1;
		color: #171a1f;
		font-size: 32rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 45rpx;
		flex-shrink: 0;
	}

	.title-input {
		flex: 1;
		height: 45rpx;
		color: #333333;
		font-size: 28rpx;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		margin-left: 54rpx;
	}

	.placeholder {
		color: #cccccc;
		font-size: 28rpx;
	}

	/* 文本域区域 */
	.textarea-wrapper {
		margin: 24rpx 28rpx 0 38rpx;
	}

	.content-textarea {
		width: 100%;
		height: 200rpx;
		color: #333333;
		font-size: 28rpx;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 40rpx;
		box-sizing: border-box;
	}

	/* 上传区域 */
	.upload-section {
		margin: 24rpx 28rpx 0 38rpx;
	}

	.upload-list {
		display: flex;
		flex-wrap: wrap;
		gap: 20rpx;
	}

	.upload-item {
		width: 166rpx;
		height: 166rpx;
		position: relative;
	}

	.uploaded-image {
		width: 100%;
		height: 100%;
		border-radius: 12rpx;
	}

	.delete-btn {
		position: absolute;
		top: -16rpx;
		right: -16rpx;
		width: 40rpx;
		height: 40rpx;
		border-radius: 50%;
		background: rgba(0, 0, 0, 0.5);
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.delete-icon {
		color: #ffffff;
		font-size: 28rpx;
		line-height: 1;
	}

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
		opacity: 1;
		color: #ffffff;
		font-size: 36rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
	}
</style>
