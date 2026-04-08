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
| `FRONTEND_ADMIN_PATH` | `/opt/1panel/www/sites/admin.caigon.cn/index` |
| `FRONTEND_H5_PATH` | `/opt/1panel/www/sites/app.caigon.cn/index` |
| `BACKEND_JAR_PATH` | `/www/wwwroot/ghz-backend` |

---

## 五、后端服务器配置（1Panel + Supervisor）

### 5.1 SSH 登录后端服务器，安装 JDK 17 和 Supervisor

> 1Panel 2.x 的应用商店里没有独立的 JDK 和 Supervisor 应用。需要直接在服务器上用 apt 安装，然后在 1Panel 工具箱里接管。

```bash
# 登录后端服务器
ssh -p 3322 root@36.133.39.49

# 更新软件源
sudo apt update

# 安装 JDK 17
sudo apt install -y openjdk-17-jdk

# 确认安装成功及路径（记住这个路径，后面要用）
java -version
which java          # 通常是 /usr/bin/java
readlink -f $(which java)  # 实际路径，如 /usr/lib/jvm/java-17-openjdk-amd64/bin/java

# 安装 Supervisor
sudo apt install -y supervisor

# 启动并设置开机自启
sudo systemctl start supervisor
sudo systemctl enable supervisor
```

### 5.2 在 1Panel 初始化进程守护

Supervisor 安装好后，在 1Panel 面板里接管它：

```
1Panel → 工具箱 → 进程守护 → 点击「初始化」按钮
```

初始化完成后，进程守护功能就可以在面板里使用了。

### 5.3 创建应用目录

```bash
sudo mkdir -p /www/wwwroot/ghz-backend/logs
sudo chown -R deploy:deploy /www/wwwroot/ghz-backend
```

### 5.4 在 1Panel 创建守护进程

```
1Panel → 工具箱 → 进程守护 → 创建守护进程
```

填入以下信息：

| 字段 | 值 |
|------|---|
| 名称 | `ghz-backend` |
| 运行目录 | `/www/wwwroot/ghz-backend` |
| 启动命令 | `/usr/bin/java -Xms256m -Xmx512m -jar /www/wwwroot/ghz-backend/ruoyi-admin.jar --spring.profiles.active=prod` |
| 用户 | `deploy` |
| 自动启动 | 是 |
| 异常重启 | 是 |

> **启动命令说明**：必须使用 java 的绝对路径（`/usr/bin/java`），不能只写 `java`，否则 Supervisor 可能找不到。路径以上一步 `readlink -f $(which java)` 的输出为准。

保存后点击**启动**，在列表里可以看到进程状态、查看日志、重启等操作。

---

## 六、前端服务器配置（1Panel + OpenResty）

### 6.1 在 1Panel 创建网站

```
1Panel → 网站 → 创建网站
  类型：静态网站
  域名：admin.caigon.cn
  根目录：/opt/1panel/www/sites/admin.caigon.cn/index
```

```
1Panel → 网站 → 创建网站
  类型：静态网站
  域名：app.caigon.cn
  根目录：/opt/1panel/www/sites/app.caigon.cn/index
```

还需要为 API 单独创建一个反向代理站点：

```
1Panel → 网站 → 创建网站
  类型：反向代理
  域名：api.caigon.cn
  代理地址：http://36.133.39.49:8090
```

### 6.2 配置反向代理（接口转发到后端）

`admin.caigon.cn` 和 `app.caigon.cn` 只托管静态文件，**不需要配置反向代理**。
API 请求由浏览器直接发给 `api.caigon.cn`，再由该站点转发到后端。

`api.caigon.cn` 站点已在 6.1 中以「反向代理」类型创建，1Panel 会自动生成转发配置。

完整的 OpenResty 配置参考：

