<template>
	<template>
	  <nut-notify></nut-notify>
	</template>
	<div class="box">
		<nut-cell  title="姓名">
			<template #icon>
				<image class="nut-icon" src="/static/icons/姓名.png"></image>
			</template>
			<template #link>
			  <nut-input   v-model="submitForm.name"
						   placeholder="输入框无边框"
						   :border="false"
						   style="text-align: right;"></nut-input>
			</template>
		</nut-cell>
		<nut-cell  title="手机号"> 
		<template #icon>
			<image class="nut-icon" src="/static/icons/手机号.png"></image>
		</template>
			<template #link>
			  <nut-input v-model="submitForm.phone"
						   placeholder="输入框无边框"
						   :border="false"
						   style="text-align: right;"></nut-input>
			</template>
		</nut-cell>
	</div>
	<div class="box">
		<nut-tabs background="linear-gradient(180deg, rgb(220 240 255) 0%, rgba(255,255,255,1) 100%);" v-model="tabValue">
		  <nut-tab-pane title="快捷地址">
		    <nut-cell @click="showRegion"  is-link title="选择地点" :desc="submitForm.title"></nut-cell>
		    <nut-cell title="详细地址">
				<template #link>
				  <nut-input v-model="submitForm.detail"
				               placeholder="输入框无边框"
				               :border="false"
							   style="text-align: right;"></nut-input>
				</template>
			</nut-cell>
		  </nut-tab-pane>
		
		  <nut-tab-pane title="自选地址">
		    <nut-cell @click="chooseLocation" is-link title="选择地点" :desc="submitForm.title"></nut-cell>
		    <nut-cell title="详细地址">
		    	<template #link>
		    	  <nut-input v-model="submitForm.detail"
		    	               placeholder="输入框无边框"
		    	               :border="false"
		    				   style="text-align: right;"></nut-input>
		    	</template>
		    </nut-cell>
		  </nut-tab-pane>
		</nut-tabs>
	</div>
	<div v-show="!formOrder" class="box" style="padding: 16px;">
		<nut-checkbox v-model="submitForm.isDefault">是否设为默认地址</nut-checkbox>
	</div>
	<div v-show="formOrder" class="box" style="padding: 16px;">
		<nut-checkbox v-model="formIsSubmit">是否保存到<span style="font-weight: bold;">我的地址</span></nut-checkbox>
	</div>
	<div v-show="!formOrder" class="box">
		<nut-button :loading="btnLoading" @click="addAddress" block type="info">添加地址</nut-button>
	</div>
	<div v-show="formOrder" class="box">
		<nut-button :loading="btnLoading" @click="saveAddress" block type="info">保存</nut-button>
	</div>
	
	<nut-address
	    v-model:visible="showPopup"
	    :province="province"
	    :city="city"
	    :country="country"
	    :town="town"
	    @change="addressChange"
	    @close="addressClose"
	    custom-address-title="请选择所在地区"
	  ></nut-address>

	
	  <nut-safe-area position="bottom" />
</template>
<script setup>

