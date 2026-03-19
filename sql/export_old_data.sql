-- ============================================
-- 港好住平台 - 老数据导出脚本
-- ============================================
-- 生成时间: 2026-03-03
-- 用途: 导出需要迁移的老系统数据
-- 使用方法: 在MySQL中执行此脚本，或使用 mysqldump 工具
-- ============================================


-- ============================================
-- 一、基础房源数据导出 (最优先)
-- ============================================

-- 1. 项目信息表
SELECT
    project_id AS '项目ID',
    project_name AS '项目名称',
    project_code AS '项目编码',
    CASE project_type
        WHEN '1' THEN '人才公寓'
        WHEN '2' THEN '保租房'
        WHEN '3' THEN '市场租赁'
        ELSE project_type
    END AS '项目类型',
    address AS '项目地址',
    longitude AS '经度',
    latitude AS '纬度',
    total_buildings AS '总楼栋数',
    total_houses AS '总房源数',
    available_houses AS '可房源数',
    distribution AS '户型分布',
    house_type AS '户型类型',
    area AS '面积范围',
    rent_detail AS '租金详情',
    property_company AS '物业公司',
    property_fee AS '物业费',
    electric_fee AS '电费标准',
    water_fee AS '水费标准',
    gas_fee AS '燃气费标准',
    project_intro AS '项目介绍',
    cover_image AS '封面图片',
    price AS '参考租金',
    facilities AS '配套设施',
    transportation AS '交通配套',
    manager_name AS '负责人',
    manager_phone AS '负责人电话',
    CASE status
        WHEN '0' THEN '启用'
        WHEN '1' THEN '停用'
        ELSE status
    END AS '状态'
FROM hz_project
WHERE del_flag = '0'
ORDER BY project_id;


-- 2. 楼栋信息表
SELECT
    building_id AS '楼栋ID',
    project_id AS '所属项目ID',
    building_name AS '楼栋名称',
    building_code AS '楼栋编码',
    total_floors AS '总楼层数',
    total_units AS '总单元数',
    total_houses AS '总房源数',
    CASE status
        WHEN '0' THEN '启用'
        WHEN '1' THEN '停用'
        ELSE status
    END AS '状态'
FROM hz_building
WHERE del_flag = '0'
ORDER BY project_id, building_id;


-- 3. 单元信息表
SELECT
    unit_id AS '单元ID',
    building_id AS '所属楼栋ID',
    unit_name AS '单元名称',
    unit_code AS '单元编码',
    total_floors AS '总楼层数',
    total_houses AS '总房源数',
    CASE status
        WHEN '0' THEN '启用'
        WHEN '1' THEN '停用'
        ELSE status
    END AS '状态'
FROM hz_unit
WHERE del_flag = '0'
ORDER BY building_id, unit_id;


-- 4. 户型信息表
SELECT
    house_type_id AS '户型ID',
    house_type_name AS '户型名称',
    house_type_code AS '户型编码',
    bedrooms AS '卧室数',
    living_rooms AS '客厅数',
    bathrooms AS '卫生间数',
    area AS '面积',
    rent_price AS '参考租金',
    house_type_desc AS '户型描述',
    sort_order AS '排序',
    CASE status
        WHEN '0' THEN '启用'
        WHEN '1' THEN '停用'
        ELSE status
    END AS '状态'
FROM hz_house_type
WHERE del_flag = '0'
ORDER BY sort_order, house_type_id;


-- 5. 房源信息表
SELECT
    house_id AS '房源ID',
    project_id AS '所属项目ID',
    building_id AS '所属楼栋ID',
    unit_id AS '所属单元ID',
    house_code AS '房源编码',
    house_no AS '房号',
    floor AS '楼层',
    house_type_id AS '户型ID',
    house_type_name AS '户型名称',
    area AS '面积',
    rent_price AS '租金',
    deposit AS '押金',
    orientation AS '朝向',
    decoration AS '装修情况',
    facilities AS '配套设施',
    CASE house_status
        WHEN '0' THEN '空置'
        WHEN '1' THEN '已出租'
        WHEN '2' THEN '已预订'
        WHEN '3' THEN '维修中'
        WHEN '4' THEN '不可租'
        ELSE house_status
    END AS '房源状态',
    CASE is_featured
        WHEN '0' THEN '普通'
        WHEN '1' THEN '精选'
        ELSE is_featured
    END AS '是否精选',
    CASE allocation_type
        WHEN '1' THEN '正常分配'
        WHEN '2' THEN '人才分配'
        WHEN '3' THEN '企业分配'
        ELSE allocation_type
    END AS '分配类型',
    CASE is_fresh_graduate
        WHEN '0' THEN '否'
        WHEN '1' THEN '是'
        ELSE is_fresh_graduate
    END AS '是否应届生专享',
    manager_phone AS '管家电话'
