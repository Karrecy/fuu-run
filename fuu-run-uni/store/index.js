// store/index.js
import { createStore } from 'vuex'; // 使用 vuex 4.x 支持的 createStore
import { createApp } from 'vue';    // Vue 3 使用 createApp

const store = createStore({
  state: {
	appLaunch:false, //appLaunch是否执行完毕
    userInfo: null, // 用户信息
	currSchool:null, //当前学校信息
	config:null, //系统配置
	weather:null //天气
  },
  mutations: {
	  setAppLaunch(state,bool) {
	  	state.appLaunch = bool;
	  	 console.log('设置appLaunch成功');
	  },
	setSchool(state,school) {
		state.currSchool = school;
		 console.log('设置学校成功');
	},
	setConfig(state,config) {
		state.config = config;
		 console.log('设置配置成功');
	},
	setWeather(state,weather) {
		state.weather = weather;
		 console.log('设置天气成功');
	},
    // 登录
    login(state, user) {
      // 登录状态为已登录
      state.userInfo = user;
      console.log('登陆成功');
    },

    // 退出登录
    logout(state) {
      // 登录状态为未登录
      state.userInfo = {};
      console.log('退出登录');
    },
  },
  actions: {
    // 你可以在这里定义异步操作
  },
});

export default store;
