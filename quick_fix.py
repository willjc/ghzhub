import os
import re

base_dir = r"d:\lasthm\gangzhu\ruoyi-system\src\main\java\com\ruoyi\system\service\impl"

# 剩余需要修复的文件
files_to_fix = [
    ("HzCheckInServiceImpl.java", "HzCheckIn", "checkInId"),
    ("HzCheckOutServiceImpl.java", "HzCheckOut", "checkOutId"),
    ("HzCommitmentServiceImpl.java", "HzCommitment", "commitmentId"),
    ("HzContractTemplateServiceImpl.java", "HzContractTemplate", "templateId"),
    ("HzDocumentServiceImpl.java", "HzDocument", "documentId"),
    ("HzEnterpriseServiceImpl.java", "HzEnterprise", "enterpriseId"),
    ("HzHouseTypeImageServiceImpl.java", "HzHouseTypeImage", "imageId"),
    ("HzHouseTypeServiceImpl.java", "HzHouseType", "houseTypeId"),
    ("HzInvoiceApplyServiceImpl.java", "HzInvoiceApply", "applyId"),
    ("HzInvoiceServiceImpl.java", "HzInvoice", "invoiceId"),
    ("HzMessageServiceImpl.java", "HzMessage", "messageId"),
    ("HzPaymentServiceImpl.java", "HzPayment", "paymentId"),
    ("HzQualificationAppealServiceImpl.java", "HzQualificationAppeal", "appealId"),
    ("HzQualificationServiceImpl.java", "HzQualification", "qualificationId"),
    ("HzTenantServiceImpl.java", "HzTenant", "tenantId"),
    ("HzTransactionServiceImpl.java", "HzTransaction", "transactionId"),
]

count_success = 0
count_fail = 0

for filename, entity_class, id_field in files_to_fix:
    filepath = os.path.join(base_dir, filename)

    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()

        original = content

        # 1. 添加import (如果没有)
        if 'import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;' not in content:
            content = content.replace(
                'import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;',
                'import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;\nimport com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;'
            )

        # 2. 修复删除方法
        # 将变量名首字母小写
        var_name = entity_class[0].lower() + entity_class[1:]
        id_field_getter = id_field[0].upper() + id_field[1:]

        # 构建要查找的模式
        old_pattern = f'''{var_name} = new {entity_class}();
        {var_name}.set{id_field_getter}({id_field});
        {var_name}.setDelFlag("2");
        return this.updateById({var_name}) ? 1 : 0;'''

        # 新代码
        new_code = f'''LambdaUpdateWrapper<{entity_class}> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set({entity_class}::getDelFlag, "2")
               .eq({entity_class}::get{id_field_getter}, {id_field})
               .eq({entity_class}::getDelFlag, "0");
        return this.update(wrapper) ? 1 : 0;'''

        content = content.replace(old_pattern, new_code)

        # 写回文件
        if content != original:
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"OK {filename}")
            count_success += 1
        else:
            print(f"SKIP {filename} (no changes)")
            count_fail += 1

    except Exception as e:
        print(f"FAIL {filename}: {e}")
        count_fail += 1

print(f"\nSuccess: {count_success}, Failed: {count_fail}")
