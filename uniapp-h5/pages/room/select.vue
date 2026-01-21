<template>
	<view class="page">
		<!-- 楼栋选择 -->
		<view class="section-card">
			<text class="section-title">楼栋</text>
			<view class="building-list">
				<view 
					class="building-item" 
					:class="{ active: selectedBuilding === item.id }"
					v-for="item in buildingList" 
					:key="item.id"
					@click="selectBuilding(item.id)"
				>
					<text class="building-text" :class="{ active: selectedBuilding === item.id }">{{ item.name }}</text>
				</view>
			</view>
		</view>
		
		<!-- 单元选择 -->
		<view class="section-card">
			<text class="section-title">单元</text>
			<view class="unit-list">
				<view 
					class="unit-item" 
					:class="{ active: selectedUnit === item.id }"
					v-for="item in unitList" 
					:key="item.id"
					@click="selectUnit(item.id)"
				>
					<text class="unit-text" :class="{ active: selectedUnit === item.id }">{{ item.name }}</text>
				</view>
			</view>
		</view>
		
		<!-- 房间列表 -->
		<view class="room-card">
			<view class="room-header">
				<text class="room-building-name">{{ currentBuildingName }}</text>
				<view class="filter-btn" @click="showFilter">
					<text class="filter-text">筛选</text>
				</view>
			</view>
			
			<!-- 表格头部 -->
			<view class="table-header">
				<view class="table-col floor-col">
					<text class="room-building-name">楼层</text>
				</view>
				<view class="table-col room-col">
					<text class="room-building-name">房间号</text>
				</view>
			</view>
			
			<!-- 楼层列表 -->
			<view class="floor-list" >
				<view class="floor-row" v-for="floor in floorList" :key="floor.floor">
					<view class="table-col floor-col">
						<view class="floor-item" :class="{ active: selectedFloor === floor.floor }" @click="selectFloor(floor.floor)">
							<text class="floor-text" :class="{ active: selectedFloor === floor.floor }">{{ floor.floor }}F</text>
						</view>
					</view>
					<view class="table-col room-col">
						<view class="room-list">
							<view 
								class="room-item" 
								:class="{ active: selectedRoom === room.id, disabled: !room.available }"
								v-for="room in floor.rooms" 
								:key="room.id"
								@click="selectRoom(room)"
							>
								<text class="room-text" :class="{ active: selectedRoom === room.id }">{{ room.name }}</text>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 筛选弹窗 -->
		<view class="filter-popup-overlay" v-if="showFilterPopup" @click="closeFilter">
			<view class="filter-popup" @click.stop>
				<!-- 弹窗头部 -->
				<view class="filter-popup-header">
					<text class="filter-popup-title">筛选</text>
					<text class="filter-popup-close" @click="closeFilter">×</text>
				</view>
				
				<!-- 弹窗内容 -->
				<view class="filter-popup-content">
					<!-- 户型选择 -->
					<view class="filter-section">
						<text class="filter-section-title">户型（选填）</text>
						<view class="filter-option-list">
							<view 
								class="filter-option-item"
								:class="{ active: filterData.houseType === item.value }"
								v-for="item in houseTypeOptions"
								:key="item.value"
								@click="selectHouseType(item.value)"
							>
								<text class="filter-option-text" :class="{ active: filterData.houseType === item.value }">{{ item.label }}</text>
							</view>
						</view>
					</view>
					
					<!-- 楼层选择 -->
					<view class="filter-section">
						<text class="filter-section-title">楼层（选填）</text>
						<view class="filter-floor-range">
							<input class="filter-floor-input" type="number" v-model="filterData.floorMin"  />
							<text class="filter-floor-separator">—</text>
							<input class="filter-floor-input" type="number" v-model="filterData.floorMax"  />
						</view>
					</view>
					
					<!-- 朝向选择 -->
					<view class="filter-section">
						<text class="filter-section-title">朝向（选填）</text>
						<view class="filter-option-list">
							<view 
								class="filter-option-item"
								:class="{ active: filterData.orientation === item.value }"
								v-for="item in orientationOptions"
								:key="item.value"
								@click="selectOrientation(item.value)"
							>
								<text class="filter-option-text" :class="{ active: filterData.orientation === item.value }">{{ item.label }}</text>
							</view>
						</view>
					</view>
				</view>
				
				<!-- 弹窗底部按钮 -->
				<view class="filter-popup-footer">
					<view class="filter-clear-btn" @click="clearFilter">
						<text class="filter-clear-text">清空</text>
					</view>
					<view class="filter-confirm-btn" @click="confirmFilter">
						<text class="filter-confirm-text">确定</text>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import { getBuildingListByProject } from '@/api/building'
