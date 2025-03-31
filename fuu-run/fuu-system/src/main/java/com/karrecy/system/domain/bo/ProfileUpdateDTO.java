package com.karrecy.system.domain.bo;

import com.karrecy.common.core.validate.EditGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 更新个人信息请求体
 */
@Data
public class ProfileUpdateDTO {
    private String avatar;

    private String nickname;

    private Integer emailEnable;
}
