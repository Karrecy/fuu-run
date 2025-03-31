import App from './App'

// #ifndef VUE3
import Vue from 'vue'
import './uni.promisify.adaptor'

Vue.config.productionTip = false
import store from './store'   // 引入 vuex

// 挂载vuex
Vue.prototype.$store = store;

App.mpType = 'app'
const app = new Vue({
  ...App,
  store,  // 将 store 传入 Vue 实例

})
app.$mount()
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'
import store from './store'   // 引入 vuex

export function createApp() {
  const app = createSSRApp(App)
  app.use(store)  // 使用 Vuex store 
  return { app }
}
// #endif
