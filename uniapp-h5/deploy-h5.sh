#!/bin/bash
# H5 用户端一键部署脚本
# 使用方式：
#   1. 用 HBuilderX 构建 H5（发行 → 网站 PC Web），输出到 unpackage/dist/build/web/
#   2. 在项目根目录执行：bash uniapp-h5/deploy-h5.sh

set -e

# ============ 配置（与 GitHub Secrets 保持一致）============
FRONTEND_HOST="36.133.39.148"
DEPLOY_USER="deploy"
SSH_PORT="3322"
REMOTE_PATH="/opt/1panel/www/sites/app.caigon.cn/index"
SSH_KEY="$HOME/.ssh/github_deploy"
# ==========================================================

DIST_DIR="$(dirname "$0")/unpackage/dist/build/web"

# 检查构建产物是否存在
if [ ! -d "$DIST_DIR" ]; then
  echo "错误：找不到构建目录 $DIST_DIR"
  echo "请先在 HBuilderX 中执行：发行 → 网站 PC Web（H5）"
  exit 1
fi

echo "开始部署 H5 到 $FRONTEND_HOST ..."

# 备份旧版本
ssh -i "$SSH_KEY" -p "$SSH_PORT" "$DEPLOY_USER@$FRONTEND_HOST" \
  "[ -d $REMOTE_PATH ] && rm -rf ${REMOTE_PATH}-backup && cp -r $REMOTE_PATH ${REMOTE_PATH}-backup || true"

# 上传新版本
scp -i "$SSH_KEY" -P "$SSH_PORT" -r \
  "$DIST_DIR/." \
  "$DEPLOY_USER@$FRONTEND_HOST:$REMOTE_PATH/"

# 重载 OpenResty
ssh -i "$SSH_KEY" -p "$SSH_PORT" "$DEPLOY_USER@$FRONTEND_HOST" \
  "sudo systemctl reload openresty"

echo "H5 部署成功！访问 https://app.caigon.cn 验证"
