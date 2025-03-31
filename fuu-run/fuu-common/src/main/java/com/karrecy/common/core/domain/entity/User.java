package com.karrecy.common.core.domain.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.karrecy.common.constant.UserConstants;
import com.karrecy.common.enums.UserType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 全局用户表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 全局uid
     */
    @TableId(value = "uid", type = IdType.AUTO)
    private Long uid;

    @TableField(exist = false)
    private UserPc userPc;

    @TableField(exist = false)
    private UserWx userWx;

    /**
     * 0 pc 1 小程序
     */
    @TableField("device_type")
    private Integer deviceType;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 上次登录时间
     */
    @TableField("login_time")
    private LocalDateTime loginTime;

    /**
     * 登录ip
     */
    @TableField("login_ip")
    private String loginIp;

    /**
     * 登录地址
     */
    @TableField("login_region")
    private String loginRegion;

    /**
     * 用户类型 0 超级管理员 1 校区管理员 2 普通管理员 3 普通用户 4 跑腿用户
     */
    @TableField("user_type")
    private Integer userType;

    /**
     * 创建人
     */
    @TableField("create_id")
    private Long createId;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 更新人
     */
    @TableField("update_id")
    private Long updateId;

    /**
     * 获取登录id
     */
    public String getLoginId() {
        return UserType.getByCode(userType).getUserType() + ":" + uid;
    }
    public boolean isAdmin() {
        return UserConstants.ADMIN_ID.equals(this.uid);
    }
}
