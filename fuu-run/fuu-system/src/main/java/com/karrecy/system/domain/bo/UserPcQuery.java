package com.karrecy.system.domain.bo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.karrecy.common.constant.UserConstants;
import com.karrecy.common.core.domain.BaseEntity;
import com.karrecy.common.xss.Xss;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * pc用户分页查询
 */
@Data
public class UserPcQuery extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 全局uid
     */
    private Long uid;
    /**
     * 0 女 1 男
     */
    private Integer sex;
    /**
     * 0 禁用 1 启用
     */
    private Integer status;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 用户名
     */
    private String username;
    /**
     * 真实姓名
     */
    private String name;
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
