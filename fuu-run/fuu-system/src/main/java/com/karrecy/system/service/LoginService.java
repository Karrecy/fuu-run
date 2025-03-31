package com.karrecy.system.service;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.util.WxMaConfigHolder;
import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.karrecy.common.core.domain.entity.User;
import com.karrecy.common.core.domain.entity.UserPc;
import com.karrecy.common.core.domain.entity.UserWx;
import com.karrecy.common.core.domain.model.EmailBody;
import com.karrecy.common.core.domain.model.LoginBody;
import com.karrecy.common.core.domain.model.LoginUser;
import com.karrecy.common.core.service.EmailService;
import com.karrecy.common.enums.DeviceType;
import com.karrecy.common.enums.Status;
import com.karrecy.common.enums.UserType;
import com.karrecy.common.exception.ServiceException;
import com.karrecy.common.exception.user.UserPasswordNotMatchException;
import com.karrecy.common.helper.LoginHelper;
import com.karrecy.common.utils.DateUtils;
import com.karrecy.common.utils.ServletUtils;
import com.karrecy.common.utils.ip.RegionUtils;
import com.karrecy.system.domain.po.Wallet;
import com.karrecy.system.mapper.UserMapper;
import com.karrecy.common.exception.user.UserException;
import com.karrecy.system.mapper.UserPcMapper;
import com.karrecy.system.mapper.UserWxMapper;
import com.karrecy.system.mapper.WalletMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * 登录服务
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class LoginService {

    private final UserMapper userMapper;
    private final UserPcMapper userPcMapper;
    private final UserWxMapper userWxMapper;
    private final PermissionService permissionService;

    private final WxMaService wxMaService;
    private final IUserService userService;
    private final IUserWxService userWxService;

    private final IUserWxService walletService;
    private final WalletMapper walletMapper;

    private final EmailService emailService;
    /**
     * PC端登录
     * @param loginBody
     * @return
     */
    public String pcLogin(LoginBody loginBody) {
        //查询用户
        User user = loadUserByUsername(loginBody.getUsername());
        //检查密码
        checkLogin(user,loginBody);
        //构建LoginUser
        LoginUser loginUser = buildLoginUser(user);
        //生成token
        LoginHelper.loginByDevice(loginUser);
        //更新User表
        recordLoginInfo(user.getUid());
        return StpUtil.getTokenValue();
    }
    /**
     * PC端邮箱登录
     * @param emailBody
     * @return
     */
    public String emailLogin(EmailBody emailBody) {
        //检查验证码
        boolean verified = emailService.verifyCode(emailBody.getEmail(), emailBody.getCode());
        if (!verified) throw new ServiceException("验证码错误");

        //查询用户
        User user = loadUserByEmail(emailBody.getEmail());
        //构建LoginUser
        LoginUser loginUser = buildLoginUser(user);
        //生成token
        LoginHelper.loginByDevice(loginUser);
        //更新User表
        recordLoginInfo(user.getUid());
        return StpUtil.getTokenValue();
    }

    /**
     * 小程序登录
     * @param xcxCode
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String xcxLogin(String xcxCode) {
        String openid;
        try {
            WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(xcxCode);
            openid = session.getOpenid();
        } catch (WxErrorException e) {
            log.error(e.getMessage(), e);
            throw new ServiceException("获取openid失败");
        } finally {
            WxMaConfigHolder.remove();//清理ThreadLocal
        }
        UserWx userWx = userWxMapper.selectOne(new LambdaQueryWrapper<UserWx>()
                .eq(UserWx::getOpenid, openid));
        User user;
        if (ObjectUtil.isNull(userWx)) {
            //自动注册
            user = userService.buildUser(UserType.COMMON_USER.getCode(), DeviceType.WX);
            userMapper.insert(user);
            userWx = userWxService.createNewUser();
            userWx.setUid(user.getUid());
            userWx.setOpenid(openid);
            userWxMapper.insert(userWx);
            //新建钱包
            Wallet wallet = new Wallet()
                    .setUid(user.getUid())
                    .setBalance(BigDecimal.valueOf(0))
                    .setWithdrawn(BigDecimal.valueOf(0))
                    .setCreateTime(LocalDateTime.now())
                    .setUpdateTime(LocalDateTime.now());
            walletMapper.insert(wallet);
        }
        else {
            //正式查询
            user = userMapper.selectById(userWx.getUid());
        }
        user.setUserWx(userWx);

        //构建LoginUser
        LoginUser loginUser = buildLoginUser(user);
        //生成token
        LoginHelper.loginByDevice(loginUser);
        //更新User表
        recordLoginInfo(user.getUid());
        return StpUtil.getTokenValue();

    }
    private void checkLogin(User user, LoginBody loginBody) {
        boolean checkpw = BCrypt.checkpw(loginBody.getPassword(), user.getUserPc().getPassword());
        if (!checkpw) {
            throw new UserPasswordNotMatchException();
        }
    }

    private void recordLoginInfo(Long uid) {
        String clientIP = ServletUtils.getClientIP();
        User user = new User();
        user.setUid(uid);
        user.setLoginTime(DateUtils.getNowDateTime());
        user.setLoginIp(clientIP);
        user.setLoginRegion(RegionUtils.getCityInfo(clientIP));
        userMapper.updateById(user);
    }

    private LoginUser buildLoginUser(User user) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUid(user.getUid());
        loginUser.setDeviceType(DeviceType.getByCode(user.getDeviceType()).getDevice());
        loginUser.setUserType(UserType.getByCode(user.getUserType()).getUserType());
        loginUser.setMenuPermission(permissionService.getMenuPermission(user));
        String clientIP = ServletUtils.getClientIP();
        loginUser.setLoginIp(clientIP);
        loginUser.setLoginRegion(RegionUtils.getCityInfo(clientIP));
        loginUser.setLoginTime(LocalDateTime.now());
        return loginUser;
    }


    private User loadUserByUsername(String username) {
        UserPc userPc = userPcMapper.selectOne(new LambdaQueryWrapper<UserPc>()
                        .eq(UserPc::getUsername, username));
        if (ObjectUtil.isNull(userPc)) {
            log.info("登录用户：{} 不存在.", username);
            throw new UserException("user.not.exists", username);
        } else if (Status.DISABLE.getCode().equals(userPc.getStatus())) {
            log.info("登录用户：{} 已被停用.", username);
            throw new UserException("user.blocked", username);
        }

        //正式查询
        User user = userMapper.selectById(userPc.getUid());
        if (ObjectUtil.isNull(user)) {
            log.info("pc登录用户：{} 不存在.", username);
            throw new UserException("user.not.exists", username);
        }
        user.setUserPc(userPc);
        return user;
    }
    private User loadUserByEmail(String email) {
        UserPc userPc = userPcMapper.selectOne(new LambdaQueryWrapper<UserPc>()
                .eq(UserPc::getEmail, email));
        if (ObjectUtil.isNull(userPc)) {
            log.info("登录用户：{} 不存在.", email);
            throw new UserException("user.not.exists", email);
        } else if (Status.DISABLE.getCode().equals(userPc.getStatus())) {
            log.info("登录用户：{} 已被停用.", email);
            throw new UserException("user.blocked", email);
        }

        //正式查询
        User user = userMapper.selectById(userPc.getUid());
        if (ObjectUtil.isNull(user)) {
            log.info("pc登录用户：{} 不存在.", email);
            throw new UserException("user.not.exists", email);
        }
        user.setUserPc(userPc);
        return user;
    }
}
