<template>
	<view class="chat">
    <!-- 顶部标题 -->
    <view class="topTabbar">
      <!-- 返回图标 -->
	  <nut-icon name="rect-left" @click="goBack()"></nut-icon>
      <!-- 标题 -->
      <view class="text">在线沟通</view>
    </view>
		<scroll-view  :style="{height: `${windowHeight-inputHeight - 180}rpx`}"
		id="scrollview"
		scroll-y="true" 
		:scroll-top="scrollTop"
		@scroll="handleScroll"
		class="scroll-view" 
		>
			<div v-show="skeletonLoading" style="width: 90%;margin-left: 5%;">
				<div style="width: 100%;height: 20px;"></div>
				  <nut-skeleton width="240px" height="15px" title animated avatar avatarSize="60px" row="3"> </nut-skeleton>
				  <div style="width: 100%;height: 20px;"></div>
				  <nut-skeleton width="240px" height="15px" title animated avatar avatarSize="60px" row="3"> </nut-skeleton>
				  <div style="width: 100%;height: 20px;"></div>
				  <nut-skeleton width="240px" height="15px" title animated avatar avatarSize="60px" row="3"> </nut-skeleton>
				  <div style="width: 100%;height: 20px;"></div>
				  <nut-skeleton width="240px" height="15px" title animated avatar avatarSize="60px" row="3"> </nut-skeleton>
				 <div style="width: 100%;height: 20px;"></div>
				  <nut-skeleton width="240px" height="15px" title animated avatar avatarSize="60px" row="3"> </nut-skeleton>
				  <div style="width: 100%;height: 20px;"></div>
				  <nut-skeleton width="240px" height="15px" title animated avatar avatarSize="60px" row="3"> </nut-skeleton>
			</div>
			<!-- 聊天主体 -->
			<view v-show="!skeletonLoading" id="msglistview" class="chat-body">
				<!-- 聊天记录 -->
				<view v-for="(item,index) in rows" :key="index">
					<!-- 自己发的消息 -->
					<view class="item self" v-if="item.senderId == userInfo.uid" >
						<!-- 文字内容 -->
						<div class="contentBox">
							<div class="contentTop textRight"> 
								<nut-tag v-if="userInfo.userType == 0" custom-color="#ff0000" type="primary">超管</nut-tag>
								<nut-tag v-if="userInfo.userType == 1" custom-color="#aa55ff" type="primary">校区代理</nut-tag>
								<nut-tag v-if="chatInitData.userId == userInfo.uid" custom-color="#cccccc" type="primary">我</nut-tag>
								<nut-tag v-if="chatInitData.runnerId == userInfo.uid" custom-color="#cccccc" type="primary">我</nut-tag>
								<nut-tag v-if="userInfo.userType == 5" type="primary">系统</nut-tag>
								<span class="ellipsis">{{userInfo.userWx.nickname}}</span>
							</div>
							<view class="content right">
								<span style="display: block;">{{item.message}}</span>
								<span class="chatTime">{{item.createTime}}</span>
							</view>
						</div>
						<!-- 头像 -->
						<image class="avatar" :src="userInfo.userWx.avatar">
						</image>
					</view>
					<!-- 机器人发的消息 -->
					<view class="item Ai" v-if="item.senderId != userInfo.uid">
						<!-- 头像 -->     
						<image v-if="item.senderType == 0" class="avatar" :src="chatInitData.adminAvatar"></image>
						<image v-if="item.senderType == 1" class="avatar" :src="chatInitData.agentAvatar"></image>
						<image v-if="item.senderId == chatInitData.userId" class="avatar" :src="chatInitData.userAvatar"></image>
						<image v-if="item.senderId == chatInitData.runnerId" class="avatar" :src="chatInitData.runnerAvatar"></image>
						<!-- 文字内容 -->
						<div class="contentBox">
							<div class="contentTop textLeft">
								<nut-tag v-if="item.senderType == 0" custom-color="#ff0000" type="primary">超管</nut-tag> 
								<nut-tag v-if="item.senderType == 1" custom-color="#aa55ff" type="primary">校区代理</nut-tag>
								<nut-tag v-if="item.senderId == chatInitData.userId" custom-color="#cccccc" type="primary">用户</nut-tag>
								<nut-tag v-if="item.senderId == chatInitData.runnerId" custom-color="#0055ff" type="primary">跑腿员</nut-tag>
								<span v-if="item.senderType == 0" class="ellipsis">{{chatInitData.adminName}}</span>
								<span v-if="item.senderType == 1" class="ellipsis">{{chatInitData.agentName}}</span>
								<span v-if="item.senderId == chatInitData.userId" class="ellipsis">{{chatInitData.userName}}</span>
								<span v-if="item.senderId == chatInitData.runnerId" class="ellipsis">{{chatInitData.runnerName}}</span>
							</div>
							<view class="content left">
								<span style="display: block;">{{item.message}}</span>
								<span class="chatTime">{{item.createTime}}</span>
							</view>
						</div>
						
					</view>
				</view>
			</view>
		</scroll-view>
		<!-- 底部消息发送栏 -->
		<!-- 用来占位，防止聊天消息被发送框遮挡 -->
		<view class="chat-bottom" :style="{height: `${inputHeight}rpx`}">
			<view class="send-msg" :style="{bottom:`${keyboardHeight - 60}rpx`}">
        <view class="uni-textarea">
          <textarea v-model="chatText"
            maxlength="300"
            confirm-type="send"
            @confirm="handleSend"
            placeholder="请文明沟通~"
            :show-confirm-bar="false"
            :adjust-position="false"
            @linechange="sendHeight"
            @focus="focus" @blur="blur"
           auto-height></textarea>
        </view>
				<button :disabled="skeletonLoading" @click="handleSend" class="send-btn">发送</button>
			</view>
		</view>
	</view>
