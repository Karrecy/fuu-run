// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

/** 根据地址ID删除地址 根据地址ID删除地址 DELETE /address/address/${param0} */
export async function remove6(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.remove6Params,
  options?: { [key: string]: any },
) {
  const { addressIds: param0, ...queryParams } = params;
  return request<API.RVoid>(`/dev/address/address/${param0}`, {
    method: 'DELETE',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 新增用户地址 新增用户地址 POST /address/address/add */
export async function add4(body: API.Address, options?: { [key: string]: any }) {
  return request<API.RVoid>('/dev/address/address/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 根据地址ID获取当前用户的地址 根据地址ID获取当前用户的地址 GET /address/address/curr/${param0} */
export async function getById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getByIdParams,
  options?: { [key: string]: any },
) {
  const { addressId: param0, ...queryParams } = params;
  return request<API.RAddress>(`/dev/address/address/curr/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 根据地址ID删除当前用户的地址 根据地址ID删除当前用户的地址 DELETE /address/address/curr/${param0} */
export async function removeById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.removeByIdParams,
  options?: { [key: string]: any },
) {
  const { addressId: param0, ...queryParams } = params;
  return request<API.RVoid>(`/dev/address/address/curr/${param0}`, {
    method: 'DELETE',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 更新用户地址 更新用户地址 PUT /address/address/edit */
export async function edit6(body: API.Address, options?: { [key: string]: any }) {
  return request<API.RVoid>('/dev/address/address/edit', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 分页查询地址 分页查询地址 GET /address/address/list */
export async function list9(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.list9Params,
  options?: { [key: string]: any },
) {
  return request<API.TableDataInfoAddress>('/dev/address/address/list', {
    method: 'GET',
    params: {
      ...params,
      address: undefined,
      ...params['address'],
      pageQuery: undefined,
      ...params['pageQuery'],
    },
    ...(options || {}),
  });
}

/** 获取当前用户的地址列表 获取当前用户的地址列表 GET /address/address/list/curr */
export async function listCurr1(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listCurr1Params,
  options?: { [key: string]: any },
) {
  return request<API.RListAddress>('/dev/address/address/list/curr', {
    method: 'GET',
    params: {
      ...params,
      pageQuery: undefined,
      ...params['pageQuery'],
    },
    ...(options || {}),
  });
}
