package com.karrecy.payment.domain.po;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.karrecy.common.core.validate.AddGroup;
import com.karrecy.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 提现审核表
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("money_recode")
public class MoneyRecode implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * uid
     */
    @TableField("uid")
    private Long uid;

    /**
     * 提现金额
     */
    @TableField("cash")
    @NotNull(message = "提现金额不能为空",groups = AddGroup.class)
    private BigDecimal cash;

    /**
     * 提现去向平台
     */
    @TableField("platform")
    @NotBlank(message = "提现去向平台不能为空",groups = AddGroup.class)
    private String platform;

    /**
     * 卡号
     */
    @TableField("card")
    @NotBlank(message = "卡号不能为空",groups = AddGroup.class)
    private String card;

    /**
     * 状态 0 驳回 1 通过 2 审核中
     */
    @TableField("status")
    @NotNull(message = "状态不能为空",groups = EditGroup.class)
    private Integer status;

    /**
     * 用户类型 
     */
    @TableField("type")
    private Integer type;

    /**
     * 用户备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 审核人
     */
    @TableField("update_id")
    private Long updateId;

    /**
     * 审核时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 审核反馈
     */
    @TableField("feedback")
    @NotBlank(message = "状态不能为空",groups = EditGroup.class)
    private String feedback;


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
