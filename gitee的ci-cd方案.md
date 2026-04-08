# 后端服务器从 Gitee 拉代码部署方案

> 解决 GitHub Actions runner 在境外，直接传 JAR（86MB）到国内服务器极慢的问题。
> 改为：GitHub Actions 只负责触发，实际编译和部署在服务器本地完成，代码从 Gitee（国内）拉取。

---

## 一、整体流程

```
git push → Gitee + GitHub（同时推送）
                │
        GitHub Actions 触发（只发一条 SSH 命令，秒级完成）
                │
        SSH 登录后端服务器 36.133.39.49
                │
                ▼
        服务器执行 server-deploy.sh：
          1. git pull（从 Gitee 拉最新代码，国内速度快）
          2. mvn package（服务器本地编译，约2-3分钟）
          3. 替换 JAR
          4. supervisorctl restart
          5. 健康检查，失败自动回滚
```

**对比新旧方案：**

| 项目 | 旧方案 | 新方案 |
|------|--------|--------|
| GitHub Actions 做的事 | 编译（2分钟）+ 传 JAR（1小时+） | 只发一条 SSH 命令（秒级） |
| 传输内容 | 86MB JAR（境外→国内） | 0 字节（不传文件） |
| 服务器做的事 | 重启 | 拉代码 + 编译 + 重启 |
| 总耗时 | 1小时以上 | 约 3~5 分钟 |
| 网络依赖 | GitHub→国内服务器（慢） | 服务器→Gitee（快） |

---

## 二、前置条件

- 后端服务器：`36.133.39.49`，SSH 端口 `3322`
- Gitee 仓库：`git@gitee.com:xszhensheng/ghz.git`（私有仓库）
- 服务器已安装 1Panel + Supervisor，`deploy` 用户已创建

---

## 三、后端服务器安装依赖

SSH 登录后端服务器：

```bash
ssh -p 3322 root@36.133.39.49

# 安装 Maven 和 Git
sudo apt update
sudo apt install -y maven git

# 验证安装
mvn -version
git --version
java -version   # 应显示 17.x
```

---

## 四、为 deploy 用户生成 Gitee 专用 SSH 密钥

```bash
# 切换到 deploy 用户
sudo su - deploy

# 生成密钥（一路回车，不设密码）
ssh-keygen -t ed25519 -C "ghz-server-deploy" -f ~/.ssh/gitee_deploy

# 查看公钥，下一步要填到 Gitee
cat ~/.ssh/gitee_deploy.pub
```

---

## 五、把公钥添加到 Gitee

1. 打开 **https://gitee.com/profile/sshkeys**
2. 点击「添加公钥」
3. 标题填：`ghz-server-deploy`
4. 公钥内容：粘贴上一步 `cat` 输出的整行内容（`ssh-ed25519 AAAA...`）
5. 点击确认

**验证是否配置成功：**

```bash
# 在服务器上（deploy 用户）执行
ssh -T git@gitee.com -i ~/.ssh/gitee_deploy
# 看到 "Hi xxx! You've successfully authenticated" 表示成功
```

---

## 六、配置 SSH config 让 Gitee 自动使用该密钥

```bash
# 还是在 deploy 用户下
cat >> ~/.ssh/config << 'EOF'
Host gitee.com
    HostName gitee.com
    User git
    IdentityFile ~/.ssh/gitee_deploy
    StrictHostKeyChecking no
EOF

chmod 600 ~/.ssh/config
```

---

## 七、克隆代码到服务器

```bash
# 还是在 deploy 用户下
cd /www/wwwroot

# 克隆（使用 SSH 地址）
git clone git@gitee.com:xszhensheng/ghz.git ghz-source

# 验证
ls /www/wwwroot/ghz-source/
```

---

## 八、创建服务器端部署脚本

```bash
cat > /www/wwwroot/ghz-source/server-deploy.sh << 'EOF'
#!/bin/bash
set -e

echo "===== 开始部署 $(date) ====="
cd /www/wwwroot/ghz-source

# 1. 拉取最新代码
echo ">>> 拉取代码..."
git pull origin master

# 2. 编译打包（跳过测试）
echo ">>> 开始编译（约2-3分钟）..."
mvn clean package -DskipTests -pl ruoyi-admin -am

# 3. 备份旧 JAR
echo ">>> 备份旧版本..."
[ -f /www/wwwroot/ghz-backend/ruoyi-admin.jar ] && \
    cp /www/wwwroot/ghz-backend/ruoyi-admin.jar \
       /www/wwwroot/ghz-backend/ruoyi-admin-backup.jar

# 4. 替换新 JAR
echo ">>> 替换新版本..."
cp ruoyi-admin/target/ruoyi-admin.jar /www/wwwroot/ghz-backend/ruoyi-admin.jar

# 5. 重启服务
echo ">>> 重启服务..."
sudo supervisorctl restart ghz-backend

# 6. 等待启动并健康检查
sleep 20
STATUS=$(sudo supervisorctl status ghz-backend | awk '{print $2}')
if [ "$STATUS" != "RUNNING" ]; then
    echo "❌ 启动失败（状态: $STATUS），开始回滚..."
    [ -f /www/wwwroot/ghz-backend/ruoyi-admin-backup.jar ] && \
        cp /www/wwwroot/ghz-backend/ruoyi-admin-backup.jar \
           /www/wwwroot/ghz-backend/ruoyi-admin.jar
    sudo supervisorctl restart ghz-backend
    exit 1
fi

echo "===== 部署成功 $(date) ====="
EOF

chmod +x /www/wwwroot/ghz-source/server-deploy.sh
```

