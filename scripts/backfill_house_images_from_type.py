#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
老房源户型图片批量补齐脚本。

严格复刻管理端 ruoyi-ui/src/views/gangzhu/house/index.vue 中
  handleHouseTypeChange -> pullHouseTypeImages -> saveImages
的前端手动流程：
  - 仅对 "有 house_type_id 且当前 hz_house_image 无任何有效记录" 的房源进行补图
  - 按 image_type 1/2/3/4/5/6 (主图/户型图/卧室/卫生间/室内/室外) 从户型图拷贝
  - is_cover: 仅主图第一张为 '1'，其他 '0'
  - sort_order: 跨类型全局递增，类型内按 sort_order ASC, image_id ASC
  - 覆盖策略: 物理 DELETE 该 house_id 所有 hz_house_image 行后批量 INSERT
  - 户型本身无图时: 跳过该房源，不做任何操作

支持 DRY_RUN 模式（默认 True），仅打印统计结果，不落库。
"""

import sys
import pymysql
from collections import defaultdict
from datetime import datetime

# ============== 配置 ==============
DRY_RUN = True  # 正式执行时改为 False

DB_CONFIG = {
    "host": "36.133.39.148",
    "port": 33061,
    "user": "ghzuser",
    "password": "!aNJ-7vTE+Tsddce",
    "database": "ghz",
    "charset": "utf8mb4",
    "autocommit": False,
}

IMAGE_TYPES = ["1", "2", "3", "4", "5", "6"]
IMAGE_TYPE_NAMES = {
    "1": "主图",
    "2": "户型图",
    "3": "卧室",
    "4": "卫生间",
    "5": "室内",
    "6": "室外",
}


def fetch_candidate_houses(cur):
    """查询待补图的老房源: 有 house_type_id 且 hz_house_image 里没有一条有效记录。"""
    sql = """
        SELECT h.house_id, h.house_type_id, h.house_code, h.house_no, h.project_id
        FROM hz_house h
        WHERE h.del_flag = '0'
          AND h.house_type_id IS NOT NULL
          AND NOT EXISTS (
              SELECT 1 FROM hz_house_image i
              WHERE i.house_id = h.house_id AND i.del_flag = '0'
          )
        ORDER BY h.house_id ASC
    """
    cur.execute(sql)
    return cur.fetchall()


def fetch_house_type_images(cur, house_type_id):
    """按 image_type ASC, sort_order ASC, image_id ASC 查户型图。"""
    sql = """
        SELECT image_id, image_url, image_type
        FROM hz_house_type_image
        WHERE house_type_id = %s AND del_flag = '0'
        ORDER BY image_type ASC, sort_order ASC, image_id ASC
    """
    cur.execute(sql, (house_type_id,))
    return cur.fetchall()


def build_all_images(house_id, type_images):
    """严格复刻前端 saveImages 的规则生成待插入记录。

    type_images: list[dict{image_url, image_type}] 已按 image_type/sort 排好序。
    """
    grouped = defaultdict(list)
    for row in type_images:
        t = (row["image_type"] or "1").strip()
        if t in IMAGE_TYPES:
            grouped[t].append(row["image_url"])

    all_images = []
    for t in IMAGE_TYPES:
        for idx, url in enumerate(grouped[t]):
            all_images.append({
                "house_id": house_id,
                "image_url": url,
                "image_type": t,
                "is_cover": "1" if (t == "1" and idx == 0) else "0",
                "sort_order": len(all_images),
                "del_flag": "0",
            })
    return all_images


def main():
    mode = "DRY-RUN (仅统计, 不落库)" if DRY_RUN else "正式执行 (将修改数据)"
    print("=" * 70)
    print(f"老房源户型图片批量补齐脚本 - {mode}")
    print(f"开始时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print("=" * 70)

    conn = pymysql.connect(**DB_CONFIG)
    try:
        with conn.cursor(pymysql.cursors.DictCursor) as cur:
            # 1) 候选房源
            houses = fetch_candidate_houses(cur)
            total_houses = len(houses)
            print(f"\n[候选统计] 无图且有户型的老房源数量: {total_houses}")
            if total_houses == 0:
                print("无待处理房源，脚本结束。")
                return

            # 统计按项目分布
            by_project = defaultdict(int)
            house_type_usage = defaultdict(int)  # house_type_id -> 引用次数
            for h in houses:
                by_project[h["project_id"]] += 1
                house_type_usage[h["house_type_id"]] += 1
            print(f"[候选统计] 涉及项目数: {len(by_project)}；涉及不同户型数: {len(house_type_usage)}")

            # 2) 户型图预查（去重后的 house_type_id 集合一次性查出缓存）
            type_cache = {}
            for htid in house_type_usage.keys():
                type_cache[htid] = fetch_house_type_images(cur, htid)

            # 3) 模拟处理，统计每条房源"将补多少图"
            will_fill_count = 0     # 将补图的房源数
            will_skip_count = 0     # 因户型无图而跳过的房源数
            total_image_rows = 0    # 将插入的图片总行数
            fill_by_type = defaultdict(int)     # 每类将插入多少张（汇总）
            fill_count_hist = defaultdict(int)  # 每房源将补张数的直方图

            empty_type_houses = []  # 因户型无图被跳过的房源明细（前 10）
            sample_fill = []        # 将补图的房源样例（前 5）

            for h in houses:
                type_rows = type_cache.get(h["house_type_id"], [])
                all_images = build_all_images(h["house_id"], type_rows)
                if not all_images:
                    will_skip_count += 1
                    if len(empty_type_houses) < 10:
                        empty_type_houses.append(h)
                    continue
                will_fill_count += 1
                total_image_rows += len(all_images)
                fill_count_hist[len(all_images)] += 1
                for img in all_images:
                    fill_by_type[img["image_type"]] += 1
                if len(sample_fill) < 5:
                    sample_fill.append((h, all_images))

            # 4) 打印汇总
            print("\n" + "-" * 70)
            print("[处理预测]")
            print(f"  将补图房源数         : {will_fill_count}")
            print(f"  跳过(户型自身无图)   : {will_skip_count}")
            print(f"  将插入图片总行数     : {total_image_rows}")
            print("  按类型分布：")
            for t in IMAGE_TYPES:
                print(f"    {t}-{IMAGE_TYPE_NAMES[t]:<4}: {fill_by_type[t]} 张")

            print("\n[每房源补图张数分布]")
            for cnt in sorted(fill_count_hist.keys()):
                print(f"  补 {cnt:>2} 张 : {fill_count_hist[cnt]} 个房源")

            if sample_fill:
                print("\n[抽样 5 个将补图的房源]")
                for h, imgs in sample_fill:
                    types_brief = defaultdict(int)
                    for im in imgs:
                        types_brief[im["image_type"]] += 1
                    brief = ", ".join(
                        f"{IMAGE_TYPE_NAMES[t]}×{types_brief[t]}" for t in IMAGE_TYPES if types_brief[t] > 0
                    )
                    print(f"  house_id={h['house_id']:<8} code={h['house_code']} no={h['house_no']} "
                          f"house_type_id={h['house_type_id']}  →  共 {len(imgs)} 张 ({brief})")

            if empty_type_houses:
                print("\n[抽样 10 个因户型无图被跳过的房源]")
                for h in empty_type_houses:
                    print(f"  house_id={h['house_id']:<8} code={h['house_code']} "
                          f"house_type_id={h['house_type_id']} (该户型 hz_house_type_image 无有效记录)")

            # 5) 若非 DRY_RUN，执行写库（本次不触发，仅为完整闭环保留）
            if not DRY_RUN:
                print("\n" + "=" * 70)
                print("开始正式写入 ...")
                ok = 0
                fail = 0
                for h in houses:
                    type_rows = type_cache.get(h["house_type_id"], [])
                    all_images = build_all_images(h["house_id"], type_rows)
                    if not all_images:
                        continue
                    try:
                        with conn.cursor() as wcur:
                            wcur.execute(
                                "DELETE FROM hz_house_image WHERE house_id=%s",
                                (h["house_id"],),
                            )
                            insert_sql = (
                                "INSERT INTO hz_house_image "
                                "(house_id, image_url, image_type, is_cover, sort_order, del_flag, create_time) "
                                "VALUES (%s, %s, %s, %s, %s, %s, NOW())"
                            )
                            params = [
                                (im["house_id"], im["image_url"], im["image_type"],
                                 im["is_cover"], im["sort_order"], im["del_flag"])
                                for im in all_images
                            ]
                            wcur.executemany(insert_sql, params)
                        conn.commit()
                        ok += 1
                    except Exception as e:
                        conn.rollback()
                        fail += 1
                        print(f"  [失败] house_id={h['house_id']}: {e}")
                print(f"写入完成: 成功 {ok} 条, 失败 {fail} 条")

            print("\n" + "=" * 70)
            print(f"结束时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
            print(f"模式: {mode}")
            print("=" * 70)
    finally:
        conn.close()


if __name__ == "__main__":
    main()
