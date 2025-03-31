<template>
	<template>
	  <nut-notify></nut-notify>
	</template>
  <view class="container">
    <!-- 余额信息卡片 -->
    <view class="balance-card">
      <view class="label">可提现余额</view>
      <view class="amount">¥{{ balance }}</view>
    </view>

    <!-- 提现表单 -->
    <view class="form-section">
      <!-- 提现平台选择 -->
      <nut-cell
        title="提现平台"
        :desc="platformOptions[selectedPlatform].text"
        @click="showPlatformPicker = true"
		is-link
      />

      <!-- 支付宝转账表单 -->
      <template>
        <nut-input
          v-model="form.card"
          label="支付宝账号"
          :placeholder="placeholderCard"
          type="text"
        />
      </template>

      <!-- 提现金额 -->
      <view class="amount-input">
        <text class="label">提现金额</text>
        <view class="input-wrapper">
          <text class="currency">¥</text>
          <input
            type="digit"
            v-model="form.cash"
            placeholder="最低提现金额10元"
            @input="handleAmountInput"
          />
        </view>
      </view>
		<view class="amount-input">
				<text class="label">备注</text>
				<!-- 备注信息 -->
				<nut-input
				  v-model="form.remark"
				  label="备注"
				  :placeholder="placeholderRemark"
				  type="text"
				/>
		</view>
      
    </view>

    <!-- 提交按钮 -->
    <view class="submit-section">
		<nut-button :loading="btnLoading" :disabled="btnDisable" @click="handleSubmit()" block type="info">
				申请提现
		</nut-button>
     <!-- <button 
	  :disabled="btnDisable"
	  :loading="btnLoading"
        class="submit-btn" 
        @click="handleSubmit()"
      >
        申请提现
      </button> -->
    </view>
	
	<view style="width: 90%;margin-left: 5%;color: #bdbdbd;font-size: small;font-weight: bold;margin-top: 20px;">
		预计2个工作日内到账
	</view>

    <!-- 最近提现记录卡片 -->
    <view class="record-card" v-show="lastRecord.status != 3 && lastRecord != null">
      <view class="card-header">
        <text class="title">上次提现</text>
        <text :class="['status', getStatusClass(lastRecord.status)]">
          {{ getStatusText(lastRecord.status) }}
        </text>
      </view>

      <view class="card-content">
        <view class="amount-row"> 
          <text class="label">提现金额</text>
          <text class="amount">¥{{ lastRecord.cash }}</text>
        </view>

        <view class="info-row">
          <view class="info-item">
            <text class="label">提现平台</text>
            <text class="value">{{ getPlatformText(lastRecord.platform) }}</text>
          </view>
          <view class="info-item">
            <text class="label">收款账号</text>
            <text class="value">{{ maskCard(lastRecord.card) }}</text>
          </view>
        </view>

        <view class="time-row">
          <text class="label">申请时间</text>
          <text class="value">{{ formatTime(lastRecord.createTime) }}</text>
        </view>

        <!-- 显示反馈信息（如果有） -->
        <view class="feedback-row" v-if="lastRecord.feedback">
          <text class="label">审核反馈</text>
          <text class="value">{{ lastRecord.feedback }}</text>
        </view>
      </view>
    </view>

    <!-- 平台选择器 -->
    <nut-popup position="bottom" v-model:visible="showPlatformPicker">
      <nut-picker
        :columns="[platformOptions]"
        @confirm="onPlatformConfirm"
        @cancel="showPlatformPicker = false"
      />
    </nut-popup>
  </view>
</template>

