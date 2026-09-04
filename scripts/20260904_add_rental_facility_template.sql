-- 保租房/市场租赁共用点验单设施映射（MySQL 8.0+，可重复执行）
CREATE TABLE IF NOT EXISTS hz_facility_template_item (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    template_type VARCHAR(20) NOT NULL COMMENT '点验单模板类型',
    facility_item_id BIGINT NOT NULL COMMENT '设施物品ID',
    esign_component_key VARCHAR(64) DEFAULT NULL COMMENT 'e签宝控件编码（人才公寓旧模板可为空）',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    status CHAR(1) NOT NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
    del_flag CHAR(1) NOT NULL DEFAULT '0' COMMENT '删除标志(0正常 1删除)',
    create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (id),
    UNIQUE KEY uk_template_facility (template_type, facility_item_id),
    UNIQUE KEY uk_template_component (template_type, esign_component_key),
    KEY idx_facility_item_id (facility_item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点验单模板设施映射';

ALTER TABLE hz_facility_template_item
    MODIFY COLUMN esign_component_key VARCHAR(64) DEFAULT NULL COMMENT 'e签宝控件编码（人才公寓旧模板可为空）';

-- 先固化人才公寓当前可用设施；后续新增的 RENTAL 专用设施不会混入人才公寓。
INSERT INTO hz_facility_template_item
    (template_type, facility_item_id, esign_component_key, sort_order, status, del_flag, create_by, create_time)
SELECT 'TALENT', fi.facility_item_id, NULL, COALESCE(fi.sort_order, 0), '0', '0', 'migration', NOW()
FROM hz_facility_item fi
LEFT JOIN hz_facility_template_item rental
    ON rental.template_type = 'RENTAL'
    AND rental.facility_item_id = fi.facility_item_id
    AND rental.del_flag = '0'
WHERE fi.status = '0' AND fi.del_flag = '0' AND rental.id IS NULL
ON DUPLICATE KEY UPDATE
    sort_order = VALUES(sort_order),
    status = '0',
    del_flag = '0',
    update_by = 'migration',
    update_time = NOW();

SET @rental_facilities = JSON_ARRAY(
  JSON_OBJECT('name','电视','category','电气类','key','rental_facility_tv'),
  JSON_OBJECT('name','空调','category','电气类','key','rental_facility_air_conditioner'),
  JSON_OBJECT('name','洗衣机','category','电气类','key','rental_facility_washing_machine'),
  JSON_OBJECT('name','冰箱','category','电气类','key','rental_facility_refrigerator'),
  JSON_OBJECT('name','热水器','category','电气类','key','rental_facility_water_heater'),
  JSON_OBJECT('name','燃气灶','category','电气类','key','rental_facility_gas_stove'),
  JSON_OBJECT('name','抽油烟机','category','电气类','key','rental_facility_range_hood'),
  JSON_OBJECT('name','客厅灯具','category','灯具类','key','rental_facility_living_room_light'),
  JSON_OBJECT('name','卧室灯具','category','灯具类','key','rental_facility_bedroom_light'),
  JSON_OBJECT('name','厨房灯具','category','灯具类','key','rental_facility_kitchen_light'),
  JSON_OBJECT('name','卫生间灯','category','灯具类','key','rental_facility_bathroom_light'),
  JSON_OBJECT('name','阳台灯具','category','灯具类','key','rental_facility_balcony_light'),
  JSON_OBJECT('name','卫生间淋浴','category','卫浴类','key','rental_facility_bathroom_shower'),
  JSON_OBJECT('name','镜面','category','卫浴类','key','rental_facility_mirror'),
  JSON_OBJECT('name','洗漱台','category','卫浴类','key','rental_facility_washstand'),
  JSON_OBJECT('name','水池','category','卫浴类','key','rental_facility_sink'),
  JSON_OBJECT('name','软管','category','卫浴类','key','rental_facility_hose'),
  JSON_OBJECT('name','马桶','category','卫浴类','key','rental_facility_toilet'),
  JSON_OBJECT('name','厨房水龙头','category','厨房类','key','rental_facility_kitchen_faucet'),
  JSON_OBJECT('name','洗菜池','category','厨房类','key','rental_facility_kitchen_sink'),
  JSON_OBJECT('name','墙面','category','墙地面类','key','rental_facility_wall'),
  JSON_OBJECT('name','地板','category','墙地面类','key','rental_facility_floor'),
  JSON_OBJECT('name','地毯','category','墙地面类','key','rental_facility_carpet'),
  JSON_OBJECT('name','电闸盒','category','电气类','key','rental_facility_breaker_box'),
  JSON_OBJECT('name','瓷砖','category','墙地面类','key','rental_facility_tile'),
  JSON_OBJECT('name','入户门','category','门窗类','key','rental_facility_entry_door'),
  JSON_OBJECT('name','密码锁','category','门窗类','key','rental_facility_smart_lock'),
  JSON_OBJECT('name','套房内门','category','门窗类','key','rental_facility_interior_door'),
  JSON_OBJECT('name','厨房推拉门','category','门窗类','key','rental_facility_kitchen_sliding_door'),
  JSON_OBJECT('name','窗户','category','门窗类','key','rental_facility_window'),
  JSON_OBJECT('name','客厅窗帘','category','门窗类','key','rental_facility_living_room_curtain'),
  JSON_OBJECT('name','卧室窗帘','category','门窗类','key','rental_facility_bedroom_curtain'),
  JSON_OBJECT('name','客厅茶几','category','家具类','key','rental_facility_coffee_table'),
  JSON_OBJECT('name','餐桌','category','家具类','key','rental_facility_dining_table'),
  JSON_OBJECT('name','椅子','category','家具类','key','rental_facility_chair'),
  JSON_OBJECT('name','桌子','category','家具类','key','rental_facility_desk'),
  JSON_OBJECT('name','电视柜','category','家具类','key','rental_facility_tv_cabinet'),
  JSON_OBJECT('name','橱柜','category','家具类','key','rental_facility_kitchen_cabinet'),
  JSON_OBJECT('name','鞋柜','category','家具类','key','rental_facility_shoe_cabinet'),
  JSON_OBJECT('name','衣柜','category','家具类','key','rental_facility_wardrobe'),
  JSON_OBJECT('name','床头柜','category','家具类','key','rental_facility_bedside_table'),
  JSON_OBJECT('name','沙发','category','家具类','key','rental_facility_sofa'),
  JSON_OBJECT('name','床','category','家具类','key','rental_facility_bed'),
  JSON_OBJECT('name','床垫','category','家具类','key','rental_facility_mattress'),
  JSON_OBJECT('name','晾衣架','category','家具类','key','rental_facility_drying_rack')
);

-- 补齐总字典中缺少的物品，不改动已有物品。
INSERT INTO hz_facility_item
    (facility_name, facility_category, sort_order, status, del_flag, create_by, create_time)
SELECT jt.name, jt.category, jt.ord, '0', '0', 'migration', NOW()
FROM JSON_TABLE(@rental_facilities, '$[*]' COLUMNS (
    ord FOR ORDINALITY,
    name VARCHAR(100) PATH '$.name',
    category VARCHAR(50) PATH '$.category'
)) jt
LEFT JOIN hz_facility_item fi
    ON fi.facility_name = jt.name AND fi.status = '0' AND fi.del_flag = '0'
WHERE fi.facility_item_id IS NULL;

-- 建立 RENTAL 点验单映射；已存在时只刷新控件编码、排序并启用。
INSERT INTO hz_facility_template_item
    (template_type, facility_item_id, esign_component_key, sort_order, status, del_flag, create_by, create_time)
SELECT 'RENTAL', fi.facility_item_id, jt.component_key, jt.ord, '0', '0', 'migration', NOW()
FROM JSON_TABLE(@rental_facilities, '$[*]' COLUMNS (
    ord FOR ORDINALITY,
    name VARCHAR(100) PATH '$.name',
    component_key VARCHAR(64) PATH '$.key'
)) jt
JOIN (
    SELECT facility_name, MIN(facility_item_id) AS facility_item_id
    FROM hz_facility_item
    WHERE status = '0' AND del_flag = '0'
    GROUP BY facility_name
) fi ON fi.facility_name = jt.name
ON DUPLICATE KEY UPDATE
    esign_component_key = VALUES(esign_component_key),
    sort_order = VALUES(sort_order),
    status = '0',
    del_flag = '0',
    update_by = 'migration',
    update_time = NOW();

-- 执行后应返回 45、45、0。
SELECT COUNT(*) AS rental_mapping_count
FROM hz_facility_template_item
WHERE template_type = 'RENTAL' AND status = '0' AND del_flag = '0';

SELECT COUNT(DISTINCT fi.facility_name) AS rental_facility_count
FROM hz_facility_template_item m
JOIN hz_facility_item fi ON fi.facility_item_id = m.facility_item_id
WHERE m.template_type = 'RENTAL' AND m.status = '0' AND m.del_flag = '0'
  AND fi.status = '0' AND fi.del_flag = '0';

SELECT COUNT(*) AS missing_component_key_count
FROM hz_facility_template_item
WHERE template_type = 'RENTAL' AND status = '0' AND del_flag = '0'
  AND (esign_component_key IS NULL OR esign_component_key = '');

-- 回滚（仅在确认没有保租房/市场租赁设施数据引用后人工执行）：
-- DELETE FROM hz_facility_template_item WHERE template_type = 'RENTAL';
-- DROP TABLE hz_facility_template_item;
