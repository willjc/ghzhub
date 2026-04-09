<template>
	<!-- #ifdef MP-WEIXIN -->
	<web-view :src="url" @message="onMessage"></web-view>
	<!-- #endif -->

	<!-- #ifdef H5 -->
	<view class="page">
		<text>此页面仅在微信小程序中使用</text>
	</view>
	<!-- #endif -->
</template>

<script>
	export default {
		data() {
			return {
				url: ''
			}
		},
		onLoad(options) {
			if (options.url) {
				this.url = decodeURIComponent(options.url)
			}
		},
		methods: {
			onMessage(e) {
				console.log('webview message:', e.detail)
			}
		}
	}
</script>

<style scoped>
	.page {
		display: flex;
		align-items: center;
		justify-content: center;
		min-height: 100vh;
	}
</style>
