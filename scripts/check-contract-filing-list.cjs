// 静态回归检查，按仓库规范留给 CI 执行。
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const read = file => fs.readFileSync(path.join(__dirname, '..', file), 'utf8');
const api = read('uniapp-h5/api/contractFiling.js');
assert.ok(!api.includes('URLSearchParams'));
assert.ok(api.includes("get('/h5/app/contractFiling/myList', { tenantId, approveStatus })"));
const page = read('uniapp-h5/subpkg/purchase/contract-filing-list.vue');
assert.ok(page.includes('this.loadError = true'));
assert.ok(page.includes('加载失败，点击重试'));
assert.ok(page.includes('v-else-if="dataList.length === 0 && !loading"'));
console.log('合同备案列表静态检查通过');
