package com.karrecy.common.core.service;

/**
 * 脱敏服务
 * 默认管理员不过滤
 */
public interface SensitiveService {

    /**
     * 是否脱敏
     */
    boolean isSensitive();

}