import { getUnitListByBuilding } from '@/api/unit'
import { getHouseList } from '@/api/house'

	export default {
		data() {
			return {
				projectId: '',
				selectedBuilding: null,
				selectedUnit: null,
				selectedFloor: null,
				selectedRoom: null,
				showFilterPopup: false,
				filterData: {
					houseType: '',
					floorMin: '',
					floorMax: '',
					orientation: ''
				},
				houseTypeOptions: [
					{ label: '一室', value: '1' },
					{ label: '二室', value: '2' },
					{ label: '三室', value: '3' },
					{ label: '四室', value: '4' },
					{ label: '五室', value: '5' },
					{ label: '六室', value: '6' }
				],
				orientationOptions: [
					{ label: '南', value: 'south' },
					{ label: '东', value: 'east' },
					{ label: '北', value: 'north' },
					{ label: '西', value: 'west' },
					{ label: '其他', value: 'other' },
					{ label: '东南', value: 'southeast' },
					{ label: '西南', value: 'southwest' },
					{ label: '东北', value: 'northeast' },
					{ label: '西北', value: 'northwest' }
				],
				buildingList: [],
				unitList: [],
				floorList: []
			}
		},
		computed: {
			currentBuildingName() {
				const building = this.buildingList.find(item => item.id === this.selectedBuilding)
				return building ? building.name : ''
			}
		},
		onLoad(options) {
			if (options.projectId) {
				this.projectId = options.projectId
				this.loadBuildings()
			}
		},
		methods: {
			/** 加载楼栋列表 */
			async loadBuildings() {
				try {
					uni.showLoading({ title: '加载中...' })
					const response = await getBuildingListByProject(this.projectId)

					if (response.code === 200 && response.data) {
						this.buildingList = response.data

						// 自动选择第一个楼栋
						if (this.buildingList.length > 0) {
							this.selectedBuilding = this.buildingList[0].id
							this.loadUnits()
						}
					} else {
						uni.showToast({
							title: response.msg || '获取楼栋列表失败',
							icon: 'none'
						})
					}
				} catch (error) {
					console.error('获取楼栋列表失败:', error)
					uni.showToast({
						title: '获取楼栋列表失败',
						icon: 'none'
					})
				} finally {
					uni.hideLoading()
				}
			},

			/** 加载单元列表 */
			async loadUnits() {
				try {
					const response = await getUnitListByBuilding(this.selectedBuilding)

					if (response.code === 200 && response.data) {
						this.unitList = response.data

						// 自动选择第一个单元
						if (this.unitList.length > 0) {
							this.selectedUnit = this.unitList[0].id
							this.loadRoomData()
						}
					} else {
						uni.showToast({
							title: response.msg || '获取单元列表失败',
							icon: 'none'
						})
					}
				} catch (error) {
					console.error('获取单元列表失败:', error)
					uni.showToast({
						title: '获取单元列表失败',
						icon: 'none'
					})
				}
			},

			/** 加载房源数据 */
			async loadRoomData() {
				if (!this.selectedBuilding || !this.selectedUnit) {
					return
				}

				try {
					const params = {
						projectId: this.projectId,
						buildingId: this.selectedBuilding,
						unitId: this.selectedUnit
					}

					// 添加筛选条件
					if (this.filterData.houseType) {
						params.houseType = this.filterData.houseType
					}
					if (this.filterData.floorMin) {
						params.floorMin = parseInt(this.filterData.floorMin)
					}
					if (this.filterData.floorMax) {
						params.floorMax = parseInt(this.filterData.floorMax)
					}
					if (this.filterData.orientation) {
						params.orientation = this.filterData.orientation
					}

					const response = await getHouseList(params)

					if (response.code === 200) {
						this.floorList = response.data || []
					} else {
						uni.showToast({
							title: response.msg || '获取房源列表失败',
							icon: 'none'
						})
					}
				} catch (error) {
					console.error('获取房源列表失败:', error)
					uni.showToast({
						title: '获取房源列表失败',
						icon: 'none'
					})
				}
			},

			selectBuilding(id) {
				this.selectedBuilding = id
				this.selectedUnit = null
				this.selectedFloor = null
				this.selectedRoom = null
				this.unitList = []
				this.floorList = []
				this.loadUnits()
			},
			selectUnit(id) {
				this.selectedUnit = id
				this.selectedFloor = null
				this.selectedRoom = null
				this.floorList = []
				this.loadRoomData()
			},
			selectFloor(floor) {
				this.selectedFloor = floor
			},
			selectRoom(room) {
				if (!room.available) {
					uni.showToast({
						title: '该房间暂不可选',
						icon: 'none'
					})
					return
				}
				this.selectedRoom = room.id
				// 跳转到房间详情或预约页面
				uni.navigateTo({
					url: `/pages/room/detail?roomId=${room.id}&projectId=${this.projectId}`
				})
			},
			showFilter() {
				this.showFilterPopup = true
			},
			closeFilter() {
				this.showFilterPopup = false
			},
			selectHouseType(value) {
				this.filterData.houseType = this.filterData.houseType === value ? '' : value
			},
			selectOrientation(value) {
				this.filterData.orientation = this.filterData.orientation === value ? '' : value
			},
			clearFilter() {
				this.filterData = {
					houseType: '',
					floorMin: '',
					floorMax: '',
					orientation: ''
				}
			},
			confirmFilter() {
				console.log('筛选条件:', this.filterData)
				this.showFilterPopup = false
				this.loadRoomData()
			}
		}
	}
