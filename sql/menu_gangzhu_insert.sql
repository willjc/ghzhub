-- 港好住信息系统后台管理菜单配置
-- 数据库: newgangzhu
-- 执行时间: 2025-01-18

-- 一级菜单: 港好住管理 (menu_id=2000)
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2000, '港好住管理', 0, 5, 'gangzhu', NULL, 1, 0, 'M', '0', '0', '', 'guide', 'admin', sysdate(), '港好住业务管理');

-- ========== 房源管理模块 (menu_id=2001-2006) ==========
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2001, '房源管理', 2000, 1, 'house', 'gangzhu/house/index', 1, 0, 'C', '0', '0', 'gangzhu:house:list', 'build', 'admin', sysdate(), '房源管理菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2002, '房源查询', 2001, 1, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:house:query', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2003, '房源新增', 2001, 2, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:house:add', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2004, '房源修改', 2001, 3, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:house:edit', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2005, '房源删除', 2001, 4, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:house:remove', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2006, '房源导出', 2001, 5, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:house:export', '#', 'admin', sysdate(), '');

-- ========== 项目管理模块 (menu_id=2007-2011) ==========
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2007, '项目管理', 2000, 2, 'project', 'gangzhu/project/index', 1, 0, 'C', '0', '0', 'gangzhu:project:list', 'tree', 'admin', sysdate(), '项目管理菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2008, '项目查询', 2007, 1, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:project:query', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2009, '项目新增', 2007, 2, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:project:add', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2010, '项目修改', 2007, 3, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:project:edit', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2011, '项目删除', 2007, 4, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:project:remove', '#', 'admin', sysdate(), '');

-- ========== 租户管理模块 (menu_id=2012-2016) ==========
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2012, '租户管理', 2000, 3, 'tenant', 'gangzhu/tenant/index', 1, 0, 'C', '0', '0', 'gangzhu:tenant:list', 'peoples', 'admin', sysdate(), '租户管理菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2013, '租户查询', 2012, 1, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:tenant:query', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2014, '租户详情', 2012, 2, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:tenant:detail', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2015, '租户修改', 2012, 3, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:tenant:edit', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2016, '租户导出', 2012, 4, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:tenant:export', '#', 'admin', sysdate(), '');

-- ========== 资格审核模块 (menu_id=2017-2021) ==========
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2017, '资格审核', 2000, 4, 'qualification', 'gangzhu/qualification/index', 1, 0, 'C', '0', '0', 'gangzhu:qualification:list', 'form', 'admin', sysdate(), '资格审核菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2018, '审核查询', 2017, 1, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:qualification:query', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2019, '审核通过', 2017, 2, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:qualification:approve', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2020, '审核拒绝', 2017, 3, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:qualification:reject', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2021, '审核导出', 2017, 4, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:qualification:export', '#', 'admin', sysdate(), '');

-- ========== 合同管理模块 (menu_id=2022-2026) ==========
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2022, '合同管理', 2000, 5, 'contract', 'gangzhu/contract/index', 1, 0, 'C', '0', '0', 'gangzhu:contract:list', 'documentation', 'admin', sysdate(), '合同管理菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2023, '合同查询', 2022, 1, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:contract:query', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2024, '合同详情', 2022, 2, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:contract:detail', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2025, '合同审核', 2022, 3, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:contract:audit', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2026, '合同导出', 2022, 4, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:contract:export', '#', 'admin', sysdate(), '');

-- ========== 账单管理模块 (menu_id=2027-2031) ==========
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2027, '账单管理', 2000, 6, 'bill', 'gangzhu/bill/index', 1, 0, 'C', '0', '0', 'gangzhu:bill:list', 'money', 'admin', sysdate(), '账单管理菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2028, '账单查询', 2027, 1, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:bill:query', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2029, '账单新增', 2027, 2, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:bill:add', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2030, '账单修改', 2027, 3, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:bill:edit', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2031, '账单导出', 2027, 4, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:bill:export', '#', 'admin', sysdate(), '');

