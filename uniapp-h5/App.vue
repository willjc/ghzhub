<script>
	import { getStartupNotice } from '@/api/commitment'

	export default {
		// 全局数据：保存启动时拉到的公告，供页面消费（保证 page.onLoad 晚于 App.onLaunch 时也能拿到）
		globalData: {
			pendingNotice: null
		},
		onLaunch: function() {
			console.log('App Launch')
			// 首次打开小程序即拉取友情提醒公告（template_code='gonggao'）
			this.fetchStartupNotice()
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		},
		methods: {
			fetchStartupNotice() {
				getStartupNotice().then(res => {
					const data = res && res.data ? res.data : null
					if (!data || data.empty || !data.templateId || !data.content) {
						return
					}
					// 本轮是否已看过（按 templateId 记忆；后台换一条新模板 → 用户重新弹一次）
					const shownKey = 'noticeShown_' + data.templateId
					if (uni.getStorageSync(shownKey)) {
						return
					}
					// 挂到全局 + 广播事件（双通道保证首页必然能拿到）
					const app = getApp({ allowDefault: true })
					if (app) {
						app.globalData = app.globalData || {}
						app.globalData.pendingNotice = data
					}
					uni.$emit('app:pendingNotice', data)
				}).catch(err => {
					console.warn('[notice] 拉取启动公告失败:', err)
				})
			}
		}
	}
</script>

<style>
	/*每个页面公共css */
	
	/* 苹方字体声明 */
	@font-face {
		font-family: "PingFang SC";
		src: url('/static/苹方字体/PingFang Regular_0.ttf') format('truetype');
		font-weight: normal;
		font-style: normal;
	}

	@font-face {
		font-family: "PingFang SC";
		src: url('/static/苹方字体/PingFang Light_0.ttf') format('truetype');
		font-weight: 300;
		font-style: normal;
	}

	@font-face {
		font-family: "PingFang SC";
		src: url('/static/苹方字体/PingFang ExtraLight_0.ttf') format('truetype');
		font-weight: 200;
		font-style: normal;
	}

	@font-face {
		font-family: "PingFang SC";
		src: url('/static/苹方字体/PingFang Medium_0.ttf') format('truetype');
		font-weight: 500;
		font-style: normal;
	}

	@font-face {
		font-family: "PingFang SC";
		src: url('/static/苹方字体/PingFang Bold_0.ttf') format('truetype');
		font-weight: 700;
		font-style: normal;
	}

	@font-face {
		font-family: "PingFang SC";
		src: url('/static/苹方字体/PingFang Heavy_0.ttf') format('truetype');
		font-weight: 900;
		font-style: normal;
	}

	/* 全局字体设置 */
	page {
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}
	
	* {
		font-family: "PingFang SC", "苹方-简", sans-serif;
	}
</style>
