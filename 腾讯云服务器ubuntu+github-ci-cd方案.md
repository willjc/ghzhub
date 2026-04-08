# 腾讯云国内服务器 Ubuntu + GitHub CI/CD 方案

> 代码托管：GitHub | 服务器：腾讯云国内 Ubuntu（101.35.56.240）| 部署方式：Docker + GitHub Actions
> 国内服务器通过腾讯云镜像加速解决 Docker Hub 访问问题，GitHub Actions 在境外构建无障碍

---

## docker-compose.yml 是什么？

`docker-compose.yml` 是一个**服务编排配置文件**，用一个文件描述你的整个应用由哪些服务组成，以及它们如何协同工作。

```
docker-compose.yml 相当于一张"说明书"，告诉 Docker：
  - 启动哪些容器（MySQL、Redis、后端、前端）
  - 每个容器用哪个镜像
  - 容器之间如何通信
  - 数据保存在哪里（数据卷）
  - 哪些端口对外开放
```

有了它，一条命令 `docker compose up -d` 就能把所有服务全部启动，不用一个个手动操作。

---

## 整体架构

```
git push master
      ↓
GitHub Actions 触发（GitHub 境外云服务器构建，可访问 Docker Hub）
      ↓
构建前端/后端 Docker 镜像
      ↓
推送到 阿里云 ACR（国内镜像仓库）
      ↓
SSH 连接 101.35.56.240
      ↓
服务器从 阿里云 ACR 拉取新镜像（国内，速度快）
      ↓
docker compose up -d 重启服务，上线完成
```

**网络说明：**
- 服务器不需要访问 GitHub，是 GitHub 主动 SSH 连接服务器
- MySQL / Redis 镜像通过腾讯云镜像加速拉取，不走 Docker Hub
- 前后端镜像由 GitHub Actions 构建后存放在阿里云 ACR，国内拉取无障碍

---

## 第一步：服务器初始化（101.35.56.240）

SSH 登录服务器后依次执行：

### 安装 Docker

```bash
# 更新系统
apt update && apt upgrade -y

# 安装常用工具
apt install -y git curl wget vim

# 安装 Docker 依赖
apt install -y apt-transport-https ca-certificates curl gnupg lsb-release

# 添加腾讯云 Docker GPG 密钥（走国内，不会被墙）
curl -fsSL https://mirrors.cloud.tencent.com/docker-ce/linux/ubuntu/gpg | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# 添加腾讯云 Docker 软件源
echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://mirrors.cloud.tencent.com/docker-ce/linux/ubuntu $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null

# 安装 Docker
apt update
apt install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

# 启动并设置开机自启
systemctl enable docker && systemctl start docker

# 验证
docker --version
docker compose version
```

### 配置腾讯云 Docker 镜像加速（重要！）

国内服务器直接拉取 Docker Hub 镜像会被墙，必须配置镜像加速：

```bash
mkdir -p /etc/docker
cat > /etc/docker/daemon.json << 'EOF'
{
  "registry-mirrors": [
    "https://mirror.ccs.tencentyun.com"
  ]
}
EOF

# 重启 Docker 使配置生效
systemctl daemon-reload
systemctl restart docker

# 验证是否生效
docker info | grep -A 3 "Registry Mirrors"
```

> `mirror.ccs.tencentyun.com` 是腾讯云官方镜像加速，在腾讯云服务器上使用免费且走内网，速度极快。

### 配置防火墙

> 腾讯云使用**安全组**控制网络访问，无需配置 ufw。
> 在腾讯云控制台 → 安全组 → 入站规则，确认已开放 22（SSH）和 80（HTTP）端口即可。

### 创建部署目录

```bash
# /opt/ghz 是整个项目的部署根目录
mkdir -p /opt/ghz/uploadfile    # 上传文件持久化目录（头像、附件等）
                                # 说明：使用 bind mount 而非 Docker 卷，
                                # 数据直接存在宿主机，容器删了数据仍在，方便备份和迁移
```

### 配置 SSH 免密登录（GitHub Actions 用）

