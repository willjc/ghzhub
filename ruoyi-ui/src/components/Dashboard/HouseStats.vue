<template>
  <div>
    <!-- 房源统计卡片 -->
    <el-row :gutter="24">
      <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
        <div class="house-card total">
          <div class="card-icon">
            <i class="el-icon-s-home"></i>
          </div>
          <div class="card-content">
            <div class="card-title">总房间数</div>
            <div class="card-number">
              <count-to :start-val="0" :end-val="data.totalRooms" :duration="2600" class="number-value" />
              <span class="number-unit">间</span>
            </div>
            <div class="card-trend">
              <i class="el-icon-trend-charts trend-up"></i>
              <span class="trend-text">+5.2%</span>
            </div>
          </div>
        </div>
      </el-col>

      <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
        <div class="house-card rented">
          <div class="card-icon">
            <i class="el-icon-user-solid"></i>
          </div>
          <div class="card-content">
            <div class="card-title">已租房间数</div>
            <div class="card-number">
              <count-to :start-val="0" :end-val="data.rentedRooms" :duration="2600" class="number-value" />
              <span class="number-unit">间</span>
            </div>
            <div class="card-trend">
              <i class="el-icon-trend-charts trend-up"></i>
              <span class="trend-text">+8.7%</span>
            </div>
          </div>
        </div>
      </el-col>

      <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
        <div class="house-card empty">
          <div class="card-icon">
            <i class="el-icon-office-building"></i>
          </div>
          <div class="card-content">
            <div class="card-title">空房间数</div>
            <div class="card-number">
              <count-to :start-val="0" :end-val="data.emptyRooms" :duration="2600" class="number-value" />
              <span class="number-unit">间</span>
            </div>
            <div class="card-trend">
              <i class="el-icon-trend-charts trend-down"></i>
              <span class="trend-text">-3.1%</span>
            </div>
          </div>
        </div>
      </el-col>

      <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
        <div class="house-card maintenance">
          <div class="card-icon">
            <i class="el-icon-tools"></i>
          </div>
          <div class="card-content">
            <div class="card-title">维修房间数</div>
            <div class="card-number">
              <count-to :start-val="0" :end-val="data.maintenanceRooms" :duration="2600" class="number-value warning" />
              <span class="number-unit">间</span>
            </div>
            <div class="card-trend">
              <i class="el-icon-trend-charts trend-up"></i>
              <span class="trend-text warning">+2.4%</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 出租率和转化率 -->
    <el-row :gutter="24" class="rate-row">
      <el-col :xs="24" :sm="8" :md="8" :lg="8" :xl="8">
        <div class="rate-card">
          <div class="rate-header">
            <div class="rate-icon">
              <i class="el-icon-pie-chart"></i>
            </div>
            <div class="rate-info">
              <div class="rate-title">当前出租率</div>
              <div class="rate-desc">本月实时数据</div>
            </div>
          </div>
          <div class="rate-content">
            <div class="rate-number">{{ data.currentRentRate }}<span class="unit">%</span></div>
            <el-progress
              :percentage="data.currentRentRate"
              :color="getProgressColor(data.currentRentRate)"
              :stroke-width="8"
              :show-text="false"
            />
          </div>
        </div>
      </el-col>

      <el-col :xs="24" :sm="8" :md="8" :lg="8" :xl="8">
        <div class="rate-card">
          <div class="rate-header">
            <div class="rate-icon">
              <i class="el-icon-data-analysis"></i>
            </div>
            <div class="rate-info">
              <div class="rate-title">累计出租率</div>
              <div class="rate-desc">年度累计数据</div>
            </div>
          </div>
          <div class="rate-content">
            <div class="rate-number">{{ data.cumulativeRentRate }}<span class="unit">%</span></div>
            <el-progress
              :percentage="data.cumulativeRentRate"
              :color="getProgressColor(data.cumulativeRentRate)"
              :stroke-width="8"
              :show-text="false"
            />
          </div>
        </div>
      </el-col>

      <el-col :xs="24" :sm="8" :md="8" :lg="8" :xl="8">
        <div class="rate-card">
          <div class="rate-header">
            <div class="rate-icon">
              <i class="el-icon-user"></i>
            </div>
            <div class="rate-info">
              <div class="rate-title">用户转化率</div>
              <div class="rate-desc">访问转化数据</div>
            </div>
          </div>
          <div class="rate-content">
            <div class="rate-number">{{ data.userConversionRate }}<span class="unit">%</span></div>
            <el-progress
              :percentage="data.userConversionRate"
              :color="getProgressColor(data.userConversionRate)"
              :stroke-width="8"
              :show-text="false"
            />
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import CountTo from 'vue-count-to'

