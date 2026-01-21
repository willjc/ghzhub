-- ====================================
-- 港好住信息系统平台 - 字典数据初始化脚本
-- 数据库: newgangzhu
-- 字符集: utf8
-- 创建日期: 2025-11-17
-- ====================================

USE newgangzhu;

-- ====================================
-- 删除旧的字典类型和字典数据
-- ====================================
DELETE FROM sys_dict_data WHERE dict_type IN (
    'hz_project_type', 'hz_house_status', 'hz_house_type', 'hz_orientation', 'hz_decoration',
    'hz_contract_type', 'hz_contract_status', 'hz_payment_cycle', 'hz_qualification_result',
    'hz_bill_type', 'hz_bill_status', 'hz_payment_method', 'hz_payment_status',
    'hz_invoice_type', 'hz_apply_status', 'hz_appointment_status', 'hz_document_type',
    'hz_audit_status', 'hz_message_type', 'hz_announcement_type', 'hz_checkin_status',
    'hz_checkout_reason', 'hz_item_status', 'hz_damage_level', 'hz_coupon_type',
    'hz_refund_status', 'hz_banner_type', 'hz_education_level', 'hz_marriage_status',
    'hz_gender', 'hz_yes_no'
);

DELETE FROM sys_dict_type WHERE dict_type IN (
    'hz_project_type', 'hz_house_status', 'hz_house_type', 'hz_orientation', 'hz_decoration',
    'hz_contract_type', 'hz_contract_status', 'hz_payment_cycle', 'hz_qualification_result',
    'hz_bill_type', 'hz_bill_status', 'hz_payment_method', 'hz_payment_status',
    'hz_invoice_type', 'hz_apply_status', 'hz_appointment_status', 'hz_document_type',
    'hz_audit_status', 'hz_message_type', 'hz_announcement_type', 'hz_checkin_status',
    'hz_checkout_reason', 'hz_item_status', 'hz_damage_level', 'hz_coupon_type',
    'hz_refund_status', 'hz_banner_type', 'hz_education_level', 'hz_marriage_status',
    'hz_gender', 'hz_yes_no'
);

-- ====================================
-- 字典类型
-- ====================================

-- 1. 项目类型
INSERT INTO sys_dict_type VALUES (100, '租赁类型', 'hz_project_type', '0', 'admin', NOW(), '', NULL, '港好住租赁类型');

-- 2. 房源状态
INSERT INTO sys_dict_type VALUES (101, '房源状态', 'hz_house_status', '0', 'admin', NOW(), '', NULL, '房源状态');

-- 3. 户型
INSERT INTO sys_dict_type VALUES (102, '户型', 'hz_house_type', '0', 'admin', NOW(), '', NULL, '房源户型');

-- 4. 朝向
INSERT INTO sys_dict_type VALUES (103, '朝向', 'hz_orientation', '0', 'admin', NOW(), '', NULL, '房源朝向');

-- 5. 装修情况
INSERT INTO sys_dict_type VALUES (104, '装修情况', 'hz_decoration', '0', 'admin', NOW(), '', NULL, '房源装修情况');

-- 6. 合同类型
INSERT INTO sys_dict_type VALUES (105, '合同类型', 'hz_contract_type', '0', 'admin', NOW(), '', NULL, '租赁合同类型');

-- 7. 合同状态
INSERT INTO sys_dict_type VALUES (106, '合同状态', 'hz_contract_status', '0', 'admin', NOW(), '', NULL, '合同状态');

-- 8. 缴费周期
INSERT INTO sys_dict_type VALUES (107, '缴费周期', 'hz_payment_cycle', '0', 'admin', NOW(), '', NULL, '租金缴费周期');

-- 9. 资格审核结果
INSERT INTO sys_dict_type VALUES (108, '资格审核结果', 'hz_qualification_result', '0', 'admin', NOW(), '', NULL, '资格审核结果');

-- 10. 账单类型
INSERT INTO sys_dict_type VALUES (109, '账单类型', 'hz_bill_type', '0', 'admin', NOW(), '', NULL, '账单类型');

