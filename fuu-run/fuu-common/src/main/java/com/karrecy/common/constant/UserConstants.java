package com.karrecy.common.constant;

/**
 * 用户常量信息
 */
public interface UserConstants {

    /**
     * 男
     */
    Integer USER_GENDER_MAN = 1;

    /**
     * 女
     */
    Integer USER_GENDER_WOMAN = 0;

    /**
     * 不限
     */
    Integer USER_GENDER_UNLIMITED = 1;

    /**
     * 用户正常状态
     */
    String USER_NORMAL = "0";

    /**
     * 用户封禁状态
     */
    String USER_DISABLE = "1";

    /**
     * 用户名长度限制
     */
    int USERNAME_MIN_LENGTH = 2;
    int USERNAME_MAX_LENGTH = 20;

    /**
     * 密码长度限制
     */
    int PASSWORD_MIN_LENGTH = 5;
    int PASSWORD_MAX_LENGTH = 20;

    /**
     * 管理员ID
     */
    Long ADMIN_ID = 100L;
}
