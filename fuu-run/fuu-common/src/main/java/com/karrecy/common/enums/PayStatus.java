package com.karrecy.common.enums;

/**
 * 支付状态
 */
public enum PayStatus {

    /**
     * 未支付
     */
    UNPAID(0),
    /**
     * 已支付
     */
    PAID(1),
    /**
     * 退款中
     */
    REFUND_IN_PROGRESS(2),
    /**
     * 已退款
     */
    REFUNDED(3);

    private final Integer code;

    PayStatus(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

}
