<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 内容区域 -->
			<view class="content-wrapper">
				<!-- 标题 -->
				<text class="detail-title">{{ detailData.activityTitle }}</text>

				<!-- 活动信息 -->
				<view class="info-section">
					<view class="info-item" v-if="detailData.activityLocation">
						<text class="info-label">活动地点：</text>
						<text class="info-value">{{ detailData.activityLocation }}</text>
					</view>
					<view class="info-item" v-if="detailData.registrationTime">
						<text class="info-label">报名时间：</text>
						<text class="info-value">{{ detailData.registrationTime }}</text>
					</view>
					<view class="info-item" v-if="detailData.activityTime">
						<text class="info-label">活动时间：</text>
						<text class="info-value">{{ detailData.activityTime }}</text>
					</view>
					<view class="info-item" v-if="detailData.organizer">
						<text class="info-label">主办单位：</text>
						<text class="info-value">{{ detailData.organizer }}</text>
					</view>
					<view class="info-item" v-if="detailData.maxParticipants">
						<text class="info-label">报名人数：</text>
						<text class="info-value">{{ detailData.currentParticipants || 0 }} / {{ detailData.maxParticipants }}人</text>
					</view>
				</view>

				<!-- 正文内容 -->
				<view class="content-divider"></view>
				<rich-text class="detail-content" :nodes="detailData.activityContent"></rich-text>
			</view>

			<!-- 底部占位，避免内容被固定按钮遮挡 -->
			<view class="bottom-placeholder"></view>
		</scroll-view>

		<!-- 底部固定报名按钮 -->
		<view class="bottom-action" v-if="detailData.activityTitle">
			<button v-if="registrationStatus === 'not_started'" class="btn-register" disabled>报名未开始</button>
			<button v-else-if="registrationStatus === 'registered'" class="btn-registered" disabled>已报名</button>
			<button v-else-if="registrationStatus === 'full'" class="btn-register" disabled>报名已满</button>
			<button v-else-if="registrationStatus === 'ended'" class="btn-register" disabled>报名已结束</button>
			<button v-else-if="registering" class="btn-register-active" disabled>报名中...</button>
			<button v-else class="btn-register-active" @click="handleRegister">立即报名</button>
		</view>
	</view>
</template>

