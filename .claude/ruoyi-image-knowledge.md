# RuoYi框架图片处理机制 - Claude记忆

## 核心原则 (必须牢记!)

**数据库只存储相对路径,前端拼接baseUrl显示**

## 数据流向

### 上传
1. 文件 → 后端 → 磁盘 (`D:/ruoyi/uploadPath/upload/...`)
2. 返回相对路径 `fileName: "/profile/upload/..."`
3. 数据库存储: `/profile/upload/...`

### 显示
1. 数据库读取: `/profile/upload/...`
2. 前端拼接: `/dev-api + /profile/upload/...`
3. 浏览器请求: `/dev-api/profile/...`
4. 代理转发: `http://localhost:8080/profile/...`
5. 静态映射: `D:/ruoyi/uploadPath/...`

## 关键代码模式

### 后端返回格式
```java
ajax.put("fileName", "/profile/upload/xxx.jpg");  // ✅ 使用这个
ajax.put("url", "http://localhost:8080/profile/..."); // ❌ 不使用
```

### 前端getImageUrl
```javascript
const baseUrl = process.env.VUE_APP_BASE_API; // /dev-api
return baseUrl + url; // /dev-api + /profile/upload/xxx.jpg
```

### 编辑回显 (最容易出错!)
```javascript
// ✅ 正确
Object.assign(this.form, response.data);
this.$nextTick(() => {
  this.form.imageList = imageUrls;
});

// ❌ 错误
this.form = response.data;  // 丢失响应式
this.form.imageList = "";   // 覆盖数据
```

## 常见错误

1. ❌ 数据库存储完整URL (应该只存相对路径)
2. ❌ 使用response.url字段 (应该使用fileName)
3. ❌ 直接赋值this.form (应该用Object.assign)
4. ❌ 异步前清空imageList (应该等待异步完成)
5. ❌ 不用$nextTick (可能导致watch未触发)

## 快速检查清单

- [ ] 数据库存的是 `/profile/...` 格式吗?
- [ ] 使用的是 `response.fileName` 而不是 `response.url` 吗?
- [ ] 编辑时用了 `Object.assign` 吗?
- [ ] 图片回显用了 `$nextTick` 吗?
- [ ] getImageUrl 拼接了 baseUrl 吗?
