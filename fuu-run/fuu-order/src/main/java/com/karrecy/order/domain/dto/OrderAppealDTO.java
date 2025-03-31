package com.karrecy.order.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.karrecy.common.sensitive.SensitiveWord;
import com.karrecy.system.domain.po.Oss;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单申诉请求体
 */
@Data
public class OrderAppealDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "必须设置订单号")
    private Long orderId;

    /**
     * 申诉理由
     */
    @NotBlank(message = "请先说明申诉理由")
    @SensitiveWord
    private String appealReason;

    @NotEmpty(message = "请补充申诉凭证")
    private List<Long> ossAppealList;


}
