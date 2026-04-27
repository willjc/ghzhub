<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<view class="content-wrapper">
				<text class="detail-title">用户服务协议</text>
				<view class="content-divider"></view>
				<view class="detail-content">
					<text class="section-title">一、协议接受</text>
					<text class="section-text">欢迎使用港好住平台。本协议是您与郑州航空港区人才公寓服务平台之间就使用本服务所达成的协议。请您仔细阅读本协议，如您不同意本协议任何内容，请勿使用本服务。</text>

					<text class="section-title">二、服务内容</text>
					<text class="section-text">港好住为用户提供人才公寓信息展示、在线签约、入住办理、账单缴纳、退租申请及相关配套服务。具体服务内容以平台实际提供为准。</text>

					<text class="section-title">三、用户义务</text>
					<text class="section-text">1. 您应当提供真实、准确、完整的个人信息，并及时更新。
2. 您应妥善保管账号及密码，对账号下的所有行为承担法律责任。
3. 您不得利用本服务从事违法违规活动。</text>

					<text class="section-title">四、账号安全</text>
					<text class="section-text">您理解并同意，因您保管不善导致账号密码泄露、被他人使用，或您未及时修改密码导致账号被盗用的，本平台不承担相应责任。</text>

					<text class="section-title">五、知识产权</text>
					<text class="section-text">本平台的所有内容，包括但不限于文字、图片、图标、标识、软件等，均受知识产权法律法规保护。未经本平台书面许可，任何单位或个人不得擅自使用。</text>

					<text class="section-title">六、免责声明</text>
					<text class="section-text">1. 本平台会尽力确保服务的稳定性和安全性，但不对服务的连续性、准确性、及时性作绝对保证。
2. 因不可抗力、系统维护、网络故障等原因导致服务中断的，本平台不承担责任。</text>

					<text class="section-title">七、协议修改</text>
					<text class="section-text">本平台有权根据业务需要随时修改本协议内容，修改后的协议将在平台上公布，公布后即时生效。您继续使用本服务视为接受修改后的协议。</text>

					<text class="section-title">八、其他</text>
					<text class="section-text">本协议适用中华人民共和国法律。如双方就本协议内容发生争议，应友好协商解决；协商不成的，任何一方均可向本平台所在地人民法院提起诉讼。</text>

					<text class="date-text">发布日期：2026年4月23日</text>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	export default {
		data() {
			return {}
		}
	}
</script>

<style scoped>
	.page {
		min-height: 100vh;
		background: #f8f9fa;
	}

	.scroll-content {
		height: 100vh;
	}

	.content-wrapper {
		padding: 40rpx;
		background: #ffffff;
		margin: 20rpx;
		border-radius: 16rpx;
	}

	.detail-title {
		font-size: 40rpx;
		font-weight: bold;
		color: #333333;
		display: block;
		text-align: center;
		margin-bottom: 30rpx;
	}

	.content-divider {
		height: 2rpx;
		background: #eeeeee;
		margin-bottom: 30rpx;
	}

	.detail-content {
		font-size: 30rpx;
		color: #666666;
		line-height: 52rpx;
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333333;
		display: block;
		margin-top: 30rpx;
		margin-bottom: 16rpx;
	}

	.section-text {
		display: block;
		margin-bottom: 16rpx;
		white-space: pre-wrap;
	}

	.date-text {
		display: block;
		margin-top: 40rpx;
		font-size: 28rpx;
		color: #999999;
		text-align: right;
	}
</style>
