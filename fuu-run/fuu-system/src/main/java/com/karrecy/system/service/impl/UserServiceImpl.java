package com.karrecy.system.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.karrecy.common.constant.CacheConstants;
import com.karrecy.common.constant.UserConstants;
import com.karrecy.common.core.domain.R;
import com.karrecy.common.core.domain.entity.StatisticsDaily;
import com.karrecy.common.core.domain.entity.User;
import com.karrecy.common.core.domain.entity.UserPc;
import com.karrecy.common.core.domain.entity.UserWx;
import com.karrecy.common.enums.DeviceType;
import com.karrecy.common.enums.Status;
import com.karrecy.common.enums.UserType;
import com.karrecy.common.exception.ServiceException;
import com.karrecy.common.helper.LoginHelper;
import com.karrecy.common.utils.ServletUtils;
import com.karrecy.common.utils.ip.RegionUtils;
import com.karrecy.common.utils.redis.RedisUtils;
import com.karrecy.system.mapper.UserMapper;
import com.karrecy.system.mapper.UserPcMapper;
import com.karrecy.system.mapper.UserWxMapper;
import com.karrecy.system.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 全局用户表 服务实现类
 * </p>
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final UserMapper userMapper;
    private final UserPcMapper userPcMapper;
    private final UserWxMapper userWxMapper;

    private final WxMaService wxMaService;

    //用户一天内最多请求手机号次数
    private static final int MAX_ATTEMPTS = 2;

    /**
     * 构建全局用户
     * @return
     */
    @Override
    public User buildUser(Integer userType, DeviceType deviceType) {
        String clientIP = ServletUtils.getClientIP();
        Long userId = LoginHelper.getUserId() == null ? 0L : LoginHelper.getUserId();
        return new User()
                .setUserType(userType)
                .setDeviceType(deviceType.getCode())
                .setLoginIp(clientIP)
                .setLoginRegion(RegionUtils.getCityInfo(clientIP))
                .setLoginTime(LocalDateTime.now())
                .setCreateTime(LocalDateTime.now())
                .setCreateId(userId)
                .setUpdateTime(LocalDateTime.now())
                .setUpdateId(userId);
    }

    /**
     * 判断当前用户是否可被操作
     * @param uid
     */
    @Override
    public void checkUserAllowed(Long uid) {
        if (ObjectUtil.isNotNull(uid) && UserConstants.ADMIN_ID.equals(uid)) {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    /**
     * 根据uid查询全局用户
     * @param uid
     * @return
     */
    @Override
    public User selectUserByUid(Long uid) {
        User user = userMapper.selectById(uid);
        if (Objects.equals(user.getDeviceType(), DeviceType.PC.getCode())) {
            UserPc userPc = userPcMapper.selectOne(new LambdaQueryWrapper<UserPc>()
                    .eq(UserPc::getUid, uid));
            user.setUserPc(userPc);
        }
        else if (Objects.equals(user.getDeviceType(), DeviceType.WX.getCode())) {
            UserWx userWx = userWxMapper.selectOne(new LambdaQueryWrapper<UserWx>()
                    .eq(UserWx::getUid, uid));
            user.setUserWx(userWx);
        }
        return user;
    }

    /**
     * 绑定手机号
     * @param phoneCode
     */
    @Override
    public R<String> bindPhone(String phoneCode) {
        Long uid = LoginHelper.getUserId();
        UserWx userWxDB = userWxMapper.selectOne(new LambdaQueryWrapper<UserWx>().eq(UserWx::getUid, uid));

        String key = CacheConstants.PHONE_REQUEST_KEY +  uid;

        if (!canReqPhone(key)) {
            return R.fail("一天内最多更改"+MAX_ATTEMPTS+"次");
        }
        try {
            WxMaPhoneNumberInfo phoneNoInfo = wxMaService.getUserService().getPhoneNoInfo(phoneCode);
            String phone = phoneNoInfo.getPurePhoneNumber();

            // 增加计数
            RedisUtils.incrAtomicValue(key);

            userWxDB.setPhone(phone);
            userWxDB.setCanOrder(Status.OK.getCode()); //可以下单了
            userWxMapper.updateById(userWxDB);
        }
        catch (Exception e) {
            return R.fail(e.getMessage());
        }
        return R.ok("获取成功");
    }

    /**
     * 获取手机号防刷
     * @param key
     * @return
     */
    @Override
    public boolean canReqPhone(String key) {
        Integer count = RedisUtils.getCacheObject(key);
        if (Objects.isNull(count)) {
            RedisUtils.setCacheObject(key, 1, getRemainingSecondsToday());
            return true;
        }
        return count < MAX_ATTEMPTS;
    }

    /**
     * 统计用户数据
     * @param statistics
     * @param lastDay
     * @param today
     */
    @Override
    public void calculateUserStatistics(StatisticsDaily statistics, LocalDate lastDay, LocalDate today) {
        List<User> users = userMapper.selectList(
                new LambdaQueryWrapper<User>()
                        .between(User::getCreateTime, lastDay, today)
                        .eq(User::getDeviceType, DeviceType.WX.getCode())
        );
        Integer newUsers = 0;
        Integer newRunners = 0;
        if (ObjectUtil.isEmpty(users)) return;
        for (User user : users) {
            if (ObjectUtil.equals(user.getUserType(), UserType.RUNNER_USER.getCode())) {
                newRunners++;
            }
            if (ObjectUtil.equals(user.getUserType(), UserType.COMMON_USER.getCode())) {
                newUsers++;
            }
        }
        statistics.setNewUsers(newUsers);
        statistics.setNewRunners(newRunners);
    }

    /**
     * 计算从当前时间到午夜剩余的秒数
     *
     * @return 剩余秒数
     */
    private Duration getRemainingSecondsToday() {
        LocalDateTime now = LocalDateTime.now(); // 当前时间
        LocalDateTime midnight = LocalDateTime.of(now.toLocalDate(), LocalTime.MAX); // 当天午夜
        Duration duration = Duration.between(now, midnight); // 计算时间差
        return duration;
    }


}
