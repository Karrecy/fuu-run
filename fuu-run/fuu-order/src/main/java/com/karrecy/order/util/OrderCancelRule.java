package com.karrecy.order.util;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

public class OrderCancelRule {

    /**
     * 根据取消时间计算扣费比例
     *
     * @param orderAcceptTime 接单时间
     * @return 扣费比例（0.1 = 10%）
     */
    public static BigDecimal getCancelFeeRate(LocalDateTime orderAcceptTime) {
        // 计算时间差（单位：分钟）
        long minutesDiff = Duration.between(orderAcceptTime, LocalDateTime.now()).toMinutes();

        // 根据时间区间设置扣费比例
        if (minutesDiff <= 5) {
            return BigDecimal.valueOf(0.1); // 5 分钟内扣除 10%
        } else if (minutesDiff <= 20) {
            return BigDecimal.valueOf(0.3); // 5~20 分钟扣除 30%
        } else {
            return BigDecimal.valueOf(0.5); // 20 分钟以上扣除 50%
        }
    }

    /**
     * 计算扣除费用
     *
     * @param totalPrice      订单总金额
     * @param orderAcceptTime 接单时间
     * @return 实际退款金额
     */
    public static BigDecimal calculateRefund(BigDecimal totalPrice, LocalDateTime orderAcceptTime) {
        BigDecimal feeRate = getCancelFeeRate(orderAcceptTime); // 获取扣费比例
        BigDecimal deduct = totalPrice.multiply(feeRate); // 计算扣除金额
        return totalPrice.subtract(deduct).setScale(2, BigDecimal.ROUND_HALF_UP); // 计算实际退款金额
    }


}
