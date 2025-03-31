package com.karrecy.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.karrecy.common.config.FuuConfig;
import com.karrecy.common.core.domain.PageQuery;
import com.karrecy.common.core.domain.entity.User;
import com.karrecy.common.core.domain.entity.UserWx;
import com.karrecy.common.core.page.TableDataInfo;
import com.karrecy.common.utils.StringUtils;
import com.karrecy.system.domain.bo.UserWxQuery;
import com.karrecy.system.mapper.UserMapper;
import com.karrecy.system.mapper.UserWxMapper;
import com.karrecy.system.service.IUserWxService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 小程序普通用户表 服务实现类
 * </p>
 */
@Service
@RequiredArgsConstructor
public class UserWxServiceImpl extends ServiceImpl<UserWxMapper, UserWx> implements IUserWxService {

    private final UserWxMapper userWxMapper;
    private final UserMapper userMapper;
    private final FuuConfig fuuConfig;

    /**
     * 创建新用户
     * @return
     */
    @Override
    public UserWx createNewUser() {
        return new UserWx()
                .setAvatar("http://static.singoval.com/karrecy-fuu-run/2025/03/06/a47d546a446e40a3aba137625ac362ddjpg")
                .setNickname("用户"+getUserCount())
                .setPhone(null)
                .setCanOrder(0)
                .setCanTake(0)
                .setIsRunner(0)
                .setCreditScore(fuuConfig.getCreditUpperLimit())
                .setPoints(0);
    }

    private int getUserCount() {
        List<User> users = userMapper.selectList(new QueryWrapper<>());
        return users.size();
    }

    @Override
    public UserWx getByUid(Long uid) {
        return userWxMapper.selectOne(new LambdaQueryWrapper<UserWx>().eq(UserWx::getUid,uid));
    }

    /**
     * 获取WX用户列表
     * @param userWxQuery
     * @param pageQuery
     * @return
     */
    @Override
    public TableDataInfo<User> selectPageUserWxList(UserWxQuery userWxQuery, PageQuery pageQuery) {
        Page<User> page = userWxMapper.selectPageUserList(pageQuery.build(), this.buildQueryWrapper(userWxQuery));
        return TableDataInfo.build(page);
    }

    private Wrapper<UserWxQuery> buildQueryWrapper(UserWxQuery userWxQuery) {
        // 创建 QueryWrapper
        QueryWrapper<UserWxQuery> wrapper = new QueryWrapper<>();

        wrapper.eq(ObjectUtil.isNotNull(userWxQuery.getUid()), "u.uid", userWxQuery.getUid());
        wrapper.eq(ObjectUtil.isNotNull(userWxQuery.getGender()), "userWx.gender", userWxQuery.getGender());
        wrapper.eq(ObjectUtil.isNotNull(userWxQuery.getIsRunner()), "userWx.is_runner", userWxQuery.getIsRunner());
        wrapper.eq(ObjectUtil.isNotNull(userWxQuery.getCanOrder()), "userWx.can_order", userWxQuery.getCanOrder());
        wrapper.eq(ObjectUtil.isNotNull(userWxQuery.getCanTake()), "userWx.can_take", userWxQuery.getCanTake());
        wrapper.eq(ObjectUtil.isNotNull(userWxQuery.getSchoolId()), "userWx.school_id", userWxQuery.getSchoolId());
        wrapper.eq(StringUtils.isNotBlank(userWxQuery.getOpenid()), "userWx.openid", userWxQuery.getOpenid());
        wrapper.eq(StringUtils.isNotBlank(userWxQuery.getPhone()), "userWx.phone", userWxQuery.getPhone());
        wrapper.like(StringUtils.isNotBlank(userWxQuery.getNickname()), "userWx.nickname", userWxQuery.getNickname());
        wrapper.like(StringUtils.isNotBlank(userWxQuery.getRealname()), "userWx.realname", userWxQuery.getRealname());
        // 创建时间
        Map<String, Object> params = userWxQuery.getParams();
        if (params.get("createTimeBegin") != null) {
            wrapper.between("u.create_time", params.get("createTimeBegin"), params.get("createTimeEnd"));
        }
        // 上次登录时间
        if (params.get("loginTimeBegin") != null) {
            wrapper.between("u.login_time", params.get("loginTimeBegin"), params.get("loginTimeEnd"));
        }
        wrapper.like(StringUtils.isNotBlank(userWxQuery.getLoginRegion()), "u.login_region", userWxQuery.getLoginRegion());
        wrapper.eq(ObjectUtil.isNotNull(userWxQuery.getUserType()), "u.user_type", userWxQuery.getUserType());
        wrapper.eq(ObjectUtil.isNotNull(userWxQuery.getCreateId()), "u.create_id", userWxQuery.getCreateId());
        if (params.get("updateTimeBegin") != null) {
            wrapper.between("u.update_time", params.get("updateTimeBegin"), params.get("updateTimeEnd"));
        }



        return wrapper;
    }

}
