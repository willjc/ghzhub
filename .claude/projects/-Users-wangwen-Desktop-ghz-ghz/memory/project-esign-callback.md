---
name: e签宝回调验签规则
description: e签宝V3回调header正确名称、签名拼接规则、排查记录
type: project
---

e签宝V3签署完成回调验签已修复（commit 7f40a2a）。

**正确的 header 名（全大写）：**
- 签名：X-Tsign-Open-SIGNATURE（原代码错误用了 X-Tsign-Open-Sign）
- 时间戳：X-Tsign-Open-TIMESTAMP

**签名数据拼接规则：**
```
data = timestamp + sortedQueryValues + requestBody
```
- sortedQueryValues：Query参数按key ASCII升序后，只拼接 value（不含 key=），如 `b=2&a=1` → `"12"`
- 回调URL无Query参数时，sortedQueryValues 为空串

**回调成功结果：**
- contract_content 字段更新为 e签宝 PDF 下载链接（https://esignoss.esign.cn/...）
- 同时生成账单（押金+租金）、入住记录，更新订单状态

**Why：** 原代码读的 header 名不对，导致 signature 始终为 null，验签必然失败。
**How to apply：** 如果回调验签失败，先打印所有 header，确认 e签宝实际发来的 header 名。
