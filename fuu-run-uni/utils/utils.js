export const tansParams = (params) => {
  let str = '';
  for (let key in params) {
    // 只有当参数值不为 undefined 或 null 时才拼接
    if (params[key] !== undefined && params[key] !== null) {
      str += `${key}=${encodeURIComponent(params[key])}&`;
    }
  }
  return str.slice(0, -1);  // 去掉最后的 "&"
};


