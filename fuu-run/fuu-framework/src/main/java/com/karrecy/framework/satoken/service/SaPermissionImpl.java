package com.karrecy.framework.satoken.service;

import cn.dev33.satoken.stp.StpInterface;
import com.karrecy.common.core.domain.model.LoginUser;
import com.karrecy.common.enums.UserType;
import com.karrecy.common.helper.LoginHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * sa-token 权限管理实现类
 */
public class SaPermissionImpl implements StpInterface {


    /**
     * 获取菜单权限列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        LoginUser loginUser = LoginHelper.getLoginUser();
        return new ArrayList<>(loginUser.getMenuPermission());
    }


    /**
     * 获取角色权限列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        LoginUser loginUser = LoginHelper.getLoginUser();
        return Collections.singletonList(loginUser.getUserType());
    }
}
