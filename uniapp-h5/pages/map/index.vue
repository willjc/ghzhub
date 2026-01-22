<template>
	<view class="page">
		<!-- Tab 切换 -->
		<view class="tab-bar">
			<view
				class="tab-item"
				:class="{ active: currentType === '2' }"
				@click="switchTab('2')"
			>
				<text class="tab-text" :class="{ active: currentType === '2' }">保租房</text>
				<view class="tab-line" v-if="currentType === '2'"></view>
			</view>
			<view
				class="tab-item"
				:class="{ active: currentType === '3' }"
				@click="switchTab('3')"
			>
				<text class="tab-text" :class="{ active: currentType === '3' }">市场租赁</text>
				<view class="tab-line" v-if="currentType === '3'"></view>
			</view>
			<view
				class="tab-item"
				:class="{ active: currentType === '1' }"
				@click="switchTab('1')"
			>
				<text class="tab-text" :class="{ active: currentType === '1' }">人才公寓</text>
				<view class="tab-line" v-if="currentType === '1'"></view>
			</view>
		</view>

		<!-- 地图容器 -->
		<view id="map-container" class="map-container"></view>

		<!-- 加载提示 -->
		<view class="loading-mask" v-if="loading">
			<text class="loading-text">加载中...</text>
		</view>

		<!-- 空状态 -->
		<view class="empty-state" v-if="!loading && isEmpty">
			<text class="empty-text">暂无项目数据</text>
		</view>
	</view>
</template>

<script>
import { getProjectListByType } from '@/api/project.js'

// 高德地图配置
const AMAP_KEY = '2b05ba24adfcbb78c8ccec1cf6e5afc3'
const AMAP_SECURITY_CODE = 'aa6ec22a6a1fac3ebae4e25ef51cbe73'

