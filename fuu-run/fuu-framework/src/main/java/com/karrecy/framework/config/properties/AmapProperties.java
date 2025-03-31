package com.karrecy.framework.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 地图相关配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "amap")
public class AmapProperties {

    /**
     * key
     */
    private String key;


}
