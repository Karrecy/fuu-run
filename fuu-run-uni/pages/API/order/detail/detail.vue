<template>
	<nut-navbar safe-area-inset-top="true" fixed="true" placeholder="false">
		<template #left>
			<nut-icon @click="back" name="left" /> 
			<span  style="vertical-align: middle;">{{showNav ? '订单' + order.orderMain.statusText : ''}}</span>
		</template>
	</nut-navbar>
	<div class="top" v-show="!skeletonLoading">
		<span @click="showOrderProgressPopup = true" style="vertical-align: middle;">订单{{order.orderMain.statusText}}</span>
		<nut-icon @click="back" name="right" />
	</div>
	<div class="top" style="padding: 0 0;" v-show="skeletonLoading">
		<nut-skeleton :title="false" width="30%" height="24px" animated row="1"> </nut-skeleton>
	</div>
	<div class="line"></div>
	
	<div v-show="skeletonLoading">
		<div class="height20"></div>
		<nut-skeleton width="100%" height="24px" title animated row="3"> </nut-skeleton>
	</div>
	<div v-show="!skeletonLoading">
		<!-- 待支付 -->
		<div v-if="order.orderMain.status == 0" class="box">
			<div class="tagMy">
				<div>
					<span>将在 </span>
					<nut-countdown @on-end="countdownHandleEnd()"  format="mm:ss" :end-time="countdownEnd" :start-time="countdownStart" ></nut-countdown>
					<span> 后自动取消</span>
				</div>
				<div></div>
			</div>
			<div class="splitLine"></div>
			<div class="operaBox">
				<div @click="payAgain" class="oprea">
					<nut-icon custom-color="#1296db" name="/static/icons/微信支付.png"></nut-icon>
					<span style="color: #1296db;" class="o">继续付款</span>
				</div>
				<div @click="visibleCancelDialog = true" class="oprea">
					<nut-icon custom-color="#8a8a8a" name="/static/icons/取消订单.png"></nut-icon>
					<span style="color: #8a8a8a;" class="o">取消订单</span>
				</div>
			</div>
		</div>
		
		<!-- 待接单 -->
		<div v-if="order.orderMain.status == 1" class="box">
			<div class="tagMy">
				<div>
					<span>若无人接单,将在 </span>
					<nut-countdown @on-end="countdownHandleEnd()"  :end-time="countdownEnd" :start-time="countdownStart" ></nut-countdown>
					<span> 后下架并退款</span>
				</div>
				<div></div>
			</div>
			<div class="splitLine"></div>
			<div class="operaBox">
				<div @click="cancelBefore" class="oprea">
					<nut-icon custom-color="#8a8a8a" name="/static/icons/取消订单.png"></nut-icon>
					<span style="color: #8a8a8a;" class="o">取消订单</span>
				</div>
			
			</div>
		</div>
		
		<!-- 待配送 -->
		<div v-if="order.orderMain.status == 2" class="box">
			<div class="tagMy">
				<div>
					<span>等待跑腿员配送</span>
				</div>
				<div></div>
			</div>
			<div class="splitLine"></div>
			<div class="operaBox">
				<div @click="cancelBefore" class="oprea">
					<nut-icon custom-color="#8a8a8a" name="/static/icons/取消订单.png"></nut-icon>
					<span style="color: #8a8a8a;" class="o">取消订单</span>
				</div>
			</div>
		</div>
		
		<!-- 配送中 -->
		<div v-if="order.orderMain.status == 3" class="box">
			<div class="tagMy">
				<div>
					<span>跑腿员正在努力配送中，请耐心等待~</span>
				</div>
				<div></div>
			</div>
			<div class="splitLine"></div>
			<div class="operaBox">
				<div @click="cancelBefore" class="oprea">
					<nut-icon custom-color="#8a8a8a" name="/static/icons/取消订单.png"></nut-icon>
					<span style="color: #8a8a8a;" class="o">取消订单</span>
				</div>
			</div>
		</div>
		
		<!-- 已送达 -->
		<div v-if="order.orderMain.status == 4" class="box">
			<div class="tagMy">
				<div>
					<div>跑腿员已送达，查看凭证无误后可点击确认送达，完成该订单</div>
					<span style="font-size: small;color: gray;margin-top: 2px;">若无问题订单将于</span>
					<nut-countdown @on-end="countdownHandleEnd()"  :end-time="countdownEnd" :start-time="countdownStart" ></nut-countdown>
					<span style="font-size: small;color: gray;margin-top: 2px;">后由系统自动确认</span>
				</div>
				<div></div>
			</div>
			<div class="splitLine"></div>
			<div class="operaBox">
				<div @click="visibleConfirmDialog = true" class="oprea">
					<nut-icon custom-color="#8a8a8a" name="/static/icons/完成订单.png"></nut-icon>
					<span style="color: #8a8a8a;" class="o">确认完成</span>
				</div>
				<div @click="showCompletionImagePopup = true" class="oprea">
					<nut-icon custom-color="#8a8a8a" name="/static/icons/查看订单.png"></nut-icon>
					<span style="color: #8a8a8a;" class="o">查看凭证</span>
				</div>
				<div @click="toAppeal" class="oprea">
					<nut-icon custom-color="#8a8a8a" name="/static/icons/订单申诉.png"></nut-icon>
					<span style="color: #8a8a8a;" class="o">问题申诉</span>
				</div>
			</div>
		</div>
		
		<!-- 已取消 -->
		<div v-if="order.orderMain.status == 5" class="box">
			<div class="tagMy">
				<div>
					<span>取消原因：</span>
					<span>{{order.progress.cancelReason == null ? '暂无':order.progress.cancelReason}}</span>
					<span v-show="order.orderPayment.paymentStatus == 2">已发起退款，请耐心等待</span>
					<span v-show="order.orderPayment.paymentStatus == 3">退款已到账</span>
				</div>
				<div></div>
			</div>
			<div class="splitLine"></div>
			<div class="operaBox">
				<div @click="visibleCancelDialog = true" class="oprea">
					<nut-icon custom-color="#8a8a8a" name="/static/icons/再来一单.png"></nut-icon>
					<span style="color: #8a8a8a;" class="o">再来一单</span>
				</div>
			</div>
		</div>
		
		<!-- 已完成 -->
		<div v-if="order.orderMain.status == 10" class="box">
			<div class="tagMy">
				<div>
					<span>订单已完成</span>
				</div>
				<div></div>
			</div>
			<div class="splitLine"></div>
			<div class="operaBox">
				<div @click="showCompletionImagePopup = true" class="oprea">
					<nut-icon custom-color="#8a8a8a" name="/static/icons/查看订单.png"></nut-icon>
					<span style="color: #8a8a8a;" class="o">查看凭证</span>
				</div>
			</div>
		</div>
		
		<!-- 已申诉 -->
		<div v-if="order.orderMain.status == 11" class="box">
			<div class="tagMy">
				<div>
					<div>已发起申诉</div>
					<span v-show="order.orderPayment.paymentStatus == 2">已发起退款，请耐心等待</span>
					<span v-show="order.orderPayment.paymentStatus == 3">退款已到账</span>
				</div>
				<div></div>
			</div>
			<div class="splitLine"></div>
			<div class="operaBox">
				<div @click="showActionProcess" class="oprea">
					<nut-icon custom-color="#8a8a8a" name="/static/icons/查看申诉.png"></nut-icon>
					<span style="color: #8a8a8a;" class="o">查看申诉</span>
				</div>
				<div @click="toAppeal" class="oprea">
					<nut-icon custom-color="#8a8a8a" name="/static/icons/订单申诉.png"></nut-icon>
					<span style="color: #8a8a8a;" class="o">再次申诉</span>
				</div>
				
			</div>
		</div>
		
	</div>
	
	
	<div class="box" v-show="order.orderMain.runnerId != null && !skeletonLoading">
		<div class="t">跑腿员</div>
		<div class="userBox">
			<image :src="order.avatarRunner" mode=""></image>
			<div class="nickname ellipsisMy">{{order.nicknameRunner}}</div>
			<nut-icon @click="toChat" name="/static/icons/消息.png"></nut-icon>
			<span style="width: 6px;"></span>
			<nut-icon @click="getPhone" name="/static/icons/电话.png"></nut-icon>
		</div>
	</div>
	<div v-show="order.orderMain.runnerId != null && skeletonLoading">
		<div class="height20"></div>
		<nut-skeleton avatar width="100%" height="24px" title animated row="1"> </nut-skeleton>
	</div>
	
	<div class="box" v-show="!skeletonLoading">
		<div class="t">订单信息</div>
		<nut-cell title="类型" :desc="order.orderMain.typeText"></nut-cell>
		<nut-cell title="就近购买" sub-title="" v-if="order.orderMain.startAddress == null && order.orderMain.serviceType == 1">
			<template #icon>
					<image class="nut-icon" src="/static/icons/start.png"></image>
			</template>
		</nut-cell>
		<nut-cell @click="openLocation(order.orderMain.startAddress)" :title="order.orderMain.startAddress.title+' '+order.orderMain.startAddress.detail" :sub-title="order.orderMain.startAddress.name+' '+order.orderMain.startAddress.phone" v-if="order.orderMain.startAddress != null">
			<template #icon>
					<image class="nut-icon" src="/static/icons/start.png"></image>
			</template>
		</nut-cell>
		<nut-cell @click="openLocation(order.orderMain.endAddress)" :title="order.orderMain.endAddress.title+' '+order.orderMain.endAddress.detail" :sub-title="order.orderMain.endAddress.name+' '+order.orderMain.endAddress.phone" v-if="order.orderMain.endAddress != null">
			<template #icon>
				<image class="nut-icon" src="/static/icons/end.png"></image>
			</template>
		</nut-cell>
		<div class="detail">
			<nut-ellipsis
			    direction="end"
				rows="2"
			    expandText="展开"
			    collapseText="收起"
			    :content="'备注:' + order.orderMain.detail" ></nut-ellipsis>
		</div>
		<nut-cell @click="showImagePopup = true" v-if="order.attachImages.length > 0" is-link title="附加图片" :desc="order.attachImages.length + '张'"></nut-cell>
		<nut-cell @click="showFilePopup = true" v-if="order.attachFiles.length > 0" is-link title="附加文件" :desc="order.attachFiles.length + '个'"></nut-cell>
		<nut-cell @click="showPricePopup = true" is-link title="实付款" :desc="'￥' + order.orderPayment.actualPayment"></nut-cell>
		 <nut-collapse @change="collapseChange" v-model="activeName" :accordion="true" > 
		    <nut-collapse-item :title="collapseText" :name="1" rotate="-180">
				<nut-cell v-if="order.orderMain.weight != null" title="物品重量" :desc="order.orderMain.weight"></nut-cell>
				<nut-cell @click="copyOrderId(order.orderMain.id)" is-link title="订单号" :desc="order.orderMain.id + ' '">
					<template #link>
						<nut-icon name="/static/icons/复制.png"></nut-icon>
					</template>
				</nut-cell>
				<nut-cell title="下单时间" :desc="order.orderMain.createTime"></nut-cell>
				<nut-cell v-if="order.orderMain.gender == 1" title="性别限制" desc="男"></nut-cell>
				<nut-cell v-if="order.orderMain.gender == 2" title="性别限制" desc="不限制"></nut-cell>
				<nut-cell v-if="order.orderMain.gender == 0" title="性别限制" desc="女"></nut-cell>
				<nut-cell v-if="order.orderMain.isTimed == 1" title="指定配送时间" :desc="order.orderMain.specifiedTime"></nut-cell>
		    </nut-collapse-item>
		  </nut-collapse>
	</div>
	<div v-show="skeletonLoading">
		<div class="height20"></div>
		<nut-skeleton width="100%" height="24px" title animated row="1"> </nut-skeleton>
		<div class="height20"></div>
		<nut-skeleton width="100%" height="24px" title animated row="5"> </nut-skeleton>
	</div>
	<nut-safe-area position="bottom" />

	<template>
		<nut-dialog content="确定订单已完成？" v-model:visible="visibleConfirmDialog" @cancel="visibleConfirmDialog = false" @ok="confirmSubmit" >
			
		</nut-dialog>
		<nut-dialog title="确定取消订单？" v-model:visible="visibleCancelDialog" @cancel="visibleCancelDialog = false" @ok="cancelSubmit" >
			<template #default>
				<span v-show="cancelBeforeText != ''">{{cancelBeforeText}}</span>
				<nut-input v-model="cancelForm.cancelReason" type="text" placeholder="填写原因" max-length="10" show-word-limit>
			<!-- 		<template #left>
						<nut-icon name="/static/icons/RMB.png"></nut-icon>
					</template> -->
							
				</nut-input>
				
			</template>
		</nut-dialog>
		<nut-popup v-model:visible="showImagePopup" position="bottom" safe-area-inset-bottom>
			<div class="nut-picker__bar">
				<div class="nut-picker__title">附件图片</div>
			</div>
			<div class="custom-content">
				 <view class="container_image">
				    <view class="image-box" v-for="(item, index) in order.attachImages" :key="index">
							  <image @click="previewImage(index)" :src="item.fileUrl" mode="aspectFit"  class="image-item" ></image>
				    </view>
				  </view>
			</div>
		</nut-popup>
		<nut-popup v-model:visible="showFilePopup" position="bottom" safe-area-inset-bottom>
			<div class="nut-picker__bar"> 
				<div class="nut-picker__title">附件文件</div>
			</div>
			<div class="custom-content" style="padding-bottom: 20px;">
				 <div 
					 @click="viewFile(item)"
					 v-for="(item, index) in order.attachFiles" 
					 :key="index" 
					 class="fileBox">
					  <image style="float: left;height: 35px;width: 35px;margin: 5px;" :src="'/static/fileicon/'+item.icon" mode=""/>
					  <div class="title">
						<div class="name">{{item.fileName}}</div>
						<div class="size">{{item.fileSize}}</div>
					  </div>
		        </div>
			</div>
		</nut-popup>
		<nut-popup v-model:visible="showCompletionImagePopup" position="bottom" safe-area-inset-bottom>
			
			<div class="nut-picker__bar">
				<div class="nut-picker__title">完成凭证</div>
			</div>
			<div class="custom-content">
				 <view class="container_image">
				    <view class="image-box" v-for="(item, index) in order.completionImages" :key="index">
							  <image @click="previewCompletionImage(index)" :src="item.fileUrl" mode="aspectFit"  class="image-item" ></image>
				    </view>
				  </view>
			</div>
		</nut-popup>
		<nut-popup v-model:visible="showPricePopup" position="bottom" safe-area-inset-bottom>
			<div class="nut-picker__bar">
				<div class="nut-picker__title">费用明细</div>
			</div>
			<div class="custom-content">
				<div class="priceBox">
					<div class="priceChild">
					    <span class="left">总金额</span>
					    <span class="fill"></span>
					    <span class="right">￥{{order.orderMain.totalAmount}}</span>
				    </div>
					<div class="priceChild">
					    <span class="left">追加金额</span>
					    <span class="fill"></span>
					    <span class="right">￥{{order.orderPayment.additionalAmount}}</span>
					</div>
				</div>
			</div>
		</nut-popup>
		<nut-popup v-model:visible="showOrderProgressPopup" position="bottom" safe-area-inset-bottom>
			
			<div class="nut-picker__bar">
				<div class="nut-picker__title">订单跟踪</div>
			</div>
			<div class="custom-content">
				 <view class="order-tracking">
				   <view v-for="(step, index) in orderSteps" :key="index" class="step">
				     <!-- 左侧图标和竖线 -->
				     <view class="icon-container">
					   <view v-if="index == orderSteps.length-1" class="stepBox">
						   <view class="stepLine"  style="transform: translateY(-4px);"></view>
							<image class="stepImage" src="/static/order/check-circle.png"></image>
							<view class="stepIconFake"></view>
					   </view>
					   <view v-else-if="index == 0" class="stepBox">
							<view class="stepIconFake"></view>
							<image v-if="(step.description == '订单已申诉'||step.description == '订单已取消')" class="stepImage" src="/static/order/close-circle.png"></image>
							<image v-else class="stepImage" src="/static/order/check-circle-fill.png"></image>
							<view class="stepLine" style="transform: translateY(4px);"></view>
					   </view>
					   <view v-else class="stepBox">
							<view class="stepLine" style="transform: translateY(-4px);"></view>
							<image class="stepImage" src="/static/order/check-circle.png"></image>
							<view class="stepLine" style="transform: translateY(4px);"></view>
					   </view>
				     </view>
				 
				     <!-- 右侧内容 -->
				     <view class="content">
				       <text class="status">{{ step.description }}</text>
				       <text class="time">{{ formatTime(step.time) }}</text>
				     </view>
				   </view>
				 </view>

			</div>
		</nut-popup>
		
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
</template>

