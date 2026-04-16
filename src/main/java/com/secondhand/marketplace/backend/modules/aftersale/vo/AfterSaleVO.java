package com.secondhand.marketplace.backend.modules.aftersale.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AfterSaleVO {
    private Long id;
    private String afterSaleNo;
    private Long orderId;
    private Long orderItemId;
    private Long buyerId;
    private Long sellerId;
    private String requestType;
    private String requestReason;
    private String detailDesc;
    private BigDecimal requestedAmount;
    private BigDecimal finalAmount;
    private String requestStatus;
    private String sellerResponse;
    private LocalDateTime sellerRespondedAt;
    private Long adminId;
    private String adminDecision;
    private LocalDateTime closedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AfterSaleEvidenceVO> evidences;
}
