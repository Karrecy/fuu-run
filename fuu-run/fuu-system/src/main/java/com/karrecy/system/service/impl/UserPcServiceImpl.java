package com.karrecy.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.karrecy.common.core.domain.PageQuery;
import com.karrecy.common.core.domain.entity.User;
import com.karrecy.common.core.domain.entity.UserPc;
import com.karrecy.common.core.domain.entity.UserWx;
import com.karrecy.common.core.page.TableDataInfo;
import com.karrecy.common.helper.LoginHelper;
import com.karrecy.common.utils.StringUtils;
import com.karrecy.system.domain.bo.UserPcQuery;
import com.karrecy.system.mapper.UserMapper;
import com.karrecy.system.mapper.UserPcMapper;
import com.karrecy.system.mapper.UserWxMapper;
import com.karrecy.system.service.IUserPcService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 平台管理员表 服务实现类
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserPcServiceImpl extends ServiceImpl<UserPcMapper, UserPc> implements IUserPcService {

    private final UserPcMapper userPcMapper;
    private final UserMapper userMapper;
    private final UserWxMapper userWxMapper;

    /**
     * 校验用户名称是否唯一
     * @param userPc
     * @return
     */
    @Override
    public boolean checkUserNameUnique(UserPc userPc) {
        boolean exist = userPcMapper.exists(new LambdaQueryWrapper<UserPc>()
                .eq(UserPc::getUsername, userPc.getUsername())
                .ne(ObjectUtil.isNotNull(userPc.getId()), UserPc::getId, userPc.getId()));
        return !exist;
    }

    /**
     * 校验手机号码是否唯一
     * @param userPc
     * @return
     */
    @Override
    public boolean checkPhoneUnique(UserPc userPc) {
        boolean exist = userPcMapper.exists(new LambdaQueryWrapper<UserPc>()
                .eq(UserPc::getPhone, userPc.getPhone())
                .ne(ObjectUtil.isNotNull(userPc.getId()), UserPc::getId, userPc.getId()));
        return !exist;
    }


    @Override
    public TableDataInfo<User> selectPageUserPcList(UserPcQuery userPcQuery, PageQuery pageQuery) {
        Page<User> page = userPcMapper.selectPageUserList(pageQuery.build(), this.buildQueryWrapper(userPcQuery));
        return TableDataInfo.build(page);
    }

    /**
     * 删除用户
     * @param uIds
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteUserByIds(Long[] uIds) {
        List<Long> ids = Arrays.asList(uIds);
        userPcMapper.delete(new QueryWrapper<UserPc>().in("uid",uIds));
        return userMapper.deleteBatchIds(ids);
    }

    /**
     * 新增pc用户
     * @param user
     * @return
     */
    @Override
    public int insertUser(User user) {
        userMapper.insert(user);
        UserPc userPc = user.getUserPc();
        userPc.setUid(user.getUid());
        return userPcMapper.insert(userPc);
    }

    /**
     * 修改pc用户
     * @param userPc
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updatePcUser(UserPc userPc) {
        User user = userMapper.selectById(userPc.getUid());
        user.setUpdateId(LoginHelper.getUserId());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        return userPcMapper.updateById(userPc);
    }
    /**
     * 修改pc用户
     * @param userWx
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateWxUser(UserWx userWx) {
        User user = userMapper.selectById(userWx.getUid());
        user.setUpdateId(LoginHelper.getUserId());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        return userWxMapper.updateById(userWx);
    }

    @Override
    public UserPc getByUid(Long uid) {
        return userPcMapper.selectOne(new LambdaQueryWrapper<UserPc>().eq(UserPc::getUid,uid));

    }

    @Override
    public boolean checkEmailUnique(UserPc userPc) {
        boolean exist = userPcMapper.exists(new LambdaQueryWrapper<UserPc>()
                .eq(UserPc::getEmail, userPc.getEmail())
                .ne(ObjectUtil.isNotNull(userPc.getId()), UserPc::getId, userPc.getId()));
        return !exist;

    }

    private Wrapper<UserPcQuery> buildQueryWrapper(UserPcQuery userPcQuery) {
        // 创建 QueryWrapper
        QueryWrapper<UserPcQuery> wrapper = new QueryWrapper<>();

        wrapper.eq(ObjectUtil.isNotNull(userPcQuery.getUid()), "u.uid", userPcQuery.getUid());
        wrapper.eq(ObjectUtil.isNotNull(userPcQuery.getSex()), "userPc.sex", userPcQuery.getSex());
        wrapper.eq(ObjectUtil.isNotNull(userPcQuery.getStatus()), "userPc.status", userPcQuery.getStatus());
        wrapper.eq(StringUtils.isNotBlank(userPcQuery.getPhone()), "userPc.phone", userPcQuery.getPhone());
        wrapper.like(StringUtils.isNotBlank(userPcQuery.getUsername()), "userPc.username", userPcQuery.getUsername());
        wrapper.like(StringUtils.isNotBlank(userPcQuery.getName()), "userPc.name", userPcQuery.getName());

        Map<String, Object> params = userPcQuery.getParams();
        if (params.get("createTimeBegin") != null) {
            wrapper.between("u.create_time", params.get("createTimeBegin"), params.get("createTimeEnd"));
        }
        // 上次登录时间
        if (params.get("loginTimeBegin") != null) {
            wrapper.between("u.login_time", params.get("loginTimeBegin"), params.get("loginTimeEnd"));
        }
        wrapper.like(StringUtils.isNotBlank(userPcQuery.getLoginRegion()), "u.login_region", userPcQuery.getLoginRegion());
        wrapper.eq(ObjectUtil.isNotNull(userPcQuery.getUserType()), "u.user_type", userPcQuery.getUserType());
        wrapper.eq(ObjectUtil.isNotNull(userPcQuery.getCreateId()), "u.create_id", userPcQuery.getCreateId());
        if (params.get("updateTimeBegin") != null) {
            wrapper.between("u.update_time", params.get("updateTimeBegin"), params.get("updateTimeEnd"));
        }
        return wrapper;
    }


}
