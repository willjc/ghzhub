import pandas as pd
import json

# 读取Excel文件
excel_file = r'D:\lasthm\gangzhu\港好住信息系统平台功能清单.xlsx'
json_file = r'D:\lasthm\gangzhu\功能清单.json'

# 尝试读取Excel的所有工作表
try:
    # 读取所有工作表
    all_sheets = pd.read_excel(excel_file, sheet_name=None)

    # 将所有工作表保存到字典
    result = {}
    for sheet_name, df in all_sheets.items():
        # 将NaN替换为None
        df = df.where(pd.notnull(df), None)
        # 转换为列表格式
        result[sheet_name] = df.to_dict('records')

    # 保存为JSON文件
    with open(json_file, 'w', encoding='utf-8') as f:
        json.dump(result, f, ensure_ascii=False, indent=2, default=str)

    print(f"转换成功！")
    print(f"工作表数量: {len(all_sheets)}")
    for sheet_name, df in all_sheets.items():
        print(f"  - {sheet_name}: {len(df)} 行数据")
    print(f"\nJSON文件已保存到: {json_file}")

except Exception as e:
    print(f"转换失败: {e}")
    print("\n建议：")
    print("1. 确保Excel文件路径正确")
    print("2. 确保安装了所需库: pip install pandas openpyxl")
    print("3. 确保Excel文件没有被其他程序打开")