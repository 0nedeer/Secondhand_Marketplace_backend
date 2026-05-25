package com.secondhand.marketplace.backend.modules.message.vo;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ConversationDetailVO {
    private Long id;
    private String conversationType;
    private Long productId;
    private Long orderId;
    private Long userId;
    private String userNickname;
    private String userAvatar;
    private List<ChatMessageVO> messages;
}