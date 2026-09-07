## [LRN-20260827-001] correction

**Logged**: 2026-08-27T00:00:00+08:00
**Priority**: high
**Status**: resolved
**Area**: backend

### Summary
客户明确要求保留的测试和管理能力不得在安全整改中擅自关闭。

### Details
本项目的测试用户切换、账单直接标记已支付均为客户要求保留的能力。安全整改应限制调用身份和数据归属，不能以软测为由删除或禁用功能。

### Suggested Action
后续审计先区分客户保留能力与遗留漏洞；对保留能力采用服务端授权、当前用户归属校验和可配置开关。

### Metadata
- Source: user_feedback
- Related Files: ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/HzAuthController.java, ruoyi-admin/src/main/java/com/ruoyi/web/controller/h5/HzBillAppController.java
- Tags: customer-requirement, auth, testing

### Resolution
- **Resolved**: 2026-08-27T00:00:00+08:00
- **Notes**: 已恢复账单直接支付接口并保留测试用户切换，仅增加服务端身份和归属校验。

---

## [LRN-20260828-001] correction

**Logged**: 2026-08-28T20:00:00+08:00
**Priority**: high
**Status**: pending
**Area**: infra

### Summary
Actions 显示成功和生产接口存活不能证明新后端 JAR 已部署，应检查完整部署日志和新版本特征。

### Details
后端部署步骤通过远程 `server-deploy.sh 2>&1 | tee deploy.log` 执行。Maven 因 `HzCheckInAppController` 缺少 `SecurityUtils` import 而构建失败，但管道返回了 `tee` 的成功状态，Actions 仍显示绿色。生产接口继续由旧 JAR 提供服务，导致新版小程序与旧后端不匹配。

### Suggested Action
部署验证必须检查日志中的 `BUILD SUCCESS`、JAR 替换和重启结果，并用新版本独有行为验证运行时；CI 远程管道应启用 `pipefail` 或直接传播部署脚本退出码。

### Metadata
- Source: error
- Related Files: .github/workflows/backend-deploy.yml, server-deploy.sh, ruoyi-admin/src/main/java/com/ruoyi/web/controller/h5/HzCheckInAppController.java
- Tags: ci-cd, false-positive, deployment, maven

---

## [LRN-20260905-001] correction

**Logged**: 2026-09-05T01:31:18+08:00
**Priority**: high
**Status**: resolved
**Area**: backend

### Summary
区分系统本地合同模板记录与 e签宝远端文档模板，不能把前者当成实际电子合同来源。

### Details
人才公寓和租赁业务的正式合同 PDF 都由 `EsignServiceImpl#createFileByTemplate` 调用 e签宝远端模板生成。`hz_contract_template` 目前仍被签约前置接口读取，用于预览字段、押金兜底和支付周期，并保存一个本地模板主键，但它的 `template_content` 不会生成最终签署 PDF。此前把"需要本地记录通过旧前置流程"表述成"需要配置实际合同模板"，概念错误。

再次复核还发现，e签宝模板详情会返回签章控件（`componentType=6`）及精确页码、X/Y 坐标。此前虽然查询了租赁模板的填充控件，却没有把签章控件与人才公寓逐项对比，导致 `initSign` 继续使用人才模板的固定坐标。

### Suggested Action
以后审查签约流程时先从 `/h5/esign/init-sign` 追到 e签宝创建文件接口，再单独标注 `hz_contract_template` 的遗留前置依赖；查询新模板后必须同时核对填充控件、签章控件、签署日期控件及位置，不能只统计业务字段。

### Metadata
- Source: user_feedback
- Related Files: ruoyi-system/src/main/java/com/ruoyi/system/service/impl/EsignServiceImpl.java, ruoyi-admin/src/main/java/com/ruoyi/web/controller/h5/HzContractAppController.java
- Tags: esign, contract-template, terminology, flow-tracing
- Pattern-Key: esign.template_full_component_audit
- Recurrence-Count: 2
- Last-Seen: 2026-09-05

### Resolution
- **Resolved**: 2026-09-05T01:31:18+08:00
- **Notes**: 已重新按实际调用链核对并纠正结论，未修改业务代码。

---
