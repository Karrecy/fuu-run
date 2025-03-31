// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

/** 处理申请 处理申请 PUT /system/runnerApply/edit */
export async function edit(body: API.RunnerApply, options?: { [key: string]: any }) {
  return request<API.RVoid>('/dev/system/runnerApply/edit', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 跑腿申请分页查询 跑腿申请分页查询 GET /system/runnerApply/list */
export async function list(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.list1Params,
  options?: { [key: string]: any },
) {
  return request<API.TableDataInfoRunnerApply>('/dev/system/runnerApply/list', {
    method: 'GET',
    params: {
      ...params,
      runnerApply: undefined,
      ...params['runnerApply'],
      pageQuery: undefined,
      ...params['pageQuery'],
    },
    ...(options || {}),
  });
}

/** 查询自己的申请进度 查询自己的申请进度 GET /system/runnerApply/process */
export async function process(options?: { [key: string]: any }) {
  return request<API.RListRunnerApply>('/dev/system/runnerApply/process', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 跑腿申请 跑腿申请 POST /system/runnerApply/submit */
export async function submit(body: API.RunnerApply, options?: { [key: string]: any }) {
  return request<API.RVoid>('/dev/system/runnerApply/submit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
