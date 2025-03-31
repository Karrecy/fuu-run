/**
 * @see https://umijs.org/docs/max/access#access
 * */
export default function access(initialState: { currentUser?: API.CurrentUser } | undefined) {
  const { currentUser } = initialState ?? {};
  
  return {
    hasPerms: (perm: string) => {
      // 如果是超级管理员(userType === 0)，直接返回 true
      if (currentUser?.user?.userType === 0) {
        return true;
      }
      // 其他用户需要检查具体权限
      return currentUser?.permissions?.includes(perm);
    },

    // 路由权限过滤
    routeFilter: (route: { perms?: string }) => {
      // 如果是超级管理员，可以访问所有路由
      if (currentUser?.user?.userType === 0) {
        return true;
      }
      
      // 如果路由需要权限
      if (route.perms) {
        return currentUser?.permissions?.includes(route.perms);
      }
      
      // 如果路由没有配置权限要求，则默认可以访问
      return true;
    },
  };
}
