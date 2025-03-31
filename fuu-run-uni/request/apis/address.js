// 引入 request 文件
import { toRaw } from 'vue';
import {request} from '@/request/request.js'

// 以下 api 为博主项目示例，实际与项目相匹配

// 添加地址
export const postAddAddress = (params,) => {
	return request({
		url: '/address/address/add',
		method: 'post',
		data: params,
	})
}

// 地址列表
export const getListAddress = (params) => {
	return request({
		url: '/address/address/list/curr',
		method: 'get',
		params: toRaw(params),
	})
}

// 根据id删除地址
export const delAddress = (params) => {
	return request({
		url: '/address/address/curr/' + params,
		method: 'delete',
	})
}


// 根据id获取地址
export const getAddressById = (params) => {
	return request({
		url: '/address/address/curr/' + params,
		method: 'get',
	})
}


// 修改地址
export const putEditAddress = (params) => {
	return request({
		url: '/address/address/edit',
		method: 'put',
		data:params
	})
}