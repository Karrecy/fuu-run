<template>
	
	<template>
	  <nut-notify></nut-notify>
	</template>
	
	<view v-show="skeletonLoading">
		<nut-skeleton width="100%" height="24px" animated row="1"> </nut-skeleton>
		<nut-skeleton width="100%" height="24px" animated row="3"> </nut-skeleton>
		<nut-skeleton width="100%" height="24px" animated avatar avatarSize="60px" row="3"> </nut-skeleton>
	</view>
	
	
	<nut-form v-show="!skeletonLoading">
		<nut-form-item label="申诉订单号">
			<nut-input v-model="form.orderId"  class="nut-input-text"  type="text" disabled/>
		</nut-form-item>
	      <nut-form-item label="问题描述">
			  <nut-textarea v-model="form.appealReason" rows="2" limit-show max-length="100" />
	      </nut-form-item>
	      <nut-form-item label="申诉凭证" label-position="top">
			  <nut-uploader v-model:file-list="defaultUploadList" @file-item-click="fileClick" @success="uploadSuccess" @delete="uploadDelete"
			  	@oversize="oversize" :headers="headers" :data="uploaderData" :maximize="5242880" name="file" maximum="5"
			  	:url="uploadUrl"></nut-uploader>
	      </nut-form-item>
	  
	</nut-form>
	<nut-row type="flex" justify="space-evenly">
	    <nut-col :span="18">
	      <nut-button :loading="btnSubmitLoading" @click="submit" block type="info">
	      		提交申诉
	      </nut-button>
	    </nut-col>
	    <nut-col :span="4">
	      <nut-button :loading="btnListLoading" @click="showActionProcess" shape="square" plain type="info">
	         <template #icon>
	           <nut-icon name="horizontal" />
	         </template>
	       </nut-button>
	    </nut-col>
	  </nut-row>
	
	<nut-action-sheet v-model:visible="visible1" title="申请记录">
		  <div class="custom-content">
			  <scroll-view style="width: 100%;height: 100%;" scroll-y="true">
				  <nut-collapse
				  	v-for="(item, index) in appealList"
				  	:key="index" 
				  	:accordion="true"
					 :v-model="index">
				  	<nut-collapse-item v-if="item.orderAppeal.appealStatus == 1" title="已通过" :name="index" :value="item.orderAppeal.appealTime">	
				  		<nut-cell title="申诉理由" :desc="item.orderAppeal.appealReason"></nut-cell>
				  		<nut-cell title="真实姓名" >
							<view class="container_image">
							   <view class="image-box" v-for="(item1, index1) in item.imageUrls" :key="index">
								  <image @click="previewAppealedImage(index,index1)" :src="item1" mode="aspectFit"  class="image-item" ></image>
							   </view>
							 </view>
						</nut-cell>
				  		<nut-cell title="更新时间" :desc="item.orderAppeal.updateTime"></nut-cell>
						<span style="color: red;">备注：{{item.orderAppeal.remarks == '' ? '暂无':item.orderAppeal.remarks}}</span>	
				  	</nut-collapse-item>
				  	<nut-collapse-item v-if="item.orderAppeal.appealStatus == 0" title="已驳回" :name="index" :value="item.orderAppeal.appealTime">
				  		<nut-cell title="申诉理由" :desc="item.orderAppeal.appealReason"></nut-cell>
				  		<nut-cell title="真实姓名">
				  			<view class="container_image">
				  			   <view class="image-box" v-for="(item1, index1) in item.imageUrls" :key="index">
				  				  <image @click="previewAppealedImage(index,index1)" :src="item1" mode="aspectFit"  class="image-item" ></image>
				  			   </view>
				  			 </view>
				  		</nut-cell>
				  		<nut-cell title="更新时间" :desc="item.orderAppeal.updateTime"></nut-cell>
				  		<span style="color: red;">备注：{{item.orderAppeal.remarks == '' ? '暂无':item.orderAppeal.remarks}}</span>	
				  	</nut-collapse-item>
				  	<nut-collapse-item v-if="item.orderAppeal.appealStatus == 2" title="审核中" :name="index" :value="item.orderAppeal.appealTime">
				  		<nut-cell title="申诉理由" :desc="item.orderAppeal.appealReason"></nut-cell>
				  		<nut-cell title="真实姓名">
				  			<view class="container_image">
				  			   <view class="image-box" v-for="(item1, index1) in item.imageUrls" :key="index">
				  				  <image @click="previewAppealedImage(index,index1)" :src="item1" mode="aspectFit"  class="image-item" ></image>
				  			   </view>
				  			 </view>
				  		</nut-cell>
				  		<nut-cell title="更新时间" :desc="item.orderAppeal.updateTime"></nut-cell>
				  		<span style="color: red;">备注：{{item.orderAppeal.remarks == '' ? '暂无':item.orderAppeal.remarks}}</span>	
				  	</nut-collapse-item>
				  </nut-collapse>
			  </scroll-view>
				
		  </div>
	</nut-action-sheet>
	