FROM hz_house
WHERE del_flag = '0'
ORDER BY project_id, building_id, unit_id, house_no;


-- 6. 房源图片表
SELECT
    house_id AS '房源ID',
    image_type AS '图片类型(1:封面 2:室内 3:室外 4:其他)',
    image_url AS '图片路径',
    image_order AS '图片顺序',
    CASE status
        WHEN '0' THEN '启用'
        WHEN '1' THEN '停用'
        ELSE status
    END AS '状态'
FROM hz_house_image
WHERE del_flag = '0'
ORDER BY house_id, image_order;


-- 7. VR看房表
SELECT
    house_id AS '房源ID',
    vr_name AS 'VR名称',
    vr_url AS 'VR链接',
    vr_type AS 'VR类型',
    CASE status
        WHEN '0' THEN '启用'
        WHEN '1' THEN '停用'
        ELSE status
    END AS '状态'
FROM hz_house_vr
WHERE del_flag = '0'
ORDER BY house_id;


-- ============================================
-- 二、客户/租户数据导出
-- ============================================

-- 8. 租户信息表
SELECT
    tenant_id AS '租户ID',
    user_id AS '关联用户ID',
    tenant_name AS '租户姓名',
    id_card AS '身份证号',
    phone AS '手机号',
    CASE gender
        WHEN '0' THEN '男'
        WHEN '1' THEN '女'
        ELSE gender
    END AS '性别',
    birth_date AS '出生日期',
    education AS '学历',
    marriage_status AS '婚姻状况',
    work_unit AS '工作单位',
    emergency_contact AS '紧急联系人',
    emergency_phone AS '紧急联系电话',
    household_register AS '户口地址',
    current_address AS '现住址',
    email AS '邮箱',
    wechat AS '微信号',
    avatar AS '头像',
    credit_score AS '信用分',
    CASE is_blacklist
        WHEN '0' THEN '正常'
        WHEN '1' THEN '黑名单'
        ELSE is_blacklist
    END AS '是否黑名单',
    CASE status
        WHEN '0' THEN '正常'
        WHEN '1' THEN '冻结'
        ELSE status
    END AS '状态',
    create_time AS '创建时间'
FROM hz_tenant
WHERE del_flag = '0'
ORDER BY tenant_id;


-- 9. 用户账号表
SELECT
    user_id AS '用户ID',
    username AS '用户名',
    nickname AS '昵称',
    phone AS '手机号',
    email AS '邮箱',
    avatar AS '头像',
    CASE user_type
        WHEN '1' THEN '租户'
        WHEN '2' THEN '管理员'
        ELSE user_type
    END AS '用户类型',
    CASE status
        WHEN '0' THEN '正常'
        WHEN '1' THEN '停用'
        ELSE status
    END AS '状态',
    create_time AS '注册时间'
FROM hz_user
WHERE del_flag = '0'
ORDER BY user_id;


-- 10. 企业信息表
SELECT
    enterprise_id AS '企业ID',
    enterprise_name AS '企业名称',
    credit_code AS '统一社会信用代码',
    legal_person AS '法定代表人',
    contact_person AS '联系人',
    contact_phone AS '联系电话',
    province AS '省份',
    city AS '城市',
    district AS '区县',
    address AS '详细地址',
    business_license AS '营业执照',
    industry AS '所属行业',
    scale AS '企业规模',
    CASE status
        WHEN '0' THEN '正常'
        WHEN '1' THEN '审核中'
        WHEN '2' THEN '已拒绝'
        ELSE status
    END AS '状态',
    create_time AS '创建时间'
FROM hz_enterprise
WHERE del_flag = '0'
ORDER BY enterprise_id;


-- 11. 合租人信息表
SELECT
    co_tenant_id AS '合租人ID',
    tenant_id AS '主租户ID',
    contract_id AS '合同ID',
    co_tenant_name AS '合租人姓名',
    id_card AS '身份证号',
    phone AS '手机号',
    CASE gender
        WHEN '0' THEN '男'
        WHEN '1' THEN '女'
        ELSE gender
    END AS '性别',
    relationship AS '与主租户关系',
    create_time AS '创建时间'
