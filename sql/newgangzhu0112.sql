/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 80015
Source Host           : localhost:3306
Source Database       : newgangzhu

Target Server Type    : MYSQL
Target Server Version : 80015
File Encoding         : 65001

Date: 2026-01-12 03:40:06
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for gen_table
-- ----------------------------
DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table` (
  `table_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `tpl_web_type` varchar(30) DEFAULT '' COMMENT '前端模板类型（element-ui模版 element-plus模版）',
  `package_name` varchar(100) DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) DEFAULT NULL COMMENT '生成功能作者',
  `gen_type` char(1) DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='代码生成业务表';

-- ----------------------------
-- Records of gen_table
-- ----------------------------

-- ----------------------------
-- Table structure for gen_table_column
-- ----------------------------
DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column` (
  `column_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` bigint(20) DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) DEFAULT '' COMMENT '字典类型',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='代码生成业务表字段';

-- ----------------------------
-- Records of gen_table_column
-- ----------------------------

-- ----------------------------
-- Table structure for hz_announcement
-- ----------------------------
DROP TABLE IF EXISTS `hz_announcement`;
CREATE TABLE `hz_announcement` (
  `announcement_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '鍏?憡ID',
  `announcement_title` varchar(200) NOT NULL COMMENT '鍏?憡鏍囬?',
  `announcement_type` char(1) NOT NULL COMMENT '鍏?憡绫诲瀷(1:閫氱煡 2:鍏?憡 3:娲诲姩)',
  `announcement_content` text NOT NULL COMMENT '鍏?憡鍐呭?',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '灏侀潰鍥',
  `publish_time` datetime DEFAULT NULL COMMENT '鍙戝竷鏃堕棿',
  `end_time` datetime DEFAULT NULL COMMENT '缁撴潫鏃堕棿',
  `target_type` char(1) DEFAULT '0' COMMENT '鐩?爣绫诲瀷(0:鍏ㄩ儴 1:浜烘墠鍏?瘬 2:淇濈?鎴?3:甯傚満绉熻祦)',
  `is_top` char(1) DEFAULT '0' COMMENT '鏄?惁缃?《(0:鍚?1:鏄?',
  `view_count` int(11) DEFAULT '0' COMMENT '娴忚?娆℃暟',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:鑽夌? 1:宸插彂甯?2:宸蹭笅绾?',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`announcement_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='閫氱煡鍏?憡琛';

-- ----------------------------
-- Records of hz_announcement
-- ----------------------------

-- ----------------------------
-- Table structure for hz_appointment
-- ----------------------------
DROP TABLE IF EXISTS `hz_appointment`;
CREATE TABLE `hz_appointment` (
  `appointment_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '棰勭害ID',
  `appointment_no` varchar(50) NOT NULL COMMENT '棰勭害缂栧彿',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '绉熸埛ID',
  `visitor_name` varchar(50) NOT NULL COMMENT '鐪嬫埧浜哄?鍚',
  `visitor_phone` varchar(20) NOT NULL COMMENT '鐪嬫埧浜虹數璇',
  `id_card` varchar(18) DEFAULT NULL COMMENT '韬?唤璇佸彿',
  `project_id` bigint(20) NOT NULL COMMENT '椤圭洰ID',
  `house_id` bigint(20) DEFAULT NULL COMMENT '鎴挎簮ID',
  `appointment_date` date NOT NULL COMMENT '棰勭害鏃ユ湡',
  `time_slot` varchar(20) DEFAULT NULL COMMENT '预约时段(1:上午 2:下午 3:晚上)',
  `appointment_time` varchar(20) NOT NULL COMMENT '棰勭害鏃堕棿娈',
  `visitor_count` int(11) DEFAULT '1' COMMENT '鐪嬫埧浜烘暟',
  `appointment_source` varchar(20) DEFAULT NULL COMMENT '棰勭害鏉ユ簮(H5/灏忕▼搴?鐢佃瘽)',
  `appointment_status` char(1) DEFAULT '0' COMMENT '棰勭害鐘舵?(0:寰呯‘璁?1:宸茬‘璁?2:宸插畬鎴?3:宸插彇娑?',
  `confirm_by` varchar(64) DEFAULT NULL COMMENT '纭??浜',
  `confirm_time` datetime DEFAULT NULL COMMENT '纭??鏃堕棿',
  `cancel_reason` varchar(500) DEFAULT NULL COMMENT '鍙栨秷鍘熷洜',
  `cancel_time` datetime DEFAULT NULL COMMENT '鍙栨秷鏃堕棿',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`appointment_id`),
  UNIQUE KEY `uk_appointment_no` (`appointment_no`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='棰勭害鐪嬫埧琛';

-- ----------------------------
-- Records of hz_appointment
-- ----------------------------

-- ----------------------------
-- Table structure for hz_banner
-- ----------------------------
DROP TABLE IF EXISTS `hz_banner`;
CREATE TABLE `hz_banner` (
  `banner_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '杞?挱鍥綢D',
  `banner_title` varchar(100) NOT NULL COMMENT '杞?挱鍥炬爣棰',
  `banner_image` varchar(255) NOT NULL COMMENT '杞?挱鍥惧浘鐗',
  `banner_url` varchar(255) DEFAULT NULL COMMENT '璺宠浆閾炬帴',
  `banner_type` char(1) DEFAULT '1' COMMENT '杞?挱鍥剧被鍨?1:棣栭〉 2:椤圭洰璇︽儏)',
  `target_id` bigint(20) DEFAULT NULL COMMENT '鐩?爣ID(椤圭洰ID绛?',
  `start_time` datetime DEFAULT NULL COMMENT '寮??鏃堕棿',
  `end_time` datetime DEFAULT NULL COMMENT '缁撴潫鏃堕棿',
  `sort_order` int(11) DEFAULT '0' COMMENT '鏄剧ず椤哄簭',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:姝ｅ父 1:鍋滅敤)',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`banner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='杞?挱鍥捐〃';

-- ----------------------------
-- Records of hz_banner
-- ----------------------------

