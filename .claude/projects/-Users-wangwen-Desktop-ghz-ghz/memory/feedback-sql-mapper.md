---
name: SQL mapper 字段遗漏排查经验
description: 前端字段显示为空时，从前端映射函数往上追溯到SQL SELECT列的排查链路
type: feedback
---

当前端某字段始终为空/false时，排查链路：
前端展示条件 → mapXxxData映射函数 → API返回字段名 → SQL SELECT列表

**典型案例：** contract.vue 电子合同始终显示"待生成"
- 数据库 contract_content 有正确的 PDF URL
- 但 HzContractMapper.selectContractVOByUserId 的 SELECT 没有 c.contract_content
- 导致 API 返回数据中没有这个字段，前端 hasPdf 永远 false

**Why：** MyBatis XML 的 resultType="java.util.HashMap" 只返回 SELECT 里列出的字段，不像实体类自动映射所有列。

**How to apply：** 每次新增前端展示字段，先确认：
1. 前端 mapData 函数有没有映射该字段
2. 后端 SQL SELECT 列表有没有包含该列
3. 字段名是否一致（snake_case vs camelCase）
