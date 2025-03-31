package com.karrecy.order.domain.dto;

import com.karrecy.common.sensitive.SensitiveWord;
import com.karrecy.order.domain.vo.AddressVO;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 *订单提交请求体
 */
@Data
public class OrderSubmitDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 学校id
     */
    @NotNull(message = "学校尚未初始化")
    private Long schoolId;
    /**
     * 服务类型 0 帮取送 1 代买 2 万能服务
     */
    @NotNull(message = "请先选择服务类型")
    private Integer serviceType;
    /**
     * 标签
     */
    @NotBlank(message = "请先填写物品类型")
    @SensitiveWord
    private String tag;
    /**
     * 物品重量
     */
    private String weight;
    /**
     * 详情
     */
    @NotBlank(message = "请先填写详情")
    @SensitiveWord
    private String detail;
    /**
     * 是否指定时间 0 否 1 是
     */
    @NotNull(message = "请先填写指定时间")
    private Integer isTimed;
    /**
     * 指定时间
     */
    private LocalDateTime specifiedTime;
    /**
     * 未接单取消时间（秒）
     */
    @NotNull(message = "请先填写未接单取消时间")
    private Integer autoCancelTtl;
    /**
     * 0女 1男 2不限
     */
    @NotNull(message = "请先选择性别限制")
    private Integer gender;
    /**
     * 追加金额
     */
    @NotNull(message = "追加金额不能为空")
    private BigDecimal additionalAmount;
    /**
     * 预估商品价格
     */
    private BigDecimal estimatedPrice;
    /**
     * 附件图片的ossIds
     */
    private List<Long> attachImages;
    /**
     * 附件文件的ossIds
     */
    private List<Long> attachFiles;
    /**
     * 起始地点
     */
    private AddressVO startAddress;
    /**
     * 终点
     */
    @NotNull(message = "请先填写地址")
    @Valid
    private AddressVO endAddress;

}
