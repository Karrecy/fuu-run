// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

/** 支付成功回调 支付成功回调 POST /notify/payNotify */
export async function paySuccessNotify(body: string, options?: { [key: string]: any }) {
  return request<string>('/dev/notify/payNotify', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 退款成功回调 退款成功回调 POST /notify/refundNotify */
export async function refundNotify(body: string, options?: { [key: string]: any }) {
  return request<string>('/dev/notify/refundNotify', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