FROM hz_co_tenant
WHERE del_flag = '0'
ORDER BY contract_id, co_tenant_id;


-- ============================================
-- 三、合同数据导出
-- ============================================

-- 12. 合同信息表
SELECT
    contract_id AS '合同ID',
    contract_no AS '合同编号',
    CASE contract_type
        WHEN '1' THEN '人才公寓'
        WHEN '2' THEN '保租房'
        WHEN '3' THEN '市场租赁'
        ELSE contract_type
    END AS '合同类型',
    template_id AS '合同模板ID',
    batch_id AS '批次ID',
    tenant_id AS '租户ID',
    tenant_name AS '租户姓名',
    tenant_id_card AS '租户身份证',
    tenant_phone AS '租户电话',
    project_id AS '项目ID',
    house_id AS '房源ID',
    house_code AS '房源编码',
    house_address AS '房源地址',
    rent_price AS '租金',
    deposit AS '押金',
    start_date AS '租赁开始日期',
    end_date AS '租赁结束日期',
    rent_months AS '租期月数',
    CASE payment_cycle
        WHEN '1' THEN '按月付'
        WHEN '3' THEN '按季付'
        WHEN '6' THEN '半年付'
        WHEN '12' THEN '年付'
        ELSE payment_cycle
    END AS '付款周期',
    payment_day AS '付款日',
    free_rent_periods AS '免租期数',
    contract_file AS '合同文件',
    sign_time AS '签署时间',
    CASE contract_status
        WHEN '0' THEN '待签署'
        WHEN '1' THEN '已签署'
        WHEN '2' THEN '执行中'
        WHEN '3' THEN '已到期'
        WHEN '4' THEN '已解除'
        ELSE contract_status
    END AS '合同状态',
    CASE is_renewed
        WHEN '0' THEN '否'
        WHEN '1' THEN '是'
        ELSE is_renewed
    END AS '是否续租',
    renewed_contract_id AS '续租合同ID',
    create_time AS '创建时间'
FROM hz_contract
WHERE del_flag = '0'
ORDER BY contract_id DESC;


-- 13. 合同模板表
SELECT
    template_id AS '模板ID',
    template_name AS '模板名称',
    CASE template_type
        WHEN '1' THEN '人才公寓'
        WHEN '2' THEN '保租房'
        WHEN '3' THEN '市场租赁'
        ELSE template_type
    END AS '模板类型',
    template_content AS '模板内容',
    version AS '版本号',
    CASE status
        WHEN '0' THEN '启用'
        WHEN '1' THEN '停用'
        ELSE status
    END AS '状态',
    create_time AS '创建时间'
FROM hz_contract_template
WHERE del_flag = '0'
ORDER BY template_id;


-- 14. 合同附件表
SELECT
    attachment_id AS '附件ID',
    contract_id AS '合同ID',
    attachment_name AS '附件名称',
    attachment_url AS '附件路径',
    attachment_type AS '附件类型',
    file_size AS '文件大小',
    upload_time AS '上传时间'
FROM hz_contract_attachment
WHERE del_flag = '0'
ORDER BY contract_id, upload_time;


-- ============================================
-- 四、入住/退租数据导出
-- ============================================

-- 15. 入住申请表
SELECT
    apply_id AS '申请ID',
    tenant_id AS '租户ID',
    tenant_name AS '租户姓名',
    project_id AS '项目ID',
    house_id AS '房源ID',
    house_code AS '房源编码',
    CASE apply_type
        WHEN '1' THEN '人才公寓'
        WHEN '2' THEN '保租房'
        WHEN '3' THEN '市场租赁'
        ELSE apply_type
    END AS '申请类型',
    CASE apply_status
        WHEN '0' THEN '待审核'
        WHEN '1' THEN '已通过'
        WHEN '2' THEN '已拒绝'
        WHEN '3' THEN '已取消'
        ELSE apply_status
    END AS '申请状态',
    apply_time AS '申请时间',
    approve_time AS '审核时间',
    approver AS '审核人',
    remark AS '备注'
FROM hz_checkin_apply
WHERE del_flag = '0'
ORDER BY apply_time DESC;


