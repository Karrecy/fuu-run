package com.karrecy.common.utils.wx;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import lombok.RequiredArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 小程序订阅消息类
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class WxHelper {

    public static final String TEMPLATE_MSG_UN_READ = "nFzHoJjaKP8W6jdiFZkXsX6Z2A1u6O1F7wGrNAUpBlY";
    public static final String TEMPLATE_ORDER_STATUS_CHANGE = "uQ8cRcy8jM8Rb09EUDZopOZgCLQcrxlFlGNzVez8_-w";

    public static final String PAGE_CHAT = "/pages/API/chat/chat";
    public static final String PAGE_ORDER_DETAIL = "/pages/API/order/detail/detail";
    public static final String PAGE_ORDER_RUNNER = "/pages/API/order/runner/runner";

    private final WxMaService wxMaService;

    public void sendSubMsg(
            List<WxMaSubscribeMessage.MsgData> data,
            String page,
            String templateId,
            String openid) {
        WxMaSubscribeMessage msg = new WxMaSubscribeMessage();
        msg.setData(data);
        msg.setPage(page);
        msg.setTemplateId(templateId);
        msg.setToUser(openid);
        try {
            wxMaService.getMsgService().sendSubscribeMsg(msg);
        } catch (WxErrorException e) {
            log.error(e.getMessage(), e);
        }
    }


    /**
     * 构建订单状态消息数据
     * @param orderId
     * @param title
     * @param status
     * @param remarks
     * @return
     */
    public List<WxMaSubscribeMessage.MsgData> buildOrderStatusData(
            Long orderId,
            String title,
            String status,
            String remarks) {
        List<WxMaSubscribeMessage.MsgData> list = new ArrayList<>();
        WxMaSubscribeMessage.MsgData orderIdData = new WxMaSubscribeMessage.MsgData();
        orderIdData.setName("character_string1");
        orderIdData.setValue(String.valueOf(orderId));
        WxMaSubscribeMessage.MsgData messageData = new WxMaSubscribeMessage.MsgData();
        messageData.setName("thing2");
        messageData.setValue(truncate(title,20));
        WxMaSubscribeMessage.MsgData senderNameData = new WxMaSubscribeMessage.MsgData();
        senderNameData.setName("phrase3");
        senderNameData.setValue(truncate(status,5));
        WxMaSubscribeMessage.MsgData remarksData = new WxMaSubscribeMessage.MsgData();
        remarksData.setName("thing11");
        remarksData.setValue(truncate(remarks,20));
        WxMaSubscribeMessage.MsgData timeData = new WxMaSubscribeMessage.MsgData();
        timeData.setName("time4");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm");
        timeData.setValue(now.format(formatter));
        list.add(messageData);
        list.add(orderIdData);
        list.add(senderNameData);
        list.add(remarksData);
        list.add(timeData);
        return list;
    }

    /**
     * 构建未读消息数据
     * @param orderId
     * @param content
     * @param senderName
     * @param remarks
     * @return
     */
    public List<WxMaSubscribeMessage.MsgData> buildMsgUnReadData(
            Long orderId,
            String content,
            String senderName,
            String remarks) {
        List<WxMaSubscribeMessage.MsgData> list = new ArrayList<>();
        WxMaSubscribeMessage.MsgData orderIdData = new WxMaSubscribeMessage.MsgData();
        orderIdData.setName("character_string27");
        orderIdData.setValue(String.valueOf(orderId));
        WxMaSubscribeMessage.MsgData messageData = new WxMaSubscribeMessage.MsgData();
        messageData.setName("thing2");
        messageData.setValue(truncate(content,20));
        WxMaSubscribeMessage.MsgData senderNameData = new WxMaSubscribeMessage.MsgData();
        senderNameData.setName("thing4");
        senderNameData.setValue(truncate(senderName,20));
        WxMaSubscribeMessage.MsgData remarksData = new WxMaSubscribeMessage.MsgData();
        remarksData.setName("thing7");
        remarksData.setValue(truncate(remarks,20));
        list.add(messageData);
        list.add(orderIdData);
        list.add(senderNameData);
        list.add(remarksData);
        return list;
    }

    private String truncate(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }
}
