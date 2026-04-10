<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<view class="card">
				<!-- 头像 -->
				<view class="form-row1" @click="changeAvatar">
					<text class="form-label">头像</text>
					<view class="form-value-wrap">
						<image class="avatar" :src="displayAvatar" mode="aspectFill"></image>
						<image class="arrow-icon" src="/static/向右1@2x.png" mode="aspectFit"></image>
					</view>
				</view>

				<!-- 昵称 -->
				<view class="form-row" @click="editNickname">
					<text class="form-label">昵称</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ userInfo.nickname || '未设置' }}</text>
						<image class="arrow-icon" src="/static/向右1@2x.png" mode="aspectFit"></image>
					</view>
				</view>

				<!-- 姓名 -->
				<view class="form-row" @click="editRealName">
					<text class="form-label">姓名</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ userInfo.realName || '未设置' }}</text>
						<image class="arrow-icon" src="/static/向右1@2x.png" mode="aspectFit"></image>
					</view>
				</view>

				<!-- 性别 -->
				<view class="form-row" @click="showGenderPicker = true">
					<text class="form-label">性别</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ genderText }}</text>
						<image class="arrow-icon" src="/static/向右1@2x.png" mode="aspectFit"></image>
					</view>
				</view>

				<!-- 身份证号 -->
				<view class="form-row">
					<text class="form-label">身份证号</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ maskedIdCard }}</text>
					</view>
				</view>

				<!-- 学历 -->
				<view class="form-row">
					<text class="form-label">学历</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ educationText }}</text>
					</view>
				</view>

				<!-- 实名认证 -->
				<view class="form-row" v-if="authStatus !== '2'" @click="goToAuth">
					<text class="form-label">实名认证</text>
					<view class="form-value-wrap">
						<text class="form-value auth-value">去认证</text>
						<image class="arrow-icon" src="/static/向右1@2x.png" mode="aspectFit"></image>
					</view>
				</view>
				<view class="form-row last-row" v-else>
					<text class="form-label">实名认证</text>
					<view class="form-value-wrap">
						<text class="form-value verified-text">已认证</text>
					</view>
				</view>
			</view>
		</scroll-view>

		<!-- 性别选择器 -->
		<view class="picker-mask" v-if="showGenderPicker" @click="showGenderPicker = false"></view>
		<view class="picker-popup" v-if="showGenderPicker" :class="{ 'show': showGenderPicker }">
			<view class="picker-header">
				<text class="picker-cancel" @click="showGenderPicker = false">取消</text>
				<text class="picker-title">选择性别</text>
				<text class="picker-confirm" @click="confirmGender">确定</text>
			</view>
			<view class="picker-body">
				<picker-view :value="genderPickerValue" @change="onGenderChange" class="picker-view">
					<picker-view-column>
						<view class="picker-item" v-for="(item, index) in genderOptions" :key="index">
							<text class="picker-item-text">{{ item.label }}</text>
						</view>
					</picker-view-column>
				</picker-view>
			</view>
		</view>
	</view>
</template>

