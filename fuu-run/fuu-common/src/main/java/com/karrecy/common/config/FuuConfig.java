package com.karrecy.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 项目相关配置
 */

@Data
@Component
@ConfigurationProperties(prefix = "fuu")
public class FuuConfig {

    /**
     * 项目名称
     */
    private String name;

    /**
     * 版本
     */
    private String version;

    /**
     * 版权年份
     */
    private String copyrightYear;


    /**
     * 超时未支付取消时长 (分钟)
     */
    private Long payCancelTtl;

    /**
     * 超时未完成自动完成时长 (小时)
     */
    private Long autoCompleteTtl;

    /**
     * 完成订单凭证上限 （张）
     */
    private Integer completionImagesLimit;

    /**
     * 信用分上限（初始）
     */
    private Integer creditUpperLimit;

    /**
     * 信用分下限
     */
    private Integer creditLowerLimit;

    /**
     * 信用分每次扣除
     */
    private Integer creditDeduction;

    /**
     * 用户地址上限
     */
    private Integer maxAddress;
}
