package com.karrecy.common.core.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 聊天请求体
 */
@Data
public class ChatBody implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderId;

    private Integer isBroadcast;

    private Collection<Long> recipientIds;

    private Integer msgType;

    private String message;

    private Long senderId;

    private Integer senderType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}