export default {
	data() {
		return {
			currentType: '2', // 默认保租房（2:保租房 1:人才公寓 3:市场租赁）
			map: null,
			markers: [],
			infoWindows: [], // 存储信息窗体 DOM 元素
			loading: true,
			isEmpty: false,
			sdkLoaded: false
		}
	},

	onLoad() {
		// #ifdef H5
		this.loadAMapSDK()
		// #endif

		// #ifndef H5
		uni.showToast({
			title: '地图功能仅支持H5',
			icon: 'none'
		})
		this.loading = false
		// #endif
	},

	onUnload() {
		// 页面卸载时销毁地图
		this.destroyMap()
	},

	methods: {
		/**
		 * 动态加载高德地图 SDK
		 */
		loadAMapSDK() {
			// 如果已经加载过，直接初始化
			if (typeof AMap !== 'undefined') {
				console.log('高德地图 SDK 已存在')
				this.sdkLoaded = true
				this.$nextTick(() => {
					this.initMap()
				})
				return
			}

			// 设置安全密钥
			window._AMapSecurityConfig = {
				securityJsCode: AMAP_SECURITY_CODE
			}

			// 创建 script 标签动态加载
			const script = document.createElement('script')
			script.type = 'text/javascript'
			script.src = `https://webapi.amap.com/maps?v=2.0&key=${AMAP_KEY}`
			script.async = true
			script.onload = () => {
				console.log('高德地图 SDK 加载成功')
				this.sdkLoaded = true
				this.$nextTick(() => {
					this.initMap()
				})
			}
			script.onerror = () => {
				console.error('高德地图 SDK 加载失败')
				uni.showToast({
					title: '地图SDK加载失败',
					icon: 'none'
				})
				this.loading = false
			}

			document.head.appendChild(script)
		},

		/**
		 * 初始化地图
		 */
		initMap() {
			// 初始化地图，默认中心点（郑州航空港区）
			this.map = new AMap.Map('map-container', {
				zoom: 13,
				center: [113.84, 34.56],
				viewMode: '2D'
			})

			console.log('地图初始化完成')

			// 加载默认保租房数据
			this.loadProjects()
		},

		/**
		 * 加载项目数据
		 */
		async loadProjects() {
			this.loading = true
			this.isEmpty = false

			try {
				const res = await getProjectListByType(this.currentType)
				if (res.code === 200 && res.data) {
					const projects = res.data
					if (projects && projects.length > 0) {
						// 检查有多少项目有经纬度
						const validProjects = projects.filter(p =>
							p.longitude && p.latitude &&
							!isNaN(parseFloat(p.longitude)) &&
							!isNaN(parseFloat(p.latitude))
						)

						// 如果有效项目少于3个，补充测试数据
						if (validProjects.length < 3) {
							console.log('有效项目不足3个，使用测试数据')
							this.useTestData(projects)
						} else {
							this.addMarkers(projects)
						}
						this.isEmpty = false
					} else {
						// 没有数据时使用测试数据
						this.useTestData()
					}
				} else {
					// 使用测试数据
					this.useTestData()
				}
			} catch (error) {
				console.error('加载项目数据失败:', error)
				// 加载失败时使用测试数据
				this.useTestData()
			} finally {
				this.loading = false
			}
		},

		/**
		 * 使用测试数据（用于演示）
		 * @param {Array} existingProjects 现有的真实项目数据
		 */
		useTestData(existingProjects = []) {
			// 测试项目数据（郑州航空港区附近）
			const testProjects = [
				{
					projectId: 1,
					projectName: '航南新城专家公寓',
					longitude: 113.84,
					latitude: 34.56,
					address: '航空港区新港大道与遵大路交叉口',
					price: 2000,
					availableHouses: 15,
					projectType: this.currentType
				},
				{
					projectId: 2,
					projectName: '港区人才公寓',
					longitude: 113.86,
					latitude: 34.58,
					address: '航空港区郑港六路',
					price: 1800,
					availableHouses: 8,
					projectType: this.currentType
				},
				{
					projectId: 3,
					projectName: '锦绣桂园',
					longitude: 113.82,
					latitude: 34.54,
					address: '航空港区航天路',
					price: 2200,
					availableHouses: 12,
					projectType: this.currentType
				},
				{
					projectId: 4,
					projectName: '万科美景',
					longitude: 113.88,
					latitude: 34.57,
					address: '航空港区巡航路',
					price: 2500,
					availableHouses: 5,
					projectType: this.currentType
				},
				{
					projectId: 5,
					projectName: '绿地香榭',
					longitude: 113.85,
					latitude: 34.52,
					address: '航空港区雍州路',
					price: 1900,
					availableHouses: 20,
					projectType: this.currentType
				},
				{
					projectId: 6,
					projectName: '保利文化广场',
					longitude:  113.87,
					latitude: 34.53,
					address: '航空港区郑港四街',
					price: 2100,
					availableHouses: 10,
					projectType: this.currentType
				}
			]

			// 合并真实数据和测试数据（真实数据优先）
			const allProjects = [...existingProjects, ...testProjects]

			this.addMarkers(allProjects)
			this.isEmpty = false
			console.log('使用测试数据，共', allProjects.length, '个项目（包含', existingProjects.length, '个真实项目）')
		},

		/**
		 * 添加标记点到地图
		 */
		addMarkers(projects) {
			// #ifdef H5
			if (!this.map) return

			console.log('addMarkers 收到项目数量:', projects.length)

			// 清除旧标记和信息窗体
			this.clearMarkers()

			this.markers = []
			this.infoWindows = []
			const validProjects = [] // 存储有效项目

			// 先筛选出有经纬度的项目
			projects.forEach((project, index) => {
				if (project.longitude && project.latitude) {
					const lng = parseFloat(project.longitude)
					const lat = parseFloat(project.latitude)

					if (!isNaN(lng) && !isNaN(lat)) {
						// 创建标记
						const marker = new AMap.Marker({
							position: [lng, lat],
							title: project.projectName || '项目',
							raiseOnDrag: false
						})

						// 点击标记显示信息窗体
						marker.on('click', () => {
							this.showInfoWindow(project)
						})

						this.markers.push(marker)
						validProjects.push(project)
					}
				}
			})

			console.log('有效标记数量:', this.markers.length)

			// 添加标记到地图
			if (this.markers.length > 0) {
				this.map.add(this.markers)
				// 自动调整视野以包含所有标记
				this.map.setFitView()

				// 默认打开所有项目的信息窗体
				setTimeout(() => {
					validProjects.forEach(project => {
						this.showCustomLabel(project)
					})
				}, 500)
			}
			// #endif
		},

		/**
		 * 显示自定义标签（支持多个同时显示）
		 */
		showCustomLabel(project) {
			// #ifdef H5
			if (!this.map) return

			const lng = parseFloat(project.longitude)
			const lat = parseFloat(project.latitude)

			if (isNaN(lng) || isNaN(lat)) return

			// 创建标签内容 - 使用绝对定位的 div
			const labelDiv = document.createElement('div')
			labelDiv.className = 'map-custom-label'
			labelDiv.style.cssText = `
				padding: 8px 12px;
				min-width: 120px;
				background: white;
				border-radius: 8px;
				box-shadow: 0 2px 10px rgba(0,0,0,0.2);
				font-size: 12px;
				white-space: nowrap;
				position: absolute;
				z-index: 999;
				pointer-events: none;
			`
			labelDiv.innerHTML = `
				<div style="font-weight: bold; color: #1a1a1a; margin-bottom: 4px; font-size: 13px;">
					${project.projectName || '项目'}
				</div>
				<div style="color: #e5252b; font-weight: 500;">
					${project.price ? '¥' + project.price + '元/月起' : '价格面议'}
				</div>
				<div style="position: absolute; bottom: -6px; left: 50%; transform: translateX(-50%); width: 0; height: 0; border-left: 6px solid transparent; border-right: 6px solid transparent; border-top: 6px solid white;"></div>
			`

			// 添加到地图容器
			const mapContainer = document.getElementById('map-container')
			if (mapContainer) {
				// 将经纬度转换为像素位置
				const pixel = this.map.lngLatToContainer([lng, lat])
				labelDiv.style.left = (pixel.x - 60) + 'px'
				labelDiv.style.top = (pixel.y - 60) + 'px'

				mapContainer.appendChild(labelDiv)
				this.infoWindows.push({ element: labelDiv, lng, lat })
			}
			// #endif
		},

		/**
		 * 清除所有标记点
		 */
		clearMarkers() {
			// #ifdef H5
			if (this.map) {
				// 清除标记
				if (this.markers.length > 0) {
					this.map.remove(this.markers)
					this.markers = []
				}
				// 移除所有自定义信息窗体 DOM
				if (this.infoWindows.length > 0) {
					this.infoWindows.forEach(item => {
						if (item.element && item.element.parentNode) {
							item.element.parentNode.removeChild(item.element)
						}
					})
					this.infoWindows = []
				}
			}
			// #endif
		},

		/**
		 * 显示信息窗体（点击标记时调用）
		 */
		showInfoWindow(project) {
			// #ifdef H5
			// 点击标记时可以添加高亮效果或其他交互
			console.log('点击了项目:', project.projectName)
			// #endif
		},

		/**
		 * 切换标签
		 */
		switchTab(type) {
			if (this.currentType === type) return
			this.currentType = type
			this.loadProjects()
		},

		/**
		 * 销毁地图
		 */
		destroyMap() {
			// #ifdef H5
			if (this.map) {
				this.map.destroy()
				this.map = null
			}
			// #endif
		}
	}
}
</script>

