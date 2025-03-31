<template>
	 <template>
	  <nut-notify></nut-notify>
	</template>
	<template>
	  <nut-cell-group title="展示信息">
		  <nut-uploader @success="uploadSuccess"
		  	@oversize="oversize" :headers="headers" :data="uploaderData" :maximize="5242880" name="file" accept="image/*"
		  	:url="uploadUrl">
			<nut-cell size="large" desc="更换头像">
			  <template #icon>
				<image style="width: 30px;height: 30px;" class="nut-icon" :src="updateForm.avatar"></image>
			  </template>
			  <template  #title>
			  		<span> </span>
			  </template>
			</nut-cell>
			</nut-uploader>
			
		  
		   
		 <template>
			 <nut-cell size="large" desc="昵称">
			   <template #icon>
				   <nut-icon name="/static/icons/姓名.png"></nut-icon>
			   </template>
			   <template #title>
			   		<nut-input
					max-length="8"
					  type="nickname"
					  v-model="updateForm.nickname"
						 placeholder="请输入昵称"
						 >	 	
					</nut-input>
			   </template>
			 </nut-cell>
		 </template>
		
	  </nut-cell-group>
	</template>
	<template>
	  <nut-button :loading="btnSubmitLoading" @click="updateProfile" block type="primary">提交保存</nut-button>
	</template>
	<template>
		<div v-show="showPhoneButton">
			<nut-cell-group  title="手机号">
					 <template>
					   <nut-input 
								  v-model="userInfo.userWx.phone"
					              placeholder="请绑定手机号"
								  disabled
					              clearable>
					     <template #left>
					       <nut-icon name="/static/icons/手机号.png"></nut-icon>
					     </template>
					 
					    <template #right>
					       <nut-button :loading="btnPhoneLoading" @getphonenumber="getPhoneNumber"  open-type="getPhoneNumber" type="primary" size="small">绑定手机号</nut-button>
					     </template>
					   </nut-input>
					 </template>
			</nut-cell-group>
		</div>
		<div  v-show="!showPhoneButton" >
			<nut-cell-group title="手机号">
					 <template>
					   <nut-input 
								  v-model="userInfo.userWx.phone"
					              placeholder="请绑定手机号"
								  disabled
					              clearable>
					     <template #left>
					       <nut-icon name="/static/icons/手机号.png"></nut-icon>
					     </template>
						 <template  #right>
						    <nut-button @click="getPhoneNumber('哇哈哈')"   type="primary" size="small">绑定手机号</nut-button>
						  </template>
					   </nut-input>
					 </template>
			</nut-cell-group>
		</div>
	  
	</template>
	<template>
	  <nut-cell-group title="其它"> 
		 <nut-cell size="large" title="uuid" :desc="userInfo.uid">
			<template #icon>
			   <nut-icon name="/static/profile/business-icon-buyers-club.png"></nut-icon>
			</template>
		 </nut-cell>
		 <nut-cell size="large" title="身份" :desc="userInfo.userWx.isRunner == 1 ? '跑腿员':'普通用户'">
			 <template #icon>
			    <nut-icon name="/static/profile/customer-official.png"></nut-icon>
			 </template>
		 </nut-cell>
		 <nut-cell v-if="userInfo.userWx.isRunner == 1" size="large" title="跑腿学校" :desc="userInfo.userWx.schoolName">
			 <template #icon>
			    <nut-icon name="/static/profile/business-icon-buyers-club.png"></nut-icon>
			 </template>
		 </nut-cell>
		 <nut-cell v-if="userInfo.userWx.isRunner == 1"  size="large" title="接单状态" :desc="userInfo.userWx.canTake == 1 ? '可接单':'不可接单'">
			 <template #icon>
			    <nut-icon name="/static/profile/order-success.png"></nut-icon>
			 </template>
		 </nut-cell>
		 <nut-cell v-if="userInfo.userWx.isRunner == 1" size="large" title="真实姓名" :desc="userInfo.userWx.realname">
			 <template #icon>
			    <nut-icon name="/static/profile/name-card.png"></nut-icon>
			 </template>
		 </nut-cell>
		 <nut-cell v-if="userInfo.userWx.isRunner == 1" size="large" title="性别" :desc="userInfo.userWx.gender == 1 ? '男':'女'">
			 <template #icon>
			    <nut-icon name="/static/profile/genderless.png"></nut-icon>
			 </template>
		 </nut-cell>
		 <nut-cell size="large" title="下单状态" :desc="userInfo.userWx.canOrder == 1 ? '可下单':'不可下单'">
			 <template #icon>
			    <nut-icon name="/static/profile/order.png"></nut-icon>
			 </template>
		 </nut-cell>
		 <nut-cell size="large" title="注册时间" :desc="userInfo.createTime">
			 <template #icon>
			    <nut-icon name="/static/profile/time.png"></nut-icon>
			 </template>
		 </nut-cell>
	  </nut-cell-group>
	</template>

</template>
<script setup>

</script>
<script>
	import {upload_url} from '@/request/request.js'
	import { toRaw } from "vue";
