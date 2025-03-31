package com.karrecy.order.listener;

import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.karrecy.common.constant.QueueNames;
import com.karrecy.common.core.domain.R;
import com.karrecy.common.core.domain.entity.UserWx;
import com.karrecy.common.enums.OrderStatus;
import com.karrecy.common.enums.PayStatus;
import com.karrecy.common.enums.UserType;
import com.karrecy.common.utils.redis.QueueUtils;
import com.karrecy.common.utils.wx.WxHelper;
import com.karrecy.order.domain.po.OrderMain;
import com.karrecy.order.domain.po.OrderPayment;
import com.karrecy.order.domain.po.OrderProgress;
import com.karrecy.order.service.IOrderChatService;
import com.karrecy.order.service.IOrderMainService;
import com.karrecy.order.service.IOrderPaymentService;
import com.karrecy.order.service.IOrderProgressService;
import com.karrecy.payment.service.impl.PayServiceImpl;
import com.karrecy.system.mapper.UserWxMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;


/**
 * @订单操作监听 解耦
 */
@Component
@Slf4j
@AllArgsConstructor
public class OrderListener implements ApplicationListener<ApplicationReadyEvent> {

    private IOrderMainService orderMainService;
    private IOrderProgressService orderProgressService;
    private IOrderPaymentService orderPaymentService;
    private PayServiceImpl payService;
    private IOrderChatService orderChatService;

