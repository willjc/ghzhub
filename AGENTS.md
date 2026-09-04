# AGENTS.md

本文件是港好住仓库唯一的开发、数据库和部署规范。最后核对日期：2026-09-04。

如果本文与实际代码、运行配置或生产数据冲突，以当前代码、部署脚本和运行环境为准，并及时更新本文；不要继续复制旧说明。

## 项目现状

港好住用于人才公寓、保障性租赁住房和市场化租赁业务。

- 后端：Spring Boot 3.5.4、Java 17、Spring Security、Redis、MyBatis-Plus 3.5.9。
- 管理后台：Vue 2.6.12、Element UI 2.15.14，目录 `ruoyi-ui/`。
- 用户端：uni-app（Vue 2），目录 `uniapp-h5/`，同时构建 H5 和微信小程序。
- 数据库：生产环境使用 MySQL 8.x。
- 生产分支：`master`。

主要模块：

```text
ruoyi-admin/       后端入口、H5/小程序 Controller、运行配置
ruoyi-framework/   Spring Security、Token、Redis 等框架能力
ruoyi-system/      港好住业务 Domain、Mapper、Service
ruoyi-common/      通用工具和公共模型
ruoyi-ui/          管理后台
uniapp-h5/         H5 与微信小程序
scripts/           增量数据库脚本
```

## 工作原则

### 修改范围

- 用户说“先不要修改”时，只允许查看和分析，不得改文件、数据库、配置、服务状态或外部平台。
- 只修改当前需求直接涉及的内容，不顺手重构正常模块，不增加一次性抽象。
- 修改前先检查当前分支、工作树和相关调用链；保留用户已有的未提交文件。
- 不使用 `git add .` 批量暂存；只暂存本次明确修改的文件。
- 生产变更必须说明影响范围、验证方式和回退点。

### 验证规则

- 不在本地运行测试、构建或启动命令，包括 `mvn test/package/install`、`npm test`、`npm run dev/build`。
- 可以执行只读检查、`git diff --check`、日志检查和经用户明确授权的业务验证。
- 编译结果由 CI 验证，实际业务结果由生产日志、接口、数据库及用户端回归共同确认。
- “代码存在”“CI 成功”“健康检查成功”均不等于业务流程已经跑通。

### 代码约定

- 新增简单 CRUD 优先使用 MyBatis-Plus；复杂联表查询可保留 Mapper XML 或注解 SQL。
- Controller 使用统一响应结构，用户输入和外部接口响应必须校验。
- 保持现有代码风格，删除仅由本次改动产生的无效导入或变量。
- 不把密钥、密码、Token、签名 URL 或个人敏感信息写入代码、日志、提交信息和回复。

## 数据库规范

- 优先使用 `mcp__mcp_server_mysql__mysql_query`。
- MCP 未配置、启动失败或连接不可用时，允许使用 `mysql` CLI。
- CLI 连接信息从现有配置读取；不得把密码直接写在命令参数、脚本、日志或回复中。
- 查询前先核对表结构和真实字段，不按实体类或旧文档猜测数据库结构。
- 生产写操作必须：锁定精确范围、创建带日期和用途的备份、使用事务或可重复执行脚本、执行后做字段级校验，并给出回滚方法。
- 未经单独授权，不扩大 UPDATE/DELETE 范围，不顺手修复其他数据。
- 小程序用户主表是 `hz_user`；新代码不要依赖历史 `hz_tenant` 表。合同、账单等用户关系使用 `tenant_id = hz_user.user_id`。
- `scripts/` 中的增量 SQL 不会由 CI 自动执行，部署前后必须单独确认生产库迁移状态。

## 用户端鉴权

- 用户端目前只使用微信登录；郑好办登录已确定不对接。历史郑好办代码或配置不得作为新流程依据，也不要继续扩展。
- 新登录 Token 由 `HzUserTokenService` 生成，前缀为 `hzu_`，会话保存在 Redis。
- 小程序请求统一发送 `Authorization: Bearer <token>`。
- 登录成功后保存 `token`、`userId`、`userInfo`；HTTP 401 时统一清理三者并重新登录。
- 后端受保护接口必须通过 `SecurityUtils.getHzUserId()` 或等价服务端身份取得当前用户。前端传入的 `userId`、`tenantId` 只能作为兼容参数，不能作为数据权限依据。
- 读取、修改、删除合同、账单、入住、退租、发票、资料、投诉、报修等用户数据时，必须校验资源属于当前 Token 用户。
- 旧版 `hz_token_` 仅由 `ghz.legacy-auth` 临时兼容；是否仍有效以 `application.yml` 和运行环境为准，到期后不得自动延长。
- 客户要求保留 `ghz.debug-switch` 测试身份切换和预览账号能力；不得擅自删除或关闭，但必须维持服务端授权边界。

## 三类项目与资格规则

项目类型固定为：

- `1`：人才公寓。
- `2`：保障性租赁住房（保租房）。
- `3`：市场化租赁。

