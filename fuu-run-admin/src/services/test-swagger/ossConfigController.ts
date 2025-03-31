// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

/** 修改对象存储配置 修改对象存储配置 PUT /system/oss/config */
export async function edit1(body: API.OssConfigBo, options?: { [key: string]: any }) {
  return request<API.RVoid>('/dev/system/oss/config', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 新增对象存储配置 新增对象存储配置 POST /system/oss/config */
export async function add(body: API.OssConfigBo, options?: { [key: string]: any }) {
  return request<API.RVoid>('/dev/system/oss/config', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 获取对象存储配置详细信息 获取对象存储配置详细信息 GET /system/oss/config/${param0} */
export async function getInfo(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getInfoParams,
  options?: { [key: string]: any },
) {
  const { ossConfigId: param0, ...queryParams } = params;
  return request<API.ROssConfigVo>(`/dev/system/oss/config/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 删除对象存储配置 删除对象存储配置 DELETE /system/oss/config/${param0} */
export async function remove3(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.remove3Params,
  options?: { [key: string]: any },
) {
  const { ossConfigIds: param0, ...queryParams } = params;
  return request<API.RVoid>(`/dev/system/oss/config/${param0}`, {
    method: 'DELETE',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 状态修改 状态修改 PUT /system/oss/config/changeStatus */
export async function changeStatus(body: API.OssConfigBo, options?: { [key: string]: any }) {
  return request<API.RVoid>('/dev/system/oss/config/changeStatus', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 查询对象存储配置列表 查询对象存储配置列表 GET /system/oss/config/list */
export async function list2(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.list3Params,
  options?: { [key: string]: any },
) {
  return request<API.TableDataInfoOssConfigVo>('/dev/system/oss/config/list', {
    method: 'GET',
    params: {
      ...params,
      bo: undefined,
      ...params['bo'],
      pageQuery: undefined,
      ...params['pageQuery'],
    },
    ...(options || {}),
  });
}
