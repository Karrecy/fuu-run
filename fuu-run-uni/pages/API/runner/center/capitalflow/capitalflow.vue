<template>
  <view class="container">
    <!-- 筛选区域 -->
    <view class="filter-section">
      <nut-cell title="日期范围" @click="dateVisible = true" :desc="dateText">
      </nut-cell>
      <nut-cell title="收益类型" @click="showTypeSelector = true" :desc="selectedTypeName">
      </nut-cell>
    </view>

    <!-- 收支列表 -->
    <scroll-view 
      class="flow-list" 
      scroll-y 
      @scrolltolower="handleScrollToLower"
      :style="{ height: scrollHeight + 'px' }"
    >
	<div v-show="skeletonLoading">
		<nut-skeleton  v-for="(item, index) in 6" :key="index" width="100%" height="24px" title animated row="2"> </nut-skeleton>
	</div>
      <view  v-show="!skeletonLoading" class="flow-item" v-for="(item, index) in rows" :key="index">
        <view class="left">
          <text class="title">{{ getFlowTitle(item) }}</text>
          <text class="time">{{ formatTime(item.createTime) }}</text>
        </view>
        <view class="right">
          <text :class="['amount', getAmountClass(item)]">
            {{ getAmountText(item) }}
          </text>
        </view>
      </view>
      
     <div v-show="total == 0 && !skeletonLoading">
     	<nut-empty description="暂无数据"></nut-empty>
     </div>
     <div v-show="!hasMore && total != 0 && !skeletonLoading" >
     	<nut-divider dashed>到底啦~</nut-divider>
     </div>
    </scroll-view>
<nut-safe-area position="bottom" />
    <!-- 日期选择器 -->
	<nut-calendar  v-model:visible="dateVisible" :default-value="date" type="range" :start-date="startDate"
		:end-date="endDate" @closed="closeDate('dateVisible')" @choose="setChooseDate">
	</nut-calendar>

    <!-- 类型选择器 -->
    <nut-popup position="bottom" v-model:visible="showTypeSelector">
      <nut-picker
        :columns="typeOptions"
        @confirm="onTypeConfirm"
        @cancel="showTypeSelector = false"
      />
    </nut-popup>
  </view>
</template>

<script>
	import {getListCapital} from '@/request/apis/payment.js'
