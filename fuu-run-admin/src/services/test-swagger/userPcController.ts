// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

/** 删除用户 删除用户 DELETE /system/userpc/${param0} */
export async function remove1(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.remove1Params,
  options?: { [key: string]: any },
) {
  const { uIds: param0, ...queryParams } = params;
  return request<API.RVoid>(`/dev/system/userpc/${param0}`, {
    method: 'DELETE',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 新增校区代理 新增校区代理 POST /system/userpc/add */
export async function addAgent(body: API.UserPc, options?: { [key: string]: any }) {
  return request<API.RVoid>('/dev/system/userpc/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 修改pc用户 修改pc用户 PUT /system/userpc/edit */
export async function edit(body: API.UserPc, options?: { [key: string]: any }) {
  return request<API.RVoid>('/dev/system/userpc/edit', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 获取PC用户列表 获取PC用户列表 GET /system/userpc/list */
export async function listPc(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listPcParams,
  options?: { [key: string]: any },
) {
  return request<API.TableDataInfoUser>('/dev/system/userpc/list', {
    method: 'GET',
    params: {
      ...params,
      userPcQuery: undefined,
      ...params['userPcQuery'],
      pageQuery: undefined,
      ...params['pageQuery'],
    },
    ...(options || {}),
  });
}

/** 重设密码 重设密码 PUT /system/userpc/resetPwd/${param0} */
export async function resetPwd(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.resetPwdParams,
  options?: { [key: string]: any },
) {
  const { uId: param0, ...queryParams } = params;
  return request<API.RVoid>(`/dev/system/userpc/resetPwd/${param0}`, {
    method: 'PUT',
    params: { ...queryParams },
    ...(options || {}),
  });
}
