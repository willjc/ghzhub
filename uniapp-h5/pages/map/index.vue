<template>
	<view class="page">
		<!-- Tab 切换 -->
		<view class="tab-bar">
			<view
				class="tab-item"
				:class="{ active: isTypeSelected('2') }"
				@click="switchTab('2')"
			>
				<text class="tab-text" :class="{ active: isTypeSelected('2') }">保租房（{{ typeCounts[2] || 0 }}）</text>
				<view class="tab-line" v-if="isTypeSelected('2')"></view>
			</view>
			<view
				class="tab-item"
				:class="{ active: isTypeSelected('1') }"
				@click="switchTab('1')"
			>
				<text class="tab-text" :class="{ active: isTypeSelected('1') }">人才公寓（{{ typeCounts[1] || 0 }}）</text>
				<view class="tab-line" v-if="isTypeSelected('1')"></view>
			</view>
			<view
				class="tab-item"
				:class="{ active: isTypeSelected('3') }"
				@click="switchTab('3')"
			>
				<text class="tab-text" :class="{ active: isTypeSelected('3') }">市场租赁（{{ typeCounts[3] || 0 }}）</text>
				<view class="tab-line" v-if="isTypeSelected('3')"></view>
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

// 类型配置
const TYPE_CONFIG = {
	'1': { name: '人才公寓', color: '#002FA7' },  // 克莱因蓝
	'2': { name: '保租房', color: '#E53935' },      // 红色
	'3': { name: '市场租赁', color: '#4CAF50' }     // 绿色
}

