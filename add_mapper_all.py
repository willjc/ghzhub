#!/usr/bin/env python3
import os
import re

# 所有需要处理的目录
mapper_dirs = [
    r"d:\lasthm\gangzhu\ruoyi-admin\src\main\java\com\ruoyi\gangzhu\activity\mapper",
    r"d:\lasthm\gangzhu\ruoyi-admin\src\main\java\com\ruoyi\gangzhu\policy\mapper",
    r"d:\lasthm\gangzhu\ruoyi-generator\src\main\java\com\ruoyi\generator\mapper",
    r"d:\lasthm\gangzhu\ruoyi-quartz\src\main\java\com\ruoyi\quartz\mapper",
]

for mapper_dir in mapper_dirs:
    if not os.path.exists(mapper_dir):
        print(f"目录不存在: {mapper_dir}")
        continue

    print(f"\n处理目录: {mapper_dir}")

    for filename in os.listdir(mapper_dir):
        if not filename.endswith('.java'):
            continue

        filepath = os.path.join(mapper_dir, filename)

        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()

        # 检查是否已有 @Mapper 注解
        if '@Mapper' in content:
            print(f"  Skip {filename} (already has @Mapper)")
            continue

        # 检查是否已导入 @Mapper
        has_mapper_import = 'import org.apache.ibatis.annotations.Mapper;' in content

        # 找到 package 语句
        package_match = re.search(r'(package [^;]+;)', content)
        if not package_match:
            print(f"  Skip {filename} (no package statement)")
            continue

        # 在 package 后添加 import
        if not has_mapper_import:
            content = content.replace(
                package_match.group(1),
                package_match.group(1) + '\n\nimport org.apache.ibatis.annotations.Mapper;',
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

        print(f"  Added @Mapper to {filename}")

print("\nDone!")
