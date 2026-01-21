<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 顶部用户信息区域 -->
			<view class="header-section" @click="goToProfile">
				<image class="avatar" src="/static/my/头像@2x.png"></image>
				<view class="user-info">
					<text class="nickname">这是昵称</text>
					<text class="phone">134****3475</text>
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
	export default {
		data() {
			return {
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
			
		},
		methods: {
			goToProfile() {
				console.log('跳转到个人资料')
			},
			handleMenuClick(key) {
				console.log('点击菜单:', key)
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
	}

	.user-info {
		flex: 1;
		display: flex;
		flex-direction: column;
		gap: 8rpx;
	}

	.nickname {
		width: 176rpx;
		height: 62rpx;
		opacity: 1;
		color: #333333;
		font-size: 44rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 62rpx;
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
