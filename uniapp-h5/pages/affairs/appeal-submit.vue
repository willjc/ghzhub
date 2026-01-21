<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 申诉项卡片 -->
			<view class="card card-appeal-item">
				<view class="card-indicator"></view>
				<text class="card-label">申诉项</text>
				<text class="card-value">学历</text>
			</view>

			<!-- 申诉材料卡片 -->
			<view class="card card-material">
				<view class="card-header-material">
					<view class="card-indicator"></view>
					<text class="card-label">申诉材料</text>
				</view>

				<!-- 学历选择 -->
				<view class="form-row" @click="openEducationPicker">
					<view class="form-label">
						<text class="required">*</text>
						<text class="label-text">学历</text>
					</view>
					<view class="form-value">
						<text class="value-text" :class="{ 'placeholder': !educationText }">{{ educationText || '请选择' }}</text>
						<image class="arrow-icon" src="/static/向右1@2x.png"></image>
					</view>
				</view>

				<!-- 附件上传 -->
				<view class="form-row attachment-row">
					<view class="form-label">
						<text class="required">*</text>
						<text class="label-text">附件</text>
					</view>
				</view>

				<!-- 图片上传区域 -->
				<view class="upload-area">
					<!-- 已上传的图片 -->
					<view class="image-item" v-for="(img, index) in uploadedImages" :key="index">
						<image class="uploaded-image" :src="img" mode="aspectFill"></image>
						<view class="delete-btn" @click="deleteImage(index)">
							<image class="delete-icon" src="/static/icon-删除图片@2x.png"></image>
						</view>
					</view>

					<!-- 上传按钮 -->
					<view class="upload-btn" v-if="uploadedImages.length < 9" @click="chooseImage">
						<image class="upload-icon" src="/static/上传@2x.png"></image>
						<text class="upload-text">点击上传</text>
					</view>
				</view>

				<!-- 提示文字 -->
				<text class="upload-tip">请上传相关证明材料，支持图片格式，最多9张</text>
			</view>
		</scroll-view>

		<!-- 底部提交按钮 -->
		<view class="bottom-btn-container">
			<view class="bottom-btn" @click="handleSubmit">
				<text class="bottom-btn-text">提交申诉</text>
			</view>
		</view>

		<!-- 学历选择弹窗 -->
		<view class="picker-mask" v-if="showEducationPicker" @click="showEducationPicker = false"></view>
		<view class="picker-popup" :class="{ 'show': showEducationPicker }">
			<view class="picker-header">
				<text class="picker-cancel" @click="showEducationPicker = false">取消</text>
				<text class="picker-confirm" @click="confirmEducation">确定</text>
			</view>
			<view class="picker-body">
				<picker-view
					class="picker-view"
					:value="pickerValue"
					@change="onPickerChange"
					indicator-style="height: 80rpx;"
				>
					<picker-view-column>
						<view
							class="picker-item"
							v-for="(item, index) in educationOptions"
							:key="index"
						>
							<text class="picker-item-text">{{ item.label }}</text>
						</view>
					</picker-view-column>
				</picker-view>
			</view>
		</view>
	</view>
</template>

