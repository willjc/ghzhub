-- sql/hz_house_order_migration.sql

-- 1. 新增选房预订单表
CREATE TABLE IF NOT EXISTS hz_house_order (
    order_id                BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '预订单ID',
    order_no                VARCHAR(32)    NOT NULL UNIQUE COMMENT '预订单号，格式 HO+yyyyMMddHHmmss+4位随机',
    tenant_id               BIGINT         NOT NULL COMMENT '租户ID',
    house_id                BIGINT         NOT NULL COMMENT '房源ID',
    project_id              BIGINT         NOT NULL COMMENT '项目ID',
    deposit_amount          DECIMAL(10,2)  NOT NULL COMMENT '押金金额',
    rent_price              DECIMAL(10,2)  NOT NULL COMMENT '月租金',
    lock_expire_time        DATETIME       NOT NULL COMMENT '房源锁定到期时间（选房时间+30min）',
    doc_upload_expire_time  DATETIME       NULL     COMMENT '资料上传截止时间（付款+72h，自选用户）',
    order_status            CHAR(1)        NOT NULL DEFAULT '0'
                            COMMENT '订单状态 0待签约 1待付押金 2待上传资料 3已完成 4已过期 5已取消',
    contract_id             BIGINT         NULL     COMMENT '关联合同ID（签约后填入）',
    esign_flow_id           VARCHAR(64)    NULL     COMMENT 'e签宝签署流程ID',
    deposit_bill_id         BIGINT         NULL     COMMENT '押金账单ID',
    is_batch_alloc          CHAR(1)        NOT NULL DEFAULT '0' COMMENT '是否配租 0否 1是',
    batch_id                BIGINT         NULL     COMMENT '配租批次ID',
    del_flag                CHAR(1)        NOT NULL DEFAULT '0' COMMENT '删除标志 0存在 2删除',
    create_by               VARCHAR(64)    NULL,
    create_time             DATETIME       NULL,
    update_by               VARCHAR(64)    NULL,
    update_time             DATETIME       NULL,
    remark                  VARCHAR(500)   NULL,
    INDEX idx_tenant_id     (tenant_id),
    INDEX idx_house_id      (house_id),
    INDEX idx_order_status  (order_status),
    INDEX idx_lock_expire   (lock_expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='选房预订单';

-- 2. hz_bill 新增 order_no 字段（用于微信回调反查预订单）
ALTER TABLE hz_bill
    ADD COLUMN IF NOT EXISTS order_no VARCHAR(32) NULL COMMENT '关联选房预订单号' AFTER bill_no;

-- 3. hz_tenant 新增 esign_psn_id 字段（供 Phase 3 e签宝使用）
ALTER TABLE hz_tenant
    ADD COLUMN IF NOT EXISTS esign_psn_id VARCHAR(64) NULL COMMENT 'e签宝个人账号ID' AFTER remark;

-- 4. 在 sys_job 中注册预订单过期定时任务（每1分钟扫描一次）
INSERT IGNORE INTO sys_job (job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, remark)
VALUES ('选房预订单过期检查', 'DEFAULT',
        'houseOrderExpireTask.checkExpiredOrders()',
        '0 * * * * ?',
        '3', '1', '0',
        'admin', NOW(),
        '每分钟检查过期的选房预订单，自动释放房源');
