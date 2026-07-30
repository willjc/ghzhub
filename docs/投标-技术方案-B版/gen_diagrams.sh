#!/bin/bash
# B版技术方案专属图表生成脚本（重新设计，非照抄A版）
cd "$(dirname "$0")/mermaid" || exit 1

write() { cat > "$1"; }

write B04-分层结构.mmd <<'EOF'
flowchart TB
  L1["表现层：四端界面 交互与展示"]:::l
  L2["接口层：统一API网关 鉴权/路由/校验/限流"]:::l
  L3["业务逻辑层：八大业务域服务 规则与流程编排"]:::l
  L4["能力层：支付/签约/核验/消息/文件/调度"]:::l
  L5["数据访问层：统一数据访问组件"]:::l
  L6["数据层：MySQL / Redis / 文件存储"]:::d
  L1 --> L2 --> L3 --> L4 --> L5 --> L6
  classDef l fill:#e3f2fd,stroke:#1565c0;
  classDef d fill:#eceff1,stroke:#455a64;
EOF

write B05-部署架构.mmd <<'EOF'
flowchart TB
  U["群众/管理员"] --> LB["反向代理 Nginx/OpenResty<br/>统一入口·SSL"]:::n
  LB --> A1["应用实例1"]:::a
  LB --> A2["应用实例2"]:::a
  LB --> A3["应用实例N 可水平扩展"]:::a
  A1 --> DB[("MySQL 主")]:::db
  A2 --> DB
  A3 --> DB
  DB -.主备.-> DBS[("MySQL 备")]:::db
  A1 --> R[("Redis")]:::r
  A1 --> FS[("文件存储")]:::f
  A1 --> PROXY["政务网络合规代理"]:::p --> GOV["港区政务数据接口"]:::g
  classDef n fill:#e3f2fd,stroke:#1565c0;
  classDef a fill:#e8f5e9,stroke:#2e7d32;
  classDef db fill:#fff3e0,stroke:#ef6c00;
  classDef r fill:#fce4ec,stroke:#c2185b;
  classDef f fill:#eceff1,stroke:#455a64;
  classDef p fill:#f3e5f5,stroke:#6a1b9a;
  classDef g fill:#ede7f6,stroke:#4527a0;
EOF

write B06-郑好办对接时序.mmd <<'EOF'
sequenceDiagram
  participant U as 群众
  participant F as 前端(小程序/H5)
  participant S as 房屋管理系统
  participant Z as 郑好办
  U->>F: 进入办事
  F->>Z: 请求授权登录
  Z-->>F: 返回授权码
  F->>S: 提交授权码
  S->>Z: 换取令牌/用户信息
  Z-->>S: 返回用户信息
  S->>S: 映射系统用户/首次建档
  S-->>F: 登录成功(令牌)
  F-->>U: 进入业务
EOF

write B07-资格核验流程.mmd <<'EOF'
flowchart TB
  A["进入办事"]:::s --> B["统一核验服务调用政务数据"]:::s
  B --> C{"接口可用?"}:::d
  C -->|超时/失败| C1["重试N次"]:::s --> C2{"仍失败?"}:::d
  C2 -->|是| C3["降级:转人工/稍后重试"]:::w
  C2 -->|否| D
  C -->|是| D{"资格是否符合?"}:::d
  D -->|符合| E["通过,进入选房"]:::ok
  D -->|不符| F["展示不符项"]:::w --> G["手动刷新/在线申诉"]:::s --> H["审批"]:::s --> I{"通过?"}:::d
  I -->|是| E
  I -->|否| J["办事终止/整改"]:::w
  B -.全程留痕.-> LOG[("核验日志")]:::log
  classDef s fill:#e8f5e9,stroke:#2e7d32;
  classDef d fill:#fff3e0,stroke:#ef6c00;
  classDef ok fill:#c8e6c9,stroke:#2e7d32;
  classDef w fill:#ffebee,stroke:#c62828;
  classDef log fill:#eceff1,stroke:#455a64;
EOF

