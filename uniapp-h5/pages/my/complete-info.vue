<template>
	<view class="page">
		<!-- 自定义导航栏（无返回按钮） -->
		<view class="custom-nav">
			<view class="nav-status-bar"></view>
			<view class="nav-title-bar">
				<text class="nav-title">完善个人信息</text>
			</view>
		</view>

		<view class="content">
			<view class="header-card">
				<text class="header-title">完善个人信息</text>
				<text class="header-desc">请填写以下信息，以便使用平台服务</text>
			</view>

			<view class="form-card">
				<!-- 身份类型 -->
				<view class="form-group required">
					<text class="form-label">身份类型</text>
					<view class="radio-group">
						<view
							v-for="item in identityTypes"
							:key="item.value"
							class="radio-item"
							:class="{ active: form.identityType === item.value }"
							@click="form.identityType = item.value"
						>
							<text>{{ item.label }}</text>
						</view>
					</view>
				</view>

				<!-- 真实姓名 -->
				<view class="form-group required">
					<text class="form-label">真实姓名</text>
					<input class="form-input" v-model="form.realName" placeholder="请输入真实姓名" />
				</view>

				<!-- 身份证号 -->
				<view class="form-group required">
					<text class="form-label">身份证号</text>
					<input class="form-input" v-model="form.idCard" placeholder="请输入身份证号" maxlength="18" />
				</view>

				<!-- 联系电话 -->
				<view class="form-group required">
					<text class="form-label">联系电话</text>
					<input class="form-input" v-model="form.contactPhone" placeholder="请输入联系电话" type="number" maxlength="11" />
				</view>

				<!-- 工作单位 -->
				<view class="form-group required">
					<text class="form-label">工作单位</text>
					<input class="form-input" v-model="form.workUnit" placeholder="请输入工作单位" />
				</view>

				<!-- 单位联系电话 -->
				<view class="form-group required">
					<text class="form-label">单位联系电话</text>
					<input class="form-input" v-model="form.unitContact" placeholder="请输入单位联系电话" />
				</view>

				<!-- 配偶 -->
				<view class="form-group">
					<text class="form-label">配偶姓名</text>
					<input class="form-input" v-model="form.spouseName" placeholder="请输入配偶姓名（选填）" />
				</view>
			</view>

			<!-- 提交按钮 -->
			<view class="submit-btn" :class="{ disabled: submitting }" @click="handleSubmit">
				<text class="submit-btn-text">{{ submitting ? '提交中...' : '提交' }}</text>
			</view>
		</view>
	</view>
</template>

