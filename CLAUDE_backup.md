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

