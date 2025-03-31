package com.karrecy.payment.domain.po;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 资金流动表
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("capital_flow")
public class CapitalFlow {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单id
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 代理id
     */
    @TableField("agent_id")
    private Long agentId;

    /**
     * 代理收益
     */
    @TableField("profit_agent")
    private BigDecimal profitAgent;

    /**
     * 跑腿id
     */
    @TableField("runner_id")
    private Long runnerId;

    /**
     * 跑腿收益
     */
    @TableField("profit_runner")
    private BigDecimal profitRunner;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 用户收益
     */
    @TableField("profit_user")
    private BigDecimal profitUser;

    /**
     * 平台收益
     */
    @TableField("profit_plat")
    private BigDecimal profitPlat;



    /**
     * 类型 订单收益 跑腿提现 代理提现
     */
    @TableField("type")
    private Integer type;

    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 搜索值
     */
    @JsonIgnore
    @TableField(exist = false)
    private LocalDateTime beginTime;

    /**
     * 搜索值
     */
    @JsonIgnore
    @TableField(exist = false)
    private LocalDateTime endTime;
}
