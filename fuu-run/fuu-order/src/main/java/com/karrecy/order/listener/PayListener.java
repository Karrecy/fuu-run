package com.karrecy.order.listener;

import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.hutool.core.util.ObjectUtil;
import com.karrecy.common.constant.CacheNames;
import com.karrecy.common.constant.QueueNames;
import com.karrecy.common.core.domain.R;
import com.karrecy.common.core.domain.entity.User;
import com.karrecy.common.enums.OrderStatus;
import com.karrecy.common.enums.PayStatus;
import com.karrecy.common.enums.UserType;
import com.karrecy.common.utils.redis.QueueUtils;
import com.karrecy.common.utils.redis.RedisUtils;
import com.karrecy.common.utils.wx.WxHelper;
import com.karrecy.order.domain.po.OrderMain;
import com.karrecy.order.domain.po.OrderPayment;
import com.karrecy.order.domain.po.OrderProgress;
import com.karrecy.order.service.IOrderChatService;
import com.karrecy.order.service.IOrderMainService;
import com.karrecy.order.service.IOrderPaymentService;
import com.karrecy.order.service.IOrderProgressService;
import com.karrecy.system.service.IUserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


/**
 * 支付结果监听
 */
@Component
@Slf4j
@AllArgsConstructor
public class PayListener implements ApplicationListener<ApplicationReadyEvent> {

    private IOrderMainService orderMainService;
    private IOrderPaymentService orderPaymentService;
    private IUserService userService;
    private WxHelper wxHelper;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        subscribe(QueueNames.ORDER_PAY_SUCCESS);
        subscribe(QueueNames.ORDER_REFUND_SUCCESS);

    }
    public R<Void> subscribe(String queueName) {
        log.info("通道: {} 监听中......", queueName);
        // 项目初始化设置一次即可
        QueueUtils.subscribeBlockingQueue(queueName, (String data) -> {
            // 观察接收时间
            log.info("通道: {}, 收到数据: {}", queueName, data);
            // 使用异步方式处理回调
            CompletableFuture.runAsync(() -> {
                try {
                    switch (queueName) {
                        case QueueNames.ORDER_PAY_SUCCESS:
                            paySuccess(data);
                            break;
                        case QueueNames.ORDER_REFUND_SUCCESS:
                            refundSuccess(data);
                            break;
                    }
                } catch (Exception e) {
                    log.error("处理队列消息失败(支付回调）: {}", e.getMessage(), e);
                }
            });
//            switch (queueName) {
//                case QueueNames.ORDER_PAY_SUCCESS:
//                    paySuccess(data);
//                    break;
//                case QueueNames.ORDER_REFUND_SUCCESS:
//                    refundSuccess(data);
//                    break;
//            }


        }, true);
        return R.ok("操作成功");
    }
    @Transactional
    public void paySuccess(String orderNum) {
        Long orderId = Long.valueOf(orderNum);
        log.info("事件监听，订单id为：{}",orderId);
        try {
            //更新基本订单状态为 待接单
            OrderMain orderMainDB = orderMainService.getById(orderId);
            orderMainDB.setStatus(OrderStatus.PENDING_ACCEPTANCE.getCode());
            orderMainService.updateById(orderMainDB);
            //更新订单支付表
            OrderPayment orderPaymentDB = orderPaymentService.getById(orderId);
            orderPaymentDB.setPaymentStatus(PayStatus.PAID.getCode());
            orderPaymentDB.setPaymentTime(LocalDateTime.now());
            orderPaymentService.updateById(orderPaymentDB);

            //添加到延迟队列
            Integer autoCancelTtl = orderMainDB.getAutoCancelTtl();
            QueueUtils.addDelayedQueueObject(
                    QueueNames.ORDER_PENDING_ACCEPT_CANCEL,
                    orderId,autoCancelTtl,
                    TimeUnit.SECONDS);

            //记录活跃用户
            RedisUtils.recordAU(
                    LocalDate.now().toString(),
                    orderMainDB.getUserId().toString(),
                    CacheNames.DAILY_AU_USER
            );
        }
        catch (Exception e) {
            log.error("用户支付成功，但更新订单失败，{}", e.getMessage());
        }
    }
    public void refundSuccess(String orderNum) {
        Long orderId = Long.valueOf(orderNum);
        log.info("事件监听，订单id为：{}",orderId);
        try {
            OrderMain orderMainDB = orderMainService.getById(orderId);
            //更新订单支付表
            OrderPayment orderPaymentDB = orderPaymentService.getById(orderId);
            orderPaymentDB.setPaymentStatus(PayStatus.REFUNDED.getCode());
            orderPaymentDB.setRefundTime(LocalDateTime.now());
            orderPaymentService.updateById(orderPaymentDB);

            //通知用户
            List<WxMaSubscribeMessage.MsgData> msgData = wxHelper.buildOrderStatusData(
                    orderId,
                    orderMainDB.getTag(),
                    "退款已到账",
                    "退款已返回零钱，请注意查看"
            );
            User user = userService.selectUserByUid(orderMainDB.getUserId());
            wxHelper.sendSubMsg(
                    msgData,
                    WxHelper.PAGE_ORDER_DETAIL+"?id="+orderId,
                    WxHelper.TEMPLATE_ORDER_STATUS_CHANGE,
                    user.getUserWx().getOpenid()
            );
        }
        catch (Exception e) {
            log.error("用户退款成功，但更新订单失败，{}",e.getMessage());
        }
    }

}