---

## 九、更新 sudoers 权限

```bash
sudo visudo
```

将后端服务器的 deploy 权限更新为：

```
deploy ALL=(ALL) NOPASSWD: /usr/bin/supervisorctl restart ghz-backend, /usr/bin/supervisorctl status ghz-backend
```

---

## 十、同步推送到 Gitee + GitHub

每次 `git push` 需要同时推送到两个远程，这样 GitHub Actions 能触发，服务器也能从 Gitee 拉到最新代码。

**一次性设置（本机执行）：**

```bash
# 把 GitHub 也加到 origin 的 push 地址
git remote set-url --add origin https://github.com/willjc/ghzhub.git

# 验证（应该看到一个 fetch 地址 + 两个 push 地址）
git remote -v
```

之后每次只需执行：

```bash
git push origin master
```

就会同时推送到 Gitee 和 GitHub。

---

## 十一、更新 GitHub Actions workflow

将 `.github/workflows/backend-deploy.yml` 改为触发模式：

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
  trigger-deploy:
    runs-on: ubuntu-latest

    steps:
      - name: Setup SSH
        run: |
          mkdir -p ~/.ssh
          echo "${{ secrets.SSH_PRIVATE_KEY }}" > ~/.ssh/id_ed25519
          chmod 600 ~/.ssh/id_ed25519
          ssh-keyscan -p 3322 ${{ secrets.BACKEND_HOST }} >> ~/.ssh/known_hosts

      - name: Trigger deploy on server
        run: |
          ssh -i ~/.ssh/id_ed25519 -p 3322 \
            ${{ secrets.DEPLOY_USER }}@${{ secrets.BACKEND_HOST }} \
            "bash /www/wwwroot/ghz-source/server-deploy.sh 2>&1 | tee /www/wwwroot/ghz-backend/logs/deploy.log"

      - name: Cleanup SSH
        if: always()
        run: rm -f ~/.ssh/id_ed25519
```

---

## 十二、手动操作清单（一次性）

按顺序完成以下步骤，后续每次 `git push` 自动触发：

- [ ] 服务器安装 Maven 和 Git
- [ ] `deploy` 用户生成 `~/.ssh/gitee_deploy` 密钥对
- [ ] 公钥添加到 Gitee SSH 公钥列表
- [ ] 配置 `~/.ssh/config` 自动选用该密钥
- [ ] `git clone` 仓库到 `/www/wwwroot/ghz-source/`
- [ ] 创建 `/www/wwwroot/ghz-source/server-deploy.sh` 并赋予执行权限
- [ ] 更新 sudoers
- [ ] 本机配置 `git remote set-url --add origin`（双推）
- [ ] 更新 `.github/workflows/backend-deploy.yml`
- [ ] `git push origin master` 验证全流程

---

## 十三、常见问题

**Q: git pull 报错 `Permission denied (publickey)`？**

```bash
# 检查 SSH config 是否正确
cat ~/.ssh/config

# 手动测试 Gitee 连通性
ssh -T git@gitee.com
```

**Q: mvn 编译时报错找不到依赖？**

```bash
# 首次编译需要下载依赖，耗时较长（5~10分钟）
# 后续会走本地缓存，速度很快
# 如果一直失败，检查 Maven settings.xml 是否配置了阿里云镜像
cat ~/.m2/settings.xml
```

**Q: 如何查看部署日志？**

```bash
# 实时查看
tail -f /www/wwwroot/ghz-backend/logs/deploy.log

# 或在 1Panel → 工具箱 → 进程守护 → ghz-backend → 日志
```

**Q: 服务器 Maven 没有配置阿里云镜像，下载依赖很慢？**

```bash
mkdir -p ~/.m2
cat > ~/.m2/settings.xml << 'EOF'
<settings>
  <mirrors>
    <mirror>
      <id>aliyun</id>
      <name>阿里云镜像</name>
      <url>https://maven.aliyun.com/repository/public</url>
      <mirrorOf>central</mirrorOf>
    </mirror>
  </mirrors>
</settings>
EOF
```

---

*更新时间：2026-04-08*
