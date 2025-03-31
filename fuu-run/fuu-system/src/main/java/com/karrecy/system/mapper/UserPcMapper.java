package com.karrecy.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.karrecy.common.core.domain.entity.User;
import com.karrecy.common.core.domain.entity.UserPc;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.karrecy.system.domain.bo.UserPcQuery;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 平台管理员表 Mapper 接口
 * </p>
 */
public interface UserPcMapper extends BaseMapper<UserPc> {

    Page<User> selectPageUserList(@Param("page") Page<Object> build, @Param(Constants.WRAPPER) Wrapper<UserPcQuery> userPcWrapper);
}
