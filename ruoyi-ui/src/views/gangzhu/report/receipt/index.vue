<template>
  <div class="page-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="page-title-area">
        <h2 class="page-title"><i class="el-icon-document"></i> 项目收款台账</h2>
        <span class="page-desc">按项目统计月度收款数据，支持月份筛选与导出</span>
      </div>
    </div>

    <!-- 筛选栏 -->
    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="queryForm" class="filter-form">
        <el-form-item label="统计月份">
          <el-date-picker
            v-model="queryForm.month"
            type="month"
            placeholder="选择月份"
            format="yyyy年MM月"
            value-format="yyyy-MM"
            style="width: 160px;"
          />
        </el-form-item>
        <el-form-item label="项目名称">
          <el-input
            v-model="queryForm.projectName"
            placeholder="输入项目名称"
            clearable
            style="width: 200px;"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery" :loading="loading">查询</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
          <el-button type="success" icon="el-icon-download" @click="handleExport">导出</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 汇总统计 -->
    <el-row :gutter="16" class="summary-row">
      <el-col :xs="12" :sm="6" v-for="s in summaryStats" :key="s.key">
        <div class="summary-card" :class="'sc-' + s.theme">
          <div class="sc-label">{{ s.label }}</div>
          <div class="sc-value">{{ formatYuan(s.value) }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- 柱状图 -->
    <el-card class="chart-card" shadow="never">
      <div slot="header" class="card-head">
        <span><i class="el-icon-s-data"></i> 项目收款对比（{{ queryForm.month }}）</span>
      </div>
      <div ref="barChart" class="bar-chart"></div>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <div slot="header" class="card-head">
        <span><i class="el-icon-s-order"></i> 收款明细列表</span>
        <span class="head-tip">共 {{ tableData.length }} 个项目</span>
      </div>
      <el-table
        :data="filteredData"
        v-loading="loading"
        stripe
        :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: '600' }"
        show-summary
        :summary-method="getSummary"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="projectName" label="项目名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="projectCode" label="项目编码" width="130" />
        <el-table-column prop="receivableAmount" label="应收金额（元）" width="140" align="right">
          <template slot-scope="{ row }">
            <span class="amt blue">¥{{ formatNum(row.receivableAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="receivedAmount" label="实收金额（元）" width="140" align="right">
          <template slot-scope="{ row }">
            <span class="amt green">¥{{ formatNum(row.receivedAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="overdueAmount" label="逾期金额（元）" width="140" align="right">
          <template slot-scope="{ row }">
            <span class="amt red">¥{{ formatNum(row.overdueAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="collectionRate" label="收款率" width="100" align="center">
          <template slot-scope="{ row }">
            <el-progress
              :percentage="row.collectionRate"
              :stroke-width="8"
              :color="getRateColor(row.collectionRate)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="monthlyNewIncome" label="本月新增（元）" width="140" align="right">
          <template slot-scope="{ row }">
            <span class="amt teal">¥{{ formatNum(row.monthlyNewIncome) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="mini" @click="handleDetail(row)">明细</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 项目明细弹窗 -->
    <el-dialog :title="detailTitle" :visible.sync="detailVisible" width="700px">
      <div v-if="currentProject">
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="应收金额">¥{{ formatNum(currentProject.receivableAmount) }}</el-descriptions-item>
          <el-descriptions-item label="实收金额">¥{{ formatNum(currentProject.receivedAmount) }}</el-descriptions-item>
          <el-descriptions-item label="逾期金额">¥{{ formatNum(currentProject.overdueAmount) }}</el-descriptions-item>
          <el-descriptions-item label="收款率">{{ currentProject.collectionRate }}%</el-descriptions-item>
          <el-descriptions-item label="本月新增">¥{{ formatNum(currentProject.monthlyNewIncome) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentProject.status)" size="small">{{ getStatusText(currentProject.status) }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>
        <div ref="trendChart" class="trend-chart" style="margin-top: 24px;"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as echarts from 'echarts'

const mockData = [
  { id: 1, projectName: '港区人才公寓A区', projectCode: 'GZ-RZGY-A', receivableAmount: 1350000, receivedAmount: 1250000, overdueAmount: 45000, collectionRate: 93, monthlyNewIncome: 1250000, status: 'normal' },
  { id: 2, projectName: '港区人才公寓B区', projectCode: 'GZ-RZGY-B', receivableAmount: 1050000, receivedAmount: 980000,  overdueAmount: 28000, collectionRate: 93, monthlyNewIncome: 980000,  status: 'normal' },
  { id: 3, projectName: '保租房项目一期',   projectCode: 'GZ-BZF-1',  receivableAmount: 680000,  receivedAmount: 580000,  overdueAmount: 72000, collectionRate: 85, monthlyNewIncome: 580000,  status: 'warning' },
  { id: 4, projectName: '保租房项目二期',   projectCode: 'GZ-BZF-2',  receivableAmount: 820000,  receivedAmount: 780000,  overdueAmount: 18000, collectionRate: 95, monthlyNewIncome: 780000,  status: 'normal' },
  { id: 5, projectName: '市场租赁项目',     projectCode: 'GZ-SCZL',   receivableAmount: 490000,  receivedAmount: 440000,  overdueAmount: 22000, collectionRate: 90, monthlyNewIncome: 440000,  status: 'normal' }
]

export default {
  name: 'ReceiptLedger',
  data() {
    const now = new Date()
    return {
      loading: false,
      queryForm: {
        month: `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`,
        projectName: ''
      },
      tableData: [...mockData],
      detailVisible: false,
      currentProject: null,
      barChartInst: null,
      trendChartInst: null
    }
  },
  computed: {
    detailTitle() {
      return this.currentProject ? this.currentProject.projectName + ' - 月度明细' : '月度明细'
    },
    filteredData() {
      if (!this.queryForm.projectName) return this.tableData
      return this.tableData.filter(r => r.projectName.includes(this.queryForm.projectName))
    },
    summaryStats() {
      const d = this.tableData
      const totalReceivable = d.reduce((s, r) => s + r.receivableAmount, 0)
      const totalReceived   = d.reduce((s, r) => s + r.receivedAmount, 0)
      const totalOverdue    = d.reduce((s, r) => s + r.overdueAmount, 0)
      const rate = totalReceivable ? Math.round(totalReceived / totalReceivable * 100) : 0
      return [
        { key: 'receivable', label: '应收总额', value: totalReceivable, theme: 'blue' },
        { key: 'received',   label: '实收总额', value: totalReceived,   theme: 'green' },
        { key: 'overdue',    label: '逾期总额', value: totalOverdue,    theme: 'red' },
        { key: 'rate',       label: '综合收款率', value: rate + '%',    theme: 'teal', noYuan: true }
      ]
    }
  },
  mounted() {
    this.$nextTick(() => this.initBarChart())
  },
  beforeDestroy() {
    if (this.barChartInst) this.barChartInst.dispose()
    if (this.trendChartInst) this.trendChartInst.dispose()
  },
  methods: {
    handleQuery() {
      this.loading = true
      setTimeout(() => { this.loading = false }, 500)
      this.$nextTick(() => this.initBarChart())
    },
    handleReset() {
      const now = new Date()
      this.queryForm = {
        month: `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`,
        projectName: ''
      }
      this.handleQuery()
    },
    handleExport() {
      this.$message.success('正在导出项目收款台账，请稍候...')
    },
    handleDetail(row) {
      this.currentProject = row
      this.detailVisible = true
      this.$nextTick(() => this.initTrendChart())
    },
    initBarChart() {
      if (!this.$refs.barChart) return
      if (this.barChartInst) this.barChartInst.dispose()
      this.barChartInst = echarts.init(this.$refs.barChart)
      const names    = this.filteredData.map(r => r.projectName)
      const received = this.filteredData.map(r => r.receivedAmount)
      const overdue  = this.filteredData.map(r => r.overdueAmount)
      this.barChartInst.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        legend: { data: ['实收金额', '逾期金额'], top: 0 },
        grid: { left: '2%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: names },
        yAxis: { type: 'value', axisLabel: { formatter: v => (v / 10000).toFixed(0) + '万' } },
        series: [
          { name: '实收金额', type: 'bar', data: received, barWidth: 30,
            itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#16a34a' }, { offset: 1, color: '#86efac' }
            ])}
          },
          { name: '逾期金额', type: 'bar', data: overdue, barWidth: 30,
            itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#dc2626' }, { offset: 1, color: '#fca5a5' }
            ])}
          }
        ]
      })
    },
    initTrendChart() {
      if (!this.$refs.trendChart || !this.currentProject) return
      if (this.trendChartInst) this.trendChartInst.dispose()
      this.trendChartInst = echarts.init(this.$refs.trendChart)
      const months = ['10月', '11月', '12月', '1月', '2月', '3月']
      const base = this.currentProject.receivedAmount
      const data = months.map(() => Math.round(base * (0.85 + Math.random() * 0.3)))
      this.trendChartInst.setOption({
        title: { text: '近6个月收款趋势', left: 'center', textStyle: { fontSize: 13 } },
        tooltip: { trigger: 'axis', formatter: '{b}: ¥{c}' },
        xAxis: { type: 'category', data: months },
        yAxis: { type: 'value', axisLabel: { formatter: v => (v / 10000).toFixed(0) + '万' } },
        series: [{ data, type: 'line', smooth: true,
          itemStyle: { color: '#2563eb' },
          areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(37,99,235,0.3)' }, { offset: 1, color: 'rgba(37,99,235,0.05)' }
          ])}
        }]
      })
    },
    getSummary({ columns, data }) {
      return columns.map((col, i) => {
        if (i === 0) return '合计'
        const keys = { receivableAmount: true, receivedAmount: true, overdueAmount: true, monthlyNewIncome: true }
        if (keys[col.property]) {
          const sum = data.reduce((s, r) => s + (r[col.property] || 0), 0)
          return '¥' + this.formatNum(sum)
        }
        return ''
      })
    },
    formatYuan(val) {
      if (typeof val === 'string') return val
      if (!val) return '¥0'
      if (val >= 100000000) return `¥${(val / 100000000).toFixed(2)}亿`
      if (val >= 10000) return `¥${(val / 10000).toFixed(1)}万`
      return `¥${Number(val).toLocaleString()}`
    },
    formatNum(v) { return v ? Number(v).toLocaleString() : '0' },
    getRateColor(r) { return r >= 90 ? '#16a34a' : r >= 80 ? '#d97706' : '#dc2626' },
    getStatusType(s) { return s === 'normal' ? 'success' : s === 'warning' ? 'warning' : 'danger' },
    getStatusText(s) { return s === 'normal' ? '正常' : s === 'warning' ? '预警' : '异常' }
  }
}
</script>

<style lang="scss" scoped>
.page-container { padding: 20px; background: #f1f5f9; min-height: calc(100vh - 84px); }

.page-header {
  margin-bottom: 20px;
  .page-title { margin: 0; font-size: 20px; color: #1e293b; font-weight: 700; i { margin-right: 8px; color: #2563eb; } }
  .page-desc  { font-size: 13px; color: #94a3b8; margin-top: 4px; display: block; }
}

.filter-card { margin-bottom: 16px; border-radius: 10px; }
.filter-form { display: flex; flex-wrap: wrap; gap: 0; }

.summary-row { margin-bottom: 16px; }
.summary-card {
  background: #fff; border-radius: 10px; padding: 16px 20px;
  text-align: center; border-top: 3px solid #e2e8f0;
  transition: box-shadow 0.2s;
  &:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.08); }
}
.sc-label { font-size: 13px; color: #94a3b8; margin-bottom: 6px; }
.sc-value  { font-size: 22px; font-weight: 700; color: #1e293b; }
.sc-blue  { border-top-color: #2563eb; .sc-value { color: #2563eb; } }
.sc-green { border-top-color: #16a34a; .sc-value { color: #16a34a; } }
.sc-red   { border-top-color: #dc2626; .sc-value { color: #dc2626; } }
.sc-teal  { border-top-color: #0891b2; .sc-value { color: #0891b2; } }

.chart-card, .table-card { border-radius: 10px; margin-bottom: 16px; }
.bar-chart { height: 280px; }
.trend-chart { height: 260px; }

.card-head {
  display: flex; align-items: center; justify-content: space-between;
  span { font-size: 14px; font-weight: 600; color: #1e293b;
    i { margin-right: 6px; color: #2563eb; }
  }
  .head-tip { font-size: 12px; color: #94a3b8; font-weight: 400; }
}

.amt { font-weight: 600; }
.amt.blue  { color: #2563eb; }
.amt.green { color: #16a34a; }
.amt.red   { color: #dc2626; }
.amt.teal  { color: #0891b2; }
</style>
