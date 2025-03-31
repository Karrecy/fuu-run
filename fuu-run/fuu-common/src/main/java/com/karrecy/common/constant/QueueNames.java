package com.karrecy.common.constant;

/**
 * redis队列名称
 */
public interface QueueNames {

    /**
     * 订单超时未支付自动取消 队列
     */
    String ORDER_PAY_CANCEL = "order_pay_cancel";

    /**
     * 订单超时未接单自动取消 队列
     */
    String ORDER_PENDING_ACCEPT_CANCEL = "order_pending_accept_cancel";

    /**
     * 订单送达未完成自动完成 队列
     */
    String ORDER_DELIVERED_AUTO_COMPLETE = "order_delivered_auto_complete";

    /**
     * 订单付款成功 队列
     */
    String ORDER_PAY_SUCCESS = "order_pay_success";

    /**
     * 订单退款成功 队列
     */
    String ORDER_REFUND_SUCCESS = "order_refund_success";

    /**
     * 订单聊天异步存储 队列
     */
    String ORDER_CHAT_STORAGE = "order_chat_storage";

    /**
     * 订单聊天异步通知 队列
     */
    String ORDER_CHAT_NOTIFY = "order_chat_notify";

}
