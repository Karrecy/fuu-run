// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

/** 跑腿员接单 跑腿员接单 GET /order/order/accept/${param0} */
export async function accept(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.acceptParams,
  options?: { [key: string]: any },
) {
  const { orderId: param0, ...queryParams } = params;
  return request<API.RVoid>(`/dev/order/order/accept/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 新建订单 新建订单 POST /order/order/add */
export async function add3(body: API.OrderSubmitDTO, options?: { [key: string]: any }) {
  return request<API.RPayedVO>('/dev/order/order/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 取消订单 取消订单 POST /order/order/cancel */
export async function cancel(body: API.OrderCancelDTO, options?: { [key: string]: any }) {
  return request<API.RVoid>('/dev/order/order/cancel', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 取消订单前置操作 取消订单前置操作 GET /order/order/cancelbefore/${param0} */
export async function cancelbefore(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.cancelbeforeParams,
  options?: { [key: string]: any },
) {
  const { orderId: param0, ...queryParams } = params;
  return request<API.RObject>(`/dev/order/order/cancelbefore/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 完成订单 完成订单 POST /order/order/complete */
export async function complete(body: API.OrderCompleteDTO, options?: { [key: string]: any }) {
  return request<API.RVoid>('/dev/order/order/complete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 确定送达 确定送达 GET /order/order/confirm/${param0} */
export async function confirm(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.confirmParams,
  options?: { [key: string]: any },
) {
  const { orderId: param0, ...queryParams } = params;
  return request<API.RVoid>(`/dev/order/order/confirm/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 跑腿员配送 跑腿员配送 GET /order/order/delivery/${param0} */
export async function delivery(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deliveryParams,
  options?: { [key: string]: any },
) {
  const { orderId: param0, ...queryParams } = params;
  return request<API.RVoid>(`/dev/order/order/delivery/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 订单详情查询 订单详情查询 GET /order/order/detail/${param0} */
export async function detail(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.detailParams,
  options?: { [key: string]: any },
) {
  const { orderId: param0, ...queryParams } = params;
  return request<API.ROrderDetailVO>(`/dev/order/order/detail/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 大厅订单查询 大厅订单查询 GET /order/order/list/hall */
export async function list4(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.list5Params,
  options?: { [key: string]: any },
) {
  return request<API.TableDataInfoOrderMain>('/dev/order/order/list/hall', {
    method: 'GET',
    params: {
      ...params,
      orderQuery: undefined,
      ...params['orderQuery'],
      pageQuery: undefined,
      ...params['pageQuery'],
    },
    ...(options || {}),
  });
}

/** 我的订单查询 我的订单查询 GET /order/order/list/user */
export async function listUser(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listUserParams,
  options?: { [key: string]: any },
) {
  return request<API.TableDataInfoOrderMain>('/dev/order/order/list/user', {
    method: 'GET',
    params: {
      ...params,
      orderQuery: undefined,
      ...params['orderQuery'],
      pageQuery: undefined,
      ...params['pageQuery'],
    },
    ...(options || {}),
  });
}

/** 继续支付 继续支付 GET /order/order/payAgain/${param0} */
export async function payAgain(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.payAgainParams,
  options?: { [key: string]: any },
) {
  const { orderId: param0, ...queryParams } = params;
  return request<API.RPayedVO>(`/dev/order/order/payAgain/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 获取用户/跑腿员电话 获取用户/跑腿员电话 GET /order/order/phone/${param0} */
export async function phone(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.phoneParams,
  options?: { [key: string]: any },
) {
  const { orderId: param0, ...queryParams } = params;
  return request<API.RMapStringString>(`/dev/order/order/phone/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 订单退款 订单退款 GET /order/order/refund */
export async function refund(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.refundParams,
  options?: { [key: string]: any },
) {
  return request<API.RVoid>('/dev/order/order/refund', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 补充凭证 补充凭证 POST /order/order/updateImages */
export async function suppleImages(body: API.OrderCompleteDTO, options?: { [key: string]: any }) {
  return request<API.RVoid>('/dev/order/order/updateImages', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
