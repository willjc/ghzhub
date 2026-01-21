# VR看房功能说明

## 功能概述

已成功集成Photo Sphere Viewer实现360°全景VR查看功能，用户可在房源详情页中进入VR看房模式，体验真实的全景浏览效果。

## 技术实现

### 1. 后端实现

#### 数据库表结构
- 表名：`hz_house_vr`
- 字段：
  - `vr_id`: VR ID（主键，自增）
  - `house_id`: 房源ID（外键）
  - `vr_name`: VR名称
  - `vr_url`: VR图片URL（存储相对路径）
  - `sort_order`: 显示顺序
  - `del_flag`: 删除标志

#### API接口
- **获取VR列表**: `GET /system/house/{houseId}/vrs`
- **保存VR**: `POST /system/house/vrs`

#### 核心文件
- `HzHouseVr.java` - VR实体类
- `HzHouseVrMapper.java` - VR Mapper接口
- `HzHouseServiceImpl.java` - VR业务逻辑（getHouseVrs, saveHouseVrs）
- `HzHouseController.java` - VR控制器

### 2. 前端实现

#### 核心组件

##### VrViewer组件 (`src/components/VrViewer/index.vue`)
- 使用 `photo-sphere-viewer` 库实现360°全景查看
- 支持多VR图片切换
- 内置控制器：自动旋转、缩放、移动、全屏
- 响应式设计，适配不同屏幕

##### 功能特性
1. **360°全景浏览**：支持鼠标拖拽、滚轮缩放
2. **多VR切换**：房源有多个VR时显示切换按钮
3. **自动销毁**：关闭对话框时自动销毁查看器，释放资源
4. **错误处理**：VR加载失败时友好提示

#### 房源管理页面集成 (`src/views/gangzhu/house/index.vue`)
1. **VR上传**：编辑房源时支持上传最多5张VR图片
2. **详情展示**：房源详情页显示VR卡片和"进入VR看房"按钮
3. **VR查看**：点击按钮打开全屏VR查看器

### 3. 依赖库

```json
{
  "photo-sphere-viewer": "4.8.1"
}
```

## 安装步骤

### 1. 安装依赖

在 `ruoyi-ui` 目录下执行：

```bash
npm install
```

这将自动安装 `photo-sphere-viewer@4.8.1` 及其他依赖。

### 2. 启动项目

```bash
# 启动后端（端口8080）
cd ruoyi-admin
mvn spring-boot:run

# 启动前端（端口80）
cd ruoyi-ui
npm run dev
```

## 使用说明

### 管理员操作

1. **上传VR图片**
   - 进入"房源管理"页面
   - 点击"新增"或"修改"按钮
   - 在表单中找到"VR看房"字段
   - 点击上传按钮，选择VR全景图片（建议使用2:1比例的全景图片）
   - 最多上传5张VR图片
   - 保存房源

2. **查看VR效果**
   - 在房源列表中点击"详情"按钮
   - 详情页下方会显示"VR看房"卡片（如果有VR）
   - 点击"进入VR看房"按钮
   - 在VR查看器中：
     - 鼠标拖拽：旋转视角
     - 滚轮：缩放视野
     - 底部工具栏：自动旋转、缩放、全屏等
     - 多VR时：顶部显示切换按钮

### VR图片要求

1. **格式**：JPG、PNG
2. **比例**：建议使用 2:1 的全景图片（例如 4096x2048）
3. **类型**：等距柱状投影（Equirectangular）全景图
4. **大小**：建议不超过 5MB
5. **拍摄**：
   - 使用360°全景相机拍摄
   - 或使用手机VR拍摄APP（如Google Street View）
   - 或使用专业VR建模软件导出

### 获取VR全景图片的方法

1. **360°全景相机**：Insta360、Ricoh Theta等
2. **手机APP**：
   - Google Street View（iOS/Android）
   - 360 Panorama（iOS/Android）
3. **专业软件**：
   - Matterport（3D建模）
   - Kuula（VR制作平台）

## VR查看器控制说明

### 鼠标操作
- **拖拽**：左键按住拖动，旋转视角
- **滚轮**：缩放视野（FOV：30°-90°）
- **双击**：全屏切换

### 工具栏按钮
- **自动旋转**：开启/关闭自动水平旋转
- **缩放**：调整视野范围
- **移动**：启用/禁用鼠标移动
- **全屏**：进入/退出全屏模式

## 技术细节

### Photo Sphere Viewer配置

```javascript
{
  panorama: vrImageUrl,          // VR图片URL
  navbar: [                       // 导航栏控件
    'autorotate',                 // 自动旋转
    'zoom',                       // 缩放
    'move',                       // 移动
    'fullscreen'                  // 全屏
  ],
  defaultLong: 0,                 // 初始水平角度
  defaultLat: 0,                  // 初始垂直角度
  minFov: 30,                     // 最小视野角度（放大限制）
  maxFov: 90,                     // 最大视野角度（缩小限制）
  mousewheel: true,               // 启用滚轮缩放
  mousemove: true,                // 启用鼠标移动
  lang: { /* 中文UI */ }
}
```

### 图片URL处理

遵循RuoYi标准：
- 数据库存储：相对路径 `/profile/upload/2025/01/19/xxx.jpg`
- 前端显示：拼接 `baseUrl + 相对路径`
- 支持外部链接（`http://` 或 `https://`开头）

### 资源管理

- VR查看器在对话框关闭时自动销毁
- 避免内存泄漏
- 切换VR时只更新全景图，不重新创建查看器

## 常见问题

### 1. VR图片无法显示
- 检查图片格式是否为等距柱状投影
- 检查图片URL是否正确
- 检查图片是否已上传到服务器
- 打开浏览器控制台查看错误信息

### 2. VR查看器卡顿
- 优化图片大小（建议不超过5MB）
- 降低图片分辨率（4096x2048已足够清晰）
- 检查浏览器性能

### 3. 无法进入全屏
- 某些浏览器需要用户交互才能触发全屏
- 检查浏览器全屏权限设置

### 4. 多个VR无法切换
- 检查VR列表是否正确加载
- 查看控制台是否有错误

## 后续优化建议

1. **热点标注**：在VR中添加可点击热点，标注家具、设施等
2. **VR导航**：多个VR间建立空间导航关系
3. **音频导览**：为VR添加语音解说
4. **VR统计**：记录VR浏览时长、热点点击等数据
5. **移动端优化**：支持陀螺仪控制（手机倾斜查看）
6. **VR拍摄指南**：为用户提供VR图片拍摄教程

## 相关文件清单

### 后端
- `ruoyi-system/src/main/java/com/ruoyi/system/domain/HzHouseVr.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/mapper/HzHouseVrMapper.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/service/IHzHouseService.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/service/impl/HzHouseServiceImpl.java`
- `ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/HzHouseController.java`

### 前端
- `ruoyi-ui/src/components/VrViewer/index.vue` (新增)
- `ruoyi-ui/src/views/gangzhu/house/index.vue`
- `ruoyi-ui/src/api/gangzhu/house.js`
- `ruoyi-ui/package.json`

## 版本信息

- Photo Sphere Viewer: 4.8.1
- Vue: 2.6.12
- Element UI: 2.15.14
- Spring Boot: 3.5.4

---

**开发完成时间**: 2026-01-11
**开发者**: Claude Code
