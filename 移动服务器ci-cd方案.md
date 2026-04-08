# 移动服务器 CI/CD 部署方案（1Panel 版）

> 基于 GitHub Actions + 1Panel，适用于前端服务器、后端服务器、Redis 缓存服务器、文件存储服务器 + MySQL 云数据库的四台 Ubuntu 服务器架构。

---

## 一、架构总览

```
git push master
       │
       ▼
GitHub Actions (自动触发)
       │
       ├──► 前端服务器 (1Panel + OpenResty)  ← 推静态文件 + reload openresty
       │
       └──► 后端服务器 (1Panel + Supervisor) ← 推 JAR + supervisorctl restart
```

**四台服务器角色：**

| 服务器 | IP | 用途 | CI/CD 操作 |
|--------|-----|------|-----------|
| 前端服务器 | `36.133.39.148` | OpenResty 托管 Vue 管理后台 + H5 用户端 | 推静态文件 + reload openresty |
| 后端服务器 | `36.133.39.49` | Spring Boot JAR（Supervisor 管理） | 推 JAR + supervisorctl restart |
| 缓存服务器 | `36.133.40.235` | Redis | 无需 CI/CD 操作 |
| 存储服务器 | `36.133.41.239` | uploadPath 文件存储 | 无需 CI/CD 操作 |

---

## 二、1Panel 与 CI/CD 的关系

**完全兼容，互不干扰。**

| 功能 | 1Panel 负责 | CI/CD 负责 |
|------|------------|-----------|
| 站点创建、SSL 证书 | ✅ | ❌ |
| Nginx 配置 | ✅ | ❌ |
| 服务启停（日常） | ✅ | ❌ |
| 代码构建、文件推送 | ❌ | ✅ |
| 服务重启（发版时） | ❌ | ✅ |

**1Panel 注意事项：**
- 使用的是 **OpenResty**（增强版 Nginx），不是原版 nginx
- reload 命令是 `systemctl reload openresty`，不是 `nginx -s reload`
- Web 文件默认用户组是 `www-data`（Ubuntu 系统），不是 `www`（CentOS 习惯）
- 默认 Web 根目录是 `/www/wwwroot/`

---

## 三、两台服务器安装 1Panel

在**前端服务器**和**后端服务器**分别执行：

```bash
curl -sSL https://resource.fit2cloud.com/1panel/package/quick_start.sh -o quick_start.sh
sudo bash quick_start.sh
```

安装完成后通过 `http://服务器IP:面板端口` 访问。

---

## 四、准备工作

### 4.1 创建部署专用用户

在**前端服务器（36.133.39.148）**和**后端服务器（36.133.39.49）**执行：
sudo useradd -m -s /bin/bash deploy

# 前端服务器：加入 www-data 组（Ubuntu 上 OpenResty 使用 www-data）
sudo usermod -aG www-data deploy

# 配置 sudoers（只允许特定命令，安全起见）
sudo visudo
```

**前端服务器** sudoers 添加：

```
deploy ALL=(ALL) NOPASSWD: /usr/bin/systemctl reload openresty
```

**后端服务器** sudoers 添加：

```
deploy ALL=(ALL) NOPASSWD: /usr/bin/supervisorctl restart ghz-backend, /usr/bin/supervisorctl status ghz-backend
```

### 4.2 生成 SSH 部署密钥

在本机执行（使用独立密钥，不要用个人密钥）：

```bash
ssh-keygen -t ed25519 -C "github-deploy" -f ~/.ssh/github_deploy
# 一路回车，不设密码
```

生成两个文件：
- `github_deploy`（**私钥**，放入 GitHub Secrets）
- `github_deploy.pub`（**公钥**，放到各台服务器）

### 4.3 将公钥部署到服务器

分别在**前端服务器（36.133.39.148）**和**后端服务器（36.133.39.49）**执行：
mkdir -p ~/.ssh && chmod 700 ~/.ssh
echo "ssh-ed25519 AAAA...你的公钥内容..." >> ~/.ssh/authorized_keys
chmod 600 ~/.ssh/authorized_keys
```

### 4.4 配置 GitHub Secrets

进入 GitHub 仓库 → **Settings → Secrets and variables → Actions → New repository secret**

| Secret 名称 | 内容 |
|------------|------|
| `SSH_PRIVATE_KEY` | `github_deploy` 私钥全文（含 `-----BEGIN...` 行） |
| `DEPLOY_USER` | `deploy` |
| `FRONTEND_HOST` | `36.133.39.148`（前端服务器 IP） |
| `BACKEND_HOST` | `36.133.39.49`（后端服务器 IP） |
| `FRONTEND_ADMIN_PATH` | `/www/wwwroot/ghz-admin` |
| `FRONTEND_H5_PATH` | `/www/wwwroot/ghz-h5` |
| `BACKEND_JAR_PATH` | `/www/wwwroot/ghz-backend` |