-- 11. 账单状态
INSERT INTO sys_dict_type VALUES (110, '账单状态', 'hz_bill_status', '0', 'admin', NOW(), '', NULL, '账单状态');

-- 12. 支付方式
INSERT INTO sys_dict_type VALUES (111, '支付方式', 'hz_payment_method', '0', 'admin', NOW(), '', NULL, '支付方式');

-- 13. 支付状态
INSERT INTO sys_dict_type VALUES (112, '支付状态', 'hz_payment_status', '0', 'admin', NOW(), '', NULL, '支付状态');

-- 14. 发票类型
INSERT INTO sys_dict_type VALUES (113, '发票类型', 'hz_invoice_type', '0', 'admin', NOW(), '', NULL, '发票类型');

-- 15. 申请状态
INSERT INTO sys_dict_type VALUES (114, '申请状态', 'hz_apply_status', '0', 'admin', NOW(), '', NULL, '各类申请状态');

-- 16. 预约状态
INSERT INTO sys_dict_type VALUES (115, '预约状态', 'hz_appointment_status', '0', 'admin', NOW(), '', NULL, '预约看房状态');

-- 17. 资料类型
INSERT INTO sys_dict_type VALUES (116, '资料类型', 'hz_document_type', '0', 'admin', NOW(), '', NULL, '租户资料类型');

-- 18. 审核状态
INSERT INTO sys_dict_type VALUES (117, '审核状态', 'hz_audit_status', '0', 'admin', NOW(), '', NULL, '审核状态');

-- 19. 消息类型
INSERT INTO sys_dict_type VALUES (118, '消息类型', 'hz_message_type', '0', 'admin', NOW(), '', NULL, '消息类型');

-- 20. 公告类型
INSERT INTO sys_dict_type VALUES (119, '公告类型', 'hz_announcement_type', '0', 'admin', NOW(), '', NULL, '公告类型');

-- 21. 入住状态
INSERT INTO sys_dict_type VALUES (120, '入住状态', 'hz_checkin_status', '0', 'admin', NOW(), '', NULL, '入住状态');

-- 22. 退租原因
INSERT INTO sys_dict_type VALUES (121, '退租原因', 'hz_checkout_reason', '0', 'admin', NOW(), '', NULL, '退租原因');

-- 23. 物品状态
INSERT INTO sys_dict_type VALUES (122, '物品状态', 'hz_item_status', '0', 'admin', NOW(), '', NULL, '物品状态');

-- 24. 损坏程度
INSERT INTO sys_dict_type VALUES (123, '损坏程度', 'hz_damage_level', '0', 'admin', NOW(), '', NULL, '物品损坏程度');

-- 25. 优惠券类型
INSERT INTO sys_dict_type VALUES (124, '优惠券类型', 'hz_coupon_type', '0', 'admin', NOW(), '', NULL, '优惠券类型');

-- 26. 退款状态
INSERT INTO sys_dict_type VALUES (125, '退款状态', 'hz_refund_status', '0', 'admin', NOW(), '', NULL, '退款状态');

-- 27. 轮播图类型
INSERT INTO sys_dict_type VALUES (126, '轮播图类型', 'hz_banner_type', '0', 'admin', NOW(), '', NULL, '轮播图类型');

-- 28. 学历
INSERT INTO sys_dict_type VALUES (127, '学历', 'hz_education_level', '0', 'admin', NOW(), '', NULL, '学历');

-- 29. 婚姻状况
INSERT INTO sys_dict_type VALUES (128, '婚姻状况', 'hz_marriage_status', '0', 'admin', NOW(), '', NULL, '婚姻状况');

-- 30. 性别
INSERT INTO sys_dict_type VALUES (129, '性别', 'hz_gender', '0', 'admin', NOW(), '', NULL, '性别');

-- 31. 是否
INSERT INTO sys_dict_type VALUES (130, '是否', 'hz_yes_no', '0', 'admin', NOW(), '', NULL, '是否');

-- ====================================
-- 字典数据
-- ====================================

