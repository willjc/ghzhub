<template>
  <div>
    <div class="ledger-card">
      <div class="card-header">
        <div class="header-icon">
          <i class="el-icon-document-checked"></i>
        </div>
        <div class="header-content">
          <h3 class="card-title">项目台账统计</h3>
          <p class="card-subtitle">月度项目数据汇总分析</p>
        </div>
        <div class="header-actions">
          <el-button size="small" icon="el-icon-refresh" circle @click="handleRefresh" />
        </div>
      </div>

      <!-- 横向柱状图 -->
      <div ref="barChart" class="bar-chart-wrap" v-show="tableData.length > 0"></div>

      <el-table
        :data="tableData"
        style="width: 100%"
        :header-cell-style="{background: '#f5f7fa', color: '#606266'}"
        @row-click="handleDetail"
        class="ledger-table"
      >
        <el-table-column prop="projectName" label="项目名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="projectCode" label="项目编码" width="120" />
        <el-table-column prop="monthlyAmount" label="本月金额" width="120" align="right">
          <template slot-scope="scope">
            <span class="amount-text">¥{{ formatNumber(scope.row.monthlyAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="yearToDateAmount" label="累计金额" width="120" align="right">
          <template slot-scope="scope">
            <span class="amount-text total">¥{{ formatNumber(scope.row.yearToDateAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              @click="handleDetail(scope.row)"
            >
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="table-footer">
        <div class="footer-stats">
          <div class="stat-item">
            <div class="stat-icon">
              <i class="el-icon-collection"></i>
            </div>
            <div class="stat-content">
              <div class="stat-label">项目总数</div>
              <div class="stat-value">{{ totalProjects }}</div>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-icon blue">
              <i class="el-icon-coin"></i>
            </div>
            <div class="stat-content">
              <div class="stat-label">本月总计</div>
              <div class="stat-value">¥{{ formatNumber(monthlyTotal) }}</div>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-icon green">
              <i class="el-icon-s-finance"></i>
            </div>
            <div class="stat-content">
              <div class="stat-label">累计总计</div>
              <div class="stat-value">¥{{ formatNumber(yearToDateTotal) }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 项目详情对话框 -->
    <el-dialog title="项目详情" :visible.sync="detailVisible" width="600px">
      <div v-if="currentProject" class="project-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="项目名称">{{ currentProject.projectName }}</el-descriptions-item>
          <el-descriptions-item label="项目编码">{{ currentProject.projectCode }}</el-descriptions-item>
          <el-descriptions-item label="本月金额">¥{{ formatNumber(currentProject.monthlyAmount) }}</el-descriptions-item>
          <el-descriptions-item label="累计金额">¥{{ formatNumber(currentProject.yearToDateAmount) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentProject.status)">
              {{ getStatusText(currentProject.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ currentProject.updateTime }}</el-descriptions-item>
        </el-descriptions>

        <div style="margin-top: 20px;">
          <h4>月度趋势</h4>
          <div ref="trendChart" class="trend-chart"></div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'ProjectLedger',
  props: {
    month: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      tableData: [],
      detailVisible: false,
      currentProject: null,
      trendChart: null,
      barChart: null,
      loading: false
    }
  },
  computed: {
    totalProjects() {
      return this.tableData.length
    },
    monthlyTotal() {
      return this.tableData.reduce((sum, item) => sum + item.monthlyAmount, 0)
    },
    yearToDateTotal() {
      return this.tableData.reduce((sum, item) => sum + item.yearToDateAmount, 0)
    }
  },
  mounted() {
    this.loadData()
  },
  beforeDestroy() {
    if (this.trendChart) this.trendChart.dispose()
    if (this.barChart) this.barChart.dispose()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const { mockGetProjectLedger } = await import('@/api/mock/dashboard')
        const response = await mockGetProjectLedger(this.month)
        this.tableData = response.rows || []
        this.$nextTick(() => this.initBarChart())
      } catch (error) {
        console.error('加载数据失败:', error)
        this.$message.error('数据加载失败')
      } finally {
        this.loading = false
      }
    },
    initBarChart() {
      if (!this.$refs.barChart || !this.tableData.length) return
      if (this.barChart) this.barChart.dispose()
      this.barChart = echarts.init(this.$refs.barChart)
      const names = this.tableData.map(r => r.projectName)
      const monthly = this.tableData.map(r => r.monthlyAmount)
      const ytd = this.tableData.map(r => r.yearToDateAmount)
      this.barChart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' },
          formatter: params => {
            return params.map(p => `${p.seriesName}: ¥${p.value.toLocaleString()}`).join('<br/>')
          }
        },
        legend: { data: ['本月金额', '累计金额'], top: 0, right: 0 },
        grid: { left: '2%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'value', axisLabel: { formatter: v => v >= 10000 ? (v / 10000).toFixed(0) + '万' : v } },
        yAxis: { type: 'category', data: names, axisLabel: { width: 120, overflow: 'truncate' } },
        series: [
          {
            name: '本月金额', type: 'bar', data: monthly, barMaxWidth: 22,
            itemStyle: { color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [
              { offset: 0, color: '#1d4ed8' }, { offset: 1, color: '#60a5fa' }
            ])},
            label: { show: true, position: 'right', formatter: p => '¥' + (p.value / 10000).toFixed(0) + '万', fontSize: 11, color: '#1d4ed8' }
          },
          {
            name: '累计金额', type: 'bar', data: ytd, barMaxWidth: 22,
            itemStyle: { color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [
              { offset: 0, color: '#15803d' }, { offset: 1, color: '#86efac' }
            ])},
            label: { show: true, position: 'right', formatter: p => '¥' + (p.value / 10000).toFixed(0) + '万', fontSize: 11, color: '#15803d' }
          }
        ]
      })
    },
    handleRefresh() {
      this.loadData()
    },
    handleDetail(row) {
      this.currentProject = row
      this.detailVisible = true
      this.$nextTick(() => {
        this.initTrendChart()
      })
    },
    initTrendChart() {
      if (this.trendChart) {
        this.trendChart.dispose()
      }

      this.trendChart = echarts.init(this.$refs.trendChart)

      // 生成模拟的月度趋势数据
      const months = ['7月', '8月', '9月', '10月', '11月', '12月']
      const monthlyData = months.map((_, index) => {
        return Math.round(this.currentProject.monthlyAmount * (0.8 + Math.random() * 0.4))
      })

      const option = {
        title: {
          text: '近6个月金额趋势',
          left: 'center',
          textStyle: {
            fontSize: 14
          }
        },
        tooltip: {
          trigger: 'axis',
          formatter: '{b}: ¥{c}'
        },
        xAxis: {
          type: 'category',
          data: months
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: '¥{value}'
          }
        },
        series: [
          {
            data: monthlyData,
            type: 'line',
            smooth: true,
            areaStyle: {
              opacity: 0.3
            },
            itemStyle: {
              color: '#409EFF'
            },
            lineStyle: {
              color: '#409EFF'
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
                { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
              ])
            }
          }
        ]
      }

      this.trendChart.setOption(option)
    },
    formatNumber(num) {
      return num.toLocaleString()
    },
    getStatusType(status) {
      const statusMap = {
        normal: 'success',
        warning: 'warning',
        error: 'danger'
      }
      return statusMap[status] || 'info'
    },
    getStatusText(status) {
      const statusMap = {
        normal: '正常',
        warning: '预警',
        error: '异常'
      }
      return statusMap[status] || '未知'
    }
  },
  watch: {
    month: {
      handler() {
        this.loadData()
      }
    }
  }
}
</script>

