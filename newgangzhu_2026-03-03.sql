# ************************************************************
# Sequel Ace SQL dump
# 版本号： 20096
#
# https://sequel-ace.com/
# https://github.com/Sequel-Ace/Sequel-Ace
#
# 主机: localhost (MySQL 8.0.44)
# 数据库: newgangzhu
# 生成时间: 2026-03-03 02:16:47 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
SET NAMES utf8mb4;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE='NO_AUTO_VALUE_ON_ZERO', SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# 转储表 hz_bill
# ------------------------------------------------------------

CREATE TABLE `hz_bill` (
  `bill_id` bigint NOT NULL AUTO_INCREMENT COMMENT '璐﹀崟ID',
  `bill_no` varchar(50) NOT NULL COMMENT '璐﹀崟缂栧彿',
  `contract_id` bigint NOT NULL COMMENT '鍚堝悓ID',
  `tenant_id` bigint NOT NULL COMMENT '绉熸埛ID',
  `tenant_name` varchar(50) NOT NULL COMMENT '绉熸埛濮撳悕',
  `house_id` bigint NOT NULL COMMENT '鎴挎簮ID',
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
  `invoice_status` char(1) DEFAULT '0' COMMENT '开票状态 0=未开票 1=开票中 2=已开票',
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
  `overdue_days` int DEFAULT '0' COMMENT '逾期天数',
  PRIMARY KEY (`bill_id`),
  UNIQUE KEY `uk_bill_no` (`bill_no`),
  KEY `idx_contract_id` (`contract_id`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_bill_status` (`bill_status`)
) ENGINE=InnoDB AUTO_INCREMENT=219 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='璐﹀崟琛';



# 转储表 hz_building
# ------------------------------------------------------------

CREATE TABLE `hz_building` (
  `building_id` bigint NOT NULL AUTO_INCREMENT COMMENT '妤兼爧ID',
  `project_id` bigint NOT NULL COMMENT '椤圭洰ID',
  `building_name` varchar(50) NOT NULL COMMENT '妤兼爧鍚嶇О',
  `building_code` varchar(50) DEFAULT NULL,
  `total_floors` int DEFAULT '0' COMMENT '鎬绘ゼ灞傛暟',
  `total_units` int DEFAULT '0' COMMENT '鍗曞厓鏁',
  `total_houses` int DEFAULT '0' COMMENT '鎴挎簮鏁',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:姝ｅ父 1:鍋滅敤)',
  `sort_order` int DEFAULT '0' COMMENT '鏄剧ず椤哄簭',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`building_id`),
  UNIQUE KEY `uk_building_code` (`building_code`),
  KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='妤兼爧琛';



# 转储表 hz_checkin_record
# ------------------------------------------------------------

CREATE TABLE `hz_checkin_record` (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '璁板綍ID',
  `checkin_no` varchar(50) DEFAULT NULL COMMENT '入住单号',
  `apply_id` bigint NOT NULL COMMENT '鐢宠?ID',
  `contract_id` bigint NOT NULL COMMENT '鍚堝悓ID',
  `tenant_id` bigint NOT NULL COMMENT '绉熸埛ID',
  `house_id` bigint NOT NULL COMMENT '鎴挎簮ID',
  `checkin_date` date NOT NULL COMMENT '鍏ヤ綇鏃ユ湡',
  `actual_checkin_date` datetime DEFAULT NULL COMMENT '实际入住日期(用户选择)',
  `roommate_info` text COMMENT '合住人信息(JSON数组)',
  `emergency_contact_name` varchar(50) DEFAULT NULL COMMENT '紧急联系人姓名',
  `emergency_contact_relation` varchar(20) DEFAULT NULL COMMENT '紧急联系人关系',
  `emergency_contact_phone` varchar(20) DEFAULT NULL COMMENT '紧急联系人电话',
  `audit_by` varchar(50) DEFAULT NULL COMMENT '审核人',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `audit_remark` varchar(500) DEFAULT NULL COMMENT '审核备注',
  `checkin_time` datetime DEFAULT NULL COMMENT '鍏ヤ綇鏃堕棿',
  `meter_reading_electric` decimal(10,2) DEFAULT NULL COMMENT '鐢佃〃璇绘暟',
  `meter_reading_water` decimal(10,2) DEFAULT NULL COMMENT '姘磋〃璇绘暟',
  `meter_reading_gas` decimal(10,2) DEFAULT NULL COMMENT '鐕冩皵琛ㄨ?鏁',
  `key_count` int DEFAULT '0' COMMENT '閽ュ寵鏁伴噺',
  `inventory_list_id` bigint DEFAULT NULL COMMENT '娓呭崟ID',
  `checkin_photos` varchar(1000) DEFAULT NULL COMMENT '鍏ヤ綇鐓х墖(澶氫釜鐢ㄩ?鍙峰垎闅?',
  `tenant_signature` text COMMENT '绉熸埛绛惧悕(base64)',
  `manager_signature` text COMMENT '绠＄悊鍛樼?鍚?base64)',
  `manager_id` bigint DEFAULT NULL COMMENT '缁忓姙浜篒D',
  `manager_name` varchar(50) DEFAULT NULL COMMENT '缁忓姙浜哄?鍚',
  `status` char(1) DEFAULT '0' COMMENT '状态(0=待办理,1=待审核,2=已入住,3=已拒绝)',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`record_id`),
  UNIQUE KEY `idx_checkin_no` (`checkin_no`),
  KEY `idx_contract_id` (`contract_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鍏ヤ綇璁板綍琛';



# 转储表 hz_checkout_record
# ------------------------------------------------------------

CREATE TABLE `hz_checkout_record` (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '璁板綍ID',
  `apply_id` bigint NOT NULL COMMENT '鐢宠?ID',
  `contract_id` bigint NOT NULL COMMENT '鍚堝悓ID',
  `tenant_id` bigint NOT NULL COMMENT '绉熸埛ID',
  `house_id` bigint NOT NULL COMMENT '鎴挎簮ID',
  `checkout_date` date NOT NULL COMMENT '閫??鏃ユ湡',
  `checkout_time` datetime DEFAULT NULL COMMENT '閫??鏃堕棿',
  `meter_reading_electric` decimal(10,2) DEFAULT NULL COMMENT '鐢佃〃璇绘暟',
  `meter_reading_water` decimal(10,2) DEFAULT NULL COMMENT '姘磋〃璇绘暟',
  `meter_reading_gas` decimal(10,2) DEFAULT NULL COMMENT '鐕冩皵琛ㄨ?鏁',
  `key_returned` int DEFAULT '0' COMMENT '褰掕繕閽ュ寵鏁伴噺',
  `inventory_list_id` bigint DEFAULT NULL COMMENT '娓呭崟ID',
  `checkout_photos` varchar(1000) DEFAULT NULL COMMENT '閫??鐓х墖(澶氫釜鐢ㄩ?鍙峰垎闅?',
  `damage_description` varchar(500) DEFAULT NULL COMMENT '鎹熷潖鎯呭喌鎻忚堪',
  `damage_deduction` decimal(10,2) DEFAULT '0.00' COMMENT '鎹熷潖璧斿伩閲戦?',
  `utility_bill` decimal(10,2) DEFAULT '0.00' COMMENT '姘寸數鐕冩皵璐',
  `unpaid_rent` decimal(10,2) DEFAULT '0.00' COMMENT '娆犵即绉熼噾',
  `penalty_amount` decimal(10,2) DEFAULT '0.00' COMMENT '杩濈害閲',
  `deposit_refund` decimal(10,2) DEFAULT '0.00' COMMENT '瀹為?鎶奸噾',
  `refund_status` char(1) DEFAULT '0' COMMENT '閫??鐘舵?(0:寰呴?娆?1:宸查?娆?',
  `refund_time` datetime DEFAULT NULL COMMENT '閫??鏃堕棿',
  `payment_method` varchar(20) DEFAULT NULL COMMENT '付款方式（1=现金 2=支付宝 3=微信 4=银行转账）',
  `payment_voucher` varchar(500) DEFAULT NULL COMMENT '付款凭证（图片路径）',
  `payment_remark` varchar(500) DEFAULT NULL COMMENT '付款备注',
  `tenant_signature` text COMMENT '绉熸埛绛惧悕(base64)',
  `manager_signature` text COMMENT '绠＄悊鍛樼?鍚?base64)',
  `manager_id` bigint DEFAULT NULL COMMENT '缁忓姙浜篒D',
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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='閫??璁板綍琛';



# 转储表 hz_co_tenant
# ------------------------------------------------------------

CREATE TABLE `hz_co_tenant` (
  `co_tenant_id` bigint NOT NULL AUTO_INCREMENT COMMENT '鍏卞悓绉熸埛ID',
  `contract_id` bigint NOT NULL COMMENT '鍚堝悓ID',
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鍏卞悓绉熸埛琛';



# 转储表 hz_contract
# ------------------------------------------------------------

CREATE TABLE `hz_contract` (
  `contract_id` bigint NOT NULL AUTO_INCREMENT COMMENT '鍚堝悓ID',
  `contract_no` varchar(50) NOT NULL COMMENT '鍚堝悓缂栧彿',
  `contract_type` char(1) NOT NULL COMMENT '鍚堝悓绫诲瀷(1:浜烘墠鍏?瘬 2:淇濈?鎴?3:甯傚満绉熻祦)',
  `template_id` bigint DEFAULT NULL COMMENT '鍚堝悓妯℃澘ID',
  `batch_id` bigint DEFAULT NULL COMMENT '配租批次ID',
  `tenant_id` bigint NOT NULL COMMENT '绉熸埛ID',
  `tenant_name` varchar(50) NOT NULL COMMENT '绉熸埛濮撳悕',
  `tenant_id_card` varchar(18) NOT NULL COMMENT '绉熸埛韬?唤璇',
  `tenant_phone` varchar(20) NOT NULL COMMENT '绉熸埛鎵嬫満',
  `project_id` bigint NOT NULL COMMENT '椤圭洰ID',
  `house_id` bigint NOT NULL COMMENT '鎴挎簮ID',
  `house_code` varchar(50) NOT NULL COMMENT '鎴挎簮缂栫爜',
  `house_address` varchar(255) DEFAULT NULL COMMENT '鎴挎簮鍦板潃',
  `rent_price` decimal(10,2) NOT NULL COMMENT '绉熼噾(鍏?鏈?',
  `deposit` decimal(10,2) NOT NULL COMMENT '鎶奸噾(鍏?',
  `start_date` date NOT NULL COMMENT '绉熻祦寮??鏃ユ湡',
  `end_date` date NOT NULL COMMENT '绉熻祦缁撴潫鏃ユ湡',
  `rent_months` int NOT NULL COMMENT '绉熻祦鏈堟暟',
  `payment_cycle` char(1) DEFAULT '1' COMMENT '缂磋垂鍛ㄦ湡(1:鏈堜粯 2:瀛ｄ粯 3:鍗婂勾浠?4:骞翠粯)',
  `payment_day` int DEFAULT '5' COMMENT '姣忔湀缂磋垂鏃',
  `free_rent_periods` int DEFAULT '0' COMMENT '免租期数',
  `contract_content` text COMMENT '鍚堝悓鍐呭?',
  `contract_file` varchar(255) DEFAULT NULL COMMENT '鍚堝悓鏂囦欢URL',
  `tenant_signature` text COMMENT '绉熸埛绛惧悕鏁版嵁(base64)',
  `landlord_signature` text COMMENT '鎴夸笢绛惧悕鏁版嵁(base64)',
  `sign_time` datetime DEFAULT NULL COMMENT '绛剧讲鏃堕棿',
  `contract_status` char(1) DEFAULT '0' COMMENT '鍚堝悓鐘舵?(0:鑽夌? 1:寰呯?缃?2:宸茬?缃?3:灞ヨ?涓?4:宸插埌鏈?5:宸茶В绾?',
  `is_renewed` char(1) DEFAULT '0' COMMENT '鏄?惁缁?害(0:鍚?1:鏄?',
  `renewed_contract_id` bigint DEFAULT NULL COMMENT '缁?害鍚堝悓ID',
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
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鍚堝悓琛';



# 转储表 hz_contract_attachment
# ------------------------------------------------------------

CREATE TABLE `hz_contract_attachment` (
  `attachment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '闄勪欢ID',
  `contract_id` bigint NOT NULL COMMENT '鍚堝悓ID',
  `attachment_name` varchar(100) NOT NULL COMMENT '闄勪欢鍚嶇О',
  `attachment_url` varchar(255) NOT NULL COMMENT '闄勪欢URL',
  `attachment_type` varchar(20) DEFAULT NULL COMMENT '闄勪欢绫诲瀷',
  `file_size` bigint DEFAULT NULL COMMENT '鏂囦欢澶у皬(瀛楄妭)',
  `sort_order` int DEFAULT '0' COMMENT '鏄剧ず椤哄簭',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`attachment_id`),
  KEY `idx_contract_id` (`contract_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鍚堝悓闄勪欢琛';



# 转储表 hz_contract_template
# ------------------------------------------------------------

CREATE TABLE `hz_contract_template` (
  `template_id` bigint NOT NULL AUTO_INCREMENT COMMENT '妯℃澘ID',
  `template_name` varchar(100) NOT NULL COMMENT '妯℃澘鍚嶇О',
  `template_code` varchar(50) NOT NULL COMMENT '妯℃澘缂栫爜',
  `contract_type` char(1) NOT NULL COMMENT '鍚堝悓绫诲瀷(1:浜烘墠鍏?瘬 2:淇濈?鎴?3:甯傚満绉熻祦)',
  `template_content` text NOT NULL COMMENT '妯℃澘鍐呭?(鏀?寔鍗犱綅绗?',
  `payment_cycle` varchar(20) DEFAULT NULL COMMENT '付款周期：monthly-按月，quarterly-按季，half_yearly-半年，yearly-按年',
  `contract_duration` int DEFAULT NULL COMMENT '合同期限（月）',
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



# 转储表 hz_enterprise
# ------------------------------------------------------------

CREATE TABLE `hz_enterprise` (
  `enterprise_id` bigint NOT NULL AUTO_INCREMENT COMMENT '浼佷笟ID',
  `enterprise_name` varchar(100) NOT NULL COMMENT '浼佷笟鍚嶇О',
  `social_credit_code` varchar(50) DEFAULT NULL COMMENT '缁熶竴绀句細淇＄敤浠ｇ爜',
  `legal_person` varchar(50) DEFAULT NULL COMMENT '娉曚汉浠ｈ〃',
  `contact_person` varchar(50) DEFAULT NULL COMMENT '鑱旂郴浜',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '鑱旂郴鐢佃瘽',
  `contact_email` varchar(100) DEFAULT NULL COMMENT '鑱旂郴閭??',
  `enterprise_address` varchar(255) DEFAULT NULL COMMENT '浼佷笟鍦板潃',
  `business_license` varchar(255) DEFAULT NULL COMMENT '钀ヤ笟鎵х収URL',
  `employee_count` int DEFAULT '0' COMMENT '鍛樺伐鏁伴噺',
  `rented_count` int DEFAULT '0' COMMENT '宸茬?鎴挎簮鏁',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:姝ｅ父 1:鍋滅敤)',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`enterprise_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='浼佷笟淇℃伅琛';



# 转储表 hz_house
# ------------------------------------------------------------

CREATE TABLE `hz_house` (
  `house_id` bigint NOT NULL AUTO_INCREMENT COMMENT '鎴挎簮ID',
  `project_id` bigint NOT NULL COMMENT '椤圭洰ID',
  `building_id` bigint NOT NULL COMMENT '妤兼爧ID',
  `unit_id` bigint DEFAULT NULL COMMENT '鍗曞厓ID',
  `house_code` varchar(50) NOT NULL COMMENT '鎴挎簮缂栫爜',
  `house_no` varchar(50) NOT NULL COMMENT '鎴块棿鍙',
  `floor` int NOT NULL COMMENT '妤煎眰',
  `house_type_id` bigint DEFAULT NULL COMMENT '鎴峰瀷ID',
  `house_type_name` varchar(50) DEFAULT NULL COMMENT '鎴峰瀷鍚嶇О',
  `area` decimal(8,2) DEFAULT NULL COMMENT '寤虹瓚闈㈢Н(骞虫柟绫?',
  `rent_price` decimal(10,2) DEFAULT NULL COMMENT '绉熼噾(鍏?鏈?',
  `deposit` decimal(10,2) DEFAULT NULL COMMENT '鎶奸噾(鍏?',
  `orientation` varchar(20) DEFAULT NULL COMMENT '鏈濆悜',
  `decoration` varchar(20) DEFAULT NULL COMMENT '瑁呬慨鎯呭喌',
  `facilities` varchar(500) DEFAULT NULL COMMENT '鎴块棿璁炬柦(澶氫釜鐢ㄩ?鍙峰垎闅?',
  `house_status` char(1) DEFAULT '0' COMMENT '鎴挎簮鐘舵?(0:绌虹疆 1:宸查?璁?2:宸插嚭绉?3:缁翠慨涓?4:涓嬫灦)',
  `is_featured` char(1) DEFAULT '0' COMMENT '鏄?惁绮鹃?鎴挎簮(0:鍚?1:鏄?',
  `view_count` int DEFAULT '0' COMMENT '娴忚?娆℃暟',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:姝ｅ父 1:鍋滅敤)',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  `allocation_type` varchar(20) DEFAULT '1' COMMENT '分配类型(1:常规分配 2:集中分配)',
  `is_fresh_graduate` char(1) DEFAULT '0' COMMENT '是否应届生房源(0:否 1:是)',
  `manager_phone` varchar(20) DEFAULT NULL COMMENT '管家电话',
  PRIMARY KEY (`house_id`),
  UNIQUE KEY `uk_house_code` (`house_code`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_building_id` (`building_id`),
  KEY `idx_house_status` (`house_status`),
  KEY `idx_is_featured` (`is_featured`)
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鎴挎簮琛';



# 转储表 hz_house_image
# ------------------------------------------------------------

CREATE TABLE `hz_house_image` (
  `image_id` bigint NOT NULL AUTO_INCREMENT COMMENT '鍥剧墖ID',
  `house_id` bigint NOT NULL COMMENT '鎴挎簮ID',
  `image_url` varchar(255) NOT NULL COMMENT '鍥剧墖URL',
  `image_type` varchar(20) DEFAULT NULL COMMENT '图片类型(1:主图 2:户型图 3:卧室 4:卫生间 5:室内 6:室外)',
  `is_cover` char(1) DEFAULT '0' COMMENT '鏄?惁灏侀潰(0:鍚?1:鏄?',
  `sort_order` int DEFAULT '0' COMMENT '鏄剧ず椤哄簭',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`image_id`),
  KEY `idx_house_id` (`house_id`)
) ENGINE=InnoDB AUTO_INCREMENT=113 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鎴挎簮鍥剧墖琛';



# 转储表 hz_house_type
# ------------------------------------------------------------

CREATE TABLE `hz_house_type` (
  `house_type_id` bigint NOT NULL AUTO_INCREMENT COMMENT '鎴峰瀷ID',
  `project_id` bigint DEFAULT NULL COMMENT '椤圭洰ID',
  `house_type_name` varchar(50) NOT NULL COMMENT '鎴峰瀷鍚嶇О(濡?涓??涓?巺)',
  `house_type_code` varchar(50) DEFAULT NULL,
  `bedroom_count` int DEFAULT '0' COMMENT '鍗у?鏁伴噺',
  `livingroom_count` int DEFAULT '0' COMMENT '瀹㈠巺鏁伴噺',
  `bathroom_count` int DEFAULT '0' COMMENT '鍗?敓闂存暟閲',
  `kitchen_count` int DEFAULT '0' COMMENT '鍘ㄦ埧鏁伴噺',
  `balcony_count` int DEFAULT '0' COMMENT '闃冲彴鏁伴噺',
  `typical_area` decimal(8,2) DEFAULT NULL COMMENT '鍏稿瀷闈㈢Н(骞虫柟绫?',
  `layout_image` varchar(255) DEFAULT NULL COMMENT '鎴峰瀷鍥綰RL',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:姝ｅ父 1:鍋滅敤)',
  `sort_order` int DEFAULT '0' COMMENT '鏄剧ず椤哄簭',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`house_type_id`),
  UNIQUE KEY `uk_house_type_code` (`house_type_code`),
  KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鎴峰瀷琛';



# 转储表 hz_house_vr
# ------------------------------------------------------------

CREATE TABLE `hz_house_vr` (
  `vr_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'VRID',
  `house_id` bigint NOT NULL COMMENT '鎴挎簮ID',
  `vr_name` varchar(100) DEFAULT NULL COMMENT 'VR鍚嶇О',
  `vr_url` varchar(255) NOT NULL COMMENT 'VR鍏ㄦ櫙鍥綰RL',
  `sort_order` int DEFAULT '0' COMMENT '鏄剧ず椤哄簭',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`vr_id`),
  KEY `idx_house_id` (`house_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鎴挎簮VR琛';



# 转储表 hz_invoice_apply
# ------------------------------------------------------------

CREATE TABLE `hz_invoice_apply` (
  `apply_id` bigint NOT NULL AUTO_INCREMENT COMMENT '鐢宠?ID',
  `apply_no` varchar(50) NOT NULL COMMENT '鐢宠?缂栧彿',
  `tenant_id` bigint NOT NULL COMMENT '绉熸埛ID',
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='寮?エ鐢宠?琛';



# 转储表 hz_payment
# ------------------------------------------------------------

CREATE TABLE `hz_payment` (
  `payment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '缂磋垂ID',
  `payment_no` varchar(50) NOT NULL COMMENT '缂磋垂缂栧彿',
  `bill_id` bigint NOT NULL COMMENT '璐﹀崟ID',
  `bill_no` varchar(50) NOT NULL COMMENT '璐﹀崟缂栧彿',
  `tenant_id` bigint NOT NULL COMMENT '绉熸埛ID',
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



# 转储表 hz_project
# ------------------------------------------------------------

CREATE TABLE `hz_project` (
  `project_id` bigint NOT NULL AUTO_INCREMENT COMMENT '椤圭洰ID',
  `project_name` varchar(100) NOT NULL COMMENT '椤圭洰鍚嶇О',
  `project_code` varchar(50) DEFAULT NULL,
  `project_type` char(1) NOT NULL COMMENT '椤圭洰绫诲瀷(1:浜烘墠鍏?瘬 2:淇濈?鎴?3:甯傚満绉熻祦)',
  `address` varchar(255) DEFAULT NULL COMMENT '椤圭洰鍦板潃',
  `longitude` decimal(10,7) DEFAULT NULL COMMENT '缁忓害',
  `latitude` decimal(10,7) DEFAULT NULL COMMENT '绾?害',
  `total_buildings` int DEFAULT '0' COMMENT '鎬绘ゼ鏍嬫暟',
  `total_houses` int DEFAULT '0' COMMENT '鎬绘埧婧愭暟',
  `available_houses` int DEFAULT '0' COMMENT '鍙?敤鎴挎簮鏁',
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
  `manager_id` bigint DEFAULT NULL COMMENT '椤圭洰璐熻矗浜篒D',
  `manager_name` varchar(50) DEFAULT NULL COMMENT '椤圭洰璐熻矗浜哄?鍚',
  `manager_phone` varchar(20) DEFAULT NULL COMMENT '璐熻矗浜虹數璇',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:姝ｅ父 1:鍋滅敤)',
  `sort_order` int DEFAULT '0' COMMENT '鏄剧ず椤哄簭',
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
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='椤圭洰琛';



# 转储表 hz_refund_apply
# ------------------------------------------------------------

CREATE TABLE `hz_refund_apply` (
  `apply_id` bigint NOT NULL AUTO_INCREMENT COMMENT '鐢宠?ID',
  `apply_no` varchar(50) NOT NULL COMMENT '鐢宠?缂栧彿',
  `payment_id` bigint NOT NULL COMMENT '缂磋垂ID',
  `tenant_id` bigint NOT NULL COMMENT '绉熸埛ID',
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='閫??鐢宠?琛';



# 转储表 hz_refund_record
# ------------------------------------------------------------

CREATE TABLE `hz_refund_record` (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '璁板綍ID',
  `refund_no` varchar(50) NOT NULL COMMENT '閫??缂栧彿',
  `apply_id` bigint NOT NULL COMMENT '鐢宠?ID',
  `payment_id` bigint NOT NULL COMMENT '缂磋垂ID',
  `tenant_id` bigint NOT NULL COMMENT '绉熸埛ID',
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



# 转储表 hz_tenant
# ------------------------------------------------------------

CREATE TABLE `hz_tenant` (
  `tenant_id` bigint NOT NULL AUTO_INCREMENT COMMENT '绉熸埛ID',
  `user_id` bigint DEFAULT NULL COMMENT '鍏宠仈鐢ㄦ埛ID(閮戝ソ鍔炵敤鎴?',
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
  `credit_score` int DEFAULT '100' COMMENT '淇＄敤鍒?榛樿?100)',
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



# 转储表 hz_tenant_document
# ------------------------------------------------------------

CREATE TABLE `hz_tenant_document` (
  `document_id` bigint NOT NULL AUTO_INCREMENT COMMENT '璧勬枡ID',
  `tenant_id` bigint NOT NULL COMMENT '绉熸埛ID',
  `document_type` varchar(50) NOT NULL COMMENT '璧勬枡绫诲瀷(韬?唤璇?瀛﹀巻璇佹槑/绀句繚璇佹槑绛?',
  `document_name` varchar(100) NOT NULL COMMENT '璧勬枡鍚嶇О',
  `document_url` varchar(255) NOT NULL COMMENT '璧勬枡URL',
  `file_size` bigint DEFAULT NULL COMMENT '鏂囦欢澶у皬(瀛楄妭)',
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



# 转储表 hz_unit
# ------------------------------------------------------------

CREATE TABLE `hz_unit` (
  `unit_id` bigint NOT NULL AUTO_INCREMENT COMMENT '鍗曞厓ID',
  `building_id` bigint NOT NULL COMMENT '妤兼爧ID',
  `unit_name` varchar(50) NOT NULL COMMENT '鍗曞厓鍚嶇О',
  `unit_code` varchar(50) DEFAULT NULL,
  `total_houses` int DEFAULT '0' COMMENT '鎴挎簮鏁',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0:姝ｅ父 1:鍋滅敤)',
  `sort_order` int DEFAULT '0' COMMENT '鏄剧ず椤哄簭',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `del_flag` char(1) DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0:姝ｅ父 2:鍒犻櫎)',
  PRIMARY KEY (`unit_id`),
  UNIQUE KEY `uk_unit_code` (`unit_code`),
  KEY `idx_building_id` (`building_id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鍗曞厓琛';



# 转储表 hz_user
# ------------------------------------------------------------

CREATE TABLE `hz_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';



# 转储表 sys_user_role
# ------------------------------------------------------------

DROP TABLE IF EXISTS `sys_user_role`;

CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='用户和角色关联表';

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;

INSERT INTO `sys_user_role` (`user_id`, `role_id`)
VALUES
	(1,1),
	(2,2);

/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