</template>
<script>
	import dayjs from 'dayjs';
	import relativeTime from 'dayjs/plugin/relativeTime'; // 引入 relativeTime 插件
	import 'dayjs/locale/zh-cn';  // 导入中文语言包
	// 扩展 dayjs 插件
	dayjs.extend(relativeTime);
	// 设置为中文
	dayjs.locale('zh-cn');
	import ws from '@/request/websocket.js'
	import { getInitChat,getPageOrderChat } from '@/request/apis/order.js'
	export default{
		data() {
			return {
				skeletonLoading:true,
				userInfo:{},
				//键盘高度
				keyboardHeight:0,
				//底部消息发送高度
				bottomHeight: 0,
				//滚动距离
				scrollTop: 0,
				userId:'',
				//发送的消息
				chatText:"",
				chatInitData:{},
				msgData:{
					'orderId':'',
					'isBroadcast':1,
					'recipientIds':[],
					'msgType':1,
					'message':'',
					'senderId':'',
					'senderType':'',
					'createTime':'1999-02-02 12:12:12'
				},
				// 查询参数
				queryParams: {
					pageNum: 1,
					pageSize: 20,
				},
				  // 总条数
				total: 0,
				rows:[],
				hasMore: true
			}
		},
		updated(){
			//页面更新时调用聊天消息定位到最底部
			// this.scrollToBottom();
		},
		computed: {
		
			windowHeight() {
			    return this.rpxTopx(uni.getSystemInfoSync().windowHeight)
			},
			// 键盘弹起来的高度+发送框高度
			inputHeight(){
				return this.bottomHeight+this.keyboardHeight
			}
		},
		onLoad(options){
			console.log("chat onLoad");
			this.skeletonLoading = true
			const checkOperationStatus = setInterval(() => {
			  if (this.$store.state.appLaunch) {
				  this.initData(options.orderId)
				  this.pageQuery()
				  ws.init()
				  uni.onKeyboardHeightChange(res => {
				  	//这里正常来讲代码直接写
				  	//this.keyboardHeight=this.rpxTopx(res.height)就行了
				  	//但是之前界面ui设计聊天框的高度有点高,为了不让键盘和聊天输入框之间距离差太大所以我改动了一下。
				  	this.keyboardHeight = this.rpxTopx(res.height)
				  	if(this.keyboardHeight<0)this.keyboardHeight = 0;
				  })
				  this.scrollToBottom()
				clearInterval(checkOperationStatus);
				console.log('首页的js文件中的代码执行');
			  }
			}, 100); // 每100毫秒检查一次状态变化
		},
		onReady() {
			// 监听 WebSocket 消息
			uni.$on('ws-message', (message) => {
			  console.log('收到 WebSocket 消息:', message);
			  // 将接收到的消息添加到 messages 中
			  this.rows.push(message);
			  this.scrollToBottom()
			});
		},
		onHide() {
			ws.completeClose()
		},
		onUnload(){
			// uni.offKeyboardHeightChange()
			ws.completeClose()
		},
		methods: {
			handleScroll(e) {
				// console.log(e);
				// console.log(this.scrollTop);
				
				if (e.target.scrollTop === 0 && this.hasMore) {
					this.pageQuery(); // 滚动到顶部加载新数据
				}
			},
			goBack() {
				uni.navigateBack()
			},
			// 发送消息
			handleSend() {
				//如果消息不为空
				if(!/^\s*$/.test(this.chatText.message)){
					this.msgData.message = this.chatText
					this.msgData.createTime =  dayjs().format("YYYY-MM-DD HH:mm:ss");
					ws.send(JSON.stringify(this.msgData))
					this.rows.push(JSON.parse(JSON.stringify(this.msgData)));
					this.chatText = '';
					this.scrollToBottom()
				}else {
					this.$modal.showToast('不能发送空白消息')
				}
			},
			initData(orderId) {
				this.msgData.orderId = orderId
				this.userInfo = this.$store.state.userInfo
				this.msgData.senderType = this.userInfo.userType
				this.msgData.senderId = this.userInfo.uid
				
				getInitChat(orderId).then(res => {
					console.log(res);
					this.chatInitData = res.data
					this.msgData.recipientIds.push(res.data.adminId)
					this.msgData.recipientIds.push(res.data.agentId)
					if(this.userInfo.uid == res.data.userId) {
						this.msgData.recipientIds.push(res.data.runnerId)
					}
					if(this.userInfo.uid == res.data.runnerId) {
						this.msgData.recipientIds.push(res.data.userId)
					}
					
				})
			},
			pageQuery() {
				getPageOrderChat(this.msgData.orderId,this.queryParams).then(res => {
					console.log(res);
					this.total = res.total
					let data = res.rows
					data.reverse()
					this.rows.unshift(...data)
					this.queryParams.pageNum += 1;
					this.hasMore = data.length > 0
					this.skeletonLoading = false
				})
			},
			focus(){
				this.scrollToBottom()
			},
			blur(){
				this.scrollToBottom()
			},
			// px转换成rpx
			rpxTopx(px){
				let deviceWidth = uni.getSystemInfoSync().windowWidth
				let rpx = ( 750 / deviceWidth ) * Number(px)
				return Math.floor(rpx)
			},
			// 监视聊天发送栏高度
			sendHeight(){
				setTimeout(()=>{
					let query = uni.createSelectorQuery();
					query.select('.send-msg').boundingClientRect()
					query.exec(res =>{
						this.bottomHeight = this.rpxTopx(res[0].height)
					})
				},10)
			},
			// 滚动至聊天底部
			scrollToBottom(e){
				setTimeout(()=>{
					let query = uni.createSelectorQuery().in(this);
					query.select('#scrollview').boundingClientRect();
					query.select('#msglistview').boundingClientRect();
					query.exec((res) =>{
						if(res[1].height > res[0].height){
							this.scrollTop = this.rpxTopx(res[1].height - res[0].height)
						}
					})
				},15)
			},
			
		}
	}
