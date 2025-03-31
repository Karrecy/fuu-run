package com.karrecy.system.domain.bo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.karrecy.common.core.domain.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 小程序用户分页查询
 */
@Data
public class UserWxQuery extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 全局uid
     */
    private Long uid;

    private String openid;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机
     */
    private String phone;

    /**
     * 是否跑腿 0 否 1 是
     */
    private Integer isRunner;

    /**
     * 是否可以下单 0 否 1 是
     */
    private Integer canOrder;

    /**
     * 是否可以接单 0 否 1 是
     */
    private Integer canTake;

    /**
     * 跑腿绑定学校id
     */
    private Long schoolId;

    /**
     * 跑腿真实姓名
     */
    private String realname;

    /**
     * 跑腿性别
     */
    private Integer gender;

    /**
     * 登录地址
     */
    private String loginRegion;
    /**
     * 用户类型 0 超级管理员 1 校区管理员 2 普通管理员 3 普通用户 4 跑腿用户
     */
    private Integer userType;
    /**
     * 创建人
     */
    private Long createId;

}