<script>
	import config from '@/config/index'

	export default {
		data() {
			return {
				housingType: '',
				appealId: '',

				// 学历
				education: '',
				pickerValue: [2], // 默认选中硕士（索引2）
				showEducationPicker: false,
				educationOptions: [
					{ label: '大专', value: '4' },
					{ label: '本科', value: '5' },
					{ label: '硕士', value: '6' },
					{ label: '博士', value: '7' }
				],

				// 上传的图片
				uploadedImages: []
			}
		},
		computed: {
			educationText() {
				const item = this.educationOptions.find(o => o.value === this.education)
				return item ? item.label : ''
			}
		},
		onLoad(options) {
			if (options.type) {
				this.housingType = options.type
			}
			if (options.id) {
				this.appealId = options.id
			}
		},
		methods: {
			// 打开学历选择弹窗
			openEducationPicker() {
				// 如果已有选择，定位到对应索引
				if (this.education) {
					const index = this.educationOptions.findIndex(o => o.value === this.education)
					if (index >= 0) {
						this.pickerValue = [index]
					}
				} else {
					// 默认选中硕士（索引2）
					this.pickerValue = [2]
				}
				this.showEducationPicker = true
			},

			// picker滚动变化
			onPickerChange(e) {
				this.pickerValue = e.detail.value
			},

			// 确认学历选择
			confirmEducation() {
				const index = this.pickerValue[0]
				this.education = this.educationOptions[index].value
				this.showEducationPicker = false
			},

			// 选择图片
			chooseImage() {
				const remainCount = 9 - this.uploadedImages.length
				uni.chooseImage({
					count: remainCount,
					sizeType: ['compressed'],
					sourceType: ['album', 'camera'],
					success: (res) => {
						this.uploadedImages = [...this.uploadedImages, ...res.tempFilePaths]
						if (this.uploadedImages.length > 9) {
							this.uploadedImages = this.uploadedImages.slice(0, 9)
						}
					}
				})
			},

			// 删除图片
			deleteImage(index) {
				this.uploadedImages.splice(index, 1)
			},

			// 提交申诉
			handleSubmit() {
				// 表单验证
				if (!this.education) {
					uni.showToast({
						title: '请选择学历',
						icon: 'none'
					})
					return
				}

				if (this.uploadedImages.length === 0) {
					uni.showToast({
						title: '请上传证明材料',
						icon: 'none'
					})
					return
				}

				uni.showLoading({
					title: '提交中...'
				})

				console.log('开始上传图片，共', this.uploadedImages.length, '张')
				console.log('上传地址:', config.uploadUrl + '/common/upload')

				// 批量上传图片
				const uploadPromises = this.uploadedImages.map((tempFilePath, index) => {
					return new Promise((resolve, reject) => {
						console.log(`正在上传第${index + 1}张图片:`, tempFilePath)

						uni.uploadFile({
							url: config.uploadUrl + '/common/upload',  // 使用配置文件中的URL
							filePath: tempFilePath,
							name: 'file',
							header: {
								'Authorization': uni.getStorageSync('token') || ''
							},
							success: (uploadFileRes) => {
								console.log(`第${index + 1}张图片上传响应:`, uploadFileRes)

								try {
									const data = JSON.parse(uploadFileRes.data)
									console.log(`第${index + 1}张图片解析结果:`, data)

									if (data.code === 200) {
										console.log(`第${index + 1}张图片上传成功:`, data.fileName)
										resolve(data.fileName)  // 返回相对路径
									} else {
										console.error(`第${index + 1}张图片上传失败:`, data.msg)
										reject(data.msg)
									}
								} catch (e) {
									console.error(`第${index + 1}张图片响应解析失败:`, e)
									reject('响应解析失败')
								}
							},
							fail: (err) => {
								console.error(`第${index + 1}张图片上传失败:`, err)
								reject(err)
							}
						})
					})
				})

				// 所有图片上传完成后提交申诉
				Promise.all(uploadPromises).then(fileNames => {
					console.log('所有图片上传成功，文件名列表:', fileNames)

					// 提交申诉数据
					const appealData = {
						appealReason: this.education,  // 申诉的学历
						appealAttachments: fileNames.join(',')  // 图片路径，逗号分隔
					}

					console.log('提交申诉数据:', appealData)
					console.log('申诉接口地址:', config.uploadUrl + '/h5/qualification/appeal')

					// 调用后端API
					uni.request({
						url: config.uploadUrl + '/h5/qualification/appeal',
						method: 'POST',
						data: appealData,
						header: {
							'Content-Type': 'application/json',
							'Authorization': uni.getStorageSync('token') || ''
						},
						success: (res) => {
							console.log('申诉提交响应:', res)

							uni.hideLoading()
							if (res.data.code === 200) {
								uni.showToast({
									title: '提交成功',
									icon: 'success'
								})
								setTimeout(() => {
									uni.navigateBack()
								}, 1500)
							} else {
								console.error('申诉提交失败:', res.data.msg)
								uni.showToast({
									title: res.data.msg || '提交失败',
									icon: 'none'
								})
							}
						},
						fail: (err) => {
							console.error('申诉提交网络错误:', err)
							uni.hideLoading()
							uni.showToast({
								title: '网络错误，请重试',
								icon: 'none'
							})
						}
					})
				}).catch(err => {
					console.error('图片上传失败:', err)
					uni.hideLoading()
					uni.showToast({
						title: typeof err === 'string' ? err : '图片上传失败',
						icon: 'none'
					})
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
	}

	.scroll-content {
		flex: 1;
		padding: 24rpx;
		padding-bottom: 140rpx;
		box-sizing: border-box;
	}

	.card {
		width: 702rpx;
		border-radius: 20rpx;
		background: #ffffff;
		margin: 0rpx 0 24rpx 0;
		box-sizing: border-box;
	}

	.card-appeal-item {
		height: 98rpx;
		display: flex;
		align-items: center;

	}

	.card-indicator {
		width: 12rpx;
		height: 36rpx;
		background: #1281ff;

		margin-right: 26rpx;
	}

	.card-label {
		color: #171a1f;
		font-size: 32rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.card-value {
		margin-left: 52rpx;
		color: #333333;
		font-size: 34rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: right;
	}

	.card-material {
		padding: 26rpx 0;
	}

	.card-header-material {
		display: flex;
		align-items: center;
		margin-bottom: 24rpx;
	}

	.form-row {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin:8rpx 41rpx;
		padding:16rpx 0;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.attachment-row {
		border-bottom: none;
		padding-bottom: 16rpx;
	}

	.form-label {
		display: flex;
		align-items: center;
	}

	.required {
		color: #ff0000;
		font-size: 28rpx;
		margin-right: 4rpx;
	}

	.label-text {
		color: #333333;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.form-value {
		display: flex;
		align-items: center;
	}

	.value-text {
		color: #333333;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.value-text.placeholder {
		color: #999999;
	}

	.arrow-icon {
		width: 24rpx;
		height: 24rpx;
		margin-left: 8rpx;
	}

	/* 图片上传区域 */
	.upload-area {
		display: flex;
		flex-wrap: wrap;
		gap: 16rpx;
		margin: 8rpx 41rpx 24rpx 41rpx;
	}

	.image-item {
		height: 166rpx;
		border-radius: 12rpx;
		position: relative;
	}

	.uploaded-image {
		width: 166rpx;
		height: 166rpx;
		border-radius: 12rpx;
		border: 2rpx solid #cdced5;
	}

	.delete-btn {
		position: absolute;
		top: 0rpx;
		right: 0rpx;
		width: 40rpx;
		height: 40rpx;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.delete-icon {
		width: 40rpx;
		height: 40rpx;
	}

	.upload-btn {
		width: 166rpx;
		height: 166rpx;
		border-radius: 12rpx;
		border: 2rpx solid #cdced5;
		background: #fafbff;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}

	.upload-icon {
		width: 38rpx;
		height: 38rpx;
		margin-bottom: 12rpx;
	}

	.upload-text {
		color: #cecfd7;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.upload-tip {
		color: #999999;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;

		display: block;
		margin: 26rpx 41rpx 0rpx 41rpx;
	}

	/* 底部按钮 */
	.bottom-btn-container {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		padding: 24rpx;
		background: #f5f6fc;
	}

	.bottom-btn {
		width: 702rpx;
		height: 92rpx;
		border-radius: 20rpx;
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
		display: flex;
		align-items: center;
		justify-content: center;
		margin: 0 auto;
	}

	.bottom-btn-text {
		color: #ffffff;
		font-size: 36rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	/* Picker弹窗样式 */
	.picker-mask {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.5);
		z-index: 998;
	}

	.picker-popup {
		position: fixed;
		left: 0;
		right: 0;
		bottom: 0;
		background: #ffffff;
		border-radius: 24rpx 24rpx 0 0;
		z-index: 999;
		transform: translateY(100%);
		transition: transform 0.3s ease;
	}

	.picker-popup.show {
		transform: translateY(0);
	}

	.picker-header {
		width: 750rpx;
		height: 95rpx;
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 0 32rpx;
		box-sizing: border-box;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.picker-cancel {
		color: rgba(0, 0, 0, 0.6);
		font-size: 32rpx;
		font-weight: 400;
		font-family: "Roboto", "PingFang SC", sans-serif;
		line-height: 48rpx;
	}

	.picker-confirm {
		color: #1281ff;
		font-size: 32rpx;
		font-weight: 400;
		font-family: "Roboto", "PingFang SC", sans-serif;
		line-height: 48rpx;
	}

	.picker-body {
		width: 750rpx;
		height: 321rpx;
		background: #ffffff;
	}

	.picker-view {
		width: 100%;
		height: 100%;
	}

	.picker-item {
		height: 80rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.picker-item-text {
		color: #000000;
		font-size: 32rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 80rpx;
	}
</style>