</script>
<style lang="scss" scoped>
$chatContentbgc: #C2DCFF;
$sendBtnbgc: #4F7DF5;

view, button, text, input, textarea {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.nut-tag {
  border-width: unset !important;
  margin-right: 2px !important;
}

.ellipsis {
  overflow: hidden;        /* 隐藏溢出内容 */
  white-space: nowrap;     /* 禁止换行 */
  text-overflow: ellipsis; /* 显示省略号 */
  max-width: 200px;        /* 设置最大宽度（按需修改）*/
  display: inline-block;   /* 或 block 根据布局需要选择 */
  padding-left: 2px;
}

.chatTime {
  font-size: 12px; /* 调整时间字体大小 */
  color: #999; /* 调整时间颜色 */
  display: inline-block;
}

/* 聊天消息 */
.chat {
  .topTabbar {
    width: 100%;
    height: 90rpx;
    line-height: 90rpx;
    display: flex;
    margin-top: 80rpx;
    justify-content: space-between;
    align-items: center;
    background-color: #ffffff; /* 背景色 */
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); /* 阴影效果 */
    border-radius: 10px; /* 圆角 */
    
    .icon {
      margin-left: 20rpx;
    }

    .text {
      margin: auto;
      font-size: 16px;
      font-weight: 700;
      color: #333; /* 标题颜色 */
    }

    .button {
      width: 10%;
      margin: auto 20rpx auto 0rpx;
    }
  }

  .scroll-view {
    background-color: #F6F6F6; /* 聊天背景色 */
    
    .chat-body {
      display: flex;
      flex-direction: column;
      padding-top: 23rpx;

      .contentTop {
        margin: 0 12px 4px 12px;
        max-width: 486rpx;
        color: #777777;
        font-size: small;
        font-weight: bold;
        display: flex;
        align-items: center;
      }

      .textLeft {
        justify-content: flex-start;
      }

      .textRight {
        justify-content: flex-end;
      }

      .self {
        justify-content: flex-end;
      }

      .item {
        display: flex;
        padding: 23rpx 30rpx;

        .right {
          background-color: $chatContentbgc;
          border-radius: 10px; /* 圆角 */
        }

        .left {
          background-color: #FFFFFF;
          border-radius: 10px; /* 圆角 */
        }

        /* 聊天消息的三角形 */
        .right::after {
          position: absolute;
          display: inline-block;
          content: '';
          width: 0;
          height: 0;
          left: 100%;
          top: 10px;
          border: 12rpx solid transparent;
          border-left: 12rpx solid $chatContentbgc;
        }

        .left::after {
          position: absolute;
          display: inline-block;
          content: '';
          width: 0;
          height: 0;
          top: 10px;
          right: 100%;
          border: 12rpx solid transparent;
          border-right: 12rpx solid #FFFFFF;
        }

        .content {
          position: relative;
          max-width: 486rpx;
          border-radius: 8px;
          word-wrap: break-word;
          padding: 24rpx 24rpx;
          margin: 0 24rpx;
          border-radius: 5px;
          font-size: 32rpx;
          font-family: PingFang SC;
          font-weight: 500;
          color: #333333;
          line-height: 42rpx;
        }

        .avatar {
          display: flex;
          justify-content: center;
          width: 78rpx;
          height: 78rpx;
          background: $sendBtnbgc;
          border-radius: 50rpx;
          overflow: hidden;
          border: 2px solid #dddddd;

          image {
            align-self: center;
            transform: translateY(8px);
          }
        }
      }
    }
  }

  /* 底部聊天发送栏 */
  .chat-bottom {
    width: 100%;
    height: 100rpx;
    background: #F4F5F7;
    transition: all 0.1s ease;

    .send-msg {
      display: flex;
      align-items: flex-end;
      padding: 16rpx 30rpx;
      width: 100%;
      min-height: 177rpx;
      position: fixed;
      bottom: 0;
      background: #fff;
      transition: all 0.1s ease;
    }

    .uni-textarea {
      padding-bottom: 70rpx;  
      textarea {
        width: 537rpx;
        min-height: 75rpx;
        max-height: 500rpx;
        background: #f1f1f1;
        border-radius: 40rpx;
        font-size: 32rpx;
        font-family: PingFang SC;
        color: #333333;
        padding: 5rpx 8rpx;
        text-indent: 30rpx;
      }
    }

    .send-btn {
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 70rpx;
      margin-left: 25rpx;
      width: 120rpx;
      height: 75rpx;
      background: #ed5a65;
      border-radius: 50rpx;
      font-size: 28rpx;
      font-family: PingFang SC;
      font-weight: 500;
      color: #FFFFFF;
      line-height: 28rpx;
      transition: background 0.3s;

      &:hover {
        background: #d9534f; /* 悬停效果 */
      }
    }
  }
}
</style>
