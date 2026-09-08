// 仅在获准的验证环境运行：node ruoyi-ui/tests/dashboard-check.cjs
const assert = require('node:assert/strict')
const fs = require('node:fs')
const path = require('node:path')
const vm = require('node:vm')
function component(name, globals = {}) {
  const source = fs.readFileSync(path.join(__dirname, '../src/components/Dashboard', name + '.vue'), 'utf8')
  const script = source.match(/<script>([\s\S]*?)<\/script>/)[1]
    .replace(/^import .*$/gm, '').replace('export default', 'module.exports =')
  const context = { module: { exports: {} }, ...globals }
  vm.runInNewContext(script, context)
  return context.module.exports
}
async function check() {
  const profile = component('TenantProfile')
  const state = { data: { household: {} } }
  for (const [name, getter] of Object.entries(profile.computed)) {
    Object.defineProperty(state, name, { get: () => getter.call(state) })
  }
  assert.ok(state.marriageData.every(item => item.value === 0 && item.percent === '0.0'))
  state.data = { marriageStatus: { married: 1, unmarried: 3 }, household: {} }
  assert.equal(state.marriageData[0].percent, '25.0')
  let query
  const ledger = component('ProjectLedger', { listBill: async value => { query = value; return { rows: [], total: 0 } } })
  const view = { ...ledger.data(), canListBills: true, currentProject: { projectId: 9, statisticsMonth: '2026-12' } }
  await ledger.methods.loadBills.call(view)
  assert.equal(query.params.ledgerProjectId, 9)
  assert.equal(query.params.ledgerStart, '2026-12-01')
  assert.equal(query.params.ledgerEnd, '2027-01-01')
  view.billBasis = 'year'
  await ledger.methods.loadBills.call(view)
  assert.equal(query.params.ledgerStart, '2026-01-01')
  view.billBasis = 'due'
  await ledger.methods.loadBills.call(view)
  assert.equal(query.params.ledgerBasis, 'due')
}
check().catch(error => { console.error(error); process.exitCode = 1 })
