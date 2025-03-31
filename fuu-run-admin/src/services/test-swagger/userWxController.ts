// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

/** 获取WX用户列表 获取WX用户列表 GET /system/userwx/list */
export async function listWx(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listWxParams,
  options?: { [key: string]: any },
) {
  return request<API.TableDataInfoUser>('/dev/system/userwx/list', {
    method: 'GET',
    params: {
      ...params,
      userWxQuery: undefined,
      ...params['userWxQuery'],
      pageQuery: undefined,
      ...params['pageQuery'],
    },
    ...(options || {}),
  });
}

/** 编辑小程序用户 PUT /system/userwx/edit */
export async function editAgent(
  body: API.UserWx,
  options?: { [key: string]: any }
) {
  return request<API.RVoid>('/dev/system/userwx/edit', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
