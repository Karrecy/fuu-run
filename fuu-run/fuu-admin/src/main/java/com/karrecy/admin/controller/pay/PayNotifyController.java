package com.karrecy.admin.controller.pay;



import cn.dev33.satoken.annotation.SaIgnore;
import com.karrecy.payment.service.IPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 支付回调相关接口
 */
@RestController
@RequestMapping("/notify")
@Slf4j
@RequiredArgsConstructor
public class PayNotifyController {

    private final IPayService payService;

    /**
     * 支付成功回调
     */
    @SaIgnore
    @PostMapping("/payNotify")
    public String paySuccessNotify(@RequestBody String notifyData, HttpServletRequest request) {
        return payService.payNotifyV3(notifyData, request);

    }

    /**
     * 退款成功回调
     */
    @SaIgnore
    @PostMapping("/refundNotify")
    public String refundNotify(@RequestBody String notifyData, HttpServletRequest request) {
        return payService.refundNotify(notifyData, request);

    }


}
