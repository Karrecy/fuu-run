<template>
 <template>
  <nut-notify></nut-notify>
</template>
	<template>
	  <nut-cell-group title="跑腿收益展示">
			<template>
			    <div class="profit-card">
			      <div class="profit-header">
			        <div class="school-info">
			          <image class="school-logo" :src="school.logo"></image>
			          <div class="school-text">
			            <div class="school-name">{{school.name}}</div>
			            <div class="school-label">当前学校</div>
			          </div>
			        </div>
			      </div>
			      
			      <div class="profit-grid">
			        <div class="profit-item">
			          <div class="item-label">跑腿收益</div>
			          <div class="item-value profit">{{school.profitRunner}}</div>
			        </div>
			        <div class="profit-item">
			          <div class="item-label">平台收益</div>
			          <div class="item-value profit">{{school.profitPlat}}</div>
			        </div>
			        <div class="profit-item">
			          <div class="item-label">代理收益</div>
			          <div class="item-value profit">{{school.profitAgent}}</div>
			        </div>
			      </div>
			  
			      <div class="profit-footer">
			        <div class="footer-item">
			          <div class="item-label">订单底价</div>
			          <div class="item-value price">￥{{school.floorPrice}}</div>
			        </div>
			        <div class="footer-item">
			          <div class="item-label">上次更新</div>
			          <div class="item-value time">{{school.updateTime}}</div>
			        </div>
			      </div>
			    </div>
			  </template>
	  </nut-cell-group>
	</template>
	<nut-row type="flex" justify="space-evenly">
	    <nut-col :span="18">
	      <nut-button @click="toApply" block type="info" :disabled="userInfo.userWx.isRunner">
			{{userInfo.userWx.isRunner == 0 ? '申请跑腿' : '已通过'}}
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
				  	v-for="(item, index) in list"
				  	:key="index" 
				  	:accordion="true"
					 :v-model="index">
				  	<nut-collapse-item v-if="item.status == 1" title="已通过" :name="index" :value="item.createTime">	
				  		<nut-cell title="申请校区" :desc="item.schoolName"></nut-cell>
				  		<nut-cell title="真实姓名" :desc="item.realname"></nut-cell>
				  		<nut-cell title="性别" :desc="item.gender == 1 ? '男':'女'"></nut-cell>
				  		<nut-cell title="更新时间" :desc="item.updateTime"></nut-cell>
				  		<image @click="viewImage(item.studentCardUrl)" mode="aspectFit" style="width: 100%;height: 120px;border-radius: 10px;" :src="item.studentCardUrl" alt="" />
						<span style="color: red;">备注：{{item.remarks == '' ? '暂无':item.remarks}}</span>	
				  	</nut-collapse-item>
				  	<nut-collapse-item v-if="item.status == 0" title="已驳回" :name="index" :value="item.createTime">
				  		<nut-cell title="申请校区" :desc="item.schoolName"></nut-cell>
				  		<nut-cell title="真实姓名" :desc="item.realname"></nut-cell>
				  		<nut-cell title="性别" :desc="item.gender == 1 ? '男':'女'"></nut-cell>
				  		<nut-cell title="更新时间" :desc="item.updateTime"></nut-cell>
				  		<image @click="viewImage(item.studentCardUrl)" mode="aspectFit" style="width: 100%;height: 120px;border-radius: 10px;" :src="item.studentCardUrl" alt="" />
						<span style="color: red;">备注：{{item.remarks == '' ? '暂无':item.remarks}}</span>
				  	</nut-collapse-item>
				  	<nut-collapse-item v-if="item.status == 2" title="审核中" :name="index" :value="item.createTime">
				  		<nut-cell title="申请校区" :desc="item.schoolName"></nut-cell>
				  		<nut-cell title="真实姓名" :desc="item.realname"></nut-cell>
				  		<nut-cell title="性别" :desc="item.gender == 1 ? '男':'女'"></nut-cell>
				  		<nut-cell title="更新时间" :desc="item.updateTime"></nut-cell>
				  		<image @click="viewImage(item.studentCardUrl)" mode="aspectFit" style="width: 100%;height: 120px;border-radius: 10px;" :src="item.studentCardUrl" alt="" />
				  		<span style="color: red;">备注：{{item.remarks == '' ? '暂无':item.remarks}}</span>
				  	</nut-collapse-item>
				  </nut-collapse>
			  </scroll-view>
				
		  </div>
    </nut-action-sheet>
</template>

<script>
	import { toRaw } from "vue";
	import { getApplyProcess } from "@/request/apis/runner.js"
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
				btnListLoading:false,
				title: 'Hello',
				school:{
				},
				userInfo:{
					userWx:{
						isRunner:0,
					}
				},
				visible1:false,
				applyDetail:null,
				list:[]
			}
		},
		onLoad() {
			console.log('onLoad');
			this.initData()
		},
		methods: {
			triggerNotify() {
				this.showPrimary("哈哈哈")
			},
			viewImage(url) {
				uni.previewImage({
					urls: [url]
				})
			},
			toApply(){
				uni.navigateTo({
					url:"/pages/API/runner/apply/apply"
				})
			},
			initData(){
				this.userInfo = this.$store.state.userInfo;
				let school = this.$store.state.currSchool;
				let totalProfit = school.profitPlat+school.profitAgent+school.profitRunner
				school.profitPlat = ((school.profitPlat / totalProfit) * 100).toFixed(2) + '%';
				school.profitAgent = ((school.profitAgent / totalProfit) * 100).toFixed(2) + '%';
				school.profitRunner = ((school.profitRunner / totalProfit) * 100).toFixed(2) + '%';
				this.school = school
			},
			showActionProcess() {
				this.btnListLoading = true
				getApplyProcess().then(res => {
					this.list = res.data
				})
				.catch(err => {
					this.showDanger(err)
				}).finally(() => {
					this.visible1 = !this.visible1
					this.btnListLoading = false
				})
			}
		},
		
	}
</script>
  <style>
  .profit-card {
    background: #fff;
    border-radius: 16px;
    padding: 20px;
    margin: 16px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  }
  
  .profit-header {
    margin-bottom: 24px;
  }
  
  .school-info {
    display: flex;
    align-items: center;
    gap: 12px;
  }
  
  .school-logo {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    object-fit: cover;
  }
  
  .school-name {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin-bottom: 4px;
  }
  
  .school-label {
    font-size: 13px;
    color: #999;
  }
  
  .profit-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 16px;
    margin-bottom: 24px;
    padding: 16px 0;
    border-top: 1px solid #f5f5f5;
    border-bottom: 1px solid #f5f5f5;
  }
  
  .profit-item {
    text-align: center;
  }
  
  .item-label {
    font-size: 14px;
    color: #666;
    margin-bottom: 8px;
  }
  
  .item-value {
    font-size: 20px;
    font-weight: 600;
  }
  
  .item-value.profit {
    color: #498ff2;
  }
  
  .profit-footer {
    display: flex;
    justify-content: space-between;
  }
  
  .footer-item {
    text-align: center;
  }
  
  .item-value.price {
    color: #ff6b6b;
  }
  
  .item-value.time {
    font-size: 14px;
    color: #999;
    font-weight: normal;
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
  </style>
