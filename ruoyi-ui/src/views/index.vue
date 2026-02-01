<template>
  <div class="dashboard-container">
    <!-- 顶部标题区域 -->
    <div class="header-section">
      <div class="header-content">
        <div class="page-header">
          <div class="title-wrapper">
            <div class="title-icon">
              <i class="el-icon-s-marketing"></i>
            </div>
            <div class="title-text">
              <h1 class="page-title">港好住数据统计仪表板</h1>
              <p class="page-subtitle">{{ getCurrentMonthText() }}</p>
            </div>
          </div>
        </div>
        <div class="filter-section">
          <el-date-picker
            v-model="currentMonth"
            type="month"
            placeholder="选择月份"
            format="yyyy年MM月"
            value-format="yyyy-MM"
            @change="handleMonthChange"
            prefix-icon="el-icon-date"
            class="month-picker"
          />
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">

      <!-- 项目台账管理 - 置顶 -->
      <div class="ledger-section top-section">
        <div class="section-header">
          <div class="section-title-wrapper">
            <div class="section-icon primary">
              <i class="el-icon-folder-opened"></i>
            </div>
            <span class="section-text">项目管理台账</span>
            <el-tag size="mini" effect="plain" class="section-tag">核心业务</el-tag>
          </div>
        </div>
        <el-card class="ledger-card featured-card" shadow="never">
          <project-ledger :month="currentMonth" ref="projectLedger" />
        </el-card>
      </div>

      <!-- 核心指标卡片 -->
      <el-row :gutter="20" class="metrics-row">
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="stat-card blue">
            <div class="stat-icon">
              <i class="el-icon-money"></i>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ formatAmount(financialData.receivedAmount) }}</div>
              <div class="stat-label">实收金额</div>
              <div class="stat-trend up">
                <i class="el-icon-top"></i>
                <span>本月</span>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="stat-card green">
            <div class="stat-icon">
              <i class="el-icon-s-finance"></i>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ formatAmount(financialData.receivableAmount) }}</div>
              <div class="stat-label">应收金额</div>
              <div class="stat-trend">
                <i class="el-icon-minus"></i>
                <span>累计</span>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="stat-card orange">
            <div class="stat-icon">
              <i class="el-icon-house"></i>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ houseData.rentedRooms }}/{{ houseData.totalRooms }}</div>
              <div class="stat-label">已租房源</div>
              <div class="stat-trend up">
                <i class="el-icon-top"></i>
                <span>出租率 {{ houseData.currentRentRate }}%</span>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <div class="stat-card purple">
            <div class="stat-icon">
              <i class="el-icon-user"></i>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ houseData.userConversionRate }}%</div>
              <div class="stat-label">转化率</div>
              <div class="stat-trend">
                <i class="el-icon-data-line"></i>
                <span>用户转化</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 数据分析区域 -->
      <el-row :gutter="20" class="analysis-row">
        <!-- 财务指标详情 -->
        <el-col :xs="24" :lg="12">
          <div class="section-card">
            <div class="section-header">
              <div class="section-title-wrapper">
                <div class="section-icon blue">
                  <i class="el-icon-coin"></i>
                </div>
                <span class="section-text">财务指标分析</span>
              </div>
            </div>
            <el-card class="content-card" shadow="never">
              <financial-stats :data="financialData" v-loading="loading" />
            </el-card>
          </div>
        </el-col>

        <!-- 房源状态分析 -->
        <el-col :xs="24" :lg="12">
          <div class="section-card">
            <div class="section-header">
              <div class="section-title-wrapper">
                <div class="section-icon green">
                  <i class="el-icon-office-building"></i>
                </div>
                <span class="section-text">房源状态分析</span>
              </div>
            </div>
            <el-card class="content-card" shadow="never">
              <house-stats :data="houseData" v-loading="loading" />
            </el-card>
          </div>
        </el-col>
      </el-row>

      <!-- 用户画像区域 -->
      <el-row :gutter="20" class="profile-row">
        <el-col :xs="24" :lg="12">
          <div class="section-card">
            <div class="section-header">
              <div class="section-title-wrapper">
                <div class="section-icon orange">
                  <i class="el-icon-user-solid"></i>
                </div>
                <span class="section-text">租户画像分析</span>
              </div>
            </div>
            <el-card class="content-card" shadow="never">
              <tenant-profile :data="tenantData" v-loading="loading" />
            </el-card>
          </div>
        </el-col>
        <el-col :xs="24" :lg="12">
          <div class="section-card">
            <div class="section-header">
              <div class="section-title-wrapper">
                <div class="section-icon purple">
                  <i class="el-icon-s-data"></i>
                </div>
                <span class="section-text">用户构成分析</span>
              </div>
            </div>
            <el-card class="content-card" shadow="never">
              <education-job :data="educationData" v-loading="loading" />
            </el-card>
          </div>
        </el-col>
      </el-row>

    </div>
  </div>