---

## 五、后端服务器配置（1Panel + Supervisor）

### 5.1 在 1Panel 安装 JDK 和 Supervisor

```
1Panel → 应用商店 → 搜索 "OpenJDK" → 安装 17
1Panel → 应用商店 → 搜索 "Supervisor" → 安装
```

### 5.2 创建应用目录

```bash
sudo mkdir -p /www/wwwroot/ghz-backend/logs
sudo chown -R deploy:deploy /www/wwwroot/ghz-backend
```

### 5.3 在 1Panel 配置 Supervisor 进程

```
1Panel → 网站（或工具）→ Supervisor → 添加守护进程
```

填入以下配置：

```ini
[program:ghz-backend]
command=/usr/local/sdkman/candidates/java/17/bin/java -Xms256m -Xmx512m -jar /www/wwwroot/ghz-backend/ruoyi-admin.jar --spring.profiles.active=prod
directory=/www/wwwroot/ghz-backend
user=deploy
autostart=true
autorestart=true
startsecs=15
stopwaitsecs=30
stdout_logfile=/www/wwwroot/ghz-backend/logs/app.log
stdout_logfile_maxbytes=50MB
stdout_logfile_backups=5
stderr_logfile=/www/wwwroot/ghz-backend/logs/app-error.log
```

> **JDK 路径说明**：1Panel 安装的 JDK 路径可能不同，通过 `which java` 或 `readlink -f $(which java)` 确认实际路径后填入。

配置完成后：

```bash
sudo supervisorctl reread
sudo supervisorctl update
sudo supervisorctl start ghz-backend
```

---

## 六、前端服务器配置（1Panel + OpenResty）

### 6.1 在 1Panel 创建网站

```
1Panel → 网站 → 创建网站
  类型：静态网站
  域名：admin.caigon.cn
  根目录：/www/wwwroot/ghz-admin
```

```
1Panel → 网站 → 创建网站
  类型：静态网站
  域名：app.caigon.cn
  根目录：/www/wwwroot/ghz-h5
```

还需要为 API 单独创建一个反向代理站点：

```
1Panel → 网站 → 创建网站
  类型：反向代理
  域名：api.caigon.cn
  代理地址：http://36.133.39.49:8080
```

### 6.2 配置反向代理（接口转发到后端）

在 1Panel 网站设置中：

```
网站 → admin.caigon.cn → 配置 → 反向代理 → 添加
  代理目录：/prod-api/
  目标URL：http://36.133.39.49:8080/
```

完整的 OpenResty 配置参考：

```nginx
# 管理后台
server {
    listen 80;
    server_name admin.caigon.cn;
    root /www/wwwroot/ghz-admin/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /prod-api/ {
        proxy_pass http://36.133.39.49:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}

# H5 用户端
server {
    listen 80;
    server_name app.caigon.cn;
    root /www/wwwroot/ghz-h5;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }
}

# API 反向代理（单独站点）
server {
    listen 80;
    server_name api.caigon.cn;

    location / {
        proxy_pass http://36.133.39.49:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_connect_timeout 60s;
        proxy_read_timeout 60s;
    }
}
```

### 6.3 申请 SSL 证书

```
1Panel → 网站 → 选择站点 → SSL → 申请证书（Let's Encrypt）
```

申请后自动配置 HTTPS，无需手动操作。

### 6.4 给 deploy 用户写入权限

```bash
# 让 deploy 用户可以写入 1Panel 的网站目录
sudo chown -R deploy:www-data /www/wwwroot/ghz-admin
sudo chown -R deploy:www-data /www/wwwroot/ghz-h5
sudo chmod -R 775 /www/wwwroot/ghz-admin
sudo chmod -R 775 /www/wwwroot/ghz-h5
```

---

## 七、GitHub Actions Workflow 文件

在项目根目录创建 `.github/workflows/` 目录，添加以下三个文件。

### 7.1 后端部署 — `.github/workflows/backend-deploy.yml`

