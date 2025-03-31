// 引入 request 文件
import { toRaw } from 'vue';
import {request} from '@/request/request.js'

// 以下 api 为博主项目示例，实际与项目相匹配

// 根据id获取地址
export const getWeatherByAdcode = (params) => {
	return request({
		url: '/amap/weather/' + params,
		method: 'get',
	})
}