export default {
  name: 'HouseStats',
  components: {
    CountTo
  },
  props: {
    data: {
      type: Object,
      default: () => ({
        totalRooms: 0,
        rentedRooms: 0,
        emptyRooms: 0,
        maintenanceRooms: 0,
        currentRentRate: 0,
        cumulativeRentRate: 0,
        userConversionRate: 0
      })
    }
  },
  methods: {
    getProgressColor(percentage) {
      if (percentage < 50) {
        return '#F56C6C'
      } else if (percentage < 80) {
        return '#E6A23C'
      } else {
        return '#67C23A'
      }
    }
  }
}
</script>

<style lang="scss" scoped>
// 房源统计卡片样式
.house-card {
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
  }

  &.total {
    border-left-color: #409EFF;
    .card-icon {
      background: linear-gradient(135deg, #409EFF 0%, #36A3F7 100%);
    }
  }

  &.rented {
    border-left-color: #67C23A;
    .card-icon {
      background: linear-gradient(135deg, #67C23A 0%, #85CE61 100%);
    }
  }

  &.empty {
    border-left-color: #E6A23C;
    .card-icon {
      background: linear-gradient(135deg, #E6A23C 0%, #EEBE77 100%);
    }
  }

  &.maintenance {
    border-left-color: #F56C6C;
    .card-icon {
      background: linear-gradient(135deg, #F56C6C 0%, #F78989 100%);
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

// 出租率卡片样式
.rate-row {
  margin-top: 24px;

  .rate-card {
    background: #fff;
    border-radius: 12px;
    padding: 24px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    border-left: 4px solid #e4e7ed;
    transition: all 0.3s ease;

    &:nth-child(1) {
      border-left-color: #409EFF;
    }

    &:nth-child(2) {
      border-left-color: #67C23A;
    }

    &:nth-child(3) {
      border-left-color: #E6A23C;
    }

    &:hover {
      transform: translateY(-3px);
      box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
    }

    .rate-header {
      display: flex;
      align-items: center;
      margin-bottom: 16px;

      .rate-icon {
        width: 40px;
        height: 40px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 12px;
        background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);

        i {
          color: #606266;
          font-size: 18px;
        }
      }

      .rate-info {
        .rate-title {
          font-size: 14px;
          color: #303133;
          font-weight: 600;
          margin-bottom: 2px;
        }

        .rate-desc {
          font-size: 12px;
          color: #909399;
        }
      }
    }

    .rate-content {
      .rate-number {
        font-size: 32px;
        font-weight: 700;
        color: #303133;
        margin-bottom: 12px;

        .unit {
          font-size: 16px;
          color: #909399;
          margin-left: 4px;
        }
      }

      ::v-deep .el-progress-bar {
        .el-progress-bar__outer {
          background-color: #f0f2f5;
        }

        .el-progress-bar__inner {
          transition: all 0.6s ease;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .house-card {
    padding: 20px;

    .card-number .number-value {
      font-size: 24px;
    }
  }

  .rate-row .rate-card {
    padding: 20px;
    margin-bottom: 16px;

    .rate-content .rate-number {
      font-size: 28px;
    }
  }
}
</style>