-- 16. 入住记录表
SELECT
    record_id AS '记录ID',
    tenant_id AS '租户ID',
    tenant_name AS '租户姓名',
    contract_id AS '合同ID',
    contract_no AS '合同编号',
    project_id AS '项目ID',
    project_name AS '项目名称',
    house_id AS '房源ID',
    house_code AS '房源编码',
    house_address AS '房源地址',
    checkin_date AS '入住日期',
    checkin_type AS '入住类型',
    operator AS '经办人',
    operator_id AS '经办人ID',
    remark AS '备注',
    create_time AS '创建时间'
FROM hz_checkin_record
WHERE del_flag = '0'
ORDER BY checkin_date DESC;


-- 17. 退租申请表
SELECT
    apply_id AS '申请ID',
    tenant_id AS '租户ID',
    tenant_name AS '租户姓名',
    contract_id AS '合同ID',
    contract_no AS '合同编号',
    house_id AS '房源ID',
    house_code AS '房源编码',
    expected_checkout_date AS '预计退租日期',
    checkout_reason AS '退租原因',
    CASE apply_status
        WHEN '0' THEN '待审核'
        WHEN '1' THEN '已通过'
        WHEN '2' THEN '已拒绝'
        WHEN '3' THEN '已取消'
        ELSE apply_status
    END AS '申请状态',
    apply_time AS '申请时间',
    approve_time AS '审核时间',
    approver AS '审核人',
    remark AS '备注'
FROM hz_checkout_apply
WHERE del_flag = '0'
ORDER BY apply_time DESC;


-- 18. 退租记录表
SELECT
    record_id AS '记录ID',
    tenant_id AS '租户ID',
    tenant_name AS '租户姓名',
    contract_id AS '合同ID',
    contract_no AS '合同编号',
    project_id AS '项目ID',
    project_name AS '项目名称',
    house_id AS '房源ID',
    house_code AS '房源编码',
    checkout_date AS '退租日期',
    checkout_reason AS '退租原因',
    rent_status AS '租金结算',
    deposit_status AS '押金退还',
    utility_status AS '水电结算',
    other_fee AS '其他费用',
    refund_amount AS '退款金额',
    operator AS '经办人',
    remark AS '备注',
    create_time AS '创建时间'
FROM hz_checkout_record
WHERE del_flag = '0'
ORDER BY checkout_date DESC;


-- 19. 资格校验表
SELECT
    qualification_id AS '校验ID',
    tenant_id AS '租户ID',
    tenant_name AS '租户姓名',
    id_card AS '身份证号',
    phone AS '手机号',
    CASE qualification_type
        WHEN '1' THEN '人才公寓'
        WHEN '2' THEN '保租房'
        ELSE qualification_type
    END AS '校验类型',
    education AS '学历',
    work_unit AS '工作单位',
    social_security_months AS '社保月数',
    household_type AS '户口性质',
    CASE qualification_status
        WHEN '0' THEN '待审核'
        WHEN '1' THEN '已通过'
        WHEN '2' THEN '未通过'
        ELSE qualification_status
    END AS '校验结果',
    fail_reason AS '失败原因',
    submit_time AS '提交时间',
    audit_time AS '审核时间',
    auditor AS '审核人'
FROM hz_qualification
WHERE del_flag = '0'
ORDER BY submit_time DESC;


-- 20. 资格申诉表
SELECT
    appeal_id AS '申诉ID',
    qualification_id AS '校验ID',
    tenant_id AS '租户ID',
    tenant_name AS '租户姓名',
    appeal_reason AS '申诉原因',
    appeal_content AS '申诉内容',
    appeal_attachments AS '申诉附件',
    CASE appeal_status
        WHEN '0' THEN '待处理'
        WHEN '1' THEN '已通过'
        WHEN '2' THEN '已拒绝'
        ELSE appeal_status
    END AS '申诉状态',
    reply_content AS '回复内容',
    submit_time AS '提交时间',
    handle_time AS '处理时间',
    handler AS '处理人'
FROM hz_qualification_appeal
WHERE del_flag = '0'
ORDER BY submit_time DESC;


-- ============================================
-- 五、财务账单数据导出
-- ============================================

