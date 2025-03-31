package com.karrecy.common.enums;

import lombok.RequiredArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 状态
 */
@Getter
@RequiredArgsConstructor
public enum Status {
    OK(1, "正常"), DISABLE(0, "停用"),PENDING(2,"进行中");

    private final Integer code;
    private final String info;

}
