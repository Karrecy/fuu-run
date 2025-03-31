<template>
	 <nut-navbar title="校区选择" fixed="true" placeholder="true">
	    <template #left>
			<nut-icon @click="naviBack" name="left" />
	    </template>
	  </nut-navbar>
	  <template>
		<nut-searchbar @change="searchName" v-model="queryParams.name">
		    <template #leftin>
		      <nut-icon name="search2" />
		    </template>
		</nut-searchbar>
	  </template>
	  
	  <template>
		  <nut-cell 
				@click="selectSchool(item)"
				v-for="(item, index) in rows" 
		        :key="index" 
		        :title="item.name">
			<template #icon>
			  <image class="nut-icon" :src="item.logo"></image>
			</template>
		  </nut-cell>
	  </template>
	<div v-show="total == 0">
		<nut-empty description="暂无数据"></nut-empty>
	</div>
	<div v-show="!hasMore && total != 0" >
		<nut-divider dashed>到底啦~</nut-divider>
	</div>
	
	  <nut-safe-area position="bottom" />
</template>
<script setup>

</script>
<script>
	import { toRaw } from "vue";
import {listSchool} from "@/request/apis/school.js"
import { getWeatherByAdcode } from "@/request/apis/amap.js"
	export default {
		data() {
			return {
				title: 'Hello',
				// 查询参数
				queryParams: {
					pageNum: 1,
					pageSize: 20,
					name: undefined,
				},
				  // 总条数
				total: 0,
				rows:[],
				hasMore:true
			}
		},
		onLoad() {
			this.getList()
		},
		onReachBottom() {
			console.log("214");
			if (this.hasMore) {
			  this.getList(); // 触底时加载更多
			}
		},
		methods: {
			selectSchool(e){
				let school = toRaw(e);
				getWeatherByAdcode(school.adcode).then(res => {
					console.log(res);
					this.$store.commit('setWeather',res.data)
				})
				uni.setStorageSync("currentSchool",school);
				this.$store.commit('setSchool', school);
				uni.reLaunch({
					url:"/pages/tabBar/index/index"
				})
				// uni.navigateBack({
					
				// 	success: function () {
				// 		const pages = getCurrentPages(); //获取当前页面栈
				// 		const prevPage = pages[pages.length - 1]; //获取上一个页面实例对象
				// 		prevPage.onLoad()
				//     }
				// });
			},
			searchName(){
				console.log(111);
				this.resizePage();
				this.getList();
			},
			resizePage(){
				this.queryParams.pageNum = 1;
				this.queryParams.pageSize = 20;
				this.rows = [];
				this.total = 0;
				this.hasMore = true
			},
			naviBack(){
				uni.navigateBack();
			},
			getList() {
				listSchool(this.queryParams).then(res => {
					console.log(res);
					this.total = res.total
					this.rows.push(...res.rows)
					this.queryParams.pageNum += 1;
					this.hasMore = res.rows.length > 0
				})
			}
		},
		
	}
</script>
<style>
/* 页面基础样式 */
page {
  background: #f7f8fa;
  min-height: 100vh;
  box-sizing: border-box;
  width: 100%;
  overflow-x: hidden;
}

/* 导航栏样式 */
:deep(.nut-navbar) {
  background: rgba(255, 255, 255, 0.98) !important;
  box-shadow: 0 1px 10px rgba(0, 0, 0, 0.05);
  backdrop-filter: blur(10px);
  width: 100%;
}

/* 搜索栏容器 */
.nut-searchbar {
  padding: 12px 16px !important;
  background: transparent !important;
  width: 100%;
  box-sizing: border-box;
}

/* 搜索框样式 */
.nut-searchbar__search-input {
  height: 40px !important;
  background: #fff !important;
  border-radius: 20px !important;
  padding: 0 16px !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  box-sizing: border-box;
}

/* 学校列表容器 */
.school-list-container {
  width: 100%;
  padding: 0 12px;
  box-sizing: border-box;
}

/* 学校列表项 */
.nut-cell {
  margin: 8px 0 !important;
  padding: 14px !important;
  border-radius: 12px !important;
  background: #fff !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03);
  transition: all 0.3s ease;
  width: 100% !important;
  box-sizing: border-box;
}

.nut-cell:active {
  transform: scale(0.98);
  background: #f8f9fa !important;
}

/* 学校图标 */
.nut-icon {
  width: 40px !important;
  height: 40px !important;
  border-radius: 10px !important;
  margin-right: 12px !important;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
  object-fit: cover;
  flex-shrink: 0;
}

/* 学校名称 */
.nut-cell__title {
  font-size: 15px !important;
  font-weight: 500 !important;
  color: #333 !important;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 空状态和底部样式 */
.nut-empty,
.nut-divider {
  width: 100%;
  box-sizing: border-box;
  padding: 0 16px;
}

/* 文本省略样式 */
.ellipsisc {
  max-width: 120px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 标题样式 */
.title {
  padding-left: 8px !important;
}

/* 修复搜索图标位置 */
.nut-icon[name="search2"] {
  font-size: 16px !important;
  color: #999 !important;
  margin-right: 8px !important;
}
</style>
<!-- <style>
.nut-icon{
	vertical-align: middle;
}
.ellipsisc {
      display: inline-block;
      width: 120px; /* 设置容器宽度 */
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
.title {
	padding-left: 5px !important;
}
</style -->>
