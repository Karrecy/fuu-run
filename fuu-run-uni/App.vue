<!-- 注意这里的 lang="scss"，并且没有 scoped -->
<style lang="scss">
	@import "nutui-uniapp/styles/index.scss"; 
	
</style>
<script>
	import {checkLogin,xcxLogin,getInfo,login} from "@/request/apis/login.js";
	import {getSchool} from "@/request/apis/school.js";
	
	export default {
	
		onLaunch: function() {
			console.log('App Launch')
			login.call(this)
			
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		},
		methods: {
			checkSchool() {
				let school = uni.getStorageSync("currentSchool")
				if(school == null || school == undefined || school == '') {
					uni.navigateTo({
						url:"/pages/API/school/select/select"
					})
				}
				else {
					getSchool(school.id).then(res => {
						console.log(res);
						this.$store.commit('setSchool', res.data);
						
						uni.setStorageSync("currentSchool",res.data)
					})
				}
				this.$store.commit('setAppLaunch', true);
				
			},
			async login(){
				let isLogined = await checkLogin();
				console.log(isLogined);
				if(!isLogined.data) {
					uni.showLoading({
						title:"登陆中...",
						duration:2000
					});
					let code = await this.getCode();
					let loginRes = await xcxLogin({"xcxCode":code});
					let token = loginRes.data.token
					uni.setStorageSync("token",token)
				}
				let info = await getInfo();
				console.log(info);
				this.$store.commit('login', info.data.user);
				this.$store.commit('setConfig', info.data.config);
				this.checkSchool()
			},
			async getCode() {
				const code = await getLoginCode();
				return code;
			}
		
			
		},
		
	}
	const getLoginCode = () => {
	  return new Promise((resolve, reject) => {
	    uni.login({
	      success: (res) => {
	        if (res.code) {
	          resolve(res.code);  // 返回 code
	        } else {
	          reject('获取 code 失败');
	        }
	      },
	      fail: (err) => {
	        reject('登录失败: ' + err.errMsg);  // 错误处理
	      },
	    });
	  });
	};
</script>


