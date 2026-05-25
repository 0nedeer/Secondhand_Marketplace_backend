package com.secondhand.marketplace.backend.modules.message.vo;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class SystemNoticeVO {
    private Long id;
    private String noticeType;
    private String title;
    private String content;
    private String targetScope;
    private String targetRoleCode;
    private Long targetUserId;
    private String publishStatus;
    private LocalDateTime publishedAt;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}