<template>
  <div class="page-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="page-title-area">
        <h2 class="page-title"><i class="el-icon-s-grid"></i> 自定义报表</h2>
        <span class="page-desc">自由组合统计字段，生成个性化报表并导出</span>
      </div>
    </div>

    <el-row :gutter="16">
      <!-- 左侧：字段配置 -->
      <el-col :xs="24" :md="7">
        <el-card class="config-card" shadow="never">
          <div slot="header" class="card-head">
            <i class="el-icon-setting"></i> 报表配置
          </div>

          <!-- 统计时间 -->
          <div class="config-section">
            <div class="config-title">统计时间范围</div>
            <el-form :model="reportForm" label-position="top" size="small">
              <el-form-item label="开始月份">
                <el-date-picker
                  v-model="reportForm.startMonth"
                  type="month" placeholder="开始月份"
                  format="yyyy年MM月" value-format="yyyy-MM"
                  style="width: 100%;"
                />
              </el-form-item>
              <el-form-item label="结束月份">
                <el-date-picker
                  v-model="reportForm.endMonth"
                  type="month" placeholder="结束月份"
                  format="yyyy年MM月" value-format="yyyy-MM"
                  style="width: 100%;"
                />
              </el-form-item>
            </el-form>
          </div>

          <el-divider />

          <!-- 数据维度 -->
          <div class="config-section">
            <div class="config-title">数据维度（行分组）</div>
            <el-checkbox-group v-model="reportForm.dimensions" class="field-group">
              <el-checkbox v-for="d in dimensionOptions" :key="d.value" :label="d.value" class="field-item">
                {{ d.label }}
              </el-checkbox>
            </el-checkbox-group>
          </div>

          <el-divider />

          <!-- 统计指标 -->
          <div class="config-section">
            <div class="config-title">统计指标（列数据）</div>
            <el-checkbox-group v-model="reportForm.metrics" class="field-group">
              <el-checkbox v-for="m in metricOptions" :key="m.value" :label="m.value" class="field-item">
                {{ m.label }}
              </el-checkbox>
            </el-checkbox-group>
          </div>

          <el-divider />

          <!-- 筛选条件 -->
          <div class="config-section">
            <div class="config-title">筛选条件</div>
            <el-form :model="reportForm" label-position="top" size="small">
              <el-form-item label="项目">
                <el-select v-model="reportForm.projectIds" multiple placeholder="全部项目" style="width: 100%;">
                  <el-option v-for="p in projectOptions" :key="p.value" :label="p.label" :value="p.value" />
                </el-select>
              </el-form-item>
              <el-form-item label="状态">
                <el-select v-model="reportForm.status" placeholder="全部状态" clearable style="width: 100%;">
                  <el-option label="正常" value="normal" />
                  <el-option label="预警" value="warning" />
                  <el-option label="异常" value="error" />
                </el-select>
              </el-form-item>
            </el-form>
          </div>

          <!-- 操作按钮 -->
          <div class="config-actions">
            <el-button type="primary" icon="el-icon-s-data" @click="handleGenerate" :loading="generating" style="width: 100%;">生成报表</el-button>
            <el-button icon="el-icon-download" @click="handleExport" :disabled="!hasResult" style="width: 100%; margin-top: 10px; margin-left: 0;">导出报表</el-button>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：报表结果 -->
      <el-col :xs="24" :md="17">
        <!-- 未生成提示 -->
        <el-card v-if="!hasResult" class="empty-card" shadow="never">
          <div class="empty-state">
            <i class="el-icon-document empty-icon"></i>
            <p class="empty-title">请在左侧配置报表参数</p>
            <p class="empty-sub">选择时间范围、数据维度和统计指标，点击「生成报表」</p>
          </div>
        </el-card>

        <template v-else>
          <!-- 图表区 -->
          <el-card class="result-card" shadow="never" style="margin-bottom: 16px;">
            <div slot="header" class="card-head">
              <span><i class="el-icon-s-data"></i> 可视化图表</span>
              <el-radio-group v-model="chartType" size="mini" @change="renderChart">
                <el-radio-button label="bar">柱状图</el-radio-button>
                <el-radio-button label="line">折线图</el-radio-button>
              </el-radio-group>
            </div>
            <div ref="reportChart" class="report-chart" v-loading="generating"></div>
          </el-card>

          <!-- 数据表格 -->
          <el-card class="result-card" shadow="never">
            <div slot="header" class="card-head">
              <span><i class="el-icon-s-order"></i> 报表数据</span>
              <span class="head-tip">共 {{ resultData.length }} 条记录</span>
            </div>
            <el-table
              :data="resultData"
              stripe
              :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: '600' }"
              size="small"
              v-loading="generating"
              show-summary
              :summary-method="getSummaryMethod"
            >
              <!-- 动态列：维度列 -->
              <el-table-column
                v-for="dim in selectedDimensions"
                :key="'dim_' + dim.value"
                :prop="dim.value"
                :label="dim.label"
                min-width="130"
                show-overflow-tooltip
              />
              <!-- 动态列：指标列 -->
              <el-table-column
                v-for="metric in selectedMetrics"
                :key="'metric_' + metric.value"
                :prop="metric.value"
                :label="metric.label"
                min-width="120"
                align="right"
              >
                <template slot-scope="{ row }">
                  <span :class="'val-' + metric.color">{{ metric.format(row[metric.value]) }}</span>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </template>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'

