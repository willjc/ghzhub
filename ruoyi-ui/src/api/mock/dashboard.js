// 模拟数据 - 首页统计数据
export const mockDashboardStats = {
  // 当前月份财务数据
  financial: {
    receivableAmount: 2850000,    // 应收金额
    receivedAmount: 2480000,      // 实收金额
    expectedAmount: 3200000,      // 预计应收金额
    overdueAmount: 185000,        // 逾期金额
    refundAmount: 65000           // 退款金额
  },

  // 房源统计
  house: {
    totalRooms: 2568,            // 总房间数
    rentedRooms: 2235,            // 已租房间数
    emptyRooms: 298,              // 空房间数
    maintenanceRooms: 35,         // 维修房间数
    currentRentRate: 87.0,        // 当前出租率
    cumulativeRentRate: 85.5,     // 累计出租率
    userConversionRate: 68.2      // 用户转化率
  },

  // 租户画像 - 婚姻状态
  marriageStatus: {
    married: 1523,    // 已婚
    unmarried: 712,   // 未婚
    divorced: 145,    // 离异
    other: 45         // 其他
  },

  // 租户画像 - 户籍分布
  household: {
    local: 1823,      // 本地户籍
    nonlocal: 602,    // 外地户籍
    unknown: 0        // 未知户籍
  },

  // 学历分布
  education: {
    highSchool: 345,     // 高中及以下
    college: 1256,        // 大专
    bachelor: 745,        // 本科
    master: 72,           // 硕士
    doctor: 7             // 博士
  },

  // 职业类型
  profession: {
    company: 1234,        // 企业职员
    civil: 456,           // 事业单位
    selfEmployed: 312,    // 自由职业
    student: 156,         // 学生
    retired: 89,          // 退休
    other: 178            // 其他
  }
}

// 项目台账数据
export const mockProjectLedger = [
  {
    id: 1,
    projectName: '港区人才公寓A区',
    projectCode: 'GZ-RZGY-A',
    monthlyAmount: 1250000,
    yearToDateAmount: 8750000,
    status: 'normal',
    updateTime: '2025-12-06 10:30:00'
  },
  {
    id: 2,
    projectName: '港区人才公寓B区',
    projectCode: 'GZ-RZGY-B',
    monthlyAmount: 980000,
    yearToDateAmount: 6860000,
    status: 'normal',
    updateTime: '2025-12-06 10:30:00'
  },
  {
    id: 3,
    projectName: '保租房项目一期',
    projectCode: 'GZ-BZF-1',
    monthlyAmount: 620000,
    yearToDateAmount: 4340000,
    status: 'warning',
    updateTime: '2025-12-06 10:30:00'
  },
  {
    id: 4,
    projectName: '保租房项目二期',
    projectCode: 'GZ-BZF-2',
    monthlyAmount: 780000,
    yearToDateAmount: 5460000,
    status: 'normal',
    updateTime: '2025-12-06 10:30:00'
  },
  {
    id: 5,
    projectName: '市场租赁项目',
    projectCode: 'GZ-SCZL',
    monthlyAmount: 450000,
    yearToDateAmount: 3150000,
    status: 'normal',
    updateTime: '2025-12-06 10:30:00'
  }
]

// 模拟API响应
export function mockGetDashboardStats(month) {
  return new Promise(resolve => {
    setTimeout(() => {
      // 根据月份动态调整数据
      const monthNum = new Date(month).getMonth() + 1
      const multiplier = 0.8 + (monthNum / 12) * 0.4 // 0.8 到 1.2 的变化

      resolve({
        code: 200,
        data: {
          financial: {
            ...mockDashboardStats.financial,
            receivableAmount: Math.round(mockDashboardStats.financial.receivableAmount * multiplier),
            receivedAmount: Math.round(mockDashboardStats.financial.receivedAmount * multiplier),
            expectedAmount: Math.round(mockDashboardStats.financial.expectedAmount * multiplier)
          }
        }
      })
    }, 500)
  })
}

export function mockGetHouseStats() {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve({
        code: 200,
        data: mockDashboardStats.house
      })
    }, 500)
  })
}

export function mockGetTenantProfile() {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve({
        code: 200,
        data: {
          marriageStatus: mockDashboardStats.marriageStatus,
          household: mockDashboardStats.household
        }
      })
    }, 500)
  })
}

export function mockGetEducationJob() {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve({
        code: 200,
        data: {
          education: mockDashboardStats.education,
          profession: mockDashboardStats.profession
        }
      })
    }, 500)
  })
}

export function mockGetProjectLedger(month) {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve({
        code: 200,
        rows: mockProjectLedger,
        total: mockProjectLedger.length
      })
    }, 500)
  })
}