export default {
  data() {
    return {
		queryParams: {
			type: null,
			beginTime: null,
			endTime: null,
			pageSize: 20,
			pageNum: 1
		},
		rows: [],
		total: 0,
		hasMore: true,
      showTypeSelector: false,
      skeletonLoading:true,
      dateText:'一周内',
      date: ['2019-12-23', '2019-12-26'],
      startDate: '',
      endDate: '',
      dateVisible: false,
      selectedTypeName:'全部类型',
      typeOptions: [[
        { text: '全部类型', value: null },
        { text: '订单收入', value: '0' },
        { text: '提现支出', value: '1' }
      ]],
      scrollHeight: 0, // 添加滚动区域高度
    }
  },
  onLoad() {
  	this.skeletonLoading = true
  	this.initDate()
  	this.initData()
  	this.getList()
    this.initScrollHeight() // 初始化滚动区域高度
  },
  methods: {
	  getList() {
		let temp = this.queryParams
		temp.params = JSON.stringify(temp.params)
	  	getListCapital(temp).then(res => {
	  		console.log(res);
	  		this.total = res.total
	  		let data = res.rows
	  		this.rows.push(...data)
	  		this.queryParams.pageNum += 1;
	  		this.hasMore = data.length === this.queryParams.pageSize
	  		this.skeletonLoading = false
	  		uni.stopPullDownRefresh();
	  	})
	  	.catch(err => {
	  		this.skeletonLoading = false
	  		uni.showToast({
	  			title:err,
	  			icon:"none",
	  			duration:3000
	  		})
	  	})
	  },
	  initData() {
	  	this.currSchool = this.$store.state.currSchool;
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
	  	this.queryParams.startDate = this.getDate(-7)
	  	this.queryParams.endDate = this.getDate(0)
		
		this.date[0] = this.getDate(-7)
		this.date[1] = this.getDate(0)
		
		this.startDate = formatDate(startDate)
		this.endDate = formatDate(endDate)
	  },
	  
    setChooseDate(param) {
    	this.date = [...[param[0][3], param[1][3]]];
    },
    openDate() {
    	this.dateVisible = true
    },
    closeDate() {
    	this.dateVisible = false
    	this.queryParams.beginTime = this.date[0]+' 00:00:00'
    	this.queryParams.endTime = this.date[1]+' 00:00:00'
    	let formattedEndTime = this.date[1].split('-').slice(1).join('-');
    	this.dateText = this.date[0] + '至' + formattedEndTime
    	this.resizePage()
    	this.getList()
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
    onTypeConfirm({ selectedValue, selectedOptions }) {
      this.selectedType = selectedValue[0]
      this.selectedTypeName = selectedOptions[0].text
      this.showTypeSelector = false
	  this.queryParams.type = selectedValue[0]
      // 这里可以调用接口刷新列表
	  this.resizePage()
	  this.getList()
    },
    // 初始化滚动区域高度
    initScrollHeight() {
      const systemInfo = uni.getSystemInfoSync()
      // 减去筛选区域高度(120rpx)和顶部状态栏高度，预留底部安全区域
      this.scrollHeight = systemInfo.windowHeight - uni.upx2px(120) - systemInfo.statusBarHeight
    },
    
    // 处理滚动到底部
    handleScrollToLower() {
      if (this.hasMore && !this.skeletonLoading) {
        this.getList()
      }
    },
    // 获取流水标题
    getFlowTitle(item) {
      if (item.type === 0) {
        return '订单收益'
      } else if (item.type === 1) {
        return '跑腿提现'
      } else {
        return '其他'
      }
    },

    // 获取金额样式类
    getAmountClass(item) {
      // 订单收益显示为收入，提现显示为支出
      return item.type === 0 ? 'income' : 'expense'
    },

    // 获取金额显示文本
    getAmountText(item) {
      let amount = 0
      if (item.type === 0) {
        // 订单收益
        amount = item.profitRunner || 0
        return `+${amount}`
      } else if (item.type === 1) {
        // 提现
        amount = item.profitRunner || 0
        return `-${amount}`
      }
      return `${amount}`
    },

    // 格式化时间
    formatTime(time) {
      if (!time) return ''
      return time.replace('T', ' ').split('.')[0]
    }
  }
}
</script>
<style lang="scss" scoped>
.container {
  min-height: 100vh;
  background-color: #f8f9fc;
  padding: 12px;
  box-sizing: border-box;
}

/* 筛选区域 */
.filter-section {
  background: #fff;
  border-radius: 16px;
  margin-bottom: 16px;
  box-shadow: 0 4px 16px rgba(149, 157, 165, 0.1);
  overflow: hidden;
}

/* Cell样式 */
.nut-cell {
  padding: 16px !important;
  
  &::after {
    left: 16px !important;
    right: 16px !important;
  }
}

.nut-cell__title {
  font-size: 15px !important;
  color: #333 !important;
}

.nut-cell__value {
  font-size: 14px !important;
  color: #666 !important;
}

/* 流水列表 */
.flow-list {
  background: #fff;
  border-radius: 16px;
  padding: 8px 16px;
  box-sizing: border-box;
  box-shadow: 0 4px 16px rgba(149, 157, 165, 0.1);
}

.flow-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  position: relative;
  
  &::after {
    content: '';
    position: absolute;
    left: 0;
    right: 0;
    bottom: 0;
    height: 1px;
    background: rgba(0, 0, 0, 0.03);
  }
  
  &:last-child::after {
    display: none;
  }
  
  .left {
    display: flex;
    flex-direction: column;
    
    .title {
      font-size: 15px;
      color: #333;
      font-weight: 500;
      margin-bottom: 6px;
    }
    
    .time {
      font-size: 13px;
      color: #999;
    }
  }
  
  .right {
    .amount {
      font-size: 16px;
      font-weight: 600;
      
      &.income {
        background: linear-gradient(45deg, #ff9a9e 0%, #fad0c4 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
      }
      
      &.expense {
        background: linear-gradient(45deg, #a18cd1 0%, #fbc2eb 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
      }
    }
  }
}

/* 骨架屏样式 */
.nut-skeleton {
  margin-bottom: 12px !important;
  border-radius: 8px !important;
  overflow: hidden !important;
}

/* 空状态样式 */
.nut-empty {
  padding: 32px 0 !important;
}

.nut-empty__description {
  color: #999 !important;
  font-size: 14px !important;
}

/* 底部分割线 */
.nut-divider {
  margin: 16px 0 !important;
  color: #999 !important;
  font-size: 13px !important;
}

/* 日期选择器样式 */
.nut-calendar {
  border-radius: 16px 16px 0 0 !important;
  overflow: hidden !important;
}

.nut-calendar-header {
  background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%) !important;
}

.nut-calendar-header__title {
  color: #fff !important;
}

/* 类型选择器样式 */
.nut-picker {
  border-radius: 16px 16px 0 0 !important;
  overflow: hidden !important;
}

.nut-picker-title {
  font-size: 16px !important;
  color: #333 !important;
}

.nut-picker-item {
  font-size: 15px !important;
}

.nut-picker-item_active {
  color: #a18cd1 !important;
}

/* 滚动区域优化 */
.flow-list::-webkit-scrollbar {
  display: none;
}
</style>