<template>
  <div>
    <el-row :gutter="24">
      <!-- 婚姻状态分布 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <div class="profile-card">
          <div class="card-header">
            <div class="header-icon">
              <i class="el-icon-user"></i>
            </div>
            <div class="header-content">
              <h3 class="card-title">婚姻状态分布</h3>
              <p class="card-subtitle">租户婚姻状况统计分析</p>
            </div>
          </div>
          <div class="chart-container">
            <div ref="marriageChart" class="chart"></div>
          </div>
          <div class="stats-legend">
            <div class="legend-item" v-for="(item, index) in marriageData" :key="index">
              <div class="legend-indicator" :style="{ backgroundColor: colors[index] }"></div>
              <div class="legend-info">
                <div class="legend-label">{{ item.name }}</div>
                <div class="legend-value">{{ item.value }}人</div>
              </div>
              <div class="legend-percent">{{ item.percent }}%</div>
            </div>
          </div>
        </div>
      </el-col>

      <!-- 户籍分布 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <div class="profile-card">
          <div class="card-header">
            <div class="header-icon">
              <i class="el-icon-location-outline"></i>
            </div>
            <div class="header-content">
              <h3 class="card-title">户籍分布</h3>
              <p class="card-subtitle">租户户籍类型统计分析</p>
            </div>
          </div>
          <div class="chart-container">
            <div ref="householdChart" class="chart"></div>
          </div>
          <div class="stats-legend">
            <div class="legend-item" v-for="(item, index) in householdData" :key="index">
              <div class="legend-indicator" :style="{ backgroundColor: colors[index + 3] }"></div>
              <div class="legend-info">
                <div class="legend-label">{{ item.name }}</div>
                <div class="legend-value">{{ item.value }}人</div>
              </div>
              <div class="legend-percent">{{ item.percent }}%</div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'TenantProfile',
  props: {
    data: {
      type: Object,
      default: () => ({
        marriageStatus: {},
        household: {}
      })
    }
  },
  data() {
    return {
      colors: ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272'],
      marriageChart: null,
      householdChart: null
    }
  },
  computed: {
    marriageData() {
      const total = Object.values(this.data.marriageStatus).reduce((sum, val) => sum + val, 0)
      return [
        { name: '已婚', value: this.data.marriageStatus.married || 0, percent: ((this.data.marriageStatus.married || 0) / total * 100).toFixed(1) },
        { name: '未婚', value: this.data.marriageStatus.unmarried || 0, percent: ((this.data.marriageStatus.unmarried || 0) / total * 100).toFixed(1) },
        { name: '离异', value: this.data.marriageStatus.divorced || 0, percent: ((this.data.marriageStatus.divorced || 0) / total * 100).toFixed(1) },
        { name: '其他', value: this.data.marriageStatus.other || 0, percent: ((this.data.marriageStatus.other || 0) / total * 100).toFixed(1) }
      ]
    },
    householdData() {
      const total = Object.values(this.data.household).reduce((sum, val) => sum + val, 0)
      return [
        { name: '本地户籍', value: this.data.household.local || 0, percent: ((this.data.household.local || 0) / total * 100).toFixed(1) },
        { name: '外地户籍', value: this.data.household.nonlocal || 0, percent: ((this.data.household.nonlocal || 0) / total * 100).toFixed(1) },
        { name: '未知户籍', value: this.data.household.unknown || 0, percent: ((this.data.household.unknown || 0) / total * 100).toFixed(1) }
      ]
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initCharts()
    })
  },
  beforeDestroy() {
    if (this.marriageChart) {
      this.marriageChart.dispose()
    }
    if (this.householdChart) {
      this.householdChart.dispose()
    }
  },
  methods: {
    initCharts() {
      // 初始化婚姻状态图表
      this.marriageChart = echarts.init(this.$refs.marriageChart)
      const marriageOption = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c}人 ({d}%)'
        },
        series: [
          {
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['50%', '50%'],
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '20',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: this.marriageData.map(item => ({
              value: item.value,
              name: item.name,
              itemStyle: {
                color: this.colors[this.marriageData.indexOf(item)]
              }
            }))
          }
        ]
      }
      this.marriageChart.setOption(marriageOption)

      // 初始化户籍分布图表
      this.householdChart = echarts.init(this.$refs.householdChart)
      const householdOption = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c}人 ({d}%)'
        },
        series: [
          {
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['50%', '50%'],
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '20',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: this.householdData.map(item => ({
              value: item.value,
              name: item.name,
              itemStyle: {
                color: this.colors[this.householdData.indexOf(item) + 3]
              }
            }))
          }
        ]
      }
      this.householdChart.setOption(householdOption)

      // 监听窗口大小变化
      window.addEventListener('resize', this.handleResize)
    },
    handleResize() {
      if (this.marriageChart) {
        this.marriageChart.resize()
      }
      if (this.householdChart) {
        this.householdChart.resize()
      }
    }
  },
  watch: {
    data: {
      handler() {
        this.$nextTick(() => {
          this.initCharts()
        })
      },
      deep: true
    }
  }
}
</script>

