// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

/** 提交申诉 提交申诉 POST /order/appeal */
export async function submitAppeal(body: API.OrderAppealDTO, options?: { [key: string]: any }) {
  return request<API.RVoid>('/dev/order/appeal', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 根据orderId查询申诉 根据orderId查询申诉 GET /order/appeal/${param0} */
export async function getAppeal(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getAppealParams,
  options?: { [key: string]: any },
) {
  const { orderId: param0, ...queryParams } = params;
  return request<API.RListOrderAppealVO>(`/dev/order/appeal/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 处理申诉 处理申诉 PUT /order/appeal/edit */
export async function edit3(body: API.OrderAppeal, options?: { [key: string]: any }) {
  return request<API.RVoid>('/dev/order/appeal/edit', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 申诉分页查询 申诉分页查询 GET /order/appeal/list */
export async function list5(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.list6Params,
  options?: { [key: string]: any },
) {
  return request<API.TableDataInfoOrderAppeal>('/dev/order/appeal/list', {
    method: 'GET',
    params: {
      ...params,
      orderAppeal: undefined,
      ...params['orderAppeal'],
      pageQuery: undefined,
      ...params['pageQuery'],
    },
    ...(options || {}),
  });
}
