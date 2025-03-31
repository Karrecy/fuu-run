<template>
  <view @click="toProfile" class="container">
    <view class="left">
      <view class="nickname">
        {{userInfo.userWx.nickname}}
        <nut-icon size="10px" name="rect-right"></nut-icon>
      </view>
      <view class="desc">{{userInfo.comesTime}}</view>
    </view>
    <image class="avatar" :src="userInfo.userWx.avatar" />
  </view>

<view @click="toCenter" v-if="userInfo?.userWx?.isRunner === 1" class="cellBox">
    <view class="cell-left">
      <image class="cellImg" src="/static/profile/跑腿中心.png" mode=""></image>
      <span class="cellTitle">跑腿中心</span>
    </view>
    <view class="cell-right">
      <span class="cellDesc"></span>
      <image class="cellRight" src="/static/icons/right_grey.png" mode=""></image>
    </view>
  </view>

  <view @click="checkSchool" class="cellBox">
    <view class="cell-left">
      <image class="cellImg" src="/static/profile/注册.png" mode=""></image>
      <span class="cellTitle">申请跑腿</span>
    </view>
    <view class="cell-right">
      <span class="cellDesc"></span>
      <image class="cellRight" src="/static/icons/right_grey.png" mode=""></image>
    </view>
  </view>

  <view @click="toOrderList" class="cellBox">
    <view class="cell-left">
      <image class="cellImg" src="/static/profile/订单.png" mode=""></image>
      <span class="cellTitle">我的订单</span>
    </view>
    <view class="cell-right">
      <span class="cellDesc"></span>
      <image class="cellRight" src="/static/icons/right_grey.png" mode=""></image>
    </view>
  </view>

  <view @click="toAddressList" class="cellBox">
    <view class="cell-left">
      <image class="cellImg" src="/static/profile/地址.png" mode=""></image>
      <span class="cellTitle">我的地址</span>
    </view>
    <view class="cell-right">
      <span class="cellDesc"></span>
      <image class="cellRight" src="/static/icons/right_grey.png" mode=""></image>
    </view>
  </view>
	<button open-type="contact">
		<view class="cellBox">
		  <view class="cell-left">
		    <image class="cellImg" src="/static/profile/客服.png" mode=""></image>
		    <span class="cellTitle">咨询客服</span>
		  </view>
		  <view class="cell-right">
		    <span class="cellDesc"></span>
		    <image class="cellRight" src="/static/icons/right_grey.png" mode=""></image>
		  </view>
		</view>
	</button>
	<view @click="toSetting" class="cellBox">
	  <view class="cell-left">
	    <image class="cellImg" src="/static/profile/设置.png" mode=""></image>
	    <span class="cellTitle">设置</span>
	  </view>
	  <view class="cell-right">
	    <span class="cellDesc"></span>
	    <image class="cellRight" src="/static/icons/right_grey.png" mode=""></image>
	  </view>
	</view>
	<view @click="showAgentPop = true" class="cellBox">
	  <view class="cell-left">
	    <image class="cellImg" src="/static/profile/合作.png" mode=""></image>
	    <span class="cellTitle">校区入驻</span>
	  </view>
	  <view class="cell-right">
	    <span class="cellDesc"></span>
	    <image class="cellRight" src="/static/icons/right_grey.png" mode=""></image>
	  </view>
	</view>
  <!-- 注释掉的菜单项保持注释状态 -->
  <!-- <view class="cellBox">
    <view class="cell-left">
      <image class="cellImg" src="/static/profile/反馈.png" mode=""></image>
      <span class="cellTitle">反馈问题</span>
    </view>
    <view class="cell-right">
      <span class="cellDesc"></span>
      <image class="cellRight" src="/static/icons/right_grey.png" mode=""></image>
    </view>
  </view> -->

  <!-- <view class="cellBox">
    <view class="cell-left">
      <image class="cellImg" src="/static/profile/设置.png" mode=""></image>
      <span class="cellTitle">设置</span>
    </view>
    <view class="cell-right">
      <span class="cellDesc"></span>
      <image class="cellRight" src="/static/icons/right_grey.png" mode=""></image>
    </view>
  </view> -->
  <nut-dialog 
	  title="温馨提示" 
	  content="请咨询客服申请校区代理" 
	  v-model:visible="showAgentPop" 
      no-cancel-btn
	  @ok="onOk" />

  <nut-dialog 
    title="未选择校区" 
    content="是否前往选择校区？" 
    v-model:visible="visible1" 
    @ok="toSelectSchool" 
  />
</template>

