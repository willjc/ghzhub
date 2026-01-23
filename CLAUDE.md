# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个基于若依(RuoYi)框架的**港好住**信息系统平台,用于管理保租房、人才公寓和市场租赁业务。

- **后端**: Spring Boot 3.5.4 + Spring Security + MyBatis-Plus + Redis + JWT
- **前端**: Vue 2.6.12 + Element UI 2.15.14 + Vue Router + Vuex
- **数据库**: MySQL 8.2.0
- **JDK版本**: Java 17
- **架构模式**: 前后端分离

## 数据库操作规范 (极其重要! 必须遵守!)

**🔴 强制要求：所有数据库查询和操作必须使用 MCP MySQL 工具**

- ✅ **必须使用**: `mcp__mysql__*` 系列工具进行数据库操作
- ❌ **严禁使用**: Bash 执行 `mysql` 命令行工具
- ❌ **严禁使用**: 其他任何直接连接数据库的方式

### MCP 工具使用步骤

1. **连接数据库**: 使用 `mcp__mysql__connect_db`
2. **查询数据**: 使用 `mcp__mysql__query` (SELECT)
3. **修改数据**: 使用 `mcp__mysql__execute` (INSERT/UPDATE/DELETE)
4. **查看表结构**: 使用 `mcp__mysql__describe_table`
5. **列出所有表**: 使用 `mcp__mysql__list_tables`

## 数据库配置

**开发环境数据库连接信息**：
- **数据库名**: newgangzhu
- **主机**: localhost
- **端口**: 3306
- **用户名**: root
- **密码**: 123456
- **连接URL**: jdbc:mysql://localhost:3306/newgangzhu?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8

**说明**：
- 使用 MySQL MCP 工具时，直接连接到此数据库
- 数据库共有 **86个表**（系统表19个 + Quartz表11个 + 业务表54个 + 其他2个）
- 配置文件位置：`ruoyi-admin/src/main/resources/application-druid.yml`

## 核心开发原则 (必须遵守)

### 1. 测试规则
**重要**: 永远不要执行任何测试命令 (包括前端和后端)。所有测试由用户负责。
- ❌ 不要运行: `npm run test`, `mvn test`, `npm run dev` 等任何测试/运行命令
- ✅ 只负责: 代码编写、修改、文档更新

### 2. ORM 框架迁移计划
项目正在从 **MyBatis** 迁移到 **MyBatis-Plus**,以减少代码量。
- 新代码必须使用 MyBatis-Plus
- 旧代码逐步重构为 MyBatis-Plus
- 优先使用 `BaseMapper` 提供的 CRUD 方法,减少 XML 配置

## 常用命令

### 后端开发

```bash
# Maven 构建 (在项目根目录)
mvn clean install

# 运行后端服务
cd ruoyi-admin
mvn spring-boot:run

# 或使用 bat 脚本启动 (Windows)
ry.bat
# 选择 1 启动, 2 关闭, 3 重启, 4 查看状态

# 打包
mvn clean package
```

### 前端开发

```bash
# 进入前端目录
cd ruoyi-ui

# 安装依赖
npm install

# 启动开发服务器 (默认端口 80)
npm run dev

# 生产环境构建
npm run build:prod

# 预发布环境构建
npm run build:stage
```

### 数据库初始化

执行以下 SQL 文件初始化数据库:
- `sql/ry_20250522.sql` - 主数据库脚本
- `sql/quartz.sql` - 定时任务数据库脚本

## 项目架构

### 后端模块结构

```
ruoyi/
├── ruoyi-admin/        # 主应用模块 (启动入口)
│   └── com.ruoyi.RuoYiApplication.java  # 启动类
├── ruoyi-framework/    # 核心框架模块 (Spring Security, AOP, Redis 等)
├── ruoyi-system/       # 系统管理模块 (用户/角色/菜单/部门/岗位/字典)
├── ruoyi-common/       # 通用工具模块 (工具类/注解/异常/常量)
├── ruoyi-generator/    # 代码生成模块
└── ruoyi-quartz/       # 定时任务模块
```

### 前端目录结构

```
ruoyi-ui/src/
├── views/              # 页面组件
│   ├── index.vue      # 首页 (对应路由 /index, 非 home/index.vue)
│   ├── login.vue      # 登录页
│   ├── system/        # 系统管理页面
│   ├── monitor/       # 系统监控页面
│   └── tool/          # 系统工具页面
├── api/               # API 接口定义
├── router/            # 路由配置
│   └── index.js       # 主路由文件
├── store/             # Vuex 状态管理
├── components/        # 公共组件
├── layout/            # 布局组件
└── assets/            # 静态资源
```

### 关键配置文件

- **后端配置**: `ruoyi-admin/src/main/resources/application.yml`
  - 服务器端口: 8080
  - 数据库配置在 `application-druid.yml`
  - Redis 配置在主配置文件
  - JWT Token 配置

- **前端配置**: `ruoyi-ui/vue.config.js`
  - 开发服务器端口: 80
  - 后端代理地址: `http://localhost:8080`
  - API 前缀: `/dev-api` (开发环境)

## 重要架构说明

### 路由和首页

**重要**: 首页文件位于 `src/views/index.vue` (对应路由 `/index`),而不是 `src/views/home/index.vue`。修改首页时务必确认文件路径。

