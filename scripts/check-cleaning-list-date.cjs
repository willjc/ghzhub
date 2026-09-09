// 回归检查，按仓库规范留给 CI 执行。
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const vm = require('node:vm');
const read = file => fs.readFileSync(path.join(__dirname, '..', file), 'utf8');
const api = read('uniapp-h5/api/serviceOrder.js');
assert.ok(!api.includes('URLSearchParams'));
assert.ok(api.includes("get('/h5/app/serviceOrder/myOrders', params)"));
const source = read('uniapp-h5/subpkg/service/cleaning-submit.vue');
const script = source.match(/<script>([\s\S]*?)<\/script>/)[1]
  .replace(/^import .*$/gm, '').replace('export default', 'module.exports =');
const sandbox = { module: { exports: {} }, Date };
vm.runInNewContext(script, sandbox);
const component = sandbox.module.exports;
const page = { ...component.data(), ...component.methods };
page.initDatePicker();
const today = new Date();
const tomorrow = new Date(today.getFullYear(), today.getMonth(), today.getDate() + 1);
const format = date => `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()} 00:00:00`;
assert.equal(page.isFutureServiceTime(format(today)), false);
assert.equal(page.isFutureServiceTime(format(tomorrow)), true);
assert.equal(page.months[0], tomorrow.getMonth() + 1);
assert.equal(page.days[0], tomorrow.getDate());
const backend = read('ruoyi-system/src/main/java/com/ruoyi/system/service/impl/HzServiceOrderServiceImpl.java');
assert.ok(backend.includes('serviceDate.isAfter(java.time.LocalDate.now(zone))'));
console.log('保洁列表与日期检查通过');