</script>
<script>
	import { toRaw } from "vue";
	import {listRegion} from "@/request/apis/region.js"
	import {postAddAddress} from "@/request/apis/address.js"
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
				btnLoading:false,
				formOrder:false,
				formType:'',
				formIsSubmit:false,
				
				tabValue:0,
				showPopup:false,
				province: [],
				city: [
					{
						'name':'',
						'id':1
					}
				],
				country: [],
				town: [],
				currSchool:null,
				submitForm:{
					"id":'',
					"title": '',
					"detail": '',
					"lon": '',
					"lat": '',
					"name": '',
					"phone": '',
					"isDefault": false
				},
				userInfo:null
			}
		},
		onLoad(options) {
			console.log(options);
			this.initData()
			if(options.data != undefined) {
				options.data = JSON.parse(options.data)
				this.initDataFromOrder(options)
			}
	
		},
		onReachBottom() {
			
		},
		methods: {
			test(e,value) {
				// console.log(e);
				// console.log(value);
				// this.submitForm.isDefault = value
	
			},
			saveAddress(){
				if(!this.checkField()) {return}
				let form = {
					"id":this.submitForm.id,
					"title": this.submitForm.title,
					"detail": this.submitForm.detail,
					"lon": this.submitForm.lon,
					"lat": this.submitForm.lat,
					"name": this.submitForm.name,
					"phone": this.submitForm.phone,
				}
				
				var pages = getCurrentPages();
				var currPage = pages[pages.length - 1] //当前页面
				var prePage = pages[pages.length - 2] //上一个页面
				prePage.$vm.updateAddress(form,this.formType)
				prePage.$vm.getAddresses()
				if(this.formIsSubmit) {
					this.addAddress()
				}
				else{
					uni.navigateBack()
				}
				
			
			},
			checkField(){
				let form = this.submitForm
				if (!form || !form.detail || !form.title || !form.lat || !form.name || !form.phone) {
					this.showWarning('请填写完整')
					return false
				}
				return true
			},
			addAddress() {
				this.btnLoading = true
				if(!this.checkField()) {return}
				let form = this.submitForm
				form.isDefault = form.isDefault ? 1 : 0
				postAddAddress(form).then(res => {
					console.log(res);
					form.isDefault = form.isDefault == 1 ? true : false
					this.showSuccess('添加成功')
					setTimeout(() => {
						uni.navigateBack()
					},1000)
				})
				.catch(err => {
					this.showDanger(err)
				}).finally(res => {
					this.btnLoading = false
				})
			},
			initDataFromOrder(options) {
				let data = options.data
				let type = options.type
				
				this.submitForm = data
				this.submitForm.isDefault = false
				
				this.formOrder = true
				this.formType = type
				
			},
			initData() {
				this.currSchool = this.$store.state.currSchool;
				this.userInfo = this.$store.state.userInfo;
				
				this.submitForm.name = this.userInfo.userWx.realname
				this.submitForm.phone = this.userInfo.userWx.phone
			},
			chooseLocation(){
				let that = this
				if(this.submitForm.lat == '') {
					uni.chooseLocation({
						success(res) {
							console.log(res);
							that.submitForm.title = res.name
							that.submitForm.lat = res.latitude
							that.submitForm.lon = res.longitude
						},
						fail(e) {
							console.log(e);
						}
					})
				}
				else {
					uni.chooseLocation({
						latitude:this.submitForm.lat || null,
						longitude:this.submitForm.lon || null,
						success(res) {
							console.log(res);
							that.submitForm.title = res.name
							that.submitForm.lat = res.latitude
							that.submitForm.lon = res.longitude
						},
						fail(e) {
							console.log(e);
						}
					})
				}
				
			},
			addressClose(e){
				console.log(e);
				let str = e.data.addressStr
				let region1 = e.data.province.name
				let region2 = e.data.city.name
				let lat = e.data.city.lat
				let lon = e.data.city.lon
				this.submitForm.title = region1 + '-' + region2
				this.submitForm.lat = lat
				this.submitForm.lon = lon
			},
			addressChange(e) {
				console.log(e);
				if(e.value.type == 0) {
					this.queryRegionChildren(e.value.id)
				}
			},
			showRegion() {
				this.showPopup = !this.showPopup
				this.queryRegion()
			},
			queryRegion(){
				listRegion({
					schoolId:this.currSchool.id,
					type:0,
					parentId:null
				}).then(res => {
					console.log(res);
					this.province = res.data
				}).catch(err => {
					this.showDanger(err)
				})
			},
			queryRegionChildren(parentId){
				listRegion({
					schoolId:this.currSchool.id,
					type:1,
					parentId:parentId
				}).then(res => {
					console.log(res);
					this.city = res.data
				}).catch(err => {
					this.showDanger(err)
				})
			},
		},
		
	}
</script>

<style>
	@import url("/common/common.css");


.title {
	  display: flex;
	  align-items: center; /* 垂直居中 */
	  height: 100%; /* 确保子元素有高度 */
	  
}
input {
	text-align: right !important;
}
</style>
