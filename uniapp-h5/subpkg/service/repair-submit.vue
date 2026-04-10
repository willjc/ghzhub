<template>
	<view class="page">
		<scroll-view class="scroll-content" scroll-y>
			<!-- 报修信息卡片 -->
			<view class="card">
				<view class="card-header">
					<view class="card-indicator"></view>
					<text class="card-title">报修信息</text>
				</view>

				<!-- 所在位置 -->
				<view class="form-row" @click="handleSelectLocation">
					<text class="form-label"><text class="required">*</text>所在位置</text>
					<view class="form-value-wrap">
						<text class="form-value placeholder" v-if="!formData.projectName">选择小区</text>
						<text class="form-value" v-else>{{ formData.projectName }}</text>
						<image class="arrow-right" src="/static/向右1@2x.png" mode="aspectFit"></image>
						<text class="form-value placeholder" v-if="!formData.buildingName">楼栋</text>
						<text class="form-value" v-else>{{ formData.buildingName }}</text>
						<image class="arrow-right" src="/static/向右1@2x.png" mode="aspectFit"></image>
						<text class="form-value placeholder" v-if="!formData.unitName">单元</text>
						<text class="form-value" v-else>{{ formData.unitName }}</text>
						<image class="arrow-right" src="/static/向右1@2x.png" mode="aspectFit"></image>
					</view>
				</view>

				<!-- 房间号 -->
				<view class="form-row">
					<text class="form-label"><text class="required">*</text>房间号</text>
					<view class="form-value-wrap">
						<input class="form-input" type="text" v-model="formData.roomNo" placeholder="请输入房间号" placeholder-class="placeholder" />
					</view>
				</view>

				<!-- 联系电话 -->
				<view class="form-row">
					<text class="form-label"><text class="required">*</text>联系电话</text>
					<view class="form-value-wrap">
						<input class="form-input" type="number" v-model="formData.phone" placeholder="请输入联系电话" placeholder-class="placeholder" />
					</view>
				</view>

				<!-- 问题描述 -->
				<view class="form-row form-row-column">
					<text class="form-label form-label-full"><text class="required">*</text>问题描述</text>
					<view class="textarea-wrap">
						<textarea
							class="form-textarea"
							v-model="formData.description"
							placeholder="请输入你的问题描述"
							placeholder-class="placeholder"
							maxlength="500"
						></textarea>
					</view>
				</view>

				<!-- 上传照片 -->
				<view class="form-row form-row-column">
					<text class="form-label form-label-full">上传照片</text>
					<view class="upload-wrap">
						<view class="upload-list">
							<view class="upload-item" v-for="(img, index) in uploadedImages" :key="index">
								<image class="uploaded-image" :src="img" mode="aspectFill"></image>
								<view class="delete-btn" @click="handleDeleteImage(index)">
									<text class="delete-icon">×</text>
								</view>
							</view>
							<view class="upload-area" @click="handleUpload" v-if="uploadedImages.length < 9 && !uploading">
								<view class="upload-placeholder">
									<image class="upload-icon" src="/static/上传@2x.png"></image>
									<text class="upload-text">点击上传</text>
								</view>
							</view>
							<view class="upload-area uploading" v-if="uploading">
								<text class="uploading-text">上传中...</text>
							</view>
						</view>
					</view>
				</view>

				<!-- 维修时间 -->
				<view class="form-row" @click="showDatePicker">
					<text class="form-label"><text class="required">*</text>维修时间</text>
					<view class="form-value-wrap">
						<text class="form-value placeholder" v-if="!formData.repairDate">请选择日期</text>
						<text class="form-value" v-else>{{ formData.repairDate }}</text>
						<image class="arrow-right" src="/static/向右1@2x.png" mode="aspectFit"></image>
					</view>
				</view>
			</view>
		</scroll-view>

		<!-- 底部提交按钮 -->
		<view class="bottom-btn-container">
			<view class="bottom-btn" @click="handleSubmit">
				<text class="bottom-btn-text">{{ submitting ? '提交中...' : '提交申请' }}</text>
			</view>
		</view>

		<!-- 日期选择器 -->
		<view class="picker-mask" v-if="showDatePickerPopup" @click="showDatePickerPopup = false"></view>
		<view class="picker-popup" :class="{ 'show': showDatePickerPopup }">
			<view class="picker-header">
				<text class="picker-cancel" @click="showDatePickerPopup = false">取消</text>
				<text class="picker-title">选择日期</text>
				<text class="picker-confirm" @click="confirmDate">确定</text>
			</view>
			<view class="picker-body">
				<picker-view class="picker-view" :value="datePickerValue" @change="onDateChange">
					<picker-view-column>
						<view class="picker-item" v-for="year in years" :key="year">
							<text class="picker-item-text">{{ year }}年</text>
						</view>
					</picker-view-column>
					<picker-view-column>
						<view class="picker-item" v-for="month in months" :key="month">
							<text class="picker-item-text">{{ month }}月</text>
						</view>
					</picker-view-column>
					<picker-view-column>
						<view class="picker-item" v-for="day in days" :key="day">
							<text class="picker-item-text">{{ day }}日</text>
						</view>
					</picker-view-column>
				</picker-view>
			</view>
		</view>

		<!-- 位置选择器 -->
		<view class="picker-mask" v-if="showLocationPicker" @click="showLocationPicker = false"></view>
		<view class="picker-popup" :class="{ 'show': showLocationPicker }">
			<view class="picker-header">
				<text class="picker-cancel" @click="showLocationPicker = false">取消</text>
				<text class="picker-title">选择位置</text>
				<text class="picker-confirm" @click="confirmLocation">确定</text>
			</view>
			<view class="picker-body">
				<picker-view class="picker-view" :value="locationPickerValue" @change="onLocationChange">
					<picker-view-column>
						<view class="picker-item" v-for="project in projects" :key="project.id">
							<text class="picker-item-text">{{ project.name }}</text>
						</view>
					</picker-view-column>
					<picker-view-column>
						<view class="picker-item" v-for="building in buildings" :key="building.id">
							<text class="picker-item-text">{{ building.name }}</text>
						</view>
					</picker-view-column>
					<picker-view-column>
						<view class="picker-item" v-for="unit in units" :key="unit.id">
							<text class="picker-item-text">{{ unit.name }}</text>
						</view>
					</picker-view-column>
				</picker-view>
			</view>
		</view>
	</view>
