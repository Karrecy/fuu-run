package com.karrecy.framework.websocket.initialize;

import com.karrecy.framework.config.properties.WebSocketProperties;
import com.karrecy.framework.handler.ChannelAuthHandler;
import com.karrecy.framework.handler.NioWebSocketHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NioWebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {


    final private WebSocketProperties webSocketProperties;

    final private NioWebSocketHandler nioWebSocketHandler;

    final private ChannelAuthHandler channelAuthHandler;


    @Override
    protected void initChannel(SocketChannel socketChannel) {
        socketChannel.pipeline()
                // 1. HTTP请求处理器
                .addLast(new HttpServerCodec()) // 解码HTTP请求
                .addLast(new ChunkedWriteHandler()) // 处理大文件的分块写操作
                .addLast(new HttpObjectAggregator(8192)) // 合并HTTP请求成完整对象
                //WebSocketServerProtocolHandler 会自动处理 WebSocket 握手。
                // 如果 nioWebSocketHandler 或其他逻辑中重复处理了握手，也会导致冲突。
                // 所以，NioWebSocketHandler 中如果连接时候携带?token=xxx 等参数，发消息就会失败
                .addLast(channelAuthHandler)
                .addLast(new WebSocketServerProtocolHandler(webSocketProperties.getPath(), null, true, 65536))
                .addLast(nioWebSocketHandler)
        ; // 处理其他路径下的 WebSocket 消息
    }

}

