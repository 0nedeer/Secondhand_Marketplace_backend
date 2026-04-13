package com.secondhand.marketplace.backend.modules.aftersale.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DisputeVO {
    private Long id;
    private String disputeNo;
    private Long orderId;
    private Long afterSaleId;
    private Long buyerId;
    private Long sellerId;
    private String currentStatus;
    private String responsibility;
    private String resolutionResult;
    private BigDecimal resolutionAmount;
    private Long resolvedBy;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<DisputeActionLogVO> actions;
}