-- 1. 项目类型(租赁类型)
INSERT INTO sys_dict_data VALUES (1000, 1, '人才公寓', '1', 'hz_project_type', '', 'primary', 'N', '0', 'admin', NOW(), '', NULL, '人才公寓');
INSERT INTO sys_dict_data VALUES (1001, 2, '保租房', '2', 'hz_project_type', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '保租房');
INSERT INTO sys_dict_data VALUES (1002, 3, '市场租赁', '3', 'hz_project_type', '', 'info', 'N', '0', 'admin', NOW(), '', NULL, '市场租赁');

-- 2. 房源状态
INSERT INTO sys_dict_data VALUES (1010, 1, '空置', '0', 'hz_house_status', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '空置可租');
INSERT INTO sys_dict_data VALUES (1011, 2, '已预订', '1', 'hz_house_status', '', 'warning', 'N', '0', 'admin', NOW(), '', NULL, '已预订');
INSERT INTO sys_dict_data VALUES (1012, 3, '已出租', '2', 'hz_house_status', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '已出租');
INSERT INTO sys_dict_data VALUES (1013, 4, '维修中', '3', 'hz_house_status', '', 'info', 'N', '0', 'admin', NOW(), '', NULL, '维修中');
INSERT INTO sys_dict_data VALUES (1014, 5, '下架', '4', 'hz_house_status', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '下架');

-- 3. 户型
INSERT INTO sys_dict_data VALUES (1020, 1, '一室', '1', 'hz_house_type', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '一室');
INSERT INTO sys_dict_data VALUES (1021, 2, '一室一厅', '2', 'hz_house_type', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '一室一厅');
INSERT INTO sys_dict_data VALUES (1022, 3, '两室一厅', '3', 'hz_house_type', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '两室一厅');
INSERT INTO sys_dict_data VALUES (1023, 4, '两室两厅', '4', 'hz_house_type', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '两室两厅');
INSERT INTO sys_dict_data VALUES (1024, 5, '三室一厅', '5', 'hz_house_type', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '三室一厅');
INSERT INTO sys_dict_data VALUES (1025, 6, '三室两厅', '6', 'hz_house_type', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '三室两厅');

-- 4. 朝向
INSERT INTO sys_dict_data VALUES (1030, 1, '东', '1', 'hz_orientation', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '东');
INSERT INTO sys_dict_data VALUES (1031, 2, '南', '2', 'hz_orientation', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '南');
INSERT INTO sys_dict_data VALUES (1032, 3, '西', '3', 'hz_orientation', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '西');
INSERT INTO sys_dict_data VALUES (1033, 4, '北', '4', 'hz_orientation', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '北');
INSERT INTO sys_dict_data VALUES (1034, 5, '东南', '5', 'hz_orientation', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '东南');
INSERT INTO sys_dict_data VALUES (1035, 6, '西南', '6', 'hz_orientation', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '西南');
INSERT INTO sys_dict_data VALUES (1036, 7, '东北', '7', 'hz_orientation', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '东北');
INSERT INTO sys_dict_data VALUES (1037, 8, '西北', '8', 'hz_orientation', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '西北');

-- 5. 装修情况
INSERT INTO sys_dict_data VALUES (1040, 1, '毛坯', '1', 'hz_decoration', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '毛坯');
INSERT INTO sys_dict_data VALUES (1041, 2, '简装', '2', 'hz_decoration', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '简装');
INSERT INTO sys_dict_data VALUES (1042, 3, '精装', '3', 'hz_decoration', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '精装');
INSERT INTO sys_dict_data VALUES (1043, 4, '豪装', '4', 'hz_decoration', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '豪装');

-- 6. 合同类型
INSERT INTO sys_dict_data VALUES (1050, 1, '人才公寓', '1', 'hz_contract_type', '', 'primary', 'N', '0', 'admin', NOW(), '', NULL, '人才公寓合同');
INSERT INTO sys_dict_data VALUES (1051, 2, '保租房', '2', 'hz_contract_type', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '保租房合同');
INSERT INTO sys_dict_data VALUES (1052, 3, '市场租赁', '3', 'hz_contract_type', '', 'info', 'N', '0', 'admin', NOW(), '', NULL, '市场租赁合同');