</template>

<script>
	import { toRaw } from "vue";
	import { runnerSubmit } from "@/request/apis/runner.js"
	import { postAppealOrder,getAppealOrder } from "@/request/apis/order.js"
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
				defaultUploadList:[],
				visible1:false,
				appealList:[],
				skeletonLoading:false,
				title: 'Hello',
				canSubmit:false,
				uploadUrl:upload_url,
				form:{
					orderId:'',
					appealReason:'',
					ossAppealList:[]
				},
				headers: {
					Authorization: 'Bearer ' + uni.getStorageSync("token"),
					'Content-Type': 'multipart/form-data',
				},
				uploaderData:{
					type:6,
					name:''
				},
				btnSubmitLoading:false,
				btnListLoading:false,
			}
		},
		onLoad(options) {
			// this.skeletonLoading = true
			console.log(options);
			this.form.orderId = options.orderId
			// this.getList(options.orderId)
		},
		onReachBottom() {
			
		},
		onUnload() {
			console.log('onUnload');
			this.defaultUploadList = []
		},
		onHide() {
			console.log('onHide');
		},
		methods: {
			previewAppealedImage(index,index1) {
				let images = this.appealList[index].imageUrls
				uni.previewImage({
					current: index1,
					urls: images
				});
			},
			showActionProcess() {
				this.btnListLoading = true
				getAppealOrder(this.form.orderId).then(res => {
					console.log(res);
					this.appealList = res.data
					this.visible1 = true
				}).catch(err => {
					this.showDanger(err)
				})
				.finally(res => {
					this.btnListLoading = false
				})
			},
			submit() {
				this.btnSubmitLoading = true
				postAppealOrder(this.form).then(res => {
					console.log(res);
					this.showSuccess("提交成功，请耐心等待")
					setTimeout(() => {
						uni.navigateBack()
					},1000)
				})
				.catch(err => {
					this.showDanger(err)
				})
				.finally(res => {
					this.btnSubmitLoading = false
				})
			},
			toApply(){
				
			},
			
			fileClick(file) {
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
				this.showWarning('文件大小超出5MB')
			},
			uploadDelete(e) {
				let index = e.index
				this.form.ossAppealList.splice(index, 1)
			},
			uploadSuccess(e) {
				console.log(e);
				let data = e.data.data
				data = JSON.parse(data)
				let ossId = data.data.ossId
				this.form.ossAppealList.push(ossId)
			},
		
		},
		
	}
</script>

<style>
	.container_image {
		display: flex;
		flex-wrap: wrap;
		justify-content: space-between;
		padding: 10px;
	  
	}
	
	.image-box {
		width: 80px;
		height: 80px;
		margin-top: 2px;
	}
	
	.image-item {
	    width: 100%;
	    height: 100%;
	}
	.nut-button--plain.nut-button--info {
			border-radius: 15px !important;
		}
	.custom-content {
	  padding: 10px 10px 160px;
	}
	.nut-cell-group__wrap {
		padding: 0 10px !important;
	}
	.nut-row {
	    position: fixed;
		bottom: 6%;
	}
	.nut-collapse-item .nut-collapse__item-wrapper .nut-collapse__item-wrapper__content, .nut-collapse-item .nut-collapse__item-wrapper .nut-collapse__item-extraWrapper__extraRender, .nut-collapse-item .nut-collapse__item-extraWrapper .nut-collapse__item-wrapper__content, .nut-collapse-item .nut-collapse__item-extraWrapper .nut-collapse__item-extraWrapper__extraRender {
		padding: 0 24px !important;
	}
	.nut-popup {
		max-height: 70% !important;
	}
	.nut-cell {
		margin: 0 !important;
	}
	.nut-uploader {
		justify-content: space-between;
	}
	
	.nut-uploader__upload {
		margin: 0 10px 10px 0;
	}
	
</style>