<template>
  <div class="ghz-dash">
    <!-- ====== 顶部 Header ====== -->
    <div class="ghz-header">
      <div class="ghz-header-inner">
        <div class="ghz-header-left">
          <div class="ghz-logo-mark">
            <i class="el-icon-s-marketing"></i>
          </div>
          <div class="ghz-header-text">
            <h1 class="ghz-header-title">港好住数据管理驾驶舱</h1>
            <p class="ghz-header-sub">{{ getMonthText() }} · 综合数据概览</p>
          </div>
        </div>
        <div class="ghz-header-right">
          <span class="live-badge">
            <span class="live-dot"></span>
            实时数据
          </span>
          <el-date-picker
            v-model="currentMonth"
            type="month"
            placeholder="选择月份"
            format="yyyy年MM月"
            value-format="yyyy-MM"
            @change="handleMonthChange"
            class="ghz-month-picker"
          />
          <el-button size="small" icon="el-icon-refresh" class="ghz-refresh-btn" :loading="loading" @click="loadAll">刷新</el-button>
        </div>
      </div>
    </div>

    <!-- ====== 主体内容 ====== -->
    <div class="ghz-body" v-loading="loading" element-loading-text="数据加载中...">

      <!-- ① 财务指标 -->
      <div class="ghz-panel">
        <div class="ghz-panel-head">
          <span class="panel-accent blue"></span>
          <span class="panel-title">本月财务指标</span>
          <el-tag size="mini" class="panel-tag">{{ currentMonth }}</el-tag>
        </div>
        <div class="fin-grid">
          <div class="fin-card" v-for="f in financialList" :key="f.key" :class="'fin-' + f.theme">
            <div class="fin-icon"><i :class="f.icon"></i></div>
            <div class="fin-info">
              <div class="fin-amount">{{ formatYuan(financialData[f.key]) }}</div>
              <div class="fin-label">{{ f.label }}</div>
            </div>
            <div class="fin-deco"></div>
          </div>
        </div>
      </div>

      <!-- ② 房源统计 -->
      <div class="ghz-panel">
        <div class="ghz-panel-head">
          <span class="panel-accent teal"></span>
          <span class="panel-title">房源管理概览</span>
          <el-tag size="mini" type="success" class="panel-tag">实时</el-tag>
        </div>
        <!-- 数量卡片 -->
        <el-row :gutter="16" class="house-count-row">
          <el-col :xs="12" :sm="12" :md="6" v-for="h in houseCountList" :key="h.key">
            <div class="hc-card" :class="'hc-' + h.theme">
              <i :class="h.icon" class="hc-icon"></i>
              <div class="hc-num">{{ houseData[h.key] }}</div>
              <div class="hc-label">{{ h.label }}</div>
            </div>
          </el-col>
        </el-row>
        <!-- 出租率仪表 -->
        <el-row :gutter="16" class="house-rate-row">
          <el-col :xs="24" :sm="8" v-for="r in houseRateList" :key="r.key">
            <div class="hr-card">
              <el-progress
                type="circle"
                :percentage="+(houseData[r.key] || 0)"
                :stroke-width="10"
                :color="r.color"
                :width="110"
              />
              <div class="hr-label">{{ r.label }}</div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- ③ 项目收款台账 -->
      <div class="ghz-panel">
        <div class="ghz-panel-head">
          <span class="panel-accent amber"></span>
          <span class="panel-title">项目收款台账</span>
          <el-tag size="mini" type="warning" class="panel-tag">{{ currentMonth }}</el-tag>
        </div>
        <project-ledger :month="currentMonth" ref="projectLedger" />
      </div>

      <!-- ④ 租户画像 -->
      <el-row :gutter="16">
        <el-col :xs="24" :lg="12">
          <div class="ghz-panel">
            <div class="ghz-panel-head">
              <span class="panel-accent rose"></span>
              <span class="panel-title">婚姻状态分布</span>
            </div>
            <div class="profile-bars">
              <div class="pb-row" v-for="m in marriageList" :key="m.key">
                <span class="pb-label">{{ m.label }}</span>
                <div class="pb-track">
                  <div class="pb-fill" :class="'pbf-' + m.theme"
                       :style="{ width: getPct(tenantData.marriageStatus, m.key) + '%' }"></div>
                </div>
                <span class="pb-val">{{ (tenantData.marriageStatus && tenantData.marriageStatus[m.key]) || 0 }}人</span>
                <span class="pb-pct">{{ getPct(tenantData.marriageStatus, m.key) }}%</span>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :lg="12">
          <div class="ghz-panel">
            <div class="ghz-panel-head">
              <span class="panel-accent cyan"></span>
              <span class="panel-title">户籍来源分布</span>
            </div>
            <div class="profile-bars">
              <div class="pb-row" v-for="hh in householdList" :key="hh.key">
                <span class="pb-label">{{ hh.label }}</span>
                <div class="pb-track">
                  <div class="pb-fill" :class="'pbf-' + hh.theme"
                       :style="{ width: getPct(tenantData.household, hh.key) + '%' }"></div>
                </div>
                <span class="pb-val">{{ (tenantData.household && tenantData.household[hh.key]) || 0 }}人</span>
                <span class="pb-pct">{{ getPct(tenantData.household, hh.key) }}%</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

    </div>
  </div>
