// 引入 request 文件
import { toRaw } from 'vue';
import {request} from '@/request/request.js'

// 以下 api 为博主项目示例，实际与项目相匹配


// tag列表
export const getListTag = (params) => {
	return request({
		url: '/order/tag/list/user',
		method: 'get',
		params: toRaw(params),
	})
}

