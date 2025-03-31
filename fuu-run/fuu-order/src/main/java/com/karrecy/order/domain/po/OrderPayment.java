package com.karrecy.order.domain.po;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 订单支付类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("order_payment")
public class OrderPayment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @TableId(value = "order_id", type = IdType.AUTO)
    private Long orderId;

    /**
     * 附加金额
     */
    @TableField("additional_amount")
    private BigDecimal additionalAmount;

    /**
     * 实付金额
     */
    @TableField("actual_payment")
    private BigDecimal actualPayment;

    /**
     * 支付状态 0未支付 1已支付 2退款中 3已退款
     */
    @TableField("payment_status")
    private Integer paymentStatus;

    /**
     * 付款时间
     */
    @TableField("payment_time")
    private LocalDateTime paymentTime;

    /**
     * 退款中时间
     */
    @TableField("refund_pending_time")
    private LocalDateTime refundPendingTime;

    /**
     * 退款时间
     */
    @TableField("refund_time")
    private LocalDateTime refundTime;

    /**
     * 是否使用优惠券 0 否 1 是
     */
    @TableField("is_couponed")
    private Integer isCouponed;

    /**
     * 优惠券ID
     */
    @TableField("coupon_id")
    private Long couponId;

    /**
     * 优惠金额
     */
    @TableField("discount_amount")
    private BigDecimal discountAmount;


}
