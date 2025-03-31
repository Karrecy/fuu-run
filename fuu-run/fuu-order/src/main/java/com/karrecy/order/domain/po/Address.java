package com.karrecy.order.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.karrecy.common.core.domain.BaseEntity;
import com.karrecy.common.sensitive.SensitiveWord;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 地址类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("address")
public class Address extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("uid")
    private Long uid;

    /**
     * 地点
     */
    @TableField("title")
    @Length(min = 2, max = 50, message = "地点名称必须在{min}到{max}个字符之间")
    private String title;

    /**
     * 地址详情
     */
    @TableField("detail")
    @Length(min = 2, max = 50, message = "地点详情必须在{min}到{max}个字符之间")
    @SensitiveWord
    private String detail;

    /**
     * 经度
     */
    @TableField("lon")
    @Pattern(
            regexp = "^(-?((((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)|(180(\\.0+)?)))$",
            message = "经度格式不正确"
    )
    private String lon;

    /**
     * 纬度
     */
    @TableField("lat")
    @Pattern(
            regexp = "^(-?((([0-8]?\\d)(\\.\\d+)?)|(90(\\.0+)?)))$",
            message = "纬度格式不正确"
    )
    private String lat;

    /**
     * 姓名
     */
    @TableField("name")
    @Length(min = 1, max = 10, message = "姓名必须在{min}到{max}个字符之间")
    private String name;

    /**
     * 电话
     */
    @TableField("phone")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phone;

    /**
     * 默认 0 否 1 是
     */
    @TableField("is_default")
    @NotNull(message = "请填写是否默认")
    private Integer isDefault;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    @TableField("create_id")
    private Long createId;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 修改人
     */
    @TableField("update_id")
    private Long updateId;


}
