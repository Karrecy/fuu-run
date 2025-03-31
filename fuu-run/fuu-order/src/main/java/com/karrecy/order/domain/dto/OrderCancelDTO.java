package com.karrecy.order.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单取消请求体
 */
@Data
public class OrderCancelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单id
     */
    @NotNull(message = "必须设置订单号")
    private Long orderId;

    /**
     * 取消原因
     */
    @NotBlank(message = "请说明取消原因")
    private String cancelReason;

}
