package com.secondhand.marketplace.backend.modules.trade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderStatusLogVO {
    private Long id;
    private String fromStatus;
    private String toStatus;
    private Long changedBy;
    private String changeReason;
    private LocalDateTime changedAt;
}