<style scoped>
.page {
	width: 100%;
	height: 100vh;
	background-color: #f5f6fc;
	display: flex;
	flex-direction: column;
}

/* Tab 切换栏 */
.tab-bar {
	display: flex;
	background: #ffffff;
	padding: 0 24rpx;
	height: 88rpx;
	align-items: center;
	border-bottom: 1rpx solid #f0f0f0;
}

.tab-item {
	flex: 1;
	height: 88rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	position: relative;
	cursor: pointer;
}

.tab-text {
	font-size: 28rpx;
	color: #666666;
	transition: color 0.3s;
}

.tab-text.active {
	color: #4A90E2;
	font-weight: 500;
}

.tab-line {
	position: absolute;
	bottom: 0;
	width: 40rpx;
	height: 6rpx;
	background: #4A90E2;
	border-radius: 3rpx;
}

/* 地图容器 */
.map-container {
	width: 100%;
	flex: 1;
	min-height: 0;
	position: relative;
}

/* 加载遮罩 */
.loading-mask {
	position: absolute;
	top: 88rpx;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(255, 255, 255, 0.8);
	display: flex;
	align-items: center;
	justify-content: center;
	z-index: 10;
}

.loading-text {
	font-size: 28rpx;
	color: #999;
}

/* 空状态 */
.empty-state {
	position: absolute;
	top: 88rpx;
	left: 0;
	right: 0;
	bottom: 0;
	display: flex;
	align-items: center;
	justify-content: center;
	background: #f5f6fc;
}

.empty-text {
	font-size: 28rpx;
	color: #999;
}
</style>