当前资格规则：

- 人才公寓：保留原有综合资格流程、学历人工审核以及客户要求的批量配租/预览豁免；社保项当前为暂时关闭状态，以代码配置为准。
- 保租房：必须登录、身份证有效、年龄18至60周岁（包含18和60）；普通用户已有在住保租房合同时不可重复申请。
- 市场化租赁：必须登录、身份证有效、年龄满18周岁（包含18）；不限制已有同类型在住合同，不校验学历、社保、房产等人才资格项。
- 预览账号和批量配租用户仍必须通过保租房/市场租赁的身份证及年龄校验；现有保租房在住合同限制按当前豁免代码执行。
- 创建预订单和生成合同时，后端必须根据房源所属项目重新判断类型，不信任前端传入的项目类型。

## 房源、设施与合同

- 人才公寓使用 e签宝原合同模板；保租房与市场化租赁共用租赁合同模板。
- 模板 ID 从 `esign.template-id` 和 `esign.rental-template-id` 读取，不在业务代码新增硬编码模板 ID。
- e签宝模板控件以平台查询结果和 `EsignServiceImpl` 当前映射为准，不在规范中维护容易过期的控件 ID 清单。
- 法定代表人“栗毅”由租赁模板默认值显示，代码不重复填充。
- 设施总字典使用 `hz_facility_item`；人才公寓和租赁房源通过 `hz_facility_template_item` 的 `TALENT`、`RENTAL` 映射隔离。
- 租赁点验单的设施必须存在 e签宝 `componentKey` 映射；后台只能选择当前项目类型允许的设施。
- 合同账期按合同起始日使用自然月计算，例如13日至次月12日，禁止改回固定30天窗口。

## 管理后台约定

- 首页为 `ruoyi-ui/src/views/index.vue`。
- API 定义放在 `ruoyi-ui/src/api/`，页面放在 `ruoyi-ui/src/views/`。
- 图片上传后数据库保存 `/profile/...` 相对路径；展示时由 `VUE_APP_BASE_API` 拼接。
- 外部 HTTP(S) 图片直接使用，禁止把本地或生产域名硬编码进组件。
- Druid 控制台和 SpringDoc/Swagger 当前关闭；没有明确需求不得重新开放。

## uni-app 约定

- API、上传和静态资源地址统一从 `uniapp-h5/config/index.js` 读取，禁止在页面硬编码域名或端口。
- 请求统一复用 `uniapp-h5/utils/request.js`；新增接口放在 `uniapp-h5/api/`。
- 后端 `/profile/...` 图片使用 `config.staticUrl` 拼接；`/static/...` 和完整 HTTP(S) 地址直接使用。
- 需要登录的页面复用现有登录检查方式，避免每个页面复制一套跳转逻辑。
- `uniapp-h5/config/feature-flags.js` 当前状态：保租房开启、市场租赁开启、开票入口关闭。修改开关前先确认发布范围。
- 项目类型必须从项目详情传递到房源、资格校验、合同等后续页面，不能默认把所有业务当作人才公寓。

## 部署与发布

### 后端

- 推送 `master` 后，`ruoyi-**/**` 或 `pom.xml` 变更触发 `.github/workflows/backend-deploy.yml`。
- 服务器执行 `server-deploy.sh`：从 Gitee 拉取 `master`、Maven 打包、备份并替换 JAR、通过 Supervisor 重启和健康检查。
- CI 使用 SSH 端口3322；Supervisor 组名为 `ghz-backend:`。

### 管理后台

- 推送 `master` 后，`ruoyi-ui/**` 变更触发 `.github/workflows/frontend-admin-deploy.yml`。
- CI 使用 Node 16 构建并上传静态文件，然后重载容器内 OpenResty。

### H5 与微信小程序

- `uniapp-h5/**` 不触发自动部署。
- H5：HBuilderX 发行到 `uniapp-h5/unpackage/dist/build/web/`，再人工执行 `uniapp-h5/deploy-h5.sh`。
- 微信小程序：HBuilderX 发行到 `uniapp-h5/unpackage/dist/build/mp-weixin/`，微信开发者工具上传，随后在微信公众平台提交审核并发布。
- 新版审核和发布前，必须确认其依赖的后端及数据库迁移已上线；审核期间现网仍运行上一已发布版本。

## 生产验证与回退

- 部署后依次确认：GitHub Actions、服务器部署日志、进程状态、关键接口、数据库结果和实际用户流程。
- 涉及 e签宝时，至少验证模板填充、签署流创建、签署回调、合同 PDF、账单和入住记录；不能把“已创建签署链接”当作完整成功。
- 涉及支付时，必须核对第三方结果、金额、流水号、幂等、回调/查询和账单状态；不能把受理状态当作支付完成。
- 代码回退优先使用 `git revert`；生产数据按本次备份和回滚 SQL 恢复，不使用扩大范围的通用回滚。
