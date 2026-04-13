package com.secondhand.marketplace.backend.modules.aftersale.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DisputeActionLogVO {
    private Long id;
    private Long disputeId;
    private Long actionBy;
    private String actionType;
    private String actionDesc;
    private LocalDateTime createdAt;
}
