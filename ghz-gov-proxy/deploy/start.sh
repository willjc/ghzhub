#!/bin/bash
# ===========================================================
# ghz-gov-proxy 启动脚本
# 部署位置：172.19.25.77:/opt/ghz-gov-proxy/start.sh
# ===========================================================

APP_HOME=/opt/ghz-gov-proxy
APP_JAR=ghz-gov-proxy-1.0.0.jar
CONFIG=application-prod.yml

cd "$APP_HOME" || { echo "目录不存在: $APP_HOME"; exit 1; }

# 检查是否已运行
if [ -f "run.pid" ] && kill -0 "$(cat run.pid)" 2>/dev/null; then
  echo "服务已在运行，pid=$(cat run.pid)，请先 sh stop.sh"
  exit 1
fi

mkdir -p logs

# JVM 参数（512M 内存够用，接口服务不需要很大堆）
JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC \
  -Dfile.encoding=UTF-8 \
  -Duser.timezone=Asia/Shanghai"

nohup java $JAVA_OPTS -jar "$APP_JAR" \
  --spring.config.location=./$CONFIG \
  > run.log 2>&1 &

echo $! > run.pid

echo "========================================="
echo "启动命令已发出, pid=$(cat run.pid)"
echo "日志：tail -f $APP_HOME/run.log"
echo "5秒后检查健康状态..."
echo "========================================="

sleep 5
if curl -s -f http://127.0.0.1:9001/api/v1/gov/ping > /dev/null 2>&1; then
  echo "✅ 服务启动成功"
  curl -s http://127.0.0.1:9001/api/v1/gov/ping
  echo ""
else
  echo "⚠️ 服务暂未就绪，请再等 10-20 秒后手动 curl http://127.0.0.1:9001/api/v1/gov/ping"
  echo "或查看日志：tail -100 $APP_HOME/run.log"
fi
