<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 个人信息卡片 -->
			<view class="card">
				<view class="card-header">
					<view class="card-indicator"></view>
					<text class="card-title">个人信息</text>
				</view>

				<view class="form-row">
					<text class="form-label"><text class="required">*</text>姓名</text>
					<view class="form-value-wrap">
						<input
							class="form-input"
							type="text"
							v-model="formData.name"
							placeholder="请输入姓名"
							placeholder-class="placeholder"
						/>
					</view>
				</view>

				<view class="form-row">
					<text class="form-label"><text class="required">*</text>身份证号</text>
					<view class="form-value-wrap">
						<text class="form-value readonly">{{ maskedIdCard }}</text>
					</view>
				</view>

				<view class="form-row">
					<text class="form-label"><text class="required">*</text>联系方式</text>
					<view class="form-value-wrap">
						<input
							class="form-input"
							type="number"
							v-model="formData.phone"
							placeholder="请输入联系方式"
							placeholder-class="placeholder"
						/>
					</view>
				</view>

				<view class="form-row">
					<text class="form-label"><text class="required">*</text>工作单位</text>
					<view class="form-value-wrap">
						<input
							class="form-input"
							type="text"
							v-model="formData.company"
							placeholder="请输入工作单位"
							placeholder-class="placeholder"
						/>
					</view>
				</view>

				<view class="form-row">
					<text class="form-label"><text class="required">*</text>单位联系电话</text>
					<view class="form-value-wrap">
						<input
							class="form-input"
							type="number"
							v-model="formData.companyPhone"
							placeholder="请输入单位联系电话"
							placeholder-class="placeholder"
						/>
					</view>
				</view>

				<view class="form-row">
					<text class="form-label"><text class="required">*</text>学历</text>
					<view class="form-value-wrap">
						<text class="form-value readonly">{{ educationText }}</text>
					</view>
				</view>

				<view class="form-row last-row">
					<text class="form-label"><text class="required">*</text>配偶</text>
					<view class="form-value-wrap">
						<input
							class="form-input"
							type="text"
							v-model="formData.spouse"
							placeholder="请输入配偶名字"
							placeholder-class="placeholder"
						/>
					</view>
				</view>
			</view>
		</scroll-view>

		<!-- 底部保存按钮 -->
		<view class="bottom-btn-container">
			<view class="bottom-btn" @click="handleSubmit">
				<text class="bottom-btn-text">保存</text>
			</view>
		</view>
	</view>
</template>

<script>
	import { getUserInfo, updateUser, getEducationLabel, maskIdCard } from '@/api/user'

	export default {
		data() {
			return {
				formData: {
					name: '',
					idCard: '',
					phone: '',
					company: '',
					companyPhone: '',
					education: '',
					spouse: ''
				}
			}
		},
		computed: {
			educationText() {
				return getEducationLabel(this.formData.education) || '未知'
			},
			maskedIdCard() {
				return maskIdCard(this.formData.idCard)
			}
		},
		onLoad() {
			this.loadUserInfo()
		},
		methods: {
			// 加载用户信息
			loadUserInfo() {
				const userId = uni.getStorageSync('userId') || 1
				getUserInfo(userId).then(res => {
					if (res.code === 200 && res.data) {
						const data = res.data
						this.formData = {
							name: data.realName || '',
							idCard: data.idCard || '',
							phone: data.contactPhone || '',
							company: data.workUnit || '',
							companyPhone: data.unitContact || '',
							education: data.education || '',
							spouse: data.spouseName || ''
						}
					}
				}).catch(err => {
					console.error('获取用户信息失败:', err)
				})
			},

			// 提交保存
			handleSubmit() {
				// 验证必填项
				if (!this.formData.name) {
					uni.showToast({ title: '请输入姓名', icon: 'none' })
					return
				}
				if (!this.formData.phone) {
					uni.showToast({ title: '请输入联系方式', icon: 'none' })
					return
				}
				if (!this.formData.company) {
					uni.showToast({ title: '请输入工作单位', icon: 'none' })
					return
				}
				if (!this.formData.companyPhone) {
					uni.showToast({ title: '请输入单位联系电话', icon: 'none' })
					return
				}
				if (!this.formData.spouse) {
					uni.showToast({ title: '请输入配偶名字', icon: 'none' })
					return
				}

				const userId = uni.getStorageSync('userId') || 1
				const saveData = {
					userId,
					realName: this.formData.name,
					contactPhone: this.formData.phone,
					workUnit: this.formData.company,
					unitContact: this.formData.companyPhone,
					spouseName: this.formData.spouse
				}

				uni.showLoading({ title: '保存中...' })
				updateUser(saveData).then(res => {
					uni.hideLoading()
					if (res.code === 200) {
						uni.showToast({ title: '保存成功', icon: 'success' })
						setTimeout(() => {
							uni.navigateBack()
						}, 1500)
					} else {
						uni.showToast({ title: res.msg || '保存失败', icon: 'none' })
					}
				}).catch(err => {
					uni.hideLoading()
					console.error('保存失败:', err)
					uni.showToast({ title: '保存失败', icon: 'none' })
				})
			}
		}
	}
</script>

<style scoped>
	.page {
		width: 100%;
		min-height: 100vh;
		background-color: #f5f6fc;
		display: flex;
		flex-direction: column;
	}

	.scroll-content {
		flex: 1;
		padding: 24rpx;
		padding-bottom: 160rpx;
		box-sizing: border-box;
	}

	.card {
		width: 702rpx;
		border-radius: 20rpx;
		background: #ffffff;
		padding: 26rpx 0 10rpx 0;
		margin: 0 auto 24rpx;
		box-sizing: border-box;
	}

	.card-header {
		display: flex;
		align-items: center;
		margin-bottom: 37rpx;
	}

	.card-indicator {
		width: 12rpx;
		height: 34rpx;
		background: #0f73ff;
		margin-right: 26rpx;
	}

	.card-title {
		color: #171a1f;
		font-size: 32rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.form-row {
		display: flex;
		align-items: center;
		margin: 0 40rpx;
		margin-bottom: 28rpx;
	}

	.last-row .form-value-wrap {
		border-bottom: none;
	}

	.form-label {
		width: 220rpx;
		color: #333333;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		flex-shrink: 0;
		padding-bottom: 28rpx;
	}

	.required {
		color: #ff0000;
		margin-right: 4rpx;
	}

	.form-value-wrap {
		flex: 1;
		display: flex;
		align-items: center;
		padding-bottom: 28rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.form-input {
		flex: 1;
		color: #1a1a1a;
		font-size: 26rpx;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.form-value {
		flex: 1;
		color: #1a1a1a;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.form-value.readonly {
		color: #999999;
	}

	.placeholder {
		color: #999999;
	}

	.arrow-icon {
		width: 24rpx;
		height: 24rpx;
		flex-shrink: 0;
	}

	/* 底部按钮 */
	.bottom-btn-container {
		position: fixed;
		bottom: 24rpx;
		left: 0;
		right: 0;
		padding: 0 24rpx;
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
</style>

