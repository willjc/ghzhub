<template>
  <el-row :gutter="24">
    <!-- 应收金额 -->
    <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
      <div class="finance-card receivable">
        <div class="card-icon">
          <i class="el-icon-coin"></i>
        </div>
        <div class="card-content">
          <div class="card-title">应收金额</div>
          <div class="card-number">
            <count-to :start-val="0" :end-val="data.receivableAmount" :duration="2600" class="number-value" />
            <span class="number-unit">元</span>
          </div>
          <div class="card-trend">
            <i class="el-icon-trend-charts trend-up"></i>
            <span class="trend-text">+12.5%</span>
            <span class="trend-desc">较上月</span>
          </div>
        </div>
          </div>
    </el-col>

    <!-- 实收金额 -->
    <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
      <div class="finance-card received">
        <div class="card-icon">
          <i class="el-icon-wallet"></i>
        </div>
        <div class="card-content">
          <div class="card-title">实收金额</div>
          <div class="card-number">
            <count-to :start-val="0" :end-val="data.receivedAmount" :duration="2600" class="number-value" />
            <span class="number-unit">元</span>
          </div>
          <div class="card-trend">
            <i class="el-icon-trend-charts trend-up"></i>
            <span class="trend-text">+8.3%</span>
            <span class="trend-desc">较上月</span>
          </div>
        </div>
      </div>
    </el-col>

    <!-- 预计应收金额 -->
    <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
      <div class="finance-card expected">
        <div class="card-icon">
          <i class="el-icon-s-finance"></i>
        </div>
        <div class="card-content">
          <div class="card-title">预计应收金额</div>
          <div class="card-number">
            <count-to :start-val="0" :end-val="data.expectedAmount" :duration="2600" class="number-value" />
            <span class="number-unit">元</span>
          </div>
          <div class="card-trend">
            <i class="el-icon-trend-charts trend-up"></i>
            <span class="trend-text">+15.2%</span>
            <span class="trend-desc">较上月</span>
          </div>
        </div>
      </div>
    </el-col>

    <!-- 逾期金额 -->
    <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
      <div class="finance-card overdue">
        <div class="card-icon">
          <i class="el-icon-warning-outline"></i>
        </div>
        <div class="card-content">
          <div class="card-title">逾期金额</div>
          <div class="card-number">
            <count-to :start-val="0" :end-val="data.overdueAmount" :duration="2600" class="number-value danger" />
            <span class="number-unit">元</span>
          </div>
          <div class="card-trend">
            <i class="el-icon-trend-charts trend-down"></i>
            <span class="trend-text danger">-5.1%</span>
            <span class="trend-desc">较上月</span>
          </div>
        </div>
      </div>
    </el-col>

    <!-- 退款金额 -->
    <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
      <div class="finance-card refund">
        <div class="card-icon">
          <i class="el-icon-refresh-left"></i>
        </div>
        <div class="card-content">
          <div class="card-title">退款金额</div>
          <div class="card-number">
            <count-to :start-val="0" :end-val="data.refundAmount" :duration="2600" class="number-value warning" />
            <span class="number-unit">元</span>
          </div>
          <div class="card-trend">
            <i class="el-icon-trend-charts trend-down"></i>
            <span class="trend-text warning">-2.3%</span>
            <span class="trend-desc">较上月</span>
          </div>
        </div>
      </div>
    </el-col>
  </el-row>
</template>

<script>
import CountTo from 'vue-count-to'

export default {
  name: 'FinancialStats',
  components: {
    CountTo
  },
  props: {
    data: {
      type: Object,
      default: () => ({
        receivableAmount: 0,
        receivedAmount: 0,
        expectedAmount: 0,
        overdueAmount: 0,
        refundAmount: 0
      })
    }
  }
}
</script>

<style lang="scss" scoped>
// 财务统计卡片样式
.finance-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border-left: 4px solid #e4e7ed;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.7) 100%);
    z-index: 0;
  }

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);

    .card-icon {
      transform: scale(1.1);
    }

    .card-decoration {
      transform: scale(1.1) rotate(5deg);
    }
  }

  &.receivable {
    border-left-color: #409EFF;
    .card-icon {
      background: linear-gradient(135deg, #409EFF 0%, #36A3F7 100%);
    }
  }

  &.received {
    border-left-color: #67C23A;
    .card-icon {
      background: linear-gradient(135deg, #67C23A 0%, #85CE61 100%);
    }
  }

  &.expected {
    border-left-color: #E6A23C;
    .card-icon {
      background: linear-gradient(135deg, #E6A23C 0%, #EEBE77 100%);
    }
  }

  &.overdue {
    border-left-color: #F56C6C;
    .card-icon {
      background: linear-gradient(135deg, #F56C6C 0%, #F78989 100%);
    }
  }

  &.refund {
    border-left-color: #909399;
    .card-icon {
      background: linear-gradient(135deg, #909399 0%, #A6A9AD 100%);
    }
  }

  .card-icon {
    position: relative;
    z-index: 1;
    width: 50px;
    height: 50px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 16px;
    transition: all 0.3s ease;

    i {
      color: white;
      font-size: 24px;
    }
  }

  .card-content {
    position: relative;
    z-index: 1;
  }

  .card-title {
    font-size: 14px;
    color: #606266;
    margin-bottom: 8px;
    font-weight: 500;
  }

  .card-number {
    display: flex;
    align-items: baseline;
    margin-bottom: 8px;

    .number-value {
      font-size: 28px;
      font-weight: 600;
      color: #303133;

      &.danger {
        color: #F56C6C;
      }

      &.warning {
        color: #E6A23C;
      }
    }

    .number-unit {
      margin-left: 4px;
      font-size: 14px;
      color: #909399;
    }
  }

  .card-trend {
    display: flex;
    align-items: center;
    font-size: 12px;

    .trend-text {
      margin-left: 4px;

      &.warning {
        color: #E6A23C;
      }

      &:not(.warning) {
        color: #67C23A;
      }
    }

    .trend-desc {
      color: #909399;
      font-size: 12px;
      margin-left: 6px;
    }

    .trend-up {
      color: #67C23A;
    }

    .trend-down {
      color: #F56C6C;
    }

    i {
      font-size: 12px;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .finance-card {
    padding: 20px;
    margin-bottom: 16px;

    .card-icon {
      width: 50px;
      height: 50px;
      margin-bottom: 16px;

      i {
        font-size: 22px;
      }
    }

    .card-number .number-value {
      font-size: 24px;
    }

    .card-title {
      font-size: 14px;
    }
  }
}
</style>