write B08-支付缴费时序.mmd <<'EOF'
sequenceDiagram
  participant U as 群众
  participant S as 系统
  participant P as 港区支付
  U->>S: 选择账单缴费
  S->>S: 校验(押金优先/账期)
  S->>P: 统一下单(业务唯一键)
  P-->>S: 预支付信息
  S-->>U: 拉起支付
  U->>P: 完成支付
  P-->>S: 异步回调(幂等处理)
  alt 回调丢失
    S->>P: 定时主动查单
    P-->>S: 支付结果
  end
  S->>S: 更新账单/台账(幂等)
  S-->>U: 缴费成功
EOF

write B09-电子签章时序.mmd <<'EOF'
sequenceDiagram
  participant U as 群众
  participant S as 系统
  participant E as 电子签章平台
  U->>S: 发起签约
  S->>E: 按模板创建签署任务
  E-->>S: 返回签署链接
  S-->>U: 跳转签署
  U->>E: 完成签署
  E-->>S: 签署结果回调
  S->>S: 合同归档/更新状态
  S-->>U: 可下载合同
EOF

write B10-退租精算流程.mmd <<'EOF'
flowchart TB
  A["用户提交退租申请"]:::s --> B["管理端审批"]:::s
  B --> C["核定实际退租日期"]:::key
  C --> D["按日精算应退租金+押金"]:::s
  D --> E["退款金额一次核定锁定"]:::key
  E --> F["用户签字确认"]:::s
  F --> G["执行退款(港区支付原路)"]:::s
  G --> H["合同状态置退租"]:::s
  H --> I["房源状态释放"]:::ok
  D -.台账留痕.-> LOG[("退租退款台账")]:::log
  classDef s fill:#e8f5e9,stroke:#2e7d32;
  classDef key fill:#fff3e0,stroke:#ef6c00;
  classDef ok fill:#c8e6c9,stroke:#2e7d32;
  classDef log fill:#eceff1,stroke:#455a64;
EOF

write B11-合同状态机.mmd <<'EOF'
stateDiagram-v2
  [*] --> 待签署
  待签署 --> 待付款: 签署完成
  待签署 --> 已作废: 签署超时
  待付款 --> 生效: 押金/首期缴纳
  待付款 --> 已失效: 支付超时
  生效 --> 到期: 租期届满
  生效 --> 退租: 退租审批通过
  到期 --> 续租: 续租签约
  到期 --> 退租: 到期退租
  退租 --> [*]
  已作废 --> [*]
  已失效 --> [*]
EOF

write B12-房源状态机.mmd <<'EOF'
stateDiagram-v2
  [*] --> 空置
  空置 --> 已预订: 选定/预约
  已预订 --> 已签约: 签约完成
  已预订 --> 空置: 超时释放
  已签约 --> 已入住: 入住确认
  已入住 --> 退租释放: 退租完成
  退租释放 --> 空置: 状态释放
  已签约 --> 空置: 合同失效释放
EOF

