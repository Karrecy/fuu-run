<template>
	<view @click="showSelectOrder"  class="cellBox">
	    <view class="cell-left">
	      <span class="cellTitle">订单默认显示</span>
	    </view>
	    <view class="cell-right">
	      <span class="cellDesc">{{orderDefaultShow}}</span>
	      <image class="cellRight" src="/static/icons/right_grey.png" mode=""></image>
	    </view>
	  </view>
	
	  <view @click="toAgreement" class="cellBox">
	    <view class="cell-left">
	      <span class="cellTitle">用户协议</span>
	    </view>
	    <view class="cell-right">
	      <span class="cellDesc"></span>
	      <image class="cellRight" src="/static/icons/right_grey.png" mode=""></image>
	    </view>
	  </view>
</template>

<script>

		export default {
			data() {
				return {
					pop:false,
					orderDefaultShow:""
				}
			},
			onLoad() {
				this.orderDefaultShow = uni.getStorageSync("orderDefaultShow")
			},
			onShow() {
			},	
			methods: {
				toAgreement() {
					uni.navigateTo({
						url:"/pages/API/user/setting/agreement/agreement"
					})
				},
				showSelectOrder() {
					let that = this
					wx.showActionSheet({
						itemList: ['我的发布', '我的接单'],  //按钮的文字数组，数组长度最大为 6
						  success(res) {
							console.log(res)
							uni.setStorageSync("orderDefaultShow",res.tapIndex == 0 ? '我的发布' : '我的接单')
							that.orderDefaultShow = res.tapIndex == 0 ? '我的发布' : '我的接单'
						  },
						  fail(res) {
							console.log(res.errMsg)
						  }
					})
				}
			}
		}
</script>

<style>
	page {
	  background-color: #f7f8fa;
	  width: 100%;
	  overflow-x: hidden;
	}
	
	/* 移除浮动布局,改用flex */
	.cellBox {
	  width: 92%;
	  margin: 10px auto;
	  padding: 16px 20px;
	  background: #fff;
	  border-radius: 12px;
	  box-shadow: 0 2px 12px rgba(100, 100, 100, 0.1);
	  transition: all 0.3s ease;
	  display: flex;
	  align-items: center;
	  box-sizing: border-box;
	}
	
	.cellBox:active {
	  transform: scale(0.98);
	  background: #f8f8f8;
	}
	
	/* 左侧图标和标题容器 */
	.cell-left {
	  display: flex;
	  align-items: center;
	  flex: 1;
	}
	
	.cellImg { 
	  width: 24px;
	  height: 24px;
	  flex-shrink: 0;
	}
	
	.cellTitle {
	  margin-left: 12px;
	  font-size: 16px;
	  color: #333;
	  font-weight: 500;
	}
	
	/* 右侧箭头和描述 */
	.cell-right {
	  display: flex;
	  align-items: center;
	}
	
	.cellDesc {
	  font-size: 14px;
	  color: #999;
	  margin-right: 8px;
	}
	
	.cellRight {
	  width: 14px;
	  height: 14px;
	  opacity: 0.6;
	}
	
	/* 顶部用户信息容器 */
	.container {
	  display: flex;
	  align-items: center;
	  justify-content: space-between;
	  padding: 20px 24px;
	  margin: 15px auto 25px;
	  background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%);
	  border-radius: 16px;
	  width: 92%;
	  box-sizing: border-box;
	  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
	}
	
	.left {
	  display: flex;
	  flex-direction: column;
	  flex: 1;
	  margin-right: 20px;
	}
	
	.nickname {
	  font-weight: 600;
	  font-size: 20px;
	  color: #fff;
	  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
	  white-space: nowrap;
	  overflow: hidden;
	  text-overflow: ellipsis;
	}
	
	.desc {
	  color: rgba(255, 255, 255, 0.9);
	  font-size: 14px;
	  margin-top: 4px;
	}
	
	.avatar {
	  width: 70px;
	  height: 70px;
	  border-radius: 50%;
	  border: 3px solid rgba(255, 255, 255, 0.8);
	  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
	  flex-shrink: 0;
	}
	
	/* 移除不需要的浮动类 */
	.fl, .fr {
	  display: none;
	}
</style>