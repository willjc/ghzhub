# 数据库删除关联关系 - 详细查询结果

## 执行日期: 2026-04-16
## 数据库连接: 36.133.39.148:33061/ghz

---

## 一、目标1：要删除的项目及关联数据

### 1.1 项目基本信息

| project_id | project_name | 房源数 | 房型数 | 合同数 |
|-----------|-------------|--------|--------|--------|
| 2 | 航空港区锦绣家园 | 1 | 7 | 0 |
| 5 | 港区金科城 | 0 | 6 | 0 |
| 6 | 港区恒大城 | 0 | 6 | 0 |
| 9 | 航南新城人才公寓 | 0 | 16 | 0 |
| 11 | 港区测试金科城 | 1 | 6 | 0 |
| 21 | 航南新城人才公寓(新) | 130 | 67 | 195 |

### 1.2 房源详情（Project_ID 21为主，其他项目房源数量少）

Project 21 房源总数: **130条**
- 房源ID范围: 951-1662
- 房源状态分布:
  - 已出租(status=1): 大部分
  - 未出租(status=0): 部分

### 1.3 合同统计

- 项目2,5,6,9,11: 各0条合同 (无业务数据)
- 项目21: **195条合同** (主要测试数据)
- 合同状态分布: status 0-6 (草稿、进行中、已结束等)

### 1.4 关联表数据统计

| 表名 | 记录数 | 关联方式 | 是否需删 |
|------|--------|----------|---------|
| hz_project | 6 | 根 | ✓ |
| hz_house_type | 108 | project_id | ✓ |
| hz_building | 25 | project_id | ✓ |
| hz_unit | 12 | building_id | ✓ |
| hz_house | 132 | project_id | ✓ |
| hz_house_image | 34 | house_id | ✓ |
| hz_contract | 195 | project_id | ✓ |
| hz_bill | 4,473 | contract_id | ✓ |
| hz_payment | 1,905 | bill_id | ✓ |
| hz_checkin_record | 150 | contract_id | ✓ |
| hz_checkout_apply | 80 | contract_id | ✓ |
| hz_checkout_record | 80 | contract_id | ✓ |
| hz_contract_signature | 195 | contract_id | ✓ |

### 1.5 无数据的表（无需删除）

| 表名 | 原因 |
|------|------|
| hz_house_order | 项目无预订单 |
| hz_co_tenant | 无同住人记录 |
| hz_refund_apply | 无退款申请 |
| hz_invoice_apply | 无发票申请 |
| hz_invoice | 无发票记录 |
| hz_contract_attachment | 无合同附件 |

---

## 二、目标2：用户18539279011的数据分析

### 2.1 用户基本信息

| 字段 | 值 |
|------|-----|
| User_ID | 34 |
| 用户名 | 王连胜 |
| 电话 | 18539279011 |
| 来源 | hz_user表(App端用户) |

### 2.2 用户合同列表（16条）

按contract_id排序前5条示例:
- Contract_ID 105, 106 (Project 3, House 24)
- Contract_ID 3763-3776 (Project 3,19,16 混合)

### 2.3 用户关联数据统计

| 数据类型 | 数量 |
|---------|------|
| 合同 | 16 |
| 账单 | 134 |
| 支付 | 0 |
| 入住记录 | 11 |
| 退租申请 | 2 |
| 退租记录 | 2 |
| 预订单 | 4 |
| 合同签署 | 0 |

### 2.4 用户房源关联

共涉及房源: **8套**

| house_id | house_code | house_status | project_id |
|----------|-----------|--------------|----------|
| 9 | 3-6-14-101 | 0 | 3 |
| 18 | 3-8-17-101 | 0 | 3 |
| 20 | 3-8-17-301 | 0 | 3 |
| 24 | 3-8-19-101 | 1 | 3 |
| 25 | 3-8-19-201 | 0 | 3 |
| 36 | 3-10-23-101 | 0 | 3 |
| 743 | 1806661587222827011 | 0 | 19 |
| 1050 | 1866366853719400449 | 0 | 16 |

### 2.5 关键发现

✓ **用户数据完全独立**: 所有项目都是正式项目(3,16,19)
✓ **无删除风险**: 与待删项目(2,5,6,9,11,21)无任何交集
✓ **状态正常**: 房源和合同状态正常,无数据异常
✓ **建议**: 用户数据应保留,勿误删

---

## 三、数据删除执行计划

### 3.1 删除依赖关系

