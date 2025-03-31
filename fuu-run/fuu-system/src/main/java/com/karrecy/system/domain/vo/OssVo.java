package com.karrecy.system.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * OSS对象存储视图对象
 */
@Data
public class OssVo {

    private static final long serialVersionUID = 1L;

    /**
     * 对象存储主键
     */
    private String ossId;

    /**
     * 文件类型
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
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 上传人
     */
    private String createId;

    private LocalDateTime updateTime;

    private Long updateId;

    /**
     * 服务商
     */
    private String service;


}