-- 7. 合同状态
INSERT INTO sys_dict_data VALUES (1060, 1, '草稿', '0', 'hz_contract_status', '', 'info', 'N', '0', 'admin', NOW(), '', NULL, '草稿');
INSERT INTO sys_dict_data VALUES (1061, 2, '待签署', '1', 'hz_contract_status', '', 'warning', 'N', '0', 'admin', NOW(), '', NULL, '待签署');
INSERT INTO sys_dict_data VALUES (1062, 3, '已签署', '2', 'hz_contract_status', '', 'primary', 'N', '0', 'admin', NOW(), '', NULL, '已签署');
INSERT INTO sys_dict_data VALUES (1063, 4, '履行中', '3', 'hz_contract_status', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '履行中');
INSERT INTO sys_dict_data VALUES (1064, 5, '已到期', '4', 'hz_contract_status', '', 'info', 'N', '0', 'admin', NOW(), '', NULL, '已到期');
INSERT INTO sys_dict_data VALUES (1065, 6, '已解约', '5', 'hz_contract_status', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '已解约');

-- 8. 缴费周期
INSERT INTO sys_dict_data VALUES (1070, 1, '月付', '1', 'hz_payment_cycle', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '月付');
INSERT INTO sys_dict_data VALUES (1071, 2, '季付', '2', 'hz_payment_cycle', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '季付');
INSERT INTO sys_dict_data VALUES (1072, 3, '半年付', '3', 'hz_payment_cycle', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '半年付');
INSERT INTO sys_dict_data VALUES (1073, 4, '年付', '4', 'hz_payment_cycle', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '年付');

-- 9. 资格审核结果
INSERT INTO sys_dict_data VALUES (1080, 1, '待审核', '0', 'hz_qualification_result', '', 'info', 'N', '0', 'admin', NOW(), '', NULL, '待审核');
INSERT INTO sys_dict_data VALUES (1081, 2, '通过', '1', 'hz_qualification_result', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '通过');
INSERT INTO sys_dict_data VALUES (1082, 3, '不通过', '2', 'hz_qualification_result', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '不通过');

-- 10. 账单类型
INSERT INTO sys_dict_data VALUES (1090, 1, '押金', '1', 'hz_bill_type', '', 'primary', 'N', '0', 'admin', NOW(), '', NULL, '押金');
INSERT INTO sys_dict_data VALUES (1091, 2, '租金', '2', 'hz_bill_type', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '租金');
INSERT INTO sys_dict_data VALUES (1092, 3, '水费', '3', 'hz_bill_type', '', 'info', 'N', '0', 'admin', NOW(), '', NULL, '水费');
INSERT INTO sys_dict_data VALUES (1093, 4, '电费', '4', 'hz_bill_type', '', 'warning', 'N', '0', 'admin', NOW(), '', NULL, '电费');
INSERT INTO sys_dict_data VALUES (1094, 5, '燃气费', '5', 'hz_bill_type', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '燃气费');
INSERT INTO sys_dict_data VALUES (1095, 6, '物业费', '6', 'hz_bill_type', '', 'primary', 'N', '0', 'admin', NOW(), '', NULL, '物业费');
INSERT INTO sys_dict_data VALUES (1096, 7, '其他', '7', 'hz_bill_type', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '其他');

-- 11. 账单状态
INSERT INTO sys_dict_data VALUES (1100, 1, '待支付', '0', 'hz_bill_status', '', 'warning', 'N', '0', 'admin', NOW(), '', NULL, '待支付');
INSERT INTO sys_dict_data VALUES (1101, 2, '已支付', '1', 'hz_bill_status', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '已支付');
INSERT INTO sys_dict_data VALUES (1102, 3, '部分支付', '2', 'hz_bill_status', '', 'primary', 'N', '0', 'admin', NOW(), '', NULL, '部分支付');
INSERT INTO sys_dict_data VALUES (1103, 4, '已逾期', '3', 'hz_bill_status', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '已逾期');
INSERT INTO sys_dict_data VALUES (1104, 5, '已关闭', '4', 'hz_bill_status', '', 'info', 'N', '0', 'admin', NOW(), '', NULL, '已关闭');