```nginx
# 管理后台（只托管静态文件）
server {
    listen 80;
    server_name admin.caigon.cn;
    root /opt/1panel/www/sites/admin.caigon.cn/index/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /prod-api/ {
        proxy_pass http://36.133.39.49:8090/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}

# H5 用户端（只托管静态文件）
server {
    listen 80;
    server_name app.caigon.cn;
    root /opt/1panel/www/sites/app.caigon.cn/index;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }
}

# API 反向代理（所有 API 请求由此转发到后端）
server {
    listen 80;
    server_name api.caigon.cn;

    location / {
        proxy_pass http://36.133.39.49:8090/;
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
sudo chown -R deploy:www-data /opt/1panel/www/sites/admin.caigon.cn/index
sudo chown -R deploy:www-data /opt/1panel/www/sites/app.caigon.cn/index
sudo chmod -R 775 /opt/1panel/www/sites/admin.caigon.cn/index
sudo chmod -R 775 /opt/1panel/www/sites/app.caigon.cn/index
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

### 7.3 H5 用户端部署 — 本地脚本（非 CI/CD）

> H5 使用 HBuilderX 开发，HBuilderX 是 GUI 工具，无法在 GitHub Actions 中运行，因此 H5 不走 CI/CD，改为本地构建 + 一键脚本部署。

**部署流程：**

```
HBuilderX → 发行 → 网站 PC Web（H5）
                    │
                    ▼
      uniapp-h5/unpackage/dist/build/web/（本地产物）
                    │
                    ▼
      bash uniapp-h5/deploy-h5.sh（上传到服务器）
```

脚本已创建在 `uniapp-h5/deploy-h5.sh`，每次发版执行一次即可：

```bash
# 在项目根目录执行
bash uniapp-h5/deploy-h5.sh
```

> **前提**：本机 `~/.ssh/github_deploy` 私钥已生成，且对应公钥已放到前端服务器 deploy 用户的 `~/.ssh/authorized_keys` 中。

---

## 八、触发策略说明

| 修改内容 | 部署方式 |
|---------|---------|
| `ruoyi-**` Java 代码、`pom.xml` | GitHub Actions 自动触发 `backend-deploy` |
| `ruoyi-ui/` Vue 管理后台 | GitHub Actions 自动触发 `frontend-admin-deploy` |
| `uniapp-h5/` H5 用户端 | HBuilderX 本地构建，手动执行 `deploy-h5.sh` |

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

**后端服务器（36.133.39.49）：**

- [ ] 安装 1Panel
- [ ] SSH 执行：`sudo apt install -y openjdk-17-jdk supervisor`
- [ ] 1Panel → 工具箱 → 进程守护 → 初始化
- [ ] 创建目录：`mkdir -p /www/wwwroot/ghz-backend/logs`
- [ ] 手动上传第一个 JAR 包到 `/www/wwwroot/ghz-backend/`
- [ ] 在 1Panel 进程守护中创建并启动 `ghz-backend` 进程
- [ ] 将 deploy 公钥加入 `~/.ssh/authorized_keys`
- [ ] 配置 sudoers（允许 supervisorctl restart）

**GitHub：**

- [ ] 在仓库 Settings → Secrets 中填入所有 Secret
- [ ] 创建两个 workflow 文件并 push 到 master（`backend-deploy.yml`、`frontend-admin-deploy.yml`）

**本机（H5 部署）：**

- [ ] 生成 SSH 密钥：`ssh-keygen -t ed25519 -f ~/.ssh/github_deploy`
- [ ] 将公钥放到前端服务器 deploy 用户的 `~/.ssh/authorized_keys`
- [ ] HBuilderX 发布 H5 后执行：`bash uniapp-h5/deploy-h5.sh`

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
ls -la /opt/1panel/www/sites/admin.caigon.cn/index/
# OpenResty 在 Ubuntu 上以 www-data 运行
sudo chown -R deploy:www-data /opt/1panel/www/sites/admin.caigon.cn/index
sudo chmod -R 775 /opt/1panel/www/sites/admin.caigon.cn/index
```

**Q: GitHub Actions 免费额度够用吗？**

免费账号每月 2000 分钟。后端构建约 2~3 分钟，前端约 1 分钟，日常开发完全够用。

**Q: 如何在 1Panel 面板里实时看后端日志？**

```
1Panel → 工具箱 → 进程守护 → ghz-backend → 日志
```

或直接 SSH 查看：

```bash
tail -f /www/wwwroot/ghz-backend/logs/app.log
```

---

*更新时间：2026-04-08*
