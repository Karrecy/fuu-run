package com.karrecy.order.domain.po;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.karrecy.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 校区类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("school")
public class School extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 属于谁管理
     */
    @TableField("belong_uid")
    @NotNull(message = "必须设置归属UID")
    private Long belongUid;

    /**
     * 城市编码表
     */
    @TableField("adcode")
    @NotBlank(message = "必须设置城市编码表")
    private String adcode;

    /**
     * 学校名称
     */
    @TableField("name")
    @NotBlank(message = "必须设置学校名称")
    private String name;

    /**
     * 学校logo
     */
    @TableField("logo")
    @NotBlank(message = "必须设置学校logo")
    private String logo;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 状态 0 禁用 1 启用
     */
    @TableField("status")
    @NotNull(message = "必须设置状态")
    private Integer status;

    /**
     * 平台收益占比
     */
    @TableField("profit_plat")
    @NotNull(message = "必须设置平台收益占比")
    private Integer profitPlat;

    /**
     * 代理收益占比
     */
    @TableField("profit_agent")
    @NotNull(message = "必须设置代理收益占比")
    private Integer profitAgent;

    /**
     * 跑腿收益占比
     */
    @TableField("profit_runner")
    @NotNull(message = "必须设置跑腿收益占比")
    private Integer profitRunner;

    /**
     * 底价
     */
    @TableField("floor_price")
    @NotNull(message = "必须设置订单底价")
    private BigDecimal floorPrice;


}
