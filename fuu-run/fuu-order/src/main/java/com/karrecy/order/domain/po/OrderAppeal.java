package com.karrecy.order.domain.po;

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

/**
 * 订单申诉类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("order_appeal")
public class OrderAppeal extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单id
     */
    @TableField("order_id")
    private String orderId;

    /**
     * 学校id
     */
    @TableField("school_id")
    private Long schoolId;

    /**
     * 申诉时间
     */
    @TableField("appeal_time")
    private LocalDateTime appealTime;

    /**
     * 申诉理由
     */
    @TableField("appeal_reason")
    private String appealReason;

    /**
     * 申诉状态 0 不通过 1 通过 2 申诉中
     */
    @TableField("appeal_status")
    private Integer appealStatus;

    /**
     * 申诉更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 申诉驳回原因
     */
    @TableField("remarks")
    private String remarks;

    /**
     * 更新人id
     */
    @TableField("update_id")
    private Long updateId;

    /**
     * 更新人类型
     */
    @TableField("update_type")
    private Integer updateType;


}