<style lang="scss" scoped>
// 租户画像卡片样式
.profile-card {
  background: #fff;
  border-radius: 12px;
  padding: 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  overflow: hidden;
  margin-bottom: 24px;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);

    .header-icon {
      transform: scale(1.05);
    }
  }

  .card-header {
    display: flex;
    align-items: center;
    padding: 24px 24px 16px;
    background: linear-gradient(135deg, #f8f9fb 0%, #e9ecef 100%);
    border-bottom: 1px solid #ebeef5;

    .header-icon {
      width: 48px;
      height: 48px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #409EFF 0%, #36A3F7 100%);
      margin-right: 16px;
      transition: all 0.3s ease;

      i {
        color: white;
        font-size: 22px;
      }
    }

    .header-content {
      .card-title {
        margin: 0 0 4px;
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }

      .card-subtitle {
        margin: 0;
        font-size: 13px;
        color: #909399;
        line-height: 1.4;
      }
    }
  }

  .chart-container {
    height: 320px;
    padding: 20px 24px 0;

    .chart {
      width: 100%;
      height: 100%;
    }
  }

  .stats-legend {
    padding: 16px 24px 24px;
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;

    .legend-item {
      display: flex;
      align-items: center;
      padding: 12px;
      background: #f8f9fb;
      border-radius: 8px;
      transition: all 0.2s ease;

      &:hover {
        background: #ecf5ff;
      }

      .legend-indicator {
        width: 12px;
        height: 12px;
        border-radius: 50%;
        margin-right: 10px;
        flex-shrink: 0;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      }

      .legend-info {
        flex: 1;
        min-width: 0;

        .legend-label {
          font-size: 13px;
          color: #606266;
          line-height: 1.3;
          margin-bottom: 2px;
        }

        .legend-value {
          font-size: 14px;
          font-weight: 600;
          color: #303133;
        }
      }

      .legend-percent {
        font-size: 14px;
        font-weight: 600;
        color: #409EFF;
        margin-left: 8px;
        flex-shrink: 0;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .profile-card {
    margin-bottom: 16px;

    .card-header {
      padding: 20px 16px 12px;

      .header-icon {
        width: 40px;
        height: 40px;
        margin-right: 12px;

        i {
          font-size: 18px;
        }
      }

      .header-content {
        .card-title {
          font-size: 15px;
        }

        .card-subtitle {
          font-size: 12px;
        }
      }
    }

    .chart-container {
      height: 280px;
      padding: 16px 16px 0;
    }

    .stats-legend {
      padding: 12px 16px 16px;
      grid-template-columns: 1fr;
      gap: 8px;

      .legend-item {
        padding: 10px;

        .legend-percent {
          font-size: 13px;
        }
      }
    }
  }
}
</style>