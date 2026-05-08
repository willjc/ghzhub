#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
航港南苑试点数据导入脚本
- 导入 合同/入住/退租/账单/用户(新增) 到新系统
- 默认 DRY_RUN=True，先出预校验报告，不落库
- 所有落库记录统一 create_by='migration_hanggang'，便于回滚
详细说明见 migrate_hanggang_nanyuan.README.md
"""
import os
import sys
import time
import json
from collections import defaultdict, Counter
from datetime import datetime, date

import openpyxl
import pymysql

# ==============================
# 配置
# ==============================
DRY_RUN = True   # True: 只统计，不落库；False: 正式写库
MIGRATION_TAG = 'migration_hanggang'
PROJECT_ID = 23  # 航港南苑
PROJECT_NAME = '航港南苑'

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
EXCEL_CONTRACT_NORMAL = os.path.join(BASE_DIR, '航港南苑', '合同列表.xlsx')
EXCEL_CONTRACT_CENTRAL = os.path.join(BASE_DIR, '航港南苑', '合同列表 (集中配租).xlsx')
EXCEL_BILL = os.path.join(BASE_DIR, '航港南苑', '账单列表 (1).xlsx')

REPORT_PATH = os.path.join(BASE_DIR, 'scripts', 'migrate_hanggang_dryrun_report.txt')

DB_CONFIG = {
    'host': '36.133.39.148',
    'port': 33061,
    'user': 'ghzuser',
    'password': '!aNJ-7vTE+Tsddce',
    'database': 'ghz',
    'charset': 'utf8mb4',
    'autocommit': False,
}

# ==============================
# 枚举映射
# ==============================
MAP_CONTRACT_STATUS = {'已签约': '3', '已退租': '5'}  # 3履行中 / 5已解约
MAP_BILL_TYPE = {'押金': '1', '房租': '2'}
MAP_PAY_METHOD = {'微信': '2'}
MAP_EDUCATION = {
    '本科生': '4', '本科': '4',
    '硕士': '5', '硕士生': '5', '研究生': '5',
    '博士': '6', '博士生': '6',
    '大专': '3', '专科': '3',
    '高中': '2', '中专': '2', '高中/中专': '2',
    '初中': '1', '初中及以下': '1',
    '无学历': None, '': None, None: None,
}
# 账单状态映射（方案 A：退费用 remark 标注）
def map_bill_status(raw):
    if raw == '已支付':
        return '1', None
    if raw == '未支付':
        return '0', None
    if raw == '已退费':
        return '1', '已退费'
    if raw == '待退费':
        return '1', '待退费'
    return '0', f'未识别状态:{raw}'

# ==============================
# 工具
# ==============================
def log(msg):
    print(msg)

def parse_room(room):
    """
    '航港南苑-1号楼-2单元-29-2901' -> ('1号楼','2单元','29','2901')
    """
    if not room:
        return None
    parts = str(room).split('-')
    if len(parts) < 5:
        return None
    return parts[1].strip(), parts[2].strip(), parts[3].strip(), parts[4].strip()

def read_xlsx(path):
    wb = openpyxl.load_workbook(path, data_only=True)
    ws = wb.active
    rows = list(ws.iter_rows(values_only=True))
    header = [str(c).strip() if c else '' for c in rows[0]]
    return header, rows[1:]

def fmt_dt(v):
    if v is None or v == '':
        return None
    if isinstance(v, (datetime, date)):
        return v.strftime('%Y-%m-%d %H:%M:%S') if isinstance(v, datetime) else v.strftime('%Y-%m-%d')
    return str(v)


# ==============================
# 加载目标库参考数据
# ==============================
def load_refs(cur):
    refs = {}
    # 1) 建筑映射 building_name -> building_id
    cur.execute("SELECT building_id, building_name FROM hz_building WHERE project_id=%s AND del_flag='0'", (PROJECT_ID,))
    refs['building'] = {name: bid for bid, name in cur.fetchall()}
    # 2) 单元映射 (building_id, unit_name) -> unit_id
    cur.execute("""SELECT u.unit_id, u.unit_name, u.building_id
                   FROM hz_unit u JOIN hz_building b ON u.building_id=b.building_id
                   WHERE b.project_id=%s AND u.del_flag='0'""", (PROJECT_ID,))
    refs['unit'] = {(bid, name): uid for uid, name, bid in cur.fetchall()}
    # 3) 房源映射 (unit_id, house_no) -> house_id（含 house_code/address）
    cur.execute("""SELECT h.house_id, h.house_code, h.unit_id, h.house_no, h.building_id,
                          b.building_name, u.unit_name
                   FROM hz_house h
                   LEFT JOIN hz_building b ON h.building_id=b.building_id
                   LEFT JOIN hz_unit u ON h.unit_id=u.unit_id
                   WHERE h.project_id=%s AND h.del_flag='0'""", (PROJECT_ID,))
    refs['house'] = {}
    for hid, hcode, uid, hno, bid, bname, uname in cur.fetchall():
        refs['house'][(bname, uname, str(hno))] = {
            'house_id': hid,
            'house_code': hcode,
            'unit_id': uid,
            'building_id': bid,
            'house_no': hno,
            'address': f"{PROJECT_NAME}-{bname}-{uname}-{hno}",
        }
    # 4) 用户映射 id_card -> user_id / phone / user 全记录
    cur.execute("SELECT user_id, id_card, phone, real_name, education FROM hz_user WHERE del_flag='0' AND id_card IS NOT NULL")
    refs['user_by_idcard'] = {}
    for uid, idc, ph, rn, edu in cur.fetchall():
        refs['user_by_idcard'][idc] = {'user_id': uid, 'phone': ph, 'real_name': rn, 'education': edu}
    # 5) 已有合同号（去重）
    cur.execute("SELECT contract_no FROM hz_contract WHERE del_flag='0' AND contract_no IS NOT NULL")
    refs['existing_contract_no'] = {r[0] for r in cur.fetchall()}
    # 6) 已有账单 bill_no
    cur.execute("SELECT bill_no FROM hz_bill WHERE del_flag='0' AND bill_no IS NOT NULL")
    refs['existing_bill_no'] = {r[0] for r in cur.fetchall()}
    return refs


# ==============================
# 解析 Excel
# ==============================
CONTRACT_COLS = ['房间号','签约人','身份证号','手机号','学历','合同状态','分配类型',
                 '当前租金','押金状态','应缴押金','已缴押金','合同总金额',
                 '合同开始时间','合同结束时间','入住状态','入住时间','入住办理人',
                 '发起退租时间','退房时的描述','退租原因','退房时间','审批状态','合同到期剩余天数']

BILL_COLS = ['账单类型','缴费类型','账单金额','合同编号','实收金额','应收金额','应退金额',
             '房间地址','房间号','租户名字','支付方式','逾期状态','账单状态',
             '支付时间','账单开始时间','账单结束时间']

def load_contracts():
    contracts = []
    for path, alloc in [(EXCEL_CONTRACT_NORMAL, '常规分配'), (EXCEL_CONTRACT_CENTRAL, '集中分配')]:
        header, rows = read_xlsx(path)
        # 按列索引定位
        idx = {c: header.index(c) for c in CONTRACT_COLS if c in header}
        for r in rows:
            if all(v is None or v == '' for v in r):
                continue
            rec = {k: r[idx[k]] if k in idx else None for k in CONTRACT_COLS}
            rec['_alloc'] = alloc
            rec['_src_file'] = os.path.basename(path)
            contracts.append(rec)
    return contracts

def load_bills():
    bills = []
    header, rows = read_xlsx(EXCEL_BILL)
    idx = {c: header.index(c) for c in BILL_COLS if c in header}
    for r in rows:
        if all(v is None or v == '' for v in r):
            continue
        rec = {k: r[idx[k]] if k in idx else None for k in BILL_COLS}
        bills.append(rec)
    return bills


# ==============================
# 核心：预校验 & 计划
# ==============================
def build_plan(contracts, bills, refs):
    """生成落库计划，同时收集各种异常"""
    plan = {
        'users_new': [],        # 新建 user
        'users_update': [],     # 补空字段 update
        'contracts': [],        # insert hz_contract
        'checkin': [],          # insert hz_checkin_record
        'checkout': [],         # insert hz_checkout_record
        'bills': [],            # insert hz_bill
    }
    issues = {
        'room_not_found': [],
        'unknown_contract_status': [],
        'unknown_education': [],
        'dup_contract_no': [],
        'dup_bill_no': [],
        'bill_contract_not_found': [],
        'bill_status_unknown': [],
        'user_conflict': [],
    }

    # --- 第一步：建立 (房间号+签约人) -> [合同编号列表]（按账单最早开始时间升序）---
    #   支持同人同房间多份合同（误操作退租后重签等场景）
    #   老合同(已退租)  → 取时间最早的合同号
    #   新合同(已签约) → 取时间最晚的合同号
    room_tenant_cno_times = defaultdict(dict)  # (room, tenant) -> {cno: earliest_start_time}
    for b in bills:
        key = (str(b['房间地址'] or '').strip(), str(b['租户名字'] or '').strip())
        cno = str(b['合同编号'] or '').strip()
        if not cno:
            continue
        t = b.get('账单开始时间')
        cur_t = room_tenant_cno_times[key].get(cno)
        if cur_t is None or (t is not None and t < cur_t):
            room_tenant_cno_times[key][cno] = t
    # 排序：按每个合同号下的最早账单开始时间升序；时间为 None 的排最后
    room_tenant_to_contractnos = {}
    for key, cno_dict in room_tenant_cno_times.items():
        ordered = sorted(cno_dict.items(),
                         key=lambda kv: (kv[1] is None, kv[1] or datetime.max))
        room_tenant_to_contractnos[key] = [cno for cno, _ in ordered]

    # --- 第二步：处理每条合同 ---
    seen_contract_no = set()
    synthetic_counter = 0  # 没找到合同编号时的兜底编号
    # 记录同 (room, tenant) 已分配过的合同号下标（配合多合同场景）
    room_tenant_used_idx = defaultdict(set)
    # 按 "身份证号" 聚合 user：相同身份证只新增一次
    user_plan_by_idcard = {}

    for c in contracts:
        room = str(c['房间号'] or '').strip()
        tenant_name = str(c['签约人'] or '').strip()
        id_card = str(c['身份证号'] or '').strip()
        phone = str(c['手机号'] or '').strip() if c['手机号'] else None
        edu_raw = c['学历']
        edu = MAP_EDUCATION.get(edu_raw, 'UNKNOWN')
        if edu == 'UNKNOWN':
            issues['unknown_education'].append((tenant_name, edu_raw))
            edu = None

        # 房间号 -> house_id
        parsed = parse_room(room)
        if not parsed:
            issues['room_not_found'].append((room, tenant_name, '房间号格式无法解析'))
            continue
        bname, uname, floor, hno = parsed
        house = refs['house'].get((bname, uname, hno))
        if not house:
            issues['room_not_found'].append((room, tenant_name, f'库中无此房源: {bname}/{uname}/{hno}'))
            continue

        # 合同状态
        cstatus_raw = c['合同状态']
        cstatus = MAP_CONTRACT_STATUS.get(cstatus_raw)
        if not cstatus:
            issues['unknown_contract_status'].append((room, tenant_name, cstatus_raw))
            continue

        # 合同编号（从账单反查，支持同房同人多份合同）
        key_rt = (room, tenant_name)
        cno_list = room_tenant_to_contractnos.get(key_rt, [])
        cno = None
        if len(cno_list) == 1:
            cno = cno_list[0]
        elif len(cno_list) > 1:
            # 多合同：已退租 → 取按时间升序里第一个未用过的；已签约 → 最后一个未用过的
            used = room_tenant_used_idx[key_rt]
            if cstatus == '5':  # 已解约 -> 老合同
                for i in range(len(cno_list)):
                    if i not in used:
                        cno = cno_list[i]
                        used.add(i)
                        break
            else:  # 履行中 -> 新合同
                for i in range(len(cno_list) - 1, -1, -1):
                    if i not in used:
                        cno = cno_list[i]
                        used.add(i)
                        break
        if not cno:
            synthetic_counter += 1
            cno = f'HG{datetime.now().strftime("%Y%m%d")}{synthetic_counter:04d}'
        if cno in refs['existing_contract_no'] or cno in seen_contract_no:
            issues['dup_contract_no'].append((cno, room, tenant_name))
            continue
        seen_contract_no.add(cno)

        # 用户处理
        existed = refs['user_by_idcard'].get(id_card)
        if existed:
            # 仅补空字段
            updates = {}
            if not existed['phone'] and phone:
                updates['phone'] = phone
            if not existed['real_name'] and tenant_name:
                updates['real_name'] = tenant_name
            if not existed['education'] and edu:
                updates['education'] = edu
            if updates:
                plan['users_update'].append({'user_id': existed['user_id'], 'id_card': id_card, 'updates': updates})
                # 可能冲突信息
                if existed['phone'] and phone and existed['phone'] != phone:
                    issues['user_conflict'].append((id_card, f'phone:{existed["phone"]} vs {phone} (保留库中)'))
                if existed['real_name'] and tenant_name and existed['real_name'] != tenant_name:
                    issues['user_conflict'].append((id_card, f'name:{existed["real_name"]} vs {tenant_name} (保留库中)'))
            user_id = existed['user_id']
        else:
            if id_card in user_plan_by_idcard:
                user_id = user_plan_by_idcard[id_card]
            else:
                # 伪造一个 user_id 占位（真实执行时用 DB 自增或雪花）
                user_id = f'__NEW__{len(user_plan_by_idcard)+1}'
                user_plan_by_idcard[id_card] = user_id
                plan['users_new'].append({
                    'placeholder': user_id,
                    'real_name': tenant_name,
                    'id_card': id_card,
                    'phone': phone,
                    'education': edu,
                })

        # 合同记录
        start = c['合同开始时间']
        end = c['合同结束时间']
        rent = float(c['当前租金']) if c['当前租金'] is not None else 0.0
        deposit = float(c['应缴押金']) if c['应缴押金'] is not None else 0.0
        remark_parts = [c['_alloc']]
        if c['退租原因']:
            remark_parts.append(f"退租原因:{c['退租原因']}")
        contract_rec = {
            'contract_no': cno,
            'contract_type': '1',
            'contract_status': cstatus,
            'tenant_id': user_id,
            'tenant_name': tenant_name,
            'tenant_id_card': id_card,
            'tenant_phone': phone,
            'project_id': PROJECT_ID,
            'house_id': house['house_id'],
            'house_code': house['house_code'],
            'house_address': house['address'],
            'rent_price': rent,
            'deposit': deposit,
            'start_date': start,
            'end_date': end,
            'sign_time': start,
            'remark': ' | '.join([x for x in remark_parts if x]),
        }
        plan['contracts'].append(contract_rec)

        # 入住记录（入住时间非空 且 status 非 null）
        checkin_time = c['入住时间']
        if checkin_time:
            ci_status = '0' if cstatus == '5' else '2'  # 已退租=0(已退租)；否则=2(与现有业务数据一致)
            plan['checkin'].append({
                'contract_no': cno,
                'house_id': house['house_id'],
                'tenant_id': user_id,
                'actual_checkin_date': checkin_time,
                'checkin_time': checkin_time,
                'status': ci_status,
            })

        # 退租记录（退房时间非空）
        checkout_time = c['退房时间']
        if checkout_time:
            plan['checkout'].append({
                'contract_no': cno,
                'house_id': house['house_id'],
                'tenant_id': user_id,
                'checkout_date': checkout_time,
                'checkout_time': checkout_time,
                'refund_status': '1',
                'remark': str(c['退租原因'] or '') + (f" / {c['退房时的描述']}" if c['退房时的描述'] else ''),
            })

    # --- 第三步：处理账单 ---
    # 建立 contract_no -> plan中 合同索引
    contract_by_no = {cc['contract_no']: cc for cc in plan['contracts']}
    seen_bill_no = set()
    bill_seq = 0
    for b in bills:
        room = str(b['房间地址'] or '').strip()
        name = str(b['租户名字'] or '').strip()
        cno = str(b['合同编号'] or '').strip()
        cc = contract_by_no.get(cno)
        if not cc:
            issues['bill_contract_not_found'].append((cno, room, name))
            continue

        btype = MAP_BILL_TYPE.get(b['账单类型'])
        if not btype:
            issues['bill_status_unknown'].append(('bill_type', b['账单类型']))
            continue
        bstatus, br_remark = map_bill_status(b['账单状态'])
        if br_remark and br_remark.startswith('未识别'):
            issues['bill_status_unknown'].append(('bill_status', b['账单状态']))
            continue
        pm = MAP_PAY_METHOD.get(b['支付方式']) if b['支付方式'] else None
        is_overdue = '1' if b['逾期状态'] == '已逾期' else '0'

        amount = float(b['账单金额']) if b['账单金额'] is not None else 0.0
        paid = float(b['实收金额']) if b['实收金额'] is not None else 0.0
        unpaid = max(amount - paid, 0.0) if bstatus != '1' else 0.0

        bill_seq += 1
        bill_no = f'HG-B-{datetime.now().strftime("%Y%m%d")}-{bill_seq:06d}'
        if bill_no in refs['existing_bill_no'] or bill_no in seen_bill_no:
            issues['dup_bill_no'].append(bill_no)
            continue
        seen_bill_no.add(bill_no)

        plan['bills'].append({
            'bill_no': bill_no,
            'contract_no': cno,
            'tenant_id': cc['tenant_id'],
            'tenant_name': name or cc['tenant_name'],
            'house_id': cc['house_id'],
            'house_code': cc['house_code'],
            'bill_type': btype,
            'bill_amount': amount,
            'paid_amount': paid,
            'unpaid_amount': unpaid,
            'bill_date': b['账单开始时间'],
            'due_date': b['账单结束时间'],
            'bill_status': bstatus,
            'pay_method': pm,
            'pay_time': b['支付时间'],
            'is_overdue': is_overdue,
            'remark': br_remark,
        })

    return plan, issues


# ==============================
# 报告输出
# ==============================
def dump_report(plan, issues, contracts, bills):
    lines = []
    def p(s=''):
        lines.append(s)

    p('=' * 80)
    p('航港南苑试点数据导入 DRY-RUN 报告')
    p(f'生成时间: {datetime.now()}')
    p(f'DRY_RUN = {DRY_RUN}')
    p('=' * 80)

    p('\n【Excel 源数据】')
    p(f'  合同(常规+集中) 共: {len(contracts)} 条')
    p(f'  账单共:            {len(bills)} 条')

    p('\n【将要执行的写入计划】')
    p(f'  新建 hz_user:              {len(plan["users_new"])} 条')
    p(f'  补空字段 UPDATE hz_user:   {len(plan["users_update"])} 条')
    p(f'  新建 hz_contract:          {len(plan["contracts"])} 条')
    p(f'  新建 hz_checkin_record:    {len(plan["checkin"])} 条')
    p(f'  新建 hz_checkout_record:   {len(plan["checkout"])} 条')
    p(f'  新建 hz_bill:              {len(plan["bills"])} 条')

    p('\n【合同状态分布（将写入）】')
    c_status = Counter(r['contract_status'] for r in plan['contracts'])
    p(f'  {dict(c_status)}  (3=履行中 5=已解约)')

    p('\n【账单类型/状态分布（将写入）】')
    p(f'  bill_type:   {dict(Counter(r["bill_type"] for r in plan["bills"]))}  (1=押金 2=租金)')
    p(f'  bill_status: {dict(Counter(r["bill_status"] for r in plan["bills"]))}  (0=待支付 1=已支付)')
    p(f'  is_overdue:  {dict(Counter(r["is_overdue"] for r in plan["bills"]))}')
    # 金额对账
    total_amt = sum(r['bill_amount'] for r in plan['bills'])
    total_paid = sum(r['paid_amount'] for r in plan['bills'])
    p(f'  账单金额合计: {total_amt:.2f}   实收合计: {total_paid:.2f}')

    p('\n' + '-' * 80)
    p('【异常 / 需要关注】')
    p('-' * 80)
    for k, v in issues.items():
        p(f'  {k}: {len(v)} 条')
        for item in v[:10]:
            p(f'      - {item}')
        if len(v) > 10:
            p(f'      ... 其余 {len(v)-10} 条省略')

    p('\n【新建用户样例（前 5 条）】')
    for u in plan['users_new'][:5]:
        p(f'  {u}')

    p('\n【将写入合同样例（前 3 条）】')
    for cc in plan['contracts'][:3]:
        p(f'  {json.dumps(cc, ensure_ascii=False, default=str)}')

    p('\n【将写入账单样例（前 3 条）】')
    for bb in plan['bills'][:3]:
        p(f'  {json.dumps(bb, ensure_ascii=False, default=str)}')

    p('\n' + '=' * 80)
    p('DRY-RUN 结束。确认无误后将 DRY_RUN 改为 False 重新运行即可正式落库。')
    p('=' * 80)

    content = '\n'.join(lines)
    with open(REPORT_PATH, 'w', encoding='utf-8') as f:
        f.write(content)
    print(content)
    print(f'\n完整报告已写入: {REPORT_PATH}')


# ==============================
# 正式写库（仅 DRY_RUN=False 且 --confirm 时执行）
# ==============================
def _to_date(v):
    if v is None:
        return None
    if hasattr(v, 'date'):
        return v.date()
    return v

def _calc_months(start, end):
    if not start or not end:
        return 12
    m = (end.year - start.year) * 12 + (end.month - start.month)
    return max(m, 1)

def execute_plan(conn, cur, plan):
    """
    落库顺序：users_update → users_new → contracts → checkin → checkout → bills
    - phone 是 hz_user 唯一键：新建前按 phone 查库，撞到就复用已有 user_id（仅补空字段）
    - 所有记录 create_by=MIGRATION_TAG，便于按 tag 回滚
    """
    now = datetime.now()

    # 1) users_update
    for u in plan['users_update']:
        upds = u['updates']
        if not upds:
            continue
        sets = ', '.join([f'{k}=%s' for k in upds.keys()])
        cur.execute(
            f"UPDATE hz_user SET {sets}, update_by=%s, update_time=%s WHERE user_id=%s",
            list(upds.values()) + [MIGRATION_TAG, now, u['user_id']],
        )
    log(f'   users_update done: {len(plan["users_update"])}')

    # 2) users_new（phone 冲突兜底）
    cur.execute("SELECT user_id, phone, real_name, id_card, education FROM hz_user WHERE del_flag='0' AND phone IS NOT NULL")
    user_by_phone = {}
    for uid, ph, rn, idc, edu in cur.fetchall():
        user_by_phone[ph] = {'user_id': uid, 'real_name': rn, 'id_card': idc, 'education': edu}

    placeholder_to_uid = {}
    reused, inserted = 0, 0
    for u in plan['users_new']:
        ph = u['phone']
        if ph and ph in user_by_phone:
            # 复用已有（仅补空）
            ex = user_by_phone[ph]
            upds = {}
            if not ex['id_card'] and u['id_card']:
                upds['id_card'] = u['id_card']
            if not ex['real_name'] and u['real_name']:
                upds['real_name'] = u['real_name']
            if not ex['education'] and u['education']:
                upds['education'] = u['education']
            if upds:
                sets = ', '.join([f'{k}=%s' for k in upds.keys()])
                cur.execute(
                    f"UPDATE hz_user SET {sets}, update_by=%s, update_time=%s WHERE user_id=%s",
                    list(upds.values()) + [MIGRATION_TAG, now, ex['user_id']],
                )
            placeholder_to_uid[u['placeholder']] = ex['user_id']
            reused += 1
            continue

        # phone 为空兜底（Excel 数据全部有 phone，这是保险）
        if not ph:
            ph = '000' + (u['id_card'] or '')[-8:]

        cur.execute(
            """INSERT INTO hz_user
            (phone, real_name, id_card, education,
             source_type, gender, auth_status, status, is_info_completed, del_flag,
             create_by, create_time, remark)
            VALUES (%s,%s,%s,%s,'1','0','0','0','0','0',%s,%s,%s)""",
            (ph, u['real_name'], u['id_card'], u['education'],
             MIGRATION_TAG, now, '航港南苑迁移新增'),
        )
        placeholder_to_uid[u['placeholder']] = cur.lastrowid
        inserted += 1
    log(f'   users_new done: insert={inserted} reuse_by_phone={reused}')

    # 3) contracts
    cno_to_cid = {}
    cno_to_tid = {}
    for c in plan['contracts']:
        tid = c['tenant_id']
        if isinstance(tid, str) and tid.startswith('__NEW__'):
            tid = placeholder_to_uid[tid]
        start = c['start_date']
        end = c['end_date']
        months = _calc_months(start, end)
        cur.execute(
            """INSERT INTO hz_contract
            (contract_no, contract_type, tenant_id, tenant_name, tenant_id_card, tenant_phone,
             project_id, house_id, house_code, house_address,
             rent_price, deposit, start_date, end_date, rent_months, sign_time,
             contract_status, is_renewed, del_flag, create_by, create_time, remark)
            VALUES (%s,%s,%s,%s,%s,%s, %s,%s,%s,%s, %s,%s,%s,%s,%s,%s, %s,'0','0',%s,%s,%s)""",
            (c['contract_no'], c['contract_type'], tid, c['tenant_name'], c['tenant_id_card'], c['tenant_phone'],
             c['project_id'], c['house_id'], c['house_code'], c['house_address'],
             c['rent_price'], c['deposit'], _to_date(start), _to_date(end), months, c['sign_time'],
             c['contract_status'], MIGRATION_TAG, now, c['remark']),
        )
        cid = cur.lastrowid
        cno_to_cid[c['contract_no']] = cid
        cno_to_tid[c['contract_no']] = tid
    log(f'   contracts done: {len(cno_to_cid)}')

    # 4) checkin
    ci_cnt = 0
    for ci in plan['checkin']:
        cid = cno_to_cid.get(ci['contract_no'])
        if not cid:
            continue
        tid = cno_to_tid[ci['contract_no']]
        d = ci['actual_checkin_date']
        cur.execute(
            """INSERT INTO hz_checkin_record
            (apply_id, contract_id, tenant_id, house_id,
             checkin_date, actual_checkin_date, checkin_time, status,
             del_flag, create_by, create_time)
            VALUES (0, %s,%s,%s, %s,%s,%s,%s, '0',%s,%s)""",
            (cid, tid, ci['house_id'],
             _to_date(d), d, ci['checkin_time'], ci['status'],
             MIGRATION_TAG, now),
        )
        ci_cnt += 1
    log(f'   checkin done: {ci_cnt}')

    # 5) checkout
    co_cnt = 0
    for co in plan['checkout']:
        cid = cno_to_cid.get(co['contract_no'])
        if not cid:
            continue
        tid = cno_to_tid[co['contract_no']]
        d = co['checkout_date']
        cur.execute(
            """INSERT INTO hz_checkout_record
            (apply_id, contract_id, tenant_id, house_id,
             checkout_date, checkout_time, refund_status,
             del_flag, create_by, create_time, remark)
            VALUES (0, %s,%s,%s, %s,%s,%s, '0',%s,%s,%s)""",
            (cid, tid, co['house_id'],
             _to_date(d), co['checkout_time'], co['refund_status'],
             MIGRATION_TAG, now, co.get('remark')),
        )
        co_cnt += 1
    log(f'   checkout done: {co_cnt}')

    # 6) bills（批量 executemany 加速）
    bill_rows = []
    for b in plan['bills']:
        cid = cno_to_cid.get(b['contract_no'])
        if not cid:
            continue
        tid = cno_to_tid[b['contract_no']]
        bill_rows.append((
            b['bill_no'], cid, tid, b['tenant_name'], b['house_id'], b['house_code'],
            b['bill_type'], b['bill_amount'], b['paid_amount'], b['unpaid_amount'],
            _to_date(b['bill_date']), _to_date(b['due_date']),
            b['bill_status'], b['pay_time'], b['pay_method'], b['is_overdue'],
            MIGRATION_TAG, now, b['remark'],
        ))
    if bill_rows:
        cur.executemany(
            """INSERT INTO hz_bill
            (bill_no, contract_id, tenant_id, tenant_name, house_id, house_code,
             bill_type, bill_amount, paid_amount, unpaid_amount, bill_date, due_date,
             bill_status, pay_time, pay_method, is_overdue,
             del_flag, create_by, create_time, remark)
            VALUES (%s,%s,%s,%s,%s,%s, %s,%s,%s,%s,%s,%s, %s,%s,%s,%s, '0',%s,%s,%s)""",
            bill_rows,
        )
    log(f'   bills done: {len(bill_rows)}')


# ==============================
# 主流程
# ==============================
def main():
    t0 = time.time()
    log('[1/4] 读取 Excel ...')
    contracts = load_contracts()
    bills = load_bills()
    log(f'   合同={len(contracts)}, 账单={len(bills)}')

    log('[2/4] 连接数据库、加载参考数据 ...')
    conn = pymysql.connect(**DB_CONFIG)
    try:
        cur = conn.cursor()
        refs = load_refs(cur)
        log(f"   楼栋={len(refs['building'])}, 单元={len(refs['unit'])}, 房源={len(refs['house'])}, 用户={len(refs['user_by_idcard'])}")

        log('[3/4] 构建落库计划 + 校验 ...')
        plan, issues = build_plan(contracts, bills, refs)

        log('[4/4] 输出报告 ...')
        dump_report(plan, issues, contracts, bills)

        if DRY_RUN:
            log('\nDRY_RUN=True，未落库。')
        else:
            # 二次确认：必须在命令行带 --confirm，防止误触发
            if '--confirm' not in sys.argv:
                log('\n[!] DRY_RUN=False 但未带 --confirm，拒绝落库。')
                log('    如确认无误，执行：python3 scripts/migrate_hanggang_nanyuan.py --confirm')
                return
            # 致命异常前置检查
            fatal_keys = ['room_not_found', 'unknown_contract_status',
                          'dup_contract_no', 'dup_bill_no',
                          'bill_contract_not_found', 'bill_status_unknown']
            fatal = sum(len(issues[k]) for k in fatal_keys)
            if fatal > 0:
                log(f'\n[!] 存在致命异常 {fatal} 条，拒绝落库。请先修复。')
                return
            log('\nDRY_RUN=False --confirm 确认，开始落库 ...')
            try:
                execute_plan(conn, cur, plan)
                conn.commit()
                log('\n落库完成 ✓')
                log(f'已写入的数据可通过 create_by=\'{MIGRATION_TAG}\' 追溯/回滚。')
            except Exception as e:
                conn.rollback()
                log(f'\n[X] 落库失败，已回滚：{e}')
                raise
    finally:
        conn.close()
    log(f'总耗时: {time.time()-t0:.1f}s')


if __name__ == '__main__':
    main()