-- 21. 账单信息表
SELECT
    bill_id AS '账单ID',
    tenant_id AS '租户ID',
    tenant_name AS '租户姓名',
    contract_id AS '合同ID',
    contract_no AS '合同编号',
    project_id AS '项目ID',
    project_name AS '项目名称',
    house_id AS '房源ID',
    house_code AS '房源编码',
    CASE bill_type
        WHEN '1' THEN '租金'
        WHEN '2' THEN '押金'
        WHEN '3' THEN '物业费'
        WHEN '4' THEN '水费'
        WHEN '5' THEN '电费'
        WHEN '6' THEN '燃气费'
        WHEN '7' THEN '暖气费'
        WHEN '8' THEN '其他'
        ELSE bill_type
    END AS '账单类型',
    bill_amount AS '账单金额',
    bill_period_start AS '账期开始',
    bill_period_end AS '账期结束',
    due_date AS '应缴日期',
    paid_amount AS '已缴金额',
    CASE bill_status
        WHEN '0' THEN '待缴费'
        WHEN '1' THEN '已缴费'
        WHEN '2' THEN '已逾期'
        WHEN '3' END AS '账单状态',
    overdue_days AS '逾期天数',
    late_fee AS '滞纳金',
    bill_date AS '账单日期',
    create_time AS '创建时间'
FROM hz_bill
WHERE del_flag = '0'
ORDER BY bill_date DESC;


-- 22. 缴费记录表
SELECT
    payment_id AS '缴费ID',
    tenant_id AS '租户ID',
    tenant_name AS '租户姓名',
    bill_id AS '账单ID',
    contract_id AS '合同ID',
    payment_amount AS '缴费金额',
    payment_method AS '缴费方式',
    payment_channel AS '缴费渠道',
    transaction_no AS '交易流水号',
    payment_time AS '缴费时间',
    CASE payment_status
        WHEN '0' THEN '待支付'
        WHEN '1' THEN '已支付'
        WHEN '2' THEN '已退款'
        ELSE payment_status
    END AS '支付状态',
    remark AS '备注'
FROM hz_payment
WHERE del_flag = '0'
ORDER BY payment_time DESC;


-- 23. 退款申请表
SELECT
    refund_id AS '退款ID',
    tenant_id AS '租户ID',
    tenant_name AS '租户姓名',
    contract_id AS '合同ID',
    contract_no AS '合同编号',
    refund_amount AS '退款金额',
    refund_reason AS '退款原因',
    CASE refund_status
        WHEN '0' THEN '待审核'
        WHEN '1' THEN '已通过'
        WHEN '2' THEN '已拒绝'
        WHEN '3' THEN '已退款'
        ELSE refund_status
    END AS '退款状态',
    bank_name AS '开户行',
    bank_account AS '银行账号',
    account_name AS '账户名',
    apply_time AS '申请时间',
    approve_time AS '审核时间',
    approver AS '审核人',
    remark AS '备注'
FROM hz_refund_apply
WHERE del_flag = '0'
ORDER BY apply_time DESC;


-- 24. 退款记录表
SELECT
    record_id AS '记录ID',
    refund_id AS '退款申请ID',
    refund_amount AS '退款金额',
    refund_time AS '退款时间',
    refund_method AS '退款方式',
    transaction_no AS '交易流水号',
    operator AS '经办人',
    remark AS '备注'
FROM hz_refund_record
WHERE del_flag = '0'
ORDER BY refund_time DESC;


-- 25. 发票申请表
SELECT
    invoice_id AS '发票ID',
    tenant_id AS '租户ID',
    tenant_name AS '租户姓名',
    contract_id AS '合同ID',
    bill_ids AS '关联账单',
    invoice_type AS '发票类型',
    invoice_title AS '发票抬头',
    tax_no AS '税号',
    invoice_amount AS '发票金额',
    CASE invoice_status
        WHEN '0' THEN '待开票'
        WHEN '1' THEN '已开票'
        WHEN '2' THEN '已拒绝'
        ELSE invoice_status
    END AS '开票状态',
    invoice_no AS '发票号码',
    invoice_url AS '发票文件',
    apply_time AS '申请时间',
    issue_time AS '开票时间',
    remark AS '备注'
FROM hz_invoice_apply
WHERE del_flag = '0'
ORDER BY apply_time DESC;


-- ============================================
-- 六、其他业务数据导出
-- ============================================

-- 26. 通知公告表
SELECT
    announcement_id AS '公告ID',
    announcement_title AS '公告标题',
    announcement_content AS '公告内容',
    announcement_type AS '公告类型',
    CASE is_top
        WHEN '0' THEN '否'
        WHEN '1' THEN '是'
        ELSE is_top
    END AS '是否置顶',
    view_count AS '浏览次数',
    publish_time AS '发布时间',
    publisher AS '发布人',
    CASE status
        WHEN '0' THEN '草稿'
        WHEN '1' THEN '已发布'
        ELSE status
    END AS '状态'
