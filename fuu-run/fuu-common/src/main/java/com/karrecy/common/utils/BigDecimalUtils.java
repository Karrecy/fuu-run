package com.karrecy.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * BigDecimal工具类
 */
public class BigDecimalUtils {
    private static final int SCALE = 2; // 保留两位小数
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP; // 四舍五入

    // 将 BigDecimal 转换为分（整数）
    public static int convertToCents(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        // 乘以100并四舍五入取整
        BigDecimal scaledAmount = amount.multiply(new BigDecimal("100"));

        // 转换为整数并返回
        return scaledAmount.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
    }

    // 加法
    public static BigDecimal add(BigDecimal num1, BigDecimal num2) {
        return num1.add(num2).setScale(SCALE, ROUNDING_MODE);
    }

    // 减法
    public static BigDecimal subtract(BigDecimal num1, BigDecimal num2) {
        return num1.subtract(num2).setScale(SCALE, ROUNDING_MODE);
    }

    // 乘法
    public static BigDecimal multiply(BigDecimal num1, BigDecimal num2) {
        return num1.multiply(num2).setScale(SCALE, ROUNDING_MODE);
    }

    // 除法
    public static BigDecimal divide(BigDecimal num1, BigDecimal num2) {
        if (num2.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("除数不能为零");
        }
        return num1.divide(num2, SCALE, ROUNDING_MODE);
    }

}
