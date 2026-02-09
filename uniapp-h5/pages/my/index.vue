<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 顶部用户信息区域 -->
			<view class="header-section" @click="goToProfile">
				<image class="avatar" :src="userInfo.avatar || '/static/my/头像@2x.png'" mode="aspectFill"></image>
				<view class="user-info">
					<text class="nickname">{{ userInfo.nickname || '未设置昵称' }}</text>
					<text class="phone">{{ maskedPhone }}</text>
				</view>
				<image class="arrow" src="/static/my/youjiantou@2x.png"></image>
			</view>

			<!-- 菜单列表 -->
			<view class="menu-list">
				<view 
					class="menu-item" 
					v-for="(item, index) in menuList" 
					:key="index"
					@click="handleMenuClick(item.key)"
				>
					<image class="menu-icon" :src="item.icon"></image>
					<text class="menu-text">{{ item.name }}</text>
					<image class="menu-arrow" src="/static/my/youjiantou@2x.png"></image>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import { getUserInfo, maskPhone } from '@/api/user'
	import config from '@/config/index'
	import authCheck from '@/mixins/authCheck'

	export default {
		data() {
			return {
				userInfo: {
					nickname: '',
					phone: '',
					avatar: ''
				},
				maskedPhone: '134****3475',
				menuList: [
					{ key: 'message', name: '我的消息', icon: '/static/my/消息@2x.png' },
					{ key: 'listing', name: '我的房源', icon: '/static/my/房源@2x.png' },
					{ key: 'contract', name: '我的合同', icon: '/static/my/我的合同@2x.png' },
					{ key: 'appeal', name: '申诉记录', icon: '/static/my/申诉记录@2x.png' },
					{ key: 'maintenance', name: '信息维护', icon: '/static/my/信息维护@2x.png' },
					{ key: 'coupon', name: '优惠券', icon: '/static/my/优惠券@2x.png' },
					{ key: 'about', name: '关于我们', icon: '/static/my/我们@2x.png' }
				]
			}
		},
		onLoad() {
			// 使用统一的登录检查
			authCheck.checkLogin.call(this, {}, () => {
				this.loadUserInfo(this.userId)
			})
		},
		methods: {
			// 加载用户信息
			loadUserInfo(userId) {
				getUserInfo(userId).then(res => {
					if (res.code === 200 && res.data) {
						this.userInfo = res.data
						// 手机号脱敏
						this.maskedPhone = maskPhone(res.data.phone)
						// 处理头像URL
						this.processAvatar()
					}
				}).catch(err => {
					console.error('获取用户信息失败:', err)
				})
			},

			// 处理头像URL
			processAvatar() {
				if (!this.userInfo.avatar) {
					this.userInfo.avatar = '/static/my/头像@2x.png'
					return
				}
				// 如果是后端默认头像但文件不存在，使用前端默认头像
				if (this.userInfo.avatar === '/profile/avatar/default.jpg') {
					this.userInfo.avatar = '/static/my/头像@2x.png'
				} else if (!this.userInfo.avatar.startsWith('http')) {
					this.userInfo.avatar = config.staticUrl + this.userInfo.avatar
				}
			},

			goToProfile() {
				uni.navigateTo({
					url: '/pages/my/profile'
				})
			},
			handleMenuClick(key) {
				console.log('点击菜单:', key)
				if (key === 'message') {
					uni.navigateTo({
						url: '/pages/message/index'
					})
				} else if (key === 'listing') {
					uni.navigateTo({
						url: '/pages/my/listing'
					})
				} else if (key === 'contract') {
					uni.navigateTo({
						url: '/pages/affairs/contract'
					})
				} else if (key === 'appeal') {
					uni.navigateTo({
						url: '/pages/affairs/appeal'
					})
				} else if (key === 'maintenance') {
					uni.navigateTo({
						url: '/pages/my/maintenance'
					})
				} else if (key === 'coupon') {
					uni.navigateTo({
						url: '/pages/coupon/index'
					})
				} else if (key === 'about') {
					uni.navigateTo({
						url: '/pages/my/about'
					})
				}
			}
		}
	}
</script>

<style scoped>
	.page {
		width: 100%;
		background-color: #f5f6fc;
		display: flex;
		flex-direction: column;
	}

	.scroll-content {
		flex: 1;
		overflow-y: auto;
	}

	/* 顶部用户信息区域 */
	.header-section {
		width: 100%;
		display: flex;
		align-items: center;
		padding: 60rpx 54rpx 40rpx 44rpx;
		background-image: url('/static/my/个人中心背景@2x.png');
		background-size: 100% 100%;
		background-position: center;
		background-repeat: no-repeat;
		box-sizing: border-box;
	}

	.avatar {
		width: 128rpx;
		height: 128rpx;
		border-radius: 60rpx;
		margin-right: 32rpx;
		flex-shrink: 0;
		background-color: #e0e0e0;
	}

	.user-info {
		flex: 1;
		display: flex;
		flex-direction: column;
		gap: 8rpx;
		min-width: 0;
	}

	.nickname {
		max-width: 400rpx;
		height: 62rpx;
		opacity: 1;
		color: #333333;
		font-size: 44rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 62rpx;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.phone {
		width: 167rpx;
		height: 37rpx;
		opacity: 1;
		color: #777f95;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		letter-spacing: 1rpx;
		line-height: 37rpx;
	}

	.arrow {
		width: 32rpx;
		height: 32rpx;
		flex-shrink: 0;
		margin-left: auto;
	}

	/* 菜单列表 */
	.menu-list {
		background-color: #ffffff;
		margin: 0 24rpx;
		border-radius: 20rpx;
		overflow: hidden;
	}

	.menu-item {
		display: flex;
		align-items: center;
		padding: 30rpx 36rpx 30rpx 39rpx;
		border-bottom: 1rpx solid #f7f4f4;
		box-sizing: border-box;
	}

	.menu-item:last-child {
		border-bottom: none;
	}

	.menu-icon {
		width: 38rpx;
		height: 38rpx;
		margin-right: 31rpx;
	}

	.menu-text {
		width: 128rpx;
		height: 45rpx;
		opacity: 1;
		color: #3c3c3c;
		font-size: 32rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 45rpx;
	}

	.menu-arrow {
		width: 32rpx;
		height: 32rpx;
		flex-shrink: 0;
		margin-left: auto;
	}
</style>
