# Gitee CI/CD 方案

> 适用场景：代码托管在 Gitee，部署目标为国内 CentOS 服务器 + Docker
> 核心思路：**Gitee Webhook → 服务器本地构建 → Docker Compose 重启**
> 优点：完全国内闭环，无需镜像仓库，免费，不依赖任何外部 CI 服务

---

## 整体架构

```
开发者 git push → Gitee 仓库
                      ↓ Webhook 通知
              服务器 webhook 监听器 (9000端口)
                      ↓ 验证签名
              执行部署脚本 deploy.sh
                      ↓
              git pull 最新代码
                      ↓
              docker compose build（本地构建镜像）
                      ↓
              docker compose up -d（滚动重启）
```

---

## 第一步：服务器环境准备

### 安装 Docker

```bash
# CentOS 安装 Docker
yum install -y yum-utils
yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
yum install -y docker-ce docker-compose-plugin
systemctl enable docker && systemctl start docker

# 验证
docker --version
docker compose version
```

### 安装 webhook 工具

`webhook` 是一个轻量的 HTTP webhook 监听器，用于接收 Gitee 推送通知。

```bash
# 下载 webhook（选择对应架构，通常是 amd64）
wget https://github.com/adnanh/webhook/releases/download/2.8.1/webhook-linux-amd64.tar.gz
tar xf webhook-linux-amd64.tar.gz
mv webhook-linux-amd64/webhook /usr/local/bin/webhook
chmod +x /usr/local/bin/webhook

# 验证
webhook --version
```

> 如果 GitHub 下载慢，可以用国内镜像：
> ```bash
> wget https://ghproxy.com/https://github.com/adnanh/webhook/releases/download/2.8.1/webhook-linux-amd64.tar.gz
> ```

---

## 第二步：创建项目目录结构

```bash
# 项目部署根目录
mkdir -p /opt/ghz
cd /opt/ghz

# 克隆 Gitee 仓库（首次）
git clone https://gitee.com/你的用户名/你的仓库名.git code

# 日志目录
mkdir -p /opt/ghz/logs

# 上传文件目录（对应 ruoyi.profile）
mkdir -p /opt/ghz/uploadfile

# webhook 配置目录
mkdir -p /opt/ghz/webhook
```

---

## 第三步：创建部署脚本

### `/opt/ghz/deploy.sh`（核心部署脚本）

```bash
#!/bin/bash
set -e

PROJECT_DIR="/opt/ghz/code"
LOG_FILE="/opt/ghz/logs/deploy.log"
LOCK_FILE="/tmp/ghz-deploy.lock"

# 防止并发部署
if [ -f "$LOCK_FILE" ]; then
    echo "[$(date)] 已有部署任务在运行，跳过本次触发" >> "$LOG_FILE"
    exit 0
fi
touch "$LOCK_FILE"
trap "rm -f $LOCK_FILE" EXIT

echo "========================================" >> "$LOG_FILE"
echo "[$(date)] 开始部署" >> "$LOG_FILE"

cd "$PROJECT_DIR"

# 拉取最新代码
echo "[$(date)] 拉取最新代码..." >> "$LOG_FILE"
git fetch origin master >> "$LOG_FILE" 2>&1
git reset --hard origin/master >> "$LOG_FILE" 2>&1

# 判断哪些文件有变化，决定重建哪个服务
CHANGED_FILES=$(git diff HEAD@{1} HEAD --name-only 2>/dev/null || echo "all")
echo "[$(date)] 变更文件: $CHANGED_FILES" >> "$LOG_FILE"

REBUILD_FRONTEND=false
REBUILD_BACKEND=false

if echo "$CHANGED_FILES" | grep -q "^ruoyi-ui/\|^docker/nginx/\|^Dockerfile.frontend"; then
    REBUILD_FRONTEND=true
fi

if echo "$CHANGED_FILES" | grep -qE "^ruoyi-(admin|framework|system|common|quartz)/|^pom.xml|^Dockerfile.backend"; then
    REBUILD_BACKEND=true
fi

# 如果无法判断（首次部署），全量构建
if [ "$CHANGED_FILES" = "all" ]; then
    REBUILD_FRONTEND=true
    REBUILD_BACKEND=true
fi

# 构建前端
if [ "$REBUILD_FRONTEND" = true ]; then
    echo "[$(date)] 构建前端镜像..." >> "$LOG_FILE"
    docker compose build frontend >> "$LOG_FILE" 2>&1
    docker compose up -d frontend >> "$LOG_FILE" 2>&1
    echo "[$(date)] 前端部署完成" >> "$LOG_FILE"
fi

# 构建后端
if [ "$REBUILD_BACKEND" = true ]; then
    echo "[$(date)] 构建后端镜像..." >> "$LOG_FILE"
    docker compose build backend >> "$LOG_FILE" 2>&1
    docker compose up -d backend >> "$LOG_FILE" 2>&1
    echo "[$(date)] 后端部署完成" >> "$LOG_FILE"
fi

# 清理无用镜像
docker image prune -f >> "$LOG_FILE" 2>&1

echo "[$(date)] 部署完成 ✓" >> "$LOG_FILE"
```

