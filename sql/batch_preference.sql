-- 批次配租优惠方式功能 SQL
-- 执行时间: 2026-02-10

-- 1. 配租批次表新增优惠字段
ALTER TABLE hz_batch_allocation ADD COLUMN `preferential_type` char(1) DEFAULT '0' COMMENT '优惠类型(0:无优惠 1:免租期数)' AFTER `remark`;
ALTER TABLE hz_batch_allocation ADD COLUMN `free_rent_periods` int DEFAULT '0' COMMENT '免租期数' AFTER `preferential_type`;

-- 2. 合同表新增批次关联和优惠字段
ALTER TABLE hz_contract ADD COLUMN `batch_id` bigint DEFAULT NULL COMMENT '配租批次ID' AFTER `template_id`;
ALTER TABLE hz_contract ADD COLUMN `free_rent_periods` int DEFAULT '0' COMMENT '免租期数' AFTER `payment_day`;

-- 3. 为合同表添加批次ID索引（可选，提高查询性能）
-- ALTER TABLE hz_contract ADD INDEX `idx_batch_id` (`batch_id`);

-- 执行验证
-- SELECT batch_id, batch_name, preferential_type, free_rent_periods FROM hz_batch_allocation;
-- DESC hz_contract;
