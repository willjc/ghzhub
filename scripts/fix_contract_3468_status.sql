-- 周宁远合同3468：已解约(5) → 已签署(2)，使其能在小程序重新发起退租，同时不影响郭志园合同4108占用房源800
UPDATE hz_contract SET contract_status = '2', update_time = NOW() WHERE contract_id = 3468 AND contract_status = '5';
-- 验证
SELECT contract_id, contract_status, tenant_id FROM hz_contract WHERE contract_id IN (3468, 4108);