-- 12. 支付方式
INSERT INTO sys_dict_data VALUES (1110, 1, '支付宝', '1', 'hz_payment_method', '', 'primary', 'N', '0', 'admin', NOW(), '', NULL, '支付宝');
INSERT INTO sys_dict_data VALUES (1111, 2, '微信', '2', 'hz_payment_method', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '微信');
INSERT INTO sys_dict_data VALUES (1112, 3, '银行卡', '3', 'hz_payment_method', '', 'info', 'N', '0', 'admin', NOW(), '', NULL, '银行卡');
INSERT INTO sys_dict_data VALUES (1113, 4, '现金', '4', 'hz_payment_method', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '现金');
INSERT INTO sys_dict_data VALUES (1114, 5, '港区支付', '5', 'hz_payment_method', '', 'warning', 'N', '0', 'admin', NOW(), '', NULL, '港区支付');

-- 13. 支付状态
INSERT INTO sys_dict_data VALUES (1120, 1, '待支付', '0', 'hz_payment_status', '', 'info', 'N', '0', 'admin', NOW(), '', NULL, '待支付');
INSERT INTO sys_dict_data VALUES (1121, 2, '支付成功', '1', 'hz_payment_status', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '支付成功');
INSERT INTO sys_dict_data VALUES (1122, 3, '支付失败', '2', 'hz_payment_status', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '支付失败');
INSERT INTO sys_dict_data VALUES (1123, 4, '已退款', '3', 'hz_payment_status', '', 'warning', 'N', '0', 'admin', NOW(), '', NULL, '已退款');

-- 14. 发票类型
INSERT INTO sys_dict_data VALUES (1130, 1, '增值税普通发票', '1', 'hz_invoice_type', '', 'primary', 'N', '0', 'admin', NOW(), '', NULL, '增值税普通发票');
INSERT INTO sys_dict_data VALUES (1131, 2, '增值税专用发票', '2', 'hz_invoice_type', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '增值税专用发票');

-- 15. 申请状态
INSERT INTO sys_dict_data VALUES (1140, 1, '待审核', '0', 'hz_apply_status', '', 'info', 'N', '0', 'admin', NOW(), '', NULL, '待审核');
INSERT INTO sys_dict_data VALUES (1141, 2, '已通过', '1', 'hz_apply_status', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '已通过');
INSERT INTO sys_dict_data VALUES (1142, 3, '已驳回', '2', 'hz_apply_status', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '已驳回');

-- 16. 预约状态
INSERT INTO sys_dict_data VALUES (1150, 1, '待确认', '0', 'hz_appointment_status', '', 'info', 'N', '0', 'admin', NOW(), '', NULL, '待确认');
INSERT INTO sys_dict_data VALUES (1151, 2, '已确认', '1', 'hz_appointment_status', '', 'primary', 'N', '0', 'admin', NOW(), '', NULL, '已确认');
INSERT INTO sys_dict_data VALUES (1152, 3, '已完成', '2', 'hz_appointment_status', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '已完成');
INSERT INTO sys_dict_data VALUES (1153, 4, '已取消', '3', 'hz_appointment_status', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '已取消');

-- 17. 资料类型
INSERT INTO sys_dict_data VALUES (1160, 1, '身份证', '1', 'hz_document_type', '', 'primary', 'N', '0', 'admin', NOW(), '', NULL, '身份证');
INSERT INTO sys_dict_data VALUES (1161, 2, '学历证明', '2', 'hz_document_type', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '学历证明');
INSERT INTO sys_dict_data VALUES (1162, 3, '社保证明', '3', 'hz_document_type', '', 'info', 'N', '0', 'admin', NOW(), '', NULL, '社保证明');
INSERT INTO sys_dict_data VALUES (1163, 4, '收入证明', '4', 'hz_document_type', '', 'warning', 'N', '0', 'admin', NOW(), '', NULL, '收入证明');
INSERT INTO sys_dict_data VALUES (1164, 5, '户口本', '5', 'hz_document_type', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '户口本');
INSERT INTO sys_dict_data VALUES (1165, 6, '婚姻证明', '6', 'hz_document_type', '', 'primary', 'N', '0', 'admin', NOW(), '', NULL, '婚姻证明');
INSERT INTO sys_dict_data VALUES (1166, 7, '其他', '7', 'hz_document_type', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '其他');

