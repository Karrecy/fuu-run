package com.karrecy.order.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.karrecy.order.domain.po.OrderAppeal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单申诉返回体
 */
@Data
public class OrderAppealVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private OrderAppeal orderAppeal;

    private List<String> imageUrls;

}
