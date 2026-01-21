-- 运营配置数据字典
-- 配置类型字典
INSERT INTO sys_dict_type VALUES (NULL, '运营配置类型', 'hz_config_type', '0', 'admin', NOW(), NULL, NULL, '运营配置类型字典');

-- 配置类型字典数据
INSERT INTO sys_dict_data VALUES (NULL, 1, '轮播图', 'banner', 'hz_config_type', NULL, 'success', 'N', '0', 'admin', NOW(), NULL, NULL, '首页轮播图');
INSERT INTO sys_dict_data VALUES (NULL, 2, '广告位', 'ad', 'hz_config_type', NULL, 'warning', 'N', '0', 'admin', NOW(), NULL, NULL, '广告位');
INSERT INTO sys_dict_data VALUES (NULL, 3, '通知公告', 'notice', 'hz_config_type', NULL, 'info', 'N', '0', 'admin', NOW(), NULL, NULL, '通知公告');
INSERT INTO sys_dict_data VALUES (NULL, 4, '功能图标', 'icon', 'hz_config_type', NULL, 'primary', 'N', '0', 'admin', NOW(), NULL, NULL, '功能图标');

-- 链接类型字典
INSERT INTO sys_dict_type VALUES (NULL, '链接类型', 'hz_link_type', '0', 'admin', NOW(), NULL, NULL, '链接类型字典');

-- 链接类型字典数据
INSERT INTO sys_dict_data VALUES (NULL, 1, '页面路径', 'page', 'hz_link_type', NULL, 'primary', 'N', '0', 'admin', NOW(), NULL, NULL, 'UniApp页面路径');
INSERT INTO sys_dict_data VALUES (NULL, 2, '外部链接', 'url', 'hz_link_type', NULL, 'success', 'N', '0', 'admin', NOW(), NULL, NULL, '外部URL链接');
INSERT INTO sys_dict_data VALUES (NULL, 3, '无链接', 'none', 'hz_link_type', NULL, 'info', 'N', '0', 'admin', NOW(), NULL, NULL, '仅展示不跳转');
