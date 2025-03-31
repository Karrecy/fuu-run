package com.karrecy.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.karrecy.common.core.domain.entity.User;
import com.karrecy.common.core.domain.entity.UserWx;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.karrecy.system.domain.bo.UserPcQuery;
import com.karrecy.system.domain.bo.UserWxQuery;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 小程序普通用户表 Mapper 接口
 * </p>
 */
public interface UserWxMapper extends BaseMapper<UserWx> {

    Page<User> selectPageUserList(@Param("page") Page<Object> build, @Param(Constants.WRAPPER) Wrapper<UserWxQuery> userWxWrapper);
}
