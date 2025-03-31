package com.karrecy.order.domain.po;

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
 * 订单聊天类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("order_chat")
public class OrderChat implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("order_id")
    private Long orderId;

    /**
     * 发送者id
     */
    @TableField("sender_id")
    private Long senderId;

    /**
     * 发送者类型
     */
    @TableField("sender_type")
    private Integer senderType;

    /**
     * 接收者ids
     */
    @TableField("recipients")
    private String recipients;

    /**
     * 消息类型
     */
    @TableField("msg_type")
    private Integer msgType;

    /**
     * 消息体
     */
    @TableField("message")
    private String message;

    /**
     * 已读ids
     */
    @TableField("readIds")
    private String readIds;

    @TableField("create_time")
    private LocalDateTime createTime;


}
