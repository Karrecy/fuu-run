<template>
	<nut-navbar desc="编辑" fixed="true" placeholder="true">
		<template #content>
			<div class="h-full">
				<span @click="openDate('dateVisible')">{{dateText}}</span>
				<!-- <nut-tabs v-model="tabValue" @click="changeTab">
					<nut-tab-pane title="我的发布" />
					<nut-tab-pane title="我的接单" />
				</nut-tabs> -->
			</div>
		</template>

		<template #left>
			<nut-icon @click="back" name="left" /> 
		</template>
	</nut-navbar>


	<nut-menu>
		<template #icon>
			<nut-icon name="triangle-down" />
		</template>
		<nut-menu-item @change="chooseStatus" v-model="state.value1" :options="state.status" />
		<nut-menu-item @change="chooseOrderOrTake" v-model="state.value2" :options="state.orderOrTake" />
	</nut-menu>

	
<!-- 	<div v-show="total != 0" class="total">
		共 {{total}} 个订单
	</div> -->
	<div :style="'width: 100%;height: 100%;padding-top:' + (safeAreaInsets.top + 10) + 'px;padding-bottom:'+(safeAreaInsets.bottom + 10) +'px;'">
		<div v-show="skeletonLoading">
			<nut-skeleton width="100%" height="24px" title animated row="3"> </nut-skeleton>
			<nut-skeleton width="100%" height="24px" title animated row="3"> </nut-skeleton>
			<nut-skeleton width="100%" height="24px" title animated row="3"> </nut-skeleton>
			<nut-skeleton width="100%" height="24px" title animated row="3"> </nut-skeleton>
			<nut-skeleton width="100%" height="24px" title animated row="3"> </nut-skeleton>
		</div>
		
		<div class="box"  v-for="(item, index) in rows" :key="index">
			<div class="orderTop">
				<nut-tag v-if="item.serviceType == 0" custom-color="#e9f7ff" text-color="#248fce">帮取送</nut-tag>
				<nut-tag v-if="item.serviceType == 1" custom-color="#f9e2c0" text-color="#ef940d">帮买</nut-tag>
				<nut-tag v-if="item.serviceType == 2" custom-color="#f9dfff" text-color="#e077ec">万能帮</nut-tag>
				<span class="tag">{{item.tag}}</span>
				<span v-if="item.status == 0" class="status">待支付</span>
				<span v-if="item.status == 1" class="status">待接单</span>
				<span v-if="item.status == 2" class="status">待配送</span>
				<span v-if="item.status == 3" class="status">配送中</span>
				<span v-if="item.status == 4" class="status">已送达</span>
				<span v-if="item.status == 5" class="status">已取消</span>
				<span v-if="item.status == 10" class="status">已完成</span>
				<span v-if="item.status == 11" class="status">已申诉</span>
			</div>
			<div class="orderDesc">
				{{item.detail}}
			</div>
			<div class="orderAddress">
				<div class="left">
					<nut-cell v-if="item.startAddress != null" :title="item.startAddress.title+''+item.startAddress.detail">
						<template #icon>
							<image class="nut-icon" src="/static/icons/start.png"></image>
						</template>
					</nut-cell>
					<nut-cell :title="item.endAddress.title+''+item.endAddress.detail">
						<template #icon>
							<image class="nut-icon" src="/static/icons/end.png"></image>
						</template>
					</nut-cell>
				</div>
				<div class="right">
					￥{{item.totalAmount}}
				</div>
		
			</div>
			<div class="orderBot">
				<span class="orderTime">{{item.timeAgo}}</span>
				<nut-button @click="toDetail(item.id)" type="info">查看详情</nut-button>
			</div>
		</div>
		<div v-show="total == 0 && skeletonLoading == false">
			<nut-empty description="暂无数据"></nut-empty>
		</div>
		<div v-show="!hasMore && total != 0 && skeletonLoading == false" >
			<nut-divider dashed>到底啦~</nut-divider>
		</div>
	</div>
	
	
	<!-- <nut-safe-area position="bottom" /> -->
	<nut-calendar  v-model:visible="dateVisible" :default-value="date" type="range" :start-date="startDate"
		:end-date="endDate" @closed="closeDate('dateVisible')" @choose="setChooseDate">
	</nut-calendar>