export default {
	data() {
		return {
			selectedTypes: ['1', '2', '3'], // 默认全选（1:人才公寓 2:保租房 3:市场租赁）
			typeCounts: { '1': 0, '2': 0, '3': 0 }, // 各类型项目数量
			map: null,
			markers: [],
			infoWindow: null,
			allProjects: [], // 所有项目数据
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
		this.destroyMap()
	},

	methods: {
		/**
		 * 判断类型是否选中
		 */
		isTypeSelected(type) {
			return this.selectedTypes.includes(type)
		},

		/**
		 * 动态加载高德地图 SDK
		 */
		loadAMapSDK() {
			if (typeof AMap !== 'undefined') {
				console.log('高德地图 SDK 已存在')
				this.sdkLoaded = true
				this.$nextTick(() => {
					this.initMap()
				})
				return
			}

			window._AMapSecurityConfig = {
				securityJsCode: AMAP_SECURITY_CODE
			}

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
			this.map = new AMap.Map('map-container', {
				zoom: 13,
				center: [113.84, 34.56],
				viewMode: '2D'
			})

			console.log('地图初始化完成')

			// 加载所有类型的数据
			this.loadAllProjects()
		},

		/**
		 * 加载所有类型的项目数据
		 */
		async loadAllProjects() {
			this.loading = true
			this.isEmpty = false

			try {
				// 并行加载三种类型的数据
				const [type1, type2, type3] = await Promise.all([
					this.loadTypeData('1'),
					this.loadTypeData('2'),
					this.loadTypeData('3')
				])

				// 合并所有数据
				this.allProjects = [
					...this.processTypeData(type1, '1'),
					...this.processTypeData(type2, '2'),
					...this.processTypeData(type3, '3')
				]

				// 更新统计
				this.typeCounts = {
					'1': this.processTypeData(type1, '1').length,
					'2': this.processTypeData(type2, '2').length,
					'3': this.processTypeData(type3, '3').length
				}

				// 显示选中的类型
				this.updateMarkers()

				console.log('所有项目加载完成:', this.typeCounts)
			} catch (error) {
				console.error('加载数据失败:', error)
				this.loading = false
			}
		},

		/**
		 * 加载单个类型的数据
		 */
		async loadTypeData(type) {
			try {
				const res = await getProjectListByType(type)
				return res.code === 200 ? (res.data || []) : []
			} catch {
				return []
			}
		},

		/**
		 * 处理类型数据（过滤有效坐标的项目）
		 */
		processTypeData(projects, type) {
			return projects.filter(p =>
				p.longitude && p.latitude &&
				!isNaN(parseFloat(p.longitude)) &&
				!isNaN(parseFloat(p.latitude))
			)
		},

		/**
		 * 更新地图标记
		 */
		updateMarkers() {
			// #ifdef H5
				if (!this.map) return

				this.clearMarkers()

				// 筛选出选中类型的项目
				const selectedProjects = this.allProjects.filter(p =>
					this.selectedTypes.includes(p.projectType)
				)

				console.log('选中的类型:', this.selectedTypes, '项目数量:', selectedProjects.length)

				if (selectedProjects.length === 0) {
					this.isEmpty = true
					this.loading = false
					return
				}

				this.isEmpty = false

				// 存储项目和标记的配对
				const projectMarkerPairs = []

				// 添加标记
				selectedProjects.forEach(project => {
					const lng = parseFloat(project.longitude)
					const lat = parseFloat(project.latitude)

					if (!isNaN(lng) && !isNaN(lat)) {
						const color = TYPE_CONFIG[project.projectType]?.color || '#4A90E2'
						const icon = this.createIcon(color)

						const marker = new AMap.Marker({
							position: [lng, lat],
							icon: icon,
							title: project.projectName || '项目'
						})

						marker.on('click', () => {
							this.showInfoWindow(project, marker)
						})

						this.markers.push(marker)
						projectMarkerPairs.push({ project, marker })
					}
				})

				// 添加到地图
				if (this.markers.length > 0) {
					this.map.add(this.markers)
					this.map.setFitView()

					// 默认打开第一个项目的信息窗体
					setTimeout(() => {
						if (projectMarkerPairs.length > 0) {
							const first = projectMarkerPairs[0]
							this.showInfoWindow(first.project, first.marker)
						}
					}, 500)
				}

				this.loading = false
			// #endif
		},

		/**
		 * 创建图标
		 */
		createIcon(color) {
			return new AMap.Icon({
				size: new AMap.Size(32, 40),
				image: 'data:image/svg+xml;charset=utf-8,' + encodeURIComponent(`
					<svg xmlns="http://www.w3.org/2000/svg" width="32" height="40" viewBox="0 0 32 40">
						<path d="M16 0C7.16 0 0 7.16 0 16c0 8.84 16 24 16 24s16-15.16 16-24C32 7.16 24.84 0 16 0z" fill="${color}"/>
						<path d="M16 8c-4.42 0-8 3.58-8 8s3.58 8 8 8 8-3.58 8-8-3.58-8-8-8z" fill="white"/>
						<circle cx="16" cy="16" r="3" fill="${color}"/>
					</svg>
				`),
				imageSize: new AMap.Size(32, 40),
				imageOffset: new AMap.Pixel(0, 0)
			})
		},

		/**
		 * 清除所有标记点
		 */
		clearMarkers() {
			// #ifdef H5
				if (this.map) {
					if (this.markers.length > 0) {
						this.map.remove(this.markers)
						this.markers = []
					}
					if (this.infoWindow) {
						this.infoWindow.close()
						this.infoWindow = null
					}
				}
			// #endif
		},

		/**
		 * 显示信息窗体
		 */
		showInfoWindow(project, marker) {
			// #ifdef H5
				if (!this.map || !marker) return

				const content = `
					<div style="padding: 12px; min-width: 180px; font-size: 13px;">
						<div style="font-weight: bold; color: #1a1a1a; margin-bottom: 8px; font-size: 15px;">
							${project.projectName || '项目'}
						</div>
						<div style="color: #666; margin-bottom: 6px; font-size: 12px;">
							📍 ${project.address || '暂无地址'}
						</div>
						<div style="color: #e5252b; font-weight: 500; font-size: 15px; margin-bottom: 4px;">
							${project.price ? '¥' + project.price + '元/月起' : '价格面议'}
						</div>
						<div style="color: #999; font-size: 12px; margin-bottom: 10px;">
							可用房源: ${project.availableHouses || 0}套
						</div>
						<div class="detail-btn"
							 data-project-id="${project.projectId}"
							 style="background: #4A90E2; color: white; padding: 8px 16px; border-radius: 4px; text-align: center; cursor: pointer; font-size: 13px;">
							查看详情 →
						</div>
					</div>
				`

				if (this.infoWindow) {
					this.infoWindow.close()
				}

				this.infoWindow = new AMap.InfoWindow({
					content: content,
					offset: new AMap.Pixel(0, -35)
				})

				this.infoWindow.open(this.map, marker.getPosition())

				// 绑定按钮点击事件（需要在窗体打开后绑定）
				setTimeout(() => {
					const btn = document.querySelector('.detail-btn')
					if (btn) {
						btn.onclick = () => {
							uni.navigateTo({
								url: '/pages/project/detail?id=' + project.projectId
							})
						}
					}
				}, 100)
			// #endif
		},

		/**
		 * 切换标签选中状态
		 */
		switchTab(type) {
			const index = this.selectedTypes.indexOf(type)
			if (index > -1) {
				// 已选中，取消选中
				this.selectedTypes.splice(index, 1)
			} else {
				// 未选中，添加选中
				this.selectedTypes.push(type)
			}

			console.log('当前选中:', this.selectedTypes)

			// 更新地图显示
			this.updateMarkers()
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
	font-size: 26rpx;
	color: #999999;
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