```bash
chmod +x /opt/ghz/deploy.sh
```

---

## 第四步：配置 webhook 监听器

### `/opt/ghz/webhook/hooks.json`

```json
[
  {
    "id": "deploy",
    "execute-command": "/opt/ghz/deploy.sh",
    "command-working-directory": "/opt/ghz/code",
    "response-message": "Deployment triggered",
    "trigger-rule": {
      "match": {
        "type": "payload-hmac-sha256",
        "secret": "你的webhook密钥",
        "parameter": {
          "source": "header",
          "name": "X-Gitee-Token"
        }
      }
    }
  }
]
```

> `你的webhook密钥` 自己设一个随机字符串，后面在 Gitee 仓库设置里填同样的值。

### 创建 webhook systemd 服务

`/etc/systemd/system/webhook.service`：

```ini
[Unit]
Description=Gitee Webhook Listener
After=network.target

[Service]
Type=simple
User=root
ExecStart=/usr/local/bin/webhook \
    -hooks /opt/ghz/webhook/hooks.json \
    -port 9000 \
    -verbose \
    -logfile /opt/ghz/logs/webhook.log
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
```

```bash
systemctl daemon-reload
systemctl enable webhook
systemctl start webhook

# 验证是否启动
systemctl status webhook
```

### 开放 9000 端口

```bash
firewall-cmd --permanent --add-port=9000/tcp
firewall-cmd --permanent --add-port=80/tcp
firewall-cmd --reload
```

---

## 第五步：Docker 相关文件

### `docker-compose.yml`（放在 `/opt/ghz/code/`）

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: ghz-mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: newgangzhu
      TZ: Asia/Shanghai
    volumes:
      - mysql_data:/var/lib/mysql
      - ./sql/ry_20250522.sql:/docker-entrypoint-initdb.d/01-schema.sql
    ports:
      - "3306:3306"
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-p${MYSQL_ROOT_PASSWORD}"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    container_name: ghz-redis
    restart: always
    command: redis-server --requirepass ${REDIS_PASSWORD:-}
    volumes:
      - redis_data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 3s
      retries: 3

  backend:
    build:
      context: .
      dockerfile: Dockerfile.backend
    image: ghz-backend:local
    container_name: ghz-backend
    restart: always
    depends_on:
      mysql:
        condition: service_healthy
      redis:
        condition: service_healthy
    environment:
      SPRING_PROFILES_ACTIVE: prod
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      REDIS_PASSWORD: ${REDIS_PASSWORD:-}
    volumes:
      - /opt/ghz/uploadfile:/opt/ghz/uploadfile
      - ./docker/backend/application-prod.yml:/app/config/application-prod.yml

  frontend:
    build:
      context: .
      dockerfile: Dockerfile.frontend
    image: ghz-frontend:local
    container_name: ghz-frontend
    restart: always
    depends_on:
      - backend
    ports:
      - "80:80"

volumes:
  mysql_data:
  redis_data:
```

### `/opt/ghz/code/.env`（不提交到 Git，手动在服务器创建）

```env
MYSQL_ROOT_PASSWORD=你的强密码
REDIS_PASSWORD=
```

> 在 `.gitignore` 中确保 `.env` 已忽略。

---

## 第六步：Gitee 仓库配置

1. 进入 Gitee 仓库页面
2. 点击 **管理** → **WebHooks** → **添加 WebHook**
3. 填写：
   - **URL**：`http://你的服务器IP:9000/hooks/deploy`
   - **密码（secret）**：与 `hooks.json` 里的密钥一致
   - **事件**：勾选 **Push**
