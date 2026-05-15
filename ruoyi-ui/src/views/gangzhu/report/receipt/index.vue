<template>
  <div class="page-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="page-title-area">
        <h2 class="page-title"><i class="el-icon-document"></i> 项目收款台账</h2>
        <span class="page-desc">应收基于账单应付日期，实收基于实际支付时间，支持日/周/月/年维度筛选与导出</span>
      </div>
    </div>

    <!-- 筛选栏 -->
    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="queryForm" class="filter-form">
        <el-form-item label="统计维度">
          <el-select v-model="queryForm.periodType" style="width: 100px;" @change="onPeriodTypeChange">
            <el-option label="日" value="day" />
            <el-option label="周" value="week" />
            <el-option label="月" value="month" />
            <el-option label="年" value="year" />
          </el-select>
        </el-form-item>
        <el-form-item label="统计时段">
          <el-date-picker
            v-if="queryForm.periodType === 'day'"
            v-model="queryForm.periodValue"
            type="date" placeholder="选择日期"
            format="yyyy-MM-dd" value-format="yyyy-MM-dd"
            style="width: 160px;"
          />
          <el-date-picker
            v-else-if="queryForm.periodType === 'week'"
            v-model="queryForm.periodValueWeek"
            type="week" placeholder="选择周"
            format="yyyy 第 WW 周"
            style="width: 160px;"
            @change="onWeekChange"
          />
          <el-date-picker
            v-else-if="queryForm.periodType === 'month'"
            v-model="queryForm.periodValue"
            type="month" placeholder="选择月份"
            format="yyyy年MM月" value-format="yyyy-MM"
            style="width: 160px;"
          />
          <el-date-picker
            v-else
            v-model="queryForm.periodValue"
            type="year" placeholder="选择年份"
            format="yyyy年" value-format="yyyy"
            style="width: 160px;"
          />
        </el-form-item>
        <el-form-item label="项目">
          <el-select v-model="queryForm.projectId" placeholder="全部项目" clearable filterable style="width: 200px;">
            <el-option v-for="p in projectList" :key="p.projectId" :label="p.projectName" :value="p.projectId" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery" :loading="loading">查询</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 汇总统计 -->
    <el-row :gutter="16" class="summary-row">
      <el-col :xs="12" :sm="6" v-for="s in summaryStats" :key="s.key">
        <div class="summary-card" :class="'sc-' + s.theme">
          <div class="sc-label">
            {{ s.label }}
            <el-tooltip v-if="s.tip" effect="dark" :content="s.tip" placement="top">
              <i class="el-icon-question sc-tip"></i>
            </el-tooltip>
          </div>
          <div class="sc-value">{{ s.value }}</div>
          <div class="sc-extra" v-if="s.extra">{{ s.extra }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- 口径说明 -->
    <el-alert class="metric-tip" type="info" show-icon :closable="false">
      <template slot="title">
        <span class="tip-text">
          <b>📊 数据口径说明：</b>
          <span class="tip-item"><b class="t-blue">应收总额</b> = 所选时段内 <em>应付日期(到期日)</em> 落入的全部账单金额</span>
          <span class="tip-item"><b class="t-green">实收总额</b> = 所选时段内 <em>实际支付时间</em> 落入的已支付金额</span>
          <span class="tip-item"><b class="t-red">逾期总额</b> = 所选时段内应付但<em>尚未结清</em>的欠款（账单金额-已付金额）</span>
          <span class="tip-item"><b class="t-teal">综合收款率</b> = 实收总额 ÷ 应收总额 × 100%</span>
        </span>
      </template>
    </el-alert>

    <!-- 柱状图 -->
    <el-card class="chart-card" shadow="never">
      <div slot="header" class="card-head">
        <span><i class="el-icon-s-data"></i> 项目收款对比</span>
      </div>
      <div ref="barChart" class="bar-chart"></div>
    </el-card>

    <!-- Tab切换：收款明细 / 项目汇总 -->
    <el-card class="table-card" shadow="never">
      <el-tabs v-model="activeTab">
        <!-- Tab1: 收款明细（默认） -->
        <el-tab-pane label="收款明细" name="detail">
          <!-- 明细汇总卡片 -->
          <el-row :gutter="12" class="detail-summary-row" v-if="detailSummary">
            <el-col :xs="12" :sm="6">
              <div class="ds-card ds-blue">
                <div class="ds-label">
                  明细总笔数
                  <el-tooltip effect="dark" content="所选时段、项目、账单类型条件下，已支付账单的总条数" placement="top">
                    <i class="el-icon-question ds-tip"></i>
                  </el-tooltip>
                </div>
                <div class="ds-value">{{ detailSummary.totalCount || 0 }} 笔</div>
              </div>
            </el-col>
            <el-col :xs="12" :sm="6">
              <div class="ds-card ds-green">
                <div class="ds-label">
                  实收总额
                  <el-tooltip effect="dark" content="当前筛选条件下所有已支付账单的实付金额合计" placement="top">
                    <i class="el-icon-question ds-tip"></i>
                  </el-tooltip>
                </div>
                <div class="ds-value">¥{{ formatNum(detailSummary.totalAmount || 0) }}</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12">
              <div class="ds-card ds-teal">
                <div class="ds-label">
                  分类统计
                  <el-tooltip effect="dark" content="按账单类型(押金/租金/水电费等)分别统计的笔数与金额" placement="top">
                    <i class="el-icon-question ds-tip"></i>
                  </el-tooltip>
                </div>
                <div class="ds-value-small">
                  <span v-for="t in (detailSummary.typeStats || [])" :key="t.name" class="type-tag">
                    {{ t.name }}: {{ t.count }}笔 / ¥{{ formatNum(t.amount) }}
                  </span>
                  <span v-if="!(detailSummary.typeStats || []).length" class="muted">-</span>
                </div>
              </div>
            </el-col>
          </el-row>

          <div class="detail-toolbar">
            <el-form :inline="true" size="small">
              <el-form-item label="账单类型">
                <el-select v-model="detailQuery.billType" placeholder="全部" clearable style="width: 120px;" @change="reloadDetailAndSummary">
                  <el-option label="押金" value="1" />
                  <el-option label="租金" value="2" />
                  <el-option label="水费" value="3" />
                  <el-option label="电费" value="4" />
                  <el-option label="燃气费" value="5" />
                  <el-option label="物业费" value="6" />
                  <el-option label="其他" value="7" />
                </el-select>
              </el-form-item>
            </el-form>
            <el-button type="success" icon="el-icon-download" size="small" @click="handleExportDetail">导出明细</el-button>
          </div>
          <el-table :data="detailData" v-loading="detailLoading" stripe size="small"
            :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: '600' }">
            <el-table-column type="index" label="序号" width="50" align="center" />
            <el-table-column prop="tenantName" label="租户姓名" width="100" />
            <el-table-column prop="projectName" label="项目名称" width="130" show-overflow-tooltip />
            <el-table-column prop="houseAddress" label="房源" min-width="180" show-overflow-tooltip />
            <el-table-column prop="billTypeText" label="账单类型" width="80" align="center" />
            <el-table-column prop="billPeriod" label="账期" width="100" align="center" />
            <el-table-column prop="billAmount" label="账单金额" width="110" align="right">
              <template slot-scope="{ row }"><span class="amt blue">¥{{ formatNum(row.billAmount) }}</span></template>
            </el-table-column>
            <el-table-column prop="paidAmount" label="实付金额" width="110" align="right">
              <template slot-scope="{ row }"><span class="amt green">¥{{ formatNum(row.paidAmount) }}</span></template>
            </el-table-column>
            <el-table-column prop="payTime" label="支付时间" width="160" />
            <el-table-column prop="payMethodText" label="支付方式" width="100" align="center" />
          </el-table>
          <pagination v-show="detailTotal > 0" :total="detailTotal" :page.sync="detailQuery.pageNum" :limit.sync="detailQuery.pageSize" @pagination="loadDetail" />
        </el-tab-pane>

        <!-- Tab2: 项目汇总 -->
        <el-tab-pane label="项目汇总" name="summary">
          <el-table :data="projectData" v-loading="loading" stripe show-summary :summary-method="getSummary"
            :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: '600' }">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="projectName" label="项目名称" min-width="160" show-overflow-tooltip />
            <el-table-column prop="receivableAmount" width="160" align="right">
              <template slot="header">
                应收金额（元）
                <el-tooltip effect="dark" content="该项目在所选时段内应付日期落入的全部账单金额合计" placement="top">
                  <i class="el-icon-question th-tip"></i>
                </el-tooltip>
              </template>
              <template slot-scope="{ row }"><span class="amt blue">¥{{ formatNum(row.receivableAmount) }}</span></template>
            </el-table-column>
            <el-table-column prop="receivedAmount" width="160" align="right">
              <template slot="header">
                实收金额（元）
                <el-tooltip effect="dark" content="该项目在所选时段内已实际收到的金额合计（按支付时间）" placement="top">
                  <i class="el-icon-question th-tip"></i>
                </el-tooltip>
              </template>
              <template slot-scope="{ row }"><span class="amt green">¥{{ formatNum(row.receivedAmount) }}</span></template>
            </el-table-column>
            <el-table-column prop="overdueAmount" width="160" align="right">
              <template slot="header">
                逾期金额（元）
                <el-tooltip effect="dark" content="该项目在所选时段内应付但尚未结清的欠款（账单金额-已付金额）" placement="top">
                  <i class="el-icon-question th-tip"></i>
                </el-tooltip>
              </template>
              <template slot-scope="{ row }"><span class="amt red">¥{{ formatNum(row.overdueAmount) }}</span></template>
            </el-table-column>
            <el-table-column prop="billCount" width="110" align="center">
              <template slot="header">
                应收笔数
                <el-tooltip effect="dark" content="该项目所选时段内应支付的账单数量" placement="top">
                  <i class="el-icon-question th-tip"></i>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column prop="paidCount" width="110" align="center">
              <template slot="header">
                已收笔数
                <el-tooltip effect="dark" content="该项目所选时段内实际收到款项的账单数量" placement="top">
                  <i class="el-icon-question th-tip"></i>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column prop="overdueCount" width="110" align="center">
              <template slot="header">
                逾期笔数
                <el-tooltip effect="dark" content="该项目所选时段内应支付但尚未结清的账单数量" placement="top">
                  <i class="el-icon-question th-tip"></i>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column prop="collectionRate" width="170" align="center">
              <template slot="header">
                收款率
                <el-tooltip effect="dark" content="收款率 = 实收金额 ÷ 应收金额 × 100%" placement="top">
                  <i class="el-icon-question th-tip"></i>
                </el-tooltip>
              </template>
              <template slot-scope="{ row }">
                <el-progress :percentage="row.collectionRate" :stroke-width="8" :color="getRateColor(row.collectionRate)" />
              </template>
            </el-table-column>
            <el-table-column prop="status" width="90" align="center">
              <template slot="header">
                状态
                <el-tooltip effect="dark" content="收款率≥90% 正常 / 80%-90% 预警 / 低于80% 异常" placement="top">
                  <i class="el-icon-question th-tip"></i>
                </el-tooltip>
              </template>
              <template slot-scope="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getReceiptSummary, getReceiptDetail, getReceiptDetailSummary, getProjectList } from "@/api/gangzhu/report"

