/**
 * 账单管理相关API
 */
import { get, post } from '@/utils/request'

/**
 * 根据用户ID获取账单列表
 * @param {Number} userId 用户ID
 * @param {Object} params 查询参数（可选）
 * @param {String} params.billType 账单类型：1=押金, 2=租金, 3=水费, 4=电费, 5=燃气费, 6=物业费, 7=其他
 * @param {String} params.billStatus 账单状态：0=待支付, 1=已支付, 2=部分支付, 3=已逾期, 4=已关闭
 */
export function getBillListByUserId(userId, params) {
	return get(`/h5/app/bill/user/${userId}`, params)
}

/**
 * 根据合同ID获取账单列表
 * @param {Number} contractId 合同ID
 * @param {Object} params 查询参数（可选）
 */
export function getBillListByContractId(contractId, params) {
	return get(`/h5/app/bill/list/${contractId}`, params)
}

/**
 * 获取押金账单
 * @param {Number} contractId 合同ID
 */
export function getDepositBill(contractId) {
	return get(`/h5/app/bill/deposit/${contractId}`)
}

/**
 * 支付单个账单
 * @param {Object} data 支付数据
 * @param {Number} data.billId 账单ID
 * @param {Number} data.payAmount 支付金额
 */
export function payBill(data) {
	return post('/h5/app/bill/pay', data)
}

/**
 * 批量支付账单
 * @param {Object} data 支付数据
 * @param {Array<Number>} data.billIds 账单ID数组
 * @param {Number} data.payAmount 支付金额
 */
export function payBillBatch(data) {
	return post('/h5/app/bill/pay/batch', data)
}

export default {
	getBillListByUserId,
	getBillListByContractId,
	getDepositBill,
	payBill,
	payBillBatch
}
