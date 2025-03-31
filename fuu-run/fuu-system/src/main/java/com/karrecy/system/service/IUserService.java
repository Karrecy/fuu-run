package com.karrecy.system.service;

import com.karrecy.common.core.domain.R;
import com.karrecy.common.core.domain.entity.StatisticsDaily;
import com.karrecy.common.core.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.karrecy.common.enums.DeviceType;

import java.time.LocalDate;

/**
 * <p>
 * 全局用户表 服务类
 * </p>
 */
public interface IUserService extends IService<User> {

    /**
     * 构建全局用户
     * @return
     */
    User buildUser(Integer userType, DeviceType pc);

    /**
     * 判断当前用户是否可被操作
     * @param uid
     */
    void checkUserAllowed(Long uid);

    /**
     * 根据uid查询全局用户
     * @param uid
     * @return
     */
    User selectUserByUid(Long uid);


    /**
     * 绑定手机号
     * @param phoneCode
     */
    R<String> bindPhone(String phoneCode);

    /**
     * 获取手机号防刷
     * @param key
     * @return
     */
    boolean canReqPhone(String key);

    /**
     * 统计用户数据
     * @param statistics
     * @param lastDay
     * @param today
     */
    void calculateUserStatistics(StatisticsDaily statistics, LocalDate lastDay, LocalDate today);
}