    private final UserWxMapper userWxMapper;
    private final WxHelper wxHelper;
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        subscribe(QueueNames.ORDER_PAY_CANCEL);
        subscribe(QueueNames.ORDER_PENDING_ACCEPT_CANCEL);
        subscribe(QueueNames.ORDER_DELIVERED_AUTO_COMPLETE);

    }
    public R<Void> subscribe(String queueName) {
        log.info("通道: {} 监听中......", queueName);
        // 项目初始化设置一次即可
        QueueUtils.subscribeBlockingQueue(queueName, (String data) -> {
            // 观察接收时间
            log.info("通道: {}, 收到数据: {}", queueName, data);
            switch (queueName) {
                case QueueNames.ORDER_PAY_CANCEL:
                    payCancel(data);
                    break;
                case QueueNames.ORDER_PENDING_ACCEPT_CANCEL:
                    pendingAcceptCancel(data);
                    break;
                case QueueNames.ORDER_DELIVERED_AUTO_COMPLETE:
                    deliveredAutoComplete(data);
                    break;
            }


        }, true);
        return R.ok("操作成功");
    }

    public void payCancel(String orderNum) {
        log.info("处理订单超时未付款 自动取消");
        try {
            OrderMain orderMainDB = orderMainService.getById(orderNum);
            if (!ObjectUtil.equals(orderMainDB.getStatus(), OrderStatus.PENDING_PAYMENT.getCode())) {
                return;
            }
            //设置状态为取消
            boolean update = orderMainService.update(
                    new LambdaUpdateWrapper<OrderMain>()
                            .set(OrderMain::getStatus, OrderStatus.CANCELED.getCode())
                            .eq(OrderMain::getId, orderMainDB.getId())
                            .eq(OrderMain::getStatus, OrderStatus.PENDING_PAYMENT.getCode())
            );
            if (!update) return;

            OrderProgress orderProgressDB = orderProgressService.getById(orderNum);
            //取消原因
            orderProgressDB.setCancelUserType(UserType.SYSTEM.getCode());
            orderProgressDB.setCancelTime(LocalDateTime.now());
            orderProgressDB.setCancelReason("超时未付款自动取消");
            orderProgressService.updateById(orderProgressDB);
        }
        catch (Exception e) {
            log.error("处理订单超时未付款 出错，{}", e.getMessage());
        }
    }

    public void pendingAcceptCancel(String orderNum) {
        log.info("处理订单超时未接单 自动取消");
        try {
            OrderMain orderMainDB = orderMainService.getById(orderNum);
            if (!ObjectUtil.equals(orderMainDB.getStatus(), OrderStatus.PENDING_ACCEPTANCE.getCode())) {
                return;
            }
            //设置状态为取消
            boolean update = orderMainService.update(
                    new LambdaUpdateWrapper<OrderMain>()
                            .set(OrderMain::getStatus, OrderStatus.CANCELED.getCode())
                            .eq(OrderMain::getId, orderMainDB.getId())
                            .eq(OrderMain::getStatus, OrderStatus.PENDING_ACCEPTANCE.getCode())
            );
            if (!update) return;

            OrderProgress orderProgressDB = orderProgressService.getById(orderNum);
            //取消原因
            orderProgressDB.setCancelUserType(UserType.SYSTEM.getCode());
            orderProgressDB.setCancelTime(LocalDateTime.now());
            orderProgressDB.setCancelReason("超时未接单自动取消");
            orderProgressService.updateById(orderProgressDB);
            //退款
            OrderPayment orderPaymentDB = orderPaymentService.getById(orderNum);
            orderPaymentDB.setRefundPendingTime(LocalDateTime.now());
            orderPaymentDB.setPaymentStatus(PayStatus.REFUND_IN_PROGRESS.getCode());
            orderPaymentService.updateById(orderPaymentDB);
            //退款 全额退款
            payService.refund(Long.valueOf(orderNum), orderPaymentDB.getActualPayment(), orderPaymentDB.getActualPayment());
            //通知用户
            List<WxMaSubscribeMessage.MsgData> msgData = wxHelper.buildOrderStatusData(
                    Long.valueOf(orderNum),
                    orderMainDB.getTag(),
                    OrderStatus.getString(OrderStatus.CANCELED.getCode()),
                    "超时未接单，已取消"
            );
            UserWx orderUser = userWxMapper.selectOne(new LambdaQueryWrapper<UserWx>().eq(UserWx::getUid, orderMainDB.getUserId()));
            wxHelper.sendSubMsg(
                    msgData,
                    WxHelper.PAGE_ORDER_DETAIL+"?id="+orderNum,
                    WxHelper.TEMPLATE_ORDER_STATUS_CHANGE,
                    orderUser.getOpenid()
            );

        }
        catch (Exception e) {
            log.error("处理订单超时未接单 出错，{}", e.getMessage());
        }

    }

    public void deliveredAutoComplete(String orderNum) {
        log.info("处理订单送达未完成 自动完成");
        try {
            OrderMain orderMainDB = orderMainService.getById(orderNum);
            if (!ObjectUtil.equals(orderMainDB.getStatus(), OrderStatus.DELIVERED.getCode())) {
                return;
            }
            //设置状态为完成
            orderMainDB.setStatus(OrderStatus.COMPLETED.getCode());
            orderMainService.updateById(orderMainDB);

            OrderProgress orderProgressDB = orderProgressService.getById(orderNum);
            orderProgressDB.setCompletedTime(LocalDateTime.now());
            orderProgressDB.setCompletedType(UserType.SYSTEM.getCode());
            orderProgressService.updateById(orderProgressDB);
            //通知用户
            List<WxMaSubscribeMessage.MsgData> msgData = wxHelper.buildOrderStatusData(
                    Long.valueOf(orderNum),
                    orderMainDB.getTag(),
                    OrderStatus.getString(OrderStatus.COMPLETED.getCode()),
                    "订单已自动确认完成"
            );
            UserWx orderUser = userWxMapper.selectOne(new LambdaQueryWrapper<UserWx>().eq(UserWx::getUid, orderMainDB.getUserId()));
            wxHelper.sendSubMsg(
                    msgData,
                    WxHelper.PAGE_ORDER_DETAIL+"?id="+orderNum,
                    WxHelper.TEMPLATE_ORDER_STATUS_CHANGE,
                    orderUser.getOpenid()
            );
        }
        catch (Exception e) {
            log.error("处理订单送达未完成 自动完成出错，{}", e.getMessage());
        }

    }
}
