#!/usr/bin/env python3
import os
import re

mapper_dir = r"d:\lasthm\gangzhu\ruoyi-system\src\main\java\com\ruoyi\system\mapper"

for filename in os.listdir(mapper_dir):
    if not filename.endswith('.java'):
        continue

    filepath = os.path.join(mapper_dir, filename)

    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    # 检查是否已有 @Mapper 注解
    if '@Mapper' in content:
        print(f"Skip {filename} (already has @Mapper)")
        continue

    # 检查是否已导入 @Mapper
    has_mapper_import = 'import org.apache.ibatis.annotations.Mapper;' in content

    # 在 package 后添加 import
    if not has_mapper_import:
        content = content.replace(
            'package com.ruoyi.system.mapper;',
            'package com.ruoyi.system.mapper;\n\nimport org.apache.ibatis.annotations.Mapper;',
            1
        )

    # 在 public interface 前添加 @Mapper
    content = re.sub(
        r'(\n)(public interface)',
        r'\1@Mapper\n\2',
        content,
        count=1
    )

    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)

    print(f"Added @Mapper to {filename}")

print("\nDone!")
