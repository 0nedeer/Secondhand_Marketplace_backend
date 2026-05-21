package com.secondhand.marketplace.backend.modules.message.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("chat_message")
public class ChatMessage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long conversationId;

    private Long senderId;

    private String messageType;

    private String content;

    private String extJson;

    private LocalDateTime sentAt;

    private Integer recalled;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}