</template>

<script>
import FinancialStats from '@/components/Dashboard/FinancialStats'
import HouseStats from '@/components/Dashboard/HouseStats'
import TenantProfile from '@/components/Dashboard/TenantProfile'
import EducationJob from '@/components/Dashboard/EducationJob'
import ProjectLedger from '@/components/Dashboard/ProjectLedger'

export default {
  name: 'Dashboard',
  components: {
    FinancialStats,
    HouseStats,
    TenantProfile,
    EducationJob,
    ProjectLedger
  },
  data() {
    return {
      loading: false,
      currentMonth: new Date().toISOString().substr(0, 7), // 默认当前月份
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
      educationData: {
        education: {},
        profession: {}
      }
    }
  },
  mounted() {
    this.loadDashboardData()
  },
  methods: {
    async loadDashboardData() {
      this.loading = true
      try {
        // 获取首页统计数据
        await this.loadFinancialData()
        await this.loadHouseData()
        await this.loadTenantData()
        await this.loadEducationData()
      } catch (error) {
        console.error('加载仪表板数据失败:', error)
        this.$message.error('数据加载失败，请稍后重试')
      } finally {
        this.loading = false
      }
    },

    async loadFinancialData() {
      // 使用模拟数据
      const { mockGetDashboardStats } = await import('@/api/mock/dashboard')
      const response = await mockGetDashboardStats(this.currentMonth)
      this.financialData = response.data.financial
    },

    async loadHouseData() {
      const { mockGetHouseStats } = await import('@/api/mock/dashboard')
      const response = await mockGetHouseStats()
      this.houseData = response.data
    },

    async loadTenantData() {
      const { mockGetTenantProfile } = await import('@/api/mock/dashboard')
      const response = await mockGetTenantProfile()
      this.tenantData = response.data
    },

    async loadEducationData() {
      const { mockGetEducationJob } = await import('@/api/mock/dashboard')
      const response = await mockGetEducationJob()
      this.educationData = response.data
    },

    handleMonthChange(month) {
      console.log('切换月份:', month)
      this.loadDashboardData()
    },

    getCurrentMonthText() {
      const date = new Date()
      const year = date.getFullYear()
      const month = date.getMonth() + 1
      return `${year}年${month}月 港好住保租房业务数据概览`
    },

    formatAmount(amount) {
      if (!amount) return '¥0'
      if (amount >= 10000) {
        return `¥${(amount / 10000).toFixed(1)}万`
      }
      return `¥${amount.toLocaleString()}`
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 0;
  background: #f0f2f5;
  min-height: calc(100vh - 84px);
}

// 顶部标题区域
.header-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 0;

  .header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 24px 32px;
    max-width: 1600px;
    margin: 0 auto;
  }

  .page-header {
    flex: 1;

    .title-wrapper {
      display: flex;
      align-items: center;
      gap: 16px;
    }

    .title-icon {
      width: 56px;
      height: 56px;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 14px;
      display: flex;
      align-items: center;
      justify-content: center;
      backdrop-filter: blur(10px);

      i {
        font-size: 28px;
        color: #fff;
      }
    }

    .title-text {
      .page-title {
        margin: 0;
        font-size: 26px;
        color: #fff;
        font-weight: 600;
        letter-spacing: 0.5px;
      }

      .page-subtitle {
        margin: 4px 0 0 0;
        color: rgba(255, 255, 255, 0.85);
        font-size: 13px;
      }
    }
  }

  .filter-section {
    .month-picker {
      width: 200px;

      ::v-deep .el-input__inner {
        border: none;
        border-radius: 24px;
        background: rgba(255, 255, 255, 0.95);
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);

        &:focus {
          box-shadow: 0 4px 20px rgba(255, 255, 255, 0.3);
        }
      }

      ::v-deep .el-input__icon {
        color: #667eea;
      }
    }
  }
}