路由配置在 `src/router/index.js`:
- `constantRoutes` - 公共路由 (登录/注册/错误页/首页等)
- `dynamicRoutes` - 权限路由 (基于用户权限动态加载)

### 权限认证

- 基于 JWT Token 认证
- Spring Security + Redis 实现
- 前端通过 `js-cookie` 存储 token
- 后端通过 `@PreAuthorize` 注解控制权限

### 数据字典

系统使用数据字典管理下拉选项等固定数据,在 `系统管理 > 字典管理` 中配置。

### MyBatis-Plus 配置和使用

#### 依赖配置 (Spring Boot 3.x)

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.x</version>
</dependency>
```

#### 核心配置类

```java
@Configuration
@MapperScan("com.ruoyi.*.mapper")
public class MybatisPlusConfig {

    /**
     * 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

#### Mapper 接口定义

所有 Mapper 接口继承 `BaseMapper<T>`,自动获得 CRUD 方法:

```java
public interface UserMapper extends BaseMapper<User> {
    // BaseMapper 已提供:
    // - insert(T entity)
    // - deleteById(Serializable id)
    // - updateById(T entity)
    // - selectById(Serializable id)
    // - selectList(Wrapper<T> wrapper)
    // - selectPage(IPage<T> page, Wrapper<T> wrapper)
    // 等常用方法,无需编写 XML
}
```

#### 实体类注解

```java
@TableName("sys_user")  // 指定表名
public class User {
    @TableId(type = IdType.AUTO)  // 主键自增
    private Long userId;

    @TableField("user_name")  // 字段映射
    private String userName;

    @TableLogic  // 逻辑删除字段
    private Integer delFlag;
}
```

#### 分页查询示例

```java
// Service 层
IPage<User> page = new Page<>(1, 10);
IPage<User> result = userMapper.selectPage(page,
    new QueryWrapper<User>().eq("status", "0"));
```

#### 配置文件

```yaml
mybatis-plus:
  mapper-locations: classpath*:mapper/**/*Mapper.xml
  type-aliases-package: com.ruoyi.**.domain
  configuration:
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      logic-delete-field: delFlag  # 全局逻辑删除字段
      logic-delete-value: 2        # 删除值
      logic-not-delete-value: 0    # 未删除值
```

#### 迁移注意事项

1. **Mapper 接口**: 继承 `BaseMapper<T>` 替代手写基础 CRUD
2. **XML 文件**: 只保留复杂查询,简单 CRUD 删除
3. **分页**: 使用 `Page` 对象替代 `PageHelper`
4. **条件构造**: 使用 `QueryWrapper`/`LambdaQueryWrapper`
5. **Service 层**: 可继承 `ServiceImpl<M, T>` 获得更多便捷方法

### 代码生成器

系统内置代码生成器,可一键生成 CRUD 代码:
- 后端: Controller/Service/Mapper/Domain/XML
- 前端: Vue 页面/API/Router

访问路径: `系统工具 > 代码生成`

## 开发注意事项

### 前端开发

1. API 接口统一在 `src/api/` 目录下定义
2. 使用 Vuex 进行状态管理
3. 使用 `@/` 别名引用 `src` 目录
4. 遵循 Element UI 组件规范

### 后端开发

1. Controller 层使用 `@RestController` + `@RequestMapping`
2. Service 层接口与实现分离
3. 使用 `BaseMapper` 继承 MyBatis 通用方法
4. 统一返回格式: `AjaxResult`
5. 异常处理通过 `GlobalExceptionHandler` 全局拦截
6. 代码尽量写在ruoyi-admin模块，ruoyi-system主要是框架的一些文件

### API 文档

系统集成 SpringDoc (OpenAPI 3.0):
- 访问地址: `http://localhost:8080/swagger-ui.html`
- 配置路径: `application.yml` 中的 `springdoc` 节点

## 业务功能模块

项目实现港好住平台的以下核心功能:

1. **人才公寓管理** - 资格校验/选房/入住/退租/续租/账单缴费
2. **保租房管理** - 资格校验/选房/入住/退租/续租/账单缴费
3. **市场租赁** - 房源管理/预约看房/合同签署
4. **房源管理** - 项目/房源/楼栋/单元管理,支持地图找房和 VR 看房
5. **合同管理** - 在线签署/电子合同/合同下载
6. **账单缴费** - 押金/租金在线支付 (对接港区支付接口)
7. **开票管理** - 在线申请开票/发票查看下载
8. **资料上传** - 租户资料上传审核
9. **通知公告** - 政策文件/通知公告发布

详细功能清单见 `港好住信息系统平台功能清单.xlsx`

## 环境要求

- JDK 17+
- Maven 3.6+
- Node.js 8.9+
- MySQL 8.0+
- Redis 3.0+

## 启动顺序

1. 启动 Redis 服务
2. 启动 MySQL 并初始化数据库
3. 启动后端服务 (端口 8080)
4. 启动前端服务 (端口 80)
5. 访问 `http://localhost` 进入系统

默认管理员账号密码在初始化 SQL 中配置。

## RuoYi框架图片上传和显示机制 (重要!)

### 核心设计理念

**数据库只存储相对路径(/profile/...),前端显示时动态拼接baseUrl(/dev-api)**

这是RuoYi框架的标准设计模式,优势:
- 数据库路径简洁,不耦合域名
- 支持多环境部署(开发/测试/生产)
- 前端通过环境变量灵活配置访问地址

### 完整流程

#### 1. 上传流程
```
前端上传 → /dev-api/common/upload
     ↓
后端保存 → D:/ruoyi/uploadPath/upload/2025/01/19/xxx.jpg
     ↓
返回相对路径 → fileName: "/profile/upload/2025/01/19/xxx.jpg"
     ↓
数据库存储 → "/profile/upload/2025/01/19/xxx.jpg"
```

#### 2. 显示流程
```
数据库读取 → "/profile/upload/2025/01/19/xxx.jpg"
     ↓
前端拼接 → "/dev-api" + "/profile/upload/2025/01/19/xxx.jpg"
     ↓
浏览器请求 → "/dev-api/profile/upload/2025/01/19/xxx.jpg"
     ↓
代理转发 → "http://localhost:8080/profile/upload/2025/01/19/xxx.jpg"
     ↓
静态映射 → "D:/ruoyi/uploadPath/upload/2025/01/19/xxx.jpg"
```

### 核心代码

#### 后端: FileUploadUtils.getPathFileName()
```java
public static final String getPathFileName(String uploadDir, String fileName) {
    int dirLastIndex = RuoYiConfig.getProfile().length() + 1;
    String currentDir = StringUtils.substring(uploadDir, dirLastIndex);
    return Constants.RESOURCE_PREFIX + "/" + currentDir + "/" + fileName;
    // 返回: /profile/upload/2025/01/19/xxx.jpg
}
```

#### 后端: CommonController.uploadFile()
```java
@PostMapping("/upload")
public AjaxResult uploadFile(MultipartFile file) {
    String fileName = FileUploadUtils.upload(filePath, file);
    // fileName = "/profile/upload/2025/01/19/xxx.jpg" (相对路径)

    String url = serverConfig.getUrl() + fileName;
    // url = "http://localhost:8080/profile/..." (完整URL,不使用)

    ajax.put("fileName", fileName);  // ✅ 重要: 使用相对路径
    ajax.put("url", url);           // ❌ 不使用完整URL
    return ajax;
}
```

#### 前端: ImageUpload组件
```javascript
// 上传成功处理
handleUploadSuccess(res) {
  this.uploadList.push({
    url: res.fileName  // ✅ 只使用fileName字段(相对路径)
  })
}

// 转换为字符串
listToString(list) {
  strs += list[i].url.replace(this.baseUrl, "")
  // 返回: "/profile/upload/xxx.jpg" (去除baseUrl)
}

// 回显处理
watch: {
  value(val) {
    const list = val.split(',')
    this.fileList = list.map(item => {
      // 添加baseUrl用于显示
      if (item.indexOf(this.baseUrl) === -1 && !isExternal(item)) {
        item = { url: this.baseUrl + item }
        // /dev-api + /profile/upload/xxx.jpg
      }
      return item
    })
  }
}
```

#### 前端: 标准getImageUrl方法
```javascript
/** 获取图片完整URL - 遵循RuoYi标准 */
getImageUrl(url) {
  if (!url) return '';

  // 外部链接(http/https开头),直接返回
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url;
  }

  // RuoYi标准:数据库存储相对路径,前端拼接baseUrl
  const baseUrl = process.env.VUE_APP_BASE_API;  // /dev-api

  // 如果已包含baseUrl,直接返回
  if (url.indexOf(baseUrl) !== -1) {
    return url;
  }

  // 拼接baseUrl + 相对路径
  return baseUrl + (url.startsWith('/') ? url : '/' + url);
  // 返回: /dev-api + /profile/upload/xxx.jpg
}
```

### 编辑页面图片回显 (重要!)

#### 错误示例 ❌
```javascript
handleUpdate(row) {
  this.reset();
  getHouseType(id).then(response => {
    this.form = response.data;        // ❌ 直接赋值丢失响应式
    this.form.imageList = "";         // ❌ 覆盖图片数据
    this.loadImages(id);
  });
}
```

#### 正确示例 ✅
```javascript
handleUpdate(row) {
  this.reset();
  getHouseType(id).then(response => {
    // ✅ 使用Object.assign保持响应式
    Object.assign(this.form, response.data);
    // ✅ 不要清空imageList,等待loadImages异步加载
    this.loadImages(id);
    this.open = true;
  });
}

loadImages(id) {
  request({ url: `/api/${id}/images` }).then(response => {
    if (response.data && response.data.length > 0) {
      const imageUrls = response.data.map(item => item.imageUrl).join(',');

      // ✅ 使用$nextTick确保DOM更新后赋值
      this.$nextTick(() => {
        this.form.imageList = imageUrls;
      });
    } else {
      this.form.imageList = "";
    }
  });
}
```

### 静态资源映射

#### ResourcesConfig.java
```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // 将 /profile/** 请求映射到文件系统目录
    registry.addResourceHandler(Constants.RESOURCE_PREFIX + "/**")
            .addResourceLocations("file:" + RuoYiConfig.getProfile() + "/");
    // /profile/** → file:D:/ruoyi/uploadPath/
}
```

#### vue.config.js
```javascript
devServer: {
  proxy: {
    '/dev-api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
      pathRewrite: {
        '^/dev-api': ''  // 去除/dev-api前缀
      }
    }
  }
}
```

### 关键要点总结

1. **数据库存储**: 永远只存**相对路径** `/profile/upload/...`
2. **后端返回**: 使用`fileName`字段(相对路径),不使用`url`字段
3. **ImageUpload组件**: emit相对路径,watch时添加baseUrl
4. **前端显示**: 拼接 `baseUrl + 相对路径`
5. **编辑回显**: 使用`Object.assign`保持响应式,用`$nextTick`确保更新
6. **代理转发**: `/dev-api` → `http://localhost:8080`
7. **静态映射**: `/profile` → `D:/ruoyi/uploadPath`

### 常见错误

❌ **错误1**: 数据库存储完整URL
❌ **错误2**: 前端使用`response.url`字段
❌ **错误3**: 直接赋值`this.form = data`导致响应式丢失
❌ **错误4**: 异步加载前清空`imageList`
❌ **错误5**: 不使用`$nextTick`导致watch未触发

### 用户头像示例

```java
// 后端返回相对路径
ajax.put("imgUrl", "/profile/avatar/uuid.jpg");

// 前端拼接显示
this.avatar = process.env.VUE_APP_BASE_API + response.imgUrl;
// /dev-api + /profile/avatar/uuid.jpg

// Vuex存储
if (!isHttp(avatar)) {
  avatar = process.env.VUE_APP_BASE_API + avatar;
}
```

---

## Element UI 表格宽度自适应解决方案 (重要!)

### 问题现象
表格不能撑满页面宽度，出现空白或只显示部分内容

### 问题根源
Element UI 表格在以下情况下会出现宽度问题：

1. **所有列都设置了固定 `width`**
   - 表格总宽度等于所有列宽之和
   - 无法自动扩展填满容器

2. **设置了固定的 `height` 属性**
   - 固定高度会影响宽度的自动计算
   - `calc(100vh - 320px)` 等计算可能不准确

3. **父容器限制了可用宽度**
   - `.app-container` 有 20px padding
   - 影响实际可用空间

### 解决方案

#### 方案1：创建弹性列（推荐）
将至少一列从 `width` 改为 `min-width`：

```html
<!-- 问题代码 - 所有列固定宽度 -->
<el-table-column label="列名" width="120" />
<el-table-column label="列名" width="100" />
<el-table-column label="列名" width="100" />

<!-- 解决方案 - 至少一列使用 min-width -->
<el-table-column label="列名" width="120" />
<el-table-column label="列名" min-width="100" />  <!-- 这列可以自动扩展 -->
<el-table-column label="列名" width="100" />
```

#### 方案2：不设置表格宽度样式
移除表格的 `style="width: 100%"`，让 Element UI 自动计算：

```html
<!-- 不推荐 -->
<el-table style="width: 100%">

<!-- 推荐 -->
<el-table>
```

#### 方案3：使用 show-overflow-tooltip
对于较长的文本列，添加溢出提示：

```html
<el-table-column
  label="项目地址"
  prop="address"
  show-overflow-tooltip
/>
```

### 最佳实践
1. **混合使用固定宽度和最小宽度**：
   - 固定列（如选择框、状态、操作列）：使用 `width`
   - 内容列（如名称、地址、描述）：使用 `min-width`

2. **避免设置固定高度**：
   - 移除 `height="calc(100vh - 320px)"`
   - 让表格自然适应内容

3. **与正常页面对比**：
   - 查看项目管理页面（能正常撑满）
   - 模仿其列宽设置方式

### 调试方法
1. 打开浏览器开发者工具（F12）
2. 检查表格元素的实际宽度
3. 运行以下代码：
   ```javascript
   // 检查容器宽度
   document.querySelector('.app-container').offsetWidth
   // 检查表格宽度
   document.querySelector('.el-table').offsetWidth
   ```

### 注意事项
- `min-width` 设置最小宽度，但不会限制最大宽度
- 固定宽度的列不会被压缩
- 移除 `height` 属性后，表格高度会根据内容自适应



---

## UniApp H5 用户端项目 (uniapp-h5)

### 项目概述

用户端采用 **uni-app** 框架开发，支持编译为 H5、小程序、App 多端应用。当前主要开发 H5 端，页面功能仍在持续完善中。

- **框架**: uni-app (基于 Vue 2)
- **应用名称**: ganhaozhu (港好住)
- **appid**: __UNI__B4D14B0
- **版本**: 1.0.0
- **项目目录**: `d:\lasthm\gangzhu\uniapp-h5`

### 项目结构

```
uniapp-h5/
├── pages/                  # 页面目录
│   ├── index/             # 首页（底部导航）
│   ├── affairs/           # 办事（底部导航）
│   ├── service/           # 服务（底部导航）
│   ├── my/                # 我的（底部导航）
│   ├── talent/            # 人才公寓页面
│   └── coupon/            # 优惠券页面
├── static/                # 静态资源目录
│   ├── 苹方字体/          # PingFang SC 字体文件
│   ├── my/                # 个人中心图标
│   └── *.png              # 其他图片资源
├── App.vue                # 应用入口
├── main.js                # 项目入口文件
├── pages.json             # 页面配置文件
├── manifest.json          # 应用配置文件
└── uni.scss               # 全局样式变量
```

### 核心配置文件

#### pages.json - 页面路由配置

```json
{
  "pages": [
    { "path": "pages/index/index", "style": { "navigationBarTitleText": "首页" } },
    { "path": "pages/affairs/index", "style": { "navigationBarTitleText": "办事" } },
    { "path": "pages/service/index", "style": { "navigationBarTitleText": "服务" } },
    { "path": "pages/my/index", "style": { "navigationBarTitleText": "我的" } },
    { "path": "pages/talent/index", "style": { "navigationBarTitleText": "人才公寓" } },
    { "path": "pages/coupon/index", "style": { "navigationBarTitleText": "优惠券" } }
  ],
  "tabBar": {
    "color": "#999999",
    "selectedColor": "#4A90E2",
    "list": [...]
  }
}
```

#### manifest.json - 应用配置

- **Vue版本**: "vueVersion": "2"
- **H5配置**: `{ "router": { "base": "./" } }`
- **支持平台**: H5, 微信小程序, 支付宝小程序, 百度小程序, 头条小程序, App

### 页面功能详解

#### 1. 首页 (pages/index/index.vue)

**功能模块**:
- **Hero Banner**: 顶部宣传图
- **搜索栏**: 搜索房源（浮动在 Banner 底部）
- **功能图标网格**: 9 个快捷入口
  - 人才公寓、保租房、市场租赁、地图找房、人才家园
  - 政策文件、资料上传、优惠券、我的消息
- **通知栏**: 滚动显示最新通知
- **房源列表**:
  - **分类标签**: 人才公寓 / 保租房 / 市场租赁
  - **子标签**: 项目 / 房源
  - **房源卡片**: 显示标题、状态、地址、标签、价格

**数据结构** (listingData):
```javascript
{
  title: '航南新城专家公寓项目',      // 项目名称
  hasUnits: true,                      // 是否有房源
  totalUnits: 100,                     // 总套数
  distance: '8.2km',                   // 距离
  address: '航空港区新港大道与遵大路交叉口',  // 地址
  tags: ['商业街', '停车场', '运动场'],        // 标签
  price: 2000,                         // 起租价格
  image: '/static/矩形 21@2x.png'      // 封面图片
}
```

**跳转逻辑**:
- 点击"人才公寓"图标 → `uni.navigateTo({ url: '/pages/talent/index' })`
- 点击"优惠券"图标 → `uni.navigateTo({ url: '/pages/coupon/index' })`
- 点击房源卡片 → 跳转到人才公寓页面（待完善详情页）

#### 2. 人才公寓页面 (pages/talent/index.vue)

**功能模块**:
- **搜索栏**: 搜索框 + 搜索按钮
- **房源列表**: 与首页类似的房源卡片（垂直滚动）
- **底部提示**: "—— 我是有底线的 ——"

**当前状态**: 使用静态数据，点击房源只有 console.log 输出

#### 3. 我的页面 (pages/my/index.vue)

**功能模块**:
- **用户信息卡**: 头像 + 昵称 + 手机号（点击跳转个人资料）
- **菜单列表**:
  - 我的消息
  - 我的房源
  - 我的合同
  - 申诉记录
  - 信息维护
  - 优惠券
  - 关于我们

**当前状态**: 菜单点击只有 console.log 输出，未实现具体功能

#### 4-6. 其他页面

- **办事页面** (pages/affairs/index.vue): 页面存在但未开发
- **服务页面** (pages/service/index.vue): 页面存在但未开发
- **优惠券页面** (pages/coupon/index.vue): 页面存在但未开发

### UI 设计规范

#### 主题色彩
- **主色**: `#4A90E2` (蓝色)
- **背景色**: `#f5f6fc`
- **卡片背景**: `#ffffff`
- **文本色**: 标题 `#1a1a1a`, 正文 `#333333`, 次要 `#666666`, 提示 `#999999`
- **价格色**: `#e5252b` (红色)

#### 尺寸规范
- **rpx 单位**: 响应式像素（750rpx = 屏幕宽度）
- **圆角**: 小卡片 `16rpx`, 大卡片 `20rpx`, 按钮 `50rpx`
- **间距**: 页面边距 `24-30rpx`, 卡片间距 `20-30rpx`

#### 字体
- **全局字体**: "PingFang SC", "苹方-简", sans-serif
- **字体大小**: 标题 `32-40rpx`, 正文 `26-28rpx`, 辅助 `24rpx`, 小字 `20rpx`
- **字重**: normal 400, medium 500, bold 700

### API 对接规范

#### 当前状态
所有页面使用静态数据（硬编码在组件 data 中），**需要与后端 RuoYi API 对接**。

#### 待对接的 API 接口

##### 1. 用户相关
- `GET /app/user/info` - 获取用户信息
- `POST /app/user/login` - 用户登录
- `POST /app/user/logout` - 用户登出

##### 2. 房源相关
- `GET /app/house/list` - 获取房源列表
  - 参数: `categoryType`（人才公寓/保租房/市场租赁）
  - 参数: `pageNum`, `pageSize`
- `GET /app/house/{id}` - 获取房源详情
- `GET /app/project/list` - 获取项目列表
- `GET /app/project/{id}` - 获取项目详情

##### 3. 消息通知
- `GET /app/notice/list` - 获取通知列表
- `GET /app/notice/{id}` - 获取通知详情

##### 4. 优惠券
- `GET /app/coupon/my` - 获取我的优惠券
- `GET /app/coupon/list` - 获取可领取优惠券

##### 5. 合同相关
- `GET /app/contract/my` - 获取我的合同列表
- `GET /app/contract/{id}` - 获取合同详情

#### API 调用方式

**🔴 重要：API 地址配置规范 (必须遵守!)**

UniApp H5 的 API 配置集中管理在 `config/index.js` 文件中，**严禁硬编码 API 地址**。

- ✅ **必须使用**: 从 `config/index.js` 读取 API 地址
- ✅ **必须使用**: 从 `@/utils/request` 导入 `BASE_URL` 用于拼接图片路径
- ❌ **严禁硬编码**: `baseUrl: 'http://localhost:8080'` 或类似的硬编码

##### 正确的使用方式

**1. API 请求 - 使用 api 模块**
```javascript
// api/checkin.js
import { get } from '@/utils/request'

export function getMyListings(tenantId) {
  // utils/request 已经从 config 读取了 BASE_URL
  return get(`/h5/app/checkin/mylistings/${tenantId}`)
}
```

**2. 图片路径拼接 - 使用 BASE_URL**
```javascript
import { BASE_URL } from '@/utils/request'

export default {
  methods: {
    getImageUrl(url) {
      if (!url) return '/static/default.png'

      // 外部链接直接返回
      if (url.startsWith('http://') || url.startsWith('https://')) {
        return url
      }

      // 本地静态资源
      if (url.startsWith('/static/')) {
        return url
      }

      // 后端返回的相对路径，拼接 BASE_URL（从 config 读取）
      return BASE_URL + url
    }
  }
}
```

**3. 配置文件结构** (`config/index.js`)
```javascript
// 开发环境配置
const dev = {
  baseUrl: 'http://localhost:8080',
  uploadUrl: 'http://localhost:8080',
  staticUrl: 'http://localhost:8080',
  timeout: 30000,
}

// 测试环境配置
const test = {
  baseUrl: 'http://localhost:8090',  // ⚠️ 测试环境使用 8090 端口
  uploadUrl: 'http://localhost:8090',
  staticUrl: 'http://localhost:8090',
  timeout: 30000,
}

// 生产环境配置
const prod = {
  baseUrl: 'https://api.example.com',
  uploadUrl: 'https://api.example.com',
  staticUrl: 'https://cdn.example.com',
  timeout: 30000,
}

// 当前使用的环境（可根据需要修改）
const env = 'test'  // 'dev' | 'test' | 'prod'

export default {
  baseUrl: config[env].baseUrl,
  uploadUrl: config[env].uploadUrl,
  staticUrl: config[env].staticUrl,
  timeout: config[env].timeout,
}
```

**4. Request 工具** (`utils/request.js`)
```javascript
import config from '@/config/index'

// 从配置文件读取 BASE_URL
const BASE_URL = config.baseUrl

export function request(options) {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token') || ''

    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
        ...options.header
      },
      success: (res) => {
        if (res.statusCode === 200) {
          if (res.data.code === 200 || res.data.code === undefined) {
            resolve(res.data)
          } else {
            uni.showToast({ title: res.data.msg || '请求失败', icon: 'none' })
            reject(res.data)
          }
        } else {
          reject(res)
        }
      },
      fail: (err) => {
        console.error('网络请求失败:', err)
        uni.showToast({ title: '网络连接失败', icon: 'none' })
        reject(err)
      }
    })
  })
}

// Default export（包含 BASE_URL）
export default {
  request,
  get,
  post,
  put,
  del,
  BASE_URL
}

// 🔴 重要：命名导出 BASE_URL，供组件使用 { BASE_URL } 方式导入
export { BASE_URL }
```

##### 常见错误

❌ **错误1**: 硬编码 API 地址
```javascript
// ❌ 错误 - 硬编码端口
data() {
  return {
    baseUrl: 'http://localhost:8080'  // 错误！
  }
}
```

❌ **错误2**: 不使用配置的 BASE_URL 拼接图片
```javascript
// ❌ 错误
getImageUrl(url) {
  return 'http://localhost:8080' + url  // 硬编码
}

// ✅ 正确
import { BASE_URL } from '@/utils/request'

getImageUrl(url) {
  return BASE_URL + url  // 使用配置
}
```

#### 数据映射规范

前端字段与后端字段的对应关系：

```javascript
// 房源列表数据映射
{
  title: house.projectName,        // 项目名称
  hasUnits: house.houseStatus === '0',  // 是否有房源（空置）
  totalUnits: house.totalCount,    // 总套数
  distance: calculateDistance(house.latitude, house.longitude),  // 计算距离
  address: house.address,          // 地址
  tags: house.facilities?.split(',') || [],  // 设施标签
  price: house.rentPrice,          // 租金
  image: house.coverImage          // 封面图（需拼接 baseUrl）
}
```

### 开发注意事项

#### 1. 路由跳转
- **页面跳转**: `uni.navigateTo({ url: '/pages/xxx/xxx' })`
- **Tab 切换**: `uni.switchTab({ url: '/pages/index/index' })`
- **返回上一页**: `uni.navigateBack()`
- **重定向**: `uni.redirectTo({ url: '/pages/xxx/xxx' })`

#### 2. 本地存储
- **同步方法**: `uni.setStorageSync('key', value)`, `uni.getStorageSync('key')`
- **异步方法**: `uni.setStorage({ key, data, success, fail })`

#### 3. 图片路径处理 (标准规范 - 必须遵守!)

**🔴 核心原则：统一使用 `getImageUrl()` 方法处理所有图片路径**

##### 标准 getImageUrl() 方法模板

每个需要显示图片的页面组件都应该包含此方法：

```javascript
<script>
import config from '@/config/index'

export default {
  methods: {
    /**
     * 获取图片完整URL
     * @param {String} imagePath 图片相对路径
     * @returns {String} 完整URL
     */
    getImageUrl(imagePath) {
      // 1. 空值处理 - 返回默认图
      if (!imagePath) {
        return '/static/矩形 21@2x.png'  // 或其他默认图片
      }

      // 2. 外部链接直接返回
      if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
        return imagePath
      }

      // 3. 本地静态资源直接返回
      if (imagePath.startsWith('/static/')) {
        return imagePath
      }

      // 4. 后端图片路径 (/profile/开头) - 拼接 staticUrl
      if (imagePath.startsWith('/profile/')) {
        return config.staticUrl + imagePath
      }

      // 5. 其他情况直接返回
      return imagePath
    }
  }
}
</script>
```

##### 使用示例

```javascript
// 在数据转换中使用
transformProjectData(project) {
  return {
    title: project.projectName,
    image: this.getImageUrl(project.coverImage)  // ✅ 使用 getImageUrl
  }
}

transformHouseData(house) {
  return {
    title: house.title,
    image: this.getImageUrl(house.mainImage)  // ✅ 使用 getImageUrl
  }
}

// 在模板中使用
<template>
  <image :src="getImageUrl(item.image)" mode="aspectFill" />
</template>
```

##### 配置文件说明

`config/index.js` 中的 staticUrl 配置：

```javascript
// 测试环境
const test = {
  staticUrl: 'http://192.168.31.146:8090',  // 图片服务器地址
  // ...
}

// 生产环境
const prod = {
  staticUrl: 'http://ghzapi.dayushaiwang.com',
  // ...
}
```

##### 图片路径类型对照表

| 路径类型 | 示例 | 处理方式 |
| --- | --- | --- |
| 空值 | `null`, `""`, `undefined` | 返回默认图 `/static/矩形 21@2x.png` |
| 外部链接 | `https://example.com/image.jpg` | 直接返回 |
| 本地静态 | `/static/矩形@2x.png` | 直接返回 |
| 后端图片 | `/profile/upload/xxx.jpg` | 拼接 `staticUrl` |

##### 常见错误 ❌

```javascript
// ❌ 错误1: 直接使用后端返回的路径
image: house.mainImage  // 可能是 /profile/upload/xxx.jpg，无法直接访问

// ❌ 错误2: 硬编码 baseUrl
image: 'http://192.168.31.146:8090' + house.mainImage

// ❌ 错误3: 使用错误的配置字段
image: config.baseUrl + house.mainImage  // baseUrl 用于API，不是图片

// ❌ 错误4: 只处理 http 开头，遗漏 /profile/ 路径
getImageUrl(url) {
  if (url.startsWith('http')) return url
  return url  // /profile/xxx.jpg 无法访问
}
```

##### 正确做法 ✅

```javascript
// ✅ 正确1: 始终使用 getImageUrl
image: this.getImageUrl(house.mainImage)

// ✅ 正确2: 使用 config.staticUrl
getImageUrl(url) {
  if (url.startsWith('/profile/')) {
    return config.staticUrl + url  // staticUrl 专门用于图片资源
  }
  return url
}

// ✅ 正确3: 导入 config 模块
import config from '@/config/index'
```

#### 4. 响应式单位
- **rpx**: 响应式像素（750rpx = 屏幕宽度）
- **px**: 固定像素
- **vh/vw**: 视口高度/宽度百分比

#### 5. 条件编译
uni-app 支持多端条件编译：

```javascript
// #ifdef H5
console.log('仅在 H5 端执行')
// #endif

// #ifdef MP-WEIXIN
console.log('仅在微信小程序执行')
// #endif

// #ifndef H5
console.log('除 H5 外的平台执行')
// #endif
```

### 开发优先级

根据项目进度，建议按以下顺序完善功能：

1. **Phase 1 - API 对接**
   - 创建 `utils/request.js` 统一请求工具
   - 创建 `api/house.js`、`api/user.js` 等 API 模块
   - 替换首页静态数据为真实 API 数据
   - 实现用户登录/登出功能

2. **Phase 2 - 核心页面**
   - 完善房源详情页（显示房源完整信息、图片轮播、VR 看房）
   - 完善人才公寓页面（筛选、排序、分页）
   - 完善我的消息页面
   - 完善我的合同页面

3. **Phase 3 - 业务流程**
   - 实现预约看房流程
   - 实现资格校验流程
   - 实现在线签约流程
   - 实现账单缴费功能

4. **Phase 4 - 扩展功能**
   - 地图找房（集成地图 SDK）
   - 优惠券功能
   - 申诉记录
   - 政策文件查看
   - 资料上传

### 调试和构建

#### 运行项目
```bash
# 使用 HBuilderX 打开项目
# 或使用 CLI

# 运行 H5
npm run dev:h5

# 构建 H5
npm run build:h5

# 运行微信小程序
npm run dev:mp-weixin
```

#### 预览地址
- **H5 预览**: `http://localhost:8080` (默认)
- **编译输出**: `unpackage/dist/build/web/`

### 与后端集成清单

#### 需要后端提供的接口前缀
- **用户端 API 前缀**: `/app/*`（区别于管理端 `/system/*`）

#### 需要后端支持的功能
- ✅ JWT Token 认证（已有）
- ✅ 房源管理 API（已有，需要提供用户端简化版）
- ✅ 用户管理 API（已有）
- ✅ 文件上传 API（已有）
- ❌ 优惠券模块（待开发）
- ❌ 申诉记录模块（待开发）
- ❌ 在线签约模块（待开发）
- ❌ 在线支付模块（待开发）

---

## UniApp H5 用户端用户登录处理规范 (重要!)

### 问题背景

用户端页面在调用需要用户身份的API时，如果未正确获取和传递用户ID，会导致"用户未登录"错误。

### 登录机制

#### 登录成功后的存储
登录成功后（[pages/login/index.vue](d:\lasthm\gangzhu\uniapp-h5\pages\login\index.vue:64-66)会存储三个信息：

```javascript
uni.setStorageSync('token', response.data.token)
uni.setStorageSync('userId', response.data.userInfo.userId)
uni.setStorageSync('userInfo', response.data.userInfo)
```

#### userInfo 数据结构
```javascript
{
  userId: 123456,
  nickname: "微信用户",
  phone: "13800138001",
  realName: "张三",
  // ... 其他用户信息
}
```

### 标准用户登录处理模式

#### 页面 onLoad 模板

```javascript
onLoad(options) {
	// 1. 获取用户信息
	const userInfo = uni.getStorageSync('userInfo')
	if (!userInfo || !userInfo.userId) {
		uni.showToast({
			title: '请先登录',
			icon: 'none'
		})
		setTimeout(() => {
			uni.navigateTo({
				url: '/pages/login/index'
			})
		}, 1500)
		return
	}

	// 2. 保存用户ID到组件data
	this.userId = userInfo.userId

	// 3. 执行页面初始化逻辑
	this.loadData()
}
```

#### API 定义模板

```javascript
import { get, post, put } from '@/utils/request'

/**
 * 获取我的XXX列表
 * @param {number} userId 用户ID
 */
export function getMyList(userId) {
  return get(`/h5/app/xxx/myList?userId=${userId}`)
}

/**
 * 获取详情
 * @param {number} id 数据ID
 * @param {number} userId 用户ID (用于权限验证)
 */
export function getDetail(id, userId) {
  return get(`/h5/app/xxx/${id}?userId=${userId}`)
}

/**
 * 提交/操作
 * @param {Object} data 数据
 */
export function submitData(data) {
  return post('/h5/app/xxx/submit', data)  // userId包含在data中
}

/**
 * 催办/取消等操作
 * @param {number} id 数据ID
 * @param {number} userId 用户ID
 */
export function operate(id, userId) {
  return put(`/h5/app/xxx/operate/${id}?userId=${userId}`)
}
```

#### 页面调用示例

```javascript
export default {
	data() {
		return {
			userId: null  // 当前登录用户ID
		}
	},
	onLoad() {
		const userInfo = uni.getStorageSync('userInfo')
		if (!userInfo || !userInfo.userId) {
			uni.showToast({ title: '请先登录', icon: 'none' })
			setTimeout(() => {
				uni.navigateTo({ url: '/pages/login/index' })
			}, 1500)
			return
		}
		this.userId = userInfo.userId
		this.loadData()
	},
	methods: {
		async loadData() {
			if (!this.userId) return

			// 调用API时传递userId
			const res = await getMyList(this.userId)
			// ...
		},
		async handleUrge(id) {
			const res = await urgeComplaint(id, this.userId)
			// ...
		}
	}
}
```

### 关键要点

1. ✅ **所有需要用户身份的页面**必须在 `onLoad` 中进行登录检查
2. ✅ **userId 必须作为参数传递给API**，不能依赖后端从token解析
3. ✅ **未登录时友好提示并跳转到登录页**
4. ✅ **登录检查统一放在 `onLoad` 中**，避免重复代码

### 参考实现

可参考以下页面的完整实现：
- [pages/affairs/contract.vue](d:\lasthm\gangzhu\uniapp-h5\pages\affairs\contract.vue:124-152) - 我的合同
- [pages/affairs/appointment.vue](d:\lasthm\gangzhu\uniapp-h5\pages\affairs\appointment.vue:74-96) - 我的预约

---

**更新时间**: 2026-01-21
**维护者**: Claude Code