-- ----------------------------
-- Table structure for hz_batch_allocation
-- ----------------------------
DROP TABLE IF EXISTS `hz_batch_allocation`;
CREATE TABLE `hz_batch_allocation` (
  `batch_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '鎵规?ID',
  `batch_no` varchar(50) NOT NULL COMMENT '鎵规?缂栧彿',
  `batch_name` varchar(100) NOT NULL COMMENT '鎵规?鍚嶇О',
  `enterprise_id` bigint(20) NOT NULL COMMENT '浼佷笟ID',
  `enterprise_name` varchar(100) NOT NULL COMMENT '浼佷笟鍚嶇О',
  `contact_person` varchar(50) DEFAULT NULL COMMENT '鑱旂郴浜',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '鑱旂郴鐢佃瘽',
  `project_id` bigint(20) NOT NULL COMMENT '椤圭洰ID',
  `house_count` int(11) DEFAULT '0' COMMENT '閰嶇?鎴挎簮鏁',
  `allocated_count` int(11) DEFAULT '0' COMMENT '宸插垎閰嶆暟',
  `apply_time` datetime DEFAULT NULL COMMENT '鐢宠?鏃堕棿',
  `approve_status` char(1) DEFAULT '0' COMMENT '瀹℃壒鐘舵?(0:寰呭?鎵?1:宸查?杩?2:宸查┏鍥?',
  `approve_by` varchar(64) DEFAULT NULL COMMENT '瀹℃壒浜',
  `approve_time` datetime DEFAULT NULL COMMENT '瀹℃壒鏃堕棿',
  `batch_status` char(1) DEFAULT '0' COMMENT '鎵规?鐘舵?(0:寰呭垎閰?1:鍒嗛厤涓?2:宸插畬鎴?',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`batch_id`),
  UNIQUE KEY `uk_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='闆嗕腑閰嶇?鎵规?琛';

-- ----------------------------
-- Records of hz_batch_allocation
-- ----------------------------

-- ----------------------------
-- Table structure for hz_batch_house
-- ----------------------------
DROP TABLE IF EXISTS `hz_batch_house`;
CREATE TABLE `hz_batch_house` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `batch_id` bigint(20) NOT NULL COMMENT '鎵规?ID',
  `house_id` bigint(20) NOT NULL COMMENT '鎴挎簮ID',
  `house_code` varchar(50) NOT NULL COMMENT '鎴挎簮缂栫爜',
  `allocation_status` char(1) DEFAULT '0' COMMENT '鍒嗛厤鐘舵?(0:鏈?垎閰?1:宸插垎閰?',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '鍒嗛厤绉熸埛ID',
  `allocation_time` datetime DEFAULT NULL COMMENT '鍒嗛厤鏃堕棿',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`id`),
  KEY `idx_batch_id` (`batch_id`),
  KEY `idx_house_id` (`house_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鎵规?鎴挎簮琛';

-- ----------------------------
-- Records of hz_batch_house
-- ----------------------------

-- ----------------------------
-- Table structure for hz_batch_tenant
-- ----------------------------
DROP TABLE IF EXISTS `hz_batch_tenant`;
CREATE TABLE `hz_batch_tenant` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `batch_id` bigint(20) NOT NULL COMMENT '鎵规?ID',
  `tenant_name` varchar(50) NOT NULL COMMENT '绉熸埛濮撳悕',
  `id_card` varchar(18) NOT NULL COMMENT '韬?唤璇佸彿',
  `phone` varchar(20) NOT NULL COMMENT '鎵嬫満鍙',
  `work_no` varchar(50) DEFAULT NULL COMMENT '宸ュ彿',
  `dept_name` varchar(100) DEFAULT NULL COMMENT '閮ㄩ棬',
  `house_id` bigint(20) DEFAULT NULL COMMENT '鍒嗛厤鎴挎簮ID',
  `allocation_status` char(1) DEFAULT '0' COMMENT '鍒嗛厤鐘舵?(0:鏈?垎閰?1:宸插垎閰?',
  `allocation_time` datetime DEFAULT NULL COMMENT '鍒嗛厤鏃堕棿',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`id`),
  KEY `idx_batch_id` (`batch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鎵规?绉熸埛琛';

-- ----------------------------
-- Records of hz_batch_tenant
-- ----------------------------

-- ----------------------------
-- Table structure for hz_bill
-- ----------------------------
DROP TABLE IF EXISTS `hz_bill`;
CREATE TABLE `hz_bill` (
  `bill_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '璐﹀崟ID',
  `bill_no` varchar(50) NOT NULL COMMENT '璐﹀崟缂栧彿',
  `contract_id` bigint(20) NOT NULL COMMENT '鍚堝悓ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '绉熸埛ID',
  `tenant_name` varchar(50) NOT NULL COMMENT '绉熸埛濮撳悕',
  `house_id` bigint(20) NOT NULL COMMENT '鎴挎簮ID',
  `house_code` varchar(50) NOT NULL COMMENT '鎴挎簮缂栫爜',
  `bill_type` char(1) NOT NULL COMMENT '璐﹀崟绫诲瀷(1:鎶奸噾 2:绉熼噾 3:姘磋垂 4:鐢佃垂 5:鐕冩皵璐?6:鐗╀笟璐?7:鍏朵粬)',
  `bill_period` varchar(20) DEFAULT NULL COMMENT '璐﹀崟鍛ㄦ湡(濡?2025-01)',
  `bill_amount` decimal(10,2) NOT NULL COMMENT '璐﹀崟閲戦?',
  `paid_amount` decimal(10,2) DEFAULT '0.00' COMMENT '宸叉敮浠橀噾棰',
  `unpaid_amount` decimal(10,2) DEFAULT '0.00' COMMENT '鏈?敮浠橀噾棰',
  `bill_date` date DEFAULT NULL COMMENT '璐﹀崟鏃ユ湡',
  `due_date` date DEFAULT NULL COMMENT '搴旂即鏃ユ湡',
  `late_fee` decimal(10,2) DEFAULT '0.00' COMMENT '婊炵撼閲',
  `bill_status` char(1) DEFAULT '0' COMMENT '璐﹀崟鐘舵?(0:寰呮敮浠?1:宸叉敮浠?2:閮ㄥ垎鏀?粯 3:宸查?鏈?4:宸插叧闂?',
  `pay_time` datetime DEFAULT NULL COMMENT '鏀?粯鏃堕棿',
  `pay_method` varchar(20) DEFAULT NULL COMMENT '鏀?粯鏂瑰紡',
  `transaction_no` varchar(50) DEFAULT NULL COMMENT '浜ゆ槗娴佹按鍙',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`bill_id`),
  UNIQUE KEY `uk_bill_no` (`bill_no`),
  KEY `idx_contract_id` (`contract_id`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_bill_status` (`bill_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='璐﹀崟琛';

-- ----------------------------
-- Records of hz_bill
-- ----------------------------

-- ----------------------------
-- Table structure for hz_blacklist
-- ----------------------------
DROP TABLE IF EXISTS `hz_blacklist`;
CREATE TABLE `hz_blacklist` (
  `blacklist_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '榛戝悕鍗旾D',
  `tenant_id` bigint(20) NOT NULL COMMENT '绉熸埛ID',
  `id_card` varchar(18) NOT NULL COMMENT '韬?唤璇佸彿',
  `tenant_name` varchar(50) NOT NULL COMMENT '绉熸埛濮撳悕',
  `blacklist_reason` varchar(500) NOT NULL COMMENT '鍒楀叆鍘熷洜',
  `blacklist_time` datetime DEFAULT NULL COMMENT '鍒楀叆鏃堕棿',
  `remove_time` datetime DEFAULT NULL COMMENT '绉婚櫎鏃堕棿',
  `status` char(1) DEFAULT '1' COMMENT '鐘舵?(0:宸茬Щ闄?1:鐢熸晥涓?',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`blacklist_id`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_id_card` (`id_card`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='榛戝悕鍗曡〃';

-- ----------------------------
-- Records of hz_blacklist
-- ----------------------------

-- ----------------------------
-- Table structure for hz_building
-- ----------------------------
DROP TABLE IF EXISTS `hz_building`;
CREATE TABLE `hz_building` (
  `building_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '妤兼爧ID',
  `project_id` bigint(20) NOT NULL COMMENT '椤圭洰ID',
  `building_name` varchar(50) NOT NULL COMMENT '妤兼爧鍚嶇О',
  `building_code` varchar(50) NOT NULL COMMENT '妤兼爧缂栫爜',
  `total_floors` int(11) DEFAULT '0' COMMENT '鎬绘ゼ灞傛暟',
  `total_units` int(11) DEFAULT '0' COMMENT '鍗曞厓鏁',
  `total_houses` int(11) DEFAULT '0' COMMENT '鎴挎簮鏁',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:姝ｅ父 1:鍋滅敤)',
  `sort_order` int(11) DEFAULT '0' COMMENT '鏄剧ず椤哄簭',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`building_id`),
  UNIQUE KEY `uk_building_code` (`building_code`),
  KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='妤兼爧琛';

-- ----------------------------
-- Records of hz_building
-- ----------------------------
INSERT INTO `hz_building` VALUES ('1', '1', '1号楼', '1-1', '10', '2', '20', '0', '1', '', '2025-11-18 19:51:27', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('2', '1', '2号楼', '1-2', '10', '2', '20', '0', '2', '', '2025-11-18 19:51:27', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('3', '2', '1号楼', '2-1', '12', '3', '36', '0', '1', '', '2025-11-18 19:51:27', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('4', '2', '2号楼', '2-2', '12', '3', '36', '0', '2', '', '2025-11-18 19:51:27', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('5', '1', '3号楼', '3-1', '1', '1', '0', '0', '0', '', null, '', null, null, '0');
INSERT INTO `hz_building` VALUES ('6', '3', '楼栋1', '10', '4', '5', '0', '0', '0', '', null, '', null, null, '0');
INSERT INTO `hz_building` VALUES ('7', '4', '2号楼', '21', '10', '2', '0', '0', '0', '', null, '', null, null, '0');

-- ----------------------------
-- Table structure for hz_checkin_apply
-- ----------------------------
DROP TABLE IF EXISTS `hz_checkin_apply`;
CREATE TABLE `hz_checkin_apply` (
  `apply_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '鐢宠?ID',
  `contract_id` bigint(20) NOT NULL COMMENT '鍚堝悓ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '绉熸埛ID',
  `house_id` bigint(20) NOT NULL COMMENT '鎴挎簮ID',
  `apply_time` datetime DEFAULT NULL COMMENT '鐢宠?鏃堕棿',
  `plan_checkin_date` date DEFAULT NULL COMMENT '璁″垝鍏ヤ綇鏃ユ湡',
  `deposit_paid` char(1) DEFAULT '0' COMMENT '鏄?惁宸茬即鎶奸噾(0:鍚?1:鏄?',
  `deposit_amount` decimal(10,2) DEFAULT NULL COMMENT '鎶奸噾閲戦?',
  `deposit_pay_time` datetime DEFAULT NULL COMMENT '鎶奸噾缂寸撼鏃堕棿',
  `apply_status` char(1) DEFAULT '0' COMMENT '鐢宠?鐘舵?(0:寰呭?鏍?1:宸查?杩?2:宸查┏鍥?3:宸插叆浣?',
  `approve_by` varchar(64) DEFAULT NULL COMMENT '瀹℃壒浜',
  `approve_time` datetime DEFAULT NULL COMMENT '瀹℃壒鏃堕棿',
  `approve_opinion` varchar(500) DEFAULT NULL COMMENT '瀹℃壒鎰忚?',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`apply_id`),
  KEY `idx_contract_id` (`contract_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鍏ヤ綇鐢宠?琛';

-- ----------------------------
-- Records of hz_checkin_apply
-- ----------------------------

-- ----------------------------
-- Table structure for hz_checkin_record
-- ----------------------------
DROP TABLE IF EXISTS `hz_checkin_record`;
CREATE TABLE `hz_checkin_record` (
  `record_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '璁板綍ID',
  `apply_id` bigint(20) NOT NULL COMMENT '鐢宠?ID',
  `contract_id` bigint(20) NOT NULL COMMENT '鍚堝悓ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '绉熸埛ID',
  `house_id` bigint(20) NOT NULL COMMENT '鎴挎簮ID',
  `checkin_date` date NOT NULL COMMENT '鍏ヤ綇鏃ユ湡',
  `checkin_time` datetime DEFAULT NULL COMMENT '鍏ヤ綇鏃堕棿',
  `meter_reading_electric` decimal(10,2) DEFAULT NULL COMMENT '鐢佃〃璇绘暟',
  `meter_reading_water` decimal(10,2) DEFAULT NULL COMMENT '姘磋〃璇绘暟',
  `meter_reading_gas` decimal(10,2) DEFAULT NULL COMMENT '鐕冩皵琛ㄨ?鏁',
  `key_count` int(11) DEFAULT '0' COMMENT '閽ュ寵鏁伴噺',
  `inventory_list_id` bigint(20) DEFAULT NULL COMMENT '娓呭崟ID',
  `checkin_photos` varchar(1000) DEFAULT NULL COMMENT '鍏ヤ綇鐓х墖(澶氫釜鐢ㄩ?鍙峰垎闅?',
  `tenant_signature` text COMMENT '绉熸埛绛惧悕(base64)',
  `manager_signature` text COMMENT '绠＄悊鍛樼?鍚?base64)',
  `manager_id` bigint(20) DEFAULT NULL COMMENT '缁忓姙浜篒D',
  `manager_name` varchar(50) DEFAULT NULL COMMENT '缁忓姙浜哄?鍚',
  `status` char(1) DEFAULT '1' COMMENT '鐘舵?(0:宸查?绉?1:鍦ㄤ綇)',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`record_id`),
  KEY `idx_contract_id` (`contract_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鍏ヤ綇璁板綍琛';

-- ----------------------------
-- Records of hz_checkin_record
-- ----------------------------

-- ----------------------------
-- Table structure for hz_checkout_apply
-- ----------------------------
DROP TABLE IF EXISTS `hz_checkout_apply`;
CREATE TABLE `hz_checkout_apply` (
  `apply_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '鐢宠?ID',
  `contract_id` bigint(20) NOT NULL COMMENT '鍚堝悓ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '绉熸埛ID',
  `house_id` bigint(20) NOT NULL COMMENT '鎴挎簮ID',
  `apply_time` datetime DEFAULT NULL COMMENT '鐢宠?鏃堕棿',
  `plan_checkout_date` date DEFAULT NULL COMMENT '璁″垝閫??鏃ユ湡',
  `checkout_reason` varchar(500) DEFAULT NULL COMMENT '閫??鍘熷洜',
  `is_early_termination` char(1) DEFAULT '0' COMMENT '鏄?惁鎻愬墠瑙ｇ害(0:鍚?1:鏄?',
  `penalty_amount` decimal(10,2) DEFAULT '0.00' COMMENT '杩濈害閲戦噾棰',
  `unpaid_bills` decimal(10,2) DEFAULT '0.00' COMMENT '鏈?粨璐﹀崟閲戦?',
  `deposit_refund` decimal(10,2) DEFAULT '0.00' COMMENT '搴旈?鎶奸噾',
  `apply_status` char(1) DEFAULT '0' COMMENT '鐢宠?鐘舵?(0:寰呭?鏍?1:宸查?杩?2:宸查┏鍥?3:宸查?绉?',
  `approve_by` varchar(64) DEFAULT NULL COMMENT '瀹℃壒浜',
  `approve_time` datetime DEFAULT NULL COMMENT '瀹℃壒鏃堕棿',
  `approve_opinion` varchar(500) DEFAULT NULL COMMENT '瀹℃壒鎰忚?',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`apply_id`),
  KEY `idx_contract_id` (`contract_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='閫??鐢宠?琛';

-- ----------------------------
-- Records of hz_checkout_apply
-- ----------------------------

-- ----------------------------
-- Table structure for hz_checkout_record
-- ----------------------------
DROP TABLE IF EXISTS `hz_checkout_record`;
CREATE TABLE `hz_checkout_record` (
  `record_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '璁板綍ID',
  `apply_id` bigint(20) NOT NULL COMMENT '鐢宠?ID',
  `contract_id` bigint(20) NOT NULL COMMENT '鍚堝悓ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '绉熸埛ID',
  `house_id` bigint(20) NOT NULL COMMENT '鎴挎簮ID',
  `checkout_date` date NOT NULL COMMENT '閫??鏃ユ湡',
  `checkout_time` datetime DEFAULT NULL COMMENT '閫??鏃堕棿',
  `meter_reading_electric` decimal(10,2) DEFAULT NULL COMMENT '鐢佃〃璇绘暟',
  `meter_reading_water` decimal(10,2) DEFAULT NULL COMMENT '姘磋〃璇绘暟',
  `meter_reading_gas` decimal(10,2) DEFAULT NULL COMMENT '鐕冩皵琛ㄨ?鏁',
  `key_returned` int(11) DEFAULT '0' COMMENT '褰掕繕閽ュ寵鏁伴噺',
  `inventory_list_id` bigint(20) DEFAULT NULL COMMENT '娓呭崟ID',
  `checkout_photos` varchar(1000) DEFAULT NULL COMMENT '閫??鐓х墖(澶氫釜鐢ㄩ?鍙峰垎闅?',
  `damage_description` varchar(500) DEFAULT NULL COMMENT '鎹熷潖鎯呭喌鎻忚堪',
  `damage_deduction` decimal(10,2) DEFAULT '0.00' COMMENT '鎹熷潖璧斿伩閲戦?',
  `utility_bill` decimal(10,2) DEFAULT '0.00' COMMENT '姘寸數鐕冩皵璐',
  `unpaid_rent` decimal(10,2) DEFAULT '0.00' COMMENT '娆犵即绉熼噾',
  `penalty_amount` decimal(10,2) DEFAULT '0.00' COMMENT '杩濈害閲',
  `deposit_refund` decimal(10,2) DEFAULT '0.00' COMMENT '瀹為?鎶奸噾',
  `refund_status` char(1) DEFAULT '0' COMMENT '閫??鐘舵?(0:寰呴?娆?1:宸查?娆?',
  `refund_time` datetime DEFAULT NULL COMMENT '閫??鏃堕棿',
  `tenant_signature` text COMMENT '绉熸埛绛惧悕(base64)',
  `manager_signature` text COMMENT '绠＄悊鍛樼?鍚?base64)',
  `manager_id` bigint(20) DEFAULT NULL COMMENT '缁忓姙浜篒D',
  `manager_name` varchar(50) DEFAULT NULL COMMENT '缁忓姙浜哄?鍚',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`record_id`),
  KEY `idx_contract_id` (`contract_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='閫??璁板綍琛';

-- ----------------------------
-- Records of hz_checkout_record
-- ----------------------------

-- ----------------------------
-- Table structure for hz_commitment
-- ----------------------------
DROP TABLE IF EXISTS `hz_commitment`;
CREATE TABLE `hz_commitment` (
  `commitment_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '鎵胯?涔?D',
  `tenant_id` bigint(20) NOT NULL COMMENT '绉熸埛ID',
  `commitment_type` char(1) NOT NULL COMMENT '鎵胯?涔︾被鍨?1:浜烘墠鍏?瘬 2:淇濈?鎴?',
  `commitment_content` text NOT NULL COMMENT '鎵胯?涔﹀唴瀹',
  `signature_data` text COMMENT '绛惧悕鏁版嵁(base64)',
  `sign_time` datetime DEFAULT NULL COMMENT '绛剧讲鏃堕棿',
  `sign_ip` varchar(50) DEFAULT NULL COMMENT '绛剧讲IP',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:鏈??缃?1:宸茬?缃?',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`commitment_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鎵胯?涔﹁〃';

-- ----------------------------
-- Records of hz_commitment
-- ----------------------------

-- ----------------------------
-- Table structure for hz_contract
-- ----------------------------
DROP TABLE IF EXISTS `hz_contract`;
CREATE TABLE `hz_contract` (
  `contract_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '鍚堝悓ID',
  `contract_no` varchar(50) NOT NULL COMMENT '鍚堝悓缂栧彿',
  `contract_type` char(1) NOT NULL COMMENT '鍚堝悓绫诲瀷(1:浜烘墠鍏?瘬 2:淇濈?鎴?3:甯傚満绉熻祦)',
  `template_id` bigint(20) DEFAULT NULL COMMENT '鍚堝悓妯℃澘ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '绉熸埛ID',
  `tenant_name` varchar(50) NOT NULL COMMENT '绉熸埛濮撳悕',
  `tenant_id_card` varchar(18) NOT NULL COMMENT '绉熸埛韬?唤璇',
  `tenant_phone` varchar(20) NOT NULL COMMENT '绉熸埛鎵嬫満',
  `project_id` bigint(20) NOT NULL COMMENT '椤圭洰ID',
  `house_id` bigint(20) NOT NULL COMMENT '鎴挎簮ID',
  `house_code` varchar(50) NOT NULL COMMENT '鎴挎簮缂栫爜',
  `house_address` varchar(255) DEFAULT NULL COMMENT '鎴挎簮鍦板潃',
  `rent_price` decimal(10,2) NOT NULL COMMENT '绉熼噾(鍏?鏈?',
  `deposit` decimal(10,2) NOT NULL COMMENT '鎶奸噾(鍏?',
  `start_date` date NOT NULL COMMENT '绉熻祦寮??鏃ユ湡',
  `end_date` date NOT NULL COMMENT '绉熻祦缁撴潫鏃ユ湡',
  `rent_months` int(11) NOT NULL COMMENT '绉熻祦鏈堟暟',
  `payment_cycle` char(1) DEFAULT '1' COMMENT '缂磋垂鍛ㄦ湡(1:鏈堜粯 2:瀛ｄ粯 3:鍗婂勾浠?4:骞翠粯)',
  `payment_day` int(11) DEFAULT '5' COMMENT '姣忔湀缂磋垂鏃',
  `contract_content` text COMMENT '鍚堝悓鍐呭?',
  `contract_file` varchar(255) DEFAULT NULL COMMENT '鍚堝悓鏂囦欢URL',
  `tenant_signature` text COMMENT '绉熸埛绛惧悕鏁版嵁(base64)',
  `landlord_signature` text COMMENT '鎴夸笢绛惧悕鏁版嵁(base64)',
  `sign_time` datetime DEFAULT NULL COMMENT '绛剧讲鏃堕棿',
  `contract_status` char(1) DEFAULT '0' COMMENT '鍚堝悓鐘舵?(0:鑽夌? 1:寰呯?缃?2:宸茬?缃?3:灞ヨ?涓?4:宸插埌鏈?5:宸茶В绾?',
  `is_renewed` char(1) DEFAULT '0' COMMENT '鏄?惁缁?害(0:鍚?1:鏄?',
  `renewed_contract_id` bigint(20) DEFAULT NULL COMMENT '缁?害鍚堝悓ID',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`contract_id`),
  UNIQUE KEY `uk_contract_no` (`contract_no`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_house_id` (`house_id`),
  KEY `idx_contract_status` (`contract_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鍚堝悓琛';

-- ----------------------------
-- Records of hz_contract
-- ----------------------------

-- ----------------------------
-- Table structure for hz_contract_attachment
-- ----------------------------
DROP TABLE IF EXISTS `hz_contract_attachment`;
CREATE TABLE `hz_contract_attachment` (
  `attachment_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '闄勪欢ID',
  `contract_id` bigint(20) NOT NULL COMMENT '鍚堝悓ID',
  `attachment_name` varchar(100) NOT NULL COMMENT '闄勪欢鍚嶇О',
  `attachment_url` varchar(255) NOT NULL COMMENT '闄勪欢URL',
  `attachment_type` varchar(20) DEFAULT NULL COMMENT '闄勪欢绫诲瀷',
  `file_size` bigint(20) DEFAULT NULL COMMENT '鏂囦欢澶у皬(瀛楄妭)',
  `sort_order` int(11) DEFAULT '0' COMMENT '鏄剧ず椤哄簭',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`attachment_id`),
  KEY `idx_contract_id` (`contract_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鍚堝悓闄勪欢琛';

-- ----------------------------
-- Records of hz_contract_attachment
-- ----------------------------

-- ----------------------------
-- Table structure for hz_contract_signature
-- ----------------------------
DROP TABLE IF EXISTS `hz_contract_signature`;
CREATE TABLE `hz_contract_signature` (
  `signature_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '绛剧讲ID',
  `contract_id` bigint(20) NOT NULL COMMENT '鍚堝悓ID',
  `signer_type` char(1) NOT NULL COMMENT '绛剧讲浜虹被鍨?1:绉熸埛 2:鎴夸笢 3:鍏卞悓绉熸埛)',
  `signer_id` bigint(20) NOT NULL COMMENT '绛剧讲浜篒D',
  `signer_name` varchar(50) NOT NULL COMMENT '绛剧讲浜哄?鍚',
  `signature_data` text COMMENT '绛惧悕鏁版嵁(base64)',
  `sign_time` datetime DEFAULT NULL COMMENT '绛剧讲鏃堕棿',
  `sign_ip` varchar(50) DEFAULT NULL COMMENT '绛剧讲IP',
  `sign_location` varchar(100) DEFAULT NULL COMMENT '绛剧讲鍦扮偣',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:鏈??缃?1:宸茬?缃?',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`signature_id`),
  KEY `idx_contract_id` (`contract_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鍚堝悓绛剧讲璁板綍琛';

-- ----------------------------
-- Records of hz_contract_signature
-- ----------------------------

-- ----------------------------
-- Table structure for hz_contract_template
-- ----------------------------
DROP TABLE IF EXISTS `hz_contract_template`;
CREATE TABLE `hz_contract_template` (
  `template_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '妯℃澘ID',
  `template_name` varchar(100) NOT NULL COMMENT '妯℃澘鍚嶇О',
  `template_code` varchar(50) NOT NULL COMMENT '妯℃澘缂栫爜',
  `contract_type` char(1) NOT NULL COMMENT '鍚堝悓绫诲瀷(1:浜烘墠鍏?瘬 2:淇濈?鎴?3:甯傚満绉熻祦)',
  `template_content` text NOT NULL COMMENT '妯℃澘鍐呭?(鏀?寔鍗犱綅绗?',
  `version` varchar(20) DEFAULT '1.0' COMMENT '鐗堟湰鍙',
  `is_default` char(1) DEFAULT '0' COMMENT '鏄?惁榛樿?妯℃澘(0:鍚?1:鏄?',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:姝ｅ父 1:鍋滅敤)',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`template_id`),
  UNIQUE KEY `uk_template_code` (`template_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鍚堝悓妯℃澘琛';

-- ----------------------------
-- Records of hz_contract_template
-- ----------------------------

-- ----------------------------
-- Table structure for hz_coupon
-- ----------------------------
DROP TABLE IF EXISTS `hz_coupon`;
CREATE TABLE `hz_coupon` (
  `coupon_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '浼樻儬鍒窱D',
  `coupon_name` varchar(100) NOT NULL COMMENT '浼樻儬鍒稿悕绉',
  `coupon_code` varchar(50) NOT NULL COMMENT '浼樻儬鍒哥紪鐮',
  `coupon_type` char(1) NOT NULL COMMENT '浼樻儬鍒哥被鍨?1:婊″噺 2:鎶樻墸 3:鎶垫墸)',
  `discount_amount` decimal(10,2) DEFAULT NULL COMMENT '浼樻儬閲戦?(鍏?',
  `discount_rate` decimal(5,2) DEFAULT NULL COMMENT '鎶樻墸鐜?%)',
  `min_amount` decimal(10,2) DEFAULT '0.00' COMMENT '鏈?綆浣跨敤閲戦?',
  `max_discount` decimal(10,2) DEFAULT NULL COMMENT '鏈?珮浼樻儬閲戦?',
  `total_count` int(11) DEFAULT '0' COMMENT '鍙戣?鎬婚噺',
  `received_count` int(11) DEFAULT '0' COMMENT '宸查?鍙栨暟閲',
  `used_count` int(11) DEFAULT '0' COMMENT '宸蹭娇鐢ㄦ暟閲',
  `valid_start_date` date DEFAULT NULL COMMENT '鏈夋晥鏈熷紑濮',
  `valid_end_date` date DEFAULT NULL COMMENT '鏈夋晥鏈熺粨鏉',
  `applicable_type` varchar(50) DEFAULT NULL COMMENT '閫傜敤绫诲瀷(绉熼噾/鎶奸噾绛?',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:姝ｅ父 1:鍋滅敤)',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`coupon_id`),
  UNIQUE KEY `uk_coupon_code` (`coupon_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='浼樻儬鍒歌〃';

-- ----------------------------
-- Records of hz_coupon
-- ----------------------------

-- ----------------------------
-- Table structure for hz_coupon_receive
-- ----------------------------
DROP TABLE IF EXISTS `hz_coupon_receive`;
CREATE TABLE `hz_coupon_receive` (
  `receive_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '棰嗗彇ID',
  `coupon_id` bigint(20) NOT NULL COMMENT '浼樻儬鍒窱D',
  `tenant_id` bigint(20) NOT NULL COMMENT '绉熸埛ID',
  `receive_time` datetime DEFAULT NULL COMMENT '棰嗗彇鏃堕棿',
  `receive_status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:鏈?娇鐢?1:宸蹭娇鐢?2:宸茶繃鏈?',
  `use_time` datetime DEFAULT NULL COMMENT '浣跨敤鏃堕棿',
  `order_id` bigint(20) DEFAULT NULL COMMENT '浣跨敤璁㈠崟ID',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`receive_id`),
  KEY `idx_coupon_id` (`coupon_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='浼樻儬鍒搁?鍙栬?褰曡〃';

-- ----------------------------
-- Records of hz_coupon_receive
-- ----------------------------

-- ----------------------------
-- Table structure for hz_coupon_use
-- ----------------------------
DROP TABLE IF EXISTS `hz_coupon_use`;
CREATE TABLE `hz_coupon_use` (
  `use_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '浣跨敤ID',
  `receive_id` bigint(20) NOT NULL COMMENT '棰嗗彇ID',
  `coupon_id` bigint(20) NOT NULL COMMENT '浼樻儬鍒窱D',
  `tenant_id` bigint(20) NOT NULL COMMENT '绉熸埛ID',
  `bill_id` bigint(20) NOT NULL COMMENT '璐﹀崟ID',
  `discount_amount` decimal(10,2) NOT NULL COMMENT '浼樻儬閲戦?',
  `use_time` datetime DEFAULT NULL COMMENT '浣跨敤鏃堕棿',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`use_id`),
  KEY `idx_receive_id` (`receive_id`),
  KEY `idx_bill_id` (`bill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='浼樻儬鍒镐娇鐢ㄨ?褰曡〃';

-- ----------------------------
-- Records of hz_coupon_use
-- ----------------------------

-- ----------------------------
-- Table structure for hz_co_tenant
-- ----------------------------
DROP TABLE IF EXISTS `hz_co_tenant`;
CREATE TABLE `hz_co_tenant` (
  `co_tenant_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '鍏卞悓绉熸埛ID',
  `contract_id` bigint(20) NOT NULL COMMENT '鍚堝悓ID',
  `tenant_name` varchar(50) NOT NULL COMMENT '绉熸埛濮撳悕',
  `id_card` varchar(18) NOT NULL COMMENT '韬?唤璇佸彿',
  `phone` varchar(20) NOT NULL COMMENT '鎵嬫満鍙',
  `relationship` varchar(20) DEFAULT NULL COMMENT '涓庝富绉熸埛鍏崇郴',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:姝ｅ父 1:鍋滅敤)',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`co_tenant_id`),
  KEY `idx_contract_id` (`contract_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鍏卞悓绉熸埛琛';

-- ----------------------------
-- Records of hz_co_tenant
-- ----------------------------

-- ----------------------------
-- Table structure for hz_document_audit
-- ----------------------------
DROP TABLE IF EXISTS `hz_document_audit`;
CREATE TABLE `hz_document_audit` (
  `audit_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '瀹℃牳ID',
  `document_id` bigint(20) NOT NULL COMMENT '璧勬枡ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '绉熸埛ID',
  `audit_result` char(1) NOT NULL COMMENT '瀹℃牳缁撴灉(1:閫氳繃 2:椹冲洖)',
  `audit_opinion` varchar(500) DEFAULT NULL COMMENT '瀹℃牳鎰忚?',
  `audit_by` varchar(64) DEFAULT NULL COMMENT '瀹℃牳浜',
  `audit_time` datetime DEFAULT NULL COMMENT '瀹℃牳鏃堕棿',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`audit_id`),
  KEY `idx_document_id` (`document_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='璧勬枡瀹℃牳璁板綍琛';

-- ----------------------------
-- Records of hz_document_audit
-- ----------------------------

-- ----------------------------
-- Table structure for hz_enterprise
-- ----------------------------
DROP TABLE IF EXISTS `hz_enterprise`;
CREATE TABLE `hz_enterprise` (
  `enterprise_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '浼佷笟ID',
  `enterprise_name` varchar(100) NOT NULL COMMENT '浼佷笟鍚嶇О',
  `social_credit_code` varchar(50) DEFAULT NULL COMMENT '缁熶竴绀句細淇＄敤浠ｇ爜',
  `legal_person` varchar(50) DEFAULT NULL COMMENT '娉曚汉浠ｈ〃',
  `contact_person` varchar(50) DEFAULT NULL COMMENT '鑱旂郴浜',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '鑱旂郴鐢佃瘽',
  `contact_email` varchar(100) DEFAULT NULL COMMENT '鑱旂郴閭??',
  `enterprise_address` varchar(255) DEFAULT NULL COMMENT '浼佷笟鍦板潃',
  `business_license` varchar(255) DEFAULT NULL COMMENT '钀ヤ笟鎵х収URL',
  `employee_count` int(11) DEFAULT '0' COMMENT '鍛樺伐鏁伴噺',
  `rented_count` int(11) DEFAULT '0' COMMENT '宸茬?鎴挎簮鏁',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:姝ｅ父 1:鍋滅敤)',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`enterprise_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='浼佷笟淇℃伅琛';

-- ----------------------------
-- Records of hz_enterprise
-- ----------------------------
INSERT INTO `hz_enterprise` VALUES ('1', '阿斯蒂芬', '91410100MA45TE2X81', null, 'ss', '18539279011', null, '测试地址', null, '0', '0', '0', '', null, '', null, null, '0');

-- ----------------------------
-- Table structure for hz_featured_house
-- ----------------------------
DROP TABLE IF EXISTS `hz_featured_house`;
CREATE TABLE `hz_featured_house` (
  `featured_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '绮鹃?ID',
  `house_id` bigint(20) NOT NULL COMMENT '鎴挎簮ID',
  `featured_reason` varchar(200) DEFAULT NULL COMMENT '绮鹃?鐞嗙敱',
  `start_time` datetime DEFAULT NULL COMMENT '寮??鏃堕棿',
  `end_time` datetime DEFAULT NULL COMMENT '缁撴潫鏃堕棿',
  `sort_order` int(11) DEFAULT '0' COMMENT '鏄剧ず椤哄簭',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:姝ｅ父 1:鍋滅敤)',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`featured_id`),
  KEY `idx_house_id` (`house_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='绮鹃?鎴挎簮琛';

-- ----------------------------
-- Records of hz_featured_house
-- ----------------------------

-- ----------------------------
-- Table structure for hz_house
-- ----------------------------
DROP TABLE IF EXISTS `hz_house`;
CREATE TABLE `hz_house` (
  `house_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '鎴挎簮ID',
  `project_id` bigint(20) NOT NULL COMMENT '椤圭洰ID',
  `building_id` bigint(20) NOT NULL COMMENT '妤兼爧ID',
  `unit_id` bigint(20) DEFAULT NULL COMMENT '鍗曞厓ID',
  `house_code` varchar(50) NOT NULL COMMENT '鎴挎簮缂栫爜',
  `house_no` varchar(50) NOT NULL COMMENT '鎴块棿鍙',
  `floor` int(11) NOT NULL COMMENT '妤煎眰',
  `house_type_id` bigint(20) DEFAULT NULL COMMENT '鎴峰瀷ID',
  `house_type_name` varchar(50) DEFAULT NULL COMMENT '鎴峰瀷鍚嶇О',
  `area` decimal(8,2) DEFAULT NULL COMMENT '寤虹瓚闈㈢Н(骞虫柟绫?',
  `rent_price` decimal(10,2) DEFAULT NULL COMMENT '绉熼噾(鍏?鏈?',
  `deposit` decimal(10,2) DEFAULT NULL COMMENT '鎶奸噾(鍏?',
  `orientation` varchar(20) DEFAULT NULL COMMENT '鏈濆悜',
  `decoration` varchar(20) DEFAULT NULL COMMENT '瑁呬慨鎯呭喌',
  `facilities` varchar(500) DEFAULT NULL COMMENT '鎴块棿璁炬柦(澶氫釜鐢ㄩ?鍙峰垎闅?',
  `house_status` char(1) DEFAULT '0' COMMENT '鎴挎簮鐘舵?(0:绌虹疆 1:宸查?璁?2:宸插嚭绉?3:缁翠慨涓?4:涓嬫灦)',
  `is_featured` char(1) DEFAULT '0' COMMENT '鏄?惁绮鹃?鎴挎簮(0:鍚?1:鏄?',
  `view_count` int(11) DEFAULT '0' COMMENT '娴忚?娆℃暟',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:姝ｅ父 1:鍋滅敤)',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`house_id`),
  UNIQUE KEY `uk_house_code` (`house_code`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_building_id` (`building_id`),
  KEY `idx_house_status` (`house_status`),
  KEY `idx_is_featured` (`is_featured`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鎴挎簮琛';

-- ----------------------------
-- Records of hz_house
-- ----------------------------
INSERT INTO `hz_house` VALUES ('1', '2', '3', '5', '123', '456', '1', '14', '大三房', '60.00', '2000.00', '3000.00', '南', '毛坯', null, '0', '0', '0', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house` VALUES ('2', '1', '5', '11', 'kl', '502', '1', '10', '阿斯蒂芬456465', '0.00', '500.00', '500.00', '东', null, null, '0', '0', '0', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house` VALUES ('3', '1', '1', '1', 'llkkk', '456', '1', '10', '阿斯蒂芬456465', '60.00', '9000.00', '6000.00', '南', '毛坯', '阿斯蒂芬', '0', '1', '0', '0', '', null, '', null, '阿斯蒂芬', '0');
INSERT INTO `hz_house` VALUES ('4', '3', '6', '12', '01', '123', '1', '15', '测试01', '600.00', '5000.00', '200.00', '南', '毛坯', '少时诵诗书', '0', '0', '0', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house` VALUES ('5', '4', '7', '13', '12', '301', '1', '16', '大三房', '180.00', '2000.00', '1000.00', '东', null, null, '0', '0', '0', '0', '', null, '', null, null, '0');

-- ----------------------------
-- Table structure for hz_house_exchange
-- ----------------------------
DROP TABLE IF EXISTS `hz_house_exchange`;
CREATE TABLE `hz_house_exchange` (
  `exchange_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '鎹㈡埧ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '绉熸埛ID',
  `old_contract_id` bigint(20) NOT NULL COMMENT '鍘熷悎鍚孖D',
  `old_house_id` bigint(20) NOT NULL COMMENT '鍘熸埧婧怚D',
  `old_house_code` varchar(50) NOT NULL COMMENT '鍘熸埧婧愮紪鐮',
  `new_house_id` bigint(20) NOT NULL COMMENT '鏂版埧婧怚D',
  `new_house_code` varchar(50) NOT NULL COMMENT '鏂版埧婧愮紪鐮',
  `exchange_reason` varchar(500) DEFAULT NULL COMMENT '鎹㈡埧鍘熷洜',
  `apply_time` datetime DEFAULT NULL COMMENT '鐢宠?鏃堕棿',
  `approve_result` char(1) DEFAULT '0' COMMENT '瀹℃壒缁撴灉(0:寰呭?鎵?1:閫氳繃 2:椹冲洖)',
  `approve_opinion` varchar(500) DEFAULT NULL COMMENT '瀹℃壒鎰忚?',
  `approve_by` varchar(64) DEFAULT NULL COMMENT '瀹℃壒浜',
  `approve_time` datetime DEFAULT NULL COMMENT '瀹℃壒鏃堕棿',
  `exchange_time` datetime DEFAULT NULL COMMENT '鎹㈡埧鏃堕棿',
  `new_contract_id` bigint(20) DEFAULT NULL COMMENT '鏂板悎鍚孖D',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:寰呭?鎵?1:宸叉壒鍑?2:宸查┏鍥?3:宸插畬鎴?',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`exchange_id`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_old_contract_id` (`old_contract_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鎹㈡埧璁板綍琛';

-- ----------------------------
-- Records of hz_house_exchange
-- ----------------------------

-- ----------------------------
-- Table structure for hz_house_image
-- ----------------------------
DROP TABLE IF EXISTS `hz_house_image`;
CREATE TABLE `hz_house_image` (
  `image_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '鍥剧墖ID',
  `house_id` bigint(20) NOT NULL COMMENT '鎴挎簮ID',
  `image_url` varchar(255) NOT NULL COMMENT '鍥剧墖URL',
  `image_type` varchar(20) DEFAULT NULL COMMENT '图片类型(1:主图 2:户型图 3:卧室 4:卫生间 5:室内 6:室外)',
  `is_cover` char(1) DEFAULT '0' COMMENT '鏄?惁灏侀潰(0:鍚?1:鏄?',
  `sort_order` int(11) DEFAULT '0' COMMENT '鏄剧ず椤哄簭',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`image_id`),
  KEY `idx_house_id` (`house_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鎴挎簮鍥剧墖琛';

-- ----------------------------
-- Records of hz_house_image
-- ----------------------------
INSERT INTO `hz_house_image` VALUES ('1', '3', '/profile/upload/2026/01/11/矩形 21@2x_20260111031203A005.png', '1', '1', '0', '', null, '', null, null, '2');
INSERT INTO `hz_house_image` VALUES ('2', '3', '/profile/upload/2026/01/11/矩形 21@2x_20260111031203A005.png', '1', '1', '0', '', null, '', null, null, '2');
INSERT INTO `hz_house_image` VALUES ('3', '3', '/profile/upload/2026/01/11/市场租赁@2x_20260111031214A006.png', '1', '0', '1', '', null, '', null, null, '2');
INSERT INTO `hz_house_image` VALUES ('4', '3', '/profile/upload/2026/01/11/矩形 21@2x_20260111031203A005.png', '1', '1', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house_image` VALUES ('5', '3', '/profile/upload/2026/01/11/市场租赁@2x_20260111031214A006.png', '1', '0', '1', '', null, '', null, null, '0');
INSERT INTO `hz_house_image` VALUES ('6', '4', '/profile/upload/2026/01/11/WPS图片(1)_20260111151641A001.jpeg', '1', '1', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house_image` VALUES ('7', '5', '/profile/upload/2026/01/12/矩形 21@2x_20260112014153A004.png', '1', '1', '0', '', null, '', null, null, '2');
INSERT INTO `hz_house_image` VALUES ('8', '5', '/profile/upload/2026/01/12/人才公寓@2x_20260112014154A005.png', '1', '0', '1', '', null, '', null, null, '2');
INSERT INTO `hz_house_image` VALUES ('9', '5', '/profile/upload/2026/01/12/矩形 21@2x_20260112014153A004.png', '1', '1', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house_image` VALUES ('10', '5', '/profile/upload/2026/01/12/人才公寓@2x_20260112014154A005.png', '1', '0', '1', '', null, '', null, null, '0');
INSERT INTO `hz_house_image` VALUES ('11', '5', '/profile/upload/2026/01/12/df079c167cae476cb21790c62b610481_20260112033330A002.png', '2', '0', '2', '', null, '', null, null, '0');
INSERT INTO `hz_house_image` VALUES ('12', '5', '/profile/upload/2026/01/12/R-C (1)_20260112033332A003.jpg', '2', '0', '3', '', null, '', null, null, '0');
INSERT INTO `hz_house_image` VALUES ('13', '5', '/profile/upload/2026/01/12/4255.jpg_wh860_20260112033334A004.jpg', '3', '0', '4', '', null, '', null, null, '0');
INSERT INTO `hz_house_image` VALUES ('14', '5', '/profile/upload/2026/01/12/ScreenShot_2026-01-12_015115_648_20260112033336A005.png', '3', '0', '5', '', null, '', null, null, '0');
INSERT INTO `hz_house_image` VALUES ('15', '5', '/profile/upload/2026/01/12/R-C (1)_20260112033338A006.jpg', '4', '0', '6', '', null, '', null, null, '0');
INSERT INTO `hz_house_image` VALUES ('16', '5', '/profile/upload/2026/01/12/df079c167cae476cb21790c62b610481_20260112033344A008.png', '5', '0', '7', '', null, '', null, null, '0');
INSERT INTO `hz_house_image` VALUES ('17', '5', '/profile/upload/2026/01/12/df079c167cae476cb21790c62b610481_20260112033341A007.png', '6', '0', '8', '', null, '', null, null, '0');

-- ----------------------------
-- Table structure for hz_house_type
-- ----------------------------
DROP TABLE IF EXISTS `hz_house_type`;
CREATE TABLE `hz_house_type` (
  `house_type_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '鎴峰瀷ID',
  `project_id` bigint(20) DEFAULT NULL COMMENT '椤圭洰ID',
  `house_type_name` varchar(50) NOT NULL COMMENT '鎴峰瀷鍚嶇О(濡?涓??涓?巺)',
  `house_type_code` varchar(50) NOT NULL COMMENT '鎴峰瀷缂栫爜',
  `bedroom_count` int(11) DEFAULT '0' COMMENT '鍗у?鏁伴噺',
  `livingroom_count` int(11) DEFAULT '0' COMMENT '瀹㈠巺鏁伴噺',
  `bathroom_count` int(11) DEFAULT '0' COMMENT '鍗?敓闂存暟閲',
  `kitchen_count` int(11) DEFAULT '0' COMMENT '鍘ㄦ埧鏁伴噺',
  `balcony_count` int(11) DEFAULT '0' COMMENT '闃冲彴鏁伴噺',
  `typical_area` decimal(8,2) DEFAULT NULL COMMENT '鍏稿瀷闈㈢Н(骞虫柟绫?',
  `layout_image` varchar(255) DEFAULT NULL COMMENT '鎴峰瀷鍥綰RL',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:姝ｅ父 1:鍋滅敤)',
  `sort_order` int(11) DEFAULT '0' COMMENT '鏄剧ず椤哄簭',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`house_type_id`),
  UNIQUE KEY `uk_house_type_code` (`house_type_code`),
  KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鎴峰瀷琛';

-- ----------------------------
-- Records of hz_house_type
-- ----------------------------
INSERT INTO `hz_house_type` VALUES ('1', null, '阿斯蒂芬', '12', '0', '0', '0', '0', '0', '0.00', null, '0', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('2', null, '123123', '23', '0', '0', '0', '0', '0', '0.00', null, '0', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('4', null, '去玩儿去玩儿', '87897878', '0', '0', '0', '0', '0', '0.00', null, '0', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('6', null, '阿斯蒂芬022', '787889', '0', '0', '0', '0', '0', '0.00', null, '0', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('7', null, '阿斯蒂芬', '8712', '0', '0', '0', '0', '0', '0.00', null, '0', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('8', '1', '是是是', '45s', '0', '0', '0', '0', '0', '0.00', null, '0', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('9', '1', '阿斯蒂芬阿斯蒂芬', '4546556', '0', '0', '0', '0', '0', '0.00', null, '0', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('10', '1', '阿斯蒂芬456465', '79878', '0', '0', '0', '0', '0', '0.00', null, '0', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('11', '1', '阿斯蒂123123123', '4546啊', '0', '0', '0', '0', '0', '0.00', null, '0', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('12', '1', '阿斯蒂芬', '阿斯蒂芬000000', '0', '0', '0', '0', '0', '0.00', null, '0', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('13', '1', '森岛帆高121', '1212', '0', '0', '0', '0', '0', '0.00', null, '0', '0', '', null, '', null, '阿斯蒂', '0');
INSERT INTO `hz_house_type` VALUES ('14', '2', '大三房', 'dasanfang01', '3', '1', '1', '1', '1', '150.00', null, '0', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('15', '3', '测试01', 'ceshi01', '1', '1', '0', '0', '0', '0.00', null, '0', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('16', '4', '大三房', 'dasanfang', '3', '2', '1', '1', '1', '180.00', null, '0', '0', '', null, '', null, null, '0');

-- ----------------------------
-- Table structure for hz_house_type_image
-- ----------------------------
DROP TABLE IF EXISTS `hz_house_type_image`;
CREATE TABLE `hz_house_type_image` (
  `image_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '鍥剧墖ID',
  `house_type_id` bigint(20) NOT NULL COMMENT '鎴峰瀷ID',
  `image_url` varchar(500) NOT NULL COMMENT '鍥剧墖URL',
  `image_type` char(1) DEFAULT '1' COMMENT '鍥剧墖绫诲瀷(1:鎴峰瀷鍥?2:瀹炴櫙鍥?',
  `is_cover` char(1) DEFAULT '0' COMMENT '鏄?惁灏侀潰(0:鍚?1:鏄?',
  `sort_order` int(4) DEFAULT '0' COMMENT '鏄剧ず椤哄簭',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`image_id`),
  KEY `idx_house_type_id` (`house_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鎴峰瀷鍥剧墖琛';

-- ----------------------------
-- Records of hz_house_type_image
-- ----------------------------
INSERT INTO `hz_house_type_image` VALUES ('1', '13', '/profile/upload/2025/11/19/logo_20251119024029A016.png', '1', '1', '1', '', null, '', null, null, '2');
INSERT INTO `hz_house_type_image` VALUES ('2', '13', '/profile/upload/2025/11/19/logo_20251119024029A016.png', '1', '1', '1', '', null, '', null, null, '0');
INSERT INTO `hz_house_type_image` VALUES ('3', '13', '/profile/upload/2025/11/19/profile_20251119024737A017.jpg', '1', '0', '2', '', null, '', null, null, '0');

-- ----------------------------
-- Table structure for hz_house_vr
-- ----------------------------
DROP TABLE IF EXISTS `hz_house_vr`;
CREATE TABLE `hz_house_vr` (
  `vr_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'VRID',
  `house_id` bigint(20) NOT NULL COMMENT '鎴挎簮ID',
  `vr_name` varchar(100) DEFAULT NULL COMMENT 'VR鍚嶇О',
  `vr_url` varchar(255) NOT NULL COMMENT 'VR鍏ㄦ櫙鍥綰RL',
  `sort_order` int(11) DEFAULT '0' COMMENT '鏄剧ず椤哄簭',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`vr_id`),
  KEY `idx_house_id` (`house_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鎴挎簮VR琛';

-- ----------------------------
-- Records of hz_house_vr
-- ----------------------------
INSERT INTO `hz_house_vr` VALUES ('1', '3', 'VR1', '/profile/upload/2026/01/11/QP61014284_20260111033434A001.jpg', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house_vr` VALUES ('2', '4', 'VR1', '/profile/upload/2026/01/11/QP61014284_20260111151706A002.jpg', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house_vr` VALUES ('3', '5', 'VR1', '/profile/upload/2026/01/12/QP61014284_20260112014202A006.jpg', '0', '', null, '', null, null, '2');
INSERT INTO `hz_house_vr` VALUES ('4', '5', 'VR1', '/profile/upload/2026/01/12/QP61014284_20260112014202A006.jpg', '0', '', null, '', null, null, '0');

-- ----------------------------
-- Table structure for hz_inventory_list
-- ----------------------------
DROP TABLE IF EXISTS `hz_inventory_list`;
CREATE TABLE `hz_inventory_list` (
  `list_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '娓呭崟ID',
  `house_id` bigint(20) NOT NULL COMMENT '鎴挎簮ID',
  `list_type` char(1) NOT NULL COMMENT '娓呭崟绫诲瀷(1:鍏ヤ綇 2:閫??)',
  `list_items` text COMMENT '娓呭崟椤圭洰(JSON鏍煎紡)',
  `template_id` bigint(20) DEFAULT NULL COMMENT '娓呭崟妯℃澘ID',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`list_id`),
  KEY `idx_house_id` (`house_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鐗╁搧娓呭崟琛';

-- ----------------------------
-- Records of hz_inventory_list
-- ----------------------------

-- ----------------------------
-- Table structure for hz_invoice
-- ----------------------------
DROP TABLE IF EXISTS `hz_invoice`;
CREATE TABLE `hz_invoice` (
  `invoice_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '鍙戠エID',
  `invoice_no` varchar(50) NOT NULL COMMENT '鍙戠エ鍙风爜',
  `invoice_code` varchar(50) DEFAULT NULL COMMENT '鍙戠エ浠ｇ爜',
  `apply_id` bigint(20) NOT NULL COMMENT '鐢宠?ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '绉熸埛ID',
  `invoice_type` char(1) NOT NULL COMMENT '鍙戠エ绫诲瀷(1:澧炲?绋庢櫘閫氬彂绁?2:澧炲?绋庝笓鐢ㄥ彂绁?',
  `invoice_title` varchar(200) NOT NULL COMMENT '鍙戠エ鎶?ご',
  `tax_no` varchar(50) DEFAULT NULL COMMENT '绾崇◣浜鸿瘑鍒?彿',
  `invoice_amount` decimal(10,2) NOT NULL COMMENT '寮?エ閲戦?',
  `tax_amount` decimal(10,2) DEFAULT '0.00' COMMENT '绋庨?',
  `invoice_date` date DEFAULT NULL COMMENT '寮?エ鏃ユ湡',
  `invoice_pdf` varchar(255) DEFAULT NULL COMMENT '鍙戠エPDF',
  `invoice_status` char(1) DEFAULT '0' COMMENT '鍙戠エ鐘舵?(0:姝ｅ父 1:宸蹭綔搴?',
  `void_reason` varchar(500) DEFAULT NULL COMMENT '浣滃簾鍘熷洜',
  `void_time` datetime DEFAULT NULL COMMENT '浣滃簾鏃堕棿',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`invoice_id`),
  UNIQUE KEY `uk_invoice_no` (`invoice_no`),
  KEY `idx_apply_id` (`apply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鍙戠エ琛';

-- ----------------------------
-- Records of hz_invoice
-- ----------------------------

-- ----------------------------
-- Table structure for hz_invoice_apply
-- ----------------------------
DROP TABLE IF EXISTS `hz_invoice_apply`;
CREATE TABLE `hz_invoice_apply` (
  `apply_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '鐢宠?ID',
  `apply_no` varchar(50) NOT NULL COMMENT '鐢宠?缂栧彿',
  `tenant_id` bigint(20) NOT NULL COMMENT '绉熸埛ID',
  `tenant_name` varchar(50) NOT NULL COMMENT '绉熸埛濮撳悕',
  `invoice_type` char(1) NOT NULL COMMENT '鍙戠エ绫诲瀷(1:澧炲?绋庢櫘閫氬彂绁?2:澧炲?绋庝笓鐢ㄥ彂绁?',
  `invoice_title` varchar(200) NOT NULL COMMENT '鍙戠エ鎶?ご',
  `tax_no` varchar(50) DEFAULT NULL COMMENT '绾崇◣浜鸿瘑鍒?彿',
  `company_address` varchar(255) DEFAULT NULL COMMENT '鍗曚綅鍦板潃',
  `company_phone` varchar(20) DEFAULT NULL COMMENT '鍗曚綅鐢佃瘽',
  `bank_name` varchar(100) DEFAULT NULL COMMENT '寮?埛琛',
  `bank_account` varchar(50) DEFAULT NULL COMMENT '閾惰?璐﹀彿',
  `invoice_amount` decimal(10,2) NOT NULL COMMENT '寮?エ閲戦?',
  `invoice_content` varchar(500) DEFAULT NULL COMMENT '寮?エ鍐呭?',
  `bill_ids` varchar(500) DEFAULT NULL COMMENT '璐﹀崟ID鍒楄〃(澶氫釜鐢ㄩ?鍙峰垎闅?',
  `email` varchar(100) DEFAULT NULL COMMENT '鎺ユ敹閭??',
  `receiver_name` varchar(50) DEFAULT NULL COMMENT '鏀朵欢浜哄?鍚',
  `receiver_phone` varchar(20) DEFAULT NULL COMMENT '鏀朵欢浜虹數璇',
  `receiver_address` varchar(255) DEFAULT NULL COMMENT '鏀朵欢鍦板潃',
  `apply_time` datetime DEFAULT NULL COMMENT '鐢宠?鏃堕棿',
  `apply_status` char(1) DEFAULT '0' COMMENT '鐢宠?鐘舵?(0:寰呭?鏍?1:宸插紑绁?2:宸查┏鍥?3:宸查偖瀵?',
  `approve_by` varchar(64) DEFAULT NULL COMMENT '瀹℃壒浜',
  `approve_time` datetime DEFAULT NULL COMMENT '瀹℃壒鏃堕棿',
  `reject_reason` varchar(500) DEFAULT NULL COMMENT '椹冲洖鍘熷洜',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`apply_id`),
  UNIQUE KEY `uk_apply_no` (`apply_no`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='寮?エ鐢宠?琛';

-- ----------------------------
-- Records of hz_invoice_apply
-- ----------------------------

-- ----------------------------
-- Table structure for hz_invoice_attachment
-- ----------------------------
DROP TABLE IF EXISTS `hz_invoice_attachment`;
CREATE TABLE `hz_invoice_attachment` (
  `attachment_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '闄勪欢ID',
  `invoice_id` bigint(20) NOT NULL COMMENT '鍙戠エID',
  `attachment_name` varchar(100) NOT NULL COMMENT '闄勪欢鍚嶇О',
  `attachment_url` varchar(255) NOT NULL COMMENT '闄勪欢URL',
  `attachment_type` varchar(20) DEFAULT NULL COMMENT '闄勪欢绫诲瀷',
  `file_size` bigint(20) DEFAULT NULL COMMENT '鏂囦欢澶у皬(瀛楄妭)',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`attachment_id`),
  KEY `idx_invoice_id` (`invoice_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鍙戠エ闄勪欢琛';

-- ----------------------------
-- Records of hz_invoice_attachment
-- ----------------------------

-- ----------------------------
-- Table structure for hz_item_status
-- ----------------------------
DROP TABLE IF EXISTS `hz_item_status`;
CREATE TABLE `hz_item_status` (
  `status_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '鐘舵?ID',
  `list_id` bigint(20) NOT NULL COMMENT '娓呭崟ID',
  `item_name` varchar(100) NOT NULL COMMENT '鐗╁搧鍚嶇О',
  `item_category` varchar(50) DEFAULT NULL COMMENT '鐗╁搧鍒嗙被',
  `quantity` int(11) DEFAULT '1' COMMENT '鏁伴噺',
  `item_status` varchar(20) DEFAULT NULL COMMENT '鐗╁搧鐘舵?(瀹屽ソ/鎹熷潖/缂哄け)',
  `damage_level` varchar(20) DEFAULT NULL COMMENT '鎹熷潖绋嬪害',
  `item_photo` varchar(255) DEFAULT NULL COMMENT '鐗╁搧鐓х墖',
  `sort_order` int(11) DEFAULT '0' COMMENT '鏄剧ず椤哄簭',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`status_id`),
  KEY `idx_list_id` (`list_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鐗╁搧鐘舵?琛';

-- ----------------------------
-- Records of hz_item_status
-- ----------------------------

-- ----------------------------
-- Table structure for hz_map_point
-- ----------------------------
DROP TABLE IF EXISTS `hz_map_point`;
CREATE TABLE `hz_map_point` (
  `point_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '鐐逛綅ID',
  `project_id` bigint(20) NOT NULL COMMENT '椤圭洰ID',
  `point_name` varchar(100) NOT NULL COMMENT '鐐逛綅鍚嶇О',
  `longitude` decimal(10,7) NOT NULL COMMENT '缁忓害',
  `latitude` decimal(10,7) NOT NULL COMMENT '绾?害',
  `marker_icon` varchar(255) DEFAULT NULL COMMENT '鏍囪?鍥炬爣',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:姝ｅ父 1:鍋滅敤)',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`point_id`),
  KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鍦板浘鎵炬埧鐐逛綅琛';

-- ----------------------------
-- Records of hz_map_point
-- ----------------------------

-- ----------------------------
-- Table structure for hz_message
-- ----------------------------
DROP TABLE IF EXISTS `hz_message`;
CREATE TABLE `hz_message` (
  `message_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '娑堟伅ID',
  `message_type` char(1) NOT NULL COMMENT '娑堟伅绫诲瀷(1:绯荤粺閫氱煡 2:鐭?俊 3:閭?欢)',
  `template_id` bigint(20) DEFAULT NULL COMMENT '妯℃澘ID',
  `receiver_id` bigint(20) NOT NULL COMMENT '鎺ユ敹浜篒D',
  `receiver_name` varchar(50) DEFAULT NULL COMMENT '鎺ユ敹浜哄?鍚',
  `receiver_phone` varchar(20) DEFAULT NULL COMMENT '鎺ユ敹浜烘墜鏈',
  `receiver_email` varchar(100) DEFAULT NULL COMMENT '鎺ユ敹浜洪偖绠',
  `message_title` varchar(200) DEFAULT NULL COMMENT '娑堟伅鏍囬?',
  `message_content` text NOT NULL COMMENT '娑堟伅鍐呭?',
  `send_time` datetime DEFAULT NULL COMMENT '鍙戦?鏃堕棿',
  `send_status` char(1) DEFAULT '0' COMMENT '鍙戦?鐘舵?(0:寰呭彂閫?1:宸插彂閫?2:鍙戦?澶辫触)',
  `read_status` char(1) DEFAULT '0' COMMENT '闃呰?鐘舵?(0:鏈?? 1:宸茶?)',
  `read_time` datetime DEFAULT NULL COMMENT '闃呰?鏃堕棿',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`message_id`),
  KEY `idx_receiver_id` (`receiver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='娑堟伅璁板綍琛';

-- ----------------------------
-- Records of hz_message
-- ----------------------------

-- ----------------------------
-- Table structure for hz_message_template
-- ----------------------------
DROP TABLE IF EXISTS `hz_message_template`;
CREATE TABLE `hz_message_template` (
  `template_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '妯℃澘ID',
  `template_code` varchar(50) NOT NULL COMMENT '妯℃澘缂栫爜',
  `template_name` varchar(100) NOT NULL COMMENT '妯℃澘鍚嶇О',
  `template_type` char(1) NOT NULL COMMENT '妯℃澘绫诲瀷(1:绯荤粺閫氱煡 2:鐭?俊 3:閭?欢)',
  `template_content` text NOT NULL COMMENT '妯℃澘鍐呭?(鏀?寔鍗犱綅绗?',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:姝ｅ父 1:鍋滅敤)',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`template_id`),
  UNIQUE KEY `uk_template_code` (`template_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='娑堟伅妯℃澘琛';

-- ----------------------------
-- Records of hz_message_template
-- ----------------------------

-- ----------------------------
-- Table structure for hz_operation_config
-- ----------------------------
DROP TABLE IF EXISTS `hz_operation_config`;
CREATE TABLE `hz_operation_config` (
  `config_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '閰嶇疆ID',
  `config_key` varchar(50) NOT NULL COMMENT '閰嶇疆閿',
  `config_value` text NOT NULL COMMENT '閰嶇疆鍊',
  `config_desc` varchar(200) DEFAULT NULL COMMENT '閰嶇疆鎻忚堪',
  `config_type` varchar(50) DEFAULT NULL COMMENT '閰嶇疆绫诲瀷',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:姝ｅ父 1:鍋滅敤)',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`config_id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='杩愯惀閰嶇疆琛';

-- ----------------------------
-- Records of hz_operation_config
-- ----------------------------

-- ----------------------------
-- Table structure for hz_payment
-- ----------------------------
DROP TABLE IF EXISTS `hz_payment`;
CREATE TABLE `hz_payment` (
  `payment_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '缂磋垂ID',
  `payment_no` varchar(50) NOT NULL COMMENT '缂磋垂缂栧彿',
  `bill_id` bigint(20) NOT NULL COMMENT '璐﹀崟ID',
  `bill_no` varchar(50) NOT NULL COMMENT '璐﹀崟缂栧彿',
  `tenant_id` bigint(20) NOT NULL COMMENT '绉熸埛ID',
  `tenant_name` varchar(50) NOT NULL COMMENT '绉熸埛濮撳悕',
  `payment_amount` decimal(10,2) NOT NULL COMMENT '缂磋垂閲戦?',
  `payment_method` varchar(20) NOT NULL COMMENT '鏀?粯鏂瑰紡(鏀?粯瀹?寰?俊/閾惰?鍗＄瓑)',
  `payment_channel` varchar(20) DEFAULT NULL COMMENT '鏀?粯娓犻亾(娓?尯鏀?粯)',
  `transaction_no` varchar(100) DEFAULT NULL COMMENT '绗?笁鏂逛氦鏄撴祦姘村彿',
  `payment_time` datetime DEFAULT NULL COMMENT '鏀?粯鏃堕棿',
  `payment_status` char(1) DEFAULT '0' COMMENT '鏀?粯鐘舵?(0:寰呮敮浠?1:鏀?粯鎴愬姛 2:鏀?粯澶辫触 3:宸查?娆?',
  `callback_time` datetime DEFAULT NULL COMMENT '鍥炶皟鏃堕棿',
  `callback_data` text COMMENT '鍥炶皟鏁版嵁',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`payment_id`),
  UNIQUE KEY `uk_payment_no` (`payment_no`),
  KEY `idx_bill_id` (`bill_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='缂磋垂璁板綍琛';

-- ----------------------------
-- Records of hz_payment
-- ----------------------------

-- ----------------------------
-- Table structure for hz_policy
-- ----------------------------
DROP TABLE IF EXISTS `hz_policy`;
CREATE TABLE `hz_policy` (
  `policy_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '鏀跨瓥ID',
  `policy_title` varchar(200) NOT NULL COMMENT '鏀跨瓥鏍囬?',
  `policy_no` varchar(50) DEFAULT NULL COMMENT '鏀跨瓥鏂囧彿',
  `policy_type` varchar(50) DEFAULT NULL COMMENT '鏀跨瓥绫诲瀷',
  `policy_content` text NOT NULL COMMENT '鏀跨瓥鍐呭?',
  `policy_file` varchar(255) DEFAULT NULL COMMENT '鏀跨瓥鏂囦欢URL',
  `publish_dept` varchar(100) DEFAULT NULL COMMENT '鍙戝竷閮ㄩ棬',
  `publish_date` date DEFAULT NULL COMMENT '鍙戝竷鏃ユ湡',
  `effective_date` date DEFAULT NULL COMMENT '鐢熸晥鏃ユ湡',
  `view_count` int(11) DEFAULT '0' COMMENT '娴忚?娆℃暟',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:鑽夌? 1:宸插彂甯?2:宸蹭笅绾?',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`policy_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鏀跨瓥鏂囦欢琛';

-- ----------------------------
-- Records of hz_policy
-- ----------------------------

-- ----------------------------
-- Table structure for hz_project
-- ----------------------------
DROP TABLE IF EXISTS `hz_project`;
CREATE TABLE `hz_project` (
  `project_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '椤圭洰ID',
  `project_name` varchar(100) NOT NULL COMMENT '椤圭洰鍚嶇О',
  `project_code` varchar(50) NOT NULL COMMENT '椤圭洰缂栫爜',
  `project_type` char(1) NOT NULL COMMENT '椤圭洰绫诲瀷(1:浜烘墠鍏?瘬 2:淇濈?鎴?3:甯傚満绉熻祦)',
  `address` varchar(255) DEFAULT NULL COMMENT '椤圭洰鍦板潃',
  `longitude` decimal(10,7) DEFAULT NULL COMMENT '缁忓害',
  `latitude` decimal(10,7) DEFAULT NULL COMMENT '绾?害',
  `total_buildings` int(11) DEFAULT '0' COMMENT '鎬绘ゼ鏍嬫暟',
  `total_houses` int(11) DEFAULT '0' COMMENT '鎬绘埧婧愭暟',
  `available_houses` int(11) DEFAULT '0' COMMENT '鍙?敤鎴挎簮鏁',
  `project_intro` text COMMENT '椤圭洰浠嬬粛',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '项目主图',
  `price` decimal(10,2) DEFAULT NULL COMMENT '起租价格(元/月)',
  `facilities` varchar(500) DEFAULT NULL COMMENT '閰嶅?璁炬柦(澶氫釜鐢ㄩ?鍙峰垎闅?',
  `transportation` varchar(500) DEFAULT NULL COMMENT '浜ら?淇℃伅',
  `manager_id` bigint(20) DEFAULT NULL COMMENT '椤圭洰璐熻矗浜篒D',
  `manager_name` varchar(50) DEFAULT NULL COMMENT '椤圭洰璐熻矗浜哄?鍚',
  `manager_phone` varchar(20) DEFAULT NULL COMMENT '璐熻矗浜虹數璇',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:姝ｅ父 1:鍋滅敤)',
  `sort_order` int(11) DEFAULT '0' COMMENT '鏄剧ず椤哄簭',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`project_id`),
  UNIQUE KEY `uk_project_code` (`project_code`),
  KEY `idx_project_type` (`project_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='椤圭洰琛';

-- ----------------------------
-- Records of hz_project
-- ----------------------------
INSERT INTO `hz_project` VALUES ('1', '阿斯蒂芬', '012', '1', '阿斯蒂芬', '123.1210000', '21.1212000', '0', '0', '0', '', '/profile/upload/2026/01/12/合住人-灰@2x_20260112003348A001.png', '2000.00', '阿斯蒂芬,商业街,停车场', '00', null, 'wenwang', '18539279011', '0', '0', '', null, '', null, null, '2');
INSERT INTO `hz_project` VALUES ('2', '测试1', '56', '1', '阿斯蒂芬', '0.0000000', '0.0000000', '50', '500', '200', null, '/profile/upload/2026/01/12/df079c167cae476cb21790c62b610481_20260112015410A003.png', '6000.00', '停车场,运动场,游泳池,健身房', null, null, '菜', '18539279011', '0', '2', '', null, '', null, null, '0');
INSERT INTO `hz_project` VALUES ('3', '港区测试项目1', '12', '1', '港区大河路', '0.0000000', '0.0000000', '0', '0', '0', null, '/profile/upload/2026/01/12/5033.jpg_wh300_20260112015248A001.jpg', '5000.00', '商业街', null, null, '王飞飞', '18539279011', '0', '0', '', null, '', null, null, '0');
INSERT INTO `hz_project` VALUES ('4', '港区万科城', 'wanke', '1', '港区万科城', '0.0000000', '0.0000000', '100', '200', '120', '这是个测试项目', '/profile/upload/2026/01/12/ScreenShot_2026-01-12_015115_648_20260112015403A002.png', '1500.00', '商业街,停车场,运动场,游泳池', null, null, '陈飞飞', '18539279011', '0', '10', '', null, '', null, null, '0');
INSERT INTO `hz_project` VALUES ('5', '港区金科城', '3695', '1', '港区金科路395号', '0.0000000', '0.0000000', '185', '120', '10', null, '/profile/upload/2026/01/12/R-C_20260112015435A004.jpg', '2800.00', '停车场,商业街', null, null, '李菲数', '18564659565', '0', '0', '', null, '', null, null, '0');
INSERT INTO `hz_project` VALUES ('6', '港区恒大城', 'hengda', '2', '港区黄河路123号恒大城', '0.0000000', '0.0000000', '100', '100', '10', '暂无介绍', '/profile/upload/2026/01/12/ScreenShot_2026-01-12_015115_648_20260112031148A001.png', '6300.00', '快递柜,停车场,商业街,医疗室,运动场,游泳池', null, null, '廖凡', '13695645652', '0', '0', '', null, '', null, null, '0');

-- ----------------------------
-- Table structure for hz_project_manager
-- ----------------------------
DROP TABLE IF EXISTS `hz_project_manager`;
CREATE TABLE `hz_project_manager` (
  `manager_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '璐熻矗浜篒D',
  `user_id` bigint(20) DEFAULT NULL COMMENT '鍏宠仈鐢ㄦ埛ID',
  `manager_name` varchar(50) NOT NULL COMMENT '璐熻矗浜哄?鍚',
  `phone` varchar(20) NOT NULL COMMENT '鑱旂郴鐢佃瘽',
  `email` varchar(100) DEFAULT NULL COMMENT '閭??',
  `id_card` varchar(18) DEFAULT NULL COMMENT '韬?唤璇佸彿',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:姝ｅ父 1:鍋滅敤)',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`manager_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='椤圭洰璐熻矗浜鸿〃';

-- ----------------------------
-- Records of hz_project_manager
-- ----------------------------

-- ----------------------------
-- Table structure for hz_qualification
-- ----------------------------
DROP TABLE IF EXISTS `hz_qualification`;
CREATE TABLE `hz_qualification` (
  `qualification_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '璧勬牸ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '绉熸埛ID',
  `apply_type` char(1) NOT NULL COMMENT '鐢宠?绫诲瀷(1:浜烘墠鍏?瘬 2:淇濈?鎴?3:甯傚満绉熻祦)',
  `apply_time` datetime DEFAULT NULL COMMENT '鐢宠?鏃堕棿',
  `id_card` varchar(18) NOT NULL COMMENT '韬?唤璇佸彿',
  `name` varchar(50) NOT NULL COMMENT '濮撳悕',
  `phone` varchar(20) NOT NULL COMMENT '鎵嬫満鍙',
  `social_security_months` int(11) DEFAULT '0' COMMENT '绀句繚缂寸撼鏈堟暟',
  `has_local_house` char(1) DEFAULT '0' COMMENT '鏈?湴鏄?惁鏈夋埧(0:鏃?1:鏈?',
  `education_level` varchar(20) DEFAULT NULL COMMENT '瀛﹀巻',
  `marriage_status` varchar(20) DEFAULT NULL COMMENT '濠氬Щ鐘跺喌',
  `household_count` int(11) DEFAULT '1' COMMENT '瀹跺涵浜哄彛鏁',
  `work_unit` varchar(100) DEFAULT NULL COMMENT '宸ヤ綔鍗曚綅',
  `monthly_income` decimal(10,2) DEFAULT NULL COMMENT '鏈堟敹鍏?鍏?',
  `auto_check_result` char(1) DEFAULT '0' COMMENT '鑷?姩瀹℃牳缁撴灉(0:寰呭?鏍?1:閫氳繃 2:涓嶉?杩?',
  `auto_check_reason` varchar(500) DEFAULT NULL COMMENT '鑷?姩瀹℃牳鍘熷洜',
  `manual_check_result` char(1) DEFAULT NULL COMMENT '浜哄伐瀹℃牳缁撴灉(0:寰呭?鏍?1:閫氳繃 2:涓嶉?杩?',
  `manual_check_reason` varchar(500) DEFAULT NULL COMMENT '浜哄伐瀹℃牳鍘熷洜',
  `final_result` char(1) DEFAULT '0' COMMENT '鏈?粓缁撴灉(0:寰呭?鏍?1:閫氳繃 2:涓嶉?杩?',
  `check_by` varchar(64) DEFAULT NULL COMMENT '瀹℃牳浜',
  `check_time` datetime DEFAULT NULL COMMENT '瀹℃牳鏃堕棿',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:寰呭?鏍?1:瀹℃牳涓?2:宸插?鏍?',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`qualification_id`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_apply_type` (`apply_type`),
  KEY `idx_final_result` (`final_result`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='璧勬牸瀹℃牳琛';

-- ----------------------------
-- Records of hz_qualification
-- ----------------------------

-- ----------------------------
-- Table structure for hz_qualification_appeal
-- ----------------------------
DROP TABLE IF EXISTS `hz_qualification_appeal`;
CREATE TABLE `hz_qualification_appeal` (
  `appeal_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '鐢宠瘔ID',
  `qualification_id` bigint(20) NOT NULL COMMENT '璧勬牸ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '绉熸埛ID',
  `appeal_reason` text NOT NULL COMMENT '鐢宠瘔鐞嗙敱',
  `appeal_attachments` varchar(1000) DEFAULT NULL COMMENT '鐢宠瘔闄勪欢(澶氫釜鐢ㄩ?鍙峰垎闅?',
  `appeal_time` datetime DEFAULT NULL COMMENT '鐢宠瘔鏃堕棿',
  `handle_result` char(1) DEFAULT '0' COMMENT '澶勭悊缁撴灉(0:寰呭?鐞?1:閫氳繃 2:椹冲洖)',
  `handle_opinion` varchar(500) DEFAULT NULL COMMENT '澶勭悊鎰忚?',
  `handle_by` varchar(64) DEFAULT NULL COMMENT '澶勭悊浜',
  `handle_time` datetime DEFAULT NULL COMMENT '澶勭悊鏃堕棿',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:寰呭?鐞?1:宸插?鐞?',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`appeal_id`),
  KEY `idx_qualification_id` (`qualification_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='璧勬牸鐢宠瘔琛';

-- ----------------------------
-- Records of hz_qualification_appeal
-- ----------------------------

-- ----------------------------
-- Table structure for hz_refund_apply
-- ----------------------------
DROP TABLE IF EXISTS `hz_refund_apply`;
CREATE TABLE `hz_refund_apply` (
  `apply_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '鐢宠?ID',
  `apply_no` varchar(50) NOT NULL COMMENT '鐢宠?缂栧彿',
  `payment_id` bigint(20) NOT NULL COMMENT '缂磋垂ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '绉熸埛ID',
  `tenant_name` varchar(50) NOT NULL COMMENT '绉熸埛濮撳悕',
  `refund_amount` decimal(10,2) NOT NULL COMMENT '閫??閲戦?',
  `refund_reason` varchar(500) NOT NULL COMMENT '閫??鍘熷洜',
  `apply_time` datetime DEFAULT NULL COMMENT '鐢宠?鏃堕棿',
  `bank_name` varchar(100) DEFAULT NULL COMMENT '寮?埛琛',
  `bank_account` varchar(50) DEFAULT NULL COMMENT '閾惰?璐﹀彿',
  `account_name` varchar(50) DEFAULT NULL COMMENT '璐︽埛鍚',
  `approve_status` char(1) DEFAULT '0' COMMENT '瀹℃壒鐘舵?(0:寰呭?鎵?1:宸查?杩?2:宸查┏鍥?',
  `approve_by` varchar(64) DEFAULT NULL COMMENT '瀹℃壒浜',
  `approve_time` datetime DEFAULT NULL COMMENT '瀹℃壒鏃堕棿',
  `approve_opinion` varchar(500) DEFAULT NULL COMMENT '瀹℃壒鎰忚?',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`apply_id`),
  UNIQUE KEY `uk_apply_no` (`apply_no`),
  KEY `idx_payment_id` (`payment_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='閫??鐢宠?琛';

-- ----------------------------
-- Records of hz_refund_apply
-- ----------------------------

-- ----------------------------
-- Table structure for hz_refund_record
-- ----------------------------
DROP TABLE IF EXISTS `hz_refund_record`;
CREATE TABLE `hz_refund_record` (
  `record_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '璁板綍ID',
  `refund_no` varchar(50) NOT NULL COMMENT '閫??缂栧彿',
  `apply_id` bigint(20) NOT NULL COMMENT '鐢宠?ID',
  `payment_id` bigint(20) NOT NULL COMMENT '缂磋垂ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '绉熸埛ID',
  `refund_amount` decimal(10,2) NOT NULL COMMENT '閫??閲戦?',
  `refund_method` varchar(20) DEFAULT NULL COMMENT '閫??鏂瑰紡',
  `refund_account` varchar(100) DEFAULT NULL COMMENT '閫??璐︽埛',
  `transaction_no` varchar(100) DEFAULT NULL COMMENT '浜ゆ槗娴佹按鍙',
  `refund_time` datetime DEFAULT NULL COMMENT '閫??鏃堕棿',
  `refund_status` char(1) DEFAULT '0' COMMENT '閫??鐘舵?(0:澶勭悊涓?1:鎴愬姛 2:澶辫触)',
  `fail_reason` varchar(500) DEFAULT NULL COMMENT '澶辫触鍘熷洜',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`record_id`),
  UNIQUE KEY `uk_refund_no` (`refund_no`),
  KEY `idx_apply_id` (`apply_id`),
  KEY `idx_payment_id` (`payment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='閫??璁板綍琛';

-- ----------------------------
-- Records of hz_refund_record
-- ----------------------------

-- ----------------------------
-- Table structure for hz_shortcut
-- ----------------------------
DROP TABLE IF EXISTS `hz_shortcut`;
CREATE TABLE `hz_shortcut` (
  `shortcut_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '蹇?嵎鍏ュ彛ID',
  `shortcut_name` varchar(50) NOT NULL COMMENT '鍏ュ彛鍚嶇О',
  `shortcut_icon` varchar(255) NOT NULL COMMENT '鍏ュ彛鍥炬爣',
  `shortcut_url` varchar(255) NOT NULL COMMENT '璺宠浆閾炬帴',
  `shortcut_type` varchar(50) DEFAULT NULL COMMENT '鍏ュ彛绫诲瀷',
  `sort_order` int(11) DEFAULT '0' COMMENT '鏄剧ず椤哄簭',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:姝ｅ父 1:鍋滅敤)',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`shortcut_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='蹇?嵎鍏ュ彛琛';

-- ----------------------------
-- Records of hz_shortcut
-- ----------------------------

-- ----------------------------
-- Table structure for hz_tenant
-- ----------------------------
DROP TABLE IF EXISTS `hz_tenant`;
CREATE TABLE `hz_tenant` (
  `tenant_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '绉熸埛ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '鍏宠仈鐢ㄦ埛ID(閮戝ソ鍔炵敤鎴?',
  `tenant_name` varchar(50) NOT NULL COMMENT '绉熸埛濮撳悕',
  `id_card` varchar(18) NOT NULL COMMENT '韬?唤璇佸彿',
  `phone` varchar(20) NOT NULL COMMENT '鎵嬫満鍙',
  `gender` char(1) DEFAULT NULL COMMENT '鎬у埆(0:鐢?1:濂?',
  `birth_date` date DEFAULT NULL COMMENT '鍑虹敓鏃ユ湡',
  `education` varchar(20) DEFAULT NULL COMMENT '瀛﹀巻',
  `marriage_status` varchar(20) DEFAULT NULL COMMENT '濠氬Щ鐘跺喌',
  `work_unit` varchar(100) DEFAULT NULL COMMENT '宸ヤ綔鍗曚綅',
  `emergency_contact` varchar(50) DEFAULT NULL COMMENT '绱ф?鑱旂郴浜',
  `emergency_phone` varchar(20) DEFAULT NULL COMMENT '绱ф?鑱旂郴浜虹數璇',
  `household_register` varchar(100) DEFAULT NULL COMMENT '鎴风睄鍦板潃',
  `current_address` varchar(255) DEFAULT NULL COMMENT '鐜颁綇鍧',
  `email` varchar(100) DEFAULT NULL COMMENT '閭??',
  `wechat` varchar(50) DEFAULT NULL COMMENT '寰?俊鍙',
  `avatar` varchar(255) DEFAULT NULL COMMENT '澶村儚URL',
  `credit_score` int(11) DEFAULT '100' COMMENT '淇＄敤鍒?榛樿?100)',
  `is_blacklist` char(1) DEFAULT '0' COMMENT '鏄?惁榛戝悕鍗?0:鍚?1:鏄?',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:姝ｅ父 1:鍋滅敤)',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`tenant_id`),
  UNIQUE KEY `uk_id_card` (`id_card`),
  KEY `idx_phone` (`phone`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='绉熸埛琛';

-- ----------------------------
-- Records of hz_tenant
-- ----------------------------

-- ----------------------------
-- Table structure for hz_tenant_document
-- ----------------------------
DROP TABLE IF EXISTS `hz_tenant_document`;
CREATE TABLE `hz_tenant_document` (
  `document_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '璧勬枡ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '绉熸埛ID',
  `document_type` varchar(50) NOT NULL COMMENT '璧勬枡绫诲瀷(韬?唤璇?瀛﹀巻璇佹槑/绀句繚璇佹槑绛?',
  `document_name` varchar(100) NOT NULL COMMENT '璧勬枡鍚嶇О',
  `document_url` varchar(255) NOT NULL COMMENT '璧勬枡URL',
  `file_size` bigint(20) DEFAULT NULL COMMENT '鏂囦欢澶у皬(瀛楄妭)',
  `upload_time` datetime DEFAULT NULL COMMENT '涓婁紶鏃堕棿',
  `audit_status` char(1) DEFAULT '0' COMMENT '瀹℃牳鐘舵?(0:寰呭?鏍?1:宸查?杩?2:宸查┏鍥?',
  `audit_by` varchar(64) DEFAULT NULL COMMENT '瀹℃牳浜',
  `audit_time` datetime DEFAULT NULL COMMENT '瀹℃牳鏃堕棿',
  `audit_opinion` varchar(500) DEFAULT NULL COMMENT '瀹℃牳鎰忚?',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`document_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='绉熸埛璧勬枡琛';

-- ----------------------------
-- Records of hz_tenant_document
-- ----------------------------

-- ----------------------------
-- Table structure for hz_transaction
-- ----------------------------
DROP TABLE IF EXISTS `hz_transaction`;
CREATE TABLE `hz_transaction` (
  `transaction_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '娴佹按ID',
  `transaction_no` varchar(50) NOT NULL COMMENT '娴佹按鍙',
  `transaction_type` char(1) NOT NULL COMMENT '浜ゆ槗绫诲瀷(1:鏀跺叆 2:鏀?嚭)',
  `business_type` char(1) NOT NULL COMMENT '涓氬姟绫诲瀷(1:缂磋垂 2:閫??)',
  `business_id` bigint(20) NOT NULL COMMENT '涓氬姟ID(缂磋垂ID鎴栭?娆綢D)',
  `tenant_id` bigint(20) NOT NULL COMMENT '绉熸埛ID',
  `amount` decimal(10,2) NOT NULL COMMENT '浜ゆ槗閲戦?',
  `balance_before` decimal(10,2) DEFAULT '0.00' COMMENT '浜ゆ槗鍓嶄綑棰',
  `balance_after` decimal(10,2) DEFAULT '0.00' COMMENT '浜ゆ槗鍚庝綑棰',
  `transaction_time` datetime DEFAULT NULL COMMENT '浜ゆ槗鏃堕棿',
  `transaction_status` char(1) DEFAULT '1' COMMENT '浜ゆ槗鐘舵?(0:澶辫触 1:鎴愬姛)',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`transaction_id`),
  UNIQUE KEY `uk_transaction_no` (`transaction_no`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='浜ゆ槗娴佹按琛';

-- ----------------------------
-- Records of hz_transaction
-- ----------------------------

-- ----------------------------
-- Table structure for hz_unit
-- ----------------------------
DROP TABLE IF EXISTS `hz_unit`;
CREATE TABLE `hz_unit` (
  `unit_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '鍗曞厓ID',
  `building_id` bigint(20) NOT NULL COMMENT '妤兼爧ID',
  `unit_name` varchar(50) NOT NULL COMMENT '鍗曞厓鍚嶇О',
  `unit_code` varchar(50) NOT NULL COMMENT '鍗曞厓缂栫爜',
  `total_houses` int(11) DEFAULT '0' COMMENT '鎴挎簮鏁',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:姝ｅ父 1:鍋滅敤)',
  `sort_order` int(11) DEFAULT '0' COMMENT '鏄剧ず椤哄簭',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`unit_id`),
  UNIQUE KEY `uk_unit_code` (`unit_code`),
  KEY `idx_building_id` (`building_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鍗曞厓琛';

-- ----------------------------
-- Records of hz_unit
-- ----------------------------
INSERT INTO `hz_unit` VALUES ('1', '1', '1单元', '1-1-1', '10', '0', '1', '', '2025-11-18 19:51:41', '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('2', '1', '2单元', '1-1-2', '10', '0', '2', '', '2025-11-18 19:51:41', '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('3', '2', '1单元', '1-2-1', '10', '0', '1', '', '2025-11-18 19:51:41', '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('4', '2', '2单元', '1-2-2', '10', '0', '2', '', '2025-11-18 19:51:41', '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('5', '3', '1单元', '2-1-1', '12', '0', '1', '', '2025-11-18 19:51:41', '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('6', '3', '2单元', '2-1-2', '12', '0', '2', '', '2025-11-18 19:51:41', '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('7', '3', '3单元', '2-1-3', '12', '0', '3', '', '2025-11-18 19:51:41', '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('8', '4', '1单元', '2-2-1', '12', '0', '1', '', '2025-11-18 19:51:41', '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('9', '4', '2单元', '2-2-2', '12', '0', '2', '', '2025-11-18 19:51:41', '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('10', '4', '3单元', '2-2-3', '12', '0', '3', '', '2025-11-18 19:51:41', '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('11', '5', '啊啊', '10', '0', '0', '2', '', null, '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('12', '6', '单元1', '100', '0', '0', '0', '', null, '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('13', '7', '1单元', '1', '0', '0', '0', '', null, '', null, null, '0');

-- ----------------------------
-- Table structure for hz_viewing_record
-- ----------------------------
DROP TABLE IF EXISTS `hz_viewing_record`;
CREATE TABLE `hz_viewing_record` (
  `record_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '璁板綍ID',
  `appointment_id` bigint(20) NOT NULL COMMENT '棰勭害ID',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '绉熸埛ID',
  `visitor_name` varchar(50) NOT NULL COMMENT '鐪嬫埧浜哄?鍚',
  `project_id` bigint(20) NOT NULL COMMENT '椤圭洰ID',
  `house_id` bigint(20) NOT NULL COMMENT '鎴挎簮ID',
  `viewing_date` date DEFAULT NULL COMMENT '鐪嬫埧鏃ユ湡',
  `viewing_time` datetime DEFAULT NULL COMMENT '鐪嬫埧鏃堕棿',
  `guide_person` varchar(50) DEFAULT NULL COMMENT '甯︾湅浜',
  `viewing_duration` int(11) DEFAULT NULL COMMENT '鐪嬫埧鏃堕暱(鍒嗛挓)',
  `satisfaction_score` int(11) DEFAULT NULL COMMENT '婊℃剰搴﹁瘎鍒?1-5)',
  `is_interested` char(1) DEFAULT '0' COMMENT '鏄?惁鏈夋剰鍚?0:鍚?1:鏄?',
  `feedback` varchar(500) DEFAULT NULL COMMENT '鍙嶉?鎰忚?',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`record_id`),
  KEY `idx_appointment_id` (`appointment_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鐪嬫埧璁板綍琛';

-- ----------------------------
-- Records of hz_viewing_record
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_blob_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_blob_triggers`;
CREATE TABLE `qrtz_blob_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `blob_data` blob COMMENT '存放持久化Trigger对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Blob类型的触发器表';

-- ----------------------------
-- Records of qrtz_blob_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_calendars
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_calendars`;
CREATE TABLE `qrtz_calendars` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `calendar_name` varchar(200) NOT NULL COMMENT '日历名称',
  `calendar` blob NOT NULL COMMENT '存放持久化calendar对象',
  PRIMARY KEY (`sched_name`,`calendar_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='日历信息表';

-- ----------------------------
-- Records of qrtz_calendars
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_cron_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_cron_triggers`;
CREATE TABLE `qrtz_cron_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `cron_expression` varchar(200) NOT NULL COMMENT 'cron表达式',
  `time_zone_id` varchar(80) DEFAULT NULL COMMENT '时区',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Cron类型的触发器表';

-- ----------------------------
-- Records of qrtz_cron_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_fired_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_fired_triggers`;
CREATE TABLE `qrtz_fired_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `entry_id` varchar(95) NOT NULL COMMENT '调度器实例id',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `instance_name` varchar(200) NOT NULL COMMENT '调度器实例名',
  `fired_time` bigint(13) NOT NULL COMMENT '触发的时间',
  `sched_time` bigint(13) NOT NULL COMMENT '定时器制定的时间',
  `priority` int(11) NOT NULL COMMENT '优先级',
  `state` varchar(16) NOT NULL COMMENT '状态',
  `job_name` varchar(200) DEFAULT NULL COMMENT '任务名称',
  `job_group` varchar(200) DEFAULT NULL COMMENT '任务组名',
  `is_nonconcurrent` varchar(1) DEFAULT NULL COMMENT '是否并发',
  `requests_recovery` varchar(1) DEFAULT NULL COMMENT '是否接受恢复执行',
  PRIMARY KEY (`sched_name`,`entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='已触发的触发器表';

-- ----------------------------
-- Records of qrtz_fired_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_job_details
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_job_details`;
CREATE TABLE `qrtz_job_details` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `job_name` varchar(200) NOT NULL COMMENT '任务名称',
  `job_group` varchar(200) NOT NULL COMMENT '任务组名',
  `description` varchar(250) DEFAULT NULL COMMENT '相关介绍',
  `job_class_name` varchar(250) NOT NULL COMMENT '执行任务类名称',
  `is_durable` varchar(1) NOT NULL COMMENT '是否持久化',
  `is_nonconcurrent` varchar(1) NOT NULL COMMENT '是否并发',
  `is_update_data` varchar(1) NOT NULL COMMENT '是否更新数据',
  `requests_recovery` varchar(1) NOT NULL COMMENT '是否接受恢复执行',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`job_name`,`job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='任务详细信息表';

-- ----------------------------
-- Records of qrtz_job_details
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_locks
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_locks`;
CREATE TABLE `qrtz_locks` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `lock_name` varchar(40) NOT NULL COMMENT '悲观锁名称',
  PRIMARY KEY (`sched_name`,`lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='存储的悲观锁信息表';

-- ----------------------------
-- Records of qrtz_locks
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_paused_trigger_grps
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
CREATE TABLE `qrtz_paused_trigger_grps` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  PRIMARY KEY (`sched_name`,`trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='暂停的触发器表';

-- ----------------------------
-- Records of qrtz_paused_trigger_grps
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_scheduler_state
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_scheduler_state`;
CREATE TABLE `qrtz_scheduler_state` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `instance_name` varchar(200) NOT NULL COMMENT '实例名称',
  `last_checkin_time` bigint(13) NOT NULL COMMENT '上次检查时间',
  `checkin_interval` bigint(13) NOT NULL COMMENT '检查间隔时间',
  PRIMARY KEY (`sched_name`,`instance_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='调度器状态表';

-- ----------------------------
-- Records of qrtz_scheduler_state
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_simple_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simple_triggers`;
CREATE TABLE `qrtz_simple_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `repeat_count` bigint(7) NOT NULL COMMENT '重复的次数统计',
  `repeat_interval` bigint(12) NOT NULL COMMENT '重复的间隔时间',
  `times_triggered` bigint(10) NOT NULL COMMENT '已经触发的次数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='简单触发器的信息表';

-- ----------------------------
-- Records of qrtz_simple_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_simprop_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
CREATE TABLE `qrtz_simprop_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `str_prop_1` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第一个参数',
  `str_prop_2` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第二个参数',
  `str_prop_3` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第三个参数',
  `int_prop_1` int(11) DEFAULT NULL COMMENT 'int类型的trigger的第一个参数',
  `int_prop_2` int(11) DEFAULT NULL COMMENT 'int类型的trigger的第二个参数',
  `long_prop_1` bigint(20) DEFAULT NULL COMMENT 'long类型的trigger的第一个参数',
  `long_prop_2` bigint(20) DEFAULT NULL COMMENT 'long类型的trigger的第二个参数',
  `dec_prop_1` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第一个参数',
  `dec_prop_2` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第二个参数',
  `bool_prop_1` varchar(1) DEFAULT NULL COMMENT 'Boolean类型的trigger的第一个参数',
  `bool_prop_2` varchar(1) DEFAULT NULL COMMENT 'Boolean类型的trigger的第二个参数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='同步机制的行锁表';

-- ----------------------------
-- Records of qrtz_simprop_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_triggers`;
CREATE TABLE `qrtz_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT '触发器的名字',
  `trigger_group` varchar(200) NOT NULL COMMENT '触发器所属组的名字',
  `job_name` varchar(200) NOT NULL COMMENT 'qrtz_job_details表job_name的外键',
  `job_group` varchar(200) NOT NULL COMMENT 'qrtz_job_details表job_group的外键',
  `description` varchar(250) DEFAULT NULL COMMENT '相关介绍',
  `next_fire_time` bigint(13) DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
  `prev_fire_time` bigint(13) DEFAULT NULL COMMENT '下一次触发时间（默认为-1表示不触发）',
  `priority` int(11) DEFAULT NULL COMMENT '优先级',
  `trigger_state` varchar(16) NOT NULL COMMENT '触发器状态',
  `trigger_type` varchar(8) NOT NULL COMMENT '触发器的类型',
  `start_time` bigint(13) NOT NULL COMMENT '开始时间',
  `end_time` bigint(13) DEFAULT NULL COMMENT '结束时间',
  `calendar_name` varchar(200) DEFAULT NULL COMMENT '日程表名称',
  `misfire_instr` smallint(2) DEFAULT NULL COMMENT '补偿执行的策略',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  KEY `sched_name` (`sched_name`,`job_name`,`job_group`),
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `qrtz_job_details` (`sched_name`, `job_name`, `job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='触发器详细信息表';

-- ----------------------------
-- Records of qrtz_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `config_id` int(5) NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8 COMMENT='参数配置表';

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES ('1', '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', '2025-11-17 18:04:02', '', null, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO `sys_config` VALUES ('2', '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', '2025-11-17 18:04:02', '', null, '初始化密码 123456');
INSERT INTO `sys_config` VALUES ('3', '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-light', 'N', 'admin', '2025-11-17 18:04:02', 'admin', '2025-12-06 17:56:17', '深色主题theme-dark，浅色主题theme-light');
INSERT INTO `sys_config` VALUES ('4', '账号自助-验证码开关', 'sys.account.captchaEnabled', 'false', 'N', 'admin', '2025-11-17 18:04:02', 'admin', '2025-11-19 01:09:32', '是否开启验证码功能（true开启，false关闭）');
INSERT INTO `sys_config` VALUES ('5', '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false', 'Y', 'admin', '2025-11-17 18:04:02', '', null, '是否开启注册用户功能（true开启，false关闭）');
INSERT INTO `sys_config` VALUES ('6', '用户登录-黑名单列表', 'sys.login.blackIPList', '', 'Y', 'admin', '2025-11-17 18:04:02', '', null, '设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）');
INSERT INTO `sys_config` VALUES ('7', '用户管理-初始密码修改策略', 'sys.account.initPasswordModify', '1', 'Y', 'admin', '2025-11-17 18:04:02', '', null, '0：初始密码修改策略关闭，没有任何提示，1：提醒用户，如果未修改初始密码，则在登录时就会提醒修改密码对话框');
INSERT INTO `sys_config` VALUES ('8', '用户管理-账号密码更新周期', 'sys.account.passwordValidateDays', '0', 'Y', 'admin', '2025-11-17 18:04:02', '', null, '密码更新周期（填写数字，数据初始化值为0不限制，若修改必须为大于0小于365的正整数），如果超过这个周期登录系统时，则在登录时就会提醒修改密码对话框');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `dept_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父部门id',
  `ancestors` varchar(50) DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) DEFAULT '' COMMENT '部门名称',
  `order_num` int(4) DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(20) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `status` char(1) DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8 COMMENT='部门表';

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES ('100', '0', '0', '若依科技', '0', '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-11-17 18:04:01', '', null);
INSERT INTO `sys_dept` VALUES ('101', '100', '0,100', '深圳总公司', '1', '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-11-17 18:04:01', '', null);
INSERT INTO `sys_dept` VALUES ('102', '100', '0,100', '长沙分公司', '2', '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-11-17 18:04:01', '', null);
INSERT INTO `sys_dept` VALUES ('103', '101', '0,100,101', '研发部门', '1', '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-11-17 18:04:01', '', null);
INSERT INTO `sys_dept` VALUES ('104', '101', '0,100,101', '市场部门', '2', '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-11-17 18:04:01', '', null);
INSERT INTO `sys_dept` VALUES ('105', '101', '0,100,101', '测试部门', '3', '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-11-17 18:04:01', '', null);
INSERT INTO `sys_dept` VALUES ('106', '101', '0,100,101', '财务部门', '4', '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-11-17 18:04:01', '', null);
INSERT INTO `sys_dept` VALUES ('107', '101', '0,100,101', '运维部门', '5', '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-11-17 18:04:01', '', null);
INSERT INTO `sys_dept` VALUES ('108', '102', '0,100,102', '市场部门', '1', '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-11-17 18:04:01', '', null);
INSERT INTO `sys_dept` VALUES ('109', '102', '0,100,102', '财务部门', '2', '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-11-17 18:04:01', '', null);

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
  `dict_code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int(4) DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`)
) ENGINE=InnoDB AUTO_INCREMENT=1312 DEFAULT CHARSET=utf8 COMMENT='字典数据表';

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES ('1', '1', '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', '2025-11-17 18:04:02', '', null, '性别男');
INSERT INTO `sys_dict_data` VALUES ('2', '2', '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', '2025-11-17 18:04:02', '', null, '性别女');
INSERT INTO `sys_dict_data` VALUES ('3', '3', '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', '2025-11-17 18:04:02', '', null, '性别未知');
INSERT INTO `sys_dict_data` VALUES ('4', '1', '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', '2025-11-17 18:04:02', '', null, '显示菜单');
INSERT INTO `sys_dict_data` VALUES ('5', '2', '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', '2025-11-17 18:04:02', '', null, '隐藏菜单');
INSERT INTO `sys_dict_data` VALUES ('6', '1', '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', '2025-11-17 18:04:02', '', null, '正常状态');
INSERT INTO `sys_dict_data` VALUES ('7', '2', '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', '2025-11-17 18:04:02', '', null, '停用状态');
INSERT INTO `sys_dict_data` VALUES ('8', '1', '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', '2025-11-17 18:04:02', '', null, '正常状态');
INSERT INTO `sys_dict_data` VALUES ('9', '2', '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', '2025-11-17 18:04:02', '', null, '停用状态');
INSERT INTO `sys_dict_data` VALUES ('10', '1', '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', '2025-11-17 18:04:02', '', null, '默认分组');
INSERT INTO `sys_dict_data` VALUES ('11', '2', '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', '2025-11-17 18:04:02', '', null, '系统分组');
INSERT INTO `sys_dict_data` VALUES ('12', '1', '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', '2025-11-17 18:04:02', '', null, '系统默认是');
INSERT INTO `sys_dict_data` VALUES ('13', '2', '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', '2025-11-17 18:04:02', '', null, '系统默认否');
INSERT INTO `sys_dict_data` VALUES ('14', '1', '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', '2025-11-17 18:04:02', '', null, '通知');
INSERT INTO `sys_dict_data` VALUES ('15', '2', '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', '2025-11-17 18:04:02', '', null, '公告');
INSERT INTO `sys_dict_data` VALUES ('16', '1', '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', '2025-11-17 18:04:02', '', null, '正常状态');
INSERT INTO `sys_dict_data` VALUES ('17', '2', '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', '2025-11-17 18:04:02', '', null, '关闭状态');
INSERT INTO `sys_dict_data` VALUES ('18', '99', '其他', '0', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2025-11-17 18:04:02', '', null, '其他操作');
INSERT INTO `sys_dict_data` VALUES ('19', '1', '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2025-11-17 18:04:02', '', null, '新增操作');
INSERT INTO `sys_dict_data` VALUES ('20', '2', '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2025-11-17 18:04:02', '', null, '修改操作');
INSERT INTO `sys_dict_data` VALUES ('21', '3', '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2025-11-17 18:04:02', '', null, '删除操作');
INSERT INTO `sys_dict_data` VALUES ('22', '4', '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', '2025-11-17 18:04:02', '', null, '授权操作');
INSERT INTO `sys_dict_data` VALUES ('23', '5', '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2025-11-17 18:04:02', '', null, '导出操作');
INSERT INTO `sys_dict_data` VALUES ('24', '6', '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2025-11-17 18:04:02', '', null, '导入操作');
INSERT INTO `sys_dict_data` VALUES ('25', '7', '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2025-11-17 18:04:02', '', null, '强退操作');
INSERT INTO `sys_dict_data` VALUES ('26', '8', '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2025-11-17 18:04:02', '', null, '生成操作');
INSERT INTO `sys_dict_data` VALUES ('27', '9', '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2025-11-17 18:04:02', '', null, '清空操作');
INSERT INTO `sys_dict_data` VALUES ('28', '1', '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', '2025-11-17 18:04:02', '', null, '正常状态');
INSERT INTO `sys_dict_data` VALUES ('29', '2', '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', '2025-11-17 18:04:02', '', null, '停用状态');
INSERT INTO `sys_dict_data` VALUES ('1000', '1', '人才公寓', '1', 'hz_project_type', '', 'primary', 'N', '0', 'admin', '2025-11-17 20:20:45', '', null, '人才公寓');
INSERT INTO `sys_dict_data` VALUES ('1001', '2', '保租房', '2', 'hz_project_type', '', 'success', 'N', '0', 'admin', '2025-11-17 20:20:45', '', null, '保租房');
INSERT INTO `sys_dict_data` VALUES ('1002', '3', '市场租赁', '3', 'hz_project_type', '', 'info', 'N', '0', 'admin', '2025-11-17 20:20:45', '', null, '市场租赁');
INSERT INTO `sys_dict_data` VALUES ('1010', '1', '空置', '0', 'hz_house_status', '', 'success', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '空置可租');
INSERT INTO `sys_dict_data` VALUES ('1011', '2', '已预订', '1', 'hz_house_status', '', 'warning', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '已预订');
INSERT INTO `sys_dict_data` VALUES ('1012', '3', '已出租', '2', 'hz_house_status', '', 'danger', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '已出租');
INSERT INTO `sys_dict_data` VALUES ('1013', '4', '维修中', '3', 'hz_house_status', '', 'info', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '维修中');
INSERT INTO `sys_dict_data` VALUES ('1014', '5', '下架', '4', 'hz_house_status', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '下架');
INSERT INTO `sys_dict_data` VALUES ('1020', '1', '一室', '1', 'hz_house_type', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '一室');
INSERT INTO `sys_dict_data` VALUES ('1021', '2', '一室一厅', '2', 'hz_house_type', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '一室一厅');
INSERT INTO `sys_dict_data` VALUES ('1022', '3', '两室一厅', '3', 'hz_house_type', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '两室一厅');
INSERT INTO `sys_dict_data` VALUES ('1023', '4', '两室两厅', '4', 'hz_house_type', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '两室两厅');
INSERT INTO `sys_dict_data` VALUES ('1024', '5', '三室一厅', '5', 'hz_house_type', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '三室一厅');
INSERT INTO `sys_dict_data` VALUES ('1025', '6', '三室两厅', '6', 'hz_house_type', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '三室两厅');
INSERT INTO `sys_dict_data` VALUES ('1030', '1', '东', '1', 'hz_orientation', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '东');
INSERT INTO `sys_dict_data` VALUES ('1031', '2', '南', '2', 'hz_orientation', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '南');
INSERT INTO `sys_dict_data` VALUES ('1032', '3', '西', '3', 'hz_orientation', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '西');
INSERT INTO `sys_dict_data` VALUES ('1033', '4', '北', '4', 'hz_orientation', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '北');
INSERT INTO `sys_dict_data` VALUES ('1034', '5', '东南', '5', 'hz_orientation', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '东南');
INSERT INTO `sys_dict_data` VALUES ('1035', '6', '西南', '6', 'hz_orientation', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '西南');
INSERT INTO `sys_dict_data` VALUES ('1036', '7', '东北', '7', 'hz_orientation', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '东北');
INSERT INTO `sys_dict_data` VALUES ('1037', '8', '西北', '8', 'hz_orientation', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '西北');
INSERT INTO `sys_dict_data` VALUES ('1040', '1', '毛坯', '1', 'hz_decoration', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '毛坯');
INSERT INTO `sys_dict_data` VALUES ('1041', '2', '简装', '2', 'hz_decoration', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '简装');
INSERT INTO `sys_dict_data` VALUES ('1042', '3', '精装', '3', 'hz_decoration', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '精装');
INSERT INTO `sys_dict_data` VALUES ('1043', '4', '豪装', '4', 'hz_decoration', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '豪装');
INSERT INTO `sys_dict_data` VALUES ('1050', '1', '人才公寓', '1', 'hz_contract_type', '', 'primary', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '人才公寓合同');
INSERT INTO `sys_dict_data` VALUES ('1051', '2', '保租房', '2', 'hz_contract_type', '', 'success', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '保租房合同');
INSERT INTO `sys_dict_data` VALUES ('1052', '3', '市场租赁', '3', 'hz_contract_type', '', 'info', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '市场租赁合同');
INSERT INTO `sys_dict_data` VALUES ('1060', '1', '草稿', '0', 'hz_contract_status', '', 'info', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '草稿');
INSERT INTO `sys_dict_data` VALUES ('1061', '2', '待签署', '1', 'hz_contract_status', '', 'warning', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '待签署');
INSERT INTO `sys_dict_data` VALUES ('1062', '3', '已签署', '2', 'hz_contract_status', '', 'primary', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '已签署');
INSERT INTO `sys_dict_data` VALUES ('1063', '4', '履行中', '3', 'hz_contract_status', '', 'success', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '履行中');
INSERT INTO `sys_dict_data` VALUES ('1064', '5', '已到期', '4', 'hz_contract_status', '', 'info', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '已到期');
INSERT INTO `sys_dict_data` VALUES ('1065', '6', '已解约', '5', 'hz_contract_status', '', 'danger', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '已解约');
INSERT INTO `sys_dict_data` VALUES ('1070', '1', '月付', '1', 'hz_payment_cycle', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '月付');
INSERT INTO `sys_dict_data` VALUES ('1071', '2', '季付', '2', 'hz_payment_cycle', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '季付');
INSERT INTO `sys_dict_data` VALUES ('1072', '3', '半年付', '3', 'hz_payment_cycle', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '半年付');
INSERT INTO `sys_dict_data` VALUES ('1073', '4', '年付', '4', 'hz_payment_cycle', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '年付');
INSERT INTO `sys_dict_data` VALUES ('1080', '1', '待审核', '0', 'hz_qualification_result', '', 'info', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '待审核');
INSERT INTO `sys_dict_data` VALUES ('1081', '2', '通过', '1', 'hz_qualification_result', '', 'success', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '通过');
INSERT INTO `sys_dict_data` VALUES ('1082', '3', '不通过', '2', 'hz_qualification_result', '', 'danger', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '不通过');
INSERT INTO `sys_dict_data` VALUES ('1090', '1', '押金', '1', 'hz_bill_type', '', 'primary', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '押金');
INSERT INTO `sys_dict_data` VALUES ('1091', '2', '租金', '2', 'hz_bill_type', '', 'success', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '租金');
INSERT INTO `sys_dict_data` VALUES ('1092', '3', '水费', '3', 'hz_bill_type', '', 'info', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '水费');
INSERT INTO `sys_dict_data` VALUES ('1093', '4', '电费', '4', 'hz_bill_type', '', 'warning', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '电费');
INSERT INTO `sys_dict_data` VALUES ('1094', '5', '燃气费', '5', 'hz_bill_type', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '燃气费');
INSERT INTO `sys_dict_data` VALUES ('1095', '6', '物业费', '6', 'hz_bill_type', '', 'primary', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '物业费');
INSERT INTO `sys_dict_data` VALUES ('1096', '7', '其他', '7', 'hz_bill_type', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '其他');
INSERT INTO `sys_dict_data` VALUES ('1100', '1', '待支付', '0', 'hz_bill_status', '', 'warning', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '待支付');
INSERT INTO `sys_dict_data` VALUES ('1101', '2', '已支付', '1', 'hz_bill_status', '', 'success', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '已支付');
INSERT INTO `sys_dict_data` VALUES ('1102', '3', '部分支付', '2', 'hz_bill_status', '', 'primary', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '部分支付');
INSERT INTO `sys_dict_data` VALUES ('1103', '4', '已逾期', '3', 'hz_bill_status', '', 'danger', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '已逾期');
INSERT INTO `sys_dict_data` VALUES ('1104', '5', '已关闭', '4', 'hz_bill_status', '', 'info', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '已关闭');
INSERT INTO `sys_dict_data` VALUES ('1110', '1', '支付宝', '1', 'hz_payment_method', '', 'primary', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '支付宝');
INSERT INTO `sys_dict_data` VALUES ('1111', '2', '微信', '2', 'hz_payment_method', '', 'success', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '微信');
INSERT INTO `sys_dict_data` VALUES ('1112', '3', '银行卡', '3', 'hz_payment_method', '', 'info', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '银行卡');
INSERT INTO `sys_dict_data` VALUES ('1113', '4', '现金', '4', 'hz_payment_method', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '现金');
INSERT INTO `sys_dict_data` VALUES ('1114', '5', '港区支付', '5', 'hz_payment_method', '', 'warning', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '港区支付');
INSERT INTO `sys_dict_data` VALUES ('1120', '1', '待支付', '0', 'hz_payment_status', '', 'info', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '待支付');
INSERT INTO `sys_dict_data` VALUES ('1121', '2', '支付成功', '1', 'hz_payment_status', '', 'success', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '支付成功');
INSERT INTO `sys_dict_data` VALUES ('1122', '3', '支付失败', '2', 'hz_payment_status', '', 'danger', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '支付失败');
INSERT INTO `sys_dict_data` VALUES ('1123', '4', '已退款', '3', 'hz_payment_status', '', 'warning', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '已退款');
INSERT INTO `sys_dict_data` VALUES ('1130', '1', '增值税普通发票', '1', 'hz_invoice_type', '', 'primary', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '增值税普通发票');
INSERT INTO `sys_dict_data` VALUES ('1131', '2', '增值税专用发票', '2', 'hz_invoice_type', '', 'success', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '增值税专用发票');
INSERT INTO `sys_dict_data` VALUES ('1140', '1', '待审核', '0', 'hz_apply_status', '', 'info', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '待审核');
INSERT INTO `sys_dict_data` VALUES ('1141', '2', '已通过', '1', 'hz_apply_status', '', 'success', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '已通过');
INSERT INTO `sys_dict_data` VALUES ('1142', '3', '已驳回', '2', 'hz_apply_status', '', 'danger', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '已驳回');
INSERT INTO `sys_dict_data` VALUES ('1150', '1', '待确认', '0', 'hz_appointment_status', '', 'info', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '待确认');
INSERT INTO `sys_dict_data` VALUES ('1151', '2', '已确认', '1', 'hz_appointment_status', '', 'primary', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '已确认');
INSERT INTO `sys_dict_data` VALUES ('1152', '3', '已完成', '2', 'hz_appointment_status', '', 'success', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '已完成');
INSERT INTO `sys_dict_data` VALUES ('1153', '4', '已取消', '3', 'hz_appointment_status', '', 'danger', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '已取消');
INSERT INTO `sys_dict_data` VALUES ('1160', '1', '身份证', '1', 'hz_document_type', '', 'primary', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '身份证');
INSERT INTO `sys_dict_data` VALUES ('1161', '2', '学历证明', '2', 'hz_document_type', '', 'success', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '学历证明');
INSERT INTO `sys_dict_data` VALUES ('1162', '3', '社保证明', '3', 'hz_document_type', '', 'info', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '社保证明');
INSERT INTO `sys_dict_data` VALUES ('1163', '4', '收入证明', '4', 'hz_document_type', '', 'warning', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '收入证明');
INSERT INTO `sys_dict_data` VALUES ('1164', '5', '户口本', '5', 'hz_document_type', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '户口本');
INSERT INTO `sys_dict_data` VALUES ('1165', '6', '婚姻证明', '6', 'hz_document_type', '', 'primary', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '婚姻证明');
INSERT INTO `sys_dict_data` VALUES ('1166', '7', '其他', '7', 'hz_document_type', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '其他');
INSERT INTO `sys_dict_data` VALUES ('1170', '1', '待审核', '0', 'hz_audit_status', '', 'info', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '待审核');
INSERT INTO `sys_dict_data` VALUES ('1171', '2', '已通过', '1', 'hz_audit_status', '', 'success', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '已通过');
INSERT INTO `sys_dict_data` VALUES ('1172', '3', '已驳回', '2', 'hz_audit_status', '', 'danger', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '已驳回');
INSERT INTO `sys_dict_data` VALUES ('1180', '1', '系统通知', '1', 'hz_message_type', '', 'primary', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '系统通知');
INSERT INTO `sys_dict_data` VALUES ('1181', '2', '短信', '2', 'hz_message_type', '', 'success', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '短信');
INSERT INTO `sys_dict_data` VALUES ('1182', '3', '邮件', '3', 'hz_message_type', '', 'info', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '邮件');
INSERT INTO `sys_dict_data` VALUES ('1190', '1', '通知', '1', 'hz_announcement_type', '', 'primary', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '通知');
INSERT INTO `sys_dict_data` VALUES ('1191', '2', '公告', '2', 'hz_announcement_type', '', 'success', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '公告');
INSERT INTO `sys_dict_data` VALUES ('1192', '3', '活动', '3', 'hz_announcement_type', '', 'warning', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '活动');
INSERT INTO `sys_dict_data` VALUES ('1200', '1', '已退租', '0', 'hz_checkin_status', '', 'info', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '已退租');
INSERT INTO `sys_dict_data` VALUES ('1201', '2', '在住', '1', 'hz_checkin_status', '', 'success', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '在住');
INSERT INTO `sys_dict_data` VALUES ('1210', '1', '合同到期', '1', 'hz_checkout_reason', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:46', '', null, '合同到期');
INSERT INTO `sys_dict_data` VALUES ('1211', '2', '工作调动', '2', 'hz_checkout_reason', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '工作调动');
INSERT INTO `sys_dict_data` VALUES ('1212', '3', '购买住房', '3', 'hz_checkout_reason', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '购买住房');
INSERT INTO `sys_dict_data` VALUES ('1213', '4', '家庭原因', '4', 'hz_checkout_reason', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '家庭原因');
INSERT INTO `sys_dict_data` VALUES ('1214', '5', '房屋质量问题', '5', 'hz_checkout_reason', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '房屋质量问题');
INSERT INTO `sys_dict_data` VALUES ('1215', '6', '其他原因', '6', 'hz_checkout_reason', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '其他原因');
INSERT INTO `sys_dict_data` VALUES ('1220', '1', '完好', '1', 'hz_item_status', '', 'success', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '完好');
INSERT INTO `sys_dict_data` VALUES ('1221', '2', '损坏', '2', 'hz_item_status', '', 'warning', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '损坏');
INSERT INTO `sys_dict_data` VALUES ('1222', '3', '缺失', '3', 'hz_item_status', '', 'danger', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '缺失');
INSERT INTO `sys_dict_data` VALUES ('1230', '1', '轻微', '1', 'hz_damage_level', '', 'info', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '轻微');
INSERT INTO `sys_dict_data` VALUES ('1231', '2', '中度', '2', 'hz_damage_level', '', 'warning', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '中度');
INSERT INTO `sys_dict_data` VALUES ('1232', '3', '严重', '3', 'hz_damage_level', '', 'danger', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '严重');
INSERT INTO `sys_dict_data` VALUES ('1240', '1', '满减', '1', 'hz_coupon_type', '', 'primary', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '满减');
INSERT INTO `sys_dict_data` VALUES ('1241', '2', '折扣', '2', 'hz_coupon_type', '', 'success', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '折扣');
INSERT INTO `sys_dict_data` VALUES ('1242', '3', '抵扣', '3', 'hz_coupon_type', '', 'warning', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '抵扣');
INSERT INTO `sys_dict_data` VALUES ('1250', '1', '处理中', '0', 'hz_refund_status', '', 'info', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '处理中');
INSERT INTO `sys_dict_data` VALUES ('1251', '2', '成功', '1', 'hz_refund_status', '', 'success', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '成功');
INSERT INTO `sys_dict_data` VALUES ('1252', '3', '失败', '2', 'hz_refund_status', '', 'danger', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '失败');
INSERT INTO `sys_dict_data` VALUES ('1260', '1', '首页', '1', 'hz_banner_type', '', 'primary', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '首页');
INSERT INTO `sys_dict_data` VALUES ('1261', '2', '项目详情', '2', 'hz_banner_type', '', 'success', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '项目详情');
INSERT INTO `sys_dict_data` VALUES ('1270', '1', '初中及以下', '1', 'hz_education_level', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '初中及以下');
INSERT INTO `sys_dict_data` VALUES ('1271', '2', '高中/中专', '2', 'hz_education_level', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '高中/中专');
INSERT INTO `sys_dict_data` VALUES ('1272', '3', '大专', '3', 'hz_education_level', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '大专');
INSERT INTO `sys_dict_data` VALUES ('1273', '4', '本科', '4', 'hz_education_level', '', 'primary', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '本科');
INSERT INTO `sys_dict_data` VALUES ('1274', '5', '硕士', '5', 'hz_education_level', '', 'success', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '硕士');
INSERT INTO `sys_dict_data` VALUES ('1275', '6', '博士', '6', 'hz_education_level', '', 'warning', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '博士');
INSERT INTO `sys_dict_data` VALUES ('1280', '1', '未婚', '1', 'hz_marriage_status', '', 'primary', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '未婚');
INSERT INTO `sys_dict_data` VALUES ('1281', '2', '已婚', '2', 'hz_marriage_status', '', 'success', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '已婚');
INSERT INTO `sys_dict_data` VALUES ('1282', '3', '离异', '3', 'hz_marriage_status', '', 'info', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '离异');
INSERT INTO `sys_dict_data` VALUES ('1283', '4', '丧偶', '4', 'hz_marriage_status', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '丧偶');
INSERT INTO `sys_dict_data` VALUES ('1290', '1', '男', '0', 'hz_gender', '', 'primary', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '男性');
INSERT INTO `sys_dict_data` VALUES ('1291', '2', '女', '1', 'hz_gender', '', 'danger', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '女性');
INSERT INTO `sys_dict_data` VALUES ('1300', '1', '否', '0', 'hz_yes_no', '', 'default', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '否');
INSERT INTO `sys_dict_data` VALUES ('1301', '2', '是', '1', 'hz_yes_no', '', 'primary', 'N', '0', 'admin', '2025-11-17 20:20:47', '', null, '是');
INSERT INTO `sys_dict_data` VALUES ('1302', '1', '商业街', '商业街', 'project_facilities', null, null, 'N', '0', 'admin', '2026-01-12 00:24:49', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1303', '2', '停车场', '停车场', 'project_facilities', null, null, 'N', '0', 'admin', '2026-01-12 00:24:49', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1304', '3', '运动场', '运动场', 'project_facilities', null, null, 'N', '0', 'admin', '2026-01-12 00:24:49', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1305', '4', '游泳池', '游泳池', 'project_facilities', null, null, 'N', '0', 'admin', '2026-01-12 00:24:49', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1306', '5', '健身房', '健身房', 'project_facilities', null, null, 'N', '0', 'admin', '2026-01-12 00:24:49', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1307', '6', '儿童乐园', '儿童乐园', 'project_facilities', null, null, 'N', '0', 'admin', '2026-01-12 00:24:49', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1308', '7', '图书馆', '图书馆', 'project_facilities', null, null, 'N', '0', 'admin', '2026-01-12 00:24:49', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1309', '8', '超市', '超市', 'project_facilities', null, null, 'N', '0', 'admin', '2026-01-12 00:24:49', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1310', '9', '医疗室', '医疗室', 'project_facilities', null, null, 'N', '0', 'admin', '2026-01-12 00:24:49', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1311', '10', '快递柜', '快递柜', 'project_facilities', null, null, 'N', '0', 'admin', '2026-01-12 00:24:49', '', null, null);

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
  `dict_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`),
  UNIQUE KEY `dict_type` (`dict_type`)
) ENGINE=InnoDB AUTO_INCREMENT=132 DEFAULT CHARSET=utf8 COMMENT='字典类型表';

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES ('1', '用户性别', 'sys_user_sex', '0', 'admin', '2025-11-17 18:04:02', '', null, '用户性别列表');
INSERT INTO `sys_dict_type` VALUES ('2', '菜单状态', 'sys_show_hide', '0', 'admin', '2025-11-17 18:04:02', '', null, '菜单状态列表');
INSERT INTO `sys_dict_type` VALUES ('3', '系统开关', 'sys_normal_disable', '0', 'admin', '2025-11-17 18:04:02', '', null, '系统开关列表');
INSERT INTO `sys_dict_type` VALUES ('4', '任务状态', 'sys_job_status', '0', 'admin', '2025-11-17 18:04:02', '', null, '任务状态列表');
INSERT INTO `sys_dict_type` VALUES ('5', '任务分组', 'sys_job_group', '0', 'admin', '2025-11-17 18:04:02', '', null, '任务分组列表');
INSERT INTO `sys_dict_type` VALUES ('6', '系统是否', 'sys_yes_no', '0', 'admin', '2025-11-17 18:04:02', '', null, '系统是否列表');
INSERT INTO `sys_dict_type` VALUES ('7', '通知类型', 'sys_notice_type', '0', 'admin', '2025-11-17 18:04:02', '', null, '通知类型列表');
INSERT INTO `sys_dict_type` VALUES ('8', '通知状态', 'sys_notice_status', '0', 'admin', '2025-11-17 18:04:02', '', null, '通知状态列表');
INSERT INTO `sys_dict_type` VALUES ('9', '操作类型', 'sys_oper_type', '0', 'admin', '2025-11-17 18:04:02', '', null, '操作类型列表');
INSERT INTO `sys_dict_type` VALUES ('10', '系统状态', 'sys_common_status', '0', 'admin', '2025-11-17 18:04:02', '', null, '登录状态列表');
INSERT INTO `sys_dict_type` VALUES ('100', '租赁类型', 'hz_project_type', '0', 'admin', '2025-11-17 20:20:45', '', null, '港好住租赁类型');
INSERT INTO `sys_dict_type` VALUES ('101', '房源状态', 'hz_house_status', '0', 'admin', '2025-11-17 20:20:45', '', null, '房源状态');
INSERT INTO `sys_dict_type` VALUES ('102', '户型', 'hz_house_type', '0', 'admin', '2025-11-17 20:20:45', '', null, '房源户型');
INSERT INTO `sys_dict_type` VALUES ('103', '朝向', 'hz_orientation', '0', 'admin', '2025-11-17 20:20:45', '', null, '房源朝向');
INSERT INTO `sys_dict_type` VALUES ('104', '装修情况', 'hz_decoration', '0', 'admin', '2025-11-17 20:20:45', '', null, '房源装修情况');
INSERT INTO `sys_dict_type` VALUES ('105', '合同类型', 'hz_contract_type', '0', 'admin', '2025-11-17 20:20:45', '', null, '租赁合同类型');
INSERT INTO `sys_dict_type` VALUES ('106', '合同状态', 'hz_contract_status', '0', 'admin', '2025-11-17 20:20:45', '', null, '合同状态');
INSERT INTO `sys_dict_type` VALUES ('107', '缴费周期', 'hz_payment_cycle', '0', 'admin', '2025-11-17 20:20:45', '', null, '租金缴费周期');
INSERT INTO `sys_dict_type` VALUES ('108', '资格审核结果', 'hz_qualification_result', '0', 'admin', '2025-11-17 20:20:45', '', null, '资格审核结果');
INSERT INTO `sys_dict_type` VALUES ('109', '账单类型', 'hz_bill_type', '0', 'admin', '2025-11-17 20:20:45', '', null, '账单类型');
INSERT INTO `sys_dict_type` VALUES ('110', '账单状态', 'hz_bill_status', '0', 'admin', '2025-11-17 20:20:45', '', null, '账单状态');
INSERT INTO `sys_dict_type` VALUES ('111', '支付方式', 'hz_payment_method', '0', 'admin', '2025-11-17 20:20:45', '', null, '支付方式');
INSERT INTO `sys_dict_type` VALUES ('112', '支付状态', 'hz_payment_status', '0', 'admin', '2025-11-17 20:20:45', '', null, '支付状态');
INSERT INTO `sys_dict_type` VALUES ('113', '发票类型', 'hz_invoice_type', '0', 'admin', '2025-11-17 20:20:45', '', null, '发票类型');
INSERT INTO `sys_dict_type` VALUES ('114', '申请状态', 'hz_apply_status', '0', 'admin', '2025-11-17 20:20:45', '', null, '各类申请状态');
INSERT INTO `sys_dict_type` VALUES ('115', '预约状态', 'hz_appointment_status', '0', 'admin', '2025-11-17 20:20:45', '', null, '预约看房状态');
INSERT INTO `sys_dict_type` VALUES ('116', '资料类型', 'hz_document_type', '0', 'admin', '2025-11-17 20:20:45', '', null, '租户资料类型');
INSERT INTO `sys_dict_type` VALUES ('117', '审核状态', 'hz_audit_status', '0', 'admin', '2025-11-17 20:20:45', '', null, '审核状态');
INSERT INTO `sys_dict_type` VALUES ('118', '消息类型', 'hz_message_type', '0', 'admin', '2025-11-17 20:20:45', '', null, '消息类型');
INSERT INTO `sys_dict_type` VALUES ('119', '公告类型', 'hz_announcement_type', '0', 'admin', '2025-11-17 20:20:45', '', null, '公告类型');
INSERT INTO `sys_dict_type` VALUES ('120', '入住状态', 'hz_checkin_status', '0', 'admin', '2025-11-17 20:20:45', '', null, '入住状态');
INSERT INTO `sys_dict_type` VALUES ('121', '退租原因', 'hz_checkout_reason', '0', 'admin', '2025-11-17 20:20:45', '', null, '退租原因');
INSERT INTO `sys_dict_type` VALUES ('122', '物品状态', 'hz_item_status', '0', 'admin', '2025-11-17 20:20:45', '', null, '物品状态');
INSERT INTO `sys_dict_type` VALUES ('123', '损坏程度', 'hz_damage_level', '0', 'admin', '2025-11-17 20:20:45', '', null, '物品损坏程度');
INSERT INTO `sys_dict_type` VALUES ('124', '优惠券类型', 'hz_coupon_type', '0', 'admin', '2025-11-17 20:20:45', '', null, '优惠券类型');
INSERT INTO `sys_dict_type` VALUES ('125', '退款状态', 'hz_refund_status', '0', 'admin', '2025-11-17 20:20:45', '', null, '退款状态');
INSERT INTO `sys_dict_type` VALUES ('126', '轮播图类型', 'hz_banner_type', '0', 'admin', '2025-11-17 20:20:45', '', null, '轮播图类型');
INSERT INTO `sys_dict_type` VALUES ('127', '学历', 'hz_education_level', '0', 'admin', '2025-11-17 20:20:45', '', null, '学历');
INSERT INTO `sys_dict_type` VALUES ('128', '婚姻状况', 'hz_marriage_status', '0', 'admin', '2025-11-17 20:20:45', '', null, '婚姻状况');
INSERT INTO `sys_dict_type` VALUES ('129', '性别', 'hz_gender', '0', 'admin', '2025-11-17 20:20:45', '', null, '性别');
INSERT INTO `sys_dict_type` VALUES ('130', '是否', 'hz_yes_no', '0', 'admin', '2025-11-17 20:20:45', '', null, '是否');
INSERT INTO `sys_dict_type` VALUES ('131', '项目配套设施', 'project_facilities', '0', 'admin', '2026-01-12 00:24:28', '', null, '项目配套设施类型');

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job` (
  `job_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`,`job_name`,`job_group`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8 COMMENT='定时任务调度表';

-- ----------------------------
-- Records of sys_job
-- ----------------------------
INSERT INTO `sys_job` VALUES ('1', '系统默认（无参）', 'DEFAULT', 'ryTask.ryNoParams', '0/10 * * * * ?', '3', '1', '1', 'admin', '2025-11-17 18:04:02', '', null, '');
INSERT INTO `sys_job` VALUES ('2', '系统默认（有参）', 'DEFAULT', 'ryTask.ryParams(\'ry\')', '0/15 * * * * ?', '3', '1', '1', 'admin', '2025-11-17 18:04:02', '', null, '');
INSERT INTO `sys_job` VALUES ('3', '系统默认（多参）', 'DEFAULT', 'ryTask.ryMultipleParams(\'ry\', true, 2000L, 316.50D, 100)', '0/20 * * * * ?', '3', '1', '1', 'admin', '2025-11-17 18:04:02', '', null, '');

-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log` (
  `job_log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) DEFAULT NULL COMMENT '日志信息',
  `status` char(1) DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) DEFAULT '' COMMENT '异常信息',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='定时任务调度日志表';

-- ----------------------------
-- Records of sys_job_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor` (
  `info_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) DEFAULT '' COMMENT '操作系统',
  `status` char(1) DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) DEFAULT '' COMMENT '提示消息',
  `login_time` datetime DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`),
  KEY `idx_sys_logininfor_s` (`status`),
  KEY `idx_sys_logininfor_lt` (`login_time`)
) ENGINE=InnoDB AUTO_INCREMENT=137 DEFAULT CHARSET=utf8 COMMENT='系统访问记录';

-- ----------------------------
-- Records of sys_logininfor
-- ----------------------------
INSERT INTO `sys_logininfor` VALUES ('100', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-17 18:08:51');
INSERT INTO `sys_logininfor` VALUES ('101', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-17 19:05:51');
INSERT INTO `sys_logininfor` VALUES ('102', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-17 22:08:35');
INSERT INTO `sys_logininfor` VALUES ('103', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-17 23:30:49');
INSERT INTO `sys_logininfor` VALUES ('104', 'admin', '192.168.31.146', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-18 02:03:57');
INSERT INTO `sys_logininfor` VALUES ('105', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-18 16:58:50');
INSERT INTO `sys_logininfor` VALUES ('106', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-18 18:36:15');
INSERT INTO `sys_logininfor` VALUES ('107', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-18 19:57:38');
INSERT INTO `sys_logininfor` VALUES ('108', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-18 19:57:41');
INSERT INTO `sys_logininfor` VALUES ('109', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '验证码错误', '2025-11-18 21:21:47');
INSERT INTO `sys_logininfor` VALUES ('110', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-18 21:21:50');
INSERT INTO `sys_logininfor` VALUES ('111', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-18 23:04:18');
INSERT INTO `sys_logininfor` VALUES ('112', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-19 00:01:51');
INSERT INTO `sys_logininfor` VALUES ('113', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-19 01:08:37');
INSERT INTO `sys_logininfor` VALUES ('114', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-19 01:09:36');
INSERT INTO `sys_logininfor` VALUES ('115', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-19 01:09:48');
INSERT INTO `sys_logininfor` VALUES ('116', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-02 11:35:03');
INSERT INTO `sys_logininfor` VALUES ('117', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-06 14:27:27');
INSERT INTO `sys_logininfor` VALUES ('118', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-06 17:48:12');
INSERT INTO `sys_logininfor` VALUES ('119', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-06 17:56:41');
INSERT INTO `sys_logininfor` VALUES ('120', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-06 17:56:42');
INSERT INTO `sys_logininfor` VALUES ('121', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-06 18:46:45');
INSERT INTO `sys_logininfor` VALUES ('122', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-07 18:01:34');
INSERT INTO `sys_logininfor` VALUES ('123', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-08 01:16:13');
INSERT INTO `sys_logininfor` VALUES ('124', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-25 02:28:52');
INSERT INTO `sys_logininfor` VALUES ('125', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-06 10:57:45');
INSERT INTO `sys_logininfor` VALUES ('126', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-06 10:59:00');
INSERT INTO `sys_logininfor` VALUES ('127', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-06 11:02:29');
INSERT INTO `sys_logininfor` VALUES ('128', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-06 11:04:00');
INSERT INTO `sys_logininfor` VALUES ('129', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-06 17:10:43');
INSERT INTO `sys_logininfor` VALUES ('130', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-09 11:48:07');
INSERT INTO `sys_logininfor` VALUES ('131', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-11 00:42:02');
INSERT INTO `sys_logininfor` VALUES ('132', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-11 14:54:52');
INSERT INTO `sys_logininfor` VALUES ('133', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-11 19:44:52');
INSERT INTO `sys_logininfor` VALUES ('134', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-11 23:55:42');
INSERT INTO `sys_logininfor` VALUES ('135', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-12 01:36:28');
INSERT INTO `sys_logininfor` VALUES ('136', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-12 03:09:00');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父菜单ID',
  `order_num` int(4) DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(200) DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) DEFAULT '' COMMENT '路由名称',
  `is_frame` int(1) DEFAULT '1' COMMENT '是否为外链（0是 1否）',
  `is_cache` int(1) DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4041 DEFAULT CHARSET=utf8 COMMENT='菜单权限表';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('1', '系统管理', '0', '1', 'system', null, '', '', '1', '0', 'M', '0', '0', '', 'system', 'admin', '2025-11-17 18:04:01', '', null, '系统管理目录');
INSERT INTO `sys_menu` VALUES ('2', '系统监控', '0', '2', 'monitor', null, '', '', '1', '0', 'M', '0', '0', '', 'monitor', 'admin', '2025-11-17 18:04:01', '', null, '系统监控目录');
INSERT INTO `sys_menu` VALUES ('3', '系统工具', '0', '3', 'tool', null, '', '', '1', '0', 'M', '0', '0', '', 'tool', 'admin', '2025-11-17 18:04:01', '', null, '系统工具目录');
INSERT INTO `sys_menu` VALUES ('100', '用户管理', '1', '1', 'user', 'system/user/index', '', '', '1', '0', 'C', '0', '0', 'system:user:list', 'user', 'admin', '2025-11-17 18:04:01', '', null, '用户管理菜单');
INSERT INTO `sys_menu` VALUES ('101', '角色管理', '1', '2', 'role', 'system/role/index', '', '', '1', '0', 'C', '0', '0', 'system:role:list', 'peoples', 'admin', '2025-11-17 18:04:01', '', null, '角色管理菜单');
INSERT INTO `sys_menu` VALUES ('102', '菜单管理', '1', '3', 'menu', 'system/menu/index', '', '', '1', '0', 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', '2025-11-17 18:04:01', '', null, '菜单管理菜单');
INSERT INTO `sys_menu` VALUES ('103', '部门管理', '1', '4', 'dept', 'system/dept/index', '', '', '1', '0', 'C', '0', '0', 'system:dept:list', 'tree', 'admin', '2025-11-17 18:04:01', '', null, '部门管理菜单');
INSERT INTO `sys_menu` VALUES ('104', '岗位管理', '1', '5', 'post', 'system/post/index', '', '', '1', '0', 'C', '0', '0', 'system:post:list', 'post', 'admin', '2025-11-17 18:04:01', '', null, '岗位管理菜单');
INSERT INTO `sys_menu` VALUES ('105', '字典管理', '1', '6', 'dict', 'system/dict/index', '', '', '1', '0', 'C', '0', '0', 'system:dict:list', 'dict', 'admin', '2025-11-17 18:04:01', '', null, '字典管理菜单');
INSERT INTO `sys_menu` VALUES ('106', '参数设置', '1', '7', 'config', 'system/config/index', '', '', '1', '0', 'C', '0', '0', 'system:config:list', 'edit', 'admin', '2025-11-17 18:04:01', '', null, '参数设置菜单');
INSERT INTO `sys_menu` VALUES ('107', '通知公告', '1', '8', 'notice', 'system/notice/index', '', '', '1', '0', 'C', '0', '0', 'system:notice:list', 'message', 'admin', '2025-11-17 18:04:01', '', null, '通知公告菜单');
INSERT INTO `sys_menu` VALUES ('108', '日志管理', '1', '9', 'log', '', '', '', '1', '0', 'M', '0', '0', '', 'log', 'admin', '2025-11-17 18:04:01', '', null, '日志管理菜单');
INSERT INTO `sys_menu` VALUES ('109', '在线用户', '2', '1', 'online', 'monitor/online/index', '', '', '1', '0', 'C', '0', '0', 'monitor:online:list', 'online', 'admin', '2025-11-17 18:04:01', '', null, '在线用户菜单');
INSERT INTO `sys_menu` VALUES ('110', '定时任务', '2', '2', 'job', 'monitor/job/index', '', '', '1', '0', 'C', '0', '0', 'monitor:job:list', 'job', 'admin', '2025-11-17 18:04:01', '', null, '定时任务菜单');
INSERT INTO `sys_menu` VALUES ('111', '数据监控', '2', '3', 'druid', 'monitor/druid/index', '', '', '1', '0', 'C', '0', '0', 'monitor:druid:list', 'druid', 'admin', '2025-11-17 18:04:01', '', null, '数据监控菜单');
INSERT INTO `sys_menu` VALUES ('112', '服务监控', '2', '4', 'server', 'monitor/server/index', '', '', '1', '0', 'C', '0', '0', 'monitor:server:list', 'server', 'admin', '2025-11-17 18:04:01', '', null, '服务监控菜单');
INSERT INTO `sys_menu` VALUES ('113', '缓存监控', '2', '5', 'cache', 'monitor/cache/index', '', '', '1', '0', 'C', '0', '0', 'monitor:cache:list', 'redis', 'admin', '2025-11-17 18:04:01', '', null, '缓存监控菜单');
INSERT INTO `sys_menu` VALUES ('114', '缓存列表', '2', '6', 'cacheList', 'monitor/cache/list', '', '', '1', '0', 'C', '0', '0', 'monitor:cache:list', 'redis-list', 'admin', '2025-11-17 18:04:01', '', null, '缓存列表菜单');
INSERT INTO `sys_menu` VALUES ('115', '表单构建', '3', '1', 'build', 'tool/build/index', '', '', '1', '0', 'C', '0', '0', 'tool:build:list', 'build', 'admin', '2025-11-17 18:04:01', '', null, '表单构建菜单');
INSERT INTO `sys_menu` VALUES ('116', '代码生成', '3', '2', 'gen', 'tool/gen/index', '', '', '1', '0', 'C', '0', '0', 'tool:gen:list', 'code', 'admin', '2025-11-17 18:04:01', '', null, '代码生成菜单');
INSERT INTO `sys_menu` VALUES ('117', '系统接口', '3', '3', 'swagger', 'tool/swagger/index', '', '', '1', '0', 'C', '0', '0', 'tool:swagger:list', 'swagger', 'admin', '2025-11-17 18:04:01', '', null, '系统接口菜单');
INSERT INTO `sys_menu` VALUES ('500', '操作日志', '108', '1', 'operlog', 'monitor/operlog/index', '', '', '1', '0', 'C', '0', '0', 'monitor:operlog:list', 'form', 'admin', '2025-11-17 18:04:01', '', null, '操作日志菜单');
INSERT INTO `sys_menu` VALUES ('501', '登录日志', '108', '2', 'logininfor', 'monitor/logininfor/index', '', '', '1', '0', 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 'admin', '2025-11-17 18:04:01', '', null, '登录日志菜单');
INSERT INTO `sys_menu` VALUES ('1000', '用户查询', '100', '1', '', '', '', '', '1', '0', 'F', '0', '0', 'system:user:query', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1001', '用户新增', '100', '2', '', '', '', '', '1', '0', 'F', '0', '0', 'system:user:add', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1002', '用户修改', '100', '3', '', '', '', '', '1', '0', 'F', '0', '0', 'system:user:edit', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1003', '用户删除', '100', '4', '', '', '', '', '1', '0', 'F', '0', '0', 'system:user:remove', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1004', '用户导出', '100', '5', '', '', '', '', '1', '0', 'F', '0', '0', 'system:user:export', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1005', '用户导入', '100', '6', '', '', '', '', '1', '0', 'F', '0', '0', 'system:user:import', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1006', '重置密码', '100', '7', '', '', '', '', '1', '0', 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1007', '角色查询', '101', '1', '', '', '', '', '1', '0', 'F', '0', '0', 'system:role:query', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1008', '角色新增', '101', '2', '', '', '', '', '1', '0', 'F', '0', '0', 'system:role:add', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1009', '角色修改', '101', '3', '', '', '', '', '1', '0', 'F', '0', '0', 'system:role:edit', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1010', '角色删除', '101', '4', '', '', '', '', '1', '0', 'F', '0', '0', 'system:role:remove', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1011', '角色导出', '101', '5', '', '', '', '', '1', '0', 'F', '0', '0', 'system:role:export', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1012', '菜单查询', '102', '1', '', '', '', '', '1', '0', 'F', '0', '0', 'system:menu:query', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1013', '菜单新增', '102', '2', '', '', '', '', '1', '0', 'F', '0', '0', 'system:menu:add', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1014', '菜单修改', '102', '3', '', '', '', '', '1', '0', 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1015', '菜单删除', '102', '4', '', '', '', '', '1', '0', 'F', '0', '0', 'system:menu:remove', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1016', '部门查询', '103', '1', '', '', '', '', '1', '0', 'F', '0', '0', 'system:dept:query', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1017', '部门新增', '103', '2', '', '', '', '', '1', '0', 'F', '0', '0', 'system:dept:add', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1018', '部门修改', '103', '3', '', '', '', '', '1', '0', 'F', '0', '0', 'system:dept:edit', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1019', '部门删除', '103', '4', '', '', '', '', '1', '0', 'F', '0', '0', 'system:dept:remove', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1020', '岗位查询', '104', '1', '', '', '', '', '1', '0', 'F', '0', '0', 'system:post:query', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1021', '岗位新增', '104', '2', '', '', '', '', '1', '0', 'F', '0', '0', 'system:post:add', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1022', '岗位修改', '104', '3', '', '', '', '', '1', '0', 'F', '0', '0', 'system:post:edit', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1023', '岗位删除', '104', '4', '', '', '', '', '1', '0', 'F', '0', '0', 'system:post:remove', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1024', '岗位导出', '104', '5', '', '', '', '', '1', '0', 'F', '0', '0', 'system:post:export', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1025', '字典查询', '105', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:dict:query', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1026', '字典新增', '105', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:dict:add', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1027', '字典修改', '105', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:dict:edit', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1028', '字典删除', '105', '4', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:dict:remove', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1029', '字典导出', '105', '5', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:dict:export', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1030', '参数查询', '106', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:config:query', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1031', '参数新增', '106', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:config:add', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1032', '参数修改', '106', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:config:edit', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1033', '参数删除', '106', '4', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:config:remove', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1034', '参数导出', '106', '5', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:config:export', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1035', '公告查询', '107', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:notice:query', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1036', '公告新增', '107', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:notice:add', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1037', '公告修改', '107', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:notice:edit', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1038', '公告删除', '107', '4', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:notice:remove', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1039', '操作查询', '500', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:operlog:query', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1040', '操作删除', '500', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:operlog:remove', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1041', '日志导出', '500', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:operlog:export', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1042', '登录查询', '501', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:logininfor:query', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1043', '登录删除', '501', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:logininfor:remove', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1044', '日志导出', '501', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:logininfor:export', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1045', '账户解锁', '501', '4', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:logininfor:unlock', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1046', '在线查询', '109', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:online:query', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1047', '批量强退', '109', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:online:batchLogout', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1048', '单条强退', '109', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1049', '任务查询', '110', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:job:query', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1050', '任务新增', '110', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:job:add', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1051', '任务修改', '110', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:job:edit', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1052', '任务删除', '110', '4', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:job:remove', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1053', '状态修改', '110', '5', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:job:changeStatus', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1054', '任务导出', '110', '6', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:job:export', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1055', '生成查询', '116', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:query', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1056', '生成修改', '116', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:edit', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1057', '生成删除', '116', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:remove', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1058', '导入代码', '116', '4', '#', '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:import', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1059', '预览代码', '116', '5', '#', '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:preview', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('1060', '生成代码', '116', '6', '#', '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:code', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu` VALUES ('2000', '港好住管理', '0', '5', 'gangzhu', null, null, '', '1', '0', 'M', '0', '0', '', 'guide', 'admin', '2025-11-18 00:18:53', '', null, '港好住业务管理');
INSERT INTO `sys_menu` VALUES ('2001', '房源管理', '3001', '4', 'house', null, null, '', '1', '0', 'M', '0', '0', 'gangzhu:house:list', 'build', 'admin', '2025-11-18 00:18:53', '', null, '房源管理菜单');
INSERT INTO `sys_menu` VALUES ('2002', '房源查询', '4001', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:house:query', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu` VALUES ('2003', '房源新增', '4001', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:house:add', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu` VALUES ('2004', '房源修改', '4001', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:house:edit', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu` VALUES ('2005', '房源删除', '4001', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:house:remove', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu` VALUES ('2006', '房源导出', '4001', '5', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:house:export', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu` VALUES ('2007', '项目管理', '3001', '1', 'project', 'gangzhu/project/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:project:list', 'tree', 'admin', '2025-11-18 00:18:53', '', null, '项目管理菜单');
INSERT INTO `sys_menu` VALUES ('2008', '项目查询', '2007', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:project:query', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu` VALUES ('2009', '项目新增', '2007', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:project:add', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu` VALUES ('2010', '项目修改', '2007', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:project:edit', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu` VALUES ('2011', '项目删除', '2007', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:project:remove', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu` VALUES ('2012', '租户管理', '3002', '1', 'tenant', 'gangzhu/tenant/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:tenant:list', 'peoples', 'admin', '2025-11-18 00:18:53', '', null, '租户管理菜单');
INSERT INTO `sys_menu` VALUES ('2013', '租户查询', '2012', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:tenant:query', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu` VALUES ('2014', '租户详情', '2012', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:tenant:detail', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu` VALUES ('2015', '租户修改', '2012', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:tenant:edit', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu` VALUES ('2016', '租户导出', '2012', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:tenant:export', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu` VALUES ('2017', '资格申诉', '3002', '3', 'qualification', 'gangzhu/qualification/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:qualification:list', 'form', 'admin', '2025-11-18 00:18:53', '', null, '资格审核菜单');
INSERT INTO `sys_menu` VALUES ('2018', '审核查询', '2017', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:qualification:query', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu` VALUES ('2019', '审核通过', '2017', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:qualification:approve', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu` VALUES ('2020', '审核拒绝', '2017', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:qualification:reject', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu` VALUES ('2021', '审核导出', '2017', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:qualification:export', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu` VALUES ('2022', '合同管理', '2000', '4', 'contract', null, null, '', '1', '0', 'M', '0', '0', '', 'documentation', 'admin', '2025-11-18 00:18:53', '', null, '合同管理菜单');
INSERT INTO `sys_menu` VALUES ('2023', '合同查询', '2022', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:contract:query', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu` VALUES ('2024', '合同详情', '2022', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:contract:detail', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu` VALUES ('2025', '合同审核', '2022', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:contract:audit', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu` VALUES ('2026', '合同导出', '2022', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:contract:export', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu` VALUES ('2028', '账单查询', '2027', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:bill:query', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu` VALUES ('2029', '账单新增', '2027', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:bill:add', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu` VALUES ('2030', '账单修改', '2027', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:bill:edit', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2031', '账单导出', '2027', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:bill:export', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2032', '入住管理', '2022', '3', 'checkin', 'gangzhu/checkin/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:checkin:list', 'log', 'admin', '2025-11-18 00:18:54', '', null, '入住管理菜单');
INSERT INTO `sys_menu` VALUES ('2033', '入住查询', '2032', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:checkin:query', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2034', '入住审核', '2032', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:checkin:audit', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2035', '入住导出', '2032', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:checkin:export', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2036', '退租管理', '2022', '3', 'checkout', 'gangzhu/checkout/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:checkout:list', 'exit', 'admin', '2025-11-18 00:18:54', '', null, '退租管理菜单');
INSERT INTO `sys_menu` VALUES ('2037', '退租查询', '2036', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:checkout:query', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2038', '退租审核', '2036', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:checkout:audit', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2039', '退租导出', '2036', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:checkout:export', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2040', '预约看房', '3002', '2', 'appointment', 'gangzhu/appointment/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:appointment:list', 'date', 'admin', '2025-11-18 00:18:54', '', null, '预约管理菜单');
INSERT INTO `sys_menu` VALUES ('2041', '预约查询', '2040', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:appointment:query', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2042', '预约确认', '2040', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:appointment:confirm', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2043', '预约取消', '2040', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:appointment:cancel', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2044', '预约导出', '2040', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:appointment:export', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2045', '承诺书', '3002', '4', 'document', 'gangzhu/document/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:document:list', 'skill', 'admin', '2025-11-18 00:18:54', '', null, '资料审核菜单');
INSERT INTO `sys_menu` VALUES ('2046', '资料查询', '2045', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:document:query', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2047', '资料审核', '2045', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:document:audit', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2048', '资料导出', '2045', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:document:export', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2049', '消息通知', '3004', '1', 'message', 'gangzhu/message/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:message:list', 'email', 'admin', '2025-11-18 00:18:54', '', null, '消息管理菜单');
INSERT INTO `sys_menu` VALUES ('2050', '消息查询', '2049', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:message:query', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2051', '消息发送', '2049', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:message:send', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2052', '消息删除', '2049', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:message:remove', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2053', '通知公告', '3004', '4', 'announcement', 'gangzhu/announcement/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:announcement:list', 'message', 'admin', '2025-11-18 00:18:54', '', null, '公告管理菜单');
INSERT INTO `sys_menu` VALUES ('2054', '公告查询', '2053', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:announcement:query', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2055', '公告新增', '2053', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:announcement:add', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2056', '公告修改', '2053', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:announcement:edit', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2057', '公告删除', '2053', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:announcement:remove', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2058', '开票管理', '2000', '9', 'invoice', 'gangzhu/invoice/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:invoice:list', 'post', 'admin', '2025-11-18 00:18:54', '', null, '开票管理菜单');
INSERT INTO `sys_menu` VALUES ('2059', '开票查询', '2058', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:invoice:query', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2060', '开票审核', '2058', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:invoice:audit', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2061', '开票导出', '2058', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:invoice:export', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('3000', '首页', '2000', '1', 'dashboard', 'gangzhu/dashboard/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:dashboard:view', 'dashboard', 'admin', '2025-11-18 20:22:04', '', null, '首页数据统计');
INSERT INTO `sys_menu` VALUES ('3001', '资产管理', '2000', '2', 'asset', null, null, '', '1', '0', 'M', '0', '0', null, 'example', 'admin', '2025-11-18 20:22:04', '', null, '资产管理菜单');
INSERT INTO `sys_menu` VALUES ('3002', '申请信息', '2000', '3', 'application', null, null, '', '1', '0', 'M', '0', '0', null, 'form', 'admin', '2025-11-18 20:22:04', '', null, '申请信息菜单');
INSERT INTO `sys_menu` VALUES ('3003', '报表管理', '2000', '5', 'report', null, null, '', '1', '0', 'M', '0', '0', null, 'chart', 'admin', '2025-11-18 20:22:04', '', null, '报表管理菜单');
INSERT INTO `sys_menu` VALUES ('3004', '配置管理', '2000', '6', 'config', null, null, '', '1', '0', 'M', '0', '0', null, 'tool', 'admin', '2025-11-18 20:22:04', '', null, '配置管理菜单');
INSERT INTO `sys_menu` VALUES ('3010', '合同管理核心', '2022', '1', 'list', 'gangzhu/contract/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:contract:list', '#', 'admin', '2025-11-18 20:23:34', '', null, '合同列表');
INSERT INTO `sys_menu` VALUES ('3020', '项目收款台账', '3003', '1', 'receipt', 'gangzhu/report/receipt/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:report:receipt', '#', 'admin', '2025-11-18 20:23:34', '', null, '项目收款台账');
INSERT INTO `sys_menu` VALUES ('3021', '自定义报表', '3003', '2', 'custom', 'gangzhu/report/custom/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:report:custom', '#', 'admin', '2025-11-18 20:23:34', '', null, '自定义报表');
INSERT INTO `sys_menu` VALUES ('3030', '合同模版', '3004', '2', 'template', 'gangzhu/config/template/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:config:template', '#', 'admin', '2025-11-18 20:23:34', '', null, '合同模版');
INSERT INTO `sys_menu` VALUES ('3031', '运营配置', '3004', '3', 'operation', 'gangzhu/config/operation/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:config:operation', '#', 'admin', '2025-11-18 20:23:34', '', null, '运营配置');
INSERT INTO `sys_menu` VALUES ('3032', '政策文件', '3004', '5', 'policy', 'gangzhu/config/policy/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:config:policy', '#', 'admin', '2025-11-18 20:23:34', '', null, '政策文件');
INSERT INTO `sys_menu` VALUES ('3040', '黑名单管理', '2000', '7', 'blacklist', 'gangzhu/blacklist/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:blacklist:list', 'user', 'admin', '2025-11-18 20:23:34', '', null, '黑名单管理');
INSERT INTO `sys_menu` VALUES ('3041', '用户管理', '2000', '8', 'user', 'gangzhu/user/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:user:list', 'peoples', 'admin', '2025-11-18 20:23:34', '', null, '前端用户管理');
INSERT INTO `sys_menu` VALUES ('4001', '房源列表', '2001', '1', 'list', 'gangzhu/house/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:house:list', 'cascader', 'admin', '2025-11-18 20:46:23', 'admin', '2025-11-18 20:59:08', '房源列表管理');
INSERT INTO `sys_menu` VALUES ('4002', '配租批次', '2001', '2', 'batch', 'gangzhu/house/batch/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:batch:list', 'edit', 'admin', '2025-11-18 20:46:36', 'admin', '2025-11-18 20:59:18', '配租批次管理');
INSERT INTO `sys_menu` VALUES ('4003', '企业客户', '2001', '3', 'enterprise', 'gangzhu/house/enterprise/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:enterprise:list', 'redis-list', 'admin', '2025-11-18 20:46:48', 'admin', '2025-11-18 20:59:26', '企业客户管理');
INSERT INTO `sys_menu` VALUES ('4010', '批次查询', '4002', '1', '#', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:batch:query', '#', 'admin', '2025-11-18 20:46:36', '', null, '');
INSERT INTO `sys_menu` VALUES ('4011', '批次新增', '4002', '2', '#', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:batch:add', '#', 'admin', '2025-11-18 20:46:36', '', null, '');
INSERT INTO `sys_menu` VALUES ('4012', '批次编辑', '4002', '3', '#', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:batch:edit', '#', 'admin', '2025-11-18 20:46:36', '', null, '');
INSERT INTO `sys_menu` VALUES ('4013', '批次作废', '4002', '4', '#', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:batch:cancel', '#', 'admin', '2025-11-18 20:46:36', '', null, '');
INSERT INTO `sys_menu` VALUES ('4014', '批次审批', '4002', '5', '#', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:batch:approve', '#', 'admin', '2025-11-18 20:46:36', '', null, '');
INSERT INTO `sys_menu` VALUES ('4015', '批次导出', '4002', '6', '#', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:batch:export', '#', 'admin', '2025-11-18 20:46:36', '', null, '');
INSERT INTO `sys_menu` VALUES ('4020', '企业查询', '4003', '1', '#', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:enterprise:query', '#', 'admin', '2025-11-18 20:46:48', '', null, '');
INSERT INTO `sys_menu` VALUES ('4021', '企业新增', '4003', '2', '#', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:enterprise:add', '#', 'admin', '2025-11-18 20:46:48', '', null, '');
INSERT INTO `sys_menu` VALUES ('4022', '企业编辑', '4003', '3', '#', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:enterprise:edit', '#', 'admin', '2025-11-18 20:46:48', '', null, '');
INSERT INTO `sys_menu` VALUES ('4023', '企业作废', '4003', '4', '#', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:enterprise:cancel', '#', 'admin', '2025-11-18 20:46:48', '', null, '');
INSERT INTO `sys_menu` VALUES ('4024', '企业审批', '4003', '5', '#', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:enterprise:approve', '#', 'admin', '2025-11-18 20:46:48', '', null, '');
INSERT INTO `sys_menu` VALUES ('4025', '企业导出', '4003', '6', '#', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:enterprise:export', '#', 'admin', '2025-11-18 20:46:48', '', null, '');
INSERT INTO `sys_menu` VALUES ('4026', '退款管理', '2022', '4', 'refund', 'gangzhu/refund/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:refund:list', 'money', 'admin', '2025-12-06 18:42:29', '', null, '退款管理菜单');
INSERT INTO `sys_menu` VALUES ('4027', '退款查询', '4026', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:refund:query', '#', 'admin', '2025-12-06 18:42:41', '', null, '');
INSERT INTO `sys_menu` VALUES ('4028', '退款审核', '4026', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:refund:audit', '#', 'admin', '2025-12-06 18:42:41', '', null, '');
INSERT INTO `sys_menu` VALUES ('4029', '退款导出', '4026', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:refund:export', '#', 'admin', '2025-12-06 18:42:41', '', null, '');
INSERT INTO `sys_menu` VALUES ('4030', '合住人管理', '2022', '5', 'cohabitant', 'gangzhu/cohabitant/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:cohabitant:list', 'peoples', 'admin', '2025-12-06 18:42:54', '', null, '合住人管理菜单');
INSERT INTO `sys_menu` VALUES ('4031', '合住人查询', '4030', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:cohabitant:query', '#', 'admin', '2025-12-06 18:43:02', '', null, '');
INSERT INTO `sys_menu` VALUES ('4032', '合住人审核', '4030', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:cohabitant:audit', '#', 'admin', '2025-12-06 18:43:02', '', null, '');
INSERT INTO `sys_menu` VALUES ('4033', '合住人导出', '4030', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:cohabitant:export', '#', 'admin', '2025-12-06 18:43:02', '', null, '');
INSERT INTO `sys_menu` VALUES ('4034', '调换房管理', '2022', '6', 'exchange', 'gangzhu/exchange/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:exchange:list', 'list', 'admin', '2025-12-06 18:43:09', '', null, '调换房管理菜单');
INSERT INTO `sys_menu` VALUES ('4035', '换房查询', '4034', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:exchange:query', '#', 'admin', '2025-12-06 18:43:21', '', null, '');
INSERT INTO `sys_menu` VALUES ('4036', '换房审核', '4034', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:exchange:audit', '#', 'admin', '2025-12-06 18:43:21', '', null, '');
INSERT INTO `sys_menu` VALUES ('4037', '换房导出', '4034', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:exchange:export', '#', 'admin', '2025-12-06 18:43:21', '', null, '');
INSERT INTO `sys_menu` VALUES ('4038', '租金账单', '2022', '7', 'bill', 'gangzhu/bill/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:bill:list', 'example', 'admin', '2025-12-06 18:43:30', '', null, '租金账单菜单');
INSERT INTO `sys_menu` VALUES ('4039', '账单查询', '4038', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:bill:query', '#', 'admin', '2025-12-06 18:43:37', '', null, '');
INSERT INTO `sys_menu` VALUES ('4040', '账单导出', '4038', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:bill:export', '#', 'admin', '2025-12-06 18:43:37', '', null, '');

-- ----------------------------
-- Table structure for sys_menu_backup_20251118_202128
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu_backup_20251118_202128`;
CREATE TABLE `sys_menu_backup_20251118_202128` (
  `menu_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父菜单ID',
  `order_num` int(4) DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(200) DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) DEFAULT '' COMMENT '路由名称',
  `is_frame` int(1) DEFAULT '1' COMMENT '是否为外链（0是 1否）',
  `is_cache` int(1) DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_menu_backup_20251118_202128
-- ----------------------------
INSERT INTO `sys_menu_backup_20251118_202128` VALUES ('2000', '港好住管理', '0', '5', 'gangzhu', null, null, '', '1', '0', 'M', '0', '0', '', 'guide', 'admin', '2025-11-18 00:18:53', '', null, '港好住业务管理');
INSERT INTO `sys_menu_backup_20251118_202128` VALUES ('2001', '房源管理', '2000', '4', 'house', 'gangzhu/house/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:house:list', 'build', 'admin', '2025-11-18 00:18:53', '', null, '房源管理菜单');
INSERT INTO `sys_menu_backup_20251118_202128` VALUES ('2007', '项目管理', '2000', '1', 'project', 'gangzhu/project/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:project:list', 'tree', 'admin', '2025-11-18 00:18:53', '', null, '项目管理菜单');
INSERT INTO `sys_menu_backup_20251118_202128` VALUES ('2012', '租户管理', '2000', '5', 'tenant', 'gangzhu/tenant/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:tenant:list', 'peoples', 'admin', '2025-11-18 00:18:53', '', null, '租户管理菜单');
INSERT INTO `sys_menu_backup_20251118_202128` VALUES ('2017', '资格审核', '2000', '6', 'qualification', 'gangzhu/qualification/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:qualification:list', 'form', 'admin', '2025-11-18 00:18:53', '', null, '资格审核菜单');
INSERT INTO `sys_menu_backup_20251118_202128` VALUES ('2022', '合同管理', '2000', '9', 'contract', 'gangzhu/contract/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:contract:list', 'documentation', 'admin', '2025-11-18 00:18:53', '', null, '合同管理菜单');
INSERT INTO `sys_menu_backup_20251118_202128` VALUES ('2027', '账单管理', '2000', '12', 'bill', 'gangzhu/bill/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:bill:list', 'money', 'admin', '2025-11-18 00:18:53', '', null, '账单管理菜单');
INSERT INTO `sys_menu_backup_20251118_202128` VALUES ('2032', '入住管理', '2000', '10', 'checkin', 'gangzhu/checkin/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:checkin:list', 'log', 'admin', '2025-11-18 00:18:54', '', null, '入住管理菜单');
INSERT INTO `sys_menu_backup_20251118_202128` VALUES ('2036', '退租管理', '2000', '11', 'checkout', 'gangzhu/checkout/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:checkout:list', 'exit', 'admin', '2025-11-18 00:18:54', '', null, '退租管理菜单');
INSERT INTO `sys_menu_backup_20251118_202128` VALUES ('2040', '预约管理', '2000', '7', 'appointment', 'gangzhu/appointment/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:appointment:list', 'date', 'admin', '2025-11-18 00:18:54', '', null, '预约管理菜单');
INSERT INTO `sys_menu_backup_20251118_202128` VALUES ('2045', '资料审核', '2000', '8', 'document', 'gangzhu/document/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:document:list', 'skill', 'admin', '2025-11-18 00:18:54', '', null, '资料审核菜单');
INSERT INTO `sys_menu_backup_20251118_202128` VALUES ('2049', '消息管理', '2000', '14', 'message', 'gangzhu/message/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:message:list', 'email', 'admin', '2025-11-18 00:18:54', '', null, '消息管理菜单');
INSERT INTO `sys_menu_backup_20251118_202128` VALUES ('2053', '公告管理', '2000', '15', 'announcement', 'gangzhu/announcement/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:announcement:list', 'message', 'admin', '2025-11-18 00:18:54', '', null, '公告管理菜单');
INSERT INTO `sys_menu_backup_20251118_202128` VALUES ('2058', '开票管理', '2000', '13', 'invoice', 'gangzhu/invoice/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:invoice:list', 'post', 'admin', '2025-11-18 00:18:54', '', null, '开票管理菜单');
INSERT INTO `sys_menu_backup_20251118_202128` VALUES ('2062', '楼栋管理', '2000', '2', 'building', 'gangzhu/building/index', '', '', '1', '0', 'C', '0', '0', 'gangzhu:building:list', 'build', 'admin', '2025-11-18 19:59:10', '', null, '楼栋管理菜单');
INSERT INTO `sys_menu_backup_20251118_202128` VALUES ('2068', '单元管理', '2000', '3', 'unit', 'gangzhu/unit/index', '', '', '1', '0', 'C', '0', '0', 'gangzhu:unit:list', 'component', 'admin', '2025-11-18 19:59:10', '', null, '单元管理菜单');

-- ----------------------------
-- Table structure for sys_menu_backup_20251206
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu_backup_20251206`;
CREATE TABLE `sys_menu_backup_20251206` (
  `menu_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父菜单ID',
  `order_num` int(4) DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(200) DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) DEFAULT '' COMMENT '路由名称',
  `is_frame` int(1) DEFAULT '1' COMMENT '是否为外链（0是 1否）',
  `is_cache` int(1) DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_menu_backup_20251206
-- ----------------------------
INSERT INTO `sys_menu_backup_20251206` VALUES ('1', '系统管理', '0', '1', 'system', null, '', '', '1', '0', 'M', '0', '0', '', 'system', 'admin', '2025-11-17 18:04:01', '', null, '系统管理目录');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2', '系统监控', '0', '2', 'monitor', null, '', '', '1', '0', 'M', '0', '0', '', 'monitor', 'admin', '2025-11-17 18:04:01', '', null, '系统监控目录');
INSERT INTO `sys_menu_backup_20251206` VALUES ('3', '系统工具', '0', '3', 'tool', null, '', '', '1', '0', 'M', '0', '0', '', 'tool', 'admin', '2025-11-17 18:04:01', '', null, '系统工具目录');
INSERT INTO `sys_menu_backup_20251206` VALUES ('4', '若依官网', '0', '10', 'http://ruoyi.vip', null, '', '', '0', '0', 'M', '0', '0', '', 'guide', 'admin', '2025-11-17 18:04:01', 'admin', '2025-11-18 18:40:51', '若依官网地址');
INSERT INTO `sys_menu_backup_20251206` VALUES ('100', '用户管理', '1', '1', 'user', 'system/user/index', '', '', '1', '0', 'C', '0', '0', 'system:user:list', 'user', 'admin', '2025-11-17 18:04:01', '', null, '用户管理菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('101', '角色管理', '1', '2', 'role', 'system/role/index', '', '', '1', '0', 'C', '0', '0', 'system:role:list', 'peoples', 'admin', '2025-11-17 18:04:01', '', null, '角色管理菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('102', '菜单管理', '1', '3', 'menu', 'system/menu/index', '', '', '1', '0', 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', '2025-11-17 18:04:01', '', null, '菜单管理菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('103', '部门管理', '1', '4', 'dept', 'system/dept/index', '', '', '1', '0', 'C', '0', '0', 'system:dept:list', 'tree', 'admin', '2025-11-17 18:04:01', '', null, '部门管理菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('104', '岗位管理', '1', '5', 'post', 'system/post/index', '', '', '1', '0', 'C', '0', '0', 'system:post:list', 'post', 'admin', '2025-11-17 18:04:01', '', null, '岗位管理菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('105', '字典管理', '1', '6', 'dict', 'system/dict/index', '', '', '1', '0', 'C', '0', '0', 'system:dict:list', 'dict', 'admin', '2025-11-17 18:04:01', '', null, '字典管理菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('106', '参数设置', '1', '7', 'config', 'system/config/index', '', '', '1', '0', 'C', '0', '0', 'system:config:list', 'edit', 'admin', '2025-11-17 18:04:01', '', null, '参数设置菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('107', '通知公告', '1', '8', 'notice', 'system/notice/index', '', '', '1', '0', 'C', '0', '0', 'system:notice:list', 'message', 'admin', '2025-11-17 18:04:01', '', null, '通知公告菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('108', '日志管理', '1', '9', 'log', '', '', '', '1', '0', 'M', '0', '0', '', 'log', 'admin', '2025-11-17 18:04:01', '', null, '日志管理菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('109', '在线用户', '2', '1', 'online', 'monitor/online/index', '', '', '1', '0', 'C', '0', '0', 'monitor:online:list', 'online', 'admin', '2025-11-17 18:04:01', '', null, '在线用户菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('110', '定时任务', '2', '2', 'job', 'monitor/job/index', '', '', '1', '0', 'C', '0', '0', 'monitor:job:list', 'job', 'admin', '2025-11-17 18:04:01', '', null, '定时任务菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('111', '数据监控', '2', '3', 'druid', 'monitor/druid/index', '', '', '1', '0', 'C', '0', '0', 'monitor:druid:list', 'druid', 'admin', '2025-11-17 18:04:01', '', null, '数据监控菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('112', '服务监控', '2', '4', 'server', 'monitor/server/index', '', '', '1', '0', 'C', '0', '0', 'monitor:server:list', 'server', 'admin', '2025-11-17 18:04:01', '', null, '服务监控菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('113', '缓存监控', '2', '5', 'cache', 'monitor/cache/index', '', '', '1', '0', 'C', '0', '0', 'monitor:cache:list', 'redis', 'admin', '2025-11-17 18:04:01', '', null, '缓存监控菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('114', '缓存列表', '2', '6', 'cacheList', 'monitor/cache/list', '', '', '1', '0', 'C', '0', '0', 'monitor:cache:list', 'redis-list', 'admin', '2025-11-17 18:04:01', '', null, '缓存列表菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('115', '表单构建', '3', '1', 'build', 'tool/build/index', '', '', '1', '0', 'C', '0', '0', 'tool:build:list', 'build', 'admin', '2025-11-17 18:04:01', '', null, '表单构建菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('116', '代码生成', '3', '2', 'gen', 'tool/gen/index', '', '', '1', '0', 'C', '0', '0', 'tool:gen:list', 'code', 'admin', '2025-11-17 18:04:01', '', null, '代码生成菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('117', '系统接口', '3', '3', 'swagger', 'tool/swagger/index', '', '', '1', '0', 'C', '0', '0', 'tool:swagger:list', 'swagger', 'admin', '2025-11-17 18:04:01', '', null, '系统接口菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('500', '操作日志', '108', '1', 'operlog', 'monitor/operlog/index', '', '', '1', '0', 'C', '0', '0', 'monitor:operlog:list', 'form', 'admin', '2025-11-17 18:04:01', '', null, '操作日志菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('501', '登录日志', '108', '2', 'logininfor', 'monitor/logininfor/index', '', '', '1', '0', 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 'admin', '2025-11-17 18:04:01', '', null, '登录日志菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1000', '用户查询', '100', '1', '', '', '', '', '1', '0', 'F', '0', '0', 'system:user:query', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1001', '用户新增', '100', '2', '', '', '', '', '1', '0', 'F', '0', '0', 'system:user:add', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1002', '用户修改', '100', '3', '', '', '', '', '1', '0', 'F', '0', '0', 'system:user:edit', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1003', '用户删除', '100', '4', '', '', '', '', '1', '0', 'F', '0', '0', 'system:user:remove', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1004', '用户导出', '100', '5', '', '', '', '', '1', '0', 'F', '0', '0', 'system:user:export', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1005', '用户导入', '100', '6', '', '', '', '', '1', '0', 'F', '0', '0', 'system:user:import', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1006', '重置密码', '100', '7', '', '', '', '', '1', '0', 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1007', '角色查询', '101', '1', '', '', '', '', '1', '0', 'F', '0', '0', 'system:role:query', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1008', '角色新增', '101', '2', '', '', '', '', '1', '0', 'F', '0', '0', 'system:role:add', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1009', '角色修改', '101', '3', '', '', '', '', '1', '0', 'F', '0', '0', 'system:role:edit', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1010', '角色删除', '101', '4', '', '', '', '', '1', '0', 'F', '0', '0', 'system:role:remove', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1011', '角色导出', '101', '5', '', '', '', '', '1', '0', 'F', '0', '0', 'system:role:export', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1012', '菜单查询', '102', '1', '', '', '', '', '1', '0', 'F', '0', '0', 'system:menu:query', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1013', '菜单新增', '102', '2', '', '', '', '', '1', '0', 'F', '0', '0', 'system:menu:add', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1014', '菜单修改', '102', '3', '', '', '', '', '1', '0', 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1015', '菜单删除', '102', '4', '', '', '', '', '1', '0', 'F', '0', '0', 'system:menu:remove', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1016', '部门查询', '103', '1', '', '', '', '', '1', '0', 'F', '0', '0', 'system:dept:query', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1017', '部门新增', '103', '2', '', '', '', '', '1', '0', 'F', '0', '0', 'system:dept:add', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1018', '部门修改', '103', '3', '', '', '', '', '1', '0', 'F', '0', '0', 'system:dept:edit', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1019', '部门删除', '103', '4', '', '', '', '', '1', '0', 'F', '0', '0', 'system:dept:remove', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1020', '岗位查询', '104', '1', '', '', '', '', '1', '0', 'F', '0', '0', 'system:post:query', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1021', '岗位新增', '104', '2', '', '', '', '', '1', '0', 'F', '0', '0', 'system:post:add', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1022', '岗位修改', '104', '3', '', '', '', '', '1', '0', 'F', '0', '0', 'system:post:edit', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1023', '岗位删除', '104', '4', '', '', '', '', '1', '0', 'F', '0', '0', 'system:post:remove', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1024', '岗位导出', '104', '5', '', '', '', '', '1', '0', 'F', '0', '0', 'system:post:export', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1025', '字典查询', '105', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:dict:query', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1026', '字典新增', '105', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:dict:add', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1027', '字典修改', '105', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:dict:edit', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1028', '字典删除', '105', '4', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:dict:remove', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1029', '字典导出', '105', '5', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:dict:export', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1030', '参数查询', '106', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:config:query', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1031', '参数新增', '106', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:config:add', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1032', '参数修改', '106', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:config:edit', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1033', '参数删除', '106', '4', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:config:remove', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1034', '参数导出', '106', '5', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:config:export', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1035', '公告查询', '107', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:notice:query', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1036', '公告新增', '107', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:notice:add', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1037', '公告修改', '107', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:notice:edit', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1038', '公告删除', '107', '4', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:notice:remove', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1039', '操作查询', '500', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:operlog:query', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1040', '操作删除', '500', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:operlog:remove', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1041', '日志导出', '500', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:operlog:export', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1042', '登录查询', '501', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:logininfor:query', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1043', '登录删除', '501', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:logininfor:remove', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1044', '日志导出', '501', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:logininfor:export', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1045', '账户解锁', '501', '4', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:logininfor:unlock', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1046', '在线查询', '109', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:online:query', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1047', '批量强退', '109', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:online:batchLogout', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1048', '单条强退', '109', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1049', '任务查询', '110', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:job:query', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1050', '任务新增', '110', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:job:add', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1051', '任务修改', '110', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:job:edit', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1052', '任务删除', '110', '4', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:job:remove', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1053', '状态修改', '110', '5', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:job:changeStatus', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1054', '任务导出', '110', '6', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:job:export', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1055', '生成查询', '116', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:query', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1056', '生成修改', '116', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:edit', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1057', '生成删除', '116', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:remove', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1058', '导入代码', '116', '4', '#', '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:import', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1059', '预览代码', '116', '5', '#', '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:preview', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('1060', '生成代码', '116', '6', '#', '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:code', '#', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2000', '港好住管理', '0', '5', 'gangzhu', null, null, '', '1', '0', 'M', '0', '0', '', 'guide', 'admin', '2025-11-18 00:18:53', '', null, '港好住业务管理');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2001', '房源管理', '3001', '4', 'house', null, null, '', '1', '0', 'M', '0', '0', 'gangzhu:house:list', 'build', 'admin', '2025-11-18 00:18:53', '', null, '房源管理菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2002', '房源查询', '4001', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:house:query', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2003', '房源新增', '4001', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:house:add', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2004', '房源修改', '4001', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:house:edit', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2005', '房源删除', '4001', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:house:remove', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2006', '房源导出', '4001', '5', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:house:export', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2007', '项目管理', '3001', '1', 'project', 'gangzhu/project/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:project:list', 'tree', 'admin', '2025-11-18 00:18:53', '', null, '项目管理菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2008', '项目查询', '2007', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:project:query', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2009', '项目新增', '2007', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:project:add', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2010', '项目修改', '2007', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:project:edit', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2011', '项目删除', '2007', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:project:remove', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2012', '租户管理', '3002', '1', 'tenant', 'gangzhu/tenant/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:tenant:list', 'peoples', 'admin', '2025-11-18 00:18:53', '', null, '租户管理菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2013', '租户查询', '2012', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:tenant:query', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2014', '租户详情', '2012', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:tenant:detail', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2015', '租户修改', '2012', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:tenant:edit', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2016', '租户导出', '2012', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:tenant:export', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2017', '资格申诉', '3002', '3', 'qualification', 'gangzhu/qualification/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:qualification:list', 'form', 'admin', '2025-11-18 00:18:53', '', null, '资格审核菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2018', '审核查询', '2017', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:qualification:query', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2019', '审核通过', '2017', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:qualification:approve', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2020', '审核拒绝', '2017', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:qualification:reject', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2021', '审核导出', '2017', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:qualification:export', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2022', '合同管理', '2000', '4', 'contract', 'gangzhu/contract/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:contract:list', 'documentation', 'admin', '2025-11-18 00:18:53', '', null, '合同管理菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2023', '合同查询', '2022', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:contract:query', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2024', '合同详情', '2022', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:contract:detail', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2025', '合同审核', '2022', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:contract:audit', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2026', '合同导出', '2022', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:contract:export', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2027', '租金账单', '2022', '2', 'bill', 'gangzhu/bill/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:bill:list', 'money', 'admin', '2025-11-18 00:18:53', '', null, '账单管理菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2028', '账单查询', '2027', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:bill:query', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2029', '账单新增', '2027', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:bill:add', '#', 'admin', '2025-11-18 00:18:53', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2030', '账单修改', '2027', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:bill:edit', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2031', '账单导出', '2027', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:bill:export', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2032', '入住管理', '2022', '3', 'checkin', 'gangzhu/checkin/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:checkin:list', 'log', 'admin', '2025-11-18 00:18:54', '', null, '入住管理菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2033', '入住查询', '2032', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:checkin:query', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2034', '入住审核', '2032', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:checkin:audit', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2035', '入住导出', '2032', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:checkin:export', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2036', '退租管理', '2022', '3', 'checkout', 'gangzhu/checkout/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:checkout:list', 'exit', 'admin', '2025-11-18 00:18:54', '', null, '退租管理菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2037', '退租查询', '2036', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:checkout:query', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2038', '退租审核', '2036', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:checkout:audit', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2039', '退租导出', '2036', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:checkout:export', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2040', '预约看房', '3002', '2', 'appointment', 'gangzhu/appointment/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:appointment:list', 'date', 'admin', '2025-11-18 00:18:54', '', null, '预约管理菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2041', '预约查询', '2040', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:appointment:query', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2042', '预约确认', '2040', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:appointment:confirm', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2043', '预约取消', '2040', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:appointment:cancel', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2044', '预约导出', '2040', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:appointment:export', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2045', '承诺书', '3002', '4', 'document', 'gangzhu/document/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:document:list', 'skill', 'admin', '2025-11-18 00:18:54', '', null, '资料审核菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2046', '资料查询', '2045', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:document:query', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2047', '资料审核', '2045', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:document:audit', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2048', '资料导出', '2045', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:document:export', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2049', '消息通知', '3004', '1', 'message', 'gangzhu/message/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:message:list', 'email', 'admin', '2025-11-18 00:18:54', '', null, '消息管理菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2050', '消息查询', '2049', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:message:query', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2051', '消息发送', '2049', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:message:send', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2052', '消息删除', '2049', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:message:remove', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2053', '通知公告', '3004', '4', 'announcement', 'gangzhu/announcement/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:announcement:list', 'message', 'admin', '2025-11-18 00:18:54', '', null, '公告管理菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2054', '公告查询', '2053', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:announcement:query', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2055', '公告新增', '2053', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:announcement:add', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2056', '公告修改', '2053', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:announcement:edit', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2057', '公告删除', '2053', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:announcement:remove', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2058', '开票管理', '2000', '9', 'invoice', 'gangzhu/invoice/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:invoice:list', 'post', 'admin', '2025-11-18 00:18:54', '', null, '开票管理菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2059', '开票查询', '2058', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:invoice:query', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2060', '开票审核', '2058', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:invoice:audit', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('2061', '开票导出', '2058', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:invoice:export', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('3000', '首页', '2000', '1', 'dashboard', 'gangzhu/dashboard/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:dashboard:view', 'dashboard', 'admin', '2025-11-18 20:22:04', '', null, '首页数据统计');
INSERT INTO `sys_menu_backup_20251206` VALUES ('3001', '资产管理', '2000', '2', 'asset', null, null, '', '1', '0', 'M', '0', '0', null, 'example', 'admin', '2025-11-18 20:22:04', '', null, '资产管理菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('3002', '申请信息', '2000', '3', 'application', null, null, '', '1', '0', 'M', '0', '0', null, 'form', 'admin', '2025-11-18 20:22:04', '', null, '申请信息菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('3003', '报表管理', '2000', '5', 'report', null, null, '', '1', '0', 'M', '0', '0', null, 'chart', 'admin', '2025-11-18 20:22:04', '', null, '报表管理菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('3004', '配置管理', '2000', '6', 'config', null, null, '', '1', '0', 'M', '0', '0', null, 'tool', 'admin', '2025-11-18 20:22:04', '', null, '配置管理菜单');
INSERT INTO `sys_menu_backup_20251206` VALUES ('3010', '合同列表', '2022', '1', 'list', 'gangzhu/contract/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:contract:list', '#', 'admin', '2025-11-18 20:23:34', '', null, '合同列表');
INSERT INTO `sys_menu_backup_20251206` VALUES ('3011', '退款管理', '2022', '4', 'refund', 'gangzhu/refund/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:refund:list', '#', 'admin', '2025-11-18 20:23:34', '', null, '退款管理');
INSERT INTO `sys_menu_backup_20251206` VALUES ('3012', '合住人管理', '2022', '5', 'roommate', 'gangzhu/roommate/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:roommate:list', '#', 'admin', '2025-11-18 20:23:34', '', null, '合住人管理');
INSERT INTO `sys_menu_backup_20251206` VALUES ('3013', '调换房管理', '2022', '6', 'transfer', 'gangzhu/transfer/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:transfer:list', '#', 'admin', '2025-11-18 20:23:34', '', null, '调换房管理');
INSERT INTO `sys_menu_backup_20251206` VALUES ('3020', '项目收款台账', '3003', '1', 'receipt', 'gangzhu/report/receipt/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:report:receipt', '#', 'admin', '2025-11-18 20:23:34', '', null, '项目收款台账');
INSERT INTO `sys_menu_backup_20251206` VALUES ('3021', '自定义报表', '3003', '2', 'custom', 'gangzhu/report/custom/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:report:custom', '#', 'admin', '2025-11-18 20:23:34', '', null, '自定义报表');
INSERT INTO `sys_menu_backup_20251206` VALUES ('3030', '合同模版', '3004', '2', 'template', 'gangzhu/config/template/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:config:template', '#', 'admin', '2025-11-18 20:23:34', '', null, '合同模版');
INSERT INTO `sys_menu_backup_20251206` VALUES ('3031', '运营配置', '3004', '3', 'operation', 'gangzhu/config/operation/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:config:operation', '#', 'admin', '2025-11-18 20:23:34', '', null, '运营配置');
INSERT INTO `sys_menu_backup_20251206` VALUES ('3032', '政策文件', '3004', '5', 'policy', 'gangzhu/config/policy/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:config:policy', '#', 'admin', '2025-11-18 20:23:34', '', null, '政策文件');
INSERT INTO `sys_menu_backup_20251206` VALUES ('3040', '黑名单管理', '2000', '7', 'blacklist', 'gangzhu/blacklist/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:blacklist:list', 'user', 'admin', '2025-11-18 20:23:34', '', null, '黑名单管理');
INSERT INTO `sys_menu_backup_20251206` VALUES ('3041', '用户管理', '2000', '8', 'user', 'gangzhu/user/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:user:list', 'peoples', 'admin', '2025-11-18 20:23:34', '', null, '前端用户管理');
INSERT INTO `sys_menu_backup_20251206` VALUES ('4001', '房源列表', '2001', '1', 'list', 'gangzhu/house/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:house:list', 'cascader', 'admin', '2025-11-18 20:46:23', 'admin', '2025-11-18 20:59:08', '房源列表管理');
INSERT INTO `sys_menu_backup_20251206` VALUES ('4002', '配租批次', '2001', '2', 'batch', 'gangzhu/house/batch/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:batch:list', 'edit', 'admin', '2025-11-18 20:46:36', 'admin', '2025-11-18 20:59:18', '配租批次管理');
INSERT INTO `sys_menu_backup_20251206` VALUES ('4003', '企业客户', '2001', '3', 'enterprise', 'gangzhu/house/enterprise/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:enterprise:list', 'redis-list', 'admin', '2025-11-18 20:46:48', 'admin', '2025-11-18 20:59:26', '企业客户管理');
INSERT INTO `sys_menu_backup_20251206` VALUES ('4010', '批次查询', '4002', '1', '#', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:batch:query', '#', 'admin', '2025-11-18 20:46:36', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('4011', '批次新增', '4002', '2', '#', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:batch:add', '#', 'admin', '2025-11-18 20:46:36', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('4012', '批次编辑', '4002', '3', '#', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:batch:edit', '#', 'admin', '2025-11-18 20:46:36', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('4013', '批次作废', '4002', '4', '#', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:batch:cancel', '#', 'admin', '2025-11-18 20:46:36', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('4014', '批次审批', '4002', '5', '#', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:batch:approve', '#', 'admin', '2025-11-18 20:46:36', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('4015', '批次导出', '4002', '6', '#', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:batch:export', '#', 'admin', '2025-11-18 20:46:36', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('4020', '企业查询', '4003', '1', '#', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:enterprise:query', '#', 'admin', '2025-11-18 20:46:48', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('4021', '企业新增', '4003', '2', '#', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:enterprise:add', '#', 'admin', '2025-11-18 20:46:48', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('4022', '企业编辑', '4003', '3', '#', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:enterprise:edit', '#', 'admin', '2025-11-18 20:46:48', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('4023', '企业作废', '4003', '4', '#', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:enterprise:cancel', '#', 'admin', '2025-11-18 20:46:48', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('4024', '企业审批', '4003', '5', '#', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:enterprise:approve', '#', 'admin', '2025-11-18 20:46:48', '', null, '');
INSERT INTO `sys_menu_backup_20251206` VALUES ('4025', '企业导出', '4003', '6', '#', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:enterprise:export', '#', 'admin', '2025-11-18 20:46:48', '', null, '');

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice` (
  `notice_id` int(4) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) NOT NULL COMMENT '公告标题',
  `notice_type` char(1) NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob COMMENT '公告内容',
  `status` char(1) DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='通知公告表';

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
INSERT INTO `sys_notice` VALUES ('1', '温馨提醒：2018-07-01 若依新版本发布啦', '2', 0xE696B0E78988E69CACE58685E5AEB9, '0', 'admin', '2025-11-17 18:04:03', '', null, '管理员');
INSERT INTO `sys_notice` VALUES ('2', '维护通知：2018-07-01 若依系统凌晨维护', '1', 0xE7BBB4E68AA4E58685E5AEB9, '0', 'admin', '2025-11-17 18:04:03', '', null, '管理员');

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log` (
  `oper_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) DEFAULT '' COMMENT '模块标题',
  `business_type` int(2) DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(200) DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) DEFAULT '' COMMENT '请求方式',
  `operator_type` int(1) DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) DEFAULT '' COMMENT '返回参数',
  `status` int(1) DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint(20) DEFAULT '0' COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`),
  KEY `idx_sys_oper_log_bt` (`business_type`),
  KEY `idx_sys_oper_log_s` (`status`),
  KEY `idx_sys_oper_log_ot` (`oper_time`)
) ENGINE=InnoDB AUTO_INCREMENT=176 DEFAULT CHARSET=utf8 COMMENT='操作日志记录';

-- ----------------------------
-- Records of sys_oper_log
-- ----------------------------
INSERT INTO `sys_oper_log` VALUES ('100', '用户头像', '2', 'com.ruoyi.web.controller.system.SysProfileController.avatar()', 'POST', '1', 'admin', '研发部门', '/system/user/profile/avatar', '127.0.0.1', '内网IP', '', '{\"msg\":\"操作成功\",\"imgUrl\":\"/profile/avatar/2025/11/17/54d9ef312c7142caa37ac2ecdddb0973.png\",\"code\":200}', '0', null, '2025-11-17 19:06:18', '229');
INSERT INTO `sys_oper_log` VALUES ('101', '个人信息', '2', 'com.ruoyi.web.controller.system.SysProfileController.updateProfile()', 'PUT', '1', 'admin', '研发部门', '/system/user/profile', '127.0.0.1', '内网IP', '{\"admin\":false,\"email\":\"ry@163.com\",\"nickName\":\"若依\",\"params\":{},\"phonenumber\":\"15888888888\",\"sex\":\"1\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-17 19:06:19', '36');
INSERT INTO `sys_oper_log` VALUES ('102', '项目管理', '1', 'com.ruoyi.web.controller.system.HzProjectController.add()', 'POST', '1', 'admin', '研发部门', '/system/project', '127.0.0.1', '内网IP', '{\"address\":\"阿斯蒂芬\",\"availableHouses\":0,\"facilities\":\"阿斯蒂芬\",\"latitude\":21.1212,\"longitude\":123.121,\"managerName\":\"wenwang\",\"managerPhone\":\"18539279011\",\"params\":{},\"projectCode\":\"012\",\"projectId\":1,\"projectIntro\":\"阿斯蒂芬\",\"projectName\":\"阿斯蒂芬\",\"projectType\":\"1\",\"sortOrder\":0,\"status\":\"0\",\"totalBuildings\":0,\"totalHouses\":0,\"transportation\":\"阿斯蒂芬\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-18 18:39:09', '29');
INSERT INTO `sys_oper_log` VALUES ('103', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"createTime\":\"2025-11-17 18:04:01\",\"icon\":\"guide\",\"isCache\":\"0\",\"isFrame\":\"0\",\"menuId\":4,\"menuName\":\"若依官网\",\"menuType\":\"M\",\"orderNum\":10,\"params\":{},\"parentId\":0,\"path\":\"http://ruoyi.vip\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-18 18:40:51', '29');
INSERT INTO `sys_oper_log` VALUES ('104', '项目管理', '1', 'com.ruoyi.web.controller.system.HzProjectController.add()', 'POST', '1', 'admin', '研发部门', '/system/project', '127.0.0.1', '内网IP', '{\"address\":\"阿斯蒂芬\",\"availableHouses\":200,\"latitude\":0,\"longitude\":0,\"managerName\":\"菜\",\"managerPhone\":\"18539279011\",\"params\":{},\"projectCode\":\"56\",\"projectId\":2,\"projectName\":\"测试1\",\"projectType\":\"1\",\"sortOrder\":2,\"status\":\"0\",\"totalBuildings\":50,\"totalHouses\":500}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-18 19:47:20', '76');
INSERT INTO `sys_oper_log` VALUES ('105', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"gangzhu/house/index\",\"createTime\":\"2025-11-18 20:46:23\",\"icon\":\"cascader\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":4001,\"menuName\":\"房源列表\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2001,\"path\":\"list\",\"perms\":\"gangzhu:house:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-18 20:59:09', '30');
INSERT INTO `sys_oper_log` VALUES ('106', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"gangzhu/house/batch/index\",\"createTime\":\"2025-11-18 20:46:36\",\"icon\":\"edit\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":4002,\"menuName\":\"配租批次\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2001,\"path\":\"batch\",\"perms\":\"gangzhu:batch:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-18 20:59:18', '18');
INSERT INTO `sys_oper_log` VALUES ('107', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"gangzhu/house/enterprise/index\",\"createTime\":\"2025-11-18 20:46:48\",\"icon\":\"redis-list\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":4003,\"menuName\":\"企业客户\",\"menuType\":\"C\",\"orderNum\":3,\"params\":{},\"parentId\":2001,\"path\":\"enterprise\",\"perms\":\"gangzhu:enterprise:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-18 20:59:26', '17');
INSERT INTO `sys_oper_log` VALUES ('108', '企业客户', '1', 'com.ruoyi.web.controller.system.HzEnterpriseController.add()', 'POST', '1', 'admin', '研发部门', '/system/enterprise', '127.0.0.1', '内网IP', '{\"contactPerson\":\"ss\",\"contactPhone\":\"18539279011\",\"delFlag\":\"0\",\"employeeCount\":0,\"enterpriseAddress\":\"测试地址\",\"enterpriseId\":1,\"enterpriseName\":\"阿斯蒂芬\",\"params\":{},\"rentedCount\":0,\"socialCreditCode\":\"91410100MA45TE2X81\",\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-18 21:00:22', '20');
INSERT INTO `sys_oper_log` VALUES ('109', '楼栋管理', '1', 'com.ruoyi.web.controller.system.HzBuildingController.add()', 'POST', '1', 'admin', '研发部门', '/system/building', '127.0.0.1', '内网IP', '{\"buildingCode\":\"3-1\",\"buildingId\":5,\"buildingName\":\"3号楼\",\"delFlag\":\"0\",\"params\":{},\"projectId\":1,\"sortOrder\":0,\"status\":\"0\",\"totalFloors\":1,\"totalHouses\":0,\"totalUnits\":1}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-18 21:31:03', '18');
INSERT INTO `sys_oper_log` VALUES ('110', '单元管理', '1', 'com.ruoyi.web.controller.system.HzUnitController.add()', 'POST', '1', 'admin', '研发部门', '/system/unit', '127.0.0.1', '内网IP', '{\"buildingId\":5,\"delFlag\":\"0\",\"params\":{},\"sortOrder\":2,\"status\":\"0\",\"totalHouses\":0,\"unitCode\":\"10\",\"unitId\":11,\"unitName\":\"啊啊\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-18 21:42:26', '15');
INSERT INTO `sys_oper_log` VALUES ('111', '房源管理', '1', 'com.ruoyi.web.controller.system.HzHouseController.add()', 'POST', '1', 'admin', '研发部门', '/system/house', '127.0.0.1', '内网IP', '{\"area\":60,\"buildingId\":3,\"deposit\":3000,\"floor\":1,\"houseCode\":\"123\",\"houseId\":1,\"houseNo\":\"456\",\"houseStatus\":\"0\",\"houseTypeName\":\"一室一厅\",\"isFeatured\":\"0\",\"orientation\":\"南\",\"params\":{},\"projectId\":2,\"rentPrice\":2000,\"status\":\"0\",\"unitId\":5}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-18 22:07:33', '26');
INSERT INTO `sys_oper_log` VALUES ('112', '户型', '1', 'com.ruoyi.system.controller.HzHouseTypeController.add()', 'POST', '1', 'admin', '研发部门', '/gangzhu/houseType', '127.0.0.1', '内网IP', '{\"balconyCount\":0,\"bathroomCount\":0,\"bedroomCount\":0,\"delFlag\":\"0\",\"houseTypeCode\":\"12\",\"houseTypeId\":1,\"houseTypeName\":\"阿斯蒂芬\",\"kitchenCount\":0,\"livingroomCount\":0,\"params\":{},\"sortOrder\":0,\"status\":\"0\",\"typicalArea\":0}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-18 23:04:57', '23');
INSERT INTO `sys_oper_log` VALUES ('113', '参数管理', '2', 'com.ruoyi.web.controller.system.SysConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/config', '127.0.0.1', '内网IP', '{\"configId\":4,\"configKey\":\"sys.account.captchaEnabled\",\"configName\":\"账号自助-验证码开关\",\"configType\":\"Y\",\"configValue\":\"false\",\"createBy\":\"admin\",\"createTime\":\"2025-11-17 18:04:02\",\"params\":{},\"remark\":\"是否开启验证码功能（true开启，false关闭）\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-19 01:09:16', '27');
INSERT INTO `sys_oper_log` VALUES ('114', '参数管理', '2', 'com.ruoyi.web.controller.system.SysConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/config', '127.0.0.1', '内网IP', '{\"configId\":4,\"configKey\":\"sys.account.captchaEnabled\",\"configName\":\"账号自助-验证码开关\",\"configType\":\"N\",\"configValue\":\"false\",\"createBy\":\"admin\",\"createTime\":\"2025-11-17 18:04:02\",\"params\":{},\"remark\":\"是否开启验证码功能（true开启，false关闭）\",\"updateBy\":\"admin\",\"updateTime\":\"2025-11-19 01:09:16\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-19 01:09:32', '22');
INSERT INTO `sys_oper_log` VALUES ('115', '户型', '1', 'com.ruoyi.system.controller.HzHouseTypeController.add()', 'POST', '1', 'admin', '研发部门', '/gangzhu/houseType', '127.0.0.1', '内网IP', '{\"balconyCount\":0,\"bathroomCount\":0,\"bedroomCount\":0,\"delFlag\":\"0\",\"houseTypeCode\":\"23\",\"houseTypeId\":2,\"houseTypeName\":\"123123\",\"kitchenCount\":0,\"livingroomCount\":0,\"params\":{},\"sortOrder\":0,\"status\":\"0\",\"typicalArea\":0}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-19 01:10:57', '27');
INSERT INTO `sys_oper_log` VALUES ('116', '户型', '1', 'com.ruoyi.system.controller.HzHouseTypeController.add()', 'POST', '1', 'admin', '研发部门', '/gangzhu/houseType', '127.0.0.1', '内网IP', '{\"balconyCount\":0,\"bathroomCount\":0,\"bedroomCount\":0,\"delFlag\":\"0\",\"houseTypeCode\":\"23\",\"houseTypeName\":\"123123\",\"kitchenCount\":0,\"livingroomCount\":0,\"params\":{},\"sortOrder\":0,\"status\":\"0\",\"typicalArea\":0}', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'23\' for key \'uk_house_type_code\'\r\n### The error may exist in com/ruoyi/system/mapper/HzHouseTypeMapper.java (best guess)\r\n### The error may involve com.ruoyi.system.mapper.HzHouseTypeMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO hz_house_type  ( house_type_name, house_type_code, bedroom_count, livingroom_count, bathroom_count, kitchen_count, balcony_count, typical_area,  status, sort_order, del_flag )  VALUES (  ?, ?, ?, ?, ?, ?, ?, ?,  ?, ?, ?  )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'23\' for key \'uk_house_type_code\'\n; Duplicate entry \'23\' for key \'uk_house_type_code\'', '2025-11-19 01:11:01', '96');
INSERT INTO `sys_oper_log` VALUES ('117', '户型', '1', 'com.ruoyi.system.controller.HzHouseTypeController.add()', 'POST', '1', 'admin', '研发部门', '/gangzhu/houseType', '127.0.0.1', '内网IP', '{\"balconyCount\":0,\"bathroomCount\":0,\"bedroomCount\":0,\"delFlag\":\"0\",\"houseTypeCode\":\"87897878\",\"houseTypeId\":4,\"houseTypeName\":\"去玩儿去玩儿\",\"kitchenCount\":0,\"livingroomCount\":0,\"params\":{},\"sortOrder\":0,\"status\":\"0\",\"typicalArea\":0}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-19 01:27:20', '21');
INSERT INTO `sys_oper_log` VALUES ('118', '户型', '1', 'com.ruoyi.system.controller.HzHouseTypeController.add()', 'POST', '1', 'admin', '研发部门', '/gangzhu/houseType', '127.0.0.1', '内网IP', '{\"balconyCount\":0,\"bathroomCount\":0,\"bedroomCount\":0,\"delFlag\":\"0\",\"houseTypeCode\":\"87897878\",\"houseTypeName\":\"去玩儿去玩儿\",\"kitchenCount\":0,\"livingroomCount\":0,\"params\":{},\"sortOrder\":0,\"status\":\"0\",\"typicalArea\":0}', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'87897878\' for key \'uk_house_type_code\'\r\n### The error may exist in com/ruoyi/system/mapper/HzHouseTypeMapper.java (best guess)\r\n### The error may involve com.ruoyi.system.mapper.HzHouseTypeMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO hz_house_type  ( house_type_name, house_type_code, bedroom_count, livingroom_count, bathroom_count, kitchen_count, balcony_count, typical_area,  status, sort_order, del_flag )  VALUES (  ?, ?, ?, ?, ?, ?, ?, ?,  ?, ?, ?  )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'87897878\' for key \'uk_house_type_code\'\n; Duplicate entry \'87897878\' for key \'uk_house_type_code\'', '2025-11-19 01:27:25', '14');
INSERT INTO `sys_oper_log` VALUES ('119', '户型', '1', 'com.ruoyi.system.controller.HzHouseTypeController.add()', 'POST', '1', 'admin', '研发部门', '/gangzhu/houseType', '127.0.0.1', '内网IP', '{\"balconyCount\":0,\"bathroomCount\":0,\"bedroomCount\":0,\"delFlag\":\"0\",\"houseTypeCode\":\"787889\",\"houseTypeId\":6,\"houseTypeName\":\"阿斯蒂芬022\",\"kitchenCount\":0,\"livingroomCount\":0,\"params\":{},\"sortOrder\":0,\"status\":\"0\",\"typicalArea\":0}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-19 01:30:52', '24');
INSERT INTO `sys_oper_log` VALUES ('120', '户型', '1', 'com.ruoyi.system.controller.HzHouseTypeController.add()', 'POST', '1', 'admin', '研发部门', '/gangzhu/houseType', '127.0.0.1', '内网IP', '{\"balconyCount\":0,\"bathroomCount\":0,\"bedroomCount\":0,\"delFlag\":\"0\",\"houseTypeCode\":\"8712\",\"houseTypeId\":7,\"houseTypeName\":\"阿斯蒂芬\",\"kitchenCount\":0,\"livingroomCount\":0,\"params\":{},\"sortOrder\":0,\"status\":\"0\",\"typicalArea\":0}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-19 01:34:14', '11');
INSERT INTO `sys_oper_log` VALUES ('121', '户型', '1', 'com.ruoyi.system.controller.HzHouseTypeController.add()', 'POST', '1', 'admin', '研发部门', '/gangzhu/houseType', '127.0.0.1', '内网IP', '{\"balconyCount\":0,\"bathroomCount\":0,\"bedroomCount\":0,\"delFlag\":\"0\",\"houseTypeCode\":\"45s\",\"houseTypeId\":8,\"houseTypeName\":\"是是是\",\"kitchenCount\":0,\"livingroomCount\":0,\"params\":{},\"projectId\":1,\"sortOrder\":0,\"status\":\"0\",\"typicalArea\":0}', '{\"msg\":\"新增成功\",\"code\":200,\"data\":8}', '0', null, '2025-11-19 01:58:34', '96');
INSERT INTO `sys_oper_log` VALUES ('122', '户型', '1', 'com.ruoyi.system.controller.HzHouseTypeController.add()', 'POST', '1', 'admin', '研发部门', '/gangzhu/houseType', '127.0.0.1', '内网IP', '{\"balconyCount\":0,\"bathroomCount\":0,\"bedroomCount\":0,\"delFlag\":\"0\",\"houseTypeCode\":\"4546556\",\"houseTypeId\":9,\"houseTypeName\":\"阿斯蒂芬阿斯蒂芬\",\"kitchenCount\":0,\"livingroomCount\":0,\"params\":{},\"projectId\":1,\"sortOrder\":0,\"status\":\"0\",\"typicalArea\":0}', '{\"msg\":\"新增成功\",\"code\":200,\"data\":9}', '0', null, '2025-11-19 02:01:58', '21');
INSERT INTO `sys_oper_log` VALUES ('123', '户型', '1', 'com.ruoyi.system.controller.HzHouseTypeController.add()', 'POST', '1', 'admin', '研发部门', '/gangzhu/houseType', '127.0.0.1', '内网IP', '{\"balconyCount\":0,\"bathroomCount\":0,\"bedroomCount\":0,\"delFlag\":\"0\",\"houseTypeCode\":\"79878\",\"houseTypeId\":10,\"houseTypeName\":\"阿斯蒂芬456465\",\"kitchenCount\":0,\"livingroomCount\":0,\"params\":{},\"projectId\":1,\"sortOrder\":0,\"status\":\"0\",\"typicalArea\":0}', '{\"msg\":\"新增成功\",\"code\":200,\"data\":10}', '0', null, '2025-11-19 02:03:27', '18');
INSERT INTO `sys_oper_log` VALUES ('124', '户型', '1', 'com.ruoyi.system.controller.HzHouseTypeController.add()', 'POST', '1', 'admin', '研发部门', '/gangzhu/houseType', '127.0.0.1', '内网IP', '{\"balconyCount\":0,\"bathroomCount\":0,\"bedroomCount\":0,\"delFlag\":\"0\",\"houseTypeCode\":\"4546啊\",\"houseTypeId\":11,\"houseTypeName\":\"阿斯蒂123123123\",\"kitchenCount\":0,\"livingroomCount\":0,\"params\":{},\"projectId\":1,\"sortOrder\":0,\"status\":\"0\",\"typicalArea\":0}', '{\"msg\":\"新增成功\",\"code\":200,\"data\":11}', '0', null, '2025-11-19 02:13:25', '15');
INSERT INTO `sys_oper_log` VALUES ('125', '户型', '1', 'com.ruoyi.system.controller.HzHouseTypeController.add()', 'POST', '1', 'admin', '研发部门', '/gangzhu/houseType', '127.0.0.1', '内网IP', '{\"balconyCount\":0,\"bathroomCount\":0,\"bedroomCount\":0,\"delFlag\":\"0\",\"houseTypeCode\":\"阿斯蒂芬000000\",\"houseTypeId\":12,\"houseTypeName\":\"阿斯蒂芬\",\"kitchenCount\":0,\"livingroomCount\":0,\"params\":{},\"projectId\":1,\"sortOrder\":0,\"status\":\"0\",\"typicalArea\":0}', '{\"msg\":\"新增成功\",\"code\":200,\"data\":12}', '0', null, '2025-11-19 02:27:52', '16');
INSERT INTO `sys_oper_log` VALUES ('126', '户型', '1', 'com.ruoyi.system.controller.HzHouseTypeController.add()', 'POST', '1', 'admin', '研发部门', '/gangzhu/houseType', '127.0.0.1', '内网IP', '{\"balconyCount\":0,\"bathroomCount\":0,\"bedroomCount\":0,\"delFlag\":\"0\",\"houseTypeCode\":\"1212\",\"houseTypeId\":13,\"houseTypeName\":\"森岛帆高121\",\"kitchenCount\":0,\"livingroomCount\":0,\"params\":{},\"projectId\":1,\"remark\":\"阿斯蒂\",\"sortOrder\":0,\"status\":\"0\",\"typicalArea\":0}', '{\"msg\":\"新增成功\",\"code\":200,\"data\":13}', '0', null, '2025-11-19 02:35:53', '14');
INSERT INTO `sys_oper_log` VALUES ('127', '户型', '2', 'com.ruoyi.system.controller.HzHouseTypeController.edit()', 'PUT', '1', 'admin', '研发部门', '/gangzhu/houseType', '127.0.0.1', '内网IP', '{\"balconyCount\":0,\"bathroomCount\":0,\"bedroomCount\":0,\"createBy\":\"\",\"delFlag\":\"0\",\"houseTypeCode\":\"1212\",\"houseTypeId\":13,\"houseTypeName\":\"森岛帆高121\",\"kitchenCount\":0,\"livingroomCount\":0,\"params\":{},\"projectId\":1,\"remark\":\"阿斯蒂\",\"sortOrder\":0,\"status\":\"0\",\"typicalArea\":0,\"updateBy\":\"\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-19 02:40:31', '8');
INSERT INTO `sys_oper_log` VALUES ('128', '户型图片', '2', 'com.ruoyi.system.controller.HzHouseTypeController.saveImages()', 'POST', '1', 'admin', '研发部门', '/gangzhu/houseType/13/images', '127.0.0.1', '内网IP', '13 [{\"delFlag\":\"0\",\"houseTypeId\":13,\"imageId\":1,\"imageType\":\"1\",\"imageUrl\":\"/profile/upload/2025/11/19/logo_20251119024029A016.png\",\"isCover\":\"1\",\"params\":{},\"sortOrder\":1}]', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-19 02:40:31', '30');
INSERT INTO `sys_oper_log` VALUES ('129', '户型', '2', 'com.ruoyi.system.controller.HzHouseTypeController.edit()', 'PUT', '1', 'admin', '研发部门', '/gangzhu/houseType', '127.0.0.1', '内网IP', '{\"balconyCount\":0,\"bathroomCount\":0,\"bedroomCount\":0,\"createBy\":\"\",\"delFlag\":\"0\",\"houseTypeCode\":\"1212\",\"houseTypeId\":13,\"houseTypeName\":\"森岛帆高121\",\"kitchenCount\":0,\"livingroomCount\":0,\"params\":{},\"projectId\":1,\"remark\":\"阿斯蒂\",\"sortOrder\":0,\"status\":\"0\",\"typicalArea\":0,\"updateBy\":\"\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-19 02:47:38', '4');
INSERT INTO `sys_oper_log` VALUES ('130', '户型图片', '2', 'com.ruoyi.system.controller.HzHouseTypeController.saveImages()', 'POST', '1', 'admin', '研发部门', '/gangzhu/houseType/13/images', '127.0.0.1', '内网IP', '13 [{\"delFlag\":\"0\",\"houseTypeId\":13,\"imageId\":2,\"imageType\":\"1\",\"imageUrl\":\"/profile/upload/2025/11/19/logo_20251119024029A016.png\",\"isCover\":\"1\",\"params\":{},\"sortOrder\":1},{\"delFlag\":\"0\",\"houseTypeId\":13,\"imageId\":3,\"imageType\":\"1\",\"imageUrl\":\"/profile/upload/2025/11/19/profile_20251119024737A017.jpg\",\"isCover\":\"0\",\"params\":{},\"sortOrder\":2}]', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-19 02:47:38', '18');
INSERT INTO `sys_oper_log` VALUES ('131', '参数管理', '2', 'com.ruoyi.web.controller.system.SysConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/config', '127.0.0.1', '内网IP', '{\"configId\":3,\"configKey\":\"sys.index.sideTheme\",\"configName\":\"主框架页-侧边栏主题\",\"configType\":\"Y\",\"configValue\":\"theme-light\",\"createBy\":\"admin\",\"createTime\":\"2025-11-17 18:04:02\",\"params\":{},\"remark\":\"深色主题theme-dark，浅色主题theme-light\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-06 17:56:05', '28');
INSERT INTO `sys_oper_log` VALUES ('132', '参数管理', '2', 'com.ruoyi.web.controller.system.SysConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/config', '127.0.0.1', '内网IP', '{\"configId\":3,\"configKey\":\"sys.index.sideTheme\",\"configName\":\"主框架页-侧边栏主题\",\"configType\":\"N\",\"configValue\":\"theme-light\",\"createBy\":\"admin\",\"createTime\":\"2025-11-17 18:04:02\",\"params\":{},\"remark\":\"深色主题theme-dark，浅色主题theme-light\",\"updateBy\":\"admin\",\"updateTime\":\"2025-12-06 17:56:05\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-06 17:56:17', '39');
INSERT INTO `sys_oper_log` VALUES ('133', '菜单管理', '3', 'com.ruoyi.web.controller.system.SysMenuController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/menu/4', '127.0.0.1', '内网IP', '4', '{\"msg\":\"菜单已分配,不允许删除\",\"code\":601}', '0', null, '2025-12-07 06:53:20', '15');
INSERT INTO `sys_oper_log` VALUES ('134', '户型', '3', 'com.ruoyi.system.controller.HzHouseTypeController.remove()', 'DELETE', '1', 'admin', '研发部门', '/gangzhu/houseType/8', '127.0.0.1', '内网IP', '[8]', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'WHERE house_type_id=8 AND del_flag=\'0\'\' at line 1\r\n### The error may exist in com/ruoyi/system/mapper/HzHouseTypeMapper.java (best guess)\r\n### The error may involve com.ruoyi.system.mapper.HzHouseTypeMapper.updateById-Inline\r\n### The error occurred while setting parameters\r\n### SQL: UPDATE hz_house_type    WHERE house_type_id=? AND del_flag=\'0\'\r\n### Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'WHERE house_type_id=8 AND del_flag=\'0\'\' at line 1\n; bad SQL grammar []', '2026-01-11 01:07:32', '153');
INSERT INTO `sys_oper_log` VALUES ('135', '户型', '3', 'com.ruoyi.system.controller.HzHouseTypeController.remove()', 'DELETE', '1', 'admin', '研发部门', '/gangzhu/houseType/8', '127.0.0.1', '内网IP', '[8]', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'WHERE house_type_id=8 AND del_flag=\'0\'\' at line 1\r\n### The error may exist in com/ruoyi/system/mapper/HzHouseTypeMapper.java (best guess)\r\n### The error may involve com.ruoyi.system.mapper.HzHouseTypeMapper.updateById-Inline\r\n### The error occurred while setting parameters\r\n### SQL: UPDATE hz_house_type    WHERE house_type_id=? AND del_flag=\'0\'\r\n### Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'WHERE house_type_id=8 AND del_flag=\'0\'\' at line 1\n; bad SQL grammar []', '2026-01-11 01:07:34', '12');
INSERT INTO `sys_oper_log` VALUES ('136', '户型', '1', 'com.ruoyi.system.controller.HzHouseTypeController.add()', 'POST', '1', 'admin', '研发部门', '/gangzhu/houseType', '127.0.0.1', '内网IP', '{\"balconyCount\":1,\"bathroomCount\":1,\"bedroomCount\":3,\"delFlag\":\"0\",\"houseTypeCode\":\"dasanfang01\",\"houseTypeId\":14,\"houseTypeName\":\"大三房\",\"kitchenCount\":1,\"livingroomCount\":1,\"params\":{},\"projectId\":2,\"sortOrder\":0,\"status\":\"0\",\"typicalArea\":150}', '{\"msg\":\"新增成功\",\"code\":200,\"data\":14}', '0', null, '2026-01-11 01:40:13', '124');
INSERT INTO `sys_oper_log` VALUES ('137', '房源管理', '1', 'com.ruoyi.web.controller.system.HzHouseController.add()', 'POST', '1', 'admin', '研发部门', '/system/house', '127.0.0.1', '内网IP', '{\"area\":0,\"buildingId\":5,\"deposit\":500,\"floor\":1,\"houseCode\":\"kl\",\"houseId\":2,\"houseNo\":\"502\",\"houseStatus\":\"0\",\"houseTypeId\":10,\"houseTypeName\":\"阿斯蒂芬456465\",\"isFeatured\":\"0\",\"orientation\":\"东\",\"params\":{},\"projectId\":1,\"rentPrice\":500,\"status\":\"0\",\"unitId\":11}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-11 01:49:58', '11');
INSERT INTO `sys_oper_log` VALUES ('138', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/house', '127.0.0.1', '内网IP', '{\"area\":60,\"buildingId\":3,\"createBy\":\"\",\"decoration\":\"毛坯\",\"deposit\":3000,\"floor\":1,\"houseCode\":\"123\",\"houseId\":1,\"houseNo\":\"456\",\"houseStatus\":\"0\",\"houseTypeId\":14,\"houseTypeName\":\"大三房\",\"isFeatured\":\"0\",\"orientation\":\"南\",\"params\":{},\"projectId\":2,\"rentPrice\":2000,\"status\":\"0\",\"unitId\":5,\"updateBy\":\"\",\"viewCount\":0}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-11 01:51:49', '5');
INSERT INTO `sys_oper_log` VALUES ('139', '房源管理', '1', 'com.ruoyi.web.controller.system.HzHouseController.add()', 'POST', '1', 'admin', '研发部门', '/system/house', '127.0.0.1', '内网IP', '{\"area\":60,\"buildingId\":1,\"decoration\":\"毛坯\",\"deposit\":6000,\"facilities\":\"阿斯蒂芬\",\"floor\":1,\"houseCode\":\"llkkk\",\"houseId\":3,\"houseNo\":\"456\",\"houseStatus\":\"0\",\"houseTypeId\":10,\"houseTypeName\":\"阿斯蒂芬456465\",\"isFeatured\":\"1\",\"orientation\":\"南\",\"params\":{},\"projectId\":1,\"remark\":\"阿斯蒂芬\",\"rentPrice\":9000,\"status\":\"0\",\"unitId\":1}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-11 02:58:25', '20');
INSERT INTO `sys_oper_log` VALUES ('140', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/house', '127.0.0.1', '内网IP', '{\"area\":60,\"buildingId\":1,\"createBy\":\"\",\"decoration\":\"毛坯\",\"deposit\":6000,\"facilities\":\"阿斯蒂芬\",\"floor\":1,\"houseCode\":\"llkkk\",\"houseId\":3,\"houseNo\":\"456\",\"houseStatus\":\"0\",\"houseTypeId\":10,\"houseTypeName\":\"阿斯蒂芬456465\",\"isFeatured\":\"1\",\"orientation\":\"南\",\"params\":{},\"projectId\":1,\"remark\":\"阿斯蒂芬\",\"rentPrice\":9000,\"status\":\"0\",\"unitId\":1,\"updateBy\":\"\",\"viewCount\":0}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-11 03:12:04', '9');
INSERT INTO `sys_oper_log` VALUES ('141', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.saveHouseImages()', 'POST', '1', 'admin', '研发部门', '/system/house/images', '127.0.0.1', '内网IP', '{\"houseId\":3,\"images\":[{\"houseId\":3,\"imageUrl\":\"/profile/upload/2026/01/11/矩形 21@2x_20260111031203A005.png\",\"imageType\":\"1\",\"isCover\":\"1\",\"sortOrder\":0}]}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-11 03:12:05', '13');
INSERT INTO `sys_oper_log` VALUES ('142', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/house', '127.0.0.1', '内网IP', '{\"area\":60,\"buildingId\":1,\"createBy\":\"\",\"decoration\":\"毛坯\",\"deposit\":6000,\"facilities\":\"阿斯蒂芬\",\"floor\":1,\"houseCode\":\"llkkk\",\"houseId\":3,\"houseNo\":\"456\",\"houseStatus\":\"0\",\"houseTypeId\":10,\"houseTypeName\":\"阿斯蒂芬456465\",\"isFeatured\":\"1\",\"orientation\":\"南\",\"params\":{},\"projectId\":1,\"remark\":\"阿斯蒂芬\",\"rentPrice\":9000,\"status\":\"0\",\"unitId\":1,\"updateBy\":\"\",\"viewCount\":0}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-11 03:12:15', '2');
INSERT INTO `sys_oper_log` VALUES ('143', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.saveHouseImages()', 'POST', '1', 'admin', '研发部门', '/system/house/images', '127.0.0.1', '内网IP', '{\"houseId\":3,\"images\":[{\"houseId\":3,\"imageUrl\":\"/profile/upload/2026/01/11/矩形 21@2x_20260111031203A005.png\",\"imageType\":\"1\",\"isCover\":\"1\",\"sortOrder\":0},{\"houseId\":3,\"imageUrl\":\"/profile/upload/2026/01/11/市场租赁@2x_20260111031214A006.png\",\"imageType\":\"1\",\"isCover\":\"0\",\"sortOrder\":1}]}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-11 03:12:15', '13');
INSERT INTO `sys_oper_log` VALUES ('144', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/house', '127.0.0.1', '内网IP', '{\"area\":60,\"buildingId\":1,\"createBy\":\"\",\"decoration\":\"毛坯\",\"deposit\":6000,\"facilities\":\"阿斯蒂芬\",\"floor\":1,\"houseCode\":\"llkkk\",\"houseId\":3,\"houseNo\":\"456\",\"houseStatus\":\"0\",\"houseTypeId\":10,\"houseTypeName\":\"阿斯蒂芬456465\",\"isFeatured\":\"1\",\"orientation\":\"南\",\"params\":{},\"projectId\":1,\"remark\":\"阿斯蒂芬\",\"rentPrice\":9000,\"status\":\"0\",\"unitId\":1,\"updateBy\":\"\",\"viewCount\":0}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-11 03:34:36', '11');
INSERT INTO `sys_oper_log` VALUES ('145', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.saveHouseVrs()', 'POST', '1', 'admin', '研发部门', '/system/house/vrs', '127.0.0.1', '内网IP', '{\"houseId\":3,\"vrs\":[{\"houseId\":3,\"vrName\":\"VR1\",\"vrUrl\":\"/profile/upload/2026/01/11/QP61014284_20260111033434A001.jpg\",\"sortOrder\":0}]}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-11 03:34:36', '35');
INSERT INTO `sys_oper_log` VALUES ('146', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.saveHouseImages()', 'POST', '1', 'admin', '研发部门', '/system/house/images', '127.0.0.1', '内网IP', '{\"houseId\":3,\"images\":[{\"houseId\":3,\"imageUrl\":\"/profile/upload/2026/01/11/矩形 21@2x_20260111031203A005.png\",\"imageType\":\"1\",\"isCover\":\"1\",\"sortOrder\":0},{\"houseId\":3,\"imageUrl\":\"/profile/upload/2026/01/11/市场租赁@2x_20260111031214A006.png\",\"imageType\":\"1\",\"isCover\":\"0\",\"sortOrder\":1}]}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-11 03:34:36', '60');
INSERT INTO `sys_oper_log` VALUES ('147', '项目管理', '1', 'com.ruoyi.web.controller.system.HzProjectController.add()', 'POST', '1', 'admin', '研发部门', '/system/project', '127.0.0.1', '内网IP', '{\"address\":\"港区大河路\",\"availableHouses\":0,\"latitude\":0,\"longitude\":0,\"managerName\":\"王飞飞\",\"managerPhone\":\"18539279011\",\"params\":{},\"projectCode\":\"12\",\"projectId\":3,\"projectName\":\"港区测试项目1\",\"projectType\":\"1\",\"sortOrder\":0,\"status\":\"0\",\"totalBuildings\":0,\"totalHouses\":0}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-11 15:13:04', '30');
INSERT INTO `sys_oper_log` VALUES ('148', '楼栋管理', '1', 'com.ruoyi.web.controller.system.HzBuildingController.add()', 'POST', '1', 'admin', '研发部门', '/system/building', '127.0.0.1', '内网IP', '{\"buildingCode\":\"10\",\"buildingId\":6,\"buildingName\":\"楼栋1\",\"delFlag\":\"0\",\"params\":{},\"projectId\":3,\"sortOrder\":0,\"status\":\"0\",\"totalFloors\":4,\"totalHouses\":0,\"totalUnits\":5}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-11 15:13:44', '16');
INSERT INTO `sys_oper_log` VALUES ('149', '单元管理', '1', 'com.ruoyi.web.controller.system.HzUnitController.add()', 'POST', '1', 'admin', '研发部门', '/system/unit', '127.0.0.1', '内网IP', '{\"buildingId\":6,\"delFlag\":\"0\",\"params\":{},\"projectId\":3,\"sortOrder\":0,\"status\":\"0\",\"totalHouses\":0,\"unitCode\":\"100\",\"unitId\":12,\"unitName\":\"单元1\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-11 15:14:10', '43');
INSERT INTO `sys_oper_log` VALUES ('150', '户型', '1', 'com.ruoyi.system.controller.HzHouseTypeController.add()', 'POST', '1', 'admin', '研发部门', '/gangzhu/houseType', '127.0.0.1', '内网IP', '{\"balconyCount\":0,\"bathroomCount\":0,\"bedroomCount\":1,\"delFlag\":\"0\",\"houseTypeCode\":\"ceshi01\",\"houseTypeId\":15,\"houseTypeName\":\"测试01\",\"kitchenCount\":0,\"livingroomCount\":1,\"params\":{},\"projectId\":3,\"sortOrder\":0,\"status\":\"0\",\"typicalArea\":0}', '{\"msg\":\"新增成功\",\"code\":200,\"data\":15}', '0', null, '2026-01-11 15:14:59', '20');
INSERT INTO `sys_oper_log` VALUES ('151', '房源管理', '1', 'com.ruoyi.web.controller.system.HzHouseController.add()', 'POST', '1', 'admin', '研发部门', '/system/house', '127.0.0.1', '内网IP', '{\"area\":600,\"buildingId\":6,\"decoration\":\"毛坯\",\"deposit\":200,\"facilities\":\"少时诵诗书\",\"floor\":1,\"houseCode\":\"01\",\"houseId\":4,\"houseNo\":\"123\",\"houseStatus\":\"0\",\"houseTypeId\":15,\"houseTypeName\":\"测试01\",\"isFeatured\":\"0\",\"orientation\":\"南\",\"params\":{},\"projectId\":3,\"rentPrice\":5000,\"status\":\"0\",\"unitId\":12}', '{\"msg\":\"操作成功\",\"code\":200,\"data\":4}', '0', null, '2026-01-11 15:17:11', '19');
INSERT INTO `sys_oper_log` VALUES ('152', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.saveHouseImages()', 'POST', '1', 'admin', '研发部门', '/system/house/images', '127.0.0.1', '内网IP', '{\"houseId\":4,\"images\":[{\"houseId\":4,\"imageUrl\":\"/profile/upload/2026/01/11/WPS图片(1)_20260111151641A001.jpeg\",\"imageType\":\"1\",\"isCover\":\"1\",\"sortOrder\":0}]}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-11 15:17:12', '11');
INSERT INTO `sys_oper_log` VALUES ('153', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.saveHouseVrs()', 'POST', '1', 'admin', '研发部门', '/system/house/vrs', '127.0.0.1', '内网IP', '{\"houseId\":4,\"vrs\":[{\"houseId\":4,\"vrName\":\"VR1\",\"vrUrl\":\"/profile/upload/2026/01/11/QP61014284_20260111151706A002.jpg\",\"sortOrder\":0}]}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-11 15:17:12', '11');
INSERT INTO `sys_oper_log` VALUES ('154', '项目管理', '2', 'com.ruoyi.web.controller.system.HzProjectController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/project', '127.0.0.1', '内网IP', '{\"address\":\"阿斯蒂芬\",\"availableHouses\":0,\"coverImage\":\"/profile/upload/2026/01/12/合住人-灰@2x_20260112003348A001.png\",\"createBy\":\"\",\"delFlag\":\"0\",\"facilities\":\"阿斯蒂芬,商业街,停车场\",\"latitude\":21.1212,\"longitude\":123.121,\"managerName\":\"wenwang\",\"managerPhone\":\"18539279011\",\"params\":{},\"projectCode\":\"012\",\"projectId\":1,\"projectIntro\":\"阿斯蒂芬\",\"projectName\":\"阿斯蒂芬\",\"projectType\":\"1\",\"sortOrder\":0,\"status\":\"0\",\"totalBuildings\":0,\"totalHouses\":0,\"transportation\":\"阿斯蒂芬\",\"updateBy\":\"\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 00:33:54', '13');
INSERT INTO `sys_oper_log` VALUES ('155', '项目管理', '2', 'com.ruoyi.web.controller.system.HzProjectController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/project', '127.0.0.1', '内网IP', '{\"address\":\"阿斯蒂芬\",\"availableHouses\":0,\"coverImage\":\"/profile/upload/2026/01/12/合住人-灰@2x_20260112003348A001.png\",\"createBy\":\"\",\"delFlag\":\"0\",\"facilities\":\"阿斯蒂芬,商业街,停车场\",\"latitude\":21.1212,\"longitude\":123.121,\"managerName\":\"wenwang\",\"managerPhone\":\"18539279011\",\"params\":{},\"projectCode\":\"012\",\"projectId\":1,\"projectIntro\":\"\",\"projectName\":\"阿斯蒂芬\",\"projectType\":\"1\",\"sortOrder\":0,\"status\":\"0\",\"totalBuildings\":0,\"totalHouses\":0,\"transportation\":\"阿斯蒂芬\",\"updateBy\":\"\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 00:42:11', '21');
INSERT INTO `sys_oper_log` VALUES ('156', '项目管理', '2', 'com.ruoyi.web.controller.system.HzProjectController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/project', '127.0.0.1', '内网IP', '{\"address\":\"港区大河路\",\"availableHouses\":0,\"createBy\":\"\",\"delFlag\":\"0\",\"facilities\":\"商业街\",\"latitude\":0,\"longitude\":0,\"managerName\":\"王飞飞\",\"managerPhone\":\"18539279011\",\"params\":{},\"projectCode\":\"12\",\"projectId\":3,\"projectName\":\"港区测试项目1\",\"projectType\":\"1\",\"sortOrder\":0,\"status\":\"0\",\"totalBuildings\":0,\"totalHouses\":0,\"updateBy\":\"\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 00:42:30', '33');
INSERT INTO `sys_oper_log` VALUES ('157', '项目管理', '2', 'com.ruoyi.web.controller.system.HzProjectController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/project', '127.0.0.1', '内网IP', '{\"address\":\"阿斯蒂芬\",\"availableHouses\":0,\"coverImage\":\"/profile/upload/2026/01/12/合住人-灰@2x_20260112003348A001.png\",\"createBy\":\"\",\"delFlag\":\"0\",\"facilities\":\"阿斯蒂芬,商业街,停车场\",\"latitude\":21.1212,\"longitude\":123.121,\"managerName\":\"wenwang\",\"managerPhone\":\"18539279011\",\"params\":{},\"price\":2000,\"projectCode\":\"012\",\"projectId\":1,\"projectIntro\":\"\",\"projectName\":\"阿斯蒂芬\",\"projectType\":\"1\",\"sortOrder\":0,\"status\":\"0\",\"totalBuildings\":0,\"totalHouses\":0,\"transportation\":\"阿斯蒂芬\",\"updateBy\":\"\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 01:00:06', '20');
INSERT INTO `sys_oper_log` VALUES ('158', '项目管理', '2', 'com.ruoyi.web.controller.system.HzProjectController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/project', '127.0.0.1', '内网IP', '{\"address\":\"阿斯蒂芬\",\"availableHouses\":0,\"coverImage\":\"/profile/upload/2026/01/12/合住人-灰@2x_20260112003348A001.png\",\"createBy\":\"\",\"delFlag\":\"0\",\"facilities\":\"阿斯蒂芬,商业街,停车场\",\"latitude\":21.1212,\"longitude\":123.121,\"managerName\":\"wenwang\",\"managerPhone\":\"18539279011\",\"params\":{},\"price\":2000,\"projectCode\":\"012\",\"projectId\":1,\"projectIntro\":\"\",\"projectName\":\"阿斯蒂芬\",\"projectType\":\"1\",\"sortOrder\":0,\"status\":\"0\",\"totalBuildings\":0,\"totalHouses\":0,\"transportation\":\"00\",\"updateBy\":\"\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 01:00:27', '15');
INSERT INTO `sys_oper_log` VALUES ('159', '项目管理', '1', 'com.ruoyi.web.controller.system.HzProjectController.add()', 'POST', '1', 'admin', '研发部门', '/system/project', '127.0.0.1', '内网IP', '{\"address\":\"港区万科城\",\"availableHouses\":120,\"coverImage\":\"/profile/upload/2026/01/12/矩形 21@2x_20260112010426A001.png\",\"facilities\":\"商业街,停车场,运动场,游泳池\",\"latitude\":0,\"longitude\":0,\"managerName\":\"陈飞飞\",\"managerPhone\":\"18539279011\",\"params\":{},\"price\":1500,\"projectCode\":\"wanke\",\"projectId\":4,\"projectIntro\":\"这是个测试项目\",\"projectName\":\"港区万科城\",\"projectType\":\"1\",\"sortOrder\":10,\"status\":\"0\",\"totalBuildings\":100,\"totalHouses\":200}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 01:04:36', '5');
INSERT INTO `sys_oper_log` VALUES ('160', '项目管理', '3', 'com.ruoyi.web.controller.system.HzProjectController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/project/1', '127.0.0.1', '内网IP', '[1]', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 01:36:42', '48');
INSERT INTO `sys_oper_log` VALUES ('161', '项目管理', '1', 'com.ruoyi.web.controller.system.HzProjectController.add()', 'POST', '1', 'admin', '研发部门', '/system/project', '127.0.0.1', '内网IP', '{\"address\":\"港区金科路395号\",\"availableHouses\":10,\"coverImage\":\"/profile/upload/2026/01/12/矩形 2@2x_20260112013757A002.png\",\"facilities\":\"停车场,商业街\",\"latitude\":0,\"longitude\":0,\"managerName\":\"李菲数\",\"managerPhone\":\"18564659565\",\"params\":{},\"price\":2800,\"projectCode\":\"3695\",\"projectId\":5,\"projectName\":\"港区金科城\",\"projectType\":\"1\",\"sortOrder\":0,\"status\":\"0\",\"totalBuildings\":185,\"totalHouses\":120}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 01:38:07', '14');
INSERT INTO `sys_oper_log` VALUES ('162', '楼栋管理', '1', 'com.ruoyi.web.controller.system.HzBuildingController.add()', 'POST', '1', 'admin', '研发部门', '/system/building', '127.0.0.1', '内网IP', '{\"buildingCode\":\"21\",\"buildingId\":7,\"buildingName\":\"2号楼\",\"delFlag\":\"0\",\"params\":{},\"projectId\":4,\"sortOrder\":0,\"status\":\"0\",\"totalFloors\":10,\"totalHouses\":0,\"totalUnits\":2}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 01:39:45', '10');
INSERT INTO `sys_oper_log` VALUES ('163', '单元管理', '1', 'com.ruoyi.web.controller.system.HzUnitController.add()', 'POST', '1', 'admin', '研发部门', '/system/unit', '127.0.0.1', '内网IP', '{\"buildingId\":7,\"delFlag\":\"0\",\"params\":{},\"projectId\":4,\"sortOrder\":0,\"status\":\"0\",\"totalHouses\":0,\"unitCode\":\"1\",\"unitId\":13,\"unitName\":\"1单元\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 01:40:06', '8');
INSERT INTO `sys_oper_log` VALUES ('164', '户型', '1', 'com.ruoyi.system.controller.HzHouseTypeController.add()', 'POST', '1', 'admin', '研发部门', '/gangzhu/houseType', '127.0.0.1', '内网IP', '{\"balconyCount\":1,\"bathroomCount\":1,\"bedroomCount\":3,\"delFlag\":\"0\",\"houseTypeCode\":\"dasanfang\",\"houseTypeId\":16,\"houseTypeName\":\"大三房\",\"kitchenCount\":1,\"livingroomCount\":2,\"params\":{},\"projectId\":4,\"sortOrder\":0,\"status\":\"0\",\"typicalArea\":180}', '{\"msg\":\"新增成功\",\"code\":200,\"data\":16}', '0', null, '2026-01-12 01:40:42', '32');
INSERT INTO `sys_oper_log` VALUES ('165', '房源管理', '1', 'com.ruoyi.web.controller.system.HzHouseController.add()', 'POST', '1', 'admin', '研发部门', '/system/house', '127.0.0.1', '内网IP', '{\"area\":180,\"buildingId\":7,\"deposit\":1000,\"floor\":1,\"houseCode\":\"12\",\"houseId\":5,\"houseNo\":\"301\",\"houseStatus\":\"0\",\"houseTypeId\":16,\"houseTypeName\":\"大三房\",\"isFeatured\":\"0\",\"orientation\":\"东\",\"params\":{},\"projectId\":4,\"rentPrice\":2000,\"status\":\"0\",\"unitId\":13}', '{\"msg\":\"操作成功\",\"code\":200,\"data\":5}', '0', null, '2026-01-12 01:42:05', '44');
INSERT INTO `sys_oper_log` VALUES ('166', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.saveHouseVrs()', 'POST', '1', 'admin', '研发部门', '/system/house/vrs', '127.0.0.1', '内网IP', '{\"houseId\":5,\"vrs\":[{\"houseId\":5,\"vrName\":\"VR1\",\"vrUrl\":\"/profile/upload/2026/01/12/QP61014284_20260112014202A006.jpg\",\"sortOrder\":0}]}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 01:42:05', '22');
INSERT INTO `sys_oper_log` VALUES ('167', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.saveHouseImages()', 'POST', '1', 'admin', '研发部门', '/system/house/images', '127.0.0.1', '内网IP', '{\"houseId\":5,\"images\":[{\"houseId\":5,\"imageUrl\":\"/profile/upload/2026/01/12/矩形 21@2x_20260112014153A004.png\",\"imageType\":\"1\",\"isCover\":\"1\",\"sortOrder\":0},{\"houseId\":5,\"imageUrl\":\"/profile/upload/2026/01/12/人才公寓@2x_20260112014154A005.png\",\"imageType\":\"1\",\"isCover\":\"0\",\"sortOrder\":1}]}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 01:42:05', '40');
INSERT INTO `sys_oper_log` VALUES ('168', '项目管理', '2', 'com.ruoyi.web.controller.system.HzProjectController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/project', '127.0.0.1', '内网IP', '{\"address\":\"港区大河路\",\"availableHouses\":0,\"coverImage\":\"/profile/upload/2026/01/12/5033.jpg_wh300_20260112015248A001.jpg\",\"createBy\":\"\",\"delFlag\":\"0\",\"facilities\":\"商业街\",\"latitude\":0,\"longitude\":0,\"managerName\":\"王飞飞\",\"managerPhone\":\"18539279011\",\"params\":{},\"price\":5000,\"projectCode\":\"12\",\"projectId\":3,\"projectName\":\"港区测试项目1\",\"projectType\":\"1\",\"sortOrder\":0,\"status\":\"0\",\"totalBuildings\":0,\"totalHouses\":0,\"updateBy\":\"\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 01:52:55', '46');
INSERT INTO `sys_oper_log` VALUES ('169', '项目管理', '2', 'com.ruoyi.web.controller.system.HzProjectController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/project', '127.0.0.1', '内网IP', '{\"address\":\"港区万科城\",\"availableHouses\":120,\"coverImage\":\"/profile/upload/2026/01/12/ScreenShot_2026-01-12_015115_648_20260112015403A002.png\",\"createBy\":\"\",\"delFlag\":\"0\",\"facilities\":\"商业街,停车场,运动场,游泳池\",\"latitude\":0,\"longitude\":0,\"managerName\":\"陈飞飞\",\"managerPhone\":\"18539279011\",\"params\":{},\"price\":1500,\"projectCode\":\"wanke\",\"projectId\":4,\"projectIntro\":\"这是个测试项目\",\"projectName\":\"港区万科城\",\"projectType\":\"1\",\"sortOrder\":10,\"status\":\"0\",\"totalBuildings\":100,\"totalHouses\":200,\"updateBy\":\"\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 01:54:05', '6');
INSERT INTO `sys_oper_log` VALUES ('170', '项目管理', '2', 'com.ruoyi.web.controller.system.HzProjectController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/project', '127.0.0.1', '内网IP', '{\"address\":\"阿斯蒂芬\",\"availableHouses\":200,\"coverImage\":\"/profile/upload/2026/01/12/df079c167cae476cb21790c62b610481_20260112015410A003.png\",\"createBy\":\"\",\"delFlag\":\"0\",\"facilities\":\"停车场,运动场,游泳池,健身房\",\"latitude\":0,\"longitude\":0,\"managerName\":\"菜\",\"managerPhone\":\"18539279011\",\"params\":{},\"price\":6000,\"projectCode\":\"56\",\"projectId\":2,\"projectName\":\"测试1\",\"projectType\":\"1\",\"sortOrder\":2,\"status\":\"0\",\"totalBuildings\":50,\"totalHouses\":500,\"updateBy\":\"\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 01:54:20', '17');
INSERT INTO `sys_oper_log` VALUES ('171', '项目管理', '2', 'com.ruoyi.web.controller.system.HzProjectController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/project', '127.0.0.1', '内网IP', '{\"address\":\"港区金科路395号\",\"availableHouses\":10,\"coverImage\":\"/profile/upload/2026/01/12/R-C_20260112015435A004.jpg\",\"createBy\":\"\",\"delFlag\":\"0\",\"facilities\":\"停车场,商业街\",\"latitude\":0,\"longitude\":0,\"managerName\":\"李菲数\",\"managerPhone\":\"18564659565\",\"params\":{},\"price\":2800,\"projectCode\":\"3695\",\"projectId\":5,\"projectName\":\"港区金科城\",\"projectType\":\"1\",\"sortOrder\":0,\"status\":\"0\",\"totalBuildings\":185,\"totalHouses\":120,\"updateBy\":\"\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 01:54:37', '23');
INSERT INTO `sys_oper_log` VALUES ('172', '项目管理', '1', 'com.ruoyi.web.controller.system.HzProjectController.add()', 'POST', '1', 'admin', '研发部门', '/system/project', '127.0.0.1', '内网IP', '{\"address\":\"港区黄河路123号恒大城\",\"availableHouses\":10,\"coverImage\":\"/profile/upload/2026/01/12/ScreenShot_2026-01-12_015115_648_20260112031148A001.png\",\"facilities\":\"快递柜,停车场,商业街,医疗室,运动场,游泳池\",\"latitude\":0,\"longitude\":0,\"managerName\":\"廖凡\",\"managerPhone\":\"13695645652\",\"params\":{},\"price\":6300,\"projectCode\":\"hengda\",\"projectId\":6,\"projectIntro\":\"暂无介绍\",\"projectName\":\"港区恒大城\",\"projectType\":\"2\",\"sortOrder\":0,\"status\":\"0\",\"totalBuildings\":100,\"totalHouses\":100}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 03:12:01', '22');
INSERT INTO `sys_oper_log` VALUES ('173', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/house', '127.0.0.1', '内网IP', '{\"area\":180,\"buildingId\":7,\"createBy\":\"\",\"deposit\":1000,\"floor\":1,\"houseCode\":\"12\",\"houseId\":5,\"houseNo\":\"301\",\"houseStatus\":\"0\",\"houseTypeId\":16,\"houseTypeName\":\"大三房\",\"isFeatured\":\"0\",\"orientation\":\"东\",\"params\":{},\"projectId\":4,\"rentPrice\":2000,\"status\":\"0\",\"unitId\":13,\"updateBy\":\"\",\"viewCount\":0}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 03:33:51', '10');
INSERT INTO `sys_oper_log` VALUES ('174', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.saveHouseVrs()', 'POST', '1', 'admin', '研发部门', '/system/house/vrs', '127.0.0.1', '内网IP', '{\"houseId\":5,\"vrs\":[{\"houseId\":5,\"vrName\":\"VR1\",\"vrUrl\":\"/profile/upload/2026/01/12/QP61014284_20260112014202A006.jpg\",\"sortOrder\":0}]}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 03:33:51', '13');
INSERT INTO `sys_oper_log` VALUES ('175', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.saveHouseImages()', 'POST', '1', 'admin', '研发部门', '/system/house/images', '127.0.0.1', '内网IP', '{\"houseId\":5,\"images\":[{\"houseId\":5,\"imageUrl\":\"/profile/upload/2026/01/12/矩形 21@2x_20260112014153A004.png\",\"imageType\":\"1\",\"isCover\":\"1\",\"sortOrder\":0},{\"houseId\":5,\"imageUrl\":\"/profile/upload/2026/01/12/人才公寓@2x_20260112014154A005.png\",\"imageType\":\"1\",\"isCover\":\"0\",\"sortOrder\":1},{\"houseId\":5,\"imageUrl\":\"/profile/upload/2026/01/12/df079c167cae476cb21790c62b610481_20260112033330A002.png\",\"imageType\":\"2\",\"isCover\":\"0\",\"sortOrder\":2},{\"houseId\":5,\"imageUrl\":\"/profile/upload/2026/01/12/R-C (1)_20260112033332A003.jpg\",\"imageType\":\"2\",\"isCover\":\"0\",\"sortOrder\":3},{\"houseId\":5,\"imageUrl\":\"/profile/upload/2026/01/12/4255.jpg_wh860_20260112033334A004.jpg\",\"imageType\":\"3\",\"isCover\":\"0\",\"sortOrder\":4},{\"houseId\":5,\"imageUrl\":\"/profile/upload/2026/01/12/ScreenShot_2026-01-12_015115_648_20260112033336A005.png\",\"imageType\":\"3\",\"isCover\":\"0\",\"sortOrder\":5},{\"houseId\":5,\"imageUrl\":\"/profile/upload/2026/01/12/R-C (1)_20260112033338A006.jpg\",\"imageType\":\"4\",\"isCover\":\"0\",\"sortOrder\":6},{\"houseId\":5,\"imageUrl\":\"/profile/upload/2026/01/12/df079c167cae476cb21790c62b610481_20260112033344A008.png\",\"imageType\":\"5\",\"isCover\":\"0\",\"sortOrder\":7},{\"houseId\":5,\"imageUrl\":\"/profile/upload/2026/01/12/df079c167cae476cb21790c62b610481_20260112033341A007.png\",\"imageType\":\"6\",\"isCover\":\"0\",\"sortOrder\":8}]}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 03:33:52', '58');

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post` (
  `post_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) NOT NULL COMMENT '岗位名称',
  `post_sort` int(4) NOT NULL COMMENT '显示顺序',
  `status` char(1) NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='岗位信息表';

-- ----------------------------
-- Records of sys_post
-- ----------------------------
INSERT INTO `sys_post` VALUES ('1', 'ceo', '董事长', '1', '0', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_post` VALUES ('2', 'se', '项目经理', '2', '0', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_post` VALUES ('3', 'hr', '人力资源', '3', '0', 'admin', '2025-11-17 18:04:01', '', null, '');
INSERT INTO `sys_post` VALUES ('4', 'user', '普通员工', '4', '0', 'admin', '2025-11-17 18:04:01', '', null, '');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) NOT NULL COMMENT '角色权限字符串',
  `role_sort` int(4) NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) DEFAULT '1' COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) DEFAULT '1' COMMENT '部门树选择项是否关联显示',
  `status` char(1) NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8 COMMENT='角色信息表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('1', '超级管理员', 'admin', '1', '1', '1', '1', '0', '0', 'admin', '2025-11-17 18:04:01', '', null, '超级管理员');
INSERT INTO `sys_role` VALUES ('2', '普通角色', 'common', '2', '2', '1', '1', '0', '0', 'admin', '2025-11-17 18:04:01', '', null, '普通角色');

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`,`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色和部门关联表';

-- ----------------------------
-- Records of sys_role_dept
-- ----------------------------
INSERT INTO `sys_role_dept` VALUES ('2', '100');
INSERT INTO `sys_role_dept` VALUES ('2', '101');
INSERT INTO `sys_role_dept` VALUES ('2', '105');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色和菜单关联表';

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES ('1', '2023');
INSERT INTO `sys_role_menu` VALUES ('1', '2024');
INSERT INTO `sys_role_menu` VALUES ('1', '2025');
INSERT INTO `sys_role_menu` VALUES ('1', '2026');
INSERT INTO `sys_role_menu` VALUES ('1', '2032');
INSERT INTO `sys_role_menu` VALUES ('1', '2036');
INSERT INTO `sys_role_menu` VALUES ('1', '3010');
INSERT INTO `sys_role_menu` VALUES ('1', '4002');
INSERT INTO `sys_role_menu` VALUES ('1', '4003');
INSERT INTO `sys_role_menu` VALUES ('1', '4010');
INSERT INTO `sys_role_menu` VALUES ('1', '4011');
INSERT INTO `sys_role_menu` VALUES ('1', '4012');
INSERT INTO `sys_role_menu` VALUES ('1', '4013');
INSERT INTO `sys_role_menu` VALUES ('1', '4014');
INSERT INTO `sys_role_menu` VALUES ('1', '4015');
INSERT INTO `sys_role_menu` VALUES ('1', '4020');
INSERT INTO `sys_role_menu` VALUES ('1', '4021');
INSERT INTO `sys_role_menu` VALUES ('1', '4022');
INSERT INTO `sys_role_menu` VALUES ('1', '4023');
INSERT INTO `sys_role_menu` VALUES ('1', '4024');
INSERT INTO `sys_role_menu` VALUES ('1', '4025');
INSERT INTO `sys_role_menu` VALUES ('1', '4026');
INSERT INTO `sys_role_menu` VALUES ('1', '4030');
INSERT INTO `sys_role_menu` VALUES ('1', '4034');
INSERT INTO `sys_role_menu` VALUES ('1', '4038');
INSERT INTO `sys_role_menu` VALUES ('2', '1');
INSERT INTO `sys_role_menu` VALUES ('2', '2');
INSERT INTO `sys_role_menu` VALUES ('2', '3');
INSERT INTO `sys_role_menu` VALUES ('2', '4');
INSERT INTO `sys_role_menu` VALUES ('2', '100');
INSERT INTO `sys_role_menu` VALUES ('2', '101');
INSERT INTO `sys_role_menu` VALUES ('2', '102');
INSERT INTO `sys_role_menu` VALUES ('2', '103');
INSERT INTO `sys_role_menu` VALUES ('2', '104');
INSERT INTO `sys_role_menu` VALUES ('2', '105');
INSERT INTO `sys_role_menu` VALUES ('2', '106');
INSERT INTO `sys_role_menu` VALUES ('2', '107');
INSERT INTO `sys_role_menu` VALUES ('2', '108');
INSERT INTO `sys_role_menu` VALUES ('2', '109');
INSERT INTO `sys_role_menu` VALUES ('2', '110');
INSERT INTO `sys_role_menu` VALUES ('2', '111');
INSERT INTO `sys_role_menu` VALUES ('2', '112');
INSERT INTO `sys_role_menu` VALUES ('2', '113');
INSERT INTO `sys_role_menu` VALUES ('2', '114');
INSERT INTO `sys_role_menu` VALUES ('2', '115');
INSERT INTO `sys_role_menu` VALUES ('2', '116');
INSERT INTO `sys_role_menu` VALUES ('2', '117');
INSERT INTO `sys_role_menu` VALUES ('2', '500');
INSERT INTO `sys_role_menu` VALUES ('2', '501');
INSERT INTO `sys_role_menu` VALUES ('2', '1000');
INSERT INTO `sys_role_menu` VALUES ('2', '1001');
INSERT INTO `sys_role_menu` VALUES ('2', '1002');
INSERT INTO `sys_role_menu` VALUES ('2', '1003');
INSERT INTO `sys_role_menu` VALUES ('2', '1004');
INSERT INTO `sys_role_menu` VALUES ('2', '1005');
INSERT INTO `sys_role_menu` VALUES ('2', '1006');
INSERT INTO `sys_role_menu` VALUES ('2', '1007');
INSERT INTO `sys_role_menu` VALUES ('2', '1008');
INSERT INTO `sys_role_menu` VALUES ('2', '1009');
INSERT INTO `sys_role_menu` VALUES ('2', '1010');
INSERT INTO `sys_role_menu` VALUES ('2', '1011');
INSERT INTO `sys_role_menu` VALUES ('2', '1012');
INSERT INTO `sys_role_menu` VALUES ('2', '1013');
INSERT INTO `sys_role_menu` VALUES ('2', '1014');
INSERT INTO `sys_role_menu` VALUES ('2', '1015');
INSERT INTO `sys_role_menu` VALUES ('2', '1016');
INSERT INTO `sys_role_menu` VALUES ('2', '1017');
INSERT INTO `sys_role_menu` VALUES ('2', '1018');
INSERT INTO `sys_role_menu` VALUES ('2', '1019');
INSERT INTO `sys_role_menu` VALUES ('2', '1020');
INSERT INTO `sys_role_menu` VALUES ('2', '1021');
INSERT INTO `sys_role_menu` VALUES ('2', '1022');
INSERT INTO `sys_role_menu` VALUES ('2', '1023');
INSERT INTO `sys_role_menu` VALUES ('2', '1024');
INSERT INTO `sys_role_menu` VALUES ('2', '1025');
INSERT INTO `sys_role_menu` VALUES ('2', '1026');
INSERT INTO `sys_role_menu` VALUES ('2', '1027');
INSERT INTO `sys_role_menu` VALUES ('2', '1028');
INSERT INTO `sys_role_menu` VALUES ('2', '1029');
INSERT INTO `sys_role_menu` VALUES ('2', '1030');
INSERT INTO `sys_role_menu` VALUES ('2', '1031');
INSERT INTO `sys_role_menu` VALUES ('2', '1032');
INSERT INTO `sys_role_menu` VALUES ('2', '1033');
INSERT INTO `sys_role_menu` VALUES ('2', '1034');
INSERT INTO `sys_role_menu` VALUES ('2', '1035');
INSERT INTO `sys_role_menu` VALUES ('2', '1036');
INSERT INTO `sys_role_menu` VALUES ('2', '1037');
INSERT INTO `sys_role_menu` VALUES ('2', '1038');
INSERT INTO `sys_role_menu` VALUES ('2', '1039');
INSERT INTO `sys_role_menu` VALUES ('2', '1040');
INSERT INTO `sys_role_menu` VALUES ('2', '1041');
INSERT INTO `sys_role_menu` VALUES ('2', '1042');
INSERT INTO `sys_role_menu` VALUES ('2', '1043');
INSERT INTO `sys_role_menu` VALUES ('2', '1044');
INSERT INTO `sys_role_menu` VALUES ('2', '1045');
INSERT INTO `sys_role_menu` VALUES ('2', '1046');
INSERT INTO `sys_role_menu` VALUES ('2', '1047');
INSERT INTO `sys_role_menu` VALUES ('2', '1048');
INSERT INTO `sys_role_menu` VALUES ('2', '1049');
INSERT INTO `sys_role_menu` VALUES ('2', '1050');
INSERT INTO `sys_role_menu` VALUES ('2', '1051');
INSERT INTO `sys_role_menu` VALUES ('2', '1052');
INSERT INTO `sys_role_menu` VALUES ('2', '1053');
INSERT INTO `sys_role_menu` VALUES ('2', '1054');
INSERT INTO `sys_role_menu` VALUES ('2', '1055');
INSERT INTO `sys_role_menu` VALUES ('2', '1056');
INSERT INTO `sys_role_menu` VALUES ('2', '1057');
INSERT INTO `sys_role_menu` VALUES ('2', '1058');
INSERT INTO `sys_role_menu` VALUES ('2', '1059');
INSERT INTO `sys_role_menu` VALUES ('2', '1060');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) DEFAULT '' COMMENT '手机号码',
  `sex` char(1) DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) DEFAULT '' COMMENT '密码',
  `status` char(1) DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `pwd_update_date` datetime DEFAULT NULL COMMENT '密码最后更新时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8 COMMENT='用户信息表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1', '103', 'admin', '若依', '00', 'ry@163.com', '15888888888', '1', '/profile/avatar/2025/11/17/54d9ef312c7142caa37ac2ecdddb0973.png', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', '2026-01-12 03:09:01', '2025-11-17 18:04:01', 'admin', '2025-11-17 18:04:01', '', '2025-11-17 19:06:19', '管理员');
INSERT INTO `sys_user` VALUES ('2', '105', 'ry', '若依', '00', 'ry@qq.com', '15666666666', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', '2025-11-17 18:04:01', '2025-11-17 18:04:01', 'admin', '2025-11-17 18:04:01', '', null, '测试员');

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `post_id` bigint(20) NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`,`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户与岗位关联表';

-- ----------------------------
-- Records of sys_user_post
-- ----------------------------
INSERT INTO `sys_user_post` VALUES ('1', '1');
INSERT INTO `sys_user_post` VALUES ('2', '2');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户和角色关联表';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES ('1', '1');
INSERT INTO `sys_user_role` VALUES ('2', '2');
