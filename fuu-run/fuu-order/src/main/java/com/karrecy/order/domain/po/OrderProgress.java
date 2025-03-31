package com.karrecy.order.domain.po;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 订单进度类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("order_progress")
public class OrderProgress implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单id
     */
    @TableId(value = "order_id", type = IdType.AUTO)
    private Long orderId;

    /**
     * 接单时间
     */
    @TableField("accepted_time")
    private LocalDateTime acceptedTime;

    /**
     * 开始配送时间
     */
    @TableField("delivering_time")
    private LocalDateTime deliveringTime;

    /**
     * 送达时间
     */
    @TableField("delivered_time")
    private LocalDateTime deliveredTime;

    /**
     * 完成时间
     */
    @TableField("completed_time")
    private LocalDateTime completedTime;

    /**
     * 由谁完成
     */
    @TableField("completed_type")
    private Integer completedType;

    /**
     * 取消时间
     */
    @TableField("cancel_time")
    private LocalDateTime cancelTime;

    /**
     * 取消原因
     */
    @TableField("cancel_reason")
    private String cancelReason;

    /**
     * 取消人类型
     */
    @TableField("cancel_user_type")
    private Integer cancelUserType;

    /**
     * 取消人ID
     */
    @TableField("cancel_user_id")
    private Long cancelUserId;


}
