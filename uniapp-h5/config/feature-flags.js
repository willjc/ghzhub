/**
 * 业务功能开关配置
 * 用于暂时隐藏尚未开放的业务模块（领导确认后开启即可）
 * 改动后各引用页面自动生效，无需改其他代码
 */
export default {
	// 保租房：false=隐藏入口（首页分类Tab/九宫格图标、办事页保租房区块），true=正常显示
	guaranteed: true,

	// 市场租赁：false=隐藏入口（首页分类Tab/九宫格图标、办事页市场租赁区块），true=正常显示
	market: true,

	// 开票：false=点击开票图标时提示联系客服电话（功能代码保留），true=正常进入开票模块
	invoice: false
}