```bash
# 生成专用密钥对（在服务器上执行）
ssh-keygen -t ed25519 -C "github-actions" -f ~/.ssh/deploy_key -N ""

# 将公钥加入授权列表
cat ~/.ssh/deploy_key.pub >> ~/.ssh/authorized_keys
chmod 600 ~/.ssh/authorized_keys

# 查看私钥（复制全部内容，后面填到 GitHub Secrets）
cat ~/.ssh/deploy_key
```

---

## 第二步：阿里云容器镜像服务（ACR）

> 免费，国内服务器拉取速度快，GitHub Actions 推送也无障碍。

1. 登录 [阿里云控制台](https://cr.console.aliyun.com)
2. 开通**容器镜像服务个人版**（免费）
3. 创建**命名空间**，例如：`ghz-project`
4. 创建两个镜像仓库：
   - `ghz-frontend`（选择私有）
   - `ghz-backend`（选择私有）
5. 记录镜像地址，格式为：
   ```
   registry.cn-hangzhou.aliyuncs.com/ghz-project/ghz-frontend
   registry.cn-hangzhou.aliyuncs.com/ghz-project/ghz-backend
   ```
6. 在 ACR 控制台 → **访问凭证** 设置固定密码

---

## 第三步：项目文件配置

### 3.1 前端生产环境配置

修改 `ruoyi-ui/.env.production`：

```env
VUE_APP_TITLE = 港好住管理系统
ENV = 'production'
VUE_APP_BASE_API = '/prod-api'
```

### 3.2 Dockerfile.backend（项目根目录）

> 此文件在 GitHub Actions 境外服务器上执行，可正常访问 Docker Hub，无需改镜像源。

```dockerfile
# 第一阶段：Maven 构建
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# 配置阿里云 Maven 镜像（加速 jar 包下载）
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

# 先复制 pom 文件，利用 Docker 层缓存加速后续构建
COPY pom.xml .
COPY ruoyi-common/pom.xml ruoyi-common/
COPY ruoyi-framework/pom.xml ruoyi-framework/
COPY ruoyi-system/pom.xml ruoyi-system/
COPY ruoyi-admin/pom.xml ruoyi-admin/
COPY ruoyi-generator/pom.xml ruoyi-generator/
COPY ruoyi-quartz/pom.xml ruoyi-quartz/
RUN mvn dependency:go-offline -pl ruoyi-admin -am -q

# 复制源码并构建（跳过测试）
COPY . .
RUN mvn clean package -DskipTests -pl ruoyi-admin -am -q

# 第二阶段：运行镜像（最小化体积）
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 时区设置为上海
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

COPY --from=builder /app/ruoyi-admin/target/ruoyi-admin.jar app.jar

# 支持外挂配置文件（生产环境配置通过 volume 挂载）
VOLUME /app/config
VOLUME /opt/ghz/uploadfile

EXPOSE 8090

ENTRYPOINT ["java", \
  "-jar", \
  "-Xms512m", \
  "-Xmx1024m", \
  "-Dspring.config.additional-location=optional:file:/app/config/", \
  "app.jar"]
```

### 3.3 Dockerfile.frontend（项目根目录）

```dockerfile
# 第一阶段：Node 构建
FROM node:16-alpine AS builder
WORKDIR /app

COPY ruoyi-ui/package*.json ./
# 使用淘宝镜像加速 npm 下载
RUN npm ci --registry=https://registry.npmmirror.com

COPY ruoyi-ui/ .
RUN npm run build:prod

# 第二阶段：Nginx 服务
FROM nginx:alpine

# 时区设置
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

COPY --from=builder /app/dist /usr/share/nginx/html
COPY docker/nginx/default.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
```

### 3.4 docker/nginx/default.conf

```nginx
server {
    listen 80;
    server_name _;

    root /usr/share/nginx/html;
    index index.html;

    # 前端 History 路由支持
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 反向代理后端 API（/prod-api/ → 后端容器 8090 端口）
    location /prod-api/ {
        proxy_pass http://backend:8090/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_read_timeout 60s;
    }

    # 上传文件访问（头像、附件等）
    location /profile/ {
        proxy_pass http://backend:8090/profile/;
    }

    # gzip 压缩
    gzip on;
    gzip_min_length 1k;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml;
}
```

### 3.5 docker/backend/application-prod.yml

```yaml
ruoyi:
  profile: /opt/ghz/uploadfile

server:
  port: 8090

logging:
  level:
    com.ruoyi: info
    org.springframework: warn

spring:
  profiles:
    active: druid
  devtools:
    restart:
      enabled: false
  data:
    redis:
      host: redis        # Docker 内部服务名，不是 localhost
      port: 6379
      password: ${REDIS_PASSWORD:}
      database: 0
      timeout: 10s
  datasource:
    druid:
      master:
        url: jdbc:mysql://mysql:3306/newgangzhu?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8
        username: root
        password: ${MYSQL_ROOT_PASSWORD}
```

### 3.6 docker-compose.yml（项目根目录）

> MySQL 和 Redis 使用腾讯云镜像，解决国内服务器拉取 Docker Hub 被墙的问题。

```yaml
version: '3.8'

services:

  # MySQL 数据库
  mysql:
    image: ccr.ccs.tencentyun.com/library/mysql:8.0   # 腾讯云镜像，国内服务器专用
    container_name: ghz-mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: newgangzhu
      TZ: Asia/Shanghai
    volumes:
      - mysql_data:/var/lib/mysql    # 数据持久化，容器重启数据不丢失
    ports:
      - "127.0.0.1:3306:3306"        # 只绑定本机，不对公网暴露
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-p${MYSQL_ROOT_PASSWORD}"]
      interval: 10s
      timeout: 5s
      retries: 10
      start_period: 30s

  # Redis 缓存
  redis:
    image: ccr.ccs.tencentyun.com/library/redis:7      # 腾讯云镜像，国内服务器专用
    container_name: ghz-redis
    restart: always
    command: redis-server --requirepass ${REDIS_PASSWORD:-} --appendonly yes
    volumes:
      - redis_data:/data
    ports:
      - "127.0.0.1:6379:6379"        # 只绑定本机
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 3s
      retries: 5

  # Spring Boot 后端
  backend:
    image: ${REGISTRY}/ghz-backend:${VERSION:-latest}  # 从阿里云 ACR 拉取
    container_name: ghz-backend
    restart: always
    depends_on:
      mysql:
        condition: service_healthy   # 等 MySQL 健康后再启动
      redis:
        condition: service_healthy
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      REDIS_PASSWORD: ${REDIS_PASSWORD:-}
    volumes:
      - /opt/ghz/uploadfile:/opt/ghz/uploadfile                              # 上传文件持久化
      - /opt/ghz/docker/backend/application-prod.yml:/app/config/application-prod.yml  # 生产配置

  # Vue 前端（Nginx）
  frontend:
    image: ${REGISTRY}/ghz-frontend:${VERSION:-latest} # 从阿里云 ACR 拉取
    container_name: ghz-frontend
    restart: always
    depends_on:
      - backend
    ports:
      - "80:80"    # 对外暴露 80 端口

volumes:
  mysql_data:    # MySQL 数据持久化卷
  redis_data:    # Redis 数据持久化卷
```

### 3.7 服务器 .env 文件（不提交 Git！手动在服务器创建）

```bash
cat > /opt/ghz/.env << 'EOF'
REGISTRY=registry.cn-hangzhou.aliyuncs.com/ghz-project
MYSQL_ROOT_PASSWORD=替换为强密码
REDIS_PASSWORD=
VERSION=latest
EOF
```

在项目 `.gitignore` 中确认已忽略：

```gitignore
.env
*.env.local
```

---

## 第四步：GitHub Actions 工作流

### .github/workflows/deploy-frontend.yml（替换原 Cloudflare 版本）

```yaml
name: Deploy Frontend

on:
  push:
    branches: [master]
    paths:
      - 'ruoyi-ui/**'
      - 'Dockerfile.frontend'
      - 'docker/nginx/**'
      - 'docker-compose.yml'
      - '.github/workflows/deploy-frontend.yml'
  workflow_dispatch:    # 支持手动触发

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Login to Aliyun Registry
        uses: docker/login-action@v3
        with:
          registry: registry.cn-hangzhou.aliyuncs.com
          username: ${{ secrets.ALIYUN_USERNAME }}
          password: ${{ secrets.ALIYUN_PASSWORD }}

      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v3

      - name: Build & Push Frontend
        uses: docker/build-push-action@v5
        with:
          context: .
          file: Dockerfile.frontend
          push: true
          tags: |
            ${{ secrets.ALIYUN_REGISTRY }}/ghz-frontend:latest
            ${{ secrets.ALIYUN_REGISTRY }}/ghz-frontend:${{ github.sha }}
          cache-from: type=gha
          cache-to: type=gha,mode=max

      - name: Sync config files to server
        uses: appleboy/scp-action@v0.1.7
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ${{ secrets.SERVER_USER }}
          key: ${{ secrets.SERVER_SSH_KEY }}
          source: "docker-compose.yml,docker/"
          target: "/opt/ghz/"

      - name: Deploy to Server
        uses: appleboy/ssh-action@v1.0.3
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ${{ secrets.SERVER_USER }}
          key: ${{ secrets.SERVER_SSH_KEY }}
          script: |
            cd /opt/ghz
            export $(cat .env | xargs)
            docker compose pull frontend
            docker compose up -d frontend
            docker image prune -f
            echo "前端部署完成：$(date)"
```

### .github/workflows/deploy-backend.yml（新增）

```yaml
name: Deploy Backend

on:
  push:
    branches: [master]
    paths:
      - 'ruoyi-admin/**'
      - 'ruoyi-framework/**'
      - 'ruoyi-system/**'
      - 'ruoyi-common/**'
      - 'ruoyi-quartz/**'
      - 'pom.xml'
      - 'Dockerfile.backend'
      - 'docker/backend/**'
      - 'docker-compose.yml'
      - '.github/workflows/deploy-backend.yml'
  workflow_dispatch:

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Login to Aliyun Registry
        uses: docker/login-action@v3
        with:
          registry: registry.cn-hangzhou.aliyuncs.com
          username: ${{ secrets.ALIYUN_USERNAME }}
          password: ${{ secrets.ALIYUN_PASSWORD }}

      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v3

      - name: Build & Push Backend
        uses: docker/build-push-action@v5
        with:
          context: .
          file: Dockerfile.backend
          push: true
          tags: |
            ${{ secrets.ALIYUN_REGISTRY }}/ghz-backend:latest
            ${{ secrets.ALIYUN_REGISTRY }}/ghz-backend:${{ github.sha }}
          cache-from: type=gha
          cache-to: type=gha,mode=max

      - name: Sync config files to server
        uses: appleboy/scp-action@v0.1.7
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ${{ secrets.SERVER_USER }}
          key: ${{ secrets.SERVER_SSH_KEY }}
          source: "docker-compose.yml,docker/"
          target: "/opt/ghz/"

      - name: Deploy to Server
        uses: appleboy/ssh-action@v1.0.3
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ${{ secrets.SERVER_USER }}
          key: ${{ secrets.SERVER_SSH_KEY }}
          script: |
            cd /opt/ghz
            export $(cat .env | xargs)
            docker compose pull backend
            docker compose up -d backend
            docker image prune -f
            echo "后端部署完成：$(date)"
```

---

## 第五步：GitHub Secrets 配置

进入 GitHub 仓库 → **Settings** → **Secrets and variables** → **Actions** → **New repository secret**

| Secret 名称 | 值 |
|------------|-----|
| `SERVER_HOST` | `101.35.56.240` |
| `SERVER_USER` | `root` |
| `SERVER_SSH_KEY` | 服务器私钥完整内容（`cat ~/.ssh/deploy_key`）|
| `ALIYUN_USERNAME` | 阿里云账号（手机号或邮箱）|
| `ALIYUN_PASSWORD` | ACR 访问凭证密码 |
| `ALIYUN_REGISTRY` | `registry.cn-hangzhou.aliyuncs.com/ghz-project` |

---

## 第六步：首次部署

### 1. 服务器上准备配置文件

```bash
# 登录服务器
ssh root@101.35.56.240

# 登录阿里云镜像仓库（只需一次）
docker login registry.cn-hangzhou.aliyuncs.com

# 创建 .env（含数据库密码，只需手动创建这一个文件）
# docker-compose.yml 和 docker/ 配置文件由 GitHub Actions 自动同步，无需手动上传
cat > /opt/ghz/.env << 'EOF'
REGISTRY=registry.cn-hangzhou.aliyuncs.com/ghz-project
MYSQL_ROOT_PASSWORD=你的强密码
REDIS_PASSWORD=
VERSION=latest
EOF
```

### 2. 触发首次 CI/CD 构建

```bash
# 在本地项目执行，触发 GitHub Actions
git add .
git commit -m "ci: 配置 Docker 部署"
git push origin master

# GitHub Actions 会自动构建并部署
# 可在 GitHub 仓库 → Actions 页面查看进度
```

### 3. 初始化数据库

```bash
# 等后端容器启动后，导入数据库
docker exec -i ghz-mysql mysql -uroot -p你的密码 newgangzhu < /path/to/sql/ry_20250522.sql

# 验证
docker exec -it ghz-mysql mysql -uroot -p你的密码 -e "use newgangzhu; show tables;" 2>/dev/null
```

---

## 日常操作

### 查看服务状态

```bash
cd /opt/ghz
docker compose ps

# 查看日志
docker compose logs -f backend        # 后端实时日志
docker compose logs -f frontend       # 前端日志
docker compose logs --tail=100 mysql  # MySQL 最近 100 行日志
```

### 手动重启服务

```bash
cd /opt/ghz
docker compose restart backend
docker compose restart frontend
```

### 回滚版本

```bash
cd /opt/ghz
# 将 .env 中 VERSION 改为历史 commit SHA
vi .env   # 修改 VERSION=某个历史的commit sha值

docker compose pull backend
docker compose up -d backend
```

### 磁盘清理

```bash
# 清理无用镜像（每次部署后自动执行，也可手动）
docker image prune -f

# 查看磁盘占用
docker system df
```

---

## 完整文件清单

### 需要新增到 Git 仓库的文件

```
项目根目录/
├── Dockerfile.backend                          （新增）
├── Dockerfile.frontend                         （新增）
├── docker-compose.yml                          （新增）
├── docker/
│   ├── nginx/
│   │   └── default.conf                        （新增）
│   └── backend/
│       └── application-prod.yml                （新增）
├── ruoyi-ui/
│   └── .env.production                         （修改 VUE_APP_BASE_API 为 /prod-api）
└── .github/
    └── workflows/
        ├── deploy-frontend.yml                 （替换原 Cloudflare 版本）
        └── deploy-backend.yml                  （新增）
```

### 需要在服务器手动创建（不提交 Git）

```
/opt/ghz/
├── .env                                        （含数据库密码，绝不提交 Git，唯一需要手动创建的文件）
├── uploadfile/                                 （上传文件目录，mkdir 预先创建）
│
│ 以下文件由 GitHub Actions 自动同步，无需手动操作：
├── docker-compose.yml                          （CI/CD 自动同步）
└── docker/
    ├── nginx/default.conf                      （CI/CD 自动同步）
    └── backend/application-prod.yml            （CI/CD 自动同步）
```

---

## 完整部署流程

```
修改代码
  ↓
git push origin master
  ↓
GitHub Actions 检测变更文件
  ├── 改了前端相关文件 → deploy-frontend.yml 触发
  ├── 改了后端相关文件 → deploy-backend.yml 触发
  └── 都改了          → 两个 workflow 并行执行
  ↓
GitHub 境外服务器构建 Docker 镜像（可访问 Docker Hub）
  ↓
推送镜像到 阿里云 ACR
  ↓
SSH 连接 101.35.56.240
  ↓
服务器从 ACR 拉取新镜像（国内，速度快）
  ↓
docker compose up -d 重启对应服务
  ↓
GitHub Actions 显示绿色 ✓，部署完成
```
