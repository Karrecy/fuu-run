<template>
	 <nut-navbar  fixed="true" placeholder="true">
	    <template #left>
			<div @click="goToSelectSchool" style="display: flex; align-items: center; width: 120px; overflow: hidden;color: black;">
			  <span style="font-size: 16px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
				{{ currSchool.name }}
			  </span>
			  <nut-icon name="triangle-down" size="12"/>
			</div>
	    </template>
		<template #content>
		  <div class="h-full">
			  <image style="width: 18px;height: 18px;vertical-align: sub;" :src="decodeURIComponent('/static/weather/'+weather.weather+'.png')"></image>
			  <span>{{ ' ' + weather.temperature + '°'}}</span>
		  </div>
		</template>
	  </nut-navbar>
	  
	  <nut-swiper :init-page="page" :pagination-visible="true" pagination-color="#426543" auto-play="3000">
		   <nut-swiper-item v-for="item in list" :key="item">
			  <img :src="item" alt="" />
			</nut-swiper-item>
		</nut-swiper>
		
	<!-- 	 <nut-noticebar
		      text="NutUI 是京东风格的移动端组件库，使用 Vue 语言来编写可以在 H5，小程序平台上的应用，帮助研发人员提升开发效率，改善开发体验。"
		      :background="`rgba(251, 248, 220, 1)`"
		      :custom-color="`#D9500B`"
		    ></nut-noticebar> -->
	
	<nut-grid :gutter="10" :column-num="2" direction="horizontal">
	    <nut-grid-item @click="toOrderQusong" text="帮取"><nut-icon font-class-name="gridIcon" name="/static/index/qu.jpg" /></nut-grid-item>
	    <nut-grid-item @click="toOrderQusong"  text="帮送"><nut-icon font-class-name="gridIcon" name="/static/index/song.jpg" /></nut-grid-item>
	    <nut-grid-item @click="toOrderBangmai" text="帮买"><nut-icon font-class-name="gridIcon" name="/static/index/mai.jpg" /></nut-grid-item>
	    <nut-grid-item @click="toOrderWanneg" text="万能"><nut-icon font-class-name="gridIcon" name="/static/index/wan.jpg" /></nut-grid-item>
	  </nut-grid>
	  
	  <!-- <span @click="toLogin">test</span> -->
	  

  <nut-toast></nut-toast>
   <nut-notify></nut-notify>
  
</template>
<script>
	import { getWeatherByAdcode } from "@/request/apis/amap.js"
	import { getCarousel } from "@/request/apis/login.js"
	import { toRaw } from "vue";
	export default {
		data() {
			return {
				list:[
            'http://static.singoval.com/karrecy-fuu-run/2025/03/06/bab257d740f14abbbcd1e605a131cce1png',
            'http://static.singoval.com/karrecy-fuu-run/2025/03/06/aa76e46925d64c8480a354545148d153jpg',
          ],
				weather: {
					weather:'无数据',
					temperature:'~'
				},
				title: 'Hello',
				currSchool: {
					name:'选择校区'
				},
				orderServers:[]
			}
		},
		watch:{
			"$store.state.appLaunch": function(val, oldval) {
					console.log(val,oldval);
					if(val) {
						this.initSchool()
					}
			}
		},
		mounted() {
			console.log("mounted");
			this.initSchool()
			
		},
		onLoad() {
			console.log("index onLoad");
			getCarousel().then(res => {
				this.list = res.data
			})
			const checkOperationStatus = setInterval(() => {
			  if (this.$store.state.appLaunch) {
				  this.initSchool()
				  this.initWeather()
				clearInterval(checkOperationStatus);
				console.log('首页的js文件中的代码执行');
			  }
			}, 100); // 每100毫秒检查一次状态变化
			
		},
		onShow() {
			
		},
		methods: {
			toLogin() {
				uni.navigateTo({
					url:'/pages/API/login/login'
				})
			},
			toChat() {
				
				uni.navigateTo({
					url:"/pages/API/chat/chat"
				})
			},
			toDetail() {
				uni.navigateTo({
					url:"/pages/API/order/detail/detail"
				})
			},
			test() {
				const SUBSCRIBE_ID = 'nFzHoJjaKP8W6jdiFZkXsX6Z2A1u6O1F7wGrNAUpBlY' // 模板ID
				let that = this;
				    if (wx.requestSubscribeMessage) {
				      wx.requestSubscribeMessage({
				        tmplIds: [SUBSCRIBE_ID],
				        success(res) {
				          if (res[SUBSCRIBE_ID] === 'accept') {
				            // 用户主动点击同意...do something
				          } else if (res[SUBSCRIBE_ID] === 'reject') {
				            // 用户主动点击拒绝...do something
				          } else {
				            wx.showToast({
				              title: '授权订阅消息有误',
				              icon: 'none'
				            })
				          }
				        },
				        fail(res) {
				          // 20004:用户关闭了主开关，无法进行订阅,引导开启
				          if (res.errCode == 20004) {
				          	// 显示引导设置弹窗
				            that.setData({
				              isShowSetModel: true
				            })
				          }else{
				          	// 其他错误信息码，对应文档找出原因
				            wx.showModal({
				              title: '提示',
				              content: res.errMsg,
				              showCancel: false
				            })
				          }
				        }
				      });
				    } else {
				      wx.showModal({
				        title: '提示',
				        content: '请更新您微信版本，来获取订阅消息功能',
				        showCancel: false
				      })
				    }
			},
			toOrderQusong() {
				uni.navigateTo({
					url:"/pages/API/order/qusong/qusong"
				})
			},
			toOrderBangmai() {
				uni.navigateTo({
					url:"/pages/API/order/bangmai/bangmai"
				})
			},
			toOrderWanneg() {
				uni.navigateTo({
					url:"/pages/API/order/wanneng/wanneng"
				})
			},
			toOrderSubmit(){
				// uni.navigateTo({
				// 	url:"/pages/API/order/qusong/qusong"
				// })
				// uni.navigateTo({
				// 	url:"/pages/API/order/bangmai/bangmai"
				// })
				uni.navigateTo({
					url:"/pages/API/order/wanneng/wanneng"
				})
				// uni.navigateTo({
				// 	url:"/pages/API/order/test/test"
				// })
			},
			initSchool(){
				let currSchool = this.$store.state.currSchool;
				console.log(currSchool);
				if(currSchool == null) {
					
				}
				else {
					this.loadSchoolByStore();
				}
			},
			initWeather() {
				console.log('initWeather');
				let temp = this.$store.state.weather || null;
				if(temp !== null && temp != '') {
					this.weather = temp
				}else {
					let school = this.$store.state.currSchool
					console.log(school);
					if(school !== null && school != '') {
						console.log(222);
						getWeatherByAdcode(school.adcode).then(res => {
							console.log(res);
							this.$store.commit('setWeather',res.data)
							this.weather = res.data
						})
					}
				}
				console.log(this.weather);
			},
			
			loadSchoolByStore(){
				this.currSchool = toRaw(this.$store.state.currSchool);
				console.log(this.currSchool);
			},
	
			
			goToSelectSchool(){
				uni.navigateTo({
					url:"/pages/API/school/select/select"
				})
			}
		},
		
	}
