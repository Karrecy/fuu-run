package com.karrecy.system.service;

import com.karrecy.common.core.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * 用户权限处理
 */
@RequiredArgsConstructor
@Service
public class PermissionService {

    private final IPermService permService;


    /**
     * 获取菜单数据权限
     *
     * @param user 用户信息
     * @return 菜单权限信息
     */
    public Set<String> getMenuPermission(User user) {
        Set<String> perms = new HashSet<>();
        // 管理员拥有所有权限
        if (user.isAdmin()) {
            perms.add("*:*:*");
        } else {
            perms.addAll(permService.selectMenuPermsByUserType(user.getUserType()));
        }
        return perms;
    }
}
