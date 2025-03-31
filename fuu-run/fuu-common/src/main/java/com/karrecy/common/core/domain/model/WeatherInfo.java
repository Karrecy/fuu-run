package com.karrecy.common.core.domain.model;

import lombok.Data;

/**
 * 天气信息
 */
@Data
public class WeatherInfo {
    private String province; // 省份
    private String city; // 城市
    private String adcode; // 城市编码
    private String weather; // 天气
    private String temperature; // 温度
    private String winddirection;  // 风向
    private String windpower;
    private String humidity; // 湿度
    private String reporttime; // 数据更新时间
    private float temperatureFloat; // 温度float
    private float humidityFloat; // 湿度float
}
