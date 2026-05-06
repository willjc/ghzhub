# ghz-gov-proxy 部署指南（正式环境）

## 一、架构图
```
港好住后端（公网业务系统）
    ↓ HTTP POST
http://42.228.16.25:8088/api/v1/gov/***        ← 港好住调用地址
    ↓ 运营商 NAT 映射
172.27.139.244:8088  (互联网区 Nginx)
    ↓ 反向代理
172.19.25.77:9001    (政务外网 jar)
    ↓ 政务外网
59.207.50.51:4989    (政务数据接口)
```

## 二、正式 API Key
```
lWo1XjpwMfxpPqq52bCztCP-olV-dRaL
```
⚠️ 港好住后端调用时必须在 HTTP 请求头带上：
```
X-Api-Key: lWo1XjpwMfxpPqq52bCztCP-olV-dRaL
```

## 三、部署文件清单
| 文件 | 目标位置 |
|---|---|
| `target/ghz-gov-proxy-1.0.0.jar` | `172.19.25.77:/opt/ghz-gov-proxy/` |
| `deploy/application-prod.yml` | `172.19.25.77:/opt/ghz-gov-proxy/` |
| `deploy/start.sh` | `172.19.25.77:/opt/ghz-gov-proxy/` |
| `deploy/stop.sh` | `172.19.25.77:/opt/ghz-gov-proxy/` |
| `deploy/nginx-ghz-gov-proxy.conf` | `172.27.139.244:/etc/nginx/conf.d/ghz-gov-proxy.conf` |

## 四、部署步骤

### 🅰️ 172.19.25.77（政务外网主机）

**1. 安装 JRE 8**
```bash
# CentOS/RHEL
yum install -y java-1.8.0-openjdk
# 或 Ubuntu
apt install -y openjdk-8-jre-headless

java -version  # 验证
```

**2. 准备目录，上传 3 个文件**
```bash
mkdir -p /opt/ghz-gov-proxy/logs
cd /opt/ghz-gov-proxy
# 用 scp 或运维平台上传以下 3 个文件：
#   - ghz-gov-proxy-1.0.0.jar
#   - application-prod.yml
#   - start.sh, stop.sh
chmod +x start.sh stop.sh
```

**3. 验证能通政务外网**
```bash
curl -v telnet://59.207.50.51:4989
# 能连上说明政务外网专线 OK
```

**4. 启动**
```bash
sh start.sh
# 自动等 5 秒并检查健康
```

**5. 本机测试（L1）**
```bash
curl http://127.0.0.1:9001/api/v1/gov/ping
# 期望: {"code":200,"message":"success","data":{"status":"UP",...}}
```

---

### 🅱️ 172.27.139.244（互联网区 Nginx）

**1. 安装 Nginx**
```bash
yum install -y nginx    # 或 apt install -y nginx
```

**2. 上传 Nginx 配置**
```bash
# 把 nginx-ghz-gov-proxy.conf 上传为 /etc/nginx/conf.d/ghz-gov-proxy.conf
nginx -t   # 检查配置语法
systemctl enable --now nginx
systemctl reload nginx
```

**3. 开放防火墙**
```bash
firewall-cmd --permanent --add-port=8088/tcp
firewall-cmd --reload
# 如果用的是 ufw：
# ufw allow 8088/tcp
```

**4. 专线测试（L2）**
```bash
curl http://172.19.25.77:9001/api/v1/gov/ping
# 期望返回 JSON 且含 "status":"UP"  说明两机网络通
```

---

## 五、三层联调测试

### L1 本机层（172.19.25.77 上执行）
```bash
curl http://127.0.0.1:9001/api/v1/gov/ping
```

### L2 专线层（172.27.139.244 上执行）
```bash
curl http://172.19.25.77:9001/api/v1/gov/ping
```

### L3 公网层（任意联网机器，如你的开发机）
```bash
# 健康检查
curl http://42.228.16.25:8088/api/v1/gov/ping

# 业务测试 - 社保（用栗毅）
curl -X POST http://42.228.16.25:8088/api/v1/gov/social/query \
  -H "Content-Type: application/json" \
  -H "X-Api-Key: lWo1XjpwMfxpPqq52bCztCP-olV-dRaL" \
  -d '{"idCard":"410581198610109037","name":"栗毅"}'

# 业务测试 - 婚姻
curl -X POST http://42.228.16.25:8088/api/v1/gov/marriage/query \
  -H "Content-Type: application/json" \
  -H "X-Api-Key: lWo1XjpwMfxpPqq52bCztCP-olV-dRaL" \
  -d '{"idCard":"410581198610109037","name":"栗毅"}'

# 业务测试 - 不动产
curl -X POST http://42.228.16.25:8088/api/v1/gov/estate/query \
  -H "Content-Type: application/json" \
  -H "X-Api-Key: lWo1XjpwMfxpPqq52bCztCP-olV-dRaL" \
  -d '{"idCard":"410581198610109037","name":"栗毅"}'

# 业务测试 - 公租房
curl -X POST http://42.228.16.25:8088/api/v1/gov/housing/query \
  -H "Content-Type: application/json" \
  -H "X-Api-Key: lWo1XjpwMfxpPqq52bCztCP-olV-dRaL" \
  -d '{"idCard":"410581198610109037","name":"栗毅"}'
```

---

## 六、故障排查

| 现象 | 可能原因 | 排查位置 |
|---|---|---|
| L1 失败（本机健康检查不通） | jar 没起来 | 172.19.25.77 上 `tail -f run.log` |
| L1 通 / L2 失败 | 两台服务器间未互通 | 172.27.139.244 上 `telnet 172.19.25.77 9001` |
| L2 通 / L3 失败 | 公网 NAT 未生效 或 防火墙 | 运营商/Linux iptables/firewalld |
| L3 返回 401 | API Key 错 | 检查请求头是否带对 key |
| L3 返回 504 | 政务接口慢 | Nginx 已放到 60s，如仍超时则政务侧问题 |

---

## 七、港好住后端需要的参数

| 配置项 | 值 |
|---|---|
| 接口基地址 | `http://42.228.16.25:8088` |
| 请求头 Key | `X-Api-Key` |
| 请求头 Value | `lWo1XjpwMfxpPqq52bCztCP-olV-dRaL` |
| 请求超时 | 建议 30s 以上 |
| Content-Type | `application/json` |
| 业务路径 | `/api/v1/gov/{marriage,estate,housing,social}/query` |
| 请求体 | `{"idCard":"xxx","name":"xxx"}` |
