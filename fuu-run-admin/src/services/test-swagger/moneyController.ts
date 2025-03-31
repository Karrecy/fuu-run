// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

/** 当前用户资金明细查询 当前用户资金明细查询 GET /payment/capital/list */
export async function listCurr(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listCurrParams,
  options?: { [key: string]: any },
) {
  return request<API.TableDataInfoCapitalFlow>('/dev/payment/capital/list', {
    method: 'GET',
    params: {
      ...params,
      capitalFlow: undefined,
      ...params['capitalFlow'],
      pageQuery: undefined,
      ...params['pageQuery'],
    },
    ...(options || {}),
  });
}

/** 提交提现 提交提现 POST /payment/recode */
export async function submitRecode(body: API.MoneyRecode, options?: { [key: string]: any }) {
  return request<API.RVoid>('/dev/payment/recode', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 处理提现 处理提现 PUT /payment/recode/edit */
export async function edit2(body: API.MoneyRecode, options?: { [key: string]: any }) {
  return request<API.RVoid>('/dev/payment/recode/edit', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 最后一次提现 最后一次提现 GET /payment/recode/last */
export async function lastRecode(options?: { [key: string]: any }) {
  return request<API.RMoneyRecode>('/dev/payment/recode/last', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 当前用户钱包查询 当前用户钱包查询 GET /payment/wallet/curr */
export async function wallet(options?: { [key: string]: any }) {
  return request<API.RWallet>('/dev/payment/wallet/curr', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 用户钱包分页查询 用户钱包分页查询 GET /payment/wallet/page */
export async function walletPage(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.walletPageParams,
  options?: { [key: string]: any },
) {
  return request<API.TableDataInfoWallet>('/dev/payment/wallet/page', {
    method: 'GET',
    params: {
      ...params,
      wallet: undefined,
      ...params['wallet'],
      pageQuery: undefined,
      ...params['pageQuery'],
    },
    ...(options || {}),
  });
}

/** 提现申请查询 提现申请查询 GET /payment/withdraw/page */
export async function withdrawPage(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.withdrawPageParams,
  options?: { [key: string]: any },
) {
  return request<API.TableDataInfoMoneyRecode>('/dev/payment/withdraw/page', {
    method: 'GET',
    params: {
      ...params,
      moneyRecode: undefined,
      ...params['moneyRecode'],
      pageQuery: undefined,
      ...params['pageQuery'],
    },
    ...(options || {}),
  });
}