<style lang="scss" scoped>
// 横向柱状图容器
.bar-chart-wrap {
  height: 260px;
  padding: 12px 16px 0;
  border-bottom: 1px solid #f0f2f5;
}

// 表格行可点击样式
.ledger-table {
  ::v-deep tr { cursor: pointer; }
}

// 项目台账卡片样式
.ledger-card {
  background: #fff;
  border-radius: 12px;
  padding: 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  overflow: hidden;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
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
      flex: 1;

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

    .header-actions {
      .el-button {
        &:hover {
          background-color: rgba(64, 158, 255, 0.1);
          border-color: #409EFF;
          color: #409EFF;
        }
      }
    }
  }

  // 表格样式
  ::v-deep .el-table {
    .el-table__header-wrapper {
      th {
        background-color: #fafbfc !important;
        color: #606266;
        font-weight: 600;
        border-bottom: 1px solid #ebeef5;
      }
    }

    .el-table__body-wrapper {
      tr:hover > td {
        background-color: #f5f7fa !important;
      }
    }

    .amount-text {
      font-weight: 600;
      color: #409EFF;

      &.total {
        color: #67C23A;
      }
    }

    .el-tag {
      border: none;
      border-radius: 4px;
    }
  }

  .table-footer {
    margin: 0;
    padding: 24px;
    background: linear-gradient(135deg, #f8f9fb 0%, #e9ecef 100%);
    border-top: 1px solid #ebeef5;

    .footer-stats {
      display: flex;
      justify-content: space-around;
      align-items: center;

      .stat-item {
        display: flex;
        align-items: center;
        padding: 16px;
        background: #fff;
        border-radius: 10px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
        transition: all 0.2s ease;
        min-width: 140px;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
        }

        .stat-icon {
          width: 40px;
          height: 40px;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          background: linear-gradient(135deg, #909399 0%, #c0c4cc 100%);
          margin-right: 12px;

          &.blue {
            background: linear-gradient(135deg, #409EFF 0%, #36A3F7 100%);
          }

          &.green {
            background: linear-gradient(135deg, #67C23A 0%, #85CE61 100%);
          }

          i {
            color: white;
            font-size: 18px;
          }
        }

        .stat-content {
          .stat-label {
            font-size: 12px;
            color: #909399;
            margin-bottom: 4px;
            line-height: 1;
          }

          .stat-value {
            font-size: 16px;
            font-weight: 600;
            color: #303133;
            line-height: 1;
          }
        }
      }
    }
  }

  .project-detail {
    .trend-chart {
      height: 300px;
      margin-top: 20px;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .ledger-card {
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

    .table-footer {
      padding: 16px;

      .footer-stats {
        flex-direction: column;
        gap: 12px;

        .stat-item {
          width: 100%;
          min-width: unset;

          .stat-value {
            font-size: 14px;
          }
        }
      }
    }
  }
}
</style>