write B13-数据实体ER.mmd <<'EOF'
erDiagram
  USER ||--o{ CONTRACT : 拥有
  HOUSE ||--o{ CONTRACT : 关联
  PROJECT ||--o{ BUILDING : 含
  BUILDING ||--o{ UNIT : 含
  UNIT ||--o{ HOUSE : 含
  CONTRACT ||--o{ BILL : 生成
  BILL ||--o{ PAYORDER : 对应
  CONTRACT ||--o{ CHECKOUT : 退租
  USER ||--o{ VERIFY : 核验记录
  USER ||--o{ APPEAL : 申诉记录
EOF

write B14-组织架构.mmd <<'EOF'
flowchart TB
  DEC["决策层:发起人/分管领导"]:::d
  PM["管理层:项目经理"]:::m
  DEC --> PM
  PM --> BE["后端开发"]:::e
  PM --> FE["前端开发"]:::e
  PM --> IT["对接工程师"]:::e
  PM --> QA["测试工程师"]:::e
  PM --> OPS["实施运维工程师"]:::e
  classDef d fill:#e3f2fd,stroke:#1565c0;
  classDef m fill:#fff3e0,stroke:#ef6c00;
  classDef e fill:#e8f5e9,stroke:#2e7d32;
EOF

write B15-迭代开发流程.mmd <<'EOF'
flowchart LR
  A["需求确认/基线"]:::s --> B["迭代规划"]:::s --> C["设计"]:::s --> D["开发"]:::s --> E["自测/评审"]:::s --> F["集成/联调"]:::s --> G["演示确认"]:::s --> H{"迭代结束?"}:::d
  H -->|否,下一迭代| B
  H -->|是| I["系统测试/上线"]:::ok
  classDef s fill:#e8f5e9,stroke:#2e7d32;
  classDef d fill:#fff3e0,stroke:#ef6c00;
  classDef ok fill:#c8e6c9,stroke:#2e7d32;
EOF

write B16-测试缺陷闭环.mmd <<'EOF'
flowchart TB
  A["单元测试"]:::s --> B["集成测试"]:::s --> C["系统测试"]:::s --> D["验收测试"]:::s
  C --> E{"发现缺陷?"}:::d
  E -->|是| F["登记/分级"]:::w --> G["定位修复"]:::s --> H["回归验证"]:::s --> I{"通过?"}:::d
  I -->|否| G
  I -->|是| J["关闭"]:::ok
  E -->|否| J
  classDef s fill:#e8f5e9,stroke:#2e7d32;
  classDef d fill:#fff3e0,stroke:#ef6c00;
  classDef w fill:#ffebee,stroke:#c62828;
  classDef ok fill:#c8e6c9,stroke:#2e7d32;
EOF

write B17-变更管理.mmd <<'EOF'
flowchart LR
  A["变更提出"]:::s --> B["影响评估"]:::s --> C["评审审批"]:::d --> D{"通过?"}:::d
  D -->|否| E["驳回/记录"]:::w
  D -->|是| F["实施"]:::s --> G["验证"]:::s --> H["记录归档"]:::ok
  classDef s fill:#e8f5e9,stroke:#2e7d32;
  classDef d fill:#fff3e0,stroke:#ef6c00;
  classDef w fill:#ffebee,stroke:#c62828;
  classDef ok fill:#c8e6c9,stroke:#2e7d32;
EOF

write B18-纵深防御.mmd <<'EOF'
flowchart TB
  P1["物理层:合规机房"]:::x
  P2["网络层:防火墙/分区/加密"]:::x
  P3["边界层:访问控制/入侵防范"]:::x
  P4["主机层:基线/防恶意代码"]:::x
  P5["应用层:注入/XSS/越权防护"]:::x
  P6["数据层:加密/脱敏/审计/备份"]:::x
  P7["管理层:制度/人员/应急"]:::x
  P1 --> P2 --> P3 --> P4 --> P5 --> P6 --> P7
  classDef x fill:#ede7f6,stroke:#4527a0;
EOF

write B19-等保二级体系.mmd <<'EOF'
flowchart TB
  ROOT["等保二级安全体系"]:::r
  ROOT --> T["安全技术"]:::t
  ROOT --> M["安全管理"]:::m
  T --> T1["物理环境"]:::t
  T --> T2["通信网络"]:::t
  T --> T3["区域边界"]:::t
  T --> T4["计算环境"]:::t
  T --> T5["管理中心"]:::t
  M --> M1["管理制度"]:::m
  M --> M2["管理机构"]:::m
  M --> M3["管理人员"]:::m
  M --> M4["建设管理"]:::m
  M --> M5["运维管理"]:::m
  classDef r fill:#e3f2fd,stroke:#1565c0;
  classDef t fill:#e8f5e9,stroke:#2e7d32;
  classDef m fill:#fff3e0,stroke:#ef6c00;
EOF

write B20-安全应急.mmd <<'EOF'
flowchart TB
  A["安全事件发生"]:::w --> B["监测/发现"]:::s --> C["分级研判"]:::d
  C --> D["应急响应/止损"]:::s --> E["逐级报告(含甲方)"]:::s --> F["处置恢复"]:::s --> G["复盘整改"]:::ok
  classDef s fill:#e8f5e9,stroke:#2e7d32;
  classDef d fill:#fff3e0,stroke:#ef6c00;
  classDef w fill:#ffebee,stroke:#c62828;
  classDef ok fill:#c8e6c9,stroke:#2e7d32;
EOF

write B21-监控分层.mmd <<'EOF'
flowchart TB
  M["统一监控与告警平台"]:::r
  M --> A["应用:可用性/响应/错误率"]:::a
  M --> H["主机:CPU/内存/磁盘/网络"]:::a
  M --> D["数据库:连接/慢查询/主备延迟"]:::a
  M --> C["缓存:命中率/内存"]:::a
  M --> I["接口:成功率/耗时"]:::a
  M --> P["支付/核验:成功率/回调延迟"]:::a
  M --> ALERT["多渠道告警:短信/电话/邮件"]:::w
  classDef r fill:#e3f2fd,stroke:#1565c0;
  classDef a fill:#e8f5e9,stroke:#2e7d32;
  classDef w fill:#ffebee,stroke:#c62828;
EOF

write B22-故障分级响应.mmd <<'EOF'
flowchart TB
  A["报障/告警"]:::s --> B["工单登记"]:::s --> C{"故障分级"}:::d
  C -->|紧急| E1["10分钟内响应"]:::w
  C -->|一般| E2["2小时内响应"]:::s
  E1 --> F["处置/恢复"]:::s
  E2 --> F
  F --> G{"超时/扩大?"}:::d
  G -->|是| H["逐级升级"]:::w --> F
  G -->|否| I["恢复验证/台账/复盘"]:::ok
  classDef s fill:#e8f5e9,stroke:#2e7d32;
  classDef d fill:#fff3e0,stroke:#ef6c00;
  classDef w fill:#ffebee,stroke:#c62828;
  classDef ok fill:#c8e6c9,stroke:#2e7d32;
EOF

write B23-备份恢复.mmd <<'EOF'
flowchart LR
  A["业务数据库"]:::s --> B["每日增量"]:::s
  A --> C["每周全量"]:::s
  D["配置/代码"]:::s --> E["版本化备份"]:::s
  F["上传文件"]:::s --> G["定期备份"]:::s
  B --> H["加密存储/异地保存"]:::key
  C --> H
  E --> H
  G --> H
  H --> I["定期恢复演练(RTO/RPO)"]:::ok
  classDef s fill:#e8f5e9,stroke:#2e7d32;
  classDef key fill:#fff3e0,stroke:#ef6c00;
  classDef ok fill:#c8e6c9,stroke:#2e7d32;
EOF

write B24-里程碑甘特.mmd <<'EOF'
gantt
  title 项目进度计划(60日历天开发期)
  dateFormat  D
  axisFormat  D%d
  section 启动设计
  环境/需求/设计   :a1, 0, 10d
  section 核心开发
  四端与业务域开发 :a2, 8, 37d
  对接开发         :a3, 12, 30d
  section 联调测试
  对接联调         :a4, 35, 15d
  系统/性能/安全测试 :a5, 40, 15d
  section 迁移上线
  数据迁移         :a6, 50, 8d
  上线与初验       :a7, 55, 5d
EOF

write B25-六年运维.mmd <<'EOF'
timeline
  title 六年免费运维年度工作安排
  第1年 : 稳定保障 : 问题收敛 : 培训强化
  第2年 : 性能优化 : 常态培训 : 知识库完善
  第3年 : 功能迭代 : 安全复测 : 容量评估
  第4年 : 持续优化 : 等保复查 : 数据治理
  第5年 : 稳定保障 : 优化改进 : 移交准备
  第6年 : 稳定保障 : 移交实施 : 平稳过渡
EOF

echo "全部 .mmd 已写入:"; ls B*.mmd | wc -l
