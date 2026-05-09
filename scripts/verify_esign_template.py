#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
一次性脚本：验证 e签宝正式环境模板 ID 是否有效
复刻 EsignHttpHelper.signAndBuildSignAndJsonHeader 的签名算法
"""
import base64
import hmac
import hashlib
import json
import time
import urllib.request
import urllib.error

APP_ID = "5112061749"
APP_SECRET = "0d76219647c86a41e13c2f78c31f0458"
HOST = "https://openapi.esign.cn"
TEMPLATE_ID = "4027bb6b226147b0820527a9d41609a5"

METHOD = "GET"
PATH = f"/v3/doc-templates/{TEMPLATE_ID}"
ACCEPT = "*/*"
CONTENT_TYPE = "application/json; charset=UTF-8"
CONTENT_MD5 = ""  # GET 请求无 body

# 待签名串（与 EsignEncryption.appendSignDataString 完全一致）
# HTTPMethod\nAccept\nContent-MD5\nContent-Type\n\nPathAndParameters
sign_data = f"{METHOD}\n{ACCEPT}\n{CONTENT_MD5}\n{CONTENT_TYPE}\n\n{PATH}"
print("待签名串：")
print(repr(sign_data))
print()

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

url = HOST + PATH
print(f"请求: GET {url}")
print(f"签名: {signature}")
print()

req = urllib.request.Request(url, headers=headers, method="GET")
try:
    with urllib.request.urlopen(req, timeout=15) as resp:
        body = resp.read().decode("utf-8")
        print(f"HTTP 状态: {resp.status}")
        # 完整响应写入文件
        with open("scripts/esign_template_detail.json", "w", encoding="utf-8") as f:
            f.write(body)
        try:
            data = json.loads(body)
            # 尝试多种路径统计控件总数
            d = data.get("data") or {}
            components = d.get("components") or d.get("structuralComponents") or []
            print(f"控件总数: {len(components)}")
            # 打印所有控件名
            for i, c in enumerate(components, 1):
                print(f"  {i:3d}. name={c.get('componentName'):<20s}  type={c.get('componentType'):<3d}  id={c.get('componentId')}")
            print(f"\n完整响应已保存到 scripts/esign_template_detail.json")
        except Exception as e:
            print(f"解析异常: {e}")
            print("前 500 字响应：", body[:500])
except urllib.error.HTTPError as e:
    print(f"HTTP 错误 {e.code}: {e.reason}")
    try:
        err_body = e.read().decode("utf-8")
        print("响应原文：")
        print(err_body)
    except Exception:
        pass
except Exception as e:
    print(f"请求异常: {e}")
