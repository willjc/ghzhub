-- 用户信息表
DROP TABLE IF EXISTS `hz_user`;
CREATE TABLE `hz_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `phone` varchar(11) NOT NULL COMMENT '手机号（登录账号）',
  `contact_phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- 插入测试数据（与登录页面测试账号对应）
INSERT INTO `hz_user` VALUES
(1, '13800138001', '1', 'wx_test_001', NULL, NULL, '微信测试用户', '/profile/avatar/default.jpg', NULL, '0', NULL, NULL, 'wechat', '0', '0', NOW(), '127.0.0.1', '0', 'admin', NOW(), NULL, NULL, '微信登录测试账号'),
(2, '13800138002', '2', NULL, NULL, 'zhb_test_001', '郑好办测试用户', '/profile/avatar/default.jpg', NULL, '0', NULL, NULL, 'zhenghaoban', '0', '0', NOW(), '127.0.0.1', '0', 'admin', NOW(), NULL, NULL, '郑好办登录测试账号');

-- 数据字典：用户来源类型
DELETE FROM sys_dict_type WHERE dict_type = 'hz_user_source_type';
INSERT INTO sys_dict_type VALUES (NULL, 0, '用户来源类型', 'hz_user_source_type', '0', 'admin', NOW(), NULL, NULL, '港好住用户来源类型');

DELETE FROM sys_dict_data WHERE dict_type = 'hz_user_source_type';
INSERT INTO sys_dict_data VALUES (NULL, 0, 1, '微信小程序', '1', 'hz_user_source_type', '', 'primary', 'N', '0', 'admin', NOW(), NULL, NULL, '微信小程序注册');
INSERT INTO sys_dict_data VALUES (NULL, 0, 2, '郑好办', '2', 'hz_user_source_type', '', 'success', 'N', '0', 'admin', NOW(), NULL, NULL, '郑好办注册');

-- 数据字典：学历类型
DELETE FROM sys_dict_type WHERE dict_type = 'hz_education_type';
INSERT INTO sys_dict_type VALUES (NULL, 0, '学历类型', 'hz_education_type', '0', 'admin', NOW(), NULL, NULL, '用户学历类型');

DELETE FROM sys_dict_data WHERE dict_type = 'hz_education_type';
INSERT INTO sys_dict_data VALUES (NULL, 0, 1, '小学', '1', 'hz_education_type', '', '', 'N', '0', 'admin', NOW(), NULL, NULL, '');
INSERT INTO sys_dict_data VALUES (NULL, 0, 2, '初中', '2', 'hz_education_type', '', '', 'N', '0', 'admin', NOW(), NULL, NULL, '');
INSERT INTO sys_dict_data VALUES (NULL, 0, 3, '高中', '3', 'hz_education_type', '', '', 'N', '0', 'admin', NOW(), NULL, NULL, '');
INSERT INTO sys_dict_data VALUES (NULL, 0, 4, '大专', '4', 'hz_education_type', '', '', 'N', '0', 'admin', NOW(), NULL, NULL, '');
INSERT INTO sys_dict_data VALUES (NULL, 0, 5, '本科', '5', 'hz_education_type', '', '', 'N', '0', 'admin', NOW(), NULL, NULL, '');
INSERT INTO sys_dict_data VALUES (NULL, 0, 6, '硕士', '6', 'hz_education_type', '', '', 'N', '0', 'admin', NOW(), NULL, NULL, '');
INSERT INTO sys_dict_data VALUES (NULL, 0, 7, '博士', '7', 'hz_education_type', '', '', 'N', '0', 'admin', NOW(), NULL, NULL, '');

-- 数据字典：身份类型
DELETE FROM sys_dict_type WHERE dict_type = 'hz_identity_type';
INSERT INTO sys_dict_type VALUES (NULL, 0, '身份类型', 'hz_identity_type', '0', 'admin', NOW(), NULL, NULL, '用户身份类型');

DELETE FROM sys_dict_data WHERE dict_type = 'hz_identity_type';
INSERT INTO sys_dict_data VALUES (NULL, 0, 1, '在职人员', '1', 'hz_identity_type', '', 'primary', 'N', '0', 'admin', NOW(), NULL, NULL, '在职人员');
INSERT INTO sys_dict_data VALUES (NULL, 0, 2, '应届毕业生', '2', 'hz_identity_type', '', 'success', 'N', '0', 'admin', NOW(), NULL, NULL, '应届毕业生');
