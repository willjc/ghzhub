// 静态回归检查，按仓库规范留给 CI 执行。
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const read = file => fs.readFileSync(path.join(__dirname, '..', file), 'utf8');
assert.ok(read('uniapp-h5/subpkg/service/cleaning-submit.vue').includes('houseAddress: this.formData.serviceAddress'));
const page = read('uniapp-h5/subpkg/affairs/checkin.vue');
assert.ok(page.includes('checkinCheck(this.userId, item.contractId)'));
assert.ok(page.includes('item.canCheckin ? handleCheckin(index)'));
assert.ok(page.includes('contractId=${item.contractId}&billType='));
assert.ok(page.includes('bill?type=${this.housingType}'));
assert.ok(read('uniapp-h5/subpkg/affairs/bill.vue').includes('billRes.data.filter(b => Number(b.contractId) === this.contractId)'));
const service = read('ruoyi-system/src/main/java/com/ruoyi/system/service/impl/HzHouseOrderServiceImpl.java');
const check = service.split('checkinCheck(Long tenantId, Long contractId)')[1].split('@Override')[0];
assert.ok(check.includes('.eq(contractId != null, HzContract::getContractId, contractId)'));
assert.ok(check.includes('.eq(HzContract::getTenantId, tenantId)'));
assert.ok(check.includes('"2".equals(contract.getContractType())'));
console.log('保洁及入住缴费隔离静态检查通过');
