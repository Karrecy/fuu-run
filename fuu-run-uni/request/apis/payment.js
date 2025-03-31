// 引入 request 文件
import { toRaw } from 'vue';
import {request} from '@/request/request.js'

// 以下 api 为博主项目示例，实际与项目相匹配


// 获取资金明细
export const getListCapital = (params) => {
	return request({
		url: '/payment/capital/list',
		method: 'get',
		params: toRaw(params),
	})
}

// 获取钱包
export const getWallet = (params) => {
	return request({
		url: '/payment/wallet/curr',
		method: 'get',
		params: toRaw(params),
	})
}


// 提交申请提现
export const postRecodeSubmit = (params) => {
	return request({
		url: '/payment/recode',
		method: 'post',
		data: params,
	})
}

// 最近一次提现
export const getRecodeLast = (params) => {
	return request({
		url: '/payment/recode/last',
		method: 'get',
		params: toRaw(params),
	})
}