</template>

<script>
import ProjectLedger from '@/components/Dashboard/ProjectLedger'

export default {
  name: 'Dashboard',
  components: { ProjectLedger },
  data() {
    const now = new Date()
    return {
      loading: false,
      currentMonth: `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`,
      financialData: {
        receivableAmount: 0,
        receivedAmount: 0,
        expectedAmount: 0,
        overdueAmount: 0,
        refundAmount: 0
      },
      houseData: {
        totalRooms: 0,
        rentedRooms: 0,
        emptyRooms: 0,
        maintenanceRooms: 0,
        currentRentRate: 0,
        cumulativeRentRate: 0,
        userConversionRate: 0
      },
      tenantData: {
        marriageStatus: {},
        household: {}
      },
      // 财务指标定义
      financialList: [
        { key: 'receivableAmount',  label: '应收金额',     icon: 'el-icon-coin',           theme: 'blue'   },
        { key: 'receivedAmount',    label: '实收金额',     icon: 'el-icon-wallet',         theme: 'green'  },
        { key: 'expectedAmount',    label: '预计应收金额', icon: 'el-icon-s-finance',      theme: 'amber'  },
        { key: 'overdueAmount',     label: '逾期金额',     icon: 'el-icon-warning-outline',theme: 'red'    },
        { key: 'refundAmount',      label: '退款金额',     icon: 'el-icon-refresh-left',   theme: 'gray'   }
      ],
      // 房源数量指标
      houseCountList: [
        { key: 'totalRooms',       label: '总房间数',   icon: 'el-icon-s-home',         theme: 'slate'  },
        { key: 'rentedRooms',      label: '已租房间数', icon: 'el-icon-user-solid',     theme: 'green'  },
        { key: 'emptyRooms',       label: '空房间数',   icon: 'el-icon-office-building',theme: 'amber'  },
        { key: 'maintenanceRooms', label: '维修房间数', icon: 'el-icon-tools',          theme: 'red'    }
      ],
      // 出租率仪表
      houseRateList: [
        { key: 'currentRentRate',    label: '当前出租率', color: '#0891b2' },
        { key: 'cumulativeRentRate', label: '累计出租率', color: '#16a34a' },
        { key: 'userConversionRate', label: '用户转化率', color: '#7c3aed' }
      ],
      // 婚姻状态
      marriageList: [
        { key: 'married',   label: '已婚', theme: 'blue'  },
        { key: 'unmarried', label: '未婚', theme: 'teal'  },
        { key: 'divorced',  label: '离异', theme: 'amber' },
        { key: 'other',     label: '其他', theme: 'gray'  }
      ],
      // 户籍
      householdList: [
        { key: 'local',    label: '本地户籍', theme: 'green' },
        { key: 'nonlocal', label: '外地户籍', theme: 'blue'  },
        { key: 'unknown',  label: '未知户籍', theme: 'gray'  }
      ]
    }
  },
  mounted() {
    this.loadAll()
  },
  methods: {
    async loadAll() {
      this.loading = true
      try {
        const { mockGetDashboardStats, mockGetHouseStats, mockGetTenantProfile } = await import('@/api/mock/dashboard')
        const [finRes, houseRes, tenantRes] = await Promise.all([
          mockGetDashboardStats(this.currentMonth),
          mockGetHouseStats(),
          mockGetTenantProfile()
        ])
        this.financialData = finRes.data.financial
        this.houseData     = houseRes.data
        this.tenantData    = tenantRes.data
      } catch (e) {
        console.error('加载仪表板数据失败', e)
        this.$message.error('数据加载失败，请稍后重试')
      } finally {
        this.loading = false
      }
    },
    handleMonthChange() {
      this.loadAll()
      this.$nextTick(() => {
        if (this.$refs.projectLedger) this.$refs.projectLedger.loadData()
      })
    },
    getMonthText() {
      const d = new Date()
      return `${d.getFullYear()}年${d.getMonth() + 1}月`
    },
    formatYuan(val) {
      if (!val) return '¥0'
      if (val >= 100000000) return `¥${(val / 100000000).toFixed(2)}亿`
      if (val >= 10000) return `¥${(val / 10000).toFixed(1)}万`
      return `¥${Number(val).toLocaleString()}`
    },
    getPct(obj, key) {
      if (!obj) return 0
      const total = Object.values(obj).reduce((s, v) => s + (v || 0), 0)
      if (!total) return 0
      return Math.round(((obj[key] || 0) / total) * 100)
    }
  }
}
</script>

