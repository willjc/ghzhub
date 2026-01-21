-- 港好住信息系统后台管理菜单配置
-- 使用说明: 在MySQL中执行此SQL文件来添加后台管理菜单

-- 一级菜单: 港好住管理
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('港好住管理', 0, 5, 'gangzhu', NULL, 1, 0, 'M', '0', '0', '', 'guide', 'admin', sysdate(), '', NULL, '港好住业务管理');

-- 获取刚插入的一级菜单ID (注意: 实际执行时需要根据实际插入的menu_id调整后续的parent_id)
-- 假设插入的menu_id为2000

-- ========== 房源管理模块 ==========
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('房源管理', 2000, 1, 'house', 'gangzhu/house/index', 1, 0, 'C', '0', '0', 'gangzhu:house:list', 'build', 'admin', sysdate(), '房源管理菜单');

-- 房源管理按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('房源查询', 2001, 1, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:house:query', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('房源新增', 2001, 2, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:house:add', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('房源修改', 2001, 3, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:house:edit', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('房源删除', 2001, 4, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:house:remove', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('房源导出', 2001, 5, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:house:export', '#', 'admin', sysdate(), '');

-- ========== 项目管理模块 ==========
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('项目管理', 2000, 2, 'project', 'gangzhu/project/index', 1, 0, 'C', '0', '0', 'gangzhu:project:list', 'tree', 'admin', sysdate(), '项目管理菜单');

-- 项目管理按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('项目查询', 2007, 1, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:project:query', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('项目新增', 2007, 2, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:project:add', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('项目修改', 2007, 3, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:project:edit', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('项目删除', 2007, 4, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:project:remove', '#', 'admin', sysdate(), '');

-- ========== 租户管理模块 ==========
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('租户管理', 2000, 3, 'tenant', 'gangzhu/tenant/index', 1, 0, 'C', '0', '0', 'gangzhu:tenant:list', 'peoples', 'admin', sysdate(), '租户管理菜单');

-- 租户管理按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('租户查询', 2012, 1, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:tenant:query', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('租户详情', 2012, 2, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:tenant:detail', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('租户修改', 2012, 3, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:tenant:edit', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('租户导出', 2012, 4, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:tenant:export', '#', 'admin', sysdate(), '');

-- ========== 资格审核模块 ==========
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('资格审核', 2000, 4, 'qualification', 'gangzhu/qualification/index', 1, 0, 'C', '0', '0', 'gangzhu:qualification:list', 'form', 'admin', sysdate(), '资格审核菜单');

-- 资格审核按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('审核查询', 2017, 1, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:qualification:query', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('审核通过', 2017, 2, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:qualification:approve', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('审核拒绝', 2017, 3, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:qualification:reject', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('审核导出', 2017, 4, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:qualification:export', '#', 'admin', sysdate(), '');

-- ========== 合同管理模块 ==========
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('合同管理', 2000, 5, 'contract', 'gangzhu/contract/index', 1, 0, 'C', '0', '0', 'gangzhu:contract:list', 'documentation', 'admin', sysdate(), '合同管理菜单');

-- 合同管理按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('合同查询', 2022, 1, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:contract:query', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('合同详情', 2022, 2, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:contract:detail', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('合同审核', 2022, 3, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:contract:audit', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('合同导出', 2022, 4, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:contract:export', '#', 'admin', sysdate(), '');

-- ========== 账单管理模块 ==========
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('账单管理', 2000, 6, 'bill', 'gangzhu/bill/index', 1, 0, 'C', '0', '0', 'gangzhu:bill:list', 'money', 'admin', sysdate(), '账单管理菜单');

-- 账单管理按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('账单查询', 2027, 1, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:bill:query', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('账单新增', 2027, 2, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:bill:add', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('账单修改', 2027, 3, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:bill:edit', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('账单导出', 2027, 4, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:bill:export', '#', 'admin', sysdate(), '');

-- ========== 入住管理模块 ==========
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('入住管理', 2000, 7, 'checkin', 'gangzhu/checkin/index', 1, 0, 'C', '0', '0', 'gangzhu:checkin:list', 'log', 'admin', sysdate(), '入住管理菜单');

-- 入住管理按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('入住查询', 2032, 1, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:checkin:query', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('入住审核', 2032, 2, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:checkin:audit', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('入住导出', 2032, 3, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:checkin:export', '#', 'admin', sysdate(), '');

-- ========== 退租管理模块 ==========
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('退租管理', 2000, 8, 'checkout', 'gangzhu/checkout/index', 1, 0, 'C', '0', '0', 'gangzhu:checkout:list', 'exit', 'admin', sysdate(), '退租管理菜单');

-- 退租管理按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('退租查询', 2036, 1, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:checkout:query', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('退租审核', 2036, 2, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:checkout:audit', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('退租导出', 2036, 3, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:checkout:export', '#', 'admin', sysdate(), '');

-- ========== 预约管理模块 ==========
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('预约管理', 2000, 9, 'appointment', 'gangzhu/appointment/index', 1, 0, 'C', '0', '0', 'gangzhu:appointment:list', 'date', 'admin', sysdate(), '预约管理菜单');

-- 预约管理按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('预约查询', 2040, 1, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:appointment:query', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('预约确认', 2040, 2, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:appointment:confirm', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('预约取消', 2040, 3, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:appointment:cancel', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('预约导出', 2040, 4, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:appointment:export', '#', 'admin', sysdate(), '');

-- ========== 资料审核模块 ==========
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('资料审核', 2000, 10, 'document', 'gangzhu/document/index', 1, 0, 'C', '0', '0', 'gangzhu:document:list', 'skill', 'admin', sysdate(), '资料审核菜单');

-- 资料审核按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('资料查询', 2045, 1, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:document:query', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('资料审核', 2045, 2, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:document:audit', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('资料导出', 2045, 3, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:document:export', '#', 'admin', sysdate(), '');

-- ========== 消息管理模块 ==========
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('消息管理', 2000, 11, 'message', 'gangzhu/message/index', 1, 0, 'C', '0', '0', 'gangzhu:message:list', 'email', 'admin', sysdate(), '消息管理菜单');

-- 消息管理按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('消息查询', 2049, 1, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:message:query', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('消息发送', 2049, 2, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:message:send', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('消息删除', 2049, 3, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:message:remove', '#', 'admin', sysdate(), '');

-- ========== 公告管理模块 ==========
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('公告管理', 2000, 12, 'announcement', 'gangzhu/announcement/index', 1, 0, 'C', '0', '0', 'gangzhu:announcement:list', 'message', 'admin', sysdate(), '公告管理菜单');

-- 公告管理按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('公告查询', 2053, 1, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:announcement:query', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('公告新增', 2053, 2, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:announcement:add', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('公告修改', 2053, 3, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:announcement:edit', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('公告删除', 2053, 4, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:announcement:remove', '#', 'admin', sysdate(), '');

-- ========== 开票管理模块 ==========
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('开票管理', 2000, 13, 'invoice', 'gangzhu/invoice/index', 1, 0, 'C', '0', '0', 'gangzhu:invoice:list', 'post', 'admin', sysdate(), '开票管理菜单');

-- 开票管理按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('开票查询', 2058, 1, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:invoice:query', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('开票审核', 2058, 2, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:invoice:audit', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('开票导出', 2058, 3, '#', '', 1, 0, 'F', '0', '0', 'gangzhu:invoice:export', '#', 'admin', sysdate(), '');

-- 说明:
-- 1. 上述SQL中的menu_id从2000开始是示例,实际执行时需要根据数据库中已有的最大menu_id进行调整
-- 2. 执行前建议先查询: SELECT MAX(menu_id) FROM sys_menu;
-- 3. 然后将所有的2000、2001等替换为实际的ID值
-- 4. 菜单类型: M=目录 C=菜单 F=按钮
-- 5. is_frame: 1=否(内链) 0=是(外链)
-- 6. visible: 0=显示 1=隐藏
-- 7. status: 0=正常 1=停用
