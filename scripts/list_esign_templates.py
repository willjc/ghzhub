#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""列出 e签宝正式环境当前应用下的全部模板"""
import base64, hmac, hashlib, json, time
import urllib.request, urllib.error, urllib.parse

APP_ID = "5112061749"
APP_SECRET = "0d76219647c86a41e13c2f78c31f0458"
HOST = "https://openapi.esign.cn"

METHOD = "GET"
# 按 e签宝 v3 规范：查询参数需按 key 字典序排序后参与签名
PATH_WITH_QUERY = "/v3/doc-templates?pageNum=1&pageSize=20"
ACCEPT = "*/*"
CONTENT_TYPE = "application/json; charset=UTF-8"
CONTENT_MD5 = ""

sign_data = f"{METHOD}\n{ACCEPT}\n{CONTENT_MD5}\n{CONTENT_TYPE}\n\n{PATH_WITH_QUERY}"
signature = base64.b64encode(
    hmac.new(APP_SECRET.encode("utf-8"), sign_data.encode("utf-8"), hashlib.sha256).digest()
).decode("utf-8")

headers = {
    "X-Tsign-Open-App-Id": APP_ID,
    "X-Tsign-Open-Ca-Timestamp": str(int(time.time() * 1000)),
    "X-Tsign-Open-Auth-Mode": "Signature",
    "X-Tsign-Open-Ca-Signature": signature,
    "Accept": ACCEPT,
    "Content-Type": CONTENT_TYPE,
    "Content-MD5": CONTENT_MD5,
}

url = HOST + PATH_WITH_QUERY
print(f"请求: GET {url}\n")

req = urllib.request.Request(url, headers=headers, method="GET")
try:
    with urllib.request.urlopen(req, timeout=15) as resp:
        body = resp.read().decode("utf-8")
        data = json.loads(body)
        print(f"HTTP {resp.status}  业务 code: {data.get('code')}  msg: {data.get('message')}")
        templates = (data.get("data") or {}).get("templates") or []
        print(f"模板数量: {len(templates)}\n")
        for t in templates:
            print(f"  templateId  : {t.get('templateId')}")
            print(f"  templateName: {t.get('templateName')}")
            print(f"  templateType: {t.get('templateType')}  status: {t.get('templateStatus')}")
            print(f"  createTime  : {t.get('createTime')}")
            print("-" * 60)
        if not templates:
            print("原始响应：")
            print(json.dumps(data, ensure_ascii=False, indent=2))
except urllib.error.HTTPError as e:
    print(f"HTTP 错误 {e.code}: {e.reason}")
    print(e.read().decode("utf-8"))
except Exception as e:
    print(f"请求异常: {e}")
