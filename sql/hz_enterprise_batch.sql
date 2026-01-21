-- ----------------------------
-- 企业批次表
-- ----------------------------
DROP TABLE IF EXISTS `hz_enterprise_batch`;
CREATE TABLE `hz_enterprise_batch` (
  `batch_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '批次ID',
  `batch_no` varchar(50) NOT NULL COMMENT '批次编号',
  `batch_name` varchar(100) NOT NULL COMMENT '批次名称',

  -- 企业信息
  `enterprise_id` bigint(20) DEFAULT NULL COMMENT '企业ID(关联hz_enterprise表)',
  `enterprise_name` varchar(100) NOT NULL COMMENT '单位名称',
  `contact_person` varchar(50) NOT NULL COMMENT '联系人',
  `contact_phone` varchar(20) NOT NULL COMMENT '联系方式',

  -- 项目和房源
  `project_ids` varchar(500) DEFAULT NULL COMMENT '项目ID列表(逗号分隔)',
  `house_count` int(11) DEFAULT 0 COMMENT '房源数量',

  -- 选房日期
  `selection_start_date` date DEFAULT NULL COMMENT '选房开始日期',
  `selection_end_date` date DEFAULT NULL COMMENT '选房结束日期',

  -- 审批和状态
  `approve_status` char(1) DEFAULT '0' COMMENT '审批状态(0待审批 1已通过 2已拒绝)',
  `approve_by` varchar(64) DEFAULT NULL COMMENT '审批人',
  `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
  `batch_status` char(1) DEFAULT '0' COMMENT '批次状态(0进行中 1已完成 2已作废)',

  -- 标准字段
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0正常 2删除)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',

  PRIMARY KEY (`batch_id`),
  UNIQUE KEY `uk_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业批次表';


-- ----------------------------
-- 企业批次房源表
-- ----------------------------
DROP TABLE IF EXISTS `hz_enterprise_batch_house`;
CREATE TABLE `hz_enterprise_batch_house` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `batch_id` bigint(20) NOT NULL COMMENT '批次ID',
  `house_id` bigint(20) NOT NULL COMMENT '房源ID',
  `house_code` varchar(50) NOT NULL COMMENT '房源编号',

  -- 状态和时间
  `allocation_status` char(1) DEFAULT '1' COMMENT '分配状态(0未分配 1已分配)',
  `allocation_time` datetime DEFAULT NULL COMMENT '分配时间',

  -- 标准字段
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0正常 2删除)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',

  PRIMARY KEY (`id`),
  KEY `idx_batch_id` (`batch_id`),
  KEY `idx_house_id` (`house_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业批次房源表';
