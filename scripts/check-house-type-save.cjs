// 静态回归检查，按仓库规范留给 CI 执行。
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const read = file => fs.readFileSync(path.join(__dirname, '..', file), 'utf8');
const controller = read('ruoyi-system/src/main/java/com/ruoyi/system/controller/HzHouseTypeController.java');
const method = controller.split('public AjaxResult saveImages(')[1].split('/**')[0];
assert.ok(method.includes('batchSaveImages(houseTypeId, imageList);'));
assert.ok(method.includes('return success();'));
assert.ok(!method.includes('toAjax('));
const page = read('ruoyi-ui/src/views/gangzhu/houseType/index.vue');
const submit = page.split('submitForm() {')[1].split('saveImages(houseTypeId) {')[0];
assert.ok(submit.includes('const successMsg = "房型信息已保存"'));
assert.ok(submit.includes('this.form.houseTypeId = houseTypeId'));
assert.ok(!/catch\([^]*?msgSuccess/.test(submit.split('} else {')[0]));
console.log('房型保存静态检查通过');
