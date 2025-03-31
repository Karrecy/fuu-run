// 引入 request 文件
import { toRaw } from 'vue';
import {request} from '@/request/request.js'

// 以下 api 为博主项目示例，实际与项目相匹配

// 修改个人信息
export const putUpdateProfile = (params) => {
	return request({
		url: '/system/profile',
		method: 'put',
		data: params,
	})
}

// 绑定手机号
export const getBindPhone = (params) => {
	return request({
		url: '/system/profile/bindPhone',
		method: 'get',
		data: params,
	})
}


// 手机号防刷
export const getCanReqPhone = (params) => {
	return request({
		url: '/system/profile/canReqPhone',
		method: 'get',
		data: params,
	})
}