</script>

<style scoped>
	.page {
		min-height: 100vh;
		background: #f5f6fc;
		padding: 24rpx;
		box-sizing: border-box;
	}
	
	.section-card {
		width: 702rpx;
		border-radius: 20rpx;
		background: #ffffff;
		padding: 28rpx 30rpx;
		box-sizing: border-box;
		margin-bottom: 24rpx;
	}
	
	.section-title {
		width: 56rpx;
		height: 40rpx;
		color: #1a1a1a;
		font-size: 28rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
		display: block;
		margin-bottom: 24rpx;
	}
	
	.building-list {
		display: flex;
		flex-wrap: wrap;
		gap: 20rpx;
	}
	
	.building-item {
		width: 140rpx;
		height: 68rpx;
		border-radius: 12rpx;
		border: 1.5rpx solid #e5e5e5;
		background: #f8f8f8;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.building-item.active {
		border: 1.5rpx solid #1976f8;
		background: #f8f8f8;
	}
	
	.building-text {
		height: 34rpx;
		color: #666666;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 34rpx;
	}
	
	.building-text.active {
		color: #1976f8;
		font-weight: 500;
	}
	
	.unit-list {
		display: flex;
		flex-wrap: wrap;
		gap: 20rpx;
	}
	
	.unit-item {
		width: 150rpx;
		height: 68rpx;
		border-radius: 12rpx;
		border: 1.5rpx solid #e5e5e5;
		background: #f8f8f8;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.unit-item.active {
		border: 1.5rpx solid #1976f8;
		background: #f8f8f8;
	}
	
	.unit-text {
		height: 34rpx;
		color: #666666;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 34rpx;
	}
	
	.unit-text.active {
		color: #1976f8;
		font-weight: 500;
	}
	
	.room-card {
		width: 702rpx;
		border-radius: 20rpx;
		background: #ffffff;
		padding: 28rpx;
		box-sizing: border-box;
	}
	
	.room-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin-bottom: 14rpx;
		border-bottom: 1rpx solid #f0f0f0;
		padding-bottom: 10rpx;
	}
	
	.room-building-name {
		height: 40rpx;
		color: #1a1a1a;
		font-size: 28rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
	}
	
	.filter-btn {
		width: 100rpx;
		height: 50rpx;
		border-radius: 12rpx;
		background: rgba(25, 118, 248, 0.1);
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.filter-text {
		width: 48rpx;
		height: 30rpx;
		color: #1976f8;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 30rpx;
	}
	
	.table-header {
		display: flex;
	}
	
	.table-col {
		display: flex;
		align-items: center;
	}
	
	.floor-col {
		width: 160rpx;
		flex-shrink: 0;
	}
	
	.room-col {
		flex: 1;
		margin-left: 100rpx;
	}
	
	.table-header-text {
		color: #999999;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}
	
	.floor-list {
		display: block;
	}
	
	.floor-row {
		display: flex;
		padding: 16rpx 0;
	}
	
	.floor-item {
		width: 140rpx;
		height: 68rpx;
		border-radius: 12rpx;
		border: 1.5rpx solid #e5e5e5;
		background: #f8f8f8;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.floor-item.active {
		border: 1.5rpx solid #1976f8;
		background: #f8f8f8;
	}
	
	.floor-text {
		height: 34rpx;
		color: #666666;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 34rpx;
	}
	
	.floor-text.active {
		color: #1976f8;
		font-weight: 500;
	}
	
	.room-list {
		display: flex;
		flex-wrap: wrap;
		gap: 16rpx;
	}
	
	.room-item {
		width: 140rpx;
		height: 68rpx;
		border-radius: 12rpx;
		border: 1.5rpx solid #e5e5e5;
		background: #f8f8f8;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.room-item.active {
		border: 1.5rpx solid #1976f8;
		background: #f8f8f8;
	}
	
	.room-item.disabled {
		background: #f0f0f0;
		opacity: 0.5;
	}
	
	.room-text {
		height: 34rpx;
		color: #666666;
		font-size: 24rpx;
		font-weight: normal;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 34rpx;
	}
	
	.room-text.active {
		color: #1976f8;
		font-weight: 500;
	}
	
	/* 筛选弹窗样式 */
	.filter-popup-overlay {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.5);
		z-index: 1000;
		display: flex;
		align-items: flex-end;
		justify-content: center;
	}
	
	.filter-popup {
		width: 750rpx;
		height: 1228rpx;
		border-radius: 32rpx 32rpx 0 0;
		background: #f5f6fc;
		display: flex;
		flex-direction: column;
	}
	
	.filter-popup-header {
		display: flex;
		align-items: center;
		
		padding: 28rpx 0 0 32rpx;
		position: relative;
	
		border-radius: 32rpx 32rpx 0 0;
	}
	
	.filter-popup-title {
		width: 64rpx;
		height: 45rpx;
		color: #1a1a1a;
		font-size: 32rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 45rpx;
	}
	
	.filter-popup-close {
		position: absolute;
		right: 30rpx;
		top: 50%;
		transform: translateY(-50%);
		font-size: 40rpx;
		color: #999999;
		width: 50rpx;
		height: 50rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.filter-popup-content {
		
		padding: 12rpx 24rpx 24rpx 24rpx;
		overflow-y: auto;
	}
	
	.filter-section {
		background: #ffffff;
		border-radius: 20rpx;
		padding: 28rpx 30rpx;
		margin-bottom: 20rpx;
	}
	
	.filter-section-title {
		width: 168rpx;
		height: 40rpx;
		color: #1a1a1a;
		font-size: 28rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: left;
		line-height: 40rpx;
		display: block;
		margin-bottom: 24rpx;
	}
	
	.filter-option-list {
		display: flex;
		flex-wrap: wrap;
		gap: 20rpx;
	}
	
	.filter-option-item {
		width: 140rpx;
		height: 68rpx;
		border-radius: 12rpx;
		background: #f8f8f8;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.filter-option-item.active {
		border: 1.5rpx solid #1976f8;
		background: #f8f8f8;
	}
	
	.filter-option-text {
		height: 34rpx;
		color: #333333;
		font-size: 24rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 34rpx;
	}
	
	.filter-option-text.active {
		color: #1976f8;
		font-weight: 500;
	}
	
	.filter-floor-range {
		display: flex;
		align-items: center;
		gap: 20rpx;
	}
	
	.filter-floor-input {
		width: 150rpx;
		height: 68rpx;
		border-radius: 12rpx;
		background: #f8f8f8;
		text-align: center;
		font-size: 28rpx;
		color: #333333;
	}
	
	.filter-floor-separator {
		color: #d7d3d3;
		font-size: 28rpx;
	}
	
	.filter-popup-footer {
		display: flex;
		padding: 30rpx;
		gap: 20rpx;
		
	}
	
	.filter-clear-btn {
		width: 340rpx;
		height: 92rpx;
		border-radius: 20rpx;
		border: 2rpx solid #1976f8;
		background: #ffffff;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.filter-clear-text {
		width: 72rpx;
		height: 51rpx;
		color: #1976f8;
		font-size: 36rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 51rpx;
	}
	
	.filter-confirm-btn {
		width: 340rpx;
		height: 92rpx;
		border-radius: 20rpx;
		background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
		backdrop-filter: blur(6rpx);
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.filter-confirm-text {
		width: 72rpx;
		height: 51rpx;
		color: #ffffff;
		font-size: 36rpx;
		font-weight: 500;
		font-family: "PingFang SC", "苹方-简", sans-serif;
		text-align: center;
		line-height: 51rpx;
	}
</style>

