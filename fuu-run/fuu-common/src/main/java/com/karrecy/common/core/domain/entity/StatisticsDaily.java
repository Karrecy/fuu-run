package com.karrecy.common.core.domain.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 每日数据统计表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("statistics_daily")
public class StatisticsDaily implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 总订单量
     */
    @TableField("total_orders")
    private Integer totalOrders;

    /**
     * 取消订单量
     */
    @TableField("canceled_orders")
    private Integer canceledOrders;

    /**
     * 申诉订单量
     */
    @TableField("appealed_orders")
    private Integer appealedOrders;

    /**
     * 完成订单量
     */
    @TableField("completed_orders")
    private Integer completedOrders;

    /**
     * 完单率(%)
     */
    @TableField("completion_rate")
    private BigDecimal completionRate;

    /**
     * 帮取送订单量
     */
    @TableField("delivery_orders")
    private Integer deliveryOrders;

    /**
     * 代买订单量
     */
    @TableField("purchase_orders")
    private Integer purchaseOrders;

    /**
     * 万能服务订单量
     */
    @TableField("universal_orders")
    private Integer universalOrders;

    /**
     * 帮取送订单占比(%)
     */
    @TableField("delivery_rate")
    private BigDecimal deliveryRate;

    /**
     * 代买订单占比(%)
     */
    @TableField("purchase_rate")
    private BigDecimal purchaseRate;

    /**
     * 万能订单占比(%)
     */
    @TableField("universal_rate")
    private BigDecimal universalRate;

    /**
     * 总收款金额
     */
    @TableField("total_payment")
    private BigDecimal totalPayment;

    /**
     * 总退款金额
     */
    @TableField("total_refund")
    private BigDecimal totalRefund;

    /**
     * 平台总收益
     */
    @TableField("platform_profit")
    private BigDecimal platformProfit;

    /**
     * 代理总收益
     */
    @TableField("agent_profit")
    private BigDecimal agentProfit;

    /**
     * 跑腿总收益
     */
    @TableField("runner_profit")
    private BigDecimal runnerProfit;

    /**
     * 总访问量
     */
    @TableField("total_visits")
    private Integer totalVisits;

    /**
     * 独立访问用户数
     */
    @TableField("unique_visitors")
    private Integer uniqueVisitors;

    /**
     * 恶意请求数量
     */
    @TableField("malicious_requests")
    private Integer maliciousRequests;

    /**
     * 新增用户数
     */
    @TableField("new_users")
    private Integer newUsers;

    /**
     * 活跃用户数
     */
    @TableField("active_users")
    private Integer activeUsers;

    /**
     * 新增跑腿用户数
     */
    @TableField("new_runners")
    private Integer newRunners;

    /**
     * 活跃跑腿用户数
     */
    @TableField("active_runners")
    private Integer activeRunners;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createTime;

    /**
     * 搜索值
     */
    @JsonIgnore
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private LocalDate beginTime;

    /**
     * 搜索值
     */
    @JsonIgnore
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private LocalDate endTime;

}
