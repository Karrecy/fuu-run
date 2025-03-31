//是否已经连接上ws
let isOpenSocket = false
//心跳间隔，单位毫秒
let heartBeatDelay = 3000
let heartBeatInterval = null
//心跳时发送的消息文本
let heartBeatText = "PING"
//最大重连次数
let reconnectTimes = 10
let reconnectInterval = null
//重连间隔，单位毫秒
let reconnectDelay = 3000
import { ws_url } from "./request"
let socketTask = null

//这个参数是防止重连失败之后onClose方法会重复执行reconnect方法，导致重连定时器出问题
//连接并打开之后可重连，且只执行重连方法一次
let canReconnect = false

//封装的对象，最后以模块化向外暴露，
//init方法 初始化socketTask对象
//completeClose方法 完全将socketTask关闭（不重连）
//其他关于socketTask的方法与uniapp的socketTask api一致
let ws = {
    socketTask: null,
    init,
    completeClose,
    send
}

function init() {
    socketTask = uni.connectSocket({
        url: ws_url,
		header:{
			'Authorization':'Bearer ' + uni.getStorageSync("token")
		},
        complete: (res)=> {console.log("WebSocket连接成功",res)}
    })
    socketTask.onOpen((res) => {
        console.log("WebSocket连接已打开",res)
        clearInterval(heartBeatInterval)
        clearInterval(reconnectInterval)
        isOpenSocket = true
        canReconnect = true
        //开启心跳机制  向websocket发送数据，json格式，参数：sceneId
        heartBeat()
    })
    socketTask.onMessage((res) => {
        // 每次返回的数据不一样,需要加入判断
        console.log('收到服务器内容',JSON.parse(res.data))
        let result = JSON.parse(res.data)
　　　　 //这边可以根据推送的数据, 做相应的 do somethings, 根据自己需求, 下面只是例子..
        // token存在,说明,小程序用户点击了授权(比如推送的数据有token,那就做什么事xxxx
		 // 触发全局事件，将消息传递到页面
		    uni.$emit('ws-message', result);  // 这里传递的是 WebSocket 接收到的消息
        // if(result.token){
        //     uni.setStorageSync('token', result.token)
        //     uni.setStorageSync('userInfo', JSON.stringify(result.userInfo))
        // }
        // // client_id存在,说明连websocket接成功
        // if(result.msg=="连接成功"){
        //     uni.setStorageSync('client_id', result.data.client_id);
        // }
        // // JSON.parse(res.data).msg=="已归还导览机", 已归还导览机,清空用户缓存
        // if(result.msg=="已归还导览机"){
        //     // 返回首页,清空数据
        //     uni.clearStorageSync();
        //     uni.navigateBack({
        //         delta: 100
        //     })
        // }

    })
    socketTask.onClose(() => {
        if(isOpenSocket){
            console.log("ws与服务器断开")
        }else{
            console.log("连接失败")
        }
        // isOpenSocket = false
        // if(canReconnect){
        //     reconnect()
        //     canReconnect = false
        // }
    })
    ws.socketTask = socketTask
}

function heartBeat() {
    heartBeatInterval = setInterval(() => {
        // console.log(heartBeatText)
        // uni.getSystemInfo({
        //     success: function (res) {
        //         console.log('设备id----',res.deviceId);//设备id
        //         uni.setStorageSync('deviceId', res.deviceId)
        //         let obj = {
        //             sceneId:res.deviceId
        //         }
        //         send(JSON.stringify(obj));
        //     }
        // });
        
    }, heartBeatDelay)
}

// 发送消息
function send(value) {
    ws.socketTask.send({
        data: value,
        async success(res) {
            console.log("消息发送成功",res)
        }
    });
}
function reconnect() {
    //停止发送心跳
    clearInterval(heartBeatInterval)
    //如果不是人为关闭的话，进行重连
    if (!isOpenSocket) {
        let count = 0;
        reconnectInterval = setInterval(() => {
            console.log("正在尝试重连")
            init();
            count++
            //重连一定次数后就不再重连
            if(count >= reconnectTimes){
                clearInterval(reconnectInterval)
                console.log("网络异常或服务器错误")
            }
        }, reconnectDelay)
    }
}
function completeClose(){
    //先将心跳与重连的定时器清除
    clearInterval(heartBeatInterval)
    clearInterval(reconnectInterval)
    canReconnect = false
    ws.socketTask.close()
}

export default ws