// 维度选项
const DIMENSIONS = [
  { value: 'projectName',   label: '项目名称' },
  { value: 'month',         label: '月份' },
  { value: 'houseType',     label: '房型' },
  { value: 'tenantCategory',label: '租户类型' }
]

// 指标选项
const METRICS = [
  { value: 'receivableAmount',  label: '应收金额',   color: 'blue',  format: v => '¥' + (v || 0).toLocaleString() },
  { value: 'receivedAmount',    label: '实收金额',   color: 'green', format: v => '¥' + (v || 0).toLocaleString() },
  { value: 'overdueAmount',     label: '逾期金额',   color: 'red',   format: v => '¥' + (v || 0).toLocaleString() },
  { value: 'refundAmount',      label: '退款金额',   color: 'gray',  format: v => '¥' + (v || 0).toLocaleString() },
  { value: 'roomCount',         label: '房间数量',   color: 'teal',  format: v => (v || 0) + '间' },
  { value: 'occupiedCount',     label: '已租数量',   color: 'teal',  format: v => (v || 0) + '间' },
  { value: 'collectionRate',    label: '收款率',     color: 'amber', format: v => (v || 0) + '%' }
]

const PROJECT_OPTIONS = [
  { value: '1', label: '港区人才公寓A区' },
  { value: '2', label: '港区人才公寓B区' },
  { value: '3', label: '保租房项目一期' },
  { value: '4', label: '保租房项目二期' },
  { value: '5', label: '市场租赁项目' }
]

// 生成模拟结果
function genMockResult(dims, metrics) {
  const projects = ['港区人才公寓A区', '港区人才公寓B区', '保租房项目一期', '保租房项目二期', '市场租赁项目']
  const months   = ['2026-01', '2026-02', '2026-03', '2026-04']
  const houseTypes = ['一室', '两室', '三室']
  const categories = ['人才租赁', '保障租赁', '市场租赁']

  const rows = []
  const count = 10 + Math.floor(Math.random() * 10)
  for (let i = 0; i < count; i++) {
    const row = {}
    if (dims.includes('projectName'))    row.projectName    = projects[Math.floor(Math.random() * projects.length)]
    if (dims.includes('month'))          row.month          = months[Math.floor(Math.random() * months.length)]
    if (dims.includes('houseType'))      row.houseType      = houseTypes[Math.floor(Math.random() * houseTypes.length)]
    if (dims.includes('tenantCategory')) row.tenantCategory = categories[Math.floor(Math.random() * categories.length)]
    if (metrics.includes('receivableAmount')) row.receivableAmount = Math.round(50000 + Math.random() * 200000)
    if (metrics.includes('receivedAmount'))   row.receivedAmount   = Math.round(40000 + Math.random() * 180000)
    if (metrics.includes('overdueAmount'))    row.overdueAmount    = Math.round(1000  + Math.random() * 20000)
    if (metrics.includes('refundAmount'))     row.refundAmount     = Math.round(500   + Math.random() * 5000)
    if (metrics.includes('roomCount'))        row.roomCount        = Math.round(50    + Math.random() * 200)
    if (metrics.includes('occupiedCount'))    row.occupiedCount    = Math.round(40    + Math.random() * 180)
    if (metrics.includes('collectionRate'))   row.collectionRate   = Math.round(75    + Math.random() * 25)
    rows.push(row)
  }
  return rows
}

