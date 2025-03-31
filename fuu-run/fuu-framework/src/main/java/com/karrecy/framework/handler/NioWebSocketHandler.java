package com.karrecy.framework.handler;


import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karrecy.common.constant.QueueNames;
import com.karrecy.common.core.domain.model.ChatBody;
import com.karrecy.common.enums.Status;
import com.karrecy.common.utils.JsonUtils;
import com.karrecy.common.utils.redis.QueueUtils;
import com.karrecy.framework.config.properties.WebSocketProperties;
import com.karrecy.framework.websocket.channel.NioWebSocketChannelPool;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * netty websocket处理器
 */
@Slf4j
@ChannelHandler.Sharable
@Component
@RequiredArgsConstructor
public class NioWebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private final NioWebSocketChannelPool webSocketChannelPool;
    private final WebSocketProperties webSocketProperties;

    private final ObjectMapper objectMapper;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("客户端连接成功：{}", ctx.channel().id().asLongText());

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("客户端断开连接：{}", ctx.channel().id().asLongText());
        // 移除连接并释放资源
        webSocketChannelPool.removeChannel(ctx.channel());
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.info("14214214");
        try {
            if (msg instanceof FullHttpRequest) { // HTTP 请求处理 携带参数的
                handleHttpRequest(ctx, (FullHttpRequest) msg);
            } else if (msg instanceof WebSocketFrame) { // WebSocket 帧处理
                super.channelRead(ctx, msg); // 转交给 `channelRead0` 处理
            }
        } catch (Exception e) {
            log.error("处理客户端请求时发生异常：", e);
            ctx.close();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws JsonProcessingException {
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
        } else if (frame instanceof TextWebSocketFrame) {
            // 解析 JSON 为 MyMessage 对象
            String jsonStr = ((TextWebSocketFrame) frame).text();
            ChatBody message = objectMapper.readValue(jsonStr, ChatBody.class);
            System.out.println("收到消息：" + message);
            // 处理业务逻辑
            handleTextFrame(ctx, message);
        } else if (frame instanceof CloseWebSocketFrame) {
            ctx.close();
        } else {
            log.warn("收到未知类型的 WebSocket 帧：{}", frame.getClass().getName());
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        try {
//            String tokenValue = request.headers().get("Authorization").substring(7);
//            LoginUser loginUser = LoginHelper.getLoginUser(tokenValue);
//            if (ObjectUtil.isNull(loginUser)) {
//                log.error("token校验失败,拒绝连接");
//                ctx.close();
//            }
            // TODO orderId校验
//            webSocketChannelPool.addChannel(loginUser.getUid(),ctx.channel());
        } finally {
            request.release(); // 确保 FullHttpRequest 被正确释放
        }
    }

    private void handleTextFrame(ChannelHandlerContext ctx, ChatBody chatBody) {
        log.info("客户端发送消息：{}", chatBody);
        Integer userType = (Integer) ctx.channel().attr(AttributeKey.valueOf("userType")).get();
        Long uid = (Long) ctx.channel().attr(AttributeKey.valueOf("uid")).get();
        chatBody.setSenderId(uid);
        chatBody.setSenderType(userType);
        chatBody.setCreateTime(LocalDateTime.now());
        Set<Long> set = new HashSet<>(chatBody.getRecipientIds());
        chatBody.setRecipientIds(set);
        sendMessage(chatBody);
    }

    private String getWebSocketLocation(FullHttpRequest request) {
        String location = request.headers().get(HttpHeaderNames.HOST) + webSocketProperties.getPath();
        return "ws://" + location;
    }

//    private void sendMessageToOne(Long uid, String message) {
//        Channel channel = webSocketChannelPool.getChannelById(uid);
//        if (channel != null && channel.isActive()) {
//            channel.writeAndFlush(new TextWebSocketFrame(message)).addListener(future -> {
//                if (!future.isSuccess()) {
//                    log.error("发送消息失败，目标客户端：{}", uid, future.cause());
//                }
//            });
//        } else {
//            log.warn("目标客户端未在线：{}", uid);
//        }
//    }
    private void sendMessage(ChatBody chatBody) {
        //加入存储队列
        QueueUtils.addBoundedQueueObject(
                QueueNames.ORDER_CHAT_STORAGE,
                JsonUtils.toJsonString(chatBody));

        String json = toJson(chatBody);
        // 使用异步任务避免阻塞主线程
        List<Channel> channels = webSocketChannelPool.getListByIds(chatBody.getRecipientIds()); //在线
        Collection<Long> notifyList = getNotifyList(chatBody.getRecipientIds(),channels); //不在线
        //加入通知队列
        chatBody.setRecipientIds(notifyList);
        QueueUtils.addBoundedQueueObject(
                QueueNames.ORDER_CHAT_NOTIFY,
                JsonUtils.toJsonString(chatBody));
        channels.forEach(channel -> {
            if (ObjectUtil.isNull(channel)) {
                return;
            }
            if (channel.attr(webSocketChannelPool.UID).get().equals(chatBody.getSenderId())) {
                return;
            }
            if (channel.isActive()) {
                channel.writeAndFlush(new TextWebSocketFrame(json)).addListener(future -> {
                    if (!future.isSuccess()) {
                        log.error("广播消息失败，目标客户端：{}", channel.id(), future.cause());
                    }
                    else {

                    }
                });
            }
            //对方不在线
            else {
                if (!ObjectUtil.equals(chatBody.getIsBroadcast(), Status.OK.getCode())) {
                    //需要通知

                }
                else {

                }
            }
        });

    }

    private Collection<Long> getNotifyList(Collection<Long> recipientIds, List<Channel> channels) {
        // 将 channels 的 id 提取为一个集合
        Set<Long> channelIds = channels.stream()
                .map(channel -> (Long) channel.attr(AttributeKey.valueOf("uid")).get())  // 转换 Long 到 long
                .collect(Collectors.toSet());
        // 使用 removeAll 删除在 channelIds 中的 ID
        recipientIds.removeAll(channelIds);
        return recipientIds;
    }

    public String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON 转换失败", e);
        }
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("处理过程中发生异常：", cause);
        ctx.close();
    }
}

