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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * tag表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tags")
public class Tags extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("school_id")
    @NotNull(message = "必须设置学校ID")
    private Long schoolId;

    /**
     * tag
     */
    @TableField("name")
    @NotBlank(message = "tag名称不能为空")
    private String name;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 服务类型 0 帮取送 1 代买 2 万能服务
     */
    @TableField("service_type")
    @NotNull(message = "服务类型不能为空")
    private Integer serviceType;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("create_id")
    private Long createId;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("update_id")
    private Long updateId;


}
