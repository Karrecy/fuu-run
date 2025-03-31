package com.karrecy.common.enums;

/**
 * 文件类型
 */
public enum FileType {

    /**
     * 附件图片
     */
    ADDITIONAL_IMAGES(1),
    /**
     * 附件文件
     */
    ADDITIONAL_FILES(2),
    /**
     * 完成凭证
     */
    COMPLETION_IMAGES(3),
    /**
     * 用户头像
     */
    USER_AVATAR(4),
    /**
     * 学生证照片
     */
    STUDENT_CARD(5),

    /**
     * 订单申诉凭证
     */
    APPEAL_IMAGES(6),

    /**
     * 学校logo
     */
    SCHOOL_LOGO(7),

    /**
     * 身份证图片
     */
    ID_CARD(8);
    private final Integer code;

    FileType(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

}