<style lang="scss" scoped>
// ====== 全局变量 ======
$navy:  #1e3a5f;
$teal:  #0891b2;
$green: #16a34a;
$amber: #d97706;
$red:   #dc2626;
$gray:  #6b7280;
$cyan:  #0e7490;
$rose:  #be185d;
$bg:    #f1f5f9;
$card:  #ffffff;
$radius: 12px;

.ghz-dash {
  background: $bg;
  min-height: calc(100vh - 84px);
  font-family: 'PingFang SC', 'Helvetica Neue', 'Microsoft YaHei', sans-serif;
}

// ====== Header ======
.ghz-header {
  background: linear-gradient(135deg, #0f2444 0%, #1e3a6e 60%, #1a4a80 100%);
  position: relative;
  overflow: hidden;
  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background-image: repeating-linear-gradient(
      45deg,
      rgba(255,255,255,0.02) 0px,
      rgba(255,255,255,0.02) 1px,
      transparent 1px,
      transparent 20px
    );
  }
}
.ghz-header-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 32px;
  position: relative;
  z-index: 1;
}
.ghz-header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.ghz-logo-mark {
  width: 52px; height: 52px;
  background: rgba(255,255,255,0.12);
  border: 1px solid rgba(255,255,255,0.2);
  border-radius: 14px;
  display: flex; align-items: center; justify-content: center;
  backdrop-filter: blur(10px);
  i { font-size: 26px; color: #fff; }
}
.ghz-header-title {
  margin: 0;
  font-size: 24px; font-weight: 700;
  color: #fff;
  letter-spacing: 0.5px;
}
.ghz-header-sub {
  margin: 3px 0 0;
  font-size: 13px;
  color: rgba(255,255,255,0.7);
}
.ghz-header-right {
  display: flex; align-items: center; gap: 12px;
}
.live-badge {
  display: flex; align-items: center; gap: 6px;
  background: rgba(255,255,255,0.12);
  border: 1px solid rgba(255,255,255,0.2);
  border-radius: 20px;
  padding: 4px 12px;
  color: #fff; font-size: 12px;
}
.live-dot {
  width: 7px; height: 7px;
  background: #4ade80;
  border-radius: 50%;
  animation: livePulse 1.5s ease-in-out infinite;
}
@keyframes livePulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(0.8); }
}
.ghz-month-picker {
  width: 160px;
  ::v-deep .el-input__inner {
    border: none;
    background: rgba(255,255,255,0.9);
    border-radius: 20px;
    height: 34px; line-height: 34px;
  }
}
.ghz-refresh-btn {
  border-radius: 20px;
  background: rgba(255,255,255,0.15);
  border-color: rgba(255,255,255,0.3);
  color: #fff;
  &:hover { background: rgba(255,255,255,0.25); }
}

// ====== Body ======
.ghz-body {
  padding: 24px 32px 40px;
  max-width: 1600px;
  margin: 0 auto;
}