FROM hz_announcement
WHERE del_flag = '0'
ORDER BY publish_time DESC;


-- 27. 政策文件表
SELECT
    policy_id AS '政策ID',
    policy_title AS '政策标题',
    policy_content AS '政策内容',
    policy_type AS '政策类型',
    policy_file AS '政策文件',
    publish_date AS '发布日期',
    publisher AS '发布人',
    view_count AS '浏览次数',
    CASE status
        WHEN '0' THEN '草稿'
        WHEN '1' THEN '已发布'
        ELSE status
    END AS '状态',
    create_time AS '创建时间'
FROM hz_policy
WHERE del_flag = '0'
ORDER BY publish_date DESC;


-- 28. 优惠券表
SELECT
    coupon_id AS '优惠券ID',
    coupon_name AS '优惠券名称',
    CASE coupon_type
        WHEN '1' THEN '租金券'
        WHEN '2' THEN '押金券'
        WHEN '3' THEN '服务券'
        ELSE coupon_type
    END AS '优惠券类型',
    discount_type AS '优惠类型(1:金额 2:折扣)',
    discount_value AS '优惠值',
    min_amount AS '最低消费',
    max_discount AS '最大优惠',
    total_quantity AS '总数量',
    issued_quantity AS '已发放数量',
    used_quantity AS '已使用数量',
    valid_days AS '有效天数',
    start_date AS '开始日期',
    end_date AS '结束日期',
    CASE status
        WHEN '0' THEN '草稿'
        WHEN '1' THEN '进行中'
        WHEN '2' THEN '已结束'
        ELSE status
    END AS '状态',
    create_time AS '创建时间'
FROM hz_coupon
WHERE del_flag = '0'
ORDER BY create_time DESC;


-- 29. 优惠券领取记录表
SELECT
    receive_id AS '领取ID',
    coupon_id AS '优惠券ID',
    coupon_name AS '优惠券名称',
    tenant_id AS '租户ID',
    tenant_name AS '租户姓名',
    receive_time AS '领取时间',
    expire_time AS '过期时间',
    CASE use_status
        WHEN '0' THEN '未使用'
        WHEN '1' THEN '已使用'
        WHEN '2' THEN '已过期'
        ELSE use_status
    END AS '使用状态',
    use_time AS '使用时间',
    contract_id AS '使用合同ID'
FROM hz_coupon_receive
WHERE del_flag = '0'
ORDER BY receive_time DESC;


-- 30. 投诉记录表
SELECT
    complaint_id AS '投诉ID',
    tenant_id AS '租户ID',
    tenant_name AS '租户姓名',
    phone AS '联系电话',
    project_id AS '项目ID',
    project_name AS '项目名称',
    house_id AS '房源ID',
    house_code AS '房源编码',
    complaint_type AS '投诉类型',
    complaint_content AS '投诉内容',
    complaint_images AS '投诉图片',
    CASE complaint_status
        WHEN '0' THEN '待处理'
        WHEN '1' THEN '处理中'
        WHEN '2' THEN '已完成'
        WHEN '3' THEN '已关闭'
        ELSE complaint_status
    END AS '处理状态',
    handler AS '处理人',
    handle_result AS '处理结果',
    submit_time AS '提交时间',
    handle_time AS '处理时间',
    reply_content AS '回复内容'
FROM hz_complaint
WHERE del_flag = '0'
ORDER BY submit_time DESC;


-- 31. 报修记录表
SELECT
    repair_id AS '报修ID',
    tenant_id AS '租户ID',
    tenant_name AS '租户姓名',
    phone AS '联系电话',
    project_id AS '项目ID',
    project_name AS '项目名称',
    house_id AS '房源ID',
    house_code AS '房源编码',
    repair_type AS '报修类型',
    repair_content AS '报修内容',
    repair_images AS '报修图片',
    CASE repair_status
        WHEN '0' THEN '待派单'
        WHEN '1' THEN '已派单'
        WHEN '2' THEN '维修中'
        WHEN '3' THEN '已完成'
        WHEN '4' THEN '已取消'
        ELSE repair_status
    END AS '维修状态',
    repairman AS '维修人员',
    repairman_phone AS '维修电话',
    repair_cost AS '维修费用',
    submit_time AS '提交时间',
    assign_time AS '派单时间',
    complete_time AS '完成时间',
    remark AS '备注'
FROM hz_repair
WHERE del_flag = '0'
ORDER BY submit_time DESC;


