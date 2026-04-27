<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<view class="content-wrapper">
				<text class="detail-title">隐私政策</text>
				<view class="content-divider"></view>
				<view class="detail-content">
					<text class="section-title">一、信息收集范围</text>
					<text class="section-text">为向您提供服务，我们可能会收集以下信息：
1. 个人基本信息：手机号；
2. 账号信息：微信OpenID、手机号等；
3. 使用信息：浏览记录、操作日志、设备信息等；
4. 房源相关信息：入住记录、合同信息、缴费记录等。</text>

					<text class="section-title">二、信息使用目的</text>
					<text class="section-text">我们收集您的信息仅用于：
1. 为您提供人才公寓信息查询、在线签约、入住办理等服务；
2. 进行身份核验和资格审核；
3. 发送服务通知、账单提醒等必要信息；
4. 保障账号安全，防范欺诈风险。</text>

					<text class="section-title">三、信息共享</text>
					<text class="section-text">我们不会将您的个人信息出售给任何第三方。仅在以下情形下可能会共享：
1. 获得您的明确同意；
2. 根据法律法规要求或政府部门要求；
3. 与关联公司共享以提供服务所必需的信息。</text>

					<text class="section-title">四、信息保护</text>
					<text class="section-text">我们采取合理的技术和管理措施保护您的个人信息安全，防止信息泄露、丢失或被滥用。但请您理解，互联网传输无法保证绝对安全。</text>

					<text class="section-title">五、信息存储</text>
					<text class="section-text">您的个人信息将存储于中华人民共和国境内。我们仅在为实现服务目的所必需的期限内保留您的信息，超出期限后将删除或匿名化处理。</text>

					<text class="section-title">六、用户权利</text>
					<text class="section-text">您有权：
1. 查阅、更正您的个人信息；
2. 删除您的个人信息；
3. 撤回对我们处理您个人信息的同意；
4. 注销账号（请联系客服处理）。</text>

					<text class="section-title">七、Cookie与同类技术</text>
					<text class="section-text">我们可能会使用Cookie等技术收集您的使用信息，以优化服务体验。您可以通过浏览器设置管理Cookie。</text>

					<text class="section-title">八、未成年人保护</text>
					<text class="section-text">本平台主要面向成年人提供服务。如您为未成年人，请在监护人指导下使用，并由监护人代为提供相关信息。</text>

					<text class="section-title">九、政策更新</text>
					<text class="section-text">我们可能会不时更新本隐私政策，更新后的政策将在平台上公布。如您继续使用服务，视为接受更新后的政策。</text>

					<text class="section-title">十、联系我们</text>
					<text class="section-text">如您对本隐私政策有任何疑问，请通过平台客服渠道联系我们。</text>

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
