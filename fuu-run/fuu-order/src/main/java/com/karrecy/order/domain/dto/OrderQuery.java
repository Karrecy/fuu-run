package com.karrecy.order.domain.dto;

import com.karrecy.order.domain.vo.AddressVO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单分页请求体
 */
@Data
public class OrderQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long orderUid;

    private Long takerUid;


    /**
     * 下单或接单
     */
    private Integer orderOrTake;

    /**
     * 学校id
     */
    private Long schoolId;

    /**
     * 服务类型 0 帮取送 1 代买 2 万能服务
     */
    private Integer serviceType;

    /**
     * 标签
     */
    private String tag;


    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 起始时间
     */
    private LocalDateTime beginTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

}
