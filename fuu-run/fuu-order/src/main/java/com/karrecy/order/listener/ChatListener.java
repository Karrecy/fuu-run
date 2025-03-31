package com.karrecy.order.listener;

import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.hutool.core.util.ObjectUtil;
import com.karrecy.common.constant.QueueNames;
import com.karrecy.common.core.domain.R;
import com.karrecy.common.core.domain.entity.User;
import com.karrecy.common.core.domain.model.ChatBody;
import com.karrecy.common.enums.DeviceType;
import com.karrecy.common.enums.Status;
import com.karrecy.common.enums.UserType;
import com.karrecy.common.utils.BeanCopyUtils;
import com.karrecy.common.utils.JsonUtils;
import com.karrecy.common.utils.redis.QueueUtils;
import com.karrecy.common.utils.wx.WxHelper;
import com.karrecy.order.domain.po.OrderChat;
import com.karrecy.order.domain.po.OrderMain;
import com.karrecy.order.service.IOrderChatService;
import com.karrecy.order.service.IOrderMainService;
import com.karrecy.order.service.IOrderProgressService;
import com.karrecy.system.service.IUserService;
import com.karrecy.system.service.IUserWxService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 聊天监听 解耦
 */
@Component
@Slf4j
@AllArgsConstructor
public class ChatListener implements ApplicationListener<ApplicationReadyEvent> {

    private IOrderMainService orderMainService;
    private IOrderProgressService orderProgressService;
    private IUserService userService;
    private IUserWxService userWxService;
    private IOrderChatService orderChatService;

    private WxHelper wxHelper;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        subscribe(QueueNames.ORDER_CHAT_STORAGE);
        subscribe(QueueNames.ORDER_CHAT_NOTIFY);

    }
    public R<Void> subscribe(String queueName) {
        log.info("通道: {} 监听中......", queueName);
        // 项目初始化设置一次即可
        QueueUtils.subscribeBlockingQueue(queueName, (String data) -> {
            // 观察接收时间
            log.info("通道: {}, 收到数据: {}", queueName, data);
            switch (queueName) {
                case QueueNames.ORDER_CHAT_STORAGE:
                    chatStorage(data);
                    break;
                case QueueNames.ORDER_CHAT_NOTIFY:
                    notifyWx(data);
                    break;
            }
        }, true);
        return R.ok("操作成功");
    }

    private void notifyWx(String data) {
        try {
            ChatBody chatBody = JsonUtils.parseObject(data, ChatBody.class);
            OrderMain orderMainDB = orderMainService.getById(chatBody.getOrderId());
            User sender = userService.selectUserByUid(chatBody.getSenderId());
            String senderName = "";
            if (ObjectUtil.equals(sender.getDeviceType(), DeviceType.WX.getCode())) {
                if (sender.getUid().equals(orderMainDB.getRunnerId())) senderName = "跑腿员-"+sender.getUserWx().getNickname();
                else if (sender.getUid().equals(orderMainDB.getUserId())) senderName = "用户-"+sender.getUserWx().getNickname();
            }
            else {
                if (ObjectUtil.equals(sender.getUserType(), UserType.SCHOOL_AGENT.getCode())) senderName = "校区代理-"+sender.getUserPc().getName();
                if (ObjectUtil.equals(sender.getUserType(), UserType.SUPER_ADMIN.getCode())) senderName = "超级管理员";
                if (ObjectUtil.equals(sender.getUserType(), UserType.SYSTEM.getCode())) senderName = "系统";

            }


            Collection<Long> recipientIds = chatBody.getRecipientIds();
            for (Long recipientId : recipientIds) {
                User user = userService.selectUserByUid(recipientId);
                //如果是跑腿员发给用户 直接通知
                if ((sender.getUid().equals(orderMainDB.getRunnerId())
                        && recipientId.equals(orderMainDB.getUserId()))
                        //如果是用户发给跑腿员 直接通知
                || (sender.getUid().equals(orderMainDB.getUserId())
                        && recipientId.equals(orderMainDB.getRunnerId()))
                        //如果是@ 直接通知
                || !ObjectUtil.equals(chatBody.getIsBroadcast(), Status.OK.getCode())) {
                    List<WxMaSubscribeMessage.MsgData> msgData = wxHelper.buildMsgUnReadData(
                            Long.valueOf(chatBody.getOrderId()),
                            chatBody.getMessage(),
                            senderName,
                            "订单标题:"+orderMainDB.getTag());
                    wxHelper.sendSubMsg(
                            msgData,
                            WxHelper.PAGE_CHAT+"?orderId="+chatBody.getOrderId(),
                            WxHelper.TEMPLATE_MSG_UN_READ,
                            String.valueOf(user.getUserWx().getOpenid())
                    );
                }
            }
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    private void chatStorage(String data) {
            ChatBody chatBody = JsonUtils.parseObject(data, ChatBody.class);
            OrderChat orderChat = new OrderChat();
            BeanUtils.copyProperties(chatBody,orderChat);
            orderChat.setOrderId(Long.valueOf(chatBody.getOrderId()));
            String listStr = chatBody.getRecipientIds().stream().map(String::valueOf).collect(Collectors.joining(","));
            orderChat.setRecipients(listStr);
            orderChatService.save(orderChat);
    }


}
