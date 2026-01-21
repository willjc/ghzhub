-- 为 hz_checkin_record 表添加入住单相关字段
-- 执行日期: 2025-01-15

USE newgangzhu;

-- 添加入住单号字段
ALTER TABLE hz_checkin_record ADD COLUMN checkin_no VARCHAR(50) NULL COMMENT '入住单号' AFTER record_id;

-- 添加实际入住日期字段
ALTER TABLE hz_checkin_record ADD COLUMN actual_checkin_date DATETIME NULL COMMENT '实际入住日期(用户选择)' AFTER checkin_date;

-- 添加合住人信息字段（JSON格式存储）
ALTER TABLE hz_checkin_record ADD COLUMN roommate_info TEXT NULL COMMENT '合住人信息(JSON数组)' AFTER actual_checkin_date;

-- 添加紧急联系人字段
ALTER TABLE hz_checkin_record ADD COLUMN emergency_contact_name VARCHAR(50) NULL COMMENT '紧急联系人姓名' AFTER roommate_info;
ALTER TABLE hz_checkin_record ADD COLUMN emergency_contact_relation VARCHAR(20) NULL COMMENT '紧急联系人关系' AFTER emergency_contact_name;
ALTER TABLE hz_checkin_record ADD COLUMN emergency_contact_phone VARCHAR(20) NULL COMMENT '紧急联系人电话' AFTER emergency_contact_relation;

-- 添加审核相关字段
ALTER TABLE hz_checkin_record ADD COLUMN audit_by VARCHAR(50) NULL COMMENT '审核人' AFTER emergency_contact_phone;
ALTER TABLE hz_checkin_record ADD COLUMN audit_time DATETIME NULL COMMENT '审核时间' AFTER audit_by;
ALTER TABLE hz_checkin_record ADD COLUMN audit_remark VARCHAR(500) NULL COMMENT '审核备注' AFTER audit_time;

-- 修改 status 字段注释，明确状态含义
ALTER TABLE hz_checkin_record MODIFY COLUMN status CHAR(1) DEFAULT '0' COMMENT '状态(0=待办理,1=待审核,2=已入住,3=已拒绝)';

-- 为入住单号添加唯一索引
CREATE UNIQUE INDEX idx_checkin_no ON hz_checkin_record(checkin_no);

-- 为入住单号添加示例数据更新（为现有数据生成入住单号）
UPDATE hz_checkin_record
SET checkin_no = CONCAT('RZ', DATE_FORMAT(create_time, '%Y%m%d%H%i%s'), LPAD(record_id, 4, '0'))
WHERE checkin_no IS NULL;

-- 查看表结构
DESC hz_checkin_record;
