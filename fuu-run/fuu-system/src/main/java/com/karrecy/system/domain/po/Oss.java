package com.karrecy.system.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.karrecy.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * OSS对象存储对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("oss")
public class Oss  {

    /**
     * 对象存储主键
     */
    @TableId(value = "id")
    private String ossId;


    /**
     * 业务类型
     */
    private Integer type;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 原名
     */
    private String originalName;

    /**
     * 文件后缀名
     */
    private String fileSuffix;

    /**
     * URL地址
     */
    private String url;

    /**
     * 服务商
     */
    private String service;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("create_id")
    private Long createId;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("update_id")
    private Long updateId;

}
