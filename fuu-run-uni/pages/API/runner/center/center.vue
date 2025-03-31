<template>
  <view class="container">
    <!-- 顶部卡片 -->
    <view class="top-card">
      <view class="user-info">
        <view class="credit-score">
          <text class="score">{{ userInfo.userWx.creditScore }}</text>
          <text class="label">信用分</text>
        </view>
      </view>
      
      <!-- 收益统计 -->
 <!--     <view class="earnings-row">
        <view class="earnings-item">
          <text class="amount">¥{{ todayEarnings }}</text>
          <text class="label">今日赏金</text>
        </view>
        <view class="earnings-item">
          <text class="amount">¥{{ totalEarnings }}</text>
          <text class="label">总赏金</text>
        </view>
      </view> -->
      
      <!-- 接单统计 -->
  <!--    <view class="orders-row">
        <view class="orders-item">
          <text class="number">{{ todayOrders }}</text>
          <text class="label">今日接单</text>
        </view>
        <view class="orders-item">
          <text class="number">{{ totalOrders }}</text>
          <text class="label">总接单量</text>
        </view>
      </view> -->
    </view>
    <view class="top-card" style="margin-top: 20px;">
    	  <view class="user-info">
    	    <view class="credit-score">
    	      <text style="font-size: large;" class="label">钱包</text>
    	    </view>
    	  </view>
    	  <!-- 收益统计 -->
    	    <view class="earnings-row">
    	      <view class="earnings-item">
    	        <text class="amount">¥{{ wallet.balance }}</text>
    	        <text class="label">余额</text>
    	      </view>
    	      <view class="earnings-item">
    	        <text class="amount">¥{{ wallet.withdrawn }}</text>
    	        <text class="label">已提现</text>
    	      </view>
    	    </view>
    	</view>
    <!-- 功能按钮 -->
    <view class="action-buttons">
      <button class="btn withdraw" @tap="handleWithdraw">提现</button>
      <button class="btn details" @tap="handleDetails">资金明细</button>
    </view>
  </view>
  
  
</template>

<script>
	import {getWallet} from '@/request/apis/payment.js'
	import {getInfo} from "@/request/apis/login.js"
export default {
  data() {
    return {
      creditScore: 100,
      todayEarnings: '0.00',
      totalEarnings: '0.00',
      todayOrders: 0,
      totalOrders: 0,
	  wallet:{},
	  userInfo:{
		  userWx:{
			  creditScore:0
		  }
	  }
    }
  },
  onLoad() {
	  this.getInfo()
  	this.getWalletInit()
  },
  methods: {
	  getWalletInit() {
		  getWallet().then(res => {
			  console.log(res);
			  this.wallet = res.data
		  })
		  
	  },
	  getInfo() {
	  	let that = this
	  	getInfo().then(res => {
	  		let info = res
	  		this.$store.commit('login', info.data.user);
	  		this.$store.commit('setConfig', info.data.config);
	  		this.userInfo = this.$store.state.userInfo;
	  	}).catch(err => {
	  		this.showDanger(err)
	  	})
	  },
    handleWithdraw() {
      // 处理提现逻辑
	  uni.navigateTo({
	  	url:"/pages/API/runner/center/recode/recode?balance="+this.wallet.balance
	  })
    },
    handleDetails() {
      // 处理查看资金明细逻辑
	  uni.navigateTo({
	  	url:"/pages/API/runner/center/capitalflow/capitalflow"
	  })
    }
  }
}
</script>

<style lang="scss" scoped>
.container {
  padding: 20px;
  background-color: #f8f9fc;
  min-height: 100vh;
  box-sizing: border-box;
}

.top-card {
  background: linear-gradient(135deg, #fff 0%, #f8f9ff 100%);
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 8px 24px rgba(149, 157, 165, 0.1);
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: linear-gradient(
      45deg,
      rgba(255, 255, 255, 0.1) 25%,
      transparent 25%,
      transparent 50%,
      rgba(255, 255, 255, 0.1) 50%,
      rgba(255, 255, 255, 0.1) 75%,
      transparent 75%,
      transparent
    );
    background-size: 20px 20px;
    opacity: 0.5;
  }
  
  .user-info {
    display: flex;
    justify-content: center;
    margin-bottom: 24px;
    position: relative;
    
    .credit-score {
      text-align: center;
      
      .score {
        font-size: 36px;
        font-weight: 600;
        background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        display: block;
        margin-bottom: 4px;
      }
      
      .label {
        font-size: 15px;
        color: #666;
        display: block;
      }
    }
  }
  
  .earnings-row {
    display: flex;
    justify-content: space-around;
    margin: 0 10px;
    position: relative;
    
    .earnings-item {
      text-align: center;
      padding: 12px 20px;
      background: rgba(255, 255, 255, 0.8);
      border-radius: 16px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
      flex: 1;
      margin: 0 10px;
      
      .amount {
        font-size: 24px;
        font-weight: 600;
        color: #ff9a9e;
        display: block;
        margin-bottom: 4px;
      }
      
      .label {
        font-size: 14px;
        color: #666;
      }
    }
  }
}

.action-buttons {
  margin-top: 24px;
  display: flex;
  gap: 16px;
  padding: 0 10px;
  
  .btn {
    flex: 1;
    height: 48px;
    line-height: 48px;
    border-radius: 24px;
    font-size: 16px;
    font-weight: 500;
    transition: all 0.3s ease;
    
    &.withdraw {
      background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%);
      color: #fff;
      border: none;
      box-shadow: 0 4px 12px rgba(161, 140, 209, 0.3);
      
      &:active {
        transform: scale(0.98);
        box-shadow: 0 2px 8px rgba(161, 140, 209, 0.2);
      }
    }
    
    &.details {
      background: #fff;
      color: #666;
      border: 1px solid #eee;
      
      &:active {
        background: #f8f9fa;
        transform: scale(0.98);
      }
    }
  }
}

/* 添加动画效果 */
@keyframes shine {
  0% {
    background-position: 0 0;
  }
  100% {
    background-position: 40px 0;
  }
}
</style>