</template>

<script>
import { submitRepair } from '@/api/repair.js'
import { getProjectListByType } from '@/api/project.js'
import { getBuildingListByProject } from '@/api/building.js'
import { getUnitListByBuilding } from '@/api/unit.js'
import { post, BASE_URL } from '@/utils/request'

export default {
	data() {
		return {
			userId: null,  // 当前登录用户ID
			formData: {
				projectId: null,
				buildingId: null,
				unitId: null,
				projectName: '',
				buildingName: '',
				unitName: '',
				location: '',  // 拼接后的完整位置
				roomNo: '',
				phone: '',
				description: '',
				repairDate: ''
			},
			uploadedImages: [],  // 本地临时图片路径
			uploadedImageUrls: [],  // 上传后返回的URL
			uploading: false,
			submitting: false,

			// 日期选择器
			showDatePickerPopup: false,
			datePickerValue: [0, 0, 0],
			years: [],
			months: [],
			days: [],

			// 位置选择器
			showLocationPicker: false,
			locationPickerValue: [0, 0, 0],
			projects: [],
			buildings: [],
			units: []
		}
	},
	onLoad() {
		// 获取用户信息
		const userInfo = uni.getStorageSync('userInfo')
		if (!userInfo || !userInfo.userId) {
			uni.showToast({
				title: '请先登录',
				icon: 'none'
			})
			setTimeout(() => {
				uni.navigateTo({
					url: '/pages/login/index'
				})
			}, 1500)
			return
		}

		this.userId = userInfo.userId

		// 设置默认电话号码
		if (userInfo.phone) {
			this.formData.phone = userInfo.phone
		}

		this.initDatePicker()
		this.loadProjects()
	},
	methods: {
		// 初始化日期选择器
		initDatePicker() {
			const currentYear = new Date().getFullYear()
			this.years = []
			for (let i = currentYear; i <= currentYear + 5; i++) {
				this.years.push(i)
			}
			this.months = Array.from({ length: 12 }, (_, i) => i + 1)
			this.days = Array.from({ length: 31 }, (_, i) => i + 1)
		},

		// 加载项目列表
		async loadProjects() {
			try {
				const res = await getProjectListByType('0')  // 0表示全部类型
				if (res.code === 200 && res.data) {
					this.projects = res.data.map(item => ({
						id: item.projectId,
						name: item.projectName
					}))
					// 加载完项目后，加载第一个项目的楼栋
					if (this.projects.length > 0) {
						await this.loadBuildings(this.projects[0].id)
					}
				}
			} catch (err) {
				console.error('加载项目列表失败:', err)
			}
		},

		// 加载楼栋列表
		async loadBuildings(projectId) {
			try {
				const res = await getBuildingListByProject(projectId)
				if (res.code === 200 && res.data) {
					this.buildings = res.data
					// 加载完楼栋后，加载第一个楼栋的单元
					if (this.buildings.length > 0) {
						await this.loadUnits(this.buildings[0].id)
					}
				}
			} catch (err) {
				console.error('加载楼栋列表失败:', err)
			}
		},

		// 加载单元列表
		async loadUnits(buildingId) {
			try {
				const res = await getUnitListByBuilding(buildingId)
				if (res.code === 200 && res.data) {
					this.units = res.data
				}
			} catch (err) {
				console.error('加载单元列表失败:', err)
			}
		},

		// 选择位置
		handleSelectLocation() {
			if (this.projects.length === 0) {
				uni.showToast({
					title: '暂无可选项目',
					icon: 'none'
				})
				return
			}
			this.locationPickerValue = [0, 0, 0]
			this.showLocationPicker = true
		},

		// 位置变化
		async onLocationChange(e) {
			this.locationPickerValue = e.detail.value
			const projectIndex = e.detail.value[0]
			const buildingIndex = e.detail.value[1]

			// 项目变化时，加载对应的楼栋
			if (projectIndex >= 0 && this.projects[projectIndex]) {
				await this.loadBuildings(this.projects[projectIndex].id)
			}

			// 楼栋变化时，加载对应的单元
			if (buildingIndex >= 0 && this.buildings[buildingIndex]) {
				await this.loadUnits(this.buildings[buildingIndex].id)
			}
		},

		// 确认位置选择
		confirmLocation() {
			const projectIndex = this.locationPickerValue[0]
			const buildingIndex = this.locationPickerValue[1]
			const unitIndex = this.locationPickerValue[2]

			if (this.projects[projectIndex]) {
				this.formData.projectId = this.projects[projectIndex].id
				this.formData.projectName = this.projects[projectIndex].name
			}

			if (this.buildings[buildingIndex]) {
				this.formData.buildingId = this.buildings[buildingIndex].id
				this.formData.buildingName = this.buildings[buildingIndex].name
			}

			if (this.units[unitIndex]) {
				this.formData.unitId = this.units[unitIndex].id
				this.formData.unitName = this.units[unitIndex].name
			}

			// 拼接完整位置
			this.formData.location = `${this.formData.projectName}${this.formData.buildingName}${this.formData.unitName}`

			this.showLocationPicker = false
		},

		// 显示日期选择器
		showDatePicker() {
			this.datePickerValue = [0, 0, 0]
			this.showDatePickerPopup = true
		},

		// 日期变化
		onDateChange(e) {
			this.datePickerValue = e.detail.value
		},

		// 确认日期
		confirmDate() {
			const year = this.years[this.datePickerValue[0]]
			const month = String(this.months[this.datePickerValue[1]]).padStart(2, '0')
			const day = String(this.days[this.datePickerValue[2]]).padStart(2, '0')
			this.formData.repairDate = `${year}年${month}月${day}日`
			this.showDatePickerPopup = false
		},

		// 上传图片
		handleUpload() {
			const remainCount = 9 - this.uploadedImages.length
			uni.chooseImage({
				count: remainCount,
				sizeType: ['compressed'],
				sourceType: ['album', 'camera'],
				success: (res) => {
					this.uploadImages(res.tempFilePaths)
				},
				fail: (err) => {
					console.error('选择图片失败:', err)
				}
			})
		},

		// 上传图片到服务器
		async uploadImages(tempFilePaths) {
			this.uploading = true

			for (let filePath of tempFilePaths) {
				try {
					// 读取文件并上传
					uni.uploadFile({
						url: BASE_URL + '/common/upload',
						filePath: filePath,
						name: 'file',
						header: {
							'Authorization': uni.getStorageSync('token') || ''
						},
						success: (uploadRes) => {
							if (uploadRes.statusCode === 200) {
								const data = JSON.parse(uploadRes.data)
								if (data.code === 200) {
									this.uploadedImages.push(filePath)
									this.uploadedImageUrls.push(data.fileName || data.url)
								} else {
									uni.showToast({
										title: data.msg || '上传失败',
										icon: 'none'
									})
								}
							}
						},
						fail: (err) => {
							console.error('上传失败:', err)
							uni.showToast({
								title: '上传失败',
								icon: 'none'
							})
						}
					})
				} catch (err) {
					console.error('上传图片失败:', err)
				}
			}

			// 等待所有图片上传完成
			setTimeout(() => {
				this.uploading = false
			}, 2000)
		},

		// 删除图片
		handleDeleteImage(index) {
			this.uploadedImages.splice(index, 1)
			this.uploadedImageUrls.splice(index, 1)
		},

		// 提交
		async handleSubmit() {
			if (this.submitting || this.uploading) {
				return
			}

			// 验证必填项
			if (!this.formData.projectId) {
				uni.showToast({ title: '请选择所在位置', icon: 'none' })
				return
			}
			if (!this.formData.roomNo) {
				uni.showToast({ title: '请输入房间号', icon: 'none' })
				return
			}
			if (!this.formData.phone) {
				uni.showToast({ title: '请输入联系电话', icon: 'none' })
				return
			}
			if (!this.formData.description) {
				uni.showToast({ title: '请输入问题描述', icon: 'none' })
				return
			}
			if (!this.formData.repairDate) {
				uni.showToast({ title: '请选择维修时间', icon: 'none' })
				return
			}

			// 验证手机号格式
			const phoneReg = /^1[3-9]\d{9}$/
			if (!phoneReg.test(this.formData.phone)) {
				uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
				return
			}

			try {
				this.submitting = true
				uni.showLoading({ title: '提交中...' })

				const submitData = {
					userId: this.userId,
					projectId: this.formData.projectId,
					buildingId: this.formData.buildingId,
					unitId: this.formData.unitId,
					location: this.formData.location,
					roomNo: this.formData.roomNo,
					phone: this.formData.phone,
					description: this.formData.description,
					images: this.uploadedImageUrls.join(','),
					repairDate: this.formData.repairDate
				}

				const res = await submitRepair(submitData)

				uni.hideLoading()

				if (res.code === 200) {
					uni.showToast({
						title: '提交成功',
						icon: 'success'
					})
					setTimeout(() => {
						uni.navigateBack()
					}, 1500)
				} else {
					uni.showToast({
						title: res.msg || '提交失败',
						icon: 'none'
					})
				}
			} catch (err) {
				uni.hideLoading()
				console.error('提交报修失败:', err)
				uni.showToast({
					title: '提交失败',
					icon: 'none'
				})
			} finally {
				this.submitting = false
			}
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

	.form-row-column {
		flex-direction: column;
		align-items: flex-start;
	}

	.required {
		color: #ff0000;
		font-size: 28rpx;
	}

	.form-label {
		width: 166rpx;
		color: #333333;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		padding-bottom: 28rpx;
		flex-shrink: 0;
	}

	.form-label-full {
		width: 100%;
		margin-bottom: 16rpx;
		padding-bottom: 0;
	}

	.form-value-wrap {
		flex: 1;
		padding-bottom: 28rpx;
		border-bottom: 1rpx solid #f0f0f0;
		display: flex;
		align-items: center;
	}

	.form-value {
		color: #1a1a1a;
		font-size: 26rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.form-input {
		width: 100%;
		color: #1a1a1a;
		font-size: 26rpx;
		text-align: left;
	}

	.arrow-right {
		width: 24rpx;
		height: 24rpx;
		margin-left: 8rpx;
		flex-shrink: 0;
	}

	.placeholder {
		color: #999999;
	}

	/* 文本域 */
	.textarea-wrap {
		width: 100%;
		border: 1rpx solid #e5e5e5;
		border-radius: 12rpx;
		padding: 16rpx;
		box-sizing: border-box;
	}

	.form-textarea {
		width: 100%;
		height: 121rpx;
		color: #333333;
		font-size: 26rpx;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		line-height: 40rpx;
	}

	/* 上传区域 */
	.upload-wrap {
		width: 100%;
	}

	.upload-list {
		display: flex;
		flex-wrap: wrap;
		gap: 20rpx;
	}

	.upload-item {
		width: 166rpx;
		height: 166rpx;
		position: relative;
	}

	.uploaded-image {
		width: 100%;
		height: 100%;
		border-radius: 12rpx;
	}

	.delete-btn {
		position: absolute;
		top: -16rpx;
		right: -16rpx;
		width: 40rpx;
		height: 40rpx;
		border-radius: 50%;
		background: rgba(0, 0, 0, 0.5);
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.delete-icon {
		color: #ffffff;
		font-size: 28rpx;
		line-height: 1;
	}

	.upload-area {
		width: 166rpx;
		height: 166rpx;
		border-radius: 12rpx;
		border: 2rpx solid #cdced5;
		background: #fafbff;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.upload-area.uploading {
		border-style: dashed;
	}

	.uploading-text {
		font-size: 24rpx;
		color: #999999;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}

	.upload-placeholder {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}

	.upload-icon {
		width: 38rpx;
		height: 38rpx;
		margin-bottom: 10rpx;
	}

	.upload-text {
		font-size: 24rpx;
		color: #999999;
		font-family: "PingFang SC", "苹方-简", sans-serif;
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

	.bottom-btn {
		width: 702rpx;
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

	/* Picker弹窗样式 */
	.picker-mask {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.5);
		z-index: 998;
	}

	.picker-popup {
		position: fixed;
		left: 0;
		right: 0;
		bottom: 0;
		background: #ffffff;
		border-radius: 24rpx 24rpx 0 0;
		z-index: 999;
		transform: translateY(100%);
		transition: transform 0.3s ease;
	}

	.picker-popup.show {
		transform: translateY(0);
	}

	.picker-header {
		width: 750rpx;
		height: 95rpx;
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 0 32rpx;
		box-sizing: border-box;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.picker-cancel {
		color: rgba(0, 0, 0, 0.6);
		font-size: 32rpx;
		font-weight: 400;
		font-family: "Roboto", "PingFang SC", sans-serif;
		line-height: 48rpx;
	}

	.picker-title {
		color: rgba(0, 0, 0, 0.9);
		font-size: 32rpx;
		font-weight: 600;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 48rpx;
	}

	.picker-confirm {
		color: #1281ff;
		font-size: 32rpx;
		font-weight: 400;
		font-family: "Roboto", "PingFang SC", sans-serif;
		line-height: 48rpx;
	}

	.picker-body {
		width: 750rpx;
		height: 400rpx;
		background: #ffffff;
	}

	.picker-view {
		width: 100%;
		height: 100%;
	}

	.picker-item {
		height: 80rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.picker-item-text {
		color: #000000;
		font-size: 28rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 80rpx;
	}
</style>