export default {
  name: 'CustomReport',
  data() {
    const now = new Date()
    const ym = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
    return {
      dimensionOptions: DIMENSIONS,
      metricOptions:    METRICS,
      projectOptions:   PROJECT_OPTIONS,
      reportForm: {
        startMonth:  `${now.getFullYear()}-01`,
        endMonth:    ym,
        dimensions:  ['projectName', 'month'],
        metrics:     ['receivableAmount', 'receivedAmount', 'collectionRate'],
        projectIds:  [],
        status:      ''
      },
      generating:  false,
      hasResult:   false,
      resultData:  [],
      chartType:   'bar',
      reportChartInst: null
    }
  },
  computed: {
    selectedDimensions() { return DIMENSIONS.filter(d => this.reportForm.dimensions.includes(d.value)) },
    selectedMetrics()    { return METRICS.filter(m => this.reportForm.metrics.includes(m.value)) }
  },
  beforeDestroy() {
    if (this.reportChartInst) this.reportChartInst.dispose()
  },
  methods: {
    handleGenerate() {
      if (!this.reportForm.dimensions.length) { this.$message.warning('请至少选择一个数据维度'); return }
      if (!this.reportForm.metrics.length)    { this.$message.warning('请至少选择一个统计指标'); return }
      this.generating = true
      setTimeout(() => {
        this.resultData = genMockResult(this.reportForm.dimensions, this.reportForm.metrics)
        this.hasResult  = true
        this.generating = false
        this.$nextTick(() => this.renderChart())
      }, 800)
    },
    handleExport() {
      this.$message.success('正在导出自定义报表，请稍候...')
    },
    renderChart() {
      if (!this.$refs.reportChart || !this.resultData.length) return
      if (this.reportChartInst) this.reportChartInst.dispose()
      this.reportChartInst = echarts.init(this.$refs.reportChart)

      const firstDim = this.selectedDimensions[0]
      const xData = this.resultData.map(r => r[firstDim ? firstDim.value : ''] || '-').slice(0, 12)
      const series = this.selectedMetrics
        .filter(m => ['receivableAmount', 'receivedAmount', 'overdueAmount', 'refundAmount'].includes(m.value))
        .map((m, idx) => {
          const colors = ['#2563eb', '#16a34a', '#dc2626', '#6b7280']
          return {
            name: m.label,
            type: this.chartType,
            data: this.resultData.slice(0, 12).map(r => r[m.value] || 0),
            smooth: true,
            barMaxWidth: 30,
            itemStyle: { color: colors[idx % colors.length] }
          }
        })

      this.reportChartInst.setOption({
        tooltip: { trigger: 'axis' },
        legend: { top: 0, data: series.map(s => s.name) },
        grid: { left: '2%', right: '4%', bottom: '3%', top: '40px', containLabel: true },
        xAxis: { type: 'category', data: xData, axisLabel: { rotate: 30, overflow: 'truncate', width: 80 } },
        yAxis: { type: 'value', axisLabel: { formatter: v => v >= 10000 ? (v / 10000).toFixed(0) + '万' : v } },
        series
      })
    },
    getSummaryMethod({ columns, data }) {
      return columns.map((col, i) => {
        if (i === 0) return '合计'
        const numMetrics = ['receivableAmount', 'receivedAmount', 'overdueAmount', 'refundAmount', 'roomCount', 'occupiedCount']
        if (numMetrics.includes(col.property)) {
          const sum = data.reduce((s, r) => s + (r[col.property] || 0), 0)
          const metric = METRICS.find(m => m.value === col.property)
          return metric ? metric.format(sum) : sum
        }
        return ''
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.page-container { padding: 20px; background: #f1f5f9; min-height: calc(100vh - 84px); }

.page-header {
  margin-bottom: 20px;
  .page-title { margin: 0; font-size: 20px; color: #1e293b; font-weight: 700; i { margin-right: 8px; color: #7c3aed; } }
  .page-desc  { font-size: 13px; color: #94a3b8; margin-top: 4px; display: block; }
}

.config-card, .result-card { border-radius: 10px; }
.config-card { position: sticky; top: 20px; }

.card-head {
  display: flex; align-items: center; justify-content: space-between;
  font-size: 14px; font-weight: 600; color: #1e293b;
  i { margin-right: 6px; color: #7c3aed; }
  .head-tip { font-size: 12px; color: #94a3b8; font-weight: 400; }
}

.config-section { margin-bottom: 4px; }
.config-title { font-size: 12px; font-weight: 600; color: #64748b; text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 10px; }

.field-group { display: flex; flex-direction: column; gap: 4px; }
.field-item { margin-left: 0 !important; line-height: 2; }

.config-actions { margin-top: 16px; }

.empty-card { border-radius: 10px; }
.empty-state {
  display: flex; flex-direction: column; align-items: center;
  padding: 60px 20px; text-align: center;
}
.empty-icon { font-size: 64px; color: #cbd5e1; margin-bottom: 16px; }
.empty-title { font-size: 16px; color: #94a3b8; font-weight: 500; margin: 0 0 8px; }
.empty-sub { font-size: 13px; color: #cbd5e1; margin: 0; }

.report-chart { height: 300px; }

.val-blue  { color: #2563eb; font-weight: 600; }
.val-green { color: #16a34a; font-weight: 600; }
.val-red   { color: #dc2626; font-weight: 600; }
.val-teal  { color: #0891b2; font-weight: 600; }
.val-amber { color: #d97706; font-weight: 600; }
.val-gray  { color: #6b7280; }
</style>
