-- ====================================
-- 港好住信息系统平台 - 业务表DDL脚本
-- 数据库: newgangzhu
-- 字符集: utf8
-- 创建日期: 2025-11-17
-- ====================================

USE newgangzhu;

-- ====================================
-- 1. 房源管理模块 (8张表)
-- ====================================

-- 1.1 项目表
DROP TABLE IF EXISTS `hz_project`;
CREATE TABLE `hz_project` (
  `project_id` bigint NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `project_name` varchar(100) NOT NULL COMMENT '项目名称',
  `project_code` varchar(50) NOT NULL COMMENT '项目编码',
  `project_type` char(1) NOT NULL COMMENT '项目类型(1:人才公寓 2:保租房 3:市场租赁)',
  `address` varchar(255) DEFAULT NULL COMMENT '项目地址',
  `longitude` decimal(10,7) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,7) DEFAULT NULL COMMENT '纬度',
  `total_buildings` int DEFAULT 0 COMMENT '总楼栋数',
  `total_houses` int DEFAULT 0 COMMENT '总房源数',
  `available_houses` int DEFAULT 0 COMMENT '可用房源数',
  `project_intro` text COMMENT '项目介绍',
  `facilities` varchar(500) DEFAULT NULL COMMENT '配套设施(多个用逗号分隔)',
  `transportation` varchar(500) DEFAULT NULL COMMENT '交通信息',
  `manager_id` bigint DEFAULT NULL COMMENT '项目负责人ID',
  `manager_name` varchar(50) DEFAULT NULL COMMENT '项目负责人姓名',
  `manager_phone` varchar(20) DEFAULT NULL COMMENT '负责人电话',
  `status` char(1) DEFAULT '0' COMMENT '状态(0:正常 1:停用)',
  `sort_order` int DEFAULT 0 COMMENT '显示顺序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`project_id`),
  UNIQUE KEY `uk_project_code` (`project_code`),
  KEY `idx_project_type` (`project_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目表';

-- 1.2 楼栋表
DROP TABLE IF EXISTS `hz_building`;
CREATE TABLE `hz_building` (
  `building_id` bigint NOT NULL AUTO_INCREMENT COMMENT '楼栋ID',
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `building_name` varchar(50) NOT NULL COMMENT '楼栋名称',
  `building_code` varchar(50) NOT NULL COMMENT '楼栋编码',
  `total_floors` int DEFAULT 0 COMMENT '总楼层数',
  `total_units` int DEFAULT 0 COMMENT '单元数',
  `total_houses` int DEFAULT 0 COMMENT '房源数',
  `status` char(1) DEFAULT '0' COMMENT '状态(0:正常 1:停用)',
  `sort_order` int DEFAULT 0 COMMENT '显示顺序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`building_id`),
  UNIQUE KEY `uk_building_code` (`building_code`),
  KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='楼栋表';

-- 1.3 单元表
DROP TABLE IF EXISTS `hz_unit`;
CREATE TABLE `hz_unit` (
  `unit_id` bigint NOT NULL AUTO_INCREMENT COMMENT '单元ID',
  `building_id` bigint NOT NULL COMMENT '楼栋ID',
  `unit_name` varchar(50) NOT NULL COMMENT '单元名称',
  `unit_code` varchar(50) NOT NULL COMMENT '单元编码',
  `total_houses` int DEFAULT 0 COMMENT '房源数',
  `status` char(1) DEFAULT '0' COMMENT '状态(0:正常 1:停用)',
  `sort_order` int DEFAULT 0 COMMENT '显示顺序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`unit_id`),
  UNIQUE KEY `uk_unit_code` (`unit_code`),
  KEY `idx_building_id` (`building_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='单元表';

-- 1.4 房源表
DROP TABLE IF EXISTS `hz_house`;
CREATE TABLE `hz_house` (
  `house_id` bigint NOT NULL AUTO_INCREMENT COMMENT '房源ID',
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `building_id` bigint NOT NULL COMMENT '楼栋ID',
  `unit_id` bigint DEFAULT NULL COMMENT '单元ID',
  `house_code` varchar(50) NOT NULL COMMENT '房源编码',
  `house_no` varchar(50) NOT NULL COMMENT '房间号',
  `floor` int NOT NULL COMMENT '楼层',
  `house_type_id` bigint DEFAULT NULL COMMENT '户型ID',
  `house_type_name` varchar(50) DEFAULT NULL COMMENT '户型名称',
  `area` decimal(8,2) DEFAULT NULL COMMENT '建筑面积(平方米)',
  `rent_price` decimal(10,2) DEFAULT NULL COMMENT '租金(元/月)',
  `deposit` decimal(10,2) DEFAULT NULL COMMENT '押金(元)',
  `orientation` varchar(20) DEFAULT NULL COMMENT '朝向',
  `decoration` varchar(20) DEFAULT NULL COMMENT '装修情况',
  `facilities` varchar(500) DEFAULT NULL COMMENT '房间设施(多个用逗号分隔)',
  `house_status` char(1) DEFAULT '0' COMMENT '房源状态(0:空置 1:已预订 2:已出租 3:维修中 4:下架)',
  `is_featured` char(1) DEFAULT '0' COMMENT '是否精选房源(0:否 1:是)',
  `view_count` int DEFAULT 0 COMMENT '浏览次数',
  `status` char(1) DEFAULT '0' COMMENT '状态(0:正常 1:停用)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`house_id`),
  UNIQUE KEY `uk_house_code` (`house_code`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_building_id` (`building_id`),
  KEY `idx_house_status` (`house_status`),
  KEY `idx_is_featured` (`is_featured`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房源表';

-- 1.5 房源图片表
DROP TABLE IF EXISTS `hz_house_image`;
CREATE TABLE `hz_house_image` (
  `image_id` bigint NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `house_id` bigint NOT NULL COMMENT '房源ID',
  `image_url` varchar(255) NOT NULL COMMENT '图片URL',
  `image_type` char(1) DEFAULT '1' COMMENT '图片类型(1:实景图 2:户型图)',
  `is_cover` char(1) DEFAULT '0' COMMENT '是否封面(0:否 1:是)',
  `sort_order` int DEFAULT 0 COMMENT '显示顺序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`image_id`),
  KEY `idx_house_id` (`house_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房源图片表';

-- 1.6 房源VR表
DROP TABLE IF EXISTS `hz_house_vr`;
CREATE TABLE `hz_house_vr` (
  `vr_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'VRID',
  `house_id` bigint NOT NULL COMMENT '房源ID',
  `vr_name` varchar(100) DEFAULT NULL COMMENT 'VR名称',
  `vr_url` varchar(255) NOT NULL COMMENT 'VR全景图URL',
  `sort_order` int DEFAULT 0 COMMENT '显示顺序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`vr_id`),
  KEY `idx_house_id` (`house_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房源VR表';

-- 1.7 户型表
DROP TABLE IF EXISTS `hz_house_type`;
CREATE TABLE `hz_house_type` (
  `house_type_id` bigint NOT NULL AUTO_INCREMENT COMMENT '户型ID',
  `house_type_name` varchar(50) NOT NULL COMMENT '户型名称(如:一室一厅)',
  `house_type_code` varchar(50) NOT NULL COMMENT '户型编码',
  `bedroom_count` int DEFAULT 0 COMMENT '卧室数量',
  `livingroom_count` int DEFAULT 0 COMMENT '客厅数量',
  `bathroom_count` int DEFAULT 0 COMMENT '卫生间数量',
  `kitchen_count` int DEFAULT 0 COMMENT '厨房数量',
  `balcony_count` int DEFAULT 0 COMMENT '阳台数量',
  `typical_area` decimal(8,2) DEFAULT NULL COMMENT '典型面积(平方米)',
  `layout_image` varchar(255) DEFAULT NULL COMMENT '户型图URL',
  `status` char(1) DEFAULT '0' COMMENT '状态(0:正常 1:停用)',
  `sort_order` int DEFAULT 0 COMMENT '显示顺序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`house_type_id`),
  UNIQUE KEY `uk_house_type_code` (`house_type_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='户型表';

-- 1.8 项目负责人表
DROP TABLE IF EXISTS `hz_project_manager`;
CREATE TABLE `hz_project_manager` (
  `manager_id` bigint NOT NULL AUTO_INCREMENT COMMENT '负责人ID',
  `user_id` bigint DEFAULT NULL COMMENT '关联用户ID',
  `manager_name` varchar(50) NOT NULL COMMENT '负责人姓名',
  `phone` varchar(20) NOT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `status` char(1) DEFAULT '0' COMMENT '状态(0:正常 1:停用)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`manager_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目负责人表';

-- ====================================
-- 2. 租户管理模块 (5张表)
-- ====================================

-- 2.1 租户表
DROP TABLE IF EXISTS `hz_tenant`;
CREATE TABLE `hz_tenant` (
  `tenant_id` bigint NOT NULL AUTO_INCREMENT COMMENT '租户ID',
  `user_id` bigint DEFAULT NULL COMMENT '关联用户ID(郑好办用户)',
  `tenant_name` varchar(50) NOT NULL COMMENT '租户姓名',
  `id_card` varchar(18) NOT NULL COMMENT '身份证号',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `gender` char(1) DEFAULT NULL COMMENT '性别(0:男 1:女)',
  `birth_date` date DEFAULT NULL COMMENT '出生日期',
  `education` varchar(20) DEFAULT NULL COMMENT '学历',
  `marriage_status` varchar(20) DEFAULT NULL COMMENT '婚姻状况',
  `work_unit` varchar(100) DEFAULT NULL COMMENT '工作单位',
  `emergency_contact` varchar(50) DEFAULT NULL COMMENT '紧急联系人',
  `emergency_phone` varchar(20) DEFAULT NULL COMMENT '紧急联系人电话',
  `household_register` varchar(100) DEFAULT NULL COMMENT '户籍地址',
  `current_address` varchar(255) DEFAULT NULL COMMENT '现住址',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `wechat` varchar(50) DEFAULT NULL COMMENT '微信号',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `credit_score` int DEFAULT 100 COMMENT '信用分(默认100)',
  `is_blacklist` char(1) DEFAULT '0' COMMENT '是否黑名单(0:否 1:是)',
  `status` char(1) DEFAULT '0' COMMENT '状态(0:正常 1:停用)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`tenant_id`),
  UNIQUE KEY `uk_id_card` (`id_card`),
  KEY `idx_phone` (`phone`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户表';

-- 2.2 资格审核表
DROP TABLE IF EXISTS `hz_qualification`;
CREATE TABLE `hz_qualification` (
  `qualification_id` bigint NOT NULL AUTO_INCREMENT COMMENT '资格ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `apply_type` char(1) NOT NULL COMMENT '申请类型(1:人才公寓 2:保租房 3:市场租赁)',
  `apply_time` datetime DEFAULT NULL COMMENT '申请时间',
  `id_card` varchar(18) NOT NULL COMMENT '身份证号',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `social_security_months` int DEFAULT 0 COMMENT '社保缴纳月数',
  `has_local_house` char(1) DEFAULT '0' COMMENT '本地是否有房(0:无 1:有)',
  `education_level` varchar(20) DEFAULT NULL COMMENT '学历',
  `marriage_status` varchar(20) DEFAULT NULL COMMENT '婚姻状况',
  `household_count` int DEFAULT 1 COMMENT '家庭人口数',
  `work_unit` varchar(100) DEFAULT NULL COMMENT '工作单位',
  `monthly_income` decimal(10,2) DEFAULT NULL COMMENT '月收入(元)',
  `auto_check_result` char(1) DEFAULT '0' COMMENT '自动审核结果(0:待审核 1:通过 2:不通过)',
  `auto_check_reason` varchar(500) DEFAULT NULL COMMENT '自动审核原因',
  `manual_check_result` char(1) DEFAULT NULL COMMENT '人工审核结果(0:待审核 1:通过 2:不通过)',
  `manual_check_reason` varchar(500) DEFAULT NULL COMMENT '人工审核原因',
  `final_result` char(1) DEFAULT '0' COMMENT '最终结果(0:待审核 1:通过 2:不通过)',
  `check_by` varchar(64) DEFAULT NULL COMMENT '审核人',
  `check_time` datetime DEFAULT NULL COMMENT '审核时间',
  `status` char(1) DEFAULT '0' COMMENT '状态(0:待审核 1:审核中 2:已审核)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`qualification_id`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_apply_type` (`apply_type`),
  KEY `idx_final_result` (`final_result`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资格审核表';

-- 2.3 资格申诉表
DROP TABLE IF EXISTS `hz_qualification_appeal`;
CREATE TABLE `hz_qualification_appeal` (
  `appeal_id` bigint NOT NULL AUTO_INCREMENT COMMENT '申诉ID',
  `qualification_id` bigint NOT NULL COMMENT '资格ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `appeal_reason` text NOT NULL COMMENT '申诉理由',
  `appeal_attachments` varchar(1000) DEFAULT NULL COMMENT '申诉附件(多个用逗号分隔)',
  `appeal_time` datetime DEFAULT NULL COMMENT '申诉时间',
  `handle_result` char(1) DEFAULT '0' COMMENT '处理结果(0:待处理 1:通过 2:驳回)',
  `handle_opinion` varchar(500) DEFAULT NULL COMMENT '处理意见',
  `handle_by` varchar(64) DEFAULT NULL COMMENT '处理人',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `status` char(1) DEFAULT '0' COMMENT '状态(0:待处理 1:已处理)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`appeal_id`),
  KEY `idx_qualification_id` (`qualification_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资格申诉表';

-- 2.4 承诺书表
DROP TABLE IF EXISTS `hz_commitment`;
CREATE TABLE `hz_commitment` (
  `commitment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '承诺书ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `commitment_type` char(1) NOT NULL COMMENT '承诺书类型(1:人才公寓 2:保租房)',
  `commitment_content` text NOT NULL COMMENT '承诺书内容',
  `signature_data` text COMMENT '签名数据(base64)',
  `sign_time` datetime DEFAULT NULL COMMENT '签署时间',
  `sign_ip` varchar(50) DEFAULT NULL COMMENT '签署IP',
  `status` char(1) DEFAULT '0' COMMENT '状态(0:未签署 1:已签署)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`commitment_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='承诺书表';

-- 2.5 黑名单表
DROP TABLE IF EXISTS `hz_blacklist`;
CREATE TABLE `hz_blacklist` (
  `blacklist_id` bigint NOT NULL AUTO_INCREMENT COMMENT '黑名单ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `id_card` varchar(18) NOT NULL COMMENT '身份证号',
  `tenant_name` varchar(50) NOT NULL COMMENT '租户姓名',
  `blacklist_reason` varchar(500) NOT NULL COMMENT '列入原因',
  `blacklist_time` datetime DEFAULT NULL COMMENT '列入时间',
  `remove_time` datetime DEFAULT NULL COMMENT '移除时间',
  `status` char(1) DEFAULT '1' COMMENT '状态(0:已移除 1:生效中)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`blacklist_id`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_id_card` (`id_card`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='黑名单表';

-- ====================================
-- 3. 合同管理模块 (6张表)
-- ====================================

-- 3.1 合同表
DROP TABLE IF EXISTS `hz_contract`;
CREATE TABLE `hz_contract` (
  `contract_id` bigint NOT NULL AUTO_INCREMENT COMMENT '合同ID',
  `contract_no` varchar(50) NOT NULL COMMENT '合同编号',
  `contract_type` char(1) NOT NULL COMMENT '合同类型(1:人才公寓 2:保租房 3:市场租赁)',
  `template_id` bigint DEFAULT NULL COMMENT '合同模板ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `tenant_name` varchar(50) NOT NULL COMMENT '租户姓名',
  `tenant_id_card` varchar(18) NOT NULL COMMENT '租户身份证',
  `tenant_phone` varchar(20) NOT NULL COMMENT '租户手机',
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `house_id` bigint NOT NULL COMMENT '房源ID',
  `house_code` varchar(50) NOT NULL COMMENT '房源编码',
  `house_address` varchar(255) DEFAULT NULL COMMENT '房源地址',
  `rent_price` decimal(10,2) NOT NULL COMMENT '租金(元/月)',
  `deposit` decimal(10,2) NOT NULL COMMENT '押金(元)',
  `start_date` date NOT NULL COMMENT '租赁开始日期',
  `end_date` date NOT NULL COMMENT '租赁结束日期',
  `rent_months` int NOT NULL COMMENT '租赁月数',
  `payment_cycle` char(1) DEFAULT '1' COMMENT '缴费周期(1:月付 2:季付 3:半年付 4:年付)',
  `payment_day` int DEFAULT 5 COMMENT '每月缴费日',
  `contract_content` text COMMENT '合同内容',
  `contract_file` varchar(255) DEFAULT NULL COMMENT '合同文件URL',
  `tenant_signature` text COMMENT '租户签名数据(base64)',
  `landlord_signature` text COMMENT '房东签名数据(base64)',
  `sign_time` datetime DEFAULT NULL COMMENT '签署时间',
  `contract_status` char(1) DEFAULT '0' COMMENT '合同状态(0:草稿 1:待签署 2:已签署 3:履行中 4:已到期 5:已解约)',
  `is_renewed` char(1) DEFAULT '0' COMMENT '是否续约(0:否 1:是)',
  `renewed_contract_id` bigint DEFAULT NULL COMMENT '续约合同ID',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`contract_id`),
  UNIQUE KEY `uk_contract_no` (`contract_no`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_house_id` (`house_id`),
  KEY `idx_contract_status` (`contract_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同表';

-- 3.2 合同模板表
DROP TABLE IF EXISTS `hz_contract_template`;
CREATE TABLE `hz_contract_template` (
  `template_id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_name` varchar(100) NOT NULL COMMENT '模板名称',
  `template_code` varchar(50) NOT NULL COMMENT '模板编码',
  `contract_type` char(1) NOT NULL COMMENT '合同类型(1:人才公寓 2:保租房 3:市场租赁)',
  `template_content` text NOT NULL COMMENT '模板内容(支持占位符)',
  `version` varchar(20) DEFAULT '1.0' COMMENT '版本号',
  `is_default` char(1) DEFAULT '0' COMMENT '是否默认模板(0:否 1:是)',
  `status` char(1) DEFAULT '0' COMMENT '状态(0:正常 1:停用)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`template_id`),
  UNIQUE KEY `uk_template_code` (`template_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同模板表';

-- 3.3 合同附件表
DROP TABLE IF EXISTS `hz_contract_attachment`;
CREATE TABLE `hz_contract_attachment` (
  `attachment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '附件ID',
  `contract_id` bigint NOT NULL COMMENT '合同ID',
  `attachment_name` varchar(100) NOT NULL COMMENT '附件名称',
  `attachment_url` varchar(255) NOT NULL COMMENT '附件URL',
  `attachment_type` varchar(20) DEFAULT NULL COMMENT '附件类型',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小(字节)',
  `sort_order` int DEFAULT 0 COMMENT '显示顺序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`attachment_id`),
  KEY `idx_contract_id` (`contract_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同附件表';

-- 3.4 合同签署记录表
DROP TABLE IF EXISTS `hz_contract_signature`;
CREATE TABLE `hz_contract_signature` (
  `signature_id` bigint NOT NULL AUTO_INCREMENT COMMENT '签署ID',
  `contract_id` bigint NOT NULL COMMENT '合同ID',
  `signer_type` char(1) NOT NULL COMMENT '签署人类型(1:租户 2:房东 3:共同租户)',
  `signer_id` bigint NOT NULL COMMENT '签署人ID',
  `signer_name` varchar(50) NOT NULL COMMENT '签署人姓名',
  `signature_data` text COMMENT '签名数据(base64)',
  `sign_time` datetime DEFAULT NULL COMMENT '签署时间',
  `sign_ip` varchar(50) DEFAULT NULL COMMENT '签署IP',
  `sign_location` varchar(100) DEFAULT NULL COMMENT '签署地点',
  `status` char(1) DEFAULT '0' COMMENT '状态(0:未签署 1:已签署)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`signature_id`),
  KEY `idx_contract_id` (`contract_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同签署记录表';

-- 3.5 共同租户表
DROP TABLE IF EXISTS `hz_co_tenant`;
CREATE TABLE `hz_co_tenant` (
  `co_tenant_id` bigint NOT NULL AUTO_INCREMENT COMMENT '共同租户ID',
  `contract_id` bigint NOT NULL COMMENT '合同ID',
  `tenant_name` varchar(50) NOT NULL COMMENT '租户姓名',
  `id_card` varchar(18) NOT NULL COMMENT '身份证号',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `relationship` varchar(20) DEFAULT NULL COMMENT '与主租户关系',
  `status` char(1) DEFAULT '0' COMMENT '状态(0:正常 1:停用)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`co_tenant_id`),
  KEY `idx_contract_id` (`contract_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='共同租户表';

-- 3.6 换房记录表
DROP TABLE IF EXISTS `hz_house_exchange`;
CREATE TABLE `hz_house_exchange` (
  `exchange_id` bigint NOT NULL AUTO_INCREMENT COMMENT '换房ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `old_contract_id` bigint NOT NULL COMMENT '原合同ID',
  `old_house_id` bigint NOT NULL COMMENT '原房源ID',
  `old_house_code` varchar(50) NOT NULL COMMENT '原房源编码',
  `new_house_id` bigint NOT NULL COMMENT '新房源ID',
  `new_house_code` varchar(50) NOT NULL COMMENT '新房源编码',
  `exchange_reason` varchar(500) DEFAULT NULL COMMENT '换房原因',
  `apply_time` datetime DEFAULT NULL COMMENT '申请时间',
  `approve_result` char(1) DEFAULT '0' COMMENT '审批结果(0:待审批 1:通过 2:驳回)',
  `approve_opinion` varchar(500) DEFAULT NULL COMMENT '审批意见',
  `approve_by` varchar(64) DEFAULT NULL COMMENT '审批人',
  `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
  `exchange_time` datetime DEFAULT NULL COMMENT '换房时间',
  `new_contract_id` bigint DEFAULT NULL COMMENT '新合同ID',
  `status` char(1) DEFAULT '0' COMMENT '状态(0:待审批 1:已批准 2:已驳回 3:已完成)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`exchange_id`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_old_contract_id` (`old_contract_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='换房记录表';

-- ====================================
-- 4. 入住退租模块 (6张表)
-- ====================================

-- 4.1 入住申请表
DROP TABLE IF EXISTS `hz_checkin_apply`;
CREATE TABLE `hz_checkin_apply` (
  `apply_id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `contract_id` bigint NOT NULL COMMENT '合同ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `house_id` bigint NOT NULL COMMENT '房源ID',
  `apply_time` datetime DEFAULT NULL COMMENT '申请时间',
  `plan_checkin_date` date DEFAULT NULL COMMENT '计划入住日期',
  `deposit_paid` char(1) DEFAULT '0' COMMENT '是否已缴押金(0:否 1:是)',
  `deposit_amount` decimal(10,2) DEFAULT NULL COMMENT '押金金额',
  `deposit_pay_time` datetime DEFAULT NULL COMMENT '押金缴纳时间',
  `apply_status` char(1) DEFAULT '0' COMMENT '申请状态(0:待审核 1:已通过 2:已驳回 3:已入住)',
  `approve_by` varchar(64) DEFAULT NULL COMMENT '审批人',
  `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
  `approve_opinion` varchar(500) DEFAULT NULL COMMENT '审批意见',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`apply_id`),
  KEY `idx_contract_id` (`contract_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入住申请表';

-- 4.2 入住记录表
DROP TABLE IF EXISTS `hz_checkin_record`;
CREATE TABLE `hz_checkin_record` (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `apply_id` bigint NOT NULL COMMENT '申请ID',
  `contract_id` bigint NOT NULL COMMENT '合同ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `house_id` bigint NOT NULL COMMENT '房源ID',
  `checkin_date` date NOT NULL COMMENT '入住日期',
  `checkin_time` datetime DEFAULT NULL COMMENT '入住时间',
  `meter_reading_electric` decimal(10,2) DEFAULT NULL COMMENT '电表读数',
  `meter_reading_water` decimal(10,2) DEFAULT NULL COMMENT '水表读数',
  `meter_reading_gas` decimal(10,2) DEFAULT NULL COMMENT '燃气表读数',
  `key_count` int DEFAULT 0 COMMENT '钥匙数量',
  `inventory_list_id` bigint DEFAULT NULL COMMENT '清单ID',
  `checkin_photos` varchar(1000) DEFAULT NULL COMMENT '入住照片(多个用逗号分隔)',
  `tenant_signature` text COMMENT '租户签名(base64)',
  `manager_signature` text COMMENT '管理员签名(base64)',
  `manager_id` bigint DEFAULT NULL COMMENT '经办人ID',
  `manager_name` varchar(50) DEFAULT NULL COMMENT '经办人姓名',
  `status` char(1) DEFAULT '1' COMMENT '状态(0:已退租 1:在住)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`record_id`),
  KEY `idx_contract_id` (`contract_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入住记录表';

-- 4.3 退租申请表
DROP TABLE IF EXISTS `hz_checkout_apply`;
CREATE TABLE `hz_checkout_apply` (
  `apply_id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `contract_id` bigint NOT NULL COMMENT '合同ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `house_id` bigint NOT NULL COMMENT '房源ID',
  `apply_time` datetime DEFAULT NULL COMMENT '申请时间',
  `plan_checkout_date` date DEFAULT NULL COMMENT '计划退租日期',
  `checkout_reason` varchar(500) DEFAULT NULL COMMENT '退租原因',
  `is_early_termination` char(1) DEFAULT '0' COMMENT '是否提前解约(0:否 1:是)',
  `penalty_amount` decimal(10,2) DEFAULT 0 COMMENT '违约金金额',
  `unpaid_bills` decimal(10,2) DEFAULT 0 COMMENT '未结账单金额',
  `deposit_refund` decimal(10,2) DEFAULT 0 COMMENT '应退押金',
  `apply_status` char(1) DEFAULT '0' COMMENT '申请状态(0:待审核 1:已通过 2:已驳回 3:已退租)',
  `approve_by` varchar(64) DEFAULT NULL COMMENT '审批人',
  `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
  `approve_opinion` varchar(500) DEFAULT NULL COMMENT '审批意见',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`apply_id`),
  KEY `idx_contract_id` (`contract_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退租申请表';

-- 4.4 退租记录表
DROP TABLE IF EXISTS `hz_checkout_record`;
CREATE TABLE `hz_checkout_record` (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `apply_id` bigint NOT NULL COMMENT '申请ID',
  `contract_id` bigint NOT NULL COMMENT '合同ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `house_id` bigint NOT NULL COMMENT '房源ID',
  `checkout_date` date NOT NULL COMMENT '退租日期',
  `checkout_time` datetime DEFAULT NULL COMMENT '退租时间',
  `meter_reading_electric` decimal(10,2) DEFAULT NULL COMMENT '电表读数',
  `meter_reading_water` decimal(10,2) DEFAULT NULL COMMENT '水表读数',
  `meter_reading_gas` decimal(10,2) DEFAULT NULL COMMENT '燃气表读数',
  `key_returned` int DEFAULT 0 COMMENT '归还钥匙数量',
  `inventory_list_id` bigint DEFAULT NULL COMMENT '清单ID',
  `checkout_photos` varchar(1000) DEFAULT NULL COMMENT '退租照片(多个用逗号分隔)',
  `damage_description` varchar(500) DEFAULT NULL COMMENT '损坏情况描述',
  `damage_deduction` decimal(10,2) DEFAULT 0 COMMENT '损坏赔偿金额',
  `utility_bill` decimal(10,2) DEFAULT 0 COMMENT '水电燃气费',
  `unpaid_rent` decimal(10,2) DEFAULT 0 COMMENT '欠缴租金',
  `penalty_amount` decimal(10,2) DEFAULT 0 COMMENT '违约金',
  `deposit_refund` decimal(10,2) DEFAULT 0 COMMENT '实退押金',
  `refund_status` char(1) DEFAULT '0' COMMENT '退款状态(0:待退款 1:已退款)',
  `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
  `tenant_signature` text COMMENT '租户签名(base64)',
  `manager_signature` text COMMENT '管理员签名(base64)',
  `manager_id` bigint DEFAULT NULL COMMENT '经办人ID',
  `manager_name` varchar(50) DEFAULT NULL COMMENT '经办人姓名',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`record_id`),
  KEY `idx_contract_id` (`contract_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退租记录表';

-- 4.5 物品清单表
DROP TABLE IF EXISTS `hz_inventory_list`;
CREATE TABLE `hz_inventory_list` (
  `list_id` bigint NOT NULL AUTO_INCREMENT COMMENT '清单ID',
  `house_id` bigint NOT NULL COMMENT '房源ID',
  `list_type` char(1) NOT NULL COMMENT '清单类型(1:入住 2:退租)',
  `list_items` text COMMENT '清单项目(JSON格式)',
  `template_id` bigint DEFAULT NULL COMMENT '清单模板ID',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`list_id`),
  KEY `idx_house_id` (`house_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物品清单表';

-- 4.6 物品状态表
DROP TABLE IF EXISTS `hz_item_status`;
CREATE TABLE `hz_item_status` (
  `status_id` bigint NOT NULL AUTO_INCREMENT COMMENT '状态ID',
  `list_id` bigint NOT NULL COMMENT '清单ID',
  `item_name` varchar(100) NOT NULL COMMENT '物品名称',
  `item_category` varchar(50) DEFAULT NULL COMMENT '物品分类',
  `quantity` int DEFAULT 1 COMMENT '数量',
  `item_status` varchar(20) DEFAULT NULL COMMENT '物品状态(完好/损坏/缺失)',
  `damage_level` varchar(20) DEFAULT NULL COMMENT '损坏程度',
  `item_photo` varchar(255) DEFAULT NULL COMMENT '物品照片',
  `sort_order` int DEFAULT 0 COMMENT '显示顺序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`status_id`),
  KEY `idx_list_id` (`list_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物品状态表';

-- ====================================
-- 5. 账单缴费模块 (8张表)
-- ====================================

-- 5.1 账单表
DROP TABLE IF EXISTS `hz_bill`;
CREATE TABLE `hz_bill` (
  `bill_id` bigint NOT NULL AUTO_INCREMENT COMMENT '账单ID',
  `bill_no` varchar(50) NOT NULL COMMENT '账单编号',
  `contract_id` bigint NOT NULL COMMENT '合同ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `tenant_name` varchar(50) NOT NULL COMMENT '租户姓名',
  `house_id` bigint NOT NULL COMMENT '房源ID',
  `house_code` varchar(50) NOT NULL COMMENT '房源编码',
  `bill_type` char(1) NOT NULL COMMENT '账单类型(1:押金 2:租金 3:水费 4:电费 5:燃气费 6:物业费 7:其他)',
  `bill_period` varchar(20) DEFAULT NULL COMMENT '账单周期(如:2025-01)',
  `bill_amount` decimal(10,2) NOT NULL COMMENT '账单金额',
  `paid_amount` decimal(10,2) DEFAULT 0 COMMENT '已支付金额',
  `unpaid_amount` decimal(10,2) DEFAULT 0 COMMENT '未支付金额',
  `bill_date` date DEFAULT NULL COMMENT '账单日期',
  `due_date` date DEFAULT NULL COMMENT '应缴日期',
  `late_fee` decimal(10,2) DEFAULT 0 COMMENT '滞纳金',
  `bill_status` char(1) DEFAULT '0' COMMENT '账单状态(0:待支付 1:已支付 2:部分支付 3:已逾期 4:已关闭)',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `pay_method` varchar(20) DEFAULT NULL COMMENT '支付方式',
  `transaction_no` varchar(50) DEFAULT NULL COMMENT '交易流水号',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`bill_id`),
  UNIQUE KEY `uk_bill_no` (`bill_no`),
  KEY `idx_contract_id` (`contract_id`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_bill_status` (`bill_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单表';

-- 5.2 缴费记录表
DROP TABLE IF EXISTS `hz_payment`;
CREATE TABLE `hz_payment` (
  `payment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '缴费ID',
  `payment_no` varchar(50) NOT NULL COMMENT '缴费编号',
  `bill_id` bigint NOT NULL COMMENT '账单ID',
  `bill_no` varchar(50) NOT NULL COMMENT '账单编号',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `tenant_name` varchar(50) NOT NULL COMMENT '租户姓名',
  `payment_amount` decimal(10,2) NOT NULL COMMENT '缴费金额',
  `payment_method` varchar(20) NOT NULL COMMENT '支付方式(支付宝/微信/银行卡等)',
  `payment_channel` varchar(20) DEFAULT NULL COMMENT '支付渠道(港区支付)',
  `transaction_no` varchar(100) DEFAULT NULL COMMENT '第三方交易流水号',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `payment_status` char(1) DEFAULT '0' COMMENT '支付状态(0:待支付 1:支付成功 2:支付失败 3:已退款)',
  `callback_time` datetime DEFAULT NULL COMMENT '回调时间',
  `callback_data` text COMMENT '回调数据',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`payment_id`),
  UNIQUE KEY `uk_payment_no` (`payment_no`),
  KEY `idx_bill_id` (`bill_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='缴费记录表';

-- 5.3 退款申请表
DROP TABLE IF EXISTS `hz_refund_apply`;
CREATE TABLE `hz_refund_apply` (
  `apply_id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `apply_no` varchar(50) NOT NULL COMMENT '申请编号',
  `payment_id` bigint NOT NULL COMMENT '缴费ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `tenant_name` varchar(50) NOT NULL COMMENT '租户姓名',
  `refund_amount` decimal(10,2) NOT NULL COMMENT '退款金额',
  `refund_reason` varchar(500) NOT NULL COMMENT '退款原因',
  `apply_time` datetime DEFAULT NULL COMMENT '申请时间',
  `bank_name` varchar(100) DEFAULT NULL COMMENT '开户行',
  `bank_account` varchar(50) DEFAULT NULL COMMENT '银行账号',
  `account_name` varchar(50) DEFAULT NULL COMMENT '账户名',
  `approve_status` char(1) DEFAULT '0' COMMENT '审批状态(0:待审批 1:已通过 2:已驳回)',
  `approve_by` varchar(64) DEFAULT NULL COMMENT '审批人',
  `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
  `approve_opinion` varchar(500) DEFAULT NULL COMMENT '审批意见',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`apply_id`),
  UNIQUE KEY `uk_apply_no` (`apply_no`),
  KEY `idx_payment_id` (`payment_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款申请表';

-- 5.4 退款记录表
DROP TABLE IF EXISTS `hz_refund_record`;
CREATE TABLE `hz_refund_record` (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `refund_no` varchar(50) NOT NULL COMMENT '退款编号',
  `apply_id` bigint NOT NULL COMMENT '申请ID',
  `payment_id` bigint NOT NULL COMMENT '缴费ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `refund_amount` decimal(10,2) NOT NULL COMMENT '退款金额',
  `refund_method` varchar(20) DEFAULT NULL COMMENT '退款方式',
  `refund_account` varchar(100) DEFAULT NULL COMMENT '退款账户',
  `transaction_no` varchar(100) DEFAULT NULL COMMENT '交易流水号',
  `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
  `refund_status` char(1) DEFAULT '0' COMMENT '退款状态(0:处理中 1:成功 2:失败)',
  `fail_reason` varchar(500) DEFAULT NULL COMMENT '失败原因',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`record_id`),
  UNIQUE KEY `uk_refund_no` (`refund_no`),
  KEY `idx_apply_id` (`apply_id`),
  KEY `idx_payment_id` (`payment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款记录表';

-- 5.5 交易流水表
DROP TABLE IF EXISTS `hz_transaction`;
CREATE TABLE `hz_transaction` (
  `transaction_id` bigint NOT NULL AUTO_INCREMENT COMMENT '流水ID',
  `transaction_no` varchar(50) NOT NULL COMMENT '流水号',
  `transaction_type` char(1) NOT NULL COMMENT '交易类型(1:收入 2:支出)',
  `business_type` char(1) NOT NULL COMMENT '业务类型(1:缴费 2:退款)',
  `business_id` bigint NOT NULL COMMENT '业务ID(缴费ID或退款ID)',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `amount` decimal(10,2) NOT NULL COMMENT '交易金额',
  `balance_before` decimal(10,2) DEFAULT 0 COMMENT '交易前余额',
  `balance_after` decimal(10,2) DEFAULT 0 COMMENT '交易后余额',
  `transaction_time` datetime DEFAULT NULL COMMENT '交易时间',
  `transaction_status` char(1) DEFAULT '1' COMMENT '交易状态(0:失败 1:成功)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`transaction_id`),
  UNIQUE KEY `uk_transaction_no` (`transaction_no`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易流水表';

-- 5.6 优惠券表
DROP TABLE IF EXISTS `hz_coupon`;
CREATE TABLE `hz_coupon` (
  `coupon_id` bigint NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
  `coupon_name` varchar(100) NOT NULL COMMENT '优惠券名称',
  `coupon_code` varchar(50) NOT NULL COMMENT '优惠券编码',
  `coupon_type` char(1) NOT NULL COMMENT '优惠券类型(1:满减 2:折扣 3:抵扣)',
  `discount_amount` decimal(10,2) DEFAULT NULL COMMENT '优惠金额(元)',
  `discount_rate` decimal(5,2) DEFAULT NULL COMMENT '折扣率(%)',
  `min_amount` decimal(10,2) DEFAULT 0 COMMENT '最低使用金额',
  `max_discount` decimal(10,2) DEFAULT NULL COMMENT '最高优惠金额',
  `total_count` int DEFAULT 0 COMMENT '发行总量',
  `received_count` int DEFAULT 0 COMMENT '已领取数量',
  `used_count` int DEFAULT 0 COMMENT '已使用数量',
  `valid_start_date` date DEFAULT NULL COMMENT '有效期开始',
  `valid_end_date` date DEFAULT NULL COMMENT '有效期结束',
  `applicable_type` varchar(50) DEFAULT NULL COMMENT '适用类型(租金/押金等)',
  `status` char(1) DEFAULT '0' COMMENT '状态(0:正常 1:停用)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`coupon_id`),
  UNIQUE KEY `uk_coupon_code` (`coupon_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';

-- 5.7 优惠券领取记录表
DROP TABLE IF EXISTS `hz_coupon_receive`;
CREATE TABLE `hz_coupon_receive` (
  `receive_id` bigint NOT NULL AUTO_INCREMENT COMMENT '领取ID',
  `coupon_id` bigint NOT NULL COMMENT '优惠券ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `receive_time` datetime DEFAULT NULL COMMENT '领取时间',
  `receive_status` char(1) DEFAULT '0' COMMENT '状态(0:未使用 1:已使用 2:已过期)',
  `use_time` datetime DEFAULT NULL COMMENT '使用时间',
  `order_id` bigint DEFAULT NULL COMMENT '使用订单ID',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`receive_id`),
  KEY `idx_coupon_id` (`coupon_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券领取记录表';

-- 5.8 优惠券使用记录表
DROP TABLE IF EXISTS `hz_coupon_use`;
CREATE TABLE `hz_coupon_use` (
  `use_id` bigint NOT NULL AUTO_INCREMENT COMMENT '使用ID',
  `receive_id` bigint NOT NULL COMMENT '领取ID',
  `coupon_id` bigint NOT NULL COMMENT '优惠券ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `bill_id` bigint NOT NULL COMMENT '账单ID',
  `discount_amount` decimal(10,2) NOT NULL COMMENT '优惠金额',
  `use_time` datetime DEFAULT NULL COMMENT '使用时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`use_id`),
  KEY `idx_receive_id` (`receive_id`),
  KEY `idx_bill_id` (`bill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券使用记录表';

-- ====================================
-- 6. 开票管理模块 (3张表)
-- ====================================

-- 6.1 开票申请表
DROP TABLE IF EXISTS `hz_invoice_apply`;
CREATE TABLE `hz_invoice_apply` (
  `apply_id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `apply_no` varchar(50) NOT NULL COMMENT '申请编号',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `tenant_name` varchar(50) NOT NULL COMMENT '租户姓名',
  `invoice_type` char(1) NOT NULL COMMENT '发票类型(1:增值税普通发票 2:增值税专用发票)',
  `invoice_title` varchar(200) NOT NULL COMMENT '发票抬头',
  `tax_no` varchar(50) DEFAULT NULL COMMENT '纳税人识别号',
  `company_address` varchar(255) DEFAULT NULL COMMENT '单位地址',
  `company_phone` varchar(20) DEFAULT NULL COMMENT '单位电话',
  `bank_name` varchar(100) DEFAULT NULL COMMENT '开户行',
  `bank_account` varchar(50) DEFAULT NULL COMMENT '银行账号',
  `invoice_amount` decimal(10,2) NOT NULL COMMENT '开票金额',
  `invoice_content` varchar(500) DEFAULT NULL COMMENT '开票内容',
  `bill_ids` varchar(500) DEFAULT NULL COMMENT '账单ID列表(多个用逗号分隔)',
  `email` varchar(100) DEFAULT NULL COMMENT '接收邮箱',
  `receiver_name` varchar(50) DEFAULT NULL COMMENT '收件人姓名',
  `receiver_phone` varchar(20) DEFAULT NULL COMMENT '收件人电话',
  `receiver_address` varchar(255) DEFAULT NULL COMMENT '收件地址',
  `apply_time` datetime DEFAULT NULL COMMENT '申请时间',
  `apply_status` char(1) DEFAULT '0' COMMENT '申请状态(0:待审核 1:已开票 2:已驳回 3:已邮寄)',
  `approve_by` varchar(64) DEFAULT NULL COMMENT '审批人',
  `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
  `reject_reason` varchar(500) DEFAULT NULL COMMENT '驳回原因',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`apply_id`),
  UNIQUE KEY `uk_apply_no` (`apply_no`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='开票申请表';

-- 6.2 发票表
DROP TABLE IF EXISTS `hz_invoice`;
CREATE TABLE `hz_invoice` (
  `invoice_id` bigint NOT NULL AUTO_INCREMENT COMMENT '发票ID',
  `invoice_no` varchar(50) NOT NULL COMMENT '发票号码',
  `invoice_code` varchar(50) DEFAULT NULL COMMENT '发票代码',
  `apply_id` bigint NOT NULL COMMENT '申请ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `invoice_type` char(1) NOT NULL COMMENT '发票类型(1:增值税普通发票 2:增值税专用发票)',
  `invoice_title` varchar(200) NOT NULL COMMENT '发票抬头',
  `tax_no` varchar(50) DEFAULT NULL COMMENT '纳税人识别号',
  `invoice_amount` decimal(10,2) NOT NULL COMMENT '开票金额',
  `tax_amount` decimal(10,2) DEFAULT 0 COMMENT '税额',
  `invoice_date` date DEFAULT NULL COMMENT '开票日期',
  `invoice_pdf` varchar(255) DEFAULT NULL COMMENT '发票PDF',
  `invoice_status` char(1) DEFAULT '0' COMMENT '发票状态(0:正常 1:已作废)',
  `void_reason` varchar(500) DEFAULT NULL COMMENT '作废原因',
  `void_time` datetime DEFAULT NULL COMMENT '作废时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`invoice_id`),
  UNIQUE KEY `uk_invoice_no` (`invoice_no`),
  KEY `idx_apply_id` (`apply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发票表';

-- 6.3 发票附件表
DROP TABLE IF EXISTS `hz_invoice_attachment`;
CREATE TABLE `hz_invoice_attachment` (
  `attachment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '附件ID',
  `invoice_id` bigint NOT NULL COMMENT '发票ID',
  `attachment_name` varchar(100) NOT NULL COMMENT '附件名称',
  `attachment_url` varchar(255) NOT NULL COMMENT '附件URL',
  `attachment_type` varchar(20) DEFAULT NULL COMMENT '附件类型',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小(字节)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`attachment_id`),
  KEY `idx_invoice_id` (`invoice_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发票附件表';

-- ====================================
-- 7. 预约看房模块 (2张表)
-- ====================================

-- 7.1 预约看房表
DROP TABLE IF EXISTS `hz_appointment`;
CREATE TABLE `hz_appointment` (
  `appointment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `appointment_no` varchar(50) NOT NULL COMMENT '预约编号',
  `tenant_id` bigint DEFAULT NULL COMMENT '租户ID',
  `visitor_name` varchar(50) NOT NULL COMMENT '看房人姓名',
  `visitor_phone` varchar(20) NOT NULL COMMENT '看房人电话',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `house_id` bigint DEFAULT NULL COMMENT '房源ID',
  `appointment_date` date NOT NULL COMMENT '预约日期',
  `appointment_time` varchar(20) NOT NULL COMMENT '预约时间段',
  `visitor_count` int DEFAULT 1 COMMENT '看房人数',
  `appointment_source` varchar(20) DEFAULT NULL COMMENT '预约来源(H5/小程序/电话)',
  `appointment_status` char(1) DEFAULT '0' COMMENT '预约状态(0:待确认 1:已确认 2:已完成 3:已取消)',
  `confirm_by` varchar(64) DEFAULT NULL COMMENT '确认人',
  `confirm_time` datetime DEFAULT NULL COMMENT '确认时间',
  `cancel_reason` varchar(500) DEFAULT NULL COMMENT '取消原因',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`appointment_id`),
  UNIQUE KEY `uk_appointment_no` (`appointment_no`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约看房表';

-- 7.2 看房记录表
DROP TABLE IF EXISTS `hz_viewing_record`;
CREATE TABLE `hz_viewing_record` (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `appointment_id` bigint NOT NULL COMMENT '预约ID',
  `tenant_id` bigint DEFAULT NULL COMMENT '租户ID',
  `visitor_name` varchar(50) NOT NULL COMMENT '看房人姓名',
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `house_id` bigint NOT NULL COMMENT '房源ID',
  `viewing_date` date DEFAULT NULL COMMENT '看房日期',
  `viewing_time` datetime DEFAULT NULL COMMENT '看房时间',
  `guide_person` varchar(50) DEFAULT NULL COMMENT '带看人',
  `viewing_duration` int DEFAULT NULL COMMENT '看房时长(分钟)',
  `satisfaction_score` int DEFAULT NULL COMMENT '满意度评分(1-5)',
  `is_interested` char(1) DEFAULT '0' COMMENT '是否有意向(0:否 1:是)',
  `feedback` varchar(500) DEFAULT NULL COMMENT '反馈意见',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`record_id`),
  KEY `idx_appointment_id` (`appointment_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='看房记录表';

-- ====================================
-- 8. 资料上传模块 (2张表)
-- ====================================

-- 8.1 租户资料表
DROP TABLE IF EXISTS `hz_tenant_document`;
CREATE TABLE `hz_tenant_document` (
  `document_id` bigint NOT NULL AUTO_INCREMENT COMMENT '资料ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `document_type` varchar(50) NOT NULL COMMENT '资料类型(身份证/学历证明/社保证明等)',
  `document_name` varchar(100) NOT NULL COMMENT '资料名称',
  `document_url` varchar(255) NOT NULL COMMENT '资料URL',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小(字节)',
  `upload_time` datetime DEFAULT NULL COMMENT '上传时间',
  `audit_status` char(1) DEFAULT '0' COMMENT '审核状态(0:待审核 1:已通过 2:已驳回)',
  `audit_by` varchar(64) DEFAULT NULL COMMENT '审核人',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `audit_opinion` varchar(500) DEFAULT NULL COMMENT '审核意见',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`document_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户资料表';

-- 8.2 资料审核记录表
DROP TABLE IF EXISTS `hz_document_audit`;
CREATE TABLE `hz_document_audit` (
  `audit_id` bigint NOT NULL AUTO_INCREMENT COMMENT '审核ID',
  `document_id` bigint NOT NULL COMMENT '资料ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `audit_result` char(1) NOT NULL COMMENT '审核结果(1:通过 2:驳回)',
  `audit_opinion` varchar(500) DEFAULT NULL COMMENT '审核意见',
  `audit_by` varchar(64) DEFAULT NULL COMMENT '审核人',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`audit_id`),
  KEY `idx_document_id` (`document_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资料审核记录表';

-- ====================================
-- 9. 消息通知模块 (4张表)
-- ====================================

-- 9.1 消息模板表
DROP TABLE IF EXISTS `hz_message_template`;
CREATE TABLE `hz_message_template` (
  `template_id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_code` varchar(50) NOT NULL COMMENT '模板编码',
  `template_name` varchar(100) NOT NULL COMMENT '模板名称',
  `template_type` char(1) NOT NULL COMMENT '模板类型(1:系统通知 2:短信 3:邮件)',
  `template_content` text NOT NULL COMMENT '模板内容(支持占位符)',
  `status` char(1) DEFAULT '0' COMMENT '状态(0:正常 1:停用)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`template_id`),
  UNIQUE KEY `uk_template_code` (`template_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息模板表';

-- 9.2 消息记录表
DROP TABLE IF EXISTS `hz_message`;
CREATE TABLE `hz_message` (
  `message_id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `message_type` char(1) NOT NULL COMMENT '消息类型(1:系统通知 2:短信 3:邮件)',
  `template_id` bigint DEFAULT NULL COMMENT '模板ID',
  `receiver_id` bigint NOT NULL COMMENT '接收人ID',
  `receiver_name` varchar(50) DEFAULT NULL COMMENT '接收人姓名',
  `receiver_phone` varchar(20) DEFAULT NULL COMMENT '接收人手机',
  `receiver_email` varchar(100) DEFAULT NULL COMMENT '接收人邮箱',
  `message_title` varchar(200) DEFAULT NULL COMMENT '消息标题',
  `message_content` text NOT NULL COMMENT '消息内容',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `send_status` char(1) DEFAULT '0' COMMENT '发送状态(0:待发送 1:已发送 2:发送失败)',
  `read_status` char(1) DEFAULT '0' COMMENT '阅读状态(0:未读 1:已读)',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`message_id`),
  KEY `idx_receiver_id` (`receiver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息记录表';

-- 9.3 通知公告表
DROP TABLE IF EXISTS `hz_announcement`;
CREATE TABLE `hz_announcement` (
  `announcement_id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `announcement_title` varchar(200) NOT NULL COMMENT '公告标题',
  `announcement_type` char(1) NOT NULL COMMENT '公告类型(1:通知 2:公告 3:活动)',
  `announcement_content` text NOT NULL COMMENT '公告内容',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `target_type` char(1) DEFAULT '0' COMMENT '目标类型(0:全部 1:人才公寓 2:保租房 3:市场租赁)',
  `is_top` char(1) DEFAULT '0' COMMENT '是否置顶(0:否 1:是)',
  `view_count` int DEFAULT 0 COMMENT '浏览次数',
  `status` char(1) DEFAULT '0' COMMENT '状态(0:草稿 1:已发布 2:已下线)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`announcement_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告表';

-- 9.4 政策文件表
DROP TABLE IF EXISTS `hz_policy`;
CREATE TABLE `hz_policy` (
  `policy_id` bigint NOT NULL AUTO_INCREMENT COMMENT '政策ID',
  `policy_title` varchar(200) NOT NULL COMMENT '政策标题',
  `policy_no` varchar(50) DEFAULT NULL COMMENT '政策文号',
  `policy_type` varchar(50) DEFAULT NULL COMMENT '政策类型',
  `policy_content` text NOT NULL COMMENT '政策内容',
  `policy_file` varchar(255) DEFAULT NULL COMMENT '政策文件URL',
  `publish_dept` varchar(100) DEFAULT NULL COMMENT '发布部门',
  `publish_date` date DEFAULT NULL COMMENT '发布日期',
  `effective_date` date DEFAULT NULL COMMENT '生效日期',
  `view_count` int DEFAULT 0 COMMENT '浏览次数',
  `status` char(1) DEFAULT '0' COMMENT '状态(0:草稿 1:已发布 2:已下线)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`policy_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='政策文件表';

-- ====================================
-- 10. 集中配租模块 (4张表)
-- ====================================

-- 10.1 集中配租批次表
DROP TABLE IF EXISTS `hz_batch_allocation`;
CREATE TABLE `hz_batch_allocation` (
  `batch_id` bigint NOT NULL AUTO_INCREMENT COMMENT '批次ID',
  `batch_no` varchar(50) NOT NULL COMMENT '批次编号',
  `batch_name` varchar(100) NOT NULL COMMENT '批次名称',
  `enterprise_id` bigint NOT NULL COMMENT '企业ID',
  `enterprise_name` varchar(100) NOT NULL COMMENT '企业名称',
  `contact_person` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `house_count` int DEFAULT 0 COMMENT '配租房源数',
  `allocated_count` int DEFAULT 0 COMMENT '已分配数',
  `apply_time` datetime DEFAULT NULL COMMENT '申请时间',
  `approve_status` char(1) DEFAULT '0' COMMENT '审批状态(0:待审批 1:已通过 2:已驳回)',
  `approve_by` varchar(64) DEFAULT NULL COMMENT '审批人',
  `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
  `batch_status` char(1) DEFAULT '0' COMMENT '批次状态(0:待分配 1:分配中 2:已完成)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`batch_id`),
  UNIQUE KEY `uk_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='集中配租批次表';

-- 10.2 批次房源表
DROP TABLE IF EXISTS `hz_batch_house`;
CREATE TABLE `hz_batch_house` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `batch_id` bigint NOT NULL COMMENT '批次ID',
  `house_id` bigint NOT NULL COMMENT '房源ID',
  `house_code` varchar(50) NOT NULL COMMENT '房源编码',
  `allocation_status` char(1) DEFAULT '0' COMMENT '分配状态(0:未分配 1:已分配)',
  `tenant_id` bigint DEFAULT NULL COMMENT '分配租户ID',
  `allocation_time` datetime DEFAULT NULL COMMENT '分配时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`id`),
  KEY `idx_batch_id` (`batch_id`),
  KEY `idx_house_id` (`house_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='批次房源表';

-- 10.3 批次租户表
DROP TABLE IF EXISTS `hz_batch_tenant`;
CREATE TABLE `hz_batch_tenant` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `batch_id` bigint NOT NULL COMMENT '批次ID',
  `tenant_name` varchar(50) NOT NULL COMMENT '租户姓名',
  `id_card` varchar(18) NOT NULL COMMENT '身份证号',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `work_no` varchar(50) DEFAULT NULL COMMENT '工号',
  `dept_name` varchar(100) DEFAULT NULL COMMENT '部门',
  `house_id` bigint DEFAULT NULL COMMENT '分配房源ID',
  `allocation_status` char(1) DEFAULT '0' COMMENT '分配状态(0:未分配 1:已分配)',
  `allocation_time` datetime DEFAULT NULL COMMENT '分配时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`id`),
  KEY `idx_batch_id` (`batch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='批次租户表';

-- 10.4 企业信息表
DROP TABLE IF EXISTS `hz_enterprise`;
CREATE TABLE `hz_enterprise` (
  `enterprise_id` bigint NOT NULL AUTO_INCREMENT COMMENT '企业ID',
  `enterprise_name` varchar(100) NOT NULL COMMENT '企业名称',
  `social_credit_code` varchar(50) DEFAULT NULL COMMENT '统一社会信用代码',
  `legal_person` varchar(50) DEFAULT NULL COMMENT '法人代表',
  `contact_person` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `contact_email` varchar(100) DEFAULT NULL COMMENT '联系邮箱',
  `enterprise_address` varchar(255) DEFAULT NULL COMMENT '企业地址',
  `business_license` varchar(255) DEFAULT NULL COMMENT '营业执照URL',
  `employee_count` int DEFAULT 0 COMMENT '员工数量',
  `rented_count` int DEFAULT 0 COMMENT '已租房源数',
  `status` char(1) DEFAULT '0' COMMENT '状态(0:正常 1:停用)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`enterprise_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业信息表';

-- ====================================
-- 11. 运营配置模块 (5张表)
-- ====================================

-- 11.1 运营配置表
DROP TABLE IF EXISTS `hz_operation_config`;
CREATE TABLE `hz_operation_config` (
  `config_id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(50) NOT NULL COMMENT '配置键',
  `config_value` text NOT NULL COMMENT '配置值',
  `config_desc` varchar(200) DEFAULT NULL COMMENT '配置描述',
  `config_type` varchar(50) DEFAULT NULL COMMENT '配置类型',
  `status` char(1) DEFAULT '0' COMMENT '状态(0:正常 1:停用)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`config_id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运营配置表';

-- 11.2 轮播图表
DROP TABLE IF EXISTS `hz_banner`;
CREATE TABLE `hz_banner` (
  `banner_id` bigint NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
  `banner_title` varchar(100) NOT NULL COMMENT '轮播图标题',
  `banner_image` varchar(255) NOT NULL COMMENT '轮播图图片',
  `banner_url` varchar(255) DEFAULT NULL COMMENT '跳转链接',
  `banner_type` char(1) DEFAULT '1' COMMENT '轮播图类型(1:首页 2:项目详情)',
  `target_id` bigint DEFAULT NULL COMMENT '目标ID(项目ID等)',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `sort_order` int DEFAULT 0 COMMENT '显示顺序',
  `status` char(1) DEFAULT '0' COMMENT '状态(0:正常 1:停用)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`banner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

-- 11.3 快捷入口表
DROP TABLE IF EXISTS `hz_shortcut`;
CREATE TABLE `hz_shortcut` (
  `shortcut_id` bigint NOT NULL AUTO_INCREMENT COMMENT '快捷入口ID',
  `shortcut_name` varchar(50) NOT NULL COMMENT '入口名称',
  `shortcut_icon` varchar(255) NOT NULL COMMENT '入口图标',
  `shortcut_url` varchar(255) NOT NULL COMMENT '跳转链接',
  `shortcut_type` varchar(50) DEFAULT NULL COMMENT '入口类型',
  `sort_order` int DEFAULT 0 COMMENT '显示顺序',
  `status` char(1) DEFAULT '0' COMMENT '状态(0:正常 1:停用)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`shortcut_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='快捷入口表';

-- 11.4 地图找房点位表
DROP TABLE IF EXISTS `hz_map_point`;
CREATE TABLE `hz_map_point` (
  `point_id` bigint NOT NULL AUTO_INCREMENT COMMENT '点位ID',
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `point_name` varchar(100) NOT NULL COMMENT '点位名称',
  `longitude` decimal(10,7) NOT NULL COMMENT '经度',
  `latitude` decimal(10,7) NOT NULL COMMENT '纬度',
  `marker_icon` varchar(255) DEFAULT NULL COMMENT '标记图标',
  `status` char(1) DEFAULT '0' COMMENT '状态(0:正常 1:停用)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`point_id`),
  KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地图找房点位表';

-- 11.5 精选房源表
DROP TABLE IF EXISTS `hz_featured_house`;
CREATE TABLE `hz_featured_house` (
  `featured_id` bigint NOT NULL AUTO_INCREMENT COMMENT '精选ID',
  `house_id` bigint NOT NULL COMMENT '房源ID',
  `featured_reason` varchar(200) DEFAULT NULL COMMENT '精选理由',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `sort_order` int DEFAULT 0 COMMENT '显示顺序',
  `status` char(1) DEFAULT '0' COMMENT '状态(0:正常 1:停用)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`featured_id`),
  KEY `idx_house_id` (`house_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='精选房源表';
