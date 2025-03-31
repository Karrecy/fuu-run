// 引入 request 文件
import { toRaw } from 'vue';
import {request} from '@/request/request.js'

// 以下 api 为博主项目示例，实际与项目相匹配

// 获取学校
export const getSchool = (id) => {
   
    return request({
        url: '/address/school/' + id,
        method: 'get'
    })
}
// 获取学校列表
export const listSchool = (params) => {
   
    return request({
        url: '/address/school/list',
        method: 'get',
		params:toRaw(params)
    })
}

