package com.karrecy.common.constant;

/**
 * 缓存组名称常量
 */
public interface CacheNames {



    /**
     * OSS内容
     */
    String SYS_OSS = "sys_oss#30d";

    /**
     * OSS配置
     */
    String SYS_OSS_CONFIG = "sys_oss_config";

    /**
     * 在线用户
     */
    String ONLINE_TOKEN = "online_tokens";


    /**
     * 活跃用户
     */
    String DAILY_AU_USER = "daily_au_user:";

    /**
     * 活跃跑腿
     */
    String DAILY_AU_RUNNER = "daily_au_runner:";

    /**
     * 访问用户
     */
    String DAILY_UV = "daily_uv:";

    /**
     * 访问用户
     */
    String DAILY_TOTAL_VISITS = "daily_total_visits:";

    /**
     * 恶意请求数
     */
    String MALICIOUS_REQUESTS = "malicious_requests:";

    /**
     * 邮箱验证
     */
    String EMAIL_CODE = "email_code:";
}
