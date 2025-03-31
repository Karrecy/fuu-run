package com.karrecy.admin.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.secure.BCrypt;
import com.karrecy.common.annotation.RateLimiter;
import com.karrecy.common.constant.CacheConstants;
import com.karrecy.common.core.controller.BaseController;
import com.karrecy.common.core.domain.R;
import com.karrecy.common.core.domain.entity.User;
import com.karrecy.common.core.domain.entity.UserPc;
import com.karrecy.common.core.domain.entity.UserWx;
import com.karrecy.common.core.service.EmailService;
import com.karrecy.common.enums.DeviceType;
import com.karrecy.common.exception.ServiceException;
import com.karrecy.common.helper.LoginHelper;
import com.karrecy.system.domain.bo.ProfileUpdateDTO;
import com.karrecy.system.service.IUserPcService;
import com.karrecy.system.service.IUserService;
import com.karrecy.system.service.IUserWxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/system/profile")
public class ProfileController extends BaseController {

    private final IUserService userService;
    private final IUserPcService userPcService;
    private final IUserWxService userWxService;
    private final EmailService emailService;


    /**
     * 修改密码
     */
    @PutMapping("/updatePwd")
    @SaCheckPermission("system:profile:updatePwd")
    public R<Void> updatePwd(@NotBlank(message = "原密码不可为空") String oldPassword,
                             @NotBlank(message = "新密码不可为空") String newPassword) {
        Long uid = getUserId();
        User userDB = userService.selectUserByUid(uid);
        UserPc userPc = userDB.getUserPc();
        boolean checkpw = BCrypt.checkpw(oldPassword, userPc.getPassword());
        if (!checkpw) {
            throw new ServiceException("原密码错误");
        }
        userPc.setPassword(BCrypt.hashpw(newPassword));
        userPcService.updateById(userPc);
        return R.ok();
    }

    /**
     * 绑定邮箱
     */
    @PostMapping("bindEmail")
    @SaCheckPermission("system:profile:bindEmail")
    private R<Void> bindEmail(@RequestBody Map<String, String> data) {
        String email = data.get("email");
        String code = data.get("code");
        Long uid = getUserId();
        User userDB = userService.selectUserByUid(uid);
        UserPc userPcDB = userDB.getUserPc();
        //修改邮箱
        if (userPcDB.getEmail() != null && userPcDB.getEmail().equals(email)) {
            throw new ServiceException("不可重复绑定");
        }
        // 检查验证码
        boolean verified = emailService.verifyCode(email,code);
        if (!verified) {
            throw new ServiceException("邮箱验证码错误");
        }
        userPcDB.setEmail(email);
        userPcService.updateById(userPcDB);
        return R.ok();
    }

    /**
     * 发送邮箱验证码
     */
    @GetMapping("/sendEmailCode")
    @SaIgnore
    @RateLimiter(time = 60,count = 3)
    public R<Void> sendEmailCode(@NotBlank(message = "邮箱不可为空") String email) {
        // 生成6位随机数字验证码
        emailService.sendEmailCode(email);
        return R.ok();
    }

    /**
     * 修改用户
     */
    @PutMapping
    @SaCheckPermission("system:profile:update")
    public R<Void> updateProfile(@Validated @RequestBody ProfileUpdateDTO profileUpdateDTO) {
        Long uid = getUserId();
        User userDB = userService.selectUserByUid(uid);
        Integer deviceType = userDB.getDeviceType();
        if (Objects.equals(deviceType, DeviceType.PC.getCode())) {
            UserPc userPcDB = userDB.getUserPc();
            userPcDB.setAvatar(profileUpdateDTO.getAvatar());
            userPcDB.setEmailEnable(profileUpdateDTO.getEmailEnable());

            userPcService.updateById(userPcDB);
        }
        if (Objects.equals(deviceType, DeviceType.WX.getCode())) {
            UserWx userWxDB = userDB.getUserWx();
            userWxDB.setAvatar(profileUpdateDTO.getAvatar());
            userWxDB.setNickname(profileUpdateDTO.getNickname());
            userWxService.updateById(userWxDB);
        }
        userDB.setUpdateId(uid);
        userDB.setUpdateTime(LocalDateTime.now());
        userService.updateById(userDB);
        return R.ok();
    }

    /**
     * 绑定手机号
     */
    @GetMapping("/bindPhone")
    @SaCheckPermission("system:profile:bindPhone")
    public R<String> bindPhone(@NotBlank(message = "手机号不可为空") String phoneCode) {

        return userService.bindPhone(phoneCode);
    }
    /**
     * 是否显示手机号按钮
     */
    @GetMapping("/canReqPhone")
    @SaCheckPermission("system:profile:canReqPhone")
    public R<Boolean> canReqPhone() {

        return R.ok(userService.canReqPhone(CacheConstants.PHONE_REQUEST_KEY+LoginHelper.getUserId()));
    }

}