</template>

<script>
	import {
		markRaw
	} from "vue"
	import dayjs from 'dayjs';
	import relativeTime from 'dayjs/plugin/relativeTime'; // 引入 relativeTime 插件
	import 'dayjs/locale/zh-cn';  // 导入中文语言包
	// 扩展 dayjs 插件
	dayjs.extend(relativeTime);
	// 设置为中文
	dayjs.locale('zh-cn');
	import {
		getListOrderUser
	} from "@/request/apis/order.js"
	const {safeAreaInsets} = uni.getSystemInfoSync()
	export default {
		data() {
			return {
				safeAreaInsets:safeAreaInsets,
				skeletonLoading:true,
				dateText:'选择日期',
				date: ['2019-12-23', '2019-12-26'],
				startDate: '',
				endDate: '',
				dateVisible: false,
				scrollTop: 0,
				state: {
					status: [{
							text: '全部订单',
							value: null
						},
						{
							text: '待支付',
							value: 0
						},
						{
							text: '待接单',
							value: 1
						},
						{
							text: '待配送',
							value: 2
						},
						{
							text: '配送中',
							value: 3
						},
						{
							text: '已送达',
							value: 4
						},
						{
							text: '已取消',
							value: 5
						},
						{
							text: '已完成',
							value: 10
						},
						{
							text: '已申诉',
							value: 11
						}
					],
					orderOrTake: [{
							text: '我的发布',
							value: 0
						},
						{
							text: '我的接单',
							value: 1
						}
					],
					value1: null,
					value2: 0
				},
				currSchool: null,
				tabValue: 0,
				title: 'Hello',
				queryParams: {
					schoolId: null,
					status: null,
					orderOrTake: 0,
					beginTime: null,
					endTime: null,
					pageSize: 20,
					pageNum: 1
				},
				rows: [],
				total: 0,
				hasMore: true,
			}
		},

		onLoad() {
			console.log(safeAreaInsets);
			
			this.skeletonLoading = true
			this.initDate()
			this.initData()
			this.getList()
		},
		onReachBottom() {
			if (this.hasMore) {
				this.getList(); // 触底时加载更多
			}
		},
		onPullDownRefresh() {
			// this.resizePage()
			// this.getList()
		},
		methods: {
			toDetail(id) {
				if(this.state.value2 == 1) {
					uni.navigateTo({
						url:"/pages/API/order/runner/runner?orderId=" + id
					})
				}
				else {
					uni.navigateTo({
						url:"/pages/API/order/detail/detail?id=" + id
					})
				}
				
			},
			back() {
				uni.navigateBack()
			},
			chooseOrderOrTake(e) {
				this.queryParams.orderOrTake = e
				this.resizePage()
				this.skeletonLoading = true
				this.getList()
			},
			chooseStatus(e) {
				console.log(e); 
				this.queryParams.status = e
				this.resizePage()
				this.skeletonLoading = true
				this.getList()
			},
			setChooseDate(param) {
				this.date = [...[param[0][3], param[1][3]]];
			},
			openDate() {
				this.dateVisible = true
			},
			closeDate() {
				this.dateVisible = false
				console.log(this.date);
				this.queryParams.beginTime = this.date[0]+' 00:00:00'
				this.queryParams.endTime = this.date[1]+' 00:00:00'
				let formattedEndTime = this.date[1].split('-').slice(1).join('-');
				this.dateText = this.date[0] + '至' + formattedEndTime
				this.resizePage()
				console.log(222);
				this.getList()
			},
		
			changeTab(e) {
				let key = e.paneKey
				if (key == '0') this.queryParams.orderOrTake = 0
				if (key == '1') this.queryParams.orderOrTake = 1
				this.resizePage()
				console.log(111);
				this.getList()
			},
			initData() {
				this.currSchool = this.$store.state.currSchool;
				this.queryParams.schoolId = this.currSchool.id
				let orderDefaultShow = uni.getStorageSync("orderDefaultShow")
				if(orderDefaultShow == '我的发布') {
					this.state.value2 = 0
					this.queryParams.orderOrTake = 0
				}
				if(orderDefaultShow == '我的接单') {
					this.state.value2 = 1
					this.queryParams.orderOrTake = 1
				}
			},
			initDate() {
				// 获取当前日期
				const currentDate = new Date();
				// 计算两年前的日期
				const startDate = new Date();
				startDate.setFullYear(currentDate.getFullYear() - 2);
				// 计算明天的日期
				const endDate = new Date();
				endDate.setDate(currentDate.getDate() + 1);
				// 格式化日期为 yyyy-MM-dd
				const formatDate = (date) => {
					const year = date.getFullYear();
					const month = String(date.getMonth() + 1).padStart(2, '0'); // 补齐两位
					const day = String(date.getDate()).padStart(2, '0'); // 补齐两位
					return `${year}-${month}-${day}`;
				};
				this.startDate = formatDate(startDate)
				this.endDate = formatDate(endDate)

				this.date[0] = this.getDate(-7)
				this.date[1] = this.getDate(0)
				console.log(this.date);
			},
			getDate(daysOffset) {
				const date = new Date();
				date.setDate(date.getDate() + daysOffset);
				return date.toISOString().split("T")[0]; // 格式化为 yyyy-MM-dd
			},
			resizePage() {
				this.queryParams.pageNum = 1;
				this.queryParams.pageSize = 20;
				this.rows = [];
				this.total = 0;
				this.hasMore = true
			},
			getList() {
				getListOrderUser(this.queryParams).then(res => {
					console.log(res);
					this.total = res.total
					let data = res.rows
					for (var i = 0; i < data.length; i++) {
						data[i].timeAgo = dayjs(data[i].createTime).fromNow()
					}
					this.rows.push(...data)
					this.queryParams.pageNum += 1;
					this.hasMore = data.length > 0
					this.skeletonLoading = false
					uni.stopPullDownRefresh();
				})
				.catch(err => {
					uni.showToast({
						title:err,
						icon:"none",
						duration:3000
					})
				})
			},
		}
	}