-- 32. 预约看房表
SELECT
    appointment_id AS '预约ID',
    tenant_id AS '租户ID',
    tenant_name AS '租户姓名',
    phone AS '联系电话',
    project_id AS '项目ID',
    project_name AS '项目名称',
    house_id AS '房源ID',
    house_code AS '房源编码',
    appointment_date AS '预约日期',
    appointment_time AS '预约时间',
    CASE appointment_status
        WHEN '0' THEN '待确认'
        WHEN '1' THEN '已确认'
        WHEN '2' THEN '已完成'
        WHEN '3' THEN '已取消'
        ELSE appointment_status
    END AS '预约状态',
    CASE visit_status
        WHEN '0' THEN '未看房'
        WHEN '1' THEN '已看房'
        ELSE visit_status
    END AS '看房状态',
    visit_time AS '实际看房时间',
    visitor AS '接待人',
    remark AS '备注',
    create_time AS '创建时间'
FROM hz_appointment
WHERE del_flag = '0'
ORDER BY appointment_date DESC;


-- 33. 看房记录表
SELECT
    viewing_id AS '记录ID',
    appointment_id AS '预约ID',
    tenant_id AS '租户ID',
    tenant_name AS '租户姓名',
    project_id AS '项目ID',
    house_id AS '房源ID',
    viewing_time AS '看房时间',
    visitor AS '接待人',
    CASE result
        WHEN '1' THEN '满意'
        WHEN '2' THEN '一般'
        WHEN '3' THEN '不满意'
        ELSE result
    END AS '客户反馈',
    feedback_content AS '反馈内容',
    remark AS '备注'
FROM hz_viewing_record
WHERE del_flag = '0'
ORDER BY viewing_time DESC;


-- 34. 换房记录表
SELECT
    exchange_id AS '换房ID',
    tenant_id AS '租户ID',
    tenant_name AS '租户姓名',
    contract_id AS '原合同ID',
    original_house_id AS '原房源ID',
    original_house_code AS '原房源编码',
    new_house_id AS '新房源ID',
    new_house_code AS '新房源编码',
    exchange_reason AS '换房原因',
    exchange_date AS '换房日期',
    price_difference AS '差价',
    CASE exchange_status
        WHEN '0' THEN '待审核'
        WHEN '1' THEN '已通过'
        WHEN '2' THEN '已拒绝'
        ELSE exchange_status
    END AS '换房状态',
    approver AS '审核人',
    approve_time AS '审核时间',
    remark AS '备注'
FROM hz_house_exchange
WHERE del_flag = '0'
ORDER BY exchange_date DESC;


-- 35. 续租记录表
SELECT
    renewal_id AS '续租ID',
    tenant_id AS '租户ID',
    tenant_name AS '租户姓名',
    original_contract_id AS '原合同ID',
    new_contract_id AS '新合同ID',
    original_end_date AS '原到期日',
    new_end_date AS '新到期日',
    renewal_months AS '续租月数',
    new_rent_price AS '新租金',
    renewal_date AS '续租日期',
    CASE renewal_status
        WHEN '0' THEN '待审核'
        WHEN '1' THEN '已通过'
        WHEN '2' THEN '已拒绝'
        ELSE renewal_status
    END AS '续租状态',
    remark AS '备注'
FROM hz_house_exchange
WHERE del_flag = '0'
ORDER BY renewal_date DESC;


-- 36. 租户资料表
SELECT
    document_id AS '资料ID',
    tenant_id AS '租户ID',
    tenant_name AS '租户姓名',
    CASE document_type
        WHEN '1' THEN '身份证'
        WHEN '2' THEN '户口本'
        WHEN '3' THEN '学历证'
        WHEN '4' THEN '工作证明'
        WHEN '5' THEN '社保证明'
        WHEN '6' THEN '收入证明'
        WHEN '7' THEN '无房证明'
        WHEN '8' THEN '结婚证'
        WHEN '9' THEN '其他'
        ELSE document_type
    END AS '资料类型',
    document_name AS '资料名称',
    document_url AS '文件路径',
    file_size AS '文件大小',
    CASE audit_status
        WHEN '0' THEN '待审核'
        WHEN '1' THEN '已通过'
        WHEN '2' THEN '已拒绝'
        ELSE audit_status
    END AS '审核状态',
    audit_remark AS '审核意见',
    upload_time AS '上传时间',
    audit_time AS '审核时间'
FROM hz_tenant_document
WHERE del_flag = '0'
ORDER BY upload_time DESC;


