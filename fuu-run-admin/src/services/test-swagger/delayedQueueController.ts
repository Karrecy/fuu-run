// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

/** 添加队列数据 添加队列数据 GET /demo/queue/delayed/add */
export async function add5(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.add5Params,
  options?: { [key: string]: any },
) {
  return request<API.RVoid>('/dev/demo/queue/delayed/add', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 销毁队列 销毁队列 GET /demo/queue/delayed/destroy */
export async function destroy(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.destroyParams,
  options?: { [key: string]: any },
) {
  return request<API.RVoid>('/dev/demo/queue/delayed/destroy', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 删除队列数据 删除队列数据 GET /demo/queue/delayed/remove */
export async function remove(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.removeParams,
  options?: { [key: string]: any },
) {
  return request<API.RVoid>('/dev/demo/queue/delayed/remove', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 订阅队列 订阅队列 GET /demo/queue/delayed/subscribe */
export async function subscribe(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.subscribeParams,
  options?: { [key: string]: any },
) {
  return request<API.RVoid>('/dev/demo/queue/delayed/subscribe', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}