</script>
<style>
.nut-icon {
  vertical-align: middle;
}

.ellipsisc {
  display: inline-block;
  width: 120px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.gridIcon {
  width: 40px;
  height: 40px;
}

/* 添加页面背景色 */
page {
  background-color: #f7f8fa;
  min-height: 100vh;
}

/* 导航栏样式优化 */
:deep(.nut-navbar) {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 1px 10px rgba(0, 0, 0, 0.05);
}

/* 天气信息样式 */
.h-full {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #666;
}

/* 轮播图容器样式 */
.nut-swiper {
  margin: 15px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}
</style>

<style lang="scss">
.nut-swiper-item {
  line-height: 200px;
  
  image {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

/* 网格菜单样式优化 */
.nut-grid {
  margin: 15px;
  background: #fff;
  border-radius: 12px;
  padding: 15px 0;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.nut-grid-item {
  position: relative;
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.95);
  }

  &__text {
    font-size: 15px !important;
    font-weight: 500 !important;
    color: #333;
    margin-top: 8px;
  }
}

.nut-icon__img {
  width: 55px !important;
  height: 55px !important;
  border-radius: 12px !important;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 12px rgba(0, 0, 0, 0.12);
  }
}

/* 选择校区按钮样式 */
:deep(.nut-navbar) {
  .left {
    div {
      background: rgba(0, 0, 0, 0.03);
      padding: 6px 12px;
      border-radius: 20px;
      transition: all 0.3s ease;
      
      &:active {
        background: rgba(0, 0, 0, 0.06);
      }
      
      span {
        color: #333;
        font-weight: 500;
      }
    }
  }
}
</style>
/* <style>
	.nut-icon{
		vertical-align: middle;
	}
	.ellipsisc {
		  display: inline-block;
		  width: 120px; /* 设置容器宽度 */
		  white-space: nowrap;
		  overflow: hidden;
		  text-overflow: ellipsis;
		}
	.gridIcon {
		width: 40px;
		height: 40px;
	}
</style> */
// <style lang="scss">
	
//   .nut-swiper-item {
//     line-height: 200px;
//     image {
//       width: 100%;
//       height: 100%;
//     }
//   }
//   .nut-grid-item__text {
// 	  font-size: medium !important;
// 	  font-weight: bold !important;
//   }
//   .nut-icon__img {
// 	  width: 50px !important;
// 	  height: 50px !important;
// 	  border-radius: 5px;
//   }
// </style>