package com.karrecy.admin.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ArrayUtil;
import com.karrecy.common.core.controller.BaseController;
import com.karrecy.common.core.domain.PageQuery;
import com.karrecy.common.core.domain.R;
import com.karrecy.common.core.domain.entity.User;
import com.karrecy.common.core.domain.entity.UserPc;
import com.karrecy.common.core.page.TableDataInfo;
import com.karrecy.common.enums.DeviceType;
import com.karrecy.common.enums.Status;
import com.karrecy.common.utils.StringUtils;
import com.karrecy.system.domain.bo.UserPcQuery;
import com.karrecy.system.domain.po.Wallet;
import com.karrecy.system.service.IUserPcService;
import com.karrecy.system.service.IUserService;
import com.karrecy.system.service.IWalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/system/userpc")
public class UserPcController extends BaseController {

    private final IUserService userService;
    private final IUserPcService userPcService;
    private final IWalletService walletService;
    /**
     * 重设密码
     */
    @PutMapping("/resetPwd/{uId}")
    @SaCheckPermission("system:userpc:resetPwd")
    public R<Void> resetPwd(@PathVariable Long uId) {
        User user = userService.selectUserByUid(uId);
        UserPc userPc = user.getUserPc();
        userPc.setPassword(BCrypt.hashpw(userPc.getPhone()));
        return toAjax(userPcService.updatePcUser(userPc));
    }

    /**
     * 新增校区代理
     * @param userPc
     * @return
     */
    @PostMapping("/add")
    @SaCheckPermission("system:userpc:add")
    public R<Void> addAgent(@Validated @RequestBody UserPc userPc) {
        if (!userPcService.checkUserNameUnique(userPc)) {
            return R.fail("新增用户'" + userPc.getUsername() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(userPc.getPhone()) && !userPcService.checkPhoneUnique(userPc)) {
            return R.fail("新增用户'" + userPc.getPhone() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(userPc.getEmail()) && !userPcService.checkEmailUnique(userPc)) {
            return R.fail("新增用户'" + userPc.getPhone() + "'失败，邮箱已存在");
        }
        userPc.setId(null);
        userPc.setPassword(BCrypt.hashpw(userPc.getPassword()));
        User user = userService.buildUser(userPc.getUserType(), DeviceType.PC);
        user.setUserPc(userPc);
        userPcService.insertUser(user);
        //新建钱包
        Wallet wallet = new Wallet()
                .setUid(user.getUid())
                .setBalance(BigDecimal.valueOf(0))
                .setWithdrawn(BigDecimal.valueOf(0))
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());
        walletService.save(wallet);
        return R.ok();
    }

    /**
     * 修改pc用户
     * @param userPc
     * @return
     */
    @PutMapping("/edit")
    @SaCheckPermission("system:userpc:edit")
    public R<Void> editAgent(@RequestBody UserPc userPc) {
        userService.checkUserAllowed(userPc.getUid());
        User user = userService.selectUserByUid(userPc.getUid());
        UserPc userPcDB = user.getUserPc();

        if (!userPcDB.getUsername().equals(userPc.getUsername()) && !userPcService.checkUserNameUnique(userPc)) {
            return R.fail("修改用户'" + userPc.getUsername() + "'失败，登录账号已存在");
        } else if (!userPcDB.getPhone().equals(userPc.getPhone()) && StringUtils.isNotEmpty(userPc.getPhone()) && !userPcService.checkPhoneUnique(userPc)) {
            return R.fail("修改用户'" + userPc.getUsername() + "'失败，手机号码已存在");
        }
        if (userPc.getStatus().equals(Status.DISABLE.getCode())) {
            StpUtil.kickout(user.getLoginId());
        }
        return toAjax(userPcService.updatePcUser(userPc));
    }

    /**
     * 获取PC用户列表
     */
    @GetMapping("/list")
    @SaCheckPermission("system:userpc:list")
    public TableDataInfo<User> listPc(UserPcQuery userPcQuery, PageQuery pageQuery) {
        return userPcService.selectPageUserPcList(userPcQuery, pageQuery);
    }

    /**
     * 删除用户
     *
     * @param uIds 角色ID串
     */
    @DeleteMapping("/{uIds}")
    @SaCheckPermission("system:userpc:delete")
    public R<Void> remove(@PathVariable Long[] uIds) {
        if (ArrayUtil.contains(uIds, getUserId())) {
            return R.fail("当前用户不能删除");
        }
        for (Long uid : uIds) {
            userService.checkUserAllowed(uid);
        }
        return toAjax(userPcService.deleteUserByIds(uIds));
    }



}
