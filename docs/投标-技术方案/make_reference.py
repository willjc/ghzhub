# -*- coding: utf-8 -*-
"""生成 pandoc 转换用的 reference.docx 样式模板：
正文宋体小四(12pt) 1.5倍行距，标题黑体分级，常规页边距"""
from docx import Document
from docx.shared import Pt, Cm, RGBColor
from docx.oxml.ns import qn
from docx.enum.text import WD_LINE_SPACING


def set_font(style, ascii_font, east_font, size_pt, bold=False, color=None):
    style.font.name = ascii_font
    style.font.size = Pt(size_pt)
    style.font.bold = bold
    if color is not None:
        style.font.color.rgb = color
    rpr = style.element.get_or_add_rPr()
    rfonts = rpr.find(qn('w:rFonts'))
    if rfonts is None:
        rfonts = rpr.makeelement(qn('w:rFonts'), {})
        rpr.append(rfonts)
    rfonts.set(qn('w:ascii'), ascii_font)
    rfonts.set(qn('w:hAnsi'), ascii_font)
    rfonts.set(qn('w:eastAsia'), east_font)


doc = Document('reference-default.docx')
black = RGBColor(0, 0, 0)

# 正文 Normal：宋体小四 1.5倍行距
normal = doc.styles['Normal']
set_font(normal, 'Times New Roman', '宋体', 12, color=black)
pf = normal.paragraph_format
pf.line_spacing_rule = WD_LINE_SPACING.ONE_POINT_FIVE
pf.space_before = Pt(0)
pf.space_after = Pt(0)
pf.first_line_indent = Pt(0)

# 标题分级：黑体
heads = [
    ('Heading 1', 16, True),   # 三号
    ('Heading 2', 14, True),   # 四号
    ('Heading 3', 13, True),
    ('Heading 4', 12, True),
]
for name, size, bold in heads:
    st = doc.styles[name]
    set_font(st, 'Times New Roman', '黑体', size, bold=bold, color=black)
    st.paragraph_format.line_spacing_rule = WD_LINE_SPACING.ONE_POINT_FIVE
    st.paragraph_format.space_before = Pt(10)
    st.paragraph_format.space_after = Pt(6)

# 图题/表格等辅助样式
for aux in ['Caption', 'Image Caption', 'Table Caption']:
    try:
        st = doc.styles[aux]
        set_font(st, 'Times New Roman', '宋体', 10.5, color=black)
    except KeyError:
        pass

# 表格正文小五更紧凑
try:
    st = doc.styles['Compact']
    set_font(st, 'Times New Roman', '宋体', 10.5, color=black)
except KeyError:
    pass

# 页边距常规（上下2.54 左右3.18）
for sec in doc.sections:
    sec.top_margin = Cm(2.54)
    sec.bottom_margin = Cm(2.54)
    sec.left_margin = Cm(3.18)
    sec.right_margin = Cm(3.18)

doc.save('reference.docx')
print('reference.docx OK')
