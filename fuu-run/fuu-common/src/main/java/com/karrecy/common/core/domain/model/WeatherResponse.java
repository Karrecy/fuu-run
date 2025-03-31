package com.karrecy.common.core.domain.model;

import lombok.Data;

import java.util.List;

/**
 * 天气返回体
 */
@Data
public class WeatherResponse {
    private String status;
    private String count;
    private String info;
    private String infocode;
    private List<WeatherInfo> lives;
}