<script>
	import dayjs from 'dayjs';
	import relativeTime from 'dayjs/plugin/relativeTime'; // 引入 relativeTime 插件
	import 'dayjs/locale/zh-cn';  // 导入中文语言包
	// 扩展 dayjs 插件
	dayjs.extend(relativeTime);
	// 设置为中文
	dayjs.locale('zh-cn');
	export default {
		data() {
			return {
				showAgentPop:false,
				title: 'Hello',
				userInfo:{
					userWx:{}
				},
				visible1:false, //检查有没有选择校区dialog
			}
		},
		onLoad() {

		},
		onShow() {
			this.initData()
		},	
		methods: {
			toSetting(){
				uni.navigateTo({
					url:"/pages/API/user/setting/setting"
				})
			},
			toCenter() {
				uni.navigateTo({
					url:"/pages/API/runner/center/center"
				})
			},
			toOrderList() {
				uni.navigateTo({
					url:"/pages/API/order/list/list"
				})
			},
			toAddressList() {
				uni.navigateTo({
					url:"/pages/API/address/list/list"
				})
			},
			toSelectSchool() {
				uni.navigateTo({
					url:"/pages/API/school/select/select"
				})
			},
			toRunnerIntro() {
				uni.navigateTo({
					url:"/pages/API/runner/introduce/introduce"
				})
			},
			checkSchool() {
				let school = this.$store.state.currSchool;
				if(school == null || school == undefined || school == '') {
					this.visible1 = true
				}
				else {
					this.toRunnerIntro()
				}
			},
			toProfile(){	
				uni.navigateTo({
					url:"/pages/API/user/profile/profile"
				})
			},
			initData(){
				this.userInfo = this.$store.state.userInfo;
				this.userInfo.comesTime = this.getRegisterTimeMessage(this.userInfo.createTime)
				console.log(this.userInfo);
				console.log(this.userInfo);
			},
			getRegisterTimeMessage(createTime) {
			  return dayjs().diff(dayjs(createTime), 'day') < 1 
			    ? '欢迎使用' 
			    : `您已经使用 ${dayjs(createTime).toNow(true)}啦！`;
			}
		}
	}
</script>
// ... existing code ...

<style>
button{padding: 0;margin: 0;line-height: inherit;}
button::after{ border: none; }

/* 重置基础样式 */
page {
  background-color: #f7f8fa;
  width: 100%;
  overflow-x: hidden;
}

/* 移除浮动布局,改用flex */
.cellBox {
  width: 92%;
  margin: 10px auto;
  padding: 16px 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(100, 100, 100, 0.1);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  box-sizing: border-box;
}

.cellBox:active {
  transform: scale(0.98);
  background: #f8f8f8;
}

/* 左侧图标和标题容器 */
.cell-left {
  display: flex;
  align-items: center;
  flex: 1;
}

.cellImg { 
  width: 24px;
  height: 24px;
  flex-shrink: 0;
}

.cellTitle {
  margin-left: 12px;
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

/* 右侧箭头和描述 */
.cell-right {
  display: flex;
  align-items: center;
}

.cellDesc {
  font-size: 14px;
  color: #999;
  margin-right: 8px;
}

.cellRight {
  width: 14px;
  height: 14px;
  opacity: 0.6;
}

/* 顶部用户信息容器 */
.container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  margin: 15px auto 25px;
  background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%);
  border-radius: 16px;
  width: 92%;
  box-sizing: border-box;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.left {
  display: flex;
  flex-direction: column;
  flex: 1;
  margin-right: 20px;
}

.nickname {
  font-weight: 600;
  font-size: 20px;
  color: #fff;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.desc {
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  margin-top: 4px;
}

.avatar {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  border: 3px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
  flex-shrink: 0;
}

/* 移除不需要的浮动类 */
.fl, .fr {
  display: none;
}
</style>
<!-- <style>
.fl {
	float: left;
}
.fr {
	float: right;
}
.cellBox {
	width: 90%;
	padding: 16px 10px;
	position: relative;   /* 防止浮动影响父容器 */
	left: 5%;
	box-sizing: border-box;
}
.cellTitle {
	height: 30px;
	line-height: 30px;
	margin-left: 10px;
	font-size: large;
	vertical-align: middle;
}
.cellDesc {
	height: 30px;
	line-height: 30px;
	vertical-align: middle;
	font-size: medium;
	color: gray;
}
.cellImg { 
	width: 30px;
	height: 30px;
	vertical-align: middle;
}

.cellRight {
	margin-left: 10px;
	width: 16px;
	margin-top: 7px;
	height: 16px;
	vertical-align: middle;
}
.nut-icon{
	vertical-align: middle;
}
.nickname{
	display: inline-block;
	width: 100%;
	height: 45px;
	line-height: 45px;
	vertical-align: middle;
	font-weight: bolder;
	font-size: x-large;
	white-space: nowrap; /* 禁止换行 */
	overflow: hidden; /* 超出部分隐藏 */
	text-overflow: ellipsis; /* 超出部分显示省略号 */
}
.desc {
	width: 100%;
	height: 25px;
	line-height: 25px;
}
.container {
  display: flex; /* 使用 flex 布局 */
  align-items: center; /* 垂直居中对齐 */
  justify-content: space-between; /* 左右两边水平对齐 */
  padding: 10px 30px;
}

.left {
  display: flex;
  flex-direction: column; /* 垂直排列 */
  max-width: calc(100% - 100px);
}

.avatar {
  width: 70px; /* 头像宽度 */
  height: 70px; /* 头像高度 */
  border-radius: 50%; /* 圆形头像 */
}
</style -->>