import {listSchool} from "@/request/apis/school.js"
import {putUpdateProfile,getBindPhone,getCanReqPhone} from "@/request/apis/user.js"
import {getInfo} from "@/request/apis/login.js"
	import { useNotify } from 'nutui-uniapp/composables';
	export default {
		setup() {
		   const notify = useNotify();
		   const showPrimary = (message) => {notify.primary(message);};
		   const showSuccess = (message) => {notify.success(message);};
		   const showDanger = (message) => {notify.danger(message);};
		   const showWarning = (message) => {notify.warning(message);};
		   const hideNotify = () => {notify.hide();};
		   return {showPrimary,showSuccess,showDanger,showWarning,hideNotify};
		 },
		data() {
			return {
				btnPhoneLoading:false,
				btnSubmitLoading:false,
				showPhoneButton:true,
				title: 'Hello',
				userInfo:{
					userWx:{
						nickname:''
					}
				},
				uploadUrl:upload_url,
				updateForm:{
					avatar:'',
					nickname:''
				},
				headers: {
					Authorization: 'Bearer ' + uni.getStorageSync("token"),
					'Content-Type': 'multipart/form-data',
				},
				uploaderData:{
					type:4,
					name:''
				},
			}
		},
		onLoad() {
			this.initData()
		},
		onReachBottom() {
			
		},
		methods: {
			getPhoneNumber(e) {
				console.log(e);
				let code = null
				if(e.detail == undefined) code = e
				else code = e.detail.code
				this.btnPhoneLoading = true
				getBindPhone({phoneCode:code}).then(res => {
					console.log(res);
					this.getInfo()
					this.getCanReqPhone()
				}).catch(err => {
					this.showDanger(err)
				}).finally(res => {
					this.btnPhoneLoading = false
				})
			},
			getInfo() {
				let that = this
				getInfo().then(res => {
					let info = res
					this.$store.commit('login', info.data.user);
					this.$store.commit('setConfig', info.data.config);
					this.freshUserInfo()
				}).catch(err => {
					this.showDanger(err)
				})
			},
			updateProfile(){
				this.btnSubmitLoading = true
				putUpdateProfile(this.updateForm).then(res => {
					console.log(res);
					this.getInfo()
					this.showSuccess("设置成功")
				}).catch(err => {
					this.showDanger(err)
				}).finally(res => {
					this.btnSubmitLoading = false
				})
			},
			oversize(files) {
				console.log(files);
				uni.showToast({
					title: '文件大小超出5MB',
					icon: 'none'
				})
			},
			uploadSuccess(e) {
				console.log(e);
				let data = e.data.data
				data = JSON.parse(data)
				let url = data.data.url
				this.updateForm.avatar = url
				this.userInfo.userWx.avatar = url
			},
			initData(){
				this.freshUserInfo()
				this.getCanReqPhone()
			},
			getCanReqPhone() {
				getCanReqPhone().then(res => {
					console.log(res);
					this.showPhoneButton = res.data
				})
			},
			freshUserInfo() {
				this.userInfo = this.$store.state.userInfo;
				console.log(this.userInfo);
				this.updateForm.avatar = this.userInfo.userWx.avatar
				this.updateForm.nickname = this.userInfo.userWx.nickname
			}
		},
		
	}
</script>
<style>
@import url("/common/common.css");

/* 页面基础样式 */
page {
  background: #f8f9fc;
  min-height: 100vh;
  padding: 12px;
  box-sizing: border-box;
}

/* 卡片组样式 */
.nut-cell-group {
  background: #fff;
  border-radius: 16px !important;
  margin: 8px !important;
  margin-bottom: 16px !important;
  box-shadow: 0 4px 16px rgba(149, 157, 165, 0.08);
  overflow: hidden;
  border: none !important;
}

.nut-cell-group__title {
  font-size: 15px !important;
  font-weight: 600 !important;
  color: #2c3e50 !important;
  padding: 16px 16px 8px !important;
  margin-top: 0 !important;
  opacity: 0.9;
}

/* 单元格样式 */
.nut-cell {
  padding: 14px 16px !important;
  background: #fff !important;
  transition: all 0.3s ease;
}

.nut-cell:active {
  background: #f8f9fa !important;
}

/* 头像样式 */
.nut-icon {
  width: 32px !important;
  height: 32px !important;
  border-radius: 50% !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  object-fit: cover;
}

/* 功能图标样式 */
.nut-icon[name*="/static"] {
  width: 20px !important;
  height: 20px !important;
  border-radius: 6px !important;
  padding: 4px;
  background: rgba(161, 140, 209, 0.08);
}

/* 输入框样式 */
.nut-input {
  font-size: 14px !important;
}

.nut-input__value {
  color: #333 !important;
}

/* 按钮样式 */
.nut-button--block {
  width: 94% !important;
  height: 44px !important;
  margin: 20px auto !important;
  border-radius: 22px !important;
  font-size: 15px !important;
  font-weight: 500 !important;
  background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%) !important;
  border: none !important;
  box-shadow: 0 4px 12px rgba(161, 140, 209, 0.2) !important;
  transition: all 0.3s ease;
}

.nut-button--block:active {
  transform: scale(0.98);
}

/* 小按钮样式 */
.nut-button--small {
  padding: 6px 14px !important;
  border-radius: 14px !important;
  font-size: 13px !important;
  background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%) !important;
  border: none !important;
}

/* 信息展示样式 */
.nut-cell__value {
  color: #666 !important;
  font-size: 13px !important;
  flex: none !important;
}

.nut-cell__desc {
  font-size: 13px !important;
  color: #666 !important;
}

/* 上传组件样式 */
.nut-uploader__slot {
  width: 100% !important;
}

.nut-uploader__preview {
  margin: 0 !important;
}

/* 手机号输入区域 */
.nut-input-wrapper {
  background: transparent !important;
}

/* 清除按钮样式 */
.clearBtn {
  background: transparent !important;
  font-size: 14px !important;
}

/* 禁用状态样式 */
.nut-input[disabled] {
  opacity: 0.7;
  background: #f8f9fa !important;
}

/* 分割线样式 */
.nut-cell::after {
  left: 16px !important;
  right: 16px !important;
  background-color: rgba(0, 0, 0, 0.03) !important;
}
</style>