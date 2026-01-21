# Quartz定时任务表

## 1. 任务详细信息表

**表名**: `QRTZ_JOB_DETAILS`

| 序号 | 列名 | 数据类型 | 列说明 |
|------|------|---------|--------|
| 1 | sched_name | varchar(120) | 调度名称 |
| 2 | job_name | varchar(200) | 任务名称 |
| 3 | job_group | varchar(200) | 任务组名 |
| 4 | description | varchar(250) | 相关介绍 |
| 5 | job_class_name | varchar(250) | 执行任务类名称 |
| 6 | is_durable | varchar(1) | 是否持久化 |
| 7 | is_nonconcurrent | varchar(1) | 是否并发 |
| 8 | is_update_data | varchar(1) | 是否更新数据 |
| 9 | requests_recovery | varchar(1) | 是否接受恢复执行 |
| 10 | job_data | blob | 存放持久化job对象 |

---

## 2. 触发器详细信息表

**表名**: `QRTZ_TRIGGERS`

| 序号 | 列名 | 数据类型 | 列说明 |
|------|------|---------|--------|
| 1 | sched_name | varchar(120) | 调度名称 |
| 2 | trigger_name | varchar(200) | 触发器的名字 |
| 3 | trigger_group | varchar(200) | 触发器所属组的名字 |
| 4 | job_name | varchar(200) | qrtz_job_details表job_name的外键 |
| 5 | job_group | varchar(200) | qrtz_job_details表job_group的外键 |
| 6 | description | varchar(250) | 相关介绍 |
| 7 | next_fire_time | bigint(13) | 上一次触发时间(毫秒) |
| 8 | prev_fire_time | bigint(13) | 下一次触发时间(默认为-1表示不触发) |
| 9 | priority | integer | 优先级 |
| 10 | trigger_state | varchar(16) | 触发器状态 |
| 11 | trigger_type | varchar(8) | 触发器的类型 |
| 12 | start_time | bigint(13) | 开始时间 |
| 13 | end_time | bigint(13) | 结束时间 |
| 14 | calendar_name | varchar(200) | 日程表名称 |
| 15 | misfire_instr | smallint(2) | 补偿执行的策略 |
| 16 | job_data | blob | 存放持久化job对象 |

---

## 3. 简单触发器的信息表

**表名**: `QRTZ_SIMPLE_TRIGGERS`

| 序号 | 列名 | 数据类型 | 列说明 |
|------|------|---------|--------|
| 1 | sched_name | varchar(120) | 调度名称 |
| 2 | trigger_name | varchar(200) | qrtz_triggers表trigger_name的外键 |
| 3 | trigger_group | varchar(200) | qrtz_triggers表trigger_group的外键 |
| 4 | repeat_count | bigint(7) | 重复的次数统计 |
| 5 | repeat_interval | bigint(12) | 重复的间隔时间 |
| 6 | times_triggered | bigint(10) | 已经触发的次数 |

---

## 4. Cron类型的触发器表

**表名**: `QRTZ_CRON_TRIGGERS`

| 序号 | 列名 | 数据类型 | 列说明 |
|------|------|---------|--------|
| 1 | sched_name | varchar(120) | 调度名称 |
| 2 | trigger_name | varchar(200) | qrtz_triggers表trigger_name的外键 |
| 3 | trigger_group | varchar(200) | qrtz_triggers表trigger_group的外键 |
| 4 | cron_expression | varchar(200) | cron表达式 |
| 5 | time_zone_id | varchar(80) | 时区 |

---

## 5. Blob类型的触发器表

**表名**: `QRTZ_BLOB_TRIGGERS`

| 序号 | 列名 | 数据类型 | 列说明 |
|------|------|---------|--------|
| 1 | sched_name | varchar(120) | 调度名称 |
| 2 | trigger_name | varchar(200) | qrtz_triggers表trigger_name的外键 |
| 3 | trigger_group | varchar(200) | qrtz_triggers表trigger_group的外键 |
| 4 | blob_data | blob | 存放持久化Trigger对象 |

---

## 6. 日历信息表

