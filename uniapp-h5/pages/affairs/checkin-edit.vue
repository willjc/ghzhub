<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 入住信息卡片 -->
			<view class="card">
				<view class="card-header">
					<view class="card-indicator"></view>
					<text class="card-title">入住信息</text>
				</view>
				
				<view class="form-row">
					<text class="form-label">小区名称</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ formData.community }}</text>
					</view>
				</view>
				
				<view class="form-row">
					<text class="form-label">房间地址</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ formData.room }}</text>
					</view>
				</view>
				
				<view class="form-row">
					<text class="form-label">入住日期</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ formData.checkinDate }}</text>
					</view>
				</view>
				
				<view class="form-row">
					<text class="form-label">申请人</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ formData.applicant }}</text>
					</view>
				</view>
				
				<view class="form-row">
					<text class="form-label">申请时间</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ formData.applyTime }}</text>
					</view>
				</view>
				
				<view class="form-row">
					<text class="form-label">审核状态</text>
					<view class="form-value-wrap">
						<text class="form-value status-pending">{{ formData.statusText }}</text>
					</view>
				</view>
			</view>
			
			<!-- 合住人信息卡片 -->
			<view class="card" v-for="(item, index) in cohabitantList" :key="index">
				<view class="card-header">
					<view class="card-indicator"></view>
					<text class="card-title">合住人信息</text>
				</view>
				
				<view class="form-row">
					<text class="form-label">姓名</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ item.name }}</text>
					</view>
				</view>
				
				<view class="form-row">
					<text class="form-label">关系</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ item.relation }}</text>
					</view>
				</view>
				
				<view class="form-row">
					<text class="form-label">入住日期</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ item.checkinDate }}</text>
					</view>
				</view>
			</view>
			
			<!-- 紧急联系人卡片 -->
			<view class="card">
				<view class="card-header">
					<view class="card-indicator"></view>
					<text class="card-title">紧急联系人</text>
				</view>
				
				<view class="form-row">
					<text class="form-label">姓名</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ emergencyContact.name }}</text>
					</view>
				</view>
				
				<view class="form-row">
					<text class="form-label">关系</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ emergencyContact.relation }}</text>
					</view>
				</view>
				
				<view class="form-row">
					<text class="form-label">联系电话</text>
					<view class="form-value-wrap">
						<text class="form-value">{{ emergencyContact.phone }}</text>
					</view>
				</view>
			</view>
		</scroll-view>
		
		<!-- 底部按钮 -->
		<view class="bottom-btn-container">
			<view class="btn-group">
				<view class="btn btn-cancel" @click="handleCancel">
					<text class="btn-text-blue">取消申请</text>
				</view>
				<view class="btn btn-edit" @click="handleEdit">
					<text class="btn-text-white">修改</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				housingType: '',
				checkinId: '',
				
				formData: {
					community: '美好人间',
					room: '6号楼6单元606',
					checkinDate: '2025年2月2日',
					applicant: '张三',
					applyTime: '2025年2月2日 12:12:12',
					status: 'pending',
					statusText: '审批中'
				},
				
				cohabitantList: [
					{
						name: '张三',
						relation: '配偶',
						checkinDate: '2025-12-12'
					}
				],
				
				emergencyContact: {
					name: '张三',
					relation: '配偶',
					phone: '12345678901'
				}
			}
		},
		onLoad(options) {
			if (options.type) {
				this.housingType = options.type
			}
			if (options.id) {
				this.checkinId = options.id
			}
			this.loadCheckinDetail()
		},
		methods: {
			// 加载申请详情
			loadCheckinDetail() {
				// TODO: 调用API获取申请详情
				console.log('加载申请详情，类型:', this.housingType, 'ID:', this.checkinId)
			},
			
			// 取消申请
			handleCancel() {
				uni.showModal({
					title: '提示',
					content: '确定要取消此申请吗？',
					success: (res) => {
						if (res.confirm) {
							uni.showLoading({
								title: '处理中...'
							})
							// TODO: 调用取消API
							setTimeout(() => {
								uni.hideLoading()
								uni.showToast({
									title: '已取消',
									icon: 'success'
								})
								setTimeout(() => {
									uni.navigateBack()
								}, 1500)
							}, 1000)
						}
					}
				})
			},
			
			// 修改信息
			handleEdit() {
				uni.navigateTo({
					url: `/pages/affairs/checkin-process?type=${this.housingType}&id=${this.checkinId}&mode=edit`
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
		padding-bottom: 160rpx;
		box-sizing: border-box;
	}

	.card {
		width: 702rpx;
		border-radius: 20rpx;
		background: #ffffff;
		padding: 26rpx 0 10rpx 0;
		margin: 0 auto 24rpx;
		box-sizing: border-box;
	}

	.card-header {
		display: flex;
		align-items: center;
		margin-bottom: 37rpx;
	}

	.card-indicator {
		width: 12rpx;
		height: 34rpx;
		background: #0f73ff;
		margin-right: 26rpx;
	}

	.card-title {
		color: #171a1f;
		font-size: 32rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.form-row {
		display: flex;
		align-items: center;
		margin: 0 40rpx;
		margin-bottom: 28rpx;
	}

	.form-label {
		width: 166rpx;
		color: #333333;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		
	}

	.form-value-wrap {
		flex: 1;
		
	}

	.form-value {
		color: #1a1a1a;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.status-pending {
		color: #ff8d1a;
	}

	.status-approved {
		color: #12a566;
	}

	.status-rejected {
		color: #fa5740;
	}

	/* 底部按钮 */
	.bottom-btn-container {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		padding: 24rpx;
		background: #f5f6fc;
	}

	.btn-group {
		display: flex;
		justify-content: center;
		gap: 24rpx;
	}

	.btn {
		width: 340rpx;
		height: 92rpx;
		border-radius: 20rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.btn-cancel {
		border: 2rpx solid #1976f8;
		background: #ffffff;
	}

	.btn-edit {
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
	}

	.btn-text-blue {
		color: #1976f8;
		font-size: 34rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.btn-text-white {
		color: #ffffff;
		font-size: 34rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}
</style>

