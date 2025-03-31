package com.karrecy.common.enums;

/**
 * 服务类型
 */
public enum ServiceType {

    /**
     * 帮取送
     */
    PICKUP_DELIVERY(0),
    /**
     * 帮买
     */
    PURCHASE(1),
    /**
     * 万能帮
     */
    GENERAL_HELP(2);

    private final Integer code;

    ServiceType(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

}
