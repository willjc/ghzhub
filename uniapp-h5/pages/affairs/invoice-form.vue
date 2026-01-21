<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 开票信息卡片 -->
			<view class="card">
				<!-- 发票类型 -->
				<view class="form-section">
					<text class="form-title">发票类型</text>
					<view class="tag-row">
						<view class="tag tag-active">
							<text class="tag-text-active">电子普通发票</text>
						</view>
					</view>
				</view>
				
				<!-- 发票抬头 -->
				<view class="form-section1">
					<text class="form-title">发票抬头</text>
					<view class="tag-row">
						<view 
							class="tag" 
							:class="{ 'tag-active': headType === 'personal' }"
							@click="selectHeadType('personal')"
						>
							<text class="tag-text" :class="{ 'tag-text-active': headType === 'personal' }">个人</text>
						</view>
						<view 
							class="tag" 
							:class="{ 'tag-active': headType === 'company' }"
							@click="selectHeadType('company')"
						>
							<text class="tag-text" :class="{ 'tag-text-active': headType === 'company' }">企业</text>
						</view>
					</view>
				</view>
				
				<!-- 个人名称 -->
				<view class="form-row">
					<text class="form-label">个人名称</text>
					<input 
						class="form-input" 
						type="text" 
						v-model="formData.name" 
						placeholder="请输入您的姓名"
						placeholder-class="placeholder"
					/>
				</view>
				
				<!-- 手机号码 -->
				<view class="form-row">
					<text class="form-label">手机号码</text>
					<input 
						class="form-input" 
						type="number" 
						v-model="formData.phone" 
						placeholder="请输入联系人手机号码"
						placeholder-class="placeholder"
					/>
				</view>
				
				<!-- 邮箱地址 -->
				<view class="form-row">
					<text class="form-label">邮箱地址</text>
					<input 
						class="form-input" 
						type="text" 
						v-model="formData.email" 
						placeholder="请填写邮箱地址"
						placeholder-class="placeholder"
					/>
				</view>
			</view>
			
			<!-- 发票须知 -->
			<view class="notice-section">
				<text class="notice-title">发票须知：</text>
				<text class="notice-content">1. 目前可开具的发票类型为电子增值税普通发票
2. 开票金额为用户实际支付的金额
3. 电子发票会在申请后10个工作日发至收件人邮箱
4. 电子发票与纸质发票具有相同的法律效力，可作为报销、售后的凭证。</text>
			</view>

			<!-- 底部确定按钮 -->
		<view class="bottom-btn-container">
			<view class="bottom-btn" @click="handleSubmit">
				<text class="bottom-btn-text">确定</text>
			</view>
		</view>
		</scroll-view>
		
		
	</view>
</template>

<script>
	export default {
		data() {
			return {
				housingType: '',
				billId: '',
				headType: 'personal', // personal / company
				
				formData: {
					name: '',
					phone: '',
					email: ''
				}
			}
		},
		onLoad(options) {
			if (options.type) {
				this.housingType = options.type
			}
			if (options.billId) {
				this.billId = options.billId
			}
		},
		methods: {
			// 选择发票抬头类型
			selectHeadType(type) {
				this.headType = type
			},
			
			// 提交
			handleSubmit() {
				// 验证表单
				if (!this.formData.name) {
					uni.showToast({
						title: '请输入姓名',
						icon: 'none'
					})
					return
				}
				if (!this.formData.phone) {
					uni.showToast({
						title: '请输入手机号码',
						icon: 'none'
					})
					return
				}
				if (!this.formData.email) {
					uni.showToast({
						title: '请输入邮箱地址',
						icon: 'none'
					})
					return
				}
				
				uni.showLoading({
					title: '提交中...'
				})
				
				// TODO: 调用提交开票API
				setTimeout(() => {
					uni.hideLoading()
					uni.showToast({
						title: '申请成功',
						icon: 'success'
					})
					setTimeout(() => {
						uni.navigateBack({
							delta: 2
						})
					}, 1500)
				}, 1000)
			}
		}
	}
</script>

<style scoped>
	.page {
		width: 100%;
		min-height: 95vh;
		background-color: #f5f6fc;
		display: flex;
		flex-direction: column;
	}

	.scroll-content {
		flex: 1;
		padding: 24rpx;
		box-sizing: border-box;
	}

	/* 卡片 */
	.card {
		width: 702rpx;
		border-radius: 20rpx;
		background: #ffffff;
		padding: 32rpx 28rpx;
		box-sizing: border-box;
		margin-bottom: 360rpx;
	}

	/* 表单区块 */
	.form-section {
		margin-bottom: 40rpx;
	}
	.form-section1 {
		margin-bottom: 16rpx;
	}

	.form-title {
		height: 45rpx;
		color: #323233;
		font-size: 32rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 45rpx;
		margin-bottom: 32rpx;
		display: block;
	}

	.tag-row {
		display: flex;
		gap: 20rpx;
	}

	.tag {
		min-width: 120rpx;
		height: 58rpx;
		border-radius: 29rpx;
		border: 1rpx solid #e1eaf2;
		background: #ffffff;
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 0rpx 22rpx;
	}

	.tag-active {
		border: none;
		background: #1976f8;
	}

	.tag-text {
		color: #333333;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.tag-text-active {
		color: #ffffff;
	}

	/* 表单行 */
	.form-row {
		display: flex;
		align-items: center;
		padding: 24rpx 0;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.form-row:last-child {
		border-bottom: none;
	}

	.form-label {
		width: 156rpx;
		height: 40rpx;
		color: #333333;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 40rpx;
		flex-shrink: 0;
	}

	.form-input {
		flex: 1;
		height: 40rpx;
		color: #1a1a1a;
		font-size: 26rpx;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 40rpx;
	}

	.placeholder {
		color: #999999;
		font-size: 26rpx;
	}

	/* 发票须知 */
	.notice-section {
		padding: 0 16rpx;
	}

	.notice-title {
		height: 45rpx;
		color: #737373;
		font-size: 32rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 45rpx;
		margin-bottom: 16rpx;
		display: block;
	}

	.notice-content {
		color: #737373;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 40rpx;
	}

	/* 底部按钮 */
	.bottom-btn-container {
		
		padding: 34rpx 24rpx;
		background: #f5f6fc;
	}

	.bottom-btn {
		
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

