// 静态回归检查，按仓库规范留给 CI 执行。
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const read = f => fs.readFileSync(path.join(__dirname, '..', f), 'utf8');
const draft = read('ruoyi-admin/src/main/java/com/ruoyi/web/controller/h5/HzContractAppController.java').split('public AjaxResult renewContract(')[1].split('private ')[0];
assert.ok(!draft.includes('setIsRenewed("1")'));
assert.ok(draft.includes('setRenewedContractId(contract.getContractId())'));
const sign = read('ruoyi-system/src/main/java/com/ruoyi/system/service/impl/EsignServiceImpl.java');
assert.equal(sign.split('markOriginalContractRenewed(contract);').length - 1, 2);
assert.ok(sign.includes('.eq(HzContract::getRenewedContractId, renewal.getContractId())'));
const api = read('uniapp-h5/api/subsidyApply.js');
assert.ok(!api.includes('URLSearchParams'));
assert.ok(api.includes("get('/h5/app/subsidyApply/myList', { tenantId, approveStatus })"));
console.log('续租标记及补贴列表静态检查通过');
