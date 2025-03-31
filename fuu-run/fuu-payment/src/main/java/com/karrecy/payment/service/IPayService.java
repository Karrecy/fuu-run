package com.karrecy.payment.service;

import com.karrecy.payment.domain.vo.PayedVO;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;


public interface IPayService {
    /**
     * 支付回调
     */
    String payNotifyV3(String notifyData, HttpServletRequest request);

    /**
     * 请求微信支付
     * @param desc
     * @param orderId
     * @param totalAmount
     * @param openid
     * @return
     */
    PayedVO pay(String desc, Long orderId, BigDecimal totalAmount, String openid);
    public void refund(Long orderId, BigDecimal totalAmount,BigDecimal refundAmount);
    /**
     * 退款成功回调
     * @param notifyData
     * @param request
     * @return
     */
    String refundNotify(String notifyData, HttpServletRequest request);
}
