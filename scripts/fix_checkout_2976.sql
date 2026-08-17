-- 清除周宁远(申请2976)的退租数据；合同3468系残留数据，维持已解约(5)，不恢复履行中，避免与郭志园合同4108冲突
UPDATE hz_checkout_apply SET del_flag = '1', update_time = NOW() WHERE apply_id = 2976;
UPDATE hz_checkout_record SET del_flag = '1', update_time = NOW() WHERE record_id = 2941;
-- 验证
SELECT apply_id, contract_id, del_flag FROM hz_checkout_apply WHERE apply_id = 2976;
SELECT record_id, apply_id, del_flag FROM hz_checkout_record WHERE record_id = 2941;
SELECT contract_id, contract_status FROM hz_contract WHERE contract_id IN (3468, 4108);
SELECT house_id, house_status FROM hz_house WHERE house_id = 800;
