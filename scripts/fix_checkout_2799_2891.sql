-- 清除孙原生(2799)、毛念伟(2891)的退租数据，恢复合同可重新发起退租
UPDATE hz_checkout_apply SET del_flag = '1', update_time = NOW() WHERE apply_id IN (2799, 2891);
UPDATE hz_checkout_record SET del_flag = '1', update_time = NOW() WHERE record_id IN (2787, 2857);
UPDATE hz_contract SET contract_status = '3', update_time = NOW() WHERE contract_id IN (3659, 3592);
UPDATE hz_house SET house_status = '2', update_time = NOW() WHERE house_id IN (1472, 780);
-- 验证
SELECT apply_id, del_flag FROM hz_checkout_apply WHERE apply_id IN (2799, 2891);
SELECT record_id, del_flag FROM hz_checkout_record WHERE record_id IN (2787, 2857);
SELECT contract_id, contract_status FROM hz_contract WHERE contract_id IN (3659, 3592);
SELECT house_id, house_status FROM hz_house WHERE house_id IN (1472, 780);