<script>
	import {postRecodeSubmit,getRecodeLast} from '@/request/apis/payment.js'
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
		btnDisable:false,
		btnLoading:false,
		placeholderCard:'请输入支付宝账号/手机号',
		placeholderRemark:'可注明备注',
      balance: 0, // 账户余额
      showPlatformPicker: false,
      selectedPlatform: 0, // 0-支付宝 1-银行卡
      platformOptions: [
        { text: '支付宝转账', value: 0 },
        { text: '银行卡转账', value: 1 }
      ],
      form: {
		platform:0,
        card: '', // 支付宝账号
        remark: '', // 备注
        cash: '', // 提现金额
      },
      lastRecord:{
		  status:3
	  }, // 添加最近提现记录
    }
  },
  computed: {
    isValid() {
      const { cash } = this.form
      const numAmount = Number(cash)
      
      // 基础验证
      if (!cash || numAmount < 10 || numAmount > Number(this.balance)) {
        return false
      }
	  return true
    }
  },
  onLoad(options) {
  	this.balance = options.balance
    this.getLastRecord() // 获取最近提现记录
  },
  methods: {
    onPlatformConfirm({ selectedValue, selectedOptions }) {
		console.log(selectedValue);
      this.selectedPlatform = selectedValue[0]
	  let value = selectedValue[0]
	  if(value == 0) {
		  this.placeholderCard = '请输入支付宝账号/手机号'
		  this.placeholderRemark = '可注明备注'
	  }
	  if(value == 1) {
		  this.placeholderCard = '请输入银行卡号'
		  this.placeholderRemark = '注明 开户银行 和 持卡人姓名'
	  }
      this.showPlatformPicker = false
      // 切换平台时重置表单
      this.form.cash = ''
      this.form.remark = ''
      this.form.card = ''
    },

    handleAmountInput(e) {
      const value = e.detail.value
      // 限制只能输入数字和小数点，且只能有两位小数
      this.form.amount = value.replace(/[^\d.]/g, '').replace(/\.{2,}/g, '.').replace(/^(\d+)\.(\d\d).*$/, '$1.$2')
    },

    handleSubmit() {
		console.log(11);
		this.btnLoading = true
      if (!this.isValid) {
		  this.showWarning("提现金额不合法")
		  this.btnLoading = false
		  return
	  }
      
      postRecodeSubmit(this.form).then(res => {
		  console.log(res);
		  this.showSuccess("申请成功，请耐心等待")
		  
	  }).catch(err => {
		  this.showDanger(err)
	  }).finally(res => {
		  this.btnLoading = false
	  })
	  
    },
    
    // 获取最近提现记录
    getLastRecord() {
      // 这里添加获取最近提现记录的接口调用
	  getRecodeLast().then(res =>{
		  console.log(res);
		  if(res.data.status == 2  ) {
			  this.btnDisable = true
		  }
		  this.lastRecord = res.data
	  })
    },
    
    // 获取状态对应的样式类名
    getStatusClass(status) {
      const statusMap = {
        0: 'rejected',
        1: 'success',
        2: 'pending'
      }
      return statusMap[status] || 'pending'
    },

    // 获取状态对应的文字
    getStatusText(status) {
      const statusMap = {
        0: '已驳回',
        1: '已通过',
        2: '审核中'
      }
      return statusMap[status] || '审核中'
    },

    // 获取平台对应的文字
    getPlatformText(platform) {
      return platform === '1' ? '支付宝' : '银行卡'
    },

    // 格式化卡号（隐藏中间部分）
    maskCard(card) {
      if (!card) return ''
      if (card.length <= 8) return card
      return card.substr(0, 4) + '****' + card.substr(-4)
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
  background-color: #f5f5f5;
  padding: 20rpx;
}

.balance-card {
  background: linear-gradient(135deg, #007AFF, #1989fa);
  border-radius: 16rpx;
  padding: 40rpx 30rpx;
  color: #fff;
  margin-bottom: 20rpx;
  
  .label {
    font-size: 28rpx;
    opacity: 0.9;
  }
  
  .amount {
    font-size: 60rpx;
    font-weight: bold;
    margin-top: 10rpx;
  }
}

.form-section {
  background-color: #fff;
  border-radius: 16rpx;
  overflow: hidden;
  
  :deep(.nut-input) {
    padding: 20rpx 30rpx;
  }
}

.amount-input {
  padding: 20rpx 30rpx;
  border-bottom: 1rpx solid #f5f5f5;
  
  .label {
    font-size: 28rpx;
    color: #333;
    margin-bottom: 20rpx;
    display: block;
  }
  
  .input-wrapper {
    display: flex;
    align-items: center;
    margin-bottom: 10rpx;
    
    .currency {
      font-size: 40rpx;
      color: #333;
      margin-right: 10rpx;
    }
    
    input {
      flex: 1;
      font-size: 40rpx;
      height: 80rpx;
    }
  }
  
  .tip {
    font-size: 24rpx;
    color: #999;
  }
}

.submit-section {
  margin-top: 40rpx;
  padding: 0 30rpx;
  
  .submit-btn {
    width: 100%;
    height: 88rpx;
    line-height: 88rpx;
    background-color: #007AFF;
    color: #fff;
    border-radius: 44rpx;
    font-size: 32rpx;
    border: none;
    
    &.disabled {
      opacity: 0.6;
      background-color: #ccc;
    }
  }
}

:deep(.nut-cell) {
  padding: 20rpx 30rpx;
}

.record-card {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin: 30rpx 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30rpx;
    
    .title {
      font-size: 28rpx;
      color: #333;
      font-weight: 500;
    }
    
    .status {
      font-size: 24rpx;
      padding: 4rpx 16rpx;
      border-radius: 20rpx;
      
      &.pending {
        color: #ff9800;
        background-color: rgba(255, 152, 0, 0.1);
      }
      
      &.success {
        color: #4caf50;
        background-color: rgba(76, 175, 80, 0.1);
      }
      
      &.rejected {
        color: #f44336;
        background-color: rgba(244, 67, 54, 0.1);
      }
    }
  }

  .card-content {
    .amount-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 30rpx;
      
      .label {
        font-size: 26rpx;
        color: #666;
      }
      
      .amount {
        font-size: 36rpx;
        font-weight: bold;
        color: #333;
      }
    }

    .info-row {
      display: flex;
      justify-content: space-between;
      margin-bottom: 20rpx;
      
      .info-item {
        flex: 1;
        
        .label {
          font-size: 24rpx;
          color: #999;
          margin-bottom: 8rpx;
          display: block;
        }
        
        .value {
          font-size: 26rpx;
          color: #333;
        }
      }
    }

    .time-row, .feedback-row {
      margin-top: 20rpx;
      
      .label {
        font-size: 24rpx;
        color: #999;
        margin-bottom: 8rpx;
        display: block;
      }
      
      .value {
        font-size: 26rpx;
        color: #333;
      }
    }

    .feedback-row {
      padding-top: 20rpx;
      border-top: 1rpx solid #f5f5f5;
      
      .value {
        color: #ff9800;
      }
    }
  }
}
</style>