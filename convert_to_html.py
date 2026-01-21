import re

# 读取Markdown文件
print("正在读取Markdown���件...")
with open(r'd:\lasthm\gangzhu\技术方案.md', 'r', encoding='utf-8') as f:
    md_content = f.read()

print(f"文件读取成功，共 {len(md_content)} 个字符")

# 转义特殊字符以便嵌入JavaScript
md_escaped = md_content.replace('\\', '\\\\').replace('`', '\\`').replace('$', '\\$')

# 生成HTML
html_content = f'''<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>港好住信息系统平台技术服务方案</title>
    <script src="https://cdn.jsdelivr.net/npm/mermaid/dist/mermaid.min.js"></script>
    <style>
        body {{
            font-family: "Microsoft YaHei", "Segoe UI", Arial, sans-serif;
            line-height: 1.8;
            color: #333;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            padding: 20px;
        }}

        .container {{
            max-width: 1200px;
            margin: 0 auto;
            background-color: white;
            padding: 50px;
            border-radius: 12px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
        }}

        h1 {{
            text-align: center;
            color: #2c3e50;
            font-size: 2.5em;
            margin-bottom: 10px;
            padding-bottom: 20px;
            border-bottom: 3px solid #667eea;
        }}

        h2 {{
            color: #34495e;
            font-size: 2.8em;
            margin-top: 50px;
            margin-bottom: 25px;
            padding-bottom: 15px;
            border-bottom: 2px solid #ecf0f1;
        }}

        h3 {{
            color: #7f8c8d;
            font-size: 1.8em;
            margin-top: 35px;
            margin-bottom: 20px;
        }}

        h4 {{
            color: #95a5a6;
            font-size: 1.4em;
            margin-top: 25px;
            margin-bottom: 15px;
        }}

        p {{
            margin-bottom: 15px;
            text-align: justify;
        }}

        ul, ol {{
            margin-left: 30px;
            margin-bottom: 20px;
        }}

        li {{
            margin-bottom: 8px;
            line-height: 1.6;
        }}

        code {{
            background-color: #f4f4f4;
            padding: 3px 8px;
            border-radius: 4px;
            font-family: "Consolas", "Monaco", monospace;
            font-size: 0.9em;
            color: #e74c3c;
        }}

        pre {{
            background-color: #2c3e50;
            color: #ecf0f1;
            padding: 20px;
            border-radius: 8px;
            overflow-x: auto;
            margin: 20px 0;
        }}

        pre code {{
            background-color: transparent;
            color: #ecf0f1;
            padding: 0;
        }}

        table {{
            border-collapse: collapse;
            width: 100%;
            margin: 25px 0;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }}

        th, td {{
            border: 1px solid #ddd;
            padding: 15px;
            text-align: left;
        }}

        th {{
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            font-weight: bold;
        }}

        tr:nth-child(even) {{
            background-color: #f9f9f9;
        }}

        tr:hover {{
            background-color: #f5f5f5;
        }}

        blockquote {{
            border-left: 5px solid #667eea;
            margin: 20px 0;
            padding: 15px 25px;
            background-color: #f8f9fa;
            color: #555;
        }}

        hr {{
            border: none;
            border-top: 2px solid #ecf0f1;
            margin: 40px 0;
        }}

        a {{
            color: #667eea;
            text-decoration: none;
            transition: color 0.3s;
        }}

        a:hover {{
            color: #764ba2;
            text-decoration: underline;
        }}

        strong {{
            color: #2c3e50;
            font-weight: 600;
        }}

        .mermaid {{
            background-color: #ffffff;
            padding: 30px;
            margin: 30px 0;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            text-align: center;
        }}

        @media print {{
            body {{
                background: white;
                padding: 0;
            }}
            .container {{
                box-shadow: none;
                padding: 20px;
            }}
        }}
    </style>
</head>
<body>
    <div class="container">
        <div id="content"></div>
    </div>

    <script>
        // 配置Mermaid
        mermaid.initialize({{
            startOnLoad: true,
            theme: 'default',
            flowchart: {{
                useMaxWidth: true,
                htmlLabels: true,
                curve: 'basis'
            }},
            sequence: {{
                useMaxWidth: true,
                wrap: true
            }}
        }});

        // Markdown转HTML函数
        function parseMarkdown(md) {{
            let html = md;

            // 处理Mermaid代码块
            html = html.replace(/```mermaid\\n([\\s\\S]*?)```/g, function(match, code) {{
                return '<div class="mermaid">' + code.trim() + '</div>';
            }});

            // 处理普通代码块
            html = html.replace(/```(\\w*)\\n([\\s\\S]*?)```/g, function(match, lang, code) {{
                return '<pre><code class="language-' + lang + '">' + code.trim() + '</code></pre>';
            }});

            // 处理标题
            html = html.replace(/^#### (.*$)/gim, '<h4>$1</h4>');
            html = html.replace(/^### (.*$)/gim, '<h3>$1</h3>');
            html = html.replace(/^## (.*$)/gim, '<h2>$1</h2>');
            html = html.replace(/^# (.*$)/gim, '<h1>$1</h1>');

            // 处理粗体
            html = html.replace(/\\*\\*(.*?)\\*\\*/g, '<strong>$1</strong>');

            // 处理列表
            html = html.replace(/^\\d+\\.\\s+(.*$)/gim, '<ol><li>$1</li></ol>');
            html = html.replace(/(<\\/ol>)\\s*<ol>/g, '');
            html = html.replace(/^-\\s+(.*$)/gim, '<ul><li>$1</li></ul>');
            html = html.replace(/(<\\/ul>)\\s*<ul>/g, '');

            // 处理水平线
            html = html.replace(/^---$/gim, '<hr>');

            // 处理表格 - 简化处理
            html = html.replace(/\\|.*\\|/g, function(match) {{
                const cells = match.split('|').filter(c => c.trim());
                if (cells.length > 1) {{
                    if (match.includes('---')) {{
                        return ''; // 跳过表头分隔线
                    }}
                    return '<tr>' + cells.map(c => '<td>' + c.trim() + '</td>').join('') + '</tr>';
                }}
                return match;
            }});

            // 处理段落
            html = html.replace(/\\n\\n/g, '</p><p>');
            html = html.replace(/\\n/g, '<br>');
            html = '<p>' + html + '</p>';

            // 清理多余的p标签
            html = html.replace(/<p>\\s*<\\/p>/g, '');
            html = html.replace(/<p>(<h[1-6]>)/g, '$1');
            html = html.replace(/(<\\/h[1-6]>)<\\/p>/g, '$1');
            html = html.replace(/<p>(<ul>)/g, '$1');
            html = html.replace(/(<\\/ul>)<\\/p>/g, '$1');
            html = html.replace(/<p>(<ol>)/g, '$1');
            html = html.replace(/(<\\/ol>)<\\/p>/g, '$1');
            html = html.replace(/<p>(<pre>)/g, '$1');
            html = html.replace(/(<\\/pre>)<\\/p>/g, '$1');
            html = html.replace(/<p>(<div)/g, '$1');
            html = html.replace(/(<\\/div>)<\\/p>/g, '$1');
            html = html.replace(/<p>(<hr>)/g, '$1');
            html = html.replace(/(<hr>)<\\/p>/g, '$1');
            html = html.replace(/<p>(<tr>)/g, '$1');
            html = html.replace(/(<\\/tr>)<\\/p>/g, '$1');

            return html;
        }}

        // Markdown内容
        const md = `{md_escaped}";

        // 渲染内容
        document.getElementById('content').innerHTML = parseMarkdown(md);

        // 重新初始化Mermaid以渲染新添加的图表
        setTimeout(function() {{
            mermaid.init(undefined, document.querySelectorAll('.mermaid'));
        }}, 100);
    </script>
</body>
</html>'''

# 保存HTML文件
print("正在生成HTML文件...")
output_path = r'd:\lasthm\gangzhu\技术方案-完整版.html'
with open(output_path, 'w', encoding='utf-8') as f:
    f.write(html_content)

print(f"\n✅ HTML文件生成成功！")
print(f"📄 文件路径：{output_path}")
print(f"📊 文件大小：{len(html_content)} 字节")
print("\n💡 使用方法：")
print("   1. 双击HTML文件在浏览器中打开")
print("   2. 所有流程图会自动渲染为图形")
print("   3. 支持打印为PDF")
