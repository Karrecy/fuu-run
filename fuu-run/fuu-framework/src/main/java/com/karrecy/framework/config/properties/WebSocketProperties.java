package com.karrecy.framework.config.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "chat.websocket")
public class WebSocketProperties {
    @Value("${chat.websocket.port}")
    private Integer port ; // 监听端口
    @Value("${chat.websocket.path}")
    private String path ; // 请求路径
    /**
     * Boss Group：
     * 一般设置为 1 到 2 个线程。由于 boss 组负责接受连接请求，通常不需要太多线程。
     * Work Group：
     * 线程数量建议设置为 CPU 核心数的 2 倍到 4 倍。例如，如果服务器有 8 个 CPU 核心，可以设置为 16 到 32 个线程。可以使用以下公式：
     *
     *     Work Threads = Core CPU Count × (2 到 4)
     */
    @Value("${chat.websocket.boss}")
    private Integer boss ; // bossGroup线程数
    @Value("${chat.websocket.work}")
    private Integer work ; // workGroup线程数

}