-- 37. 批次分配表
SELECT
    batch_id AS '批次ID',
    batch_name AS '批次名称',
    batch_type AS '批次类型',
    project_id AS '项目ID',
    total_houses AS '房源总数',
    allocated_houses AS '已分配数',
    start_date AS '分配开始日期',
    end_date AS '分配结束日期',
    CASE batch_status
        WHEN '0' THEN '未开始'
        WHEN '1' THEN '进行中'
        WHEN '2' THEN '已结束'
        ELSE batch_status
    END AS '批次状态',
    create_time AS '创建时间'
FROM hz_batch_allocation
WHERE del_flag = '0'
ORDER BY create_time DESC;


-- 38. 企业批次表
SELECT
    id AS '企业批次ID',
    enterprise_id AS '企业ID',
    enterprise_name AS '企业名称',
    batch_id AS '批次ID',
    batch_name AS '批次名称',
    total_quota AS '总配额',
    used_quota AS '已使用配额',
    contact_person AS '联系人',
    contact_phone AS '联系电话',
    create_time AS '创建时间'
FROM hz_enterprise_batch
WHERE del_flag = '0'
ORDER BY create_time DESC;


-- 39. 消息通知表
SELECT
    message_id AS '消息ID',
    tenant_id AS '租户ID',
    tenant_name AS '租户姓名',
    message_type AS '消息类型',
    message_title AS '消息标题',
    message_content AS '消息内容',
    CASE read_status
        WHEN '0' THEN '未读'
        WHEN '1' THEN '已读'
        ELSE read_status
    END AS '阅读状态',
    send_time AS '发送时间',
    read_time AS '阅读时间'
FROM hz_user_message
WHERE del_flag = '0'
ORDER BY send_time DESC;


-- 40. Banner轮播图表
SELECT
    banner_id AS 'BannerID',
    banner_title AS '标题',
    banner_image AS '图片路径',
    link_type AS '链接类型',
    link_url AS '链接地址',
    sort_order AS '排序',
    CASE status
        WHEN '0' THEN '启用'
        WHEN '1' THEN '停用'
        ELSE status
    END AS '状态',
    start_time AS '开始时间',
    end_time AS '结束时间'
FROM hz_banner
WHERE del_flag = '0'
ORDER BY sort_order;


-- 41. 快捷入口表
SELECT
    shortcut_id AS '入口ID',
    shortcut_name AS '入口名称',
    shortcut_icon AS '图标',
    link_type AS '链接类型',
    link_url AS '链接地址',
    sort_order AS '排序',
    CASE status
        WHEN '0' THEN '启用'
        WHEN '1' THEN '停用'
        ELSE status
    END AS '状态'
FROM hz_shortcut
WHERE del_flag = '0'
ORDER BY sort_order;


-- 42. 特色房源表
SELECT
    featured_id AS '特色房源ID',
    house_id AS '房源ID',
    house_code AS '房源编码',
    project_name AS '项目名称',
    featured_type AS '特色类型',
    sort_order AS '排序',
    CASE status
        WHEN '0' THEN '启用'
        WHEN '1' THEN '停用'
        ELSE status
    END AS '状态',
    create_time AS '创建时间'
FROM hz_featured_house
WHERE del_flag = '0'
ORDER BY sort_order;


-- 43. 地图点位表
SELECT
    map_id AS '地图ID',
    project_id AS '项目ID',
    project_name AS '项目名称',
    longitude AS '经度',
    latitude AS '纬度',
    map_type AS '点位类型',
    sort_order AS '排序',
    CASE status
        WHEN '0' THEN '启用'
        WHEN '1' THEN '停用'
        ELSE status
    END AS '状态'
FROM hz_map_point
WHERE del_flag = '0'
ORDER BY sort_order;


-- 44. 黑名单表
SELECT
    blacklist_id AS '黑名单ID',
    tenant_id AS '租户ID',
    tenant_name AS '租户姓名',
    id_card AS '身份证号',
    phone AS '手机号',
    blacklist_type AS '黑名单类型',
    blacklist_reason AS '加入原因',
    CASE blacklist_status
        WHEN '0' THEN '永久'
        WHEN '1' THEN '临时'
        ELSE blacklist_status
    END AS '状态',
    lift_time AS '解除时间',
    create_time AS '创建时间'
FROM hz_blacklist
WHERE del_flag = '0'
ORDER BY create_time DESC;


-- ============================================
-- 导出完成
-- ============================================