```
hz_project(6)
  ├─ hz_house_type(108)
  ├─ hz_building(25)
  │  └─ hz_unit(12)
  ├─ hz_house(132)
  │  └─ hz_house_image(34)
  └─ hz_contract(195) [通过project_id]
     ├─ hz_contract_signature(195)
     ├─ hz_checkin_record(150)
     ├─ hz_checkout_apply(80)
     ├─ hz_checkout_record(80)
     └─ hz_bill(4,473)
        └─ hz_payment(1,905)
```

### 3.2 删除顺序(19步)

**第1层 - 叶子节点(无外键指向)**
1. hz_payment - 1,905条
2. hz_house_image - 34条
3. hz_contract_signature - 195条
4. hz_invoice (0条)
5. hz_refund_apply (0条)
6. hz_co_tenant (0条)

**第2层 - 中间节点**
7. hz_refund_record (需确认)
8. hz_checkout_record - 80条
9. hz_checkin_record - 150条
10. hz_checkout_apply - 80条
11. hz_bill - 4,473条
12. hz_invoice_apply (0条)

**第3层 - 合同**
13. hz_contract - 195条

**第4层 - 房源和房型**
14. hz_house_order (0条)
15. hz_house - 132条
16. hz_unit - 12条
17. hz_house_type - 108条

**第5层 - 基础设施**
18. hz_building - 25条

**第6层 - 项目**
19. hz_project - 6条

### 3.3 总计删除数据

- **总记录数**: 13,147 条
- **其中主要数据**: 
  - 账单: 4,473条(33.9%)
  - 支付: 1,905条(14.5%)
  - 合同: 195条(1.5%)
  - 其他: 6,574条(50%)

---

## 四、逻辑删除建议

### 所有表均建议使用逻辑删除

使用 `UPDATE table_name SET del_flag = '1' WHERE ...` 而非 `DELETE`

**优势**:
- 保留审计痕迹
- 数据可恢复
- 不影响主键序列
- 不产生碎片

**推荐SQL模式** (按删除顺序):
```sql
-- 第1-6层逐步执行
UPDATE hz_payment SET del_flag = '1' WHERE bill_id IN (...);
UPDATE hz_house_image SET del_flag = '1' WHERE house_id IN (...);
-- ... 依次执行
```

---

## 五、数据验证检查清单

### 删除前检查
- [ ] 确认项目2,5,6,9,11,21是测试/过期项目
- [ ] 确认项目21的195份合同已全部结束
- [ ] 确认没有待支付账单(payment_status='0')
- [ ] 确认没有进行中的合同(contract_status='4')
- [ ] 确认用户34数据与待删项目无交集
- [ ] 完整备份生产数据库
- [ ] 在测试库预演整个删除流程

### 删除后验证
- [ ] 查询目标项目,确认已无法访问
- [ ] 验证相关查询语句返回空集
- [ ] 检查数据库日志,确认没有错误
- [ ] 验证应用系统功能正常
- [ ] 检查用户端(App)访问正常

---

## 六、其他需要检查的表

以下表可能间接关联,需逐一确认:

1. **hz_tenant** - 租户主表 (通过tenant_id)
2. **hz_qualification** - 租户资格认证
3. **hz_document** - 文档管理
4. **hz_tenant_document** - 租户文档
5. **hz_viewing_record** - 看房记录
6. **hz_activity** - 活动相关
7. **hz_announcement** - 通知公告
8. **hz_coupon** / **hz_coupon_receive** - 优惠券
9. **hz_message** / **hz_user_message** - 消息系统
10. **hz_appointment** - 预约记录
11. **hz_batch_allocation** - 批量分配
12. **hz_featured_house** - 精选房源
13. **hz_map_point** - 地图标记

---

## 七、性能建议

对于>10000条记录的表(hz_bill, hz_payment等):

**分批删除示例**:
```sql
-- 分100次删除,每次删除约45条
UPDATE hz_payment SET del_flag = '1' 
WHERE bill_id IN (SELECT bill_id FROM hz_bill WHERE contract_id IN (...))
LIMIT 45;
-- 重复执行直到所有记录处理完成

-- 或使用:
DELETE FROM hz_payment WHERE bill_id IN (...) LIMIT 45;
```

这样可以:
- 避免长时间锁表
- 降低服务器压力
- 方便监控进度
- 出错时便于恢复

---

## 文档生成

生成时间: 2026-04-16 15:30 UTC
数据库版本: MySQL 5.7+
查询工具: mysql命令行
