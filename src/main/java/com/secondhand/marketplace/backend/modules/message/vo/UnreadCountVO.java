package com.secondhand.marketplace.backend.modules.message.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UnreadCountVO {
    private Long unreadCount;
}