package com.karrecy.admin.controller.system;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.http.HttpUtil;
import com.karrecy.common.core.controller.BaseController;
import com.karrecy.common.core.domain.R;
import com.karrecy.common.core.domain.model.WeatherResponse;
import com.karrecy.common.utils.JsonUtils;
import com.karrecy.framework.config.properties.AmapProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文件上传 控制层
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/amap")
public class AmapController extends BaseController {

    private final AmapProperties amapProperties;

    private static final String WEATHER_URL = "https://restapi.amap.com/v3/weather/weatherInfo";

    /**
     * 查询OSS对象存储列表
     */
    @GetMapping("/weather/{adcode}")
    @SaCheckPermission("amap:weather")
    public R weather(@PathVariable String adcode) {
        String response = HttpUtil.get(WEATHER_URL + "?key=" + amapProperties.getKey() + "&city=" + adcode);
        WeatherResponse weatherResponse = JsonUtils.parseObject(response, WeatherResponse.class);
        return R.ok(weatherResponse.getLives().get(0));
    }


}
