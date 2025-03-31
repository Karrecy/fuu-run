package com.karrecy.framework.websocket.channel;


import cn.hutool.core.util.ObjectUtil;
import com.karrecy.common.helper.LoginHelper;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * netty连接池
 */
@Slf4j
@Component
@Data
public class NioWebSocketChannelPool {

    // 使用 DefaultChannelGroup 管理通道组，使用 ConcurrentMap 高效查找通道
    private final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private final ConcurrentMap<Long, Channel> channelMap = new ConcurrentHashMap<>();

    public final AttributeKey<Integer> USERTYPE = AttributeKey.valueOf("userType");
    public final AttributeKey<Long> UID = AttributeKey.valueOf("uid");

    /**
     * 新增一个客户端通道
     *如果觉得channl的id的asLongText()方法过于冗长可以推荐使用业务:id:id的形式作为key
     * @param channel 新的通道
     */
    public void addChannel(Long uid, Channel channel) {
        Channel channelExisit = channelMap.get(uid);
        if (ObjectUtil.isNotNull(channelExisit)) {
            channelExisit.close();
            channels.remove(channelExisit);
        }
        channelMap.put(uid, channel);  // 使用 map 高效查找
        channels.add(channel);
    }

    /**
     * 移除一个客户端连接通道
     *
     * @param channel 要移除的通道
     */
    public void removeChannel(Channel channel) {
        channels.remove(channel);
        channelMap.remove(channel.attr(UID).get());  // 从 map 中移除
    }

    /**
     * 获取所有活跃的客户端连接
     *
     * @return 活跃通道组
     */
    public ChannelGroup getChannels() {
        return channels;
    }

    /**
     * 通过通道 ID 获取通道
     *
     * @param uid 通道 ID
     * @return 通道，若未找到则返回 null
     */
    public Channel getChannelById(Long uid) {
        return channelMap.get(uid);  // 使用 map 的 O(1) 查找时间
    }

    public List<Channel> getListByIds(Collection<Long> uids) {
        List<Channel> result = new ArrayList<>();
        for (Long uid : uids) {
            Channel channelById = getChannelById(uid);
            if (ObjectUtil.isNotNull(channelById)) {
                result.add(channelById);
            }
        }
        return result;
    }

    public Boolean checkChannel(Long uid) {
        return channelMap.containsKey(uid);
    }

    /**
     * 向所有连接的客户端广播消息
     *
     * @param message 要广播的消息
     */
    public void broadcastMessage(String message) {
        channels.forEach(channel -> {
            if (channel.isActive()) {
                channel.writeAndFlush(message).addListener(future -> {
                    if (!future.isSuccess()) {
                        log.error("发送消息到客户端失败，目标客户端：{}", channel.id());
                    }
                });
            }
        });
    }
}

