package com.karrecy.system.service;

import com.karrecy.common.core.domain.entity.Perm;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;

/**
 * <p>
 * 权限表 服务类
 * </p>
 */
public interface IPermService extends IService<Perm> {

    Collection<String> selectMenuPermsByUserType(Integer userType);
}
