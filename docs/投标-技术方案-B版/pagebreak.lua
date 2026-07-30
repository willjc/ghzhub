-- 将 markdown 中的 \newpage 转成 docx 分页符
local pagebreak_docx = pandoc.RawBlock('openxml', '<w:p><w:r><w:br w:type="page"/></w:r></w:p>')

function RawBlock(el)
  if el.text:match('\\newpage') then
    return pagebreak_docx
  end
end

function Para(el)
  if #el.content == 1 and el.content[1].t == 'RawInline' and el.content[1].text:match('\\newpage') then
    return pagebreak_docx
  end
end
