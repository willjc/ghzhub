# -*- coding: utf-8 -*-
"""合并非技术部分（商务及资格）各章节为单一 markdown，并用 pandoc + reference.docx 生成最终 docx。
- 合并顺序：01(封面+目录+投标函+报价) → 02(商务-业绩团队驻场) → 03(商务-服务承诺) → 04(技术占位+资格审查+其他)
- 各章文件已自带结尾 \\newpage
- 封面用 raw openxml 大字居中排版；目录用 Word 自动目录域（打开后右键"更新域"生成页码）
- 排版与技术方案 C 版保持一致：reference.docx（由 make_reference.py 生成）+ pagebreak.lua
"""
import subprocess

CHAPTERS = [
    '01-封面目录投标函.md',
    '02-商务部分-业绩团队驻场.md',
    '03-商务部分-服务承诺.md',
    '04-技术占位资格审查其他.md',
]
MERGED = '投标文件-非技术部分-合并稿.md'
OUT = '郑州数据集团房屋管理系统项目-投标文件(非技术部分)-河南卓康电子科技有限公司.docx'

parts = []
for ch in CHAPTERS:
    with open(ch, encoding='utf-8') as f:
        parts.append(f.read().rstrip('\n'))

merged = '\n\n'.join(parts) + '\n'
with open(MERGED, 'w', encoding='utf-8') as f:
    f.write(merged)
print(f'merged -> {MERGED} ({len(merged)} chars)')

cmd = [
    'pandoc', MERGED,
    '-o', OUT,
    '--reference-doc', 'reference.docx',
    '--lua-filter', 'pagebreak.lua',
    '--resource-path', '.',
    '-f', 'markdown+raw_attribute',
]
subprocess.run(cmd, check=True)
print(f'docx -> {OUT}')