```yaml
name: Backend Deploy

on:
  push:
    branches: [ master ]
    paths:
      - 'ruoyi-**/**'
      - 'pom.xml'
      - '.github/workflows/backend-deploy.yml'

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven

      - name: Build with Maven
        run: mvn clean package -DskipTests -pl ruoyi-admin -am

      - name: Setup SSH
        run: |
          mkdir -p ~/.ssh
          echo "${{ secrets.SSH_PRIVATE_KEY }}" > ~/.ssh/id_ed25519
          chmod 600 ~/.ssh/id_ed25519
          # SSH 端口为 3322
          ssh-keyscan -p 3322 ${{ secrets.BACKEND_HOST }} >> ~/.ssh/known_hosts

      - name: Upload JAR
        run: |
          scp -i ~/.ssh/id_ed25519 -P 3322 \
            ruoyi-admin/target/ruoyi-admin.jar \
            ${{ secrets.DEPLOY_USER }}@${{ secrets.BACKEND_HOST }}:${{ secrets.BACKEND_JAR_PATH }}/ruoyi-admin-new.jar

      - name: Deploy & Restart via Supervisor
        run: |
          ssh -i ~/.ssh/id_ed25519 -p 3322 \
            ${{ secrets.DEPLOY_USER }}@${{ secrets.BACKEND_HOST }} << 'EOF'
            set -e
            cd ${{ secrets.BACKEND_JAR_PATH }}

            # 备份旧版本
            [ -f ruoyi-admin.jar ] && cp ruoyi-admin.jar ruoyi-admin-backup.jar

            # 替换新版本
            mv ruoyi-admin-new.jar ruoyi-admin.jar

            # 通过 Supervisor 重启（1Panel 管理）
            sudo supervisorctl restart ghz-backend

            # 等待启动并检查状态
            sleep 20
            STATUS=$(sudo supervisorctl status ghz-backend | awk '{print $2}')
            if [ "$STATUS" != "RUNNING" ]; then
              echo "❌ 启动失败（状态: $STATUS），自动回滚..."
              [ -f ruoyi-admin-backup.jar ] && mv ruoyi-admin-backup.jar ruoyi-admin.jar
              sudo supervisorctl restart ghz-backend
              exit 1
            fi

            echo "✅ 后端部署成功，状态: $STATUS"
          EOF

      - name: Cleanup SSH
        if: always()
        run: rm -f ~/.ssh/id_ed25519
```

### 7.2 管理后台前端部署 — `.github/workflows/frontend-admin-deploy.yml`

```yaml
name: Frontend Admin Deploy

on:
  push:
    branches: [ master ]
    paths:
      - 'ruoyi-ui/**'
      - '.github/workflows/frontend-admin-deploy.yml'

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '16'
          cache: 'npm'
          cache-dependency-path: ruoyi-ui/package-lock.json

      - name: Install & Build
        run: |
          cd ruoyi-ui
          npm ci
          npm run build:prod

      - name: Setup SSH
        run: |
          mkdir -p ~/.ssh
          echo "${{ secrets.SSH_PRIVATE_KEY }}" > ~/.ssh/id_ed25519
          chmod 600 ~/.ssh/id_ed25519
          # SSH 端口为 3322
          ssh-keyscan -p 3322 ${{ secrets.FRONTEND_HOST }} >> ~/.ssh/known_hosts

      - name: Deploy to Frontend Server
        run: |
          # 备份旧版本
          ssh -i ~/.ssh/id_ed25519 -p 3322 \
            ${{ secrets.DEPLOY_USER }}@${{ secrets.FRONTEND_HOST }} \
            "[ -d ${{ secrets.FRONTEND_ADMIN_PATH }}/dist ] && \
             mv ${{ secrets.FRONTEND_ADMIN_PATH }}/dist ${{ secrets.FRONTEND_ADMIN_PATH }}/dist-backup || true"

          # 上传新版本
          scp -i ~/.ssh/id_ed25519 -P 3322 -r \
            ruoyi-ui/dist \
            ${{ secrets.DEPLOY_USER }}@${{ secrets.FRONTEND_HOST }}:${{ secrets.FRONTEND_ADMIN_PATH }}/

          # 重载 OpenResty（1Panel 使用 openresty，不是 nginx）
          ssh -i ~/.ssh/id_ed25519 -p 3322 \
            ${{ secrets.DEPLOY_USER }}@${{ secrets.FRONTEND_HOST }} \
            "sudo systemctl reload openresty && echo '✅ 管理后台部署成功'"

      - name: Cleanup SSH
        if: always()
        run: rm -f ~/.ssh/id_ed25519
```

### 7.3 H5 用户端部署 — `.github/workflows/h5-deploy.yml`

