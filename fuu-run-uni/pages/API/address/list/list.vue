<template>
	<template>
	  <nut-notify></nut-notify>
	</template>
	<div v-show="skeletonLoading">
		<nut-skeleton  v-for="(item, index) in 8" :key="index" width="100%" height="24px" title animated row="1"> </nut-skeleton>
	</div>
	<div v-show="!skeletonLoading" style="width: 100%;height: 88vh;box-sizing: border-box;">
		<div v-show="total != 0">
			<nut-address-list
								:data="rows"
							   :options="dataOptions"
							   :show-bottom-button="false"
							   :swipe-edition="true"
							   @scroll="onScroll"
							   @click-item="onItemClick"
							   @swipe-del="onItemDelClick">
				 <template #itemIcon="{ item }">
					  <nut-icon name="edit" @click="onItemEditClick(item)"></nut-icon>
				</template>
			</nut-address-list>
		</div>
		
		<div v-show="total == 0 && skeletonLoading == false">
			<nut-empty description="暂无数据"></nut-empty>
		</div>
		
	</div>
	

	
	<nut-button @click="toAddressAdd" block type="info">添加地址</nut-button>
	 
	
<!-- 	<div class="box">
		
	</div> -->

	  <nut-safe-area position="bottom" />
</template>
<script>
	import { toRaw } from "vue";
	import {listSchool} from "@/request/apis/school.js"
	import {getListAddress,delAddress} from "@/request/apis/address.js"
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
				skeletonLoading:true,
				loading:true,
				title: 'Hello',
				// 查询参数
				queryParams: {
					pageNum: 1,
					pageSize: 20,
				},
				  // 总条数
				total: 0,
				rows:[],
				dataOptions:{
					fullAddress: (item) => `${item.title || ''}${'-' + item.detail || ''}`,
					addressName: 'name',
					defaultAddress:(item) => item.isDefault === 1
				}
			}
		},
		onLoad() {
		
		},
		onShow() {
			this.resizePage()
			this.getList()
		},
		onHide() {
			
		},
		onReachBottom() {
			
		},
		methods: {
			onScroll(e) {
				console.log(e);
			},
			resizePage(){
				this.rows = []
			},
			onItemClick(Event,item,index){
				console.log(index);
			},
			onItemEditClick(item){
				console.log(item);
				uni.navigateTo({
					url:"/pages/API/address/edit/edit?addressId=" + item.id
				})
			},
			onItemDelClick(Event,item,index){
				
				delAddress(item.id).then(res => {
					console.log(res);
					this.rows.splice(index,1)
					this.showSuccess("删除成功")
				})
				.catch(err => {
					this.showDanger(err)
				})
			},
			getList(){
				this.skeletonLoading = true
				getListAddress().then(res => {
					console.log(res);
					this.total = res.data.length
					this.rows.push(...res.data)
					this.skeletonLoading = false
				}).catch(err => {
					this.showDanger(err)
				})
			},
			toAddressAdd() {
				uni.navigateTo({
					url:"/pages/API/address/add/add"
				})
			},
		},
		
	}
</script>
<style>
@import url("/common/common.css");

/* 页面容器 */
page {
  background-color: #f7f8f8;
  min-height: 100vh;
}

/* 骨架屏样式优化 */
.nut-skeleton {
  margin: 12px 16px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.8);
}

/* 地址列表容器 */
.nut-address-list {
  padding: 12px 16px !important;
  height: calc(100% - 80px);
  overflow-y: auto;
  box-sizing: border-box;
}

/* 地址卡片样式 */
.nut-address-list-item {
  background: #fff;
  border-radius: 12px !important;
  margin-bottom: 0 !important;
  padding: 16px !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
}

.nut-address-list-item:active {
  transform: scale(0.98);
}

/* 地址信息样式 */
.nut-address-list-item__info {
  padding: 0 !important;
}

.nut-address-list-item__info__name {
  font-size: 16px !important;
  font-weight: 500 !important;
  color: #333 !important;
}

.nut-address-list-item__info__address {
  font-size: 14px !important;
  color: #666 !important;
  margin-top: 6px !important;
}

/* 默认地址标签 */
.nut-tag {
  background: rgba(73, 143, 242, 0.1) !important;
  color: #498ff2 !important;
  border-radius: 4px !important;
  padding: 2px 6px !important;
  font-size: 12px !important;
}

/* 编辑图标 */
.nut-icon {
  color: #666;
  padding: 6px;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.nut-icon:active {
  background: rgba(0, 0, 0, 0.05);
}
.nut-address-list-swipe, .nut-address-list-general {
	background-color: transparent !important; 
	padding: 0 !important;
}
.nut-swipe__right {
	    border-radius: 10px !important;
	    overflow: hidden !important;
}
/* 底部按钮 */
.nut-button--block {
  position: fixed !important;
  bottom: 20px !important;
  left: 50% !important;
  transform: translateX(-50%) !important;
  width: 90% !important;
  height: 44px !important;
  border-radius: 22px !important;
  font-size: 16px !important;
  font-weight: 500 !important;
  background: linear-gradient(135deg, #498ff2 0%, #3c7be7 100%) !important;
  box-shadow: 0 4px 12px rgba(73, 143, 242, 0.3) !important;
  border: none !important;
}

/* 空状态样式 */
.nut-empty {
  margin-top: 40% !important;
}

.nut-empty__description {
  color: #999 !important;
  font-size: 14px !important;
}

/* 滑动删除按钮 */
.nut-address-list-item__right {
  background: #ff4949 !important;
  color: white !important;
  width: 65px !important;
}

/* 安全区域适配 */
.nut-safe-area {
  background: transparent !important;
}
</style>
<!-- <style>
	@import url("/common/common.css");
.nut-icon{
	vertical-align: middle;
}
.nut-address-list:last-child {
	padding-bottom: 0 !important;
	border-bottom: 84px solid rgba(255, 255, 255, 0); /* 透明的边框 */
}
.nut-address-list-swipe, .nut-address-list-general {
	padding: 5px 18px !important;
}
.nut-button--block {
    position: fixed !important;
    bottom: 40px !important;
    width: 90% !important;
    margin-left: 5% !important;
}
.nut-address-list {
	padding: 0 5px;
	width: 100%;
	height: 100%;
	overflow-y: auto !important;
	box-sizing: border-box !important;
}
.nut-skeleton {
		margin-top: 20px;
	}
</style -->>
