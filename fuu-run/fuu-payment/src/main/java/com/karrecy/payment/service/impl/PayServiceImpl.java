package com.karrecy.payment.service.impl;

import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyV3Result;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayRefundV3Request;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.bean.result.WxPayRefundV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderV3Result;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.service.WxPayService;
import com.karrecy.common.constant.QueueNames;
import com.karrecy.common.exception.PayException;
import com.karrecy.common.utils.BigDecimalUtils;
import com.karrecy.common.utils.redis.QueueUtils;
import com.karrecy.payment.domain.vo.PayedVO;
import com.karrecy.payment.properties.WxPayProperties;
import com.karrecy.payment.service.IPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * <p>
 * 提现审核表 服务实现类
 * </p>
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class PayServiceImpl implements IPayService {

    private final WxPayService wxPayService;

    private final WxPayProperties wxPayProperties;

    /**
     * 支付回调
     */
    @Override
    public String payNotifyV3(String notifyData, HttpServletRequest request) {
        SignatureHeader signatureHeader = this.getRequestHeader(request);
        try{
            // 使用maven包中的方法进行解析与验证
            WxPayNotifyV3Result notifyV3Result =
                    wxPayService.parseOrderNotifyV3Result(notifyData, signatureHeader);
            WxPayNotifyV3Result.DecryptNotifyResult payResult = notifyV3Result.getResult();
            log.info(payResult.toString());
            //订单支付成功后执行
            if (payResult.getTradeState().equals("SUCCESS")) {
//                PaymentSuccessEvent paymentSuccessEvent = new PaymentSuccessEvent();
//                paymentSuccessEvent.setOrderId(Long.valueOf(payResult.getOutTradeNo()));
//                SpringUtils.context().publishEvent(paymentSuccessEvent);
                QueueUtils.addBoundedQueueObject(
                        QueueNames.ORDER_PAY_SUCCESS,
                        payResult.getOutTradeNo());
            }

        }
        catch (Exception e){
            log.error("【支付回调通知失败】:{}", e.getMessage());
            return WxPayNotifyResponse.fail("微信支付-回调失败");
        }

        return WxPayNotifyResponse.success("OK");
    }

    /**
     * 请求微信支付
     * @param desc
     * @param orderId
     * @param totalAmount
     * @param openid
     * @return
     */
    @Override
    public PayedVO pay(String desc, Long orderId, BigDecimal totalAmount, String openid) {
        WxPayUnifiedOrderV3Request request = new WxPayUnifiedOrderV3Request();

        request.setDescription(desc);  //订单描述
        request.setOutTradeNo(String.valueOf(orderId)); //订单id
        request.setNotifyUrl(wxPayProperties.getReturnUrl()); // 回调地址
//        request.setAmount(new WxPayUnifiedOrderV3Request.Amount()
//                .setTotal(1)); // 支付金额，单位分
        request.setAmount(new WxPayUnifiedOrderV3Request.Amount()
                .setTotal(BigDecimalUtils.convertToCents(totalAmount))); // 支付金额，单位分
        request.setPayer(new WxPayUnifiedOrderV3Request.Payer()
                .setOpenid(openid)); // 用户的 openid

        try {
            WxPayUnifiedOrderV3Result.JsapiResult appResult = wxPayService.createOrderV3(TradeTypeEnum.JSAPI, request);
            log.info(String.valueOf(appResult));
            PayedVO payedVO = new PayedVO();
            BeanUtils.copyProperties(appResult, payedVO);
            payedVO.setOrderId(orderId);
            return payedVO;
        } catch (Exception e) {
            throw new PayException(e.getMessage());
        }
    }
    /**
     * 请求微信支付
     * @param orderId
     * @param totalAmount
     * @return
     */
    @Override
    public void refund(Long orderId, BigDecimal totalAmount,BigDecimal refundAmount) {
        try {
            WxPayRefundV3Request refundRequest = new WxPayRefundV3Request();
            WxPayRefundV3Request.Amount amount = new WxPayRefundV3Request.Amount();
            amount.setRefund(BigDecimalUtils.convertToCents(refundAmount));
            amount.setTotal(BigDecimalUtils.convertToCents(totalAmount));
            amount.setCurrency("CNY");
            refundRequest.setAmount(amount);
            refundRequest.setNotifyUrl(wxPayProperties.getRefundUrl());
            refundRequest.setOutTradeNo(String.valueOf(orderId));
            refundRequest.setOutRefundNo(String.valueOf(orderId)); // 退款单号 每个订单最多退款一次
            WxPayRefundV3Result wxPayRefundV3Result = wxPayService.refundV3(refundRequest);
            log.info(String.valueOf(wxPayRefundV3Result));
        } catch (Exception e) {
            throw new PayException(e.getMessage());
        }
    }
    /**
     * 退款成功回调
     * @param notifyData
     * @param request
     * @return
     */
    @Override
    public String refundNotify(String notifyData, HttpServletRequest request) {
        SignatureHeader signatureHeader = this.getRequestHeader(request);
        try{
            // 使用maven包中的方法进行解析与验证
            WxPayNotifyV3Result notifyV3Result =
                    wxPayService.parseOrderNotifyV3Result(notifyData, signatureHeader);
            WxPayNotifyV3Result.DecryptNotifyResult payResult = notifyV3Result.getResult();
            log.info(payResult.toString());
            QueueUtils.addBoundedQueueObject(
                    QueueNames.ORDER_REFUND_SUCCESS,
                    payResult.getOutTradeNo());
        }
        catch (Exception e){
            log.error("【退款回调通知失败】:{}", e.getMessage());
            return WxPayNotifyResponse.fail("微信退款-回调失败");
        }

        return WxPayNotifyResponse.success("OK");
    }

    /**
     * 获取回调请求头：签名相关
     *
     * @param request HttpServletRequest
     * @return SignatureHeader
     */
    public SignatureHeader getRequestHeader(HttpServletRequest request) {
        // 获取通知签名
        String signature = request.getHeader("Wechatpay-Signature");
        String nonce = request.getHeader("Wechatpay-Nonce");
        String serial = request.getHeader("Wechatpay-Serial");
        String timestamp = request.getHeader("Wechatpay-Timestamp");

        SignatureHeader signatureHeader = new SignatureHeader();
        signatureHeader.setSignature(signature);
        signatureHeader.setNonce(nonce);
        signatureHeader.setSerial(serial);
        signatureHeader.setTimeStamp(timestamp);
        return signatureHeader;
    }

}
