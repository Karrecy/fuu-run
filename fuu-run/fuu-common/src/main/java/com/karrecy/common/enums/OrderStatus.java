package com.karrecy.common.enums;

/**
 * 订单状态
 */
public enum OrderStatus {

    /**
     * 待支付
     */
    PENDING_PAYMENT(0),
    /**
     * 待接单
     */
    PENDING_ACCEPTANCE(1),
    /**
     * 待配送
     */
    PENDING_DELIVER(2),
    /**
     * 配送中
     */
    DELIVERING(3),
    /**
     * 已送达
     */
    DELIVERED(4),
    /**
     * 已取消
     */
    CANCELED(5),
    /**
     * 已完成
     */
    COMPLETED(10),
    /**
     * 已申诉
     */
    CLOSED(11);

    private final Integer code;

    OrderStatus(Integer code) {
        this.code = code;
    }


    public Integer getCode() {
        return code;
    }

    public String getString() {
        switch (code) {
            case 0:
                return "待支付";
            case 1:
                return "待接单";
            case 2:
                return "待配送";
            case 3:
                return "配送中";
            case 4:
                return "已送达";
            case 5:
                return "已取消";
            case 10:
                return "已完成";
            case 11:
                return "已申诉";
            default:
                return "未知";
        }
    }
    public static String getString(Integer code) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getCode().equals(code)) {
                return status.getString();
            }
        }
        return "未知";
    }

}