<script>
	import { updateUserInfo } from '@/api/auth'

	export default {
		data() {
			return {
				userId: null,
				submitting: false,
				identityTypes: [
					{ label: '在职人员', value: '1' },
					{ label: '应届毕业生', value: '2' }
				],
				form: {
					identityType: '',
					realName: '',
					idCard: '',
					contactPhone: '',
					workUnit: '',
					unitContact: '',
					spouseName: ''
				}
			}
		},
		onLoad() {
			const userInfo = uni.getStorageSync('userInfo')
			const token = uni.getStorageSync('token')
			if (!userInfo || !userInfo.userId || !token) {
				uni.reLaunch({ url: '/pages/login/index' })
				return
			}
			this.userId = userInfo.userId
			if (userInfo.phone) {
				this.form.contactPhone = userInfo.phone
			}
		},
		methods: {
			validate() {
				if (!this.form.identityType) {
					uni.showToast({ title: '请选择身份类型', icon: 'none' })
					return false
				}
				if (!this.form.realName.trim()) {
					uni.showToast({ title: '请输入真实姓名', icon: 'none' })
					return false
				}
				if (!this.form.idCard.trim()) {
					uni.showToast({ title: '请输入身份证号', icon: 'none' })
					return false
				}
				const idCardReg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
				if (!idCardReg.test(this.form.idCard.trim())) {
					uni.showToast({ title: '身份证号格式不正确', icon: 'none' })
					return false
				}
				if (!this.form.contactPhone.trim()) {
					uni.showToast({ title: '请输入联系电话', icon: 'none' })
					return false
				}
				if (!this.form.workUnit.trim()) {
					uni.showToast({ title: '请输入工作单位', icon: 'none' })
					return false
				}
				if (!this.form.unitContact.trim()) {
					uni.showToast({ title: '请输入单位联系电话', icon: 'none' })
					return false
				}
				return true
			},

			async handleSubmit() {
				if (this.submitting) return
				if (!this.validate()) return

				this.submitting = true
				try {
					await updateUserInfo({
						userId: this.userId,
						identityType: this.form.identityType,
						realName: this.form.realName.trim(),
						idCard: this.form.idCard.trim(),
						contactPhone: this.form.contactPhone.trim(),
						workUnit: this.form.workUnit.trim(),
						unitContact: this.form.unitContact.trim(),
						spouseName: this.form.spouseName.trim()
					})

					const userInfo = uni.getStorageSync('userInfo') || {}
					userInfo.isInfoCompleted = '1'
					userInfo.identityType = this.form.identityType
					userInfo.realName = this.form.realName.trim()
					userInfo.idCard = this.form.idCard.trim()
					userInfo.contactPhone = this.form.contactPhone.trim()
					userInfo.workUnit = this.form.workUnit.trim()
					userInfo.unitContact = this.form.unitContact.trim()
					userInfo.spouseName = this.form.spouseName.trim()
					uni.setStorageSync('userInfo', userInfo)

					uni.showModal({
						title: '信息提交成功',
						content: '是否立即进行实名认证？完成实名认证后可使用更多平台功能。',
						confirmText: '去认证',
						cancelText: '稍后再说',
						success: (res) => {
							if (res.confirm) {
								uni.navigateTo({ url: '/pages/auth/verify' })
							} else {
								uni.reLaunch({ url: '/pages/index/index' })
							}
						}
					})

				} catch (error) {
					console.error('提交失败:', error)
					uni.showToast({
						title: error.msg || '提交失败',
						icon: 'none'
					})
				} finally {
					this.submitting = false
				}
			}
		}
	}
</script>

<style scoped>
	.page {
		width: 100%;
		min-height: 100vh;
		background: #f5f6fc;
		box-sizing: border-box;
	}

	.custom-nav {
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
	}

	.nav-status-bar {
		height: var(--status-bar-height, 44px);
	}

	.nav-title-bar {
		height: 88rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.nav-title {
		font-size: 34rpx;
		font-weight: 500;
		color: #ffffff;
	}

	.content {
		padding: 24rpx;
	}

	.header-card {
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
		border-radius: 20rpx;
		padding: 40rpx 32rpx;
		margin-bottom: 24rpx;
	}

	.header-title {
		display: block;
		font-size: 40rpx;
		font-weight: 700;
		color: #ffffff;
		margin-bottom: 12rpx;
	}

	.header-desc {
		display: block;
		font-size: 26rpx;
		color: rgba(255, 255, 255, 0.85);
	}

	.form-card {
		background: #ffffff;
		border-radius: 20rpx;
		padding: 32rpx;
		margin-bottom: 40rpx;
	}

	.form-group {
		margin-bottom: 32rpx;
	}

	.form-group:last-child {
		margin-bottom: 0;
	}

	.form-group.required .form-label::before {
		content: '*';
		color: #e5252b;
		margin-right: 6rpx;
	}

	.form-label {
		display: block;
		font-size: 28rpx;
		color: #333333;
		font-weight: 500;
		margin-bottom: 16rpx;
	}

	.form-input {
		width: 100%;
		height: 88rpx;
		background: #f5f6fc;
		border-radius: 12rpx;
		padding: 0 24rpx;
		font-size: 28rpx;
		color: #333333;
		box-sizing: border-box;
	}

	.radio-group {
		display: flex;
		gap: 20rpx;
	}

	.radio-item {
		flex: 1;
		height: 88rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		background: #f5f6fc;
		border-radius: 12rpx;
		font-size: 28rpx;
		color: #666666;
		border: 2rpx solid transparent;
	}

	.radio-item.active {
		background: #e6f0ff;
		color: #4A90E2;
		border-color: #4A90E2;
		font-weight: 500;
	}

	.submit-btn {
		width: 100%;
		height: 96rpx;
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
		border-radius: 48rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.submit-btn.disabled {
		opacity: 0.6;
	}

	.submit-btn-text {
		font-size: 32rpx;
		font-weight: 500;
		color: #ffffff;
	}
</style>
