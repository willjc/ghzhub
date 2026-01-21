-- 运营配置表
DROP TABLE IF EXISTS `hz_operation_config`;
CREATE TABLE `hz_operation_config` (
  `config_id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_type` varchar(20) NOT NULL COMMENT '配置类型（banner=轮播图 ad=广告位 notice=通知公告 icon=功能图标）',
  `title` varchar(100) NOT NULL COMMENT '标题/名称',
  `image_url` varchar(500) DEFAULT NULL COMMENT '图片地址（相对路径）',
  `content` text COMMENT '文字描述/内容',
  `link_url` varchar(500) DEFAULT NULL COMMENT '跳转链接',
  `link_type` varchar(20) DEFAULT 'none' COMMENT '链接类型（page=页面路径 url=外部链接 none=无链接）',
  `sort_order` int DEFAULT 0 COMMENT '排序号（数字越小越靠前）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0=启用 1=禁用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`),
  KEY `idx_config_type` (`config_type`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='运营配置表';

-- 插入示例数据
INSERT INTO `hz_operation_config` VALUES
(1, 'banner', '新春租房优惠活动', '/profile/upload/banner/demo1.jpg', '新春特惠，人才公寓租金8折起', '/pages/coupon/index', 'page', 1, '0', 'admin', NOW(), NULL, NULL, '首页轮播图示例1'),
(2, 'banner', '保租房申请指南', '/profile/upload/banner/demo2.jpg', '保租房申请流程详解', '/pages/talent/index', 'page', 2, '0', 'admin', NOW(), NULL, NULL, '首页轮播图示例2'),
(3, 'notice', '重要通知', NULL, '港好住平台系统升级公告，将于本周六凌晨进行系统维护', '/pages/notice/detail', 'page', 1, '0', 'admin', NOW(), NULL, NULL, '滚动通知示例');
