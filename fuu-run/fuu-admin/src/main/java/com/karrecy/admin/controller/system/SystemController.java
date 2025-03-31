package com.karrecy.admin.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.karrecy.common.config.FuuConfig;
import com.karrecy.common.core.controller.BaseController;
import com.karrecy.common.core.domain.R;
import com.karrecy.common.core.domain.entity.Perm;
import com.karrecy.common.core.domain.entity.RolePerm;
import com.karrecy.common.core.domain.entity.User;
import com.karrecy.common.enums.UserType;
import com.karrecy.framework.web.Server;
import com.karrecy.system.service.IPermService;
import com.karrecy.system.service.IRolePermService;
import com.karrecy.system.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 对象存储配置
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/system")
public class SystemController extends BaseController {

    private final FuuConfig fuuConfig;
    private final IPermService permService;
    private final IRolePermService rolePermService;
    private final IUserService userService;

    /**
     * 查询全局配置
     */
    @GetMapping("monitor")
    @SaCheckPermission("system:system:monitor:get")
    public R<Server> monitor() throws Exception
    {
        Server server = new Server();
        server.copyTo();
        return R.ok(server);
    }
    /**
     * 修改全局配置
     */
    @PostMapping("/config/edit")
    @SaCheckPermission("system:system:config:edit")
    public R<Void> edit(@RequestBody FuuConfig config) {
        fuuConfig.setName(config.getName());
        fuuConfig.setVersion(config.getVersion());
        fuuConfig.setCopyrightYear(config.getCopyrightYear());
        fuuConfig.setPayCancelTtl(config.getPayCancelTtl());
        fuuConfig.setAutoCompleteTtl(config.getAutoCompleteTtl());
        fuuConfig.setCompletionImagesLimit(config.getCompletionImagesLimit());
        fuuConfig.setCreditUpperLimit(config.getCreditUpperLimit());
        fuuConfig.setCreditLowerLimit(config.getCreditLowerLimit());
        fuuConfig.setCreditDeduction(config.getCreditDeduction());
        fuuConfig.setMaxAddress(config.getMaxAddress());
        return R.ok();
    }
    /**
     * 查询全部权限
     */
    @GetMapping("/perms/list")
    @SaCheckPermission("system:system:perms:list")
    public List<Perm> listPerms() {
        // 1. 查询所有权限
        List<Perm> permList = permService.list(new LambdaQueryWrapper<>());
        // 2. 构建 ID -> Perm 映射表
        Map<Long, Perm> permMap = permList.stream().collect(Collectors.toMap(Perm::getId, p -> p));
        // 3. 构建树结构
        List<Perm> rootList = new ArrayList<>();
        for (Perm perm : permList) {
            if (perm.getParentId() == null || perm.getParentId() == 0) {
                // 根节点
                rootList.add(perm);
            } else {
                // 子节点，加入父节点的 children
                Perm parent = permMap.get(perm.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(perm);
                }
            }
        }
        return rootList;
    }
    /**
     * 根据角色查询权限
     */
    @GetMapping("/perms/{userType}")
    @SaCheckPermission("system:system:perms:get")
    public R<List<Perm>> list(@PathVariable Integer userType) {
        if (userType.equals(UserType.SUPER_ADMIN.getCode())) {
            return R.ok(permService.list());
        }
        List<RolePerm> listRolePerm = rolePermService.list(new LambdaQueryWrapper<RolePerm>()
                .eq(RolePerm::getRoleId, userType));
        if (listRolePerm.isEmpty()) {
            return R.ok(new ArrayList<>());
        }
        List<Long> collect = listRolePerm.stream().map(RolePerm::getPermId).collect(Collectors.toList());
        // 1. 查询所有权限
//        List<Perm> permList = permService.listByIds(collect);
        List<Perm> permList = permService.list(new LambdaQueryWrapper<Perm>()
                .in(Perm::getId, collect)
                .orderByAsc(Perm::getSort));
        return R.ok(permList);
    }
    /**
     * 分配权限
     */
    @PostMapping("/roleperms/handle")
    @SaCheckPermission("system:system:roleperms:handle")
    public R<Void> roleperms(Integer userType, @RequestBody List<Long> perms) {
        rolePermService.remove(new LambdaQueryWrapper<RolePerm>().eq(RolePerm::getRoleId, userType));
        for (Long permId : perms) {
            RolePerm rolePerm = new RolePerm();
            rolePerm.setRoleId(Long.valueOf(userType));
            rolePerm.setPermId(permId);
            rolePermService.save(rolePerm);
        }
        kickout(userType);
        return R.ok();
    }

    /**
     * 踢下线，重新登录才能重新获取最新权限
     */
    @Async
    public void kickout(Integer userType) {
        String userTypeStr = UserType.getByCode(userType).getUserType();
        System.out.println(userTypeStr);
        List<User> list = userService.list(new LambdaQueryWrapper<User>()
                .eq(User::getUserType, userType));
        for (User user : list) {
            StpUtil.logout(userTypeStr + ":" + user.getUid());
        }
    }
    /**
     * 添加权限
     */
    @PostMapping("/perms/add")
    @SaCheckPermission("system:system:perms:add")
    public R<Void> roleperms(@RequestBody Perm perm) {
        permService.save(perm);
        return R.ok();
    }
    /**
     * 删除权限
     */
    @DeleteMapping("/perms/del")
    @SaCheckPermission("system:system:perms:del")
    public R<Void> rolepermsDel(@RequestBody List<Long> perms) {
        permService.removeBatchByIds(perms);
        rolePermService.remove(new LambdaQueryWrapper<RolePerm>()
                .in(RolePerm::getPermId,perms));
        return R.ok();
    }
}