**表名**: `QRTZ_CALENDARS`

| 序号 | 列名 | 数据类型 | 列说明 |
|------|------|---------|--------|
| 1 | sched_name | varchar(120) | 调度名称 |
| 2 | calendar_name | varchar(200) | 日历名称 |
| 3 | calendar | blob | 存放持久化calendar对象 |

---

## 7. 暂停的触发器表

**表名**: `QRTZ_PAUSED_TRIGGER_GRPS`

| 序号 | 列名 | 数据类型 | 列说明 |
|------|------|---------|--------|
| 1 | sched_name | varchar(120) | 调度名称 |
| 2 | trigger_group | varchar(200) | qrtz_triggers表trigger_group的外键 |

---

## 8. 已触发的触发器表

**表名**: `QRTZ_FIRED_TRIGGERS`

| 序号 | 列名 | 数据类型 | 列说明 |
|------|------|---------|--------|
| 1 | sched_name | varchar(120) | 调度名称 |
| 2 | entry_id | varchar(95) | 调度器实例id |
| 3 | trigger_name | varchar(200) | qrtz_triggers表trigger_name的外键 |
| 4 | trigger_group | varchar(200) | qrtz_triggers表trigger_group的外键 |
| 5 | instance_name | varchar(200) | 调度器实例名 |
| 6 | fired_time | bigint(13) | 触发的时间 |
| 7 | sched_time | bigint(13) | 定时器制定的时间 |
| 8 | priority | integer | 优先级 |
| 9 | state | varchar(16) | 状态 |
| 10 | job_name | varchar(200) | 任务名称 |
| 11 | job_group | varchar(200) | 任务组名 |
| 12 | is_nonconcurrent | varchar(1) | 是否并发 |
| 13 | requests_recovery | varchar(1) | 是否接受恢复执行 |

---

## 9. 调度器状态表

**表名**: `QRTZ_SCHEDULER_STATE`

| 序号 | 列名 | 数据类型 | 列说明 |
|------|------|---------|--------|
| 1 | sched_name | varchar(120) | 调度名称 |
| 2 | instance_name | varchar(200) | 实例名称 |
| 3 | last_checkin_time | bigint(13) | 上次检查时间 |
| 4 | checkin_interval | bigint(13) | 检查间隔时间 |

---

## 10. 存储的悲观锁信息表

**表名**: `QRTZ_LOCKS`

| 序号 | 列名 | 数据类型 | 列说明 |
|------|------|---------|--------|
| 1 | sched_name | varchar(120) | 调度名称 |
| 2 | lock_name | varchar(40) | 悲观锁名称 |

---

## 11. 同步机制的行锁表

**表名**: `QRTZ_SIMPROP_TRIGGERS`

| 序号 | 列名 | 数据类型 | 列说明 |
|------|------|---------|--------|
| 1 | sched_name | varchar(120) | 调度名称 |
| 2 | trigger_name | varchar(200) | qrtz_triggers表trigger_name的外键 |
| 3 | trigger_group | varchar(200) | qrtz_triggers表trigger_group的外键 |
| 4 | str_prop_1 | varchar(512) | String类型的trigger的第一个参数 |
| 5 | str_prop_2 | varchar(512) | String类型的trigger的第二个参数 |
| 6 | str_prop_3 | varchar(512) | String类型的trigger的第三个参数 |
| 7 | int_prop_1 | int | int类型的trigger的第一个参数 |
| 8 | int_prop_2 | int | int类型的trigger的第二个参数 |
| 9 | long_prop_1 | bigint | long类型的trigger的第一个参数 |
| 10 | long_prop_2 | bigint | long类型的trigger的第二个参数 |
| 11 | dec_prop_1 | numeric(13,4) | decimal类型的trigger的第一个参数 |
| 12 | dec_prop_2 | numeric(13,4) | decimal类型的trigger的第二个参数 |
| 13 | bool_prop_1 | varchar(1) | Boolean类型的trigger的第一个参数 |
| 14 | bool_prop_2 | varchar(1) | Boolean类型的trigger的第二个参数 |
