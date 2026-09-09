// 静态回归检查：node scripts/check-appointment-status.cjs（按仓库规范留给 CI 执行）
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const read = file => fs.readFileSync(path.join(__dirname, '..', file), 'utf8');
const service = read('ruoyi-system/src/main/java/com/ruoyi/system/service/impl/HzAppointmentServiceImpl.java');
const expiry = service.split('public int autoExpireAppointments()')[1].split('@Override')[0];
assert.match(expiry, /\.in\(HzAppointment::getAppointmentStatus, "0", "1"\)/);
assert.doesNotMatch(expiry, /"2"/);
for (const [method, allowed] of [['updateAppointmentStatus', '"0"'], ['cancelAppointment', '"0", "1"'], ['confirmViewing', '"1"'], ['completeViewing', '"2"']]) {
  const body = service.split(`public int ${method}(`)[1].split('@Override')[0];
  assert.ok(body.includes(`transitionAppointment(appointment, ${allowed})`), method);
}
assert.match(service, /\.in\(HzAppointment::getAppointmentStatus, \(Object\[\]\) allowedStatuses\)/);
assert.match(service, /appointment\.setAppointmentStatus\(null\)/);
const admin = read('ruoyi-ui/src/views/gangzhu/appointment/index.vue');
const miniapp = read('uniapp-h5/subpkg/affairs/appointment.vue');
for (const label of ['待确认预约', '待看房', '已看房待核实', '已完成', '已取消', '已过期']) {
  assert.ok(admin.includes(label) && miniapp.includes(label), label);
}
assert.ok(admin.includes('statusDescriptions[detailData.appointmentStatus]'));
assert.ok(miniapp.includes('我已完成看房'));
assert.ok(!admin.includes('detailData.isVisited'));
console.log('预约状态静态回归检查通过');
