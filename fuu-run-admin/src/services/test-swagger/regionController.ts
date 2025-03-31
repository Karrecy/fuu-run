// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

/** 删除校区区域 删除校区区域 DELETE /address/region/${param0} */
export async function remove5(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.remove5Params,
  options?: { [key: string]: any },
) {
  const { schoolRegionIds: param0, ...queryParams } = params;
  return request<API.RVoid>(`/dev/address/region/${param0}`, {
    method: 'DELETE',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 新增校区区域 新增校区区域 POST /address/region/add */
export async function add3(body: API.SchoolRegion, options?: { [key: string]: any }) {
  return request<API.RVoid>('/dev/address/region/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 修改校区区域 修改校区区域 PUT /address/region/edit */
export async function edit5(body: API.SchoolRegion, options?: { [key: string]: any }) {
  return request<API.RVoid>('/dev/address/region/edit', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 用户校区区域分页查询 用户校区区域分页查询 GET /address/region/list/${param0} */
export async function list7(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.list8Params,
  options?: { [key: string]: any },
) {
  const { schoolId: param0, ...queryParams } = params;
  return request<API.RListRegionVO>(`/dev/address/region/list/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 校区区域列表查询 校区区域列表查询 GET /address/region/list/user */
export async function listUser1(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listUser1Params,
  options?: { [key: string]: any },
) {
  return request<API.RListSchoolRegion>('/dev/address/region/list/user', {
    method: 'GET',
    params: {
      ...params,
      schoolRegion: undefined,
      ...params['schoolRegion'],
    },
    ...(options || {}),
  });
}
