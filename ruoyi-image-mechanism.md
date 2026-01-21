# RuoYi框架图片机制完全解析

## 我对RuoYi的理解

### 核心设计理念

**数据库只存储相对路径(/profile/...),前端显示时动态拼接baseUrl(/dev-api)**

---

## 完整流程

### 上传流程
```
1. 前端上传 → /dev-api/common/upload
2. 后端保存 → D:/ruoyi/uploadPath/upload/2025/01/19/xxx.jpg
3. 返回相对路径 → fileName: "/profile/upload/2025/01/19/xxx.jpg"
4. 数据库存储 → "/profile/upload/2025/01/19/xxx.jpg"
```

### 显示流程
```
1. 数据库读取 → "/profile/upload/2025/01/19/xxx.jpg"
2. 前端拼接 → "/dev-api" + "/profile/upload/2025/01/19/xxx.jpg"
3. 浏览器请求 → "/dev-api/profile/upload/2025/01/19/xxx.jpg"
4. 代理转发 → "http://localhost:8080/profile/upload/2025/01/19/xxx.jpg"
5. 静态映射 → "D:/ruoyi/uploadPath/upload/2025/01/19/xxx.jpg"
```

---

## 关键代码

### FileUploadUtils.getPathFileName()
```java
public static final String getPathFileName(String uploadDir, String fileName) {
    int dirLastIndex = RuoYiConfig.getProfile().length() + 1;
    String currentDir = StringUtils.substring(uploadDir, dirLastIndex);
    return Constants.RESOURCE_PREFIX + "/" + currentDir + "/" + fileName;
    // 返回: /profile/upload/2025/01/19/xxx.jpg
}
```

### CommonController.uploadFile()
```java
String fileName = FileUploadUtils.upload(filePath, file);
// fileName = "/profile/upload/2025/01/19/xxx.jpg"

ajax.put("fileName", fileName);  // 相对路径(重要!)
ajax.put("url", serverConfig.getUrl() + fileName);  // 完整URL(不使用)
```

### ImageUpload 组件
```javascript
handleUploadSuccess(res) {
  this.uploadList.push({
    url: res.fileName  // 只使用fileName,不使用url
  })
}

listToString(list) {
  // 去除baseUrl,只保留相对路径
  strs += list[i].url.replace(this.baseUrl, "")
  // 返回: "/profile/upload/xxx.jpg"
}

watch: {
  value(val) {
    // 显示时添加baseUrl
    item = { url: this.baseUrl + item }
  }
}
```

### 标准getImageUrl
```javascript
getImageUrl(url) {
  if (!url) return '';
  if (url.startsWith('http')) return url;

  const baseUrl = process.env.VUE_APP_BASE_API;
  if (url.indexOf(baseUrl) !== -1) return url;

  return baseUrl + (url.startsWith('/') ? url : '/' + url);
  // 返回: /dev-api + /profile/upload/xxx.jpg
}
```

---

## 核心要点

1. **数据库**: 存储 `/profile/upload/...`
2. **ImageUpload**: emit `/profile/upload/...`
3. **前端显示**: 拼接 `/dev-api/profile/upload/...`
4. **代理转发**: `/dev-api` → `http://localhost:8080`
5. **静态映射**: `/profile` → `D:/ruoyi/uploadPath`

---

## 用户头像案例

```java
// 后端返回
ajax.put("imgUrl", avatar);  // /profile/avatar/xxx.jpg

// 前端处理
this.options.img = process.env.VUE_APP_BASE_API + response.imgUrl
// /dev-api + /profile/avatar/xxx.jpg

// Vuex存储
avatar = process.env.VUE_APP_BASE_API + avatar
```

---

**总结**: RuoYi的核心是数据库只存相对路径,前端通过环境变量动态拼接,实现多环境部署。
