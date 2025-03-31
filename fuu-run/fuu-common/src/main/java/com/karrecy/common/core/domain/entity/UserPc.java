package com.karrecy.common.core.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.karrecy.common.annotation.Sensitive;
import com.karrecy.common.constant.UserConstants;
import com.karrecy.common.enums.SensitiveStrategy;
import com.karrecy.common.xss.Xss;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

/**
 * 平台管理员表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_pc")
public class UserPc implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("uid")
    private Long uid;

    @TableField(exist = false)
    @NotNull(message = "用户类型不能为空")
    private Integer userType;

    /**
     * 用户名
     */
    @Xss(message = "用户账号不能包含脚本字符")
//    @NotBlank(message = "用户名不能为空")
    @Length(min = UserConstants.USERNAME_MIN_LENGTH, max = UserConstants.USERNAME_MAX_LENGTH, message = "账户长度必须在{min}到{max}个字符之间")
    @TableField("username")
    private String username;

    /**
     * 密码
     */
    @TableField("password")
    @NotBlank(message = "密码不能为空")
    @Length(min = UserConstants.USERNAME_MIN_LENGTH, max = UserConstants.USERNAME_MAX_LENGTH, message = "密码长度必须在{min}到{max}个字符之间")
    private String password;

    @JsonIgnore
    @JsonProperty
    public String getPassword() {
        return password;
    }

    /**
     * 手机号
     */
    @TableField("phone")
    @Sensitive(strategy = SensitiveStrategy.PHONE)
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phone;

    /**
     * 真实姓名
     */
    @TableField("name")
    @NotBlank(message = "真实姓名不能为空")
    private String name;

    /**
     * 学生证
     */
    @TableField("student_card_url")
    @NotNull(message = "缺少学生证")
    private String studentCardUrl;

    /**
     * 身份证
     */
    @TableField("id_card_url")
    @NotNull(message = "缺少身份证")
    private String idCardUrl;

    /**
     * 0 女 1 男
     */
    @TableField("sex")
    @NotNull(message = "请先设置性别")
    private Integer sex;

    /**
     * 0 禁用 1 启用
     */
    @TableField("status")
    @NotNull(message = "请先设置用户状态")
    private Integer status;

    /**
     * 头像
     */
    @TableField("avatar")
    @NotNull(message = "请先设置用户头像")
    private String avatar;

    /**
     * 0 禁用 1 启用
     */
    @TableField("email_enable")
    @NotNull(message = "请先设置邮件启用状态")
    private Integer emailEnable;

    /**
     * 头像
     */
    @TableField("email")
    @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "邮箱格式不正确")
    private String email;

    /**
     * 当前代理校区
     */
    @TableField(exist = false)
    private String agentSchool;

    /**
     * 当前代理校区id
     */
    @TableField(exist = false)
    private Long agentSchoolId;


    /**
     * 请求参数
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @TableField(exist = false)
    private Map<String, Object> params = new HashMap<>();


}
