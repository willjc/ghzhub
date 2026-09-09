// 静态回归检查，按仓库规范留给 CI 执行。
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const read = file => fs.readFileSync(path.join(__dirname, '..', file), 'utf8');
const source = read('ruoyi-admin/src/main/java/com/ruoyi/web/controller/h5/HzCheckInAppController.java');
const submit = source.split('public AjaxResult submitCheckIn(')[1].split('@GetMapping')[0];
assert.ok(submit.includes('Arrays.asList("0", "1", "3").contains(originalStatus)'));
assert.ok(submit.includes('validateCheckinSubmit(checkIn,'));
assert.ok(submit.includes('.eq(HzCheckIn::getStatus, originalStatus)'));
assert.ok(submit.includes('.eq(HzCheckIn::getTenantId, SecurityUtils.getHzUserId())'));
assert.ok(submit.includes('.set(HzCheckIn::getAuditRemark, null)'));
assert.ok(submit.includes('if (alreadyExists) continue;'));
const page = read('uniapp-h5/subpkg/affairs/checkin.vue');
assert.ok(page.includes('修改并重新提交'));
assert.ok(page.includes('auditRemark: item.auditRemark'));
console.log('入住重提静态检查通过');
