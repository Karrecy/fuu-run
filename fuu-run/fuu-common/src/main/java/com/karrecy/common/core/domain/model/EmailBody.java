package com.karrecy.common.core.domain.model;

import com.karrecy.common.constant.UserConstants;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * email登录对象
 */
@Data
public class EmailBody {
    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不可为空")
    private String email;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不可为空")
    private String code;
}
