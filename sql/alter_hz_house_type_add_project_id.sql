-- ----------------------------
-- 为户型表添加项目ID字段
-- ----------------------------

-- 添加项目ID字段和索引
ALTER TABLE `hz_house_type`
ADD COLUMN `project_id` bigint NULL COMMENT '项目ID' AFTER `house_type_id`,
ADD INDEX `idx_project_id` (`project_id`);

-- 注意: 如果需要设置外键约束,可以执行以下语句(可选)
-- ALTER TABLE `hz_house_type`
-- ADD CONSTRAINT `fk_house_type_project`
-- FOREIGN KEY (`project_id`) REFERENCES `hz_project`(`project_id`);