-- 18. 审核状态
INSERT INTO sys_dict_data VALUES (1170, 1, '待审核', '0', 'hz_audit_status', '', 'info', 'N', '0', 'admin', NOW(), '', NULL, '待审核');
INSERT INTO sys_dict_data VALUES (1171, 2, '已通过', '1', 'hz_audit_status', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '已通过');
INSERT INTO sys_dict_data VALUES (1172, 3, '已驳回', '2', 'hz_audit_status', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '已驳回');

-- 19. 消息类型
INSERT INTO sys_dict_data VALUES (1180, 1, '系统通知', '1', 'hz_message_type', '', 'primary', 'N', '0', 'admin', NOW(), '', NULL, '系统通知');
INSERT INTO sys_dict_data VALUES (1181, 2, '短信', '2', 'hz_message_type', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '短信');
INSERT INTO sys_dict_data VALUES (1182, 3, '邮件', '3', 'hz_message_type', '', 'info', 'N', '0', 'admin', NOW(), '', NULL, '邮件');

-- 20. 公告类型
INSERT INTO sys_dict_data VALUES (1190, 1, '通知', '1', 'hz_announcement_type', '', 'primary', 'N', '0', 'admin', NOW(), '', NULL, '通知');
INSERT INTO sys_dict_data VALUES (1191, 2, '公告', '2', 'hz_announcement_type', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '公告');
INSERT INTO sys_dict_data VALUES (1192, 3, '活动', '3', 'hz_announcement_type', '', 'warning', 'N', '0', 'admin', NOW(), '', NULL, '活动');

-- 21. 入住状态
INSERT INTO sys_dict_data VALUES (1200, 1, '已退租', '0', 'hz_checkin_status', '', 'info', 'N', '0', 'admin', NOW(), '', NULL, '已退租');
INSERT INTO sys_dict_data VALUES (1201, 2, '在住', '1', 'hz_checkin_status', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '在住');

-- 22. 退租原因
INSERT INTO sys_dict_data VALUES (1210, 1, '合同到期', '1', 'hz_checkout_reason', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '合同到期');
INSERT INTO sys_dict_data VALUES (1211, 2, '工作调动', '2', 'hz_checkout_reason', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '工作调动');
INSERT INTO sys_dict_data VALUES (1212, 3, '购买住房', '3', 'hz_checkout_reason', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '购买住房');
INSERT INTO sys_dict_data VALUES (1213, 4, '家庭原因', '4', 'hz_checkout_reason', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '家庭原因');
INSERT INTO sys_dict_data VALUES (1214, 5, '房屋质量问题', '5', 'hz_checkout_reason', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '房屋质量问题');
INSERT INTO sys_dict_data VALUES (1215, 6, '其他原因', '6', 'hz_checkout_reason', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '其他原因');

-- 23. 物品状态
INSERT INTO sys_dict_data VALUES (1220, 1, '完好', '1', 'hz_item_status', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '完好');
INSERT INTO sys_dict_data VALUES (1221, 2, '损坏', '2', 'hz_item_status', '', 'warning', 'N', '0', 'admin', NOW(), '', NULL, '损坏');
INSERT INTO sys_dict_data VALUES (1222, 3, '缺失', '3', 'hz_item_status', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '缺失');

