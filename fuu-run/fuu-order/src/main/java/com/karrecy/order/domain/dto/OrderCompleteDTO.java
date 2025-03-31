package com.karrecy.order.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 订单完成请求体
 */
@Data
public class OrderCompleteDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    @NotEmpty(message = "请上传至少一张完成凭证")
    private List<Long> completionImages;
    @NotNull(message = "必须设置订单号")
    private Long orderId;
}
