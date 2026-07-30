# -*- coding: utf-8 -*-
"""合并 B 版章节为单一 markdown，并用 pandoc + reference.docx 生成最终 docx。
- 合并顺序：ch0(封面) → 目录页(Word TOC域) → ch1 → ch2a → ch2b → ch3 → ch4 → ch5 → ch6
- 各章文件已自带结尾 \\newpage
- 目录采用 Word 自动目录域（打开后右键"更新域"生成页码），封面在前、目录其次、正文从第一章开始
- 排版与 C 版保持一致：reference.docx（由 make_reference_b.py 生成）+ pagebreak.lua
"""
import subprocess

CHAPTERS = ['ch0', 'ch1', 'ch2a', 'ch2b', 'ch3', 'ch4', 'ch5', 'ch6']
MERGED = '技术方案-B版-合并稿.md'
OUT = '郑州数据集团房屋管理系统项目-技术方案-B版-河南卓康电子科技有限公司.docx'

# 目录页：非标题的居中"目录"字样 + Word TOC 域（1-3级），随后分页
TOC_BLOCK = '''<center>**目 录**</center>

```{=openxml}
<w:p><w:r><w:fldChar w:fldCharType="begin"/></w:r><w:r><w:instrText xml:space="preserve"> TOC \\o "1-3" \\h \\z \\u </w:instrText></w:r><w:r><w:fldChar w:fldCharType="separate"/></w:r><w:r><w:t>请在 Word 中右键"更新域"生成目录与页码。</w:t></w:r><w:r><w:fldChar w:fldCharType="end"/></w:r></w:p>
```

\\newpage
'''

parts = []
for ch in CHAPTERS:
    with open(f'chapters/{ch}.md', encoding='utf-8') as f:
        text = f.read().rstrip('\n')
    parts.append(text)
    if ch == 'ch0':  # 封面之后插入目录页
        parts.append(TOC_BLOCK.rstrip('\n'))

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
