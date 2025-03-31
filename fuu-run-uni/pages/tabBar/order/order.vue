<template>


	<nut-tabs v-model="tabValue" @change="tabChange">
		<nut-tab-pane title="全部订单">
			<div v-show="skeletonLoading">
				<nut-skeleton  v-for="(item, index) in 6" :key="index" width="100%" height="24px" title animated row="2"> </nut-skeleton>
			</div>
			<div v-show="!skeletonLoading" class="box"  v-for="(item, index) in rows" :key="index">
				<div class="orderTop">
					<nut-tag v-if="item.serviceType == 0" custom-color="#e9f7ff" text-color="#248fce">帮取送</nut-tag>
					<nut-tag v-if="item.serviceType == 1" custom-color="#f9e2c0" text-color="#ef940d">帮买</nut-tag>
					<nut-tag v-if="item.serviceType == 2" custom-color="#f9dfff" text-color="#e077ec">万能帮</nut-tag>
					<span class="tag">{{item.tag}}</span>
					<span v-if="item.status == 0" class="status">待支付</span>
					<span v-if="item.status == 1" class="status">待接单</span>
					<span v-if="item.status == 2" class="status">进行中</span>
					<span v-if="item.status == 3" class="status">已送达</span>
					<span v-if="item.status == 4" class="status">已取消</span>
					<span v-if="item.status == 10" class="status">已完成</span>
					<span v-if="item.status == 11" class="status">已申诉</span>
				</div>
				<div class="orderAddress">
					<div class="left">
						<nut-cell v-if="item.startAddress != null"
							:title="item.startAddress.title+''+item.startAddress.detail">
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
					<nut-button  @click="toOrderDetailRunner(item)" type="info">查看详情</nut-button>
				</div>
			</div>
			<div v-show="total == 0 && !skeletonLoading">
				<nut-empty description="暂无数据"></nut-empty>
			</div>
			<div v-show="!hasMore && total != 0 && !skeletonLoading" >
				<nut-divider dashed>到底啦~</nut-divider>
			</div>
	
			<nut-safe-area position="bottom" />
		</nut-tab-pane>
		<nut-tab-pane title="待接单">
			<div v-show="skeletonLoading">
				<nut-skeleton  v-for="(item, index) in 6" :key="index" width="100%" height="24px" title animated row="2"> </nut-skeleton>
			</div>
			<div v-show="!skeletonLoading" class="box"  v-for="(item, index) in rows" :key="index">
				<div class="orderTop">
					<nut-tag v-if="item.serviceType == 0" custom-color="#e9f7ff" text-color="#248fce">帮取送</nut-tag>
					<nut-tag v-if="item.serviceType == 1" custom-color="#f9e2c0" text-color="#ef940d">帮买</nut-tag>
					<nut-tag v-if="item.serviceType == 2" custom-color="#f9dfff" text-color="#e077ec">万能帮</nut-tag>
					<span class="tag">{{item.tag}}</span>
					<span v-if="item.status == 0" class="status">待支付</span>
					<span v-if="item.status == 1" class="status">待接单</span>
					<span v-if="item.status == 2" class="status">进行中</span>
					<span v-if="item.status == 3" class="status">已送达</span>
					<span v-if="item.status == 4" class="status">已取消</span>
					<span v-if="item.status == 10" class="status">已完成</span>
					<span v-if="item.status == 11" class="status">已申诉</span>
				</div>
				<div class="orderAddress">
					<div class="left">
						<nut-cell v-if="item.startAddress != null"
							:title="item.startAddress.title+''+item.startAddress.detail">
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
					<span class="orderTime">1分钟前</span>
					<nut-button @click="toOrderDetailRunner(item)" type="info">查看详情</nut-button>
				</div>
			</div>
			<div v-show="total == 0 && !skeletonLoading">
				<nut-empty description="暂无数据"></nut-empty>
			</div>
			<div v-show="!hasMore && total != 0 && !skeletonLoading" >
				<nut-divider dashed>到底啦~</nut-divider>
			</div>
			
			<nut-safe-area position="bottom" />
		</nut-tab-pane>

	</nut-tabs>
</template>

<script>
	import dayjs from 'dayjs';
	import relativeTime from 'dayjs/plugin/relativeTime'; // 引入 relativeTime 插件
	import 'dayjs/locale/zh-cn';  // 导入中文语言包
	// 扩展 dayjs 插件
	dayjs.extend(relativeTime);
	// 设置为中文
	dayjs.locale('zh-cn');
	import {
		getListOrderHall
	} from "@/request/apis/order.js"
	export default {
		data() {
			return {
				skeletonLoading:true,
				currSchool: null,
				tabValue: 0,
				title: 'Hello',
				queryParams: {
					schoolId: null,
					status: null,
					pageSize: 20,
					pageNum: 1
				},
				rows: [],
				total: 0,
				hasMore: true
			}
		},
		onLoad() {
			this.skeletonLoading = true
			this.initData()
			this.getList()
		},
		onReachBottom() {
			if (this.hasMore) {
				this.getList(); // 触底时加载更多
			}
		},
		onPullDownRefresh() {
			this.skeletonLoading = true
			this.resizePage()
			this.getList()
		},
		methods: {
			toOrderDetailRunner(e) {
				console.log(e);
				uni.navigateTo({
					url:"/pages/API/order/runner/runner?orderId=" + e.id
				})
			},
			tabChange(e) {
				let key = e.paneKey
				if(key == '0') this.queryParams.status = null
				if(key == '1') this.queryParams.status = 1
				this.resizePage()
				this.skeletonLoading = true
				this.getList()
			},
			initData() {
				this.currSchool = this.$store.state.currSchool;
				this.queryParams.schoolId = this.currSchool.id
			},
			resizePage() {
				this.queryParams.pageNum = 1;
				this.queryParams.pageSize = 20;
				this.rows = [];
				this.total = 0;
				this.hasMore = true
			},
			getList() {
				getListOrderHall(this.queryParams).then(res => {
					console.log(res);
					let data = res.rows
					for (var i = 0; i < data.length; i++) {
						data[i].timeAgo = dayjs(data[i].createTime).fromNow()
					}
					res.rows = data
					this.total = res.total
					this.rows.push(...res.rows)
					this.queryParams.pageNum += 1;
					this.hasMore = res.rows.length > 0
					
					this.skeletonLoading = false
					uni.stopPullDownRefresh();
				}).catch(err => {
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
</style>