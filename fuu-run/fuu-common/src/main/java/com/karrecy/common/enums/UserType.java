package com.karrecy.common.enums;

import com.karrecy.common.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 用户类型
 */
@Getter
@RequiredArgsConstructor
public enum UserType {

    /**
     * 超级管理员
     */
    SUPER_ADMIN(0,"super_admin"),

    /**
     * 校区代理
     */
    SCHOOL_AGENT(1,"school_agent"),

    /**
     * 普通管理员
     */
    COMMON_ADMIN(2,"common_admin"),

    /**
     * 普通用户
     */
    COMMON_USER(3,"common_user"),

    /**
     * 跑腿用户
     */
    RUNNER_USER(4,"runner_agent"),

    /**
     * 系统
     */
    SYSTEM(5,"system");

    private final Integer code;

    private final String userType;

    public static UserType getByCode(Integer code) {
        for (UserType userType : UserType.values()) {
            if (userType.getCode().equals(code)) {
                return userType;
            }
        }
        throw new RuntimeException("根据code匹配用户类型失败-"+code);
    }
    public static UserType getByUserType(String userType) {
        for (UserType type : UserType.values()) {
            if (StringUtils.contains(userType, type.getUserType())) {
                return type;
            }
        }
        throw new RuntimeException("根据string匹配用户类型失败-"+userType);
    }
}
