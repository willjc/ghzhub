/**
 * 运营配置API
 */
import { get } from '@/utils/request'

/**
 * 获取轮播图列表
 * @returns {Promise}
 */
export function getBannerList() {
  return get('/h5/config/banners')
}

/**
 * 获取通知公告列表
 * @returns {Promise}
 */
export function getNoticeList() {
  return get('/h5/config/notices')
}

/**
 * 获取功能图标列表
 * @returns {Promise}
 */
export function getIconList() {
  return get('/h5/config/icons')
}
