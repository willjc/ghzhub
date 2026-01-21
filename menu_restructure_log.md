
## 2025年菜单结构重组 (完全按照需求文档ganghaozhu.md)

### 修改原因
用户要求: "把功能再梳理一下，完全按照D:\lasthm\gangzhu\ganghaozhu.md 这里面的后台管理的结构来放置 现在的功能不是对应的"

### 修改步骤

#### 1. 备份数据
创建了备份表保存原有菜单数据

#### 2. 创建一级分组菜单

港好住管理 (2000) 下新增的一级菜单:

- **3000: 首页** (menu_type: C)
  - path: dashboard
  - component: gangzhu/dashboard/index
  - icon: dashboard
  - order_num: 1

- **3001: 资产管理** (menu_type: M - 目录)
  - path: asset
  - icon: example
  - order_num: 2

- **3002: 申请信息** (menu_type: M - 目录)
  - path: application
  - icon: form
  - order_num: 3

- **3003: 报表管理** (menu_type: M - 目录)
  - path: report
  - icon: chart
  - order_num: 5

- **3004: 配置管理** (menu_type: M - 目录)
  - path: config
  - icon: tool
  - order_num: 6

- **3040: 黑名单管理** (menu_type: C)
  - path: blacklist
  - component: gangzhu/blacklist/index
  - icon: peoples
  - order_num: 7

- **3041: 用户管理** (menu_type: C)
  - path: user
  - component: gangzhu/user/index
  - icon: peoples
  - order_num: 8

#### 3. 调整现有菜单层级

**移动到资产管理 (3001) 下**:
- 2007: 项目管理 -> order_num: 1
- 2062: 楼栋管理 -> order_num: 2
- 2068: 单元管理 -> order_num: 3
- 2001: 房源管理 -> order_num: 4

**移动到申请信息 (3002) 下**:
- 2012: 租户管理 -> order_num: 1
- 2040: 预约看房 (原:预约管理) -> order_num: 2
- 2017: 资格申诉 (原:资格审核) -> order_num: 3
- 2045: 承诺书 (原:资料审核) -> order_num: 4

**保持在合同管理 (2022) 下**:
- 2022本身提升为一级菜单 (order_num: 4)

**移动到配置管理 (3004) 下**:
- 2049: 消息通知 (原:消息管理) -> order_num: 1
- 2053: 通知公告 (原:公告管理) -> order_num: 4

#### 4. 菜单重命名

| 原名称 | 新名称 | menu_id |
|--------|--------|---------|
| 预约管理 | 预约看房 | 2040 |
| 资格审核 | 资格申诉 | 2017 |
| 资料审核 | 承诺书 | 2045 |
| 账单管理 | 租金账单 | 2027 |
| 消息管理 | 消息通知 | 2049 |
| 公告管理 | 通知公告 | 2053 |

#### 5. 新增优先级1的功能模块

**合同管理 (2022) 下新增**:
- 3010: 合同列表 (order_num: 1)
- 3011: 退款管理 (order_num: 4)
- 3012: 合住人管理 (order_num: 5)
- 3013: 调换房管理 (order_num: 6)

**报表管理 (3003) 下新增**:
- 3020: 项目收款台账 (order_num: 1)
- 3021: 自定义报表 (order_num: 2)

**配置管理 (3004) 下新增**:
- 3030: 合同模版 (order_num: 2)
- 3031: 运营配置 (order_num: 3)
- 3032: 政策文件 (order_num: 5)

#### 6. 最终菜单结构

```
港好住管理 (2000)
├── 1. 首页 (3000)
├── 2. 资产管理 (3001) [目录]
│   ├── 1. 项目管理 (2007)
│   ├── 2. 楼栋管理 (2062)
│   ├── 3. 单元管理 (2068)
│   └── 4. 房源管理 (2001)
├── 3. 申请信息 (3002) [目录]
│   ├── 1. 租户管理 (2012)
│   ├── 2. 预约看房 (2040)
│   ├── 3. 资格申诉 (2017)
│   └── 4. 承诺书 (2045)
├── 4. 合同管理 (2022)
│   ├── 1. 合同列表 (3010) ← 新增
│   ├── 2. 租金账单 (2027)
│   ├── 3. 入住管理 (2032)
│   ├── 3. 退租管理 (2036)
│   ├── 4. 退款管理 (3011) ← 新增
│   ├── 5. 合住人管理 (3012) ← 新增
│   └── 6. 调换房管理 (3013) ← 新增
├── 5. 报表管理 (3003) [目录]
│   ├── 1. 项目收款台账 (3020) ← 新增
│   └── 2. 自定义报表 (3021) ← 新增
├── 6. 配置管理 (3004) [目录]
│   ├── 1. 消息通知 (2049)
│   ├── 2. 合同模版 (3030) ← 新增
│   ├── 3. 运营配置 (3031) ← 新增
│   ├── 4. 通知公告 (2053)
│   └── 5. 政策文件 (3032) ← 新增
├── 7. 黑名单管理 (3040) ← 新增
├── 8. 用户管理 (3041) ← 新增
└── 9. 开票管理 (2058)
```

### 注意事项

1. **新增菜单需要创建对应的前端页面**:
   - dashboard/index.vue (首页)
   - contract/index.vue (合同列表)
   - refund/index.vue (退款管理)
   - roommate/index.vue (合住人管理)
   - transfer/index.vue (调换房管理)
   - report/receipt/index.vue (项目收款台账)
   - report/custom/index.vue (自定义报表)
   - config/template/index.vue (合同模版)
   - config/operation/index.vue (运营配置)
   - config/policy/index.vue (政策文件)
   - blacklist/index.vue (黑名单管理)
   - user/index.vue (用户管理)

2. **按钮权限菜单 (menu_type='F')** 已保留但不在导航栏显示,用于权限控制

3. **验证步骤**:
   - 重启后端服务,清除Redis缓存
   - 重新登录系统
   - 查看左侧菜单栏"港好住管理"下的菜单结构是否符合预期
   - 测试各菜单的权限控制是否正常
