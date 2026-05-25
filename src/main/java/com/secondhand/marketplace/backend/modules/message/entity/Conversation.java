package com.secondhand.marketplace.backend.modules.message.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("conversation")
public class Conversation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String conversationType;

    private Long productId;

    private Long orderId;

    //private Long userId;

    private Long initiatorId;   // 会话发起方用户ID

    private Long receiverId;    // 会话接收方用户ID

    private LocalDateTime lastMessageAt;

    private String lastMessageContent;  // 最后消息内容

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;



}