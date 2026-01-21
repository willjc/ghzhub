<script>
	export default {
		onLaunch: function() {
			console.log('App Launch')
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
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
