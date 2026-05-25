package com.secondhand.marketplace.backend.modules.message.vo;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageVO {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private String senderNickname;
    private String messageType;
    private String content;
    private String extJson;
    private LocalDateTime sentAt;
    private Boolean recalled;
}