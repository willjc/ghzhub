#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""幂等执行：hz_house_type 表增加 vr_url 列。"""
import pymysql

CONF = dict(
    host="36.133.39.148",
    port=33061,
    user="ghzuser",
    password="!aNJ-7vTE+Tsddce",
    database="ghz",
    charset="utf8mb4",
    autocommit=True,
)

ALTER_SQL = (
    "ALTER TABLE hz_house_type "
    "ADD COLUMN vr_url VARCHAR(500) DEFAULT NULL "
    "COMMENT '户型VR链接(新增房源选中户型时自动继承)' "
    "AFTER layout_image"
)

CHECK_SQL = (
    "SELECT COLUMN_NAME, COLUMN_TYPE, COLUMN_COMMENT "
    "FROM information_schema.COLUMNS "
    "WHERE TABLE_SCHEMA=%s AND TABLE_NAME='hz_house_type' AND COLUMN_NAME='vr_url'"
)

def main():
    conn = pymysql.connect(**CONF)
    try:
        with conn.cursor() as cur:
            cur.execute(CHECK_SQL, (CONF["database"],))
            row = cur.fetchone()
            if row:
                print(f"[SKIP] vr_url 已存在: {row}")
                return
            print("[EXEC] " + ALTER_SQL)
            cur.execute(ALTER_SQL)
            print("[OK] ALTER 成功")
            cur.execute(CHECK_SQL, (CONF["database"],))
            print(f"[VERIFY] {cur.fetchone()}")
    finally:
        conn.close()

if __name__ == "__main__":
    main()
