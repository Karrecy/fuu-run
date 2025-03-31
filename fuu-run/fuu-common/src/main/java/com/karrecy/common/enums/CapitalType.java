package com.karrecy.common.enums;

import lombok.RequiredArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 明细类型
 */
@Getter
@RequiredArgsConstructor
public enum CapitalType {
    /**
     * 订单收益
     */
    ORDER_COMPLETE(0,"订单收益"),

    /**
     * 跑腿提现
     */
    RECODE_RUNNER(1,"跑腿提现"),

    /**
     * 代理提现
     */
    RECODE_AGENT(2,"代理提现");

    private final Integer code;

    private final String type;

}
