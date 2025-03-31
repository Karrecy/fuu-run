package com.karrecy.order.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 订单附件类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("order_attachment")
public class OrderAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单id
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 文件原始名
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 文件地址
     */
    @TableField("file_url")
    private String fileUrl;

    /**
     * 文件后缀
     */
    @TableField("file_type")
    private String fileType;

    /**
     * 文件大小字节
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * 类型 1订单附件 2完成凭证 3申诉凭证
     */
    @TableField("type")
    private Integer type;


}
