<template>
	<view>
		<!-- 信息完善弹框 -->
		<uni-popup ref="popup" type="dialog" :maskClick="false">
			<view class="dialog-container">
				<view class="dialog-header">
					<text class="dialog-title">完善个人信息</text>
					<text class="dialog-subtitle">首次登录需要完善个人信息</text>
				</view>

				<view class="dialog-body">
					<!-- 身份类型 -->
					<view class="form-item required">
						<text class="label">身份类型</text>
						<view class="radio-group">
							<view
								v-for="item in identityTypes"
								:key="item.value"
								class="radio-item"
								:class="{ active: form.identityType === item.value }"
								@click="selectIdentityType(item.value)"
							>
								<text>{{ item.label }}</text>
							</view>
						</view>
					</view>

					<!-- 真实姓名 -->
					<view class="form-item required">
						<text class="label">真实姓名</text>
						<input
							class="input"
							v-model="form.realName"
							placeholder="请输入真实姓名"
							placeholder-class="placeholder"
						/>
					</view>

					<!-- 身份证号 -->
					<view class="form-item required">
						<text class="label">身份证号</text>
						<input
							class="input"
							v-model="form.idCard"
							placeholder="请输入身份证号"
							placeholder-class="placeholder"
							maxlength="18"
						/>
					</view>

					<!-- 联系电话 -->
					<view class="form-item required">
						<text class="label">联系电话</text>
						<input
							class="input"
							v-model="form.phone"
							placeholder="请输入联系电话"
							placeholder-class="placeholder"
							type="number"
							maxlength="11"
							disabled
						/>
					</view>

					<!-- 工作单位 -->
					<view class="form-item required">
						<text class="label">工作单位</text>
						<input
							class="input"
							v-model="form.workUnit"
							placeholder="请输入工作单位"
							placeholder-class="placeholder"
						/>
					</view>

					<!-- 单位联系方式 -->
					<view class="form-item">
						<text class="label">单位联系方式</text>
						<input
							class="input"
							v-model="form.unitContact"
							placeholder="请输入单位联系方式（选填）"
							placeholder-class="placeholder"
						/>
					</view>

					<!-- 配偶姓名 -->
					<view class="form-item">
						<text class="label">配偶姓名</text>
						<input
							class="input"
							v-model="form.spouseName"
							placeholder="请输入配偶姓名（选填）"
							placeholder-class="placeholder"
						/>
					</view>
				</view>

				<view class="dialog-footer">
					<view class="btn-submit" @click="handleSubmit">
						<text>提交</text>
					</view>
				</view>
			</view>
		</uni-popup>
	</view>
</template>

<script>
import { updateUserInfo } from '@/api/auth'

export default {
	name: 'UserInfoDialog',
	data() {
		return {
			identityTypes: [
				{ label: '在职人员', value: '1' },
				{ label: '应届毕业生', value: '2' }
			],
			form: {
				identityType: '',
				realName: '',
				idCard: '',
				phone: '',
				workUnit: '',
				unitContact: '',
				spouseName: ''
			}
		}
	},
	methods: {
		/**
		 * 显示弹框
		 */
		show(userInfo) {
			// 填充手机号
			if (userInfo && userInfo.phone) {
				this.form.phone = userInfo.phone
			}
			this.$refs.popup.open()
		},

		/**
		 * 隐藏弹框
		 */
		hide() {
			this.$refs.popup.close()
		},

		/**
		 * 选择身份类型
		 */
		selectIdentityType(value) {
			this.form.identityType = value
		},

		/**
		 * 表单验证
		 */
		validate() {
			if (!this.form.identityType) {
				uni.showToast({ title: '请选择身份类型', icon: 'none' })
				return false
			}
			if (!this.form.realName) {
				uni.showToast({ title: '请输入真实姓名', icon: 'none' })
				return false
			}
			if (!this.form.idCard) {
				uni.showToast({ title: '请输入身份证号', icon: 'none' })
				return false
			}
			// 身份证号格式验证
			const idCardReg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
			if (!idCardReg.test(this.form.idCard)) {
				uni.showToast({ title: '身份证号格式不正确', icon: 'none' })
				return false
			}
			if (!this.form.workUnit) {
				uni.showToast({ title: '请输入工作单位', icon: 'none' })
				return false
			}
			return true
		},

		/**
		 * 提交表单
		 */
		async handleSubmit() {
			if (!this.validate()) {
				return
			}

			try {
				uni.showLoading({ title: '提交中...' })

				// 调用更新接口
				await updateUserInfo(this.form)

				uni.hideLoading()
				uni.showToast({
					title: '信息完善成功',
					icon: 'success'
				})

				// 更新本地用户信息
				const userInfo = uni.getStorageSync('userInfo') || {}
				uni.setStorageSync('userInfo', {
					...userInfo,
					...this.form,
					isInfoCompleted: '1'
				})

				// 延迟关闭弹框
				setTimeout(() => {
					this.hide()
					// 触发完成事件
					this.$emit('completed')
				}, 1000)
			} catch (error) {
				uni.hideLoading()
				console.error('信息完善失败:', error)
				uni.showToast({
					title: error.msg || '提交失败',
					icon: 'none'
				})
			}
		}
	}
}
</script>

<style scoped>
.dialog-container {
	width: 650rpx;
	background: #ffffff;
	border-radius: 24rpx;
	overflow: hidden;
}

.dialog-header {
	padding: 40rpx 30rpx 30rpx;
	text-align: center;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.dialog-title {
	display: block;
	font-size: 36rpx;
	font-weight: 500;
	color: #ffffff;
	margin-bottom: 12rpx;
}

.dialog-subtitle {
	display: block;
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.8);
}

.dialog-body {
	max-height: 700rpx;
	overflow-y: auto;
	padding: 30rpx;
}

.form-item {
	margin-bottom: 30rpx;
}

.form-item.required .label::before {
	content: '*';
	color: #e5252b;
	margin-right: 6rpx;
}

.label {
	display: block;
	font-size: 28rpx;
	color: #333333;
	margin-bottom: 16rpx;
	font-weight: 500;
}

.input {
	width: 100%;
	height: 80rpx;
	background: #f5f6fc;
	border-radius: 12rpx;
	padding: 0 20rpx;
	font-size: 28rpx;
	color: #333333;
	box-sizing: border-box;
}

.input[disabled] {
	background: #f0f0f0;
	color: #999999;
}

.placeholder {
	color: #999999;
	font-size: 28rpx;
}

.radio-group {
	display: flex;
	gap: 20rpx;
}

.radio-item {
	flex: 1;
	height: 80rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	background: #f5f6fc;
	border-radius: 12rpx;
	font-size: 28rpx;
	color: #666666;
	transition: all 0.3s;
	border: 2rpx solid transparent;
}

.radio-item.active {
	background: #e6f0ff;
	color: #4A90E2;
	border-color: #4A90E2;
	font-weight: 500;
}

.dialog-footer {
	padding: 20rpx 30rpx 30rpx;
}

.btn-submit {
	width: 100%;
	height: 88rpx;
	background: linear-gradient(135deg, #4A90E2 0%, #357ABD 100%);
	border-radius: 44rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 32rpx;
	font-weight: 500;
	color: #ffffff;
	box-shadow: 0 8rpx 20rpx rgba(74, 144, 226, 0.3);
}

.btn-submit:active {
	transform: scale(0.98);
	opacity: 0.8;
}
</style>
