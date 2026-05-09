<template>
	<view class="notice-mask" v-if="visible" @click="onMaskClick">
		<view class="notice-box" @click.stop>
			<!-- 顶部背景区：复用首页个人信息弹窗的风格 -->
			<view class="notice-head">
				<image class="notice-head-bg" src="/static/弹窗bg@2x.png" mode="aspectFill"></image>
				<text class="notice-head-title">{{ title || '友情提醒' }}</text>
				<!-- 右上角关闭按钮 -->
				<view class="notice-close" @click="close">
					<text class="notice-close-icon">×</text>
				</view>
			</view>
			<!-- 内容区：rich-text 渲染公告 HTML -->
			<scroll-view class="notice-body" scroll-y>
				<rich-text class="notice-rich" :nodes="htmlContent"></rich-text>
			</scroll-view>
			<!-- 底部按钮 -->
			<view class="notice-foot">
				<view class="notice-btn" @click="close">
					<text class="notice-btn-text">我知道了</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		name: 'notice-popup',
		props: {
			visible: { type: Boolean, default: false },
			title: { type: String, default: '' },
			content: { type: String, default: '' },
			// 点击遮罩是否允许关闭
			maskClosable: { type: Boolean, default: true }
		},
		computed: {
			// rich-text 对空串会渲染 "null"，做一层保护
			htmlContent() {
				return this.content || ''
			}
		},
		methods: {
			close() {
				this.$emit('close')
				this.$emit('update:visible', false)
			},
			onMaskClick() {
				if (this.maskClosable) {
					this.close()
				}
			}
		}
	}
</script>

<style scoped>
	.notice-mask {
		position: fixed;
		top: 0; left: 0; right: 0; bottom: 0;
		background-color: rgba(0, 0, 0, 0.5);
		display: flex;
		align-items: center;
		justify-content: center;
		z-index: 999;
	}
	.notice-box {
		width: 640rpx;
		max-height: 842rpx;
		border-radius: 32rpx;
		background: #ffffff;
		display: flex;
		flex-direction: column;
		overflow: hidden;
		position: relative;
	}
	.notice-head {
		width: 640rpx;
		height: 186rpx;
		border-radius: 32rpx 32rpx 0 0;
		position: relative;
		overflow: hidden;
		display: flex;
		justify-content: flex-start;
		padding-left: 30rpx;
	}
	.notice-head-bg {
		position: absolute;
		top: 0; left: 0;
		width: 100%; height: 100%;
	}
	.notice-head-title {
		color: #000000;
		font-size: 36rpx;
		font-weight: 500;
		padding-top: 42rpx;
		padding-left: 10rpx;
		line-height: 51rpx;
		position: relative;
		z-index: 1;
	}
	.notice-close {
		position: absolute;
		top: 16rpx;
		right: 20rpx;
		width: 56rpx;
		height: 56rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		z-index: 2;
	}
	.notice-close-icon {
		color: #333333;
		font-size: 44rpx;
		line-height: 44rpx;
	}
	.notice-body {
		flex: 1;
		padding: 20rpx 36rpx 20rpx;
		box-sizing: border-box;
		margin-top: -60rpx;
		position: relative;
		z-index: 1;
		max-height: 520rpx;
	}
	.notice-rich {
		color: #333333;
		font-size: 28rpx;
		line-height: 44rpx;
		word-break: break-all;
	}
	.notice-foot {
		padding: 20rpx 0 36rpx;
		display: flex;
		justify-content: center;
	}
	.notice-btn {
		width: 550rpx;
		height: 90rpx;
		border-radius: 20rpx;
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
		display: flex;
		align-items: center;
		justify-content: center;
	}
	.notice-btn-text {
		color: #ffffff;
		font-size: 32rpx;
		font-weight: 500;
	}
</style>
