// 仅在获准环境运行：node scripts/check-affairs-category.cjs
// 检查 API 参数兼容性与分类链路结构，不替代真实账号业务验证。
const assert = require('node:assert/strict')
const fs = require('node:fs')
const path = require('node:path')
const vm = require('node:vm')
const root = path.resolve(__dirname, '..')
const read = file => fs.readFileSync(path.join(root, file), 'utf8')
const cases = [
  ['appointment', 'getMyAppointments', 'appointment', '/h5/appointment/user/7'],
  ['cohabitant', 'getCohabitantList', 'cohabitant-list', '/h5/app/cohabitant/list/7'],
  ['cohabitant', 'getConfirmedContractList', 'cohabitant', '/h5/app/cohabitant/confirmed/7'],
  ['exchange', 'getExchangeList', 'exchange', '/h5/app/exchange/list/7'],
  ['exchange', 'getConfirmedContractList', 'exchange-apply', '/h5/app/exchange/confirmed/7'],
  ['checkout', 'getCheckoutList', 'checkout', '/h5/app/checkout/list/7'],
  ['checkin', 'getConfirmedCheckInList', 'renew', '/h5/app/checkin/confirmed/7?type=renew'],
  ['checkin', 'getConfirmedCheckInList', 'checkout', '/h5/app/checkin/confirmed/7?type=checkout']
]
for (const [api, name, page, url] of cases) {
  const source = read(`uniapp-h5/api/${api}.js`).replace(/^import .*$/gm, '')
    .replace(/export function /g, 'function ').replace(/export default[\s\S]*$/, '')
  let request
  const context = { get: (address, params) => { request = { address, params } } }
  vm.runInNewContext(source, context)
  for (const projectType of [undefined, '1', '2', '3']) {
    if (api === 'checkin') context[name](7, page, projectType)
    else context[name](7, projectType)
    assert.equal(request.address, url)
    assert.equal(JSON.stringify(request.params), JSON.stringify(projectType ? { projectType } : {}))
  }
  const component = read(`uniapp-h5/subpkg/affairs/${page}.vue`)
  assert.match(component, /talent: '1', guaranteed: '2', market: '3'/)
  assert.match(component, new RegExp(name + '\\([^\\n]*projectType\\)'))
}
const menu = read('uniapp-h5/pages/affairs/index.vue')
assert.equal((menu.match(/in rentalFunctionList/g) || []).length, 2)
assert.match(menu, /filter\(item => item.key !== 'appeal'\)/)
const controllerRoot = 'ruoyi-admin/src/main/java/com/ruoyi/web/controller/h5/'
for (const name of ['HzCheckInAppController', 'HzExchangeAppController']) {
  const source = read(controllerRoot + name + '.java')
  assert.match(source, /checkInList = checkInList.stream\(\).filter|list = list.stream\(\).filter/)
  assert.match(source, /allContracts.stream\(\)\s*\.filter\(c => contractIds == null \|\| contractIds.contains\(c.getContractId\(\)\)\)/)
}
const mapper = read('ruoyi-system/src/main/java/com/ruoyi/system/mapper/HzContractMapper.java')
assert.match(mapper, /c.tenant_id = #\{tenantId\} AND p.project_type = #\{projectType\}/)
console.log('Affairs category checks passed')
