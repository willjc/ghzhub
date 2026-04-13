#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
港好住老系统数据迁移脚本
从 Excel 文件读取老系统数据，清洗转换后导入新系统 MySQL 数据库

使用方法:
  python3 migrate_data.py                  # 执行全量迁移
  python3 migrate_data.py --dry-run        # 仅输出SQL，不执行
  python3 migrate_data.py --step 1         # 只执行第1步
  python3 migrate_data.py --from-step 3    # 从第3步开始执行
  python3 migrate_data.py --filter-active  # 只导入活跃数据(过滤已退租/已失效)
"""

import os
import sys
import argparse
import datetime
import re
import json
import traceback
from collections import OrderedDict

import openpyxl
import xlrd
import pymysql
from pymysql import Error

# ============================================================
# 配置区
# ============================================================

DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': '123456789w',
    'database': 'newgangzhu',
    'charset': 'utf8mb4',
    'cursorclass': pymysql.cursors.DictCursor,
}

DATA_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), '系统数据')

# 数据文件列表
DATA_FILES = {
    'project_house': '项目房源.xlsx',
    'person': '人员.xlsx',
    'contract1': '合同1.xlsx',
    'contract2': '合同2.xlsx',
    'checkin_checkout': '入退.xlsx',
    'bill': '账单.xlsx',
    'payment': '缴费记录.xlsx',
    'house_type': '户型.xlsx',
    'co_tenant': '同住人.xlsx',
    'appeal': '申诉记录.xlsx',
    'exchange': '调换房.xlsx',
    'blacklist': '黑名单.xls',
}

# ============================================================
# 枚举值映射
# ============================================================

# 性别映射
GENDER_MAP = {'男': '0', '女': '1', '1': '0', '2': '1'}

# 婚姻状况映射
MARRIAGE_MAP = {'未婚': '未婚', '已婚': '已婚', '离婚': '离婚', '丧偶': '丧偶'}

# 学历映射
EDUCATION_MAP = {
    '小学': '小学', '初中': '初中', '高中': '高中', '中专': '中专',
    '专科': '大专', '大专': '大专', '本科生': '本科', '本科': '本科',
    '硕士研究生': '硕士', '硕士': '硕士', '博士研究生': '博士', '博士': '博士',
}

# 房间状态映射 -> hz_house.house_status
HOUSE_STATUS_MAP = {
    '空置': '0', '已租': '1', '已预订': '2', '修缮': '3', '维修中': '3', '下架': '4',
}

# 合同状态映射 -> hz_contract.contract_status
# 0:草稿 1:待签署 2:已签署 3:履行中 4:已到期 5:已解除
CONTRACT_STATUS_MAP = {
    '草稿': '0', '待签署': '1', '已签署': '2', '执行中': '3', '履行中': '3',
    '已到期': '4', '已失效': '4', '已退租': '5', '已解除': '5', '退租': '5',
}

# 账单类型映射 -> hz_bill.bill_type
# 1:押金 2:租金 3:水费 4:电费 5:燃气费 6:物业费 7:其他
BILL_TYPE_MAP = {
    '押金': '1', '房租': '2', '租金': '2', '水费': '3', '电费': '4',
    '燃气费': '5', '物业费': '6', '其他': '7',
}

# 账单状态映射 -> hz_bill.bill_status
# 0:待支付 1:已支付 2:部分支付 3:已逾期 4:已关闭
BILL_STATUS_MAP = {
    '待支付': '0', '未支付': '0', '已支付': '1', '部分支付': '2', '已逾期': '3', '已关闭': '4',
}

# 支付方式映射 -> hz_payment.payment_method
PAYMENT_METHOD_MAP = {
    '微信': '2', '支付宝': '1', '银行转账': '3', '现金': '4',
}

# 房源分配类型映射
ALLOCATION_TYPE_MAP = {
    '常规分配': '1', '集中分配': '2',
}

# 申请人类型映射 -> hz_qualification.apply_type
# 1:人才公寓 2:保租房 3:市场租赁
APPLY_TYPE_MAP = {
    '主申请人': '1', '人才公寓': '1', '保租房': '2', '市场租赁': '3',
}

# 入住流程状态映射 -> hz_checkin_record.status
# 0:待办理 1:待审核 2:已入住 3:已拒绝 4:已退租
CHECKIN_STATUS_MAP = {
    '点验已签字': '2', '已入住': '2', '已作废': '3', '待办理': '0', '待审核': '1',
}

# 学历映射 -> hz_user.education (char(1))
EDUCATION_USER_MAP = {
    '小学': '1', '初中': '2', '高中': '3', '中专': '3',
    '大专': '4', '本科': '5', '硕士': '6', '博士': '7',
}

# 装修类型映射
DECORATION_MAP = {
    '精装': '精装', '简装': '简装', '毛坯': '毛坯', '豪装': '豪装',
}

# 朝向映射
ORIENTATION_MAP = {
    '东': '东', '南': '南', '西': '西', '北': '北',
    '东南': '东南', '东北': '东北', '西南': '西南', '西北': '西北',
}

# ============================================================
# 工具函数
# ============================================================

class IdMapper:
    """旧ID到新ID的映射器"""
    def __init__(self):
        self.mappings = {}  # {mapping_name: {old_id: new_id}}

    def register(self, name, old_id, new_id):
        if name not in self.mappings:
            self.mappings[name] = {}
        self.mappings[name][str(old_id)] = new_id

    def get(self, name, old_id, default=None):
        mapping = self.mappings.get(name, {})
        return mapping.get(str(old_id), default)

    def has(self, name, old_id):
        return str(old_id) in self.mappings.get(name, {})

    def size(self, name):
        return len(self.mappings.get(name, {}))

    def save_to_file(self, filepath):
        """保存映射关系到文件，用于断点续传"""
        with open(filepath, 'w', encoding='utf-8') as f:
            json.dump(self.mappings, f, ensure_ascii=False, indent=2)

    def load_from_file(self, filepath):
        """从文件加载映射关系"""
        if os.path.exists(filepath):
            with open(filepath, 'r', encoding='utf-8') as f:
                self.mappings = json.load(f)
            return True
        return False


id_mapper = IdMapper()

# 全局统计
stats = {}


def log(msg, level='INFO'):
    """输出日志"""
    timestamp = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    print(f"[{timestamp}] [{level}] {msg}")


def safe_str(val, max_len=None):
    """安全转字符串"""
    if val is None or str(val).strip() == '' or str(val).strip() == 'None':
        return None
    s = str(val).strip()
    if max_len and len(s) > max_len:
        s = s[:max_len]
    return s


def safe_int(val, default=0):
    """安全转整数"""
    if val is None or str(val).strip() == '' or str(val).strip() == 'None':
        return default
    try:
        return int(float(str(val).strip()))
    except (ValueError, TypeError):
        return default


def safe_float(val, default=0.0):
    """安全转浮点数"""
    if val is None or str(val).strip() == '' or str(val).strip() == 'None':
        return default
    try:
        return float(str(val).strip())
    except (ValueError, TypeError):
        return default


def safe_date(val):
    """安全转日期字符串 (YYYY-MM-DD)"""
    if val is None or str(val).strip() == '' or str(val).strip() == 'None':
        return None
    s = str(val).strip()
    # 处理带毫秒的时间格式
    if '.' in s:
        s = s.split('.')[0]
    # 尝试多种格式
    for fmt in ('%Y-%m-%d %H:%M:%S', '%Y-%m-%d', '%Y/%m/%d'):
        try:
            dt = datetime.datetime.strptime(s[:19], fmt)
            return dt.strftime('%Y-%m-%d')
        except ValueError:
            continue
    return None


def safe_datetime(val):
    """安全转日期时间字符串 (YYYY-MM-DD HH:MM:SS)"""
    if val is None or str(val).strip() == '' or str(val).strip() == 'None':
        return None
    s = str(val).strip()
    if '.' in s:
        s = s.split('.')[0]
    for fmt in ('%Y-%m-%d %H:%M:%S', '%Y-%m-%d', '%Y/%m/%d %H:%M:%S'):
        try:
            dt = datetime.datetime.strptime(s[:19], fmt)
            return dt.strftime('%Y-%m-%d %H:%M:%S')
        except ValueError:
            continue
    return None


def map_enum(val, mapping, default=None):
    """枚举值映射"""
    if val is None or str(val).strip() == '' or str(val).strip() == 'None':
        return default
    return mapping.get(str(val).strip(), default)


def generate_bill_no(bill_type_code, date_str, seq):
    """生成账单编号: ZJ20231019001 / YJ20231019001"""
    prefix_map = {'1': 'YJ', '2': 'ZJ', '3': 'SF', '4': 'DF', '5': 'RQ', '6': 'WY', '7': 'QT'}
    prefix = prefix_map.get(str(bill_type_code), 'QT')
    date_part = date_str.replace('-', '')[:8] if date_str else datetime.datetime.now().strftime('%Y%m%d')
    return f"{prefix}{date_part}{seq:04d}"


def generate_contract_no(date_str, seq):
    """生成合同编号: HT20231019001"""
    date_part = date_str.replace('-', '')[:8] if date_str else datetime.datetime.now().strftime('%Y%m%d')
    return f"HT{date_part}{seq:05d}"


def generate_checkin_no(date_str, seq):
    """生成入住单号: RZ20231019001"""
    date_part = date_str.replace('-', '')[:8] if date_str else datetime.datetime.now().strftime('%Y%m%d')
    return f"RZ{date_part}{seq:06d}"


def generate_payment_no(seq):
    """生成缴费编号"""
    return f"PAY{datetime.datetime.now().strftime('%Y%m%d')}{seq:06d}"


def read_excel(filepath):
    """读取Excel文件，返回(列名列表, 数据行列表)"""
    log(f"读取文件: {filepath}")
    if filepath.endswith('.xls'):
        return read_xls(filepath)
    wb = openpyxl.load_workbook(filepath, read_only=True)
    ws = wb.active
    rows = list(ws.iter_rows(values_only=True))
    wb.close()
    if not rows:
        return [], []
    headers = [str(h).strip() if h else f'col_{i}' for i, h in enumerate(rows[0])]
    data = []
    for row in rows[1:]:
        if any(v is not None for v in row):
            data.append(row)
    log(f"  读取到 {len(data)} 行数据, {len(headers)} 列")
    return headers, data


def read_xls(filepath):
    """读取 .xls 格式文件"""
    wb = xlrd.open_workbook(filepath)
    ws = wb.sheet_by_index(0)
    headers = [str(ws.cell_value(0, c)).strip() for c in range(ws.ncols)]
    data = []
    for r in range(1, ws.nrows):
        row = tuple(ws.cell_value(r, c) for c in range(ws.ncols))
        if any(v != '' for v in row):
            data.append(row)
    log(f"  读取到 {len(data)} 行数据, {len(headers)} 列")
    return headers, data


def make_row_dict(headers, row):
    """将行数据转为字典"""
    return {headers[i]: row[i] if i < len(row) else None for i in range(len(headers))}


def batch_insert(cursor, table, columns, rows, batch_size=500):
    """批量插入数据"""
    if not rows:
        return 0
    inserted = 0
    for i in range(0, len(rows), batch_size):
        batch = rows[i:i + batch_size]
        placeholders = ', '.join(['%s'] * len(columns))
        col_str = ', '.join([f'`{c}`' for c in columns])
        sql = f"INSERT INTO `{table}` ({col_str}) VALUES ({placeholders})"
        try:
            cursor.executemany(sql, batch)
            inserted += len(batch)
        except Error as e:
            log(f"批量插入失败(表={table}, 批次={i//batch_size}): {e}", 'ERROR')
            # 逐条插入以定位问题行
            for j, row in enumerate(batch):
                try:
                    cursor.execute(sql, row)
                    inserted += 1
                except Error as e2:
                    log(f"  第{j+1}行插入失败: {e2}, 数据: {row[:3]}...", 'ERROR')
    return inserted


def get_next_auto_id(cursor, table, id_column):
    """获取表的自增ID起始值(当前最大值+1)"""
    cursor.execute(f"SELECT COALESCE(MAX(`{id_column}`), 0) as max_id FROM `{table}`")
    result = cursor.fetchone()
    return result['max_id'] + 1 if result else 1


# ============================================================
# 第1步: 导入项目数据 (hz_project)
# ============================================================

def step1_import_projects(cursor, filter_active=False):
    """从项目房源.xlsx中提取项目信息导入hz_project"""
    log("=" * 60)
    log("第1步: 导入项目数据 (hz_project)")
    log("=" * 60)

    headers, data = read_excel(os.path.join(DATA_DIR, DATA_FILES['project_house']))
    if not data:
        log("无数据，跳过", 'WARN')
        return 0

    # 提取去重的项目信息
    projects = OrderedDict()  # 用小区Id去重
    for row in data:
        d = make_row_dict(headers, row)
        project_old_id = safe_str(d.get('小区Id'))
        if not project_old_id or project_old_id in projects:
            continue
        projects[project_old_id] = {
            'old_id': project_old_id,
            'project_name': safe_str(d.get('小区名称'), 100),
            'address': safe_str(d.get('详细地址'), 255),
            'longitude': safe_float(d.get('经度')),
            'latitude': safe_float(d.get('纬度')),
            'project_intro': safe_str(d.get('小区简介')),
        }

    # 获取新系统的起始ID
    next_id = get_next_auto_id(cursor, 'hz_project', 'project_id')

    # 构建插入数据
    columns = ['project_id', 'project_name', 'project_code', 'project_type',
               'address', 'longitude', 'latitude', 'total_buildings', 'total_houses',
               'available_houses', 'status', 'del_flag', 'create_by']
    insert_rows = []
    for i, (old_id, proj) in enumerate(projects.items()):
        new_id = next_id + i
        id_mapper.register('project', old_id, new_id)
        insert_rows.append((
            new_id,
            proj['project_name'] or f'项目{new_id}',
            f'OLD-{old_id[:10]}',  # project_code
            '1',  # project_type 默认人才公寓
            proj['address'],
            proj['longitude'] if proj['longitude'] != 0.0 else None,
            proj['latitude'] if proj['latitude'] != 0.0 else None,
            0, 0, 0,  # 统计字段后续更新
            '0', '0', 'migration',
        ))

    count = batch_insert(cursor, 'hz_project', columns, insert_rows)
    stats['project'] = count
    log(f"项目导入完成: {count} 条, ID映射: {id_mapper.size('project')} 个")
    return count


# ============================================================
# 第2步: 导入楼栋数据 (hz_building)
# ============================================================

def step2_import_buildings(cursor, filter_active=False):
    """从项目房源.xlsx中提取楼栋信息导入hz_building"""
    log("=" * 60)
    log("第2步: 导入楼栋数据 (hz_building)")
    log("=" * 60)

    headers, data = read_excel(os.path.join(DATA_DIR, DATA_FILES['project_house']))
    if not data:
        return 0

    # 提取去重的楼栋信息
    buildings = OrderedDict()
    for row in data:
        d = make_row_dict(headers, row)
        building_old_id = safe_str(d.get('楼栋Id'))
        project_old_id = safe_str(d.get('小区Id'))
        if not building_old_id or building_old_id in buildings:
            continue
        new_project_id = id_mapper.get('project', project_old_id)
        if not new_project_id:
            continue
        buildings[building_old_id] = {
            'old_id': building_old_id,
            'project_id': new_project_id,
            'building_name': safe_str(d.get('楼栋名称'), 50),
            'project_old_id': project_old_id,
        }

    next_id = get_next_auto_id(cursor, 'hz_building', 'building_id')
    columns = ['building_id', 'project_id', 'building_name', 'building_code',
               'total_floors', 'total_units', 'total_houses', 'status', 'del_flag', 'create_by']
    insert_rows = []
    for i, (old_id, bldg) in enumerate(buildings.items()):
        new_id = next_id + i
        id_mapper.register('building', old_id, new_id)
        insert_rows.append((
            new_id,
            bldg['project_id'],
            bldg['building_name'] or f'楼栋{new_id}',
            f'OLD-B-{old_id[:10]}',
            0, 0, 0,
            '0', '0', 'migration',
        ))

    count = batch_insert(cursor, 'hz_building', columns, insert_rows)
    stats['building'] = count
    log(f"楼栋导入完成: {count} 条, ID映射: {id_mapper.size('building')} 个")
    return count


# ============================================================
# 第3步: 导入单元数据 (hz_unit)
# ============================================================

def step3_import_units(cursor, filter_active=False):
    """从项目房源.xlsx中提取单元信息导入hz_unit"""
    log("=" * 60)
    log("第3步: 导入单元数据 (hz_unit)")
    log("=" * 60)

    headers, data = read_excel(os.path.join(DATA_DIR, DATA_FILES['project_house']))
    if not data:
        return 0

    units = OrderedDict()
    for row in data:
        d = make_row_dict(headers, row)
        unit_old_id = safe_str(d.get('单元Id'))
        building_old_id = safe_str(d.get('楼栋Id'))
        project_old_id = safe_str(d.get('小区Id'))
        if not unit_old_id or unit_old_id in units:
            continue
        new_building_id = id_mapper.get('building', building_old_id)
        new_project_id = id_mapper.get('project', project_old_id)
        if not new_building_id:
            continue
        units[unit_old_id] = {
            'old_id': unit_old_id,
            'building_id': new_building_id,
            'project_id': new_project_id,
            'unit_name': safe_str(d.get('单元名称'), 50),
        }

    next_id = get_next_auto_id(cursor, 'hz_unit', 'unit_id')

    # 先查看 hz_unit 表结构
    columns = ['unit_id', 'building_id', 'unit_name', 'unit_code',
               'total_houses', 'status', 'del_flag', 'create_by']
    insert_rows = []
    for i, (old_id, unit) in enumerate(units.items()):
        new_id = next_id + i
        id_mapper.register('unit', old_id, new_id)
        insert_rows.append((
            new_id,
            unit['building_id'],
            unit['unit_name'] or f'单元{new_id}',
            f'OLD-U-{old_id[:10]}',
            0,
            '0', '0', 'migration',
        ))

    count = batch_insert(cursor, 'hz_unit', columns, insert_rows)
    stats['unit'] = count
    log(f"单元导入完成: {count} 条, ID映射: {id_mapper.size('unit')} 个")
    return count


# ============================================================
# 第4步: 导入户型数据 (hz_house_type)
# ============================================================

def step4_import_house_types(cursor, filter_active=False):
    """从户型.xlsx中提取户型信息导入hz_house_type"""
    log("=" * 60)
    log("第4步: 导入户型数据 (hz_house_type)")
    log("=" * 60)

    headers, data = read_excel(os.path.join(DATA_DIR, DATA_FILES['house_type']))
    if not data:
        return 0

    next_id = get_next_auto_id(cursor, 'hz_house_type', 'house_type_id')
    columns = ['house_type_id', 'project_id', 'house_type_name', 'house_type_code',
               'bedroom_count', 'livingroom_count', 'bathroom_count',
               'sort_order', 'status', 'del_flag', 'create_by']

    insert_rows = []
    for i, row in enumerate(data):
        d = make_row_dict(headers, row)
        old_id = safe_str(d.get('id'))
        new_id = next_id + i
        id_mapper.register('house_type', old_id, new_id)

        # 项目ID映射
        building_old_id = safe_str(d.get('楼栋_id'))
        project_old_id = safe_str(d.get('小区编号'))
        new_project_id = id_mapper.get('project', project_old_id)

        insert_rows.append((
            new_id,
            new_project_id,
            safe_str(d.get('房型名称'), 50) or f'户型{new_id}',
            f'OLD-HT-{old_id}' if old_id else f'HT{new_id}',
            safe_int(d.get('房间数')),
            safe_int(d.get('客厅数')),
            safe_int(d.get('卫生间')),
            i + 1,  # sort_order
            '0', '0', 'migration',
        ))

    count = batch_insert(cursor, 'hz_house_type', columns, insert_rows)
    stats['house_type'] = count
    log(f"户型导入完成: {count} 条, ID映射: {id_mapper.size('house_type')} 个")
    return count


# ============================================================
# 第5步: 导入房源数据 (hz_house)
# ============================================================

def step5_import_houses(cursor, filter_active=False):
    """从项目房源.xlsx中提取房源信息导入hz_house"""
    log("=" * 60)
    log("第5步: 导入房源数据 (hz_house)")
    log("=" * 60)

    headers, data = read_excel(os.path.join(DATA_DIR, DATA_FILES['project_house']))
    if not data:
        return 0

    next_id = get_next_auto_id(cursor, 'hz_house', 'house_id')
    columns = ['house_id', 'project_id', 'building_id', 'unit_id', 'house_code', 'house_no',
               'floor', 'house_type_id', 'house_type_name', 'area', 'rent_price', 'orientation', 'decoration',
               'house_status', 'allocation_type', 'status', 'del_flag', 'create_by']

    insert_rows = []
    for i, row in enumerate(data):
        d = make_row_dict(headers, row)
        old_house_id = safe_str(d.get('房间编号'))
        if not old_house_id:
            continue

        new_id = next_id + i
        id_mapper.register('house', old_house_id, new_id)

        # 关联ID映射
        project_old_id = safe_str(d.get('小区Id'))
        building_old_id = safe_str(d.get('楼栋Id'))
        unit_old_id = safe_str(d.get('单元Id'))
        house_type_old_id = safe_str(d.get('户型编号Id'))

        new_project_id = id_mapper.get('project', project_old_id)
        new_building_id = id_mapper.get('building', building_old_id)
        new_unit_id = id_mapper.get('unit', unit_old_id)
        new_house_type_id = id_mapper.get('house_type', house_type_old_id)

        # 房间状态映射
        house_status = map_enum(safe_str(d.get('房间状态')), HOUSE_STATUS_MAP, '0')
        # 分配类型
        allocation_type = map_enum(safe_str(d.get('房源分配类型')), ALLOCATION_TYPE_MAP, '1')

        # 户型名称 - 从户型映射获取
        house_type_name = None
        if new_house_type_id:
            cursor.execute("SELECT house_type_name FROM hz_house_type WHERE house_type_id = %s", (new_house_type_id,))
            ht = cursor.fetchone()
            if ht:
                house_type_name = ht['house_type_name']

        insert_rows.append((
            new_id,
            new_project_id,
            new_building_id,
            new_unit_id,
            old_house_id,  # house_code 用老ID
            safe_str(d.get('房间'), 50) or f'房间{new_id}',
            safe_int(d.get('楼层')),
            new_house_type_id,
            house_type_name,
            safe_float(d.get('房间面积')),
            safe_float(d.get('房租单价 元/月')),
            map_enum(safe_str(d.get('朝向')), ORIENTATION_MAP),
            map_enum(safe_str(d.get('装修类型')), DECORATION_MAP),
            house_status,
            allocation_type,
            '0', '0', 'migration',
        ))

    count = batch_insert(cursor, 'hz_house', columns, insert_rows)
    stats['house'] = count
    log(f"房源导入完成: {count} 条, ID映射: {id_mapper.size('house')} 个")
    return count


# ============================================================
# 第6步: 导入租户数据 (hz_tenant)
# ============================================================

def step6_import_tenants(cursor, filter_active=False):
    """从人员.xlsx中提取租户信息导入hz_tenant"""
    log("=" * 60)
    log("第6步: 导入租户数据 (hz_tenant)")
    log("=" * 60)

    headers, data = read_excel(os.path.join(DATA_DIR, DATA_FILES['person']))
    if not data:
        return 0

    # 去重: 同一身份证号只保留最新一条
    tenants_by_idcard = OrderedDict()
    for row in data:
        d = make_row_dict(headers, row)
        id_card = safe_str(d.get('申请人证件号'))
        if not id_card or len(id_card) < 15:
            continue
        status = safe_str(d.get('当前人员状态'))
        # 过滤: 只导入符合资格的（如果开启了过滤）
        if filter_active and status != '符合资格':
            continue
        # 去重: 保留最新
        if id_card in tenants_by_idcard:
            old_time = safe_str(tenants_by_idcard[id_card].get('创建时间'))
            new_time = safe_str(d.get('创建时间'))
            if old_time and new_time and new_time > old_time:
                tenants_by_idcard[id_card] = d
        else:
            tenants_by_idcard[id_card] = d

    log(f"去重后租户数: {len(tenants_by_idcard)} (原始: {len(data)})")

    next_id = get_next_auto_id(cursor, 'hz_tenant', 'tenant_id')
    columns = ['tenant_id', 'tenant_name', 'id_card', 'phone', 'gender',
               'education', 'marriage_status', 'emergency_contact', 'emergency_phone',
               'work_unit', 'credit_score', 'is_blacklist', 'status', 'del_flag', 'create_by']

    insert_rows = []
    skipped = 0
    for i, (id_card, d) in enumerate(tenants_by_idcard.items()):
        old_id = safe_str(d.get('id'))
        new_id = next_id + i
        id_mapper.register('tenant', old_id, new_id)
        # 同时建立 资格证号 -> tenant_id 映射
        qual_no = safe_str(d.get('资格证号（一个家庭一个号）'))
        if qual_no:
            id_mapper.register('qualification', qual_no, new_id)
        # 身份证 -> tenant_id 映射
        id_mapper.register('idcard', id_card, new_id)

        phone = safe_str(d.get('申请人手机号'), 20)
        if not phone or len(phone) < 8:
            skipped += 1
            phone = f'0000000{new_id}'[-11:]  # 生成占位手机号

        insert_rows.append((
            new_id,
            safe_str(d.get('申请人姓名'), 50) or f'租户{new_id}',
            id_card[:18],
            phone,
            map_enum(safe_str(d.get('性别')), GENDER_MAP),
            map_enum(safe_str(d.get('学历')), EDUCATION_MAP),
            map_enum(safe_str(d.get('婚姻情况')), MARRIAGE_MAP),
            safe_str(d.get('紧急联系人'), 50),
            safe_str(d.get('紧急联系人联系方式'), 20),
            safe_str(d.get('缴纳社保公司'), 100),
            100,  # 默认信用分
            '0',  # 非黑名单
            '0', '0', 'migration',
        ))

    count = batch_insert(cursor, 'hz_tenant', columns, insert_rows)
    stats['tenant'] = count
    log(f"租户导入完成: {count} 条 (跳过无效手机号: {skipped}), ID映射: {id_mapper.size('tenant')} 个")
    return count


# ============================================================
# 第6.5步: 从hz_tenant创建hz_user并关联
# ============================================================

def step6_5_create_users(cursor, filter_active=False):
    """从已导入的hz_tenant数据创建hz_user记录，并将id_mapper映射从tenant_id切换到user_id"""
    log("=" * 60)
    log("第6.5步: 创建用户数据 (hz_user) 并关联租户")
    log("=" * 60)

    # 1. 查询所有迁移的租户
    cursor.execute("""
        SELECT t.tenant_id, t.tenant_name, t.id_card, t.phone, t.gender,
               t.education, t.work_unit, t.create_time
        FROM hz_tenant t
        WHERE t.create_by = 'migration' AND t.del_flag = '0'
    """)
    tenants = cursor.fetchall()
    log(f"  查询到 {len(tenants)} 条迁移租户记录")

    # 2. 按手机号去重（同一手机号只创建一个hz_user）
    seen_phones = set()
    unique_tenants = []
    duplicate_count = 0
    for t in tenants:
        phone = t['phone']
        if phone in seen_phones:
            duplicate_count += 1
            continue
        seen_phones.add(phone)
        unique_tenants.append(t)
    log(f"  去重后 {len(unique_tenants)} 条 (重复手机号: {duplicate_count})")

    # 3. 创建hz_user记录（跳过手机号已存在的）
    next_id = get_next_auto_id(cursor, 'hz_user', 'user_id')
    columns = ['user_id', 'phone', 'source_type', 'nickname', 'real_name', 'gender',
               'id_card', 'education', 'work_unit', 'login_type', 'is_info_completed',
               'auth_status', 'status', 'del_flag', 'create_by', 'create_time', 'remark']

    phone_to_user_id = {}  # phone -> hz_user.user_id
    insert_rows = []
    skipped_existing = 0

    for t in unique_tenants:
        phone = t['phone']
        # 检查手机号是否已存在于hz_user
        cursor.execute("SELECT user_id FROM hz_user WHERE phone = %s", (phone,))
        existing = cursor.fetchone()
        if existing:
            phone_to_user_id[phone] = existing['user_id']
            skipped_existing += 1
            continue

        new_user_id = next_id + len(insert_rows)
        phone_to_user_id[phone] = new_user_id

        # 性别映射: hz_tenant '0'=男,'1'=女 -> hz_user '1'=男,'2'=女
        user_gender = '0'  # 未知
        if t['gender'] == '0':
            user_gender = '1'
        elif t['gender'] == '1':
            user_gender = '2'

        # 学历映射 -> hz_user char(1)
        user_education = map_enum(t['education'], EDUCATION_USER_MAP)

        insert_rows.append((
            new_user_id,
            phone,
            '3',  # source_type: 3=迁移导入
            t['tenant_name'],  # nickname
            t['tenant_name'],  # real_name
            user_gender,
            t['id_card'],
            user_education,
            t['work_unit'],
            'migration',  # login_type
            '1',  # is_info_completed（迁移数据已完善）
            '0',  # auth_status
            '0',  # status
            '0',  # del_flag
            'migration',
            t['create_time'] or datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'),
            '老系统数据迁移自动创建',
        ))

    count = batch_insert(cursor, 'hz_user', columns, insert_rows)
    log(f"  创建hz_user: {count} 条 (跳过已存在: {skipped_existing})")

    # 4. 更新hz_tenant.user_id = hz_user.user_id
    updated = 0
    for phone, user_id in phone_to_user_id.items():
        cursor.execute(
            "UPDATE hz_tenant SET user_id = %s WHERE phone = %s AND create_by = 'migration'",
            (user_id, phone)
        )
        updated += cursor.rowcount
    log(f"  更新hz_tenant.user_id: {updated} 条")

    # 5. 构建 tenant_id -> user_id 映射并更新id_mapper
    # 查询所有迁移租户的 tenant_id 和对应的 user_id
    cursor.execute("""
        SELECT t.tenant_id, u.user_id
        FROM hz_tenant t
        JOIN hz_user u ON t.phone = u.phone
        WHERE t.create_by = 'migration' AND t.user_id IS NOT NULL
    """)
    tenant_to_user = {}
    for row in cursor.fetchall():
        tenant_to_user[row['tenant_id']] = row['user_id']

    # 更新id_mapper中所有 'tenant', 'qualification', 'idcard' 映射的值
    # 从 hz_tenant.tenant_id 改为 hz_user.user_id
    remapped = 0
    for mapping_name in ('tenant', 'qualification', 'idcard'):
        if mapping_name in id_mapper.mappings:
            for old_key in list(id_mapper.mappings[mapping_name].keys()):
                old_tenant_id = id_mapper.mappings[mapping_name][old_key]
                if old_tenant_id in tenant_to_user:
                    id_mapper.mappings[mapping_name][old_key] = tenant_to_user[old_tenant_id]
                    remapped += 1

    stats['user'] = count
    log(f"  ID映射重映射: {remapped} 个 (tenant/qualification/idcard -> user_id)")
    log(f"用户创建完成: hz_user {count} 条, 映射 {len(tenant_to_user)} 个")
    return count


# ============================================================
# 第7步: 导入资格审核数据 (hz_qualification)
# ============================================================

def step7_import_qualifications(cursor, filter_active=False):
    """从人员.xlsx中提取资格信息导入hz_qualification"""
    log("=" * 60)
    log("第7步: 导入资格审核数据 (hz_qualification)")
    log("=" * 60)

    headers, data = read_excel(os.path.join(DATA_DIR, DATA_FILES['person']))
    if not data:
        return 0

    next_id = get_next_auto_id(cursor, 'hz_qualification', 'qualification_id')
    columns = ['qualification_id', 'tenant_id', 'apply_type', 'apply_time',
               'id_card', 'name', 'phone', 'education_level', 'marriage_status',
               'work_unit', 'final_result', 'status', 'del_flag', 'create_by']

    insert_rows = []
    for i, row in enumerate(data):
        d = make_row_dict(headers, row)
        old_id = safe_str(d.get('id'))
        new_tenant_id = id_mapper.get('tenant', old_id)
        if not new_tenant_id:
            continue

        id_card = safe_str(d.get('申请人证件号'))
        status = safe_str(d.get('当前人员状态'))
        # 最终审核结果映射
        if status == '符合资格':
            final_result = '1'  # 通过
        elif status == '不符合资格':
            final_result = '2'  # 不通过
        else:
            final_result = '0'  # 待审核

        insert_rows.append((
            next_id + i,
            new_tenant_id,
            map_enum(safe_str(d.get('申请人类型')), APPLY_TYPE_MAP, '1'),
            safe_datetime(d.get('创建时间')),
            id_card[:18] if id_card else None,
            safe_str(d.get('申请人姓名'), 50),
            safe_str(d.get('申请人手机号'), 20),
            map_enum(safe_str(d.get('学历')), EDUCATION_MAP),
            map_enum(safe_str(d.get('婚姻情况')), MARRIAGE_MAP),
            safe_str(d.get('缴纳社保公司'), 100),
            final_result,
            '2' if final_result == '1' else '0',  # status
            '0', 'migration',
        ))

    count = batch_insert(cursor, 'hz_qualification', columns, insert_rows)
    stats['qualification'] = count
    log(f"资格审核导入完成: {count} 条")
    return count


# ============================================================
# 第8步: 导入合同数据 (hz_contract) - 合并合同1和合同2
# ============================================================

def step8_import_contracts(cursor, filter_active=False):
    """合并合同1.xlsx和合同2.xlsx，导入hz_contract"""
    log("=" * 60)
    log("第8步: 导入合同数据 (hz_contract)")
    log("=" * 60)

    # 读取合同1
    h1, d1 = read_excel(os.path.join(DATA_DIR, DATA_FILES['contract1']))
    # 读取合同2
    h2, d2 = read_excel(os.path.join(DATA_DIR, DATA_FILES['contract2']))

    if not d1 and not d2:
        return 0

    # 合同2按档案标识建立索引
    contract2_by_archive = {}
    for row in d2:
        d = make_row_dict(h2, row)
        archive_id = safe_str(d.get('档案标识'))
        if archive_id:
            if archive_id not in contract2_by_archive:
                contract2_by_archive[archive_id] = []
            contract2_by_archive[archive_id].append(d)

    next_id = get_next_auto_id(cursor, 'hz_contract', 'contract_id')
    columns = ['contract_id', 'contract_no', 'contract_type', 'tenant_id', 'tenant_name',
               'tenant_id_card', 'tenant_phone', 'project_id', 'house_id', 'house_code',
               'rent_price', 'deposit', 'start_date', 'end_date', 'rent_months',
               'payment_cycle', 'sign_time', 'contract_status', 'del_flag', 'create_by',
               'create_time']

    insert_rows = []
    seq = 1
    filtered = 0
    for row in d1:
        d = make_row_dict(h1, row)
        old_id = safe_str(d.get('id'))
        archive_id = safe_str(d.get('档案标识'))
        qual_no = safe_str(d.get('资格证号'))
        house_old_id = safe_str(d.get('房间编号'))

        # 查找合同2的补充信息
        c2_list = contract2_by_archive.get(archive_id, [])
        c2 = c2_list[0] if c2_list else {}

        # 合同状态映射
        c1_status = safe_str(d.get('合同状态'))
        c2_status = safe_str(d.get('签订状态')) or safe_str(c2.get('签订状态'))
        contract_status = map_enum(c1_status or c2_status, CONTRACT_STATUS_MAP, '0')

        # 过滤已失效/已退租的历史合同
        if filter_active and contract_status in ('4', '5'):
            filtered += 1
            continue

        new_id = next_id + len(insert_rows)
        id_mapper.register('contract', old_id, new_id)
        # 建立档案标识映射
        if archive_id:
            id_mapper.register('archive', archive_id, new_id)

        # 租户ID映射(通过资格证号)
        new_tenant_id = id_mapper.get('qualification', qual_no)
        # 如果资格证号没匹配到，尝试用身份证号
        if not new_tenant_id:
            new_tenant_id = id_mapper.get('tenant', old_id)

        # 房源ID映射
        new_house_id = id_mapper.get('house', house_old_id)
        # 需要找到该项目ID
        new_project_id = None
        if new_house_id:
            # 从房源找项目ID
            cursor.execute("SELECT project_id FROM hz_house WHERE house_id = %s", (new_house_id,))
            result = cursor.fetchone()
            if result:
                new_project_id = result['project_id']

        # 租户信息(如果找不到租户ID，从数据中取)
        tenant_name = safe_str(d.get('申请人姓名')) or safe_str(c2.get('申请人姓名'), 50)
        tenant_id_card = None
        tenant_phone = None
        if new_tenant_id:
            cursor.execute("SELECT real_name as tenant_name, id_card, phone FROM hz_user WHERE user_id = %s", (new_tenant_id,))
            t = cursor.fetchone()
            if t:
                tenant_name = tenant_name or t['tenant_name']
                tenant_id_card = t['id_card']
                tenant_phone = t['phone']

        # 租金和押金
        rent_price = safe_float(d.get('当前租金')) or safe_float(c2.get('租金标准'))
        deposit = safe_float(d.get('已缴押金'))

        # 日期
        start_date = safe_date(d.get('开始日期'))
        end_date = safe_date(d.get('结束日期'))
        rent_months = 0
        if start_date and end_date:
            try:
                sd = datetime.datetime.strptime(start_date, '%Y-%m-%d')
                ed = datetime.datetime.strptime(end_date, '%Y-%m-%d')
                rent_months = (ed.year - sd.year) * 12 + (ed.month - sd.month)
            except:
                pass

        # 签订时间
        sign_time = safe_datetime(d.get('签订时间')) or safe_datetime(c2.get('签订时间'))

        # 合同编号
        contract_no = generate_contract_no(start_date or '2023-01-01', seq)
        seq += 1

        # 合同类型
        contract_type = safe_str(c2.get('合同类型'))
        if contract_type == '退租':
            contract_type = '1'  # 退租合同也算人才公寓类型
        elif contract_type in ('1', '2', '3'):
            pass
        else:
            contract_type = '1'

        insert_rows.append((
            new_id,
            contract_no,
            contract_type,
            new_tenant_id,
            tenant_name,
            tenant_id_card,
            tenant_phone,
            new_project_id,
            new_house_id,
            house_old_id,  # house_code
            rent_price,
            deposit,
            start_date,
            end_date,
            rent_months,
            '1',  # 默认月付
            sign_time,
            contract_status,
            '0', 'migration',
            safe_datetime(d.get('创建时间')),  # create_time
        ))

    count = batch_insert(cursor, 'hz_contract', columns, insert_rows)
    stats['contract'] = count
    log(f"合同导入完成: {count} 条 (过滤: {filtered}), ID映射: {id_mapper.size('contract')} 个, 档案映射: {id_mapper.size('archive')} 个")
    return count


# ============================================================
# 第9步: 导入账单数据 (hz_bill)
# ============================================================

def step9_import_bills(cursor, filter_active=False):
    """从账单.xlsx导入hz_bill"""
    log("=" * 60)
    log("第9步: 导入账单数据 (hz_bill)")
    log("=" * 60)

    headers, data = read_excel(os.path.join(DATA_DIR, DATA_FILES['bill']))
    if not data:
        return 0

    next_id = get_next_auto_id(cursor, 'hz_bill', 'bill_id')
    columns = ['bill_id', 'bill_no', 'contract_id', 'tenant_id', 'tenant_name',
               'house_id', 'house_code', 'bill_type', 'bill_period',
               'bill_amount', 'paid_amount', 'unpaid_amount',
               'bill_date', 'due_date', 'bill_status', 'pay_time', 'pay_method',
               'del_flag', 'create_by', 'create_time']

    insert_rows = []
    filtered = 0
    for i, row in enumerate(data):
        d = make_row_dict(headers, row)
        archive_id = safe_str(d.get('档案标识'))
        qual_no = safe_str(d.get('资格证号'))
        bill_status_str = safe_str(d.get('账单状态'))
        bill_type_str = safe_str(d.get('账单类型'))

        bill_status = map_enum(bill_status_str, BILL_STATUS_MAP, '0')
        bill_type = map_enum(bill_type_str, BILL_TYPE_MAP, '7')

        # 过滤已结清的历史账单
        if filter_active and bill_status == '1':
            filtered += 1
            continue

        new_id = next_id + len(insert_rows)
        old_id = safe_str(d.get('id'))
        id_mapper.register('bill', old_id, new_id)

        # 映射外键
        new_contract_id = id_mapper.get('archive', archive_id)
        new_tenant_id = id_mapper.get('qualification', qual_no)
        new_house_id = None

        # 租户名称
        tenant_name = None
        if new_tenant_id:
            cursor.execute("SELECT real_name as tenant_name FROM hz_user WHERE user_id = %s", (new_tenant_id,))
            t = cursor.fetchone()
            if t:
                tenant_name = t['tenant_name']

        # 从合同获取房源信息
        house_code = safe_str(d.get('合同编号'))
        if new_contract_id:
            cursor.execute("SELECT house_id, house_code FROM hz_contract WHERE contract_id = %s", (new_contract_id,))
            c = cursor.fetchone()
            if c:
                new_house_id = c['house_id']
                house_code = c['house_code']

        # 金额
        bill_amount = safe_float(d.get('应收金额')) or safe_float(d.get('租金'))
        paid_amount = safe_float(d.get('实收金额'))
        unpaid_amount = bill_amount - (paid_amount or 0) if bill_amount else 0

        # 日期
        bill_date = safe_date(d.get('账单开始时间'))
        due_date = safe_date(d.get('账单结束时间'))
        pay_time = safe_datetime(d.get('支付时间'))

        # 账单周期
        bill_period = None
        if bill_date:
            bill_period = bill_date[:7]  # YYYY-MM

        # 生成账单编号
        bill_no = generate_bill_no(bill_type, bill_date or '2023-01-01', len(insert_rows) + 1)

        insert_rows.append((
            new_id,
            bill_no,
            new_contract_id,
            new_tenant_id,
            tenant_name,
            new_house_id,
            house_code,
            bill_type,
            bill_period,
            bill_amount,
            paid_amount,
            unpaid_amount,
            bill_date,
            due_date,
            bill_status,
            pay_time,
            map_enum(safe_str(d.get('支付方式')), PAYMENT_METHOD_MAP),
            '0', 'migration',
            safe_datetime(d.get('创建时间')),  # create_time
        ))

    count = batch_insert(cursor, 'hz_bill', columns, insert_rows, batch_size=1000)
    stats['bill'] = count
    log(f"账单导入完成: {count} 条 (过滤: {filtered})")
    return count


# ============================================================
# 第10步: 导入缴费记录 (hz_payment)
# ============================================================

def step10_import_payments(cursor, filter_active=False):
    """从缴费记录.xlsx导入hz_payment"""
    log("=" * 60)
    log("第10步: 导入缴费记录 (hz_payment)")
    log("=" * 60)

    headers, data = read_excel(os.path.join(DATA_DIR, DATA_FILES['payment']))
    if not data:
        return 0

    next_id = get_next_auto_id(cursor, 'hz_payment', 'payment_id')
    columns = ['payment_id', 'payment_no', 'bill_id', 'bill_no',
               'tenant_id', 'tenant_name', 'payment_amount', 'payment_method',
               'transaction_no', 'payment_time', 'payment_status', 'del_flag', 'create_by',
               'create_time']

    insert_rows = []
    for i, row in enumerate(data):
        d = make_row_dict(headers, row)
        archive_id = safe_str(d.get('合同档案标识'))
        old_bill_id = safe_str(d.get('账单id'))
        account_type = safe_str(d.get('支付账单类型'))

        # 跳过退费记录（老系统退费记录与缴费混在一起）
        if account_type == '退费':
            continue

        new_id = next_id + len(insert_rows)

        # 映射外键 - 缴费记录的账单id是老系统的雪花ID，需要通过 档案标识+账单类型+费用 匹配
        new_bill_id = None
        bill_no = None
        new_tenant_id = None
        tenant_name = None

        # 先尝试通过老账单id的映射
        new_bill_id = id_mapper.get('bill', old_bill_id)

        if not new_bill_id and archive_id:
            # 通过档案标识在新系统账单中查找匹配的账单
            new_contract_id = id_mapper.get('archive', archive_id)
            if new_contract_id:
                # 从合同获取租户
                cursor.execute("SELECT tenant_id, tenant_name FROM hz_contract WHERE contract_id = %s", (new_contract_id,))
                c = cursor.fetchone()
                if c:
                    new_tenant_id = c['tenant_id']
                    tenant_name = c['tenant_name']

                # 按金额和类型匹配账单
                payment_amount = safe_float(d.get('费用'))
                bill_type_str = safe_str(d.get('账单类型'))
                bill_type_code = map_enum(bill_type_str, BILL_TYPE_MAP, '7')
                cursor.execute(
                    "SELECT bill_id, bill_no, tenant_id, tenant_name FROM hz_bill WHERE contract_id = %s AND bill_type = %s AND bill_amount = %s AND create_by='migration' LIMIT 1",
                    (new_contract_id, bill_type_code, payment_amount)
                )
                b = cursor.fetchone()
                if b:
                    new_bill_id = b['bill_id']
                    bill_no = b['bill_no']
                    new_tenant_id = b['tenant_id']
                    tenant_name = b['tenant_name']
        elif new_bill_id:
            cursor.execute("SELECT tenant_id, tenant_name, bill_no FROM hz_bill WHERE bill_id = %s", (new_bill_id,))
            b = cursor.fetchone()
            if b:
                new_tenant_id = b['tenant_id']
                tenant_name = b['tenant_name']
                bill_no = b['bill_no']

        # 如果没有匹配到账单，跳过此缴费记录
        if not new_bill_id:
            continue

        # 金额
        payment_amount = safe_float(d.get('费用')) or safe_float(d.get('流水金额'))

        # 缴费时间
        payment_time = safe_datetime(d.get('支付时间'))

        # 账单状态判断
        bill_status = safe_str(d.get('账单状态'))
        payment_status = '1' if bill_status == '已支付' else '0'

        # 交易流水号
        transaction_no = safe_str(d.get('流水编号'), 100)

        # 生成缴费编号
        payment_no = generate_payment_no(len(insert_rows) + 1)

        insert_rows.append((
            new_id,
            payment_no,
            new_bill_id,
            bill_no,  # bill_no 从账单表获取
            new_tenant_id,
            tenant_name,
            payment_amount,
            map_enum(safe_str(d.get('支付方式')), PAYMENT_METHOD_MAP),
            transaction_no,
            payment_time,
            payment_status,
            '0', 'migration',
            safe_datetime(d.get('创建时间')),  # create_time
        ))

    count = batch_insert(cursor, 'hz_payment', columns, insert_rows, batch_size=1000)
    stats['payment'] = count
    log(f"缴费记录导入完成: {count} 条")
    return count


# ============================================================
# 第11步: 导入入住/退租记录 (hz_checkin_record + hz_checkout_record)
# ============================================================

def step11_import_checkin_checkout(cursor, filter_active=False):
    """从入退.xlsx导入入住记录和退租记录"""
    log("=" * 60)
    log("第11步: 导入入住/退租记录")
    log("=" * 60)

    headers, data = read_excel(os.path.join(DATA_DIR, DATA_FILES['checkin_checkout']))
    if not data:
        return 0

    # --- 入住记录 ---
    checkin_next_id = get_next_auto_id(cursor, 'hz_checkin_record', 'record_id')
    checkin_columns = ['record_id', 'checkin_no', 'apply_id', 'contract_id', 'tenant_id',
                       'house_id', 'checkin_date', 'checkin_time', 'manager_name',
                       'status', 'del_flag', 'create_by', 'create_time']

    # --- 退租记录 ---
    checkout_next_id = get_next_auto_id(cursor, 'hz_checkout_record', 'record_id')
    checkout_columns = ['record_id', 'apply_id', 'contract_id', 'tenant_id', 'house_id',
                        'checkout_date', 'checkout_time', 'manager_name',
                        'del_flag', 'create_by', 'create_time']

    checkin_rows = []
    checkout_rows = []
    checkin_seq = 1
    filtered = 0
    skipped_no_contract = 0

    for row in data:
        d = make_row_dict(headers, row)
        archive_id = safe_str(d.get('档案标识'))
        qual_no = safe_str(d.get('资格证号'))
        house_old_id = safe_str(d.get('房间Id'))
        process_status = safe_str(d.get('入住流程状态'))

        # 过滤已作废的
        if filter_active and process_status == '已作废':
            filtered += 1
            continue

        # 映射外键 - 优先用档案标识找合同，如果找不到跳过
        new_contract_id = id_mapper.get('archive', archive_id)
        if not new_contract_id:
            # 合同可能在 filter-active 模式下被过滤掉了，跳过这条记录
            skipped_no_contract += 1
            continue

        new_tenant_id = id_mapper.get('qualification', qual_no)
        new_house_id = id_mapper.get('house', house_old_id)

        # 入住记录
        checkin_date = safe_date(d.get('入住点验时间'))
        checkin_time = safe_datetime(d.get('入住签字时间')) or safe_datetime(d.get('入住点验时间'))
        manager_name = safe_str(d.get('经办人姓名'), 50)
        checkin_status = map_enum(process_status, CHECKIN_STATUS_MAP, '0')

        if checkin_date:
            checkin_rows.append((
                checkin_next_id + len(checkin_rows),
                generate_checkin_no(checkin_date, checkin_seq),
                0,  # apply_id
                new_contract_id,
                new_tenant_id,
                new_house_id,
                checkin_date,
                checkin_time,
                manager_name,
                checkin_status,
                '0', 'migration',
                safe_datetime(d.get('创建时间')),  # create_time
            ))

            checkin_seq += 1

        # 退租记录
        checkout_date = safe_date(d.get('退租时间'))
        if checkout_date and process_status not in ('已作废',):
            checkout_time = safe_datetime(d.get('退租签字时间')) or safe_datetime(d.get('退租时间'))
            checkout_rows.append((
                checkout_next_id + len(checkout_rows),
                0,  # apply_id
                new_contract_id,
                new_tenant_id,
                new_house_id,
                checkout_date,
                checkout_time,
                manager_name,
                '0', 'migration',
                safe_datetime(d.get('创建时间')),  # create_time
            ))

    checkin_count = batch_insert(cursor, 'hz_checkin_record', checkin_columns, checkin_rows)
    checkout_count = batch_insert(cursor, 'hz_checkout_record', checkout_columns, checkout_rows)
    stats['checkin_record'] = checkin_count
    stats['checkout_record'] = checkout_count
    log(f"入住记录导入: {checkin_count} 条, 退租记录导入: {checkout_count} 条 (过滤: {filtered}, 无合同跳过: {skipped_no_contract})")
    return checkin_count + checkout_count


# ============================================================
# 第12步: 导入同住人数据 (hz_co_tenant)
# ============================================================

def step12_import_co_tenants(cursor, filter_active=False):
    """从同住人.xlsx导入hz_co_tenant"""
    log("=" * 60)
    log("第12步: 导入同住人数据 (hz_co_tenant)")
    log("=" * 60)

    headers, data = read_excel(os.path.join(DATA_DIR, DATA_FILES['co_tenant']))
    if not data:
        return 0

    next_id = get_next_auto_id(cursor, 'hz_co_tenant', 'co_tenant_id')
    columns = ['co_tenant_id', 'contract_id', 'tenant_name', 'id_card', 'phone',
               'relationship', 'status', 'del_flag', 'create_by']

    insert_rows = []
    skipped = 0
    for i, row in enumerate(data):
        d = make_row_dict(headers, row)
        archive_id = safe_str(d.get('档案标识'))
        new_contract_id = id_mapper.get('archive', archive_id)
        if not new_contract_id:
            skipped += 1
            continue

        insert_rows.append((
            next_id + i,
            new_contract_id,
            safe_str(d.get('姓名'), 50),
            safe_str(d.get('身份证号码'), 18),
            safe_str(d.get('手机号码'), 20),
            '其他',  # 默认关系，老数据无此字段
            '0', '0', 'migration',
        ))

    count = batch_insert(cursor, 'hz_co_tenant', columns, insert_rows)
    stats['co_tenant'] = count
    log(f"同住人导入完成: {count} 条 (跳过无合同: {skipped})")
    return count


# ============================================================
# 第13步: 导入换房记录 (hz_house_exchange)
# ============================================================

def step13_import_exchanges(cursor, filter_active=False):
    """从调换房.xlsx导入hz_house_exchange"""
    log("=" * 60)
    log("第13步: 导入换房记录 (hz_house_exchange)")
    log("=" * 60)

    headers, data = read_excel(os.path.join(DATA_DIR, DATA_FILES['exchange']))
    if not data:
        return 0

    next_id = get_next_auto_id(cursor, 'hz_house_exchange', 'exchange_id')
    columns = ['exchange_id', 'tenant_id', 'old_contract_id', 'old_house_id', 'old_house_code',
               'new_house_id', 'new_house_code', 'exchange_reason',
               'apply_time', 'approve_result', 'del_flag', 'create_by', 'create_time']

    insert_rows = []
    for i, row in enumerate(data):
        d = make_row_dict(headers, row)
        qual_no = safe_str(d.get('资格证号'))
        archive_id = safe_str(d.get('档案标识'))

        new_tenant_id = id_mapper.get('qualification', qual_no)
        new_contract_id = id_mapper.get('archive', archive_id)
        old_house_old_id = safe_str(d.get('原房间编号'))
        new_house_old_id = safe_str(d.get('目标房间编号'))

        new_contract_id = id_mapper.get('archive', archive_id) or 0  # 默认0如果找不到

        approve_result = '0'
        if safe_str(d.get('审批状态')) == '同意':
            approve_result = '1'

        insert_rows.append((
            next_id + i,
            new_tenant_id,
            new_contract_id,  # old_contract_id
            id_mapper.get('house', old_house_old_id),
            old_house_old_id,
            id_mapper.get('house', new_house_old_id),
            new_house_old_id,
            safe_str(d.get('换房原因'), 500),
            safe_datetime(d.get('创建时间')),
            approve_result,
            '0', 'migration',
            safe_datetime(d.get('创建时间')),  # create_time
        ))

    count = batch_insert(cursor, 'hz_house_exchange', columns, insert_rows)
    stats['house_exchange'] = count
    log(f"换房记录导入完成: {count} 条")
    return count


# ============================================================
# 第14步: 导入黑名单 (hz_blacklist)
# ============================================================

def step14_import_blacklist(cursor, filter_active=False):
    """从黑名单.xls导入hz_blacklist"""
    log("=" * 60)
    log("第14步: 导入黑名单数据 (hz_blacklist)")
    log("=" * 60)

    headers, data = read_excel(os.path.join(DATA_DIR, DATA_FILES['blacklist']))
    if not data:
        return 0

    next_id = get_next_auto_id(cursor, 'hz_blacklist', 'blacklist_id')
    columns = ['blacklist_id', 'tenant_id', 'id_card', 'tenant_name',
               'blacklist_reason', 'blacklist_time', 'status', 'del_flag', 'create_by']

    insert_rows = []
    for i, row in enumerate(data):
        d = make_row_dict(headers, row)
        id_card = safe_str(d.get('身份证号'), 18)
        # 通过身份证找tenant_id
        new_tenant_id = id_mapper.get('idcard', id_card)

        insert_rows.append((
            next_id + i,
            new_tenant_id,
            id_card,
            safe_str(d.get('姓名'), 50),
            safe_str(d.get('加入原因'), 500) or '老系统导入',
            safe_datetime(d.get('创建时间')),
            '1',  # 生效中
            '0', 'migration',
        ))

    count = batch_insert(cursor, 'hz_blacklist', columns, insert_rows)
    stats['blacklist'] = count
    log(f"黑名单导入完成: {count} 条")
    return count


# ============================================================
# 第15步: 数据修正（房源状态 + 合同签署状态）
# ============================================================

def step15_post_migration(cursor, filter_active=False):
    """迁移后数据修正：合同状态、房源状态、退租申请、退款等"""
    log("=" * 60)
    log("第15步: 数据修正（合同状态 + 房源状态 + 退租申请 + 退款）")
    log("=" * 60)

    # 1. 修正迁移合同状态：filter-active模式下保留的合同应标记为履行中
    log("  修正合同状态...")
    cursor.execute("""
        UPDATE hz_contract
        SET contract_status = '3'
        WHERE create_by = 'migration' AND contract_status = '0' AND del_flag = '0'
    """)
    active_contract_count = cursor.rowcount
    log(f"  合同标记为履行中: {active_contract_count} 条")

    # 2. 有退租记录的合同标记为已解约(5)
    log("  根据退租记录修正合同状态...")
    cursor.execute("""
        UPDATE hz_contract c
        JOIN hz_checkout_record cr ON cr.contract_id = c.contract_id
        SET c.contract_status = '5'
        WHERE cr.create_by = 'migration' AND c.contract_status IN ('3', '4') AND c.del_flag = '0'
    """)
    terminated_count = cursor.rowcount
    log(f"  合同标记为已解约: {terminated_count} 条")

    # 3. 根据有效合同更新房源状态
    log("  修正房源状态...")
    # 有履行中合同的房源标记为已出租
    cursor.execute("""
        UPDATE hz_house h
        JOIN hz_contract c ON h.house_id = c.house_id
        SET h.house_status = '1'
        WHERE c.contract_status IN ('1','2','3') AND c.del_flag = '0' AND h.create_by = 'migration'
    """)
    rented_count = cursor.rowcount
    log(f"  标记已出租: {rented_count} 条")

    # 无有效合同的房源标记为空置
    cursor.execute("""
        UPDATE hz_house h
        SET h.house_status = '0'
        WHERE h.create_by = 'migration' AND h.del_flag = '0'
          AND h.house_id NOT IN (
              SELECT c.house_id FROM hz_contract c
              WHERE c.contract_status IN ('1','2','3') AND c.del_flag = '0' AND c.house_id IS NOT NULL
          )
    """)
    vacant_count = cursor.rowcount
    log(f"  标记空置: {vacant_count} 条")

    # 4. 为迁移合同创建签署记录（老合同为线下签署）
    log("  创建合同签署记录...")
    cursor.execute("""
        SELECT contract_id, tenant_id, tenant_name, sign_time
        FROM hz_contract WHERE create_by = 'migration' AND del_flag = '0'
    """)
    contracts = cursor.fetchall()
    if contracts:
        sig_next_id = get_next_auto_id(cursor, 'hz_contract_signature', 'signature_id')
        sig_columns = ['signature_id', 'contract_id', 'signer_type', 'signer_id', 'signer_name',
                       'status', 'del_flag', 'create_by', 'create_time']
        sig_rows = []
        for i, c in enumerate(contracts):
            sig_rows.append((
                sig_next_id + i,
                c['contract_id'],
                '1',  # signer_type: 1=租户
                c['tenant_id'],
                c['tenant_name'],
                '1',  # status: 1=已签署
                '0', 'migration',
                c['sign_time'] or datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'),
            ))
        signed_count = batch_insert(cursor, 'hz_contract_signature', sig_columns, sig_rows)
    else:
        signed_count = 0
    log(f"  创建签署记录: {signed_count} 条")

    # 5. 创建退租申请记录(hz_checkout_apply)
    # 退款页面查询的是 hz_checkout_apply 中 apply_status='5' 且 refund_amount > 0 的记录
    log("  创建退租申请记录...")
    cursor.execute("""
        SELECT cr.contract_id, cr.tenant_id, cr.house_id, cr.checkout_date, cr.checkout_time
        FROM hz_checkout_record cr
        WHERE cr.create_by = 'migration'
          AND cr.contract_id NOT IN (
              SELECT contract_id FROM hz_checkout_apply WHERE del_flag = '0'
          )
    """)
    checkout_records = cursor.fetchall()
    if checkout_records:
        # 获取合同押金
        cursor.execute("SELECT contract_id, deposit FROM hz_contract WHERE create_by = 'migration'")
        contract_deposits = {r['contract_id']: r['deposit'] for r in cursor.fetchall()}

        apply_next_id = get_next_auto_id(cursor, 'hz_checkout_apply', 'apply_id')
        apply_columns = ['apply_id', 'contract_id', 'tenant_id', 'house_id',
                        'apply_time', 'plan_checkout_date', 'checkout_reason',
                        'is_early_termination', 'penalty_amount', 'unpaid_bills', 'deposit_refund',
                        'apply_status', 'approve_by', 'approve_time',
                        'refund_amount', 'create_by', 'create_time', 'del_flag']
        apply_rows = []
        for i, cr in enumerate(checkout_records):
            deposit = contract_deposits.get(cr['contract_id'], 0) or 0
            checkout_time = cr['checkout_time'] or cr['checkout_date']
            apply_rows.append((
                apply_next_id + i,
                cr['contract_id'],
                cr['tenant_id'],
                cr['house_id'],
                checkout_time,  # apply_time
                cr['checkout_date'],  # plan_checkout_date
                '老系统迁移-退租',
                '0',  # is_early_termination
                0, 0,  # penalty_amount, unpaid_bills
                deposit,  # deposit_refund
                '5',  # apply_status = 已完成
                'migration',
                checkout_time,  # approve_time
                deposit,  # refund_amount
                'migration',
                checkout_time,  # create_time
                '0',
            ))
        apply_count = batch_insert(cursor, 'hz_checkout_apply', apply_columns, apply_rows)
    else:
        apply_count = 0
    log(f"  创建退租申请: {apply_count} 条")

    # 6. 更新退租记录中的退款金额和状态
    log("  更新退租记录退款信息...")
    cursor.execute("""
        UPDATE hz_checkout_record cr
        JOIN hz_contract c ON cr.contract_id = c.contract_id
        SET cr.deposit_refund = c.deposit
        WHERE cr.create_by = 'migration' AND (cr.deposit_refund IS NULL OR cr.deposit_refund = 0)
    """)
    refund_update_count = cursor.rowcount
    log(f"  更新退款金额: {refund_update_count} 条")

    cursor.execute("""
        UPDATE hz_checkout_record cr
        JOIN hz_checkout_apply ca ON cr.contract_id = ca.contract_id
        SET cr.refund_status = '1'
        WHERE cr.create_by = 'migration' AND ca.create_by = 'migration' AND ca.apply_status = '5'
          AND cr.refund_status = '0'
    """)
    refund_status_count = cursor.rowcount
    log(f"  更新退款状态为已退还: {refund_status_count} 条")

    # 7. 更新项目统计信息
    log("  更新项目统计...")
    cursor.execute("""
        UPDATE hz_project p
        SET total_buildings = (SELECT COUNT(*) FROM hz_building b WHERE b.project_id = p.project_id AND b.del_flag = '0'),
            total_houses = (SELECT COUNT(*) FROM hz_house h WHERE h.project_id = p.project_id AND h.del_flag = '0'),
            available_houses = (SELECT COUNT(*) FROM hz_house h WHERE h.project_id = p.project_id AND h.house_status = '0' AND h.del_flag = '0')
        WHERE p.create_by = 'migration'
    """)
    project_count = cursor.rowcount
    log(f"  更新项目统计: {project_count} 条")

    log(f"数据修正完成: 履行中{active_contract_count}, 已解约{terminated_count}, 已出租{rented_count}, 空置{vacant_count}, 签署{signed_count}, 退租申请{apply_count}, 退款{refund_update_count}, 项目{project_count}")
    return 1


# ============================================================
# 清理迁移数据
# ============================================================

def clean_migration_data(cursor):
    """清理所有迁移数据（create_by='migration'的记录）"""
    log("=" * 60)
    log("清理迁移数据")
    log("=" * 60)

    tables = [
        'hz_blacklist', 'hz_house_exchange', 'hz_co_tenant',
        'hz_checkout_record', 'hz_checkin_record',
        'hz_payment', 'hz_bill', 'hz_contract',
        'hz_qualification', 'hz_tenant', 'hz_user',
        'hz_house', 'hz_house_type', 'hz_unit', 'hz_building', 'hz_project',
    ]

    for table in tables:
        try:
            cursor.execute(f"DELETE FROM `{table}` WHERE create_by = 'migration'")
            deleted = cursor.rowcount
            if deleted > 0:
                log(f"  清理 {table}: {deleted} 条")
        except Error as e:
            log(f"  清理 {table} 失败: {e}", 'ERROR')

    # 重置自增ID（可选）
    log("  清理完成")


# ============================================================
# 数据验证
# ============================================================

def validate_data(cursor):
    """验证导入数据的完整性"""
    log("=" * 60)
    log("数据验证")
    log("=" * 60)

    checks = [
        ("项目数", "SELECT COUNT(*) as cnt FROM hz_project WHERE create_by='migration'"),
        ("楼栋数", "SELECT COUNT(*) as cnt FROM hz_building WHERE create_by='migration'"),
        ("单元数", "SELECT COUNT(*) as cnt FROM hz_unit WHERE create_by='migration'"),
        ("户型数", "SELECT COUNT(*) as cnt FROM hz_house_type WHERE create_by='migration'"),
        ("房源数", "SELECT COUNT(*) as cnt FROM hz_house WHERE create_by='migration'"),
        ("租户数", "SELECT COUNT(*) as cnt FROM hz_tenant WHERE create_by='migration'"),
        ("用户数", "SELECT COUNT(*) as cnt FROM hz_user WHERE create_by='migration'"),
        ("租户关联用户", "SELECT COUNT(*) as cnt FROM hz_tenant WHERE create_by='migration' AND user_id IS NOT NULL"),
        ("资格审核数", "SELECT COUNT(*) as cnt FROM hz_qualification WHERE create_by='migration'"),
        ("合同数", "SELECT COUNT(*) as cnt FROM hz_contract WHERE create_by='migration'"),
        ("账单数", "SELECT COUNT(*) as cnt FROM hz_bill WHERE create_by='migration'"),
        ("缴费记录数", "SELECT COUNT(*) as cnt FROM hz_payment WHERE create_by='migration'"),
        ("入住记录数", "SELECT COUNT(*) as cnt FROM hz_checkin_record WHERE create_by='migration'"),
        ("退租记录数", "SELECT COUNT(*) as cnt FROM hz_checkout_record WHERE create_by='migration'"),
        ("同住人数", "SELECT COUNT(*) as cnt FROM hz_co_tenant WHERE create_by='migration'"),
        ("换房记录数", "SELECT COUNT(*) as cnt FROM hz_house_exchange WHERE create_by='migration'"),
        ("黑名单数", "SELECT COUNT(*) as cnt FROM hz_blacklist WHERE create_by='migration'"),
    ]

    # 检查外键完整性
    fk_checks = [
        ("合同-用户外键", "SELECT COUNT(*) as cnt FROM hz_contract c LEFT JOIN hz_user u ON c.tenant_id=u.user_id WHERE c.create_by='migration' AND c.tenant_id IS NOT NULL AND u.user_id IS NULL"),
        ("合同-房源外键", "SELECT COUNT(*) as cnt FROM hz_contract c LEFT JOIN hz_house h ON c.house_id=h.house_id WHERE c.create_by='migration' AND c.house_id IS NOT NULL AND h.house_id IS NULL"),
        ("账单-合同外键", "SELECT COUNT(*) as cnt FROM hz_bill b LEFT JOIN hz_contract c ON b.contract_id=c.contract_id WHERE b.create_by='migration' AND b.contract_id IS NOT NULL AND c.contract_id IS NULL"),
        ("账单-用户外键", "SELECT COUNT(*) as cnt FROM hz_bill b LEFT JOIN hz_user u ON b.tenant_id=u.user_id WHERE b.create_by='migration' AND b.tenant_id IS NOT NULL AND u.user_id IS NULL"),
        ("房源-项目外键", "SELECT COUNT(*) as cnt FROM hz_house h LEFT JOIN hz_project p ON h.project_id=p.project_id WHERE h.create_by='migration' AND h.project_id IS NOT NULL AND p.project_id IS NULL"),
        ("租户-用户关联", "SELECT COUNT(*) as cnt FROM hz_tenant t LEFT JOIN hz_user u ON t.user_id=u.user_id WHERE t.create_by='migration' AND t.user_id IS NOT NULL AND u.user_id IS NULL"),
    ]

    all_ok = True
    for name, sql in checks:
        try:
            cursor.execute(sql)
            result = cursor.fetchone()
            cnt = result['cnt']
            log(f"  {name}: {cnt}")
        except Error as e:
            log(f"  {name}: 查询失败 - {e}", 'ERROR')
            all_ok = False

    log("")
    log("外键完整性检查:")
    for name, sql in fk_checks:
        try:
            cursor.execute(sql)
            result = cursor.fetchone()
            cnt = result['cnt']
            if cnt > 0:
                log(f"  {name}: {cnt} 条记录外键缺失!", 'WARN')
                all_ok = False
            else:
                log(f"  {name}: OK")
        except Error as e:
            log(f"  {name}: 查询失败 - {e}", 'ERROR')
            all_ok = False

    return all_ok


# ============================================================
# 主函数
# ============================================================

STEPS = [
    (1, "导入项目数据", step1_import_projects),
    (2, "导入楼栋数据", step2_import_buildings),
    (3, "导入单元数据", step3_import_units),
    (4, "导入户型数据", step4_import_house_types),
    (5, "导入房源数据", step5_import_houses),
    (6, "导入租户数据", step6_import_tenants),
    ('6.5', "创建用户数据并关联", step6_5_create_users),
    (7, "导入资格审核数据", step7_import_qualifications),
    (8, "导入合同数据", step8_import_contracts),
    (9, "导入账单数据", step9_import_bills),
    (10, "导入缴费记录", step10_import_payments),
    (11, "导入入住/退租记录", step11_import_checkin_checkout),
    (12, "导入同住人数据", step12_import_co_tenants),
    (13, "导入换房记录", step13_import_exchanges),
    (14, "导入黑名单数据", step14_import_blacklist),
    (15, "数据修正(合同状态+房源状态+退租申请+退款)", step15_post_migration),
]


def main():
    parser = argparse.ArgumentParser(description='港好住老系统数据迁移脚本')
    parser.add_argument('--dry-run', action='store_true', help='仅输出SQL，不执行')
    parser.add_argument('--filter-active', action='store_true', help='只导入活跃数据(过滤已退租/已失效)')
    parser.add_argument('--step', type=int, help='只执行指定步骤')
    parser.add_argument('--from-step', type=int, help='从指定步骤开始执行')
    parser.add_argument('--clean', action='store_true', help='清理所有迁移数据后重新执行')
    parser.add_argument('--resume', action='store_true', help='从断点恢复(加载ID映射)')
    args = parser.parse_args()

    log("=" * 60)
    log("港好住老系统数据迁移")
    log(f"数据库: {DB_CONFIG['host']}:{DB_CONFIG['port']}/{DB_CONFIG['database']}")
    log(f"数据目录: {DATA_DIR}")
    log(f"过滤模式: {'是' if args.filter_active else '否(全量导入)'}")
    log(f"Dry Run: {'是' if args.dry_run else '否'}")
    log("=" * 60)

    # 断点恢复
    mapping_file = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'id_mappings.json')
    if args.resume:
        if id_mapper.load_from_file(mapping_file):
            log(f"从断点恢复，已加载 {sum(len(v) for v in id_mapper.mappings.values())} 个ID映射")
        else:
            log("未找到映射文件，从头开始", 'WARN')

    # 连接数据库
    if args.dry_run:
        log("Dry Run 模式，不连接数据库")
        conn = None
        cursor = None
    else:
        try:
            conn = pymysql.connect(**DB_CONFIG)
            cursor = conn.cursor()
            log("数据库连接成功")
        except Error as e:
            log(f"数据库连接失败: {e}", 'ERROR')
            sys.exit(1)

    # 清理旧数据
    if args.clean and cursor:
        clean_migration_data(cursor)
        conn.commit()
        # 重置id_mapper
        id_mapper.mappings = {}
        log("旧数据清理完成，开始重新迁移")

    # 执行迁移步骤
    start_time = datetime.datetime.now()
    try:
        for step_num, step_name, step_func in STEPS:
            if args.step and str(args.step) != str(step_num):
                continue
            if args.from_step and str(step_num) == '6.5' and args.from_step <= 6:
                pass  # 6.5步在6之后执行
            elif args.from_step and float(str(step_num).replace('6.5', '6.5')) < args.from_step:
                continue

            log(f"\n执行步骤 {step_num}: {step_name}")
            step_start = datetime.datetime.now()
            try:
                if cursor:
                    count = step_func(cursor, filter_active=args.filter_active)
                    conn.commit()
                else:
                    count = step_func(type('MockCursor', (), {
                        'execute': lambda self, *a, **kw: None,
                        'fetchone': lambda self, *a, **kw: {'max_id': 1, 'tenant_name': None, 'real_name': None, 'id_card': None, 'phone': None, 'house_id': None, 'house_code': None, 'project_id': None, 'tenant_id': None, 'user_id': None, 'house_type_name': None},
                        'fetchall': lambda self, *a, **kw: [],
                        'executemany': lambda self, *a, **kw: None,
                        'rowcount': 0,
                    })(), filter_active=args.filter_active)

                elapsed = (datetime.datetime.now() - step_start).total_seconds()
                log(f"步骤 {step_num} 完成，耗时 {elapsed:.1f}秒")
            except Exception as e:
                log(f"步骤 {step_num} 失败: {e}", 'ERROR')
                traceback.print_exc()
                if conn:
                    conn.rollback()
                break

        # 数据验证
        if cursor and not args.step:
            log("")
            validate_data(cursor)

    finally:
        # 保存映射
        id_mapper.save_to_file(mapping_file)
        log(f"\nID映射已保存到: {mapping_file}")

        if conn:
            conn.close()
            log("数据库连接已关闭")

    # 打印统计
    elapsed_total = (datetime.datetime.now() - start_time).total_seconds()
    log("\n" + "=" * 60)
    log("迁移统计")
    log("=" * 60)
    for key, val in stats.items():
        log(f"  {key}: {val}")
    log(f"总耗时: {elapsed_total:.1f}秒")
    log("=" * 60)


if __name__ == '__main__':
    main()