```yaml
name: H5 Frontend Deploy

on:
  push:
    branches: [ master ]
    paths:
      - 'uniapp-h5/**'
      - '.github/workflows/h5-deploy.yml'

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '18'

      - name: Install & Build H5
        run: |
          cd uniapp-h5
          npm install
          npx @dcloudio/uni-app-cli build:h5

      - name: Setup SSH
        run: |
          mkdir -p ~/.ssh
          echo "${{ secrets.SSH_PRIVATE_KEY }}" > ~/.ssh/id_ed25519
          chmod 600 ~/.ssh/id_ed25519
          # SSH 端口为 3322
          ssh-keyscan -p 3322 ${{ secrets.FRONTEND_HOST }} >> ~/.ssh/known_hosts

      - name: Deploy H5
        run: |
          # 备份旧版本
          ssh -i ~/.ssh/id_ed25519 -p 3322 \
            ${{ secrets.DEPLOY_USER }}@${{ secrets.FRONTEND_HOST }} \
            "[ -d ${{ secrets.FRONTEND_H5_PATH }} ] && \
             rm -rf ${{ secrets.FRONTEND_H5_PATH }}-backup && \
             cp -r ${{ secrets.FRONTEND_H5_PATH }} ${{ secrets.FRONTEND_H5_PATH }}-backup || true"

          # 上传新版本
          scp -i ~/.ssh/id_ed25519 -P 3322 -r \
            uniapp-h5/unpackage/dist/build/web/. \
            ${{ secrets.DEPLOY_USER }}@${{ secrets.FRONTEND_HOST }}:${{ secrets.FRONTEND_H5_PATH }}/

          # 重载 OpenResty
          ssh -i ~/.ssh/id_ed25519 -p 3322 \
            ${{ secrets.DEPLOY_USER }}@${{ secrets.FRONTEND_HOST }} \
            "sudo systemctl reload openresty && echo '✅ H5 部署成功'"

      - name: Cleanup SSH
        if: always()
        run: rm -f ~/.ssh/id_ed25519
```

---

## 八、触发策略说明

每个 workflow 通过 `paths` 过滤，只有对应目录有改动才触发。

| 修改内容 | 触发的 Workflow |
|---------|----------------|
| `ruoyi-**` Java 代码、`pom.xml` | `backend-deploy` |
| `ruoyi-ui/` Vue 管理后台 | `frontend-admin-deploy` |
| `uniapp-h5/` H5 用户端 | `h5-deploy` |
| 同时修改多处 | 并行触发多个 Workflow |

---

## 九、第一次部署前的手动操作清单

按顺序完成以下步骤后，后续所有发版全部由 CI/CD 自动完成。

**前端服务器：**

- [ ] 安装 1Panel
- [ ] 在 1Panel 创建两个静态站点（admin / h5）
- [ ] 配置反向代理（接口转发到后端服务器）
- [ ] 申请 SSL 证书
- [ ] 手动创建网站目录并授权给 deploy 用户
- [ ] 将 deploy 公钥加入 `~/.ssh/authorized_keys`
- [ ] 配置 sudoers（允许 reload openresty）

**后端服务器：**

- [ ] 安装 1Panel
- [ ] 通过 1Panel 应用商店安装 OpenJDK 17
- [ ] 通过 1Panel 应用商店安装 Supervisor
- [ ] 手动上传第一个 JAR 包到 `/www/wwwroot/ghz-backend/`
- [ ] 在 1Panel Supervisor 中配置并启动 `ghz-backend` 进程
- [ ] 将 deploy 公钥加入 `~/.ssh/authorized_keys`
- [ ] 配置 sudoers（允许 supervisorctl restart）

**GitHub：**

- [ ] 在仓库 Settings → Secrets 中填入所有 Secret
- [ ] 创建三个 workflow 文件并 push 到 master

---

## 十、常见问题

**Q: OpenResty reload 失败，提示权限不足？**

```bash
sudo visudo
# 确认有这一行：
deploy ALL=(ALL) NOPASSWD: /usr/bin/systemctl reload openresty
```

**Q: 如何确认 1Panel 安装的 JDK 路径？**

```bash
which java
# 或
readlink -f $(which java)
# 把结果填入 Supervisor 配置的 command 字段
```

**Q: Supervisor 状态显示 FATAL 而不是 RUNNING？**

```bash
# 查看详细错误日志
sudo supervisorctl tail ghz-backend stderr
# 或直接查看日志文件
tail -100 /www/wwwroot/ghz-backend/logs/app-error.log
```

**Q: deploy 用户上传文件后 OpenResty 无法读取（403）？**

```bash
# 确认目录权限
ls -la /www/wwwroot/ghz-admin/
# OpenResty 在 Ubuntu 上以 www-data 运行
sudo chown -R deploy:www-data /www/wwwroot/ghz-admin
sudo chmod -R 775 /www/wwwroot/ghz-admin
```

**Q: GitHub Actions 免费额度够用吗？**

免费账号每月 2000 分钟。后端构建约 2~3 分钟，前端约 1 分钟，日常开发完全够用。

**Q: 如何在 1Panel 面板里实时看后端日志？**

```
1Panel → 工具箱（或面板工具）→ Supervisor → ghz-backend → 查看日志
```

或直接 SSH 查看：

```bash
tail -f /www/wwwroot/ghz-backend/logs/app.log
```

---

*更新时间：2026-04-08*
