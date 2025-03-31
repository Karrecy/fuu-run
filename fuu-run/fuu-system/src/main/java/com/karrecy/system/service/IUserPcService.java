package com.karrecy.system.service;

import com.karrecy.common.core.domain.PageQuery;
import com.karrecy.common.core.domain.entity.User;
import com.karrecy.common.core.domain.entity.UserPc;
import com.baomidou.mybatisplus.extension.service.IService;
import com.karrecy.common.core.domain.entity.UserWx;
import com.karrecy.common.core.page.TableDataInfo;
import com.karrecy.system.domain.bo.UserPcQuery;

/**
 * <p>
 * 平台管理员表 服务类
 * </p>
 */
public interface IUserPcService extends IService<UserPc> {

    /**
     * 校验用户名称是否唯一
     * @param userPc
     * @return
     */
    boolean checkUserNameUnique(UserPc userPc);

    /**
     * 校验手机号码是否唯一
     * @param userPc
     * @return
     */
    boolean checkPhoneUnique(UserPc userPc);

    TableDataInfo<User> selectPageUserPcList(UserPcQuery userPcQuery, PageQuery pageQuery);

    /**
     * 删除用户
     * @param userIds
     * @return
     */
    int deleteUserByIds(Long[] userIds);

    /**
     * 新增pc用户
     * @param user
     * @return
     */
    int insertUser(User user);

    /**
     * 修改pc用户
     * @param userPc
     * @return
     */
    int updatePcUser(UserPc userPc);

    /**
     * 修改pc用户
     * @param userWx
     * @return
     */
    int updateWxUser(UserWx userWx);

    /**
     * 根据uid查询
     * @param uid
     * @return
     */
    UserPc getByUid(Long uid);

    /**
     * 检查邮箱是否唯一
     * @param userPc
     * @return
     */
    boolean checkEmailUnique(UserPc userPc);
}