-- ========== 入住管理模块 (menu_id=2032-2035) ==========
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2032, '入住管理', 2000, 7, 'checkin', 'gangzhu/checkin/index', 1, 0, 'C', '0', '0', 'gangzhu:checkin:list', 'log', 'admin', sysdate(), '入住管理菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2033, '入住查询', 2032, 1, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:checkin:query', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2034, '入住审核', 2032, 2, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:checkin:audit', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2035, '入住导出', 2032, 3, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:checkin:export', '#', 'admin', sysdate(), '');

-- ========== 退租管理模块 (menu_id=2036-2039) ==========
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2036, '退租管理', 2000, 8, 'checkout', 'gangzhu/checkout/index', 1, 0, 'C', '0', '0', 'gangzhu:checkout:list', 'exit', 'admin', sysdate(), '退租管理菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2037, '退租查询', 2036, 1, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:checkout:query', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2038, '退租审核', 2036, 2, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:checkout:audit', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2039, '退租导出', 2036, 3, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:checkout:export', '#', 'admin', sysdate(), '');

-- ========== 预约管理模块 (menu_id=2040-2044) ==========
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2040, '预约管理', 2000, 9, 'appointment', 'gangzhu/appointment/index', 1, 0, 'C', '0', '0', 'gangzhu:appointment:list', 'date', 'admin', sysdate(), '预约管理菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2041, '预约查询', 2040, 1, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:appointment:query', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2042, '预约确认', 2040, 2, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:appointment:confirm', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2043, '预约取消', 2040, 3, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:appointment:cancel', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2044, '预约导出', 2040, 4, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:appointment:export', '#', 'admin', sysdate(), '');

-- ========== 资料审核模块 (menu_id=2045-2048) ==========
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2045, '资料审核', 2000, 10, 'document', 'gangzhu/document/index', 1, 0, 'C', '0', '0', 'gangzhu:document:list', 'skill', 'admin', sysdate(), '资料审核菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2046, '资料查询', 2045, 1, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:document:query', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2047, '资料审核', 2045, 2, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:document:audit', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2048, '资料导出', 2045, 3, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:document:export', '#', 'admin', sysdate(), '');

-- ========== 消息管理模块 (menu_id=2049-2052) ==========
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2049, '消息管理', 2000, 11, 'message', 'gangzhu/message/index', 1, 0, 'C', '0', '0', 'gangzhu:message:list', 'email', 'admin', sysdate(), '消息管理菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2050, '消息查询', 2049, 1, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:message:query', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2051, '消息发送', 2049, 2, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:message:send', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2052, '消息删除', 2049, 3, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:message:remove', '#', 'admin', sysdate(), '');

-- ========== 公告管理模块 (menu_id=2053-2057) ==========
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2053, '公告管理', 2000, 12, 'announcement', 'gangzhu/announcement/index', 1, 0, 'C', '0', '0', 'gangzhu:announcement:list', 'message', 'admin', sysdate(), '公告管理菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2054, '公告查询', 2053, 1, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:announcement:query', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2055, '公告新增', 2053, 2, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:announcement:add', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2056, '公告修改', 2053, 3, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:announcement:edit', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2057, '公告删除', 2053, 4, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:announcement:remove', '#', 'admin', sysdate(), '');

-- ========== 开票管理模块 (menu_id=2058-2061) ==========
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2058, '开票管理', 2000, 13, 'invoice', 'gangzhu/invoice/index', 1, 0, 'C', '0', '0', 'gangzhu:invoice:list', 'post', 'admin', sysdate(), '开票管理菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2059, '开票查询', 2058, 1, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:invoice:query', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2060, '开票审核', 2058, 2, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:invoice:audit', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2061, '开票导出', 2058, 3, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:invoice:export', '#', 'admin', sysdate(), '');
