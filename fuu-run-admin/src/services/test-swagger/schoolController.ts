// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

/** 根据校区ID获取校区 根据校区ID获取校区 GET /address/school/${param0} */
export async function get1(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.get1Params,
  options?: { [key: string]: any },
) {
  const { id: param0, ...queryParams } = params;
  return request<API.RSchool>(`/dev/address/school/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 新增校区 新增校区 POST /address/school/add */
export async function addAgent1(body: API.School, options?: { [key: string]: any }) {
  return request<API.RVoid>('/dev/address/school/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 修改校区 修改校区 PUT /address/school/edit */
export async function editAgent1(body: API.School, options?: { [key: string]: any }) {
  return request<API.RVoid>('/dev/address/school/edit', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 校区分页查询 校区分页查询 GET /address/school/list */
export async function list5(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.list7Params,
  options?: { [key: string]: any },
) {
  return request<API.TableDataInfoSchool>('/dev/address/school/list', {
    method: 'GET',
    params: {
      ...params,
      school: undefined,
      ...params['school'],
      pageQuery: undefined,
      ...params['pageQuery'],
    },
    ...(options || {}),
  });
}
