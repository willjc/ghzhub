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
