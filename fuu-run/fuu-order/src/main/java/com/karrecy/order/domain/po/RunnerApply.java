package com.karrecy.order.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.karrecy.common.core.domain.BaseEntity;
import com.karrecy.common.core.validate.AddGroup;
import com.karrecy.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 跑腿申请表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("runner_apply")
public class RunnerApply extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("uid")
    private Long uid;

    /**
     * 学校id
     */
    @TableField("school_id")
    @NotNull(message = "学校id不能为空",groups = AddGroup.class)
    private Long schoolId;

    /**
     * 学校名称
     */
    @TableField("school_name")
    @NotBlank(message = "学校名称不能为空",groups = AddGroup.class)
    private String schoolName;


    /**
     * 姓名
     */
    @TableField("realname")
    @NotBlank(message = "姓名不能为空",groups = AddGroup.class)
    private String realname;

    /**
     * 性别 0 女 1 男
     */
    @TableField("gender")
    @NotNull(message = "性别不能为空",groups = AddGroup.class)
    private Integer gender;

    /**
     * 学生证
     */
    @TableField("student_card_url")
    @NotBlank(message = "请先上传学生证",groups = AddGroup.class)
    private String studentCardUrl;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 申请状态 0驳回 1 通过 2申请中
     */
    @TableField("status")
    @NotNull(message = "申请状态不能为空",groups = EditGroup.class)
    private Integer status;

    /**
     * 备注
     */
    @TableField("remarks")
    @NotBlank(message = "备注不能为空",groups = EditGroup.class)
    private String remarks;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 更新人
     */
    @TableField("update_id")
    private Long updateId;


}
