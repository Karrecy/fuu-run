// 引入 request 文件
import {
	toRaw
} from 'vue';
import {request} from '@/request/request.js'

// 以下 api 为博主项目示例，实际与项目相匹配


// 取消前置操作
export const getCancelBefore = (params) => {
	return request({
		url: '/order/order/cancelbefore/' + params,
		method: 'get',
	})
}
// 订单详情
export const getDetailOrderUser = (params) => {
	return request({
		url: '/order/order/detail/' + params,
		method: 'get',
	})
}

// 我的订单分页查询
export const getListOrderUser = (params) => {
	return request({
		url: '/order/order/list/user',
		method: 'get',
		params: toRaw(params),
	})
}

// 大厅分页查询
export const getListOrderHall = (params) => {
	return request({
		url: '/order/order/list/hall',
		method: 'get',
		params: toRaw(params),
	})
}

//提交订单
export const postSubmitOrder = (params) => {
	return request({
		url: '/order/order/add',
		method: 'post',
		data: params,
	})
}

//取消订单
export const postCancelOrder = (params) => {
	return request({
		url: '/order/order/cancel',
		method: 'post',
		data: params,
	})
}

//继续支付
export const getPayAgain = (params) => {
	return request({
		url: '/order/order/payAgain/' + params,
		method: 'get',
	})
}

//接单
export const getAccept = (params) => {
	return request({
		url: '/order/order/accept/' + params,
		method: 'get',
	})
}

//开始配送
export const getBeginDelivery = (params) => {
	return request({
		url: '/order/order/delivery/' + params,
		method: 'get',
	})
}

//完成订单
export const postCompleteOrder = (params) => {
	return request({
		url: '/order/order/complete',
		method: 'post',
		data: params,
	})
}

//修改凭证
export const postUpdateImages = (params) => {
	return request({
		url: '/order/order/updateImages',
		method: 'post',
		data: params,
	})
}

//确认完成
export const getConfirmOrder = (params) => {
	return request({
		url: '/order/order/confirm/' + params,
		method: 'get',
	})
}

//获取电话
export const getPhoneOrder = (params) => {
	return request({
		url: '/order/order/phone/' + params,
		method: 'get',
	})
}

//根据id查询申诉
export const getAppealOrder = (params) => {
	return request({
		url: '/order/appeal/' + params,
		method: 'get',
	})
}
//提交申诉
export const postAppealOrder = (params) => {
	return request({
		url: '/order/appeal',
		method: 'post',
		data: params,
	})
}
//初始化聊天
export const getInitChat = (params) => {
	return request({
		url: '/order/chat/initchat/' + params,
		method: 'get',
	})
}

// 订单聊天记录查询
export const getPageOrderChat = (orderId,params) => {
	return request({
		url: '/order/chat/list/'+orderId,
		method: 'get',
		params: toRaw(params),
	})
}