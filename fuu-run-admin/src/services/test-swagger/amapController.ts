// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

/** 查询OSS对象存储列表 查询OSS对象存储列表 GET /amap/weather/${param0} */
export async function weather(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.weatherParams,
  options?: { [key: string]: any },
) {
  const { adcode: param0, ...queryParams } = params;
  return request<API.R>(`/dev/amap/weather/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}