4. 点击 **添加**，然后点击 **测试** 验证连通性

---

## 第七步：首次手动部署

```bash
cd /opt/ghz/code

# 创建 .env 文件
cat > .env << 'EOF'
MYSQL_ROOT_PASSWORD=你的强密码
REDIS_PASSWORD=
EOF

# 首次构建并启动所有服务（会比较慢，后续增量构建）
docker compose up -d --build

# 查看启动状态
docker compose ps

# 查看后端日志
docker compose logs -f backend
```

---

## 日常使用

### 部署流程

```bash
# 本地开发完成后
git add .
git commit -m "feat: xxx功能"
git push gitee master    # 推送到 Gitee

# → 自动触发服务器部署，无需手动操作
```

### 查看部署日志

```bash
# 查看部署历史日志
tail -f /opt/ghz/logs/deploy.log

# 查看 webhook 接收日志
tail -f /opt/ghz/logs/webhook.log

# 查看容器运行状态
docker compose -f /opt/ghz/code/docker-compose.yml ps

# 查看后端实时日志
docker compose -f /opt/ghz/code/docker-compose.yml logs -f backend
```

### 手动触发部署

```bash
# 在服务器上手动执行部署脚本
/opt/ghz/deploy.sh
```

### 回滚

```bash
cd /opt/ghz/code

# 查看提交历史
git log --oneline -10

# 回滚到指定版本
git reset --hard <commit-hash>
docker compose up -d --build backend  # 或 frontend
```

---

## 与 GitHub Actions 方案对比

| 对比项 | GitHub Actions 方案 | Gitee Webhook 方案 |
|--------|--------------------|--------------------|
| 代码托管 | GitHub（国外，可能访问慢）| Gitee（国内，稳定）|
| CI 执行环境 | GitHub 云服务器 | 自己的 CentOS 服务器 |
| 镜像仓库 | 阿里云 ACR | **不需要**（本地构建）|
| 构建速度 | 快（云服务器资源好）| 取决于服务器配置 |
| 费用 | 免费（公开仓库）| 完全免费 |
| 网络依赖 | 需要访��� GitHub | 仅需 Gitee 能访问服务器 |
| 维护复杂度 | 低 | 低（脚本简单）|

---

## 常见问题

### webhook 测试返回 200 但脚本没执行

检查 webhook 日志：
```bash
cat /opt/ghz/logs/webhook.log
```
通常是密钥不匹配，确认 Gitee 填写的 secret 与 `hooks.json` 里的一致。

### 构建时 Maven 依赖下载慢

在 `Dockerfile.backend` 的 Maven 构建阶段添加阿里云镜像：
```dockerfile
RUN mkdir -p /root/.m2 && cat > /root/.m2/settings.xml << 'EOF'
<settings>
  <mirrors>
    <mirror>
      <id>aliyun</id>
      <mirrorOf>central</mirrorOf>
      <url>https://maven.aliyun.com/repository/central</url>
    </mirror>
  </mirrors>
</settings>
EOF
```

### 前端 npm install 慢

在 `Dockerfile.frontend` 中添加淘宝镜像：
```dockerfile
RUN npm ci --registry=https://registry.npmmirror.com
```

### 服务器磁盘被 Docker 镜像占满

```bash
# 清理所有未使用的镜像/容器/网络
docker system prune -af

# 只清理悬空镜像（推荐，定期执行）
docker image prune -f
```

---

## 文件清单

需要在项目中新增/修改的文件：

| 文件 | 说明 |
|------|------|
| `Dockerfile.backend` | 后端镜像构建文件 |
| `Dockerfile.frontend` | 前端镜像构建文件 |
| `docker-compose.yml` | 服务编排文件 |
| `docker/nginx/default.conf` | Nginx 反向代理配置 |
| `docker/backend/application-prod.yml` | 后端生产配置（不含密码）|
| `ruoyi-ui/.env.production` | 前端生产 API 配置（改为 `/prod-api`）|

需要在服务器上创建的文件（不提交 Git）：

| 文件 | 说明 |
|------|------|
| `/opt/ghz/deploy.sh` | 部署脚��� |
| `/opt/ghz/webhook/hooks.json` | webhook 规则配置 |
| `/opt/ghz/code/.env` | 环境变量（含数据库密码）|
| `/etc/systemd/system/webhook.service` | webhook 开机自启服务 |
