package com.karrecy.common.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * email配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "email")
public class EmailProperties {
    private String host;
    private int port;
    private String user;
    private String pass;
}
