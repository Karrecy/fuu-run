// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

/** 修改用户 修改用户 PUT /system/profile */
export async function updateProfile(body: API.ProfileUpdateDTO, options?: { [key: string]: any }) {
  return request<API.RVoid>('/dev/system/profile', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 绑定邮箱 绑定邮箱 POST /system/profile/bindEmail */
export async function bindEmail(body: Record<string, any>, options?: { [key: string]: any }) {
  return request<API.RVoid>('/dev/system/profile/bindEmail', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 绑定手机号 绑定手机号 GET /system/profile/bindPhone */
export async function bindPhone(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.bindPhoneParams,
  options?: { [key: string]: any },
) {
  return request<API.RString>('/dev/system/profile/bindPhone', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 是否显示手机号按钮 是否显示手机号按钮 GET /system/profile/canReqPhone */
export async function canReqPhone(options?: { [key: string]: any }) {
  return request<API.RBoolean>('/dev/system/profile/canReqPhone', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 发送邮箱验证码 发送邮箱验证码 GET /system/profile/sendEmailCode */
export async function sendEmailCode(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.sendEmailCodeParams,
  options?: { [key: string]: any },
) {
  return request<API.RVoid>('/dev/system/profile/sendEmailCode', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 修改密码 修改密码 PUT /system/profile/updatePwd */
export async function updatePwd(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.updatePwdParams,
  options?: { [key: string]: any },
) {
  return request<API.RVoid>('/dev/system/profile/updatePwd', {
    method: 'PUT',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}