</script>

<style>
	@import url("/common/common.css");

	page {
		background-color: #f8f8f8;
	}	
	.nut-navbar {
		box-shadow: none !important;
	}
	.nut-menu .nut-menu__bar {
		box-shadow: none !important;
	}
	.total {
		width: 100%;
		text-align: center;
		font-size: small;
		    color: gray;
	}

	.nut-cell__value {
		flex: 2 !important;
	}

	.nut-menu {
		position: fixed;
		width: 100%;
		z-index: 999;
	}

	.nut-tabs.horizontal .nut-sticky__box>.nut-tabs__titles .nut-tabs__titles-item,
	.nut-tabs.horizontal>.nut-tabs__titles .nut-tabs__titles-item {
		min-width: 76px !important;
	}

	.box {
		padding: 5px 0;
	}

	.nut-button {
		height: 32px !important;
	}

	.nut-tag {
		border-radius: 2px !important;
		margin-bottom: 0 !important;
		margin-right: 0 !important;
		padding: 0px 6px !important;
	}

	.nut-tab-pane {
		padding: 0 !important;
	}

	.nut-cell__icon image {
		width: 16px;
		height: 16px;

	}

	.nut-cell {
		padding: 6px 16px !important;
	}
	.nut-skeleton {
		margin-top: 20px;
	}
	.nut-popup {
		border-radius:0 0 10px 10px !important;
	}
</style>