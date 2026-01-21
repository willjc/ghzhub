/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 80015
Source Host           : localhost:3306
Source Database       : newgangzhu

Target Server Type    : MYSQL
Target Server Version : 80015
File Encoding         : 65001

Date: 2026-01-15 17:16:21
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
-- Table structure for hz_activity
-- ----------------------------
DROP TABLE IF EXISTS `hz_activity`;
CREATE TABLE `hz_activity` (
  `activity_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `activity_title` varchar(200) NOT NULL COMMENT '活动标题',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '活动主图',
  `activity_location` varchar(200) DEFAULT NULL COMMENT '活动地点',
  `registration_start_time` datetime DEFAULT NULL COMMENT '报名开始时间',
  `registration_end_time` datetime DEFAULT NULL COMMENT '报名结束时间',
  `activity_start_time` datetime DEFAULT NULL COMMENT '活动开始时间',
  `activity_end_time` datetime DEFAULT NULL COMMENT '活动结束时间',
  `activity_content` text COMMENT '活动详情（富文本）',
  `max_participants` int(11) DEFAULT NULL COMMENT '最大参与人数',
  `current_participants` int(11) DEFAULT '0' COMMENT '当前报名人数',
  `activity_type` varchar(50) DEFAULT NULL COMMENT '活动类型',
  `organizer` varchar(100) DEFAULT NULL COMMENT '主办单位',
  `contact_person` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `view_count` int(11) DEFAULT '0' COMMENT '浏览次数',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  PRIMARY KEY (`activity_id`),
  KEY `idx_activity_type` (`activity_type`),
  KEY `idx_status` (`status`),
  KEY `idx_registration_time` (`registration_start_time`,`registration_end_time`),
  KEY `idx_activity_time` (`activity_start_time`,`activity_end_time`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='人才家园活动表';

-- ----------------------------
-- Records of hz_activity
-- ----------------------------
INSERT INTO `hz_activity` VALUES ('1', '这是测试活动', '/profile/upload/2026/01/12/s1070959_20260112144541A001.jpg', '郑州高新区', '2026-01-01 00:00:00', '2026-01-05 00:00:00', '2026-01-13 00:00:00', '2026-01-28 00:00:00', '<p>阿斯蒂芬阿道夫阿斯蒂芬阿斯蒂芬<img src=\"/dev-api/profile/upload/2026/01/12/s34099286_20260112144549A002.jpg\"></p>', '500', '0', '6', '郑好办', '来来来', '18566555555', '5', '0', '', null, '', null, null, '0');

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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='棰勭害鐪嬫埧琛';

-- ----------------------------
-- Records of hz_appointment
-- ----------------------------
INSERT INTO `hz_appointment` VALUES ('1', 'YY202601140416391993', null, '晨晨', '18564656522', null, '3', '54', '2026-01-14', null, '10:00', '1', 'H5', '0', null, null, null, null, '', null, '', null, null, '2');
INSERT INTO `hz_appointment` VALUES ('2', 'YY202601141633393047', null, '哈哈', '18569595222', null, '3', '54', '2026-01-14', null, '10:00', '1', 'H5', '0', null, null, null, null, '', null, '', null, null, '2');
INSERT INTO `hz_appointment` VALUES ('3', 'YY202601141656503663', '2', '冲冲冲', '18512121121', null, '3', '54', '2026-01-14', null, '10:00', '1', 'H5', '2', null, null, '\"客户原因\"', null, '', null, '', null, null, '0');
INSERT INTO `hz_appointment` VALUES ('4', 'YY202601141837224750', '1', '阿斯蒂芬', '18569562632', null, '3', '54', '2026-01-14', null, '10:00', '1', 'H5', '1', null, '2026-01-14 18:37:40', null, null, '', null, '', null, null, '0');

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
  `enterprise_id` bigint(20) DEFAULT NULL COMMENT '企业ID',
  `enterprise_name` varchar(100) DEFAULT NULL COMMENT '企业名称',
  `contact_person` varchar(50) DEFAULT NULL COMMENT '鑱旂郴浜',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '鑱旂郴鐢佃瘽',
  `project_id` bigint(20) DEFAULT NULL COMMENT '项目ID',
  `project_ids` varchar(500) DEFAULT NULL COMMENT '项目ID列表(逗号分隔)',
  `talent_type` char(1) DEFAULT '0' COMMENT '批次人才类型(0普通人才 1定向人才)',
  `selection_start_date` date DEFAULT NULL COMMENT '选房开始日期',
  `selection_end_date` date DEFAULT NULL COMMENT '选房结束日期',
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='闆嗕腑閰嶇?鎵规?琛';

-- ----------------------------
-- Records of hz_batch_allocation
-- ----------------------------
INSERT INTO `hz_batch_allocation` VALUES ('2', 'BATCH1768216142041', 'ceshi ', null, null, null, null, null, '3,6,2', '0', '2026-01-14', '2026-01-31', '2', '0', '2026-01-12 19:09:02', '0', null, null, '0', '', null, '', null, 'ces', '2');
INSERT INTO `hz_batch_allocation` VALUES ('3', 'BATCH1768217485206', 'ceshi ', null, null, null, null, null, '3,6,2', '0', '2026-01-12', '2026-01-31', '2', '0', '2026-01-12 19:31:25', '0', null, null, '0', '', null, '', null, 'ces', '2');
INSERT INTO `hz_batch_allocation` VALUES ('4', 'BATCH1768218084109', 'ceshi', null, null, null, null, null, '3,5,6,2', '0', '2026-01-04', '2026-01-31', '2', '0', '2026-01-12 19:41:24', '0', null, null, '0', '', null, '', null, null, '0');

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
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鎵规?鎴挎簮琛';

-- ----------------------------
-- Records of hz_batch_house
-- ----------------------------
INSERT INTO `hz_batch_house` VALUES ('1', '2', '1', '123', '1', '1', '2026-01-12 19:09:02', '', null, '', null, null, '2');
INSERT INTO `hz_batch_house` VALUES ('2', '2', '4', '01', '1', '2', '2026-01-12 19:09:02', '', null, '', null, null, '2');
INSERT INTO `hz_batch_house` VALUES ('3', '3', '1', '123', '1', '3', '2026-01-12 19:31:25', '', null, '', null, null, '2');
INSERT INTO `hz_batch_house` VALUES ('4', '3', '4', '01', '1', '4', '2026-01-12 19:31:25', '', null, '', null, null, '2');
INSERT INTO `hz_batch_house` VALUES ('5', '2', '1', '123', '1', '5', '2026-01-12 19:40:17', '', null, '', null, null, '2');
INSERT INTO `hz_batch_house` VALUES ('6', '2', '4', '01', '1', '6', '2026-01-12 19:40:17', '', null, '', null, null, '2');
INSERT INTO `hz_batch_house` VALUES ('7', '4', '1', '123', '1', '7', '2026-01-12 19:41:24', '', null, '', null, null, '0');
INSERT INTO `hz_batch_house` VALUES ('8', '4', '4', '01', '1', '8', '2026-01-12 19:41:24', '', null, '', null, null, '0');

-- ----------------------------
-- Table structure for hz_batch_tenant
-- ----------------------------
DROP TABLE IF EXISTS `hz_batch_tenant`;
CREATE TABLE `hz_batch_tenant` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `batch_id` bigint(20) NOT NULL COMMENT '鎵规?ID',
  `tenant_name` varchar(50) NOT NULL COMMENT '绉熸埛濮撳悕',
  `id_card` varchar(50) NOT NULL COMMENT '身份证号',
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
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鎵规?绉熸埛琛';

-- ----------------------------
-- Records of hz_batch_tenant
-- ----------------------------
INSERT INTO `hz_batch_tenant` VALUES ('1', '2', '张三', '412895656523256623', '18539279522', null, null, '1', '1', '2026-01-12 19:09:02', '', null, '', null, null, '2');
INSERT INTO `hz_batch_tenant` VALUES ('2', '2', '张三', '412895656523256723', '18539379522', null, null, '4', '1', '2026-01-12 19:09:02', '', null, '', null, null, '2');
INSERT INTO `hz_batch_tenant` VALUES ('3', '3', '张三', '412895656523256623', '18539279522', null, null, '1', '1', '2026-01-12 19:31:25', '', null, '', null, null, '2');
INSERT INTO `hz_batch_tenant` VALUES ('4', '3', '张三', '412895656523256723', '18539379522', null, null, '4', '1', '2026-01-12 19:31:25', '', null, '', null, null, '2');
INSERT INTO `hz_batch_tenant` VALUES ('5', '2', '张三', '412895656523256623', '18539279522', null, null, '1', '1', '2026-01-12 19:40:17', '', null, '', null, null, '2');
INSERT INTO `hz_batch_tenant` VALUES ('6', '2', '张三', '412895656523256723', '18539379522', null, null, '4', '1', '2026-01-12 19:40:17', '', null, '', null, null, '2');
INSERT INTO `hz_batch_tenant` VALUES ('7', '4', '张三', '412895656523256623', '18539279522', null, null, '1', '1', '2026-01-12 19:41:24', '', null, '', null, null, '0');
INSERT INTO `hz_batch_tenant` VALUES ('8', '4', '张三', '412895656523246623', '18539279523', null, null, '4', '1', '2026-01-12 19:41:24', '', null, '', null, null, '0');

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
  `is_overdue` char(1) DEFAULT '0' COMMENT '是否逾期(0:否 1:是)',
  `overdue_days` int(11) DEFAULT '0' COMMENT '逾期天数',
  PRIMARY KEY (`bill_id`),
  UNIQUE KEY `uk_bill_no` (`bill_no`),
  KEY `idx_contract_id` (`contract_id`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_bill_status` (`bill_status`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='璐﹀崟琛';

-- ----------------------------
-- Records of hz_bill
-- ----------------------------
INSERT INTO `hz_bill` VALUES ('1', 'YJ20260114154947', '2', '1', '张三', '54', '101', '1', '2026-01-14', '2000.00', '0.00', '2000.00', '2026-01-14', '2026-01-14', '0.00', '0', null, null, null, '', null, '', null, null, '0', '0', '0');
INSERT INTO `hz_bill` VALUES ('2', 'ZJ2026011415494701', '2', '1', '张三', '54', '101', '2', '2026-01', '2200.00', '0.00', '2200.00', '2026-01-14', '2026-01-14', '0.00', '0', null, null, null, '', null, '', null, null, '0', '0', '0');
INSERT INTO `hz_bill` VALUES ('3', 'ZJ2026011415494702', '2', '1', '张三', '54', '101', '2', '2026-02', '2200.00', '0.00', '2200.00', '2026-02-14', '2026-02-14', '0.00', '0', null, null, null, '', null, '', null, null, '0', '0', '0');
INSERT INTO `hz_bill` VALUES ('4', 'ZJ2026011415494703', '2', '1', '张三', '54', '101', '2', '2026-03', '2200.00', '0.00', '2200.00', '2026-03-14', '2026-03-14', '0.00', '0', null, null, null, '', null, '', null, null, '0', '0', '0');
INSERT INTO `hz_bill` VALUES ('5', 'YJ20260115011930', '3', '1', '张三', '54', '101', '1', '2026-01-15', '2000.00', '0.00', '2000.00', '2026-01-15', '2026-01-15', '0.00', '0', null, null, null, '', null, '', null, null, '0', '0', '0');
INSERT INTO `hz_bill` VALUES ('6', 'ZJ2026011501193001', '3', '1', '张三', '54', '101', '2', '2026-01', '2200.00', '0.00', '2200.00', '2026-01-15', '2026-01-15', '0.00', '0', null, null, null, '', null, '', null, null, '0', '0', '0');
INSERT INTO `hz_bill` VALUES ('7', 'ZJ2026011501193002', '3', '1', '张三', '54', '101', '2', '2026-02', '2200.00', '0.00', '2200.00', '2026-02-15', '2026-02-15', '0.00', '0', null, null, null, '', null, '', null, null, '0', '0', '0');
INSERT INTO `hz_bill` VALUES ('8', 'ZJ2026011501193003', '3', '1', '张三', '54', '101', '2', '2026-03', '2200.00', '0.00', '2200.00', '2026-03-15', '2026-03-15', '0.00', '0', null, null, null, '', null, '', null, null, '0', '0', '0');
INSERT INTO `hz_bill` VALUES ('9', 'YJ20260115153918', '4', '1', '张三', '54', '101', '1', '2026-01-15', '2000.00', '0.00', '2000.00', '2026-01-15', '2026-01-15', '0.00', '0', null, null, null, '', null, '', null, null, '0', '0', '0');
INSERT INTO `hz_bill` VALUES ('10', 'ZJ2026011515391901', '4', '1', '张三', '54', '101', '2', '2026-01', '2200.00', '0.00', '2200.00', '2026-01-15', '2026-01-15', '0.00', '0', null, null, null, '', null, '', null, null, '0', '0', '0');
INSERT INTO `hz_bill` VALUES ('11', 'ZJ2026011515391902', '4', '1', '张三', '54', '101', '2', '2026-02', '2200.00', '0.00', '2200.00', '2026-02-15', '2026-02-15', '0.00', '0', null, null, null, '', null, '', null, null, '0', '0', '0');
INSERT INTO `hz_bill` VALUES ('12', 'ZJ2026011515391903', '4', '1', '张三', '54', '101', '2', '2026-03', '2200.00', '0.00', '2200.00', '2026-03-15', '2026-03-15', '0.00', '0', null, null, null, '', null, '', null, null, '0', '0', '0');
INSERT INTO `hz_bill` VALUES ('13', 'YJ20260115154126', '5', '1', '张三', '54', '101', '1', '2026-01-15', '2000.00', '0.00', '2000.00', '2026-01-15', '2026-01-15', '0.00', '0', null, null, null, '', null, '', null, null, '0', '0', '0');
INSERT INTO `hz_bill` VALUES ('14', 'ZJ2026011515412601', '5', '1', '张三', '54', '101', '2', '2026-01', '2200.00', '0.00', '2200.00', '2026-01-15', '2026-01-15', '0.00', '0', null, null, null, '', null, '', null, null, '0', '0', '0');
INSERT INTO `hz_bill` VALUES ('15', 'ZJ2026011515412602', '5', '1', '张三', '54', '101', '2', '2026-02', '2200.00', '0.00', '2200.00', '2026-02-15', '2026-02-15', '0.00', '0', null, null, null, '', null, '', null, null, '0', '0', '0');
INSERT INTO `hz_bill` VALUES ('16', 'ZJ2026011515412603', '5', '1', '张三', '54', '101', '2', '2026-03', '2200.00', '0.00', '2200.00', '2026-03-15', '2026-03-15', '0.00', '0', null, null, null, '', null, '', null, null, '0', '0', '0');

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
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='妤兼爧琛';

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
INSERT INTO `hz_building` VALUES ('8', '3', '1号楼', 'YJY-B01', '9', '2', '36', '0', '1', '', '2026-01-13 23:40:51', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('9', '3', '2号楼', 'YJY-B02', '9', '2', '36', '0', '2', '', '2026-01-13 23:40:51', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('10', '3', '3号楼', 'YJY-B03', '9', '2', '36', '0', '3', '', '2026-01-13 23:40:51', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('11', '3', '4号楼', 'YJY-B04', '9', '2', '36', '0', '4', '', '2026-01-13 23:40:51', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('12', '3', '5号楼', 'YJY-B05', '9', '2', '36', '0', '5', '', '2026-01-13 23:40:51', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('13', '9', '1号楼', 'HNXC-B01', '11', '3', '66', '0', '1', '', '2026-01-13 23:41:03', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('14', '9', '2号楼', 'HNXC-B02', '11', '3', '66', '0', '2', '', '2026-01-13 23:41:03', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('15', '9', '3号楼', 'HNXC-B03', '11', '3', '66', '0', '3', '', '2026-01-13 23:41:03', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('16', '9', '4号楼', 'HNXC-B04', '11', '3', '66', '0', '4', '', '2026-01-13 23:41:03', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('17', '9', '5号楼', 'HNXC-B05', '11', '3', '66', '0', '5', '', '2026-01-13 23:41:03', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('18', '2', '1号楼', 'JXJY-B01', '11', '2', '44', '0', '1', '', '2026-01-13 23:43:08', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('19', '2', '2号楼', 'JXJY-B02', '11', '2', '44', '0', '2', '', '2026-01-13 23:43:08', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('20', '2', '3号楼', 'JXJY-B03', '11', '2', '44', '0', '3', '', '2026-01-13 23:43:08', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('21', '2', '4号楼', 'JXJY-B04', '11', '2', '44', '0', '4', '', '2026-01-13 23:43:08', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('22', '2', '5号楼', 'JXJY-B05', '11', '2', '44', '0', '5', '', '2026-01-13 23:43:08', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('23', '4', '1号楼', 'WKC-B01', '12', '3', '72', '0', '1', '', '2026-01-13 23:43:20', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('24', '4', '2号楼', 'WKC-B02', '12', '3', '72', '0', '2', '', '2026-01-13 23:43:20', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('25', '4', '3号楼', 'WKC-B03', '12', '3', '72', '0', '3', '', '2026-01-13 23:43:20', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('26', '4', '4号楼', 'WKC-B04', '12', '3', '72', '0', '4', '', '2026-01-13 23:43:20', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('27', '4', '5号楼', 'WKC-B05', '12', '3', '72', '0', '5', '', '2026-01-13 23:43:20', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('28', '5', '1号楼', 'JKC-B01', '10', '2', '40', '0', '1', '', '2026-01-13 23:43:34', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('29', '5', '2号楼', 'JKC-B02', '10', '2', '40', '0', '2', '', '2026-01-13 23:43:34', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('30', '5', '3号楼', 'JKC-B03', '10', '2', '40', '0', '3', '', '2026-01-13 23:43:34', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('31', '5', '4号楼', 'JKC-B04', '10', '2', '40', '0', '4', '', '2026-01-13 23:43:34', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('32', '5', '5号楼', 'JKC-B05', '10', '2', '40', '0', '5', '', '2026-01-13 23:43:34', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('33', '6', '1号楼', 'HDC-B01', '18', '2', '72', '0', '1', '', '2026-01-13 23:43:46', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('34', '6', '2号楼', 'HDC-B02', '18', '2', '72', '0', '2', '', '2026-01-13 23:43:46', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('35', '6', '3号楼', 'HDC-B03', '18', '2', '72', '0', '3', '', '2026-01-13 23:43:46', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('36', '6', '4号楼', 'HDC-B04', '18', '2', '72', '0', '4', '', '2026-01-13 23:43:46', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('37', '6', '5号楼', 'HDC-B05', '18', '2', '72', '0', '5', '', '2026-01-13 23:43:46', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('38', '10', '1号楼', 'ZHGY-B01', '15', '3', '90', '0', '1', '', '2026-01-13 23:43:59', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('39', '10', '2号楼', 'ZHGY-B02', '15', '3', '90', '0', '2', '', '2026-01-13 23:43:59', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('40', '10', '3号楼', 'ZHGY-B03', '15', '3', '90', '0', '3', '', '2026-01-13 23:43:59', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('41', '10', '4号楼', 'ZHGY-B04', '15', '3', '90', '0', '4', '', '2026-01-13 23:43:59', '', null, null, '0');
INSERT INTO `hz_building` VALUES ('42', '10', '5号楼', 'ZHGY-B05', '15', '3', '90', '0', '5', '', '2026-01-13 23:43:59', '', null, null, '0');

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
  `project_id` bigint(20) DEFAULT NULL COMMENT '项目ID',
  `commitment_type` char(1) NOT NULL COMMENT '鎵胯?涔︾被鍨?1:浜烘墠鍏?瘬 2:淇濈?鎴?',
  `commitment_content` text NOT NULL COMMENT '鎵胯?涔﹀唴瀹',
  `signature_image` text COMMENT '签名图片(base64)',
  `sign_time` datetime DEFAULT NULL COMMENT '绛剧讲鏃堕棿',
  `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `device_info` varchar(200) DEFAULT NULL COMMENT '设备信息',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:鏈??缃?1:宸茬?缃?',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`commitment_id`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鎵胯?涔﹁〃';

-- ----------------------------
-- Records of hz_commitment
-- ----------------------------
INSERT INTO `hz_commitment` VALUES ('1', '1', '3', '1', '<h2 style=\"text-align: center;\"><strong>人才公寓申请承诺书</strong></h2>\n<p><br></p>\n<p>本人郑重承诺：</p>\n<p><br></p>\n<ol>\n  <li><strong>真实信息承诺</strong><br>本人提供的所有申请材料和信息真实、准确、完整，不存在任何虚假、隐瞒或误导性陈述。如有虚假，愿意承担由此产生的一切法律责任和后果。</li>\n  <li><br></li>\n  <li><strong>资格条件承诺</strong><br>本人符合人才公寓申请条件，包括但不限于：学历要求、工作单位要求、社保缴纳要求、住房情况要求等。如条件发生变化不再符合要求，将主动退出。</li>\n  <li><br></li>\n  <li><strong>用途限制承诺</strong><br>承租的人才公寓仅用于本人及家庭成员居住使用，不将房屋用于经营、办公或其他商业用途，不擅自转租、转借、转让或与他人调换使用。</li>\n  <li><br></li>\n  <li><strong>诚信居住承诺</strong><br>入住后将遵守相关管理规定，按时缴纳租金及各项费用，爱护房屋及配套设施，维护小区公共秩序，做文明租户。</li>\n  <li><br></li>\n  <li><strong>动态管理承诺</strong><br>理解并接受人才公寓实行动态管理，同意配合定期资格复审。如本人或家庭成员住房、收入等情况发生变化不再符合保障条件的，将在规定时间内主动退出。</li>\n  <li><br></li>\n  <li><strong>退出机制承诺</strong><br>租赁期满不再续租或因违规被取消资格的，将在规定时间内搬离并办理退房手续，按要求将房屋及设施归还，结清所有费用。</li>\n</ol>\n<p><br></p>\n<p>本人已充分知晓并理解以上承诺内容，愿意严格遵守。如有违反，自愿承担相应责任。</p>\n<p><br></p>\n<p><br></p>\n<p><strong>承诺人签字：</strong>______________</p>\n<p><strong>承诺日期：</strong>______________</p>', 'data:image/png;base64,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', '2026-01-15 00:44:07', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 18_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.5 Mobile/15E148 Safari/604.1', '0', '', null, '', null, null, '0');
INSERT INTO `hz_commitment` VALUES ('2', '1', '3', '1', '<h2 style=\"text-align: center;\"><strong>人才公寓申请承诺书</strong></h2>\n<p><br></p>\n<p>本人郑重承诺：</p>\n<p><br></p>\n<ol>\n  <li><strong>真实信息承诺</strong><br>本人提供的所有申请材料和信息真实、准确、完整，不存在任何虚假、隐瞒或误导性陈述。如有虚假，愿意承担由此产生的一切法律责任和后果。</li>\n  <li><br></li>\n  <li><strong>资格条件承诺</strong><br>本人符合人才公寓申请条件，包括但不限于：学历要求、工作单位要求、社保缴纳要求、住房情况要求等。如条件发生变化不再符合要求，将主动退出。</li>\n  <li><br></li>\n  <li><strong>用途限制承诺</strong><br>承租的人才公寓仅用于本人及家庭成员居住使用，不将房屋用于经营、办公或其他商业用途，不擅自转租、转借、转让或与他人调换使用。</li>\n  <li><br></li>\n  <li><strong>诚信居住承诺</strong><br>入住后将遵守相关管理规定，按时缴纳租金及各项费用，爱护房屋及配套设施，维护小区公共秩序，做文明租户。</li>\n  <li><br></li>\n  <li><strong>动态管理承诺</strong><br>理解并接受人才公寓实行动态管理，同意配合定期资格复审。如本人或家庭成员住房、收入等情况发生变化不再符合保障条件的，将在规定时间内主动退出。</li>\n  <li><br></li>\n  <li><strong>退出机制承诺</strong><br>租赁期满不再续租或因违规被取消资格的，将在规定时间内搬离并办理退房手续，按要求将房屋及设施归还，结清所有费用。</li>\n</ol>\n<p><br></p>\n<p>本人已充分知晓并理解以上承诺内容，愿意严格遵守。如有违反，自愿承担相应责任。</p>\n<p><br></p>\n<p><br></p>\n<p><strong>承诺人签字：</strong>2026年01月15日</p>\n<p><strong>承诺日期：</strong>2026年01月15日</p>', 'data:image/png;base64,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', '2026-01-15 01:11:46', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 18_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.5 Mobile/15E148 Safari/604.1', '0', '', null, '', null, null, '0');
INSERT INTO `hz_commitment` VALUES ('3', '1', '3', '1', '<h2 style=\"text-align: center;\"><strong>人才公寓申请承诺书</strong></h2>\n<p><br></p>\n<p>本人郑重承诺：</p>\n<p><br></p>\n<ol>\n  <li><strong>真实信息承诺</strong><br>本人提供的所有申请材料和信息真实、准确、完整，不存在任何虚假、隐瞒或误导性陈述。如有虚假，愿意承担由此产生的一切法律责任和后果。</li>\n  <li><br></li>\n  <li><strong>资格条件承诺</strong><br>本人符合人才公寓申请条件，包括但不限于：学历要求、工作单位要求、社保缴纳要求、住房情况要求等。如条件发生变化不再符合要求，将主动退出。</li>\n  <li><br></li>\n  <li><strong>用途限制承诺</strong><br>承租的人才公寓仅用于本人及家庭成员居住使用，不将房屋用于经营、办公或其他商业用途，不擅自转租、转借、转让或与他人调换使用。</li>\n  <li><br></li>\n  <li><strong>诚信居住承诺</strong><br>入住后将遵守相关管理规定，按时缴纳租金及各项费用，爱护房屋及配套设施，维护小区公共秩序，做文明租户。</li>\n  <li><br></li>\n  <li><strong>动态管理承诺</strong><br>理解并接受人才公寓实行动态管理，同意配合定期资格复审。如本人或家庭成员住房、收入等情况发生变化不再符合保障条件的，将在规定时间内主动退出。</li>\n  <li><br></li>\n  <li><strong>退出机制承诺</strong><br>租赁期满不再续租或因违规被取消资格的，将在规定时间内搬离并办理退房手续，按要求将房屋及设施归还，结清所有费用。</li>\n</ol>\n<p><br></p>\n<p>本人已充分知晓并理解以上承诺内容，愿意严格遵守。如有违反，自愿承担相应责任。</p>\n<p><br></p>\n<p><br></p>\n<p><strong>承诺人签字：</strong>______________</p>\n<p><strong>承诺日期：</strong>2026年01月15日</p>', 'data:image/png;base64,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', '2026-01-15 01:14:15', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 18_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.5 Mobile/15E148 Safari/604.1', '0', '', null, '', null, null, '0');
INSERT INTO `hz_commitment` VALUES ('4', '1', '3', '1', '<h2 style=\"text-align: center;\"><strong>人才公寓申请承诺书</strong></h2>\n<p><br></p>\n<p>本人郑重承诺：</p>\n<p><br></p>\n<ol>\n  <li><strong>真实信息承诺</strong><br>本人提供的所有申请材料和信息真实、准确、完整，不存在任何虚假、隐瞒或误导性陈述。如有虚假，愿意承担由此产生的一切法律责任和后果。</li>\n  <li><br></li>\n  <li><strong>资格条件承诺</strong><br>本人符合人才公寓申请条件，包括但不限于：学历要求、工作单位要求、社保缴纳要求、住房情况要求等。如条件发生变化不再符合要求，将主动退出。</li>\n  <li><br></li>\n  <li><strong>用途限制承诺</strong><br>承租的人才公寓仅用于本人及家庭成员居住使用，不将房屋用于经营、办公或其他商业用途，不擅自转租、转借、转让或与他人调换使用。</li>\n  <li><br></li>\n  <li><strong>诚信居住承诺</strong><br>入住后将遵守相关管理规定，按时缴纳租金及各项费用，爱护房屋及配套设施，维护小区公共秩序，做文明租户。</li>\n  <li><br></li>\n  <li><strong>动态管理承诺</strong><br>理解并接受人才公寓实行动态管理，同意配合定期资格复审。如本人或家庭成员住房、收入等情况发生变化不再符合保障条件的，将在规定时间内主动退出。</li>\n  <li><br></li>\n  <li><strong>退出机制承诺</strong><br>租赁期满不再续租或因违规被取消资格的，将在规定时间内搬离并办理退房手续，按要求将房屋及设施归还，结清所有费用。</li>\n</ol>\n<p><br></p>\n<p>本人已充分知晓并理解以上承诺内容，愿意严格遵守。如有违反，自愿承担相应责任。</p>\n<p><br></p>\n<p><br></p>\n<p><strong>承诺人签字：</strong>______________</p>\n<p><strong>承诺日期：</strong>2026年01月15日</p>', 'data:image/png;base64,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', '2026-01-15 01:19:18', '127.0.0.1', 'Mozilla/5.0 (iPhone; CPU iPhone OS 18_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.5 Mobile/15E148 Safari/604.1', '0', '', null, '', null, null, '0');
INSERT INTO `hz_commitment` VALUES ('5', '1', '3', '1', '<h2 style=\"text-align: center;\"><strong>人才公寓申请承诺书</strong></h2>\n<p><br></p>\n<p>本人郑重承诺：</p>\n<p><br></p>\n<ol>\n  <li><strong>真实信息承诺</strong><br>本人提供的所有申请材料和信息真实、准确、完整，不存在任何虚假、隐瞒或误导性陈述。如有虚假，愿意承担由此产生的一切法律责任和后果。</li>\n  <li><br></li>\n  <li><strong>资格条件承诺</strong><br>本人符合人才公寓申请条件，包括但不限于：学历要求、工作单位要求、社保缴纳要求、住房情况要求等。如条件发生变化不再符合要求，将主动退出。</li>\n  <li><br></li>\n  <li><strong>用途限制承诺</strong><br>承租的人才公寓仅用于本人及家庭成员居住使用，不将房屋用于经营、办公或其他商业用途，不擅自转租、转借、转让或与他人调换使用。</li>\n  <li><br></li>\n  <li><strong>诚信居住承诺</strong><br>入住后将遵守相关管理规定，按时缴纳租金及各项费用，爱护房屋及配套设施，维护小区公共秩序，做文明租户。</li>\n  <li><br></li>\n  <li><strong>动态管理承诺</strong><br>理解并接受人才公寓实行动态管理，同意配合定期资格复审。如本人或家庭成员住房、收入等情况发生变化不再符合保障条件的，将在规定时间内主动退出。</li>\n  <li><br></li>\n  <li><strong>退出机制承诺</strong><br>租赁期满不再续租或因违规被取消资格的，将在规定时间内搬离并办理退房手续，按要求将房屋及设施归还，结清所有费用。</li>\n</ol>\n<p><br></p>\n<p>本人已充分知晓并理解以上承诺内容，愿意严格遵守。如有违反，自愿承担相应责任。</p>\n<p><br></p>\n<p><br></p>\n<p><strong>承诺人签字：</strong>______________</p>\n<p><strong>承诺日期：</strong>2026年01月15日</p>', 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAABr0AAAKsCAYAAABRd3ZkAAAQAElEQVR4AezdBbxVVdo/8OcSigKCiN3dPXaPjq1j69jYLdgBYotidyt2i2Mrdjt2d+DY3YoC/3n2+78M1xG9wI1z9vn6mX3vib33Ws93Xa/ve353rdVmuH8IECBAgAABAgQIECBAgACBsguojwABAgQIECBAgEDpBdqEfwgQIFDzAgAIECBAgAABAgQIECBAgACB8guokAABAgTKLiD0KvsIq48AAQIECBAg0BgB5xAgQIAAAQIECBAgQIAAAQLlFyh5hUKvkg+w8ggQIECAAAECBAgQIECgcQLOIkCAAAECBAgQIECgugWEXtU9fnpPoKUEtEOAAAECBAgQIECAAAECBAiUX0CFBAgQIECgqgWEXlU9fDpPgAABAgQItJyAlggQIECAAAECBAgQIECAAIHyC6iwmgWEXtU8evpOgAABAgQIECBAgACBlhTQFgECBAgQIECAAAECBCpYQOhVwYOja9UloLcECBAgQIAAAQIECBAgQIBA+QVUSIAAAQIECFSugNCrcsdGzwgQIECAQLUJ6C8BAgQIECBAgAABAgQIECBQfgEVEqhYAaFXxQ6NjhEgQIAAAQIECBAgUH0CekyAAAECBAgQIECAAAECrSUg9Got+VpsV80ECBAgQIAAAQIECBAgQIBA+QVUSIAAAQIECBBoJQGhVyvBa5YAAQIEalNA1QQIECBAgAABAgQIECBAgED5BVRIgEDrCAi9WsddqwQIECBAgAABAgRqVUDdBAgQIECAAAECBAgQIECgWQSEXs3COqY3dR0BAgQIECBAgAABAgQIECBQfgEVEiBAgAABAgQINIeA0Ks5VN2TAAECBMZcwJUECBAgQIAAAQIECBAgQIBA+QVUSIAAgWYQEHo1A6pbEiBAgAABAgQIEBgbAdcSIECAAAECBAgQIECAAAECoy9QbaHX6FfoCgIECBAgQIAAAQIECBAgQKDaBPSXAAECBAgQIECAwGgLCL1Gm8wFBAgQaG0B7RMgQIAAAQIECBAgQIAAAQLlF1AhAQIECIyugNBrdMWcT4AAAQIECBAg0PoCekCAAAECBAgQIECAAAECBAiUX2A0KxR6jSaY0wkQIECAAAECBAgQIECAQCUI6AMBAgQIECBAgAABAg0FhF4NPTwjQKAcAqogQIAAAQIECBAgQIAAAQIEyi+gQgIECBAg0EBA6NWAwxMCBAgQIECAQFkE1EGAAAECBAgQIECAAAECBAiUX0CFIwsIvUbW8JgAAQIECBAgQIAAAQIEyiOgEgIECBAgQIAAAQIEakpA6FVTw61YAv8V8IgAAQIECBAgQIAAAQIECBAov4AKCRAgQIBALQkIvWpptNVKgAABAgQIjCzgMQECBAgQIECAAAECBAgQIFB+ARXWkIDQq4YGW6kECBAgQIAAAQIECBBoKOAZAQIECBAgQIAAAQIEyiMg9CrPWKqkqQXcjwABAgQIECBAgAABAgQIECi/gAoJECBAgACB0ggIvUozlAohQIAAAQJNL+COBAgQIECAAAECBAgQIECAQPkFVEigLAJCr7KMpDoIECBAgAABAgQIEGgOAfckQIAAAQIECBAgQIAAgSoREHpVyUBVZjf1igABAgQIECBAgAABAgQIECi/gAoJECBAgAABAtUhIPSqjnHSSwIECBCoVAH9IkCAAAECBAgQIECAAAECBMovoEICBKpCQOhVFcOkkwQIECBAgAABAgQqV0DPCBAgQIAAAQIECBAgQIBAJQgIvZp3FNydAAECBAgQIECAAAECBAgQKL+ACgkQIECAAAECBCpAQOhVAYOgCwQIECi3gOoIECBAgAABAgQIECBAgACB8guokAABAq0vIPRq/THQAwIECBAgQIAAgbILqI8AAQIECBAgQIAAAQIECBBodoFWD72avUINECBAgAABAgQIECBAgAABAq0uoAMECBAgQIAAAQIEmltA6NXcwu5PgACBPxdwBgECBAgQIECAAAECBAgQIFB+ARUSIECAQDMLCL2aGdjtCRAgQIAAAQIEGiPgHAIECBAgQIAAAQIECBAgQKD8As1bodCreX3dnQABAgQIECBAgAABAgQINE7AWQQIECBAgAABAgQIjJWA0Gus+FxMgEBLCWiHAAECBAgQIECAAAECBAgQKL+ACgkQIECAwNgICL3GRs+1BAgQIECAAIGWE9ASAQIECBAgQIAAAQIECBAgUH4BFY6FgNBrLPBcSoAAAQIECBAgQIAAAQItKaAtAgQIECBAgAABAgQIjFpA6DVqG+8QqC4BvSVAgAABAgQIECBAgAABAgTKL6BCAgQIECBAYJQCQq9R0niDAAECBAgQqDYB/SVAgAABAgQIECBAgAABAgTKL6BCAqMSEHqNSsbrBAgQIECAAAECBAgQqD4BPSZAgAABAgQIECBAgEDNCgi9anboa7FwNRMgQIAAAQIECBAgQIAAAQLlF1AhAQIECBAgUKsCQq9aHXl1EyBAgEBtCqiaAAECBAgQIECAAAECBAgQKL+ACgnUqIDQq0YHXtkECBAgQIAAAQIEalVA3QQIECBAgAABAgQIECBQTgGhVznHdUyrch0BAgQIECBAgAABAgQIECBQfgEVEiBAgAABAgRKKSD0KuWwKooAAQIExlzAlQQIECBAgAABAgQIECBAgED5BVRIgEAZBYReZRxVNREgQIAAAQIECBAYGwHXEiBAgAABAgQIECBAgACBKhQQeo3moDmdAAECBAgQIECAAAECBAgQKL+ACgkQIECAAAECBKpPQOhVfWOmxwQIEGhtAe0TIECAAAECBAgQIECAAAEC5RdQIQECBKpOQOhVdUOmwwQIECBAgAABAq0voAcECBAgQIAAAQIECBAgQIBApQk0fehVaRXqDwECBAgQIECAAAECBAgQIND0Au5IgAABAgQIECBAoMIEhF4VNiC6Q4BAOQRUQYAAAQIECBAgQIAAAQIECJRfQIUECBAgUFkCQq/KGg+9IUCAAAECBAiURUAdBAgQIECAAAECBAgQIECAQPkFKqpCoVdFDYfOECBAgAABAgQIECBAgEB5BFRCgAABAgQIECBAgEBLCgi9WlJbWwQI/FfAIwIECBAgQIAAAQIECBAgQKD8AiokQIAAAQItKCD0akFsTREgQIAAAQIERhbwmAABAgQIECBAgAABAgQIECi/gApbTkDo1XLWWiJAgAABAgQIECBAgACBhgKeESBAgAABAgQIECBAoMkEhF5NRulGBJpawP0IECBAgAABAgQIECBAgACB8guokAABAgQIEGgqAaFXU0m6DwECBAgQIND0Au5IgAABAgQIECBAgAABAgQIlF9AhQSaSEDo1USQbkOAAAECBAgQIECAAIHmEHBPAgQIECBAgAABAgQIEGicgNCrcU7OqkwBvSJAgAABAgQIECBAgAABAgTKL6BCAgQIECBAgECjBIRejWJyEgECBAgQqFQB/SJAgAABAgQIECBAgAABAgTKL6BCAgQaIyD0aoyScwgQIECAAAECBAgQqFwBPSNAgAABAgQIECBAgAABAv8REHr9B6HM/1MbAQIECBAgQIAAAQIECBAgUH4BFRIgQIAAAQIECEQIvfwUECBAgEDZBdRHgAABAgQIECBAgAABAgQIlF9AhQQIEBB6+RkgQIAAAQIECBAgUH4BFRIgQIAAAQIECBAgQIAAgfILmOlV/jFWIQECBAgQIECAAAECBAgQIECAAAECBAgQIECg9AJCr9IPsQIJECDw5wLOIECAAAECBAgQIECAAAECBMovoEICBAiUXUDoVfYRVh8BAgQIECBAgEBjBJxDgAABAgQIECBAgAABAgQIVLlAI0KvKq9Q9wkQIECAAAECBAgQIECAAIFGCDiFAAECBAgQIECAQHULCL2qe/z0ngCBlhLQDgECBAgQIECAAAECBAgQIFB+ARUSIECAQFULCL2qevh0ngABAgQIECDQcgJaIkCAAAECBAgQIECAAAECBMovUM0VCr2qefT0nQABAgQIECBAgAABAgRaUkBbBAgQIECAAAECBAhUsIDQq4IHR9cIVJeA3hIgQIAAAQIECBAgQIAAAQLlF1AhAQIECBCoXAGhV+WOjZ4RIECAAAEC1SagvwQIECBAgAABAgQIECBAgED5BVRYsQJCr4odGh0jQIAAAQIECBAgQIBA9QnoMQECBAgQIECAAAECBFpLQOjVWvLarUUBNRMgQIAAAQIECBAgQIAAAQLlF1AhAQIECBAg0EoCQq9WgtcsAQIECBCoTQFVEyBAgAABAgQIECBAgAABAuUXUCGB1hEQerWOu1YJECBAgAABAgQIEKhVAXUTIECAAAECBAgQIECAQLMICL2ahdVNx1TAdQQIECBAgAABAgQIECBAgED5BVRIgAABAgQIEGgOAaFXc6i6JwECBAgQGHMBVxIgQIAAAQIECBAgQIAAAQLlF1AhAQLNICD0agZUtyRAgAABAgQIECBAYGwEXEuAAAECBAgQIECAAAECBEZfQOg1+mate4XWCRAgQIAAAQIECBAgQIAAgfILqJAAAQIECBAgQGC0BYReo03mAgIECBBobQHtEyBAgAABAgQIECBAgAABAuUXUCEBAgRGV0DoNbpizidAgAABAgQIECDQ+gJ6QIAAAQIECBAgQIAAAQIECPxGoISh128q9JQAAQIECBAgQIAAAQIECBAooYCSCBAgQIAAAQIECDQUEHo19PCMAAEC5RBQBQECBAgQIECAAAECBAgQIFB+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AAECBAgQIECAAAECBAgQKL+ACgkQIECAAAECEUIvPwUECBAgQKDsAuojQIAAAQIECBAgQIAAAQIEyi+gQgIEhF5+BggQIECAAAECBAgQKL+ACgkQIECAAAECBAgQIECg/AJmepV/jP+sQu8TIECAAAECBAgQIECAAAEC5RdQIQECBAgQIECg9AJCr9IPsQIJECBA4M8FnEGAAAECBAgQIECAAAECBAiUX0CFBAiUXUDoVfYRVh8BAgQIECBAgACBxgg4hwABAgQIECBAgAABAgQIVLmA0KsRA+gUAgQIECBAgAABAgQIECBAoPwCKiRAgAABAgQIEKhuAaFXdY+f3hMgQKClBLRDgAABAgQIECBAgAABAgQIlF9AhQQIEKhqAaFXVQ+fzhMgQIAAAQIECLScgJYIECBAgAABAgQIECBAgACBShZomtCrkivUNwIECBAgQIAAAQIECBAgQKBpBNyFAAECBAgQIECAQAULCL0qeHB0jQCB6hLQWwIECBAgQIAAAQIECBAgQKD8AiokQIAAgcoVEHpV7tjoGQECBAgQIECg2gT0lwABAgQIECBAgAABAgQIECi/QMVWKPSq2KHRMQIECBAgQIAAAQIECBCoPgE9JkCAAAECBAgQIECgtQSEXq0lr10CtSigZgIECFS5wPvvvx9HHXVUPPLII1Veie4TIECAAAECBAgQaEYBtyZAgAABAq0kIPRqJXjNEiBAgAABAtUlcMYZZ8Qcc8wRBxxwQCy55JKx2GKLFeHXr7/+OlqFOJkAAQIECBAgQIAAAQIECBAov4AKW0dA6NU67lolQIAAAQIEqkjg559/jgEDBsQ333xT9HrYsGHx6KOPxqabbhrXX399DB06tHjdFwIECBBolICTCBAgQIAAAQIECBAg0CwCQq9mYXVTAmMq4DoCBAgQqESBwYMHxw8//PA/XXv77bfj7LPPjhdeeCHM+PofHi8QIECAAAECBAiMUsAbBAgQIECAQHMICL2aQ9U9CRAgQIAAgTEXqMArJ5tssphzzjmjffv2DXo3fPjweOCBB+Lwww+P559/XvDVQMcTAgQIECBAgAABAgQIECDwBwLeItAMAkKvZkB1SwIECBAgQKBcAp07d45evXrFzjvvHB07dmxQXC59eNNNN8Whhx4ar732WoP3PCFAgMCYCriOAAECBAgQIECAAAECBEZfQOg1+mauaF0BrRMgQIAAgVYRWHjhheOwww6L448/PhZffPEGffjpp5/ixhtvjCuvvDK++uqrBu95QoAAAQIECBAgMEYCLiJAgAABAgQIjLaA0Gu0yVxAgAABAgRaW0D7rSXQqVOn2G677eKYY46JFVZYoUE3hg4dGhdccEFcdtllv7v/V4OTPSFAgAABAgQIECBAgAABAn8q4AQCBEZXQOg1umLOJ0CAAAECBGpeYNFFFy328VpqqaUaWLz33ntxxhlnxBtvvBG531eDNz0hQKBpBdyNAAECBAgQIECAAAECBAj8RkDo9RuQMjxVAwECBAgQINC8Am3bto0FF1wwjjrqqFhiiSUaNPbyyy/H1ltvHYMHD27wuicECBAgQIAAgaYWcD8CBAgQIECAAIGGAkKvhh6eESBAgEA5BFRBoNkF2rVrF4sttlgMHDgwDj744OjSpUvRZi5z+OSTT8aKK64Y//73v4vXfCFAgAABAgQIECBAgACBZhFwUwIECDQQEHo14PCEAAECBAgQINB4gTZt2kT37t2jV69eccABB8QUU0wRdXV1xdKGr732Wqyyyirx5ptvNv6GziTQpAJuRoAAAQIECBAgQIAAAQIEakugNkOv2hpj1RIgQIAAAQLNLDDBBBPE9ttvH7vssktMOumkRfCVTb700kvRo0eP+Pjjj/OpgwABAgQIEGhpAe0RIECAAAECBAjUlIDQq6aGW7EECBD4r4BHBAg0rUAub7jjjjvGxhtvHF27di1uPmzYsHjsscdi1113jQ8++KB4zRcCBAgQIECAAAECBAi0pIC2CBAgUEsCQq9aGm21EiBAgAABAs0qkGFXnz59Yuutt45OnToVbQ0ZMiQGDRoUJ510khlfhUhFfdEZAgQIECBAgAABAgQIECBAoEQCowi9SlShUggQIECAAAECLSiQwVfv3r1jiy22iHHHHbdo+csvv4yrrroqrr/++vjuu++K13whQIAAAQKVIaAXBAgQIECAAAECBMojIPQqz1iqhACBphZwPwIECIyhQC51eMABB8See+4Zud9X3ub999+P8847L+6+++7I2V/5moMAAQIECBAgQIAAgQoQ0AUCBAgQKI2A0Ks0Q6kQAgQIECBAoJIEpphiiiL02nfffSNDsF9++SWefvrpOP300+PVV1+tpK7+YV+8SYAAAQIECBAgQIAAAQIECJRfoCwVCr3KMpLqIECAAAECBCpOYMIJJ4ztttsu9thjjxhnnHFi6NChcf/998f5558fn3zyScX1V4cIECBA4HcFvEiAAAECBAgQIECAQJUICL2qZKB0k0BlCugVAQIECPyRQF1dXUw00USx0047FcFXnvvjjz8WyxwOGDAgvv/++3zJQYAAAQIECBAgQKDCBXSPAAECBAhUh4DQqzrGSS8JECBAgACBShX4k37V1dVF9+7dY4sttoi11lqrOPvbb7+NU089Ne64444YNmxY8ZovBAgQIECAAAECBAgQIECAQAUL6FpVCAi9qmKYdJIAAQIECBCodoHZZpst9t5771h88cWjrq4uBg8eHEceeWT89NNP1V6a/hMgQCAQECBAgAABAgQIECBAoBIEhF6VMAr6UGYBtREgQIAAgRECc889d2y88cYx5ZRTRv7zxBNPxCmnnJIPHQQIECBAgAABAtUtoPcECBAgQIBABQgIvSpgEHSBAAECBAiUW0B19QKdO3eOzTbbLP72t7/FuOOOW7x8wgknxOuvv1489oUAAQIECBAgQIAAAQIECFSvgJ4TaH0BoVfrj4EeECBAgAABAjUkMMEEExT7e80yyyzFMocff/xx9OrVK3788ccaUlAqgRoUUDIBAgQIECBAgAABAgQINLuA0KvZiTXwZwLeJ0CAAAECtSaw4IILFssc5syvrP2+++6Le+65Jx86CBAgQIAAAQKlFVAYAQIECBAgQKC5BYRezS3s/gQIECBA4M8FnFFjAp06dYqtt946pp566qLynOV15plnxrfffls894UAAQIECBAgQIAAAQIESimgKAIEmllA6NXMwG5PgAABAgQIEPg9gYknnjh22mmnaNeuXQwdOjQeffTRuPrqq3/vVK8RqBEBZRIgQIAAAQIECBAgQIAAgbETEHqNnV/LXK0VAgQIECBAoJQCm266aay88spFbV988UVcd9118fXXXxfPfSFAgAABAgRqUEDJBAgQIECAAAECYyUg9BorPhcTIECAQEsJaIdAGQUmmGCCOOSQQ2KSSSYpZns9/vjjcf7555exVDURIECAAAECBAgQIECgUQJOIkCAwNgICL3GRs+1BAgQIECAAIGxFJhvvvli2WWXLe7y2WefxaBBg+Kdd94pnvtC4DcCnhIgQIAAAQIECBAgQIAAAQJ/IFCS0OsPKvQWAQIECBAgQKCCBdq0aRO9e/eOtm3bxvDhw+OJJ56IW2+9tXhcwd3WNQIECBAg0EoCmiVAgAABAgQIECAwagGh16htvEOAAIHqEtBbAgSqVmDGGWeM3N8rC/jkk0/izjvvjLfeeiufOggQIECAAAECBAgQINBQwDMCBAgQGKWA0GuUNN4gQIAAAQIECLSMQIcOHaSaMNEAABAASURBVGKbbbaJCSecsGjwscceizyGDh1aPPel8QLOJECAAAECBAgQIECAAAECBMovMKoKhV6jkvE6AQIECBAgQKCFBHKJw9lmmy3WX3/9osWvvvoqnnnmmfjiiy+K574QIECAAIHREHAqAQIECBAgQIAAgZoVaFOzlSucAIEaFFAyAQIEKlcgZ3mtueaa0a1bt/jhhx/igQceiMGDB1duh/WMAAECBAgQIECAQMUK6BgBAgQI1KqA0KtWR17dBAgQIECAQEUJtG3bNqabbrqYa665in698sor8dxzz8VPP/1UPG+yL25EgAABAgQIECBAgAABAgQIlF+gRisUetXowCubAAECBAgQqDyBySefPBZddNGiY7nE4SOPPBL5vXjBFwIECBBoMgE3IkCAAAECBAgQIECgnAJCr3KOq6oIjKmA6wgQIECgFQW6du0aCy+8cEw66aRFLx566KH49NNPi8e+ECBAgAABAgQIEGhCAbciQIAAAQKlFBB6lXJYFUWAAAECBAiMuUDrXdmmTZuYcsopI5c5zF589tlnMWTIkHzoIECAAAECBAgQIECAAAECBJpUwM3KKCD0KuOoqokAAQIECBCoWoHZZ589llxyyaL/Ocvr8ccft69XoeELAQItKqAxAgQIECBAgAABAgQIVKGA0KsKB02XW1dA6wQIECBAoDkFunTpEn/5y19iqqmmiuHDh0eGXj/++GNzNuneBAgQIECAAAECvyPgJQIECBAgQKD6BIRe1TdmekyAAAECBFpbQPvNLJDLG0477bRFKzfffHN89dVXxWNfCBAgQIAAAQIECBAgQIBACwpoikDVCQi9qm7IdJgAAQIECBAou8A000xT7O1VV1cXucThW2+9Vcz6Knvd6iNQXQJ6S4AAAQIECBAgQIAAAQKVJiD0qrQRKUN/1ECAAAECBAiMlUDXrl0jg68OHToU93nooYdi6NChxWNfCBAgQIAAAQIVI6AjBAgQIECAAIEKExB6VdiA6A4BAgQIlENAFQTGRmD88cePhRdeOKaYYoriNldddVUMGzaseOwLAQIECBAgQIAAAQIECFSOgJ4QIFBZAkKvyhoPvSFAgAABAgQIFAILLLBATD311MXjwYMHx5tvvlk89oVAFQnoKgECBAgQIECAAAECBAgQaFEBoVeLctc35jsBAgQIECBA4I8FpppqqphzzjljvPHGi2+//TYGDRr0xxd4lwABAgQIEKhAAV0iQIAAAQIECBBoSQGhV0tqa4sAAQIE/ivgEQECfygw7rjjRvfu3WOcccYpzhN6FQy+ECBAgAABAgQIECBQbQL6S4AAgRYUEHq1ILamCBAgQIAAAQKjI7DSSivFZJNNVlzy4osvFt99KZeAaggQIECAAAECBAgQIECAAIGmE6jU0KvpKnQnAgQIECBAgECVCswwwwzRqVOnovdvv/12PProo8VjXwgQIECAQIkElEKAAAECBAgQIECgyQSEXk1G6UYECBBoagH3I0Cg1gUmnXTSmH766aNdu3YxbNiweOONN2qdRP0ECBAgQIAAAQIESiigJAIECBBoKgGhV1NJug8BAgQIECBAoBkEcrZX/b5el112WTO0UOG31D0CBAgQIECAAAECBAgQIECg/AJNVKHQq4kg3YYAAQIECBAg0BwCq666aoMlDocMGdIczbgnAQIECFSwgK4RIECAAAECBAgQINA4AaFX45ycRYBAZQroFQECBEovMO+880aXLl2KOj/88MMYPHhw8dgXAgQIECBAgAABAjUkoFQCBAgQINAoAaFXo5icRIAAAQIECBBoHYGuXbvGrLPOGnV1dTF06NB47733ftMRTwkQIECAAAECBAgQIECAAIHyC6iwMQJCr8YoOYcAAQIECBAg0IoCyy23XLRt27YIvV566aVW7ImmCRAgUKECukWAAAECBAgQIECAAIH/CAi9/oPgfwTKLKA2AgQIEKh+gfnmmy/atGkTv/zySzz00EPVX5AKCBAgQIAAAQIEmlzADQkQIECAAIEIoZefAgIECBAgQKDsAlVf34ILLhiTTTZZMdPrxRdfjC+//LLqa1IAAQIECBAgQIAAAQIECBBoYgG3IyD08jNAgAABAgQIEKh0gS5dusSSSy4Zw4cPj2+//TY++OCDSu+y/hEgUHECOkSAAAECBAgQIECAAIHyC5jpVf4xVuGfCXifAAECBAhUgcAKK6xQ9HLIkCHx4YcfFo99IUCAAAECBAgQGA0BpxIgQIAAAQKlFxB6lX6IFUiAAAECBP5cwBmVL9CjR4+oq6uL77//Pp5++unK77AeEiBAgAABAgQIECBAgEDFCegQgbILCL3KPsLqI0CAAAECBEojMOOMM0bO9Hr//fdLU5NCCFSQgK4QIECAAAECBAgQIECAQJULCL2qfABbpvtaIUCAAAECBCpBYLnllosff/wxXnnlleJ7JfRJHwgQIECAAIEyCaiFAAECBAgQIFDdAkKv6h4/vSdAgACBlhLQDoEKENhggw1i+PDhxZ5ezz//fAX0SBcIECBAgACB3wrkf6t/+OGHGPn48ssv49NPPx1xvPvuu1F/vPnmm/Hiiy/Gyy+/3OB48MEH44knnij+2OW9996Lb7755rdNeU6AAAECzSHgngQIVLWA0Kuqh0/nCRAgQIAAgVoSmH766Ytyv/7663jjjTeKx74QaEkBbREgQKAsArlHZoZIeeSywXm88847ReiUe2c+9thj8fjjj4847rrrrsjjjjvuiOuvv37Ecfnll8dFF11UHKeddlqceuqpccopp8Sxxx7b4DjqqKPi8MMPH3H07t076o/9998/9tprr/85tt1229hll11i7733joMOOiiOO+64eOCBB+Lnn38uyzCogwABAgQIECDQ5AJCr6YhdRcCBAgQIECAQLMLTDTRRDHVVFMVfzn+ySefNHt7GiBAgAABAq0lkMFOzo7KI2c5vfXWW5HHU089FXlkIHX33XdH/XHjjTfGtddeWxznnXdenHvuuXH22WdH//79iyMDpwMPPDDqjwya6o9999036o999tmnCJ/23HPP4nt9GLXHHntEr1698oiePXuOOHbffffYbbfdimPXXXeNPPK1vn37xshH9uPkk0+O+uOSSy6J+uOaa66JDNNuueWWGPnI5YwzfLvpppviwgsvjAzOTjjhhMhwrrXGRbsECBAgQIAAgUoXEHpV+gjpHwECBKpGQEcJEGhugXbt2sUcc8xR7Ocl9GpubfcnQIAAgaYQyKX+Pvvss2Jp3meffTYGDRoUAwcOLI6cIZUB1THHHBP9+vWLDJq22mqryGObbbaJHXbYIbbbbrvYcccdY+eddy6ODJTy6NmzZ3F+XpNHhlb77bdf5NGnT5/II2dHHXrooZFHhl5HHnlk1B85G+v000+PPC699NLI46qrrooMmDJIe+ihh4pZVbmccC47+MEHH8Qvv/wSw4YNi65du8Y000xTHLPOOmssscQSseyyy8aaa64Zf//732Prrbcujux39qv+OOKII4oALgOwPM4///yoP3KWWDrUHwcccEBkKJfXbr755jHffPMV7Wd/XnrppaYYGvcgQIDAWAi4lAABApUrIPSq3LHRMwIECBAgQIBAA4E2bdqMmOn16quvFh8gNjjBk9YX0AMCBAjUkMDQoUPj448/LvameuSRR+K6666Liy++OA455JDIGVNbbrllrLzyyrHuuutG7kuZYVbOhKqfPZXL+2UwdfTRR0eGPRlAXXDBBZFHzoLKECpnQd18881x2223FUfuc5VHtpczoT788MPiv4dffPFF5B+HjD/++DHxxBPH3HPPHfPMM08ss8wysdZaa8X6668fm266aRGcZUiWR327uWzggAEDIo+rr766aKe+vYH/CejqZ5BlSJdH9u+MM86I+iNnX9UvZ5j3yvozaMva0qH+yLCuPrzL7z169Ig8tvyPU4Z8uZRh/ZFLGuaR12b4Nfvss0f+8+uvv0Ye+dhBgAABAgQIECDwvwItFnr9b9NeIUCAAAECBAgQGB2B9u3bx1/+8pfir8y/++67YsbX6FzvXAIECBAgMLoC+d+bnOWUQVPOSsqgJ/eayjBr/vnnL2Y4rbjiirHxxhuP2H/q+OOPL2ZQ5X5XuWzf/fffH3l9LkuYQdWnn35a/DcsQ7NJJpmkmCmV4VSGPzvssEOxhGAGYhlK5UywPHL5wltvvTUyjMoZY3n861//invvvXfEkefU77eV1+Rx1llnNdhbKwOp+iUOd9ppp2IW2fbbbx8bbbRRcay99tqx0korjTiyX0svvXTksdBCC0UeCyywQMw111wjjllmmSVmmmmm4phxxhljyimnjCmmmCImm2yy6Nat24ijY8eOMd544404Rh6LDOxGfq9r164x4YQTFkfuP5Z15/kZfs0777z50EGAAAECBAgQIPA7AkKv30HxEgECBJpJwG0JECAwVgJt27aNGWaYobhHLm/47rvvFo99IUCAAAECYyOQ/00ZPHhw5MyqnHGVwVCGWrPNNltMN910Rbiz2mqrFftW5SytnBF15513Ri61lyHWa6+9Fu+8804RZI0zzjgx88wzxyqrrFLsb5Uzlc4888zIwCrPzcAr9+Z64YUXIo97/xNa5dKCOUMsZ0nlkcsAZjCVM6M222yzyCPv97e//S3yyBlceWTwlH2sPzJ4yv9O5jH11FNHHhlATTrppFF/5P6Y9WFSp06dIo8Mo7LfeeR/a8fGsqmvzWAwlzjMvc1yX8+cNZd1NnU77keAQJMLuCEBAgQItJKA0KuV4DVLgAABAgQIEBhdgVzeMD/Iy78c//bbbyP3SBnde7T++XpAgAABAi0hkHtp5f5TGZrkcng///xzvPHGG/HEE0/EySefHLn0Xi73Vx8GTTvttLHhhhtG7omVoVfO0Hr99dfjq6++im+++SZ++OGHGDJkSOTMrKWWWir69u1b7I915ZVXFvfM5QUzmMnwLNvIZQJzyb+crZUzqTJEy/2vunfvHiMHT126dInOnTsX4VMuTZhHznjq0KFDjDvuuJGznPPIMCqP/G9hS/hVQhs5dn/961/jrrvuivRYY401ir3CasmgEsZBHwgQIECAAIExFWid69q0TrNaJUCAAAECBAgQGBOB/OAvP6DM0Cs/YByTe7iGAAECBFpZoImazzArj1yCMAOn3N/qvffei1z676abboqDDz44cmZWLtGXoUnOwMrl+XIGVc6myr2qfvrpp8jgKcOsaaaZJhZffPFiKb/cYypDq/pZWh999FERer399ttFCJOhWe41lXt1LbjggsUyfE1Ultv8RyADxjR+4IEHIkOuNO7Zs+d/3vE/AgQIECBAgACBPxIQev2RjvcIEGhxAQ0SIECAwB8L1NXVFX/1nn9x//XXX//xyd4lQIAAgVII5IyfnKmVv/dzf61cTvDxxx+PDLYGDhwYGWDtuOOOsfzyy8ecc84Z882fdO7tAAAQAElEQVQ3X6y55ppx2GGHFXtr5TKCuTRehlpzzz13LLroorHccstFBluHH354sedV3icDrdx7K5ccPPvss4u9tfKcXFIwZ2eVArMKivjll1/iuuuuK2bkZXenm266yGUnc++wfO4gUBYBdRAgQIAAgeYQEHo1h6p7EiBAgAABAgTGXOAPr8z9RiaffPLIv+zPvwL/w5O9SYAAAQJVJ/Djjz8WSwm+/PLLxZ5Xd999d+QSghdeeGGccMIJkbOrco+rXGJw7bXXjvXXX78IRG655ZbIWcAZbv3lL3+JFVZYIf7+97/H5ptvXuzFlbO2TjrppLjhhhvikUceiUGDBsUpp5xS7LuVwdZiiy1WzCiqOrCSdTj/2/7www8Xy0zmDL6c3b3zzjsXM/BKVqpyCBAgQIAAgQgGzSAg9GoGVLckQIAAAQIECDSXQH3olctR5fKG+eFYc7XlvgQIEGg9gfK3nH+8kL/HX3311fjXv/4Vt99+e5x77rlx/PHHx3HHHVcsS9irV6/YYostYpNNNokddtghcq+t3Cvr3XffLWZ05VKF66yzTmQosvfeexfv9+/fv5jdddFFF0Wee8455xQzvnK/rrXWWiumn3768uNWcYWPPfZYsVdaLifZqVOnOPDAA2OPPfao4op0nQABAgQIECDQsgJCr5b11hqBsRdwBwIECBCoaYEMvaaYYorC4Kuvvopc6qp44gsBAgQIVKxAzsDKoOree+8t9sPKfbJ69+5dzNraZ599RgRcu+yyS+TruZfTrbfeGk8//XR07ty5WIowZ3VlsJVLGfbr169YkjBnb5144onFMnh9+vSJrbbaKlZbbbXIMCxnBec+kBWLomP/I/Dkk08Wgef9998fuZxlzujbdddd/+c8L9SQgFIJECBAgACB0RYQeo02mQsIECBAgACB1hao5fZHDr2+/PJLoVct/zConQCBihPIvbc+++yzyKUJ//nPf0bOsurZs2dst912xWysPffcswi4MqA6+uijI/fNyvMy7Pj000+LJexWXHHFyCDsvPPOi7POOqtYgjBnfmXQ1bdv32KfrS233DL++te/xtJLLx1TTz11xTno0OgL5H5qp512Wtxxxx2Rs7l79OgRGXKO/p1cQYAAAQIEyiWgGgKjKyD0Gl0x5xMgQIAAAQIEWlFg2LBh8csvvxT7ruQHpPnhait2R9MECLSegJZbWSBn2n788cdx3XXXxaWXXloEFDnLKvfYylAqA66DDz44zjjjjLjiiivi5ptvjqeeeiqeffbZGH/88YsZWTmz6/zzz4+BAwcWSxHmHlu579a+++4bGXqsu+66sfzyy8f8888fs8wyS3Ts2LGVq9Z8cwh88MEHxc9J/hzknm45Y++AAw6Iaaedtjmac08CBAgQIECAQKkFhF6lHt5aLU7dBAgQIECgvALjjjtu8Vf9GX7lclnfffddeYtVGQECBCpAIH/fZiiRYVUuS5hB1korrRRLLLFEsexgLj+XAVeGW7kvVy5h+Pjjj8cbb7wR33//fcw888yx6aabRi5ZmDO/nnnmmWI2T/29cr+uFVZYIZZddtlin67ZZpstunXrVgGV60JLCOR/x6+55ppi1l/O4M6fg9yjbYYZZij+wKUl+lDdbeg9AQIECBAgQKChgNCroYdnBAgQIECgHAKqKK1AmzZtomvXrsUHosOHD488SluswggQINDCAhk6vPfee3HLLbdE7pWVM61yb6x55pmnCLj22muvyGUJBw0aFC+++GKxjGEGYjnjq3v37pHhVS5Jd8wxx0R98PXAAw8Us3j222+/2HzzzSPvNeuss8ZUU00VE000UeSytS1cpuYqRODXX3+NDElzVlfOHJx00kljhx12iHnnnVfgVSFjpBsECBCoCgGdJECggYDQqwGHJwQIECBAgACByheoq6uLtm3bhn8IEPhjAe8SqBfIPxDII2dt5b5bGTa8+eab8dxzz0Xuo5RBQ86syRlW00wzTbH0YK9evYqlCz/55JPIMCxDidxrKZcYnH766SOXoDv00EPjtttui7znO++8E3feeWdk4JXB10ILLRQZhE044YTRqVOnGG+88QRc9QPie/FHKxmMbrvttsWMwNyb7fjjj48NN9zQf+P9fBAgQIAAAQIExkJA6DUWeFV8qa4TIECAAAECVSyQe8FMMskkkR/AfvPNN1Vcia4TIECg6QUy0Mol47766qvIGVivvPJKPProozFgwIA48MADi9lYuVTsTDPNVMyoyX21zjrrrHj33XeLUCoDqgkmmCAmnnjiYjnZpZZaqtivK5cmzHvlXoq5dOF5550Xffr0iVzqMGfhNn0l7lhmgQ8//DByacz8Gc2gNZfN3HjjjZujZPckQIAAAQIECNSUgNCrpoZbsQQIECDwXwGPCFSvQH5Ym7MRcmZBHtVbiZ4TIEBgzAXy91+G/xkevPbaa5F7bj322GMxcODAOPLII4tl4pZbbrlYeOGFY/HFF48ePXpEv379iuXk2rdvHx06dIgpp5wypp122mLJwWWXXTY22mij6Nu3bxx33HFxww03RM7euv/++4vrcmZXBmVj3mNXEvg/gfyZXX311ePpp58ulrjMn838+fq/d30lQIAAgaYXcEcCBGpJQOhVS6OtVgIECBAgQKAUArm0Ye4BkzMZvv/++1LUpIhWEtAsgSoR+OGHHyKXGcx9tHIPpJtuuimuvPLKYt+t/fffPzbbbLNYZZVVYrHFFov1118/jjrqqOL9l19+OXJJwpy1NfPMM8ciiywSa6yxRrG3VoYMGW6deuqpcfPNN8ddd91VzAbLfbu22Wab4l5mcFXJD0gVdTOXyswZghl45ZKXObsrfxarqARdJUCAAAECBAhUtIDQaxTD42UCBAgQIECAQKUK5IdkubzhkCFD4pdffqnUbuoXAQIExkggf7f9+9//jmeeeSZuvfXWyGUFTzjhhDjiiCOiZ8+escUWW8R6660Xm2yySeSScLlsYe6NlLNncmnCqaaaqgi3/va3vxUzt3L5wt69e8fRRx8dZ599dlx77bXF99zLK/dPyhk3ec0YddZFpRBoqSI+//zzOOOMM+LSSy+Nurq62GCDDSJD1lzesKX6oB0CBAgQIECAQNkFhF5lH2H1ESBAYMwFXEmAQIUK1IdeP/74Y+RRod3ULQIECDRKIPfIuvvuu+OWW26Jk08+OTKg2m+//YowIEOunXbaqXgt3xs0aFAMHjw4cmnDnL214IILxnLLLRc5W+aggw4qgrFcwvDYY4+NDMpOOeWUOP7442O33XaLtddeu1jGMJc2bFTHnESgCQVytmLuHde/f/9i9mHOJjz88MOLfeOasBm3IkCAwJgKuI4AAQKlERB6lWYoFUKAAAECBAjUikB+YDv++OMX5ebyhj///HPx2JfmEHBPAgSaSiBD+gy4HnroofjnP/8ZJ554Ymy77bax4447xt577x377LNPsZ9WhgI5EyaXG8y9un799ddi36M55pijmOGVe27ljK3TTz+9uEcGXLmHV4Zeu+66azEDbMkll4w555wzunfvHv4hUAkCt99+e5x//vnx1VdfxcorrxyHHHJIsadcJfRNHwgQIECAAAECZRIY89CrTApqIUCAAAECBAhUkUDu5zXBBBMUPc7H7dq1Kx77QoAAgUoSyOUGc9nBDLBy36ztt9++2G8rZ27lkm65XOG5554b11xzTTz11FOR+3VlINCxY8eYaaaZIpcdzCBr4MCBcfXVV8cFF1xQLFG45557Ro8ePYoALMOtBRZYIKaddtrI/Q4rqf5S9UUxYyWQgdcxxxxTzFJcdNFFiz3nJp988rG6p4sJECBAgAABAgR+X6DN77/sVQIECBBojIBzCBAg0BoC9csbZts5cyL3v8nHDgIECLSWwNdffx0vvPBCXHLJJXHooYdGLju4wgorxD/+8Y9imcKc1XLllVfGvffeG88991y8/vrrkbO+sr+5n9E666xTzPa67rrr4uGHH46bbrqp2Ptojz32iDXXXLNYwnDhhReOGWaYITp37pyXOQhUhUD+e5H7eD3xxBMx3XTTRYZf88wzT1X0XScJEGgo4BkBAgQIVIeA0Ks6xkkvCRAgQIAAAQIjBNq0aRP1s7vyQ+Nc4jBa7x8tEyBQgwK//PJL5CyuXIpw3XXXjZlnnjmWWWaZyFlcuadWztx66aWX4q233oqPPvqoCLgyoJ9wwgljo402KoKwK664InI22JNPPlnM4jr44INjjTXWKPbdmnXWWWOqqaaKLl26RF1dXQ0KK7kMAp9//nlcfPHFxX51GXhl+LX44otH/ne8DPWpgQABAgQIEKg5gaooWOhVFcOkkwQIECBAgACB3xcYNmxYDB8+/Pff9CoBAgTGUiB/v+SRv2sGDx4cGUzlh/a5tOoiiyxS7MOVs7M+/fTT+OKLL+K7776Ln376qQiqclZWzvjKa84888wiAMtzLr/88siwbMMNN4zJJpus2Hcrl2zNvQrrA/2x7HYrXK5JAg0FcvZjLuuZM7tydmKGwcsvv7xlOBsyeUaAAAECBAgQaHIBoVeTk7ohAQINBDwhQIAAgWYRyL8Sz6NZbu6mBAjUpMCvv/5aBFZfffVVvPnmm3HHHXcU4VTurZUzrnLfrFym8JFHHilCrQy+MqyaaKKJYs455yz24MqAK/fweuihh4p75JJuffv2jdzPa/rpp69JV0XXnsCXX34Zp5xyShESTzzxxNG7d+9YeeWVaw9CxbUnoGICBAgQIFABAkKvChgEXSBAgAABAgTKLdAc1eWMiPwQOj+kHjp0aHM04Z4ECJRcIJco/OSTT+L9998v9tmq30crA6qckbLSSivFvvvuWyzNljO4MuSaYoopYuqpp45FF100Nt988yIUu/rqq+Oxxx6LG2+8MQ466KBiH6/FFlus5HrKI/D7Ahka5zKGffr0Kfafy+U8c2+7jh07/v4FXiVAgAABAgRKJaCY1hcQerX+GOgBAQIECBAgQGCMBb755ptiZsYY38CFBAjUjEAG5BlyvfLKK3HfffdFLjOYH8zvtddeseKKK8baa68de+yxR1x11VXx7rvvRvv27WPKKaeMhRZaKFZdddXYdNNNi5DrpJNOiocffjjOOeec2G677WK55ZaLRnygXzPOCq1dgR9//DHy348DDzywCLy22GKL2G+//YplPGtXReUECBAgQIAAgZYVEHq1rLfWCPyOgJcIECBAgMDoC+SH0eOOO+7oX+gKAgRqSuDbb7+Nxx9/PK699to4/fTT44gjjogMufLD+DzOPvvsuOKKK+Ljjz+OXKowlzBcYoklImen7LLLLnHIIYdE7seV15933nmx8cYbF+FYTSEqlkAjBL7//vvIGV65xGfOxt5yyy1j//33j5wd2YjLnVIzAgolQIAAAQIEmltA6NXcwu5PgAABAgQI/LmAM0ZbIAOv/FAt/6o8lygb7Ru4gACBUgr88MMPKiqzUwAAEABJREFU8dRTT8Vll10Whx12WORMrj333DN23XXX2G233eLkk0+Om2++uZjJlbOzcqnCJZdcMvID+sMPPzyOOuqoOO6444r9iI4//vjYeuutY4EFFogOHTqU0ktRBJpCIJf/rJ/h1bZt22IGpMCrKWTdgwABAgRKKaAoAs0sIPRqZmC3J0CAAAECBAg0p0CGXkOGDGnOJtybAIEWEhiTZnJfvw8//LCYzZVh1Q477BC9evUqZpjkLK38IP7BBx+MPCf35Jpxxhkj9+vafffdiwDs1FNPjQy3cgZYBmO599AiiywS3bt3H5PuuIZAzQl89NFH0a9fvzjmmGOK5YZ79OhR/Ps3+eST15yFggkQIECAAAEClSDQphI6oQ8E/kTA2wQIECBAgAABAgQIjCSQe3NddNFFkbO4MqjKsCtnaV188cVx//33x+DBgyP38Jpkkkki399pp51i4MCBMWDAgBF7DuXyhmuuuWaxZ5cl2EbC9ZBAIwUy8Orfv3+cdtpp8fXXXxczK/v27Rv5710jb+G0/xXwCgECBAgQIEBgrASEXmPF52ICBAgQINBSAtoh0FBgvPHGK/bfafiqZwQIlFUgP1B/4YUX4pJLLomll166mK217777xrnnnhv33XdfPP300/H5559Hly5dIoOs7bbbrgi/7rrrrmK5wkMOOSRWWWWVyP265pxzzph44okjl2Erq5e6CDS3wNtvv13seZf/Dn711VeRe3nlDMqpppqquZt2fwIECBAovYACCRAYGwGh19jouZYAAQIECBAg0EoC+WF1HvlBWy5x2Erd0CyBlhWosdby3+0MtHr27FkEXcstt1zsvPPOkcsVZgCWs0xyD6/cbyuDsBNOOKEIwAYMGBDHHntsEXDNNddckcusWa6wxn54lNusAu+9914cffTRccEFF0S7du0ilwfdZZddolu3bs3arpsTIECAAAECBAj8uYDQ68+NquIMnSRAgAABAgRqU2DYsGExfPjw2ixe1QRKJJD/HueRs0dy5sj2228f448/fiy77LLFcoTPPfdcfPbZZ/HNN98UVU8//fSRy6ideeaZMXI4Nu+880bXrl2jc+fO0aaN/3evwPKFQBMKvP7667H55pvHWWedFR07dow99tgjcnnRiSaaKOrq6pqwpVHfyjsECBAgQIAAAQKjFvD/BY3axjsECBAgUF0CekugpgTyg7b8YFvoVVPDrtiSCGS4NWTIkPj+++/j008/jQy0jjzyyMiZXDPMMENsu+22cfbZZxcfoI877rjFkoW5R9A888wTBxxwQNxzzz3x1ltvRS6nluFY+IcAgWYX+PXXX+PFF1+MddZZp1g6dMopp4zjjz8+DjzwQDO8ml1fAwQIEPgfAS8QIEBglAJCr1HSeIMAAQIECBAgUPkCljes/DFq2R5qrZIFfvrpp8hl0XL/rdyb66CDDoplllkmcnZW7969i6UJs/+5XOE000wTCy+8cGyzzTZxxhlnxOOPPx7PPvtssYxaXpPnOQgQaBmB7777Lm688cbYaKON4pVXXom555672Ctviy22aJkOaIUAAQIECBAgQKDRArUTejWaxIkECBAgQIAAAQIECBBoGoEMpt94443iA/Pc/2e33XYrZopsvfXWxSyRl19+OXI213TTTVeEX2ussUb06NEjTjvttLjtttvi1FNPjX/84x8x7bTTNk2H3IVALQg0YY3573DOvMwZXq+++mqxV17u4bXhhhs2YStuRYAAAQIECBAg0FQCQq+mknQfAgQIVIGALhIgUB6BoUOHRi619MsvvxTfy1OZSghUt0D+O/nOO+/E3XffHRdddFEce+yxsc8++8Saa64ZO+20UwwcODDefffdaN++fWTQteqqqxb7AeXyhhlw/fOf/4zTTz89Vl999WJvrurW0HsC1S3w2muvxSmnnFIsYZh75K233npx+OGHx2qrrVbdhek9AQI1IaBIAgQI1KqA0KtWR17dBAgQIECAQCkEfv75Z6HX6I2kswk0uUDurffBBx/ErbfeGieffHLsv//+0bNnz9hxxx2L5Qivv/76os1u3boVs0Ty9UMOOSSOOuqo4vz+/fsXs7mWXHLJ4jxfCBBofYEHH3yw2Dfv6KOPLmZj5gzMY445Jvx72vpjowcECBAgQIAAgT8SGCn0+qPTvEeAAAECBAgQIFBJAuOPP35MMMEE0a5du8i/Pq+kvukLgVoR+PLLL+Omm24qZmrtuuuuxYyunAVyxRVXxPPPPx8//PBDdOnSJVZcccXYY489iqUKjzvuuGKmSAZjuT/QjDPOWMz6qhUzdVaKgH6MSiD33rvzzjsjg+nrrrsuJp544mL/rsMOOyymmmqqUV3mdQIECBAgQIAAgQoREHpVyEDoBgECFSKgGwQIEKgSgbZt2xaBV11dXdTV1VVJr3WTQHULfP/993HffffFGWecEZtssklssMEGseeee8Y555wT+eH4Cy+8ELn/T8eOHWOZZZaJfv36xeWXXx4nnnhiHHDAAcVsrkUWWSRyxld1S+g9gXIKfP3113H++edHz54944EHHog55pgjTjjhhNhiiy1i8sknL2fRqiJQywJqJ0CAAIFSCgi9SjmsiiJAgAABAgQIjLmAKwkQ+K9A7tH19NNPR+65lSHXVlttFX379o2rrroqBg0aFLnnT56de3TNN998xSyuXOYwPzjfeeedY+WVV47ZZ589JppoojzNQYBAhQp89NFHkbO5DjnkkHjppZdiscUWK2Z45b57Oau6QrutWwQIECBAgACBsRIo48VCrzKOqpoIECBAgAABAgQIEBgrgffee6/Yj2vhhRculic84ogj4vbbb4+33norPv300xF76S2++OJx0EEHxbvvvhu5JFouY7jEEkvEDDPMEJ06dTITc6xGoVUv1niNCAwfPjyeeeaZ2HTTTeO0004rliXdYYcd4swzz4yllloqxhlnnBqRUCYBAgQIECBAoBwCQq9yjKMqCLSggKYIECBAgAABAuUT+Pzzz+O8884r9vHJ/fKmnXba6N27d/Fh+GeffVZ8ED506NBixtbWW29dzPb65ptv4sEHHyyuyaXPunfvHuONN5599sr346GiEgtkWL3AAgvEXXfdFZ07dy5mdeYSprPOOmuxjHCJS1cagUYIOIUAAQIECFSfgNCr+sZMjwkQIECAAIHWFtA+AQJVLfDrr79GBlbvvPNOsefWhhtuGHPOOWdss802cfDBB8e3334bOfsjlzTLAGzqqacu9uO68sor48UXX4xzzz23OC8/IK+rs6deVf8w6HzNCuT+exlsr7TSSkW4Nc888xTB96677lqzJgonQIAAAQIEfkfAS1UnIPSquiHTYQIECBAgQIAAAQIERkcg9+XKD7hzz56nnnoqBg4cGNttt10suOCCsfHGGxf7c3388cfFLXNvrkknnTTmmmuuWGuttYrlzu6+++647LLLIvf0yveKE30JBASqUSBD77fffju23HLL6NevX3To0CHWWWedyFB7jTXWqMaS9JkAAQIECBAgQGAkAaHXSBgeEmgiAbchQIAAAQIECBBoZYH8YDtDrqeffjquv/76OProo2Pvvfcugq7111+/+ID7iy++KHqZQdeUU04Zyy23XDGjKz8Iz6XOrr766mKfn5lmmqk4zxcCBKpb4Icffoh77rmnCLxuueWWmGyyyWKLLbaIU089NWabbbbqLk7vW0tAuwQIECBAgECFCQi9KmxAdIcAAQIECJRDQBUECBBoeYFckjD337r//vvjggsuiMMOOyx22GGH2GqrrYoZHZdccsmITmXQNf3000cubbbjjjsWyxVeccUVMWDAgOID8UkmmWTEuR4QIFD9Ajm7K5cm7dWrV+TviNzH66CDDoozzzwzcj++6q9QBQQIECBAoLUEtEugsgTaVFZ39IYAAQIECBAgQGB0BHLZtqFDh47OJc4lUCqB3H9r8ODBcd9998Whhx4auUdPfqjds2fPOP300+Pxxx+P77//vqg59+daZpllYt11142+ffvGUUcdFSeffHKcdNJJxX5eTR50Fa36QoBAawrkrM+XXnop+vfvHxly5VKma665ZhGEb7311q3ZNW0TIECAAAECBAg0g4DQqxlQ3fLPBZxBgAABAgQIjJ1ALtGUH/b//PPPkUfOcBm7O7qaQPUI5M9/hllnn3127LHHHrHbbrvFnnvuWczWOuussyL37cpzsqJOnTrFEkssETvttFMRcB133HHFUocHHnhgbLjhhjHLLLPkaQ4CBEookH8Ycu+990aG4LkvX5cuXSJD8VzudMkll4y2bduWsOrKK0mPCBAgQIAAAQItKSD0akltbREgQIAAgf8KeERgrATGGWec6NChQ3Tu3DnGG2+8sbqXiwlUg0B+eP3iiy8Ws7cywMrj4IMPjgsvvDBuuOGGePLJJ0eUMe6448b8888fu+++e2QIlvv15AyPzTbbrNjTa8YZZxxxrgcECJRTIGd47bfffkUwfuedd8Ycc8wRxx9/fLHkae7f1a5du3IWrioCBAgQqEQBfSJAoAUF2rRgW5oiQIAAAQIECBBoIoH60Cu/51FXV9dEd3YbAi0p8OdtZZh14oknxmKLLRbrrbdeMZvr8ssvL0KuDz/8MPKD7fq7zDDDDLH33nvHTTfdFLk/Vy51uP7668d8880Xk046abRp4//9qbfynUCZBT766KPIvfoy9M69vPJ3x5FHHhlrrLFGdOvWrcylq40AAQIECBAgUPMC/r++Sv0R0C8CBAgQIECAAAECNSjwxRdfRC5DtvPOO0fOyFpllVWiT58+Rcj1yiuvxKeffhpDhgwZIZNhVs76eu+99+Lhhx8u9uz561//Wixb2L1792jfvv2Icz0gQKD8As8880wstdRScfHFFxf//udef+ecc07kcob5RyIVKaBTBAgQIECAAAECTSYg9GoySjciQIAAgaYWcD8CBAgQqA2B3H/r9ttvjx49esTcc88dm266aZxxxhnx1ltvFSHXd999NwIil/Rcdtlliz28Xn311fjggw8ily+caqqpitlcuYeXGV0juDwgUFMCGXSttNJK8cYbbxR133LLLcV+Xl27dg3LGRYkvhAgQKBiBXSMAAECTSUg9GoqSfchQIAAAQIECLSgQIYAX375ZQu2qKlWEihds8OGDYvvv/++CLPuv//+Yn+dBRZYIFZeeeVif64MsYYPHx55tG3bNjp27BgTTzxx5AfZ5513XuQsjnvuuSeOPfbYYjZXBlx1dXWlc1IQAQKNExg6dGgMHjy4CLd23XXX+Pbbb2P11Vcvgq9cFrWuzu+Hxkk6iwABAgQIECBQDoEqDr3KMQCqIECAAAECBAgQIFB2gfxQOkPal156KQYNGhT7779/zDvvvLHMMstE7rmTM7bqDXI2xkQTTRSzzjprrLbaanH88ccX19x2222x1VZbRe7bFf4hQKDGBH6/3J9++ikee5G7leUAABAASURBVOyx2H777eOkk06KnOmZe/ldc801kbM/f/8qrxIgQIAAAQIECJRZoE2Zi1MbAQIESi+gQAIECBAgUKECv/76a3z44YfxxBNPxLXXXhtHHnlkrLDCCsWMrVNOOaV4r77rOaMrZ3Mtuuiisd566xX7ct18881x/fXX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ulh22WXj8MMPL56bWdpc6k10X7chQIAAAQIECFSwQJsK7puuESBAgACBqhLQWQItJZD7eeVyddlex44dY5xxxsmHjhoWyA+PH3/88TjnnHNir732ij333DMOPPDAIvh68sknR8iMP/74sdBCC0UGXSeddFIx6ys/cM4Pm2eZZZYR53lAgACB5hTI2ab9+vWLww47LN59993YYYcdit9H66+/fnM2694ECBAgQKDJBNyIAIHKFRB6Ve7Y6BkBAgQIECBA4HcFcjmoTz75pHhv3HHHjfbt2xePfak9gVzm8qmnnipCrFy+MMOs8847Lx599NERswHz52POOecsZk+cf/75xfKFGYzlh8v5ehOruR0BAgT+UOCRRx4pljDM4P2LL74oAvrevXtbzvAP1bxJgAABAgQIECDQWAGhV2Olxvo8NyBAgAABAgQINI1ABh1fffVVcbMJJpggOnToUDz2pTYEhg8fHm+++WYxK2LNNdeMTTbZJM4888x44okn4oMPPojc8y0lppxyythpp50ig66rrroqDjjggFhvvfWKmV45QzDPcRAgQKClBHJp3htvvDF23XXXuPXWW4u9BC+66KLYZpttIpdbbal+tEw7WiFAgAABAgQIEGgtAaFXa8lrlwABArUooGYCBJpEoF27djHeeONF/pOP27Txf9KlRS0cGWrl/ltLLbVUsSzYoEGD4pVXXhkxqysNJp100iLseuyxx+LII4+MDTbYIOaYY47Ifbratm2bpzgIECDQogK5JG/uG9ijR494+umnI2eann766cW+gvnHGy3aGY0RIECAQMsIaIUAAQKtJOATklaC1ywBAgQIECBAYGwE6urqxuZy17aiwOg2/dlnn8W5555bzNCaYYYZYt99940PP/ywCLqGDRtW3G7CCSeMjTfeOO67775466234uSTT46c6dWlSxd7vhVCvhAg0FoCOTt5q622Kmab/vTTT9GrV69iv8HZZpst8g83Wqtf2iVAgAABAgQIECinQCWFXuUUVhUBAgQIECBAoIkFck+vTz/9tLhrt27dwlJ1BUVpvvz6669FoPX222/HhRdeGMsuu2xsu+22xfKF+eFxBl35QXGnTp1i/vnnL2ZzPfTQQ3HppZfG0ksvHeOPP36Y0VWaHweFEKhagfxd9uyzzxa/w6699trI0P7UU0+NY489NiaccMKoq/PHG1U7uDpOgAABAgQIEKhgAaFXBQ+OrhEgUIsCaiZAgMCfC+SeTvX7Nv352c6oFoEMtHKWVu6/lXtv5QfEuRTYiy++OKKErl27xjTTTBO5l1fuhfPUU0/F/vvvH7PPPvuIczwgQIBAawt8+eWXkUHXoosuWixnuNBCC8UFF1wQW265ZWt3TfsECBCoIAFdIUCAAIHmEBB6NYeqexIgQIAAAQIEmlEgw5GvvvqqGVto5VvXUPM5lu+++248+OCDcdlll8UWW2wRm2yySdxxxx0jFMYZZ5yYaqqpYrnlliuWNjz//POLD5PXXnvtEed4QIAAgUoQ+OWXX+L111+PY445Jrbbbrti/8lNN900LrzwwlhyySUroYv6QIAAAQIECBAgUEkCzdAXoVczoLolAQIECBAgQKAlBNq3bx95NGdb3333XXz//ffN2URN3jvDrjfffLNYkjBnaq2//vqRe95k+FUPMsEEE8QiiyxSBGGHH354DBw4MPbbb79Yfvnl60/xnQCBEgtUW2kZeD388MOx9957j9hXcM899yz2JJxtttmqrRz9JUCAAAECBAgQqFIBoVeVDpxuE6hhAaUTIECAwP8XyL2bxhtvvP//rGm/ffLJJ3HeeefFYYcdFoceemj0798/brvttvjhhx+atqEaulvub5OzujK8yj1tMuzKD4cvv/zy+OijjwqJNm3axBRTTBGrr7567LPPPnH88cfHCSecUARfGYIVJ/lCgACBChPI2cfXXXddHHTQQXHfffcV+wtmWL/vvvtWWE91hwCBKhPQXQIECBAgMNoCQq/RJnMBAQIECBAgQKC1BZq+/Zx59MILL0TuE3XAAQdEz549I2cV5RJVeeRreZx00kmR5zV9D8p7x9yD7YMPPiiWL+zVq1cxCyKDxKuvvjq++OKLEYV379491l133TjxxBOLpcFyhsTiiy8eHTt2HHGOBwQIEKg0gfwjiVNOOSX69OkTTzzxRKyzzjpx5JFHFnsPtmvXrtK6qz8ECBAgQIAAgSoT0N3RFRB6ja6Y8wkQIECAAAECrSyQs61yZlB+//HHH8e4N3fffXcMGDAgdtttt9h8881j++23j969exezi3L20WeffTbi3jlL6dlnny0Cmfxw87XXXhvxnge/L5CBVs582HHHHYuZWn379o3rr78+3njjjRgyZMiIi7p161bsfXPJJZcUHxTnUoezzz57dOjQYcQ5HhAg8DsCXmpVgQz0878FObvr5JNPjp9++qnYdzD/OzLPPPOEwKtVh0fjBAgQIECAAIGaFRB61ezQK7zMAmojQIAAgdoQyP1T8mhstfXLT2255Zax5JJLFkFLzt4699xz45prronci+W9996LnPX1e/ccNmxYfPrpp8W5ueTe22+//Xun1fxr6XzDDTfEhhtuGDmz68ILL4xBgwbFO++8M8Kmc+fOsfLKK8dZZ50VN998cxxyyCGx0korxUwzzTTiHA8IECBQyQKPPvporLfeepGB/aSTThpHH3108UcU008/fbRt27aSu65vBEoloBgCBAgQIECgoYDQq6GHZwQIECBAgEA5BEpdRc4S+uabb4oav/766/ij2V45G+yuu+6KHj16xKKLLloEXTmL66GHHoo333wzctm9vD4DreKGI33JZfUynOnateuIV/Mv+7/88su48sorY+DAgVHfjxEn1PCDp59+Orbeeuv/CSSPAAAQAElEQVTCOb/nTLrBgwc3CBFzD7bllluumGF38cUXFzPAFllkkZhssslqWE7pBAhUm0D+/l977bXj5ZdfjgUXXDBOP/30YlnDrl27Vlsp+kuAAAECBAhUv4AKCDQQEHo14PCEAAECBAgQIFD5ArnUYIZZ2dOhQ4fGbwOrV199tfhr+yWWWCJy6bxVVlml2KsrX//8888bLK2X96g/JphggmJPqVya6vXXX488N2d23XfffZFBTf159cFX7vV11VVX/WHoVn9NWb/nWPzrX/+KjTfeODK8ylld9c4jj0su85Wz63IG2O233x5rrbVW5B5e4447btTV1ZWVR12tLqADBJpeIPfuyt95uQTuBhtsUMxkXWqppSJ/nzV9a+5IgAABAgQIECBAYPQEhF6j5+XssgiogwABAgQIVLFAzvT69ttvI2cNZZiSs71eeumlIkjJJaVmm222yH23crnCXKowl0CsD2DatGlT7BWV10444YSRy1Dts88+xaytd999t1i68LDDDiuW2csPMPOYa6654rjjjov8q/68fz1d7it24IEHxv333/8/wVv9OWX7no65b03u17X33nsXs7oWXnjhyNlzv+ecoeOxxx4bafvAAw/E3/72t2jfvr2gq2w/GOohUHKB/AOLXNK2R48exX8P8r8lPXv2jEsvvdTvtJKPfSnKUwQBAgQIECBQUwJCr5oabsUSIECAAIH/CnhU/QK5LOE///nPWHzxxWPOOeeMnEWUocxvK8sPJ7t06RJTTz11sZfXoYceGkceeWS8+OKL8dZbbxX7sPz973+Prl27/vbS4nleP/fcc8fuu+8eiy22WIwzzjjF6/nlk08+ic033zxyL7B8XtYjrT/++ON45JFH4uCDD47JJ588Msx68sknG5ScoeBEE01UOB1yyCHx4Ycfxp577hlTTDFFg/M8IUCAQLUIZNCfv/tydlfu35W/484777zid2C11KCfBAgQIECg1gXUT6CWBIRetTTaaiVAgAABAgRKJ/DEE08Us4h+W1hdXV3kflzzzDNPrLnmmpGzkvLDyltuuaV4nH+hn8HNb68b1fOcUZaBV9++fYvZTfm8/twMvvbbb7/Ivb7qXyvD9wwQc3nHxx57LPID3t12262wPProo/9nichOnTrFvPPOG6uttlocfvjhkWFkzqAbOSAsg0kJa1ASAQJ/IJAzifMPKnr16hXPP/98rLjiipH7Ea6//vp/cJW3CBAgQIAAAQIECLSegNCr9ewrvGXdI0CAAAECBKpRIEOWWWedNXKflZyZdc4558T1118fuQzh0ksvHR07dhzjsvLeuW9LBmZzzDFHg/tcccUVceqpp0b9XmMN3qyyJ7lnWc5cy723jjrqqGK/rl133TVy/7Jc1nDkcnKJyNw7LQOxs88+O9Jhhx12KPZSG/k8jwkQIFBtAvn7LgP//fffP3Jpw3/84x9x+umnx7LLLhs5A7ja6tHfPxLwHgECBAgQIECgPAJCr/KMpUoIECBAoKkF3I9AhQrkh40jz7Sqq6uLnNGVf3mfH07269cvMoDJvblyv6mmLCP3+FpllVVi2223LZb4G/neudzf448/PvJLVfU490nLJbzyQ93evXtHhnsnnHBCsQTkbwuZYIIJYplllomc+XbiiSfGEUccEWmde6WFfwgQIFDFAjnLNZe/zSVac2ZrlrLLLrtEnz59Ytppp82nDgIECBAgUD4BFREgUBoBoVdphlIhBAgQIECAQK0ITDPNNLHuuuvGJJNMUuzTleHMcccdF/379y9CmLXWWisylGkujw4dOkQGbOutt16xhGJ9O99//32cfPLJ8f7779e/VBXfv/vuu3jooYeK2XC5/1YGhxdddFG89tpr/9P/2WabLfbaa6/I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+eefP1ZYYYXIx/U9v/322+Opp56qf/qn34cOHRp33313bLnlltGrV6+45ZZb4sMPP4xff/21wbVTTz115Ie6V111VTGbYckll4zu3bs3OMcTAgQI1JLA+++/Xyztmn8o8OabbxazXo866qgw67WWfgrUSoAAAQIEqldAzwmMjYDQa2z0XEuAAAECBAgQIPC7AhNNNFH85S9/iUkmmWTE+xli9evXb8TzP3qQSxdusskmkUtw5eN33nnnf8KuSSedNA4//PB48MEHi+UM55tvPmHXH6F6rwwCaiDwpwIPPPBA5B8LXHDBBcXvzVzqdfvtt4/8A4E/vdgJBAgQIECAAAECBKpcQOhV5QOo+/UCvhMgQIAAAQKVJNCmTZtYaqmlYvHFF2/QrZtvvjnee++9Bq+N/OTOO++MDTbYoFiG6+qrr46PP/44fvnll5FPKYKtk08+OV566aXYd999Y5pppomOHTs2OMcTAgQI1KLA448/Xiwle9ddd8W0004bN910U+yyyy7RuXPnWuRQc2kFFEaAAAECBAgQGLWA0GvUNt4hQIAAAQLVJaC3BCpMoFu3bjHTTDNFp06dov6fYcOGRS61Vf98+PDh8dNPP8U999wTm266aay//vqRYdePP/7YYM+udu3aFfc64IAD4l//+lfsuuuukffP18M/BAgQqHGB/J15zTXXxOabbx4ZfC2wwAJx7rnnFssZjrzMbI0zKZ8AAQIECJRHQCUECIxSQOg1ShpvECBAgAABAgQIjI3ABBNMUMw0GH/88Rvc5rbbbosMu77//vt48skni6UJ11xzzbj00kvj66+/bnBuXV1ddO3aNdZee+24/vrr44gjjojpppuuwTmeEBhZwGMCtSbw7bffxhVXXFHMks19D/MPCPL5EkssUWsU6iVAgAABAgQIECAQQq/a+SFQKQECBAgQIECgxQWGDBkSuZfXyA3nbISXX345TjnllFh33XXj7LPPju+++27kU4rHuURi7gvWt2/fOOecc2KuueYqXveFAAECBP5P4J133olTTz01tt5665hiiimiV69exR8H5Czb/zvD1xoVUDYBAgQIECBAoGYFhF41O/QKJ0CAQC0KqJkAgZYWeP/99+PLL79s0OwHH3xQzO7af//9Y/DgwQ3eq38yySSTxHrrrRenn3569OzZM7p01W6ohgAAEABJREFU6VL/lu8ECBAg8B+Bhx56qNjX8MADDyyWf91rr71izz33LMKv/7ztfwQIECBAoMYFlE+AQK0KCL1qdeTVTYAAAQIECBBoAYGc5ZX7eI3cVO49M2jQoJFfavB4tdVWi4MOOijOPPPMyJleDd70ZOwF3IEAgaoW+OGHH+KOO+6IPfbYo9gDcemll47DDjus+AOBzp07V3VtOk+AAAECBAgQIEBgbAWEXiMJekiAAAECBAgQINB0Aj/99FPk0dg7Tj311LHPPvtE//79Y6eddooJJ5ywsZc6jwABAjUhkIHXeeedFzmr66mnnoptt902jjnmmNhwww1rov6mLNK9CBAgQIAAAQIEyikg9CrnuKqKAAECYyrgOgIECDSZwEcffRQff/zxn95vvPHGi4033jhOO+20OOCAA2L22WePurq6P73OCQQIEKglgc8//7zYv6tfv37x3nvvFX8ckLNiF1544VpiUCsBAgQINJ2AOxEgQKCUAkKvUg6roggQIECAAAECrS+Q+3JNMcUU0bZt29/tzDjjjBPLL798DBgwIPJD3FVXXbVC9u763e56kQABAq0m8Oabb0bfvn2LmbA526t3796x33772b+r1UZEwwQIECBAgAABApUqMHqhV6VWoV8ECBAgQIAAAQIVJzD++OMXe3JNOeWUDfpWV1cXM888cxx++OFxySWXxFprrRW5tOGowrEGF3tCgACBGhPIwGvfffeNCy64IPL3as6K3WGHHWKyySZr3lmxNeasXAIECBAgQIAAgXIICL3KMY6qIECgBQU0RYAAAQKNF8jZWzvvvHMRatXV1UXO7lp//fXj2WefjV69ehUf2rZv377xN3QmAQIEakgglzHcZJNNYuDAgTHddNMVyxvm/l0dO3YUeNXQz4FSCRBoPQEtEyBAgED1CbSpvi7rMQECBAgQIECAQCsLNLr57t27xz777BNPPPFEPPzww5HLcl155ZWR+3i1a9eu0fdxIgECBGpJYNiwYfGvf/0r1l577eL35zLLLFPMjF1jjTVGuWRsLfmolQABAgQIECBAoMUEqq4hoVfVDZkOEyBAgAABAgSqTyD391p00UV9WFt9Q6fHBAiMUqB53vjxxx/j/vvvj4UXXjheeumlWGmlleLss8+O+eefv3kadFcCBAgQIECAAAECJRIQepVoMJVCoGIEdIQAAQIECBAgQIAAgdEW+Pbbb+Oyyy4rgq7OnTvH1ltvHSeddFLMOOOMo30vFxAgQKBFBDRCgAABAgQqTEDoVWEDojsECBAgQIBAOQRUQYAAAQIERkfgk08+iQEDBsTee+8dXbp0iZ122ilOOeWUmGmmmUbnNs4lQIAAAQIECBBoYQHNVZaA0KuyxkNvCBAgQIAAAQIECBAgUBYBdTRS4J133ikCrkMOOSQ6dOgQvXv3jn79+jXyaqcRIECAAAECBAgQIFAvIPSql/CdQIsKaIwAAQIECBAgQIAAAQIRr7zySvTv3z/OOOOMyCUNDzrooGKWFxsCBMoioA4CBAgQIECgJQWEXi2prS0CBAgQIEDgvwIeESBAgACBGhd4/vnno2/fvnHppZfGJJNMEjnTa4sttoh27drVuIzyCRAgQIAAgVIJKIZACwoIvVoQW1MECBAgQIAAAQIECBAYWcDj2hV4/fXX48ADD4wbb7wxJp988sgZXuutt16MN954tYuicgIECBAgQIAAAQJjKSD0GktAlzebgBsTIECAAAECBAgQIECgdALDhg2Lp59+Onbddde4/fbbY6qppoo+ffrEWmutJfAq3WgrqJECTiNAgAABAgQINJmA0KvJKN2IAAECBAg0tYD7ESBAgAABAmUTeO2112L33XePO++8M2aeeeY4/vjjY4MNNogOHTqUrVT1ECBAgAABAo0WcCIBAk0lIPRqKkn3IUCAAAECBAgQIECg6QXckUBJBIYPHx4vv/xyzD777PHggw/GvPPOG2eddVasvvrq9vAqyRgrgwABAgQIECBAoPUFhF6tPwZj3AMXEiBAgAABAgQIECBAgEDlC/z888/xyCOPxAILLBB1dXUx99xzx8knnxxLLLFE5XdeDytCQCcIECBAgAABAgQaJyD0apyTswgQIECgMgX0igABAgQIECBQ0QLff/99XHfddbHmmmvGTz/9FAsvvHD0798/llxyyYrut84RIECAAIEKE9AdAgQINEpA6NUoJicRIECAAAECBAgQqFQB/SJAoFIFvv3227j22mtjv/32i88//zyWXXbZGDBgQKy44oqV2mX9IkCAAAECBAgQIFDVAuUOvap6aHSeAAECBAgQIECAAAECBKpVIAOva665Jg499NAYPHhwrLrqqnH66afHrLPOWq0lVXa/9Y4AAQIECBAgQIDAfwSEXv9B8D8CBAiUWUBtBAgQIECAAAECLSuQs7ouvvji6NevX3z88cex3nrrxVFHHRWzzTZby3ZEawQIECBQUwKKJUCAAIEIoZefAgIECBAgQIAAgbILqI8AAQItJvDWW2/F/vvvH0cffXT8+9//LgKvPn36xJxzzhl1dXUt1g8NESBAgAABAgQIEKhBAaFXLQ66mgkQIECAAAECBAgQIECg6QW++eabuPTSS+OSSy4pljT89ddf46mnnopbb701brvttnj66afj+eefb/qGG3VHJxEgQIAAAQIECBAov4CZXuUfYxUSIPBnAt4nQIAAAQIECBAg0AQCP/zwQzzxxBPx448/FncbMmRIvPDCC8Uyh9tss02stdZasfrqq8fcc88dK6ywQqy88sqRr5955plx3333Re4DVlzoCwECBAg0j4C7EiBAgEDpBYRepR9iBRIgQIAAAQIE/lzAGQQIECAw9gLdunWL3XbbLZZffvkRNxs2bFh89dVX8dFHHxWzvwYPHlwEYffcc0/ceeedMWDAgOjZs2esuuqqMeWUU8bss89ePN5qq63iqquuigceeCBeffXVEffzgAABAgQIECBAgMDYCJT9WqFX2UdYfQQIECBAgAABAgQIECDQGIGxPmecccYpAq8bbrghHn300TjuuOMiw6uc3ZWB2Pjjjx8dOnSIcccdN9q2bVvs8ZWhWC6D+NNPPxUzvV555ZViOcQLLrggNtxww1h66aVjwQUXjGmnnTaWW265IiC78sor47PPPoucWfbzzz/HL7/8EnmfsS7ADQgQIECAAAECBAhUuYDQq8oHUPcJtIyAVggQIECAAAECBAgQaKxAx44dY5FFFok99tgjzjvvvLjxxhvjxRdfjNdeey1yhtdFF10Uhx12WGyxxRaxzjrrxD/+8Y9YaaWVYs4554yZZ545pptuuuKYdNJJo3v37kU49uGHH8a9994bJ510Umy00UYx8cQTF+dusskmceCBB8ZNN90Ub775Zvz73/+OL774Ir7//ntBWGMHzHkECIwk4CEBAgQIEKhuAaFXdY+f3hMgQIAAAQItJaAdAgQIECAwFgKTTTZZsXzhoosuGhtssEHsu+++RSB29dVXx8UXXxy33HJLsexhBmM5S+zBBx+MnO11+umnF+HZuuuuG4sttljMNddcMf3000fOHMsg7Nprr43+/fvH3//+9+K91VZbLfbZZ5849dRT4+67745nn322WB7x66+/Hoveu5QAAQIECBAgUEMCSq1qAaFXVQ+fzhMgQIAAAQIECBAgQKDlBLTUMgI5wyv391pllVVi/fXXj0MOOSQuv/zyuOOOO4qA7IQTTiiCrZzxlUsnLrTQQjHLLLNELq/43HPPFWHafvvtF+utt16xROK2224bJ554Ytx8882RYdobb7wR3333XcsUoxUCBAgQIECAAAECLSgg9GpBbE2VWkBxBAgQIECAAAECBAgQaFaBTp06xXzzzVfM6sqZYpdddllceOGFcfLJJ8fRRx8de++9d2y99daRs8L+8pe/xIQTTlgsefjAAw8UwVmPHj1ip512iv333z+OP/74uPTSS4sg7NVXX43cV6xZO+/mBMojoBICBAgQIECgggWEXhU8OLpGgAABAgSqS0BvCRAgQIAAgZYWmGiiiSKXTFxrrbWKJROPO+64yOPYY48tlj3cbbfditlef/3rX6NDhw7x/PPPxzXXXBNHHnlk7LnnnsWRYdkRRxwR/fr1i1wucfDgwS1dhvYIECBAgACBqhLQWQKVKyD0qtyx0TMCBAgQIECAAAECBKpNQH8JtKJA+/bto0uXLjHttNPGMsssUyxveOCBBxYBVwZhZ599dpx//vlx8MEHxxprrFHsMZb7gt14441xzDHHxOGHHx777bdfMRusV69e0bdv3yIg+/e//x3+IUCAAAECBAgQIFANAm2qoZP6WA4BVRAgQIAAAQIECBAgQIBAywp069YtZphhhph33nlj5ZVXji222CIy0MqQ64ILLiiWODzllFNik002iVlnnTVyv69bbrklzjzzzMh9wHIWWO4Ntvbaa0efPn3in//8p6UQW3YIq7I1nSZAgAABAgQItJaA0Ku15LVLgAABArUooGYCBAgQIECAQKsKtGnTJiaYYIKYfvrpY5555olVVlml2AfsqKOOiuuvvz5efPHFItjq2bNnLLbYYjF06NB47LHHImeDnXTSSbH99tvH3HPPHSuttFIceuihkfuFtWpBGidAgAABApUpoFcECLSSgNCrleA1S4AAAQIECBAgQKA2BVRNgEAlCbRt2zbGG2+8yL3BpplmmphjjjmKICyXQMz9vR555JEi2Orfv38svfTS8fXXX8crr7wSgwYNitwHLEOzKaecMlZdddU499xz49NPPw3/ECBAgAABAgQIEGgtAaFXa8n/XrteI0CAAAECBAgQIECAAAECrSyQQdi4444bHTt2LPb9WnLJJYslEW+66aZi+cMHH3yw2O9r0UUXLWaCff7553HnnXfGdtttV4Rma665ZuTSiC+88EL8+OOP8csvv8SwYcNauaoKa153CBAgQIAAAQIEmkWgTbPc1U0JECBAgMAYCriMAAECBAgQIECgcgWmmGKKWGKJJeKggw6Ku+66q1gO8brrrou99torMgRr37593HPPPbHHHnsUyyDOPvvs0bt378h9wj744IP47rvvBGCVO7x6RoAAgRYV0BgBAgSaQ0Do1Ryq7kmAAAECBAgQIEBgzAVcSYAAgaoQaNeuXcwwwwzF0oa5J9jtt98e9QHYUkstFbPOOmsx0ytnfa277rqx+uqrx/HHHx933313vPXWWzFkyJCqqFMnCRAgQIAAAQIEqkegykKv6oHVUwIECBAgQIAAAQIECBAgUEsCnTt3LmZ79e3bN66//vo4++yzo0+fPrHBBhvEAgssEP/+97+LZRHXWmut2G233eLee++Nn3/+eRREXiZAgAABAgQIECAw+gJCr9E3cwUBAgRaV0DrBAgQIECAAAECBCpcoFu3brH00kvHzjvvHCeccEKcfvrpsf/++8f6668fc801Vzz55Nl0wzEAABAASURBVJPFjK/c86vCS9E9AgQItJ6AlgkQIEBgtAWEXqNN5gICBAgQIECAAIHWFtA+AQIECFSHQF1dXXTv3j3mn3/+2GWXXYrlDY899tg45phjYsMNN4yOHTtWRyF6SYAAAQIECBAg0CoCo9uo0Gt0xZxPgAABAgQIECBAgAABAgRaX6DqetC+ffuYaqqpYskll4zNNtusCMLytaorRIcJECBAgAABAgQqVqBNxfZMxwgQIDDGAi4kQIAAAQIECBAgQIAAAQIEyi+gQgIECBAg0FBA6NXQwzMCBAgQIECAQDkEVEGAAAECBAgQIECAAAECBAiUX0CFDQSEXg04PCFAgAABAgQIECBAgACBsgiogwABAgQIECBAgACB2hIQetXWeKuWQL2A7wQIECBAgAABAgQIECBAgED5BVRIgAABAgRqSkDoVVPDrVgCBAgQIEDgvwIeESBAgAABAgQIECBAgAABAuUXUGEtCQi9amm01UqAAAECBAgQIECAAIGRBTwmQIAAAQIECBAgQIBAiQSE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AQIEyi+gQgKlFBB6lXJYFUWAAAECBAgQIECAwJgLuJIAAQIECBAgQIAAAQIEqlFA6FWNo9aafdY2AQIECBAgQIAAAQIECBAgUH4BFRIgQIAAAQIEqlBA6FWFg6bLBAgQINC6AlplANEeAAAQAElEQVQnQIAAAQIECBAgQIAAAQIEyi+gQgIEqk9A6FV9Y6bHBAgQIECAAAECBFpbQPsECBAgQIAAAQIECBAgQKDiBIReTT4kbkiAAAECBAgQIECAAAECBAiUX0CFBAgQIECAAAEClSYg9Kq0EdEfAgQIlEFADQQIECBAgAABAgQIECBAgED5BVRIgACBChMQelXYgOgOAQIECBAgQIBAOQRUQYAAAQIECBAgQIAAAQIECLSsQGuEXi1bodYIECBAgAABAgQIECBAgACB1hDQJgECBAgQIECAAIEWFRB6tSi3xggQIFAv4DsBAgQIECBAgAABAgQIECBQfgEVEiBAgEBLCgi9WlJbWwQIECBAgAABAv8V8IgAAQIECBAgQIAAAQIECBAov0ALVij0akFsTREgQIAAAQIECBAgQIAAgZEFPCZAgAABAgQIECBAoOkEhF5NZ+lOBAg0rYC7ESBAgAABAgQIECBAgAABAuUXUCEBAgQIEGgyAaFXk1G6EQECBAgQIECgqQXcjwABAgQIECBAgAABAgQIECi/gAqbSkDo1VSS7kOAAAECBAgQIECAAAECTS/gjgQIECBAgAABAgQIEGikgNCrkVBOI1CJAvpEgAABAgQIECBAgAABAgQIlF9AhQQIECBAgEDjBIRejXNyFgECBAgQIFCZAnpFgAABAgQIECBAgAABAgQIlF9AhQQaJSD0ahSTkwgQIECAAAECBAgQIFCpAvpFgAABAgQIECBAgAABAikg9EoFR3kFVEaAAAECBAgQIECAAAECBAiUX0CFBAgQIECAAIH/CAi9/oPgfwQIECBAoMwCaiNAgAABAgQIECBAgAABAgTKL6BCAgQihF5+CggQIECAAAECBAgQKLuA+ggQIECAAAECBAgQIECgBgSEXjUwyH9concJECBAgAABAgQIECBAgACB8guokAABAgQIECBQfgGhV/nHWIUECBAg8GcC3idAgAABAgQIECBAgAABAgTKL6BCAgRKLyD0Kv0QK5AAAQIECBAgQIDAnws4gwABAgQIECBAgAABAgQIVLuA0OvPR9AZBAgQIECAAAECBAgQIECAQPkFVEiAAAECBAgQIFDlAkKvKh9A3SdAgEDLCGiFAAECBAgQIECAAAECBAgQKL+ACgkQIFDdAkKv6h4/vSdAgAABAgQIEGgpAe0QIECAAAECBAgQIECAAAECFS3QJKFXRVeocwQIECBAgAABAgQIECBAgECTCLgJAQIECBAgQIAAgUoWEHpV8ujoGwEC1SSgrwQIECBAgAABAgQIECBAgED5BVRIgAABAhUsIPSq4MHRNQIECBAgQIBAdQnoLQECBAgQIECAAAECBAgQIFB+gcqtUOhVuWOjZwQIECBAgAABAgQIECBQbQL6S4AAAQIECBAgQIBAqwkIvVqNXsMEak9AxQQIECBAgAABAgQIECBAgED5BVRIgAABAgRaS0Do1Vry2iVAgAABAgRqUUDNBAgQIECAAAECBAgQIECAQPkFVNhKAkKvVoLXLAECBAgQIECAAAECBGpTQNUECBAgQIAAAQIECBBoHgGhV/O4uiuBMRNwFQECBAgQIECAAAECBAgQIFB+ARUSIECAAAECzSIg9GoWVjclQIAAAQIExlTAdQQIECBAgAABAgQIECBAgED5BVRIoDkEhF7NoeqeBAgQIECAAAECBAgQGHMBVxIgQIAAAQIECBAgQIDAGAgIvcYAzSWtKaBtAgQIECBAgAABAgQIECBAoPwCKiRAgAABAgQIjL6A0Gv0zVxBgAABAgRaV0DrBAgQIECAAAECBAgQIECAQPkFVEiAwGgLCL1Gm8wFBAgQIECAAAECBAi0toD2CRAgQIAAAQIECBAgQIDAbwWEXr8Vqf7nKiBAgAABAgQIECBAgAABAgTKL6BCAgQIECBAgACB3wgIvX4D4ikBAgQIlEFADQQIECBAgAABAgQIECBAgED5BVRIgACBhgJCr4YenhEgQIAAAQIECBAoh4AqCBAgQIAAAQIECBAgQIBAjQnUZOhVY2OsXAIECBAgQIAAAQIECBAgUJMCiiZAgAABAgQIEKgtAaFXbY23agkQIFAv4DsBAgQIECBAgAABAgQIECBQfgEVEiBAoKYEhF41NdyKJUCAAAECBAgQ+K+ARwQIECBAgAABAgQIECBAgECZBH4/9CpThWohQIAAAQIECBAgQIAAAQIEfl/AqwQIECBAgAABAgRKJCD0KtFgKoUAgaYVcDcCBAgQIECAAAECBAj8P/bsJbttJQYCqPa/6ncc+U2smJH4aQKFO7ByIordqIthESBAgAABAgQIEOgjoPTqsyuTEiBAoJqAeQgQIECAAAECBAgQIECAAIF8AQkJECDQRkDp1WZVBiVAgAABAgQIEKgnYCICBAgQIECAAAECBAgQIECgisB1pVeVhOYgQIAAAQIECBAgQIAAAQIErhNwMgECBAgQIECAAIEiAkqvIoswBgECmQJSESBAgAABAgQIECBAgAABAvkCEhIgQIBADQGlV409mIIAAQIECBAgkCogFwECBAgQIECAAAECBAgQIJAvUCKh0qvEGgxBgAABAgQIECBAgAABArkCkhEgQIAAAQIECBAgsEJA6bVC2R0ECPwu4AkBAgQIECBAgAABAgQIECCQLyAhAQIECBBYIKD0WoDsCgIECBAgQIDAloBnBAgQIECAAAECBAgQIECAQL6AhNcLKL2uN3YDAQIECBAgQIAAAQIECGwLeEqAAAECBAgQIECAAIHDAkqvw4QOIHC1gPMJECBAgAABAgQIECBAgACBfAEJCRAgQIAAgaMCSq+jgt4nQIAAAQIErhdwAwECBAgQIECAAAECBAgQIJAvICGBgwJKr4OAXidAgAABAgQIECBAgMAKAXcQIECAAAECBAgQIECAwLaA0mvbx9MeAqYkQIAAAQIECBAgQIAAAQIE8gUkJECAAAECBAhsCii9Nnk8JECAAAECXQTMSYAAAQIECBAgQIAAAQIECOQLSEiAwJaA0mtLxzMCBAgQIECAAAECBPoImJQAAQIECBAgQIAAAQIERgsovYasX0wCBAgQIECAAAECBAgQIEAgX0BCAgQIECBAgMBkAaXX5O3LToAAgVkC0hIgQIAAAQIECBAgQIAAAQL5AhISIDBYQOk1ePmiEyBAgAABAgQITBOQlwABAgQIECBAgAABAgQI5Aoovf7frX8JECBAgAABAgQIECBAgACBfAEJCRAgQIAAAQIEYgWUXrGrFYwAAQKfC3iDAAECBAgQIECAAAECBAgQyBeQkAABAqkCSq/UzcpFgAABAgQIECCwR8A7BAgQIECAAAECBAgQIECAQFOBD0qvpgmNTYAAAQIECBAgQIAAAQIECHwg4KcECBAgQIAAAQIEegoovXruzdQECNwl4F4CBAgQIECAAAECBAgQIEAgX0BCAgQIEGgpoPRquTZDEyBAgAABAgTuE3AzAQIECBAgQIAAAQIECBAgkC/QMaHSq+PWzEyAAAECBAgQIECAAAECdwq4mwABAgQIECBAgACBggJKr4JLMRKB3gKmJ0CAAAECBAgQIECAAAECBPIFJCRAgAABAvUElF71dmIiAgQIECBAoLuA+QkQIECAAAECBAgQIECAAIF8AQnLCSi9yq3EQAQIECBAgAABAgQIEOgvIAEBAgQIECBAgAABAgRWCyi9Vou7j8DjwYAAAQIECBAgQIAAAQIECBDIF5CQAAECBAgQWCyg9FoM7joCBAgQIEDgS8AfAQIECBAgQIAAAQIECBAgkC8gIYG1Akqvtd5uI0CAAAECBAgQIECAwFPAJwECBAgQIECAAAECBAicKqD0OpXTYWcJOIcAAQIECBAgQIAAAQIECBDIF5CQAAECBAgQIHCmgNLrTE1nESBAgACB8wScRIAAAQIECBAgQIAAAQIECOQLSEiAwIkCSq8TMR1FgAABAgQIECBAgMCZAs4iQIAAAQIECBAgQIAAAQLvCyi93req9UvTECBAgAABAgQIECBAgAABAvkCEhIgQIAAAQIECLwtoPR6m8oPCRAgQKCagHkIECBAgAABAgQIECBAgACBfAEJCRAg8K6A0utdKb8jQIAAAQIECBAgUE/ARAQIECBAgAABAgQIECBAgMC3QHDp9Z3QPwQIECBAgAABAgQIECBAgECwgGgECBAgQIAAAQIEngJKr6eDTwIECGQKSEWAAAECBAgQIECAAAECBAjkC0hIgAABAn8ElF5/GHwQIECAAAECBAikCshFgAABAgQIECBAgAABAgQI5At8JVR6fSn4I0CAAAECBAgQIECAAAECuQKSESBAgAABAgQIEBghoPQasWYhCRD4XcATAgQIECBAgAABAgQIECBAIF9AQgIECBCYIKD0mrBlGQkQIECAAAECWwKeESBAgAABAgQIECBAgAABAvkCAxIqvQYsWUQCBAgQIECAAAECBAgQ2BbwlAABAgQIECBAgACB/gJKr/47lIDA1QLOJ0CAAAECBAgQIECAAAECBPIFJCRAgAABAu0FlF7tVygAAQIECBAgcL2AGwgQIECAAAECBAgQIECAAIF8AQm7Cyi9um/Q/AQIECBAgAABAgQIEFgh4A4CBAgQIECAAAECBAgUF1B6FV+Q8XoImJIAAQIECBAgQIAAAQIECBDIF5CQAAECBAgQqC2g9Kq9H9MRIECAAIEuAuYkQIAAAQIECBAgQIAAAQIE8gUkJFBaQOlVej2GI0CAAAECBAgQIECgj4BJCRAgQIAAAQIECBAgQOBOAaXXnfqT7paVAAECBAgQIECAAAECBAgQyBeQkAABAgQIECBwo4DS60Z8VxMgQIDALAFpCRAgQIAAAQIECBAgQIAAgXwBCQkQuE9A6XWfvZsJECBAgAABAgQITBOQlwABAgQIECBAgAABAgQIXCag9LqM9tOD/Z4AAQIECBAgQIAAAQIECBDIF5CQAAECBAgQIEDgKgGl11WyziVAgACBzwW8QYAAAQIECBAgQIAAAQIECOQLSEiAAIGLBJReF8E6lgABAgQIECBAgMAeAe8QIECAAAECBAgQIECAAAEC+wQ6lV77EnqLAAECBAgQIECAAAECBAgQ6CRgVgIECBAgQIAAAQK7BJReu9i8RIAAgbsE3EuAAAECBAgQIECAAAECBAjkC0hIgAABAnsElF571LxDgAABAgQIECBwn4CbCRAgQIAAAQIECBAgQIAAgXyBHQmVXjvQvEKAAAECBAgQIECAAAECBO4UcDcBAgQIECBAgAABAq8CSq9XE98QINBbwPQECBAgQIAAAQIECBAgQIBAvoCEBAgQIEDgRUDp9ULiCwIECBAgQIBAdwHzEyBAgAABAgQIECBAgAABAvkCEv4UUHr9FPF/AgQIECBAgAABAgQIEOgvIAEBAgQIECBAgAABAuMElF7jVi4wgceDAQECBAgQIECAAAECpVYH/QAADxFJREFUBAgQIJAvICEBAgQIEJgmoPSatnF5CRAgQIAAgS8BfwQIECBAgAABAgQIECBAgEC+gITDBJRewxYuLgECBAgQIECAAAECBJ4CPgkQIECAAAECBAgQIJAloPTK2qc0Zwk4hwABAgQIECBAgAABAgQIEMgXkJAAAQIECBCIElB6Ra1TGAIECBAgcJ6AkwgQIECAAAECBAgQIECAAIF8AQkJJAkovZK2KQsBAgQIECBAgAABAmcKOIsAAQIECBAgQIAAAQIEGgkovRotq9aopiFAgAABAgQIECBAgAABAgTyBSQkQIAAAQIECPQRUHr12ZVJCRAgQKCagHkIECBAgAABAgQIECBAgACBfAEJCRBoI6D0arMqgxIgQIAAAQIECBCoJ2AiAgQIECBAgAABAgQIECBQRUDpdd0mnEyAAAECBAgQIECAAAECBAjkC0hIgAABAgQIECBQREDpVWQRxiBAgECmgFQECBAgQIAAAQIECBAgQIBAvoCEBAgQqCGg9KqxB1MQIECAAAECBAikCshFgAABAgQIECBAgAABAgQILBG4tfRaktAlBAgQIECAAAECBAgQIECAwK0CLidAgAABAgQIECCwQkDptULZHQQIEPhdwBMCBAgQIECAAAECBAgQIEAgX0BCAgQIEFggoPRagOwKAgQIECBAgACBLQHPCBAgQIAAAQIECBAgQIAAgXyB6xMqva43dgMBAgQIECBAgAABAgQIENgW8JQAAQIECBAgQIAAgcMCSq/DhA4gQOBqAecTIECAAAECBAgQIECAAAEC+QISEiBAgACBowJKr6OC3idAgAABAgQIXC/gBgIECBAgQIAAAQIECBAgQCBfQMKDAkqvg4BeJ0CAAAECBAgQIECAAIEVAu4gQIAAAQIECBAgQIDAtoDSa9vHUwI9BExJgAABAgQIECBAgAABAgQI5AtISIAAAQIECGwKKL02eTwkQIAAAQIEugiYkwABAgQIECBAgAABAgQIEMgXkJDAloDSa0vHMwIECBAgQIAAAQIECPQRMCkBAgQIECBAgAABAgRGCyi9Rq9/UnhZCRAgQIAAAQIECBAgQIAAgXwBCQkQIECAAIHJAkqvyduXnQABAgRmCUhLgAABAgQIECBAgAABAgQI5AtISGCwgNJr8PJFJ0CAAAECBAgQIDBNQF4CBAgQIECAAAECBAgQyBVQeuXu9tNkfk+AAAECBAgQIECAAAECBAjkC0hIgAABAgQIEIgVUHrFrlYwAgQIEPhcwBsECBAgQIAAAQIECBAgQIBAvoCEBAikCii9UjcrFwECBAgQIECAAIE9At4hQIAAAQIECBAgQIAAAQJNBZReHyzOTwkQIECAAAECBAgQIECAAIF8AQkJECBAgAABAgR6Cii9eu7N1AQIELhLwL0ECBAgQIAAAQIECBAgQIBAvoCEBAgQaCmg9Gq5NkMTIECAAAECBAjcJ+BmAgQIECBAgAABAgQIECBAoKLAuaVXxYRmIkCAAAECBAgQIECAAAECBM4VcBoBAgQIECBAgACBggJKr4JLMRIBAr0FTE+AAAECBAgQIECAAAECBAjkC0hIgAABAvUElF71dmIiAgQIECBAgEB3AfMTIECAAAECBAgQIECAAAEC+QLlEiq9yq3EQAQIECBAgAABAgQIECDQX0ACAgQIECBAgAABAgRWCyi9Vou7jwCBx4MBAQIECBAgQIAAAQIECBAgkC8gIQECBAgQWCyg9FoM7joCBAgQIECAwJeAPwIECBAgQIAAAQIECBAgQCBfQMK1Akqvtd5uI0CAAAECBAgQIECAAIGngE8CBAgQIECAAAECBAicKqD0OpXTYQTOEnAOAQIECBAgQIAAAQIECBAgkC8gIQECBAgQIHCmgNLrTE1nESBAgAABAucJOIkAAQIECBAgQIAAAQIECBDIF5CQwIkCSq8TMR1FgAABAgQIECBAgACBMwWcRYAAAQIECBAgQIAAAQLvCyi93rfyy1oCpiFAgAABAgQIECBAgAABAgTyBSQkQIAAAQIECLwtoPR6m8oPCRAgQIBANQHzECBAgAABAgQIECBAgAABAvkCEhIg8K6A0utdKb8jQIAAAQIECBAgQKCegIkIECBAgAABAgQIECBAgMC3gNLrGyLxH5kIECBAgAABAgQIECBAgACBfAEJCRAgQIAAAQIEngJKr6eDTwIECBDIFJCKAAECBAgQIECAAAECBAgQyBeQkAABAn8ElF5/GHwQIECAAAECBAgQSBWQiwABAgQIECBAgAABAgQIzBCYXXrN2LGUBAgQIECAAAECBAgQIEBgtoD0BAgQIECAAAECIwSUXiPWLCQBAgR+F/CEAAECBAgQIECAAAECBAgQyBeQkAABAhMElF4TtiwjAQIECBAgQIDAloBnBAgQIECAAAECBAgQIECAQIDAP0qvgIQiECBAgAABAgQIECBAgAABAv8Q8JgAAQIECBAgQIBAfwGlV/8dSkCAwNUCzidAgAABAgQIECBAgAABAgTyBSQkQIAAgfYCSq/2KxSAAAECBAgQIHC9gBsIECBAgAABAgQIECBAgACBfIHuCZVe3TdofgIECBAgQIAAAQIECBBYIeAOAgQIECBAgAABAgSKCyi9ii/IeAR6CJiSAAECBAgQIECAAAECBAgQyBeQkAABAgQI1BZQetXej+kIECBAgACBLgLmJECAAAECBAgQIECAAAECBPIFJCwtoPQqvR7DESBAgAABAgQIECBAoI+ASQkQIECAAAECBAgQIHCngNLrTn13TxKQlQABAgQIECBAgAABAgQIEMgXkJAAAQIECBC4UUDpdSO+qwkQIECAwCwBaQkQIECAAAECBAgQIECAAIF8AQkJ3Ceg9LrP3s0ECBAgQIAAAQIECEwTkJcAAQIECBAgQIAAAQIELhNQel1G6+BPBfyeAAECBAgQIECAAAECBAgQyBeQkAABAgQIECBwlYDS6ypZ5xIgQIAAgc8FvEGAAAECBAgQIECAAAECBAjkC0hIgMBFAkqvi2AdS4AAAQIECBAgQIDAHgHvECBAgAABAgQIECBAgACBfQJKr31u97zlVgIECBAgQIAAAQIECBAgQCBfQEICBAgQIECAAIFdAkqvXWxeIkCAAIG7BNxLgAABAgQIECBAgAABAgQI5AtISIAAgT0CSq89at4hQIAAAQIECBAgcJ+AmwkQIECAAAECBAgQIECAAIG/CISVXn9J6CsCBAgQIECAAAECBAgQIEAgTEAcAgQIECBAgAABAq8CSq9XE98QIECgt4DpCRAgQIAAAQIECBAgQIAAgXwBCQkQIEDgRUDp9ULiCwIECBAgQIAAge4C5idAgAABAgQIECBAgAABAgTyBX4mVHr9FPF/AgQIECBAgAABAgQIECDQX0ACAgQIECBAgAABAuMElF7jVi4wAQKPBwMCBAgQIECAAAECBAgQIEAgX0BCAgQIEJgmoPSatnF5CRAgQIAAAQJfAv4IECBAgAABAgQIECBAgACBfIFhCZVewxYuLgECBAgQIECAAAECBAg8BXwSIECAAAECBAgQIJAloPTK2qc0BM4ScA4BAgQIECBAgAABAgQIECCQLyAhAQIECBCIElB6Ra1TGAIECBAgQOA8AScRIECAAAECBAgQIECAAAEC+QISJgkovZK2KQsBAgQIECBAgAABAgTOFHAWAQIECBAgQIAAAQIEGgkovRoty6i1BExDgAABAgQIECBAgAABAgQI5AtISIAAAQIECPQRUHr12ZVJCRAgQIBANQHzECBAgAABAgQIECBAgAABAvkCEhJoI6D0arMqgxIgQIAAAQIECBAgUE/ARAQIECBAgAABAgQIECBQRUDpVWUTiXPIRIAAAQIECBAgQIAAAQIECOQLSEiAAAECBAgQKCKg9CqyCGMQIECAQKaAVAQIECBAgAABAgQIECBAgEC+gIQECNQQUHrV2IMpCBAgQIAAAQIECKQKyEWAAAECBAgQIECAAAECBJYIKL2WMP92ie8JECBAgAABAgQIECBAgACBfAEJCRAgQIAAAQIEVggovVYou4MAAQIEfhfwhAABAgQIECBAgAABAgQIEMgXkJAAAQILBJReC5BdQYAAAQIECBAgQGBLwDMCBAgQIECAAAECBAgQIEDguED10ut4QicQIECAAAECBAgQIECAAAEC1QXMR4AAAQIECBAgQOCwgNLrMKEDCBAgcLWA8wkQIECAAAECBAgQIECAAIF8AQkJECBA4KiA0uuooPcJECBAgAABAgSuF3ADAQIECBAgQIAAAQIECBAgkC9wMKHS6yCg1wkQIECAAAECBAgQIECAwAoBdxAgQIAAAQIECBAgsC2g9Nr28ZQAgR4CpiRAgAABAgQIECBAgAABAgTyBSQkQIAAAQKbAkqvTR4PCRAgQIAAAQJdBMxJgAABAgQIECBAgAABAgQI5AtIuCWg9NrS8YwAAQIECBAgQIAAAQIE+giYlAABAgQIECBAgACB0QJKr9HrF36SgKwECBAgQIAAAQIECBAgQIBAvoCEBAgQIEBgsoDSa/L2ZSdAgAABArMEpCVAgAABAgQIECBAgAABAgTyBSQcLKD0Grx80QkQIECAAAECBAgQmCYgLwECBAgQIECAAAECBHIFlF65u5XsUwG/J0CAAAECBAgQIECAAAECBPIFJCRAgAABAgRiBZResasVjAABAgQIfC7gDQIECBAgQIAAAQIECBAgQCBfQEICqQJKr9TNykWAAAECBAgQIECAwB4B7xAgQIAAAQIECBAgQIBAUwGlV9PF3TO2WwkQIECAAAECBAgQIECAAIF8AQkJECBAgAABAj0FlF4992ZqAgQIELhLwL0ECBAgQIAAAQIECBAgQIBAvoCEBAi0FFB6tVyboQkQIECAAAECBAjcJ+BmAgQIECBAgAABAgQIECBQUUDpde5WnEaAAAECBAgQIECAAAECBAjkC0hIgAABAgQIECBQUEDpVXApRiJAgEBvAdMTIECAAAECBAgQIECAAAEC+QISEiBAoJ6A0qveTkxEgAABAgQIECDQXcD8BAgQIECAAAECBAgQIECAwHKB5aXX8oQuJECAAAECBAgQIECAAAECBJYLuJAAAQIECBAgQIDAaoH/AAAA///8X17vAAAABklEQVQDADvHMK1yiX2tAAAAAElFTkSuQmCC', '2026-01-15 15:39:05', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36', '0', '', null, '', null, null, '0');

-- ----------------------------
-- Table structure for hz_commitment_template
-- ----------------------------
DROP TABLE IF EXISTS `hz_commitment_template`;
CREATE TABLE `hz_commitment_template` (
  `template_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_name` varchar(100) NOT NULL COMMENT '模板名称',
  `template_code` varchar(50) NOT NULL COMMENT '模板编码',
  `commitment_type` char(1) NOT NULL COMMENT '承诺书类型(1:人才公寓 2:保租房 3:市场租赁)',
  `template_content` text NOT NULL COMMENT '模板内容(HTML)',
  `version` varchar(20) DEFAULT '1.0' COMMENT '版本号',
  `is_default` char(1) DEFAULT '0' COMMENT '是否默认(0:否 1:是)',
  `status` char(1) DEFAULT '0' COMMENT '状态(0:正常 1:停用)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:存在 2:删除)',
  PRIMARY KEY (`template_id`),
  UNIQUE KEY `uk_template_code` (`template_code`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='承诺书模板表';

-- ----------------------------
-- Records of hz_commitment_template
-- ----------------------------
INSERT INTO `hz_commitment_template` VALUES ('1', '人才公寓申请承诺书', 'RCGY_COMMITMENT_V1.0', '1', '<h2 style=\"text-align: center;\"><strong>人才公寓申请承诺书</strong></h2>\n<p><br></p>\n<p>本人郑重承诺：</p>\n<p><br></p>\n<ol>\n  <li><strong>真实信息承诺</strong><br>本人提供的所有申请材料和信息真实、准确、完整，不存在任何虚假、隐瞒或误导性陈述。如有虚假，愿意承担由此产生的一切法律责任和后果。</li>\n  <li><br></li>\n  <li><strong>资格条件承诺</strong><br>本人符合人才公寓申请条件，包括但不限于：学历要求、工作单位要求、社保缴纳要求、住房情况要求等。如条件发生变化不再符合要求，将主动退出。</li>\n  <li><br></li>\n  <li><strong>用途限制承诺</strong><br>承租的人才公寓仅用于本人及家庭成员居住使用，不将房屋用于经营、办公或其他商业用途，不擅自转租、转借、转让或与他人调换使用。</li>\n  <li><br></li>\n  <li><strong>诚信居住承诺</strong><br>入住后将遵守相关管理规定，按时缴纳租金及各项费用，爱护房屋及配套设施，维护小区公共秩序，做文明租户。</li>\n  <li><br></li>\n  <li><strong>动态管理承诺</strong><br>理解并接受人才公寓实行动态管理，同意配合定期资格复审。如本人或家庭成员住房、收入等情况发生变化不再符合保障条件的，将在规定时间内主动退出。</li>\n  <li><br></li>\n  <li><strong>退出机制承诺</strong><br>租赁期满不再续租或因违规被取消资格的，将在规定时间内搬离并办理退房手续，按要求将房屋及设施归还，结清所有费用。</li>\n</ol>\n<p><br></p>\n<p>本人已充分知晓并理解以上承诺内容，愿意严格遵守。如有违反，自愿承担相应责任。</p>\n<p><br></p>\n<p><br></p>\n<p><strong>承诺人签字：</strong>______________</p>\n<p><strong>承诺日期：</strong>______________</p>', '1.0', '1', '0', 'admin', '2026-01-14 21:28:12', '', null, '人才公寓申请承诺书模板', '0');
INSERT INTO `hz_commitment_template` VALUES ('2', '保障性租赁住房申请承诺书', 'BZF_COMMITMENT_V1.0', '2', '<h2 style=\"text-align: center;\"><strong>保障性租赁住房申请承诺书</strong></h2>\n<p><br></p>\n<p>本人郑重承诺：</p>\n<p><br></p>\n<ol>\n  <li><strong>资格真实性承诺</strong><br>本人及家庭成员符合保障性租赁住房申请条件，提交的所有证明材料真实有效，不存在伪造、变造等情况。如有虚假，愿意承担法律责任并无条件退出。</li>\n  <li><br></li>\n  <li><strong>住房情况承诺</strong><br>本人及共同申请的家庭成员在本市无自有住房，未承租公有住房或其他保障性住房，未享受其他住房保障政策。</li>\n  <li><br></li>\n  <li><strong>收入情况承诺</strong><br>本人及家庭成员收入、财产状况符合保障性住房申请标准，如实申报家庭经济状况，不隐瞒、不虚报。</li>\n  <li><br></li>\n  <li><strong>专房专用承诺</strong><br>保障性租赁住房仅用于家庭自住，不擅自转租、转借、闲置或改变房屋用途，不利用保障房从事经营活动或违法行为。</li>\n  <li><br></li>\n  <li><strong>年度审核承诺</strong><br>同意每年按要求进行资格复审，如实提供家庭人口、住房、收入、财产等变化情况。如不再符合保障条件，自愿在3个月内退出。</li>\n  <li><br></li>\n  <li><strong>费用缴纳承诺</strong><br>按时足额缴纳租金、物业费、水电费等各项费用，不无故拖欠。</li>\n  <li><br></li>\n  <li><strong>房屋维护承诺</strong><br>合理使用房屋及配套设施，不擅自装修改造、破坏房屋结构。造成损坏的，承担维修或赔偿责任。</li>\n  <li><br></li>\n  <li><strong>退出机制承诺</strong><br>理解保障性住房的社会属性和保障性质，接受动态管理。出现以下情形之一的，自愿退出：\n    <ul>\n      <li>家庭收入、财产超过规定标准的</li>\n      <li>在本市购买、继承、受赠住房的</li>\n      <li>擅自转租、转借、闲置房屋的</li>\n      <li>无正当理由连续6个月以上未在保障房内居住的</li>\n      <li>累计拖欠租金6个月以上的</li>\n    </ul>\n  </li>\n</ol>\n<p><br></p>\n<p>本人已充分了解保障性租赁住房相关政策，自愿遵守以上承诺。如有违反，愿意承担相应法律责任和经济责任。</p>\n<p><br></p>\n<p><br></p>\n<p><strong>承诺人签字：</strong>______________</p>\n<p><strong>承诺日期：</strong>______________</p>', '1.0', '1', '0', 'admin', '2026-01-14 21:28:37', '', null, '保障性租赁住房申请承诺书模板', '0');
INSERT INTO `hz_commitment_template` VALUES ('3', '市场化租赁住房承租承诺书', 'SCZL_COMMITMENT_V1.0', '3', '<h2 style=\"text-align: center;\"><strong>市场化租赁住房承租承诺书</strong></h2>\n<p><br></p>\n<p>本人郑重承诺：</p>\n<p><br></p>\n<ol>\n  <li><strong>身份信息真实性承诺</strong><br>本人提供的身份证明、联系方式等个人信息真实准确，如有变更将及时告知出租方。</li>\n  <li><br></li>\n  <li><strong>合法居住承诺</strong><br>承租房屋仅用于正当居住目的，不从事违法犯罪活动，不利用房屋进行赌博、吸毒、卖淫、传销等非法活动。</li>\n  <li><br></li>\n  <li><strong>房屋使用承诺</strong><br>合理使用房屋及附属设施设备，不擅自改变房屋结构和用途，不私自改装水电气设施。如需装修改造，事先征得出租方书面同意。</li>\n  <li><br></li>\n  <li><strong>安全责任承诺</strong><br>严格遵守消防安全规定，不在室内存放易燃易爆危险物品，不乱拉电线，不违规使用大功率电器。做好防火防盗安全防范工作。</li>\n  <li><br></li>\n  <li><strong>邻里和谐承诺</strong><br>遵守小区管理规定和业主公约，尊重邻里，不制造噪音影响他人，维护公共环境卫生和秩序。</li>\n  <li><br></li>\n  <li><strong>费用缴纳承诺</strong><br>按合同约定按时足额缴纳租金及押金，及时缴纳水电气费、物业费、网络费等相关费用。</li>\n  <li><br></li>\n  <li><strong>转租限制承诺</strong><br>未经出租方书面同意，不擅自将房屋部分或全部转租、转借他人使用，不与他人调换使用。</li>\n  <li><br></li>\n  <li><strong>退租规范承诺</strong><br>租期届满或提前退租的，提前按合同约定时间书面通知出租方，配合办理退房手续，将房屋及设施以良好状态归还，结清所有费用。</li>\n  <li><br></li>\n  <li><strong>诚信履约承诺</strong><br>严格遵守租赁合同约定，诚实守信，履行合同义务。如违反承诺或合同约定，愿意承担相应违约责任和法律后果。</li>\n</ol>\n<p><br></p>\n<p>本人已认真阅读并充分理解以上承诺内容，自愿作出承诺并严格遵守。</p>\n<p><br></p>\n<p><br></p>\n<p><strong>承诺人签字：</strong>______________</p>\n<p><strong>承诺日期：</strong>______________</p>', '1.0', '1', '0', 'admin', '2026-01-14 21:29:02', '', null, '市场化租赁住房承租承诺书模板', '0');

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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鍚堝悓琛';

-- ----------------------------
-- Records of hz_contract
-- ----------------------------
INSERT INTO `hz_contract` VALUES ('1', 'HT20260114134542', '1', '1', '1', '张三', '410123199001011234', '13800138000', '3', '54', '101', '郑州航空港区大河路与雍州路交叉口西南角5号楼1单元101', '2200.00', '2000.00', '2026-01-14', '2026-04-14', '3', '1', '5', '<h2><strong>人才公寓租赁合同</strong></h2><p><br></p><p><strong>甲方（出租方）：</strong>港好住管理公司</p><p><strong>乙方（承租方）：</strong>张三</p><p><strong>身份证号：</strong>410123199001011234</p><p><strong>联系电话：</strong>13800138000</p><p><br></p><h3><strong>第一条 租赁房屋基本情况</strong></h3><p>甲方将位于<strong>郑州航空港区大河路与雍州路交叉口西南角5号楼1单元101</strong>的房屋出租给乙方使用。</p><p><br></p><h3><strong>第二条 租赁期限</strong></h3><p>租赁期限自<strong>2026-01-14</strong>起至<strong>2026-04-14</strong>止。</p><p><br></p><h3><strong>第三条 租金及押金</strong></h3><p>1. 月租金为人民币<strong>2200.00</strong>元（大写：<strong>贰仟贰佰元整</strong>）。</p><p>2. 押金为人民币<strong>2000.00</strong>元（大写：<strong>贰仟元整</strong>）。</p><p>3. 乙方应按<u>月</u>支付租金，每月<u>1</u>日前支付当月租金。</p><p><br></p><h3><strong>第四条 房屋使用要求</strong></h3><p>1. 乙方应合理使用并爱护房屋及其附属设施，不得擅自改变房屋结构。</p><p>2. 乙方不得利用房屋从事违法活动。</p><p>3. 乙方应按时缴纳水电费、物业费等相关费用。</p><p><br></p><h3><strong>第五条 违约责任</strong></h3><p>1. 乙方逾期支付租金超过15天的，甲方有权解除合同并收回房屋。</p><p>2. 任何一方违约的，应向对方支付相当于<strong>月租金30%</strong>的违约金。</p><p>3. 因乙方原因造成房屋及设施损坏的，乙方应负责修复或赔偿。</p><p><br></p><h3><strong>第六条 合同解除</strong></h3><p>1. 租赁期满，本合同自动终止。</p><p>2. 乙方需提前退租的，应提前<strong>30天</strong>书面通知甲方。</p><p>3. 合同解除后，甲方应在<strong>7个工作日</strong>内退还押金（扣除应付未付费用及损坏赔偿）。</p><p><br></p><h3><strong>第七条 其他约定</strong></h3><p>1. 本合同一式两份，甲乙双方各执一份，具有同等法律效力。</p><p>2. 本合同未尽事宜，双方协商解决。</p><p>3. 本合同自双方签字或盖章之日起生效。</p><p><br></p><p><br></p><p><strong>甲方（盖章）：</strong>______________        <strong>乙方（签字）：</strong>______________</p><p><strong>签订日期：</strong>______________        <strong>签订日期：</strong>______________</p>', null, '/profile/upload/2026/01/14/0c3fd659abe84c39bc014087e22bf474.png', null, '2026-01-14 13:45:42', '0', '0', null, '', null, '', null, null, '2');
INSERT INTO `hz_contract` VALUES ('2', 'HT20260114154947', '1', '1', '1', '张三', '410123199001011234', '13800138000', '3', '54', '101', '郑州航空港区大河路与雍州路交叉口西南角5号楼1单元101', '2200.00', '2000.00', '2026-01-14', '2026-04-14', '3', '1', '5', '<h2><strong>人才公寓租赁合同</strong></h2><p><br></p><p><strong>甲方（出租方）：</strong>港好住管理公司</p><p><strong>乙方（承租方）：</strong>张三</p><p><strong>身份证号：</strong>410123199001011234</p><p><strong>联系电话：</strong>13800138000</p><p><br></p><h3><strong>第一条 租赁房屋基本情况</strong></h3><p>甲方将位于<strong>郑州航空港区大河路与雍州路交叉口西南角5号楼1单元101</strong>的房屋出租给乙方使用。</p><p><br></p><h3><strong>第二条 租赁期限</strong></h3><p>租赁期限自<strong>2026-01-14</strong>起至<strong>2026-04-14</strong>止。</p><p><br></p><h3><strong>第三条 租金及押金</strong></h3><p>1. 月租金为人民币<strong>2200.00</strong>元（大写：<strong>贰仟贰佰元整</strong>）。</p><p>2. 押金为人民币<strong>2000.00</strong>元（大写：<strong>贰仟元整</strong>）。</p><p>3. 乙方应按<u>月</u>支付租金，每月<u>1</u>日前支付当月租金。</p><p><br></p><h3><strong>第四条 房屋使用要求</strong></h3><p>1. 乙方应合理使用并爱护房屋及其附属设施，不得擅自改变房屋结构。</p><p>2. 乙方不得利用房屋从事违法活动。</p><p>3. 乙方应按时缴纳水电费、物业费等相关费用。</p><p><br></p><h3><strong>第五条 违约责任</strong></h3><p>1. 乙方逾期支付租金超过15天的，甲方有权解除合同并收回房屋。</p><p>2. 任何一方违约的，应向对方支付相当于<strong>月租金30%</strong>的违约金。</p><p>3. 因乙方原因造成房屋及设施损坏的，乙方应负责修复或赔偿。</p><p><br></p><h3><strong>第六条 合同解除</strong></h3><p>1. 租赁期满，本合同自动终止。</p><p>2. 乙方需提前退租的，应提前<strong>30天</strong>书面通知甲方。</p><p>3. 合同解除后，甲方应在<strong>7个工作日</strong>内退还押金（扣除应付未付费用及损坏赔偿）。</p><p><br></p><h3><strong>第七条 其他约定</strong></h3><p>1. 本合同一式两份，甲乙双方各执一份，具有同等法律效力。</p><p>2. 本合同未尽事宜，双方协商解决。</p><p>3. 本合同自双方签字或盖章之日起生效。</p><p><br></p><p><br></p><p><strong>甲方（盖章）：</strong>______________        <strong>乙方（签字）：</strong>______________</p><p><strong>签订日期：</strong>______________        <strong>签订日期：</strong>______________</p>', null, '/profile/upload/2026/01/14/e3f850e5374d40279e0146c03d48d0da.png', null, '2026-01-14 15:49:47', '0', '0', null, '', null, '', null, null, '0');
INSERT INTO `hz_contract` VALUES ('3', 'HT20260115011930', '1', '1', '1', '张三', '410123199001011234', '13800138000', '3', '54', '101', '郑州航空港区大河路与雍州路交叉口西南角5号楼1单元101', '2200.00', '2000.00', '2026-01-15', '2026-04-15', '3', '1', '5', '<h2><strong>人才公寓租赁合同</strong></h2><p><br></p><p><strong>甲方（出租方）：</strong>港好住管理公司</p><p><strong>乙方（承租方）：</strong>张三</p><p><strong>身份证号：</strong>410123199001011234</p><p><strong>联系电话：</strong>13800138000</p><p><br></p><h3><strong>第一条 租赁房屋基本情况</strong></h3><p>甲方将位于<strong>郑州航空港区大河路与雍州路交叉口西南角5号楼1单元101</strong>的房屋出租给乙方使用。</p><p><br></p><h3><strong>第二条 租赁期限</strong></h3><p>租赁期限自<strong>2026-01-15</strong>起至<strong>2026-04-15</strong>止。</p><p><br></p><h3><strong>第三条 租金及押金</strong></h3><p>1. 月租金为人民币<strong>2200.00</strong>元（大写：<strong>贰仟贰佰元整</strong>）。</p><p>2. 押金为人民币<strong>2000.00</strong>元（大写：<strong>贰仟元整</strong>）。</p><p>3. 乙方应按<u>月</u>支付租金，每月<u>1</u>日前支付当月租金。</p><p><br></p><h3><strong>第四条 房屋使用要求</strong></h3><p>1. 乙方应合理使用并爱护房屋及其附属设施，不得擅自改变房屋结构。</p><p>2. 乙方不得利用房屋从事违法活动。</p><p>3. 乙方应按时缴纳水电费、物业费等相关费用。</p><p><br></p><h3><strong>第五条 违约责任</strong></h3><p>1. 乙方逾期支付租金超过15天的，甲方有权解除合同并收回房屋。</p><p>2. 任何一方违约的，应向对方支付相当于<strong>月租金30%</strong>的违约金。</p><p>3. 因乙方原因造成房屋及设施损坏的，乙方应负责修复或赔偿。</p><p><br></p><h3><strong>第六条 合同解除</strong></h3><p>1. 租赁期满，本合同自动终止。</p><p>2. 乙方需提前退租的，应提前<strong>30天</strong>书面通知甲方。</p><p>3. 合同解除后，甲方应在<strong>7个工作日</strong>内退还押金（扣除应付未付费用及损坏赔偿）。</p><p><br></p><h3><strong>第七条 其他约定</strong></h3><p>1. 本合同一式两份，甲乙双方各执一份，具有同等法律效力。</p><p>2. 本合同未尽事宜，双方协商解决。</p><p>3. 本合同自双方签字或盖章之日起生效。</p><p><br></p><p><br></p><p><strong>甲方（盖章）：</strong>______________        <strong>乙方（签字）：</strong>______________</p><p><strong>签订日期：</strong>______________        <strong>签订日期：</strong>______________</p>', null, '/profile/upload/2026/01/15/c7d43043c9144a4dbf249d466a6a3a54.png', null, '2026-01-15 01:19:30', '0', '0', null, '', null, '', null, null, '0');
INSERT INTO `hz_contract` VALUES ('4', 'HT20260115153918', '1', '1', '1', '张三', '410123199001011234', '13800138000', '3', '54', '101', '郑州航空港区大河路与雍州路交叉口西南角5号楼1单元101', '2200.00', '2000.00', '2026-01-15', '2026-04-15', '3', '1', '5', '<h2><strong>人才公寓租赁合同</strong></h2><p><br></p><p><strong>甲方（出租方）：</strong>港好住管理公司</p><p><strong>乙方（承租方）：</strong>张三</p><p><strong>身份证号：</strong>410123199001011234</p><p><strong>联系电话：</strong>13800138000</p><p><br></p><h3><strong>第一条 租赁房屋基本情况</strong></h3><p>甲方将位于<strong>郑州航空港区大河路与雍州路交叉口西南角5号楼1单元101</strong>的房屋出租给乙方使用。</p><p><br></p><h3><strong>第二条 租赁期限</strong></h3><p>租赁期限自<strong>2026-01-15</strong>起至<strong>2026-04-15</strong>止。</p><p><br></p><h3><strong>第三条 租金及押金</strong></h3><p>1. 月租金为人民币<strong>2200.00</strong>元（大写：<strong>贰仟贰佰元整</strong>）。</p><p>2. 押金为人民币<strong>2000.00</strong>元（大写：<strong>贰仟元整</strong>）。</p><p>3. 乙方应按<u>月</u>支付租金，每月<u>1</u>日前支付当月租金。</p><p><br></p><h3><strong>第四条 房屋使用要求</strong></h3><p>1. 乙方应合理使用并爱护房屋及其附属设施，不得擅自改变房屋结构。</p><p>2. 乙方不得利用房屋从事违法活动。</p><p>3. 乙方应按时缴纳水电费、物业费等相关费用。</p><p><br></p><h3><strong>第五条 违约责任</strong></h3><p>1. 乙方逾期支付租金超过15天的，甲方有权解除合同并收回房屋。</p><p>2. 任何一方违约的，应向对方支付相当于<strong>月租金30%</strong>的违约金。</p><p>3. 因乙方原因造成房屋及设施损坏的，乙方应负责修复或赔偿。</p><p><br></p><h3><strong>第六条 合同解除</strong></h3><p>1. 租赁期满，本合同自动终止。</p><p>2. 乙方需提前退租的，应提前<strong>30天</strong>书面通知甲方。</p><p>3. 合同解除后，甲方应在<strong>7个工作日</strong>内退还押金（扣除应付未付费用及损坏赔偿）。</p><p><br></p><h3><strong>第七条 其他约定</strong></h3><p>1. 本合同一式两份，甲乙双方各执一份，具有同等法律效力。</p><p>2. 本合同未尽事宜，双方协商解决。</p><p>3. 本合同自双方签字或盖章之日起生效。</p><p><br></p><p><br></p><p><strong>甲方（盖章）：</strong>______________        <strong>乙方（签字）：</strong>______________</p><p><strong>签订日期：</strong>______________        <strong>签订日期：</strong>______________</p>', null, '/profile/upload/2026/01/15/1270f33c161b4faa81d4c1ab182e876f.png', null, '2026-01-15 15:39:18', '0', '0', null, '', null, '', null, null, '0');
INSERT INTO `hz_contract` VALUES ('5', 'HT20260115154126', '1', '1', '1', '张三', '410123199001011234', '13800138000', '3', '54', '101', '郑州航空港区大河路与雍州路交叉口西南角5号楼1单元101', '2200.00', '2000.00', '2026-01-15', '2026-04-15', '3', '1', '5', '<h2><strong>人才公寓租赁合同</strong></h2><p><br></p><p><strong>甲方（出租方）：</strong>港好住管理公司</p><p><strong>乙方（承租方）：</strong>张三</p><p><strong>身份证号：</strong>410123199001011234</p><p><strong>联系电话：</strong>13800138000</p><p><br></p><h3><strong>第一条 租赁房屋基本情况</strong></h3><p>甲方将位于<strong>郑州航空港区大河路与雍州路交叉口西南角5号楼1单元101</strong>的房屋出租给乙方使用。</p><p><br></p><h3><strong>第二条 租赁期限</strong></h3><p>租赁期限自<strong>2026-01-15</strong>起至<strong>2026-04-15</strong>止。</p><p><br></p><h3><strong>第三条 租金及押金</strong></h3><p>1. 月租金为人民币<strong>2200.00</strong>元（大写：<strong>贰仟贰佰元整</strong>）。</p><p>2. 押金为人民币<strong>2000.00</strong>元（大写：<strong>贰仟元整</strong>）。</p><p>3. 乙方应按<u>月</u>支付租金，每月<u>1</u>日前支付当月租金。</p><p><br></p><h3><strong>第四条 房屋使用要求</strong></h3><p>1. 乙方应合理使用并爱护房屋及其附属设施，不得擅自改变房屋结构。</p><p>2. 乙方不得利用房屋从事违法活动。</p><p>3. 乙方应按时缴纳水电费、物业费等相关费用。</p><p><br></p><h3><strong>第五条 违约责任</strong></h3><p>1. 乙方逾期支付租金超过15天的，甲方有权解除合同并收回房屋。</p><p>2. 任何一方违约的，应向对方支付相当于<strong>月租金30%</strong>的违约金。</p><p>3. 因乙方原因造成房屋及设施损坏的，乙方应负责修复或赔偿。</p><p><br></p><h3><strong>第六条 合同解除</strong></h3><p>1. 租赁期满，本合同自动终止。</p><p>2. 乙方需提前退租的，应提前<strong>30天</strong>书面通知甲方。</p><p>3. 合同解除后，甲方应在<strong>7个工作日</strong>内退还押金（扣除应付未付费用及损坏赔偿）。</p><p><br></p><h3><strong>第七条 其他约定</strong></h3><p>1. 本合同一式两份，甲乙双方各执一份，具有同等法律效力。</p><p>2. 本合同未尽事宜，双方协商解决。</p><p>3. 本合同自双方签字或盖章之日起生效。</p><p><br></p><p><br></p><p><strong>甲方（盖章）：</strong>______________        <strong>乙方（签字）：</strong>______________</p><p><strong>签订日期：</strong>______________        <strong>签订日期：</strong>______________</p>', null, '/profile/upload/2026/01/15/b401c91ac66d4e13b94c368fbecaacd2.png', null, '2026-01-15 15:41:26', '0', '0', null, '', null, '', null, null, '0');

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
  `payment_cycle` varchar(20) DEFAULT NULL COMMENT '付款周期：monthly-按月，quarterly-按季，half_yearly-半年，yearly-按年',
  `contract_duration` int(11) DEFAULT NULL COMMENT '合同期限（月）',
  `deposit_months` decimal(10,2) DEFAULT NULL COMMENT '押金月数（如1表示押一付一的押金）',
  `deposit_amount` decimal(10,2) DEFAULT NULL COMMENT '固定押金金额（元，与deposit_months二选一）',
  `penalty_rate` decimal(5,2) DEFAULT NULL COMMENT '违约金比例（%）',
  `penalty_amount` decimal(10,2) DEFAULT NULL COMMENT '固定违约金金额（元，与penalty_rate二选一）',
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鍚堝悓妯℃澘琛';

-- ----------------------------
-- Records of hz_contract_template
-- ----------------------------
INSERT INTO `hz_contract_template` VALUES ('1', '人才公寓租赁合同模版', 'RCGY_V1.0', '1', '<h2><strong>人才公寓租赁合同</strong></h2><p><br></p><p><strong>甲方（出租方）：</strong>港好住管理公司</p><p><strong>乙方（承租方）：</strong>${tenantName}</p><p><strong>身份证号：</strong>${tenantIdCard}</p><p><strong>联系电话：</strong>${tenantPhone}</p><p><br></p><h3><strong>第一条 租赁房屋基本情况</strong></h3><p>甲方将位于<strong>${houseAddress}</strong>的房屋出租给乙方使用。</p><p><br></p><h3><strong>第二条 租赁期限</strong></h3><p>租赁期限自<strong>${startDate}</strong>起至<strong>${endDate}</strong>止。</p><p><br></p><h3><strong>第三条 租金及押金</strong></h3><p>1. 月租金为人民币<strong>${rentPrice}</strong>元（大写：<strong>${rentPriceUpper}</strong>）。</p><p>2. 押金为人民币<strong>${deposit}</strong>元（大写：<strong>${depositUpper}</strong>）。</p><p>3. 乙方应按<u>月</u>支付租金，每月<u>1</u>日前支付当月租金。</p><p><br></p><h3><strong>第四条 房屋使用要求</strong></h3><p>1. 乙方应合理使用并爱护房屋及其附属设施，不得擅自改变房屋结构。</p><p>2. 乙方不得利用房屋从事违法活动。</p><p>3. 乙方应按时缴纳水电费、物业费等相关费用。</p><p><br></p><h3><strong>第五条 违约责任</strong></h3><p>1. 乙方逾期支付租金超过15天的，甲方有权解除合同并收回房屋。</p><p>2. 任何一方违约的，应向对方支付相当于<strong>月租金30%</strong>的违约金。</p><p>3. 因乙方原因造成房屋及设施损坏的，乙方应负责修复或赔偿。</p><p><br></p><h3><strong>第六条 合同解除</strong></h3><p>1. 租赁期满，本合同自动终止。</p><p>2. 乙方需提前退租的，应提前<strong>30天</strong>书面通知甲方。</p><p>3. 合同解除后，甲方应在<strong>7个工作日</strong>内退还押金（扣除应付未付费用及损坏赔偿）。</p><p><br></p><h3><strong>第七条 其他约定</strong></h3><p>1. 本合同一式两份，甲乙双方各执一份，具有同等法律效力。</p><p>2. 本合同未尽事宜，双方协商解决。</p><p>3. 本合同自双方签字或盖章之日起生效。</p><p><br></p><p><br></p><p><strong>甲方（盖章）：</strong>______________        <strong>乙方（签字）：</strong>______________</p><p><strong>签订日期：</strong>______________        <strong>签订日期：</strong>______________</p>', 'monthly', '12', null, '2000.00', '30.00', null, '1.0', '1', '0', 'admin', '2026-01-14 09:48:52', '', null, '人才公寓标准租赁合同模版', '0');
INSERT INTO `hz_contract_template` VALUES ('2', '保租房租赁合同模版', 'BZF_V1.0', '2', '<h2><strong>保障性租赁住房租赁合同</strong></h2><p><br></p><p><strong>出租方：</strong>港好住管理公司</p><p><strong>承租方：</strong>${tenantName}</p><p><strong>身份证号：</strong>${tenantIdCard}</p><p><strong>联系电话：</strong>${tenantPhone}</p><p><br></p><h3><strong>一、房屋基本信息</strong></h3><p>房屋地址：<strong>${houseAddress}</strong></p><p><br></p><h3><strong>二、租赁期限及租金</strong></h3><p>1. 租期：自<strong>${startDate}</strong>至<strong>${endDate}</strong></p><p>2. 月租金：<strong>${rentPrice}</strong>元</p><p>3. 押金：<strong>${deposit}</strong>元</p><p>4. 付款方式：<u>按季</u>支付，每季度首月<u>5</u>日前支付。</p><p><br></p><h3><strong>三、保障性住房特别条款</strong></h3><p>1. 本房屋为保障性租赁住房，承租方需符合保租房申请条件。</p><p>2. 承租方不得将房屋转租、转借或与他人调换使用。</p><p>3. 承租方应按规定每年进行资格复审。</p><p>4. 承租方家庭收入、住房等情况发生变化不再符合条件的，应主动退出。</p><p><br></p><h3><strong>四、双方权利义务</strong></h3><p><strong>（一）出租方义务：</strong></p><p>1. 保证房屋符合居住条件，设施设备完好。</p><p>2. 负责房屋主体结构的维修。</p><p>3. 不得随意涨租或单方解除合同。</p><p><br></p><p><strong>（二）承租方义务：</strong></p><p>1. 按时缴纳租金及相关费用。</p><p>2. 合理使用房屋，不得改变房屋用途。</p><p>3. 配合出租方进行房屋检查和资格复审。</p><p><br></p><h3><strong>五、违约责任</strong></h3><p>1. 承租方违规转租的，出租方有权解除合同并要求支付违约金<strong>5000元</strong>。</p><p>2. 承租方逾期缴纳租金超过30天的，出租方有权解除合同。</p><p>3. 承租方故意损坏房屋的，应承担修复费用及违约金<strong>5000元</strong>。</p><p><br></p><h3><strong>六、合同终止</strong></h3><p>1. 租期届满，合同自然终止。</p><p>2. 承租方不再符合保障条件的，应在<strong>3个月</strong>内退房。</p><p>3. 押金在承租方结清所有费用后<strong>15日</strong>内退还。</p><p><br></p><p><br></p><p><strong>出租方（盖章）：</strong>______________</p><p><strong>承租方（签字）：</strong>______________</p><p><strong>签订日期：</strong>______________</p>', 'quarterly', '24', null, '3000.00', null, '5000.00', '1.0', '1', '0', 'admin', '2026-01-14 09:49:20', '', null, '保租房标准租赁合同模版', '0');
INSERT INTO `hz_contract_template` VALUES ('3', '市场租赁合同模版', 'SCZL_V1.0', '3', '<h2 style=\"text-align: center;\"><strong>房屋租赁合同</strong></h2><p><br></p><p><strong>出租方（甲方）：</strong>港好住管理公司</p><p><strong>承租方（乙方）：</strong>${tenantName}</p><p><strong>身份证号码：</strong>${tenantIdCard}</p><p><strong>联系方式：</strong>${tenantPhone}</p><p><br></p><p>根据《中华人民共和国民法典》等相关法律法规，甲乙双方在平等、自愿的基础上，就房屋租赁事宜达成如下协议：</p><p><br></p><h3><strong>第一条 房屋基本情况</strong></h3><p>甲方将其合法拥有的位于<strong>${houseAddress}</strong>的房屋出租给乙方使用。</p><p><br></p><h3><strong>第二条 租赁用途</strong></h3><p>该房屋用途为<u>居住使用</u>。乙方不得擅自改变房屋用途，不得利用该房屋从事违法活动。</p><p><br></p><h3><strong>第三条 租赁期限</strong></h3><p>1. 租赁期限：自<strong>${startDate}</strong>起至<strong>${endDate}</strong>止。</p><p>2. 租赁期满，如乙方需续租，应提前<strong>30日</strong>向甲方提出书面申请。</p><p><br></p><h3><strong>第四条 租金及支付方式</strong></h3><p>1. 月租金：人民币<strong>${rentPrice}</strong>元（大写：<strong>${rentPriceUpper}</strong>）。</p><p>2. 押金：人民币<strong>${deposit}</strong>元（大写：<strong>${depositUpper}</strong>）。</p><p>3. 支付方式：<u>按半年</u>支付，每半年首月<u>5</u>日前支付。</p><p>4. 逾期支付租金的，每逾期1日，乙方应按日租金的<strong>3%</strong>支付滞纳金。</p><p><br></p><h3><strong>第五条 房屋交付及返还</strong></h3><p>1. 甲方应确保交付的房屋及附属设施、设备完好，符合居住条件。</p><p>2. 租赁期满或合同解除，乙方应将房屋及设施以良好状态返还甲方。</p><p>3. 乙方未按约定返还的，应按日支付占用费用。</p><p><br></p><h3><strong>第六条 房屋维修与使用</strong></h3><p>1. 甲方负责房屋主体结构及附属设施的维修。</p><p>2. 乙方应合理使用并妥善保管房屋及设施。</p><p>3. 乙方不得擅自拆改房屋结构或改变房屋用途。</p><p>4. 因乙方使用不当造成的损坏，由乙方负责修复或赔偿。</p><p><br></p><h3><strong>第七条 费用承担</strong></h3><p>租赁期间，水费、电费、燃气费、物业费、网络费等日常使用费用由<u>乙方</u>承担。</p><p><br></p><h3><strong>第八条 转租</strong></h3><p>未经甲方书面同意，乙方不得将房屋部分或全部转租。</p><p><br></p><h3><strong>第九条 违约责任</strong></h3><p>1. 甲方违约：</p><p>   （1）未按约定提供房屋的，应退还已收租金并支付违约金。</p><p>   （2）擅自收回房屋的，应双倍返还押金。</p><p><br></p><p>2. 乙方违约：</p><p>   （1）逾期支付租金超过<strong>30日</strong>的，甲方有权解除合同并没收押金。</p><p>   （2）擅自转租、改变用途的，应支付<strong>2个月</strong>租金的违约金。</p><p>   （3）提前退租的，押金不予退还。</p><p><br></p><h3><strong>第十条 合同解除</strong></h3><p>1. 有下列情形之一的，甲方可解除合同：</p><p>   - 乙方逾期支付租金超过30日的；</p><p>   - 乙方擅自改变房屋用途或转租的；</p><p>   - 乙方利用房屋从事违法活动的。</p><p><br></p><p>2. 有下列情形之一的，乙方可解除合同：</p><p>   - 房屋存在安全隐患危及人身安全的；</p><p>   - 甲方未经乙方同意擅自进入房屋的。</p><p><br></p><h3><strong>第十一条 争议解决</strong></h3><p>本合同履行过程中发生的争议，由双方协商解决；协商不成的，可向房屋所在地人民法院提起诉讼。</p><p><br></p><h3><strong>第十二条 其他约定</strong></h3><p>1. 本合同一式<u>两</u>份，甲乙双方各执一份，具有同等法律效力。</p><p>2. 本合同自双方签字或盖章之日起生效。</p><p>3. 本合同未尽事宜，双方可另行签订补充协议。</p><p><br></p><p><br></p><p><br></p><p><strong>甲方（盖章）：</strong>______________          <strong>乙方（签字）：</strong>______________</p><p><strong>签订日期：</strong>______________              <strong>签订日期：</strong>______________</p>', 'half_yearly', '12', null, '4000.00', '200.00', null, '1.0', '1', '0', 'admin', '2026-01-14 09:49:58', '', null, '市场租赁标准合同模版', '0');

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
-- Table structure for hz_enterprise_batch
-- ----------------------------
DROP TABLE IF EXISTS `hz_enterprise_batch`;
CREATE TABLE `hz_enterprise_batch` (
  `batch_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '批次ID',
  `batch_no` varchar(50) NOT NULL COMMENT '批次编号',
  `batch_name` varchar(100) NOT NULL COMMENT '批次名称',
  `enterprise_id` bigint(20) DEFAULT NULL COMMENT '企业ID(关联hz_enterprise表)',
  `enterprise_name` varchar(100) NOT NULL COMMENT '单位名称',
  `contact_person` varchar(50) NOT NULL COMMENT '联系人',
  `contact_phone` varchar(20) NOT NULL COMMENT '联系方式',
  `project_ids` varchar(500) DEFAULT NULL COMMENT '项目ID列表(逗号分隔)',
  `house_count` int(11) DEFAULT '0' COMMENT '房源数量',
  `selection_start_date` date DEFAULT NULL COMMENT '选房开始日期',
  `selection_end_date` date DEFAULT NULL COMMENT '选房结束日期',
  `approve_status` char(1) DEFAULT '0' COMMENT '审批状态(0待审批 1已通过 2已拒绝)',
  `approve_by` varchar(64) DEFAULT NULL COMMENT '审批人',
  `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
  `batch_status` char(1) DEFAULT '0' COMMENT '批次状态(0进行中 1已完成 2已作废)',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0正常 2删除)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`batch_id`),
  UNIQUE KEY `uk_batch_no` (`batch_no`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='企业批次表';

-- ----------------------------
-- Records of hz_enterprise_batch
-- ----------------------------
INSERT INTO `hz_enterprise_batch` VALUES ('1', 'EP202601120001', 'QY206584', null, '郑州港区投资集团有限公司', '张正', '18696356545', '6,5,2,4,3', '3', '2026-01-12', '2026-01-12', '0', null, null, '0', '0', '', '2026-01-12 22:44:49', '', null, null);

-- ----------------------------
-- Table structure for hz_enterprise_batch_house
-- ----------------------------
DROP TABLE IF EXISTS `hz_enterprise_batch_house`;
CREATE TABLE `hz_enterprise_batch_house` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `batch_id` bigint(20) NOT NULL COMMENT '批次ID',
  `house_id` bigint(20) NOT NULL COMMENT '房源ID',
  `house_code` varchar(50) NOT NULL COMMENT '房源编号',
  `allocation_status` char(1) DEFAULT '1' COMMENT '分配状态(0未分配 1已分配)',
  `allocation_time` datetime DEFAULT NULL COMMENT '分配时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0正常 2删除)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_batch_id` (`batch_id`),
  KEY `idx_house_id` (`house_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='企业批次房源表';

-- ----------------------------
-- Records of hz_enterprise_batch_house
-- ----------------------------
INSERT INTO `hz_enterprise_batch_house` VALUES ('1', '1', '1', 'HOUSE-1', '1', '2026-01-12 22:44:49', '0', 'admin', '2026-01-12 22:44:49', '', null, null);
INSERT INTO `hz_enterprise_batch_house` VALUES ('2', '1', '4', 'HOUSE-4', '1', '2026-01-12 22:44:49', '0', 'admin', '2026-01-12 22:44:49', '', null, null);
INSERT INTO `hz_enterprise_batch_house` VALUES ('3', '1', '5', 'HOUSE-5', '1', '2026-01-12 22:44:49', '0', 'admin', '2026-01-12 22:44:49', '', null, null);

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
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鎴挎簮琛';

-- ----------------------------
-- Records of hz_house
-- ----------------------------
INSERT INTO `hz_house` VALUES ('1', '2', '3', '5', '123', '456', '1', '14', '大三房', '60.00', '2000.00', '3000.00', '南', '毛坯', null, '0', '0', '0', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house` VALUES ('2', '1', '5', '11', 'kl', '502', '1', '10', '阿斯蒂芬456465', '0.00', '500.00', '500.00', '东', null, null, '0', '0', '0', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house` VALUES ('3', '1', '1', '1', 'llkkk', '456', '1', '10', '阿斯蒂芬456465', '60.00', '9000.00', '6000.00', '南', '毛坯', '阿斯蒂芬', '0', '1', '0', '0', '', null, '', null, '阿斯蒂芬', '0');
INSERT INTO `hz_house` VALUES ('4', '3', '6', '12', '01', '123', '1', '15', '测试01', '600.00', '5000.00', '200.00', '南', '毛坯', '少时诵诗书', '0', '0', '0', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house` VALUES ('5', '4', '7', '13', '12', '301', '1', '16', '大三房', '180.00', '2000.00', '1000.00', '东', null, null, '0', '0', '0', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house` VALUES ('6', '3', '6', '12', '3-6-12-101', '101', '1', null, '一室一厅', '45.00', '1500.00', '3000.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:15:08', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('7', '3', '6', '12', '3-6-12-201', '201', '2', null, '一室一厅', '45.00', '1500.00', '3000.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:15:08', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('8', '3', '6', '12', '3-6-12-301', '301', '3', null, '一室一厅', '45.00', '1500.00', '3000.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:15:08', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('9', '3', '6', '14', '3-6-14-101', '101', '1', null, '两室一厅', '65.00', '2000.00', '4000.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:15:08', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('10', '3', '6', '14', '3-6-14-201', '201', '2', null, '两室一厅', '65.00', '2000.00', '4000.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:15:08', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('11', '3', '6', '14', '3-6-14-301', '301', '3', null, '两室一厅', '65.00', '2000.00', '4000.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:15:08', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('12', '3', '6', '15', '3-6-15-101', '101', '1', null, '三室两厅', '90.00', '2800.00', '5600.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:15:08', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('13', '3', '6', '15', '3-6-15-201', '201', '2', null, '三室两厅', '90.00', '2800.00', '5600.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:15:08', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('14', '3', '6', '15', '3-6-15-301', '301', '3', null, '三室两厅', '90.00', '2800.00', '5600.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:15:08', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('15', '3', '6', '16', '3-6-16-101', '101', '1', null, '一室一厅', '50.00', '1600.00', '3200.00', '东', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:15:08', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('16', '3', '6', '16', '3-6-16-201', '201', '2', null, '一室一厅', '50.00', '1600.00', '3200.00', '东', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:15:08', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('17', '3', '6', '16', '3-6-16-301', '301', '3', null, '一室一厅', '50.00', '1600.00', '3200.00', '东', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:15:08', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('18', '3', '8', '17', '3-8-17-101', '101', '1', null, '两室一厅', '70.00', '2100.00', '4200.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:15:27', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('19', '3', '8', '17', '3-8-17-201', '201', '2', null, '两室一厅', '70.00', '2100.00', '4200.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:15:27', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('20', '3', '8', '17', '3-8-17-301', '301', '3', null, '两室一厅', '70.00', '2100.00', '4200.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:15:27', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('21', '3', '8', '18', '3-8-18-101', '101', '1', null, '一室一厅', '55.00', '1700.00', '3400.00', '西', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:15:27', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('22', '3', '8', '18', '3-8-18-201', '201', '2', null, '一室一厅', '55.00', '1700.00', '3400.00', '西', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:15:27', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('23', '3', '8', '18', '3-8-18-301', '301', '3', null, '一室一厅', '55.00', '1700.00', '3400.00', '西', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:15:27', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('24', '3', '8', '19', '3-8-19-101', '101', '1', null, '三室两厅', '95.00', '2900.00', '5800.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:15:27', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('25', '3', '8', '19', '3-8-19-201', '201', '2', null, '三室两厅', '95.00', '2900.00', '5800.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:15:27', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('26', '3', '8', '19', '3-8-19-301', '301', '3', null, '三室两厅', '95.00', '2900.00', '5800.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:15:27', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('27', '3', '9', '20', '3-9-20-101', '101', '1', null, '两室一厅', '68.00', '2050.00', '4100.00', '东南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:15:27', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('28', '3', '9', '20', '3-9-20-201', '201', '2', null, '两室一厅', '68.00', '2050.00', '4100.00', '东南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:15:27', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('29', '3', '9', '20', '3-9-20-301', '301', '3', null, '两室一厅', '68.00', '2050.00', '4100.00', '东南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:15:27', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('30', '3', '9', '21', '3-9-21-101', '101', '1', null, '一室一厅', '48.00', '1550.00', '3100.00', '北', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:05', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('31', '3', '9', '21', '3-9-21-201', '201', '2', null, '一室一厅', '48.00', '1550.00', '3100.00', '北', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:05', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('32', '3', '9', '21', '3-9-21-301', '301', '3', null, '一室一厅', '48.00', '1550.00', '3100.00', '北', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:05', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('33', '3', '9', '22', '3-9-22-101', '101', '1', null, '两室两厅', '75.00', '2300.00', '4600.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:05', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('34', '3', '9', '22', '3-9-22-201', '201', '2', null, '两室两厅', '75.00', '2300.00', '4600.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:05', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('35', '3', '9', '22', '3-9-22-301', '301', '3', null, '两室两厅', '75.00', '2300.00', '4600.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:05', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('36', '3', '10', '23', '3-10-23-101', '101', '1', null, '三室两厅', '92.00', '2850.00', '5700.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:05', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('37', '3', '10', '23', '3-10-23-201', '201', '2', null, '三室两厅', '92.00', '2850.00', '5700.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:05', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('38', '3', '10', '23', '3-10-23-301', '301', '3', null, '三室两厅', '92.00', '2850.00', '5700.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:05', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('39', '3', '10', '24', '3-10-24-101', '101', '1', null, '两室一厅', '66.00', '2000.00', '4000.00', '西南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:05', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('40', '3', '10', '24', '3-10-24-201', '201', '2', null, '两室一厅', '66.00', '2000.00', '4000.00', '西南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:05', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('41', '3', '10', '24', '3-10-24-301', '301', '3', null, '两室一厅', '66.00', '2000.00', '4000.00', '西南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:05', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('42', '3', '10', '25', '3-10-25-101', '101', '1', null, '一室一厅', '52.00', '1650.00', '3300.00', '东', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:24', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('43', '3', '10', '25', '3-10-25-201', '201', '2', null, '一室一厅', '52.00', '1650.00', '3300.00', '东', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:24', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('44', '3', '10', '25', '3-10-25-301', '301', '3', null, '一室一厅', '52.00', '1650.00', '3300.00', '东', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:24', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('45', '3', '11', '26', '3-11-26-101', '101', '1', null, '两室两厅', '78.00', '2400.00', '4800.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:24', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('46', '3', '11', '26', '3-11-26-201', '201', '2', null, '两室两厅', '78.00', '2400.00', '4800.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:24', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('47', '3', '11', '26', '3-11-26-301', '301', '3', null, '两室两厅', '78.00', '2400.00', '4800.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:24', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('48', '3', '11', '27', '3-11-27-101', '101', '1', null, '三室两厅', '98.00', '3000.00', '6000.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:24', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('49', '3', '11', '27', '3-11-27-201', '201', '2', null, '三室两厅', '98.00', '3000.00', '6000.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:24', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('50', '3', '11', '27', '3-11-27-301', '301', '3', null, '三室两厅', '98.00', '3000.00', '6000.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:24', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('51', '3', '11', '28', '3-11-28-101', '101', '1', null, '一室一厅', '49.00', '1580.00', '3160.00', '西', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:24', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('52', '3', '11', '28', '3-11-28-201', '201', '2', null, '一室一厅', '49.00', '1580.00', '3160.00', '西', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:24', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('53', '3', '11', '28', '3-11-28-301', '301', '3', null, '一室一厅', '49.00', '1580.00', '3160.00', '西', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:24', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('54', '3', '12', '29', '3-12-29-101', '101', '1', '17', '一室', '72.00', '2200.00', '4400.00', '东南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:41', '', null, '不错的房源哦', '0');
INSERT INTO `hz_house` VALUES ('55', '3', '12', '29', '3-12-29-201', '201', '2', '24', '一室', '72.00', '2200.00', '4400.00', '东南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:41', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('56', '3', '12', '29', '3-12-29-301', '301', '3', '25', '两室', '72.00', '2200.00', '4400.00', '东南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:41', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('57', '3', '12', '30', '3-12-30-101', '101', '1', '26', '三室', '88.00', '2750.00', '5500.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:41', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('58', '3', '12', '30', '3-12-30-201', '201', '2', '26', '三室', '88.00', '2750.00', '5500.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:41', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('59', '3', '12', '30', '3-12-30-301', '301', '3', '26', '三室', '88.00', '2750.00', '5500.00', '南', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:41', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('60', '3', '12', '31', '3-12-31-101', '101', '1', null, '一室一厅', '51.00', '1620.00', '3240.00', '北', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:41', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('61', '3', '12', '31', '3-12-31-201', '201', '2', null, '一室一厅', '51.00', '1620.00', '3240.00', '北', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:41', '', null, null, '0');
INSERT INTO `hz_house` VALUES ('62', '3', '12', '31', '3-12-31-301', '301', '3', null, '一室一厅', '51.00', '1620.00', '3240.00', '北', '精装', null, '0', '0', '0', '0', '', '2026-01-14 00:16:41', '', null, null, '0');

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
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鎴挎簮鍥剧墖琛';

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
INSERT INTO `hz_house_image` VALUES ('18', '54', '/profile/upload/2026/01/14/矩形 21@2x_20260114024633A001.png', '1', '1', '0', '', null, '', null, null, '2');
INSERT INTO `hz_house_image` VALUES ('19', '54', '/profile/upload/2026/01/14/矩形 2@2x(1)_20260114024637A002.png', '2', '0', '1', '', null, '', null, null, '2');
INSERT INTO `hz_house_image` VALUES ('20', '54', '/profile/upload/2026/01/14/人才家园@2x_20260114024639A003.png', '3', '0', '2', '', null, '', null, null, '2');
INSERT INTO `hz_house_image` VALUES ('21', '54', '/profile/upload/2026/01/14/人才公寓@2x_20260114024641A004.png', '3', '0', '3', '', null, '', null, null, '2');
INSERT INTO `hz_house_image` VALUES ('22', '54', '/profile/upload/2026/01/14/服务@2x_20260114024645A005.png', '3', '0', '4', '', null, '', null, null, '2');
INSERT INTO `hz_house_image` VALUES ('23', '54', '/profile/upload/2026/01/14/矩形 2@2x_20260114024648A006.png', '4', '0', '5', '', null, '', null, null, '2');
INSERT INTO `hz_house_image` VALUES ('24', '54', '/profile/upload/2026/01/14/矩形 21@2x_20260114024650A007.png', '5', '0', '6', '', null, '', null, null, '2');
INSERT INTO `hz_house_image` VALUES ('25', '54', '/profile/upload/2026/01/14/矩形 21@2x_20260114024633A001.png', '1', '1', '0', '', null, '', null, null, '2');
INSERT INTO `hz_house_image` VALUES ('26', '54', '/profile/upload/2026/01/14/矩形 2@2x(1)_20260114024637A002.png', '2', '0', '1', '', null, '', null, null, '2');
INSERT INTO `hz_house_image` VALUES ('27', '54', '/profile/upload/2026/01/14/人才家园@2x_20260114024639A003.png', '3', '0', '2', '', null, '', null, null, '2');
INSERT INTO `hz_house_image` VALUES ('28', '54', '/profile/upload/2026/01/14/人才公寓@2x_20260114024641A004.png', '3', '0', '3', '', null, '', null, null, '2');
INSERT INTO `hz_house_image` VALUES ('29', '54', '/profile/upload/2026/01/14/服务@2x_20260114024645A005.png', '3', '0', '4', '', null, '', null, null, '2');
INSERT INTO `hz_house_image` VALUES ('30', '54', '/profile/upload/2026/01/14/矩形 2@2x_20260114024648A006.png', '4', '0', '5', '', null, '', null, null, '2');
INSERT INTO `hz_house_image` VALUES ('31', '54', '/profile/upload/2026/01/14/矩形 21@2x_20260114024650A007.png', '5', '0', '6', '', null, '', null, null, '2');
INSERT INTO `hz_house_image` VALUES ('32', '54', '/profile/upload/2026/01/14/矩形 21@2x_20260114024633A001.png', '1', '1', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house_image` VALUES ('33', '54', '/profile/upload/2026/01/14/矩形 2@2x(1)_20260114024637A002.png', '2', '0', '1', '', null, '', null, null, '0');
INSERT INTO `hz_house_image` VALUES ('34', '54', '/profile/upload/2026/01/14/人才家园@2x_20260114024639A003.png', '3', '0', '2', '', null, '', null, null, '0');
INSERT INTO `hz_house_image` VALUES ('35', '54', '/profile/upload/2026/01/14/人才公寓@2x_20260114024641A004.png', '3', '0', '3', '', null, '', null, null, '0');
INSERT INTO `hz_house_image` VALUES ('36', '54', '/profile/upload/2026/01/14/服务@2x_20260114024645A005.png', '3', '0', '4', '', null, '', null, null, '0');
INSERT INTO `hz_house_image` VALUES ('37', '54', '/profile/upload/2026/01/14/矩形 2@2x_20260114024648A006.png', '4', '0', '5', '', null, '', null, null, '0');
INSERT INTO `hz_house_image` VALUES ('38', '54', '/profile/upload/2026/01/14/矩形 21@2x_20260114024650A007.png', '5', '0', '6', '', null, '', null, null, '0');

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
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鎴峰瀷琛';

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
INSERT INTO `hz_house_type` VALUES ('17', '3', '一室', 'yishiyiting', '1', '1', '1', '1', '1', '60.00', null, '0', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('18', '2', '一室', 'JXJY2024-1S', '1', '1', '1', '1', '0', '60.00', null, '0', '1', '', '2026-01-14 02:19:19', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('19', '2', '两室', 'JXJY2024-2S', '2', '2', '1', '1', '1', '90.00', null, '0', '2', '', '2026-01-14 02:19:19', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('20', '2', '三室', 'JXJY2024-3S', '3', '3', '2', '1', '1', '120.00', null, '0', '3', '', '2026-01-14 02:19:19', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('21', '2', '四室', 'JXJY2024-4S', '4', '4', '2', '1', '1', '150.00', null, '0', '4', '', '2026-01-14 02:19:19', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('22', '2', '五室', 'JXJY2024-5S', '5', '5', '2', '1', '1', '180.00', null, '0', '5', '', '2026-01-14 02:19:19', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('23', '2', '六室', 'JXJY2024-6S', '6', '6', '2', '1', '1', '210.00', null, '0', '6', '', '2026-01-14 02:19:19', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('24', '3', '一室', 'YJY2024-1S', '1', '1', '1', '1', '0', '60.00', null, '0', '1', '', '2026-01-14 02:19:19', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('25', '3', '两室', 'YJY2024-2S', '2', '2', '1', '1', '1', '90.00', null, '0', '2', '', '2026-01-14 02:19:19', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('26', '3', '三室', 'YJY2024-3S', '3', '3', '2', '1', '1', '120.00', null, '0', '3', '', '2026-01-14 02:19:19', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('27', '3', '四室', 'YJY2024-4S', '4', '4', '2', '1', '1', '150.00', null, '0', '4', '', '2026-01-14 02:19:19', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('28', '3', '五室', 'YJY2024-5S', '5', '5', '2', '1', '1', '180.00', null, '0', '5', '', '2026-01-14 02:19:19', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('29', '3', '六室', 'YJY2024-6S', '6', '6', '2', '1', '1', '210.00', null, '0', '6', '', '2026-01-14 02:19:19', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('30', '4', '一室', 'wanke-1S', '1', '1', '1', '1', '0', '60.00', null, '0', '1', '', '2026-01-14 02:19:19', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('31', '4', '两室', 'wanke-2S', '2', '2', '1', '1', '1', '90.00', null, '0', '2', '', '2026-01-14 02:19:19', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('32', '4', '三室', 'wanke-3S', '3', '3', '2', '1', '1', '120.00', null, '0', '3', '', '2026-01-14 02:19:19', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('33', '4', '四室', 'wanke-4S', '4', '4', '2', '1', '1', '150.00', null, '0', '4', '', '2026-01-14 02:19:19', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('34', '4', '五室', 'wanke-5S', '5', '5', '2', '1', '1', '180.00', null, '0', '5', '', '2026-01-14 02:19:19', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('35', '4', '六室', 'wanke-6S', '6', '6', '2', '1', '1', '210.00', null, '0', '6', '', '2026-01-14 02:19:19', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('36', '5', '一室', '3695-1S', '1', '1', '1', '1', '0', '60.00', null, '0', '1', '', '2026-01-14 02:19:19', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('37', '5', '两室', '3695-2S', '2', '2', '1', '1', '1', '90.00', null, '0', '2', '', '2026-01-14 02:19:19', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('38', '5', '三室', '3695-3S', '3', '3', '2', '1', '1', '120.00', null, '0', '3', '', '2026-01-14 02:19:19', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('39', '5', '四室', '3695-4S', '4', '4', '2', '1', '1', '150.00', null, '0', '4', '', '2026-01-14 02:19:19', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('40', '5', '五室', '3695-5S', '5', '5', '2', '1', '1', '180.00', null, '0', '5', '', '2026-01-14 02:19:19', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('41', '5', '六室', '3695-6S', '6', '6', '2', '1', '1', '210.00', null, '0', '6', '', '2026-01-14 02:19:19', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('42', '6', '一室', 'hengda-1S', '1', '1', '1', '1', '0', '60.00', null, '0', '1', '', '2026-01-14 02:19:38', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('43', '6', '两室', 'hengda-2S', '2', '2', '1', '1', '1', '90.00', null, '0', '2', '', '2026-01-14 02:19:38', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('44', '6', '三室', 'hengda-3S', '3', '3', '2', '1', '1', '120.00', null, '0', '3', '', '2026-01-14 02:19:38', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('45', '6', '四室', 'hengda-4S', '4', '4', '2', '1', '1', '150.00', null, '0', '4', '', '2026-01-14 02:19:38', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('46', '6', '五室', 'hengda-5S', '5', '5', '2', '1', '1', '180.00', null, '0', '5', '', '2026-01-14 02:19:38', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('47', '6', '六室', 'hengda-6S', '6', '6', '2', '1', '1', '210.00', null, '0', '6', '', '2026-01-14 02:19:38', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('48', '9', '一室', 'HNXC-RC-2024001-1S', '1', '1', '1', '1', '0', '60.00', null, '0', '1', '', '2026-01-14 02:19:38', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('49', '9', '两室', 'HNXC-RC-2024001-2S', '2', '2', '1', '1', '1', '90.00', null, '0', '2', '', '2026-01-14 02:19:38', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('50', '9', '三室', 'HNXC-RC-2024001-3S', '3', '3', '2', '1', '1', '120.00', null, '0', '3', '', '2026-01-14 02:19:38', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('51', '9', '四室', 'HNXC-RC-2024001-4S', '4', '4', '2', '1', '1', '150.00', null, '0', '4', '', '2026-01-14 02:19:38', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('52', '9', '五室', 'HNXC-RC-2024001-5S', '5', '5', '2', '1', '1', '180.00', null, '0', '5', '', '2026-01-14 02:19:38', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('53', '9', '六室', 'HNXC-RC-2024001-6S', '6', '6', '2', '1', '1', '210.00', null, '0', '6', '', '2026-01-14 02:19:38', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('54', '10', '一室', 'GQZH-RC-2024002-1S', '1', '1', '1', '1', '0', '60.00', null, '0', '1', '', '2026-01-14 02:19:38', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('55', '10', '两室', 'GQZH-RC-2024002-2S', '2', '2', '1', '1', '1', '90.00', null, '0', '2', '', '2026-01-14 02:19:38', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('56', '10', '三室', 'GQZH-RC-2024002-3S', '3', '3', '2', '1', '1', '120.00', null, '0', '3', '', '2026-01-14 02:19:38', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('57', '10', '四室', 'GQZH-RC-2024002-4S', '4', '4', '2', '1', '1', '150.00', null, '0', '4', '', '2026-01-14 02:19:38', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('58', '10', '五室', 'GQZH-RC-2024002-5S', '5', '5', '2', '1', '1', '180.00', null, '0', '5', '', '2026-01-14 02:19:38', '', null, null, '0');
INSERT INTO `hz_house_type` VALUES ('59', '10', '六室', 'GQZH-RC-2024002-6S', '6', '6', '2', '1', '1', '210.00', null, '0', '6', '', '2026-01-14 02:19:38', '', null, null, '0');

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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鎴挎簮VR琛';

-- ----------------------------
-- Records of hz_house_vr
-- ----------------------------
INSERT INTO `hz_house_vr` VALUES ('1', '3', 'VR1', '/profile/upload/2026/01/11/QP61014284_20260111033434A001.jpg', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house_vr` VALUES ('2', '4', 'VR1', '/profile/upload/2026/01/11/QP61014284_20260111151706A002.jpg', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house_vr` VALUES ('3', '5', 'VR1', '/profile/upload/2026/01/12/QP61014284_20260112014202A006.jpg', '0', '', null, '', null, null, '2');
INSERT INTO `hz_house_vr` VALUES ('4', '5', 'VR1', '/profile/upload/2026/01/12/QP61014284_20260112014202A006.jpg', '0', '', null, '', null, null, '0');
INSERT INTO `hz_house_vr` VALUES ('5', '54', 'VR1', '/profile/upload/2026/01/14/quanjing_20260114025108A008.png', '0', '', null, '', null, null, '2');
INSERT INTO `hz_house_vr` VALUES ('6', '54', 'VR1', '/profile/upload/2026/01/14/quanjing_20260114025108A008.png', '0', '', null, '', null, null, '0');

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
  `config_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置类型（banner=轮播图 ad=广告位 notice=通知公告 icon=功能图标）',
  `title` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题/名称',
  `image_url` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '图片地址（相对路径）',
  `content` text COLLATE utf8mb4_general_ci COMMENT '文字描述/内容',
  `link_url` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '跳转链接',
  `link_type` varchar(20) COLLATE utf8mb4_general_ci DEFAULT 'none' COMMENT '链接类型（page=页面路径 url=外部链接 none=无链接）',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序号（数字越小越靠前）',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0=启用 1=禁用）',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`),
  KEY `idx_config_type` (`config_type`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='运营配置表';

-- ----------------------------
-- Records of hz_operation_config
-- ----------------------------
INSERT INTO `hz_operation_config` VALUES ('3', 'notice', '重要通知', null, '港好住平台系统升级公告，将于本周六凌晨进行系统维护', '/pages/notice/detail', 'page', '1', '0', 'admin', '2026-01-13 00:20:37', null, null, '滚动通知示例');
INSERT INTO `hz_operation_config` VALUES ('4', 'banner', '测试1', '/profile/upload/2026/01/15/750262_20260115013106A001.png', '无', null, 'none', '0', '0', '', null, '', null, null);
INSERT INTO `hz_operation_config` VALUES ('5', 'banner', '测试3', '/profile/upload/2026/01/15/banner@2x_20260115013146A002.png', null, null, 'none', '0', '0', '', null, '', null, null);
INSERT INTO `hz_operation_config` VALUES ('6', 'banner', '测试2', '/profile/upload/2026/01/15/750263_20260115013309A003.png', null, null, 'none', '0', '0', '', null, '', null, null);

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
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图片',
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鏀跨瓥鏂囦欢琛';

-- ----------------------------
-- Records of hz_policy
-- ----------------------------
INSERT INTO `hz_policy` VALUES ('1', '测试政策标题', '/profile/upload/2026/01/13/矩形 21@2x_20260113185305A002.png', '12300', '4', '<p>阿斯蒂芬阿斯蒂芬<img src=\"/dev-api/profile/upload/2026/01/12/R-C (2)_20260112121627A001.jpg\"></p>', '/profile/upload/2026/01/12/数据比对系统需求方案(1)_20260112121644A002.doc', '商务部', '2026-01-12', '2026-01-29', '9', '0', '', null, '', null, null, '0');

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
  `distribution` varchar(255) DEFAULT NULL COMMENT '房源分布',
  `house_type` varchar(255) DEFAULT NULL COMMENT '户型',
  `area` varchar(255) DEFAULT NULL COMMENT '面积范围',
  `rent_detail` text COMMENT '租金详情',
  `property_company` varchar(255) DEFAULT NULL COMMENT '物业公司',
  `property_fee` varchar(100) DEFAULT NULL COMMENT '物业费',
  `electric_fee` varchar(100) DEFAULT NULL COMMENT '电费',
  `water_fee` varchar(100) DEFAULT NULL COMMENT '水费',
  `gas_fee` varchar(100) DEFAULT NULL COMMENT '燃气费',
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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='椤圭洰琛';

-- ----------------------------
-- Records of hz_project
-- ----------------------------
INSERT INTO `hz_project` VALUES ('1', '阿斯蒂芬', '012', '1', '阿斯蒂芬', '123.1210000', '21.1212000', '0', '0', '0', null, null, null, null, null, null, null, null, null, '', '/profile/upload/2026/01/12/合住人-灰@2x_20260112003348A001.png', '2000.00', '阿斯蒂芬,商业街,停车场', '00', null, 'wenwang', '18539279011', '0', '0', '', null, '', null, null, '2');
INSERT INTO `hz_project` VALUES ('2', '航空港区锦绣家园', 'JXJY2024', '1', '郑州航空港区迎宾大道与四港联动大道交叉口', '113.8256000', '34.5198000', '50', '500', '200', '1-6号楼/共6栋楼/每栋11层', '一室一厅/两室一厅/两室两厅', '45-75平方米', '500-650元/月（一室）\n750-900元/月（两室一厅）\n850-1050元/月（两室两厅）', '郑州港区锦绣物业管理有限公司', '2.0元/平方米/月', '0.56元/度', '4.2元/吨', '2.45元/立方', null, '/profile/upload/2026/01/12/df079c167cae476cb21790c62b610481_20260112015410A003.png', '6000.00', '停车场,运动场,游泳池,健身房', null, null, '菜', '18539279011', '0', '2', '', null, '', null, null, '0');
INSERT INTO `hz_project` VALUES ('3', '航空港区雅居苑', 'YJY2024', '1', '郑州航空港区大河路与雍州路交叉口西南角', '113.8124000', '34.5312000', '8', '360', '85', '1-8号楼/每栋9层', '单间配套/一室一厅/两室一厅', '35-70平方米', '400-480元/月（单间配套）\n550-680元/月（一室一厅）\n700-850元/月（两室一厅）', '河南雅居物业服务有限公司', '1.8元/平方米/月', '0.56元/度', '4.2元/吨', '2.45元/立方', null, '/profile/upload/2026/01/12/5033.jpg_wh300_20260112015248A001.jpg', '5000.00', '商业街', null, null, '王飞飞', '18539279011', '0', '0', '', null, '', null, null, '0');
INSERT INTO `hz_project` VALUES ('4', '港区万科城', 'wanke', '1', '郑州航空港区梁州大道与雍州路交叉口', '113.8367000', '34.5287000', '100', '200', '120', '1-15号楼/每栋12层', '一室一厅/两室一厅/两室两厅/三室两厅', '50-120平方米', '600-750元/月（一室）\n800-1000元/月（两室一厅）\n950-1200元/月（两室两厅）\n1200-1500元/月（三室）', '万科物业服务有限公司郑州分公司', '2.8元/平方米/月', '0.56元/度（阶梯电价）', '4.5元/吨', '2.58元/立方', '这是个测试项目', '/profile/upload/2026/01/12/ScreenShot_2026-01-12_015115_648_20260112015403A002.png', '1500.00', '商业街,停车场,运动场,游泳池', null, null, '陈飞飞', '18539279011', '0', '10', '', null, '', null, null, '0');
INSERT INTO `hz_project` VALUES ('5', '港区金科城', '3695', '1', '港区金科路395号', '113.8489000', '34.5156000', '185', '120', '10', '20-35号楼/共16栋/每栋10-11层', '单间/一室一厅/两室一厅/两室两厅', '30-85平方米', '350-450元/月（单间）\n500-650元/月（一室）\n700-900元/月（两室一厅）\n800-1000元/月（两室两厅）', '金科物业服务集团有限公司', '2.3元/平方米/月', '0.56元/度', '4.2元/吨', '2.45元/立方', null, '/profile/upload/2026/01/12/R-C_20260112015435A004.jpg', '2800.00', '停车场,商业街', null, null, '李菲数', '18564659565', '0', '0', '', null, '', null, null, '0');
INSERT INTO `hz_project` VALUES ('6', '港区恒大城', 'hengda', '2', '港区黄河路123号恒大城', '113.8523000', '34.5234000', '100', '100', '10', '3-10号楼/共8栋/每栋18层', '两室一厅/两室两厅/三室两厅', '70-130平方米', '900-1100元/月（两室一厅）\n1100-1400元/月（两室两厅）\n1400-1800元/月（三室两厅）', '恒大金碧物业有限公司', '3.2元/平方米/月', '0.6元/度（阶梯电价）', '4.5元/吨', '2.58元/立方', '暂无介绍', '/profile/upload/2026/01/12/ScreenShot_2026-01-12_015115_648_20260112031148A001.png', '6300.00', '快递柜,停车场,商业街,医疗室,运动场,游泳池', null, null, '廖凡', '13695645652', '0', '0', '', null, '', null, null, '0');
INSERT INTO `hz_project` VALUES ('9', '航南新城人才公寓', 'HNXC-RC-2024001', '1', '航空港区新港大道与遵大路交叉口南100米', '113.8124560', '34.5167890', '4', '150', '150', '1号楼1-8层/2号楼1-8层/3号楼1-6层/4号楼1-6层', '一室一厅/二室一厅/三室两厅', '45平/65平/90平', '550-600元/月（45平一室一厅）\n750-800元/月（65平二室一厅）\n1050-1100元/月（90平三室两厅）', '郑州航空港区合达物业服务有限公司', '2.5元/平方米/月', '0.56元/度（阶梯电价）', '4.2元/吨', '2.58元/立方', '航南新城人才公寓位于航空港区核心区域，紧邻地铁2号线，交通便利。项目配备独立卫浴、床、衣柜、空调、冰箱、洗衣机、热水器、电视、书桌、椅子、晾衣架等家具家电，拎包入住。小区环境优美，配套设施完善，有生活超市、健身房、图书馆、公交车站等，是港区人才的理想居所。', '/profile/upload/2026/01/13/矩形 21@2x_20260113224107A001.png', '550.00', '停车场,健身房,图书馆,24小时保安,监控系统,快递柜', '地铁2号线航南新城站500米，公交T26、T27路直达，紧邻郑港大道、迎宾大道', null, '陈大福', '18565645644', '0', '1', '', '2026-01-13 22:39:57', '', null, null, '0');
INSERT INTO `hz_project` VALUES ('10', '港区智慧公寓', 'GQZH-RC-2024002', '1', '航空港区郑港大道与雍州路交叉口东北角', '113.8245670', '34.5234560', '6', '230', '230', '1号楼1-10层/2号楼1-10层/3号楼1-10层/4号楼1-8层/5号楼1-8层/6号楼1-6层', '单间配套/一室一厅/二室一厅/二室两厅', '35平/50平/70平/85平', '480-520元/月（35平单间配套）\n600-650元/月（50平一室一厅）\n850-900元/月（70平二室一厅）\n1000-1050元/月（85平二室两厅）', '河南航投物业管理有限公司', '2.8元/平方米/月', '0.60元/度（阶梯电价）', '4.5元/吨', '2.58元/立方', '港区智慧公寓是航空港区重点打造的现代化人才公寓项目，采用智能门禁、智能停车、智能安防等先进设施。房间精装修，全套家具家电配置，包含床、沙发、餐桌、空调、冰箱、洗衣机、热水器、油烟机、电视等。小区配有超市、餐厅、健身房、羽毛球馆、篮球场、儿童乐园等生活配套，周边有郑州一中航空港校区、河南省人民医院航空港院区，生活便利。', '/profile/upload/2026/01/13/项目图片@2x_20260113224717A002.png', '480.00', '智能门禁,地下停车场,健身房,游泳池,羽毛球馆,篮球场,儿童乐园,商业街,快递柜,监控系统', '地铁17号线雍州路站200米，公交T21、T23路，临近机场高速、郑港大道', null, '王鹏 ', '15696365694', '0', '2', '', '2026-01-13 22:39:57', '', null, null, '0');

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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='璧勬牸鐢宠瘔琛';

-- ----------------------------
-- Records of hz_qualification_appeal
-- ----------------------------
INSERT INTO `hz_qualification_appeal` VALUES ('1', '0', '1', '6', '/profile/upload/2026/01/15/1_20260115114932A002.jpg,/profile/upload/2026/01/15/2-1_20260115114932A001.jpg', '2026-01-15 11:49:32', '2', '1', null, '2026-01-15 16:04:44', '0', '', null, '', null, null, '0');
INSERT INTO `hz_qualification_appeal` VALUES ('2', '0', '1', '5', '/profile/upload/2026/01/15/1_20260115160532A002.jpg,/profile/upload/2026/01/15/2_20260115160532A001.jpg', '2026-01-15 16:05:32', '1', '信息准确 ', null, '2026-01-15 16:57:58', '0', '', null, '', null, null, '0');

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
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鍗曞厓琛';

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
INSERT INTO `hz_unit` VALUES ('14', '6', '1单元', '3-6-1', '0', '0', '0', '', '2026-01-13 23:50:39', '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('15', '6', '2单元', '3-6-2', '0', '0', '0', '', '2026-01-13 23:50:39', '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('16', '6', '3单元', '3-6-3', '0', '0', '0', '', '2026-01-13 23:50:39', '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('17', '8', '1单元', '3-8-1', '0', '0', '0', '', '2026-01-13 23:50:39', '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('18', '8', '2单元', '3-8-2', '0', '0', '0', '', '2026-01-13 23:50:39', '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('19', '8', '3单元', '3-8-3', '0', '0', '0', '', '2026-01-13 23:50:39', '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('20', '9', '1单元', '3-9-1', '0', '0', '0', '', '2026-01-13 23:50:39', '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('21', '9', '2单元', '3-9-2', '0', '0', '0', '', '2026-01-13 23:50:39', '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('22', '9', '3单元', '3-9-3', '0', '0', '0', '', '2026-01-13 23:50:39', '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('23', '10', '1单元', '3-10-1', '0', '0', '0', '', '2026-01-13 23:50:39', '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('24', '10', '2单元', '3-10-2', '0', '0', '0', '', '2026-01-13 23:50:39', '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('25', '10', '3单元', '3-10-3', '0', '0', '0', '', '2026-01-13 23:50:39', '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('26', '11', '1单元', '3-11-1', '0', '0', '0', '', '2026-01-13 23:50:39', '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('27', '11', '2单元', '3-11-2', '0', '0', '0', '', '2026-01-13 23:50:39', '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('28', '11', '3单元', '3-11-3', '0', '0', '0', '', '2026-01-13 23:50:39', '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('29', '12', '1单元', '3-12-1', '0', '0', '0', '', '2026-01-13 23:50:39', '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('30', '12', '2单元', '3-12-2', '0', '0', '0', '', '2026-01-13 23:50:39', '', null, null, '0');
INSERT INTO `hz_unit` VALUES ('31', '12', '3单元', '3-12-3', '0', '0', '0', '', '2026-01-13 23:50:39', '', null, null, '0');

-- ----------------------------
-- Table structure for hz_user
-- ----------------------------
DROP TABLE IF EXISTS `hz_user`;
CREATE TABLE `hz_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `phone` varchar(11) NOT NULL COMMENT '手机号（登录账号）',
  `contact_phone` varchar(11) DEFAULT NULL COMMENT '鑱旂郴鐢佃瘽',
  `source_type` char(1) NOT NULL DEFAULT '1' COMMENT '来源类型（1=微信小程序 2=郑好办）',
  `wechat_openid` varchar(100) DEFAULT NULL COMMENT '微信OpenID',
  `wechat_unionid` varchar(100) DEFAULT NULL COMMENT '微信UnionID',
  `zhaohao_user_id` varchar(100) DEFAULT NULL COMMENT '郑好办用户ID',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `gender` char(1) DEFAULT '0' COMMENT '性别（0=未知 1=男 2=女）',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `education` char(1) DEFAULT NULL COMMENT '学历（1=小学 2=初中 3=高中 4=大专 5=本科 6=硕士 7=博士）',
  `identity_type` char(1) DEFAULT NULL COMMENT '身份类型（1=在职人员 2=应届毕业生）',
  `work_unit` varchar(100) DEFAULT NULL COMMENT '工作单位',
  `unit_contact` varchar(50) DEFAULT NULL COMMENT '单位联系方式',
  `spouse_name` varchar(50) DEFAULT NULL COMMENT '配偶姓名',
  `work_proof_attachment` varchar(255) DEFAULT NULL COMMENT '工作证明附件',
  `login_type` varchar(20) DEFAULT NULL COMMENT '登录类型（wechat=微信 zhenghaoban=郑好办）',
  `is_info_completed` char(1) DEFAULT '0' COMMENT '是否完善个人信息（0=否 1=是）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0=正常 1=停用）',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(50) DEFAULT NULL COMMENT '最后登录IP',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0=正常 2=删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_wechat_openid` (`wechat_openid`),
  KEY `idx_zhaohao_user_id` (`zhaohao_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';

-- ----------------------------
-- Records of hz_user
-- ----------------------------
INSERT INTO `hz_user` VALUES ('1', '13800138001', '18296541256', '1', 'wx_test_001', null, null, '微信测试用户', '/profile/avatar/default.jpg', '陈庆志', '0', '410623199602255325', '5', '1', '郑州港区投资集团', '037166525654', '无', '/profile/upload/2026/01/13/s1024407_20260113150722A001.jpg', 'wechat', '1', '0', '2026-01-15 16:11:00', '127.0.0.1', '0', 'admin', '2026-01-13 01:55:13', null, null, '微信登录测试账号');
INSERT INTO `hz_user` VALUES ('2', '13800138002', null, '2', null, null, 'zhb_test_001', '郑好办测试用户', '/profile/avatar/default.jpg', null, '0', null, null, null, null, null, null, null, 'zhenghaoban', '0', '0', '2026-01-15 02:10:38', '127.0.0.1', '0', 'admin', '2026-01-13 01:55:13', null, null, '郑好办登录测试账号');

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
) ENGINE=InnoDB AUTO_INCREMENT=1349 DEFAULT CHARSET=utf8 COMMENT='字典数据表';

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
INSERT INTO `sys_dict_data` VALUES ('1312', '1', '人才公寓政策', '1', 'policy_type', '', 'primary', 'N', '0', 'admin', '2026-01-12 11:48:39', '', null, '人才公寓相关政策');
INSERT INTO `sys_dict_data` VALUES ('1313', '2', '保租房政策', '2', 'policy_type', '', 'success', 'N', '0', 'admin', '2026-01-12 11:48:39', '', null, '保租房相关政策');
INSERT INTO `sys_dict_data` VALUES ('1314', '3', '市场租赁政策', '3', 'policy_type', '', 'info', 'N', '0', 'admin', '2026-01-12 11:48:39', '', null, '市场租赁相关政策');
INSERT INTO `sys_dict_data` VALUES ('1315', '4', '通用政策', '4', 'policy_type', '', 'warning', 'N', '0', 'admin', '2026-01-12 11:48:39', '', null, '通用政策文件');
INSERT INTO `sys_dict_data` VALUES ('1316', '1', '文艺活动', '1', 'activity_type', '', 'primary', 'N', '0', 'admin', '2026-01-12 13:04:23', '', null, '文艺类活动');
INSERT INTO `sys_dict_data` VALUES ('1317', '2', '体育活动', '2', 'activity_type', '', 'success', 'N', '0', 'admin', '2026-01-12 13:04:23', '', null, '体育类活动');
INSERT INTO `sys_dict_data` VALUES ('1318', '3', '培训讲座', '3', 'activity_type', '', 'info', 'N', '0', 'admin', '2026-01-12 13:04:23', '', null, '培训和讲座类活动');
INSERT INTO `sys_dict_data` VALUES ('1319', '4', '社交联谊', '4', 'activity_type', '', 'warning', 'N', '0', 'admin', '2026-01-12 13:04:23', '', null, '社交联谊类活动');
INSERT INTO `sys_dict_data` VALUES ('1320', '5', '志愿服务', '5', 'activity_type', '', 'danger', 'N', '0', 'admin', '2026-01-12 13:04:23', '', null, '志愿服务类活动');
INSERT INTO `sys_dict_data` VALUES ('1321', '6', '其他活动', '6', 'activity_type', '', 'default', 'N', '0', 'admin', '2026-01-12 13:04:23', '', null, '其他类型活动');
INSERT INTO `sys_dict_data` VALUES ('1331', '1', '轮播图', 'banner', 'hz_config_type', null, 'success', 'N', '0', 'admin', '2026-01-13 00:20:56', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1332', '2', '广告位', 'ad', 'hz_config_type', null, 'warning', 'N', '0', 'admin', '2026-01-13 00:20:56', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1333', '3', '通知公告', 'notice', 'hz_config_type', null, 'info', 'N', '0', 'admin', '2026-01-13 00:20:56', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1334', '4', '功能图标', 'icon', 'hz_config_type', null, 'primary', 'N', '0', 'admin', '2026-01-13 00:20:56', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1335', '1', '页面路径', 'page', 'hz_link_type', null, 'primary', 'N', '0', 'admin', '2026-01-13 00:21:15', '', null, 'UniApp页面路径');
INSERT INTO `sys_dict_data` VALUES ('1336', '2', '外部链接', 'url', 'hz_link_type', null, 'success', 'N', '0', 'admin', '2026-01-13 00:21:15', '', null, '外部URL链接');
INSERT INTO `sys_dict_data` VALUES ('1337', '3', '无链接', 'none', 'hz_link_type', null, 'info', 'N', '0', 'admin', '2026-01-13 00:21:15', '', null, '仅展示不跳转');
INSERT INTO `sys_dict_data` VALUES ('1338', '1', '微信小程序', '1', 'hz_user_source_type', '', 'primary', 'N', '0', 'admin', '2026-01-13 01:55:46', '', null, '微信小程序注册');
INSERT INTO `sys_dict_data` VALUES ('1339', '2', '郑好办', '2', 'hz_user_source_type', '', 'success', 'N', '0', 'admin', '2026-01-13 01:55:46', '', null, '郑好办注册');
INSERT INTO `sys_dict_data` VALUES ('1340', '1', '小学', '1', 'hz_education_type', null, null, 'N', '0', 'admin', '2026-01-13 01:55:58', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1341', '2', '初中', '2', 'hz_education_type', null, null, 'N', '0', 'admin', '2026-01-13 01:55:58', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1342', '3', '高中', '3', 'hz_education_type', null, null, 'N', '0', 'admin', '2026-01-13 01:55:58', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1343', '4', '大专', '4', 'hz_education_type', null, null, 'N', '0', 'admin', '2026-01-13 01:55:58', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1344', '5', '本科', '5', 'hz_education_type', null, null, 'N', '0', 'admin', '2026-01-13 01:55:58', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1345', '6', '硕士', '6', 'hz_education_type', null, null, 'N', '0', 'admin', '2026-01-13 01:55:58', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1346', '7', '博士', '7', 'hz_education_type', null, null, 'N', '0', 'admin', '2026-01-13 01:55:58', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1347', '1', '在职人员', '1', 'hz_identity_type', '', 'primary', 'N', '0', 'admin', '2026-01-13 02:33:54', null, null, '在职人员');
INSERT INTO `sys_dict_data` VALUES ('1348', '2', '应届毕业生', '2', 'hz_identity_type', '', 'success', 'N', '0', 'admin', '2026-01-13 02:34:02', null, null, '应届毕业生');

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
) ENGINE=InnoDB AUTO_INCREMENT=141 DEFAULT CHARSET=utf8 COMMENT='字典类型表';

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
INSERT INTO `sys_dict_type` VALUES ('132', '政策类型', 'policy_type', '0', 'admin', '2026-01-12 11:48:28', '', null, '政策文件的分类类型');
INSERT INTO `sys_dict_type` VALUES ('133', '活动类型', 'activity_type', '0', 'admin', '2026-01-12 13:03:57', '', null, '人才家园活动的分类类型');
INSERT INTO `sys_dict_type` VALUES ('136', '运营配置类型', 'hz_config_type', '0', 'admin', '2026-01-13 00:20:45', '', null, null);
INSERT INTO `sys_dict_type` VALUES ('137', '链接类型', 'hz_link_type', '0', 'admin', '2026-01-13 00:21:06', '', null, null);
INSERT INTO `sys_dict_type` VALUES ('138', '用户来源类型', 'hz_user_source_type', '0', 'admin', '2026-01-13 01:55:45', '', null, '港好住用户来源类型');
INSERT INTO `sys_dict_type` VALUES ('139', '学历类型', 'hz_education_type', '0', 'admin', '2026-01-13 01:55:58', '', null, '用户学历类型');
INSERT INTO `sys_dict_type` VALUES ('140', '身份类型', 'hz_identity_type', '0', 'admin', '2026-01-13 02:33:29', null, null, '用户身份类型');

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
) ENGINE=InnoDB AUTO_INCREMENT=176 DEFAULT CHARSET=utf8 COMMENT='系统访问记录';

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
INSERT INTO `sys_logininfor` VALUES ('137', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-12 11:20:46');
INSERT INTO `sys_logininfor` VALUES ('138', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-12 11:58:07');
INSERT INTO `sys_logininfor` VALUES ('139', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-12 11:58:09');
INSERT INTO `sys_logininfor` VALUES ('140', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-12 14:22:13');
INSERT INTO `sys_logininfor` VALUES ('141', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-12 14:43:59');
INSERT INTO `sys_logininfor` VALUES ('142', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-12 14:44:06');
INSERT INTO `sys_logininfor` VALUES ('143', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-12 14:46:32');
INSERT INTO `sys_logininfor` VALUES ('144', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-12 15:45:11');
INSERT INTO `sys_logininfor` VALUES ('145', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-12 16:33:05');
INSERT INTO `sys_logininfor` VALUES ('146', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-12 17:43:10');
INSERT INTO `sys_logininfor` VALUES ('147', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-12 19:00:11');
INSERT INTO `sys_logininfor` VALUES ('148', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-12 20:40:38');
INSERT INTO `sys_logininfor` VALUES ('149', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-12 22:05:57');
INSERT INTO `sys_logininfor` VALUES ('150', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-13 01:29:31');
INSERT INTO `sys_logininfor` VALUES ('151', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-13 11:54:39');
INSERT INTO `sys_logininfor` VALUES ('152', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-13 14:44:22');
INSERT INTO `sys_logininfor` VALUES ('153', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-13 17:36:10');
INSERT INTO `sys_logininfor` VALUES ('154', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-13 18:14:21');
INSERT INTO `sys_logininfor` VALUES ('155', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-13 21:03:41');
INSERT INTO `sys_logininfor` VALUES ('156', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-13 22:15:55');
INSERT INTO `sys_logininfor` VALUES ('157', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-13 23:37:41');
INSERT INTO `sys_logininfor` VALUES ('158', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-14 01:26:13');
INSERT INTO `sys_logininfor` VALUES ('159', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-14 04:07:55');
INSERT INTO `sys_logininfor` VALUES ('160', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-14 09:46:16');
INSERT INTO `sys_logininfor` VALUES ('161', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-14 10:56:28');
INSERT INTO `sys_logininfor` VALUES ('162', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-14 13:46:08');
INSERT INTO `sys_logininfor` VALUES ('163', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-14 15:32:47');
INSERT INTO `sys_logininfor` VALUES ('164', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-14 15:50:04');
INSERT INTO `sys_logininfor` VALUES ('165', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-14 18:27:23');
INSERT INTO `sys_logininfor` VALUES ('166', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-14 20:57:11');
INSERT INTO `sys_logininfor` VALUES ('167', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-14 21:33:27');
INSERT INTO `sys_logininfor` VALUES ('168', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-14 22:45:09');
INSERT INTO `sys_logininfor` VALUES ('169', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-15 00:40:10');
INSERT INTO `sys_logininfor` VALUES ('170', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-15 03:04:14');
INSERT INTO `sys_logininfor` VALUES ('171', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-15 11:13:11');
INSERT INTO `sys_logininfor` VALUES ('172', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-15 11:49:54');
INSERT INTO `sys_logininfor` VALUES ('173', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-15 12:29:17');
INSERT INTO `sys_logininfor` VALUES ('174', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-15 15:02:30');
INSERT INTO `sys_logininfor` VALUES ('175', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-15 16:56:26');

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
) ENGINE=InnoDB AUTO_INCREMENT=4061 DEFAULT CHARSET=utf8 COMMENT='菜单权限表';

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
INSERT INTO `sys_menu` VALUES ('2032', '入住管理', '2022', '3', 'checkin', 'gangzhu/checkin/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:checkin:list', 'log', 'admin', '2025-11-18 00:18:54', '', null, '入住管理菜单');
INSERT INTO `sys_menu` VALUES ('2033', '入住查询', '2032', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:checkin:query', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2034', '入住审核', '2032', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:checkin:audit', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2035', '入住导出', '2032', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:checkin:export', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2036', '退租管理', '2022', '3', 'checkout', 'gangzhu/checkout/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:checkout:list', 'enter', 'admin', '2025-11-18 00:18:54', 'admin', '2026-01-14 15:35:11', '退租管理菜单');
INSERT INTO `sys_menu` VALUES ('2037', '退租查询', '2036', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:checkout:query', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2038', '退租审核', '2036', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:checkout:audit', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2039', '退租导出', '2036', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:checkout:export', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2040', '预约看房', '3002', '2', 'appointment', 'gangzhu/appointment/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:appointment:list', 'date', 'admin', '2025-11-18 00:18:54', '', null, '预约管理菜单');
INSERT INTO `sys_menu` VALUES ('2041', '预约查询', '2040', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:appointment:query', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2042', '预约确认', '2040', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:appointment:confirm', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2043', '预约取消', '2040', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:appointment:cancel', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2044', '预约导出', '2040', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:appointment:export', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2045', '承诺书', '3002', '4', 'document', 'gangzhu/application/document/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:document:list', 'skill', 'admin', '2025-11-18 00:18:54', '', null, '资料审核菜单');
INSERT INTO `sys_menu` VALUES ('2046', '资料查询', '2045', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:document:query', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2047', '资料审核', '2045', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:document:audit', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2048', '资料导出', '2045', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:document:export', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2058', '开票管理', '2000', '9', 'invoice', 'gangzhu/invoice/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:invoice:list', 'post', 'admin', '2025-11-18 00:18:54', '', null, '开票管理菜单');
INSERT INTO `sys_menu` VALUES ('2059', '开票查询', '2058', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:invoice:query', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2060', '开票审核', '2058', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:invoice:audit', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('2061', '开票导出', '2058', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:invoice:export', '#', 'admin', '2025-11-18 00:18:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('3000', '首页', '2000', '1', 'dashboard', 'gangzhu/dashboard/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:dashboard:view', 'dashboard', 'admin', '2025-11-18 20:22:04', '', null, '首页数据统计');
INSERT INTO `sys_menu` VALUES ('3001', '资产管理', '2000', '2', 'asset', null, null, '', '1', '0', 'M', '0', '0', null, 'example', 'admin', '2025-11-18 20:22:04', '', null, '资产管理菜单');
INSERT INTO `sys_menu` VALUES ('3002', '申请信息', '2000', '3', 'application', null, null, '', '1', '0', 'M', '0', '0', null, 'form', 'admin', '2025-11-18 20:22:04', '', null, '申请信息菜单');
INSERT INTO `sys_menu` VALUES ('3003', '报表管理', '2000', '5', 'report', null, null, '', '1', '0', 'M', '0', '0', null, 'chart', 'admin', '2025-11-18 20:22:04', '', null, '报表管理菜单');
INSERT INTO `sys_menu` VALUES ('3004', '配置管理', '2000', '6', 'config', null, null, '', '1', '0', 'M', '0', '0', null, 'tool', 'admin', '2025-11-18 20:22:04', '', null, '配置管理菜单');
INSERT INTO `sys_menu` VALUES ('3010', '合同管理核心', '2022', '1', 'list', 'gangzhu/contract/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:contract:list', 'log', 'admin', '2025-11-18 20:23:34', 'admin', '2026-01-14 15:34:40', '合同列表');
INSERT INTO `sys_menu` VALUES ('3020', '项目收款台账', '3003', '1', 'receipt', 'gangzhu/report/receipt/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:report:receipt', '#', 'admin', '2025-11-18 20:23:34', '', null, '项目收款台账');
INSERT INTO `sys_menu` VALUES ('3021', '自定义报表', '3003', '2', 'custom', 'gangzhu/report/custom/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:report:custom', '#', 'admin', '2025-11-18 20:23:34', '', null, '自定义报表');
INSERT INTO `sys_menu` VALUES ('3030', '合同模版', '3004', '2', 'template', 'gangzhu/config/template/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:config:template', 'code', 'admin', '2025-11-18 20:23:34', 'admin', '2026-01-13 00:23:10', '合同模版');
INSERT INTO `sys_menu` VALUES ('3031', '运营配置', '3004', '3', 'operation', 'gangzhu/config/operation/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:config:operation', 'education', 'admin', '2025-11-18 20:23:34', 'admin', '2026-01-13 00:23:18', '运营配置');
INSERT INTO `sys_menu` VALUES ('3032', '政策文件', '3004', '5', 'policy', 'gangzhu/policy/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:policy:list', 'edit', 'admin', '2025-11-18 20:23:34', 'admin', '2026-01-12 12:20:30', '政策文件');
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
INSERT INTO `sys_menu` VALUES ('4041', '政策查询', '3032', '1', '', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:policy:query', '#', 'admin', '2026-01-12 12:09:52', '', null, '');
INSERT INTO `sys_menu` VALUES ('4042', '政策新增', '3032', '2', '', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:policy:add', '#', 'admin', '2026-01-12 12:09:52', '', null, '');
INSERT INTO `sys_menu` VALUES ('4043', '政策修改', '3032', '3', '', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:policy:edit', '#', 'admin', '2026-01-12 12:09:52', '', null, '');
INSERT INTO `sys_menu` VALUES ('4044', '政策删除', '3032', '4', '', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:policy:remove', '#', 'admin', '2026-01-12 12:09:52', '', null, '');
INSERT INTO `sys_menu` VALUES ('4045', '政策导出', '3032', '5', '', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:policy:export', '#', 'admin', '2026-01-12 12:09:52', '', null, '');
INSERT INTO `sys_menu` VALUES ('4046', '人才家园', '3004', '6', 'activity', 'gangzhu/activity/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:activity:list', 'guide', 'admin', '2026-01-12 13:03:26', 'admin', '2026-01-13 00:23:29', '人才家园活动管理');
INSERT INTO `sys_menu` VALUES ('4047', '活动查询', '4046', '1', '', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:activity:query', '#', 'admin', '2026-01-12 13:03:44', '', null, '');
INSERT INTO `sys_menu` VALUES ('4048', '活动新增', '4046', '2', '', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:activity:add', '#', 'admin', '2026-01-12 13:03:44', '', null, '');
INSERT INTO `sys_menu` VALUES ('4049', '活动修改', '4046', '3', '', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:activity:edit', '#', 'admin', '2026-01-12 13:03:44', '', null, '');
INSERT INTO `sys_menu` VALUES ('4050', '活动删除', '4046', '4', '', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:activity:remove', '#', 'admin', '2026-01-12 13:03:44', '', null, '');
INSERT INTO `sys_menu` VALUES ('4051', '活动导出', '4046', '5', '', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:activity:export', '#', 'admin', '2026-01-12 13:03:44', '', null, '');
INSERT INTO `sys_menu` VALUES ('4052', '合同模版查询', '3030', '1', '', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:template:query', '#', 'admin', '2026-01-14 09:45:16', '', null, '');
INSERT INTO `sys_menu` VALUES ('4053', '合同模版新增', '3030', '2', '', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:template:add', '#', 'admin', '2026-01-14 09:45:16', '', null, '');
INSERT INTO `sys_menu` VALUES ('4054', '合同模版修改', '3030', '3', '', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:template:edit', '#', 'admin', '2026-01-14 09:45:16', '', null, '');
INSERT INTO `sys_menu` VALUES ('4055', '合同模版删除', '3030', '4', '', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:template:remove', '#', 'admin', '2026-01-14 09:45:16', '', null, '');
INSERT INTO `sys_menu` VALUES ('4056', '合同模版导出', '3030', '5', '', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:template:export', '#', 'admin', '2026-01-14 09:45:16', '', null, '');
INSERT INTO `sys_menu` VALUES ('4057', '合同模版列表', '3030', '6', '', null, null, '', '1', '0', 'F', '0', '0', 'gangzhu:template:list', '#', 'admin', '2026-01-14 09:45:16', '', null, '');
INSERT INTO `sys_menu` VALUES ('4058', '承诺书管理', '3002', '2', 'commitment', 'gangzhu/application/commitment/index', null, '', '1', '0', 'C', '0', '0', 'gangzhu:commitment:list', 'form', 'admin', '2026-01-14 22:10:42', '', null, '查看所有租户签署的承诺书记录');
INSERT INTO `sys_menu` VALUES ('4059', '承诺书查询', '4058', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:commitment:query', '#', 'admin', '2026-01-14 22:10:54', '', null, '');
INSERT INTO `sys_menu` VALUES ('4060', '承诺书删除', '4058', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'gangzhu:commitment:remove', '#', 'admin', '2026-01-14 22:10:54', '', null, '');

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
) ENGINE=InnoDB AUTO_INCREMENT=254 DEFAULT CHARSET=utf8 COMMENT='操作日志记录';

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
INSERT INTO `sys_oper_log` VALUES ('176', '菜单管理', '3', 'com.ruoyi.web.controller.system.SysMenuController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/menu/2028', '127.0.0.1', '内网IP', '2028', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 11:23:01', '18');
INSERT INTO `sys_oper_log` VALUES ('177', '菜单管理', '3', 'com.ruoyi.web.controller.system.SysMenuController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/menu/2029', '127.0.0.1', '内网IP', '2029', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 11:23:04', '11');
INSERT INTO `sys_oper_log` VALUES ('178', '菜单管理', '3', 'com.ruoyi.web.controller.system.SysMenuController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/menu/2030', '127.0.0.1', '内网IP', '2030', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 11:23:07', '15');
INSERT INTO `sys_oper_log` VALUES ('179', '菜单管理', '3', 'com.ruoyi.web.controller.system.SysMenuController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/menu/2031', '127.0.0.1', '内网IP', '2031', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 11:23:09', '13');
INSERT INTO `sys_oper_log` VALUES ('180', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"gangzhu/policy/index\",\"createTime\":\"2025-11-18 20:23:34\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":3032,\"menuName\":\"政策文件\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":3004,\"path\":\"policy\",\"perms\":\"gangzhu:policy:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 11:57:31', '22');
INSERT INTO `sys_oper_log` VALUES ('181', '政策文件', '1', 'com.ruoyi.gangzhu.policy.controller.HzPolicyController.add()', 'POST', '1', 'admin', '研发部门', '/gangzhu/policy', '127.0.0.1', '内网IP', '{\"effectiveDate\":\"2026-01-29\",\"params\":{},\"policyContent\":\"<p>阿斯蒂芬阿斯蒂芬<img src=\\\"/dev-api/profile/upload/2026/01/12/R-C (2)_20260112121627A001.jpg\\\"></p>\",\"policyFile\":\"/profile/upload/2026/01/12/数据比对系统需求方案(1)_20260112121644A002.doc\",\"policyId\":1,\"policyNo\":\"12300\",\"policyTitle\":\"测试政策标题\",\"policyType\":\"4\",\"publishDate\":\"2026-01-12\",\"publishDept\":\"商务部\",\"status\":\"0\",\"viewCount\":0}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 12:16:49', '17');
INSERT INTO `sys_oper_log` VALUES ('182', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"gangzhu/policy/index\",\"createTime\":\"2025-11-18 20:23:34\",\"icon\":\"edit\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":3032,\"menuName\":\"政策文件\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":3004,\"path\":\"policy\",\"perms\":\"gangzhu:policy:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 12:20:30', '17');
INSERT INTO `sys_oper_log` VALUES ('183', '人才家园活动', '1', 'com.ruoyi.gangzhu.activity.controller.HzActivityController.add()', 'POST', '1', 'admin', '研发部门', '/gangzhu/activity', '127.0.0.1', '内网IP', '{\"activityContent\":\"<p>阿斯蒂芬阿道夫阿斯蒂芬阿斯蒂芬<img src=\\\"/dev-api/profile/upload/2026/01/12/s34099286_20260112144549A002.jpg\\\"></p>\",\"activityEndTime\":\"2026-01-28 00:00:00\",\"activityId\":1,\"activityLocation\":\"郑州高新区\",\"activityStartTime\":\"2026-01-13 00:00:00\",\"activityTitle\":\"这是测试活动\",\"activityType\":\"6\",\"contactPerson\":\"来来来\",\"contactPhone\":\"18566555555\",\"coverImage\":\"/profile/upload/2026/01/12/s1070959_20260112144541A001.jpg\",\"currentParticipants\":0,\"maxParticipants\":500,\"organizer\":\"郑好办\",\"params\":{},\"registrationEndTime\":\"2026-01-05 00:00:00\",\"registrationStartTime\":\"2026-01-01 00:00:00\",\"status\":\"0\",\"viewCount\":0}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 14:45:52', '16');
INSERT INTO `sys_oper_log` VALUES ('184', '保存批次分配', '1', 'com.ruoyi.web.controller.system.HzBatchAllocationController.saveAllocation()', 'POST', '1', 'admin', '研发部门', '/system/batch/saveAllocation', '127.0.0.1', '内网IP', '{\"batchInfo\":{\"batchName\":\"2026第一批次港区配租\",\"talentType\":\"0\",\"projectIds\":\"3,5,6,2,4\",\"houseCount\":3},\"houseList\":[{\"houseId\":1,\"houseCode\":\"123\",\"houseNo\":\"456\"},{\"houseId\":4,\"houseCode\":\"01\",\"houseNo\":\"123\"},{\"houseId\":5,\"houseCode\":\"12\",\"houseNo\":\"301\"}],\"tenantList\":[{\"tenantName\":\"张三\",\"idCard\":\"410123199001011234\",\"phone\":\"13800138000\"},{\"tenantName\":\"李四\",\"idCard\":\"410123199001011238\",\"phone\":\"13800138001\"},{\"tenantName\":\"张司\",\"idCard\":\"4101990020112340256\",\"phone\":\"13600100010\"}]}', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'enterprise_id\' doesn\'t have a default value\r\n### The error may exist in com/ruoyi/system/mapper/HzBatchAllocationMapper.java (best guess)\r\n### The error may involve com.ruoyi.system.mapper.HzBatchAllocationMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO hz_batch_allocation  ( batch_no, batch_name,      project_ids, talent_type, house_count, allocated_count, apply_time, approve_status,   batch_status, del_flag )  VALUES (  ?, ?,      ?, ?, ?, ?, ?, ?,   ?, ?  )\r\n### Cause: java.sql.SQLException: Field \'enterprise_id\' doesn\'t have a default value\n; Field \'enterprise_id\' doesn\'t have a default value', '2026-01-12 18:09:16', '139');
INSERT INTO `sys_oper_log` VALUES ('185', '保存批次分配', '1', 'com.ruoyi.web.controller.system.HzBatchAllocationController.saveAllocation()', 'POST', '1', 'admin', '研发部门', '/system/batch/saveAllocation', '127.0.0.1', '内网IP', '{\"batchInfo\":{\"batchName\":\"测试批次\",\"talentType\":\"0\",\"projectIds\":\"3,5,6,2\",\"houseCount\":2},\"houseList\":[{\"houseId\":1,\"houseCode\":\"123\",\"houseNo\":\"456\"},{\"houseId\":4,\"houseCode\":\"01\",\"houseNo\":\"123\"}],\"tenantList\":[{\"tenantName\":\"张三\",\"idCard\":\"4112121565656565656\",\"phone\":\"18565656565\"},{\"tenantName\":\"lisi\",\"idCard\":\"4112121565656565657\",\"phone\":\"15656565656\"}]}', null, '1', '\r\n### Error updating database.  Cause: com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Data too long for column \'id_card\' at row 1\r\n### The error may exist in com/ruoyi/system/mapper/HzBatchTenantMapper.java (best guess)\r\n### The error may involve com.ruoyi.system.mapper.HzBatchTenantMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO hz_batch_tenant  ( batch_id, tenant_name, id_card, phone,    allocation_status,  del_flag )  VALUES (  ?, ?, ?, ?,    ?,  ?  )\r\n### Cause: com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Data too long for column \'id_card\' at row 1\n; Data truncation: Data too long for column \'id_card\' at row 1', '2026-01-12 19:03:13', '17');
INSERT INTO `sys_oper_log` VALUES ('186', '保存批次分配', '1', 'com.ruoyi.web.controller.system.HzBatchAllocationController.saveAllocation()', 'POST', '1', 'admin', '研发部门', '/system/batch/saveAllocation', '127.0.0.1', '内网IP', '{\"batchInfo\":{\"batchName\":\"ceshi \",\"talentType\":\"0\",\"projectIds\":\"3,6,2\",\"remark\":\"ces\",\"houseCount\":2},\"houseList\":[{\"houseId\":1,\"houseCode\":\"123\",\"houseNo\":\"456\"},{\"houseId\":4,\"houseCode\":\"01\",\"houseNo\":\"123\"}],\"tenantList\":[{\"tenantName\":\"张三\",\"idCard\":\"412895656523256623\",\"phone\":\"18539279522\"},{\"tenantName\":\"张三\",\"idCard\":\"412895656523256723\",\"phone\":\"18539379522\"}]}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 19:09:02', '38');
INSERT INTO `sys_oper_log` VALUES ('187', '保存批次分配', '1', 'com.ruoyi.web.controller.system.HzBatchAllocationController.saveAllocation()', 'POST', '1', 'admin', '研发部门', '/system/batch/saveAllocation', '127.0.0.1', '内网IP', '{\"batchInfo\":{\"batchId\":2,\"batchName\":\"ceshi \",\"talentType\":\"0\",\"projectIds\":\"3,6,2\",\"selectionStartDate\":1768147200000,\"selectionEndDate\":1769788800000,\"remark\":\"ces\",\"houseCount\":2},\"houseList\":[{\"houseId\":1,\"houseCode\":\"123\",\"houseNo\":\"456\"},{\"houseId\":4,\"houseCode\":\"01\",\"houseNo\":\"123\"}],\"tenantList\":[{\"tenantName\":\"张三\",\"idCard\":\"412895656523256623\",\"phone\":\"18539279522\"},{\"tenantName\":\"张三\",\"idCard\":\"412895656523256723\",\"phone\":\"18539379522\"}]}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 19:31:25', '42');
INSERT INTO `sys_oper_log` VALUES ('188', '配租批次', '3', 'com.ruoyi.web.controller.system.HzBatchAllocationController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/batch/2', '127.0.0.1', '内网IP', '[2]', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'WHERE batch_id=2 AND del_flag=\'0\'\' at line 1\r\n### The error may exist in com/ruoyi/system/mapper/HzBatchAllocationMapper.java (best guess)\r\n### The error may involve com.ruoyi.system.mapper.HzBatchAllocationMapper.updateById-Inline\r\n### The error occurred while setting parameters\r\n### SQL: UPDATE hz_batch_allocation    WHERE batch_id=? AND del_flag=\'0\'\r\n### Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'WHERE batch_id=2 AND del_flag=\'0\'\' at line 1\n; bad SQL grammar []', '2026-01-12 19:32:07', '7');
INSERT INTO `sys_oper_log` VALUES ('189', '配租批次', '3', 'com.ruoyi.web.controller.system.HzBatchAllocationController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/batch/2', '127.0.0.1', '内网IP', '[2]', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'WHERE batch_id=2 AND del_flag=\'0\'\' at line 1\r\n### The error may exist in com/ruoyi/system/mapper/HzBatchAllocationMapper.java (best guess)\r\n### The error may involve com.ruoyi.system.mapper.HzBatchAllocationMapper.updateById-Inline\r\n### The error occurred while setting parameters\r\n### SQL: UPDATE hz_batch_allocation    WHERE batch_id=? AND del_flag=\'0\'\r\n### Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'WHERE batch_id=2 AND del_flag=\'0\'\' at line 1\n; bad SQL grammar []', '2026-01-12 19:32:15', '6');
INSERT INTO `sys_oper_log` VALUES ('190', '配租批次', '3', 'com.ruoyi.web.controller.system.HzBatchAllocationController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/batch/3', '127.0.0.1', '内网IP', '[3]', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'WHERE batch_id=3 AND del_flag=\'0\'\' at line 1\r\n### The error may exist in com/ruoyi/system/mapper/HzBatchAllocationMapper.java (best guess)\r\n### The error may involve com.ruoyi.system.mapper.HzBatchAllocationMapper.updateById-Inline\r\n### The error occurred while setting parameters\r\n### SQL: UPDATE hz_batch_allocation    WHERE batch_id=? AND del_flag=\'0\'\r\n### Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'WHERE batch_id=3 AND del_flag=\'0\'\' at line 1\n; bad SQL grammar []', '2026-01-12 19:36:55', '9');
INSERT INTO `sys_oper_log` VALUES ('191', '配租批次', '3', 'com.ruoyi.web.controller.system.HzBatchAllocationController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/batch/3', '127.0.0.1', '内网IP', '[3]', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'WHERE batch_id=3 AND del_flag=\'0\'\' at line 1\r\n### The error may exist in com/ruoyi/system/mapper/HzBatchAllocationMapper.java (best guess)\r\n### The error may involve com.ruoyi.system.mapper.HzBatchAllocationMapper.updateById-Inline\r\n### The error occurred while setting parameters\r\n### SQL: UPDATE hz_batch_allocation    WHERE batch_id=? AND del_flag=\'0\'\r\n### Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'WHERE batch_id=3 AND del_flag=\'0\'\' at line 1\n; bad SQL grammar []', '2026-01-12 19:37:24', '5');
INSERT INTO `sys_oper_log` VALUES ('192', '配租批次', '3', 'com.ruoyi.web.controller.system.HzBatchAllocationController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/batch/3', '127.0.0.1', '内网IP', '[3]', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 19:40:06', '32');
INSERT INTO `sys_oper_log` VALUES ('193', '保存批次分配', '1', 'com.ruoyi.web.controller.system.HzBatchAllocationController.saveAllocation()', 'POST', '1', 'admin', '研发部门', '/system/batch/saveAllocation', '127.0.0.1', '内网IP', '{\"batchInfo\":{\"batchId\":2,\"batchName\":\"ceshi \",\"talentType\":\"0\",\"projectIds\":\"3,6,2\",\"selectionStartDate\":1768320000000,\"selectionEndDate\":1769788800000,\"remark\":\"ces\",\"houseCount\":2},\"houseList\":[{\"houseId\":1,\"houseCode\":\"123\",\"houseNo\":\"456\"},{\"houseId\":4,\"houseCode\":\"01\",\"houseNo\":\"123\"}],\"tenantList\":[{\"tenantName\":\"张三\",\"idCard\":\"412895656523256623\",\"phone\":\"18539279522\"},{\"tenantName\":\"张三\",\"idCard\":\"412895656523256723\",\"phone\":\"18539379522\"}]}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 19:40:17', '30');
INSERT INTO `sys_oper_log` VALUES ('194', '配租批次', '3', 'com.ruoyi.web.controller.system.HzBatchAllocationController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/batch/2', '127.0.0.1', '内网IP', '[2]', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 19:40:41', '13');
INSERT INTO `sys_oper_log` VALUES ('195', '保存批次分配', '1', 'com.ruoyi.web.controller.system.HzBatchAllocationController.saveAllocation()', 'POST', '1', 'admin', '研发部门', '/system/batch/saveAllocation', '127.0.0.1', '内网IP', '{\"batchInfo\":{\"batchName\":\"ceshi\",\"talentType\":\"0\",\"projectIds\":\"3,5,6,2\",\"selectionStartDate\":1767456000000,\"selectionEndDate\":1769788800000,\"houseCount\":2},\"houseList\":[{\"houseId\":1,\"houseCode\":\"123\",\"houseNo\":\"456\"},{\"houseId\":4,\"houseCode\":\"01\",\"houseNo\":\"123\"}],\"tenantList\":[{\"tenantName\":\"张三\",\"idCard\":\"412895656523256623\",\"phone\":\"18539279522\"},{\"tenantName\":\"张三\",\"idCard\":\"412895656523246623\",\"phone\":\"18539279523\"}]}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 19:41:24', '44');
INSERT INTO `sys_oper_log` VALUES ('196', '保存企业批次', '1', 'com.ruoyi.web.controller.system.HzEnterpriseBatchController.saveWithHouses()', 'POST', '1', 'admin', '研发部门', '/system/enterpriseBatch/saveWithHouses', '127.0.0.1', '内网IP', '{\"batchInfo\":{\"batchName\":\"QY206584\",\"enterpriseName\":\"郑州港区投资集团有限公司\",\"contactPerson\":\"张正\",\"contactPhone\":\"18696356545\",\"projectIds\":\"6,5,2,4,3\",\"selectionStartDate\":1768147200000,\"selectionEndDate\":1768147200000,\"houseCount\":3},\"houseList\":[{\"houseId\":1,\"houseCode\":\"123\",\"houseNo\":\"456\"},{\"houseId\":4,\"houseCode\":\"01\",\"houseNo\":\"123\"},{\"houseId\":5,\"houseCode\":\"12\",\"houseNo\":\"301\"}]}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 22:44:48', '45');
INSERT INTO `sys_oper_log` VALUES ('197', '用户信息', '5', 'com.ruoyi.web.controller.system.HzUserController.export()', 'POST', '1', 'admin', '研发部门', '/gangzhu/user/export', '127.0.0.1', '内网IP', '{\"pageSize\":\"10\",\"pageNum\":\"1\"}', null, '0', null, '2026-01-12 23:31:06', '298');
INSERT INTO `sys_oper_log` VALUES ('198', '菜单管理', '3', 'com.ruoyi.web.controller.system.SysMenuController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/menu/2049', '127.0.0.1', '内网IP', '2049', '{\"msg\":\"存在子菜单,不允许删除\",\"code\":601}', '0', null, '2026-01-12 23:34:12', '3');
INSERT INTO `sys_oper_log` VALUES ('199', '菜单管理', '3', 'com.ruoyi.web.controller.system.SysMenuController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/menu/2050', '127.0.0.1', '内网IP', '2050', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 23:34:16', '18');
INSERT INTO `sys_oper_log` VALUES ('200', '菜单管理', '3', 'com.ruoyi.web.controller.system.SysMenuController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/menu/2051', '127.0.0.1', '内网IP', '2051', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 23:34:19', '21');
INSERT INTO `sys_oper_log` VALUES ('201', '菜单管理', '3', 'com.ruoyi.web.controller.system.SysMenuController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/menu/2052', '127.0.0.1', '内网IP', '2052', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 23:34:22', '11');
INSERT INTO `sys_oper_log` VALUES ('202', '菜单管理', '3', 'com.ruoyi.web.controller.system.SysMenuController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/menu/2049', '127.0.0.1', '内网IP', '2049', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 23:34:27', '11');
INSERT INTO `sys_oper_log` VALUES ('203', '菜单管理', '3', 'com.ruoyi.web.controller.system.SysMenuController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/menu/2057', '127.0.0.1', '内网IP', '2057', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 23:34:34', '5');
INSERT INTO `sys_oper_log` VALUES ('204', '菜单管理', '3', 'com.ruoyi.web.controller.system.SysMenuController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/menu/2054', '127.0.0.1', '内网IP', '2054', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 23:34:36', '14');
INSERT INTO `sys_oper_log` VALUES ('205', '菜单管理', '3', 'com.ruoyi.web.controller.system.SysMenuController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/menu/2055', '127.0.0.1', '内网IP', '2055', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 23:34:40', '27');
INSERT INTO `sys_oper_log` VALUES ('206', '菜单管理', '3', 'com.ruoyi.web.controller.system.SysMenuController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/menu/2056', '127.0.0.1', '内网IP', '2056', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 23:34:43', '19');
INSERT INTO `sys_oper_log` VALUES ('207', '菜单管理', '3', 'com.ruoyi.web.controller.system.SysMenuController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/menu/2053', '127.0.0.1', '内网IP', '2053', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-12 23:34:48', '12');
INSERT INTO `sys_oper_log` VALUES ('208', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"gangzhu/config/template/index\",\"createTime\":\"2025-11-18 20:23:34\",\"icon\":\"code\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":3030,\"menuName\":\"合同模版\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":3004,\"path\":\"template\",\"perms\":\"gangzhu:config:template\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-13 00:23:10', '11');
INSERT INTO `sys_oper_log` VALUES ('209', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"gangzhu/config/operation/index\",\"createTime\":\"2025-11-18 20:23:34\",\"icon\":\"education\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":3031,\"menuName\":\"运营配置\",\"menuType\":\"C\",\"orderNum\":3,\"params\":{},\"parentId\":3004,\"path\":\"operation\",\"perms\":\"gangzhu:config:operation\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-13 00:23:18', '6');
INSERT INTO `sys_oper_log` VALUES ('210', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"gangzhu/activity/index\",\"createTime\":\"2026-01-12 13:03:26\",\"icon\":\"guide\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":4046,\"menuName\":\"人才家园\",\"menuType\":\"C\",\"orderNum\":6,\"params\":{},\"parentId\":3004,\"path\":\"activity\",\"perms\":\"gangzhu:activity:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-13 00:23:29', '8');
INSERT INTO `sys_oper_log` VALUES ('211', '运营配置', '2', 'com.ruoyi.web.controller.system.HzOperationConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/gangzhu/config', '127.0.0.1', '内网IP', '{\"configId\":1,\"configType\":\"banner\",\"content\":\"新春特惠，人才公寓租金8折起\",\"createBy\":\"admin\",\"createTime\":\"2026-01-13 00:20:37\",\"imageUrl\":\"/profile/upload/2026/01/13/s1078958_20260113002427A001.jpg\",\"linkType\":\"page\",\"linkUrl\":\"/pages/coupon/index\",\"params\":{},\"remark\":\"首页轮播图示例1\",\"sortOrder\":1,\"status\":\"0\",\"title\":\"新春租房优惠活动\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-13 00:24:28', '54');
INSERT INTO `sys_oper_log` VALUES ('212', '运营配置', '2', 'com.ruoyi.web.controller.system.HzOperationConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/gangzhu/config', '127.0.0.1', '内网IP', '{\"configId\":2,\"configType\":\"banner\",\"content\":\"保租房申请流程详解\",\"createBy\":\"admin\",\"createTime\":\"2026-01-13 00:20:37\",\"imageUrl\":\"/profile/upload/2026/01/13/s29799269_20260113002444A002.jpg\",\"linkType\":\"page\",\"linkUrl\":\"/pages/talent/index\",\"params\":{},\"remark\":\"首页轮播图示例2\",\"sortOrder\":2,\"status\":\"0\",\"title\":\"保租房申请指南\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-13 00:24:46', '5');
INSERT INTO `sys_oper_log` VALUES ('213', '政策文件', '2', 'com.ruoyi.gangzhu.policy.controller.HzPolicyController.edit()', 'PUT', '1', 'admin', '研发部门', '/gangzhu/policy', '127.0.0.1', '内网IP', '{\"coverImage\":\"/profile/upload/2026/01/13/矩形 21@2x_20260113185305A002.png\",\"createBy\":\"\",\"delFlag\":\"0\",\"effectiveDate\":\"2026-01-29\",\"params\":{},\"policyContent\":\"<p>阿斯蒂芬阿斯蒂芬<img src=\\\"/dev-api/profile/upload/2026/01/12/R-C (2)_20260112121627A001.jpg\\\"></p>\",\"policyFile\":\"/profile/upload/2026/01/12/数据比对系统需求方案(1)_20260112121644A002.doc\",\"policyId\":1,\"policyNo\":\"12300\",\"policyTitle\":\"测试政策标题\",\"policyType\":\"4\",\"publishDate\":\"2026-01-12\",\"publishDept\":\"商务部\",\"status\":\"0\",\"updateBy\":\"\",\"viewCount\":0}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-13 18:53:08', '28');
INSERT INTO `sys_oper_log` VALUES ('214', '户型', '1', 'com.ruoyi.system.controller.HzHouseTypeController.add()', 'POST', '1', 'admin', '研发部门', '/gangzhu/houseType', '127.0.0.1', '内网IP', '{\"balconyCount\":1,\"bathroomCount\":1,\"bedroomCount\":1,\"delFlag\":\"0\",\"houseTypeCode\":\"yishiyiting\",\"houseTypeId\":17,\"houseTypeName\":\"一室一厅\",\"kitchenCount\":1,\"livingroomCount\":1,\"params\":{},\"projectId\":3,\"sortOrder\":0,\"status\":\"0\",\"typicalArea\":60}', '{\"msg\":\"新增成功\",\"code\":200,\"data\":17}', '0', null, '2026-01-13 22:18:29', '25');
INSERT INTO `sys_oper_log` VALUES ('215', '项目管理', '2', 'com.ruoyi.web.controller.system.HzProjectController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/project', '127.0.0.1', '内网IP', '{\"address\":\"航空港区新港大道与遵大路交叉口南100米\",\"area\":\"45平/65平/90平\",\"availableHouses\":150,\"coverImage\":\"/profile/upload/2026/01/13/矩形 21@2x_20260113224107A001.png\",\"createBy\":\"\",\"createTime\":\"2026-01-13 22:39:57\",\"delFlag\":\"0\",\"distribution\":\"1号楼1-8层/2号楼1-8层/3号楼1-6层/4号楼1-6层\",\"electricFee\":\"0.56元/度（阶梯电价）\",\"facilities\":\"停车场,健身房,图书馆,24小时保安,监控系统,快递柜\",\"gasFee\":\"2.58元/立方\",\"houseType\":\"一室一厅/二室一厅/三室两厅\",\"latitude\":34.516789,\"longitude\":113.812456,\"managerName\":\"陈大福\",\"managerPhone\":\"18565645644\",\"params\":{},\"price\":550,\"projectCode\":\"HNXC-RC-2024001\",\"projectId\":9,\"projectIntro\":\"航南新城人才公寓位于航空港区核心区域，紧邻地铁2号线，交通便利。项目配备独立卫浴、床、衣柜、空调、冰箱、洗衣机、热水器、电视、书桌、椅子、晾衣架等家具家电，拎包入住。小区环境优美，配套设施完善，有生活超市、健身房、图书馆、公交车站等，是港区人才的理想居所。\",\"projectName\":\"航南新城人才公寓\",\"projectType\":\"1\",\"propertyCompany\":\"郑州航空港区合达物业服务有限公司\",\"propertyFee\":\"2.5元/平方米/月\",\"rentDetail\":\"550-600元/月（45平一室一厅）\\n750-800元/月（65平二室一厅）\\n1050-1100元/月（90平三室两厅）\",\"sortOrder\":1,\"status\":\"0\",\"totalBuildings\":4,\"totalHouses\":150,\"transportation\":\"地铁2号线航南新城站500米，公交T26、T27路直达，紧邻郑港大道、迎宾大道\",\"updateBy\":\"\",\"waterFee\":\"4.2元/吨\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-13 22:41:29', '27');
INSERT INTO `sys_oper_log` VALUES ('216', '项目管理', '2', 'com.ruoyi.web.controller.system.HzProjectController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/project', '127.0.0.1', '内网IP', '{\"address\":\"航空港区郑港大道与雍州路交叉口东北角\",\"area\":\"35平/50平/70平/85平\",\"availableHouses\":230,\"coverImage\":\"/profile/upload/2026/01/13/项目图片@2x_20260113224717A002.png\",\"createBy\":\"\",\"createTime\":\"2026-01-13 22:39:57\",\"delFlag\":\"0\",\"distribution\":\"1号楼1-10层/2号楼1-10层/3号楼1-10层/4号楼1-8层/5号楼1-8层/6号楼1-6层\",\"electricFee\":\"0.60元/度（阶梯电价）\",\"facilities\":\"智能门禁,地下停车场,健身房,游泳池,羽毛球馆,篮球场,儿童乐园,商业街,快递柜,监控系统\",\"gasFee\":\"2.58元/立方\",\"houseType\":\"单间配套/一室一厅/二室一厅/二室两厅\",\"latitude\":34.523456,\"longitude\":113.824567,\"managerName\":\"王鹏 \",\"managerPhone\":\"15696365694\",\"params\":{},\"price\":480,\"projectCode\":\"GQZH-RC-2024002\",\"projectId\":10,\"projectIntro\":\"港区智慧公寓是航空港区重点打造的现代化人才公寓项目，采用智能门禁、智能停车、智能安防等先进设施。房间精装修，全套家具家电配置，包含床、沙发、餐桌、空调、冰箱、洗衣机、热水器、油烟机、电视等。小区配有超市、餐厅、健身房、羽毛球馆、篮球场、儿童乐园等生活配套，周边有郑州一中航空港校区、河南省人民医院航空港院区，生活便利。\",\"projectName\":\"港区智慧公寓\",\"projectType\":\"1\",\"propertyCompany\":\"河南航投物业管理有限公司\",\"propertyFee\":\"2.8元/平方米/月\",\"rentDetail\":\"480-520元/月（35平单间配套）\\n600-650元/月（50平一室一厅）\\n850-900元/月（70平二室一厅）\\n1000-1050元/月（85平二室两厅）\",\"sortOrder\":2,\"status\":\"0\",\"totalBuildings\":6,\"totalHouses\":230,\"transportation\":\"地铁17号线雍州路站200米，公交T21、T23路，临近机场高速、郑港大道\",\"updateBy\":\"\",\"waterFee\":\"4.5元/吨\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-13 22:47:39', '6');
INSERT INTO `sys_oper_log` VALUES ('217', '单元管理', '3', 'com.ruoyi.web.controller.system.HzUnitController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/unit/12', '127.0.0.1', '内网IP', '[12]', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'WHERE unit_id=12 AND del_flag=\'0\'\' at line 1\r\n### The error may exist in com/ruoyi/system/mapper/HzUnitMapper.java (best guess)\r\n### The error may involve com.ruoyi.system.mapper.HzUnitMapper.updateById-Inline\r\n### The error occurred while setting parameters\r\n### SQL: UPDATE hz_unit    WHERE unit_id=? AND del_flag=\'0\'\r\n### Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'WHERE unit_id=12 AND del_flag=\'0\'\' at line 1\n; bad SQL grammar []', '2026-01-13 23:45:07', '147');
INSERT INTO `sys_oper_log` VALUES ('218', '单元管理', '3', 'com.ruoyi.web.controller.system.HzUnitController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/unit/12', '127.0.0.1', '内网IP', '[12]', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'WHERE unit_id=12 AND del_flag=\'0\'\' at line 1\r\n### The error may exist in com/ruoyi/system/mapper/HzUnitMapper.java (best guess)\r\n### The error may involve com.ruoyi.system.mapper.HzUnitMapper.updateById-Inline\r\n### The error occurred while setting parameters\r\n### SQL: UPDATE hz_unit    WHERE unit_id=? AND del_flag=\'0\'\r\n### Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'WHERE unit_id=12 AND del_flag=\'0\'\' at line 1\n; bad SQL grammar []', '2026-01-13 23:48:34', '5');
INSERT INTO `sys_oper_log` VALUES ('219', '户型', '3', 'com.ruoyi.system.controller.HzHouseTypeController.remove()', 'DELETE', '1', 'admin', '研发部门', '/gangzhu/houseType/41', '127.0.0.1', '内网IP', '[41]', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'WHERE house_type_id=41 AND del_flag=\'0\'\' at line 1\r\n### The error may exist in com/ruoyi/system/mapper/HzHouseTypeMapper.java (best guess)\r\n### The error may involve com.ruoyi.system.mapper.HzHouseTypeMapper.updateById-Inline\r\n### The error occurred while setting parameters\r\n### SQL: UPDATE hz_house_type    WHERE house_type_id=? AND del_flag=\'0\'\r\n### Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'WHERE house_type_id=41 AND del_flag=\'0\'\' at line 1\n; bad SQL grammar []', '2026-01-14 02:22:07', '108');
INSERT INTO `sys_oper_log` VALUES ('220', '项目管理', '1', 'com.ruoyi.web.controller.system.HzProjectController.generateHouseTypes()', 'POST', '1', 'admin', '研发部门', '/system/project/3/generateHouseTypes', '127.0.0.1', '内网IP', '3', '{\"msg\":\"成功生成 0 种房型\",\"code\":200}', '0', null, '2026-01-14 02:22:41', '31');
INSERT INTO `sys_oper_log` VALUES ('221', '户型', '3', 'com.ruoyi.system.controller.HzHouseTypeController.remove()', 'DELETE', '1', 'admin', '研发部门', '/gangzhu/houseType/56', '127.0.0.1', '内网IP', '[56]', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'WHERE house_type_id=56 AND del_flag=\'0\'\' at line 1\r\n### The error may exist in com/ruoyi/system/mapper/HzHouseTypeMapper.java (best guess)\r\n### The error may involve com.ruoyi.system.mapper.HzHouseTypeMapper.updateById-Inline\r\n### The error occurred while setting parameters\r\n### SQL: UPDATE hz_house_type    WHERE house_type_id=? AND del_flag=\'0\'\r\n### Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'WHERE house_type_id=56 AND del_flag=\'0\'\' at line 1\n; bad SQL grammar []', '2026-01-14 02:23:17', '10');
INSERT INTO `sys_oper_log` VALUES ('222', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/house', '127.0.0.1', '内网IP', '{\"area\":72,\"buildingId\":12,\"createBy\":\"\",\"createTime\":\"2026-01-14 00:16:41\",\"decoration\":\"精装\",\"deposit\":4400,\"floor\":1,\"houseCode\":\"3-12-29-101\",\"houseId\":54,\"houseNo\":\"101\",\"houseStatus\":\"0\",\"houseTypeId\":17,\"houseTypeName\":\"一室\",\"isFeatured\":\"0\",\"orientation\":\"东南\",\"params\":{},\"projectId\":3,\"rentPrice\":2200,\"status\":\"0\",\"unitId\":29,\"updateBy\":\"\",\"viewCount\":0}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-14 02:32:30', '43');
INSERT INTO `sys_oper_log` VALUES ('223', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/house', '127.0.0.1', '内网IP', '{\"area\":72,\"buildingId\":12,\"createBy\":\"\",\"createTime\":\"2026-01-14 00:16:41\",\"decoration\":\"精装\",\"deposit\":4400,\"floor\":2,\"houseCode\":\"3-12-29-201\",\"houseId\":55,\"houseNo\":\"201\",\"houseStatus\":\"0\",\"houseTypeId\":24,\"houseTypeName\":\"一室\",\"isFeatured\":\"0\",\"orientation\":\"东南\",\"params\":{},\"projectId\":3,\"rentPrice\":2200,\"status\":\"0\",\"unitId\":29,\"updateBy\":\"\",\"viewCount\":0}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-14 02:38:39', '14');
INSERT INTO `sys_oper_log` VALUES ('224', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/house', '127.0.0.1', '内网IP', '{\"area\":72,\"buildingId\":12,\"createBy\":\"\",\"createTime\":\"2026-01-14 00:16:41\",\"decoration\":\"精装\",\"deposit\":4400,\"floor\":3,\"houseCode\":\"3-12-29-301\",\"houseId\":56,\"houseNo\":\"301\",\"houseStatus\":\"0\",\"houseTypeId\":25,\"houseTypeName\":\"两室\",\"isFeatured\":\"0\",\"orientation\":\"东南\",\"params\":{},\"projectId\":3,\"rentPrice\":2200,\"status\":\"0\",\"unitId\":29,\"updateBy\":\"\",\"viewCount\":0}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-14 02:44:29', '14');
INSERT INTO `sys_oper_log` VALUES ('225', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/house', '127.0.0.1', '内网IP', '{\"area\":88,\"buildingId\":12,\"createBy\":\"\",\"createTime\":\"2026-01-14 00:16:41\",\"decoration\":\"精装\",\"deposit\":5500,\"floor\":1,\"houseCode\":\"3-12-30-101\",\"houseId\":57,\"houseNo\":\"101\",\"houseStatus\":\"0\",\"houseTypeId\":26,\"houseTypeName\":\"三室\",\"isFeatured\":\"0\",\"orientation\":\"南\",\"params\":{},\"projectId\":3,\"rentPrice\":2750,\"status\":\"0\",\"unitId\":30,\"updateBy\":\"\",\"viewCount\":0}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-14 02:45:25', '9');
INSERT INTO `sys_oper_log` VALUES ('226', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/house', '127.0.0.1', '内网IP', '{\"area\":88,\"buildingId\":12,\"createBy\":\"\",\"createTime\":\"2026-01-14 00:16:41\",\"decoration\":\"精装\",\"deposit\":5500,\"floor\":2,\"houseCode\":\"3-12-30-201\",\"houseId\":58,\"houseNo\":\"201\",\"houseStatus\":\"0\",\"houseTypeId\":26,\"houseTypeName\":\"三室\",\"isFeatured\":\"0\",\"orientation\":\"南\",\"params\":{},\"projectId\":3,\"rentPrice\":2750,\"status\":\"0\",\"unitId\":30,\"updateBy\":\"\",\"viewCount\":0}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-14 02:45:43', '12');
INSERT INTO `sys_oper_log` VALUES ('227', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/house', '127.0.0.1', '内网IP', '{\"area\":88,\"buildingId\":12,\"createBy\":\"\",\"createTime\":\"2026-01-14 00:16:41\",\"decoration\":\"精装\",\"deposit\":5500,\"floor\":3,\"houseCode\":\"3-12-30-301\",\"houseId\":59,\"houseNo\":\"301\",\"houseStatus\":\"0\",\"houseTypeId\":26,\"houseTypeName\":\"三室\",\"isFeatured\":\"0\",\"orientation\":\"南\",\"params\":{},\"projectId\":3,\"rentPrice\":2750,\"status\":\"0\",\"unitId\":30,\"updateBy\":\"\",\"viewCount\":0}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-14 02:45:52', '37');
INSERT INTO `sys_oper_log` VALUES ('228', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/house', '127.0.0.1', '内网IP', '{\"area\":72,\"buildingId\":12,\"createBy\":\"\",\"createTime\":\"2026-01-14 00:16:41\",\"decoration\":\"精装\",\"deposit\":4400,\"floor\":1,\"houseCode\":\"3-12-29-101\",\"houseId\":54,\"houseNo\":\"101\",\"houseStatus\":\"0\",\"houseTypeId\":17,\"houseTypeName\":\"一室\",\"isFeatured\":\"0\",\"orientation\":\"东南\",\"params\":{},\"projectId\":3,\"rentPrice\":2200,\"status\":\"0\",\"unitId\":29,\"updateBy\":\"\",\"viewCount\":0}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-14 02:46:53', '2');
INSERT INTO `sys_oper_log` VALUES ('229', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.saveHouseImages()', 'POST', '1', 'admin', '研发部门', '/system/house/images', '127.0.0.1', '内网IP', '{\"houseId\":54,\"images\":[{\"houseId\":54,\"imageUrl\":\"/profile/upload/2026/01/14/矩形 21@2x_20260114024633A001.png\",\"imageType\":\"1\",\"isCover\":\"1\",\"sortOrder\":0},{\"houseId\":54,\"imageUrl\":\"/profile/upload/2026/01/14/矩形 2@2x(1)_20260114024637A002.png\",\"imageType\":\"2\",\"isCover\":\"0\",\"sortOrder\":1},{\"houseId\":54,\"imageUrl\":\"/profile/upload/2026/01/14/人才家园@2x_20260114024639A003.png\",\"imageType\":\"3\",\"isCover\":\"0\",\"sortOrder\":2},{\"houseId\":54,\"imageUrl\":\"/profile/upload/2026/01/14/人才公寓@2x_20260114024641A004.png\",\"imageType\":\"3\",\"isCover\":\"0\",\"sortOrder\":3},{\"houseId\":54,\"imageUrl\":\"/profile/upload/2026/01/14/服务@2x_20260114024645A005.png\",\"imageType\":\"3\",\"isCover\":\"0\",\"sortOrder\":4},{\"houseId\":54,\"imageUrl\":\"/profile/upload/2026/01/14/矩形 2@2x_20260114024648A006.png\",\"imageType\":\"4\",\"isCover\":\"0\",\"sortOrder\":5},{\"houseId\":54,\"imageUrl\":\"/profile/upload/2026/01/14/矩形 21@2x_20260114024650A007.png\",\"imageType\":\"5\",\"isCover\":\"0\",\"sortOrder\":6}]}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-14 02:46:53', '38');
INSERT INTO `sys_oper_log` VALUES ('230', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/house', '127.0.0.1', '内网IP', '{\"area\":72,\"buildingId\":12,\"createBy\":\"\",\"createTime\":\"2026-01-14 00:16:41\",\"decoration\":\"精装\",\"deposit\":4400,\"floor\":1,\"houseCode\":\"3-12-29-101\",\"houseId\":54,\"houseNo\":\"101\",\"houseStatus\":\"0\",\"houseTypeId\":17,\"houseTypeName\":\"一室\",\"isFeatured\":\"0\",\"orientation\":\"东南\",\"params\":{},\"projectId\":3,\"rentPrice\":2200,\"status\":\"0\",\"unitId\":29,\"updateBy\":\"\",\"viewCount\":0}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-14 02:51:09', '4');
INSERT INTO `sys_oper_log` VALUES ('231', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.saveHouseVrs()', 'POST', '1', 'admin', '研发部门', '/system/house/vrs', '127.0.0.1', '内网IP', '{\"houseId\":54,\"vrs\":[{\"houseId\":54,\"vrName\":\"VR1\",\"vrUrl\":\"/profile/upload/2026/01/14/quanjing_20260114025108A008.png\",\"sortOrder\":0}]}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-14 02:51:09', '22');
INSERT INTO `sys_oper_log` VALUES ('232', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.saveHouseImages()', 'POST', '1', 'admin', '研发部门', '/system/house/images', '127.0.0.1', '内网IP', '{\"houseId\":54,\"images\":[{\"houseId\":54,\"imageUrl\":\"/profile/upload/2026/01/14/矩形 21@2x_20260114024633A001.png\",\"imageType\":\"1\",\"isCover\":\"1\",\"sortOrder\":0},{\"houseId\":54,\"imageUrl\":\"/profile/upload/2026/01/14/矩形 2@2x(1)_20260114024637A002.png\",\"imageType\":\"2\",\"isCover\":\"0\",\"sortOrder\":1},{\"houseId\":54,\"imageUrl\":\"/profile/upload/2026/01/14/人才家园@2x_20260114024639A003.png\",\"imageType\":\"3\",\"isCover\":\"0\",\"sortOrder\":2},{\"houseId\":54,\"imageUrl\":\"/profile/upload/2026/01/14/人才公寓@2x_20260114024641A004.png\",\"imageType\":\"3\",\"isCover\":\"0\",\"sortOrder\":3},{\"houseId\":54,\"imageUrl\":\"/profile/upload/2026/01/14/服务@2x_20260114024645A005.png\",\"imageType\":\"3\",\"isCover\":\"0\",\"sortOrder\":4},{\"houseId\":54,\"imageUrl\":\"/profile/upload/2026/01/14/矩形 2@2x_20260114024648A006.png\",\"imageType\":\"4\",\"isCover\":\"0\",\"sortOrder\":5},{\"houseId\":54,\"imageUrl\":\"/profile/upload/2026/01/14/矩形 21@2x_20260114024650A007.png\",\"imageType\":\"5\",\"isCover\":\"0\",\"sortOrder\":6}]}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-14 02:51:09', '106');
INSERT INTO `sys_oper_log` VALUES ('233', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/house', '127.0.0.1', '内网IP', '{\"area\":72,\"buildingId\":12,\"createBy\":\"\",\"createTime\":\"2026-01-14 00:16:41\",\"decoration\":\"精装\",\"deposit\":4400,\"floor\":1,\"houseCode\":\"3-12-29-101\",\"houseId\":54,\"houseNo\":\"101\",\"houseStatus\":\"0\",\"houseTypeId\":17,\"houseTypeName\":\"一室\",\"isFeatured\":\"0\",\"orientation\":\"东南\",\"params\":{},\"projectId\":3,\"remark\":\"不错的房源哦\",\"rentPrice\":2200,\"status\":\"0\",\"unitId\":29,\"updateBy\":\"\",\"viewCount\":0}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-14 03:06:53', '11');
INSERT INTO `sys_oper_log` VALUES ('234', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.saveHouseVrs()', 'POST', '1', 'admin', '研发部门', '/system/house/vrs', '127.0.0.1', '内网IP', '{\"houseId\":54,\"vrs\":[{\"houseId\":54,\"vrName\":\"VR1\",\"vrUrl\":\"/profile/upload/2026/01/14/quanjing_20260114025108A008.png\",\"sortOrder\":0}]}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-14 03:06:54', '52');
INSERT INTO `sys_oper_log` VALUES ('235', '房源管理', '2', 'com.ruoyi.web.controller.system.HzHouseController.saveHouseImages()', 'POST', '1', 'admin', '研发部门', '/system/house/images', '127.0.0.1', '内网IP', '{\"houseId\":54,\"images\":[{\"houseId\":54,\"imageUrl\":\"/profile/upload/2026/01/14/矩形 21@2x_20260114024633A001.png\",\"imageType\":\"1\",\"isCover\":\"1\",\"sortOrder\":0},{\"houseId\":54,\"imageUrl\":\"/profile/upload/2026/01/14/矩形 2@2x(1)_20260114024637A002.png\",\"imageType\":\"2\",\"isCover\":\"0\",\"sortOrder\":1},{\"houseId\":54,\"imageUrl\":\"/profile/upload/2026/01/14/人才家园@2x_20260114024639A003.png\",\"imageType\":\"3\",\"isCover\":\"0\",\"sortOrder\":2},{\"houseId\":54,\"imageUrl\":\"/profile/upload/2026/01/14/人才公寓@2x_20260114024641A004.png\",\"imageType\":\"3\",\"isCover\":\"0\",\"sortOrder\":3},{\"houseId\":54,\"imageUrl\":\"/profile/upload/2026/01/14/服务@2x_20260114024645A005.png\",\"imageType\":\"3\",\"isCover\":\"0\",\"sortOrder\":4},{\"houseId\":54,\"imageUrl\":\"/profile/upload/2026/01/14/矩形 2@2x_20260114024648A006.png\",\"imageType\":\"4\",\"isCover\":\"0\",\"sortOrder\":5},{\"houseId\":54,\"imageUrl\":\"/profile/upload/2026/01/14/矩形 21@2x_20260114024650A007.png\",\"imageType\":\"5\",\"isCover\":\"0\",\"sortOrder\":6}]}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-14 03:06:54', '161');
INSERT INTO `sys_oper_log` VALUES ('236', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"gangzhu/contract/index\",\"createTime\":\"2025-11-18 20:23:34\",\"icon\":\"log\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":3010,\"menuName\":\"合同管理核心\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2022,\"path\":\"list\",\"perms\":\"gangzhu:contract:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-14 15:34:40', '27');
INSERT INTO `sys_oper_log` VALUES ('237', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"gangzhu/checkout/index\",\"createTime\":\"2025-11-18 00:18:54\",\"icon\":\"enter\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2036,\"menuName\":\"退租管理\",\"menuType\":\"C\",\"orderNum\":3,\"params\":{},\"parentId\":2022,\"path\":\"checkout\",\"perms\":\"gangzhu:checkout:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-14 15:35:11', '31');
INSERT INTO `sys_oper_log` VALUES ('238', '合同管理', '3', 'com.ruoyi.web.controller.system.HzContractController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/contract/1', '127.0.0.1', '内网IP', '[1]', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'WHERE contract_id=1 AND del_flag=\'0\'\' at line 1\r\n### The error may exist in com/ruoyi/system/mapper/HzContractMapper.java (best guess)\r\n### The error may involve com.ruoyi.system.mapper.HzContractMapper.updateById-Inline\r\n### The error occurred while setting parameters\r\n### SQL: UPDATE hz_contract    WHERE contract_id=? AND del_flag=\'0\'\r\n### Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'WHERE contract_id=1 AND del_flag=\'0\'\' at line 1\n; bad SQL grammar []', '2026-01-14 15:44:43', '8');
INSERT INTO `sys_oper_log` VALUES ('239', '合同管理', '3', 'com.ruoyi.web.controller.system.HzContractController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/contract/1', '127.0.0.1', '内网IP', '[1]', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-14 15:48:45', '15');
INSERT INTO `sys_oper_log` VALUES ('240', '预约管理', '3', 'com.ruoyi.web.controller.system.HzAppointmentController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/appointment/2', '127.0.0.1', '内网IP', '[2]', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'WHERE appointment_id=2 AND del_flag=\'0\'\' at line 1\r\n### The error may exist in com/ruoyi/system/mapper/HzAppointmentMapper.java (best guess)\r\n### The error may involve com.ruoyi.system.mapper.HzAppointmentMapper.updateById-Inline\r\n### The error occurred while setting parameters\r\n### SQL: UPDATE hz_appointment    WHERE appointment_id=? AND del_flag=\'0\'\r\n### Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'WHERE appointment_id=2 AND del_flag=\'0\'\' at line 1\n; bad SQL grammar []', '2026-01-14 16:52:38', '10');
INSERT INTO `sys_oper_log` VALUES ('241', '预约管理', '3', 'com.ruoyi.web.controller.system.HzAppointmentController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/appointment/2', '127.0.0.1', '内网IP', '[2]', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-14 16:56:28', '23');
INSERT INTO `sys_oper_log` VALUES ('242', '预约管理', '3', 'com.ruoyi.web.controller.system.HzAppointmentController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/appointment/1', '127.0.0.1', '内网IP', '[1]', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-14 16:56:31', '5');
INSERT INTO `sys_oper_log` VALUES ('243', '确认预约', '2', 'com.ruoyi.web.controller.system.HzAppointmentController.confirm()', 'PUT', '1', 'admin', '研发部门', '/system/appointment/confirm/4', '127.0.0.1', '内网IP', '4', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-14 18:37:40', '22');
INSERT INTO `sys_oper_log` VALUES ('244', '取消预约', '2', 'com.ruoyi.web.controller.system.HzAppointmentController.cancel()', 'PUT', '1', 'admin', '研发部门', '/system/appointment/cancel/3', '127.0.0.1', '内网IP', '3 \"\\\"客户原因\\\"\"', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-14 18:37:53', '14');
INSERT INTO `sys_oper_log` VALUES ('245', '运营配置', '1', 'com.ruoyi.web.controller.system.HzOperationConfigController.add()', 'POST', '1', 'admin', '研发部门', '/gangzhu/config', '127.0.0.1', '内网IP', '{\"configId\":4,\"configType\":\"banner\",\"content\":\"无\",\"imageUrl\":\"/profile/upload/2026/01/15/750262_20260115013106A001.png\",\"linkType\":\"none\",\"params\":{},\"sortOrder\":0,\"status\":\"0\",\"title\":\"测试\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-15 01:31:13', '31');
INSERT INTO `sys_oper_log` VALUES ('246', '运营配置', '1', 'com.ruoyi.web.controller.system.HzOperationConfigController.add()', 'POST', '1', 'admin', '研发部门', '/gangzhu/config', '127.0.0.1', '内网IP', '{\"configId\":5,\"configType\":\"banner\",\"imageUrl\":\"/profile/upload/2026/01/15/banner@2x_20260115013146A002.png\",\"linkType\":\"none\",\"params\":{},\"sortOrder\":0,\"status\":\"0\",\"title\":\"测试3\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-15 01:31:47', '26');
INSERT INTO `sys_oper_log` VALUES ('247', '运营配置', '1', 'com.ruoyi.web.controller.system.HzOperationConfigController.add()', 'POST', '1', 'admin', '研发部门', '/gangzhu/config', '127.0.0.1', '内网IP', '{\"configId\":6,\"configType\":\"banner\",\"imageUrl\":\"/profile/upload/2026/01/15/750263_20260115013309A003.png\",\"linkType\":\"none\",\"params\":{},\"sortOrder\":0,\"status\":\"0\",\"title\":\"测试3\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-15 01:33:11', '5');
INSERT INTO `sys_oper_log` VALUES ('248', '运营配置', '3', 'com.ruoyi.web.controller.system.HzOperationConfigController.remove()', 'DELETE', '1', 'admin', '研发部门', '/gangzhu/config/2', '127.0.0.1', '内网IP', '[2]', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-15 01:57:49', '31');
INSERT INTO `sys_oper_log` VALUES ('249', '运营配置', '3', 'com.ruoyi.web.controller.system.HzOperationConfigController.remove()', 'DELETE', '1', 'admin', '研发部门', '/gangzhu/config/1', '127.0.0.1', '内网IP', '[1]', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-15 01:57:59', '3');
INSERT INTO `sys_oper_log` VALUES ('250', '运营配置', '2', 'com.ruoyi.web.controller.system.HzOperationConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/gangzhu/config', '127.0.0.1', '内网IP', '{\"configId\":6,\"configType\":\"banner\",\"createBy\":\"\",\"imageUrl\":\"/profile/upload/2026/01/15/750263_20260115013309A003.png\",\"linkType\":\"none\",\"params\":{},\"sortOrder\":0,\"status\":\"0\",\"title\":\"测试2\",\"updateBy\":\"\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-15 01:58:15', '11');
INSERT INTO `sys_oper_log` VALUES ('251', '运营配置', '2', 'com.ruoyi.web.controller.system.HzOperationConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/gangzhu/config', '127.0.0.1', '内网IP', '{\"configId\":4,\"configType\":\"banner\",\"content\":\"无\",\"createBy\":\"\",\"imageUrl\":\"/profile/upload/2026/01/15/750262_20260115013106A001.png\",\"linkType\":\"none\",\"params\":{},\"sortOrder\":0,\"status\":\"0\",\"title\":\"测试1\",\"updateBy\":\"\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-15 01:58:21', '5');
INSERT INTO `sys_oper_log` VALUES ('252', '资格申述审核', '2', 'com.ruoyi.web.controller.system.HzQualificationAppealController.handle()', 'PUT', '1', 'admin', '研发部门', '/system/qualificationAppeal/handle', '127.0.0.1', '内网IP', '{\"appealId\":1,\"appealReason\":\"6\",\"handleOpinion\":\"1\",\"handleResult\":\"2\",\"params\":{}}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-15 16:04:44', '29');
INSERT INTO `sys_oper_log` VALUES ('253', '资格申述审核', '2', 'com.ruoyi.web.controller.system.HzQualificationAppealController.handle()', 'PUT', '1', 'admin', '研发部门', '/system/qualificationAppeal/handle', '127.0.0.1', '内网IP', '{\"appealId\":2,\"appealReason\":\"5\",\"handleOpinion\":\"信息准确 \",\"handleResult\":\"1\",\"params\":{}}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-15 16:57:58', '16');

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
INSERT INTO `sys_role_menu` VALUES ('1', '3030');
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
INSERT INTO `sys_role_menu` VALUES ('1', '4052');
INSERT INTO `sys_role_menu` VALUES ('1', '4053');
INSERT INTO `sys_role_menu` VALUES ('1', '4054');
INSERT INTO `sys_role_menu` VALUES ('1', '4055');
INSERT INTO `sys_role_menu` VALUES ('1', '4056');
INSERT INTO `sys_role_menu` VALUES ('1', '4057');
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
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8 COMMENT='用户信息表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1', '103', 'admin', '若依', '00', 'ry@163.com', '15888888888', '1', '/profile/avatar/2025/11/17/54d9ef312c7142caa37ac2ecdddb0973.png', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', '2026-01-15 16:56:26', '2025-11-17 18:04:01', 'admin', '2025-11-17 18:04:01', '', '2025-11-17 19:06:19', '管理员');
INSERT INTO `sys_user` VALUES ('2', '105', 'ry', '若依', '00', 'ry@qq.com', '15666666666', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', '2025-11-17 18:04:01', '2025-11-17 18:04:01', 'admin', '2025-11-17 18:04:01', '', null, '测试员');
INSERT INTO `sys_user` VALUES ('100', null, 'h5user', '微信测试用户', '00', '', '13800138001', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/Tye/2x8uzYa', '0', '0', '', null, null, 'admin', '2026-01-14 18:29:03', '', null, null);

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
