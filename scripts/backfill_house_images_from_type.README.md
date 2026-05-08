# 老房源户型图批量补齐脚本

脚本：[`backfill_house_images_from_type.py`](./backfill_house_images_from_type.py)

## 用途

将"有户型、但 `hz_house_image` 里没有任何有效记录"的老房源，按其 `house_type_id` 从 `hz_house_type_image` 拷贝图片补齐，逻辑严格复刻前端"选择户型自动拉图"。

## 执行

```bash
# 1) 先跑 DRY-RUN 看统计（脚本默认 DRY_RUN=True）
/usr/bin/python3 scripts/backfill_house_images_from_type.py

# 2) 确认无误后改开关，重跑
#    打开脚本，把 DRY_RUN = True 改为 DRY_RUN = False
/usr/bin/python3 scripts/backfill_house_images_from_type.py

# 3) 跑完改回 DRY_RUN = True（安全默认）
```

## 何时需要再跑

| 场景 | 跑不跑 |
|---|---|
| 运营在户型管理给老户型补了图 | ✅ 跑一次，把对应老房源补上 |
| 新增房源但未上传图 | ✅ 跑一次会从户型拉图 |
| 房源已有图，想重新同步 | ❌ 脚本不处理，不会覆盖运营已传的图 |

## 幂等性

脚本只处理"当前完全无图"的房源，已有图的房源自动跳过，**重复执行不会覆盖任何已有数据**。

## 规则（与前端手动拉图完全一致）

- 来源：`hz_house_type_image.house_type_id = hz_house.house_type_id` 且 `del_flag='0'`
- 按 `image_type` 分 6 类：`1`主图 / `2`户型图 / `3`卧室 / `4`卫生间 / `5`室内 / `6`室外
- `is_cover`：仅主图第一张为 `1`，其余 `0`
- `sort_order`：跨类型全局递增，顺序 1→2→3→4→5→6
- 写入前先物理 `DELETE` 该 `house_id` 所有 `hz_house_image` 行，再批量 `INSERT`
- 户型本身无图时静默跳过该房源

## 数据库连接

脚本内硬编码生产库 `36.133.39.148:33061/ghz`，如需切换改脚本顶部 `DB_CONFIG`。

## 回滚

每次写入记录的 `create_time` 即执行时间，按时段反查后 `DELETE` 即可：

```sql
SELECT * FROM hz_house_image
WHERE create_time BETWEEN '起始时间' AND '结束时间';

DELETE FROM hz_house_image
WHERE create_time BETWEEN '起始时间' AND '结束时间';
```

## 历史执行记录

| 时间 | 候选房源 | 成功补图 | 跳过(户型无图) | 写入行数 |
|---|---|---|---|---|
| 2026-05-07 12:54 | 1770 | 216 | 1554 | 903 |