<script>
	import dayjs from 'dayjs';
	import relativeTime from 'dayjs/plugin/relativeTime'; // 引入 relativeTime 插件
	import 'dayjs/locale/zh-cn';  // 导入中文语言包
	// 扩展 dayjs 插件
	dayjs.extend(relativeTime);
	// 设置为中文
	dayjs.locale('zh-cn');
	import {getCancelBefore,getDetailOrderUser,getPayAgain,postCancelOrder,getConfirmOrder,getPhoneOrder,getAppealOrder} from '@/request/apis/order.js'
	export default {
		data() {
			return {
				showPricePopup:false,
				visible1:false,
				appealList:[],
				showOrderProgressPopup:false,
				 orderSteps: [],
				config:{},
				defaultCompletionImageList:[],
				showCompletionImagePopup:false,
				skeletonLoading:true,
				collapseText:'展开订单信息',
				cancelForm:{
					orderId:null,
					cancelReason:null
				},
				visibleConfirmDialog:false,
				visibleCancelDialog:false,
				title: 'Hello',
				showNav:false,
				userInfo:{
					userWx:{}
				},
				visible1:false, //检查有没有选择校区dialog
				showFilePopup:false,
				showImagePopup:false,
				cancelBeforeText:'',
				order:{
					attachFiles:[],
					attachImages:[],
					completionImages:[],
					avatarRunner:null,
					nicknameRunner:null,
					orderMain:{
						startAddress:null,
						endAddress:null,
					},
					orderPayment:{},
					progress:{},
				},
				countdownStart: Date.now(),
				countdownEnd: Date.now() + 10*1000,
			}
		},
		onPageScroll(e) {
		    // 监听页面滚动
		    if (e.scrollTop > 48) {
		      this.showNav = true
		    } else {
		      this.showNav = false
		    }
		},
		onLoad(options) {
			console.log("detail onLoad");
			const checkOperationStatus = setInterval(() => {
			  if (this.$store.state.appLaunch) {
				 this.skeletonLoading = true
				 this.config = this.$store.state.config;
				 this.getDetail(options.id)
				clearInterval(checkOperationStatus);
				console.log('首页的js文件中的代码执行');
			  }
			}, 100); // 每100毫秒检查一次状态变化
			
			
		},
		onShow() {
			// this.initData()
		},	
		onPullDownRefresh() {
			this.getDetail(this.order.orderMain.id)
			//uni.stopPullDownRefresh()
		},
		methods: {
			toHome() {
				uni.redirectTo({
					url:"/pages/tabBar/index/index"
				})
			},
			openLocation(address) {
				console.log(address);
				uni.openLocation({
				        // 目标位置的经纬度
				        latitude: parseFloat(address.lat),
				        longitude: parseFloat(address.lon),
				        // 目标位置的名称
				        name: address.title,
				        // 目标位置的详细地址
				        address: address.detail,
				        // 地图缩放比例
				        scale: 18,
				        // 调用成功时的回调函数
				        success: function(res) {
				          console.log('调用成功：', res)
				        },
				        // 调用失败时的回调函数
				        fail: function(res) {
				          console.log('调用失败：', res)
				        },
				        // 调用完成时的回调函数
				        complete: function(res) {
				          console.log('调用完成：', res)
				        }
				      })
			},
			cancelBefore() {
				getCancelBefore(this.order.orderMain.id).then(res => {
					console.log(res);
					this.cancelBeforeText = res.data
					this.visibleCancelDialog = true
				})
			},
			toChat() {
				const SUBSCRIBE_ID = 'nFzHoJjaKP8W6jdiFZkXsX6Z2A1u6O1F7wGrNAUpBlY' // 模板ID
				let that = this;
				if (wx.requestSubscribeMessage) {
				      wx.requestSubscribeMessage({
				        tmplIds: [SUBSCRIBE_ID],
				        success(res) {
				          console.log(res);
				        },
				        fail(res) {
				          console.log(res);
				        },
						complete() {
							uni.navigateTo({
								url:"/pages/API/chat/chat?orderId="+that.order.orderMain.id
							})
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
			previewAppealedImage(index,index1) {
				let images = this.appealList[index].imageUrls
				uni.previewImage({
					current: index1,
					urls: images
				});
			},
			showActionProcess() {
				uni.showLoading()
				getAppealOrder(this.order.orderMain.id).then(res => {
					console.log(res);
					this.appealList = res.data
					this.visible1 = true
				}).catch(err => {
					uni.showToast({
						title:err,
						icon:'none',
						duration:2000
					})
				})
				.finally(res => {
					uni.hideLoading()
				})
			},
			toAppeal() {
				uni.navigateTo({
					url:"/pages/API/order/appeal/appeal?orderId=" + this.order.orderMain.id
				})
			},
			getPhone() {
				uni.showLoading()
				getPhoneOrder(this.order.orderMain.id).then(res => {
					console.log(res);
					uni.makePhoneCall({
						phoneNumber:res.data.phone
					})
				}).catch(err => {
					uni.showToast({
						title:err,
						icon:'none',
						duration:2000
					})
				}).finally(res => {
					uni.hideLoading()
				})
			},
			confirmSubmit() {
				uni.showLoading()
				getConfirmOrder(this.order.orderMain.id).then(res => {
					uni.showToast({
						title:"已完成",
						icon:'none',
						duration:2000
					})
					this.visibleConfirmDialog = false
					this.getDetail(this.order.orderMain.id)
				}).catch(err => {
					uni.showToast({
						title:err,
						icon:'none',
						duration:2000
					})
				}).finally(() => {
					uni.hideLoading()
				})
			},
			previewCompletionImage(index) {
				let imageList = this.order.completionImages.map(item => {
					return item.fileUrl;
				});
				uni.previewImage({
					current: index,
					urls: imageList
				});
			},
			previewImage(index) {
				let imageList = this.order.attachImages.map(item => {
					return item.fileUrl;
				});
				uni.previewImage({
					current: index,
					urls: imageList
				});
			},
			formatBytes(bytes, decimals = 2) {
				 if (bytes === 0) return '0 B';
				
				  const k = 1024; // 1KB = 1024B
				  const dm = decimals < 0 ? 0 : decimals; // 小数点位数
				  const sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']; // 各单位
				
				  const i = Math.floor(Math.log(bytes) / Math.log(k)); // 计算字节属于哪个单位
				  return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
			},
			viewFile(item) {
				console.log(item);
				     //下载简历
				    uni.downloadFile({
				      url: item.fileUrl, //仅为测试接口，并非真实的
				      success: function (res) {
				        var filePath = res.tempFilePath
				        console.log(filePath)
				        uni.openDocument({
				          filePath: filePath,
						  showMenu:true,
				          success: function (res) {
				            console.log("打开文档成功")
				            console.log(res);
				          },
						  fail(err) {
						  	uni.showToast({
						  		title: err,
						  		icon: 'none',
						  		duration:3000
						  	});
						  }
				        })
				      },
					  fail(err) {
					  	uni.showToast({
					  		title: err,
					  		icon: 'none',
							duration:3000
					  	});
					  }
				    })
			},
			collapseChange(modelValue, currName, status) {
				console.log(status);
				if(status) this.collapseText = '收起订单信息'
				else this.collapseText = '展开订单信息'
				
			},
			countdownHandleEnd(){
				console.log('结束及时');
				this.getDetail(this.order.orderMain.id)
			},
			copyOrderId(id) {
				uni.setClipboardData({
					data: id,
					success: function () {
						uni.showToast({
							title: '复制成功',
							icon: 'none'
						});
					},
					fail: function () {
						uni.showToast({
							title: '复制失败',
							icon: 'none'
						});
					}
				});
			},
			cancelSubmit() {
				this.cancelForm.orderId = this.order.orderMain.id
				postCancelOrder(this.cancelForm).then(res => {
					console.log(res);
					setTimeout(() => {
						this.getDetail(this.order.orderMain.id)
					},800)
				})
				.catch(err => {
					uni.showToast({
						title:err,
						icon:'none',
						duration:3000
					})
				})
			},
			payAgain() {
				let that = this
				getPayAgain(this.order.orderMain.id).then(res => {
					console.log(res);
					uni.requestPayment({
						timeStamp: res.data.timeStamp, // 时间戳
						nonceStr: res.data.nonceStr, // 随机字符串
						package: res.data.packageValue,
						signType: res.data.signType, // 签名算法
						paySign: res.data.paySign, // 签名
						success: function (res) {
							console.log('支付成功',res);
							// 业务逻辑。。。
							setTimeout(()=> {
								that.getDetail(res.data.orderId)
							},1500)
						},
						fail: function (err) {
							console.log('支付失败',err);
							uni.showToast({
								title:"取消支付",
								icon:"none",
								duration:2000
							})
						}
					});
				}).catch(err => {
					uni.showToast({
						title:err,
						icon:'none',
						duration:3000
					})
				})
			},
			back() {
				uni.navigateBack()
			},
			getDetail(id) {
				let that = this
				getDetailOrderUser(id).then(res => {
					console.log(res);
					let data = res.data
					
					let status = data.orderMain.status
					if(status == 0) data.orderMain.statusText = '待支付';
					if(status == 1) data.orderMain.statusText = '待接单';
					if(status == 2) data.orderMain.statusText = '待配送';
					if(status == 3) data.orderMain.statusText = '配送中';
					if(status == 4) data.orderMain.statusText = '已送达';
					if(status == 5) data.orderMain.statusText = '已取消';
					if(status == 10) data.orderMain.statusText = '已完成';
					if(status == 11) data.orderMain.statusText = '已关闭';
					let serviceType = data.orderMain.serviceType
					if(serviceType == 0) data.orderMain.serviceTypeText = '帮取送'
					if(serviceType == 1) data.orderMain.serviceTypeText = '帮买'
					if(serviceType == 2) data.orderMain.serviceTypeText  = '万能帮'
					data.orderMain.typeText = data.orderMain.serviceTypeText + '-' + data.orderMain.tag
					
					let attachFiles = data.attachFiles
					for (var i = 0; i < attachFiles.length; i++) {
						attachFiles[i].fileSize = that.formatBytes(attachFiles[i].fileSize)
						attachFiles[i].icon = that.getFileIcon(attachFiles[i].fileType)
					}
					data.attachFiles = attachFiles
					
					if(data.completionImages.length != 0) {
						let tmpList = this.convertCompletionImages(data.completionImages)
						this.defaultCompletionImageList = tmpList
					}
					
					if(data.orderMain.status == 0) {
						const createTimestamp = dayjs(data.orderMain.createTime, "YYYY-MM-DD HH:mm:ss").valueOf();
						const nowstamp = dayjs(Date.now(), "YYYY-MM-DD HH:mm:ss").valueOf();
						const stamp =this.config.payCancelTtl*60*1000 + 2000 - (nowstamp - createTimestamp)
						this.countdownEnd = Date.now() + stamp 
					}
					if(data.orderMain.status == 1) {
						const createTimestamp = dayjs(data.orderMain.createTime, "YYYY-MM-DD HH:mm:ss").valueOf();
						const nowstamp = dayjs(Date.now(), "YYYY-MM-DD HH:mm:ss").valueOf();
						const stamp =data.orderMain.autoCancelTtl*1000 + 2000 - (nowstamp - createTimestamp)
						this.countdownEnd = Date.now() + stamp 
					}
					if(data.orderMain.status == 4) {
						const createTimestamp = dayjs(data.orderMain.createTime, "YYYY-MM-DD HH:mm:ss").valueOf();
						const nowstamp = dayjs(Date.now(), "YYYY-MM-DD HH:mm:ss").valueOf();
						const stamp =this.config.autoCompleteTtl*60*1000*60 + 2000 - (nowstamp - createTimestamp)
						this.countdownEnd = Date.now() + stamp 
					}
					this.order = data
					
					that.skeletonLoading = false
					uni.stopPullDownRefresh()
					
					this.buildTimeLine()
					
				}).catch(err => {
					console.log(err);
					uni.showToast({
						title:err,
						icon:'none',
						duration:3000
					})
				})
			},
			getCountEnd() {
				
			},
			getFileIcon(extension) {
				// 定义文件类型及其图标
				  const fileTypes = {
				    documents: {
				      extensions: ['pdf'],
				      icon: 'pdf-icon.png' // PDF文件图标
				    },
				    word: {
				      extensions: ['doc', 'docx'],
				      icon: 'word-icon.png' // Word文件图标
				    },
				    excel: {
				      extensions: ['xls', 'xlsx'],
				      icon: 'excel-icon.png' // Excel文件图标
				    },
				    powerpoint: {
				      extensions: ['ppt', 'pptx'],
				      icon: 'ppt-icon.png' // PowerPoint文件图标
				    },
				    unknown: {
				      extensions: [],
				      icon: 'unknown-icon.png' // 未知文件类型图标
				    },
				}
				 // 遍历每种文件类型，检查后缀并返回相应的图标
				  for (let category in fileTypes) {
				    if (fileTypes[category].extensions.includes(extension)) {
				      return fileTypes[category].icon;
				    }
				  }
				  // 如果找不到匹配的后缀，返回未知文件类型
				  return fileTypes.unknown.icon
			},
			buildTimeLine() {
				let orderMain = this.order.orderMain
				let orderProgress = this.order.progress
				let orderPayment = this.order.orderPayment
				
				let createTime = orderMain.createTime //创建时间
				let paymentTime = orderPayment.paymentTime //付款时间
				let refundPendingTime = orderPayment.refundPendingTime //开始退款时间
				let refundTime = orderPayment.refundTime //退款到账时间
				let acceptedTime = orderProgress.acceptedTime //接单时间
				let deliveringTime = orderProgress.deliveringTime //配送时间
				let deliveredTime = orderProgress.deliveredTime //送达时间
				let completedTime = orderProgress.completedTime //完成时间
				let cancelTime = orderProgress.cancelTime //取消时间
				
				let times = [
					  { time: createTime, description: "订单已提交" },
					  { time: paymentTime, description: "支付成功" },
					  { time: refundPendingTime, description: "开始退款" },
					  { time: refundTime, description: "退款已到账" },
					  { time: acceptedTime, description: "跑腿员已接单" },
					  { time: deliveringTime, description: "配送中" },
					  { time: deliveredTime, description: "订单已送达" },
					  { time: completedTime, description: "订单已完成" },
					  { time: cancelTime, description: "订单已取消" },
				]
				console.log(times);
				// 过滤掉空时间并排序
				  this.orderSteps = times
					.filter(item => item.time) // 过滤掉时间为空的项
					.sort((a, b) => new Date(b.time.replace(/-/g, '/')) - new Date(a.time.replace(/-/g, '/'))); // 按时间升序排序
				console.log(this.orderSteps);
			},
			// 格式化时间
			formatTime(time) {
			  if (!time) return ''; // 如果时间为空，返回空字符串
			  return dayjs(time).format('MM月DD日 HH:mm');
			},
			convertCompletionImages(data) {
				let tmpList = []
				for (var i = 0; i < data.length; i++) {
					let tmpData = {}
					tmpData.uid = data[i].id
					tmpData.name = data[i].fileName
					tmpData.url = data[i].fileUrl
					tmpData.type = 'image'
					tmpData.status = 'success'
					tmpList.push(tmpData)
				}
				return tmpList
			},
 		}
	}
</script>
<style>
		@import url("/common/common.css");
	page {
		background-color: #e9f7fd;
	}
	/* 页面样式 */
	.order-tracking {
	  display: flex;
	  flex-direction: column;
	  padding: 20rpx;
	}
	.transparent {
		background-color: transparent;
	}
	 /* 价格明细容器 */
	    .priceBox {
	      background: #f8f9fc;
	      border-radius: 12px;
	      padding: 16px;
	    }
	    
	    /* 价格项样式 */
	    .priceChild {
	      display: flex;
	      align-items: center;
	      padding: 8px 0;
	      font-size: 15px;
	      line-height: 1.5;
	    }
	    
	    .priceChild:not(:last-child) {
	      margin-bottom: 8px;
	    }
	    
	    /* 左侧文字 */
	    .left {
	      color: #666;
	      flex-shrink: 0;
	    }
	    
	    /* 中间虚线 */
	    .fill {
	      flex: 1;
	      margin: 0 12px;
	      border-bottom: 1px dashed #ddd;
	    }
	    
	    /* 右侧金额 */
	    .right {
	      color: #333;
	      font-weight: 500;
	      flex-shrink: 0;
	    }
	    
	    /* 总金额特殊样式 */
	    .priceChild:first-child .right {
	      color: #ff6b6b;
	      font-size: 16px;
	      font-weight: 600;
	    }
	.step {
	  display: flex;
	  align-items: flex-start;
	  margin-bottom: 30rpx;
	}
	.stepBox {
		display: flex;
		    flex-direction: column;
		    align-items: center;
	}
	/* 图标和竖线容器 */
	.icon-container {
	  display: flex;
	  flex-direction: column;
	  align-items: center;
	  margin-right: 20rpx;
	}
	.stepImage {
		display: inline-block;
		width: 16px;
		height: 16px;
		border-radius: 50px;
	}
	/* 竖线 */
	.stepIcon {
	  width: 10px;
	  height: 10px;
	  background-color: #ccc;
	  border-radius: 50px;
	}
	.green {
		background-color: #00aa47;
	}
	/* 竖线 */
	.stepIconFake {
	  width: 10px;
	  height: 10px;
	  background-color: transparent;
	}
	.stepLine {
		width: 1px;
		height: 10px;
		background-color: grey;
		float: left;
	}
	
	/* 右侧内容 */
	.content {
	  display: flex;
	  flex-direction: column;
	}
	
	.status {
	  font-size: 28rpx;
	  font-weight: bold;
	  margin-bottom: 5rpx;
	}
	
	.time {
	  font-size: 24rpx;
	  color: #999;
	}

	
	
	
	
	.nut-uploader {
		justify-content: space-between;
	}
	
	.nut-uploader__upload {
		margin: 0 10px 10px 0;
	}
	.nut-popup {
		border-radius: 10px 10px 0 0 !important;
	}
	.nut-popup--center.round {
		border-radius: var(--nut-popup-border-radius, 20px) !important;
	}
	.nut-navbar--placeholder {
		background-color: #14bafa !important;
	}
	.box .tagMy {
		padding: 4px 20px;
		font-size: medium;
	}
	.splitLine {
		border-top: 1px solid #efefef;
		width: 90%;
		/* margin-left: 10%; */
		margin: 6px 5%;
	}
	.box .operaBox {
		display: flex;
		justify-content: flex-start;
		padding: 4px 20px;
		font-size: small;
	}
	.box .operaBox .oprea {
		display: flex;
	    width: 33.33%;
	    align-items: center;
		justify-content: center;
	}
	.box .operaBox .oprea .o {
	   padding-left: 6px;
	}
	.nut-collapse-item .nut-collapse__item-wrapper .nut-collapse__item-wrapper__content, .nut-collapse-item .nut-collapse__item-wrapper .nut-collapse__item-extraWrapper__extraRender, .nut-collapse-item .nut-collapse__item-extraWrapper .nut-collapse__item-wrapper__content, .nut-collapse-item .nut-collapse__item-extraWrapper .nut-collapse__item-extraWrapper__extraRender {
		padding: 0 !important;
	}
	.nut-cell__value {
		color: black !important;
	}
	.nut-navbar {
		box-shadow: none !important;
	}
	.nut-cell {
		padding: 10px 16px !important;
	}
	.nut-navbar {
		background-color: #14bafa !important;
	}
	.nut-icon {
		vertical-align: middle !important;
	}
	.nut-navbar__left, .nut-navbar__right {
		color: #fff !important;
	}
	.top {
		width: 100%;
		height: 80px;
		background-color: #14bafa;
		    color: white;
		    font-size: x-large;
		    font-weight: bold;
		    padding: 0 20px;
		    box-sizing: border-box;
		    line-height: 60px;
	}
	.box {
		    padding: 10px 0;
	}
	.box .t {
		font-size: large;
		    padding: 4px 20px;
	}
	.box .detail {
		    width: 90%;
			margin: 4px 5%;
		    padding: 14px;
		    box-sizing: border-box;
		    background-color: #f5f5f5;
		    border-radius: 6px;
		    font-size: small;
	}
	.box .userBox {
		padding: 4px 20px;
		    display: flex;
		    align-content: space-between;
		    /* vertical-align: middle; */
		    align-items: center;
	}
	.box .userBox image {
		width: 30px;
		height: 30px;
		border-radius: 50px;
	}
	.box .userBox .nickname {
		    flex: 1;
		    padding: 0 6px;
	}
	.line {
		width: 100%;
		height: 10px;
		background-color: #e9f7fd;
		    border-radius: 10px 10px 0 0;
		    transform: translateY(-9px);
	}
	
	
	.container_image {
	display: flex;
		flex-wrap: wrap;
		justify-content: space-between;
		padding: 10px;
	  
	}
	.container_image .image-box image {
		width: 100% !important;
		height: 100% !important;
	}
	.image-box {
		width: 100px;
		height: 100px;
		margin-top: 2px;
	}
	
	.image-item {
	    width: 100%;
	    height: 100%;
	}
	.nut-popup {
		max-height: 70% !important;
	}
	
</style>