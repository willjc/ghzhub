-- ----------------------------
-- 户型图片表
-- ----------------------------
DROP TABLE IF EXISTS `hz_house_type_image`;
CREATE TABLE `hz_house_type_image` (
  `image_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `house_type_id` bigint(20) NOT NULL COMMENT '户型ID',
  `image_url` varchar(500) NOT NULL COMMENT '图片URL',
  `image_type` char(1) DEFAULT '1' COMMENT '图片类型(1:户型图 2:实景图)',
  `is_cover` char(1) DEFAULT '0' COMMENT '是否封面(0:否 1:是)',
  `sort_order` int(4) DEFAULT '0' COMMENT '显示顺序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0:正常 2:删除)',
  PRIMARY KEY (`image_id`),
  KEY `idx_house_type_id` (`house_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='户型图片表';
