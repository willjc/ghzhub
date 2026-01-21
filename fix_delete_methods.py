#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
批量修复ServiceImpl中的逻辑删除方法
将错误的 updateById() 方式改为正确的 LambdaUpdateWrapper 方式
"""

import os
import re

# 需要修复的文件列表
files_to_fix = [
    "HzAnnouncementServiceImpl.java",
    "HzBillServiceImpl.java",
    "HzBuildingServiceImpl.java",
    "HzCheckInServiceImpl.java",
    "HzCheckOutServiceImpl.java",
    "HzCommitmentServiceImpl.java",
    "HzContractTemplateServiceImpl.java",
    "HzDocumentServiceImpl.java",
    "HzEnterpriseServiceImpl.java",
    "HzHouseTypeImageServiceImpl.java",
    "HzHouseTypeServiceImpl.java",
    "HzInvoiceApplyServiceImpl.java",
    "HzInvoiceServiceImpl.java",
    "HzMessageServiceImpl.java",
    "HzPaymentServiceImpl.java",
    "HzQualificationAppealServiceImpl.java",
    "HzQualificationServiceImpl.java",
    "HzTenantServiceImpl.java",
    "HzTransactionServiceImpl.java",
]

base_dir = r"d:\lasthm\gangzhu\ruoyi-system\src\main\java\com\ruoyi\system\service\impl"

def fix_file(filepath):
    """修复单个文件"""
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    original_content = content

    # 提取类名（用于LambdaUpdateWrapper）
    class_match = re.search(r'public class (\w+ServiceImpl) extends ServiceImpl<\w+, (\w+)>', content)
    if not class_match:
        print(f"  ⚠️  无法提取类名: {os.path.basename(filepath)}")
        return False

    entity_class = class_match.group(2)  # 实体类名

    # 提取主键字段名（通常是xxxId）
    # 尝试从deleteXxxById方法中提取
    delete_method_match = re.search(r'public int delete\w+ById\(Long (\w+)\)', content)
    if not delete_method_match:
        print(f"  ⚠️  无法提取主键参数名: {os.path.basename(filepath)}")
        return False

    id_param = delete_method_match.group(1)  # 如 appointmentId

    # 从参数名推导实体字段名（首字母大写）
    id_field = id_param[0].upper() + id_param[1:]  # appointmentId -> AppointmentId

    # 查找并替换错误的删除方法模式
    # 匹配模式：
    # SomeEntity entity = new SomeEntity();
    # entity.setSomeId(someId);
    # entity.setDelFlag("2");
    # return this.updateById(entity) ? 1 : 0;

    pattern = re.compile(
        r'(@Override\s+)?'
        r'public int delete\w+ById\(Long ' + id_param + r'\)\s*\{\s*'
        r'(\w+)\s+(\w+)\s*=\s*new\s+\2\(\);?\s*'
        r'\3\.set' + id_field + r'\(' + id_param + r'\);?\s*'
        r'\3\.setDelFlag\("2"\);?\s*'
        r'return this\.updateById\(\3\)\s*\?\s*1\s*:\s*0;?\s*'
        r'\}',
        re.MULTILINE | re.DOTALL
    )

    replacement = (
        r'@Override\n'
        r'    public int delete' + entity_class.replace('Hz', '') + r'ById(Long ' + id_param + r') {\n'
        r'        LambdaUpdateWrapper<' + entity_class + r'> wrapper = new LambdaUpdateWrapper<>();\n'
        r'        wrapper.set(' + entity_class + r'::getDelFlag, "2")\n'
        r'               .eq(' + entity_class + r'::get' + id_field + r', ' + id_param + r')\n'
        r'               .eq(' + entity_class + r'::getDelFlag, "0");\n'
        r'        return this.update(wrapper) ? 1 : 0;\n'
        r'    }'
    )

    content = pattern.sub(replacement, content)

    # 检查是否需要添加import
    if content != original_content:
        if 'import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;' not in content:
            # 在其他import之后添加
            import_pattern = re.compile(r'(import com\.baomidou\.mybatisplus\.core\.conditions\.query\.LambdaQueryWrapper;)')
            if import_pattern.search(content):
                content = import_pattern.sub(
                    r'\1\nimport com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;',
                    content
                )
            else:
                # 如果没有LambdaQueryWrapper，在package后添加
                content = re.sub(
                    r'(package com\.ruoyi\.system\.service\.impl;)\n\n',
                    r'\1\n\nimport com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;\n',
                    content
                )

    # 写回文件
    if content != original_content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        return True
    else:
        print(f"  ℹ️  未找到需要修复的模式: {os.path.basename(filepath)}")
        return False

def main():
    print("=" * 60)
    print("批量修复逻辑删除方法")
    print("=" * 60)

    fixed_count = 0
    failed_count = 0

    for filename in files_to_fix:
        filepath = os.path.join(base_dir, filename)

        if not os.path.exists(filepath):
            print(f"❌ 文件不存在: {filename}")
            failed_count += 1
            continue

        print(f"\n处理: {filename}")
        try:
            if fix_file(filepath):
                print(f"  ✅ 修复成功")
                fixed_count += 1
            else:
                failed_count += 1
        except Exception as e:
            print(f"  ❌ 修复失败: {e}")
            failed_count += 1

    print("\n" + "=" * 60)
    print(f"修复完成！")
    print(f"  成功: {fixed_count} 个文件")
    print(f"  失败: {failed_count} 个文件")
    print("=" * 60)

if __name__ == '__main__':
    main()