<script>
	import config from '@/config/index'
	import { registerActivity, checkRegistered } from '@/api/activity'

	export default {
		data() {
			return {
				activityId: null,
				detailData: {
					activityTitle: '',
					coverImage: '',
					activityLocation: '',
					registrationTime: '',
					activityTime: '',
					organizer: '',
					maxParticipants: null,
					currentParticipants: 0,
					activityContent: '',
					// 原始时间字段，用于报名状态判断
					registrationStartTime: null,
					registrationEndTime: null
				},
				isRegistered: false,
				registering: false
			}
		},
		computed: {
			registrationStatus() {
				if (!this.detailData.activityTitle) return 'loading'
				if (this.isRegistered) return 'registered'

				const now = new Date().getTime()

				// 检查报名时间
				if (this.detailData.registrationStartTime) {
					const regStart = new Date(this.detailData.registrationStartTime).getTime()
					if (now < regStart) return 'not_started'
				}
				if (this.detailData.registrationEndTime) {
					const regEnd = new Date(this.detailData.registrationEndTime).getTime()
					if (now > regEnd) return 'ended'
				}

				// 检查人数限制
				if (this.detailData.maxParticipants && this.detailData.maxParticipants > 0) {
					if ((this.detailData.currentParticipants || 0) >= this.detailData.maxParticipants) {
						return 'full'
					}
				}

				return 'available'
			}
		},
		onLoad(options) {
			// 获取活动ID
			if (options.id) {
				this.activityId = options.id;
				this.loadDetail(options.id);
			}
		},
		methods: {
			/** 加载活动详情 */
			loadDetail(id) {
				uni.request({
					url: config.baseUrl + '/h5/activity/' + id,
					method: 'GET',
					success: (res) => {
						if (res.data.code === 200) {
							const data = res.data.data;

							// 格式化报名时间
							let registrationTime = '';
							if (data.registrationStartTime && data.registrationEndTime) {
								const startDate = this.formatDate(data.registrationStartTime);
								const endDate = this.formatDate(data.registrationEndTime);
								registrationTime = `${startDate} 至 ${endDate}`;
							}

							// 格式化活动时间
							let activityTime = '';
							if (data.activityStartTime && data.activityEndTime) {
								const startDateTime = this.formatDateTime(data.activityStartTime);
								const endTime = this.formatTime(data.activityEndTime);
								activityTime = `${startDateTime} - ${endTime}`;
							}

							this.detailData = {
								activityTitle: data.activityTitle,
								coverImage: data.coverImage,
								activityLocation: data.activityLocation,
								registrationTime: registrationTime,
								activityTime: activityTime,
								organizer: data.organizer,
								maxParticipants: data.maxParticipants,
								currentParticipants: data.currentParticipants,
								activityContent: this.processHtmlContent(data.activityContent),
								// 保留原始时间用于报名状态判断
								registrationStartTime: data.registrationStartTime,
								registrationEndTime: data.registrationEndTime
							};

							// 加载详情后检查报名状态
							this.checkRegistrationStatus();
						} else {
							uni.showToast({
								title: res.data.msg || '加载失败',
								icon: 'none'
							});
						}
					},
					fail: (err) => {
						console.error('请求失败:', err);
						uni.showToast({
							title: '网络请求失败',
							icon: 'none'
						});
					}
				});
			},

			/** 检查用户是否已报名 */
			async checkRegistrationStatus() {
				try {
					const userInfo = uni.getStorageSync('userInfo')
					if (!userInfo || !userInfo.userId) return // 未登录不检查
					const res = await checkRegistered(this.activityId, userInfo.userId)
					this.isRegistered = res.registered === true
				} catch (e) {
					console.log('检查报名状态失败', e)
				}
			},

			/** 报名操作 */
			async handleRegister() {
				const userInfo = uni.getStorageSync('userInfo')
				const token = uni.getStorageSync('token')
				if (!token || !userInfo || !userInfo.userId) {
					uni.showModal({
						title: '提示',
						content: '请先登录后再报名',
						confirmText: '去登录',
						success(res) {
							if (res.confirm) {
								uni.navigateTo({ url: '/pages/login/index' })
							}
						}
					})
					return
				}

				this.registering = true
				try {
					const res = await registerActivity({
						activityId: this.activityId,
						userId: userInfo.userId,
						realName: userInfo.realName || userInfo.nickName || null,
						phone: userInfo.phonenumber || userInfo.phone || null
					})
					if (res.code === 200) {
						uni.showToast({ title: '报名成功', icon: 'success' })
						this.isRegistered = true
						// 更新当前参与人数
						if (this.detailData.currentParticipants !== undefined) {
							this.detailData.currentParticipants++
						}
					} else {
						uni.showToast({ title: res.msg || '报名失败', icon: 'none' })
					}
				} catch (e) {
					// request 工具已经有统一 toast，这里不重复提示
					console.log('报名失败', e)
				} finally {
					this.registering = false
				}
			},

			/** 获取图片完整URL */
			getImageUrl(url) {
				if (!url) return '';

				// 外部链接(http/https开头)，直接返回
				if (url.startsWith('http://') || url.startsWith('https://')) {
					return url;
				}

				// 拼接后端baseUrl
				const baseUrl = config.staticUrl;
				return baseUrl + (url.startsWith('/') ? url : '/' + url);
			},

			/** 格式化日期 YYYY-MM-DD */
			formatDate(dateStr) {
				if (!dateStr) return '';
				const date = new Date(dateStr);
				const year = date.getFullYear();
				const month = String(date.getMonth() + 1).padStart(2, '0');
				const day = String(date.getDate()).padStart(2, '0');
				return `${year}-${month}-${day}`;
			},

			/** 格式化日期时间 YYYY-MM-DD HH:mm */
			formatDateTime(dateStr) {
				if (!dateStr) return '';
				const date = new Date(dateStr);
				const datepart = this.formatDate(dateStr);
				const hour = String(date.getHours()).padStart(2, '0');
				const minute = String(date.getMinutes()).padStart(2, '0');
				return `${datepart} ${hour}:${minute}`;
			},

			/** 格式化时间 HH:mm */
			formatTime(dateStr) {
				if (!dateStr) return '';
				const date = new Date(dateStr);
				const hour = String(date.getHours()).padStart(2, '0');
				const minute = String(date.getMinutes()).padStart(2, '0');
				return `${hour}:${minute}`;
			},

			/** 处理HTML内容，替换图片路径并适配图片显示 */
			processHtmlContent(html) {
				if (!html) return '';

				// 替换 /dev-api 前缀为完整URL
				const baseUrl = config.staticUrl;
				let processedHtml = html.replace(/src="\/dev-api/g, `src="${baseUrl}`);

				// 同时处理可能存在的相对路径图片
				processedHtml = processedHtml.replace(/src="\/profile/g, `src="${baseUrl}/profile`);

				// 给所有 img 标签添加内联样式，确保图片自适应屏幕宽度
				processedHtml = processedHtml.replace(/<img([^>]*?)style="([^"]*?)"([^>]*?)>/gi, (match, before, style, after) => {
					// 移除原有的 width/height 样式，添加自适应样式
					let newStyle = style.replace(/width\s*:\s*[^;]+;?/gi, '').replace(/height\s*:\s*[^;]+;?/gi, '');
					newStyle = newStyle + ';max-width:100%!important;height:auto!important;display:block;';
					return `<img${before}style="${newStyle}"${after}>`;
				});

				// 处理没有 style 属性的 img 标签
				processedHtml = processedHtml.replace(/<img((?![^>]*style=)[^>]*?)>/gi, (match, attrs) => {
					return `<img${attrs} style="max-width:100%!important;height:auto!important;display:block;">`;
				});

				return processedHtml;
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
		min-height: 100vh;
	}

	.scroll-content {
		flex: 1;
		overflow-y: auto;
	}

	.content-wrapper {
		padding: 24rpx;
		box-sizing: border-box;
		background-color: #ffffff;
	}

	.detail-title {
		min-height: 100rpx;
		opacity: 1;
		color: #1a1a1a;
		font-size: 40rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 50rpx;
		display: block;
		margin-bottom: 30rpx;
		word-wrap: break-word;
	}

	.info-section {
		margin-bottom: 20rpx;
	}

	.info-item {
		display: flex;
		align-items: flex-start;
		margin-bottom: 16rpx;
	}

	.info-label {
		color: #999999;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 36rpx;
		flex-shrink: 0;
	}

	.info-value {
		color: #666666;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 36rpx;
		flex: 1;
	}

	.content-divider {
		width: 100%;
		height: 1rpx;
		background-color: #e5e5e5;
		margin: 20rpx 0 30rpx;
	}

	.detail-content {
		opacity: 1;
		color: #333333;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
		display: block;
		word-wrap: break-word;
	}

	/* rich-text 内部元素样式 */
	.detail-content >>> img,
	/deep/ .detail-content img {
		max-width: 100% !important;
		height: auto !important;
		display: block;
		margin: 10rpx 0;
	}

	.detail-content >>> p,
	/deep/ .detail-content p {
		margin: 10rpx 0;
		line-height: 40rpx;
	}

	/* 底部占位 */
	.bottom-placeholder {
		height: 140rpx;
	}

	/* 底部固定报名按钮 */
	.bottom-action {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		padding: 16rpx 30rpx;
		padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
		background: #fff;
		box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
		z-index: 99;
	}

	.btn-register {
		width: 100%;
		height: 88rpx;
		line-height: 88rpx;
		text-align: center;
		border-radius: 44rpx;
		font-size: 32rpx;
		color: #999;
		background: #f5f5f5;
		border: none;
	}

	.btn-register-active {
		width: 100%;
		height: 88rpx;
		line-height: 88rpx;
		text-align: center;
		border-radius: 44rpx;
		font-size: 32rpx;
		color: #fff;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		border: none;
	}

	.btn-registered {
		width: 100%;
		height: 88rpx;
		line-height: 88rpx;
		text-align: center;
		border-radius: 44rpx;
		font-size: 32rpx;
		color: #fff;
		background: #67c23a;
		border: none;
	}
</style>
