import { get, post } from '@/utils/request'

/**
 * 获取承诺书内容
 * @param {Number} projectId 项目ID
 */
export function getCommitmentContent(projectId) {
  return get('/h5/app/commitment/content', { projectId })
}

/**
 * 提交签署的承诺书
 * @param {Object} data 承诺书数据
 */
export function signCommitment(data) {
  return post('/h5/app/commitment/sign', data)
}

/**
 * 检查用户是否已签署承诺书
 * @param {Number} tenantId 租户ID
 * @param {String} commitmentType 承诺书类型
 */
export function checkSigned(tenantId, commitmentType) {
  return get('/h5/app/commitment/checkSigned', { tenantId, commitmentType })
}

/**
 * 获取小程序首页友情提醒公告（承诺书模板 template_code='gonggao'）
 * 免登录接口，首次打开小程序时调用
 */
export function getStartupNotice() {
  return get('/h5/app/commitment/notice')
}
