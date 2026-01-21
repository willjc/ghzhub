#!/usr/bin/env python3
"""
修复实体类缺失的字段和方法
根据编译错误自动添加缺失的属性和 getter/setter 方法
"""

import os
import re

# 需要添加的字段定义 (实体类名 -> [(字段名, Java类型, 数据库字段名, 注释)])
MISSING_FIELDS = {
    'HzBill': [
        ('isOverdue', 'String', 'is_overdue', '是否逾期(0:否 1:是)'),
        ('overdueDays', 'Integer', 'overdue_days', '逾期天数'),
    ],
    'HzCheckOut': [
        ('checkOutId', 'Long', 'checkout_id', '退租ID'),
        ('checkOutNo', 'String', 'checkout_no', '退租编号'),
        ('applyStatus', 'String', 'apply_status', '申请状态(0:待审核 1:已通过 2:已拒绝)'),
    ],
    'HzCheckIn': [
        ('checkInId', 'Long', 'checkin_id', '入住ID'),
        ('checkInNo', 'String', 'checkin_no', '入住编号'),
        ('applyStatus', 'String', 'apply_status', '申请状态(0:待审核 1:已通过 2:已拒绝)'),
    ],
}

def add_fields_to_entity(file_path, class_name):
    """给实体类添加缺失的字段"""
    if class_name not in MISSING_FIELDS:
        print(f"跳过 {class_name}: 无需添加字段")
        return

    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # 检查哪些字段已存在
    existing_fields = set()
    for field_name, _, _, _ in MISSING_FIELDS[class_name]:
        if f'private {field_name}' in content or f'private String {field_name}' in content or f'private Integer {field_name}' in content or f'private Long {field_name}' in content:
            existing_fields.add(field_name)
            print(f"  {field_name} 已存在,跳过")

    # 过滤出需要添加的字段
    fields_to_add = [(f, t, d, c) for f, t, d, c in MISSING_FIELDS[class_name] if f not in existing_fields]

    if not fields_to_add:
        print(f"{class_name}: 所有字段已存在")
        return

    # 找到最后一个字段定义的位置
    # 匹配模式: @TableField("xxx")\n    private Type fieldName;
    pattern = r'(@TableField\("[^"]+"\)\s+private\s+\w+\s+\w+;)'
    matches = list(re.finditer(pattern, content))

    if not matches:
        print(f"错误: 无法找到字段插入位置 in {class_name}")
        return

    # 在最后一个字段后插入新字段
    last_match = matches[-1]
    insert_pos = last_match.end()

    # 生成字段定义
    fields_code = []
    for field_name, java_type, db_field, comment in fields_to_add:
        fields_code.append(f'\n    /** {comment} */')
        fields_code.append(f'\n    @TableField("{db_field}")')
        fields_code.append(f'\n    private {java_type} {field_name};')
        print(f"  添加字段: {field_name} ({java_type})")

    # 插入字段定义
    content = content[:insert_pos] + ''.join(fields_code) + content[insert_pos:]

    # 找到最后一个 getter/setter 方法的位置
    # 匹配模式: public void setXxx(...) { ... } 或 public Type getXxx() { ... }
    getter_setter_pattern = r'(public\s+(?:void|[\w<>]+)\s+(?:get|set)\w+\([^)]*\)\s*\{[^}]*\})'
    getter_setter_matches = list(re.finditer(getter_setter_pattern, content))

    if getter_setter_matches:
        # 在最后一个 getter/setter 后插入
        last_getter_setter = getter_setter_matches[-1]
        method_insert_pos = last_getter_setter.end()
    else:
        # 找到 toString 方法前插入
        to_string_match = re.search(r'@Override\s+public\s+String\s+toString\(\)', content)
        if to_string_match:
            method_insert_pos = to_string_match.start()
        else:
            # 找到类的最后一个 } 前插入
            method_insert_pos = content.rfind('}')

    # 生成 getter/setter 方法
    methods_code = []
    for field_name, java_type, _, _ in fields_to_add:
        # Getter
        getter_name = 'get' + field_name[0].upper() + field_name[1:]
        methods_code.append(f'\n\n    public {java_type} {getter_name}() {{')
        methods_code.append(f'\n        return {field_name};')
        methods_code.append(f'\n    }}')

        # Setter
        setter_name = 'set' + field_name[0].upper() + field_name[1:]
        methods_code.append(f'\n\n    public void {setter_name}({java_type} {field_name}) {{')
        methods_code.append(f'\n        this.{field_name} = {field_name};')
        methods_code.append(f'\n    }}')

    # 插入方法
    content = content[:method_insert_pos] + ''.join(methods_code) + '\n' + content[method_insert_pos:]

    # 写回文件
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)

    print(f"[OK] {class_name}: 成功添加 {len(fields_to_add)} 个字段及其 getter/setter 方法")

def main():
    """主函数"""
    base_dir = r'd:\lasthm\gangzhu\ruoyi-system\src\main\java\com\ruoyi\system\domain'

    for class_name in MISSING_FIELDS.keys():
        file_path = os.path.join(base_dir, f'{class_name}.java')
        if not os.path.exists(file_path):
            print(f"警告: 文件不存在 - {file_path}")
            continue

        print(f"\n处理 {class_name}...")
        add_fields_to_entity(file_path, class_name)

    print("\n[OK] 所有实体类修复完成!")

if __name__ == '__main__':
    main()
