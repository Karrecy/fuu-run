package com.karrecy.common.exception;

/**
 * 支付异常
 */
public final class PayException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误提示
     */
    private String message;

    /**
     * 错误明细，内部调试错误
     */
    private String detailMessage;

    /**
     * 空构造方法，避免反序列化问题
     */
    public PayException() {
    }

    public PayException(String message) {
        this.message = message;
    }

    public PayException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }

    public PayException setMessage(String message) {
        this.message = message;
        return this;
    }

    public PayException setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
        return this;
    }
}
