<template>
  <div>
    <el-row :gutter="24">
      <!-- 学历分布 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <div class="profile-card">
          <div class="card-header">
            <div class="header-icon education">
              <i class="el-icon-medal"></i>
            </div>
            <div class="header-content">
              <h3 class="card-title">学历分布</h3>
              <p class="card-subtitle">租户教育水平统计分析</p>
            </div>
          </div>
          <div class="chart-container">
            <div ref="educationChart" class="chart"></div>
          </div>
          <div class="stats-legend">
            <div class="legend-item" v-for="(item, index) in educationData" :key="index">
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

      <!-- 职业分布 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <div class="profile-card">
          <div class="card-header">
            <div class="header-icon profession">
              <i class="el-icon-suitcase-1"></i>
            </div>
            <div class="header-content">
              <h3 class="card-title">职业类型分布</h3>
              <p class="card-subtitle">租户职业背景统计分析</p>
            </div>
          </div>
          <div class="chart-container">
            <div ref="professionChart" class="chart"></div>
          </div>
          <div class="stats-legend">
            <div class="legend-item" v-for="(item, index) in professionData" :key="index">
              <div class="legend-indicator" :style="{ backgroundColor: colors[index + 5] }"></div>
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
  name: 'EducationJob',
  props: {
    data: {
      type: Object,
      default: () => ({
        education: {},
        profession: {}
      })
    }
  },
  data() {
    return {
      colors: ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc'],
      educationChart: null,
      professionChart: null
    }
  },
  computed: {
    educationData() {
      const total = Object.values(this.data.education).reduce((sum, val) => sum + val, 0)
      return [
        { name: '高中及以下', value: this.data.education.highSchool || 0, percent: ((this.data.education.highSchool || 0) / total * 100).toFixed(1) },
        { name: '大专', value: this.data.education.college || 0, percent: ((this.data.education.college || 0) / total * 100).toFixed(1) },
        { name: '本科', value: this.data.education.bachelor || 0, percent: ((this.data.education.bachelor || 0) / total * 100).toFixed(1) },
        { name: '硕士', value: this.data.education.master || 0, percent: ((this.data.education.master || 0) / total * 100).toFixed(1) },
        { name: '博士', value: this.data.education.doctor || 0, percent: ((this.data.education.doctor || 0) / total * 100).toFixed(1) }
      ]
    },
    professionData() {
      const total = Object.values(this.data.profession).reduce((sum, val) => sum + val, 0)
      return [
        { name: '企业职员', value: this.data.profession.company || 0, percent: ((this.data.profession.company || 0) / total * 100).toFixed(1) },
        { name: '事业单位', value: this.data.profession.civil || 0, percent: ((this.data.profession.civil || 0) / total * 100).toFixed(1) },
        { name: '自由职业', value: this.data.profession.selfEmployed || 0, percent: ((this.data.profession.selfEmployed || 0) / total * 100).toFixed(1) },
        { name: '学生', value: this.data.profession.student || 0, percent: ((this.data.profession.student || 0) / total * 100).toFixed(1) },
        { name: '退休', value: this.data.profession.retired || 0, percent: ((this.data.profession.retired || 0) / total * 100).toFixed(1) },
        { name: '其他', value: this.data.profession.other || 0, percent: ((this.data.profession.other || 0) / total * 100).toFixed(1) }
      ]
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initCharts()
    })
  },
  beforeDestroy() {
    if (this.educationChart) {
      this.educationChart.dispose()
    }
    if (this.professionChart) {
      this.professionChart.dispose()
    }
    window.removeEventListener('resize', this.handleResize)
  },
  methods: {
    initCharts() {
      // 初始化学历分布图表
      this.educationChart = echarts.init(this.$refs.educationChart)
      const educationOption = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c}人 ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: 10,
          top: 'center'
        },
        series: [
          {
            name: '学历分布',
            type: 'pie',
            radius: ['30%', '60%'],
            center: ['40%', '50%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 14,
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: this.educationData.map((item, index) => ({
              value: item.value,
              name: item.name,
              itemStyle: {
                color: this.colors[index]
              }
            }))
          }
        ]
      }
      this.educationChart.setOption(educationOption)

      // 初始化职业分布图表
      this.professionChart = echarts.init(this.$refs.professionChart)
      const professionOption = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c}人 ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: 10,
          top: 'center'
        },
        series: [
          {
            name: '职业分布',
            type: 'pie',
            radius: ['30%', '60%'],
            center: ['40%', '50%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 14,
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: this.professionData.map((item, index) => ({
              value: item.value,
              name: item.name,
              itemStyle: {
                color: this.colors[index + 5]
              }
            }))
          }
        ]
      }
      this.professionChart.setOption(professionOption)

      // 监听窗口大小变化
      window.addEventListener('resize', this.handleResize)
    },
    handleResize() {
      if (this.educationChart) {
        this.educationChart.resize()
      }
      if (this.professionChart) {
        this.professionChart.resize()
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
// 学历职业分析卡片样式（与租户画像保持一致）
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
      margin-right: 16px;
      transition: all 0.3s ease;

      &.education {
        background: linear-gradient(135deg, #67C23A 0%, #85CE61 100%);
      }

      &.profession {
        background: linear-gradient(135deg, #E6A23C 0%, #EEBE77 100%);
      }

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
        color: #67C23A;
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