<script>
	import { getUserInfo, updateUser, uploadAvatar, getEducationLabel, getGenderLabel, maskIdCard } from '@/api/user'
	import { post } from '@/utils/request'
	import config from '@/config/index'
	import authCheck from '@/mixins/authCheck'

	export default {
		data() {
			return {
				userInfo: {
					avatar: '',
					nickname: '',
					realName: '',
					gender: '0',
					idCard: '',
					education: ''
				},
				authStatus: '0',
				showGenderPicker: false,
				genderPickerValue: [0],
				genderOptions: [
					{ label: '男', value: '0' },
					{ label: '女', value: '1' }
				]
			}
		},
		computed: {
			displayAvatar() {
				if (!this.userInfo.avatar) return '/static/my/头像@2x.png'
				// 如果是后端默认头像，使用前端默认头像
				if (this.userInfo.avatar === '/profile/avatar/default.jpg') return '/static/my/头像@2x.png'
				if (this.userInfo.avatar.startsWith('http')) return this.userInfo.avatar
				return config.staticUrl + this.userInfo.avatar
			},
			genderText() {
				return getGenderLabel(this.userInfo.gender)
			},
			educationText() {
				return getEducationLabel(this.userInfo.education)
			},
			maskedIdCard() {
				return maskIdCard(this.userInfo.idCard)
			}
		},
		onLoad() {
			authCheck.checkLogin.call(this, {}, () => {
				this.loadUserInfo()
			})
		},
		onShow() {
			// 从认证页返回时刷新数据（onLoad 不会重新触发）
			if (this.userId) {
				this.loadUserInfo()
			}
		},
		methods: {
			// 加载用户信息
			loadUserInfo() {
				getUserInfo(this.userId).then(res => {
					if (res.code === 200 && res.data) {
						this.userInfo = res.data
						this.authStatus = res.data.authStatus || '0'
					}
				}).catch(err => {
					console.error('获取用户信息失败:', err)
				})
			},

			// 更换头像
			changeAvatar() {
				uni.chooseImage({
					count: 1,
					sizeType: ['compressed'],
					sourceType: ['album', 'camera'],
					success: (res) => {
						const tempFilePath = res.tempFilePaths[0]
						const userId = uni.getStorageSync('userId') || 1

						uni.showLoading({ title: '上传中...' })
						uploadAvatar(tempFilePath, userId).then(uploadRes => {
							uni.hideLoading()
							if (uploadRes.code === 200) {
								this.userInfo.avatar = uploadRes.fileName
								uni.showToast({ title: '头像上传成功', icon: 'success' })
							}
						}).catch(err => {
							uni.hideLoading()
							console.error('头像上传失败:', err)
						})
					}
				})
			},

			// 编辑昵称
			editNickname() {
				uni.showModal({
					title: '修改昵称',
					editable: true,
					placeholderText: '请输入昵称',
					content: this.userInfo.nickname,
					success: (res) => {
						if (res.confirm && res.content) {
							this.saveUserInfo({ nickname: res.content })
						}
					}
				})
			},

			// 编辑姓名
			editRealName() {
				uni.showModal({
					title: '修改姓名',
					editable: true,
					placeholderText: '请输入真实姓名',
					content: this.userInfo.realName,
					success: (res) => {
						if (res.confirm && res.content) {
							this.saveUserInfo({ realName: res.content })
						}
					}
				})
			},

			// 性别选择变化
			onGenderChange(e) {
				this.genderPickerValue = e.detail.value
			},

			// 确认性别选择
			confirmGender() {
				const index = this.genderPickerValue[0]
				const gender = this.genderOptions[index].value
				this.showGenderPicker = false
				this.saveUserInfo({ gender })
			},

			// 实名认证
			goToAuth() {
				const userInfo = uni.getStorageSync('userInfo')
				if (!userInfo || !userInfo.userId) return

				uni.showLoading({ title: '获取认证链接...' })
				post('/h5/esign/auth-url', {
					userId: userInfo.userId,
					realName: this.userInfo.realName || '',
					idCard: this.userInfo.idCard || ''
				}).then(res => {
					uni.hideLoading()
					if (res.code === 200 && res.data) {
						if (res.data.needAuth && res.data.authUrl) {
							window.location.href = res.data.authUrl
						} else {
							uni.showToast({ title: '已完成实名认证', icon: 'success' })
							this.authStatus = '2'
						}
					} else {
						uni.showToast({ title: res.msg || '获取认证链接失败', icon: 'none' })
					}
				}).catch(() => {
					uni.hideLoading()
					uni.showToast({ title: '网络异常', icon: 'none' })
				})
			},

			// 实名认证 - 跳转认证页
			goToAuth() {
				uni.navigateTo({ url: "/pages/auth/verify" })
			},

			// 保存用户信息
			saveUserInfo(data) {
				const userId = uni.getStorageSync('userId') || 1
				uni.showLoading({ title: '保存中...' })
				updateUser({ userId, ...data }).then(res => {
					uni.hideLoading()
					if (res.code === 200) {
						// 更新本地数据
						Object.assign(this.userInfo, data)
						uni.showToast({ title: '保存成功', icon: 'success' })
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
		box-sizing: border-box;
	}

	.card {
		width: 702rpx;
		border-radius: 20rpx;
		background: #ffffff;
		margin: 0 auto;
		box-sizing: border-box;
	}

	.form-row {
		display: flex;
		align-items: center;
		padding: 50rpx 0rpx;
		margin: 0 28rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}
	.form-row1 {
		display: flex;
		align-items: center;
		padding: 32rpx 0rpx;
		margin: 0 28rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.last-row {
		border-bottom: none;
	}

	.form-label {
		width: 140rpx;
		color: #333333;
		font-size: 32rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.form-value-wrap {
		flex: 1;
		display: flex;
		align-items: center;
		justify-content: flex-end;
	}

	.form-value {
		color: #999999;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: right;
		margin-right: 30rpx;
	}
	.form-value1 {
		color: #999999;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: right;
	}

	.avatar {
		width: 94rpx;
		height: 94rpx;
		border-radius: 50%;
		margin-right: 30rpx;
	}

	.arrow-icon {
		width: 24rpx;
		height: 24rpx;
		flex-shrink: 0;
	}

		.auth-value {
			color: #4A90E2 !important;
		}
		.verified-text {
			color: #12a566 !important;
		}
</style>