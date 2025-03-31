// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

/** 修改全局配置 修改全局配置 POST /system/system/config/edit */
export async function edit7(body: API.FuuConfig, options?: { [key: string]: any }) {
  return request<API.RVoid>('/dev/system/system/config/edit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /system/system/monitor */
export async function getServerMonitor(options?: { [key: string]: any }) {
  return request<any>('/dev/system/system/monitor', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 根据角色查询权限 根据角色查询权限 GET /system/system/perms/${param0} */
export async function list(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listParams,
  options?: { [key: string]: any },
) {
  const { userType: param0, ...queryParams } = params;
  return request<API.RListPerm>(`/dev/system/system/perms/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 添加权限 添加权限 POST /system/system/perms/add */
export async function roleperms1(body: API.Perm, options?: { [key: string]: any }) {
  return request<API.RVoid>('/dev/system/system/perms/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 删除权限 删除权限 DELETE /system/system/perms/del */
export async function rolepermsDel(body: number[], options?: { [key: string]: any }) {
  return request<API.RVoid>('/dev/system/system/perms/del', {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 查询全部权限 查询全部权限 GET /system/system/perms/list */
export async function listPerms(options?: { [key: string]: any }) {
  return request<API.Perm[]>('/dev/system/system/perms/list', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 分配权限 分配权限 POST /system/system/roleperms/handle */
export async function roleperms(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.rolepermsParams,
  body: number[],
  options?: { [key: string]: any },
) {
  return request<API.RVoid>('/dev/system/system/roleperms/handle', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    params: {
      ...params,
    },
    data: body,
    ...(options || {}),
  });
}
