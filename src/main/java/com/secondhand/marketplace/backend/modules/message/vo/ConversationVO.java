package com.secondhand.marketplace.backend.modules.message.vo;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ConversationVO {
    private Long id;
    private String conversationType;
    private Long productId;
    private Long orderId;
    private Long userId;
    private String userNickname;
    private String lastMessageContent;
    private LocalDateTime lastMessageAt;
    private LocalDateTime createdAt;
    private Integer unreadCount;
}