#!/bin/bash
# 批量修复ServiceImpl中的逻辑删除方法

cd "d:\lasthm\gangzhu\ruoyi-system\src\main\java\com\ruoyi\system\service\impl"

files=(
    "HzBuildingServiceImpl.java"
    "HzCheckInServiceImpl.java"
    "HzCheckOutServiceImpl.java"
    "HzCommitmentServiceImpl.java"
    "HzContractTemplateServiceImpl.java"
    "HzDocumentServiceImpl.java"
    "HzEnterpriseServiceImpl.java"
    "HzHouseTypeImageServiceImpl.java"
    "HzHouseTypeServiceImpl.java"
    "HzInvoiceApplyServiceImpl.java"
    "HzInvoiceServiceImpl.java"
    "HzMessageServiceImpl.java"
    "HzPaymentServiceImpl.java"
    "HzQualificationAppealServiceImpl.java"
    "HzQualificationServiceImpl.java"
    "HzTenantServiceImpl.java"
    "HzTransactionServiceImpl.java"
)

for file in "${files[@]}"; do
    echo "Processing $file..."

    # 1. 添加 import (如果没有)
    if ! grep -q "import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper" "$file"; then
        sed -i '/import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper/a import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;' "$file"
    fi

    echo "  ✓ Import added"
done

echo "Done!"
