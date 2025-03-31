package com.karrecy.payment.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 请求支付后的回调参数
 */
@Data
public class PayedVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;
    private String appId;
    private String timeStamp;
    private String nonceStr;
    private String packageValue;
    private String signType;
    private String paySign;

}
