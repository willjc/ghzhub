---
name: 微信支付配置
description: 微信支付商户信息、公钥模式配置、证书文件位置
type: project
---

微信支付已完整配置并开启（enabled=true）。

**商户信息：**
- 商户号：1106404234
- APPID：wx74efa287343d0fa2
- API v3密钥：FSHwjDLGjooxhXIZQtGYAfimmZyvstGg

**重要：使用公钥模式（新商户默认，不支持平台证书）**
- 代码用 RSAPublicKeyConfig，不是 RSAAutoCertificateConfig
- 公钥ID：PUB_KEY_ID_0111064042342026021200192262000001
- 证书文件在 ruoyi-admin/src/main/resources/certs/（apiclient_key.pem、apiclient_cert.pem、pub_key.pem）

**配置路径：** application.yml → wechat.pay.*
**回调地址：** https://app.caigon.cn/h5/pay/wechat/notify
**支付流程：** uni.requestPayment JSAPI → 需要 openid（登录时返回并存 localStorage）

**Why：** 新注册商户不支持 RSAAutoCertificateConfig，启动时会报 404 平台证书不存在。
**How to apply：** 如需修改支付配置或排查支付问题，先确认使用公钥模式而非证书模式。
