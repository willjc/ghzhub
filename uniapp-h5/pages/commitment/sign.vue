<template>
	<view class="page">
		<!-- 标题 -->
		<view class="title-card">
			<text class="title-text">{{ commitmentData.templateName || '承诺书' }}</text>
		</view>

		<!-- 承诺书内容卡片 -->
		<view class="content-card">
			<view class="content-scroll">
				<rich-text :nodes="commitmentData.commitmentContent"></rich-text>
			</view>
		</view>

		<!-- 底部操作区 -->
		<view class="bottom-area">
			<view class="agree-row">
				<checkbox-group @change="onAgreeChange">
					<label class="checkbox-label">
						<checkbox value="agree" :checked="agreed" color="#0f73ff"/>
						<text class="agree-text">我已阅读并同意以上承诺内容</text>
					</label>
				</checkbox-group>
			</view>

			<view class="sign-btn" :class="{ disabled: !canSign }" @click="handleSign">
				<text class="btn-text">立即签字</text>
			</view>
		</view>
	</view>
</template>

<script>
import { getCommitmentContent, signCommitment } from '@/api/commitment'

export default {
	data() {
		return {
			projectId: '',
			tenantId: null, // 从登录信息获取
			agreed: false,
			signatureData: '',
			commitmentData: {
				templateId: null,
				commitmentType: '',
				commitmentContent: '',
				templateName: '',
				projectName: ''
			}
		}
	},
	computed: {
		canSign() {
			return this.agreed && this.commitmentData.commitmentContent
		}
	},
	onLoad(options) {
		// 从本地存储获取登录用户信息
		const userInfo = uni.getStorageSync('userInfo');
		if (!userInfo || !userInfo.userId) {
			uni.showToast({
				title: '请先登录',
				icon: 'none'
			});
			setTimeout(() => {
				uni.navigateTo({
					url: '/pages/login/index'
				});
			}, 1500);
			return;
		}

		this.tenantId = userInfo.userId;

		if (options.projectId) {
			this.projectId = options.projectId
			this.loadCommitmentContent()
		} else {
			uni.showToast({
				title: '缺少项目ID',
				icon: 'none'
			})
			setTimeout(() => {
				uni.navigateBack()
			}, 1500)
		}
	},
	methods: {
		async loadCommitmentContent() {
			try {
				uni.showLoading({ title: '加载中...' })
				const response = await getCommitmentContent(this.projectId)

				if (response.code === 200 && response.data) {
					this.commitmentData = response.data
					console.log('承诺书内容加载成功:', this.commitmentData)
				} else {
					throw new Error(response.msg || '获取承诺书内容失败')
				}
			} catch (error) {
				console.error('加载承诺书内容失败:', error)
				uni.showToast({
					title: error.message || '加载失败',
					icon: 'none'
				})
			} finally {
				uni.hideLoading()
			}
		},

		onAgreeChange(e) {
			this.agreed = e.detail.value.includes('agree')
		},

		handleSign() {
			if (!this.canSign) {
				if (!this.agreed) {
					uni.showToast({
						title: '请先同意承诺内容',
						icon: 'none'
					})
				}
				return
			}

			// 跳转到签名页面
			uni.navigateTo({
				url: '/pages/signature/index',
				events: {
					// 监听签名页面返回的数据
					acceptSignature: (data) => {
						this.signatureData = data.signature
						console.log('收到签名数据')
						// 自动提交承诺书
						this.submitCommitment()
					}
				}
			})
		},

		async submitCommitment() {
			try {
				uni.showLoading({ title: '提交中...' })

				const response = await signCommitment({
					projectId: this.projectId,
					tenantId: this.tenantId,
					commitmentType: this.commitmentData.commitmentType,
					commitmentContent: this.commitmentData.commitmentContent,
					signatureData: this.signatureData
				})

				uni.hideLoading()

				if (response.code === 200) {
					uni.showToast({
						title: '签署成功',
						icon: 'success'
					})

					// 跳转到选房页面
					setTimeout(() => {
						uni.redirectTo({
							url: `/pages/room/select?projectId=${this.projectId}`
						})
					}, 1500)
				} else {
					throw new Error(response.msg || '签署失败')
				}
			} catch (error) {
				uni.hideLoading()
				console.error('提交承诺书失败:', error)
				uni.showToast({
					title: error.message || '提交失败',
					icon: 'none'
				})
			}
		}
	}
}
</script>

<style scoped>
.page {
	min-height: 100vh;
	background: #f5f6fc;
	padding-bottom: 200rpx;
}

/* 标题卡片 */
.title-card {
	background: #ffffff;
	padding: 30rpx;
	border-bottom: 1rpx solid #e1eaf2;
}

.title-text {
	font-size: 36rpx;
	font-weight: 500;
	color: #1a1a1a;
	text-align: center;
	display: block;
}

/* 内容卡片 */
.content-card {
	background: #ffffff;
	margin: 24rpx;
	border-radius: 20rpx;
	padding: 30rpx;
}

.content-scroll {
	line-height: 1.8;
	font-size: 28rpx;
	color: #333333;
}

/* 底部操作区 */
.bottom-area {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	background: #ffffff;
	padding: 24rpx;
	box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.agree-row {
	margin-bottom: 20rpx;
}

.checkbox-label {
	display: flex;
	align-items: center;
}

.agree-text {
	font-size: 26rpx;
	color: #666666;
	margin-left: 12rpx;
}

.sign-btn {
	width: 100%;
	height: 92rpx;
	border-radius: 20rpx;
	background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
	display: flex;
	align-items: center;
	justify-content: center;
}

.sign-btn.disabled {
	background: #cccccc;
}

.btn-text {
	font-size: 32rpx;
	font-weight: 500;
	color: #ffffff;
}
</style>
