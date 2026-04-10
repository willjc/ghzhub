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

# 5. 重启服务（使用组名兼容 ghz-backend:ghz-backend_00 格式）
echo ">>> 重启服务..."
sudo supervisorctl restart ghz-backend

# 6. 等待启动并健康检查
sleep 20
STATUS=$(sudo supervisorctl status ghz-backend | grep ghz-backend | awk '{print $2}')
if [ "$STATUS" != "RUNNING" ]; then
    echo "启动失败（状态: $STATUS），开始回滚..."
    [ -f /www/wwwroot/ghz-backend/ruoyi-admin-backup.jar ] && \
        cp /www/wwwroot/ghz-backend/ruoyi-admin-backup.jar \
           /www/wwwroot/ghz-backend/ruoyi-admin.jar
    sudo supervisorctl restart ghz-backend
    exit 1
fi

echo "===== 部署成功 $(date) ====="
