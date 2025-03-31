// 引入 request 文件
import { toRaw } from 'vue';
import {request} from '@/request/request.js'

// 以下 api 为博主项目示例，实际与项目相匹配

// 跑腿申请
export const runnerSubmit = (params) => {
    return request({
        url: '/system/runnerApply/submit',
        method: 'post',
        data: params
    })
}


// 获取申请进度
export const getApplyProcess = () => {
    return request({
        url: '/system/runnerApply/process',
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