// ====== Panel 通用 ======
.ghz-panel {
  background: $card;
  border-radius: $radius;
  padding: 20px 24px;
  margin-bottom: 20px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06), 0 4px 16px rgba(0,0,0,0.04);
}
.ghz-panel-head {
  display: flex; align-items: center; gap: 10px;
  margin-bottom: 20px;
}
.panel-accent {
  display: inline-block;
  width: 4px; height: 20px;
  border-radius: 2px;
  &.blue  { background: #2563eb; }
  &.teal  { background: $teal; }
  &.amber { background: $amber; }
  &.rose  { background: $rose; }
  &.cyan  { background: $cyan; }
}
.panel-title {
  font-size: 16px; font-weight: 600;
  color: #1e293b;
}
.panel-tag { margin-left: 4px; }

// ====== 财务指标 5卡片 ======
.fin-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 14px;
  @media (max-width: 1200px) { grid-template-columns: repeat(3, 1fr); }
  @media (max-width: 768px)  { grid-template-columns: repeat(2, 1fr); }
}
.fin-card {
  background: $card;
  border-radius: 10px;
  padding: 18px 20px;
  border: 1px solid #e8edf5;
  display: flex; align-items: center; gap: 14px;
  position: relative; overflow: hidden;
  transition: box-shadow 0.25s, transform 0.25s;
  &:hover { box-shadow: 0 6px 20px rgba(0,0,0,0.1); transform: translateY(-2px); }
}
.fin-icon {
  width: 48px; height: 48px;
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
  i { font-size: 22px; color: #fff; }
}
.fin-amount {
  font-size: 22px; font-weight: 700; color: #1e293b;
  line-height: 1.2;
}
.fin-label { font-size: 13px; color: #94a3b8; margin-top: 4px; }
.fin-deco {
  position: absolute; right: -20px; bottom: -20px;
  width: 80px; height: 80px;
  border-radius: 50%;
  opacity: 0.06;
}

.fin-blue  .fin-icon { background: linear-gradient(135deg, #2563eb, #60a5fa); }
.fin-green .fin-icon { background: linear-gradient(135deg, $green, #4ade80); }
.fin-amber .fin-icon { background: linear-gradient(135deg, $amber, #fcd34d); }
.fin-red   .fin-icon { background: linear-gradient(135deg, $red, #f87171); }
.fin-gray  .fin-icon { background: linear-gradient(135deg, $gray, #9ca3af); }
.fin-blue  .fin-deco { background: #2563eb; }
.fin-green .fin-deco { background: $green; }
.fin-amber .fin-deco { background: $amber; }
.fin-red   .fin-deco { background: $red; }
.fin-gray  .fin-deco { background: $gray; }
.fin-blue  { border-top: 3px solid #2563eb; }
.fin-green { border-top: 3px solid $green; }
.fin-amber { border-top: 3px solid $amber; }
.fin-red   { border-top: 3px solid $red; }
.fin-gray  { border-top: 3px solid $gray; }

// ====== 房源数量卡片 ======
.house-count-row { margin-bottom: 0; }
.hc-card {
  background: $card;
  border-radius: 10px;
  padding: 20px;
  text-align: center;
  border: 1px solid #e8edf5;
  transition: all 0.25s;
  margin-bottom: 16px;
  &:hover { transform: translateY(-3px); box-shadow: 0 6px 20px rgba(0,0,0,0.1); }
}
.hc-icon {
  font-size: 28px;
  display: block;
  margin-bottom: 8px;
}
.hc-num {
  font-size: 32px; font-weight: 700; color: #1e293b;
  line-height: 1;
}
.hc-label { font-size: 13px; color: #94a3b8; margin-top: 6px; }

.hc-slate .hc-icon { color: #475569; }
.hc-green .hc-icon { color: $green; }
.hc-amber .hc-icon { color: $amber; }
.hc-red   .hc-icon { color: $red; }
.hc-slate { border-top: 3px solid #475569; }
.hc-green { border-top: 3px solid $green; }
.hc-amber { border-top: 3px solid $amber; }
.hc-red   { border-top: 3px solid $red; }

// ====== 出租率仪表 ======
.house-rate-row { margin-bottom: 0; }
.hr-card {
  background: #f8fafc;
  border-radius: 10px;
  padding: 20px;
  text-align: center;
  border: 1px solid #e8edf5;
  display: flex; flex-direction: column; align-items: center; gap: 12px;
}
.hr-label { font-size: 14px; font-weight: 500; color: #475569; }

// ====== 租户画像进度条 ======
.profile-bars { display: flex; flex-direction: column; gap: 16px; }
.pb-row {
  display: flex; align-items: center; gap: 10px;
}
.pb-label {
  width: 70px; font-size: 13px; color: #64748b; text-align: right; flex-shrink: 0;
}
.pb-track {
  flex: 1;
  height: 10px;
  background: #f1f5f9;
  border-radius: 5px;
  overflow: hidden;
}
.pb-fill {
  height: 100%; border-radius: 5px;
  transition: width 0.8s cubic-bezier(0.4, 0, 0.2, 1);
}
.pb-val  { width: 48px; font-size: 13px; color: #475569; text-align: right; }
.pb-pct  { width: 40px; font-size: 12px; color: #94a3b8; text-align: right; }

.pbf-blue  { background: linear-gradient(90deg, #2563eb, #60a5fa); }
.pbf-teal  { background: linear-gradient(90deg, $teal, #22d3ee); }
.pbf-amber { background: linear-gradient(90deg, $amber, #fcd34d); }
.pbf-gray  { background: linear-gradient(90deg, $gray, #d1d5db); }
.pbf-green { background: linear-gradient(90deg, $green, #86efac); }

// ====== 响应式 ======
@media (max-width: 768px) {
  .ghz-header-inner { flex-direction: column; padding: 16px 20px; gap: 14px; }
  .ghz-header-right { flex-wrap: wrap; justify-content: center; }
  .ghz-body { padding: 16px; }
  .ghz-header-title { font-size: 18px; }
  .fin-amount { font-size: 18px; }
  .hc-num { font-size: 26px; }
}
</style>