export default {
  name: 'ReceiptLedger',
  data() {
    const now = new Date()
    return {
      loading: false,
      activeTab: 'detail',
      queryForm: {
        periodType: 'month',
        periodValue: `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`,
        periodValueWeek: null,
        projectId: null
      },
      projectList: [],
      projectData: [],
      summaryData: {},
      // 收款明细
      detailLoading: false,
      detailData: [],
      detailTotal: 0,
      detailSummary: null,
      detailQuery: { pageNum: 1, pageSize: 20, billType: null },
      barChartInst: null
    }
  },
  computed: {
    summaryStats() {
      const s = this.summaryData
      return [
        { key: 'receivable', label: '应收总额', value: this.formatYuan(s.totalReceivable || 0), extra: (s.totalBillCount || 0) + ' 笔', theme: 'blue',
          tip: '所选时段内应付日期(到期日)落入的全部账单金额（已关闭的除外）' },
        { key: 'received', label: '实收总额', value: this.formatYuan(s.totalReceived || 0), extra: (s.totalPaidCount || 0) + ' 笔', theme: 'green',
          tip: '所选时段内实际支付时间落入的已支付账单金额' },
        { key: 'overdue', label: '逾期总额', value: this.formatYuan(s.totalOverdue || 0), extra: '', theme: 'red',
          tip: '所选时段内应付但尚未结清的欠款（账单金额-已付金额）' },
        { key: 'rate', label: '综合收款率', value: (s.collectionRate || 0) + '%', extra: '', theme: 'teal',
          tip: '实收总额 ÷ 应收总额 × 100%，反映本时段回款情况' }
      ]
    }
  },
  created() {
    this.loadProjectList()
    this.handleQuery()
  },
  beforeDestroy() {
    if (this.barChartInst) this.barChartInst.dispose()
  },
  methods: {
    loadProjectList() {
      getProjectList().then(res => {
        this.projectList = res.rows || res.data || []
      })
    },
    onPeriodTypeChange() {
      const now = new Date()
      switch (this.queryForm.periodType) {
        case 'day':
          this.queryForm.periodValue = now.toISOString().slice(0, 10)
          break
        case 'week':
          this.queryForm.periodValueWeek = now
          this.onWeekChange(now)
          break
        case 'month':
          this.queryForm.periodValue = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
          break
        case 'year':
          this.queryForm.periodValue = String(now.getFullYear())
          break
      }
    },
    onWeekChange(val) {
      if (!val) return
      const d = new Date(val)
      const year = d.getFullYear()
      const oneJan = new Date(year, 0, 1)
      const weekNum = Math.ceil(((d - oneJan) / 86400000 + oneJan.getDay() + 1) / 7)
      this.queryForm.periodValue = `${year}-W${weekNum}`
    },
    handleQuery() {
      this.loading = true
      const params = {
        periodType: this.queryForm.periodType,
        periodValue: this.queryForm.periodValue,
        projectId: this.queryForm.projectId || undefined
      }
      getReceiptSummary(params).then(res => {
        const data = res.data || res
        this.projectData = data.projects || []
        this.summaryData = data.summary || {}
        this.$nextTick(() => this.initBarChart())
      }).finally(() => {
        this.loading = false
      })
      // 同时刷新明细
      this.detailQuery.pageNum = 1
      this.loadDetail()
      this.loadDetailSummary()
    },
    handleReset() {
      const now = new Date()
      this.queryForm = {
        periodType: 'month',
        periodValue: `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`,
        periodValueWeek: null,
        projectId: null
      }
      this.detailQuery.billType = null
      this.handleQuery()
    },
    reloadDetailAndSummary() {
      this.detailQuery.pageNum = 1
      this.loadDetail()
      this.loadDetailSummary()
    },
    loadDetail() {
      this.detailLoading = true
      const params = {
        periodType: this.queryForm.periodType,
        periodValue: this.queryForm.periodValue,
        projectId: this.queryForm.projectId || undefined,
        billType: this.detailQuery.billType || undefined,
        pageNum: this.detailQuery.pageNum,
        pageSize: this.detailQuery.pageSize
      }
      getReceiptDetail(params).then(res => {
        this.detailData = res.rows || []
        this.detailTotal = res.total || 0
      }).finally(() => {
        this.detailLoading = false
      })
    },
    loadDetailSummary() {
      const params = {
        periodType: this.queryForm.periodType,
        periodValue: this.queryForm.periodValue,
        projectId: this.queryForm.projectId || undefined,
        billType: this.detailQuery.billType || undefined
      }
      getReceiptDetailSummary(params).then(res => {
        this.detailSummary = res.data || res
      })
    },
    handleExportDetail() {
      const params = {
        periodType: this.queryForm.periodType,
        periodValue: this.queryForm.periodValue,
        projectId: this.queryForm.projectId || undefined,
        billType: this.detailQuery.billType || undefined
      }
      this.download('system/report/receipt/export', params, `收款明细_${new Date().getTime()}.xlsx`)
    },
    initBarChart() {
      if (!this.$refs.barChart) return
      if (this.barChartInst) this.barChartInst.dispose()
      this.barChartInst = echarts.init(this.$refs.barChart)
      const names = this.projectData.map(r => r.projectName)
      const received = this.projectData.map(r => r.receivedAmount)
      const overdue = this.projectData.map(r => r.overdueAmount)
      this.barChartInst.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        legend: { data: ['实收金额', '逾期金额'], top: 0 },
        grid: { left: '2%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: names, axisLabel: { rotate: 20, overflow: 'truncate', width: 80 } },
        yAxis: { type: 'value', axisLabel: { formatter: v => v >= 10000 ? (v / 10000).toFixed(0) + '万' : v } },
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
    getSummary({ columns, data }) {
      return columns.map((col, i) => {
        if (i === 0) return '合计'
        const amtKeys = { receivableAmount: true, receivedAmount: true, overdueAmount: true }
        const cntKeys = { billCount: true, paidCount: true, overdueCount: true }
        if (amtKeys[col.property]) {
          const sum = data.reduce((s, r) => s + (Number(r[col.property]) || 0), 0)
          return '¥' + this.formatNum(sum)
        }
        if (cntKeys[col.property]) {
          return data.reduce((s, r) => s + (Number(r[col.property]) || 0), 0)
        }
        return ''
      })
    },
    formatYuan(val) {
      if (!val) return '¥0'
      val = Number(val)
      if (val >= 100000000) return `¥${(val / 100000000).toFixed(2)}亿`
      if (val >= 10000) return `¥${(val / 10000).toFixed(1)}万`
      return `¥${val.toLocaleString()}`
    },
    formatNum(v) { return v ? Number(v).toLocaleString() : '0' },
    getRateColor(r) { return r >= 90 ? '#16a34a' : r >= 80 ? '#d97706' : '#dc2626' },
    getStatusType(s) { return s === 'normal' ? 'success' : s === 'warning' ? 'warning' : 'danger' },
    getStatusText(s) { return s === 'normal' ? '正常' : s === 'warning' ? '预警' : '异常' }
  },
  watch: {
    activeTab(val) {
      if (val === 'detail' && this.detailData.length === 0) {
        this.loadDetail()
        this.loadDetailSummary()
      }
    }
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
.sc-tip { color: #cbd5e1; cursor: help; margin-left: 4px; font-size: 13px; }
.sc-tip:hover { color: #64748b; }
.sc-value  { font-size: 22px; font-weight: 700; color: #1e293b; }
.sc-extra  { font-size: 12px; color: #64748b; margin-top: 4px; }
.sc-blue  { border-top-color: #2563eb; .sc-value { color: #2563eb; } }
.sc-green { border-top-color: #16a34a; .sc-value { color: #16a34a; } }
.sc-red   { border-top-color: #dc2626; .sc-value { color: #dc2626; } }
.sc-teal  { border-top-color: #0891b2; .sc-value { color: #0891b2; } }

.metric-tip {
  margin-bottom: 16px; border-radius: 8px;
  background: linear-gradient(90deg, #eff6ff 0%, #ecfdf5 50%, #fef2f2 100%) !important;
  border: 1px solid #dbeafe !important;
  ::v-deep .el-alert__title { font-size: 13px; line-height: 1.7; }
  .tip-text { color: #475569; }
  .tip-item { margin-right: 18px; display: inline-block; }
  .tip-item em { font-style: normal; color: #1e293b; font-weight: 600; }
  .t-blue  { color: #2563eb; }
  .t-green { color: #16a34a; }
  .t-red   { color: #dc2626; }
  .t-teal  { color: #0891b2; }
}

.th-tip { color: #94a3b8; cursor: help; margin-left: 3px; font-size: 13px; }
.th-tip:hover { color: #2563eb; }
.ds-tip { color: #cbd5e1; cursor: help; margin-left: 3px; font-size: 12px; }
.ds-tip:hover { color: #64748b; }
.chart-card, .table-card { border-radius: 10px; margin-bottom: 16px; }
.bar-chart { height: 280px; }
.card-head {
  display: flex; align-items: center; justify-content: space-between;
  span { font-size: 14px; font-weight: 600; color: #1e293b;
    i { margin-right: 6px; color: #2563eb; }
  }
}
.amt { font-weight: 600; }
.amt.blue  { color: #2563eb; }
.amt.green { color: #16a34a; }
.amt.red   { color: #dc2626; }
.detail-summary-row { margin-bottom: 12px; }
.ds-card {
  background: #f8fafc; border-radius: 8px; padding: 10px 14px; height: 100%;
  border-left: 3px solid #cbd5e1;
}
.ds-label { font-size: 12px; color: #64748b; margin-bottom: 4px; }
.ds-value { font-size: 18px; font-weight: 700; color: #1e293b; }
.ds-value-small { font-size: 12px; color: #1e293b; line-height: 1.6; }
.ds-blue  { border-left-color: #2563eb; .ds-value { color: #2563eb; } }
.ds-green { border-left-color: #16a34a; .ds-value { color: #16a34a; } }
.ds-teal  { border-left-color: #0891b2; }
.type-tag { display: inline-block; margin-right: 12px; padding: 2px 8px; background: #e0f2fe; color: #0c4a6e; border-radius: 4px; font-size: 12px; }
.muted { color: #94a3b8; }
.detail-toolbar {
  margin-bottom: 12px; display: flex; justify-content: space-between; align-items: center;
}
</style>
