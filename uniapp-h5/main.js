import App from './App'
import config from './config'

// #ifndef VUE3
import Vue from 'vue'
import './uni.promisify.adaptor'
Vue.config.productionTip = false

// 将配置挂载到Vue原型上，所有组件都可以通过 this.$config 访问
Vue.prototype.$config = config

App.mpType = 'app'
const app = new Vue({
  ...App
})
app.$mount()
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'
export function createApp() {
  const app = createSSRApp(App)
  // Vue3 方式挂载全局配置
  app.config.globalProperties.$config = config
  return {
    app
  }
}
// #endif
