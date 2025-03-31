// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

/** 获取用户信息 获取用户信息 GET /getInfo */
export async function currentUser(options?: { [key: string]: any }) {
  return request<API.RMapStringObject>('/dev/getInfo', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 登录方法 登录方法 POST /login */
export async function login(body: API.LoginBody, options?: { [key: string]: any }) {
  return request<API.RMapStringObject>('/dev/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /statistic */
export async function statistic(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.statisticParams,
  options?: { [key: string]: any },
) {
  return request<API.TableDataInfoStatisticsDaily>('/dev/statistic', {
    method: 'GET',
    params: {
      ...params,
      statisticsDaily: undefined,
      ...params['statisticsDaily'],
      pageQuery: undefined,
      ...params['pageQuery'],
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /test */
export async function testLogin(options?: { [key: string]: any }) {
  return request<API.R>('/dev/test', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 小程序检查是否需要登录 小程序检查是否需要登录 GET /xcxCheckLogin */
export async function xcxCheckLogin(options?: { [key: string]: any }) {
  return request<API.R>('/dev/xcxCheckLogin', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 登录方法 登录方法 POST /xcxLogin */
export async function xcxLogin(body: string, options?: { [key: string]: any }) {
  return request<API.R>('/dev/xcxLogin', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 邮箱验证码登录 POST /emailLogin */
export async function emailLogin(body: API.EmailLoginBody, options?: { [key: string]: any }) {
  return request<API.RMapStringObject>('/dev/emailLogin', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
/** 获取轮播图列表 GET /carousel */
export async function getCarouselImages(options?: { [key: string]: any }) {
  return request<API.RListString>('/dev/carousel', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 更新指定位置的轮播图 PUT /carousel/{index} */
export async function updateCarouselImage(
  index: number,
  base64Content: string,
  options?: { [key: string]: any }
) {
  return request<any>(`/dev/carousel/${index}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: { base64Content },
    ...(options || {}),
  });
}

/** 添加轮播图 POST /carousel */
export async function addCarouselImage(
  base64Content: string,
  options?: { [key: string]: any }
) {
  return request<API.R>('/dev/carousel', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: { base64Content },
    ...(options || {}),
  });
}

/** 删除指定位置的轮播图 DELETE /carousel/{index} */
export async function deleteCarouselImage(
  index: number,
  options?: { [key: string]: any }
) {
  return request<any>(`/dev/carousel/${index}`, {
    method: 'DELETE',
    ...(options || {}),
  });
} 