// 主内容区域
.main-content {
  max-width: 1600px;
  margin: 0 auto;
  padding: 24px 32px 40px;
}

// 统计卡片
.metrics-row {
  margin-bottom: 20px;
}

.stat-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  margin-bottom: 20px;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 4px;
    height: 100%;
  }

  &.blue::before { background: linear-gradient(180deg, #409EFF, #66b3ff); }
  &.green::before { background: linear-gradient(180deg, #67C23A, #85d64d); }
  &.orange::before { background: linear-gradient(180deg, #E6A23C, #f0b857); }
  &.purple::before { background: linear-gradient(180deg, #9C27B0, #b654d1); }

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
  }

  .stat-icon {
    width: 60px;
    height: 60px;
    border-radius: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;

    i {
      font-size: 28px;
      color: #fff;
    }
  }

  &.blue .stat-icon { background: linear-gradient(135deg, #409EFF 0%, #66b3ff 100%); }
  &.green .stat-icon { background: linear-gradient(135deg, #67C23A 0%, #85d64d 100%); }
  &.orange .stat-icon { background: linear-gradient(135deg, #E6A23C 0%, #f0b857 100%); }
  &.purple .stat-icon { background: linear-gradient(135deg, #9C27B0 0%, #b654d1 100%); }

  .stat-content {
    flex: 1;
  }

  .stat-value {
    font-size: 24px;
    font-weight: 700;
    color: #303133;
    line-height: 1.2;
  }

  .stat-label {
    font-size: 13px;
    color: #909399;
    margin-top: 4px;
  }

  .stat-trend {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    font-size: 12px;
    margin-top: 6px;
    padding: 2px 8px;
    border-radius: 10px;

    &.up {
      color: #67C23A;
      background: rgba(103, 194, 58, 0.1);
    }

    &:not(.up) {
      color: #909399;
      background: rgba(144, 147, 153, 0.1);
    }
  }
}

// 区块样式
.section-card {
  margin-bottom: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;

  .section-icon {
    width: 36px;
    height: 36px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;

    i {
      font-size: 18px;
      color: #fff;
    }

    &.primary { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
    &.blue { background: linear-gradient(135deg, #409EFF 0%, #66b3ff 100%); }
    &.green { background: linear-gradient(135deg, #67C23A 0%, #85d64d 100%); }
    &.orange { background: linear-gradient(135deg, #E6A23C 0%, #f0b857 100%); }
    &.purple { background: linear-gradient(135deg, #9C27B0 0%, #b654d1 100%); }
  }

  .section-text {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }

  .section-tag {
    margin-left: 8px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;
    color: #fff;
  }
}

.ledger-section.top-section {
  margin-bottom: 24px;

  .featured-card {
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 4px 20px rgba(102, 126, 234, 0.15);

    ::v-deep .el-card__body {
      padding: 0;
    }
  }
}

.content-card {
  border-radius: 14px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
  }

  ::v-deep .el-card__body {
    padding: 20px;
  }
}

.analysis-row,
.profile-row {
  margin-bottom: 0;
}

// 响应式设计
@media (max-width: 1200px) {
  .main-content {
    padding: 20px;
  }
}

@media (max-width: 768px) {
  .header-section {
    .header-content {
      flex-direction: column;
      padding: 20px;
      gap: 16px;
    }

    .page-header {
      text-align: left;

      .title-wrapper {
        gap: 12px;
      }

      .title-icon {
        width: 48px;
        height: 48px;

        i {
          font-size: 24px;
        }
      }

      .title-text .page-title {
        font-size: 20px;
      }
    }

    .filter-section .month-picker {
      width: 100%;
    }
  }

  .main-content {
    padding: 16px;
  }

  .stat-card {
    padding: 16px;

    .stat-icon {
      width: 50px;
      height: 50px;

      i {
        font-size: 24px;
      }
    }

    .stat-value {
      font-size: 20px;
    }
  }

  .section-title-wrapper {
    .section-text {
      font-size: 14px;
    }
  }
}

@media (max-width: 576px) {
  .header-section {
    .header-content {
      padding: 16px;
    }

    .title-icon {
      width: 42px;
      height: 42px;
    }

    .title-text .page-title {
      font-size: 18px;
    }
  }

  .stat-card {
    .stat-value {
      font-size: 18px;
    }
  }
}
</style>