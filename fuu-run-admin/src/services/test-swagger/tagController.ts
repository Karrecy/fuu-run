// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

/** 删除tags 删除tags DELETE /order/tag/${param0} */
export async function remove4(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.remove4Params,
  options?: { [key: string]: any },
) {
  const { schoolRegionIds: param0, ...queryParams } = params;
  return request<API.RVoid>(`/dev/order/tag/${param0}`, {
    method: 'DELETE',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 新增tag 新增tag POST /order/tag/add */
export async function add2(body: API.Tags, options?: { [key: string]: any }) {
  return request<API.RVoid>('/dev/order/tag/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 修改tag 修改tag PUT /order/tag/edit */
export async function edit2(body: API.Tags, options?: { [key: string]: any }) {
  return request<API.RVoid>('/dev/order/tag/edit', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** tag分页查询 tag分页查询 GET /order/tag/list */
export async function list3(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.list4Params,
  options?: { [key: string]: any },
) {
  return request<API.TableDataInfoTags>('/dev/order/tag/list', {
    method: 'GET',
    params: {
      ...params,
      tags: undefined,
      ...params['tags'],
      pageQuery: undefined,
      ...params['pageQuery'],
    },
    ...(options || {}),
  });
}

/** 根据school和ordertype查询list 根据school和ordertype查询list GET /order/tag/list/user */
export async function get(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getParams,
  options?: { [key: string]: any },
) {
  return request<API.RListTags>('/dev/order/tag/list/user', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}