-- 24. 损坏程度
INSERT INTO sys_dict_data VALUES (1230, 1, '轻微', '1', 'hz_damage_level', '', 'info', 'N', '0', 'admin', NOW(), '', NULL, '轻微');
INSERT INTO sys_dict_data VALUES (1231, 2, '中度', '2', 'hz_damage_level', '', 'warning', 'N', '0', 'admin', NOW(), '', NULL, '中度');
INSERT INTO sys_dict_data VALUES (1232, 3, '严重', '3', 'hz_damage_level', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '严重');

-- 25. 优惠券类型
INSERT INTO sys_dict_data VALUES (1240, 1, '满减', '1', 'hz_coupon_type', '', 'primary', 'N', '0', 'admin', NOW(), '', NULL, '满减');
INSERT INTO sys_dict_data VALUES (1241, 2, '折扣', '2', 'hz_coupon_type', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '折扣');
INSERT INTO sys_dict_data VALUES (1242, 3, '抵扣', '3', 'hz_coupon_type', '', 'warning', 'N', '0', 'admin', NOW(), '', NULL, '抵扣');

-- 26. 退款状态
INSERT INTO sys_dict_data VALUES (1250, 1, '处理中', '0', 'hz_refund_status', '', 'info', 'N', '0', 'admin', NOW(), '', NULL, '处理中');
INSERT INTO sys_dict_data VALUES (1251, 2, '成功', '1', 'hz_refund_status', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '成功');
INSERT INTO sys_dict_data VALUES (1252, 3, '失败', '2', 'hz_refund_status', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '失败');

-- 27. 轮播图类型
INSERT INTO sys_dict_data VALUES (1260, 1, '首页', '1', 'hz_banner_type', '', 'primary', 'N', '0', 'admin', NOW(), '', NULL, '首页');
INSERT INTO sys_dict_data VALUES (1261, 2, '项目详情', '2', 'hz_banner_type', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '项目详情');

-- 28. 学历
INSERT INTO sys_dict_data VALUES (1270, 1, '初中及以下', '1', 'hz_education_level', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '初中及以下');
INSERT INTO sys_dict_data VALUES (1271, 2, '高中/中专', '2', 'hz_education_level', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '高中/中专');
INSERT INTO sys_dict_data VALUES (1272, 3, '大专', '3', 'hz_education_level', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '大专');
INSERT INTO sys_dict_data VALUES (1273, 4, '本科', '4', 'hz_education_level', '', 'primary', 'N', '0', 'admin', NOW(), '', NULL, '本科');
INSERT INTO sys_dict_data VALUES (1274, 5, '硕士', '5', 'hz_education_level', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '硕士');
INSERT INTO sys_dict_data VALUES (1275, 6, '博士', '6', 'hz_education_level', '', 'warning', 'N', '0', 'admin', NOW(), '', NULL, '博士');

-- 29. 婚姻状况
INSERT INTO sys_dict_data VALUES (1280, 1, '未婚', '1', 'hz_marriage_status', '', 'primary', 'N', '0', 'admin', NOW(), '', NULL, '未婚');
INSERT INTO sys_dict_data VALUES (1281, 2, '已婚', '2', 'hz_marriage_status', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '已婚');
INSERT INTO sys_dict_data VALUES (1282, 3, '离异', '3', 'hz_marriage_status', '', 'info', 'N', '0', 'admin', NOW(), '', NULL, '离异');
INSERT INTO sys_dict_data VALUES (1283, 4, '丧偶', '4', 'hz_marriage_status', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '丧偶');

-- 30. 性别
INSERT INTO sys_dict_data VALUES (1290, 1, '男', '0', 'hz_gender', '', 'primary', 'N', '0', 'admin', NOW(), '', NULL, '男性');
INSERT INTO sys_dict_data VALUES (1291, 2, '女', '1', 'hz_gender', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '女性');

-- 31. 是否
INSERT INTO sys_dict_data VALUES (1300, 1, '否', '0', 'hz_yes_no', '', 'default', 'N', '0', 'admin', NOW(), '', NULL, '否');
INSERT INTO sys_dict_data VALUES (1301, 2, '是', '1', 'hz_yes_no', '', 'primary', 'N', '0', 'admin', NOW(), '', NULL, '是');
