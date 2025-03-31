// 引入 request 文件
import { toRaw } from 'vue';
import {request} from '@/request/request.js'

// 以下 api 为博主项目示例，实际与项目相匹配

// 获取地址区域列表
export const listRegion = (params) => {
   
    return request({
        url: '/address/region/list/user',
        method: 'get',
		params:toRaw(params)
    })
}

// 登录
export const xcxLogin = (params) => {
   
    return request({
   
        url: '/xcxLogin',
        method: 'post',
        data: params,
		header:{'Content-Type': 'application/x-www-form-urlencoded'}
    })
}
// 查询学习列表
export const getInfo = () => {
   
    return request({
        url: '/getInfo',
        method: 'get'
    })
}

