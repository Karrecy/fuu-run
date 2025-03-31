package com.karrecy.system.service.impl;

import com.karrecy.common.core.service.SensitiveService;
import com.karrecy.common.helper.LoginHelper;
import org.springframework.stereotype.Service;

/**
 * 脱敏服务
 */
@Service
public class SysSensitiveServiceImpl implements SensitiveService {

    /**
     * 是否脱敏
     */
    @Override
    public boolean isSensitive() {
        return !LoginHelper.isAdmin();
    }

}
