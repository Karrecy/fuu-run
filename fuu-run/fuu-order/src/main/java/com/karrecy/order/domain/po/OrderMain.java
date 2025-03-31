package com.karrecy.order.domain.po;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.karrecy.common.handler.JsonTypeHandler;
import com.karrecy.order.domain.vo.AddressVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 主订单类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "order_main",autoResultMap = true)
public class OrderMain implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 学校id
     */
    @TableField("school_id")
    private Long schoolId;

    /**
     * 服务类型 0 帮取送 1 代买 2 万能服务
     */
    @TableField("service_type")
    private Integer serviceType;

    /**
     * 标签
     */
    @TableField("tag")
    private String tag;

    /**
     * 物品重量
     */
    @TableField("weight")
    private String weight;

    /**
     * 起点地址
     */
    @TableField(value = "start_address",typeHandler = JsonTypeHandler.class)
    private AddressVO startAddress;

    /**
     * 终点地址
     */
    @TableField(value = "end_address",typeHandler = JsonTypeHandler.class)
    private AddressVO endAddress;

    /**
     * 具体描述（暴露）
     */
    @TableField("detail")
    private String detail;

    /**
     * 是否指定时间 0 否 1 是
     */
    @TableField("is_timed")
    private Integer isTimed;

    /**
     * 指定时间
     */
    @TableField("specified_time")
    private LocalDateTime specifiedTime;

    /**
     * 未结单取消时间（秒）
     */
    @TableField("auto_cancel_ttl")
    private Integer autoCancelTtl;

    /**
     * 0女 1男 2不限
     */
    @TableField("gender")
    private Integer gender;

    /**
     * 预估商品价格
     */
    @TableField("estimated_price")
    private BigDecimal estimatedPrice;

    /**
     * 订单总金额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 订单状态
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 跑腿id
     */
    @TableField("runner_id")
    private Long runnerId;


}
