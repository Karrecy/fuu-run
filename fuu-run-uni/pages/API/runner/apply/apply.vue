<template>
	  <nut-notify></nut-notify>
	<nut-form>
		<nut-form-item label="申请校区">
			<nut-input v-model="school.name"  class="nut-input-text"  type="text" disabled/>
		</nut-form-item>
	      <nut-form-item label="真实姓名">
	          <nut-input v-model="form.realname"  class="nut-input-text" placeholder="请输入姓名" type="text" />
	      </nut-form-item>
	      <nut-form-item label="性别">
	         <nut-radio-group direction="horizontal" v-model="form.gender">
				 <nut-radio label="1">男生</nut-radio>
				 <nut-radio label="0">女生</nut-radio>
			 </nut-radio-group>
	      </nut-form-item>
	      <nut-form-item label="学生证" label-position="top">
	          <nut-uploader v-model:fileList="fileList" @success="uploadSuccess" maximum="1" @file-item-click="imageClick"
	          	@oversize="oversize" :headers="headers" :data="uploaderData" :maximize="5242880" name="file" accept="image/*"
	          	:url="uploadUrl">
	          </nut-uploader>
	      </nut-form-item>
	  
	</nut-form>
	
	<view style="position: fixed;bottom: 6%;width: 90%;margin-left: 5%;">
		<nut-button :loading="btnLoading" @click="submit" block type="info">
				提交审核
		</nut-button>
	</view>
	
</template>
<script setup>

</script>
<script>
	import { toRaw } from "vue";
	import { runnerSubmit } from "@/request/apis/runner.js"
	import {upload_url} from '@/request/request.js'
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
				fileList:[],
				title: 'Hello',
				school:null,
				visible1:false,
				applyDetail:null,
				uploadUrl:upload_url,
				form:{
					schoolId:'',
					realname:'',
					gender:'1',
					studentCardUrl:''
				},
				headers: {
					Authorization: 'Bearer ' + uni.getStorageSync("token"),
					'Content-Type': 'multipart/form-data',
				},
				uploaderData:{
					type:5,
					name:''
				},
				btnLoading:false,
			}
		},
		onLoad() {
			this.initData()
		},
		onHide() {
			this.fileList = []
		},
		onReachBottom() {
			
		},
		methods: {
			submit() {
				this.btnLoading = true
				runnerSubmit(this.form).then(res => {
					console.log(res);
					this.showSuccess("提交成功")
					setTimeout(() => {
						uni.navigateBack()
					},1000)
				})
				.catch(err => {
					this.showDanger(err)
				})
				.finally(res => {
					this.btnLoading = false
				})
			},
			toApply(){
				
			},
			initData(){
				this.school = this.$store.state.currSchool;
				this.form.schoolId = this.school.id
			},
			imageClick(file) {
				file = file.fileItem
				console.log(file);
				let imgsArray = [];
				imgsArray[0] = file.url;
				uni.previewImage({
					current: 0,
					urls: imgsArray
				});
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
				this.form.studentCardUrl = url
			},
		
		},
		
	}
</script>

<style>
.custom-content {
  padding: 10px 10px 160px;
}
.nut-cell-group__wrap {
	padding: 0 10px !important;
}
.nut-uploader__upload {
	width: 100% !important;
	height: 160px !important;
}
.nut-uploader__preview-img {
	width: 100% !important;
	height: 160px !important;
}
</style>
