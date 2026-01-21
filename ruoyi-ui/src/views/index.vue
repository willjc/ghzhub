<template>
  <div class="dashboard-container">
    <!-- 顶部标题区域 -->
    <div class="header-section">
      <div class="page-header">
        <h1 class="page-title">
          <i class="el-icon-s-marketing"></i>
          港好住数据统计仪表板
        </h1>
        <p class="page-subtitle">{{ getCurrentMonthText() }}</p>
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

    <!-- 核心指标概览 -->
    <div class="metrics-section">
      <div class="section-title">
        <i class="el-icon-coin"></i>
        <span>财务指标概览</span>
      </div>
      <el-card class="metrics-card" shadow="hover">
        <financial-stats :data="financialData" v-loading="loading" />
      </el-card>
    </div>

    <!-- 房源状态分析 -->
    <div class="house-section">
      <div class="section-title">
        <i class="el-icon-house"></i>
        <span>房源状态分析</span>
      </div>
      <el-card class="house-card" shadow="hover">
        <house-stats :data="houseData" v-loading="loading" />
      </el-card>
    </div>

    <!-- 用户画像分析 -->
    <el-row :gutter="20" class="profile-section">
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <div class="section-title">
          <i class="el-icon-user"></i>
          <span>租户画像分析</span>
        </div>
        <el-card class="profile-card" shadow="hover">
          <tenant-profile :data="tenantData" v-loading="loading" />
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <div class="section-title">
          <i class="el-icon-s-cooperation"></i>
          <span>用户构成分析</span>
        </div>
        <el-card class="profile-card" shadow="hover">
          <education-job :data="educationData" v-loading="loading" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 项目台账管理 -->
    <div class="ledger-section">
      <div class="section-title">
        <i class="el-icon-document"></i>
        <span>项目台账管理</span>
      </div>
      <el-card class="ledger-card" shadow="hover">
        <project-ledger :month="currentMonth" ref="projectLedger" />
      </el-card>
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
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 0;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: calc(100vh - 84px);
}

// 顶部标题区域
.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30px 40px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border-bottom: 3px solid #409EFF;

  .page-header {
    flex: 1;

    .page-title {
      margin: 0;
      font-size: 28px;
      color: #303133;
      font-weight: 600;
      display: flex;
      align-items: center;
      gap: 10px;

      i {
        color: #409EFF;
        font-size: 32px;
      }
    }

    .page-subtitle {
      margin: 5px 0 0 0;
      color: #606266;
      font-size: 14px;
      opacity: 0.8;
    }
  }

  .filter-section {
    .month-picker {
      width: 240px;

      ::v-deep .el-input__inner {
        border-radius: 20px;
      }
    }
  }
}

// 区块标题样式
.section-title {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;

  i {
    margin-right: 8px;
    color: #409EFF;
    font-size: 18px;
  }

  &::after {
    content: '';
    flex: 1;
    height: 1px;
    background: linear-gradient(to right, #e4e7ed, transparent);
    margin-left: 15px;
  }
}

// 各个区块统一样式
.metrics-section,
.house-section,
.profile-section,
.ledger-section {
  padding: 0 40px 32px;

  .metrics-card,
  .house-card,
  .profile-card,
  .ledger-card {
    border-radius: 16px;
    overflow: hidden;
    transition: all 0.3s ease;
    background: #fff;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .header-section {
    flex-direction: column;
    text-align: center;
    gap: 20px;
    padding: 20px;

    .page-header {
      .page-title {
        font-size: 24px;
      }
    }

    .filter-section {
      .month-picker {
        width: 100%;
      }
    }
  }

  .metrics-section,
  .house-section,
  .profile-section,
  .ledger-section {
    padding: 0 20px 24px;

    .metrics-card,
    .house-card,
    .profile-card,
    .ledger-card {
      border-radius: 12px;

      &:hover {
        transform: translateY(-3px);
      }
    }
  }
}

@media (max-width: 576px) {
  .header-section {
    padding: 16px;
  }

  .page-title {
    font-size: 20px !important;
  }

  .section-title {
    font-size: 14px;
    margin-bottom: 12px;
  }

  .metrics-section,
  .house-section,
  .profile-section,
  .ledger-section {
    padding: 0 16px 20px;
  }
}
</style>