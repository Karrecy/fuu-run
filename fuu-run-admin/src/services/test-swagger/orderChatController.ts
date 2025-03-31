// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

/** 获取聊天展示信息 获取聊天展示信息 GET /order/chat/initchat/${param0} */
export async function initchat(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.initchatParams,
  options?: { [key: string]: any },
) {
  const { orderId: param0, ...queryParams } = params;
  return request<API.RMapStringObject>(`/dev/order/chat/initchat/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 订单聊天记录分页查询 订单聊天记录分页查询 GET /order/chat/list/${param0} */
export async function pageChat(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.pageChatParams,
  options?: { [key: string]: any },
) {
  const { orderId: param0, ...queryParams } = params;
  return request<API.TableDataInfoOrderChat>(`/dev/order/chat/list/${param0}`, {
    method: 'GET',
    params: {
      ...queryParams,
      pageQuery: undefined,
      ...queryParams['pageQuery'],
    },
    ...(options || {}),
  });
}
