#!/bin/bash
# ===========================================================
# ghz-gov-proxy 停止脚本
# 部署位置：172.19.25.77:/opt/ghz-gov-proxy/stop.sh
# ===========================================================

APP_HOME=/opt/ghz-gov-proxy
cd "$APP_HOME" || exit 1

if [ ! -f "run.pid" ]; then
  echo "未找到 run.pid，尝试通过端口查找进程..."
  PID=$(lsof -t -i:9001 2>/dev/null)
else
  PID=$(cat run.pid)
fi

if [ -z "$PID" ]; then
  echo "没有运行中的服务"
  exit 0
fi

echo "正在停止 pid=$PID ..."
kill "$PID" 2>/dev/null

# 优雅等待 10 秒
for i in {1..10}; do
  if kill -0 "$PID" 2>/dev/null; then
    sleep 1
  else
    break
  fi
done

# 还在就强制 kill
if kill -0 "$PID" 2>/dev/null; then
  echo "优雅停止超时，强制 kill -9"
  kill -9 "$PID" 2>/dev/null
fi

rm -f run.pid
echo "✅ 已停止"
