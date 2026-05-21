package com.secondhand.marketplace.backend.modules.message.vo;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UserNoticeVO {
    private Long inboxId;
    private Long noticeId;
    private String title;
    private String content;
    private String noticeType;
    private String readStatus;
    private LocalDateTime deliveredAt;
    private LocalDateTime readAt;
}